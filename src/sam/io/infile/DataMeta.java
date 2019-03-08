package sam.io.infile;

import java.util.Arrays;

public class DataMeta {
	public static final int BYTES = Long.BYTES + Integer.BYTES;
	
	public final long position;
	public final int size;

	public DataMeta(long position, int size) {
		this.position = position;
		this.size = size;
	}

	public final long position() { return position; }
	public final long size() { return size; }
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(new long[]{position, size});
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DataMeta))
			return false;
		
		DataMeta other = (DataMeta) obj;
		return position == other.position && size == other.size;
	}

	@Override
	public String toString() {
		return "DataMeta [position=" + position + ", size=" + size + "]";
	}
}
