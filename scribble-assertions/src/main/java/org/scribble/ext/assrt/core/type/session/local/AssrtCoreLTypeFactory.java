package org.scribble.ext.assrt.core.type.session.local;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.core.type.kind.Local;
import org.scribble.core.type.kind.NonRoleParamKind;
import org.scribble.core.type.name.ProtoName;
import org.scribble.core.type.name.RecVar;
import org.scribble.core.type.name.Role;
import org.scribble.core.type.session.Arg;
import org.scribble.core.type.session.Msg;
import org.scribble.core.type.session.local.LAcc;
import org.scribble.core.type.session.local.LChoice;
import org.scribble.core.type.session.local.LContinue;
import org.scribble.core.type.session.local.LDisconnect;
import org.scribble.core.type.session.local.LDo;
import org.scribble.core.type.session.local.LRecursion;
import org.scribble.core.type.session.local.LRecv;
import org.scribble.core.type.session.local.LReq;
import org.scribble.core.type.session.local.LSend;
import org.scribble.core.type.session.local.LSeq;
import org.scribble.core.type.session.local.LType;
import org.scribble.core.type.session.local.LTypeFactoryImpl;
import org.scribble.ext.assrt.core.type.formula.AssrtAFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;
import org.scribble.ext.assrt.core.type.name.AssrtDataVar;
import org.scribble.ext.assrt.core.type.session.AssrtCoreMsg;

// CHECKME: override pattern not ideal, but maybe OK due to the "language shift" -- also no Impl suffix
public class AssrtCoreLTypeFactory extends LTypeFactoryImpl
{

	public AssrtCoreLChoice AssrtCoreLChoice(CommonTree source, Role peer,
			AssrtCoreLActionKind kind, Map<AssrtCoreMsg, AssrtCoreLType> cases)
	{
		return new AssrtCoreLChoice(source, peer, kind, cases);
	}

	public AssrtCoreLRec AssrtCoreLRec(CommonTree source, RecVar rv,
			LinkedHashMap<AssrtDataVar, AssrtAFormula> avars,
			AssrtCoreLType body, AssrtBFormula bform)
	{
		return new AssrtCoreLRec(source, rv, avars, body, bform);
	}

	public AssrtCoreLRecVar AssrtCoreLRecVar(CommonTree source, RecVar rv,
			List<AssrtAFormula> aforms)
	{
		return new AssrtCoreLRecVar(source, rv, aforms);
	}

	public AssrtCoreLEnd AssrtCoreLEnd()
	{
		return AssrtCoreLEnd.END;
	}

	@Override
	public LAcc LAcc(CommonTree source, Role src, Msg msg)
	{
		throw new RuntimeException(
				"Deprecated for " + getClass() + ":\n\t" + source);
	}

	@Override
	public LChoice LChoice(CommonTree source, Role subj, List<LSeq> blocks)
	{
		throw new RuntimeException(
				"Deprecated for " + getClass() + ":\n\t" + source);
	}

	@Override
	public LContinue LContinue(CommonTree source, RecVar recvar)
	{
		throw new RuntimeException(
				"Deprecated for " + getClass() + ":\n\t" + source);
	}

	@Override
	public LDisconnect LDisconnect(CommonTree source, Role peer)
	{
		throw new RuntimeException(
				"Deprecated for " + getClass() + ":\n\t" + source);
	}

	@Override
	public LDo LDo(CommonTree source, ProtoName<Local> proto, List<Role> roles,
			List<Arg<? extends NonRoleParamKind>> args)
	{
		throw new RuntimeException(
				"Deprecated for " + getClass() + ":\n\t" + source);
	}

	@Override
	public LRecursion LRecursion(CommonTree source, RecVar recvar, LSeq body)
	{
		throw new RuntimeException(
				"Deprecated for " + getClass() + ":\n\t" + source);
	}

	@Override
	public LRecv LRecv(CommonTree source, Role src, Msg msg)
	{
		throw new RuntimeException(
				"Deprecated for " + getClass() + ":\n\t" + source);
	}

	@Override
	public LReq LReq(CommonTree source, Msg msg, Role dst)
	{
		throw new RuntimeException(
				"Deprecated for " + getClass() + ":\n\t" + source);
	}

	@Override
	public LSend LSend(CommonTree source, Msg msg, Role dst)
	{
		throw new RuntimeException(
				"Deprecated for " + getClass() + ":\n\t" + source);
	}

	@Override
	public LSeq LSeq(CommonTree source, List<LType> elems)
	{
		throw new RuntimeException(
				"Deprecated for " + getClass() + ":\n\t" + source);
	}
}
