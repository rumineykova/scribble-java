package org.scribble.ext.assrt.cli;

import org.scribble.cli.CLArgParser;
import org.scribble.cli.CommandLineException;
import org.scribble.cli.ScribTestBase;
import org.scribble.main.AntlrSourceException;

public abstract class AssrtTestBase extends ScribTestBase
{
	// relative to cli/src/test/resources (or target/test-classes/)
	protected static final String ASSRT_TEST_ROOT_DIR = ".";

	public AssrtTestBase(String example, boolean isBadTest)
	{
		super(example, isBadTest);
	}
	
	@Override
	protected String getTestRootDir()
	{
		return AssrtTestBase.ASSRT_TEST_ROOT_DIR;
	}

	@Override
	protected boolean checkSkip()
	{
		if (super.checkSkip())
		{
			return true;
		}

		String tmp = this.example.replace("\\", "/");
		if (tmp.contains("scribble-assertions/target/test-classes/good/extensions/assrtcore")
						|| tmp.contains("scribble-assertions/target/test-classes/bad/extensions/assrtcore"))
		{
			ScribTestBase.NUM_SKIPPED++;
			System.out.println("[assrt] Skipping assrt-core test: " + this.example + " (" + ScribTestBase.NUM_SKIPPED + " skipped.)");
			return true;
		}
		return false;
	}
	
	@Override
	protected void runTest(String dir) throws CommandLineException, AntlrSourceException
	{
		new AssrtCommandLine(this.example, CLArgParser.JUNIT_FLAG, CLArgParser.IMPORT_PATH_FLAG, dir).run();
	}
}
