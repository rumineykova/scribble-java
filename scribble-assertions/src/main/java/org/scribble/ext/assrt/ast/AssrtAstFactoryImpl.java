package org.scribble.ext.assrt.ast;

import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactoryImpl;
import org.scribble.ast.DataOrSigDeclNode;
import org.scribble.ast.ImportDecl;
import org.scribble.ast.MessageNode;
import org.scribble.ast.ModuleDecl;
import org.scribble.ast.NonRoleArgList;
import org.scribble.ast.NonRoleParamDeclList;
import org.scribble.ast.ProtocolDecl;
import org.scribble.ast.RoleArgList;
import org.scribble.ast.RoleDeclList;
import org.scribble.ast.global.GChoice;
import org.scribble.ast.global.GInteractionSeq;
import org.scribble.ast.global.GProtocolBlock;
import org.scribble.ast.global.GProtocolDecl;
import org.scribble.ast.global.GProtocolDef;
import org.scribble.ast.global.GProtocolHeader;
import org.scribble.ast.global.GRecursion;
import org.scribble.ast.local.LInteractionSeq;
import org.scribble.ast.local.LProjectionDecl;
import org.scribble.ast.local.LProtocolBlock;
import org.scribble.ast.local.LProtocolDef;
import org.scribble.ast.local.LProtocolHeader;
import org.scribble.ast.local.LReceive;
import org.scribble.ast.name.NameNode;
import org.scribble.ast.name.qualified.DataTypeNode;
import org.scribble.ast.name.qualified.GProtocolNameNode;
import org.scribble.ast.name.qualified.LProtocolNameNode;
import org.scribble.ast.name.simple.AmbigNameNode;
import org.scribble.ast.name.simple.RecVarNode;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.ext.assrt.ast.global.AssrtGConnect;
import org.scribble.ext.assrt.ast.global.AssrtGContinue;
import org.scribble.ext.assrt.ast.global.AssrtGDo;
import org.scribble.ext.assrt.ast.global.AssrtGDoDel;
import org.scribble.ext.assrt.ast.global.AssrtGMsgTransfer;
import org.scribble.ext.assrt.ast.global.AssrtGProtocolHeader;
import org.scribble.ext.assrt.ast.global.AssrtGRecursion;
import org.scribble.ext.assrt.ast.local.AssrtLContinue;
import org.scribble.ext.assrt.ast.local.AssrtLDo;
import org.scribble.ext.assrt.ast.local.AssrtLProtocolHeader;
import org.scribble.ext.assrt.ast.local.AssrtLRecursion;
import org.scribble.ext.assrt.ast.local.AssrtLRequest;
import org.scribble.ext.assrt.ast.local.AssrtLSend;
import org.scribble.ext.assrt.ast.name.qualified.AssrtAssertNameNode;
import org.scribble.ext.assrt.ast.name.simple.AssrtIntVarNameNode;
import org.scribble.ext.assrt.ast.name.simple.AssrtSortNode;
import org.scribble.ext.assrt.core.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtSmtFormula;
import org.scribble.ext.assrt.core.type.kind.AssrtVarNameKind;
import org.scribble.ext.assrt.del.AssrtAnnotDataTypeElemDel;
import org.scribble.ext.assrt.del.AssrtModuleDel;
import org.scribble.ext.assrt.del.global.AssrtGChoiceDel;
import org.scribble.ext.assrt.del.global.AssrtGConnectDel;
import org.scribble.ext.assrt.del.global.AssrtGContinueDel;
import org.scribble.ext.assrt.del.global.AssrtGMessageTransferDel;
import org.scribble.ext.assrt.del.global.AssrtGProtocolBlockDel;
import org.scribble.ext.assrt.del.global.AssrtGProtocolDeclDel;
import org.scribble.ext.assrt.del.global.AssrtGProtocolDefDel;
import org.scribble.ext.assrt.del.global.AssrtGRecursionDel;
import org.scribble.ext.assrt.del.local.AssrtLContinueDel;
import org.scribble.ext.assrt.del.local.AssrtLDoDel;
import org.scribble.ext.assrt.del.local.AssrtLProjectionDeclDel;
import org.scribble.ext.assrt.del.local.AssrtLProtocolBlockDel;
import org.scribble.ext.assrt.del.local.AssrtLProtocolDefDel;
import org.scribble.ext.assrt.del.local.AssrtLReceiveDel;
import org.scribble.ext.assrt.del.local.AssrtLRecursionDel;
import org.scribble.ext.assrt.del.local.AssrtLRequestDel;
import org.scribble.ext.assrt.del.local.AssrtLSendDel;
import org.scribble.ext.assrt.del.name.AssrtAmbigNameNodeDel;
import org.scribble.type.kind.Kind;
import org.scribble.type.name.GProtocolName;
import org.scribble.type.name.Role;


// FIXME: separate modified-del-only from new categories
public class AssrtAstFactoryImpl extends AstFactoryImpl implements AssrtAstFactory
{
	
	/**
	 *  Instantiating existing node classes with new dels
	 */

	@Override
	public GProtocolDecl GProtocolDecl(CommonTree source, List<GProtocolDecl.Modifiers> mods, GProtocolHeader header, GProtocolDef def)
	{
		GProtocolDecl gpd = new GProtocolDecl(source, mods, header, def);
		gpd = del(gpd, new AssrtGProtocolDeclDel());
		return gpd;
	}

	@Override
	public GProtocolDef GProtocolDef(CommonTree source, GProtocolBlock block)
	{
		GProtocolDef gpd = new GProtocolDef(source, block);
		gpd = del(gpd, new AssrtGProtocolDefDel());  // Uses header annot to do AssrtAnnotationChecker Def enter/exit
		return gpd;
	}
	
	@Override
	public GProtocolBlock GProtocolBlock(CommonTree source, GInteractionSeq seq)
	{
		GProtocolBlock gpb = new GProtocolBlock(source, seq);
		gpb = del(gpb, new AssrtGProtocolBlockDel());
		return gpb;
	}

	@Override
	public GChoice GChoice(CommonTree source, RoleNode subj, List<GProtocolBlock> blocks)
	{
		GChoice gc = new GChoice(source, subj, blocks);
		gc = del(gc, new AssrtGChoiceDel());
		return gc;
	}

	/*@Override
	public GRecursion GRecursion(CommonTree source, RecVarNode recvar, GProtocolBlock block)
	{
		GRecursion gr = new GRecursion(source, recvar, block);
		gr = del(gr, new AssrtGRecursionDel());
		return gr;
	}*/

	@Override
	public AmbigNameNode AmbiguousNameNode(CommonTree source, String identifier)
	{
		AmbigNameNode ann = new AmbigNameNode(source, identifier); 
		ann = (AmbigNameNode) ann.del(new AssrtAmbigNameNodeDel());
		return ann;
	}

	@Override
	public LProjectionDecl LProjectionDecl(CommonTree source, List<ProtocolDecl.Modifiers> mods, GProtocolName fullname, Role self, LProtocolHeader header, LProtocolDef def)
	{
		LProjectionDecl lpd = new LProjectionDecl(source, mods, header, def);
		lpd = del(lpd, new AssrtLProjectionDeclDel(fullname, self));
		return lpd;
	}

	@Override
	public LProtocolDef LProtocolDef(CommonTree source, LProtocolBlock block)
	{
		LProtocolDef lpd = new LProtocolDef(source, block);
		lpd = del(lpd, new AssrtLProtocolDefDel());
		return lpd;
	}

	
	@Override
	public LReceive LReceive(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests)
	{
		LReceive ls = new LReceive(source, src, msg, dests);  // FIXME: AssrtLReceive with assertion?
		ls = del(ls, new AssrtLReceiveDel());
		return ls;
	}

	@Override
	public LProtocolBlock LProtocolBlock(CommonTree source, LInteractionSeq seq)
	{
		LProtocolBlock lpb = new LProtocolBlock(source, seq);
		lpb = del(lpb, new AssrtLProtocolBlockDel());
		return lpb;
	}

	/* // Cf. GMessageTransfer -- empty-annotation sends still created as AssrtLSend, with null assertion -- but AssrtLSendDel still needed
	 * // No: not needed, because all globals made as Assrts, so projections are always Assrts
	@Override
	public AssrtLSend LSend(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests)
	{
		//LSend ls = new LSend(source, src, msg, dests);
		AssrtLSend ls = new AssrtLSend(source, src, msg, dests);
		ls = del(ls, new AssrtLSendDel());
		return ls;
	}*/

	/*@Override
	public AssrtLConnect LConnect(CommonTree source, RoleNode src, MessageNode msg, RoleNode dests)
	{
		AssrtLConnect ls = new AssrtLConnect(source, src, msg, dests);
		ls = del(ls, new AssrtLConnectDel());
		return ls;
	}*/
	
	
	/**
	 *  Returning new node classes in place of existing
	 */
	
	@Override
	public AssrtModule Module(CommonTree source, ModuleDecl moddecl, List<ImportDecl<?>> imports, List<DataOrSigDeclNode<?>> data,
			List<ProtocolDecl<?>> protos)
	{
		AssrtModule mod = new AssrtModule(source, moddecl, imports, data, protos);
		mod = del(mod, new AssrtModuleDel());
		return mod;
	}

	// Still used by parsing for empty annotation/assertion nodes -- but we return an Assrt node
	// Easier to make all global as Assrt nodes, to avoid cast checks in, e.g., AssrtGProtocolDeclDel::leaveProjection (for GProtocolHeader), and so all projections will be Assrt kinds only

	@Override
	public AssrtGProtocolHeader GProtocolHeader(CommonTree source, GProtocolNameNode name, RoleDeclList roledecls, NonRoleParamDeclList paramdecls)
	{
		// Alternative is to make parsing return all as AssrtGProtocolHeader directly
		AssrtGProtocolHeader gpb = new AssrtGProtocolHeader(source, name, roledecls, paramdecls);
		gpb = del(gpb, createDefaultDelegate());  // Annots handled directly by AssrtAnnotationChecker Def enter/exit
		return gpb;
	}

	// Same pattern as for GProtocolHeader
	// Non-annotated message transfers still created as AssrtGMessageTransfer -- null assertion, but AssrtGMessageTransferDel is still needed (why?)
	@Override
	public AssrtGMsgTransfer GMessageTransfer(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests)
	{
		AssrtGMsgTransfer gmt = new AssrtGMsgTransfer(source, src, msg, dests);
		gmt = del(gmt, new AssrtGMessageTransferDel());
		return gmt;
	}

	@Override 
	public AssrtGConnect GConnect(CommonTree source, RoleNode src, MessageNode msg, RoleNode dest)  // Cf. AssrtAstFactoryImpl::GMessageTransfer
	{
		AssrtGConnect gc = new AssrtGConnect(source, src, msg, dest);
		gc = del(gc, new AssrtGConnectDel());
		return gc;
	}
	
	@Override
	public GRecursion GRecursion(CommonTree source, RecVarNode recvar, GProtocolBlock block)
	{
		AssrtGRecursion gr = new AssrtGRecursion(source, recvar, block);
		gr = del(gr, new AssrtGRecursionDel());
		return gr;
	}

	@Override
	public AssrtGContinue GContinue(CommonTree source, RecVarNode recvar)
	{
		AssrtGContinue gc = new AssrtGContinue(source, recvar);
		gc = del(gc, new AssrtGContinueDel());
		return gc;
	}

	@Override
	public AssrtGDo GDo(CommonTree source, RoleArgList roleinstans, NonRoleArgList arginstans, GProtocolNameNode proto)
	{
		AssrtGDo gd = new AssrtGDo(source, roleinstans, arginstans, proto);
		gd = del(gd, new AssrtGDoDel());
		return gd;
	}
	

	/**
	 *  Explicitly creating new Assrt nodes
	 */

	@Override
	public AssrtGProtocolHeader AssrtGProtocolHeader(CommonTree source, GProtocolNameNode name, RoleDeclList roledecls, NonRoleParamDeclList paramdecls, //AssrtAssertion ass)
			List<AssrtIntVarNameNode> annotvars, List<AssrtArithExpr> annotexprs,
			AssrtAssertion ass)
	{
		AssrtGProtocolHeader gpb = new AssrtGProtocolHeader(source, name, roledecls, paramdecls, //ass);
				annotvars, annotexprs,
				ass);
		gpb = del(gpb, createDefaultDelegate());  // Annots handled directly by AssrtAnnotationChecker Def enter/exit
		return gpb;
	}

	@Override
	public AssrtGMsgTransfer AssrtGMessageTransfer(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests, AssrtAssertion assertion)
	{
		AssrtGMsgTransfer gmt = new AssrtGMsgTransfer(source, src, msg, dests, assertion);
		gmt = del(gmt, new AssrtGMessageTransferDel());
		return gmt;
	}

	@Override
	public AssrtGConnect AssrtGConnect(CommonTree source, RoleNode src, MessageNode msg, RoleNode dest, AssrtAssertion assertion)
	{
		AssrtGConnect gc = new AssrtGConnect(source, src, msg, dest, assertion);
		gc = del(gc, new AssrtGConnectDel());
		return gc;
	}

	@Override
	public AssrtGRecursion AssrtGRecursion(CommonTree source, RecVarNode recvar, GProtocolBlock block, //AssrtAssertion ass)
			List<AssrtIntVarNameNode> annotvars, List<AssrtArithExpr> annotexprs,
			AssrtAssertion ass)
	{
		AssrtGRecursion gr = new AssrtGRecursion(source, recvar, block, //ass);
				annotvars, annotexprs,
				ass);
		gr = del(gr, new AssrtGRecursionDel());
		return gr;
	}

	@Override
	public AssrtGContinue AssrtGContinue(CommonTree source, RecVarNode recvar, //AssrtArithExpr annot)
			List<AssrtArithExpr> annotexprs)
	{
		AssrtGContinue gc = new AssrtGContinue(source, recvar, //annot);
				annotexprs);
		gc = del(gc, new AssrtGContinueDel());
		return gc;
	}

	@Override
	public AssrtGDo AssrtGDo(CommonTree source, RoleArgList roleinstans, NonRoleArgList arginstans, GProtocolNameNode proto, //AssrtArithExpr annot)
			List<AssrtArithExpr> annotexprs)
	{
		AssrtGDo gd = new AssrtGDo(source, roleinstans, arginstans, proto, //annot);
				annotexprs);
		gd = del(gd, new AssrtGDoDel());
		return gd;
	}

	@Override
	public AssrtLProtocolHeader AssrtLProtocolHeader(CommonTree source, LProtocolNameNode name, RoleDeclList roledecls, NonRoleParamDeclList paramdecls, //AssrtAssertion ass)
			List<AssrtIntVarNameNode> annotvars, List<AssrtArithExpr> annotexprs,
			AssrtAssertion ass)
	{
		AssrtLProtocolHeader lph = new AssrtLProtocolHeader(source, name, roledecls, paramdecls, //ass);
				annotvars, annotexprs,
				ass);
		lph = del(lph, createDefaultDelegate());  // Annots handled directly by AssrtAnnotationChecker Def enter/exit
		return lph;
	}

	@Override
	public AssrtLSend AssrtLSend(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests, AssrtAssertion assertion)
	{
		AssrtLSend ls = new AssrtLSend(source, src, msg, dests, assertion);
		ls = del(ls, new AssrtLSendDel());
		return ls;
	}

	@Override
	public AssrtLRequest AssrtLConnect(CommonTree source, RoleNode src, MessageNode msg, RoleNode dest, AssrtAssertion ass)
	{
		AssrtLRequest ls = new AssrtLRequest(source, src, msg, dest, ass);
		ls = del(ls, new AssrtLRequestDel());
		return ls;
	}

	@Override
	public AssrtLRecursion AssrtLRecursion(CommonTree source, RecVarNode recvar, LProtocolBlock block, //AssrtAssertion ass)
			List<AssrtIntVarNameNode> annotvars, List<AssrtArithExpr> annotexprs,
			AssrtAssertion ass)
	{
		AssrtLRecursion lr = new AssrtLRecursion(source, recvar, block, //ass);
				annotvars, annotexprs,
				ass);
		lr = del(lr, new AssrtLRecursionDel());
		return lr;
	}

	@Override
	public AssrtLContinue AssrtLContinue(CommonTree source, RecVarNode recvar, //AssrtArithExpr annot)
			List<AssrtArithExpr> annotexprs)
	{
		AssrtLContinue lc = new AssrtLContinue(source, recvar, //annot);
				annotexprs);
		lc = del(lc, new AssrtLContinueDel());
		return lc;
	}

	@Override
	public AssrtLDo AssrtLDo(CommonTree source, RoleArgList roleinstans, NonRoleArgList arginstans, LProtocolNameNode proto, //AssrtArithExpr annot)
			List<AssrtArithExpr> annotexprs)
	{
		AssrtLDo gd = new AssrtLDo(source, roleinstans, arginstans, proto, //annot);
				annotexprs);
		gd = del(gd, new AssrtLDoDel());
		return gd;
	}
	
	// An "additional" category, does not "replace" an existing one -- cf. AssrtGMessageTransfer
	@Override
	public AssrtAnnotDataTypeElem AssrtAnnotDataTypeElem(CommonTree source, AssrtIntVarNameNode var, DataTypeNode data)
	{
		AssrtAnnotDataTypeElem de = new AssrtAnnotDataTypeElem(source, var, data);
		de = del(de, new AssrtAnnotDataTypeElemDel());
		return de;
	}

	@Override
	public <K extends Kind> NameNode<K> SimpleNameNode(CommonTree source, K kind, String identifier)
	{
		if (kind.equals(AssrtVarNameKind.KIND))
		{
			NameNode<? extends Kind> snn = new AssrtIntVarNameNode(source, identifier);
			snn = del(snn, createDefaultDelegate()); 
			return castNameNode(kind, snn);
		}

		return super.SimpleNameNode(source, kind, identifier);
	}


	@Override
	//public AssrtAssertionNode AssertionNode(CommonTree source, String assertion)
	public AssrtAssertion AssrtAssertion(CommonTree source, AssrtBoolFormula f)
	{
		//AssrtAssertionNode node = new AssrtAssertionNode(source, assertion); 
		AssrtAssertion node = new AssrtAssertion(source, f); 
		node = del(node, createDefaultDelegate());
		return node; 
	}

	@Override
	public AssrtArithExpr AssrtArithAnnotation(CommonTree source, AssrtArithFormula expr)
	{
		AssrtArithExpr node = new AssrtArithExpr(source, expr); 
		node = del(node, createDefaultDelegate());
		return node; 
	}

	@Override
	public AssrtModule AssrtModule(CommonTree source, ModuleDecl moddecl, List<ImportDecl<?>> imports, List<DataOrSigDeclNode<?>> data,
			List<ProtocolDecl<?>> protos, List<AssrtAssertDecl> asserts)
	{
		AssrtModule mod = new AssrtModule(source, moddecl, imports, data, protos, asserts);
		mod = del(mod, new AssrtModuleDel());
		return mod;
	}

	@Override
	public AssrtAssertDecl AssrtAssertDecl(CommonTree source, AssrtAssertNameNode name, List<AssrtSortNode> params, AssrtSortNode ret, AssrtSmtFormula<?> expr)
	{
		AssrtAssertDecl node = new AssrtAssertDecl(source, name, params, ret, expr); 
		node = del(node, createDefaultDelegate());
		return node; 
	}
}
