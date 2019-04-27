package org.scribble.core.type.session.local;

import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.core.type.kind.Local;
import org.scribble.core.type.kind.NonRoleParamKind;
import org.scribble.core.type.name.LProtoName;
import org.scribble.core.type.name.RecVar;
import org.scribble.core.type.name.Role;
import org.scribble.core.type.session.Arg;
import org.scribble.core.type.session.Msg;
import org.scribble.core.type.session.SType;

public class LTypeFactoryImpl implements LTypeFactory
{

	@Override
	public LAcc LAcc(CommonTree source, Role src, Msg msg)
	{
		return new LAcc(source, src, msg);
	}

	@Override
	public LChoice LChoice(CommonTree source, Role subj, List<LSeq> blocks)
	{
		return new LChoice(source, subj, blocks);
	}

	@Override
	public LContinue LContinue(CommonTree source, RecVar recvar)
	{
		return new LContinue(source, recvar);
	}

	@Override
	public LDisconnect LDisconnect(CommonTree source, Role peer)
	{
		return new LDisconnect(source, peer);
	}	

	@Override
	public LDo LDo(CommonTree source, LProtoName proto, List<Role> roles, 
			List<Arg<? extends NonRoleParamKind>> args)
	{
		return new LDo(source, proto, roles, args);
	}

	@Override
	public LRecursion LRecursion(CommonTree source, RecVar recvar, LSeq body)
	{
		return new LRecursion(source, recvar, body);
	}

	@Override
	public LRecv LRecv(CommonTree source, Role src, Msg msg)
	{
		return new LRecv(source, src, msg);
	}

	@Override
	public LReq LReq(CommonTree source, Msg msg, Role dst)
	{
		return new LReq(source, msg, dst);
	}

	@Override
	public LSend LSend(CommonTree source, Msg msg,
			Role dst)
	{
		return new LSend(source, msg, dst);
	}

	@Override
	public LSeq LSeq(CommonTree source, List<? extends SType<Local, LSeq>> elems)
	{
		return new LSeq(source, elems);
	}
}
