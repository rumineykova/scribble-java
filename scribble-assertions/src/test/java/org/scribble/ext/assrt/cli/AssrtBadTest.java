package org.scribble.ext.assrt.cli;

import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.scribble.test.Harness;
import org.scribble.test.ScribTestBase;

@RunWith(Parameterized.class)
public class AssrtBadTest extends AssrtTestBase
{
	protected static final String BAD_DIR = "bad";

	public AssrtBadTest(String example, boolean isBadTest)
	{
		super(example, isBadTest);
	}

	@Parameters(name = "{0}")
	public static Collection<Object[]> data()
	{
		return Harness.checkTestDirProperty(ScribTestBase.BAD_TEST, AssrtBadTest.BAD_DIR);
	}
}
