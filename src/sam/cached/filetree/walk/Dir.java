package sam.cached.filetree.walk;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

import sam.collection.ArrayIterator;
import sam.myutils.Checker;

public abstract class Dir extends PathWrap implements IDir {
    public static final PathWrap[] EMPTY = new PathWrap[0];
    
    protected PathWrap[] children;

    public Dir(Dir parent, String name, long lastModified) {
        super(parent, name, lastModified);
    }
    @Override
    public final boolean isDir() {
        return true;
    }
    
    public boolean isEmpty() {
        return Checker.isEmpty(children);
    }
    
    @Override
    public Iterator<IPathWrap> iterator() {
        return isEmpty() ? Collections.emptyIterator() : new ArrayIterator<>(children);
    }
    @Override
    public void forEach(Consumer<? super IPathWrap> action) {
        if(isEmpty())
            return;
        
        for (PathWrap p : children)
            action.accept(p);
    }
    @Override
    public Spliterator<IPathWrap> spliterator() {
        return isEmpty() ? Spliterators.emptySpliterator() : Arrays.spliterator(children);
    }
}
