package org.scribble.ext.assrt.ast.global;

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
import org.scribble.ast.global.GProtocolHeader;
import org.scribble.ast.name.qualified.GProtocolNameNode;
import org.scribble.ast.name.qualified.LProtocolNameNode;
import org.scribble.ast.name.qualified.ProtocolNameNode;
import org.scribble.del.ScribDel;
import org.scribble.ext.assrt.ast.AssrtArithExpr;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.ext.assrt.ast.local.AssrtLProtocolHeader;
import org.scribble.ext.assrt.ast.name.simple.AssrtIntVarNameNode;
import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.main.ScribbleException;
import org.scribble.type.kind.Global;
import org.scribble.type.name.Role;
import org.scribble.visit.AstVisitor;

public class AssrtGProtocolHeader extends GProtocolHeader
{
	//public final AssrtAssertion ass;  // null if not specified -- currently duplicated from AssrtGMessageTransfer
	public final List<AssrtIntVarNameNode> annotvars;
	public final List<AssrtArithExpr> annotexprs;

	public AssrtGProtocolHeader(CommonTree source, GProtocolNameNode name, RoleDeclList roledecls, NonRoleParamDeclList paramdecls)
	{
		this(source, name, roledecls, paramdecls, //null);
				Collections.emptyList(), Collections.emptyList());
	}

	//Pre: annotvars.size() == annotexprs.size()
	public AssrtGProtocolHeader(CommonTree source, GProtocolNameNode name, RoleDeclList roledecls, NonRoleParamDeclList paramdecls, //AssrtAssertion ass)
			List<AssrtIntVarNameNode> annotvars, List<AssrtArithExpr> annotexprs)
	{
		super(source, name, roledecls, paramdecls);
		//this.ass = ass;
		this.annotvars = Collections.unmodifiableList(annotvars);
		this.annotexprs = Collections.unmodifiableList(annotexprs);
	}
	
	// FIXME: define restrictions directly in ANTLR grammar, and make a separate AST class for protocol header var init-decl annotations
	// Pre: ass != null
	//public AssrtBinCompFormula getAnnotDataTypeVarInitDecl()  // Cf. AssrtAnnotDataTypeElem (no "initializer")
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
		return new AssrtGProtocolHeader(this.source, getNameNode(), this.roledecls, this.paramdecls, //this.ass);
				this.annotvars, this.annotexprs);
	}
	
	@Override
	public AssrtGProtocolHeader clone(AstFactory af)
	{
		GProtocolNameNode name = getNameNode().clone(af);
		RoleDeclList roledecls = this.roledecls.clone(af);
		NonRoleParamDeclList paramdecls = this.paramdecls.clone(af);
		//AssrtAssertion ass = (this.ass == null) ? null : this.ass.clone(af);
		
		List<AssrtIntVarNameNode> annotvars = this.annotvars.stream().map(v -> v.clone(af)).collect(Collectors.toList());
		List<AssrtArithExpr> annotexprs = this.annotexprs.stream().map(e -> e.clone(af)).collect(Collectors.toList());
		
		return ((AssrtAstFactory) af).AssrtGProtocolHeader(this.source, name, roledecls, paramdecls, //ass);
				annotvars, annotexprs);
	}

	@Override
	public AssrtGProtocolHeader reconstruct(ProtocolNameNode<Global> name, RoleDeclList rdl, NonRoleParamDeclList pdl)
	{
		throw new RuntimeException("[assrt] Shouldn't get in here: " + this);
	}

	public AssrtGProtocolHeader reconstruct(ProtocolNameNode<Global> name, RoleDeclList rdl, NonRoleParamDeclList pdl, //AssrtAssertion ass)
			List<AssrtIntVarNameNode> annotvars, List<AssrtArithExpr> annotexprs)
	{
		ScribDel del = del();

		AssrtGProtocolHeader gph = new AssrtGProtocolHeader(this.source, (GProtocolNameNode) name, rdl, pdl, //ass);
				annotvars, annotexprs);

		gph = (AssrtGProtocolHeader) gph.del(del);
		return gph;
	}
	
	@Override
	public GProtocolHeader visitChildren(AstVisitor nv) throws ScribbleException
	{
		RoleDeclList rdl = (RoleDeclList) visitChild(this.roledecls, nv);
		NonRoleParamDeclList pdl = (NonRoleParamDeclList) visitChild(this.paramdecls, nv);
		//AssrtAssertion ass = (this.ass == null) ? null : (AssrtAssertion) visitChild(this.ass, nv);

		List<AssrtIntVarNameNode> annotvars = visitChildListWithClassEqualityCheck(this, this.annotvars, nv);
		List<AssrtArithExpr> annotexprs = visitChildListWithClassEqualityCheck(this, this.annotexprs, nv);

		return reconstruct((GProtocolNameNode) this.name, rdl, pdl, //ass);
				annotvars, annotexprs);
	}

	// project method pattern is similar to reconstruct
	@Override
	public AssrtLProtocolHeader project(AstFactory af, Role self, LProtocolNameNode name, RoleDeclList roledecls, NonRoleParamDeclList paramdecls)
	{
		//return ((AssrtAstFactory) af).AssrtLProtocolHeader(this.source, name, roledecls, paramdecls, null);
		throw new RuntimeException("[assrt] Shouldn't get in here: " + this);
	}

	// FIXME: make a delegate and move there?
	public AssrtLProtocolHeader project(AstFactory af, Role self, LProtocolNameNode name, RoleDeclList roledecls, NonRoleParamDeclList paramdecls, //AssrtAssertion ass)
			List<AssrtIntVarNameNode> annotvars, List<AssrtArithExpr> annotexprs)
	{
		return ((AssrtAstFactory) af).AssrtLProtocolHeader(this.source, name, roledecls, paramdecls, //ass);
				annotvars, annotexprs);
	}
	
	@Override
	public String toString()
	{
		Iterator<AssrtArithExpr> exprs = this.annotexprs.iterator();
		return super.toString() + " " //+ this.ass;
				+ "<" + this.annotvars.stream().map(v -> v + " := " + exprs.next()).collect(Collectors.joining(", ")) +  ">";
	}
}
