package org.scribble.ext.assrt.cli;

import org.scribble.cli.ScribTest;
import org.scribble.cli.CLArgParser;
import org.scribble.cli.CommandLineException;
import org.scribble.main.ScribbleException;

public abstract class AssrtBaseTest extends ScribTest
{
	// relative to cli/src/test/resources (or target/test-classes/)
	protected static final String ASSRT_TEST_ROOT_DIR = ".";

	public AssrtBaseTest(String example, boolean isBadTest)
	{
		super(example, isBadTest);
	}
	
	protected String getTestRootDir()
	{
		return AssrtBaseTest.ASSRT_TEST_ROOT_DIR;
	}
	
	protected void runTest(String dir) throws CommandLineException, ScribbleException
	{
		new AssrtCommandLine(this.example, CLArgParser.JUNIT_FLAG, CLArgParser.IMPORT_PATH_FLAG, dir).run();
	}
}
