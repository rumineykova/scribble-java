/*
 * Copyright 2009-11 www.scribble.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.scribble.parser.antlr;

import org.antlr.runtime.CommonToken;
import org.scribble.model.Argument;
import org.scribble.model.ParameterDecl;
import org.scribble.model.ProtocolDecl;
import org.scribble.model.Role;
import org.scribble.model.RoleDecl;
import org.scribble.model.RoleInstantiation;
import org.scribble.model.local.LBlock;
import org.scribble.model.local.LProtocolDefinition;
import org.scribble.model.local.LProtocolInstance;

/**
 * This class provides the model adapter for the 'localProtocolDecl' parser rule.
 *
 */
public class LocalProtocolDeclModelAdaptor extends AbstractModelAdaptor {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Object createModelObject(ParserContext context) {
		ProtocolDecl ret=null;
		
		if (context.peek() instanceof LBlock) {
			ret = new LProtocolDefinition();
			
			setEndProperties(ret, context.peek());
			
			((LProtocolDefinition)ret).setBlock((LBlock)context.pop());
		} else {
			ret = new LProtocolInstance();

			setEndProperties(ret, context.pop()); // consume ;

			((LProtocolInstance)ret).getRoleInstantiations().addAll((java.util.List<RoleInstantiation>)context.pop());
			
			if (context.peek() instanceof java.util.List<?>) {
				((LProtocolInstance)ret).getArguments().addAll((java.util.List<Argument>)context.pop());
			}
			
			((LProtocolInstance)ret).setMemberName(((CommonToken)context.pop()).getText());		

			context.pop(); // instantiates

		}
		
		ret.getRoleDeclarations().addAll((java.util.List<RoleDecl>)context.pop());
		
		if (context.peek() instanceof java.util.List<?>) {
			ret.getParameterDeclarations().addAll((java.util.List<ParameterDecl>)context.pop());
		}
		
		if (ret instanceof LProtocolDefinition) {
			Role r=new Role();
			
			setStartProperties(r, context.peek());
			setEndProperties(r, context.peek());
			
			r.setName(((CommonToken)context.pop()).getText());

			((LProtocolDefinition)ret).setLocalRole(r);
		} else if (ret instanceof LProtocolInstance) {
			Role r=new Role();
			
			setStartProperties(r, context.peek());
			setEndProperties(r, context.peek());
			
			r.setName(((CommonToken)context.pop()).getText());

			((LProtocolInstance)ret).setLocalRole(r);
		}
		
		context.pop(); // at

		ret.setName(((CommonToken)context.pop()).getText());
		
		context.pop(); // protocol
		
		setStartProperties(ret, context.pop()); // local
		
		context.push(ret);
		
		return ret;
	}

}
