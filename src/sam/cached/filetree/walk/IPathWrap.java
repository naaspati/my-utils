package sam.cached.filetree.walk;

import java.io.File;
import java.nio.file.Path;

public interface IPathWrap {
    String name();
    Path subpath();
    Path fullpath();
    File fullpathAsFile();
    File subpathAsFile();
    IDir parent();
    boolean isDir();
    long lastModified();
    boolean isModified();
}
