package test.sam.collection;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Random;
import java.util.TreeSet;
import java.util.function.IntConsumer;

import org.junit.Test;

import sam.collection.IntSet;

public class IntSetTest {
	private static final int SAMPLE_SIZE = 10000;

	static IntSet listStatic = new IntSet(10);
	static TreeSet<Integer> arraylistStatic = new TreeSet<>();

	static Runnable asrt = () -> assertArrayEquals(arraylistStatic.stream().mapToInt(Integer::intValue).toArray(), listStatic.toArray());   

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
			int[] a = list.toArray();
			int[] b = list2.stream().mapToInt(Integer::intValue).toArray();

			assertArrayEquals(a, b);

			r = new Random(10);
			afterFill.run();
		}

		int[] a = list.toArray();
		int[] b = list2.stream().mapToInt(Integer::intValue).toArray();

		assertArrayEquals(a, b);
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
	@Test (expected = ConcurrentModificationException.class, timeout=10) 
	public void testConcurrentModification()  {
		IntSet list = new IntSet(new int[]{10});
		list.forEach(i -> {
			list.add(10);
			list.add(10);
			list.add(11);
		});
	}
}
