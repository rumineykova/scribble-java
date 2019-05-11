package org.scribble.ext.assrt.cli;

import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.scribble.test.Harness;
import org.scribble.test.ScribTestBase;

@RunWith(value = Parameterized.class)
public class AssrtGoodTest extends AssrtTestBase
{
	protected static final String GOOD_DIR = "good";

	public AssrtGoodTest(String example, boolean isBadTest)
	{
		super(example, isBadTest);
	}

	@Parameters(name = "{0}")
	public static Collection<Object[]> data()
	{
		String dir = AssrtGoodTest.GOOD_DIR;
		return Harness.checkTestDirProperty(ScribTestBase.GOOD_TEST, dir);
	}
}
