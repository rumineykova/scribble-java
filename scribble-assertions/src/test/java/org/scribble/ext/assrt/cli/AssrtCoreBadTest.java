package org.scribble.ext.assrt.cli;

import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.scribble.cli.ScribTestBase;
import org.scribble.cli.Harness;

@RunWith(Parameterized.class)
public class AssrtCoreBadTest extends AssrtCoreTestBase
{
	protected static final String BAD_DIR = "bad";

	public AssrtCoreBadTest(String example, boolean isBadTest)
	{
		super(example, isBadTest);
	}

	@Parameters(name = "{0}")
	public static Collection<Object[]> data()
	{
		return Harness.checkTestDirProperty(ScribTestBase.BAD_TEST, AssrtCoreBadTest.BAD_DIR);
	}
}
