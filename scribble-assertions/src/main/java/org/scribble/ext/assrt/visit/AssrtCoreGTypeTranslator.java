/**
 * Copyright 2008 The Scribble Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.scribble.ext.assrt.visit;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.scribble.ast.DirectedInteraction;
import org.scribble.ast.Module;
import org.scribble.ast.MsgNode;
import org.scribble.ast.PayElem;
import org.scribble.ast.ScribNode;
import org.scribble.ast.ScribNodeBase;
import org.scribble.ast.SigLitNode;
import org.scribble.ast.UnaryPayElem;
import org.scribble.ast.global.GChoice;
import org.scribble.ast.global.GContinue;
import org.scribble.ast.global.GProtoBlock;
import org.scribble.ast.global.GProtoDecl;
import org.scribble.ast.global.GProtoDef;
import org.scribble.ast.global.GSessionNode;
import org.scribble.ast.global.GSimpleSessionNode;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.core.lang.ProtoMod;
import org.scribble.core.lang.global.GNode;
import org.scribble.core.type.kind.Global;
import org.scribble.core.type.kind.NonRoleParamKind;
import org.scribble.core.type.name.DataName;
import org.scribble.core.type.name.GProtoName;
import org.scribble.core.type.name.MemberName;
import org.scribble.core.type.name.ModuleName;
import org.scribble.core.type.name.Op;
import org.scribble.core.type.name.PayElemType;
import org.scribble.core.type.name.RecVar;
import org.scribble.core.type.name.Role;
import org.scribble.core.type.session.STypeFactory;
import org.scribble.ext.assrt.ast.AssrtActionAssertNode;
import org.scribble.ext.assrt.ast.AssrtAnnotDataElem;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.ext.assrt.ast.AssrtBExprNode;
import org.scribble.ext.assrt.ast.global.AssrtGDo;
import org.scribble.ext.assrt.ast.global.AssrtGMsgTransfer;
import org.scribble.ext.assrt.ast.global.AssrtGProtoHeader;
import org.scribble.ext.assrt.core.lang.global.AssrtCoreGProtocol;
import org.scribble.ext.assrt.core.type.formula.AssrtAFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtTrueFormula;
import org.scribble.ext.assrt.core.type.name.AssrtAnnotDataName;
import org.scribble.ext.assrt.core.type.name.AssrtIntVar;
import org.scribble.ext.assrt.core.type.session.AssrtCoreActionKind;
import org.scribble.ext.assrt.core.type.session.AssrtCoreMsg;
import org.scribble.ext.assrt.core.type.session.AssrtCoreSTypeFactory;
import org.scribble.ext.assrt.core.type.session.AssrtCoreSyntaxException;
import org.scribble.ext.assrt.core.type.session.global.AssrtCoreGActionKind;
import org.scribble.ext.assrt.core.type.session.global.AssrtCoreGChoice;
import org.scribble.ext.assrt.core.type.session.global.AssrtCoreGType;
import org.scribble.job.Job;
import org.scribble.visit.GTypeTranslator;

public class AssrtCoreGTypeTranslator extends GTypeTranslator
{
	public static final DataName UNIT_DATATYPE = new DataName("_Unit");  // TODO
	
	private static int varCounter = 1;
	private static int recCounter = 1;
	
	public final AssrtCoreSTypeFactory tf;  // Shadows super
	
	//private static DataType UNIT_TYPE;
	protected AssrtCoreGTypeTranslator(Job job, ModuleName rootFullname, STypeFactory tf)
	{
		super(job, rootFullname, tf);
		this.tf = (AssrtCoreSTypeFactory) tf;
	}

	@Override
	public GNode visit(ScribNode n)
	{
		//return ((GDel) n.del()).translate(n, this);
		if (n instanceof GProtoDecl)
		{
			return translate((GProtoDecl) n);
		}
		throw new RuntimeException("Deprecated for " + getClass() + ":\n" + n);
	}

	// Cf. GProtoDeclDel::translate
	public AssrtCoreGProtocol translate(GProtoDecl n)
			throws AssrtCoreSyntaxException
	{
		GProtoDef def = n.getDefChild();
		
		Module m = (Module) n.getParent();
		List<ProtoMod> mods = n.getModifierListChild().getModList().stream()
				.map(x -> x.toProtoMod()).collect(Collectors.toList());
		AssrtGProtoHeader hdr = (AssrtGProtoHeader) n.getHeaderChild();
		GProtoName fullname = new GProtoName(m.getFullModuleName(),
				hdr.getDeclName());
		List<Role> rs = n.getRoles();
		List<MemberName<? extends NonRoleParamKind>> ps = hdr
				.getParamDeclListChild().getParams();  // CHECKME: make more uniform with source::getRoles ?
		if (!ps.isEmpty())
		{
			throw new RuntimeException(
					"[TODO] Proto params not yet supported:\n" + n);
		}
		
		System.out.println("aaaa:\n" + def + "\n");

		AssrtCoreGType body = parseSeq(
				def.getBlockChild().getInteractSeqChild().getInteractionChildren(),
				new HashMap<>(), false, false);

		LinkedHashMap<AssrtIntVar, AssrtAFormula> svars = new LinkedHashMap<>();
		hdr.getStateVarDeclListChild().getDeclChildren().forEach(
				x -> svars.put(x.getDeclName(), x.getStateVarExprChild().expr));
		AssrtBFormula ass = hdr.getAnnotAssertChild().expr;
		return new AssrtCoreGProtocol(n, mods, fullname, rs, ps, body, svars, ass);
	}

	// List<GSessionNode> because subList is useful for parsing the continuation
	private AssrtCoreGType parseSeq(List<GSessionNode> is, Map<RecVar, RecVar> rvs,
			boolean checkChoiceGuard, boolean checkRecGuard) throws AssrtCoreSyntaxException
	{
		if (is.isEmpty())
		{
			return this.tf.global.AssrtCoreGEnd();
		}

		GSessionNode first = is.get(0);
		if (first instanceof GSimpleSessionNode && !(first instanceof GContinue))
		{
			if (first instanceof AssrtGMsgTransfer)
			{
				return parseAssrtGMsgTransfer(is, rvs, (AssrtGMsgTransfer) first);
			}
			/*else if (first instanceof AssrtGConnect)
			{
				return parseAssrtGConnect(is, rvs, (AssrtGConnect) first);
			}*/
			/*else if (first instanceof GDisconnect)
			{
				F17GDisconnect gdc = parseGDisconnect((GDisconnect) first);
				AssrtCoreGType cont = parseSeq(jc, mc, is.subList(1, is.size()), false, false);
				Map<AssrtCoreGAction, AssrtCoreGType> cases = new HashMap<>();
				cases.put(gdc, cont);
				return this.factory.GChoice(cases);
			}*/
			else if (first instanceof AssrtGDo)  // Inlining done later on AssrtCoreGType
			{
				throw new RuntimeException("[TODO] :\n" + first);
			}
			else
			{
				throw new RuntimeException("[assrt-core] [TODO] :\n" + first);
			}
		}
		else
		{
			if (checkChoiceGuard)  // No "flattening" of nested choices not allowed?
			{
				throw new AssrtCoreSyntaxException(first.getSource(),
						"[assrt-core] Unguarded in choice case: " + first);
			}
			if (is.size() > 1)
			{
				throw new AssrtCoreSyntaxException(is.get(1).getSource(),
						"[assrt-core] Bad sequential composition after: " + first);
			}

			if (first instanceof GChoice)
			{
				return parseGChoice(rvs, checkRecGuard, (GChoice) first);
			}
			/*else if (first instanceof AssrtGRecursion)
			{
				return parseAssrtGRecursion(rvs, checkChoiceGuard, (AssrtGRecursion) first);
			}
			else if (first instanceof GContinue)
			{
				return parseAssrtGContinue(rvs, checkRecGuard, (AssrtGContinue) first);
			}*/
			else
			{
				throw new RuntimeException("[assrt-core] Shouldn't get in here: " + first);
			}
		}
	}

	private AssrtCoreGType parseGChoice(Map<RecVar, RecVar> rvs,
			boolean checkRecGuard, GChoice n) throws AssrtCoreSyntaxException
	{
		List<AssrtCoreGType> children = new LinkedList<>();
		for (GProtoBlock b : n.getBlockChildren())
		{
			children.add(parseSeq(b.getInteractSeqChild().getInteractionChildren(),
					rvs, true, checkRecGuard));  // Check cases are guarded
		}

		Role src = null;
		Role dst = null;
		AssrtCoreActionKind<Global> kind = null;  // CHECKME: generic parameter ?
		Map<AssrtCoreMsg, AssrtCoreGType> cases = new HashMap<>();
		for (AssrtCoreGType c : children)
		{
			// Cases must be "action-guarded" (unary choices), as already parsed successfully
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
				dst = tmp.dst;
			}
			else if (!kind.equals(tmp.kind))
			{
				throw new AssrtCoreSyntaxException(n.getSource(),
						"[assrt-core] Mixed-action choices not supported:\n" + n);
			}
			else if (!src.equals(tmp.src) || !dst.equals(tmp.dst))
			{
				throw new AssrtCoreSyntaxException(n.getSource(),
						"[assrt-core] Non-directed choice not supported:\n" + n);
			}
			
			// "Flatten" nested choices (already checked they are guarded) -- Scribble choice subjects ignored
			for (Entry<AssrtCoreMsg, AssrtCoreGType> e : tmp.cases.entrySet())
			{
				AssrtCoreMsg k = e.getKey();
				if (cases.keySet().stream().anyMatch(x -> x.op.equals(k.op)))
				{
					throw new AssrtCoreSyntaxException(n.getSource(),
							"[assrt-core] Non-deterministic actions not supported: " + k.op);
				}
				cases.put(k, e.getValue());
			}
		}
		
		return this.tf.global.AssrtCoreGChoice(n, src, (AssrtCoreGActionKind) kind,
				dst, cases);
	}

	/*private AssrtCoreGType parseAssrtGRecursion(Map<RecVar, RecVar> rvs,
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
	}*/

	// Parses message interactions as unary choices
	private AssrtCoreGChoice parseAssrtGMsgTransfer(List<GSessionNode> is,
			Map<RecVar, RecVar> rvs, AssrtGMsgTransfer n)
			throws AssrtCoreSyntaxException
	{
		Op op = parseOp(n);
		List<AssrtAnnotDataName> pay = parsePayload(n);
		AssrtBExprNode bexpr = parseAssertion(n);
		AssrtCoreMsg a = this.tf.AssrtCoreAction(op, pay, bexpr.getFormula());
		Role src = parseSrcRole(n);
		Role dst = parseDstRole(n);
		AssrtCoreGActionKind kind = AssrtCoreGActionKind.MSG_TRANSFER;
		return parseGSimpleInteractionNode(is, rvs, src, a, dst, kind);
	}

	// Duplicated from parseGMsgTransfer (MsgTransfer and ConnectionAction have no common base
	
	/*private AssrtCoreGChoice parseAssrtGConnect(List<GSimpleSessionNode> is,
			Map<RecVar, RecVar> rvs, AssrtGConnect gc) throws AssrtCoreSyntaxException
	{
		Op op = parseOp(gc);
		//AssrtAnnotDataType pay = parsePayload(gc);
		List<AssrtAnnotDataType> pays = parsePayload(gc);
		AssrtAssertion ass = parseAssertion(gc);
		AssrtCoreMsg a = this.af.AssrtCoreAction(op, pays, ass.getFormula());
		Role src = parseSourceRole(gc);
		Role dst = parseDestinationRole(gc);
		AssrtCoreGActionKind kind = AssrtCoreGActionKind.CONNECT;
		return parseGSimpleInteractionNode(is, rvs, src, a, dst, kind);
	}*/

	private AssrtCoreGChoice parseGSimpleInteractionNode(List<GSessionNode> is,
			Map<RecVar, RecVar> rvs, Role src, AssrtCoreMsg msg, Role dst,
			AssrtCoreGActionKind kind) throws AssrtCoreSyntaxException
	{
		if (src.equals(dst))
		{
			throw new RuntimeException(
					"[assrt-core] Self-communication (shouldn't get in here): " + src
							+ ", " + dst);
		}
		
		AssrtCoreGType cont = parseSeq(is.subList(1, is.size()), rvs, false, false);  
				// Subseqeuent choice/rec is guarded by (at least) this action
		
		System.out.println("bbbb: " + is.get(0) + " ,, " + msg);
		
		return this.tf.global.AssrtCoreGChoice((ScribNodeBase) is.get(0), src, kind,
				dst, Stream.of(msg).collect(Collectors.toMap(x -> x, x -> cont)));
	}

	private Op parseOp(DirectedInteraction<Global> n)
			throws AssrtCoreSyntaxException
	{
		return parseOp(n.getMessageNodeChild());
	}

	private Op parseOp(MsgNode n) throws AssrtCoreSyntaxException
	{
		if (!n.isSigLitNode())
		{
			throw new AssrtCoreSyntaxException(n.getSource(),
					"[TODO] " + n.getClass() + ":\n\t" + n);  // TODO: SigName
		}
		SigLitNode msn = ((SigLitNode) n);
		return msn.getOpChild().toName();
	}

	//private AssrtAnnotDataType parsePayload(AssrtGMsgTransfer gmt) throws AssrtCoreSyntaxException
	private List<AssrtAnnotDataName> parsePayload(DirectedInteraction<Global> n)
			throws AssrtCoreSyntaxException
	{
		return parsePayload(n.getMessageNodeChild());
	}

	private List<AssrtAnnotDataName> parsePayload(MsgNode n) throws AssrtCoreSyntaxException
	{
		if (!n.isSigLitNode())
		{
			throw new AssrtCoreSyntaxException(n.getSource(),
					" [TODO] " + n.getClass() + ":\n\t" + n); // TODO: SigName
		}
		SigLitNode msn = ((SigLitNode) n);
		if (msn.getPayElemListChild().getElementChildren().isEmpty())
		{
//			DataTypeNode dtn = (DataTypeNode) ((AssrtAstFactory) this.job.af).QualifiedNameNode(null, DataTypeKind.KIND, "_Unit");
//			AssrtVarNameNode nn = (AssrtVarNameNode) ((AssrtAstFactory) this.job.af).SimpleNameNode(null, AssrtVarNameKind.KIND, "_x" + nextVarIndex());
//			return ((AssrtAstFactory) this.job.af).AssrtAnnotPayloadElem(null, nn, dtn);  // null source OK?

			return Stream
					.of(new AssrtAnnotDataName(makeFreshDataTypeVar(),
							AssrtCoreGProtoDeclTranslator.UNIT_DATATYPE))
					.collect(Collectors.toList()); // TODO: make empty list
		}
//		else if (msn.payloads.getElements().size() > 1)
//		{
//			throw new AssrtCoreSyntaxException(msn.getSource(), "[assrt-core] Payload with more than one element not supported: " + mn);
//		}

		//PayloadElem<?> pe = msn.payloads.getElements().get(0);
		List<AssrtAnnotDataName> res = new LinkedList<>();
		for (PayElem<?> e : msn.getPayElemListChild().getElementChildren())
		{
			if (!(e instanceof AssrtAnnotDataElem)
					&& !(e instanceof UnaryPayElem<?>))
			{
				// Already ruled out by parsing?  e.g., GDelegationElem
				throw new AssrtCoreSyntaxException(
						"[assrt-core] Payload element not supported: " + e);
			}
//			else if (pe instanceof LDelegationElem)  // No: only created by projection, won't be parsed
//			{
//				throw new AssrtCoreSyntaxException("[assrt-core] Payload element not supported: " + pe);
//			}
			
			if (e instanceof AssrtAnnotDataElem)
			{
				AssrtAnnotDataName data = ((AssrtAnnotDataElem) e).toPayloadType();
				String type = data.data.toString();
				if (!type.equals("int") && !type.endsWith(".int"))  // HACK FIXME (currently "int" for F#)
				{
					throw new AssrtCoreSyntaxException(e.getSource(),
							"[assrt-core] Payload annotations not supported for non- \"int\" type kind: "
									+ e);
				}
				
				// FIXME: non-annotated payload elems get created with fresh vars, i.e., non- int types
				// Also empty payloads are created as Unit type
				// But current model building implicitly treats all vars as int -- this works, but is not clean
				
				//return adt;
				res.add(data);
			}
			else
			{
				PayElemType<?> e1 = ((UnaryPayElem<?>) e).toPayloadType();
				if (!e1.isDataName())
				{
				// i.e., AssrtDataTypeVar not allowed (should be "encoded")
					throw new AssrtCoreSyntaxException(e.getSource(),
							"[assrt-core] Non- datatype kind payload not supported: " + e);
				}
				res.add(new AssrtAnnotDataName(makeFreshDataTypeVar(), (DataName) e1));
			}
		}
		return res;
	}

	private AssrtBExprNode parseAssertion(AssrtActionAssertNode n)
	{
		return parseAssertion(n.getAssertionChild());
	}

	// Empty assertions translated to True
	private AssrtBExprNode parseAssertion(AssrtBExprNode n)
	{
		return (n == null)
				? ((AssrtAstFactory) this.job.config.af).AssrtBExprNode(null,
						AssrtTrueFormula.TRUE)
				: n;
			// FIXME: singleton constant
	}

	private Role parseSrcRole(DirectedInteraction<Global> n)
	{
		return parseSrcRole(n.getSourceChild());
	}

	private Role parseSrcRole(RoleNode n)
	{
		return n.toName();
	}
	
	private Role parseDstRole(DirectedInteraction<Global> n)
			throws AssrtCoreSyntaxException
	{
		List<RoleNode> dsts = n.getDestinationChildren();
		if (dsts.size() > 1)
		{
			throw new AssrtCoreSyntaxException(n.getSource(),
					"[assrt-core] Multisend not supported:\n\t" + n);
		}
		return parseDstRole(dsts.get(0));
	}

	private Role parseDstRole(RoleNode r) throws AssrtCoreSyntaxException
	{
		return r.toName();
	}
	
	private static AssrtIntVar makeFreshDataTypeVar()
	{
		return new AssrtIntVar("_dum" + varCounter++);
	}

	private static String makeFreshRecVarName()
	{
		return "_X" + recCounter++;
	}
}
