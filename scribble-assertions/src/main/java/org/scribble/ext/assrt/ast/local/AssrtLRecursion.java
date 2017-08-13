package org.scribble.ext.assrt.ast.local;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.Constants;
import org.scribble.ast.ProtocolBlock;
import org.scribble.ast.local.LInteractionNode;
import org.scribble.ast.local.LProtocolBlock;
import org.scribble.ast.local.LRecursion;
import org.scribble.ast.name.simple.RecVarNode;
import org.scribble.del.ScribDel;
import org.scribble.ext.assrt.ast.AssrtArithExpr;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.ext.assrt.ast.AssrtStateVarDeclAnnotation;
import org.scribble.ext.assrt.ast.name.simple.AssrtIntVarNameNode;
import org.scribble.main.ScribbleException;
import org.scribble.type.kind.Local;
import org.scribble.visit.AstVisitor;

public class AssrtLRecursion extends LRecursion implements AssrtStateVarDeclAnnotation
{
	//public final AssrtAssertion ass;  // cf. AssrtGProtocolHeader  // FIXME: make specific syntactic expr

	public final List<AssrtIntVarNameNode> annotvars;
	public final List<AssrtArithExpr> annotexprs;
	
	public AssrtLRecursion(CommonTree source, RecVarNode recvar, LProtocolBlock block)
	{
		this(source, recvar, block, //null);
				Collections.emptyList(), Collections.emptyList());
	}

	public AssrtLRecursion(CommonTree source, RecVarNode recvar, LProtocolBlock block, //AssrtAssertion ass)
			List<AssrtIntVarNameNode> annotvars, List<AssrtArithExpr> annotexprs)
	{
		super(source, recvar, block);
		//this.ass = ass;
		this.annotvars = Collections.unmodifiableList(annotvars);
		this.annotexprs = Collections.unmodifiableList(annotexprs);
	}

	@Override
	protected AssrtLRecursion copy()
	{
		return new AssrtLRecursion(this.source, this.recvar, getBlock(), //this.ass);
				this.annotvars, this.annotexprs);
	}
	
	@Override
	public AssrtLRecursion clone(AstFactory af)
	{
		RecVarNode recvar = this.recvar.clone(af);
		LProtocolBlock block = getBlock().clone(af);
		//AssrtAssertion ass = (this.ass == null) ? null : this.ass.clone(af);

		List<AssrtIntVarNameNode> annotvars = this.annotvars.stream().map(v -> v.clone(af)).collect(Collectors.toList());
		List<AssrtArithExpr> annotexprs = this.annotexprs.stream().map(e -> e.clone(af)).collect(Collectors.toList());

		return ((AssrtAstFactory) af).AssrtLRecursion(this.source, recvar, block, //ass);
				annotvars, annotexprs);
	}

	@Override
	public AssrtLRecursion reconstruct(RecVarNode recvar, ProtocolBlock<Local> block)
	{
		throw new RuntimeException("[assrt] Shouldn't get in here: " + this);
	}

	public AssrtLRecursion reconstruct(RecVarNode recvar, ProtocolBlock<Local> block, //AssrtAssertion ass)
			List<AssrtIntVarNameNode> annotvars, List<AssrtArithExpr> annotexprs)
	{
		ScribDel del = del();
		AssrtLRecursion lr = new AssrtLRecursion(this.source, recvar, (LProtocolBlock) block, //ass);
				annotvars, annotexprs);
		lr = (AssrtLRecursion) lr.del(del);
		return lr;
	}

	@Override
	public AssrtLRecursion visitChildren(AstVisitor nv) throws ScribbleException
	{
		RecVarNode recvar = (RecVarNode) visitChild(this.recvar, nv);
		LProtocolBlock block = visitChildWithClassEqualityCheck(this, getBlock(), nv);
		//AssrtAssertion ass = (this.ass == null) ? null : (AssrtAssertion) visitChild(this.ass, nv);

		List<AssrtIntVarNameNode> annotvars = visitChildListWithClassEqualityCheck(this, this.annotvars, nv);
		List<AssrtArithExpr> annotexprs = visitChildListWithClassEqualityCheck(this, this.annotexprs, nv);

		return reconstruct(recvar, block, //ass);
				annotvars, annotexprs);
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
	public List<AssrtIntVarNameNode> getAnnotVars()
	{
		return this.annotvars;
	}

	@Override
	public List<AssrtArithExpr> getAnnotExprs()
	{
		return this.annotexprs;
	}

	@Override
	public String toString()
	{
		return Constants.REC_KW + " " + this.recvar //+ " " + this.ass + " " 
				+ annotToString()
				+ this.block;
	}
}
