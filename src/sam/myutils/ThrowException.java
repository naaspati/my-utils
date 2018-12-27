package sam.myutils;

public interface ThrowException {
	
	public static <E> E notYetImplemented() throws IllegalAccessError {
		throw new IllegalAccessError("NOT YET IMPLEMENTED");
	}
	public static <E> E illegalAccessError() throws IllegalAccessError {
		throw new IllegalAccessError();
	}
	public static <E> E illegalAccessError(String msg) throws IllegalAccessError {
		throw new IllegalAccessError(msg);
	}
	public static <E> E illegalStateException() throws IllegalStateException {
		throw new IllegalStateException();
	}
	public static <E> E illegalStateException(String msg) throws IllegalStateException {
		throw new IllegalStateException(msg);
	}
	public static <E> E illegalArgumentException() throws IllegalArgumentException {
		throw new IllegalArgumentException();
	}
	public static <E> E illegalArgumentException(String msg) throws IllegalArgumentException {
		throw new IllegalArgumentException(msg);
	}
}
