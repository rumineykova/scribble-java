package org.scribble.ext.assrt.core.ast;

import java.util.Collections;
import java.util.List;

import org.scribble.core.type.name.Op;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.name.AssrtAnnotDataType;


public //abstract
		class AssrtCoreMsg
{
	public final Op op;
	//public final AssrtAnnotDataType pay;
	public final List<AssrtAnnotDataType> pays;
	public final AssrtBoolFormula ass;  // cnf?  Set?  // Not null -- empty ass set to True by AssrtCoreGProtocolDeclTranslator
	
	//public AssrtCoreMessage(Op op, AssrtAnnotDataType pay, AssrtBoolFormula ass)
	public AssrtCoreMsg(Op op, List<AssrtAnnotDataType> pays, AssrtBoolFormula ass)
	{
		this.op = op;
		this.pays = Collections.unmodifiableList(pays);
		this.ass = ass;
	}
	
	@Override
	public String toString()
	{
		String pays = this.pays.toString();
		if (this.pays.size() == 1)
		{
			pays = pays.substring(1, pays.length() - 1);  // For back-compat with prev. unary pay restriction
		}
		return this.op + "<" + pays + " | " + this.ass + ">";
	}

	@Override
	public int hashCode()
	{
		int hash = 43;
		//hash = 31 * hash + this.subjs.hashCode();
		hash = 31 * hash + this.op.hashCode();
		hash = 31 * hash + this.pays.hashCode();
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
		if (!(obj instanceof AssrtCoreMsg))
		{
			return false;
		}
		AssrtCoreMsg them = (AssrtCoreMsg) obj;
		//return them.canEquals(this) && this.subjs.equals(them.subjs);
		return //them.canEquals(this) && 
				   this.op.equals(them.op) && this.pays.equals(them.pays)
				&& this.ass.equals(them.ass);
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
