package sam.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class InputStreamSource {
    private InputStream is;
    private File file;
    private Path path;

    public InputStreamSource(InputStream is) {
        this.is = is;
    }

    public InputStreamSource(File file) {
        this.file = file;
    } 
    public InputStreamSource(Path path) {
        this.path = path;
    } 
    
    public InputStream getInputStream() throws IOException {
        if(is != null)
            return is;
        if(file != null)
            return new FileInputStream(file);
        if(path != null)
            return Files.newInputStream(path, StandardOpenOption.READ);
        
        throw new NullPointerException();
    }
}
