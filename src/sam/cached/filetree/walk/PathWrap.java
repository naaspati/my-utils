package sam.cached.filetree.walk;

import java.io.File;
import java.nio.file.Path;

public abstract class PathWrap implements IPathWrap {
    protected final String name;
    protected final Dir parent;
    protected Path subpath, fullpath;
    protected File file_subpath, file_fullpath;
    protected long old_last_modified, current_last_modified;
    
    public PathWrap(Dir parent, String name, long last_modified) {
        this.name = name;
        this.old_last_modified = last_modified;
        this.parent = parent;
    }

    @Override
    public String name() {
        return name;
    }
    
    @Override
    public long lastModified() {
        if(current_last_modified <= 0)
            current_last_modified = fullpathAsFile().lastModified();
        
        return current_last_modified;
    }
    @Override
    public boolean isModified() {
        return old_last_modified != lastModified();
    }
    
    @Override
    public Path subpath() {
        if(subpath == null)
            subpath = parent().subpath().resolve(name);
        new File(new File(""), "");
        return subpath;
    }

    @Override
    public Path fullpath() {
        if(fullpath == null)
            fullpath = parent().fullpath().resolve(name);
        return fullpath;
    }
    
    @Override
    public File subpathAsFile() {
        if(file_subpath == null)
            file_subpath = new File(parent().subpathAsFile(), name);
        new File(new File(""), "");
        return file_subpath;
    }

    @Override
    public File fullpathAsFile() {
        if(file_fullpath == null)
            file_fullpath = new File(parent().fullpathAsFile(), name);
        return file_fullpath;
    }

    @Override
    public boolean isDir() {
        return false;
    }

    @Override 
    public Dir parent() {
        return parent;
    }
}
