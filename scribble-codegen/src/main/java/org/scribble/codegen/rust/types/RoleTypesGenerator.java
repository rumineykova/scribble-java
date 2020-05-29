package org.scribble.codegen.rust.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
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
	private Role self; 
	private EState init; 
	private List<EState> visited = new ArrayList<EState>(); 
	private final List<Role> otherRoles; 
	
	public RoleTypesGenerator(Job job, GProtocolName fullname, Role self, List<Role> otherRoles) throws ScribbleException  // FIXME: APIGenerationException?
	{
		super(job, fullname);
		this.self = self;
		JobContext jc = job.getContext();
		this.init = job.minEfsm ? jc.getMinimisedEGraph(fullname, self).init : jc.getEGraph(fullname, self).init;
		//this.initBinTypesMap(otherRoles); 
		this.otherRoles = otherRoles; 
		//constructTypes(this.init);

		//EndpointState term = EndpointState.findTerminalState(new HashSet<>(), this.init);
		/*EState term = EState.getTerminal(this.init);
		if (term != null)
		{
		    this.binTypeDecl.put("End", "EndType");
		}*/
	}
	
	private void constructTypes(EState curr, Stack<IRustMpstBuilder> acc)  {
		if (curr.isTerminal())
		{
			return; // Generic EndType for terminal states
		}
		if (this.visited.contains(curr))
		{
		
			return;
		}
		//RustMpstSessionBuilder builder = acc.g
		
		//processCurrent()
		// if curr is normla then pop it the last one, change it, and push it again
		// keep current
		this.visited.add(curr);	
		Boolean isChoice = false; 
		Boolean isOffer = false; 
		if (curr.getAllSuccessors().size()>1) {
			
			ArrayList<IRustMpstBuilder> paths = new ArrayList<>();
			// Link the choice and the simple...
			for (int j=0;j<curr.getActions().size(); j++) {
				RustMpstSessionBuilder newSimpleType = 
						new RustMpstSessionBuilder(this.otherRoles, this.self);
				EAction a = curr.getActions().get(j);
				newSimpleType.binTypes.get(a.peer).add(a);
				newSimpleType.execOrder.add(a.peer);
				paths.add(newSimpleType);
				
			}
		  BuilderKind kind; 
		  IRustMpstBuilder newType; 
		  RustMpstSessionBuilder currType = (RustMpstSessionBuilder)acc.pop();
		  EAction a = curr.getActions().get(0);
		  if (this.isOffer(curr)) {
			  isOffer = true;
			  kind = BuilderKind.Offer; 
			  newType = new OfferTypeBuilder(paths, kind, this.self, a.peer); 
			  currType.continuations.put(a.peer, newType);
		  } 
		  else { 
			isChoice = true; 
			kind = BuilderKind.Choice;
		  	newType = new ChoiceTypeBuilder(paths, kind, this.self, this.otherRoles); 
		  	currType.continuations.put(this.self, newType);
		  }	  
		  acc.push(currType);
		  acc.push(newType);
		} else {updateBinTypes(curr, acc);}
		
		int i = 0; 
		for (EState succ : curr.getAllSuccessors())
		{   // start exploring one path 
			//List<RustMpstSessionBuilder> accn = new ArrayList<>(); 
			if (isChoice) {
				ChoiceTypeBuilder bust = (ChoiceTypeBuilder)acc.get(acc.size()-1);
				acc.push(bust.paths.get(i));
				constructTypes(succ, acc);
				acc.pop();
			} else if (isOffer) {
				OfferTypeBuilder bust = (OfferTypeBuilder)acc.get(acc.size()-1);
				acc.push(bust.paths.get(i));
				constructTypes(succ, acc);
				acc.pop();
			}
			else {
				constructTypes(succ, acc);
			}
			i++; 
			// we are done exploring that path so get me the types res
			// add them to a list
			
		}
		if (isChoice||isOffer) {acc.pop();}
		// pop from the one above, if it is a choice -- choice<all the list> ... construct
		// if it is an offer, offer and construct the list...
		// if not... then we are done 
		return;
	}
	
	public Boolean isOffer(EState curr) {
		switch (curr.getStateKind())
		{ case POLY_INPUT: {return true; }
		}
		return false; 
	}
	public void updateBinTypes(EState curr, Stack<IRustMpstBuilder> currTypes) {
		
		if (!curr.isTerminal()) {
			switch (curr.getStateKind())
			{ 
				case UNARY_INPUT: 
				case OUTPUT:
				case POLY_INPUT:
				{
					RustMpstSessionBuilder currType = (RustMpstSessionBuilder)currTypes.pop();
					EAction a = curr.getActions().get(0);
					currType.binTypes.get(a.peer).add(a);
					currType.execOrder.add(a.peer);
					currTypes.push(currType);
				} 
				/*
				case OUTPUT: 
				{ 
					if (curr.getActions().size() == 1) {
						RustMpstSessionBuilder currType = (RustMpstSessionBuilder)currTypes.pop();
						EAction a = curr.getActions().get(0);
						currType.binTypes.get(a.peer).add(a);
						currType.execOrder.add(a.peer);
						currTypes.push(currType);
					}
					else 
					{ 
						ArrayList<IRustMpstBuilder> paths = new ArrayList<>();
						// Link the choice and the simple...
						for (int i=0;i<curr.getActions().size(); i++) {
							RustMpstSessionBuilder newSimpleType = 
									new RustMpstSessionBuilder(this.otherRoles, this.self);
							paths.add(newSimpleType);
						}
					  ChoiceTypeBuilder newType = new ChoiceTypeBuilder(paths, BuilderKind.Choice, this.self);
					  currTypes.push(newType);
					  return true; 
					} 
				}
				case POLY_INPUT:
				{
					ArrayList<IRustMpstBuilder> paths = new ArrayList<>();
					// Link the choice and the simple...
					for (int i=0;i<curr.getActions().size(); i++) {
						RustMpstSessionBuilder newSimpleType = 
								new RustMpstSessionBuilder(this.otherRoles, this.self);
						paths.add(newSimpleType);
					}
				  ChoiceTypeBuilder newType = new ChoiceTypeBuilder(paths, BuilderKind.Offer, this.self);
				  currTypes.push(newType);
				  return true; 
				}*/
				}
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
	
	@Override
	public Map<String, String> generateApi()  {
		Stack<IRustMpstBuilder> types = new Stack<>(); 
		RustMpstSessionBuilder type = new RustMpstSessionBuilder(this.otherRoles, this.self);
		types.add(type);
		this.constructTypes(this.init, types);
		Map<String, String> resMap = new HashMap<>();
		//String res = types.stream().map(t-> t.build()).reduce("", String::concat));
		StringBuilder sb = new StringBuilder(); 
		for (int i=0; i<types.size(); i++) {
			sb.append(types.get(i).build());
		}
		resMap.put(this.self.toString(), sb.toString());
		return resMap; 
	}
	
}
