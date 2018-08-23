package sam.myutils;

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

}
