package org.scribble.ext.assrt.cli;

import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.scribble.cli.ScribTest;
import org.scribble.cli.Harness;

@RunWith(value = Parameterized.class)
public class AssrtGoodTest extends AssrtBaseTest
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
		return Harness.checkTestDirProperty(ScribTest.GOOD_TEST, dir);
	}
}
