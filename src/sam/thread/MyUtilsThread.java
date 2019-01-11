package sam.thread;

import java.util.Arrays;
import java.util.stream.Collectors;

public interface MyUtilsThread {
	/**
     * run on a new deamon thread
     * @param r
     */
    public static void runOnDeamonThread(Runnable r) {
        run(true, r);
    }
    /**
     * run on a new thread
     * @param isDeamon
     * @param r
     */
    public static void run(boolean isDeamon, Runnable r) {
        Thread t = new Thread(r);
        t.setDaemon(isDeamon);
        t.start();
    }
    public static void addShutdownHook(Runnable r) {
    	Runtime.getRuntime().addShutdownHook(new Thread(r));
    }
    public static StackTraceElement stackLocation() {
		return Thread.currentThread().getStackTrace()[2];
	}
	public static void printstackLocation(String msg) {
		System.out.println((msg == null ? "" : msg+" ")+Thread.currentThread().getStackTrace()[2]);
	}
	public static void printstackLocation() {
		System.out.println(Thread.currentThread().getStackTrace()[2]);
	}
	public static void printTrack(int depth) {
		System.out.println(Arrays.stream(Thread.currentThread().getStackTrace()).skip(2).limit(depth).map(String::valueOf).collect(Collectors.joining("\n  ")));
	}

}
