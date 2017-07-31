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
package org.scribble.ext.assrt.del.local;

import java.util.HashMap;
import java.util.Map;

import org.scribble.ast.ScribNode;
import org.scribble.ast.local.LRecursion;
import org.scribble.del.local.LRecursionDel;
import org.scribble.ext.assrt.ast.AssrtAssertion;
import org.scribble.ext.assrt.ast.local.AssrtLRecursion;
import org.scribble.ext.assrt.model.endpoint.AssrtEGraphBuilderUtil;
import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.formula.AssrtBinCompFormula;
import org.scribble.ext.assrt.type.formula.AssrtIntVarFormula;
import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.main.ScribbleException;
import org.scribble.type.name.RecVar;
import org.scribble.visit.context.EGraphBuilder;

public class AssrtLRecursionDel extends LRecursionDel
{
	/*@Override
	public ScribNode leaveUnguardedChoiceDoProjectionCheck(ScribNode parent, ScribNode child, UnguardedChoiceDoProjectionChecker checker, ScribNode visited) throws ScribbleException
	{
		Recursion<?> rec = (Recursion<?>) visited;
		UnguardedChoiceDoEnv merged = checker.popEnv().mergeContext((UnguardedChoiceDoEnv) rec.block.del().env());
		checker.pushEnv(merged);
		return (Recursion<?>) super.leaveUnguardedChoiceDoProjectionCheck(parent, child, checker, rec);
	}*/

	/*@Override
	public ScribNode leaveProtocolInlining(ScribNode parent, ScribNode child, ProtocolDefInliner inl, ScribNode visited) throws ScribbleException
	{
		LRecursion lr = (LRecursion) visited;
		//RecVarNode recvar = lr.recvar.clone();
		RecVarNode recvar = (RecVarNode) ((InlineProtocolEnv) lr.recvar.del().env()).getTranslation();	
		LProtocolBlock block = (LProtocolBlock) ((InlineProtocolEnv) lr.block.del().env()).getTranslation();	

		//LRecursion inlined = inl.job.af.LRecursion(lr.getSource(), recvar, block);
		AssrtAssertion ass = ((AssrtLRecursion) lr).ass;
		LRecursion inlined = ((AssrtAstFactory) inl.job.af).AssrtLRecursion(lr.getSource(), recvar, block, ass);

		inl.pushEnv(inl.popEnv().setTranslation(inlined));
		return (LRecursion) super.leaveProtocolInlining(parent, child, inl, lr);
	}*/

	/*@Override
	public LRecursion leaveReachabilityCheck(ScribNode parent, ScribNode child, ReachabilityChecker checker, ScribNode visited) throws ScribbleException
	{
		LRecursion lr = (LRecursion) visited;
		ReachabilityEnv env = checker.popEnv().mergeContext((ReachabilityEnv) lr.block.del().env());
		env = env.removeContinueLabel(lr.recvar.toName());
		checker.pushEnv(env);
		return (LRecursion) LCompoundInteractionNodeDel.super.leaveReachabilityCheck(parent, child, checker, visited);  // records the current checker Env to the current del; also pops and merges that env into the parent env
	}*/
	
	@Override
	public void enterEGraphBuilding(ScribNode parent, ScribNode child, EGraphBuilder graph)
	{
		super.enterEGraphBuilding(parent, child, graph);
		AssrtLRecursion lr = (AssrtLRecursion) child;
		RecVar rv = lr.recvar.toName();
		AssrtAssertion ass = lr.ass;
		graph.util.addEntryLabel(rv);
		
		// Cf. AssrtLProjectionDeclDel::enterEGraphBuilding
		Map<AssrtDataTypeVar, AssrtArithFormula> vars = new HashMap<>();
		if (ass != null)
		{
			AssrtBinCompFormula bcf = (AssrtBinCompFormula) ass.getFormula();
			vars.put(((AssrtIntVarFormula) bcf.left).toName(), bcf.right);
		}
		((AssrtEGraphBuilderUtil) graph.util).addAnnotVarInits(vars);

		graph.util.pushRecursionEntry(rv, graph.util.getEntry());
	}

	@Override
	public LRecursion leaveEGraphBuilding(ScribNode parent, ScribNode child, EGraphBuilder graph, ScribNode visited) throws ScribbleException
	{
		LRecursion lr = (LRecursion) visited;
		RecVar rv = lr.recvar.toName();
		graph.util.popRecursionEntry(rv);
		return (LRecursion) super.leaveEGraphBuilding(parent, child, graph, lr);
	}
}
