package org.scribble.ext.assrt.ast;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.runtime.Token;
import org.scribble.ast.ImportDecl;
import org.scribble.ast.Module;
import org.scribble.ast.ModuleDecl;
import org.scribble.ast.NonProtoDecl;
import org.scribble.ast.ProtoDecl;
import org.scribble.ast.ScribNodeBase;
import org.scribble.del.DelFactory;
import org.scribble.ext.assrt.core.type.name.AssrtAssertName;
import org.scribble.ext.assrt.del.AssrtDelFactory;
import org.scribble.util.ScribException;
import org.scribble.visit.AstVisitor;

public class AssrtModule extends Module
{
	// ScribTreeAdaptor#create constructor
	public AssrtModule(Token t)
	{
		super(t);
	}

	// Tree#dupNode constructor
	protected AssrtModule(AssrtModule node)
	{
		super(node);
	}

	public List<AssrtAssertDecl> getAssertDeclChildren()
	{
		return getMemberChildren(x -> x instanceof AssrtAssertDecl,
				x -> (AssrtAssertDecl) x);
	}

	// "add", not "set"
	public void addScribChildren(ModuleDecl moddecl,
			List<? extends ImportDecl<?>> imports,
			List<? extends NonProtoDecl<?>> data, List<? extends ProtoDecl<?>> protos,
			List<AssrtAssertDecl> asserts)
	{
		// Cf. above getters and Scribble.g children order
		//super.addScribChildren(moddecl, imports, data, protos);  // No: need asserts before protos
		addChild(moddecl);
		addChildren(imports);
		addChildren(data);
		addChildren(asserts);
		addChildren(protos);
	}
	
	@Override
	public AssrtModule dupNode()
	{
		return new AssrtModule(this);
	}
	
	@Override
	public void decorateDel(DelFactory df)
	{
		((AssrtDelFactory) df).AssrtModule(this);
	}
	
	@Override
	protected Module reconstruct(ModuleDecl moddecl, List<ImportDecl<?>> imports,
			List<NonProtoDecl<?>> data, List<ProtoDecl<?>> protos)
	{
		throw new RuntimeException(
				"[assert] Deprecated for " + getClass() + ": " + this);
	}

	protected AssrtModule reconstruct(ModuleDecl moddecl, List<ImportDecl<?>> imports,
			List<NonProtoDecl<?>> data, List<ProtoDecl<?>> protos, List<AssrtAssertDecl> asserts)
	{
		AssrtModule dup = dupNode();
		dup.addScribChildren(moddecl, imports, data, protos, asserts);
		dup.setDel(del());  // No copy
		return dup;
	}
	
	@Override
	public AssrtModule visitChildren(AstVisitor v) throws ScribException
	{
		Module sup = super.visitChildren(v);  // CHECKME: inefficient?
		List<AssrtAssertDecl> asserts = ScribNodeBase
				.visitChildListWithClassEqualityCheck(this, getAssertDeclChildren(), v);
		return reconstruct(sup.getModuleDeclChild(), sup.getImportDeclChildren(),
				sup.getNonProtoDeclChildren(), sup.getProtoDeclChildren(), asserts);
	}

	// Cf., e.g., getNonProtoDeclChild 
	public AssrtAssertDecl getAssertDeclChild(AssrtAssertName simpname)
	{
		Optional<AssrtAssertDecl> res = getAssertDeclChildren().stream()
				.filter(x -> x.getDeclName().equals(simpname))
				.findFirst();  // No duplication check, rely on WF (or currently ModuleContext building?)
		if (!res.isPresent())
		{
			throw new RuntimeException("Assert decl not found: " + simpname);
		}
		return res.get();
	}

	@Override
	public String toString()
	{
		return getModuleDeclChild().toString()
				+ getImportDeclChildren().stream().map(x -> "\n" + x)
						.collect(Collectors.joining(""))
				+ getNonProtoDeclChildren().stream().map(x -> "\n" + x)
						.collect(Collectors.joining(""))
				+ getAssertDeclChildren().stream().map(x -> "\n" + x)
						.collect(Collectors.joining(""))
				+ getProtoDeclChildren().stream().map(x -> "\n" + x)
						.collect(Collectors.joining(""));
	}
}












/*
	public final List<AssrtAssertDecl> asserts;

	public AssrtModule(CommonTree source, ModuleDecl moddecl, List<ImportDecl<?>> imports,
			List<DataOrSigDeclNode<?>> data, List<ProtocolDecl<?>> protos)
	{
		this(source, moddecl, imports, data, protos, Collections.emptyList());
	}
	
	public AssrtModule(CommonTree source, ModuleDecl moddecl, List<ImportDecl<?>> imports,
			List<DataOrSigDeclNode<?>> data, List<ProtocolDecl<?>> protos, List<AssrtAssertDecl> asserts)
	{
		super(source, moddecl, imports, data, protos);
		this.asserts = Collections.unmodifiableList(asserts);
	}
	
	public List<AssrtAssertDecl> getAssertDecls()
	{
		return this.asserts;
	}
//*/
