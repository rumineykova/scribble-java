package org.scribble.ext.assrt.cli;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.scribble.cli.CLArgParser;
import org.scribble.cli.CommandLineException;

public class AssrtCoreCLArgParser extends CLArgParser
{
	// Unique flags
	public static final String ASS_FLAG           = "-ass";
	public static final String ASS_MODEL_FLAG     = "-ass-model";  // cf. SGRAPH
	public static final String ASS_MODEL_PNG_FLAG = "-ass-modelpng";
	
	// Non-unique flags
	public static final String ASS_EFSM_FLAG      = "-ass-fsm";
	public static final String ASS_EFSM_PNG_FLAG  = "-ass-fsmpng";
	
	private static final Map<String, AssrtCoreCLArgFlag> ASSRT_UNIQUE_FLAGS = new HashMap<>();
	{
		AssrtCoreCLArgParser.ASSRT_UNIQUE_FLAGS.put(AssrtCoreCLArgParser.ASS_FLAG, AssrtCoreCLArgFlag.ASSRT_CORE);
		AssrtCoreCLArgParser.ASSRT_UNIQUE_FLAGS.put(AssrtCoreCLArgParser.ASS_MODEL_FLAG, AssrtCoreCLArgFlag.ASSRT_CORE_MODEL);
		AssrtCoreCLArgParser.ASSRT_UNIQUE_FLAGS.put(AssrtCoreCLArgParser.ASS_MODEL_PNG_FLAG, AssrtCoreCLArgFlag.ASSRT_CORE_MODEL_PNG);
	}

	private static final Map<String, AssrtCoreCLArgFlag> ASSRT_NON_UNIQUE_FLAGS = new HashMap<>();
	{
		AssrtCoreCLArgParser.ASSRT_NON_UNIQUE_FLAGS.put(AssrtCoreCLArgParser.ASS_EFSM_FLAG, AssrtCoreCLArgFlag.ASSRT_CORE_EFSM);
		AssrtCoreCLArgParser.ASSRT_NON_UNIQUE_FLAGS.put(AssrtCoreCLArgParser.ASS_EFSM_PNG_FLAG, AssrtCoreCLArgFlag.ASSRT_CORE_EFSM_PNG);
	}

	private static final Map<String, AssrtCoreCLArgFlag> ASSRT_FLAGS = new HashMap<>();
	{
		AssrtCoreCLArgParser.ASSRT_FLAGS.putAll(AssrtCoreCLArgParser.ASSRT_UNIQUE_FLAGS);
		AssrtCoreCLArgParser.ASSRT_FLAGS.putAll(AssrtCoreCLArgParser.ASSRT_NON_UNIQUE_FLAGS);
	}

	private final Map<AssrtCoreCLArgFlag, String[]> assrtParsed = new HashMap<>();
	
	public AssrtCoreCLArgParser(String[] args) throws CommandLineException
	{
		super(args);  // Assigns this.args and calls parseArgs
	}		
	
	public Map<AssrtCoreCLArgFlag, String[]> getAssrtArgs()
	{
		return this.assrtParsed;
	}

	@Override
	protected boolean isFlag(String arg)
	{
		return AssrtCoreCLArgParser.ASSRT_FLAGS.containsKey(arg) || super.isFlag(arg);
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

			case AssrtCoreCLArgParser.ASS_FLAG:
			{
				return assrtParseAss(i);
			}
			case AssrtCoreCLArgParser.ASS_MODEL_PNG_FLAG:
			{
				return assrtParseFileArg(flag, i);
			}
			// No-value args -- just boolean flags
			case AssrtCoreCLArgParser.ASS_MODEL_FLAG:
			{
				assrtCheckAndAddNoArgUniqueFlag(flag);
				return i;
			}
			
			
			// Non-unique flags
			
			case AssrtCoreCLArgParser.ASS_EFSM_FLAG:     return assrtParseRoleArg(flag, i);
			case AssrtCoreCLArgParser.ASS_EFSM_PNG_FLAG: return assrtParseRoleAndFileArgs(flag, i);
			
			
			// Base CL
			
			default:
			{
				return super.parseFlag(i);
			}
		}
	}

	private int assrtParseAss(int i) throws CommandLineException
	{
		if ((i + 1) >= this.args.length)
		{
			throw new CommandLineException("Missing simple global protocol name argument.");
		}
		String proto = this.args[++i];
		assrtCheckAndAddUniqueFlag(AssrtCoreCLArgParser.ASS_FLAG, new String[] { proto });
		return i;
	}

	private void assrtCheckAndAddNoArgUniqueFlag(String flag) throws CommandLineException
	{
		assrtCheckAndAddUniqueFlag(flag, new String[0]);
	}

	private void assrtCheckAndAddUniqueFlag(String flag, String[] args) throws CommandLineException
	{
		AssrtCoreCLArgFlag argFlag = AssrtCoreCLArgParser.ASSRT_UNIQUE_FLAGS.get(flag);
		if (this.assrtParsed.containsKey(argFlag))
		{
			throw new CommandLineException("Duplicate flag: " + flag);
		}
		this.assrtParsed.put(argFlag, args);
	}

	private int assrtParseFileArg(String f, int i) throws CommandLineException
	{
		AssrtCoreCLArgFlag flag = AssrtCoreCLArgParser.ASSRT_UNIQUE_FLAGS.get(f);
		if ((i + 1) >= this.args.length)
		{
			throw new CommandLineException("Missing file argument");
		}
		String file = this.args[++i];
		assrtConcatArgs(flag, file);
		return i;
	}

	private int assrtParseRoleArg(String f, int i) throws CommandLineException
	{
		AssrtCoreCLArgFlag flag = AssrtCoreCLArgParser.ASSRT_NON_UNIQUE_FLAGS.get(f);
		if ((i + 1) >= this.args.length)
		{
			throw new CommandLineException("Missing role argument");
		}
		String role = this.args[++i];
		assrtConcatArgs(flag, role);
		return i;
	}

	protected int assrtParseRoleAndFileArgs(String f, int i) throws CommandLineException
	{
		AssrtCoreCLArgFlag flag = AssrtCoreCLArgParser.ASSRT_NON_UNIQUE_FLAGS.get(f);
		if ((i + 2) >= this.args.length)
		{
			throw new CommandLineException("Missing role/file arguments");
		}
		String role = this.args[++i];
		String png = this.args[++i];
		assrtConcatArgs(flag, role, png);
		return i;
	}
	
	private void assrtConcatArgs(AssrtCoreCLArgFlag flag, String... toAdd)
	{
		String[] args = this.assrtParsed.get(flag);
		if (args == null)
		{
			args = Arrays.copyOf(toAdd, toAdd.length);
		}
		else
		{
			String[] tmp = new String[args.length + toAdd.length];
			System.arraycopy(args, 0, tmp, 0, args.length);
			System.arraycopy(toAdd, 0, tmp, args.length, toAdd.length);
			args = tmp;
		}
		this.assrtParsed.put(flag, args);
	}
}
