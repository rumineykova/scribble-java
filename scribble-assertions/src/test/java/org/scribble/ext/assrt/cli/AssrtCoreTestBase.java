package org.scribble.ext.assrt.cli;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.scribble.cli.CLArgParser;
import org.scribble.cli.CommandLineException;
import org.scribble.cli.ScribTestBase;
import org.scribble.ext.assrt.core.ast.AssrtCoreSyntaxException;
import org.scribble.main.AntlrSourceException;

public abstract class AssrtCoreTestBase extends ScribTestBase
{
	// relative to cli/src/test/resources (or target/test-classes/)
	protected static final String ASSRT_TEST_ROOT_DIR = ".";

	public AssrtCoreTestBase(String example, boolean isBadTest)
	{
		super(example, isBadTest);
	}
	
	// FIXME: base class should not specify "."
	@Override
	protected String getTestRootDir()
	{
		/*return "../../../scribble-test/target/test-classes/";  
				// FIXME: not needed?  only doing assrt tests (not scribble-test)
				// Why does this still work?*/
		return AssrtCoreTestBase.ASSRT_TEST_ROOT_DIR;
	}

	@Override
	protected String[] getSkipList()
	{
		String[] SKIP =  // Hack: for assrt-core  // FIXME: factor out "manual skip" mechanism -- cf. "auto" skip via some Exception (e.g., AssrtCoreSyntaxException)
			{
				"scribble-assertions/target/test-classes/good/extensions/annotations/ChoiceWithAnnot.scr",  // Repeat annot var 
			};
		return SKIP;
	}
	
	/*protected void runTest(String dir) throws CommandLineException, ScribbleException
	{
		new AssrtCommandLine(this.example, CLArgParser.JUNIT_FLAG, CLArgParser.IMPORT_PATH_FLAG, dir).run();
	}*/
	@Override
	protected void runTest(String dir) throws CommandLineException, AntlrSourceException
	{
		new AssrtCommandLine(this.example, CLArgParser.JUNIT_FLAG, CLArgParser.IMPORT_PATH_FLAG, dir,
						AssrtCoreCLArgParser.ASS_FLAG, "[AssrtCoreAllTest]")  // HACK: for AssrtCommandLine (assrt-core mode)
				.run();
	}

	@Override
	@Test
	public void tests() throws IOException, InterruptedException, ExecutionException
	{
		try
		{
			super.tests();
		}
		catch (RuntimeException e)  // Hack: for assrt-core
		{
			Throwable cause = e.getCause();
			if (cause instanceof AssrtCoreSyntaxException)
			{
				ScribTestBase.NUM_SKIPPED++;
				System.out.println("[assrt-core] Skipping (non-core syntax): " + this.example + "  (" + ScribTestBase.NUM_SKIPPED + " skipped)");
			}
			else
			{
				throw e;
			}
		}
	}
}
