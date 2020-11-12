package org.scribble.codegen.rust.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.scribble.type.name.Role;

public class EnumChoiceTypeBuilder implements IRustMpstBuilder {
	private String finalTypeName; 
	private Map<Role, String> finalTypeNames = new HashMap<>(); 
	public ArrayList<IRustMpstBuilder> paths = new ArrayList<>();
	BuilderKind kind; 
	String execOrderName; 
	int counter; 
	public Map<Role, String> rolesToNames = new HashMap<>(); 
	Role self; 
	List<Role> otherRoles = new ArrayList<Role>();
	
	public EnumChoiceTypeBuilder(ArrayList<IRustMpstBuilder> paths, BuilderKind kind, Role self, List<Role> otherRoles) {
		this.paths = paths;
		this.kind = kind; 
		this.self = self; 
		this.otherRoles = otherRoles; 
		for (int i=0; i< otherRoles.size(); i++) {
			this.finalTypeNames.put(otherRoles.get(i),
				String.format("Choose%sfor%sto%s<N>", this.self, 
						otherRoles.get(i), this.self)); 
		}
	}

	public BuilderKind getKind() {
		return this.kind;
	}
	// example: 
	//type ChooseCtoB<N> = ChooseMpst<AtoBVideo<N>, CtoBClose, AtoBClose, CtoBClose, QueueBVideoDual, QueueBEnd>;
	// as many ChooseTypes as the OtherRoles
	private String buildMpstChoice() {
		StringBuilder sb = new StringBuilder(); 
		StringBuilder qb = new StringBuilder(); 
		String simpleTypes =  this.paths.stream()
				.map(t -> t.build() + "\n").reduce("", String::concat);
		sb.append(simpleTypes);
		this.execOrderName = String.format("Ordering%sChoice", this.self.toString());
	    //type QueueCChoice = RoleCtoAll<QueueCVideo, QueueCEnd>;
	    String choiceQueue = 
	    		String.format("type %s = Role%stoAll<%s, %s>;",
	    		this.execOrderName, this.self, 
	    		((RustMpstSessionBuilder)this.paths.get(0)).execOrderName, 
	    		((RustMpstSessionBuilder)this.paths.get(1)).execOrderName);
	    qb.append(choiceQueue);
	    
		//collect(Collectors.toList());
	    /*
		for (int i=0;i<this.otherRoles.size(); i++) {
			Role role = this.otherRoles.get(i);
			String name = String.format("%s%sto%s<N>", this.getKindString(), 
					this.self.toString(), role);
			this.finalTypeNames.put(role, name); 
			String decl = String.format("type %s = ChooseMpst<", name);
			StringBuilder declBuilder = new StringBuilder(); 
			declBuilder.append(decl);
	
			StringBuilder sbQ = new StringBuilder(); 
			List<String> binaryNames =  this.paths.stream()
						.map(t -> ((RustMpstSessionBuilder)t).rolesToTypeNames.get(role))
						.collect(Collectors.toList());
			for (int j=0;j<binaryNames.size(); j++) {
				declBuilder.append("End," + binaryNames.get(j) + ",");
				sbQ.append(((RustMpstSessionBuilder)this.paths.get(j)).execOrderName + ",");
			}
			sb.append("\n").append(declBuilder);
			sb.append(sbQ);
			sb.deleteCharAt(sb.length()-1);
			sb.append(">;");}
			//return sb.toString();
			return qb.toString();*/
	    return qb.toString();
	}
	
	public int getCounter() {
		return Util.getNextCounter(this.self);
	}
	
	public String getKindString() {
		if (this.kind== BuilderKind.Choice) return "Choice";
		return  "Offer";
	}
	
	@Override
	public String build() {
		return "";//this.buildMpstChoice();
	}
	
	public int getNext() {
		return this.counter++; 
	}
	@Override
	public String getFinalTypeName() {
		return this.finalTypeName;
	}
	
	public String getFinalTypeNameByRole(Role r) {
		return this.finalTypeNames.get(r);
	}
	
	public String getExecuteOrderName() {		
		return "RoleEnd";//this.execOrderName;
	}

	@Override
	public Role getSelf() {
		return this.self;
	}
}
