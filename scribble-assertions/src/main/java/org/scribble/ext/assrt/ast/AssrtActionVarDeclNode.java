package org.scribble.ext.assrt.ast;

import org.scribble.ext.assrt.ast.name.simple.AssrtIntVarNameNode;

// Modifiers, e.g., "lin", will probably be added here
public interface AssrtActionVarDeclNode
{
	AssrtIntVarNameNode getAnnotVar();  // CHECKME: needed?
}
