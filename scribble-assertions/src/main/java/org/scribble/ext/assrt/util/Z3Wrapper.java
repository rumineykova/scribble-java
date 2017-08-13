package org.scribble.ext.assrt.util;

import java.io.File;
import java.util.Arrays;

import org.scribble.main.ScribbleException;
import org.scribble.util.ScribUtil;

// "Native" Z3 -- not Z3 Java API
public class Z3Wrapper
{

	/*public Z3Wrapper()
	{
		// TODO Auto-generated constructor stub
	}*/
	
	public static String toSmt2(String f)
	{
		return "(assert " + f + ")\n"
				+ "(check-sat)\n"
				+ "(exit)";
	}

	// Duplicated from JobContext::runAut
	// protoname only used for naming tmp file
	public static boolean isSat(String smt2, String protoname) //throws ScribbleException
	{
		String tmpName = protoname + ".smt2.tmp";
		File tmp = new File(tmpName);
		if (tmp.exists())  // Factor out with CommandLine.runDot (file exists check)
		{
			throw new RuntimeException("Cannot overwrite: " + tmpName);
		}
		try
		{
			ScribUtil.writeToFile(tmpName, smt2);
			String[] res = ScribUtil.runProcess("Z3", tmpName);
			String trim = res[0].trim();
			if (trim.equals("sat"))  // FIXME: factor out
			{
				return true;
			}
			else if (trim.equals("unsat"))
			{
				return false;
			}
			else
			{
				throw new RuntimeException("[assrt] Z3 error: " + Arrays.toString(res));
			}
		}
		catch (ScribbleException e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			tmp.delete();
		}
	}
}
