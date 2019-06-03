package org.scribble.ext.assrt.ast;

// ProtoHeader or Recursion
public interface AssrtStateVarDeclNode
{
	//CommonTree getAnnotChild();
	AssrtBExprNode getAnnotAssertChild();
	//List<AssrtIntVarNameNode> getAnnotVarChildren();
	//List<AssrtAExprNode> getAnnotExprChildren();
	public AssrtStateVarDeclList getStateVarDeclListChild();

	default String annotToString()
	{
		/*CommonTree ext = getAnnotChild();
		if (ext == null)
		{
			return "";
		}*/
		/*List<AssrtIntVarNameNode> svars = getAnnotVarChildren();
		Iterator<AssrtAExprNode> sexprs = getAnnotExprChildren().iterator();
		AssrtBExprNode ass = getAnnotAssertChild();
		return " @(\""
				+ (svars.isEmpty()
						? ""
						: "<" + svars.stream().map(v -> v + " := " + sexprs.next())
								.collect(Collectors.joining(", ")) + ">\"")
				+ ((ass == null) ? "" : " " + ass);*/
		AssrtBExprNode ass = getAnnotAssertChild();
		AssrtStateVarDeclList svars = getStateVarDeclListChild();
		return " @(\""
				+ (svars == null ? "" : svars)
				+ (ass == null ? "" : (svars == null ? "" : " ") + ass);
	}
}
