package org.scribble.ext.assrt.core.model.global;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// Duplicated from F17LTSSafetyErrors
// Wait-for errors?
public class AssrtCoreSafetyErrors
{
	/*public final Set<AssrtCoreSState> reception;
	public final Set<AssrtCoreSState> orphan;
	public final Set<AssrtCoreSState> unfinishedRole;
	public final Set<AssrtCoreSState> connection;
	public final Set<AssrtCoreSState> unconnected;
	public final Set<AssrtCoreSState> synchronisation;
	public final Set<AssrtCoreSState> disconnect;
	public final Set<AssrtCoreSState> unknownVars;
	public final Set<AssrtCoreSState> unsats;*/

	/*public final Set<AssrtCoreSState> portOpens;
	public final Set<AssrtCoreSState> portOwners;*/

	private enum ERR
	{
		Reception,
		UnfinishedRole,
		Orphan,
		Connection,
		Unconnected,
		Synchronisation,
		Disconnect,
		UnknownDataTypeVar,
		AssertionProgress,
		UnsatisfiableError,
		RecursionAssertionError,
	}
	
	private final Map<ERR, Set<AssrtCoreSState>> errors = new LinkedHashMap<>();
	
	public AssrtCoreSafetyErrors(Set<AssrtCoreSState> reception, Set<AssrtCoreSState> orphan, Set<AssrtCoreSState> unfinishedRole,
			Set<AssrtCoreSState> connection, Set<AssrtCoreSState> unconnected, Set<AssrtCoreSState> synchronisation, Set<AssrtCoreSState> disconnect,
			Set<AssrtCoreSState> unknownVars, Set<AssrtCoreSState> asserts, Set<AssrtCoreSState> unsats, Set<AssrtCoreSState> recasserts
			)
			//Set<AssrtCoreSState> portOpens, Set<AssrtCoreSState> portOwners)
	{
		/*this.reception = reception;
		this.orphan = orphan;
		this.unfinishedRole = unfinishedRole;
		this.connection = connection;
		this.unconnected = unconnected;
		this.synchronisation = synchronisation;
		this.disconnect = disconnect;
		this.unknownVars = unknownVars;
		this.unsats = unsats;*/
		
		this.errors.put(ERR.Reception, reception);
		this.errors.put(ERR.Orphan, orphan);
		this.errors.put(ERR.UnfinishedRole, unfinishedRole);
		this.errors.put(ERR.Connection, connection);
		this.errors.put(ERR.Unconnected, unconnected);
		this.errors.put(ERR.Synchronisation, synchronisation);
		this.errors.put(ERR.Disconnect, disconnect);
		this.errors.put(ERR.UnknownDataTypeVar, unknownVars);
		this.errors.put(ERR.AssertionProgress, asserts);
		this.errors.put(ERR.UnsatisfiableError, unsats);
		this.errors.put(ERR.RecursionAssertionError, recasserts);
	}
	
	public boolean isSafe()
	{
		return this.errors.values().stream().allMatch(es -> es.isEmpty());
	}
	
	@Override
	public String toString()
	{
		String m = this.errors.entrySet().stream().map(e -> 
				e.getValue().isEmpty() ? ""
					:	"\n[assrt-core] " + e.getKey() + " error(s):\n  "
						+ e.getValue().stream().map(s -> getNodeLabel(s)).collect(Collectors.joining("\n  "))
				).collect(Collectors.joining(""));
		if (m.length() != 0)
		{
			m = m.substring(1, m.length());
		}
		return m;
	}
	
	private static final String getNodeLabel(AssrtCoreSState s)
	{
		String m = s.getNodeLabel();
		return m.substring("label=\"".length(), m.length() - 1);
	}
}
