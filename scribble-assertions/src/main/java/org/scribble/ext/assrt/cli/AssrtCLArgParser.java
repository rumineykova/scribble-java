package org.scribble.ext.assrt.cli;

import java.util.HashMap;
import java.util.Map;

import org.scribble.cli.CLArgParser;
import org.scribble.cli.CommandLineException;

public class AssrtCLArgParser extends CLArgParser
{
	// Unique flags
	public static final String ASSRT_FLAG = "-ass";
	
	private static final Map<String, AssrtCLArgFlag> ASSRT_UNIQUE_FLAGS = new HashMap<>();
	{
		AssrtCLArgParser.ASSRT_UNIQUE_FLAGS.put(AssrtCLArgParser.ASSRT_FLAG, AssrtCLArgFlag.ASSRT);
	}

	private static final Map<String, AssrtCLArgFlag> ASSRT_FLAGS = new HashMap<>();
	{
		AssrtCLArgParser.ASSRT_FLAGS.putAll(AssrtCLArgParser.ASSRT_UNIQUE_FLAGS);
	}

	private final Map<AssrtCLArgFlag, String[]> assrtParsed = new HashMap<>();
	
	public AssrtCLArgParser(String[] args) throws CommandLineException
	{
		super(args);  // Assigns this.args and calls parseArgs
	}		
	
	public Map<AssrtCLArgFlag, String[]> getAssrtArgs()
	{
		return this.assrtParsed;
	}

	@Override
	protected boolean isFlag(String arg)
	{
		return AssrtCLArgParser.ASSRT_FLAGS.containsKey(arg) || super.isFlag(arg);
	}
	
	// Pre: i is the index of the current flag to parse
	// Post: i is the index of the last argument parsed -- parseArgs does the index increment to the next current flag
	// Currently allows repeat flag decls: next overrides previous
	@Override
	protected int parseFlag(int i) throws CommandLineException
	{
		String flag = this.args[i];
		switch (flag)
		{
			// Unique flags
			case AssrtCLArgParser.ASSRT_FLAG:
			{
				return parseAssrt(i);
			}
			default:
			{
				return super.parseFlag(i);
			}
		}
	}

	private int parseAssrt(int i) throws CommandLineException
	{
		if ((i + 1) >= this.args.length)
		{
			throw new CommandLineException("Missing simple global protocol name argument.");
		}
		String proto = this.args[++i];
		assrtCheckAndAddNoArgUniqueFlag(AssrtCLArgParser.ASSRT_FLAG, new String[] { proto });
		return i;
	}

	private void assrtCheckAndAddNoArgUniqueFlag(String flag, String[] args) throws CommandLineException
	{
		AssrtCLArgFlag argFlag = AssrtCLArgParser.ASSRT_UNIQUE_FLAGS.get(flag);
		if (this.assrtParsed.containsKey(argFlag))
		{
			throw new CommandLineException("Duplicate flag: " + flag);
		}
		this.assrtParsed.put(argFlag, args);
	}
}
