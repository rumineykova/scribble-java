package org.scribble.ext.assrt.cli;

import java.util.Map;

import org.scribble.cli.CLFlag;
import org.scribble.cli.CLFlags;

public class AssrtCoreCLFlags extends CLFlags
{
	// Unique flags
	public static final String ASSRT_CORE_FLAG = "-assrt";
	/*public static final String ASSRT_CORE_MODEL_FLAG = "-assrt-model";  // cf. SGRAPH
	public static final String ASSRT_CORE_MODEL_PNG_FLAG = "-assrt-modelpng";  // Unique because -assrt proto*/

	public static final String ASSRT_CORE_NATIVE_Z3_FLAG = "-z3";
	public static final String ASSRT_CORE_BATCHING_FLAG = "-batch";  // TODO: -z3-flag
	
	// Non-unique flags
	public static final String ASSRT_CORE_PROJECT_FLAG = "-assrt-project";  // TODO FIXME: return inlined projection (currently deprecated)
	/*public static final String ASSRT_CORE_EFSM_FLAG = "-assrt-fsm";
	public static final String ASSRT_CORE_EFSM_PNG_FLAG = "-assrt-fsmpng";*/

	/*public static final String ASSRT_STP_EFSM_FLAG = "-stp-fsm";
	public static final String ASSRT_STP_EFSM_PNG_FLAG = "-stp-fsmpng";*/
	

	@Override
	protected Map<String, CLFlag> getFlags()
	{
		Map<String, CLFlag> flags = super.getFlags();

		// Unique; barrier irrelevant
		flags.put(ASSRT_CORE_FLAG, 
				new CLFlag(ASSRT_CORE_FLAG, 0, true, false, false, ""));
		/*flags.put(ASSRT_CORE_MODEL_FLAG, 
				new CLFlag(ASSRT_CORE_MODEL_FLAG, 0, true, false, false, ""));
		flags.put(ASSRT_CORE_MODEL_PNG_FLAG, new CLFlag(ASSRT_CORE_MODEL_PNG_FLAG,
				1, true, false, false, "Missing file arg: "));*/
		flags.put(ASSRT_CORE_NATIVE_Z3_FLAG, 
				new CLFlag(ASSRT_CORE_NATIVE_Z3_FLAG, 0, true, false, false, ""));
		flags.put(ASSRT_CORE_BATCHING_FLAG, 
				new CLFlag(ASSRT_CORE_BATCHING_FLAG, 0, true, false, false, ""));

		// Non-unique, no barrier
		flags.put(ASSRT_CORE_PROJECT_FLAG, new CLFlag(ASSRT_CORE_PROJECT_FLAG, 1,
				false, true, false, "Missing role arg: "));
		
		return flags;
	}
}

