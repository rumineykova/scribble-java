package org.scribble.codegen.rust.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scribble.codegen.java.ApiGen;
import org.scribble.codegen.java.statechanapi.AcceptSockGen;
import org.scribble.codegen.java.statechanapi.BranchSockGen;
import org.scribble.codegen.java.statechanapi.EndSockGen;
import org.scribble.codegen.java.statechanapi.OutputSockGen;
import org.scribble.codegen.java.statechanapi.ReceiveSockGen;
import org.scribble.codegen.java.util.ClassBuilder;
import org.scribble.main.Job;
import org.scribble.main.JobContext;
import org.scribble.main.ScribbleException;
import org.scribble.model.endpoint.EState;
import org.scribble.model.endpoint.actions.EAction;
import org.scribble.type.name.GProtocolName;
import org.scribble.type.name.Role;
import org.scribble.visit.context.Projector;

public class RoleTypesGenerator extends ApiGen {
	private Role self; 
	private EState init; 
	private List<EState> visited = new ArrayList<EState>(); 
	private List<String> types = new ArrayList<String>();
	private final Map<String, String> typeNames = new HashMap<>(); 
	private final Map<String, String> binTypeDecl = new HashMap<>();
	
	public RoleTypesGenerator(Job job, GProtocolName fullname, Role self) throws ScribbleException  // FIXME: APIGenerationException?
	{
		super(job, fullname);
		this.self = self;
		JobContext jc = job.getContext();
		this.init = job.minEfsm ? jc.getMinimisedEGraph(fullname, self).init : jc.getEGraph(fullname, self).init;
		constructTypes(this.init);

		//EndpointState term = EndpointState.findTerminalState(new HashSet<>(), this.init);
		EState term = EState.getTerminal(this.init);
		if (term != null)
		{
		    this.binTypeDecl.put("End", "EndType");
		}
	}
	
	private void constructTypes(EState curr) throws ScribbleException
	{
		if (curr.isTerminal())
		{
			return;  // Generic EndType for terminal states
		}
		if (this.visited.contains(curr))
		{
			return;
		}
		String binType = constructType(curr);
		this.binTypeDecl.put(binType, binType);
		
		this.visited.add(curr);
		for (EState succ : curr.getAllSuccessors())
		{
			constructTypes(succ);
		}
	}

	private String constructType(EState curr) {
		switch (curr.getStateKind())
		{
			case OUTPUT:
			{
				if (curr.getActions().size() > 1) {
					System.out.println("Offer");
					return "OfferType";
				}
				else {
					EAction a = curr.getActions().get(0);
					return constructTypeDecl("Send", a);
				}
			}
			case UNARY_INPUT:
			{
				EAction a = curr.getActions().get(0);
				return constructTypeDecl("Recv", a);
			}
			case POLY_INPUT:
			{   
				System.out.println("Choice");
				return "ChoiceType";
			}
			default:
			{
				throw new RuntimeException("[TODO] Rust API generation not supported for: " + curr.getStateKind() + ", " + curr.toLongString());
			}
		}
	}
	
	private String constructTypeDecl(String type, EAction a) {
		String result =  type + this.self + a.peer.toString() + 
				a.mid.toString() + a.payload.toString(); 
		System.out.println(result);
		return result; 
	}
	
	@Override
	public Map<String, String> generateApi() {
		this.constructType(this.init);
		return binTypeDecl;
	}
	
}
