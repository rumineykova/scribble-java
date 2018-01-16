package org.scribble.ext.assrt.cli;

public enum AssrtCoreCLArgFlag
{
	// Unique flags
	ASSRT_CORE,  // For assrt-*core* mode
	ASSRT_CORE_MODEL,  // cf. SGRAPH
	ASSRT_CORE_MODEL_PNG,
	
	ASSRT_CORE_NATIVE_Z3,
	ASSRT_CORE_BATCHING,
	
	// Non-unique flags
	ASSRT_CORE_EFSM,
	ASSRT_CORE_EFSM_PNG,

	ASSRT_STP_EFSM,
	ASSRT_STP_EFSM_PNG,
}
