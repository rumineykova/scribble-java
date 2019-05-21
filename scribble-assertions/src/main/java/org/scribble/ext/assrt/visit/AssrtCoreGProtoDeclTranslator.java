package org.scribble.ext.assrt.visit;

import org.scribble.ast.global.GProtoDecl;
import org.scribble.core.type.name.DataName;
import org.scribble.ext.assrt.core.type.name.AssrtDataVar;
import org.scribble.ext.assrt.core.type.session.AssrtCoreSTypeFactory;
import org.scribble.ext.assrt.core.type.session.AssrtCoreSyntaxException;
import org.scribble.ext.assrt.core.type.session.global.AssrtCoreGType;
import org.scribble.job.Job;

// Cf. GTypeTranslator
@Deprecated
public class AssrtCoreGProtoDeclTranslator
{
	public static final DataName UNIT_DATATYPE = new DataName("_Unit");  // TODO
	
	private final Job job;
	private final AssrtCoreSTypeFactory af;
	
	private static int varCounter = 1;
	private static int recCounter = 1;
	
	private static AssrtDataVar makeFreshDataTypeVar()
	{
		return new AssrtDataVar("_dum" + varCounter++);
	}

	private static String makeFreshRecVarName()
	{
		return "_X" + recCounter++;
	}
	
	//private static DataType UNIT_TYPE;

	public AssrtCoreGProtoDeclTranslator(Job job, AssrtCoreSTypeFactory af)
	{
		this.job = job;
		this.af = af;
		
//		if (F17GProtoDeclTranslator.UNIT_TYPE == null)
//		{
//			F17GProtoDeclTranslator.UNIT_TYPE = new DataType("_UNIT");
//		}
	}

	public AssrtCoreGType translate(GProtoDecl gpd)
			throws AssrtCoreSyntaxException
	{
		throw new RuntimeException("[TODO] :\n" + gpd);
		/*GProtoDef inlined = ((GProtoDefDel) gpd.def.del()).getInlinedProtoDef();
		return parseSeq(
				inlined.getBlockChild().getInteractSeqChild().getInteractionChildren(),
				new HashMap<>(), false, false);*/
	}

	/*
	// List<GInteractionNode> because subList is useful for parsing the continuation
	private AssrtCoreGType parseSeq(List<GSessionNode> is, Map<RecVar, RecVar> rvs,
			boolean checkChoiceGuard, boolean checkRecGuard) throws AssrtCoreSyntaxException
	{
		if (is.isEmpty())
		{
			return this.af.AssrtCoreGEnd();
		}

		GSessionNode first = is.get(0);
		if (first instanceof GSimpleSessionNode && !(first instanceof GContinue))
		{
			if (first instanceof AssrtGMsgTransfer)
			{
				return parseAssrtGMsgTransfer(is, rvs, (AssrtGMsgTransfer) first);
			}
			else if (first instanceof AssrtGConnect)
			{
				return parseAssrtGConnect(is, rvs, (AssrtGConnect) first);
			}
//			else if (first instanceof GDisconnect)
//			{
//				F17GDisconnect gdc = parseGDisconnect((GDisconnect) first);
//				AssrtCoreGType cont = parseSeq(jc, mc, is.subList(1, is.size()), false, false);
//				Map<AssrtCoreGAction, AssrtCoreGType> cases = new HashMap<>();
//				cases.put(gdc, cont);
//				return this.factory.GChoice(cases);
//			}
			else
			{
				throw new RuntimeException("[assrt-core] Shouldn't get in here: " + first);
			}
		}
		else
		{
			if (checkChoiceGuard)  // No "flattening" of nested choices not allowed?
			{
				throw new AssrtCoreSyntaxException(first.getSource(), "[assrt-core] Unguarded in choice case: " + first);
			}
			if (is.size() > 1)
			{
				throw new AssrtCoreSyntaxException(is.get(1).getSource(), "[assrt-core] Bad sequential composition after: " + first);
			}

			if (first instanceof GChoice)
			{
				return parseGChoice(rvs, checkRecGuard, (GChoice) first);
			}
			else if (first instanceof AssrtGRecursion)
			{
				return parseAssrtGRecursion(rvs, checkChoiceGuard, (AssrtGRecursion) first);
			}
			else if (first instanceof GContinue)
			{
				return parseAssrtGContinue(rvs, checkRecGuard, (AssrtGContinue) first);
			}
			else
			{
				throw new RuntimeException("[assrt-core] Shouldn't get in here: " + first);
			}
		}
	}

	private AssrtCoreGType parseGChoice(Map<RecVar, RecVar> rvs,
			boolean checkRecGuard, GChoice gc) throws AssrtCoreSyntaxException
	{
		List<AssrtCoreGType> children = new LinkedList<>();
		for (GProtoBlock b : gc.getBlockChildren())
		{
			children.add(parseSeq(b.getInteractSeqChild().getInteractionChildren(),
					rvs, true, checkRecGuard));  // Check cases are guarded
		}

		Role src = null;
		Role dest = null;
		AssrtCoreActionKind<Global> kind = null;  // FIXME: generic parameter
		Map<AssrtCoreMsg, AssrtCoreGType> cases = new HashMap<>();
		for (AssrtCoreGType c : children)
		{
			// Because all cases should be action guards (unary choices)
			if (!(c instanceof AssrtCoreGChoice))
			{
				throw new RuntimeException("[assrt-core] Shouldn't get in here: " + c);
			}
			AssrtCoreGChoice tmp = (AssrtCoreGChoice) c;
			if (tmp.cases.size() > 1)
			{
				throw new RuntimeException("[assrt-core] Shouldn't get in here: " + c);
			}
			
			if (kind == null)
			{
				kind = tmp.kind;
				src = tmp.src;
				dest = tmp.dest;
			}
			else if (!kind.equals(tmp.kind))
			{
				throw new AssrtCoreSyntaxException(gc.getSource(), "[assrt-core] Mixed-action choices not supported: " + gc);
			}
			else if (!src.equals(tmp.src) || !dest.equals(tmp.dest))
			{
				throw new AssrtCoreSyntaxException(gc.getSource(), "[assrt-core] Non-directed choice not supported: " + gc);
			}
			
			// "Flatten" nested choices (already checked they are guarded) -- Scribble choice subjects ignored
			for (Entry<AssrtCoreMsg, AssrtCoreGType> e : tmp.cases.entrySet())
			{
				AssrtCoreMsg k = e.getKey();
				if (cases.keySet().stream().anyMatch(x -> x.op.equals(k.op)))
				{
					throw new AssrtCoreSyntaxException(gc.getSource(), "[assrt-core] Non-deterministic actions not supported: " + k.op);
				}
				cases.put(k, e.getValue());
			}
		}
		
		return this.af.AssrtCoreGChoice(src, (AssrtCoreGActionKind) kind, dest, cases);
	}

	private AssrtCoreGType parseAssrtGRecursion(Map<RecVar, RecVar> rvs,
			boolean checkChoiceGuard, AssrtGRecursion gr) throws AssrtCoreSyntaxException
	{
		RecVar recvar = gr.getRecVarChild().toName();
		if (recvar.toString().contains("__"))  // HACK: "inlined proto rec var"
		{
			RecVarNode rvn = (RecVarNode) ((AssrtAstFactory) this.job.af).SimpleNameNode(null, RecVarKind.KIND, makeFreshRecVarName());
			rvs.put(recvar, rvn.toName());
			recvar = rvn.toName();
		}
		AssrtCoreGType body = parseSeq(gr.getBlockChild().getInteractSeqChild().getInteractionChildren(), rvs, checkChoiceGuard, true);  // Check rec body is guarded

		//return this.af.AssrtCoreGRec(recvar, annot, init, body);
		//return this.af.AssrtCoreGRec(recvar, Stream.of(annot).collect(Collectors.toMap(a -> a, a -> init)), body);
		Iterator<AssrtArithExpr> exprs = gr.annotexprs.iterator();
//		Map<AssrtDataTypeVar, AssrtArithFormula> vars
//				= gr.annotvars.stream().collect(Collectors.toMap(v -> v.getFormula().toName(), v -> exprs.next().getFormula()));
		LinkedHashMap<AssrtDataTypeVar, AssrtArithFormula> vars = new LinkedHashMap<>();
		for (AssrtIntVarNameNode vv : gr.annotvars)
		{
			vars.put(vv.getFormula().toName(), exprs.next().getFormula());
		}
		AssrtBoolFormula ass = (gr.ass == null) ? AssrtTrueFormula.TRUE : gr.ass.getFormula();
		return this.af.AssrtCoreGRec(recvar, vars, body, ass);
	}

	private AssrtCoreGType parseAssrtGContinue(Map<RecVar, RecVar> rvs, boolean checkRecGuard, AssrtGContinue gc)
			throws AssrtCoreSyntaxException
	{
		if (checkRecGuard)
		{
			throw new AssrtCoreSyntaxException(gc.getSource(), "[assrt-core] Unguarded in recursion: " + gc);  // FIXME: too conservative, e.g., rec X . A -> B . rec Y . X
		}
		RecVar recvar = gc.recvar.toName();
		if (rvs.containsKey(recvar))
		{
			recvar = rvs.get(recvar);
		}
		List<AssrtArithFormula> exprs = gc.annotexprs.stream().map(e -> e.getFormula()).collect(Collectors.toList());
		return this.af.AssrtCoreGRecVar(recvar, exprs);
	}

	// Parses message interactions as unary choices
	private AssrtCoreGChoice parseAssrtGMsgTransfer(List<GSessionNode> is, Map<RecVar, RecVar> rvs, AssrtGMsgTransfer gmt) throws AssrtCoreSyntaxException 
	{
		Op op = parseOp(gmt);
		//AssrtAnnotDataType pay = parsePayload(gmt);
		List<AssrtAnnotDataType> pays = parsePayload(gmt);
		AssrtAssertion ass = parseAssertion(gmt);
		AssrtCoreMsg a = this.af.AssrtCoreAction(op, pays, ass.getFormula());
		Role src = parseSourceRole(gmt);
		Role dest = parseDestinationRole(gmt);
		AssrtCoreGActionKind kind = AssrtCoreGActionKind.MESSAGE;
		return parseGSimpleInteractionNode(is, rvs, src, a, dest, kind);
	}

	// Duplicated from parseGMsgTransfer (MsgTransfer and ConnectionAction have no common base
	
	private AssrtCoreGChoice parseAssrtGConnect(List<GInteractionNode> is, Map<RecVar, RecVar> rvs, AssrtGConnect gc) throws AssrtCoreSyntaxException 
	{
		Op op = parseOp(gc);
		//AssrtAnnotDataType pay = parsePayload(gc);
		List<AssrtAnnotDataType> pays = parsePayload(gc);
		AssrtAssertion ass = parseAssertion(gc);
		AssrtCoreMsg a = this.af.AssrtCoreAction(op, pays, ass.getFormula());
		Role src = parseSourceRole(gc);
		Role dest = parseDestinationRole(gc);
		AssrtCoreGActionKind kind = AssrtCoreGActionKind.CONNECT;
		return parseGSimpleInteractionNode(is, rvs, src, a, dest, kind);
	}

	private AssrtCoreGChoice parseGSimpleInteractionNode(
			List<GSessionNode> is, Map<RecVar, RecVar> rvs, 
			Role src, AssrtCoreMsg a, Role dest, AssrtCoreGActionKind kind) throws AssrtCoreSyntaxException 
	{
		if (src.equals(dest))
		{
			throw new RuntimeException("[assrt-core] Shouldn't get in here (self-communication): " + src + ", " + dest);
		}
		
		AssrtCoreGType cont = parseSeq(is.subList(1, is.size()), rvs, false, false);  // Subseqeuent choice/rec is guarded by (at least) this action
		return this.af.AssrtCoreGChoice(src, kind, dest, Stream.of(a).collect(Collectors.toMap(x -> x, x -> cont)));
	}

	private Op parseOp(AssrtGMsgTransfer gmt) throws AssrtCoreSyntaxException
	{
		return parseOp(gmt.msg);
	}

	private Op parseOp(AssrtGConnect gc) throws AssrtCoreSyntaxException
	{
		return parseOp(gc.msg);
	}

	private Op parseOp(MsgNode mn) throws AssrtCoreSyntaxException
	{
		if (!mn.isSigNameNode())
		{
			throw new AssrtCoreSyntaxException(mn.getSource(), " [assrt-core] Msg sig names not supported: " + mn);  // TODO: SigName
		}
		SigLitNode msn = ((SigLitNode) mn);
		return msn.getOpChild().toName();
	}

	//private AssrtAnnotDataType parsePayload(AssrtGMsgTransfer gmt) throws AssrtCoreSyntaxException
	private List<AssrtAnnotDataType> parsePayload(AssrtGMsgTransfer gmt) throws AssrtCoreSyntaxException
	{
		return parsePayload(gmt.msg);
	}

	//private AssrtAnnotDataType parsePayload(AssrtGConnect gc) throws AssrtCoreSyntaxException
	private List<AssrtAnnotDataType> parsePayload(AssrtGConnect gc) throws AssrtCoreSyntaxException
	{
		return parsePayload(gc.getMessageNodeChild());
	}

	//private AssrtAnnotDataType parsePayload(MsgNode mn) throws AssrtCoreSyntaxException
	private List<AssrtAnnotDataType> parsePayload(MsgNode mn) throws AssrtCoreSyntaxException
	{
		if (!mn.isSigNameNode())
		{
			throw new AssrtCoreSyntaxException(mn.getSource(), " [assrt-core] Msg sig names not supported: " + mn);  // TODO: SigName
		}
		SigLitNode msn = ((SigLitNode) mn);
		if (msn.getPayloadListChild().getElementChildren().isEmpty())
		{
//			DataTypeNode dtn = (DataTypeNode) ((AssrtAstFactory) this.job.af).QualifiedNameNode(null, DataTypeKind.KIND, "_Unit");
//			AssrtVarNameNode nn = (AssrtVarNameNode) ((AssrtAstFactory) this.job.af).SimpleNameNode(null, AssrtVarNameKind.KIND, "_x" + nextVarIndex());
//			return ((AssrtAstFactory) this.job.af).AssrtAnnotPayloadElem(null, nn, dtn);  // null source OK?

			return Stream.of(new AssrtAnnotDataType(makeFreshDataTypeVar(), AssrtCoreGProtoDeclTranslator.UNIT_DATATYPE))
					.collect(Collectors.toList());  // FIXME: make empty list
		}
//		else if (msn.payloads.getElements().size() > 1)
//		{
//			throw new AssrtCoreSyntaxException(msn.getSource(), "[assrt-core] Payload with more than one element not supported: " + mn);
//		}

		//PayloadElem<?> pe = msn.payloads.getElements().get(0);
		List<AssrtAnnotDataType> res = new LinkedList<>();
		for (PayElem<?> pe : msn.getPayloadListChild().getElementChildren())
		{
			if (!(pe instanceof AssrtAnnotDataElem) && !(pe instanceof UnaryPayElem<?>))
			{
				// Already ruled out by parsing?  e.g., GDelegationElem
				throw new AssrtCoreSyntaxException("[assrt-core] Payload element not supported: " + pe);
			}
//			else if (pe instanceof LDelegationElem)  // No: only created by projection, won't be parsed
//			{
//				throw new AssrtCoreSyntaxException("[assrt-core] Payload element not supported: " + pe);
//			}
			
			if (pe instanceof AssrtAnnotDataElem)
			{
				AssrtAnnotDataType adt = ((AssrtAnnotDataElem) pe).toPayloadType();
				String type = adt.data.toString();
				if (!type.equals("int") && !type.endsWith(".int"))  // HACK FIXME (currently "int" for F#)
				{
					throw new AssrtCoreSyntaxException(pe.getSource(), "[assrt-core] Payload annotations not supported for non- \"int\" type kind: " + pe);
				}
				
				// FIXME: non-annotated payload elems get created with fresh vars, i.e., non- int types
				// Also empty payloads are created as Unit type
				// But current model building implicitly treats all vars as int -- this works, but is not clean
				
				//return adt;
				res.add(adt);
			}
			else
			{
				PayElemType<?> pet = ((UnaryPayElem<?>) pe).toPayloadType();
				if (!pet.isDataName())
				{
				// i.e., AssrtDataTypeVar not allowed (should be "encoded")
					throw new AssrtCoreSyntaxException(pe.getSource(), "[assrt-core] Non- datatype kind payload not supported: " + pe);
				}
				//return new AssrtAnnotDataType(makeFreshDataTypeVar(), (DataType) pet);
				res.add(new AssrtAnnotDataType(makeFreshDataTypeVar(), (DataName) pet));
			}
		}
		return res;
	}

	private AssrtAssertion parseAssertion(AssrtGMsgTransfer gmt)
	{
		return parseAssertion(gmt.ass);
	}

	private AssrtAssertion parseAssertion(AssrtGConnect gc)
	{
		return parseAssertion(gc.ass);
	}

	// Empty assertions generated as True
	private AssrtAssertion parseAssertion(AssrtAssertion ass)
	{
		return (ass == null) ? ((AssrtAstFactory) this.job.af).AssrtAssertion(null, AssrtTrueFormula.TRUE) : ass;
			// FIXME: singleton constant
	}

	private Role parseSourceRole(AssrtGMsgTransfer gmt)
	{
		return parseSourceRole(gmt.src);
	}

	private Role parseSourceRole(GConnect gmt)
	{
		return parseSourceRole(gmt.getSourceChild());
	}

	private Role parseSourceRole(RoleNode rn)
	{
		return rn.toName();
	}
	
	private Role parseDestinationRole(AssrtGMsgTransfer gmt) throws AssrtCoreSyntaxException
	{
		if (gmt.getDestinations().size() > 1)
		{
			throw new AssrtCoreSyntaxException(gmt.getSource(), " [TODO] Multicast not supported: " + gmt);
		}
		return parseDestinationRole(gmt.getDestinations().get(0));
	}

	private Role parseDestinationRole(AssrtGConnect gc) throws AssrtCoreSyntaxException
	{
		return parseDestinationRole(gc.getDestinationChild());
	}

	private Role parseDestinationRole(RoleNode rn) throws AssrtCoreSyntaxException
	{
		return rn.toName();
	}
	*/
}
