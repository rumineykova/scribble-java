package org.scribble.ext.assrt.core.job;

import java.util.Set;

import org.scribble.core.job.CoreArgs;
import org.scribble.core.job.CoreFlags;
import org.scribble.ext.assrt.job.AssrtJob.Solver;

public class AssrtCoreArgs extends CoreArgs
{
	public final Solver SOLVER;
	public final boolean Z3_BATCH;
	
	public AssrtCoreArgs(Set<CoreFlags> flags, Solver solver, boolean z3Batch)  // TODO: refactor extension pattern
	{
		super(flags);
		this.SOLVER = solver;
		this.Z3_BATCH = z3Batch;
	}
}
