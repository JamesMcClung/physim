package util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

/**
 * A circular linked list can function like a regular linked list, but any index is valid. It is cyclical.
 * This property means that a circular linked list is different from a normal linked list in the following ways:
 * <ul>
 * <li>Any method that takes an index (such as {@link #get(int)} and {@link #add(int, Object)}) will accept any
 * integer as a valid index.
 * <li>A <code>ListIterator</code> (e.g. {@link #listIterator()}) always has a next and a previous element, although
 * <code>nextIndex</code> and <code>previousIndex</code> are unaffected.
 * <li>The list order is consistent, but the indices can change. No methods inherited from <code>List</code> do
 * this, but {@link #cycle(int)} does.
 * </ul>
 * @author james
 *
 * @param <E> element type
 */
public class CircularLinkedList<E> implements List<E> {
	
	public CircularLinkedList() {
		cursor = ref = new RefNode();
		size = 0;
	}
	
	public CircularLinkedList(Collection<E> c) {
		this();
		addAll(c);
	}
	
	private Node cursor;
	private RefNode ref;
	private int size;
	
	
	private Node cursor() {
		if (cursor.isRef) cursor = cursor.next();
		return cursor;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public boolean contains(Object o) {
		return getNodeWith(o, true).x != null;
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			Iterator<Node> nodeIterator = nodeIterator();

			@Override
			public boolean hasNext() {
				return nodeIterator.hasNext();
			}

			@Override
			public E next() {
				return nodeIterator.next().item;
			}
		};
	}
	
	private Iterator<Node> nodeIterator() {
		return new Iterator<Node>() {
			Node currentNode = cursor();
			int i = 0;

			@Override
			public boolean hasNext() {
				return i < size;
			}

			@Override
			public Node next() {
				var n = currentNode;
				currentNode = currentNode.next();
				i++;
				return n;
			}
		};
	}

	@Override
	public Object[] toArray() {
		Object[] arr = new Object[size];
		int i = 0;
		for (var e : this)
			arr[i++] = e;
		return arr;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		if (a.length < size)
            a = (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
        int i = 0;
        Object[] result = a;
        
        for (var e : this)
            result[i++] = e;

        if (a.length > size)
            a[size] = null;

        return a;
	}

	@Override
	public boolean add(E e) {
		cursor.insertBefore(e);
		return true;
	}

	@Override
	public boolean remove(Object o) {
		Node n = getNodeWith(o, true).x;
		if (n == null) return false;
		n.remove();
		return true;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (var e : c) {
			if (!contains(e)) return false;
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		if (c.isEmpty()) return false;
		for (var e : c) 
			add(e);
		return true;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		if (c.isEmpty()) return false;
		Node n = getNodeAt(index+1);
		for (var e : c)
			n.insertBefore(e);
		return true;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for (var e : c) {
			if (remove(e))
				changed = true;
		}
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		int i = 0;
		boolean changed = false;
		for(Node currentNode = cursor(); i < size; ) {
			if (!c.contains(currentNode.item)) {
				currentNode.remove();
				changed = true;
			} else
				i++;
			currentNode = currentNode.next();
		}
		return changed;
	}

	@Override
	public void clear() {
		cursor = ref;
		ref.pinch();
		size = 0;
	}

	@Override
	public E get(int index) {
		return getNodeAt(index).item;
	}

	@Override
	public E set(int index, E element) {
		Node n = getNodeAt(index);
		E lastElement = n.item;
		n.item = element;
		return lastElement;
	}

	@Override
	public void add(int index, E element) {
		getNodeAt(index).insertAfter(element);
	}

	@Override
	public E remove(int index) {
		if (size == 0) throw new IndexOutOfBoundsException(index);
		return getNodeAt(index).remove();
	}

	@Override
	public int indexOf(Object o) {
		return getNodeWith(o, true).y;
	}

	@Override
	public int lastIndexOf(Object o) {
		return getNodeWith(o, false).y;
	}

	@Override
	public ListIterator<E> listIterator() {
		return listIterator(0);
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return new ListItr(index);
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		var subList = new CircularLinkedList<E>();
		Node cursor = getNodeAt(fromIndex);
		for (int i = fromIndex; i < toIndex; i++) {
			subList.add(cursor.item);
			cursor = cursor.next();
		}
		return subList;
	}
	
	/**
	 * Cycles the list so that the given index becomes the first in the list, and returns its element.
	 * @param index index to cycle the list to
	 * @return the element that was at that index
	 */
	public E cycle(int index) {
		cursor = getNodeAt(index);
		return cursor.item;
	}
	
	public E cycle() {
		cursor = cursor.next();
		return cursor.item;
	}
	
	public E get() {
		return cursor().item;
	}
	
	public boolean isEquivalent(CircularLinkedList<?> list) {
		if (list == this) return true;
		if (list == null || size != list.size) return false;
		
		Node n = getNodeWith(list.cursor().item, true).x;
		if (n == null) return false;
		
		var iter = list.iterator();
		while (iter.hasNext()) {
			if (!Objects.equals(iter.next(), n.item))
				return false;
			n = n.next();
		}
		return true;
	}
	
	public boolean isReverse(CircularLinkedList<?> list) {
		if (list == this) return false;
		if (list == null || size != list.size) return false;
		
		Node n = getNodeWith(list.cursor().item, true).x;
		if (n == null) return false;
		
		var iter = list.iterator();
		while (iter.hasNext()) {
			if (!Objects.equals(iter.next(), n.item))
				return false;
			n = n.prev();
		}
		return true;
	}
	
	private Node getNodeAt(int index) {
		if (size == 0) throw new IndexOutOfBoundsException(index);
		index %= size;
		if (index < 0) index += size;
		Node currentNode = cursor();
		if (index > size/2) {
			for (int i = index; i < size; i++)
				currentNode = currentNode.prev();
		} else {
			for (int i = index; i > 0; i--)
				currentNode = currentNode.next();
		}
		return currentNode;
	}
	
	private Tuple<Node, Integer> getNodeWith(Object o, boolean traverseForward) {
		Node currentNode = cursor();
		if (traverseForward) {
			for (int i = 0; i < size; i++) {
				if (Objects.equals(currentNode.item, o))
					return new Tuple<>(currentNode, i);
				currentNode = currentNode.next();
			}
		} else {
			for (int i = size; i > 0; i--) {
				if (Objects.equals(currentNode.item, o))
					return new Tuple<>(currentNode, i);
				currentNode = currentNode.prev();
			}
		}
		return new Tuple<>(null, -1);
	}

	private class Node {
		public Node(E item, Node prev, Node next) {
			this.item = item;
			this.prev = prev;
			this.next = next;
			isRef = false;
		}
		
		private Node() {
			item = null;
			prev = next = this;
			isRef = true;
		}
		
		Node prev, next;
		E item;
		final boolean isRef;
		
		void insertBefore(E element) {
			Node n = new Node(element, prev, this);
			prev.next = n;
			prev = n;
			size++;
		}
		
		void insertAfter(E element) {
			Node n = new Node(element, this, next);
			next.prev = n;
			next = n;
			size++;
		}
		
		E remove() {
			prev.next = next;
			next.prev = prev;
			size--;
			return item;
		}
		
		Node prev() {
			return prev.isRef ? prev.prev : prev;
		}
		
		Node next() {
			return next.isRef ? next.next : next;
		}
	}
	
	private class RefNode extends Node {
		@Override
		E remove() {
			throw new Error("Ref node should never be removed");
		}
		
		void pinch() {
			prev = next = this;
		}
		
		@Override
		Node prev() {
			return prev;
		}
		
		@Override
		Node next() {
			return next;
		}
	}
	
	private class ListItr implements ListIterator<E> {
		
		private ListItr(int index) {
			next = getNodeAt(index);
			this.index = index - 1;
		}
		
		Node next;
		Node lastChecked = null;
		int index;

		@Override
		public boolean hasNext() {
			return true;
		}

		@Override
		public E next() {
			lastChecked = next;
			E item = next.item;
			next = next.next();
			index++;
			return item;
		}

		@Override
		public boolean hasPrevious() {
			return true;
		}

		@Override
		public E previous() {
			next = next.prev();
			lastChecked = next;
			index--;
			return next.item;
		}

		@Override
		public int nextIndex() {
			return index+1;
		}

		@Override
		public int previousIndex() {
			return index;
		}

		@Override
		public void remove() {
			if (lastChecked != null)
				lastChecked.remove();
			if (lastChecked == next)
				next = next.next();
			else
				index--;
			lastChecked = null;
		}

		@Override
		public void set(E e) {
			if (lastChecked != null)
				lastChecked.item = e;
		}

		@Override
		public void add(E e) {
			next.insertBefore(e);
			index++;
			lastChecked = null;
		}
	}
}