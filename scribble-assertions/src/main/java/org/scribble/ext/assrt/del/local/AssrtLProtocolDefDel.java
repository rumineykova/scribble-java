package org.scribble.ext.assrt.del.local;

import java.util.Arrays;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.ScribNode;
import org.scribble.ast.local.LInteractionSeq;
import org.scribble.ast.local.LProjectionDecl;
import org.scribble.ast.local.LProtocolBlock;
import org.scribble.ast.local.LProtocolDecl;
import org.scribble.ast.local.LProtocolDef;
import org.scribble.ast.name.simple.RecVarNode;
import org.scribble.del.ScribDelBase;
import org.scribble.del.local.LProtocolDefDel;
import org.scribble.ext.assrt.ast.AssrtAssertion;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.ext.assrt.ast.local.AssrtLProtocolHeader;
import org.scribble.ext.assrt.ast.local.AssrtLRecursion;
import org.scribble.main.ScribbleException;
import org.scribble.type.SubprotocolSig;
import org.scribble.type.kind.RecVarKind;
import org.scribble.visit.ProtocolDefInliner;
import org.scribble.visit.env.InlineProtocolEnv;

public class AssrtLProtocolDefDel extends LProtocolDefDel
{
	public AssrtLProtocolDefDel()
	{

	}

	@Override
	protected AssrtLProtocolDefDel copy()
	{
		AssrtLProtocolDefDel copy = new AssrtLProtocolDefDel();
		copy.inlined = this.inlined;
		return copy;
	}

	@Override
	public ScribNode leaveProtocolInlining(ScribNode parent, ScribNode child, ProtocolDefInliner dinlr, ScribNode visited) throws ScribbleException
	{
		CommonTree blame = ((LProtocolDecl) parent).header.getSource();  // Cf., GProtocolDefDel
		SubprotocolSig subsig = dinlr.peekStack();
		LProtocolDef def = (LProtocolDef) visited;
		LProtocolBlock block = (LProtocolBlock) ((InlineProtocolEnv) def.block.del().env()).getTranslation();	
		RecVarNode recvar = (RecVarNode) dinlr.job.af.SimpleNameNode(blame,  // The parent do would probably be the better source for blame
				RecVarKind.KIND, dinlr.getSubprotocolRecVar(subsig).toString());

		//LRecursion rec = inl.job.af.LRecursion(blame, recvar, block);
		if (!(parent instanceof LProjectionDecl)) // FIXME: factor out interface for annoy LProtocolDecl and LProjectionDecl
		{
			throw new RuntimeException("[assrt] Shouldn't get in here: " + parent.getClass());
		}
		LProjectionDecl lpd = (LProjectionDecl) parent;
		AssrtAssertion ass = ((AssrtLProtocolHeader) lpd.getHeader()).ass;
		AssrtLRecursion rec = ((AssrtAstFactory) dinlr.job.af).AssrtLRecursion(blame, recvar, block, ass);  // FIXME: factor out better?

		LInteractionSeq lis = dinlr.job.af.LInteractionSeq(blame, Arrays.asList(rec));
		LProtocolDef inlined = dinlr.job.af.LProtocolDef(def.getSource(), dinlr.job.af.LProtocolBlock(blame, lis));
		dinlr.pushEnv(dinlr.popEnv().setTranslation(inlined));
		AssrtLProtocolDefDel copy = (AssrtLProtocolDefDel) setInlinedProtocolDef(inlined);  // Created using copy
		return (LProtocolDef) ScribDelBase.popAndSetVisitorEnv(this, dinlr, (LProtocolDef) def.del(copy));
	}
}
