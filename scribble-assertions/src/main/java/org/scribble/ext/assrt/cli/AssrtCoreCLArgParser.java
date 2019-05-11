package org.scribble.ext.assrt.cli;

import org.scribble.cli.CLArgParser;
import org.scribble.cli.CLFlags;
import org.scribble.cli.CommandLineException;

@Deprecated
public class AssrtCoreCLArgParser extends CLArgParser
{
	public AssrtCoreCLArgParser(CLFlags flags, String[] raw)
			throws CommandLineException
	{
		super(flags, raw);
	}		
}
