package sam.nopkg;

public abstract class LazyLoadedData<E> {
	protected E data;
	protected boolean modified;

	public abstract E init() ;
	public abstract void save() ;
	public void setModified(boolean b) {
		this.modified = b;
	}
	public boolean isModified() {
		return modified;
	}
	public E getData() {
		if(data == null)
			data = init();
		return data;
	}
}
