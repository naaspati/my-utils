package sam.myutils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


public final class MyUtils {
	public static final double VERSION = 1.214;

	private MyUtils() {}

	/** {@link MyUtilsBytes#bytesToHumanReadableUnits(long,boolean)} */
	public static String bytesToHumanReadableUnits(long bytes, boolean exact) { return MyUtilsBytes.bytesToHumanReadableUnits(bytes,exact); }
	/** {@link MyUtilsBytes#bytesToHumanReadableUnits(long,boolean,StringBuilder)} */
	public static StringBuilder bytesToHumanReadableUnits(long bytes, boolean exact, StringBuilder sink){ return MyUtilsBytes.bytesToHumanReadableUnits(bytes,exact,sink); }

	/** {@link MyUtilsCmd#beep(int)} */
	public static void beep(int count) {  MyUtilsCmd.beep(count); }

	/** {@link MyUtilsThread#runOnDeamonThread(Runnable)} */
	public static void runOnDeamonThread(Runnable r) {  MyUtilsThread.runOnDeamonThread(r); }
	/** {@link MyUtilsThread#run(boolean,Runnable)} */
	public static void run(boolean isDeamon, Runnable r) {  MyUtilsThread.run(isDeamon,r); }
	/** {@link MyUtilsThread#addShutdownHook(Runnable)} */
	public static void addShutdownHook(Runnable r) {  MyUtilsThread.addShutdownHook(r); }

	/** {@link MyUtilsException#executionTime(Runnable)} */
	public static long executionTime(Runnable r) { return MyUtilsException.executionTime(r); }
	/** {@link MyUtilsException#exceptionToString(Throwable)} */
	public static String exceptionToString(Throwable e) { return MyUtilsException.exceptionToString(e); }
	
	/** {@link MyUtilsCheck#checkArgument(boolean,String)} */
	 public static void checkArgument(boolean ifNotTrueThrow, String msg) {  MyUtilsCheck.checkArgument(ifNotTrueThrow,msg); }
	/** {@link MyUtilsCheck#checkArgument(boolean,Supplier)} */
	 public static void checkArgument(boolean ifNotTrueThrow, Supplier<String> msgSupplier) {  MyUtilsCheck.checkArgument(ifNotTrueThrow,msgSupplier); }
	/** {@link MyUtilsCheck#isEmpty(String)} */
	 public static boolean isEmpty(String s) { return MyUtilsCheck.isEmpty(s); }
	/** {@link MyUtilsCheck#isEmptyTrimmed(String)} */
	 public static boolean isEmptyTrimmed(String s) { return MyUtilsCheck.isEmptyTrimmed(s); }
	/** {@link MyUtilsCheck#isEmpty(Collection)} */
	 public static boolean isEmpty(Collection<?> s) { return MyUtilsCheck.isEmpty(s); }
	
	 public static boolean isEmpty(Map<?, ?> s) {return MyUtilsCheck.isEmpty(s);}


	/** {@link MyUtilsSystem#lookup(String,String)} */
	public static String lookup(String key, String defaultValue) { return MyUtilsSystem.lookup(key,defaultValue); }
	/** {@link MyUtilsSystem#lookup(String)} */
	public static String lookup(String key) { return MyUtilsSystem.lookup(key); }

	/** {@link MyUtilsExtra#elvis(boolean,E,E)} */
	public static <E> E elvis(boolean ifTrue, E than, E otherwise) { return MyUtilsExtra.elvis(ifTrue,than,otherwise); }
	/** {@link MyUtilsExtra#elvis(boolean,E,Supplier)} */
	public static <E> E elvis(boolean ifTrue, E than, Supplier<E> otherwise) { return MyUtilsExtra.elvis(ifTrue,than,otherwise); }
	/** {@link MyUtilsExtra#map(Object, Function)} */
	public static <E, F> F map(E value, Function<E, F> mapper) { return MyUtilsExtra.map(value, mapper);}
	/** {@link MyUtilsExtra#nullSafe(E,E)} */
	public static <E> E nullSafe(E value, E orElse) { return MyUtilsExtra.nullSafe(value,orElse); }
	/** {@link MyUtilsExtra#cast(Object,Class)} */
	public static <E> E cast(Object o, Class<E> to) { return MyUtilsExtra.cast(o,to); }

	@SafeVarargs
	public static <E> void forEach(Consumer<E> consumer, E...data ) { MyUtilsExtra.forEach(data, consumer);}
	/** {@link MyUtilsExtra#forEach(E[],Consumer)} */
	 public static <E> void forEach(E[] data, Consumer<E> consumer) {  MyUtilsExtra.forEach(data,consumer); }
	/** {@link MyUtilsExtra#forEach(Iterable)} */
	 public static <E> void forEach(Iterable<E> data, Consumer<E> consumer) {  MyUtilsExtra.forEach(data,consumer); }
	/** {@link MyUtilsExtra#forEach(Iterator)} */
	 public static <E> void forEach(Iterator<E> data, Consumer<E> consumer) {  MyUtilsExtra.forEach(data,consumer); }




}