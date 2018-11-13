package sam.zip;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

//VERSION = 1.2;
public interface ZipTools {
    
	public static void zip(Path input, Path output, boolean keepRoot) throws IOException {
		Files.createDirectories(output.getParent());
		
		try(ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(output))) {
			if(Files.isRegularFile(input)) {
				ZipEntry z = new ZipEntry(input.getFileName().toString());
				zos.putNextEntry(z);
				Files.copy(input, zos);
			}
			else {
				Iterator<Path> iterator = Files.walk(input).iterator();
				final int count = keepRoot ? input.getNameCount() - 1 : input.getNameCount() ; 

				while(iterator.hasNext()) {
					Path f = iterator.next();
					
					if(f.getNameCount() <= count)
						continue;

					String s = f.subpath(count, f.getNameCount()).toString().replace('\\', '/');

					ZipEntry z = new ZipEntry(Files.isDirectory(f) ? s.concat("/") : s);
					zos.putNextEntry(z);
					if(!z.isDirectory())
						Files.copy(f, zos);
				}
			}
		}
	}
	
	public static Path unzip(Path zipfile, Path target) throws ZipException, IOException {
		if(Files.notExists(zipfile))
			throw new FileNotFoundException("file not found: "+zipfile);
		
		Path root = null;

		if(!Files.isRegularFile(zipfile)) 
			throw new IOException("not a file: "+zipfile);

		try(ZipFile z = new ZipFile(zipfile.toFile())) {
			Enumeration<? extends ZipEntry> entries = z.entries();

			while(entries.hasMoreElements()){
				ZipEntry e = entries.nextElement();
				Path t = target.resolve(e.getName());
				
				if(root == null || root.getNameCount() > t.getNameCount())
				    root = t;
				
				if(e.isDirectory()) {
				    Files.createDirectories(t);
				}
				else{
					Files.createDirectories(t.getParent());
					Files.copy(z.getInputStream(e), t, StandardCopyOption.REPLACE_EXISTING);
				}
			}
		}
		return root;
	}
}
