package org.scribble.ext.assrt.ast;

import java.util.List;
import java.util.stream.Collectors;

import org.antlr.runtime.Token;
import org.scribble.ast.Module;
import org.scribble.ast.ModuleMember;
import org.scribble.ast.NameDeclNode;
import org.scribble.ast.ScribNode;
import org.scribble.ast.name.qualified.MemberNameNode;
import org.scribble.core.type.name.ModuleName;
import org.scribble.del.DelFactory;
import org.scribble.ext.assrt.ast.name.qualified.AssrtAssertNameNode;
import org.scribble.ext.assrt.ast.name.simple.AssrtSortNode;
import org.scribble.ext.assrt.core.type.formula.AssrtSmtFormula;
import org.scribble.ext.assrt.core.type.kind.AssrtAssertKind;
import org.scribble.ext.assrt.core.type.name.AssrtAssertName;
import org.scribble.ext.assrt.del.AssrtDelFactory;
import org.scribble.util.Constants;
import org.scribble.util.ScribException;
import org.scribble.visit.AstVisitor;

@Deprecated
public class AssrtAssertDecl extends NameDeclNode<AssrtAssertKind>
		implements ModuleMember
{
	//public static final int NAMENODE_CHILD_INDEX = 0;
	public static final int RETURN_CHILD_INDEX = 1;
	public static final int PARAM_CHILDREN_START_INDEX = 2;

	protected AssrtSmtFormula<?> expr;  // Non public, because non final for reconstruct

	// ScribTreeAdaptor#create constructor
	public AssrtAssertDecl(Token t, AssrtSmtFormula<?> expr)
	{
		super(t);
		this.expr = expr;
	}

	// Tree#dupNode constructor
	protected AssrtAssertDecl(AssrtAssertDecl node)
	{
		super(node);
		this.expr = node.expr;
	}
	
	@Override
	public AssrtAssertNameNode getNameNodeChild()  // FIXME: name node
	{
		return (AssrtAssertNameNode) getRawNameNodeChild();
	}
	
	public AssrtSortNode getReturnChild()
	{
		return (AssrtSortNode) getChild(RETURN_CHILD_INDEX);
	}

	public List<AssrtSortNode> getParamChildren()
	{
		List<? extends ScribNode> cs = getChildren();
		return cs.subList(PARAM_CHILDREN_START_INDEX, cs.size()).stream()
				.map(n -> (AssrtSortNode) n).collect(Collectors.toList());
	}
	
	public AssrtSmtFormula<?> getExpr()
	{
		return this.expr;
	}

	// "add", not "set"
	public void addScribChildren(AssrtAssertNameNode name, AssrtSortNode ret,
			List<AssrtSortNode> params)
	{
		// Cf. above getters and Scribble.g children order
		super.addScribChildren(name);
		addChild(ret);
		addChildren(params);
	}
	
	@Override
	public AssrtAssertDecl dupNode()
	{
		return new AssrtAssertDecl(this);
	}
	
	@Override
	public void decorateDel(DelFactory df)
	{
		((AssrtDelFactory) df).AssrtAssertDecl(this);
	}

	public AssrtAssertDecl reconstruct(MemberNameNode<AssrtAssertKind> name,
			List<AssrtSortNode> params, AssrtSortNode ret, AssrtSmtFormula<?> expr)
	{
		AssrtAssertDecl dup = dupNode();
		dup.addScribChildren((AssrtAssertNameNode) name, ret, params);
		dup.expr = expr;
		dup.setDel(del());  // No copy
		return dup;
	}

	@Override
	public AssrtAssertDecl visitChildren(AstVisitor v) throws ScribException
	{
		AssrtAssertNameNode name = (AssrtAssertNameNode) visitChildWithClassEqualityCheck(
				this, getNameNodeChild(), v);
		AssrtSortNode ret = (AssrtSortNode) visitChildWithClassEqualityCheck(this,
				getReturnChild(), v);
		List<AssrtSortNode> params = visitChildListWithClassEqualityCheck(this,
				getParamChildren(), v);
		return reconstruct(name, params, ret, this.expr);
	}

	@Override
	public AssrtAssertName getDeclName()
	{
		return getNameNodeChild().toName();
	}

	@Override
	public AssrtAssertName getFullMemberName(Module mod)  // CHECKME: asserts aren't directly "typed" -- ?
	{
		ModuleName fullmodname = mod.getFullModuleName();
		return new AssrtAssertName(fullmodname, getDeclName());
	}

	@Override
	public String toString()
	{
		return Constants.ASSERT_KW + getNameNodeChild() + getParamChildren() + " "
				+ getReturnChild() + " = " + this.expr + ";";
	}
}















/*
	public final List<AssrtSortNode> params;
	public final AssrtSortNode ret;
	public final AssrtSmtFormula<?> expr;
	
	public AssrtAssertDecl(CommonTree source, AssrtAssertNameNode name, List<AssrtSortNode> params, AssrtSortNode ret, AssrtSmtFormula<?> expr)
	{
		//super(source, "f#", "FIXME", "FIXME", name);  // FIXME
		super(source, name);
		this.params = Collections.unmodifiableList(params);
		this.ret = ret;
		this.expr = expr;
	}
*/