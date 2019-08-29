package org.scribble.ext.assrt.core.model.global;

import java.util.Collections;

import org.scribble.core.model.ModelFactory;
import org.scribble.core.type.name.MsgId;
import org.scribble.core.type.name.Role;
import org.scribble.core.type.session.Payload;
import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreESend;
import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;

// N.B. EMsg, but in global package because purpose is in SModel building (cf. SSingleBuffers, global view of local queues)
// Enqueued message
public class AssrtCoreEMsg extends AssrtCoreESend  
		// CHECKME: this doesn't carry stateexprs, refactor? (change super)
		// Otherwise duality mismatch re. startexprs, e.g., A->B.A->C.X<123> w.r.t. A/B (cf. DbC)
{
	//public final Map<AssrtIntVarFormula, AssrtIntVarFormula> shadow;  // N.B. not in equals/hash
	
	/*public AssrtCoreEMsg(EModelFactory ef, AssrtCoreESend es)
	{
		this(ef, es.peer, es.mid, es.payload, es.ass, es.annot, es.expr);
	}*/

	public AssrtCoreEMsg(ModelFactory mf, Role peer, MsgId<?> mid,
			Payload pay, AssrtBFormula ass)//, List<AssrtAFormula> sexprs)
	{
		//this(mf, peer, mid, pay, ass, sexprs, Collections.emptyMap());
		super(mf, peer, mid, pay, ass, Collections.emptyList());  // TODO: empty state exprs, refactor
	}

	/*public AssrtCoreEMsg(ModelFactory mf, Role peer, MsgId<?> mid, Payload pay,
			AssrtBFormula ass, List<AssrtAFormula> sexprs,
			Map<AssrtIntVarFormula, AssrtIntVarFormula> shadow)
	{
		super(mf, peer, mid, pay, ass, sexprs);
		this.shadow = Collections.unmodifiableMap(shadow);
	}*/

	@Override
	public String toString()
	{
		return this.obj + getCommSymbol() + this.mid + this.payload + assertionToString();  // Bypass stateExprsToString(); 
	} 

	@Override
	public int hashCode()
	{
		int hash = 6827;
		hash = 31 * hash + super.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (!(obj instanceof AssrtCoreEMsg))
		{
			return false;
		}
		return super.equals(obj);
	}
	
	@Override
	public boolean canEquals(Object o)  // FIXME: rename canEquals
	{
		return o instanceof AssrtCoreEMsg;
	}
}