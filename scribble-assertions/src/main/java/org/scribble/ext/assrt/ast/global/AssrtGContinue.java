package org.scribble.ext.assrt.ast.global;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.global.GContinue;
import org.scribble.ast.local.LContinue;
import org.scribble.ast.name.simple.RecVarNode;
import org.scribble.del.ScribDel;
import org.scribble.ext.assrt.ast.AssrtAssertion;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.ext.assrt.ast.local.AssrtLContinue;
import org.scribble.main.ScribbleException;
import org.scribble.type.kind.RecVarKind;
import org.scribble.type.name.Role;
import org.scribble.visit.AstVisitor;

public class AssrtGContinue extends GContinue
{
	public final AssrtAssertion ass;  // cf. AssrtGDo  // FIXME: make specific syntactic expr

	public AssrtGContinue(CommonTree source, RecVarNode recvar)
	{
		this(source, recvar, null);
	}

	public AssrtGContinue(CommonTree source, RecVarNode recvar, AssrtAssertion ass)
	{
		super(source, recvar);
		this.ass = ass;
	}

	// Similar to reconstruct pattern
	public LContinue project(AstFactory af, Role self)
	{
		throw new RuntimeException("[assrt] Shouldn't get in here: " + this);
	}

	public AssrtLContinue project(AstFactory af, Role self, AssrtAssertion ass)
	{
		RecVarNode recvar = (RecVarNode) af.SimpleNameNode(this.recvar.getSource(), RecVarKind.KIND, this.recvar.toName().toString());
		AssrtLContinue projection = ((AssrtAstFactory) af).AssrtLContinue(this.source, recvar, ass);
		return projection;
	}

	@Override
	protected AssrtGContinue copy()
	{
		return new AssrtGContinue(this.source, this.recvar, this.ass);
	}
	
	@Override
	public AssrtGContinue clone(AstFactory af)
	{
		RecVarNode rv = this.recvar.clone(af);
		AssrtAssertion ass = (this.ass == null) ? null : this.ass.clone(af);
		return ((AssrtAstFactory) af).AssrtGContinue(this.source, rv, ass);
	}

	@Override
	public AssrtGContinue reconstruct(RecVarNode recvar)
	{
		throw new RuntimeException("[assrt] Shouldn't get in here: " + this);
	}

	public AssrtGContinue reconstruct(RecVarNode recvar, AssrtAssertion ass)
	{
		ScribDel del = del();
		AssrtGContinue gc = new AssrtGContinue(this.source, recvar, ass);
		gc = (AssrtGContinue) gc.del(del);
		return gc;
	}

	@Override
	public GContinue visitChildren(AstVisitor nv) throws ScribbleException
	{
		RecVarNode recvar = (RecVarNode) visitChild(this.recvar, nv);
		AssrtAssertion ass = (this.ass == null) ? null : (AssrtAssertion) visitChild(this.ass, nv);
		return reconstruct(recvar, ass);
	}
}
