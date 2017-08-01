package org.scribble.ext.assrt.ast;

import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactoryImpl;
import org.scribble.ast.MessageNode;
import org.scribble.ast.NonRoleParamDeclList;
import org.scribble.ast.ProtocolDecl;
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
import org.scribble.ext.assrt.ast.global.AssrtGMessageTransfer;
import org.scribble.ext.assrt.ast.global.AssrtGProtocolHeader;
import org.scribble.ext.assrt.ast.global.AssrtGRecursion;
import org.scribble.ext.assrt.ast.local.AssrtLConnect;
import org.scribble.ext.assrt.ast.local.AssrtLContinue;
import org.scribble.ext.assrt.ast.local.AssrtLProtocolHeader;
import org.scribble.ext.assrt.ast.local.AssrtLRecursion;
import org.scribble.ext.assrt.ast.local.AssrtLSend;
import org.scribble.ext.assrt.ast.name.simple.AssrtVarNameNode;
import org.scribble.ext.assrt.del.AssrtAnnotDataTypeElemDel;
import org.scribble.ext.assrt.del.global.AssrtGChoiceDel;
import org.scribble.ext.assrt.del.global.AssrtGConnectDel;
import org.scribble.ext.assrt.del.global.AssrtGContinueDel;
import org.scribble.ext.assrt.del.global.AssrtGMessageTransferDel;
import org.scribble.ext.assrt.del.global.AssrtGProtocolBlockDel;
import org.scribble.ext.assrt.del.global.AssrtGProtocolDeclDel;
import org.scribble.ext.assrt.del.global.AssrtGProtocolDefDel;
import org.scribble.ext.assrt.del.global.AssrtGRecursionDel;
import org.scribble.ext.assrt.del.local.AssrtLConnectDel;
import org.scribble.ext.assrt.del.local.AssrtLContinueDel;
import org.scribble.ext.assrt.del.local.AssrtLProjectionDeclDel;
import org.scribble.ext.assrt.del.local.AssrtLProtocolBlockDel;
import org.scribble.ext.assrt.del.local.AssrtLProtocolDefDel;
import org.scribble.ext.assrt.del.local.AssrtLReceiveDel;
import org.scribble.ext.assrt.del.local.AssrtLRecursionDel;
import org.scribble.ext.assrt.del.local.AssrtLSendDel;
import org.scribble.ext.assrt.del.name.AssrtAmbigNameNodeDel;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.kind.AssrtVarNameKind;
import org.scribble.type.kind.Kind;
import org.scribble.type.name.GProtocolName;
import org.scribble.type.name.Role;


// FIXME: separate modified-del-only from new categories
public class AssrtAstFactoryImpl extends AstFactoryImpl implements AssrtAstFactory
{
	
	// Instantiating existing node classes with new dels

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
	
	
	// Returning new node classes in place of existing

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
	public AssrtGMessageTransfer GMessageTransfer(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests)
	{
		AssrtGMessageTransfer gmt = new AssrtGMessageTransfer(source, src, msg, dests);
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
	

	// Explicitly creating new Assrt nodes

	@Override
	public AssrtGProtocolHeader AssrtGProtocolHeader(CommonTree source, GProtocolNameNode name, RoleDeclList roledecls, NonRoleParamDeclList paramdecls, AssrtAssertion ass)
	{
		AssrtGProtocolHeader gpb = new AssrtGProtocolHeader(source, name, roledecls, paramdecls, ass);
		gpb = del(gpb, createDefaultDelegate());  // Annots handled directly by AssrtAnnotationChecker Def enter/exit
		return gpb;
	}

	@Override
	public AssrtGMessageTransfer AssrtGMessageTransfer(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests, AssrtAssertion assertion)
	{
		AssrtGMessageTransfer gmt = new AssrtGMessageTransfer(source, src, msg, dests, assertion);
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
	public AssrtGRecursion AssrtGRecursion(CommonTree source, RecVarNode recvar, GProtocolBlock block, AssrtAssertion ass)
	{
		AssrtGRecursion gr = new AssrtGRecursion(source, recvar, block, ass);
		gr = del(gr, new AssrtGRecursionDel());
		return gr;
	}

	@Override
	public AssrtGContinue AssrtGContinue(CommonTree source, RecVarNode recvar, AssrtAssertion ass)
	{
		AssrtGContinue gc = new AssrtGContinue(source, recvar, ass);
		gc = del(gc, new AssrtGContinueDel());
		return gc;
	}

	@Override
	public AssrtLProtocolHeader AssrtLProtocolHeader(CommonTree source, LProtocolNameNode name, RoleDeclList roledecls, NonRoleParamDeclList paramdecls, AssrtAssertion ass)
	{
		AssrtLProtocolHeader lph = new AssrtLProtocolHeader(source, name, roledecls, paramdecls, ass);
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
	public AssrtLConnect AssrtLConnect(CommonTree source, RoleNode src, MessageNode msg, RoleNode dest, AssrtAssertion ass)
	{
		AssrtLConnect ls = new AssrtLConnect(source, src, msg, dest, ass);
		ls = del(ls, new AssrtLConnectDel());
		return ls;
	}

	@Override
	public AssrtLRecursion AssrtLRecursion(CommonTree source, RecVarNode recvar, LProtocolBlock block, AssrtAssertion ass)
	{
		AssrtLRecursion lr = new AssrtLRecursion(source, recvar, block, ass);
		lr = del(lr, new AssrtLRecursionDel());
		return lr;
	}

	@Override
	public AssrtLContinue AssrtLContinue(CommonTree source, RecVarNode recvar, AssrtAssertion ass)
	{
		AssrtLContinue lc = new AssrtLContinue(source, recvar, ass);
		lc = del(lc, new AssrtLContinueDel());
		return lc;
	}
	
	// An "additional" category, does not "replace" an existing one -- cf. AssrtGMessageTransfer
	@Override
	public AssrtAnnotDataTypeElem AssrtAnnotDataTypeElem(CommonTree source, AssrtVarNameNode var, DataTypeNode data)
	{
		AssrtAnnotDataTypeElem de = new AssrtAnnotDataTypeElem(source, var, data);
		de = del(de, new AssrtAnnotDataTypeElemDel());
		return de;
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
	public <K extends Kind> NameNode<K> SimpleNameNode(CommonTree source, K kind, String identifier)
	{
		if (kind.equals(AssrtVarNameKind.KIND))
		{
			NameNode<? extends Kind> snn = new AssrtVarNameNode(source, identifier);
			snn = del(snn, createDefaultDelegate()); 
			return castNameNode(kind, snn);
		}

		return super.SimpleNameNode(source, kind, identifier);
	}
}
