package org.scribble.ext.assrt.core.type.session;

import java.util.Collections;
import java.util.List;

import org.scribble.core.type.name.Op;
import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;
import org.scribble.ext.assrt.core.type.name.AssrtAnnotDataType;


public class AssrtCoreMsg
{
	public final Op op;
	public final List<AssrtAnnotDataType> pay;
	public final AssrtBFormula ass;  // cnf?  Set?  // Not null -- empty ass set to True by AssrtCoreGProtocolDeclTranslator
	
	public AssrtCoreMsg(Op op, List<AssrtAnnotDataType> pay, AssrtBFormula ass)
	{
		this.op = op;
		this.pay = Collections.unmodifiableList(pay);
		this.ass = ass;
	}
	
	@Override
	public String toString()
	{
		String pays = this.pay.toString();
		if (this.pay.size() == 1)
		{
			pays = pays.substring(1, pays.length() - 1);  // For back-compat with prev. unary pay restriction
		}
		return this.op + "<" + pays + " | " + this.ass + ">";
	}

	@Override
	public int hashCode()
	{
		int hash = 43;
		hash = 31 * hash + this.op.hashCode();
		hash = 31 * hash + this.pay.hashCode();
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
		return this.op.equals(them.op) && this.pay.equals(them.pay)
				&& this.ass.equals(them.ass);
	}
}
