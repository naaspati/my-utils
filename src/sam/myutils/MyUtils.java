package sam.myutils;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;

import sam.string.StringUtils;


public final class MyUtils {
    public static final double VERSION = 1.214;

    private MyUtils() {}

    //2^10 = 1024
    /**
			'k', kilo  (2^10)^1 = 1024
			'M', mega  (2^10)^2 = 1048576
			'G', giga  (2^10)^3 = 1073741824
			'T', tera  (2^10)^4 = 1099511627776
			'P', peta  (2^10)^5 = 1125899906842624
			'E', exa   (2^10)^6 = 1152921504606846976 //this the limit for this method as Long.MAX_VALUE = 9223372036854775807
			'Z', zetta (2^10)^7 = 1180591620717411303424
			'Y'  yotta (2^10)^8 = 1208925819614629174706176
     */
    private static final char[] units = {
            ' ',
            'k', 
            'M', 
            'G',   
            'T', 
            'P', 
            'E', 
            'Z', 
            'Y'  
    };
    private static final long[] bytesSize = {
            1024L,	//1  -> k
            1048576L,	//2 -> M
            1073741824L,	//3 -> G
            1099511627776L,	//4 -> T
            1125899906842624L,	//5 -> P
            1152921504606846976L	//6 -> E
    };

    public static String bytesToHumanReadableUnits(long bytes, boolean exact) {
        if(bytes < 1024)
            return String.valueOf(bytes).concat(" bytes");

        return bytesToHumanReadableUnits(bytes, exact, new StringBuilder()).toString();
    }
    public static StringBuilder bytesToHumanReadableUnits(long bytes, boolean exact, StringBuilder sink){

        if(bytes < 1024)
            return sink.append(bytes).append(" bytes");

        if(exact) {
            int index = 1;

            for (; index < bytesSize.length; index++) {
                if(bytes < bytesSize[index])
                    break;
            }

            int length = sink.length();
            for (--index; index >= 0; index--) {
                long a = bytes/bytesSize[index];
                bytes -= a*bytesSize[index];
                sink.append(a).append(' ').append(units[index+1]).append('b');
                length = sink.length();
                sink.append(", ");
            }
            if(bytes != 0)
                sink.append(bytes).append(" bytes");
            else
                sink.setLength(length);

        } else {
            for (int i = 0; i < bytesSize.length - 1; i++) {
                long unit = bytesSize[i];
                if(unit == bytes)
                    return sink.append(1).append(' ').append(units[i]);
                else if(bytes > unit && bytes < bytesSize[i + 1])
                    return sink.append(StringUtils.doubleToString(((double)bytes)/bytesSize[i], 3)).append(' ').append(units[i+1]).append('b');
            }
        }
        return sink;
    }
    public static void beep(int count) {
        char[] chars = new char[count];
        Arrays.fill(chars, '\007');
        System.out.println(new String(chars));
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
    /**
     * 
     * @param ifNotTrueThrow if not true, throw new IllegalArgumentException(msg);
     * @param msg
     */
	public static void checkArgument(boolean ifNotTrueThrow, String msg) {
		if(!ifNotTrueThrow)
			throw new IllegalArgumentException(msg);
	}
	/**
     * 
     * @param condition if not true, throw new IllegalArgumentException(msg);
     * @param msg
     */
	public static void checkArgument(boolean ifNotTrueThrow, Supplier<String> msgSupplier) {
		if(!ifNotTrueThrow)
			throw new IllegalArgumentException(msgSupplier.get());
	}
	/**
	 * return ifTrue ? than : otherWise;
	 * @param ifTrue 
	 * @param expected
	 * @param other
	 * @return
	 */
	public static <E> E elvis(boolean ifTrue, E than, E otherwise) {
	    return ifTrue ? than : otherwise;
	}
	public static <E> E elvis(boolean ifTrue, E than, Supplier<E> otherwise) {
		return ifTrue ? than : otherwise.get();
    }
	public static <E, F> F map(E value, Function<E, F> mapper) {
	    return mapper.apply(value);
	}
	/**
	 * return value != null ? value : orElse
	 * 
	 * @param value
	 * @param orElse
	 * @return
	 */
	public static <E> E nullSafe(E value, E orElse) {
		return elvis(value != null, value, orElse);
	}
	/**
	 * 
	 * @param o
	 * @param to
	 * @return if o is null or not instanceof E, then return null, else casted element
	 */
	public static <E> E cast(Object o, Class<E> to) {
	    if(o == null || o.getClass() != to)
	        return null;
	    
	    return to.cast(o);
	}
	public static long executionTime(Runnable r) {
		long t = System.nanoTime();
		r.run();
		return System.nanoTime() - t;
	}
}