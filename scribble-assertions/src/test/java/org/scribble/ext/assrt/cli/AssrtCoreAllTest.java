package org.scribble.ext.assrt.cli;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.scribble.cli.Harness;
import org.scribble.cli.ScribTestBase;


@RunWith(Parameterized.class)
public class AssrtCoreAllTest extends AssrtCoreTestBase
{
	public AssrtCoreAllTest(String example, boolean isBadTest)
	{
		super(example, isBadTest);
	}
	
	@Parameters(name = "{0}")
	public static Collection<Object[]> data()
	{
		String dir_good = ClassLoader.getSystemResource(AssrtCoreGoodTest.GOOD_DIR).getFile();
		String dir_bad = ClassLoader.getSystemResource(AssrtCoreBadTest.BAD_DIR).getFile();
		List<Object[]> result = new LinkedList<>();
		result.addAll(Harness.makeTests(ScribTestBase.GOOD_TEST, dir_good));
		result.addAll(Harness.makeTests(ScribTestBase.BAD_TEST, dir_bad));
		return result;
	}
}
