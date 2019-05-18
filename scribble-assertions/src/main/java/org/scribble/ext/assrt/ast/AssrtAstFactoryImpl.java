package org.scribble.ext.assrt.ast;

import java.util.Arrays;
import java.util.List;

import org.antlr.runtime.Token;
import org.scribble.ast.AstFactoryImpl;
import org.scribble.ast.ImportDecl;
import org.scribble.ast.ModuleDecl;
import org.scribble.ast.MsgNode;
import org.scribble.ast.NonProtoDecl;
import org.scribble.ast.NonRoleArgList;
import org.scribble.ast.NonRoleParamDeclList;
import org.scribble.ast.ProtoDecl;
import org.scribble.ast.RoleArgList;
import org.scribble.ast.RoleDeclList;
import org.scribble.ast.global.GProtoBlock;
import org.scribble.ast.local.LProtoBlock;
import org.scribble.ast.name.qualified.DataNameNode;
import org.scribble.ast.name.qualified.GProtoNameNode;
import org.scribble.ast.name.qualified.LProtoNameNode;
import org.scribble.ast.name.simple.RecVarNode;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.del.DelFactory;
import org.scribble.ext.assrt.ast.global.AssrtGConnect;
import org.scribble.ext.assrt.ast.global.AssrtGContinue;
import org.scribble.ext.assrt.ast.global.AssrtGDo;
import org.scribble.ext.assrt.ast.global.AssrtGMsgTransfer;
import org.scribble.ext.assrt.ast.global.AssrtGProtoHeader;
import org.scribble.ext.assrt.ast.global.AssrtGRecursion;
import org.scribble.ext.assrt.ast.local.AssrtLContinue;
import org.scribble.ext.assrt.ast.local.AssrtLDo;
import org.scribble.ext.assrt.ast.local.AssrtLProtoHeader;
import org.scribble.ext.assrt.ast.local.AssrtLRecursion;
import org.scribble.ext.assrt.ast.local.AssrtLReq;
import org.scribble.ext.assrt.ast.local.AssrtLSend;
import org.scribble.ext.assrt.ast.name.qualified.AssrtAssertNameNode;
import org.scribble.ext.assrt.ast.name.simple.AssrtIntVarNameNode;
import org.scribble.ext.assrt.ast.name.simple.AssrtSortNode;
import org.scribble.ext.assrt.core.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtSmtFormula;
import org.scribble.parser.ScribAntlrTokens;


// CHECKME: separate modified-del-only from new categories -- now: unify original and ext classes?  e.g., GMsgTransfer, AssrtGMsgTransfer
// Token/class correspondence should match that of ScribTreeAdaptor.create Token cases (except SimpleNameNodes)
public class AssrtAstFactoryImpl extends AstFactoryImpl
		implements AssrtAstFactory
{
	
	public AssrtAstFactoryImpl(ScribAntlrTokens tokens,
			DelFactory df)
	{
		super(tokens, df);
	}

	/**
	 *  Create ext node type in place of base
	 *  Parser returns a base token type, we create an ext node type but keep the base token
	 */
	
	/*@Override
	public Module Module(Token t, ModuleDecl moddecl, List<? extends ImportDecl<?>> imports,
			List<? extends NonProtoDecl<?>> data, List<? extends ProtoDecl<?>> protos)
	{
		t = newToken(t, AssrtScribbleParser.MODULE);  // CHECKME: token/class discrepancy OK?
		AssrtModule n = new AssrtModule(t);
		n.addScribChildren(moddecl, imports, data, protos);
		n.decorateDel(this.df);
		return n;
	}*/

	// Still used by parsing for empty annotation/assertion nodes -- but we return an Assrt node
	// Easier to make all global as Assrt nodes, to avoid cast checks in, e.g., AssrtGProtoDeclDel::leaveProjection (for GProtoHeader), and so all projections will be Assrt kinds only

	// Alternative is to make parsing return all as AssrtGProtoHeader directly
	@Override
	public AssrtGProtoHeader GProtocolHeader(Token t, GProtoNameNode name,
			RoleDeclList rs, NonRoleParamDeclList ps)
	{
		t = newToken(t, this.tokens.getType("GPROTOHEADER"));
		AssrtGProtoHeader n = new AssrtGProtoHeader(t);
		n.addScribChildren(name, ps, rs);
		n.decorateDel(this.df);  // Default, annots handled directly by AssrtAnnotationChecker Def enter/exit
		return n;
	}

	// Same pattern as for GProtoHeader
	// Non-annotated message transfers still created as AssrtGMessageTransfer -- null assertion, but AssrtGMessageTransferDel is still needed (why?)
	@Override
	public AssrtGMsgTransfer GMsgTransfer(Token t, MsgNode msg, RoleNode src,
			List<RoleNode> dsts)
	{
		t = newToken(t, this.tokens.getType("GMSGTRANSFER"));
		AssrtGMsgTransfer n = new AssrtGMsgTransfer(t);
		n.addScribChildren(msg, src, dsts);
		n.decorateDel(this.df);
		return n;
	}

	@Override 
	public AssrtGConnect GConnect(Token t, MsgNode msg, RoleNode src, RoleNode dst)  // Cf. AssrtAstFactoryImpl::GMsgTransfer
	{
		t = newToken(t, this.tokens.getType("GCONNECT"));
		AssrtGConnect n = new AssrtGConnect(t);
		n.addScribChildren(msg, src, Arrays.asList(dst));
		n.decorateDel(this.df);
		return n;
	}

	/*@Override
	public AssrtGContinue GContinue(Token t, RecVarNode rv)
	{
		t = newToken(t, this.tokens.getType("GCONTINUE"));
		AssrtGContinue n = new AssrtGContinue(t);
		n.addScribChildren(rv);
		n.decorateDel(this.df);
		return n;
	}*/

	@Override
	public AssrtGDo GDo(Token t, GProtoNameNode proto, NonRoleArgList as,
			RoleArgList rs)
	{
		t = newToken(t, this.tokens.getType("GDO"));
		AssrtGDo n = new AssrtGDo(t);
		n.addScribChildren(proto, as, rs);
		n.decorateDel(this.df);
		return n;
	}
	
	/*@Override
	public AssrtGRecursion GRecursion(Token t, RecVarNode rv, GProtoBlock block)
	{
		t = newToken(t, this.tokens.getType("GRECURSION"));
		AssrtGRecursion n = new AssrtGRecursion(t);
		n.addScribChildren(rv, block);
		n.decorateDel(this.df);
		return n;
	}*/
	

	/**
	 *  Explicitly creating new Assrt nodes -- but reusing base Token types (CHECKME) 
	 *  Cf. returning new node classes in place of existing
	 */

	@Override
	public AssrtIntVarNameNode AssrtIntVarNameNode(Token t, String text)
	{
		int type = this.tokens.getType("ID");
		t = newIdToken(t, text);
		AssrtIntVarNameNode n = new AssrtIntVarNameNode(type, t);  // Cf. Scribble.g, ID<...Node>[$ID]
		n.decorateDel(this.df);
		return n;
	}

	@Override
	public AssrtModule AssrtModule(Token t, ModuleDecl modd,
			List<? extends ImportDecl<?>> imports,
			List<? extends NonProtoDecl<?>> nonprotos,
			List<? extends ProtoDecl<?>> protos, List<AssrtAssertDecl> asserts)
	{
		t = newToken(t, this.tokens.getType("MODULE"));
		AssrtModule n = new AssrtModule(t);
		n.addScribChildren(modd, imports, nonprotos, protos, asserts);
		n.decorateDel(this.df);
		return n;
	}


	/**
	 *  Explicitly creating new Assrt nodes -- new Token types
	 */

	@Override
	public AssrtAssertion AssrtAssertion(Token t, AssrtBoolFormula bexpr)
	{
		t = newToken(t, this.tokens.getType("ASSRT_ASSERT"));
		AssrtAssertion n = new AssrtAssertion(t, bexpr);
		n.decorateDel(this.df);
		return n;
	}

	@Override
	public AssrtArithExpr AssrtArithAnnotation(Token t, AssrtArithFormula aexpr)
	{
		/*t = newToken(t, ...);
		AssrtArithExpr n = new AssrtArithExpr(t, aexpr);
		n.decorateDel(this.df);
		return n;*/
		throw new RuntimeException("[TODO] " + t);
	}

	@Override
	public AssrtAssertDecl AssrtAssertDecl(Token t, AssrtAssertNameNode name,
			List<AssrtSortNode> ps, AssrtSortNode ret, AssrtSmtFormula<?> expr)
	{
		/*t = newToken(t, ...);
		AssrtAssertDecl n = new AssrtAssertDecl(t, expr);
		n.decorateDel(this.df);
		return n;*/
		throw new RuntimeException("[TODO] : " + t);
	}

	@Override
	public AssrtGProtoHeader AssrtGProtoHeader(Token t, GProtoNameNode name,
			RoleDeclList rs, NonRoleParamDeclList ps, 
			AssrtAssertion ass, List<AssrtIntVarNameNode> avars,
			List<AssrtArithExpr> aexprs)  // FIXME: not actually how parsed
	{
		t = newToken(t, this.tokens.getType("ASSRT_GLOBALPROTOCOLHEADER"));
		AssrtGProtoHeader n = new AssrtGProtoHeader(t);
		n.addScribChildren(name, ps, rs, ass, avars, aexprs);
		n.decorateDel(this.df);
		return n;
	}
	
	// An "additional" category, does not "replace" an existing one -- cf. AssrtGMessageTransfer
	@Override
	public AssrtAnnotDataElem AssrtAnnotDataTypeElem(Token t,
			AssrtIntVarNameNode var, DataNameNode data)
	{
		t = newToken(t, this.tokens.getType("ASSRT_ANNOTPAYLOADELEM"));
		AssrtAnnotDataElem n = new AssrtAnnotDataElem(t);
		n.addScribChildren(var, data);
		n.decorateDel(this.df);
		return n;
	}

	@Override
	public AssrtGMsgTransfer AssrtGMsgTransfer(Token t, MsgNode msg, RoleNode src,
			List<RoleNode> dsts, AssrtAssertion ass)
	{
		t = newToken(t, this.tokens.getType("ASSRT_GLOBALMESSAGETRANSFER"));
		AssrtGMsgTransfer n = new AssrtGMsgTransfer(t);
		if (dsts.size() > 1)
		{
			throw new RuntimeException(
					"[TODO] Multiple dest roles for " + getClass() + ":\n\t" + dsts);
		}
		n.addScribChildren(msg, src, dsts.get(0), ass);
		n.decorateDel(this.df);
		return n;
	}

	@Override
	public AssrtGConnect AssrtGConnect(Token t, MsgNode msg, RoleNode src,
			RoleNode dst, AssrtAssertion ass)
	{
		t = newToken(t, this.tokens.getType("ASSRT_GLOBALCONNECT"));
		AssrtGConnect n = new AssrtGConnect(t);
		n.addScribChildren(msg, src, dst, ass);
		n.decorateDel(this.df);
		return n;
	}

	@Override
	public AssrtGContinue AssrtGContinue(Token t, RecVarNode rv,
			List<AssrtArithExpr> aexprs)
	{
		/*t = newToken(t, ...);
		AssrtGContinue n = new AssrtGContinue(t);
		n.addScribChildren(rv, aexprs);
		n.decorateDel(this.df);
		return n;*/
		throw new RuntimeException("[TODO] : " + t);
	}

	@Override
	public AssrtGDo AssrtGDo(Token t, GProtoNameNode proto, NonRoleArgList as,
			RoleArgList rs, List<AssrtArithExpr> aexprs)
	{
		t = newToken(t, this.tokens.getType("ASSRT_GLOBALDO"));
		AssrtGDo n = new AssrtGDo(t);
		n.addScribChildren(proto, as, rs, aexprs);
		n.decorateDel(this.df);
		return n;
	}

	@Override
	public AssrtGRecursion AssrtGRecursion(Token t, RecVarNode rv,
			GProtoBlock block, AssrtAssertion ass, List<AssrtIntVarNameNode> avars,
			List<AssrtArithExpr> aexprs)  // FIXME: not actually how parsed
	{
		/*t = newToken(t, ...);
		AssrtGRecursion n = new AssrtGRecursion(t);
		n.addScribChildren(rv, block, ass, avars, aexprs);
		n.decorateDel(this.df);
		return n;*/
		throw new RuntimeException("[TODO] : " + t);
	}

	@Override
	public AssrtLProtoHeader AssrtLProtoHeader(Token t, LProtoNameNode name,
			RoleDeclList rs, NonRoleParamDeclList ps,
			List<AssrtIntVarNameNode> avars, List<AssrtArithExpr> aexprs,
			AssrtAssertion ass)  // FIXME: not actually how parsed
	{
		t = newToken(t, this.tokens.getType("ASSRT_LOCALPROTOCOLHEADER"));
		AssrtLProtoHeader n = new AssrtLProtoHeader(t);
		n.addScribChildren(name, ps, rs, ass, avars, aexprs);
		n.decorateDel(this.df);
		return n;
	}

	@Override
	public AssrtLSend AssrtLSend(Token t, MsgNode msg, RoleNode self,
			List<RoleNode> dsts, AssrtAssertion ass)
	{
		t = newToken(t, this.tokens.getType("ASSRT_LOCALSEND"));
		AssrtLSend n = new AssrtLSend(t);
		if (dsts.size() > 1)
		{
			throw new RuntimeException(
					"[TODO] Multiple dest roles for " + getClass() + ":\n\t" + dsts);
		}
		n.addScribChildren(msg, self, Arrays.asList(dsts.get(0)));
		n.decorateDel(this.df);
		return n;
	}

	@Override
	public AssrtLReq AssrtLReq(Token t, MsgNode msg, RoleNode self, RoleNode dst,
			AssrtAssertion ass)
	{
		t = newToken(t, this.tokens.getType("ASSRT_LOCALREQ"));
		AssrtLReq n = new AssrtLReq(t);
		n.addScribChildren(msg, self, Arrays.asList(dst));
		n.decorateDel(this.df);
		return n;
	}

	@Override
	public AssrtLContinue AssrtLContinue(Token t, RecVarNode rv,
			List<AssrtArithExpr> aexprs)
	{
		/*t = newToken(t, ...);
		AssrtGContinue n = new AssrtGContinue(t);
		n.addScribChildren(rv, aexprs);
		n.decorateDel(this.df);
		return n;*/
		throw new RuntimeException("[TODO] : " + t);
	}

	@Override
	public AssrtLDo AssrtLDo(Token t, RoleArgList rs, NonRoleArgList as,
			LProtoNameNode proto, List<AssrtArithExpr> aexprs)
	{
		t = newToken(t, this.tokens.getType("ASSRT_LOCALDO"));
		AssrtLDo n = new AssrtLDo(t);
		n.addScribChildren(proto, as, rs, aexprs);
		n.decorateDel(this.df);
		return n;
	}

	@Override
	public AssrtLRecursion AssrtLRecursion(Token t, RecVarNode rv, LProtoBlock block,
			List<AssrtIntVarNameNode> avars, List<AssrtArithExpr> aexprs,
			AssrtAssertion ass)  // FIXME: not actually how parsed
	{
		/*t = newToken(t, ...);
		AssrtLRecursion n = new AssrtLRecursion(t);
		n.addScribChildren(rv, block, ass, avars, aexprs);
		n.decorateDel(this.df);
		return n;*/
		throw new RuntimeException("[TODO] : " + t);
	}
}
