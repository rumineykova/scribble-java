package scribtest;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.scribble.cli.CommandLine;
import org.scribble.cli.CommandLineArgParser;
import org.scribble.main.RuntimeScribbleException;

/**
 * Runs all tests under good and bad root directories in Scribble.
 */
@RunWith(value = Parameterized.class)
public class AllTest
{
	protected static final boolean GOOD_TEST = false;  // !isBadTest
	protected static final boolean BAD_TEST = true;

	// under src/test/resources (or target/test-classes/)
	protected static final String GOOD_ROOT = "good";
	protected static final String BAD_ROOT = "bad";
	
	private final String example;
	private final boolean isBadTest;

	public AllTest(String example, boolean isBadTest)
	{
		this.example = example;
		this.isBadTest = isBadTest;
	}

	protected static Collection<Object[]> makeTests(boolean isBadTest, String dir)
	{
		return new Harness().findTests(dir).stream()
				.map((e) -> new Object[] { e, isBadTest })
				.collect(Collectors.toList());
	}

	@Parameters(name = "{0}")
	public static Collection<Object[]> data()
	{
		// Test all tests under good and bad root directories
		String dir_good = ClassLoader.getSystemResource(GOOD_ROOT).getFile();
		String dir_bad = ClassLoader.getSystemResource(BAD_ROOT).getFile();
		List<Object[]> result = new LinkedList<>();
		result.addAll(makeTests(GOOD_TEST, dir_good));
		result.addAll(makeTests(BAD_TEST, dir_bad));
		return result;
	}

	@Test
	public void tests() throws IOException, InterruptedException, ExecutionException
	{
		try
		{
			// TODO: For now just locate classpath for resources - later maybe directly execute job
			URL url = ClassLoader.getSystemResource(GOOD_ROOT);  // Assume good/bad have same parent
			String dir = url.getFile().substring(0, url.getFile().length() - ("/" + GOOD_ROOT).length());

			if (File.separator.equals("\\")) // HACK: Windows
			{
				dir = dir.substring(1).replace("/", "\\");
			}

			new CommandLine(this.example, CommandLineArgParser.PATH_FLAG, dir).run();
			Assert.assertFalse("Expecting exception", this.isBadTest);
		}
		catch (RuntimeScribbleException e)  // Runtime because CommandLine is currently a Runnable (maybe shouldn't be; throw regular ScribbleException)
		{
			Assert.assertTrue("Unexpected exception '" + e.getCause().getMessage() + "'", this.isBadTest);
		}
	}
}