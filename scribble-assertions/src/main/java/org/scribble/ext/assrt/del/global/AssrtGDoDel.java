package org.scribble.ext.assrt.del.global;

import org.scribble.ast.ScribNode;
import org.scribble.ast.name.qualified.ProtoNameNode;
import org.scribble.core.type.kind.Global;
import org.scribble.del.global.GDoDel;
import org.scribble.ext.assrt.ast.global.AssrtGDo;
import org.scribble.util.ScribException;
import org.scribble.visit.NameDisambiguator;

public class AssrtGDoDel extends GDoDel
{
	// Convert all visible names to full names for protocol inlining: otherwise could get clashes if directly inlining external visible names under the root modulecontext
	// Not done in G/LProtoNameNodeDel because it's only for do-targets that this is needed (cf. ProtoHeader)
	@Override
	public ScribNode leaveDisambiguation(ScribNode child,
			NameDisambiguator disamb, ScribNode visited) throws ScribException
	{
		AssrtGDo doo = (AssrtGDo) visited;
		ProtoNameNode<Global> proto = disambProtoNameNode(disamb,
				doo.getProtocolNameChild());  // Doesn't keep the original namenode del
		return doo.reconstruct(proto, doo.getRoleListChild(),
				doo.getNonRoleListChild(), doo.getAnnotExprChildren());
	}
}











/*
	
	@Override
	public GDo leaveProjection(ScribNode parent, ScribNode child, Projector proj, ScribNode visited) throws ScribException //throws ScribException
	{
		//GDo gd = (GDo) visited;
		AssrtGDo gd = (AssrtGDo) visited;

		Role popped = proj.popSelf();
		Role self = proj.peekSelf();
		LDo ld = null;
		if (gd.roles.getRoles().contains(self))
		{
			ModuleContext mc = proj.getModuleContext();
			LProtoNameNode target = Projector.makeProjectedFullNameNode(proj.job.af, gd.proto.getSource(), gd.getTargetProtoDeclFullName(mc), popped);

			//projection = gd.project(proj.job.af, self, target);
			ld = gd.project(proj.job.af, self, target, //gd.annot);
					gd.exprs);
			
			// FIXME: do guarded recursive subprotocol checking (i.e. role is used during chain) in reachability checking? -- required role-usage makes local choice subject inference easier, but is restrictive (e.g. proto(A, B, C) { choice at A {A->B.do Proto(A,B,C)} or {A->B.B->C} }))
		}
		proj.pushEnv(proj.popEnv().setProjection(ld));

		//return (GDo) GSimpleInteractionNodeDel.super.leaveProjection(parent, child, proj, gd);
		return (GDo) ScribDelBase.popAndSetVisitorEnv(this, proj, visited);
	}
*/
