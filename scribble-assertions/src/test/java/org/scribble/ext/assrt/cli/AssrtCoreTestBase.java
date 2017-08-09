package org.scribble.ext.assrt.cli;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.junit.Assert;
import org.junit.Test;
import org.scribble.cli.CLArgParser;
import org.scribble.cli.CommandLineException;
import org.scribble.cli.ScribTestBase;
import org.scribble.ext.assrt.core.ast.AssrtCoreSyntaxException;
import org.scribble.main.ScribbleException;

public abstract class AssrtCoreTestBase extends ScribTestBase
{
	// relative to cli/src/test/resources (or target/test-classes/)
	protected static final String ASSRT_TEST_ROOT_DIR = ".";

	public AssrtCoreTestBase(String example, boolean isBadTest)
	{
		super(example, isBadTest);
	}
	
	// FIXME: base class should not specify "."
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
				//"scribble-test/target/test-classes/bad/wfchoice/enabling/twoparty/Test01b.scr",
				"scribble-assertions/target/test-classes/good/extensions/annotations/ChoiceWithAnnot.scr",
			};
		return SKIP;
	}
	
	/*protected void runTest(String dir) throws CommandLineException, ScribbleException
	{
		new AssrtCommandLine(this.example, CLArgParser.JUNIT_FLAG, CLArgParser.IMPORT_PATH_FLAG, dir).run();
	}*/
	protected void runTest(String dir) throws CommandLineException, ScribbleException
	{
		new AssrtCommandLine(this.example, CLArgParser.JUNIT_FLAG, CLArgParser.IMPORT_PATH_FLAG, dir,
						AssrtCoreCLArgParser.ASS_FLAG, "[AssrtCoreAllTest]")  // HACK: for AssrtCommandLine 
				.run();
	}

	@Override
	@Test
	public void tests() throws IOException, InterruptedException, ExecutionException
	{
		if (checkSkip())
		{
			return;
		}

		String dir = ClassLoader.getSystemResource(getTestRootDir()).getFile();
		if (File.separator.equals("\\")) // HACK: Windows
		{
			dir = dir.substring(1).replace("/", "\\");
		}

		try
		{
			runTest(dir);
			Assert.assertFalse("Expecting exception", this.isBadTest);
		}
		catch (AssrtCoreSyntaxException e)  // Hack: for assrt-core
		{
			AssrtCoreTestBase.NUM_SKIPPED++;
			System.out.println("[assrt-core] Skipping: " + this.example + "  (" + AssrtCoreTestBase.NUM_SKIPPED + " skipped)");
		}
		catch (ScribbleException e)
		{
			Assert.assertTrue("Unexpected exception '\n" + ClassLoader.getSystemResource(getTestRootDir()).getFile() + "\n" + e.getMessage() + "'", this.isBadTest);
		}
		catch (CommandLineException e)
		{
			throw new RuntimeException(e);
		}
	}
}
