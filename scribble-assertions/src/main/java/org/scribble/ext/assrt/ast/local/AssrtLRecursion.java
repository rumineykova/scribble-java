package org.scribble.ext.assrt.ast.local;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.Constants;
import org.scribble.ast.ProtocolBlock;
import org.scribble.ast.local.LInteractionNode;
import org.scribble.ast.local.LProtocolBlock;
import org.scribble.ast.local.LRecursion;
import org.scribble.ast.name.simple.RecVarNode;
import org.scribble.del.ScribDel;
import org.scribble.ext.assrt.ast.AssrtAssertion;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.main.ScribbleException;
import org.scribble.type.kind.Local;
import org.scribble.visit.AstVisitor;

public class AssrtLRecursion extends LRecursion
{
	public final AssrtAssertion ass;  // cf. AssrtGProtocolHeader  // FIXME: make specific syntactic expr
	
	public AssrtLRecursion(CommonTree source, RecVarNode recvar, LProtocolBlock block)
	{
		this(source, recvar, block, null);
	}

	public AssrtLRecursion(CommonTree source, RecVarNode recvar, LProtocolBlock block, AssrtAssertion ass)
	{
		super(source, recvar, block);
		this.ass = ass;
	}

	@Override
	protected AssrtLRecursion copy()
	{
		return new AssrtLRecursion(this.source, this.recvar, getBlock(), this.ass);
	}
	
	@Override
	public AssrtLRecursion clone(AstFactory af)
	{
		RecVarNode recvar = this.recvar.clone(af);
		LProtocolBlock block = getBlock().clone(af);
		AssrtAssertion ass = (this.ass == null) ? null : this.ass.clone(af);
		return ((AssrtAstFactory) af).AssrtLRecursion(this.source, recvar, block, ass);
	}

	@Override
	public AssrtLRecursion reconstruct(RecVarNode recvar, ProtocolBlock<Local> block)
	{
		throw new RuntimeException("[assrt] Shouldn't get in here: " + this);
	}

	public AssrtLRecursion reconstruct(RecVarNode recvar, ProtocolBlock<Local> block, AssrtAssertion ass)
	{
		ScribDel del = del();
		AssrtLRecursion lr = new AssrtLRecursion(this.source, recvar, (LProtocolBlock) block, ass);
		lr = (AssrtLRecursion) lr.del(del);
		return lr;
	}

	@Override
	public AssrtLRecursion visitChildren(AstVisitor nv) throws ScribbleException
	{
		RecVarNode recvar = (RecVarNode) visitChild(this.recvar, nv);
		LProtocolBlock block = visitChildWithClassEqualityCheck(this, getBlock(), nv);
		AssrtAssertion ass = (this.ass == null) ? null : (AssrtAssertion) visitChild(this.ass, nv);
		return reconstruct(recvar, block, ass);
	}

	// Duplicated from super
	@Override
	public LInteractionNode merge(AstFactory af, LInteractionNode ln) throws ScribbleException
	{
		if (!(ln instanceof AssrtLRecursion) || !this.canMerge(ln))
		{
			throw new ScribbleException("Cannot merge " + this.getClass() + " and " + ln.getClass() + ": " + this + ", " + ln);
		}
		LRecursion them = ((LRecursion) ln);
		if (!this.recvar.equals(them.recvar))
		{
			throw new ScribbleException("Cannot merge recursions for " + this.recvar + " and " + them.recvar + ": " + this + ", " + ln);
		}
		
		// FIXME: ass?
		
		return af.LRecursion(this.source, this.recvar.clone(af), getBlock().merge(them.getBlock()));  // Not reconstruct: leave context building to post-projection passes
				// HACK: this source
	}
	
	@Override
	public boolean canMerge(LInteractionNode ln)
	{
		return ln instanceof AssrtLRecursion;
	}

	@Override
	public String toString()
	{
		return Constants.REC_KW + " " + this.recvar + " " + this.ass + " " + block;
	}
}
