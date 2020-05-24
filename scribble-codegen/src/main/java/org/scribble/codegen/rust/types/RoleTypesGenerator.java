package org.scribble.codegen.rust.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.scribble.codegen.java.ApiGen;
import org.scribble.main.Job;
import org.scribble.main.JobContext;
import org.scribble.main.ScribbleException;
import org.scribble.model.endpoint.EState;
import org.scribble.model.endpoint.actions.EAction;
import org.scribble.type.name.GProtocolName;
import org.scribble.type.name.Role;

public class RoleTypesGenerator extends ApiGen {
	private int counter = 1;  
	private Role self; 
	private EState init; 
	private List<EState> visited = new ArrayList<EState>(); 
	
	private final Map<Role, List<EAction>> binTypes = new HashMap<>();
	private final List<Role> execOrder = new ArrayList<Role>(); 
	private final List<Role> otherRoles; 
	
	public RoleTypesGenerator(Job job, GProtocolName fullname, Role self, List<Role> otherRoles) throws ScribbleException  // FIXME: APIGenerationException?
	{
		super(job, fullname);
		this.self = self;
		JobContext jc = job.getContext();
		this.init = job.minEfsm ? jc.getMinimisedEGraph(fullname, self).init : jc.getEGraph(fullname, self).init;
		this.initBinTypesMap(otherRoles); 
		this.otherRoles = otherRoles; 
		//constructTypes(this.init);

		//EndpointState term = EndpointState.findTerminalState(new HashSet<>(), this.init);
		/*EState term = EState.getTerminal(this.init);
		if (term != null)
		{
		    this.binTypeDecl.put("End", "EndType");
		}*/
	}
	
	private void initBinTypesMap(List<Role> roles) {
		for (int i = 0; i<roles.size(); i++) {
			this.binTypes.put(roles.get(i), new ArrayList<EAction>());		
		}
	}
	
	private void constructTypes(EState curr)  {
		if (curr.isTerminal())
		{
			return;  // Generic EndType for terminal states
		}
		if (this.visited.contains(curr))
		{
			return;
		}
		
		updateBinTypes(curr);
		this.visited.add(curr);
		for (EState succ : curr.getAllSuccessors())
		{
			constructTypes(succ);
		}
	}
	
	public void updateBinTypes(EState curr) {
		if (!curr.isTerminal()) {
			EAction a = curr.getActions().get(0);
			this.binTypes.get(a.peer).add(a);
			execOrder.add(a.peer);
		}
	}
	
	/*
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
	} */
	
	//example: //type [Label][self_role]to[receiver]<[PayloadType]> = Send<[PayloadType], End>; 
	private String[] constructBinTypeDecl(List<EAction> binTypes, Role role) {		
		Map<Role, String> roleToBinType = new HashMap<>(); 
		if (binTypes.size()<1) { return new String[] {"", "End"};}
		else {
			EAction fst = binTypes.get(0);
			StringBuilder sb = new StringBuilder();
			
			String typeName = String.format("%s%sto%s<%s>", 
					fst.mid.toString(), this.self.toString().toUpperCase(), 
					role.toString().toUpperCase(), getPayload(fst));
	
			sb.append("type ").append(typeName).append(" = ");
			
			for(int i=0; i< binTypes.size(); i++) {
				EAction a = binTypes.get(i);
				sb.append(String.format("%s<%s,", getKind(a), getPayload(a)));
			}
			
			String[] brackets = new String[binTypes.size() + 2];
			Arrays.fill(brackets, ">");
			brackets[0] = "End";
			brackets[brackets.length -1] = ";";
			sb.append(Arrays.asList(brackets).stream().reduce("", String::concat));
			String[] result = new String[] {sb.toString(), typeName};
			return result;
		}  
	}
	
	//example: type QueueCRecurs = RoleCtoA<RoleCtoB<RoleEnd>>;
	private String[] constructMpstStack(List<Role> executionStack) {
		
		StringBuilder sb = new StringBuilder(); 
		String typeName = String.format("Ordering%s%d", this.self.toString(), getNextCount());
		String prefix = String.format("type %s = ", typeName);
		sb.append(prefix);
		
		for(int i=0; i< executionStack.size(); i++) {
			sb.append(String.format("Role%sto%s<,", this.self.toString(), executionStack.get(i).toString()));
		}
		
		String[] brackets = new String[executionStack.size() + 1];
		Arrays.fill(brackets, ">");
		brackets[0] = "RoleEnd";
		brackets[brackets.length -1] = ";";
		sb.append(Arrays.asList(brackets).stream().reduce("", String::concat));
		return new String[] {sb.toString(), typeName};
	}
	
	//type EndpointARecurs<N> = SessionMpst<End, RecursAtoC<N>, QueueARecurs>;
	private String constructMpstSession(Map<Role, String> rolesToBinTypes, String mpstStackName) {
		StringBuilder sb = new StringBuilder(); 
		String prefix = String.format("type Endpoint%s%d = SessionMpst<", this.self.toString(), this.getNextCount());
	    sb.append(prefix);
	    
		for (int i=0; i<this.otherRoles.size(); i++) {
			sb.append(rolesToBinTypes.get(otherRoles.get(i)))
			.append(",");
		}
		sb.append(mpstStackName);
		sb.append(">;");
		
		return sb.toString();
	}
	
	private int getNextCount() {
		return this.counter++;
	} 
	
	private String getPayload(EAction a) {
		return "N";
	}
	private String getKind(EAction a) {
		String kind; 
		if (a.isSend()) {kind = "Send";}
		else if (a.isReceive()) { kind = "Recv";}
		else throw new RuntimeException("not implemented excetpion! for node type" + a.toString());
		return kind;
	}
	
	@Override
	public Map<String, String> generateApi()  {
		this.constructTypes(this.init);
		
		Map<String, String> resMap = new HashMap<>();
		
		String binaryPairs = 
				this.binTypes.keySet().stream()
				.map(r -> constructBinTypeDecl(binTypes.get(r), r)[0] + "\n")
				.reduce("", String::concat);
		
		Map<Role, String> roleToNames = this.otherRoles.stream()
				.collect(Collectors.toMap(r -> r, r -> constructBinTypeDecl(binTypes.get(r), r)[1] ));
		
		String[] ordering = constructMpstStack(this.execOrder);
		String mpstSession = constructMpstSession(roleToNames, ordering[1]);
		resMap.put(this.self.toString(), binaryPairs + "\n" + ordering[0] + "\n" + mpstSession);
		
		return resMap;
	}
	
}
