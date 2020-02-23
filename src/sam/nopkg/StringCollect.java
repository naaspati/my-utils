package sam.nopkg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public final class StringCollect {
	private static final List<Consumer<StringBuilder>> list = Collections.synchronizedList(new ArrayList<>());
	private StringCollect() { }

	public static void add(Consumer<StringBuilder> toString) {
		list.add(toString);
	}
	
	/**
	 * this removes need for calling Stats.add(consumer) in static {} block
	 * @param e
	 * @param toString
	 * @return
	 */
	public static <E> E add(E e, Consumer<StringBuilder> toString) {
		list.add(toString);
		return e;
	}
	
	public static StringBuilder asString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) 
			list.get(i).accept(sb);
		return sb;
	} 

}
