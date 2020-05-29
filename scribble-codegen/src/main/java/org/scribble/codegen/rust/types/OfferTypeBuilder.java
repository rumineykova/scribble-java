package org.scribble.codegen.rust.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scribble.type.name.Role;

public class OfferTypeBuilder implements IRustMpstBuilder {
	private String finalTypeName; 
	public ArrayList<IRustMpstBuilder> paths = new ArrayList<>();
	BuilderKind kind; 
	int counter; 
	public Map<Role, String> rolesToNames = new HashMap<>(); 
	Role self; 
	Role controlRole; 
	
	public OfferTypeBuilder(ArrayList<IRustMpstBuilder> paths, 
			BuilderKind kind, Role self, Role controlRole) {
		this.paths = paths;
		this.kind = kind; 
		this.self = self; 
		this.controlRole = controlRole; 
	}
	@Override
	public BuilderKind getKind() {
		return this.kind;
	}
	// example: 
	//type ChooseCtoB<N> = ChooseMpst<AtoBVideo<N>, CtoBClose, AtoBClose, CtoBClose, QueueBVideoDual, QueueBEnd>;
	// as many ChooseTypes as the OtherRoles
	//type OfferA<N> =OfferMpst<AtoBVideo<N>, AtoCVideo<N>, AtoBClose, AtoCClose, QueueAVideo, QueueAEnd>;
	private String buildMpstOffer() {
		StringBuilder sb = new StringBuilder(); 
		String name = String.format("%s%s<N>", this.getKindString(), this.self.toString());
		this.finalTypeName = name; 
		String decl = String.format("type %s = %sMpst<", name, this.getKindString());
		StringBuilder declBuilder = new StringBuilder(); 
		declBuilder.append(decl);

		StringBuilder sbQ = new StringBuilder(); 
		for (int i=0;i<this.paths.size();i++) { 
			RustMpstSessionBuilder simpleType = 
					(RustMpstSessionBuilder) this.paths.get(i);	
			String simpleTypeString = simpleType.build();
			String binaryTypes = simpleType.rolesToTypeNames
					.values().stream().map(t -> t + ",").reduce("", String::concat);
			sb.append(simpleTypeString + "\n");
			declBuilder.append(binaryTypes);
			sbQ.append(simpleType.execOrderName + ",");
		}
		sb.append("\n").append(declBuilder.toString());
		sb.append(sbQ);
		sb.deleteCharAt(sb.length()-1);
		sb.append(">;");
		return sb.toString();
	}
	
	private String buildMpstChoice() {
		StringBuilder sb = new StringBuilder(); 
		String name = String.format("Choice%sto%s<N>", 
				 this.controlRole, this.self.toString());
		String decl = String.format("type %s = ChooseMpst<", name, this.getKindString());
		StringBuilder declBuilder = new StringBuilder(); 
		declBuilder.append(decl);

		StringBuilder sbQ = new StringBuilder(); 
		for (int i=0;i<this.paths.size();i++) { 
			RustMpstSessionBuilder simpleType = 
					(RustMpstSessionBuilder) this.paths.get(i);	
			String simpleTypeString = simpleType.build();
			String binaryTypes = simpleType.rolesToTypeNames
					.values()
					.stream()
					.map(t -> t!="End"? "<" + t + " as Session>::Dual,":t + ",")
					.reduce("", String::concat);
			sb.append(simpleTypeString + "\n");
			declBuilder.append(binaryTypes);
			sbQ.append(simpleType.execOrderName + "::Dual,");
		}
		
		sb.append("\n").append(declBuilder.toString());
		sb.append(sbQ);
		sb.deleteCharAt(sb.length()-1);
		sb.append(">;");
		return sb.toString();
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
		return this.buildMpstOffer() + 
			   this.buildMpstChoice();
	}
	
	public int getNext() {
		return this.counter++; 
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
