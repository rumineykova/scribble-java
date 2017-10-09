package org.scribble.ast.context;

import java.util.HashMap;
import java.util.Map;

import org.scribble.type.kind.ProtocolKind;
import org.scribble.type.name.DataType;
import org.scribble.type.name.GProtocolName;
import org.scribble.type.name.LProtocolName;
import org.scribble.type.name.MessageSigName;
import org.scribble.type.name.ModuleName;
import org.scribble.type.name.ProtocolName;

// Mutable
public class ScribNameMap
{
	// names -> fully qualified names
	protected final Map<ModuleName, ModuleName> modules = new HashMap<>();
	protected final Map<DataType, DataType> data = new HashMap<>();
	protected final Map<MessageSigName, MessageSigName> sigs = new HashMap<>();
	protected final Map<GProtocolName, GProtocolName> globals = new HashMap<>();
	protected final Map<LProtocolName, LProtocolName> locals = new HashMap<>();
	
	@Override
	public String toString()
	{
		return "(modules=" + this.modules + ", types=" + this.data + ", sigs=" + this.sigs
				+ ", globals=" + this.globals + ", locals=" + this.locals + ")";
	}
	
	public <K extends ProtocolKind> boolean isVisibleProtocolDeclName(ProtocolName<K> visname)
	{
		return this.globals.containsKey(visname) || this.locals.containsKey(visname);
	}
	
	public boolean isVisibleDataType(DataType visname)
	{
		return this.data.containsKey(visname);
	}
}