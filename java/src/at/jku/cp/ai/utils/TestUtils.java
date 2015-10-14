package at.jku.cp.ai.utils;

import static org.junit.Assert.fail;

import java.util.List;

import at.jku.cp.ai.rau.IBoard;

public abstract class TestUtils
{
	private TestUtils()
	{

	}

	public static <T> boolean listEquals(List<T> expected, List<T> actual)
	{
		if (expected.size() != actual.size())
		{
			System.out.println(String.format("list sizes differ! (%d != %d)", expected.size(), actual.size()));
			return false;
		}

		for (int i = 0; i < expected.size(); i++)
		{
			T tExpected = expected.get(i);
			T tActual = actual.get(i);

			if (!tExpected.equals(tActual))
			{
				System.out.println("elements differ at pos (" + i + ")");
				System.out.println("expected : " + tExpected);
				System.out.println("actual   : " + tActual);
				return false;
			}
		}
		return true;
	}

	public static <T> void assertListEquals(List<T> expected, List<T> actual)
	{
		if (expected.size() > 0 && expected.get(0) instanceof IBoard)
		{
			@SuppressWarnings("unchecked")
			boolean equal = boardsEquals((List<IBoard>) expected, (List<IBoard>) actual);
			if (!equal)
			{
				fail();
			}
		}

		boolean equal = listEquals(expected, actual);
		if (!equal)
		{
			fail();
		}
	}

	private static boolean boardsEquals(List<IBoard> expected, List<IBoard> actual)
	{
		int minListSize = Math.min(expected.size(), actual.size());
		for (int i = 0; i < minListSize; i++)
		{
			IBoard e = expected.get(i);
			IBoard a = actual.get(i);

			if (!e.equals(a))
			{
				System.out.println("--------------------------------------");
				System.out.println("elements differ at pos (" + i + ")");
				

				for (int j = Math.max(0, i - 1); j < Math.min(i + 2, minListSize); j++)
				{
					System.out.println(String.format("--- pos (%d) ---", j));
					System.out.println(RenderUtils.column(expected.get(j), actual.get(j)));
				}
				return false;
			}
		}
		return true;
	}
}
