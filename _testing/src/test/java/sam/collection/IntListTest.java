package sam.collection;




import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Random;
import java.util.function.IntConsumer;

import org.junit.jupiter.api.Test;

public class IntListTest {
	private static final int SAMPLE_SIZE = 10000;

	static IntList listStatic = new IntList(10);
	static ArrayList<Integer> arraylistStatic = new ArrayList<>();

	static Runnable asrt = () -> check(listStatic, arraylistStatic);
	
	private static void check(IntList list, List<Integer> arraylist) {
		assertEquals(list.size(), arraylist.size(), "size:");

		int k = 0;
		for (Integer n : arraylist)
			assertEquals(n.intValue(), list.get(k++), "at index: "+k);	
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
		IntList list = new IntList(10);
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
	public void addAtIndex() {
		listStatic.addAllAtIndex(1, 13, 14, 15);
		arraylistStatic.addAll(1, Arrays.asList(13, 14, 15));
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
		IntList list = new IntList();
		list.addAll(10, 11, 12, 14);
		List<Integer> list2 = Arrays.asList(10, 11, 12, 14);

		for (Integer n : list2) {
			assertEquals(list2.indexOf(n), list.indexOf(n));
			assertEquals(list2.lastIndexOf(n), list.lastIndexOf(n));
		}
	}

	IntList list;
	ArrayList<Integer> list2;
	Random r;

	void arrayTest(boolean firstAdd, IntConsumer filler, Runnable afterFill) {
		list = new IntList();
		list2 = new ArrayList<>();
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
	public void randomAdd() {
		arrayTest(true, i -> {
			int n = r.nextInt();
			int add = r.nextInt(list.size());
			list.add(add, n);
			list2.add(add, n);
		}, null);
	}

	@Test
	public void randomAddAll() {
		arrayTest(true, i -> {
			int[] n = {r.nextInt(), r.nextInt(), r.nextInt()};
			int add = r.nextInt(list.size());
			list.addAllAtIndex(add, n);
			list2.addAll(add, Arrays.asList(n[0], n[1], n[2]));
		}, null);
	}

	@Test
	public void set() {
		arrayTest(true, i -> {
			int[] n = {r.nextInt(), r.nextInt(), r.nextInt()};
			int add = r.nextInt(list.size());
			list.addAllAtIndex(add, n);
			list2.addAll(add, Arrays.asList(n[0], n[1], n[2]));
		}, () -> {
			int size = list.size();
			for (int i = 0; i < size; i++) {
				int add = r.nextInt(list.size());
				int n = r.nextInt();
				list.set(add, n);
				list2.set(add, n);	
			}
		});
	}

	@Test
	public void removeIndex() {
		arrayTest(false, simplefill, () -> {
			int size = list.size()/2;
			for (int i = 0; i < size; i++) {
				int n =  r.nextInt(list.size());
				list.removeIndex(n);
				list2.remove(n);
			}
		});
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
	public void testConcurrentModification()  {
		assertThrows(ConcurrentModificationException.class, () -> {
			IntList list = new IntList(new int[]{10});
			list.forEach(i -> list.add(10));
		});
		
	}
}
