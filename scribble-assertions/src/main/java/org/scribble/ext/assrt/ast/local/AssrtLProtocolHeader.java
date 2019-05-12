package org.scribble.ext.assrt.ast.local;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.NonRoleParamDeclList;
import org.scribble.ast.RoleDeclList;
import org.scribble.ast.ScribNodeBase;
import org.scribble.ast.local.LProtocolHeader;
import org.scribble.ast.name.qualified.LProtocolNameNode;
import org.scribble.ast.name.qualified.ProtocolNameNode;
import org.scribble.del.ScribDel;
import org.scribble.ext.assrt.ast.AssrtArithExpr;
import org.scribble.ext.assrt.ast.AssrtAssertion;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.ext.assrt.ast.AssrtStateVarDeclAnnotNode;
import org.scribble.ext.assrt.ast.name.simple.AssrtIntVarNameNode;
import org.scribble.ext.assrt.core.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.core.type.name.AssrtDataTypeVar;
import org.scribble.main.ScribbleException;
import org.scribble.type.kind.Local;
import org.scribble.visit.AstVisitor;

// Based on AssrtGProtocolHeader
public class AssrtLProtocolHeader extends LProtocolHeader implements AssrtStateVarDeclAnnotNode
{
	public final List<AssrtIntVarNameNode> annotvars;
	public final List<AssrtArithExpr> annotexprs;
	public final AssrtAssertion ass;  // null if not specified -- currently duplicated from AssrtGMessageTransfer

	public AssrtLProtocolHeader(CommonTree source, LProtocolNameNode name, RoleDeclList roledecls, NonRoleParamDeclList paramdecls)
	{
		this(source, name, roledecls, paramdecls, //null);
				Collections.emptyList(), Collections.emptyList(),
				null);
	}

	public AssrtLProtocolHeader(CommonTree source, LProtocolNameNode name, RoleDeclList roledecls, NonRoleParamDeclList paramdecls, //AssrtAssertion ass)
			List<AssrtIntVarNameNode> annotvars, List<AssrtArithExpr> annotexprs,
			AssrtAssertion ass)
	{
		super(source, name, roledecls, paramdecls);
		this.annotvars = Collections.unmodifiableList(annotvars);
		this.annotexprs = Collections.unmodifiableList(annotexprs);
		this.ass = ass;
	}
	
	// Duplicated from AssrtGProtocolHeader
	// Pre: ass != null
	//public AssrtBinCompFormula getAnnotDataTypeVarInitDecl()
	public Map<AssrtDataTypeVar, AssrtArithFormula> getAnnotDataTypeVarDecls()  // Cf. AssrtAnnotDataTypeElem (no "initializer")
	{
		//return (this.ass == null) ? null : (AssrtBinCompFormula) this.ass.getFormula();
		//return (AssrtBinCompFormula) this.ass.getFormula();
		Iterator<AssrtArithExpr> exprs = this.annotexprs.iterator();
		return this.annotvars.stream().collect(Collectors.toMap(v -> v.toName(), v -> exprs.next().getFormula()));
	}

	@Override
	protected ScribNodeBase copy()
	{
		return new AssrtLProtocolHeader(this.source, getNameNode(), this.roledecls, this.paramdecls, //this.ass);
				this.annotvars, this.annotexprs,
				this.ass);
	}
	
	@Override
	public AssrtLProtocolHeader clone(AstFactory af)
	{
		LProtocolNameNode name = getNameNode().clone(af);
		RoleDeclList roledecls = this.roledecls.clone(af);
		NonRoleParamDeclList paramdecls = this.paramdecls.clone(af);

		List<AssrtIntVarNameNode> annotvars = this.annotvars.stream().map(v -> v.clone(af)).collect(Collectors.toList());
		List<AssrtArithExpr> annotexprs = this.annotexprs.stream().map(e -> e.clone(af)).collect(Collectors.toList());
		AssrtAssertion ass = (this.ass == null) ? null : this.ass.clone(af);

		return ((AssrtAstFactory) af).AssrtLProtocolHeader(this.source, name, roledecls, paramdecls, //ass);
				annotvars, annotexprs,
				ass);
	}

	@Override
	public AssrtLProtocolHeader reconstruct(ProtocolNameNode<Local> name, RoleDeclList rdl, NonRoleParamDeclList pdl)
	{
		throw new RuntimeException("[assrt] Shouldn't get in here: " + this);
	}

	public AssrtLProtocolHeader reconstruct(ProtocolNameNode<Local> name, RoleDeclList rdl, NonRoleParamDeclList pdl, //AssrtAssertion ass)
			List<AssrtIntVarNameNode> annotvars, List<AssrtArithExpr> annotexprs,
			AssrtAssertion ass)
	{
		ScribDel del = del();
		//AssrtLProtocolHeader lph = new AssrtLProtocolHeader(this.source, (LProtocolNameNode) name, rdl, pdl, ass);

		AssrtLProtocolHeader lph = new AssrtLProtocolHeader(this.source, (LProtocolNameNode) name, rdl, pdl, //ass);
				annotvars, annotexprs,
				ass);

		lph = (AssrtLProtocolHeader) lph.del(del);
		return lph;
	}
	
	@Override
	public LProtocolHeader visitChildren(AstVisitor nv) throws ScribbleException
	{
		RoleDeclList rdl = (RoleDeclList) visitChild(this.roledecls, nv);
		NonRoleParamDeclList pdl = (NonRoleParamDeclList) visitChild(this.paramdecls, nv);

		List<AssrtIntVarNameNode> annotvars = visitChildListWithClassEqualityCheck(this, this.annotvars, nv);
		List<AssrtArithExpr> annotexprs = visitChildListWithClassEqualityCheck(this, this.annotexprs, nv);
		AssrtAssertion ass = (this.ass == null) ? null : (AssrtAssertion) visitChild(this.ass, nv);

		return reconstruct((LProtocolNameNode) this.name, rdl, pdl, //ass);
				annotvars, annotexprs,
				ass);
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
	public AssrtAssertion getAssertion()
	{
		return this.ass;
	}
	
	@Override
	public String toString()
	{
		return super.toString() //+ " " + this.ass;
				+ annotToString();
	}
}
