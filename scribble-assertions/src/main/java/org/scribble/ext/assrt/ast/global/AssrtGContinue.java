package org.scribble.ext.assrt.ast.global;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.Constants;
import org.scribble.ast.global.GContinue;
import org.scribble.ast.local.LContinue;
import org.scribble.ast.name.simple.RecVarNode;
import org.scribble.del.ScribDel;
import org.scribble.ext.assrt.ast.AssrtArithExpr;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.ext.assrt.ast.local.AssrtLContinue;
import org.scribble.main.ScribbleException;
import org.scribble.type.kind.RecVarKind;
import org.scribble.type.name.Role;
import org.scribble.visit.AstVisitor;

public class AssrtGContinue extends GContinue
{
	public final AssrtArithExpr annot;  // cf. AssrtGDo  // FIXME: make specific syntactic expr

	public AssrtGContinue(CommonTree source, RecVarNode recvar)
	{
		this(source, recvar, null);
	}

	public AssrtGContinue(CommonTree source, RecVarNode recvar, AssrtArithExpr annot)
	{
		super(source, recvar);
		this.annot = annot;
	}

	// Similar to reconstruct pattern
	public LContinue project(AstFactory af, Role self)
	{
		throw new RuntimeException("[assrt] Shouldn't get in here: " + this);
	}

	public AssrtLContinue project(AstFactory af, Role self, AssrtArithExpr annot)
	{
		RecVarNode recvar = (RecVarNode) af.SimpleNameNode(this.recvar.getSource(), RecVarKind.KIND, this.recvar.toName().toString());
		AssrtLContinue projection = ((AssrtAstFactory) af).AssrtLContinue(this.source, recvar, annot);
		return projection;
	}

	@Override
	protected AssrtGContinue copy()
	{
		return new AssrtGContinue(this.source, this.recvar, this.annot);
	}
	
	@Override
	public AssrtGContinue clone(AstFactory af)
	{
		RecVarNode rv = this.recvar.clone(af);
		AssrtArithExpr annot = (this.annot == null) ? null : this.annot.clone(af);
		return ((AssrtAstFactory) af).AssrtGContinue(this.source, rv, annot);
	}

	@Override
	public AssrtGContinue reconstruct(RecVarNode recvar)
	{
		throw new RuntimeException("[assrt] Shouldn't get in here: " + this);
	}

	public AssrtGContinue reconstruct(RecVarNode recvar, AssrtArithExpr annot)
	{
		ScribDel del = del();
		AssrtGContinue gc = new AssrtGContinue(this.source, recvar, annot);
		gc = (AssrtGContinue) gc.del(del);
		return gc;
	}

	@Override
	public GContinue visitChildren(AstVisitor nv) throws ScribbleException
	{
		RecVarNode recvar = (RecVarNode) visitChild(this.recvar, nv);
		AssrtArithExpr annot = (this.annot == null) ? null : (AssrtArithExpr) visitChild(this.annot, nv);  // FIXME: visit child with cast
		return reconstruct(recvar, annot);
	}

	@Override
	public String toString()
	{
		return Constants.CONTINUE_KW + " " + this.recvar + "; " + this.annot;
	}
}
