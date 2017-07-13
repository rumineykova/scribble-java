package org.scribble.ext.assrt.cli;

import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.scribble.cli.ScribTest;
import org.scribble.cli.Harness;

@RunWith(Parameterized.class)
public class AssrtBadTest extends AssrtBaseTest
{
	protected static final String BAD_DIR = "bad";

	public AssrtBadTest(String example, boolean isBadTest)
	{
		super(example, isBadTest);
	}

	@Parameters(name = "{0}")
	public static Collection<Object[]> data()
	{
		return Harness.checkTestDirProperty(ScribTest.BAD_TEST, AssrtBadTest.BAD_DIR);
	}
}
