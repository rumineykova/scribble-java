package org.scribble.ext.assrt.ast.local;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.local.LContinue;
import org.scribble.ast.local.LInteractionNode;
import org.scribble.ast.name.simple.RecVarNode;
import org.scribble.del.ScribDel;
import org.scribble.ext.assrt.ast.AssrtArithExpr;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.ext.assrt.ast.AssrtStateVarArgAnnotation;
import org.scribble.main.ScribbleException;
import org.scribble.visit.AstVisitor;

public class AssrtLContinue extends LContinue implements AssrtStateVarArgAnnotation
{
	//public final AssrtArithExpr annot;
	public final List<AssrtArithExpr> annotexprs;  // cf. AssrtGDo
	
	public AssrtLContinue(CommonTree source, RecVarNode recvar)
	{
		this(source, recvar, //null);
				Collections.emptyList());
	}

	public AssrtLContinue(CommonTree source, RecVarNode recvar, //AssrtArithExpr annot)
			List<AssrtArithExpr> annotexprs)
	{
		super(source, recvar);
		//this.annot = annot;
		this.annotexprs = Collections.unmodifiableList(annotexprs);
	}

	@Override
	protected AssrtLContinue copy()
	{
		return new AssrtLContinue(this.source, this.recvar, //this.annot);
				this.annotexprs);
	}
	
	@Override
	public AssrtLContinue clone(AstFactory af)
	{
		RecVarNode rv = this.recvar.clone(af);
		//AssrtArithExpr annot = (this.annot == null) ? null : this.annot.clone(af);

		List<AssrtArithExpr> annotexprs = this.annotexprs.stream().map(e -> e.clone(af)).collect(Collectors.toList());

		return ((AssrtAstFactory) af).AssrtLContinue(this.source, rv, //annot);
				annotexprs);
	}

	@Override
	public AssrtLContinue reconstruct(RecVarNode recvar)
	{
		throw new RuntimeException("[assrt] Shouldn't get in here: " + this);
	}

	public AssrtLContinue reconstruct(RecVarNode recvar, //AssrtArithExpr annot)
			List<AssrtArithExpr> annotexprs)
	{
		ScribDel del = del();
		AssrtLContinue lc = new AssrtLContinue(this.source, recvar, //annot);
				annotexprs);
		lc = (AssrtLContinue) lc.del(del);
		return lc;
	}

	@Override
	public LContinue visitChildren(AstVisitor nv) throws ScribbleException
	{
		RecVarNode recvar = (RecVarNode) visitChild(this.recvar, nv);
		//AssrtArithExpr annot = (this.annot == null) ? null : (AssrtArithExpr) visitChild(this.annot, nv);

		List<AssrtArithExpr> annotexprs = visitChildListWithClassEqualityCheck(this, this.annotexprs, nv);

		return reconstruct(recvar, //annot);
				annotexprs);
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
	public List<AssrtArithExpr> getAnnotExprs()
	{
		return this.annotexprs;
	}

	@Override
	public String toString()
	{
		return super.toString() + annotToString();
	}
}
