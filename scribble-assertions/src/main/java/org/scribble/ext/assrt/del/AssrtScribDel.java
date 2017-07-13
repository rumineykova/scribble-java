package org.scribble.ext.assrt.del;

import org.scribble.ast.ScribNode;
import org.scribble.del.ScribDel;
import org.scribble.ext.assrt.visit.wf.AssrtAnnotationChecker;
import org.scribble.main.ScribbleException;

public interface AssrtScribDel extends ScribDel
{
	default void enterAnnotCheck(ScribNode parent, ScribNode child, AssrtAnnotationChecker checker) throws ScribbleException
	{
		 
	}
	
	default ScribNode leaveAnnotCheck(ScribNode parent, ScribNode child,  AssrtAnnotationChecker checker, ScribNode visited) throws ScribbleException
	{
		return visited;
	}
}
