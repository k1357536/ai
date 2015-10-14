package at.jku.cp.ai.search.datastructures;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * This is a stack implementation that has a fast 'contains' operation.
 *
 * @param <E>
 */
public class StackWithFastContains<E> extends Stack<E>
{
	private static final long serialVersionUID = 1L;
	private Set<E> set;

	public StackWithFastContains()
	{
		set = new HashSet<>();
	}

	@Override
	public E push(E item)
	{
		set.add(item);
		return super.push(item);
	}

	@Override
	public synchronized E pop()
	{
		E item = super.pop();
		set.remove(item);
		return item;
	}

	@Override
	public boolean contains(Object o)
	{
		return set.contains(o);
	}
}
