package sam.collection;




import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static sam.collection.Utils.check;

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
	public void randomRemoveAll_2() {
		arrayTest(true, i -> {
			int[] n = {r.nextInt(1000), r.nextInt(1000), r.nextInt(1000)};
			list.addAll(n);
			list2.addAll(Arrays.asList(n[0], n[1], n[2]));
		}, () -> {
			int size = list.size()/2;
			for (int i = 0; i < size; i++) {
				int[] n = {r.nextInt(1000), r.nextInt(1000), r.nextInt(1000)};
				list.removeAll(n);
				list2.removeAll(Arrays.asList(n[0], n[1], n[2]));
			}
		});
	}

	@Test
	void randomRemoveAll_3() {
		Random r = new Random();

		for (int i = 0; i < 1000; i++) 
			rra_3(r, 1 + r.nextInt(10000), 1 + r.nextInt(1000));
	}
	void rra_3(Random r, final int size, final int max) {
		IntList list = new IntList();
		ArrayList<Integer> arraylist = new ArrayList<>(size);

		for (int i = 0; i < size; i++) {
			int n = r.nextInt(max);
			list.add(n);
			arraylist.add(n);
		}

		check(list, arraylist);
		List<Integer> temp = new ArrayList<>();

		for (int i = 0; i < 100; i++) {
			int[] n = new int[r.nextInt(100)];
			temp.clear();

			for (int j = 0; j < n.length; j++) {
				n[j] = r.nextInt(max);
				temp.add(n[j]);
			}
			arraylist.removeAll(temp);
			int[] copy = Arrays.copyOf(n, n.length);
			list.removeAll(n);
			
			boolean success = false;
			try {
				assertArrayEquals(copy, n);
				check(list, arraylist);
				success = true;
			} finally {
				if(!success) {
					System.out.println("list:             "+list);
					System.out.println("arraylist:        "+arraylist);
					System.out.println("delete-list:      "+Arrays.toString(n));
					System.out.println("delete-arraylist: "+temp);
				}
			}
		}


		check(list, arraylist);
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
