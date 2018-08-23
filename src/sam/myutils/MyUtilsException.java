package sam.myutils;

public interface MyUtilsException {
	
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

}
