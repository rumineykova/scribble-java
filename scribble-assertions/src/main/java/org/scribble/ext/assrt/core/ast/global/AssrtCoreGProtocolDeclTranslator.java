package org.scribble.ext.assrt.core.ast.global;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.scribble.ast.MessageSigNode;
import org.scribble.ast.PayloadElem;
import org.scribble.ast.UnaryPayloadElem;
import org.scribble.ast.global.GChoice;
import org.scribble.ast.global.GContinue;
import org.scribble.ast.global.GDelegationElem;
import org.scribble.ast.global.GInteractionNode;
import org.scribble.ast.global.GMessageTransfer;
import org.scribble.ast.global.GProtocolBlock;
import org.scribble.ast.global.GProtocolDecl;
import org.scribble.ast.global.GProtocolDef;
import org.scribble.ast.global.GRecursion;
import org.scribble.ast.global.GSimpleInteractionNode;
import org.scribble.ast.name.qualified.DataTypeNode;
import org.scribble.ast.name.simple.RecVarNode;
import org.scribble.del.global.GProtocolDefDel;
import org.scribble.ext.assrt.ast.AssrtAnnotDataTypeElem;
import org.scribble.ext.assrt.ast.AssrtAssertion;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.ext.assrt.ast.global.AssrtGMessageTransfer;
import org.scribble.ext.assrt.core.ast.AssrtCoreAction;
import org.scribble.ext.assrt.core.ast.AssrtCoreActionKind;
import org.scribble.ext.assrt.core.ast.AssrtCoreAstFactory;
import org.scribble.ext.assrt.core.ast.AssrtCoreSyntaxException;
import org.scribble.ext.assrt.parser.assertions.formula.AssrtFormulaFactory;
import org.scribble.ext.assrt.sesstype.name.AssrtAnnotDataType;
import org.scribble.ext.assrt.sesstype.name.AssrtDataTypeVar;
import org.scribble.main.Job;
import org.scribble.sesstype.kind.Global;
import org.scribble.sesstype.kind.RecVarKind;
import org.scribble.sesstype.name.DataType;
import org.scribble.sesstype.name.Op;
import org.scribble.sesstype.name.PayloadElemType;
import org.scribble.sesstype.name.RecVar;
import org.scribble.sesstype.name.Role;


public class AssrtCoreGProtocolDeclTranslator
{
	public static final DataType UNIT_DATATYPE = new DataType("_Unit");  // FIXME: move
	
	private final Job job;
	private final AssrtCoreAstFactory af;
	
	private static int varCounter = 1;
	private static int recCounter = 1;
	
	private static int nextVarIndex()
	{
		return varCounter++;
	}

	private static int nextRecIndex()
	{
		return recCounter++;
	}
	
	//private static DataType UNIT_TYPE;

	public AssrtCoreGProtocolDeclTranslator(Job job, AssrtCoreAstFactory af)
	{
		this.job = job;
		this.af = af;
		
		/*if (F17GProtocolDeclTranslator.UNIT_TYPE == null)
		{
			F17GProtocolDeclTranslator.UNIT_TYPE = new DataType("_UNIT");
		}*/
	}

	public AssrtCoreGType translate(GProtocolDecl gpd) throws AssrtCoreSyntaxException
	{
		GProtocolDef inlined = ((GProtocolDefDel) gpd.def.del()).getInlinedProtocolDef();
		return parseSeq(inlined.getBlock().getInteractionSeq().getInteractions(), new HashMap<>(), false, false);
	}

	// List<GInteractionNode> because subList is useful for parsing the continuation
	private AssrtCoreGType parseSeq(List<GInteractionNode> is, Map<RecVar, RecVar> rvs,
			boolean checkChoiceGuard, boolean checkRecGuard) throws AssrtCoreSyntaxException
	{
		if (is.isEmpty())
		{
			return this.af.AssrtCoreGEnd();
		}

		GInteractionNode first = is.get(0);
		if (first instanceof GSimpleInteractionNode && !(first instanceof GContinue))
		{
			if (first instanceof GMessageTransfer)
			{
				return parseGMessageTransfer(is, rvs, (GMessageTransfer) first);
			}
			/*else if (first instanceof GConnect)
			{
				AssrtCoreGConnect gc = parseGConnect((GConnect) first);
				AssrtCoreGType cont = parseSeq(jc, mc, is.subList(1, is.size()), false, false);
				Map<AssrtCoreGAction, AssrtCoreGType> cases = new HashMap<>();
				cases.put(gc, cont);
				return this.factory.GChoice(cases);
			}*/
			/*else if (first instanceof GDisconnect)
			{
				F17GDisconnect gdc = parseGDisconnect((GDisconnect) first);
				AssrtCoreGType cont = parseSeq(jc, mc, is.subList(1, is.size()), false, false);
				Map<AssrtCoreGAction, AssrtCoreGType> cases = new HashMap<>();
				cases.put(gdc, cont);
				return this.factory.GChoice(cases);
			}*/
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
				return parseGChoice(rvs, checkRecGuard, first);
			}
			else if (first instanceof GRecursion)
			{
				return parseGRecursion(rvs, checkChoiceGuard, first);
			}
			else if (first instanceof GContinue)
			{
				return parseGContinue(rvs, checkRecGuard, first);
			}
			else
			{
				throw new RuntimeException("[assrt-core] Shouldn't get in here: " + first);
			}
		}
	}

	private AssrtCoreGType parseGChoice(Map<RecVar, RecVar> rvs,
			boolean checkRecGuard, GInteractionNode first) throws AssrtCoreSyntaxException
	{
		GChoice gc = (GChoice) first;
		
		List<AssrtCoreGType> children = new LinkedList<>();
		for (GProtocolBlock b : gc.getBlocks())
		{
			children.add(parseSeq(b.getInteractionSeq().getInteractions(), rvs, true, checkRecGuard));  // Check cases are guarded
		}

		Role src = null;
		Role dest = null;
		AssrtCoreActionKind<Global> kind = null;  // FIXME: generic parameter
		Map<AssrtCoreAction, AssrtCoreGType> cases = new HashMap<>();
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
			for (Entry<AssrtCoreAction, AssrtCoreGType> e : tmp.cases.entrySet())
			{
				AssrtCoreAction k = e.getKey();
				if (cases.keySet().stream().anyMatch(x -> x.op.equals(k.op)))
				{
					throw new AssrtCoreSyntaxException(gc.getSource(), "[assrt-core] Non-deterministic actions not supported: " + k.op);
				}
				cases.put(k, e.getValue());
			}
		}
		
		return this.af.AssrtCoreGChoice(src, (AssrtCoreGActionKind) kind, dest, cases);
	}

	// Parses message interactions as unary choices
	private AssrtCoreGChoice parseGMessageTransfer(List<GInteractionNode> is, Map<RecVar, RecVar> rvs, GMessageTransfer gmt) throws AssrtCoreSyntaxException 
	{
		Op op = parseOp(gmt);
		//AssrtAnnotDataTypeElem<DataTypeKind> pay = parsePayload(gmt);
		AssrtAnnotDataType pay = parsePayload(gmt);
		AssrtAssertion ass = parseAssertion(gmt);
		
		AssrtCoreAction a = this.af.action(op, pay, ass);
		
		AssrtCoreGType cont = parseSeq(is.subList(1, is.size()), rvs, false, false);  // Subseqeuent choice/rec is guarded by (at least) this action

		Role src = parseSourceRole(gmt);
		Role dest = parseDestinationRole(gmt);
		if (src.equals(dest))
		{
			throw new RuntimeException("[assrt-core] Shouldn't get in here (self-communication): " + gmt);
		}
		
		AssrtCoreGActionKind kind = AssrtCoreGActionKind.MESSAGE;
		
		Map<AssrtCoreAction, AssrtCoreGType> cases = new HashMap<>();
		cases.put(a, cont);
		return this.af.AssrtCoreGChoice(src, kind, dest, cases);
	}

	private Op parseOp(GMessageTransfer gmt) throws AssrtCoreSyntaxException
	{
		if (!gmt.msg.isMessageSigNode())
		{
			throw new AssrtCoreSyntaxException(gmt.msg.getSource(), " [assrt-core] Message sig names not supported: " + gmt.msg);  // TODO: MessageSigName
		}
		MessageSigNode msn = ((MessageSigNode) gmt.msg);
		return msn.op.toName();
	}

	private AssrtAnnotDataType parsePayload(GMessageTransfer gmt) throws AssrtCoreSyntaxException
	//private AssrtAnnotDataTypeElem<DataTypeKind> parsePayload(GMessageTransfer gmt) throws AssrtException
	{
		if (!gmt.msg.isMessageSigNode())
		{
			throw new AssrtCoreSyntaxException(gmt.msg.getSource(), " [assrt-core] Message sign names not supported: " + gmt.msg);  // TODO: MessageSigName
		}
		MessageSigNode msn = ((MessageSigNode) gmt.msg);
		if (msn.payloads.getElements().isEmpty())
		{
			/*DataTypeNode dtn = (DataTypeNode) ((AssrtAstFactory) this.job.af).QualifiedNameNode(null, DataTypeKind.KIND, "_Unit");
			AssrtVarNameNode nn = (AssrtVarNameNode) ((AssrtAstFactory) this.job.af).SimpleNameNode(null, AssrtVarNameKind.KIND, "_x" + nextVarIndex());
			return ((AssrtAstFactory) this.job.af).AssrtAnnotPayloadElem(null, nn, dtn);  // null source OK?*/
			return new AssrtAnnotDataType(new AssrtDataTypeVar("_x" + nextVarIndex()), AssrtCoreGProtocolDeclTranslator.UNIT_DATATYPE);
		}
		else if (msn.payloads.getElements().size() > 1)
		{
			throw new AssrtCoreSyntaxException(msn.getSource(), "[assrt-core] Payload with more than one element not supported: " + gmt.msg);
		}
		else
		{
			PayloadElem<?> pe = msn.payloads.getElements().get(0);
			if (pe instanceof GDelegationElem)  // Already ruled out by parsing?
			{
				throw new AssrtCoreSyntaxException("[assrt-core] Delegation not supported: " + pe);
			}
			else if (pe instanceof AssrtAnnotDataTypeElem)
			{
				return ((AssrtAnnotDataTypeElem) pe).toPayloadType();
			}
			else
			{
				UnaryPayloadElem<?> upe = (UnaryPayloadElem<?>) pe;
				PayloadElemType<?> type = upe.toPayloadType();
				if (!type.isDataType())
				{
					throw new AssrtCoreSyntaxException(upe.getSource(), "[assrt-core] Non- data type kind payload not supported: " + upe);
				}
				/*AssrtVarNameNode nn = (AssrtVarNameNode) ((AssrtAstFactory) this.job.af).SimpleNameNode(null, AssrtVarNameKind.KIND, "_x" + nextVarIndex());
				return ((AssrtAstFactory) this.job.af).AssrtAnnotPayloadElem(null, nn, (DataTypeNode) upe.name);  // null source OK?*/
				return new AssrtAnnotDataType(new AssrtDataTypeVar("_x" + nextVarIndex()), ((DataTypeNode) upe.name).toName());
			}
		}
	}

	// Empty assertions generated as True
	private AssrtAssertion parseAssertion(GMessageTransfer gmt)
	{
		AssrtAssertion ass = ((AssrtGMessageTransfer) gmt).ass;
		return (ass == null) ? ((AssrtAstFactory) this.job.af).AssrtAssertion(null, AssrtFormulaFactory.AssrtTrueFormula()) : ass;
			// FIXME: singleton constant
	}

	private Role parseSourceRole(GMessageTransfer gmt)
	{
		return gmt.src.toName();
	}
	
	private Role parseDestinationRole(GMessageTransfer gmt) throws AssrtCoreSyntaxException
	{
		if (gmt.getDestinations().size() > 1)
		{
			throw new AssrtCoreSyntaxException(gmt.getSource(), " [TODO] Multicast not supported: " + gmt);
		}
		return gmt.getDestinations().get(0).toName();
	}

	private AssrtCoreGType parseGRecursion(Map<RecVar, RecVar> rvs,
			boolean checkChoiceGuard, GInteractionNode first) throws AssrtCoreSyntaxException
	{
		GRecursion gr = (GRecursion) first;
		RecVar recvar = gr.recvar.toName();
		if (recvar.toString().contains("__"))
		{
			RecVarNode rvn = (RecVarNode) ((AssrtAstFactory) this.job.af).SimpleNameNode(null, RecVarKind.KIND, "X" + nextRecIndex());
			rvs.put(recvar, rvn.toName());
			recvar = rvn.toName();
		}
		AssrtCoreGType body = parseSeq(gr.getBlock().getInteractionSeq().getInteractions(), rvs, checkChoiceGuard, true);  // Check rec body is guarded
		return new AssrtCoreGRec(recvar, body);
	}

	private AssrtCoreGType parseGContinue(Map<RecVar, RecVar> rvs, boolean checkRecGuard, GInteractionNode first)
			throws AssrtCoreSyntaxException
	{
		if (checkRecGuard)
		{
			throw new AssrtCoreSyntaxException(first.getSource(), "[assrt-core] Unguarded in recursion: " + first);  // FIXME: too conservative, e.g., rec X . A -> B . rec Y . X
		}
		GContinue gc = (GContinue) first;
		RecVar recvar = gc.recvar.toName();
		if (rvs.containsKey(recvar))
		{
			recvar = rvs.get(recvar);
		}
		return this.af.AssrtCoreGRecVar(recvar);
	}

	
	
	
	
	
	
	
	
	
	/*
	// Mostly duplicated from parseGMessageTransfer, but GMessageTransfer/GConnect have no useful base class 
	private AssrtCoreGConnect parseGConnect(GConnect gc) throws F17Exception 
	{
		Role src = gc.src.toName();
		Role dest = gc.dest.toName();
		if (!gc.msg.isMessageSigNode())
		{
			throw new F17SyntaxException(gc.msg.getSource(), " [f17] Message kind not supported: " + gc.msg);
		}
		MessageSigNode msn = ((MessageSigNode) gc.msg);
		Op op = msn.op.toName();
		Payload pay = null;
		if (msn.payloads.getElements().isEmpty())
		{
			pay = Payload.EMPTY_PAYLOAD;
		}
		else
		{
			String tmp = msn.payloads.getElements().get(0).toString().trim();
			int i = tmp.indexOf('@');
			if (i != -1)
			{
				throw new F17Exception("[f17] Delegation not supported: " + tmp);
			}
			else
			{
				pay = msn.payloads.toPayload();
			}
		}
		return this.factory.GConnect(src, dest, op, pay);
	}
	*/
	
	/*private AssrtCoreGType parseSeq(JobContext jc, ModuleContext mc, List<GInteractionNode> is,
			boolean checkChoiceGuard, boolean checkRecGuard) throws F17Exception
	{
		//List<GInteractionNode> is = block.getInteractionSeq().getInteractions();
		if (is.isEmpty())
		{
			return this.factory.GEnd();
		}

		GInteractionNode first = is.get(0);
		if (first instanceof GSimpleInteractionNode && !(first instanceof GContinue))
		{
			if (first instanceof GMessageTransfer)
			{
				AssrtCoreGMessageTransfer gmt = parseGMessageTransfer((GMessageTransfer) first);
				AssrtCoreGType cont = parseSeq(jc, mc, is.subList(1, is.size()), false, false);
				Map<AssrtCoreGAction, AssrtCoreGType> cases = new HashMap<>();
					cases.put(gmt, cont);
				return this.factory.GChoice(cases);
			}
			else if (first instanceof GConnect)
			{
				AssrtCoreGConnect gc = parseGConnect((GConnect) first);
				AssrtCoreGType cont = parseSeq(jc, mc, is.subList(1, is.size()), false, false);
				Map<AssrtCoreGAction, AssrtCoreGType> cases = new HashMap<>();
				cases.put(gc, cont);
				return this.factory.GChoice(cases);
			}
			else if (first instanceof GDisconnect)
			{
				F17GDisconnect gdc = parseGDisconnect((GDisconnect) first);
				AssrtCoreGType cont = parseSeq(jc, mc, is.subList(1, is.size()), false, false);
				Map<AssrtCoreGAction, AssrtCoreGType> cases = new HashMap<>();
				cases.put(gdc, cont);
				return this.factory.GChoice(cases);
			}
			else
			{
				throw new RuntimeException("[f17] Shouldn't get in here: " + first);
			}
		}
		else
		{
			if (checkChoiceGuard)
			{
				throw new F17SyntaxException(first.getSource(), "[f17] Unguarded in choice case: " + first);
			}
			if (is.size() > 1)
			{
				throw new F17SyntaxException(is.get(1).getSource(), "[f17] Bad sequential composition after: " + first);
			}

			if (first instanceof GChoice)
			{
				/*if (checkRecGuard)
				{
					throw new F17Exception(first.getSource(), "[f17] Unguarded in choice case (2): " + first);
				}* /

				GChoice gc = (GChoice) first; 
				List<AssrtCoreGType> parsed = new LinkedList<>();
				for (GProtocolBlock b : gc.getBlocks())
				{
					parsed.add(parseSeq(jc, mc, b.getInteractionSeq().getInteractions(), true, checkRecGuard));  // "Directly" nested choice will still return a GlobalSend (which is really a choice; uniform global choice constructor is convenient)
				}
				Map<AssrtCoreGAction, AssrtCoreGType> cases = new HashMap<>();
				for (AssrtCoreGType p : parsed)
				{
					if (!(p instanceof AssrtCoreGChoice))
					{
						throw new RuntimeException("[f17] Shouldn't get in here: " + p);
					}
					AssrtCoreGChoice tmp = (AssrtCoreGChoice) p;
					//tmp.cases.entrySet().forEach((e) -> cases.put(e.getKey(), e.getValue()));
					for (Entry<AssrtCoreGAction, AssrtCoreGType> e : tmp.cases.entrySet())
					{
						AssrtCoreGAction k = e.getKey();
						if (k.isMessageAction())
						{
							if (cases.keySet().stream().anyMatch((x) ->
									x.isMessageAction() && ((F17MessageAction) k).getOp().equals(((F17MessageAction) x).getOp())))
							{
								throw new F17SyntaxException("[f17] Non-determinism (" + e.getKey() + ") not supported: " + gc);
							}
						}
						cases.put(k, e.getValue());
					}
				}
				return this.factory.GChoice(cases);
			}
			else if (first instanceof GRecursion)
			{
				GRecursion gr = (GRecursion) first;
				RecVar recvar = gr.recvar.toName();
				AssrtCoreGType body = parseSeq(jc, mc, gr.getBlock().getInteractionSeq().getInteractions(), checkChoiceGuard, true);
				return new AssrtCoreGRec(recvar, body);
			}
			else if (first instanceof GContinue)
			{
				if (checkRecGuard)
				{
					throw new F17SyntaxException(first.getSource(), "[f17] Unguarded in recursion: " + first);  // FIXME: conservative, e.g., rec X . A -> B . rec Y . X
				}

				return this.factory.GRecVar(((GContinue) first).recvar.toName());
			}
			else
			{
				throw new RuntimeException("[f17] Shouldn't get in here: " + first);
			}
		}
	}

	private AssrtCoreGMessageTransfer parseGMessageTransfer(GMessageTransfer gmt) throws F17Exception 
	{
		Role src = gmt.src.toName();
		if (gmt.getDestinations().size() > 1)
		{
			throw new F17Exception(gmt.getSource(), " [TODO] Multicast not supported: " + gmt);
		}
		Role dest = gmt.getDestinations().get(0).toName();
		if (!gmt.msg.isMessageSigNode())
		{
			throw new F17SyntaxException(gmt.msg.getSource(), " [f17] Not supported: " + gmt.msg);  // TODO: MessageSigName
		}
		MessageSigNode msn = ((MessageSigNode) gmt.msg);
		Op op = msn.op.toName();
		Payload pay = null;
		if (msn.payloads.getElements().isEmpty())
		{
			pay = Payload.EMPTY_PAYLOAD;
		}
		else
		{
			String tmp = msn.payloads.getElements().get(0).toString().trim();
			int i = tmp.indexOf('@');
			if (i != -1)
			{
				throw new F17Exception("[f17] Delegation not supported: " + tmp);
			}
			else
			{
				pay = msn.payloads.toPayload();
			}
		}
		return this.factory.GMessageTransfer(src, dest, op, pay);
	}

	// Mostly duplicated from parseGMessageTransfer, but GMessageTransfer/GConnect have no useful base class 
	private AssrtCoreGConnect parseGConnect(GConnect gc) throws F17Exception 
	{
		Role src = gc.src.toName();
		Role dest = gc.dest.toName();
		if (!gc.msg.isMessageSigNode())
		{
			throw new F17SyntaxException(gc.msg.getSource(), " [f17] Message kind not supported: " + gc.msg);
		}
		MessageSigNode msn = ((MessageSigNode) gc.msg);
		Op op = msn.op.toName();
		Payload pay = null;
		if (msn.payloads.getElements().isEmpty())
		{
			pay = Payload.EMPTY_PAYLOAD;
		}
		else
		{
			String tmp = msn.payloads.getElements().get(0).toString().trim();
			int i = tmp.indexOf('@');
			if (i != -1)
			{
				throw new F17Exception("[f17] Delegation not supported: " + tmp);
			}
			else
			{
				pay = msn.payloads.toPayload();
			}
		}
		return this.factory.GConnect(src, dest, op, pay);
	}

	private F17GDisconnect parseGDisconnect(GDisconnect gdc) throws F17Exception 
	{
		Role src = gdc.src.toName();
		Role dest = gdc.dest.toName();
		return this.factory.GDisconnect(src, dest);
	}*/
}
