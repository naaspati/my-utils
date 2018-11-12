package sam.myutils;

import java.util.function.Consumer;

public interface MyUtilsException {
	static final Consumer<Throwable> DEFAULT_ON_ERROR = e -> { throw new RuntimeException(e); };

	public static long executionTime(Runnable r) {
		long t = System.nanoTime();
		r.run();
		return System.nanoTime() - t;
	}
	public static String exceptionToString(Throwable e) {
		if(e == null)
			return "";

		StringBuilder sb =  new StringBuilder()
				.append('[')
				.append(e.getClass().getSimpleName());

		if(e.getMessage() != null)
			sb.append(": ").append(e.getMessage());

		sb.append(']');

		return sb.toString();
	}

	/**
	 * throws RuntimeException when supplier throws an Exception  
	 * @param supplier
	 * @return
	 */
	public static <E> E noError(ErrorSupplier<E> supplier){
		return noError(supplier, DEFAULT_ON_ERROR);
	}
	public static <E> E noError(ErrorSupplier<E> supplier, Consumer<Throwable> onError) {
		try {
			return supplier.get();
		} catch (Exception e) {
			onError.accept(e);
		}
		return null;
	}
	public static void hideError(ErrorRunnable run) {
		hideError(run, DEFAULT_ON_ERROR);
	}
	public static void hideError(ErrorRunnable run, Consumer<Throwable> onError) {
		try {
			run.run();
		} catch (Exception e) {
			onError.accept(e);
		}
	}
}
