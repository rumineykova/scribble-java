package org.scribble.ext.assrt.ast;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.ImportDecl;
import org.scribble.ast.Module;
import org.scribble.ast.ModuleDecl;
import org.scribble.ast.NonProtocolDecl;
import org.scribble.ast.ProtocolDecl;
import org.scribble.ast.ScribNodeBase;
import org.scribble.del.ScribDel;
import org.scribble.main.ScribbleException;
import org.scribble.util.ScribUtil;
import org.scribble.visit.AstVisitor;

public class AssrtModule extends Module
{
	public final List<AssrtAssertDecl> asserts;

	public AssrtModule(CommonTree source, ModuleDecl moddecl, List<ImportDecl<?>> imports,
			List<NonProtocolDecl<?>> data, List<ProtocolDecl<?>> protos)
	{
		this(source, moddecl, imports, data, protos, Collections.emptyList());
	}
	
	public AssrtModule(CommonTree source, ModuleDecl moddecl, List<ImportDecl<?>> imports,
			List<NonProtocolDecl<?>> data, List<ProtocolDecl<?>> protos, List<AssrtAssertDecl> asserts)
	{
		super(source, moddecl, imports, data, protos);
		this.asserts = Collections.unmodifiableList(asserts);
	}

	@Override
	protected AssrtModule copy()
	{
		return new AssrtModule(this.source, this.moddecl, this.imports, this.data, this.protos, this.asserts);
	}
	
	@Override
	public AssrtModule clone(AstFactory af)
	{
		ModuleDecl moddecl = (ModuleDecl) this.moddecl.clone(af);
		List<ImportDecl<?>> imports = ScribUtil.cloneList(af, this.imports);
		List<NonProtocolDecl<?>> data = ScribUtil.cloneList(af, this.data);
		List<ProtocolDecl<?>> protos = ScribUtil.cloneList(af, this.protos);

		List<AssrtAssertDecl> asserts = ScribUtil.cloneList(af, this.asserts);
		
		return ((AssrtAstFactory) af).AssrtModule(this.source, moddecl, imports, data, protos, asserts);
	}
	
	@Override
	public AssrtModule reconstruct(ModuleDecl moddecl, List<ImportDecl<?>> imports, List<NonProtocolDecl<?>> data, List<ProtocolDecl<?>> protos)
	{
		throw new RuntimeException("[assert] Shouldn't get in here: " + this);
	}

	public AssrtModule reconstruct(ModuleDecl moddecl, List<ImportDecl<?>> imports, List<NonProtocolDecl<?>> data, List<ProtocolDecl<?>> protos,
			List<AssrtAssertDecl> asserts)
	{
		ScribDel del = del();
		AssrtModule m = new AssrtModule(this.source, moddecl, imports, data, protos, asserts);
		m = (AssrtModule) m.del(del);
		return m;
	}
	
	@Override
	public AssrtModule visitChildren(AstVisitor nv) throws ScribbleException
	{
		ModuleDecl moddecl = (ModuleDecl) visitChild(this.moddecl, nv);
		// class equality check probably too restrictive
		List<ImportDecl<?>> imports = ScribNodeBase.visitChildListWithClassEqualityCheck(this, this.imports, nv);
		List<NonProtocolDecl<?>> data = ScribNodeBase.visitChildListWithClassEqualityCheck(this, this.data, nv);
		List<ProtocolDecl<?>> protos = ScribNodeBase.visitChildListWithClassEqualityCheck(this, this.protos, nv);

		List<AssrtAssertDecl> asserts = ScribNodeBase.visitChildListWithClassEqualityCheck(this, this.asserts, nv);

		return reconstruct(moddecl, imports, data, protos, asserts);
	}

	@Override
	public String toString()
	{
		return moddecl.toString()
				+ this.imports.stream().map(id -> "\n" + id).collect(Collectors.joining(""))
				+ this.data.stream().map(dtd -> "\n" + dtd).collect(Collectors.joining(""))

				+ this.asserts.stream().map(ass -> "\n" + ass).collect(Collectors.joining(""))

				+ this.protos.stream().map(proto -> "\n" + proto).collect(Collectors.joining(""));
	}
}
