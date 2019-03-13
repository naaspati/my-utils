package pkg;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sampullara.cli.Args;
import com.sampullara.cli.Argument;

import pkg.Package2.Class2;

public class Main {

	private final Path logDir = Paths.get(System.getenv("LOG_DIR")); 
    private static final double VERSION = 0.17;
    static StringBuilder sb, sb2 = new StringBuilder(500);

    @Argument(alias="h", description="print this message")
    boolean help;

    @Argument(alias="v", description="print version")
    boolean version;

    @Argument(description="only print process, doesn;t create the jar")
    boolean dryRun = false;

    @Argument(description="folder to find java files",delimiter=";")
    File[] src = {new File("src")};

    @Argument(description="created jar saved as")
    File out = new File("myutils.jar");
    
    Set<String> extras = new HashSet<>();

    final Collection<Package2> allPackages;

    public static void main(String[] args) throws URISyntaxException, IOException {
        new Main(args);
    }
    
    private static boolean isNotEmpty(String s) {
    	return !isEmpty(s);
    } 
    private static boolean isEmpty(String s) {
		return s == null || s.isEmpty();
	}
    private static boolean isNotEmpty(String[] s) {
    	return !isEmpty(s);
    } 
    private static boolean isEmpty(String[] s) {
		return s == null || s.length == 0;
	}
    private static boolean isEmptyTrimmed(String s) {
		return isEmpty(s) || s.trim().isEmpty();
	}

	private List<String> unparsed;
    private final Path myUtilsDir;

    public Main(String[] args) throws URISyntaxException, IOException {
        Path configpath = Paths.get("pack-myutils.properties");
        if(Files.exists(configpath)) {
            System.out.println(green("loading config: ")+configpath);
            Properties config = new Properties();
            config.load(Files.newInputStream(configpath));
            
            Function<String, Optional<String>> c = key -> {
            	return Optional.ofNullable(config.getProperty(key))
        		.map(String::trim)
        		.filter(Main::isNotEmpty);
            };
            src = c.apply("src")
            		.map(s -> Pattern.compile(";", Pattern.LITERAL).splitAsStream(s).map(String::trim).filter(Main::isNotEmpty).map(File::new).toArray(File[]::new))
            		.orElse(src);
            
            out = c.apply("target").map(File::new).orElse(out);
            c.apply("extras")
            .ifPresent(s -> extras.addAll(Arrays.asList(s.split(",|;"))));
            
        } else {
            unparsed =  Args.parseOrExit(this, args);
            if(!unparsed.isEmpty())
                System.out.println(red("unknown args: ")+unparsed);

            if(help) {
                Args.usage(this);
                System.exit(0);    
            } 
            if(version){
                System.out.println(yellow("version: ")+VERSION);
                System.exit(0);
            }
            
            src = Stream.of(src)
                    .filter(f -> {
                if(!f.exists()) {
                    System.out.println(red("not found: ")+f);
                    return false;
                } 
                return true;
            }).toArray(File[]::new);

            if(src.length == 0) {
                System.out.println(red("sources not found: "));
                System.exit(0);
            }
        }

        if(src.length == 1)
            println(green("src: ")+src[0].getAbsolutePath());
        else {
        	println(green("src: "));
            for (File file : src) println("   "+file.getAbsolutePath());
        }

        println(green("target: ")+out);
        println(green("extras: ")+extras);
        
        myUtilsDir = Paths.get(System.getenv("MYUTILS_DIR"));

        allPackages = walkMyUtilsPackages();
        Set<Class2> foundClasses = new HashSet<>();
        Map<Path, List<Path>> files = new LinkedHashMap<>();
        List<Path> srcFiles;
        
        for (File file : src) {
        	if(file.isFile()) {
        		if(!file.getName().endsWith(".java")) {
        			println("bad file ext: "+file);
        			continue;
        		}
        		srcFiles = Arrays.asList(file.toPath());
        	} else {
        		srcFiles = 
                        Files.walk(file.toPath())
                        .filter(Files::isRegularFile)
                        .filter(f -> f.getFileName().toString().toLowerCase().endsWith(".java"))
                        .collect(Collectors.toList());
        	}
        	
            files.put(file.toPath(), srcFiles);
            if(srcFiles.isEmpty()) {
                println(red("no .java file found: ")+file);
                continue;
            }
            final int count = file.toPath().getNameCount();

            for (Path p : srcFiles)
                search(toText(p), foundClasses, subpath(count, p).toString().replace('\\', '/'));
        }
        if(foundClasses.isEmpty()) {
            println(red("no class(es) found to pack"));
            println(red("no packing occur"));
            return;
        }

        Set<Class2> foundClasses2 = new HashSet<>(); 

        while(!foundClasses.isEmpty()) {
            foundClasses2.clear();

            for (Class2 c : foundClasses) {
                checkSiblingDependencies(c, foundClasses2);
                search(c, foundClasses2);  
            }

            Set<Class2> temp = foundClasses;
            foundClasses = foundClasses2;
            foundClasses2  = temp; 
        }

        String status = packJar();
        
        

        sb.setLength(0);
        allPackages.stream()
        .filter(Package2::anyFound)
        .sorted(Comparator.comparing(p1 -> p1.pkgName))
        .forEach(p -> {
        	sb.append(yellow(p.pkgName))
        	.append(" (").append(p.classes.stream().filter(Class2::isFound).count()).append('/').append(p.getCount()).append(")\n");
            for (Class2 c : p.classes) {
                if(c.isFound())
                    about(c);
            }
        });
        
        if(status != null)
            sb.append('\n').append(status);

        println(sb);

        sb.setLength(0);
        Package2.founded.forEach((s,t) -> {
            sb.append(s).append("\n   ");
            t.forEach(z -> sb.append(z).append("\n   "));
            sb.append('\n');
        });
        
        sb.append("\n\n-----------------------------------\n\nFiles\n-----------------------------------\n\n");
        files.forEach((f, list) -> {
            sb.append(f).append('\n');
            list.forEach(s -> sb.append("    ").append(subpath(f.getNameCount(), s)).append('\n'));
        });

        setText(logDir.resolve("summery.log"), sb);
        setText(logDir.resolve("console.log"), sb2);
    }
    private void setText(Path p, StringBuilder sb) throws IOException {
    	Files.write(p, sb.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
	}
	private void println(StringBuilder sb) {
    	System.out.println(sb);
		sb2.append(sb);
	}
    private void println(String string) {
    	System.out.println(string);
		sb2.append(string);
	}
	private Object subpath(int count, Path p) {
		return (p.getNameCount() <= count ? p : p.subpath(count, p.getNameCount()));
	}
	private void checkSiblingDependencies(Class2 cls, Set<Class2> foundClasses) {
        String text = cls.getText();

        for (Class2 c : cls.getPackage().classes) {
            if(!c.isFound() && c != cls && c.checkHas(text)) {
                foundClasses.add(c);
                c.setFound(cls);
            }
        }
    }
    List<Path> addedRequired;
    Path bin;
    private String packJar() throws IOException {
        Path temp = logDir.resolve("temp-myutils.jar");
        bin = myUtilsDir.resolve("bin");

        addedRequired = new ArrayList<>();

        try(OutputStream os = Files.newOutputStream(temp);
                JarOutputStream jos = new JarOutputStream(os)) {

            for (Package2 pkg : allPackages) {
                for (Class2 c : pkg.classes) {
                    if(c.isFound()) {
                        Path p = bin.resolve(c.getPackage().pkgName.replace('.', '/'));
                        String[] files = p.toFile().list();

                        processRequired(c, jos);
                        processClassesFiles(p, files, c, jos);
                    }
                }
            }
        }

        if(dryRun) 
            return null;

        boolean  b = Files.exists(out.toPath());
        Files.move(temp, out.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return green(b ? "\nreplaced: " : "created: ")+out+"\n";
    }
    private void processClassesFiles(Path pkgDir, String[] files, Class2 cls, JarOutputStream jos) throws IOException {
        String eql = cls.className + ".class";
        String start = cls.className + "$";

        for (int i = 0; i < files.length; i++) {
            String s = files[i];

            if(s != null && (s.equals(eql) || s.startsWith(start))) {
                writeToJar(jos, pkgDir.resolve(s));
                files[i] = null;
            }
        }
    }
    private void processRequired(Class2 c, JarOutputStream jos) throws IOException {
        if(c.getRequired() == null)
            return;


        for (String s : c.getRequired()) {
            Path p = c.file.resolveSibling(s);
            if(!addedRequired.contains(p)) {
                writeToJar(jos, p);
                addedRequired.add(p);
            }
        }
    }
    private void writeToJar(JarOutputStream jos, Path p) throws IOException {
        jos.putNextEntry(new JarEntry(p.subpath(bin.getNameCount(), p.getNameCount()).toString().replace('\\', '/')));
        Files.copy(p, jos);
        jos.closeEntry();
    }
    private void about(Class2 c) {
        String version = c.getVersion();
        String[] required = c.getRequired();

        sb.append("  ").append(c.className);
        if(isEmptyTrimmed(version) && isEmpty(required)) {
            sb.append('\n');
            return;
        }

        yellow(sb, " -> ");
        sb.append(" [");

        if(!isEmptyTrimmed(version))
            magenta(sb, " version: ").append(version).append(isNotEmpty(required) ? comma : "");
        
        append(" required: ", required);

        sb.append("]\n");
    }

	String comma = yellow(", ");

    private void append(String title, String[] values) {
        if(isEmpty(values))
            return;
        magenta(sb, title);

        if(values.length == 1)
            sb.append(values[0]).append(comma);
        else {
            sb.append(" {");
            for (int i = 0; i < values.length - 1; i++)
                sb.append(values[i]).append(comma);

            sb.append(values[values.length - 1]).append(" }").append(comma);
        }
    }
    private void search(String text, Collection<Class2> foundClasses, Object where) {
        for (Package2 p : allPackages)
            if(text.contains(p.pkgName))
                classSearch(text, p, foundClasses, where);
    }
    private void search(Class2 c, Collection<Class2> foundClasses) {
        String text = c.getText();

        for (Package2 p : allPackages)
            if(p != c.getPackage() && text.contains(p.pkgName))
                classSearch(text, p, foundClasses, c);
    }
    private void classSearch(String text, Package2 p, Collection<Class2> foundClasses, Object where) {
        for (Class2 c : p.classes) {
            if(!c.isFound() && c.checkHas(text)) { 
                c.setFound(where);
                foundClasses.add(c);
            }
        }
    }
    static String toText(Path p) {
        boolean exit = false;
        if(sb == null)
            sb = new StringBuilder(1000);
        sb.setLength(0);

        try(FileReader b = new FileReader(p.toFile())) {
            int n;

            while((n = b.read()) != -1) 
                sb.append((char)n);

        } catch (Exception e) {
            System.err.println(red("failed to read: ") +p);
            e.printStackTrace();
            exit = true;
        }

        if(exit)
            System.exit(0);

        return sb.toString();
    }

    private Collection<Package2> walkMyUtilsPackages() throws IOException {
        Path srcDir = myUtilsDir.resolve("src");
        int count = srcDir.getNameCount();

        HashMap<Path, Package2> map = new HashMap<>();

        Files.walkFileTree(srcDir, new SimpleFileVisitor<Path> () {
            Package2 current;

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if(current == null || file.getParent().equals(srcDir))
                    return FileVisitResult.CONTINUE;

                if(!file.getParent().equals(current.dir)) 
                    current = map.get(file.getParent());
                
                if(!file.getFileName().toString().endsWith(".java")) {
                	current.addFile(file);
                	return FileVisitResult.CONTINUE;
                }

                Class2 c = current.add(file);
                if(extras.contains(c.getCanonicalName()))
                	c.setFound("defined in extras");
                return FileVisitResult.CONTINUE;
            } 
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if(!dir.equals(srcDir)) {
                    current = new Package2(Main.this, dir, count);
                    map.put(dir, current);
                }
                return FileVisitResult.CONTINUE;
            }
        });

        List<Package2> list = new LinkedList<>();

        for (Package2 p : map.values()) 
            if(!p.classes.isEmpty())
                list.add(p);

        return Collections.unmodifiableCollection(list);
    }
    
    private static String ansi_wrap(String prefix, Object obj) { return prefix + obj + "\u001b[0m";}
    public static String black(Object obj) {return ansi_wrap("\u001b[30m",obj);}
    public static String red(Object obj) {return ansi_wrap("\u001b[31m",obj);}
    public static String green(Object obj) {return ansi_wrap("\u001b[32m",obj);}
    public static String yellow(Object obj) {return ansi_wrap("\u001b[33m",obj);}
    public static String blue(Object obj) {return ansi_wrap("\u001b[34m",obj);}
    public static String magenta(Object obj) {return ansi_wrap("\u001b[35m",obj);}
    public static String cyan(Object obj) {return ansi_wrap("\u001b[36m",obj);}
    public static String white(Object obj) {return ansi_wrap("\u001b[37m",obj);}

    private StringBuilder magenta(StringBuilder sb, String string) {
    	sb.append(magenta(string));
		return sb;
	}

	private StringBuilder yellow(StringBuilder sb, String string) {
		sb.append(yellow(string));
		return sb;
		
	}
}
