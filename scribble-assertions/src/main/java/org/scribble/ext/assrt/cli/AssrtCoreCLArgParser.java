package org.scribble.ext.assrt.cli;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.scribble.cli.CLArgParser;
import org.scribble.cli.CommandLineException;

public class AssrtCoreCLArgParser extends CLArgParser
{
	// Unique flags
	public static final String ASSRT_CORE_FLAG           = "-ass";
	public static final String ASSRT_CORE_MODEL_FLAG     = "-ass-model";  // cf. SGRAPH
	public static final String ASSRT_CORE_MODEL_PNG_FLAG = "-ass-modelpng";

	public static final String ASSRT_CORE_NATIVE_Z3_FLAG = "-Z3";
	public static final String ASSRT_CORE_BATCHING_FLAG  = "-batch";
	
	// Non-unique flags
	public static final String ASSRT_CORE_EFSM_FLAG      = "-ass-fsm";
	public static final String ASSRT_CORE_EFSM_PNG_FLAG  = "-ass-fsmpng";

	public static final String ASSRT_STP_EFSM_FLAG      = "-stp-fsm";
	public static final String ASSRT_STP_EFSM_PNG_FLAG  = "-stp-fsmpng";
	
	private static final Map<String, AssrtCoreCLArgFlag> ASSRT_CORE_UNIQUE_FLAGS = new HashMap<>();
	{
		AssrtCoreCLArgParser.ASSRT_CORE_UNIQUE_FLAGS.put(AssrtCoreCLArgParser.ASSRT_CORE_FLAG, AssrtCoreCLArgFlag.ASSRT_CORE);
		AssrtCoreCLArgParser.ASSRT_CORE_UNIQUE_FLAGS.put(AssrtCoreCLArgParser.ASSRT_CORE_MODEL_FLAG, AssrtCoreCLArgFlag.ASSRT_CORE_MODEL);
		AssrtCoreCLArgParser.ASSRT_CORE_UNIQUE_FLAGS.put(AssrtCoreCLArgParser.ASSRT_CORE_MODEL_PNG_FLAG, AssrtCoreCLArgFlag.ASSRT_CORE_MODEL_PNG);
		AssrtCoreCLArgParser.ASSRT_CORE_UNIQUE_FLAGS.put(AssrtCoreCLArgParser.ASSRT_CORE_NATIVE_Z3_FLAG, AssrtCoreCLArgFlag.ASSRT_CORE_NATIVE_Z3);
		AssrtCoreCLArgParser.ASSRT_CORE_UNIQUE_FLAGS.put(AssrtCoreCLArgParser.ASSRT_CORE_BATCHING_FLAG, AssrtCoreCLArgFlag.ASSRT_CORE_BATCHING);
	}

	private static final Map<String, AssrtCoreCLArgFlag> ASSRT_CORE_NON_UNIQUE_FLAGS = new HashMap<>();
	{
		AssrtCoreCLArgParser.ASSRT_CORE_NON_UNIQUE_FLAGS.put(AssrtCoreCLArgParser.ASSRT_CORE_EFSM_FLAG, AssrtCoreCLArgFlag.ASSRT_CORE_EFSM);
		AssrtCoreCLArgParser.ASSRT_CORE_NON_UNIQUE_FLAGS.put(AssrtCoreCLArgParser.ASSRT_CORE_EFSM_PNG_FLAG, AssrtCoreCLArgFlag.ASSRT_CORE_EFSM_PNG);
		AssrtCoreCLArgParser.ASSRT_CORE_NON_UNIQUE_FLAGS.put(AssrtCoreCLArgParser.ASSRT_STP_EFSM_FLAG, AssrtCoreCLArgFlag.ASSRT_STP_EFSM);
		AssrtCoreCLArgParser.ASSRT_CORE_NON_UNIQUE_FLAGS.put(AssrtCoreCLArgParser.ASSRT_STP_EFSM_PNG_FLAG, AssrtCoreCLArgFlag.ASSRT_STP_EFSM_PNG);
	}

	private static final Map<String, AssrtCoreCLArgFlag> ASSRT_CORE_FLAGS = new HashMap<>();
	{
		AssrtCoreCLArgParser.ASSRT_CORE_FLAGS.putAll(AssrtCoreCLArgParser.ASSRT_CORE_UNIQUE_FLAGS);
		AssrtCoreCLArgParser.ASSRT_CORE_FLAGS.putAll(AssrtCoreCLArgParser.ASSRT_CORE_NON_UNIQUE_FLAGS);
	}

	private final Map<AssrtCoreCLArgFlag, String[]> assrtCoreParsed = new HashMap<>();
	
	public AssrtCoreCLArgParser(String[] args) throws CommandLineException
	{
		super(args);  // Assigns this.args and calls parseArgs
	}		
	
	public Map<AssrtCoreCLArgFlag, String[]> getAssrtArgs()
	{
		return this.assrtCoreParsed;
	}

	@Override
	protected boolean isFlag(String arg)
	{
		return AssrtCoreCLArgParser.ASSRT_CORE_FLAGS.containsKey(arg) || super.isFlag(arg);
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

			case AssrtCoreCLArgParser.ASSRT_CORE_FLAG:
			{
				return assrtCoreParseAss(i);
			}
			case AssrtCoreCLArgParser.ASSRT_CORE_MODEL_PNG_FLAG:
			{
				return assrtCoreParseFileArg(flag, i);
			}
			// No-value args -- just boolean flags
			case AssrtCoreCLArgParser.ASSRT_CORE_MODEL_FLAG:
			case AssrtCoreCLArgParser.ASSRT_CORE_NATIVE_Z3_FLAG:
			case AssrtCoreCLArgParser.ASSRT_CORE_BATCHING_FLAG:
			{
				assrtCheckAndAddNoArgUniqueFlag(flag);
				return i;
			}
			
			
			// Non-unique flags
			
			case AssrtCoreCLArgParser.ASSRT_CORE_EFSM_FLAG:     return assrtCoreParseRoleArg(flag, i);
			case AssrtCoreCLArgParser.ASSRT_CORE_EFSM_PNG_FLAG: return assrtCoreParseRoleAndFileArgs(flag, i);

			case AssrtCoreCLArgParser.ASSRT_STP_EFSM_FLAG:      return assrtCoreParseRoleArg(flag, i);
			case AssrtCoreCLArgParser.ASSRT_STP_EFSM_PNG_FLAG:  return assrtCoreParseRoleAndFileArgs(flag, i);
			
			
			// Base CL
			
			default:
			{
				return super.parseFlag(i);
			}
		}
	}

	private int assrtCoreParseAss(int i) throws CommandLineException
	{
		if ((i + 1) >= this.args.length)
		{
			throw new CommandLineException("Missing simple global protocol name argument.");
		}
		String proto = this.args[++i];
		assrtCoreCheckAndAddUniqueFlag(AssrtCoreCLArgParser.ASSRT_CORE_FLAG, new String[] { proto });
		return i;
	}

	private void assrtCheckAndAddNoArgUniqueFlag(String flag) throws CommandLineException
	{
		assrtCoreCheckAndAddUniqueFlag(flag, new String[0]);
	}

	private void assrtCoreCheckAndAddUniqueFlag(String flag, String[] args) throws CommandLineException
	{
		AssrtCoreCLArgFlag argFlag = AssrtCoreCLArgParser.ASSRT_CORE_UNIQUE_FLAGS.get(flag);
		if (this.assrtCoreParsed.containsKey(argFlag))
		{
			throw new CommandLineException("Duplicate flag: " + flag);
		}
		this.assrtCoreParsed.put(argFlag, args);
	}

	private int assrtCoreParseFileArg(String f, int i) throws CommandLineException
	{
		AssrtCoreCLArgFlag flag = AssrtCoreCLArgParser.ASSRT_CORE_UNIQUE_FLAGS.get(f);
		if ((i + 1) >= this.args.length)
		{
			throw new CommandLineException("Missing file argument");
		}
		String file = this.args[++i];
		assrtCoreConcatArgs(flag, file);
		return i;
	}

	private int assrtCoreParseRoleArg(String f, int i) throws CommandLineException
	{
		AssrtCoreCLArgFlag flag = AssrtCoreCLArgParser.ASSRT_CORE_NON_UNIQUE_FLAGS.get(f);
		if ((i + 1) >= this.args.length)
		{
			throw new CommandLineException("Missing role argument");
		}
		String role = this.args[++i];
		assrtCoreConcatArgs(flag, role);
		return i;
	}

	protected int assrtCoreParseRoleAndFileArgs(String f, int i) throws CommandLineException
	{
		AssrtCoreCLArgFlag flag = AssrtCoreCLArgParser.ASSRT_CORE_NON_UNIQUE_FLAGS.get(f);
		if ((i + 2) >= this.args.length)
		{
			throw new CommandLineException("Missing role/file arguments");
		}
		String role = this.args[++i];
		String png = this.args[++i];
		assrtCoreConcatArgs(flag, role, png);
		return i;
	}
	
	private void assrtCoreConcatArgs(AssrtCoreCLArgFlag flag, String... toAdd)
	{
		String[] args = this.assrtCoreParsed.get(flag);
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
		this.assrtCoreParsed.put(flag, args);
	}
}
