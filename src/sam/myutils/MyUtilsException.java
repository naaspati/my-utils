package sam.myutils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import sam.string.StringWriter2;

public interface MyUtilsException {
	static final Consumer<Throwable> DEFAULT_ON_ERROR = e -> { throw new RuntimeException(e); };

	public static long executionTime(Runnable r) {
		long t = System.nanoTime();
		r.run();
		return System.nanoTime() - t;
	}
	
	public static String toString(Throwable e) {
		if(e == null)
			return "";
		return append(new StringBuilder(), e, false).toString();
	}

	public static StringBuilder append(StringBuilder sb, Throwable e, boolean stackTrace) {
		if(e == null)
			return sb;
		
		if(stackTrace) {
			StringWriter2 sw =  new StringWriter2(sb);
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
		} else {
			sb.append('[')
			.append(e.getClass().getSimpleName());

			if(e.getMessage() != null)
				sb.append(": ").append(e.getMessage());

			sb.append(']');			
		}
		return sb;
	}
	/**
	 * throws RuntimeException when supplier throws an Exception  
	 * @param supplier
	 * @return
	 */
	public static <E> E noError(ErrorSupplier<E> supplier){
		try {
			return supplier.get();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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

	public static <V> V toUnchecked(Callable<V> action) {
		try {
			return action.call();
		} catch (Exception e) {
			if(e instanceof IOException)
				throw new UncheckedIOException((IOException)e);
			else 
				throw new RuntimeException(e);
		}
	}
	
	public static ArrayList<Throwable> causes(Throwable e) {
		ArrayList<Throwable> list = new ArrayList<>();
		while(e != null) {
			list.add(e);
			e = e.getCause();
		}
		return list;
	}
}
