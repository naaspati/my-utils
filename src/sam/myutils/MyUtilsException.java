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
	 
	 /**
	  * throws RuntimeException when supplier throws an Exception  
	  * @param supplier
	  * @return
	  */
	 public static <E> E noError(ErrorSupplier<E> supplier){
		 try {
			return supplier.get();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	 }
	 

}
