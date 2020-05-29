package org.scribble.codegen.rust.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.scribble.model.endpoint.actions.EAction;
import org.scribble.type.name.Role;


public class RustMpstSessionBuilder implements IRustMpstBuilder {
	private String finalTypeName; 
	private BuilderKind kind;
	public Map<Role, IRustMpstBuilder> continuations = new HashMap<>(); // needed when it coninue with an offer
	public final Map<Role, List<EAction>> binTypes = new HashMap<>();
	public final List<Role> execOrder = new ArrayList<Role>(); 
	public Map<Role, String> rolesToTypeNames = new HashMap<>(); 
	public String execOrderName;
	private List<Role> otherRoles = new ArrayList<>();
	public Role self; 
	private int counter = 0; 
	
	public RustMpstSessionBuilder(List<Role> otherRoles, Role self) {
		this.otherRoles = otherRoles; 
		this.self = self; 
		this.initBinTypesMap(this.otherRoles);
		this.kind = BuilderKind.Simple; 
	}
	
	public RustMpstSessionBuilder(List<Role> otherRoles, Role self, BuilderKind kind) {
		this.otherRoles = otherRoles; 
		this.self = self; 
		this.initBinTypesMap(this.otherRoles);
		this.kind = kind; 
	}
	
	private void initBinTypesMap(List<Role> roles) {
		for (int i = 0; i<roles.size(); i++) {
			this.binTypes.put(roles.get(i), new ArrayList<EAction>());		
		}
	}
	
	
	private String[] constructBinTypeDecl(List<EAction> binTypes, Role role) {		
		Map<Role, String> roleToBinType = new HashMap<>(); 
		if (binTypes.size()<1) { 
			String finalType = "End";
			if ((this.continuations!=null) && this.continuations.containsKey(role)) {
				finalType = this.continuations.get(role).getFinalTypeName();
			} else if ((this.continuations!=null) && this.continuations.containsKey(this.self)) {
				finalType = ((ChoiceTypeBuilder)this.continuations.get(this.self)).getFinalTypeNameByRole(role);
			}
				return new String[] {"", finalType};
			}
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
			if ((this.continuations!=null) && (this.continuations.containsKey(role))) {
				brackets[0] = this.continuations.get(role).getFinalTypeName();
			} else if ((this.continuations!=null) && (this.continuations.containsKey(this.self))) {
				brackets[0] = ((ChoiceTypeBuilder)this.continuations.get(this.self)).getFinalTypeNameByRole(role);
			}
			
			else { brackets[0] = "End";}
			brackets[brackets.length -1] = ";";
			sb.append(Arrays.asList(brackets).stream().reduce("", String::concat));
			String[] result = new String[] {sb.toString(), typeName};
			return result;
		}  
	}
	
	private String[]  constructMpstContStack() {
		StringBuilder sb = new StringBuilder(); 
		String typeName = String.format("Ordering%s%dFull", 
				this.self.toString(), getNextCount());
		String prefix = String.format("type %s = ", typeName);
		sb.append(prefix);
		
		// works only for offer for now
		if (continuations!=null) {
			 Role role =  continuations.keySet().iterator().next();
			 sb.append(String.format("Role%sto%s<RoleEnd>;", this.self.toString(), role.toString()));
			}
		return new String[] {sb.toString(), typeName};
	}
	//example: type QueueCRecurs = RoleCtoA<RoleCtoB<RoleEnd>>;
	private String[] constructMpstStack(List<Role> executionStack) {
		
		StringBuilder sb = new StringBuilder(); 
		String typeName = String.format("Ordering%s%d", 
				this.self.toString(), getNextCount());
		String prefix = String.format("type %s = ", typeName);
		sb.append(prefix);
		
		for(int i=0; i< executionStack.size(); i++) {
			sb.append(String.format("Role%sto%s<", 
					this.self.toString(), executionStack.get(i).toString()));
		}
		
		String[] brackets = new String[executionStack.size() + 2];
		Arrays.fill(brackets, ">");
		
		if (this.continuations.containsKey(this.self)) {
			brackets[0] = ((ChoiceTypeBuilder)this.continuations.get(this.self)).getExecuteOrderName();
		}else { brackets[0] = "RoleEnd";}
		
		brackets[brackets.length -1] = ";";
		sb.append(Arrays.asList(brackets).stream().reduce("", String::concat));
		return new String[] {sb.toString(), typeName};
	}
	
	//type EndpointARecurs<N> = SessionMpst<End, RecursAtoC<N>, QueueARecurs>;
	private String[] constructMpstSession(Map<Role, String> rolesToBinTypes, String mpstStackName) {
		StringBuilder sb = new StringBuilder(); 
		String name = String.format("Endpoint%s%d", this.self.toString(), this.getNextCount());
		String prefix = String.format("type %s = SessionMpst<", name);
	    sb.append(prefix); 
		for (int i=0; i<this.otherRoles.size(); i++) {
			sb.append(rolesToBinTypes.get(otherRoles.get(i)))
			.append(",");
		}
		sb.append(mpstStackName);
		sb.append(">;");
		
		return new String[] {sb.toString(), name};
	}
	
	private int getNextCount() {
		return Util.getNextCounter(this.self);
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
	
	public String build() {	
		String cont = "";
		if (this.continuations.size()!=0)  {
			cont = this.continuations.values().stream().map(t -> t.build()).reduce("", String::concat);
		}
		
		String binaryPairs = 
				this.binTypes.keySet().stream()
				.map(r -> constructBinTypeDecl(binTypes.get(r), r)[0] + "\n")
				.reduce("", String::concat);
		
		Map<Role, String> roleToNames = this.otherRoles.stream()
				.collect(Collectors.toMap(r -> r, r -> constructBinTypeDecl(binTypes.get(r), r)[1] ));
		

		String[] ordering = constructMpstStack(this.execOrder);
		String[] mpstSession;
		String res; 
		if (continuations.size()==0) {
			mpstSession = constructMpstSession(roleToNames, ordering[1]);
			res = cont + binaryPairs + "\n" + ordering[0] + "\n" + mpstSession[0];
		} else {
			String[] orderingCont = constructMpstContStack();
			mpstSession = constructMpstSession(roleToNames, orderingCont[1]);
			res = cont + binaryPairs + "\n" + ordering[0] + "\n" + orderingCont[0] + "\n" + mpstSession[0];
		}
		this.rolesToTypeNames = roleToNames; 
		this.execOrderName =ordering[1];
		this.finalTypeName = mpstSession[1];
		return res; 
	}

	@Override
	public BuilderKind getKind() {
		return this.kind;
	}

	@Override
	public String getFinalTypeName() {
		return this.finalTypeName;
	}

	@Override
	public Role getSelf() {
		return this.self;
	}
	
}