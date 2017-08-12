package org.scribble.ext.assrt.ast.local;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.Constants;
import org.scribble.ast.local.LContinue;
import org.scribble.ast.local.LInteractionNode;
import org.scribble.ast.name.simple.RecVarNode;
import org.scribble.del.ScribDel;
import org.scribble.ext.assrt.ast.AssrtArithExpr;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.main.ScribbleException;
import org.scribble.visit.AstVisitor;

public class AssrtLContinue extends LContinue
{
	public final AssrtArithExpr annot;  // cf. AssrtGDo  // FIXME: make specific syntactic expr
	
	public AssrtLContinue(CommonTree source, RecVarNode recvar)
	{
		this(source, recvar, null);
	}

	public AssrtLContinue(CommonTree source, RecVarNode recvar, AssrtArithExpr annot)
	{
		super(source, recvar);
		this.annot = annot;
	}

	@Override
	protected AssrtLContinue copy()
	{
		return new AssrtLContinue(this.source, this.recvar, this.annot);
	}
	
	@Override
	public AssrtLContinue clone(AstFactory af)
	{
		RecVarNode rv = this.recvar.clone(af);
		AssrtArithExpr annot = (this.annot == null) ? null : this.annot.clone(af);
		return ((AssrtAstFactory) af).AssrtLContinue(this.source, rv, annot);
	}

	@Override
	public AssrtLContinue reconstruct(RecVarNode recvar)
	{
		throw new RuntimeException("[assrt] Shouldn't get in here: " + this);
	}

	public AssrtLContinue reconstruct(RecVarNode recvar, AssrtArithExpr annot)
	{
		ScribDel del = del();
		AssrtLContinue lc = new AssrtLContinue(this.source, recvar, annot);
		lc = (AssrtLContinue) lc.del(del);
		return lc;
	}

	@Override
	public LContinue visitChildren(AstVisitor nv) throws ScribbleException
	{
		RecVarNode recvar = (RecVarNode) visitChild(this.recvar, nv);
		AssrtArithExpr annot = (this.annot == null) ? null : (AssrtArithExpr) visitChild(this.annot, nv);
		return reconstruct(recvar, annot);
	}

	// Duplicated from super
	@Override
	public LInteractionNode merge(AstFactory af, LInteractionNode ln) throws ScribbleException
	{
		if (!(ln instanceof AssrtLContinue) || !this.canMerge(ln))
		{
			throw new ScribbleException("Cannot merge " + this.getClass() + " and " + ln.getClass() + ": " + this + ", " + ln);
		}
		AssrtLContinue them = ((AssrtLContinue) ln);
		if (!this.recvar.equals(them.recvar))
		{
			throw new ScribbleException("Cannot merge choices for " + this.recvar + " and " + them.recvar + ": " + this + ", " + ln);
		}
		
		// FIXME: ass
		
		return clone(af);
	}

	@Override
	public boolean canMerge(LInteractionNode ln)
	{
		return ln instanceof AssrtLContinue;
	}

	@Override
	public String toString()
	{
		return Constants.CONTINUE_KW + " " + this.recvar + "; " + this.annot;
	}
}
