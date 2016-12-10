import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A HashSet that uses open addressing and linear probing to resolve collisions.
 * 
 * @author jstenglein 12/9/16
 *
 */
public class OpenHashSet<E> implements Iterable<E> {

	// used to indicate that a position was once filled but is now empty
	public static final Object EMPTY = new Object();
	public static final double LOAD_LIMIT = 0.75;
	public static final int DEFAULT_CAPACITY = 11;

	// instance variables
	private int size;
	private E[] con;

	/**
	 * Default constructor that creates a new OpenHashTable with a capacity of
	 * 11. <br>
	 * pre: none <br>
	 * O(1)
	 */
	public OpenHashSet() {
		this(DEFAULT_CAPACITY);
	}

	/**
	 * Constructor that creates a new OpenHashTable with the desired capacity.
	 * <br>
	 * pre: capacity >= 0 <br>
	 * O(capacity)
	 * 
	 * @param capacity
	 *            The desired capacity for the OpenHashTable. Must be greater
	 *            than or equal to 0.
	 */
	public OpenHashSet(int capacity) {
		// check precondition
		if (capacity < 0)
			throw new IllegalArgumentException("The capacity of the OpenHashTable must be greater than or equal to 0.");

		con = (E[]) new Object[capacity];
	}

	/**
	 * Adds the specified value to the HashSet, if it is not already present.
	 * Return true if the HashSet changed as a result of this call, false
	 * otherwise. <br>
	 * pre: val != null
	 * 
	 * @param val
	 *            The value to add to the HashSet. May not be null.
	 * @return True if val was not already present in the HashSet, false
	 *         otherwise.
	 */
	public boolean add(E val) {
		// check precondition
		if (val == null)
			throw new IllegalArgumentException("Null values may not be added to the HashSet.");

		if (1.0 * size / con.length > LOAD_LIMIT) {
			resize();
		}

		boolean result = addNoResize(val);
		if (result)
			size++;

		return result;
	}

	private boolean addNoResize(E val) {
		// we won't go in circles because of load limit
		int index = Math.abs(val.hashCode()) % con.length;
		while (con[index] != null && con[index] != EMPTY) {
			if (val.equals(con[index]))
				return false;
			index = (index + 1) % con.length;
		}

		con[index] = val;
		return true;
	}

	/**
	 * Removes all elements from the HashSet. <br>
	 * pre: none <br>
	 * O(N)
	 */
	public void clear() {
		for (int i = 0; i < con.length; i++) {
			con[i] = null;
		}
		size = 0;
	}

	/**
	 * Returns a boolean indicating whether the given value is present in the
	 * HashSet or not. <br>
	 * pre: val != null
	 * 
	 * @param val
	 *            The value to search for in the HashSet. May not be null.
	 * @return True if val is in the HashSet, false otherwise.
	 */
	public boolean contains(Object val) {
		if (val == null)
			throw new IllegalArgumentException("The value to search for may not be null.");

		// when we hit a null value, we know the object isn't in the list
		// need to make sure we don't continue in circles though
		int index = Math.abs(val.hashCode()) % con.length;
		int checked = 0;
		while (con[index] != null && checked < size) {
			if (val.equals(con[index])) {
				return true;
			} else if (con[index] != EMPTY) {
				checked++;
			}

			index = (index + 1) % con.length;
		}

		return false;
	}
	
	
	public boolean containsAll(Collection<?> c) {
		Iterator<?> it = c.iterator();
		while(it.hasNext()) {
			if(!contains(it.next()))
				return false;
		}
		return true;
	}
	
	public boolean equals(Object other) {
		if(other instanceof Set) {
			Set<?> otherSet = (Set<?>)other;
			if(otherSet.size() == size && containsAll(otherSet))
				return true;
		}
		
		return false;
	}

	public int hashCode() {
		int hashCode = 1;
		HSIterator it = new HSIterator();
		while (it.hasNext()) {
			hashCode = 31 * hashCode + it.next().hashCode();
		}

		return hashCode;
	}

	/**
	 * Returns true if this HashSet contains no elements. Returns false
	 * otherwise. <br>
	 * pre: none <br>
	 * O(1)
	 * 
	 * @return True if this HashSet has a size of 0, false otherwise.
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Returns an Iterator over the elements in this HashSet. <br>
	 * pre: none <br>
	 * O(1)
	 * 
	 * @return An Iterator over the elements in this HashSet.
	 */
	public Iterator<E> iterator() {
		return new HSIterator();
	}

	/**
	 * Removes the specified value from the HashSet, if it is present. Returns
	 * true if the value was present and false if not. <br>
	 * pre: val != null
	 * 
	 * @param val
	 *            The value to remove from the HashSet.
	 * @return True if the value was present, false otherwise.
	 */
	public boolean remove(Object val) {
		if (val == null)
			throw new IllegalArgumentException("The value to remove may not be null.");

		int index = Math.abs(val.hashCode()) % con.length;
		int checked = 0;
		while (con[index] != null && checked < size) {
			if (val.equals(con[index])) {
				con[index] = (E) EMPTY;
				size--;
				return true;
			} else if (con[index] != EMPTY) {
				checked++;
			}

			index = (index + 1) % con.length;
		}

		return false;
	}

	/**
	 * Resize the internal array in this HashSet by doubling the current size
	 * and adding 1. All elements currently in the HashSet are rehashed and
	 * inserted into the new array. <br>
	 * pre: none <br>
	 * O(N)
	 */
	private void resize() {
		OpenHashSet<E> temp = new OpenHashSet<E>(size * 2 + 1);
		for (E elem : con) {
			if (elem != null && elem != EMPTY) {
				temp.addNoResize(elem);
			}
		}

		con = temp.con;
	}

	/**
	 * Returns the number of elements in this HashSet. <br>
	 * pre: none <br>
	 * O(1)
	 * 
	 * @return
	 */
	public int size() {
		return size;
	}

	public String toString() {
		HSIterator it = new HSIterator();
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		if (it.hasNext())
			sb.append(it.next());

		while (it.hasNext()) {
			sb.append(", ");
			sb.append(it.next());
		}

		sb.append("]");
		return sb.toString();
	}

	private class HSIterator implements Iterator<E> {

		// instance variables
		private boolean removeOK;
		private int nextPos;
		private int numReturned;

		/**
		 * Creates a new HSIterator object. <br>
		 * pre: none <br>
		 * O(1)
		 */
		private HSIterator() {
			// start at -1 so increment in next() moves it to 0
			nextPos = -1;
		}

		/**
		 * Returns true if the iteration has more elements. <br>
		 * pre: none <br>
		 * O(1)
		 */
		public boolean hasNext() {
			return numReturned < size;
		}

		/**
		 * Returns the next element in the iteration. <br>
		 * pre: hasNext() == true
		 */
		public E next() {
			if (!hasNext())
				throw new NoSuchElementException("The iteration has no more elements.");

			// find the next position that has an element
			nextPos++;
			while (con[nextPos] == null || con[nextPos] == EMPTY) {
				nextPos++;
			}

			numReturned++;
			removeOK = true;
			return con[nextPos];
		}

		/**
		 * Removes the last element returned by the iterator from the HashSet.
		 * <br>
		 * pre: next() has been called since the last call to remove() <br>
		 * O(1)
		 */
		public void remove() {
			if (!removeOK)
				throw new IllegalStateException("This method can be called only once per call to next().");

			removeOK = false;
			con[nextPos] = (E) EMPTY;
			size--;
			// decrement numReturned due to decrement of size
			numReturned--;
		}
	}
}
