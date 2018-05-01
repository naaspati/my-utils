package sam.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class OutputStreamSource {
    private OutputStream is;
    private Path path;
    private StandardOpenOption[] options;

    public OutputStreamSource(OutputStream is) {
        this.is = is;
    }
    public OutputStreamSource(Path path, StandardOpenOption...options) {
        this.path = path;
        this.options = options;
    } 
    
    public OutputStream getOutputStream() throws IOException {
        if(is != null)
            return is;
        if(path != null)
            return Files.newOutputStream(path, options);
        
        throw new NullPointerException();
    }
}
