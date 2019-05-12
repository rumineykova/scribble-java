package org.scribble.ext.assrt.ast.context;

import java.util.HashMap;
import java.util.Map;

import org.scribble.ast.context.ScribNameMap;
import org.scribble.ext.assrt.core.type.name.AssrtAssertName;

// Mutable
public class AssrtScribNameMap extends ScribNameMap
{
	// names -> fully qualified names
	protected final Map<AssrtAssertName, AssrtAssertName> asserts = new HashMap<>();
	
	@Override
	public String toString()
	{
		return "(modules=" + this.modules + ", types=" + this.data + ", sigs=" + this.sigs
				+ ", globals=" + this.globals + ", locals=" + this.locals + ", " + this.asserts + ")";
	}
}
