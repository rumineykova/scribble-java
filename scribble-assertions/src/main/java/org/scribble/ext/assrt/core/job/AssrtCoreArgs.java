package org.scribble.ext.assrt.core.job;

import java.util.Set;

import org.scribble.core.job.CoreArgs;
import org.scribble.core.job.CoreFlags;
import org.scribble.ext.assrt.job.AssrtJob.Solver;

public class AssrtCoreArgs extends CoreArgs
{
	public final Solver solver;
	public final boolean z3Batching;
	
	public AssrtCoreArgs(Set<CoreFlags> flags, Solver solver, boolean z3Batching)  // TODO: refactor extension pattern
	{
		super(flags);
		this.solver = solver;
		this.z3Batching = z3Batching;
		if (z3Batching)
		{
			throw new RuntimeException("[assrt-core] [TODO] z3 batching : ");
		}
	}
}
