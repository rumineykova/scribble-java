package org.scribble.ext.assrt.core.ast.global;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scribble.ast.MessageSigNode;
import org.scribble.ast.PayloadElem;
import org.scribble.ast.UnaryPayloadElem;
import org.scribble.ast.context.ModuleContext;
import org.scribble.ast.global.GChoice;
import org.scribble.ast.global.GContinue;
import org.scribble.ast.global.GInteractionNode;
import org.scribble.ast.global.GMessageTransfer;
import org.scribble.ast.global.GProtocolDecl;
import org.scribble.ast.global.GProtocolDef;
import org.scribble.ast.global.GRecursion;
import org.scribble.ast.global.GSimpleInteractionNode;
import org.scribble.ast.name.qualified.DataTypeNode;
import org.scribble.ast.name.simple.OpNode;
import org.scribble.del.global.GProtocolDefDel;
import org.scribble.ext.assrt.ast.AssrtAnnotDataTypeElem;
import org.scribble.ext.assrt.ast.AssrtAssertion;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.ext.assrt.ast.formula.AssrtTrue;
import org.scribble.ext.assrt.ast.global.AssrtGMessageTransfer;
import org.scribble.ext.assrt.ast.name.simple.AssrtVarNameNode;
import org.scribble.ext.assrt.core.AssrtCoreSyntaxException;
import org.scribble.ext.assrt.core.ast.AssrtCoreAction;
import org.scribble.ext.assrt.core.ast.AssrtCoreAstFactory;
import org.scribble.ext.assrt.core.ast.global.action.AssrtCoreGActionKind;
import org.scribble.ext.assrt.main.AssrtException;
import org.scribble.ext.assrt.sesstype.kind.AssrtVarNameKind;
import org.scribble.main.Job;
import org.scribble.main.JobContext;
import org.scribble.main.ScribbleException;
import org.scribble.sesstype.kind.DataTypeKind;
import org.scribble.sesstype.name.PayloadType;
import org.scribble.sesstype.name.RecVar;
import org.scribble.sesstype.name.Role;


public class AssrtCoreGProtocolDeclTranslator
{
	private final AssrtCoreAstFactory factory = new AssrtCoreAstFactory();
	
	private final Job job;
	
	private static int counter = 1;
	
	private static int nextIndex()
	{
		return counter++;
	}
	
	//private static DataType UNIT_TYPE;

	public AssrtCoreGProtocolDeclTranslator(Job job)
	{
		this.job = job;
		
		/*if (F17GProtocolDeclTranslator.UNIT_TYPE == null)
		{
			F17GProtocolDeclTranslator.UNIT_TYPE = new DataType("_UNIT");
		}*/
	}

	public AssrtCoreGType translate(ModuleContext mc, GProtocolDecl gpd) throws ScribbleException
	{
		GProtocolDef inlined = ((GProtocolDefDel) gpd.def.del()).getInlinedProtocolDef();
		return parseSeq(job.getContext(), mc, inlined.getBlock().getInteractionSeq().getInteractions(), false, false);
	}

	private AssrtCoreGType parseSeq(JobContext jc, ModuleContext mc, List<GInteractionNode> is,
			boolean checkChoiceGuard, boolean checkRecGuard) throws AssrtException
	{
		if (is.isEmpty())
		{
			return this.factory.GEnd();
		}

		GInteractionNode first = is.get(0);
		if (first instanceof GSimpleInteractionNode && !(first instanceof GContinue))
		{
			if (first instanceof GMessageTransfer)
			{
				return parseGMessageTransfer(jc, mc, is, (GMessageTransfer) first, checkChoiceGuard, checkRecGuard);
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
				throw new RuntimeException("[f17] Shouldn't get in here: " + first);
			}
		}
		else
		{
			if (checkChoiceGuard)
			{
				throw new AssrtCoreSyntaxException(first.getSource(), "[assrt-core] Unguarded in choice case: " + first);
			}
			if (is.size() > 1)
			{
				throw new AssrtCoreSyntaxException(is.get(1).getSource(), "[assrt-core] Bad sequential composition after: " + first);
			}

			if (first instanceof GChoice)
			{
				throw new RuntimeException("TODO");
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
					throw new AssrtCoreSyntaxException(first.getSource(), "[assrt-core] Unguarded in recursion: " + first);  // FIXME: conservative, e.g., rec X . A -> B . rec Y . X
				}

				return this.factory.GRecVar(((GContinue) first).recvar.toName());
			}
			else
			{
				throw new RuntimeException("[assrt-core] Shouldn't get in here: " + first);
			}
		}
	}


	private AssrtCoreGChoice parseGMessageTransfer(JobContext jc, ModuleContext mc, List<GInteractionNode> is, GMessageTransfer gmt, boolean checkChoiceGuard, boolean checkRecGuard) throws AssrtException 
	{
		OpNode op = parseOp(gmt);
		AssrtAnnotDataTypeElem<DataTypeKind> pay = parsePayload(gmt);
		AssrtAssertion ass = parseAssertion(gmt);
		
		AssrtCoreAction a = this.factory.action(op, pay, ass);
		
		AssrtCoreGType cont = parseSeq(jc, mc, is.subList(1, is.size()), false, false);

		Role src = parseSourceRole(gmt);
		Role dest = parseDestinationRole(gmt);
		
		AssrtCoreGActionKind kind = AssrtCoreGActionKind.MESSAGE;
		
		Map<AssrtCoreAction, AssrtCoreGType> cases = new HashMap<>();
		cases.put(a, cont);
		return this.factory.GChoice(src, kind, dest, cases);
	}

	private OpNode parseOp(GMessageTransfer gmt) throws AssrtCoreSyntaxException
	{
		if (!gmt.msg.isMessageSigNode())
		{
			throw new AssrtCoreSyntaxException(gmt.msg.getSource(), " [assrt-core] Not supported: " + gmt.msg);  // TODO: MessageSigName
		}
		MessageSigNode msn = ((MessageSigNode) gmt.msg);
		//return msn.op.toName();
		return msn.op;
	}

	private AssrtAssertion parseAssertion(GMessageTransfer gmt)
	{
		AssrtAssertion ass = ((AssrtGMessageTransfer) gmt).ass;
		return (ass == null) ? ((AssrtAstFactory) this.job.af).AssrtAssertion(null, new AssrtTrue()) : ass;
			// FIXME: singleton constant
	}

	//private AssrtAnnotDataType parsePayload(GMessageTransfer gmt)
	private AssrtAnnotDataTypeElem<DataTypeKind> parsePayload(GMessageTransfer gmt) throws AssrtException
	{
		if (!gmt.msg.isMessageSigNode())
		{
			throw new AssrtCoreSyntaxException(gmt.msg.getSource(), " [assrt-core] Not supported: " + gmt.msg);  // TODO: MessageSigName
		}
		MessageSigNode msn = ((MessageSigNode) gmt.msg);
		if (msn.payloads.getElements().isEmpty())
		{
			//return Payload.EMPTY_PAYLOAD;

			DataTypeNode dtn = (DataTypeNode) ((AssrtAstFactory) this.job.af).QualifiedNameNode(null, DataTypeKind.KIND, "_UNIT");
			AssrtVarNameNode nn = (AssrtVarNameNode) ((AssrtAstFactory) this.job.af).SimpleNameNode(null, AssrtVarNameKind.KIND, "_x" + nextIndex());
			return ((AssrtAstFactory) this.job.af).AssrtAnnotPayloadElem(null, nn, dtn);  // null source OK?
		}
		else if (msn.payloads.getElements().size() > 1)
		{
			throw new AssrtCoreSyntaxException(msn.getSource(), "[assrt-core] Payload with more than one ekement not supported: " + gmt.msg);
		}
		else
		{
			PayloadElem<?> pe = msn.payloads.getElements().get(0);
			String tmp = pe.toString().trim();

			int i = tmp.indexOf('@');  // FIXME: check by type instead
			if (i != -1)
			{
				throw new AssrtException("[assrt-core] Delegation not supported: " + tmp);
			}

				//return msn.payloads.toPayload();
				if (pe instanceof AssrtAnnotDataTypeElem<?>)  // FIXME: implicitly DataType anyway (remove redundant type parameter)
				{
					return (AssrtAnnotDataTypeElem<DataTypeKind>) pe;
				}

				UnaryPayloadElem<?> upe = (UnaryPayloadElem<?>) pe;
				PayloadType<?> type = upe.toPayloadType();
				if (!type.isDataType())
				{
					throw new AssrtCoreSyntaxException(upe.getSource(), "[assrt-core] Non- data type kind payload not supported: " + upe);
				}
				
				AssrtVarNameNode nn = (AssrtVarNameNode) ((AssrtAstFactory) this.job.af).SimpleNameNode(null, AssrtVarNameKind.KIND, "_x" + nextIndex());

				return ((AssrtAstFactory) this.job.af).AssrtAnnotPayloadElem(null, nn, (DataTypeNode) upe.name);  // null source OK?
		}
	}

	private Role parseSourceRole(GMessageTransfer gmt)
	{
		return gmt.src.toName();
	}
	
	private Role parseDestinationRole(GMessageTransfer gmt) throws AssrtException
	{
		if (gmt.getDestinations().size() > 1)
		{
			throw new AssrtException(gmt.getSource(), " [TODO] Multicast not supported: " + gmt);
		}
		return gmt.getDestinations().get(0).toName();
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
