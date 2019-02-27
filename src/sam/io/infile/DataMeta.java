package sam.io.infile;

public class DataMeta {
	public final long position;
	public final int size;

	public DataMeta(long position, int size) {
		this.position = position;
		this.size = size;
	}

	public final long position() { return position; }
	public final long size() { return size; }

	@Override
	public String toString() {
		return "DataMeta [position=" + position + ", size=" + size + "]";
	}
}
