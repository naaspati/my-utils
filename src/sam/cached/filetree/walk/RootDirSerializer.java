package sam.cached.filetree.walk;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

import sam.collection.MappedIterator;
import sam.functions.IOExceptionConsumer;
import sam.io.BufferConsumer;
import sam.io.BufferSupplier;
import sam.io.IOUtils;
import sam.io.serilizers.StringIOUtils;
import sam.nopkg.Resources;
import sam.reference.WeakAndLazy;

public class RootDirSerializer {

	private static final WeakAndLazy<ArrayList<PathWrap>> wfiles = new WeakAndLazy<>(ArrayList::new);
	private static final WeakAndLazy<ArrayList<Dir>> wdirs = new WeakAndLazy<>(ArrayList::new);

	public static void save(RootDir root, Path path) throws IOException {
		Objects.requireNonNull(root);

		synchronized (wfiles) {
			ArrayList<PathWrap> files = null;
			ArrayList<Dir> dirs = null;

			try(FileChannel fc = FileChannel.open(path, WRITE, CREATE, TRUNCATE_EXISTING);
					Resources r = Resources.get();) {

				int size = root.deepCount();

				files = wfiles.get();
				files.ensureCapacity(size + 10);
				dirs = wdirs.get();

				ArrayList<PathWrap> files2 = files;
				ArrayList<Dir> dirs2 = dirs;
				dirs.add(root);

				DefaultWalker.walk(root, e -> {
					if(e == null)
						return;

					if(e.isDir()) {
						Dir d = (Dir) e;
						dirs2.add(d);
					} else {
						files2.add(e);
					} 
				});

				dirs.sort(Comparator.comparingInt(Dir::getId));
				ByteBuffer buf = r.buffer();
				String s = root.fullpath().toString();

				buf.putInt(s.length());

				for (int i = 0; i < s.length(); i++) {
					if(buf.remaining() < 2)
						IOUtils.write(buf, fc, true);
					buf.putChar(s.charAt(i));
				}

				buf.putInt(dirs.stream().mapToInt(Dir::getId).max().orElse(0));
				buf.putInt(dirs.size());
				buf.putInt(files.size());

				final int BYTES = 4 * 3 + 8;

				for (int i = 0; i < dirs.size(); i++) {
					if(buf.remaining() < BYTES)
						IOUtils.write(buf, fc, true);

					Dir d = dirs.get(i);

					buf
					.putInt(d.getId())
					.putInt(d == root ? -1 : d.parent.getId())
					.putInt(d.count())
					.putLong(d.lastmod);
				}
				
				for (int i = 0; i < files.size(); i++) {
					if(buf.remaining() < 12)
						IOUtils.write(buf, fc, true);

					PathWrap d = files.get(i);

					buf
					.putInt(d.parent.getId())
					.putLong(d.lastmod);
				}

				IOUtils.write(buf, fc, true);

				StringIOUtils.writeJoining(new MappedIterator<>(dirs.iterator(), d -> d.name), "\n", BufferConsumer.of(fc, false), buf, r.chars(), r.encoder());
				buf.clear();
				r.chars().clear();
				StringIOUtils.writeJoining(new MappedIterator<>(files.iterator(), d -> d.name), "\n", BufferConsumer.of(fc, false), buf, r.chars(), r.encoder());
			} finally {
				if(files != null)
					files.clear();
				if(dirs != null)
					dirs.clear();
			}
		}
	}



	public static RootDir read(Path path) throws IOException {
		Objects.requireNonNull(path);

		synchronized (wfiles) {
			try(FileChannel fc = FileChannel.open(path, READ);
					Resources r = Resources.get();) {
				ByteBuffer buf = r.buffer();
				StringBuilder sb = r.sb();

				if(IOUtils.read(buf, false, fc) < 4)
					throw new IOException("empty file");

				int len = buf.getInt();
				while(sb.length() != len) {
					if(buf.remaining() < 2) {
						IOUtils.compactOrClear(buf);
						if(IOUtils.read(buf, false, fc) < 2)
							throw new IOException("bad file");
					}
					sb.append(buf.getChar());
				}

				Path rootPath = Paths.get(sb.toString());
				IOUtils.readIf(buf, fc, 8);

				int max = buf.getInt();
				int dirs_size = buf.getInt();
				int files_size = buf.getInt();

				Dir[] dirs = new Dir[max + 1];

				final int BYTES = 4 * 3 + 8;
				int[] dirs_ids = new int[dirs_size];
				int[] dirs_parent_ids = new int[dirs_size];
				int[] dirs_child_counts = new int[dirs_size];
				long[] dirs_last_mods = new long[dirs_size];

				for (int i = 0; i < dirs_size; i++) {
					IOUtils.readIf(buf, fc, BYTES);

					dirs_ids[i] = buf.getInt();
					dirs_parent_ids[i] = buf.getInt();
					dirs_child_counts[i] = buf.getInt();
					dirs_last_mods[i] = buf.getLong();
				}

				int[] files_parent_ids = new int[files_size];
				long[] files_last_mods = new long[files_size];

				for (int i = 0; i < files_size; i++) {
					IOUtils.readIf(buf, fc, 12);

					files_parent_ids[i] = buf.getInt();
					files_last_mods[i] = buf.getLong();
				}

				int[] children_indexes = new int[dirs.length]; 

				IOExceptionConsumer<String> collector = new IOExceptionConsumer<String>() {
					boolean is_dirs = true;
					int index = 0;

					@Override
					public void accept(String e) throws IOException {
						if(is_dirs) {
							int parent_id = dirs_parent_ids[index];
							int id = dirs_ids[index];
							Dir d;

							if(parent_id == -1 && id == 0) {
								d = dirs[0] = new RootDir(rootPath);
							} else {
								Dir parent = dirs[parent_id];
								d = dirs[id] = (Dir) parent.resolve(e);
								int n = children_indexes[parent_id]++; 
								parent.children[n] = d;
							}
							
							d.children = new PathWrap[dirs_child_counts[index]];
							d.lastmod = dirs_last_mods[index];

							index++;
							if(index >= dirs_size) {
								is_dirs = false;
								index = 0;
							}
						} else {
							int parent_id = files_parent_ids[index];
							Dir parent = dirs[parent_id];
							PathWrap f = parent.resolve(e);
							int n = children_indexes[parent_id]++; 
							parent.children[n] = f;
							f.lastmod = files_last_mods[index];

							index++;
						}
					}
				};

				sb.setLength(0);
				IOUtils.compactOrClear(buf);
				StringIOUtils.collect(BufferSupplier.of(fc, buf), '\n', collector, r.decoder(), r.chars(), sb);

				return (RootDir) dirs[0];
			}
		}
	}
}
