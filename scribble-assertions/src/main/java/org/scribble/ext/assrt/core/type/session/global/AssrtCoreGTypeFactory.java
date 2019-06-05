package org.scribble.ext.assrt.core.type.session.global;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.core.type.kind.Global;
import org.scribble.core.type.kind.NonRoleParamKind;
import org.scribble.core.type.name.ProtoName;
import org.scribble.core.type.name.RecVar;
import org.scribble.core.type.name.Role;
import org.scribble.core.type.session.Arg;
import org.scribble.core.type.session.Msg;
import org.scribble.core.type.session.global.GContinue;
import org.scribble.core.type.session.global.GDisconnect;
import org.scribble.core.type.session.global.GDo;
import org.scribble.core.type.session.global.GMessageTransfer;
import org.scribble.core.type.session.global.GRecursion;
import org.scribble.core.type.session.global.GSeq;
import org.scribble.core.type.session.global.GType;
import org.scribble.core.type.session.global.GTypeFactoryImpl;
import org.scribble.ext.assrt.core.type.formula.AssrtAFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;
import org.scribble.ext.assrt.core.type.name.AssrtIntVar;
import org.scribble.ext.assrt.core.type.session.AssrtCoreMsg;


// CHECKME: override pattern not ideal, but maybe OK due to the "language shift" -- also no Impl suffix
public class AssrtCoreGTypeFactory extends GTypeFactoryImpl
{
	
	public AssrtCoreGChoice AssrtCoreGChoice(CommonTree source, Role src,
			AssrtCoreGActionKind kind, Role dst,
			Map<AssrtCoreMsg, AssrtCoreGType> cases)
	{
		return new AssrtCoreGChoice(source, src, kind, dst, cases);
	}
	
	public AssrtCoreGRec AssrtCoreGRec(CommonTree source, RecVar rv,
			AssrtCoreGType body, LinkedHashMap<AssrtIntVar, AssrtAFormula> svars,
			AssrtBFormula ass)
	{
		return new AssrtCoreGRec(source, rv, body, svars, ass);
	}
	
	public AssrtCoreGRecVar AssrtCoreGRecVar(CommonTree source, RecVar rv,
			List<AssrtAFormula> sexprs)
	{
		return new AssrtCoreGRecVar(source, rv, sexprs);
	}

	public AssrtCoreGEnd AssrtCoreGEnd()
	{
		return AssrtCoreGEnd.END;
	}

	@Override
	public org.scribble.core.type.session.global.GChoice GChoice(
			CommonTree source, Role subj,
			List<org.scribble.core.type.session.global.GSeq> blocks)
	{
		throw new RuntimeException(
				"Deprecated for " + getClass() + ":\n\t" + source);
	}

	@Override
	public org.scribble.core.type.session.global.GConnect GConnect(
			CommonTree source, Role src, Msg msg, Role dst)
	{
		throw new RuntimeException(
				"Deprecated for " + getClass() + ":\n\t" + source);
	}

	@Override
	public GContinue GContinue(
			CommonTree source, RecVar rv)
	{
		throw new RuntimeException(
				"Deprecated for " + getClass() + ":\n\t" + source);
	}

	@Override
	public GDisconnect GDisconnect(
			CommonTree source, Role left, Role right)
	{
		throw new RuntimeException(
				"Deprecated for " + getClass() + ":\n\t" + source);
	}

	@Override
	public GDo GDo(CommonTree source,
			ProtoName<Global> proto, List<Role> roles,
			List<Arg<? extends NonRoleParamKind>> args)
	{
		throw new RuntimeException(
				"Deprecated for " + getClass() + ":\n\t" + source);
	}

	@Override
	public GMessageTransfer GMessageTransfer(
			CommonTree source, Role src, Msg msg, Role dst)
	{
		throw new RuntimeException(
				"Deprecated for " + getClass() + ":\n\t" + source);
	}

	@Override
	public GRecursion GRecursion(CommonTree source, RecVar rv, GSeq body)
	{
		throw new RuntimeException(
				"Deprecated for " + getClass() + ":\n\t" + source);
	}

	@Override
	public GSeq GSeq(CommonTree source,
			List<GType> elems)
	{
		throw new RuntimeException(
				"Deprecated for " + getClass() + ":\n\t" + source);
	}
}
