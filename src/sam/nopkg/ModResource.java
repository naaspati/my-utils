package sam.nopkg;

import java.io.IOException;
import java.util.Objects;

public abstract class ModResource<E> implements AutoCloseable {
	protected E data;
	protected int mod;
	private boolean loaded;
	
	public E get() {
		if(!loaded) {
			data = read();
			loaded = true;
		}
		return data;
	}
	public void set(E data) {
		loaded = true;
		
		if(!isEqual(data, this.data)) {
			this.data = data;
			mod++;
		}
	}
	
	@Override
	public void close() throws Exception {
		if(mod > 0)
			write(data); 
	}
	
	protected abstract E read();
	protected abstract void write(E e) throws IOException;
	
	protected boolean isEqual(E a, E b) {
		return Objects.equals(a, b);
	}
}
