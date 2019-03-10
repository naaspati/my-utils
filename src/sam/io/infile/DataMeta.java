package sam.io.infile;

import java.io.Serializable;
import java.util.Arrays;

public class DataMeta implements Serializable {
	private static final long serialVersionUID = 5037265506963712673L;

	public transient static final int BYTES = Long.BYTES + Integer.BYTES;
	
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
