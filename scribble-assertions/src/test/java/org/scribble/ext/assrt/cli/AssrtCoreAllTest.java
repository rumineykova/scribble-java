package org.scribble.ext.assrt.cli;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.scribble.cli.CLArgParser;
import org.scribble.cli.CommandLineException;
import org.scribble.ext.assrt.core.ast.AssrtCoreSyntaxException;
import org.scribble.main.ScribbleException;


@RunWith(Parameterized.class)
public class AssrtCoreAllTest extends AssrtAllTest
{
	private static int NUM_SKIPPED = 0;  // HACK
	
	public AssrtCoreAllTest(String example, boolean isBadTest)
	{
		super(example, isBadTest);
	}
	
	/*// FIXME: base class should not specify "."
	@Override
	protected String getTestRootDir()
	{
		return "../../../scribble-test/target/test-classes/";  
				// FIXME: not needed?  only doing assrt tests (not scribble-test)
				// Why does this still work?
	}*/

	@Override
	protected void runTest(String dir) throws CommandLineException, ScribbleException
	{
		new AssrtCommandLine(this.example, CLArgParser.JUNIT_FLAG, CLArgParser.IMPORT_PATH_FLAG, dir,
						AssrtCLArgParser.ASSRT_FLAG, "[AssrtCoreAllTest]")  // HACK: for AssrtCommandLine 
				.run();
	}

	@Override
	@Test
	public void tests() throws IOException, InterruptedException, ExecutionException
	{
		try
		{
			String dir = ClassLoader.getSystemResource(getTestRootDir()).getFile();

			if (File.separator.equals("\\")) // HACK: Windows
			{
				dir = dir.substring(1).replace("/", "\\");
			}
			
			String[] SKIP =  // Hack: for assrt-core
				{
					//"scribble-test/target/test-classes/bad/wfchoice/enabling/twoparty/Test01b.scr",
					"scribble-assertions/target/test-classes/good/extensions/annotations/ChoiceWithAnnot.scr",
				};
			String tmp = this.example.replace("\\", "/");
			for (String skip : SKIP)
			{
				if (tmp.endsWith(skip))
				{
					AssrtCoreAllTest.NUM_SKIPPED++;
					System.out.println("[assrt-core] Manually skipping: " + this.example + " (" + AssrtCoreAllTest.NUM_SKIPPED + " skipped.)");
					return;
				}
			}
			
			runTest(dir);
			Assert.assertFalse("Expecting exception", this.isBadTest);
		}

		catch (AssrtCoreSyntaxException e)  // Hack: for assrt-core
		{
			AssrtCoreAllTest.NUM_SKIPPED++;
			System.out.println("[assrt-core] Skipping: " + this.example + "  (" + AssrtCoreAllTest.NUM_SKIPPED + " skipped)");
		}

		catch (ScribbleException e)
		{
			Assert.assertTrue("Unexpected exception '\n" + ClassLoader.getSystemResource(getTestRootDir()).getFile() + "\n" + e.getMessage() + "'", this.isBadTest);
		}
		//catch (ScribParserException | CommandLineException e)
		catch (CommandLineException e)
		{
			throw new RuntimeException(e);
		}
	}

}
