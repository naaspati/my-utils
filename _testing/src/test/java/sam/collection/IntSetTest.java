package sam.collection;


import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Random;
import java.util.TreeSet;
import java.util.function.IntConsumer;

import org.junit.jupiter.api.Test;

public class IntSetTest {
	private static final int SAMPLE_SIZE = 10000;

	static IntSet listStatic = new IntSet(10);
	static TreeSet<Integer> arraylistStatic = new TreeSet<>();

	static Runnable asrt = () -> check(listStatic, arraylistStatic);
	
	private static void check(IntSet set, TreeSet<Integer> treeset) {
		assertEquals(treeset.size(), set.size(), "size:");

		int k = 0;
		for (Integer n : treeset)
			assertEquals((int)n, set.get(k++), "at index: "+(k - 1));	
	}

	private int[] array() {
		int[] array = new int[SAMPLE_SIZE];
		Random r = new Random();

		for (int i = 0; i < array.length; i++)
			array[i] = r.nextInt();

		return array;
	}

	@Test
	public void testOrder() {
		int[] array = array();
		IntSet set = new IntSet(array);

		int[] result = set.toArray();
		int[] expected = Arrays.stream(array).distinct().sorted().toArray();

		assertArrayEquals(expected, result); 
	}

	@Test
	public void add() {
		listStatic.add(25);
		arraylistStatic.add(25);
		asrt.run();

		listStatic.add(250);
		arraylistStatic.add(250);
		asrt.run();
	}
	@Test 
	public void trimToSize() {
		IntSet list = new IntSet(10);
		assertEquals(10, list.capacity());
		list.trimToSize();
		assertEquals(0, list.capacity());
	} 

	@Test
	public void addAll(){
		listStatic.addAll(10, 11, 12);
		arraylistStatic.addAll(Arrays.asList(10, 11, 12));
		asrt.run();
	}

	@Test
	public  void contains() {
		listStatic.addAll(10, 11, 12, 14);
		arraylistStatic.addAll(Arrays.asList(10, 11, 12, 14));
		asrt.run();

		for (int n : new int[]{10, 11, 12, 14})
			assertTrue(listStatic.contains(n));
	}

	@Test
	public void indexOf() {
		IntSet list = new IntSet(new int[]{10, 11, 12, 14});
		int[] list2 = {10, 11, 12, 14};

		int i = 0;
		for (int n : list2) 
			assertEquals(i++, list.indexOf(n));
	}

	IntSet list;
	TreeSet<Integer> list2;
	Random r;

	void arrayTest(boolean firstAdd, IntConsumer filler, Runnable afterFill) {
		list = new IntSet();
		list2 = new TreeSet<>();
		r = new Random(10);

		if(firstAdd) {
			list.add(0);
			list2.add(0);	
		}

		for (int i = 0; i < SAMPLE_SIZE; i++)
			filler.accept(i);

		if(afterFill != null) {
			check(list, list2);
			r = new Random(10);
			afterFill.run();
		}

		check(list, list2);
	}

	private IntConsumer simplefill = i -> {
		int n = r.nextInt();
		list.add(n);
		list2.add(n);	
	};

	@Test
	public void toArray() {
		arrayTest(false, simplefill, null);
	}

	@Test
	public void remove() {
		arrayTest(false, simplefill, () -> {
			ArrayList<Integer> list3 = new ArrayList<>(list2);
			for (int i = 0; i < SAMPLE_SIZE; i++) {
				Integer n = list3.get(r.nextInt(list.size()));
				list.remove(n);
				list2.remove(n);	
			}
		});
	}
	@Test
	public void randomRemoveAll() {
		arrayTest(true, i -> {
			int[] n = {r.nextInt(), r.nextInt(), r.nextInt()};
			list.addAll(n);
			list2.addAll(Arrays.asList(n[0], n[1], n[2]));
		}, () -> {
			int size = list.size()/2;
			for (int i = 0; i < size; i++) {
				int[] n = {r.nextInt(), r.nextInt(), r.nextInt()};
				list.removeAll(n);
				list2.removeAll(Arrays.asList(n[0], n[1], n[2]));
			}
		});
	}
	@Test 
	public void removeIf() {
		arrayTest(false, simplefill, () -> {
			list.removeIf(i -> i < 0);
			list2.removeIf(i -> i < 0);
		});
	}
	@Test 
	public void testConcurrentModification()  {
		assertThrows(ConcurrentModificationException.class, () -> {
			IntSet list = new IntSet(new int[]{10});
			list.forEach(i -> {
				list.add(10);
				list.add(10);
				list.add(11);
			});
		});
		
	}
}
