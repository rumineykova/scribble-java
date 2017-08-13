package org.scribble.ext.assrt.ast.global;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.global.GContinue;
import org.scribble.ast.local.LContinue;
import org.scribble.ast.name.simple.RecVarNode;
import org.scribble.del.ScribDel;
import org.scribble.ext.assrt.ast.AssrtArithExpr;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.ext.assrt.ast.AssrtStateVarArgAnnotNode;
import org.scribble.ext.assrt.ast.local.AssrtLContinue;
import org.scribble.main.ScribbleException;
import org.scribble.type.kind.RecVarKind;
import org.scribble.type.name.Role;
import org.scribble.visit.AstVisitor;

public class AssrtGContinue extends GContinue implements AssrtStateVarArgAnnotNode
{
	public final List<AssrtArithExpr> annotexprs;  // cf. AssrtGDo

	public AssrtGContinue(CommonTree source, RecVarNode recvar)
	{
		this(source, recvar, //null);
				Collections.emptyList());
	}

	public AssrtGContinue(CommonTree source, RecVarNode recvar, //AssrtArithExpr annot)
			List<AssrtArithExpr> annotexprs)
	{
		super(source, recvar);
		//this.annot = annot;
		this.annotexprs = Collections.unmodifiableList(annotexprs);
	}

	// Similar to reconstruct pattern
	public LContinue project(AstFactory af, Role self)
	{
		throw new RuntimeException("[assrt] Shouldn't get in here: " + this);
	}

	public AssrtLContinue project(AstFactory af, Role self, //AssrtArithExpr annot)
			List<AssrtArithExpr> annotexprs)
	{
		RecVarNode recvar = (RecVarNode) af.SimpleNameNode(this.recvar.getSource(), RecVarKind.KIND, this.recvar.toName().toString());
		AssrtLContinue projection = ((AssrtAstFactory) af).AssrtLContinue(this.source, recvar, //annot);
				annotexprs);
		return projection;
	}

	@Override
	protected AssrtGContinue copy()
	{
		return new AssrtGContinue(this.source, this.recvar, //this.annot);
				this.annotexprs);
	}
	
	@Override
	public AssrtGContinue clone(AstFactory af)
	{
		RecVarNode rv = this.recvar.clone(af);
		//AssrtArithExpr annot = (this.annot == null) ? null : this.annot.clone(af);

		List<AssrtArithExpr> annotexprs = this.annotexprs.stream().map(e -> e.clone(af)).collect(Collectors.toList());

		return ((AssrtAstFactory) af).AssrtGContinue(this.source, rv, //annot);
				annotexprs);
	}

	@Override
	public AssrtGContinue reconstruct(RecVarNode recvar)
	{
		throw new RuntimeException("[assrt] Shouldn't get in here: " + this);
	}

	public AssrtGContinue reconstruct(RecVarNode recvar, //AssrtArithExpr annot)
			List<AssrtArithExpr> annotexprs)
	{
		ScribDel del = del();
		AssrtGContinue gc = new AssrtGContinue(this.source, recvar, //annot);
				annotexprs);
		gc = (AssrtGContinue) gc.del(del);
		return gc;
	}

	@Override
	public GContinue visitChildren(AstVisitor nv) throws ScribbleException
	{
		RecVarNode recvar = (RecVarNode) visitChild(this.recvar, nv);
		//AssrtArithExpr annot = (this.annot == null) ? null : (AssrtArithExpr) visitChild(this.annot, nv);  // FIXME: visit child with cast

		List<AssrtArithExpr> annotexprs = visitChildListWithClassEqualityCheck(this, this.annotexprs, nv);

		return reconstruct(recvar, //annot);
				annotexprs);
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
