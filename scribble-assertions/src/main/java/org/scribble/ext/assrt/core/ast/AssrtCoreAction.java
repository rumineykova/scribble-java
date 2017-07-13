package org.scribble.ext.assrt.core.ast;

import org.scribble.ast.name.simple.OpNode;
import org.scribble.ext.assrt.ast.AssrtAnnotDataTypeElem;
import org.scribble.ext.assrt.ast.AssrtAssertion;
import org.scribble.sesstype.kind.DataTypeKind;


public //abstract
class AssrtCoreAction
{
	/*public final Op op;
	public final AssrtAnnotDataType pay;*/  // Not syntax -- but OK/better?

	public final OpNode op;
	public final AssrtAnnotDataTypeElem<DataTypeKind> pay;
	public final AssrtAssertion ass;  // cnf?  Set?
	
	//public AssrtCoreAction(Op op, AssrtAnnotDataType pay, AssrtAssertion ass)
	public AssrtCoreAction(OpNode op, AssrtAnnotDataTypeElem<DataTypeKind> pay, AssrtAssertion ass)
	{
		this.op = op;
		this.pay = pay;
		this.ass = ass;
	}
	
	@Override
	public String toString()
	{
		return this.op + "<" + this.pay + " | " + this.ass + ">";
	}

	@Override
	public int hashCode()
	{
		int hash = 43;
		//hash = 31 * hash + this.subjs.hashCode();
		hash = 31 * hash + this.op.hashCode();
		hash = 31 * hash + this.ass.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		if (!(obj instanceof AssrtCoreAction))
		{
			return false;
		}
		AssrtCoreAction them = (AssrtCoreAction) obj;
		//return them.canEquals(this) && this.subjs.equals(them.subjs);
		return //them.canEquals(this) && 
				this.op.equals(them.op) && this.ass.equals(them.ass);
	}

	//protected abstract boolean canEquals(Object o);
	

	
	
	
	/*public final Set<Role> subjs;  // disconnect has two subjs
	public final Set<Role> objs;  // size <= 1 (no "multicast")
	
	public AssrtCoreAstAction(List<Role> subjs, List<Role> objs)
	{
		this.subjs = Collections.unmodifiableSet(new HashSet<>(subjs));
		this.objs = Collections.unmodifiableSet(new HashSet<>(objs));
	}*/

	/*public boolean isMessageAction()
	{
		return false;
	}*/
	
	/*public Set<Role> getSubjects()
	{
		return this.subjs;
	}*/
}
