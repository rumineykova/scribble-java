package org.scribble.codegen.rust.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scribble.type.name.Role;

public class EnumOfferTypeBuilder implements IRustMpstBuilder {
	private String finalTypeName;
	public ArrayList<IRustMpstBuilder> paths = new ArrayList<>();
	public ArrayList<String> labels = new ArrayList<>();
	BuilderKind kind;
	int counter;
	public Map<Role, String> rolesToNames = new HashMap<>();
	Role self;
	Role controlRole;
	int indexBranche;

	public EnumOfferTypeBuilder(ArrayList<IRustMpstBuilder> paths, ArrayList<String> labels, BuilderKind kind,
			Role self, Role controlRole, int indexBranche) {
		this.paths = paths;
		this.kind = kind;
		this.self = self;
		this.labels = labels;
		this.controlRole = controlRole;
		this.indexBranche = indexBranche;
	}

	@Override
	public BuilderKind getKind() {
		return this.kind;
	}

	/*
	 * enum CBranchesAtoC<N: marker::Send> { End(SessionMpst<AtoBClose, AtoCClose,
	 * QueueAEnd>), Video(SessionMpst<AtoBVideo<N>, Recv<N, Send<N, RecursAtoC<N>>>,
	 * QueueAVideo>), } enum CBranchesBtoC<N: marker::Send> {
	 * End(SessionMpst<BtoAClose, BtoCClose, QueueBEnd>),
	 * Video(SessionMpst<BtoAVideo<N>, RecursBtoC<N>, QueueBVideo>), } type
	 * ChooseCforAtoC<N> = Send<CBranchesAtoC<N>, End>; type ChooseCforBtoC<N> =
	 * Send<CBranchesBtoC<N>, End>;
	 */
	private String buildMpstOffer() {

		StringBuilder sb = new StringBuilder();
		String name = String.format("Branches%s%sto%s", this.indexBranche, this.self, this.controlRole);
		this.finalTypeName = name + "<N>";
		String decl = String.format("enum %s<N: marker::Send> { \n", name);
		StringBuilder declBuilder = new StringBuilder();
		declBuilder.append(decl);

		for (int i = 0; i < this.paths.size(); i++) {
			RustMpstSessionBuilder simpleType = (RustMpstSessionBuilder) this.paths.get(i);
			String simpleTypeString = simpleType.build();

			sb.append(simpleTypeString + "\n");

			declBuilder.append("\t" + this.labels.get(i) + "(" + simpleType.getFinalTypeName() + "), \n");

		}
		sb.append("\n").append(declBuilder.toString());
		sb.append("}").append("\n");

		// String.format("", this.self, otherRoles.get(i), this.self);

		sb.append(String.format("type Choose%sfor%sto%s<N> = Send<%s, End>; \n", this.indexBranche, this.self,
				this.controlRole, this.finalTypeName));
		return sb.toString();
	}

	private String buildMpstChoice() {
		StringBuilder sb = new StringBuilder();
		String name = String.format("Choice%sto%s<N>", this.controlRole, this.self.toString());
		String decl = String.format("type %s = ChooseMpst<", name, this.getKindString());
		StringBuilder declBuilder = new StringBuilder();
		declBuilder.append(decl);

		StringBuilder sbQ = new StringBuilder();
		for (int i = 0; i < this.paths.size(); i++) {
			RustMpstSessionBuilder simpleType = (RustMpstSessionBuilder) this.paths.get(i);
			String simpleTypeString = simpleType.build();
			String binaryTypes = simpleType.rolesToTypeNames.values().stream()
					.map(t -> t != "End" ? "<" + t + " as Session>::Dual," : t + ",").reduce("", String::concat);
			// sb.append(simpleTypeString + "\n");
			declBuilder.append(binaryTypes);
			sbQ.append(simpleType.execOrderName + "::Dual,");
		}

		sb.append("\n").append(declBuilder.toString());
		sb.append(sbQ);
		sb.deleteCharAt(sb.length() - 1);
		sb.append(">;");
		return sb.toString();
	}

	public int getCounter() {
		return Util.getNextCounter(this.self);
	}

	public String getKindString() {
		if (this.kind == BuilderKind.Choice)
			return "Choice";
		return "Offer";
	}

	@Override
	public String build() {
		return this.buildMpstOffer();
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
