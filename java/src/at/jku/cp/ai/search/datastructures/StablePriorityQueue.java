package at.jku.cp.ai.search.datastructures;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class implements a priority-queue with stable sorting. That means, it remembers the order
 * in which elements with the same key have been inserted, and returns them again in that order.
 * @param <Key>
 * @param <Value>
 */
public class StablePriorityQueue<Key extends Comparable<Key>, Value> implements Queue<Pair<Key, Value>>
{
	private final static AtomicInteger seq = new AtomicInteger(0);
	private class Entry<T1 extends Comparable<T1>, T2>
	{
		final int order;
		final Pair<T1, T2> element;

		public Entry(Pair<T1, T2> element)
		{
			this.element = element;
			this.order = seq.incrementAndGet();
		}
	}

	private class OrderedComparator<T1 extends Comparable<T1>, T2> implements Comparator<Entry<T1, T2>>
	{
		@Override
		public int compare(final Entry<T1, T2> a, final Entry<T1, T2> b)
		{
			int r = a.element.f.compareTo(b.element.f);
			if (r == 0)
				return Integer.compare(a.order, b.order);
			return r;
		}
	}

	private PriorityQueue<Entry<Key, Value>> pq;
	
	public StablePriorityQueue()
	{
		pq = new PriorityQueue<>(11, new OrderedComparator<Key, Value>());
	}
	
	public StablePriorityQueue(int initialCapacity)
	{
		pq = new PriorityQueue<>(initialCapacity, new OrderedComparator<Key, Value>());
	}
	
	@Override
	public int size()
	{
		return pq.size();
	}

	@Override
	public boolean isEmpty()
	{
		return pq.isEmpty();
	}

	@Override
	public boolean contains(Object o)
	{
		return pq.contains(o);
	}

	@Override
	public Iterator<Pair<Key, Value>> iterator()
	{
		final Iterator<StablePriorityQueue<Key,Value>.Entry<Key,Value>> it = pq.iterator();
		return new Iterator<Pair<Key,Value>>()
		{
			@Override
			public boolean hasNext()
			{
				return it.hasNext();
			}

			@Override
			public Pair<Key, Value> next()
			{
				return it.next().element;
			}

			@Override
			public void remove()
			{
				pq.remove();
			}
		};
	}

	@Override
	public Object[] toArray()
	{
		return pq.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a)
	{
		return pq.toArray(a);
	}

	@Override
	public boolean remove(Object o)
	{
		return pq.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		return pq.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends Pair<Key, Value>> c)
	{
		for(Pair<Key, Value> p: c)
		{
			if(!add(p))
				return false;
		}
		return true;
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		return pq.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c)
	{
		return pq.retainAll(c);
	}

	@Override
	public void clear()
	{
		pq.clear();
	}

	@Override
	public boolean add(Pair<Key, Value> p)
	{
		return pq.add(new Entry<Key, Value>(p));
	}

	@Override
	public boolean offer(Pair<Key, Value> p)
	{
		return add(p);
	}

	@Override
	public Pair<Key, Value> remove()
	{
		return pq.remove().element;
	}

	@Override
	public Pair<Key, Value> poll()
	{
		return pq.poll().element;
	}

	@Override
	public Pair<Key, Value> element()
	{
		return pq.element().element;
	}

	@Override
	public Pair<Key, Value> peek()
	{
		return pq.peek().element;
	}	
}
