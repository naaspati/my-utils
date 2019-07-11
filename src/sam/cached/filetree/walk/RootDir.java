package sam.cached.filetree.walk;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sam.io.serilizers.DataReader;
import sam.io.serilizers.DataWriter;
import sam.myutils.Checker;
import sam.nopkg.Resources;

public abstract class RootDir {
    private static final long MARKER = -8654781922818211134L;

    public DirImpl0 loadCache(Path rootDir, Path cachedFilePath) throws IOException {
        if(!Files.isDirectory(rootDir))
            throw (Files.notExists(rootDir) ? new FileNotFoundException(rootDir.toString()) : new IOException("not a dir: "+rootDir));

        try(FileChannel fc = FileChannel.open(cachedFilePath, READ);
                Resources r = Resources.get();
                DataReader reader = new DataReader(fc, r.buffer());
                ) {
            reader.setChars(r.chars());
            reader.setDecoder(r.decoder());
            reader.setStringBuilder(r.sb());

            if(reader.readLong() != MARKER)
                throw new IOException("not a CachedRoot file: "+cachedFilePath);

            Path p2 = Paths.get(reader.readUTF());
            if(!Files.isSameFile(rootDir, p2))
                throw new IOException("bad cache file: expected: "+rootDir+", was: "+ p2);

            return newDirImpl(null, reader);
        }
    }

    protected abstract DirImpl0 newDirImpl(DirImpl0 parent, DataReader reader) throws IOException;

    public void writeCache(DirImpl0 rootDir, Path rootDirpath, Path cachedFilePath) throws IOException {
        try(FileChannel fc = FileChannel.open(cachedFilePath, WRITE, CREATE, TRUNCATE_EXISTING);
                Resources r = Resources.get();
                DataWriter writer = new DataWriter(fc, r.buffer());
                ) {
            writer.setEncoder(r.encoder());

            writer.writeLong(MARKER);
            writer.writeUTF(rootDirpath.toString());

            rootDir.write(writer);
        }
    }

    private static interface Writeable {
        void write(DataWriter writer) throws IOException ;
        default void _write(DataWriter writer, long modified) throws IOException {
            IPathWrap p = (IPathWrap) this;
            writer.writeBoolean(p.isDir());
            writer.writeUTF(p.name());
            writer.writeLong(modified);
        }
    }

    protected static abstract class DirImpl0 extends Dir implements Writeable {
        protected PathWrap[] cached;

        public DirImpl0(Dir parent, String name, File file, long lastModified) {
            super(parent, name, lastModified);
            this.file_fullpath = file;
        }

        public DirImpl0(Dir parent, DataReader reader) throws IOException {
            super(parent, reader.readUTF(), reader.readLong());

            this.cached = new PathWrap[reader.readInt()];

            for (int i = 0; i < cached.length; i++)
                cached[i] = create(reader);
        }

        @Override
        public void write(DataWriter writer) throws IOException {
            _write(writer, old_last_modified);

            PathWrap[] list = this.children == null ? cached : this.children;
            writer.writeInt(list.length);

            for (PathWrap p : list) 
                ((Writeable)p).write(writer);
        }

        @Override
        public Dir parent() {
            return parent;
        }

        private static WeakReference<Map<String, PathWrap>> wtempmap = new WeakReference<Map<String,PathWrap>>(null);
        private static WeakReference<ArrayList<PathWrap>> wtempList = new WeakReference<>(null);

        protected PathWrap[] list() {
            if(children != null)
                return children;

            File dir = this.fullpathAsFile();

            if(cached != null && !isModified())
                return this.children = cached;
            else {
                String[] list = dir.list();
                final ArrayList<PathWrap> files = arraylist();
                files.clear();

                if(Checker.isEmpty(list)) {
                    // do nothing
                } else if(Checker.isEmpty(cached)) {
                    for (int i = 0; i < list.length; i++) {
                        PathWrap f = create(list[i]);
                        if(f != null)
                            files.add(f);
                    } 
                } else {
                    Map<String, PathWrap> tempmap = wtempmap.get();
                    if(tempmap == null)
                        wtempmap = new WeakReference<Map<String,PathWrap>>(tempmap = new HashMap<>());

                    Checker.assertTrue(tempmap.isEmpty());

                    for (PathWrap s : cached)
                        tempmap.put(s.name, s);

                    for (int i = 0; i < list.length; i++) {
                        PathWrap f =  tempmap.get(list[i]);

                        if(f == null)
                            f = create(list[i]);

                        if(f != null)
                            files.add(f);
                    } 
                    tempmap.clear();
                }

                this.children = files.isEmpty() ? EMPTY : files.toArray(new PathWrap[files.size()]);
                files.clear();
                return this.children;
            }
        }
        private ArrayList<PathWrap> arraylist() {
            ArrayList<PathWrap> list = wtempList.get();
            if(list != null) {
                Checker.assertTrue(list.isEmpty());
                return list;
            }

            wtempList = new WeakReference<ArrayList<PathWrap>>(list = new ArrayList<>()); 
            return list;
        }

        protected String[] applyFilter(String[] files_name) {
            return files_name;
        }

        protected abstract PathWrap create(DataReader reader) throws IOException;
        protected abstract PathWrap create(String name);
    }

    protected static class PathWrapImpl0 extends PathWrap implements Writeable {
        public PathWrapImpl0(Dir parent, String name, File file, long lastModified) {
            super(parent, name, lastModified);
            this.file_fullpath = file;
        }

        @Override
        public void write(DataWriter writer) throws IOException {
            _write(writer, old_last_modified);
        }
        public PathWrapImpl0(DirImpl0 parent, DataReader reader) throws IOException {
            super(parent, reader.readUTF(), reader.readLong());
        }
    }
}
