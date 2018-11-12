package sam.io.serilizers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface OneObjectSerializer<E> {
	public E read(DataInputStream dis) throws IOException;
	public void write(DataOutputStream dos, E e) throws IOException;
	
	public static final OneObjectSerializer< String> STRING = new OneObjectSerializer<String>() {
		@Override
		public String read(DataInputStream d) throws IOException {
			return d.readUTF();
		}
		@Override
		public void write(DataOutputStream dos, String e) throws IOException {
			dos.writeUTF(e);
		}
	};
	
	public static final OneObjectSerializer< Integer> INT = new OneObjectSerializer<Integer>() {
		@Override
		public Integer read(DataInputStream d) throws IOException {
			return d.readInt();
		}
		@Override
		public void write(DataOutputStream dos, Integer e) throws IOException {
			dos.writeInt(e);
		}
	};
	public static final OneObjectSerializer< Long> LONG = new OneObjectSerializer<Long>() {
		@Override
		public Long read(DataInputStream d) throws IOException {
			return d.readLong();
		}
		@Override
		public void write(DataOutputStream dos, Long e) throws IOException {
			dos.writeLong(e);
		}
	};
	public static <E> OneObjectSerializer<E> reader(IOExceptionFunction<DataInputStream, E> mapper) {
		return new OneObjectSerializer<E>() {
			@Override
			public E read(DataInputStream dis) throws IOException {
				return mapper.apply(dis);
			}
			@Override
			public void write(DataOutputStream dos, E e) throws IOException {
				throw new IllegalAccessError("not implemented");
			}
		};
	}
	public static <E> OneObjectSerializer<E> writer(IOExceptionBiConsumer<DataOutputStream, E> mapper) {
		return new OneObjectSerializer<E>() {
			@Override
			public E read(DataInputStream dis) throws IOException {
				throw new IllegalAccessError("not implemented");
			}
			@Override
			public void write(DataOutputStream dos, E e) throws IOException {
				mapper.accept(dos, e);
			}
		};
	}
	
}
