package org.scribble.codegen.rust.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

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
	private Map<Integer, Role> activeRoles;
	private boolean createActiveRoles;
	private final List<Role> otherRoles;

//	static void reverseStack(Stack<IRustMpstBuilder> s) 
//    {
//		IRustMpstBuilder curr = s.pop();
//        
//        if (s.size() != 1)
//            reverseStack (s);
//        
//        placeCurrAtBottomOfStack (s, curr);
//    }
// 
//    static void placeCurrAtBottomOfStack(Stack<IRustMpstBuilder> s, IRustMpstBuilder curr) 
//    {
//    	IRustMpstBuilder top = s.pop();
//        if (s.size() == 0)
//            s.push(curr);
//        else
//            placeCurrAtBottomOfStack(s, curr);
//        s.push(top);
//    }

	public RoleTypesGenerator(Job job, GProtocolName fullname, Role self, List<Role> otherRoles,
			Map<Integer, Role> activeRoles, boolean createActiveRoles) throws ScribbleException // FIXME:
																								// APIGenerationException?
	{
		super(job, fullname);
		this.self = self;
		JobContext jc = job.getContext();
		this.init = job.minEfsm ? jc.getMinimisedEGraph(fullname, self).init : jc.getEGraph(fullname, self).init;
		// this.initBinTypesMap(otherRoles);
		this.otherRoles = otherRoles;
		this.activeRoles = activeRoles;
		this.createActiveRoles = createActiveRoles;
		// constructTypes(this.init);

		// EndpointState term = EndpointState.findTerminalState(new HashSet<>(),
		// this.init);
		/*
		 * EState term = EState.getTerminal(this.init); if (term != null) {
		 * this.binTypeDecl.put("End", "EndType"); }
		 */
	}

	public Map<Integer, Role> getActiveRoles() {
		return this.activeRoles;
	}

	private void constructTypes(EState curr, Stack<IRustMpstBuilder> acc, Integer indexingChoice,
			Map<EState, Integer> previousOffers, Map<String, Map<String, IRustMpstBuilder>> endpointOfBranchesInEnum,
			List<String> isInOffer, List<String> currentLabel) {

		System.out.println("Current state: " + curr + " Kind " + curr.getStateKind() + " for " + this.self + " with "
				+ curr.getAllSuccessors().size());

		if (curr.isTerminal()) {
			return; // Generic EndType for terminal states
		}
		// If loop
//		if (this.visited.contains(curr)) {
		if (Collections.frequency(this.visited, curr) == 1) {
			// TODO add a new action to receive as a payload the linked (previousOffers)
			// offer

			if (this.isOffer(curr)) {
				ArrayList<String> labels = new ArrayList<>();
				ArrayList<IRustMpstBuilder> paths = new ArrayList<>();

				BuilderKind kind;
				IRustMpstBuilder newOffer;
				RustMpstSessionBuilder currType = (RustMpstSessionBuilder) acc.pop();
				EAction a = curr.getActions().get(0);

				System.out.println("Recursion: " + a + " size " + curr.getActions());
				
				System.out.println("Recursion currType: " + currType.build().toString() + " / " + currType.execOrder);

				Integer newIndex = previousOffers.get(curr);

				kind = BuilderKind.Offer;

				if (createActiveRoles) { // If we are in the first pass to create the list of senders of choices, add a
											// "pawn"
					newOffer = new EnumOfferTypeBuilder(paths, labels, kind, this.self, a.peer, newIndex, true);
					currType.continuations.put(a.peer, newOffer);
				} else { // else, we are actually trying to make the types
					newOffer = new EnumOfferTypeBuilder(paths, labels, kind, this.self, this.activeRoles.get(newIndex),
							newIndex, true);
					currType.continuations.put(this.activeRoles.get(newIndex), newOffer);
				}
				
				System.out.println("Recursion currType modified: " + currType.build().toString() + " / " + currType.execOrder);

				acc.push(currType);
			}

			return;
		} else if (Collections.frequency(this.visited, curr) > 1) {
			return;
		}
		// RustMpstSessionBuilder builder = acc.g

		// processCurrent()
		// if curr is normal then pop the last one, change it, and push it again
		// keep current
		this.visited.add(curr);
		Boolean isChoice = false;
		Boolean isOffer = false;
		if (curr.getAllSuccessors().size() > 1) {
			ArrayList<String> labels = new ArrayList<>();
			ArrayList<IRustMpstBuilder> paths = new ArrayList<>();

			// Link the choice and the simple...
			// TODO need to be refined to include recursion
			for (int j = 0; j < curr.getActions().size(); j++) {
				RustMpstSessionBuilder newSimpleType = new RustMpstSessionBuilder(this.otherRoles, this.self);
				EAction a = curr.getActions().get(j);
				newSimpleType.binTypes.get(a.peer).add(a);
				newSimpleType.execOrder.add(a.peer);
				paths.add(newSimpleType);
				labels.add(a.mid.toString());

				System.out.println("action: " + a + " on label " + a.mid.toString());

				// Add a ("Recv<Branches%sto%s<N>, End>", this.self, this.controlRole) to the
				// path if recursion
			}

			System.out.println("paths: " + paths);
			System.out.println("labels: " + labels);

			BuilderKind kind;
			IRustMpstBuilder newType;
			RustMpstSessionBuilder currType = (RustMpstSessionBuilder) acc.pop();
			EAction a = curr.getActions().get(0);

			if (this.isOffer(curr)) {
				isOffer = true;
				kind = BuilderKind.Offer;
				if (createActiveRoles) { // If we are in the first pass to create the list of senders of choices, add a
											// "pawn"
					newType = new EnumOfferTypeBuilder(paths, labels, kind, this.self, a.peer, indexingChoice,
							createActiveRoles);
					currType.continuations.put(a.peer, newType);

					Map<String, IRustMpstBuilder> hasmapOfLabels = new HashMap<String, IRustMpstBuilder>();

					for (int i = 0; i < labels.size(); i++) {
						hasmapOfLabels.put(labels.get(i), paths.get(i));
					}

					newType.build();

					endpointOfBranchesInEnum.put(newType.getFinalTypeName(), hasmapOfLabels);

					isInOffer.add(0, newType.getFinalTypeName());

				} else { // else, we are actually trying to make the types
					newType = new EnumOfferTypeBuilder(paths, labels, kind, this.self,
							this.activeRoles.get(indexingChoice), indexingChoice, createActiveRoles);
					currType.continuations.put(this.activeRoles.get(indexingChoice), newType);
				}

				previousOffers.put(curr, indexingChoice);

			} else {
				isChoice = true;
				kind = BuilderKind.Choice;
				newType = new EnumChoiceTypeBuilder(paths, kind, this.self, this.otherRoles, indexingChoice);
				if (createActiveRoles) { // If we are in the first pass to create the list of senders of choices
					this.activeRoles.put(indexingChoice, this.self);
				}
				currType.continuations.put(this.self, newType);
			}
			indexingChoice += 1;

			System.out.println("newType: " + newType);
			System.out.println("getFinalTypeName: " + newType.getFinalTypeName());
			System.out.println("toString: " + newType.getKind());
			System.out.println("build newType: " + newType.build() + " ////////////// END");

			acc.push(currType);
			acc.push(newType);
		} else {
			updateBinTypes(curr, acc);
		}

		int i = 0;
		for (EState succ : curr.getAllSuccessors()) { // start exploring one path
														// List<RustMpstSessionBuilder> accn = new ArrayList<>();
			if (isChoice) {
				EnumChoiceTypeBuilder bust = (EnumChoiceTypeBuilder) acc.get(acc.size() - 1);
				acc.push(bust.paths.get(i));
				constructTypes(succ, acc, indexingChoice, previousOffers, endpointOfBranchesInEnum, isInOffer,
						currentLabel);
				acc.pop();
			} else if (isOffer) {
				EnumOfferTypeBuilder bust = (EnumOfferTypeBuilder) acc.get(acc.size() - 1);
				acc.push(bust.paths.get(i));
				constructTypes(succ, acc, indexingChoice, previousOffers, endpointOfBranchesInEnum, isInOffer,
						currentLabel);
				acc.pop();
			} else {
				constructTypes(succ, acc, indexingChoice, previousOffers, endpointOfBranchesInEnum, isInOffer,
						currentLabel);
			}

			i++;
			// we are done exploring that path so get me the types res
			// add them to a list

		}

		if (isChoice || isOffer) {
			acc.pop();
		}
		// pop from the one above, if it is a choice -- choice<all the list> ...
		// construct
		// if it is an offer, offer and construct the list...
		// if not... then we are done
		return;
	}

	public Boolean isOffer(EState curr) {
		switch (curr.getStateKind()) {
		case POLY_INPUT: {
			return true;
		}
		default:
			break;
		}
		return false;
	}

	public void updateBinTypes(EState curr, Stack<IRustMpstBuilder> currTypes) {

		if (!curr.isTerminal()) {
			switch (curr.getStateKind()) {
			case UNARY_INPUT:
			case OUTPUT:
			case POLY_INPUT: {
				RustMpstSessionBuilder currType = (RustMpstSessionBuilder) currTypes.pop();
				EAction a = curr.getActions().get(0);
				currType.binTypes.get(a.peer).add(a);
				currType.execOrder.add(a.peer);
				currTypes.push(currType);

				System.out.println("Buidling bin type: " + currType.build() + " ///////// END");
			}
			/*
			 * case OUTPUT: { if (curr.getActions().size() == 1) { RustMpstSessionBuilder
			 * currType = (RustMpstSessionBuilder)currTypes.pop(); EAction a =
			 * curr.getActions().get(0); currType.binTypes.get(a.peer).add(a);
			 * currType.execOrder.add(a.peer); currTypes.push(currType); } else {
			 * ArrayList<IRustMpstBuilder> paths = new ArrayList<>(); // Link the choice and
			 * the simple... for (int i=0;i<curr.getActions().size(); i++) {
			 * RustMpstSessionBuilder newSimpleType = new
			 * RustMpstSessionBuilder(this.otherRoles, this.self); paths.add(newSimpleType);
			 * } ChoiceTypeBuilder newType = new ChoiceTypeBuilder(paths,
			 * BuilderKind.Choice, this.self); currTypes.push(newType); return true; } }
			 * case POLY_INPUT: { ArrayList<IRustMpstBuilder> paths = new ArrayList<>(); //
			 * Link the choice and the simple... for (int i=0;i<curr.getActions().size();
			 * i++) { RustMpstSessionBuilder newSimpleType = new
			 * RustMpstSessionBuilder(this.otherRoles, this.self); paths.add(newSimpleType);
			 * } ChoiceTypeBuilder newType = new ChoiceTypeBuilder(paths, BuilderKind.Offer,
			 * this.self); currTypes.push(newType); return true; }
			 */
			default:
				break;
			}
		}
	}

	/*
	 * private String constructType(EState curr) { switch (curr.getStateKind()) {
	 * case OUTPUT: { if (curr.getActions().size() > 1) {
	 * System.out.println("Offer"); return "OfferType"; } else { EAction a =
	 * curr.getActions().get(0); return constructTypeDecl("Send", a); } } case
	 * UNARY_INPUT: { EAction a = curr.getActions().get(0); return
	 * constructTypeDecl("Recv", a); } case POLY_INPUT: {
	 * System.out.println("Choice"); return "ChoiceType"; } default: { throw new
	 * RuntimeException("[TODO] Rust API generation not supported for: " +
	 * curr.getStateKind() + ", " + curr.toLongString()); } } }
	 */

	@Override
	public Map<String, String> generateApi() {
		Stack<IRustMpstBuilder> types = new Stack<>();
		RustMpstSessionBuilder type = new RustMpstSessionBuilder(this.otherRoles, this.self);
		types.add(type);
		this.constructTypes(this.init, types, 0, new HashMap<>(), new HashMap<>(), new ArrayList<>(),
				new ArrayList<>());
		Map<String, String> resMap = new HashMap<>();
		// String res = types.stream().map(t-> t.build()).reduce("", String::concat));
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < types.size(); i++) {
			sb.append(types.get(i).build());
		}
		resMap.put(this.self.toString(), sb.toString());

		return resMap;
	}

}
