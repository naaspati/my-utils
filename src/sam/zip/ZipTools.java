package sam.zip;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
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
				zos.closeEntry();
			} else {
				Iterator<Path> iterator = Files.walk(input).iterator();
				final int count = keepRoot ? input.getNameCount() - 1 : input.getNameCount();
				byte[] bytes = new byte[8 * 1024];

				while(iterator.hasNext()) {
					Path f = iterator.next();
					
					if(f.getNameCount() <= count)
						continue;

					String s = f.subpath(count, f.getNameCount()).toString().replace('\\', '/');
					ZipEntry z = new ZipEntry(Files.isDirectory(f) ? s.concat("/") : s);
					zos.putNextEntry(z);
					
					if(!z.isDirectory()) {
					    try(InputStream is = Files.newInputStream(f)) {
                            pipe(is, zos, bytes);
                        }
					}
					zos.closeEntry();
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

		try(InputStream is = Files.newInputStream(zipfile);
		        ZipInputStream zis = new ZipInputStream(is)) {
		    
		    byte[] bytes = new byte[8 * 1024];
		    ZipEntry e;
		    
			while((e = zis.getNextEntry()) != null){
				Path t = target.resolve(e.getName());
				
				if(root == null || root.getNameCount() > t.getNameCount())
				    root = t;
				
				if(e.isDirectory()) 
				    Files.createDirectories(t);
				else{
					Files.createDirectories(t.getParent());
					try(OutputStream os = Files.newOutputStream(t, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
					    pipe(zis, os, bytes);
                    }
				}
				zis.closeEntry();
			}
		}
		return root;
	}

    public static void pipe(InputStream zis, OutputStream os, byte[] bytes) throws IOException {
        int n = 0;
        while((n = zis.read(bytes)) != -1) 
            os.write(bytes, 0, n);
    }
}
