package sam.myutils;

import java.util.function.IntFunction;

import com.carrotsearch.hppc.IntContainer;
import com.carrotsearch.hppc.IntIntAssociativeContainer;
import com.carrotsearch.hppc.IntObjectAssociativeContainer;
import com.carrotsearch.hppc.IntObjectMap;
import com.carrotsearch.hppc.ObjectCollection;
import com.carrotsearch.hppc.ShortByteAssociativeContainer;
import com.carrotsearch.hppc.ShortContainer;
import com.carrotsearch.hppc.ShortObjectAssociativeContainer;
import com.carrotsearch.hppc.procedures.IntIntProcedure;
import com.carrotsearch.hppc.procedures.IntObjectProcedure;
import com.carrotsearch.hppc.procedures.IntProcedure;
import com.carrotsearch.hppc.procedures.ObjectProcedure;
import com.carrotsearch.hppc.procedures.ShortByteProcedure;
import com.carrotsearch.hppc.procedures.ShortObjectProcedure;
import com.carrotsearch.hppc.procedures.ShortProcedure;

public interface HPPCUtils {
	public static <E> E computeIfAbsent(IntObjectMap<E> map, int key, IntFunction<? extends E> computer) {
		E value = map.get(key);
		if (value == null) {
			value = computer.apply(key);
			map.put(key, value);
		}
		return value;
	}
	

	public static <E> void forEach(IntObjectAssociativeContainer<E> map, IntObjectProcedure<E> action) {
		map.forEach(action);
	}
	
	public static <E> void forEach(ShortObjectAssociativeContainer<E> map, ShortObjectProcedure<E> action) {
		map.forEach(action);
	}
	
	public static <E> void forEach(IntIntAssociativeContainer map, IntIntProcedure action) {
		map.forEach(action);
	}
	
	public static <E> void forEach(ShortByteAssociativeContainer map, ShortByteProcedure action) {
		map.forEach(action);
	}

	public static <E> void forEach(IntContainer list, IntProcedure action) {
		list.forEach(action);
	}
	public static <E> void forEach(ShortContainer list, ShortProcedure action) {
		list.forEach(action);
	}
	
	public static <E> void forEach(ObjectCollection<E> map, ObjectProcedure<E> action) {
		map.forEach(action);
	}
}
