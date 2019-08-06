/**
 * Copyright 2008 The Scribble Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.scribble.ext.assrt.core.job;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.scribble.core.job.Core;
import org.scribble.core.job.CoreArgs;
import org.scribble.core.job.CoreContext;
import org.scribble.core.lang.global.GProtocol;
import org.scribble.core.lang.local.LProjection;
import org.scribble.core.model.ModelFactory;
import org.scribble.core.model.endpoint.EModelFactory;
import org.scribble.core.model.global.SGraph;
import org.scribble.core.model.global.SModelFactory;
import org.scribble.core.type.kind.Global;
import org.scribble.core.type.name.GProtoName;
import org.scribble.core.type.name.ModuleName;
import org.scribble.core.type.name.ProtoName;
import org.scribble.core.type.name.Role;
import org.scribble.core.type.session.STypeFactory;
import org.scribble.core.visit.STypeVisitorFactory;
import org.scribble.core.visit.STypeVisitorFactoryImpl;
import org.scribble.core.visit.global.GTypeVisitorFactoryImpl;
import org.scribble.ext.assrt.core.lang.global.AssrtCoreGProtocol;
import org.scribble.ext.assrt.core.model.endpoint.AssrtCoreEModelFactoryImpl;
import org.scribble.ext.assrt.core.model.global.AssrtCoreSGraph;
import org.scribble.ext.assrt.core.model.global.AssrtCoreSModelFactory;
import org.scribble.ext.assrt.core.model.global.AssrtCoreSModelFactoryImpl;
import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;
import org.scribble.ext.assrt.core.type.name.AssrtIntVar;
import org.scribble.ext.assrt.core.visit.local.AssrtCoreLTypeVisitorFactoryImpl;
import org.scribble.ext.assrt.job.AssrtJob.Solver;
import org.scribble.ext.assrt.util.Z3Wrapper;
import org.scribble.util.ScribException;


// A "compiler job" front-end that supports operations comprising visitor passes over the AST and/or local/global models
public class AssrtCore extends Core
{
	public AssrtCore(ModuleName mainFullname, CoreArgs args, Set<GProtocol> imeds,
			STypeFactory tf)
	{
		super(mainFullname, args, imeds, tf);
	}
	
	// A Scribble extension should override newSTypeVisitorFactory/ModelFactory as appropriate
	@Override
	protected STypeVisitorFactory newSTypeVisitorFactory()
	{
		return new STypeVisitorFactoryImpl(new GTypeVisitorFactoryImpl(),
				new AssrtCoreLTypeVisitorFactoryImpl());
	}
	
	// A Scribble extension should override newSTypeVisitorFactory/ModelFactory as appropriate
	@Override
	protected ModelFactory newModelFactory()
	{
		return new ModelFactory(
				(Function<ModelFactory, EModelFactory>) AssrtCoreEModelFactoryImpl::new,  // Explicit cast necessary (CHECKME, why?)
				(Function<ModelFactory, SModelFactory>) AssrtCoreSModelFactoryImpl::new);
	}

	/*// A Scribble extension should override newCoreConfig/Context/etc as appropriate
	@Override
	protected CoreConfig newCoreConfig(ModuleName mainFullname,
			CoreArgs args, STypeFactory tf)
	{
		STypeVisitorFactory vf = newSTypeVisitorFactory();
		ModelFactory mf = newModelFactory();
		return new CoreConfig(mainFullname, args, tf, vf, mf); 
	}*/

	// A Scribble extension should override newCoreConfig/Context/etc as appropriate
	@Override
	protected CoreContext newCoreContext(Set<GProtocol> imeds)
	{
		return new AssrtCoreContext(this, imeds);
	}

	@Override
	public void runPasses() throws ScribException
	{
		runSyntaxTransformPasses();
		runGlobalSyntaxWfPasses();
		runProjectionPasses();  // CHECKME: can try before validation (i.e., including syntactic WF), to promote greater tool feedback? (cf. CommandLine output "barrier")
		//runProjectionSyntaxWfPasses();
		runEfsmBuildingPasses();  // Currently, unfair-transform graph building must come after syntactic WF --- TODO fix graph building to prevent crash ?
		runLocalModelCheckingPasses();
		runGlobalModelCheckingPasses();
	}
	
	@Override
	protected void runSyntaxTransformPasses()  // No ScribException, no errors expected
	{
		verbosePrintPass("Inlining subprotocols for all globals...");
		for (ProtoName<Global> fullname : this.context.getParsedFullnames())
		{
			GProtocol inlined = this.context.getInlined(fullname);
			verbosePrintPass(
					"Inlined subprotocols: " + fullname + "\n" + inlined);
		}
				
		// Skipping unfolding -- unnecessary with proper guarding
	}

	@Override
	protected void runGlobalSyntaxWfPasses() throws ScribException
	{
		// super.runGlobalSyntaxWfPasses();
		// ^TODO FIXME: base API currently not compatible
		// E.g., `this.context.getInlined(fullname).def` is null
		
		// CHECKME: is below necessary? -- goes against unfolding, duplicates should be allowed in such contexts?
		verbosePrintPass(
				"Checking for distinct annot vars in each inlined global...");
		for (ProtoName<Global> fullname : this.context.getParsedFullnames())
		{
			/*List<AssrtIntVar> vs = ((AssrtCoreGProtocol)
				this.context.getInlined(fullname)).type
					.assrtCoreGather(  // TODO: factor out with base gatherer
							new AssrtCoreIntVarGatherer<Global, AssrtCoreGType>()::visit)
					.collect(Collectors.toList());*/
			List<AssrtIntVar> vs = ((AssrtCoreGProtocol) this.context
					.getInlined(fullname)).type.collectAnnotDataVarDecls().stream()
							.map(x -> x.var).collect(Collectors.toList());
			Set<AssrtIntVar> distinct = new HashSet<>(vs);
			if (vs.size() != distinct.size())
			{
				throw new ScribException("Duplicate annot var name(s): " + vs);
			}
		}
	}
	
	@Override
	protected void runProjectionPasses()  // No ScribException, no errors expected
	{
		verbosePrintPass("Projecting all inlined globals...");
		for (ProtoName<Global> fullname : this.context.getParsedFullnames())
		{
			GProtocol inlined = this.context.getInlined(fullname);
			for (Role self : inlined.roles)
			{
				// pruneRecs already done (see runContextBuildingPasses)
				// CHECKME: projection and inling commutative?
				LProjection iproj = this.context.getProjectedInlined(inlined.fullname,
						self);
				verbosePrintPass("Projected inlined onto " + self + ": "
						+ inlined.fullname + "\n" + iproj);
			}
		}
		
		// Skipping imed projection
	}

	// Overriding only for a single line, the `validate` call
	@Override
	protected void validateByScribble(ProtoName<Global> fullname, boolean fair)
			throws ScribException
	{
		SGraph graph = fair
				? this.context.getSGraph(fullname)
				: this.context.getUnfairSGraph(fullname);
		if (this.config.args.VERBOSE)
		{
			String dot = graph.init.toDot();
			String[] lines = dot.split("\\R");
			verbosePrintPass(
					//"(" + fullname + ") Built global model...\n" + graph.init.toDot() + "\n(" + fullname + ") ..." + graph.states.size() + " states");
					"Built " + (!fair ? "\"unfair\" " : "") + "global model ("
							+ graph.states.size() + " states): " + fullname + "\n"
							+ ((lines.length > 50)  // CHECKME: factor out constant?
									? "...[snip]...  (model text over 50 lines, try -[u]model[png])"
									: dot));
		}

		verbosePrintPass("Checking " + (!fair ? "\"unfair\" " : "")
				+ "global model: " + fullname);
		((AssrtCoreSModelFactory) this.config.mf.global)
				.AssrtCoreSModel(this, (AssrtCoreSGraph) graph).validate(this);  // FIXME: overriding only for this line (extra core arg)
	}
	
	@Override
	public AssrtCoreContext getContext()
	{
		return (AssrtCoreContext) super.getContext();
	}


	
	
	
	
	
	// TODO: refactor, to util?
  // Maybe record simpname as field (for core)
	public boolean checkSat(GProtoName fullname, Set<AssrtBFormula> bforms)
	{
		Solver solver = ((AssrtCoreArgs) this.config.args).solver;
		switch (solver)
		{
			case NATIVE_Z3:
			{
				AssrtCoreContext corec = getContext();
				return Z3Wrapper.checkSat(this, corec.getIntermediate(fullname), bforms);
			}
			case NONE:
			{
				verbosePrintln("\n[assrt-core] [WARNING] Skipping sat check:\n\t"
						+ bforms.stream().map(f -> f.toSmt2Formula() + "\n\t")
								.collect(Collectors.joining("")));
				return true;
			}
			default:
				throw new RuntimeException(
						"[assrt-core] Shouldn't get in here: " + solver);
		}
	}
}

