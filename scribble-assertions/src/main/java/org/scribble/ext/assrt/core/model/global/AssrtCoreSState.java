package org.scribble.ext.assrt.core.model.global;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.scribble.core.model.MState;
import org.scribble.core.model.global.SState;
import org.scribble.core.model.global.actions.SAction;
import org.scribble.core.type.name.Role;

			
//.. do we really need receive-exists?  i.e., "local" vs. "global" TS? -- is global TS really justified/used? -- local TS vs coherence?
					
//.. for scribble, need a property connecting "unrefined" safety and "refined"...


// N.B. does *not* extend AssrtSState -- affects, e.g., dot printing -- CHECKME: what is the problem? the generic param args? and SConfig constr arg
public class AssrtCoreSState extends //MPrettyState<Void, SAction, AssrtCoreSState, Global>
	SState  //AssrtSState  // No: AssrtSConfig constr arg
{

	private final Set<Role> subjs = new HashSet<>();  // Hacky: mostly because EState has no self -- for progress checking

	public AssrtCoreSState(AssrtCoreSConfig config)
	{
		super(config);
	}

	/*@Override
	public AssrtCoreSStateErrors getErrors()
	{
		return new AssrtCoreSStateErrors(core, fullname, this);  // Needs core and fullname
	}*/
	
	public Set<Role> getSubjects()
	{
		return Collections.unmodifiableSet(this.subjs);
	}
	
	public void addSubject(Role subj)
	{
		this.subjs.add(subj);
	}
	
	/*@Override
	protected String getNodeLabel()
	{
		String lab = "(P=" + this.P + ",\nQ=" 
			//+ this.Q 
			+ this.Q.toString().replaceAll("\\\"", "\\\\\"")
			+ ",\nR=" + this.R
			+ ",\nRass=" + this.Rass
			+ ",\nK=" + this.K + ",\nF=" + this.F + ",\nrename=" + this.rename + ")";
		return "label=\"" + this.id + ":" + lab + "\"";
	}*/

	@Override
	protected String getEdgeLabel(SAction msg)
	{
		return "label=\"" + msg.toString().replaceAll("\\\"", "\\\\\"") + "\"";
				// Because of @"..." syntax, need to escape the quotes
	}

	/*@Override
	public void addEdge(SAction a, SState s)  // Visibility hack (for AssrtCoreSModelBuilder::build)
	{
		super.addEdge(a, s);
	}*/
	
	@Override
	public final int hashCode()
	{
		int hash = 22391;
		hash = 31 * hash + super.hashCode();
		return hash;
	}

	// Not using id, cf. ModelState -- FIXME? use a factory pattern that associates unique states and ids? -- use id for hash, and make a separate "semantic equals"
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtCoreSState))
		{
			return false;
		}
		return super.equals(this);  // Checks canEquals
	}

	@Override
	protected boolean canEquals(MState<?, ?, ?, ?> s)
	{
		return s instanceof AssrtCoreSState;
	}


	@Override
	public Set<SState> getReachableStates()
	{
		return getReachableStatesAux(this);
	}
}
