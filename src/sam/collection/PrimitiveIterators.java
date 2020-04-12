package sam.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.PrimitiveIterator.OfDouble;
import java.util.PrimitiveIterator.OfInt;

public interface PrimitiveIterators {
	public static final OfInt EMPTY_INT = new OfInt() {
		@Override public boolean hasNext() { return false; }
		@Override public int nextInt() { throw new NoSuchElementException(); }
	};
	
	public static final OfDouble EMPTY_DOUBLE = new OfDouble() {
		@Override public boolean hasNext() { return false; }
		@Override public double nextDouble() { throw new NoSuchElementException(); }
	};
	
	public static OfDouble of(double[] values) {
		Objects.requireNonNull(values);
		if(values.length == 0)
			return EMPTY_DOUBLE;

		return new OfDouble() {
			int n = 0;

			@Override
			public boolean hasNext() {
				return n < values.length;
			}
			@Override
			public double nextDouble() {
				if(!hasNext())
					throw new NoSuchElementException();

				return values[n++];
			}
		};
	}
	public static OfInt of(Iterator<Integer> values) {
		Objects.requireNonNull(values);
		if(!values.hasNext())
			return EMPTY_INT;
		return new OfInt() {
			@Override
			public boolean hasNext() {
				return values.hasNext();
			}
			@Override
			public int nextInt() {
				return values.next();
			}
		};		
		
	}
	public static OfInt of(int[] values) {
		Objects.requireNonNull(values);
		if(values.length == 0)
			return EMPTY_INT;

		return new OfInt() {
			int n = 0;

			@Override
			public boolean hasNext() {
				return n < values.length;
			}
			@Override
			public int nextInt() {
				if(!hasNext())
					throw new NoSuchElementException();

				return values[n++];
			}
		};
	}
	public static OfInt ofInt(char[] values) {
		Objects.requireNonNull(values);
		if(values.length == 0)
			return EMPTY_INT;

		return new OfInt() {
			int n = 0;

			@Override
			public boolean hasNext() {
				return n < values.length;
			}
			@Override
			public int nextInt() {
				if(!hasNext())
					throw new NoSuchElementException();

				return values[n++];
			}
		};
	}
}
