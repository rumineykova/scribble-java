package org.scribble.ext.assrt.util;

import java.io.File;
import java.util.Arrays;

import org.scribble.main.ScribbleException;
import org.scribble.util.ScribUtil;

public class Z3Wrapper
{

	/*public Z3Wrapper()
	{
		// TODO Auto-generated constructor stub
	}*/

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
			String[] res = ScribUtil.runProcess("z3", tmpName);
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
				throw new RuntimeException("[assrt] z3 error: " + Arrays.toString(res));
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
