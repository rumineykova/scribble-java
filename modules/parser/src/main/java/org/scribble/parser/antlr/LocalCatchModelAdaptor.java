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
import org.scribble.model.Message;
import org.scribble.model.Role;
import org.scribble.model.local.LInterruptible;

/**
 * This class provides the model adapter for the 'interruptible' parser rule.
 *
 */
public class LocalCatchModelAdaptor extends AbstractModelAdaptor {

	/**
	 * {@inheritDoc}
	 */
	public Object createModelObject(ParserContext context) {
		LInterruptible.Catch ret=new LInterruptible.Catch();
		
		setEndProperties(ret, context.pop()); // ';'
		
		Role r=new Role();
		
		setStartProperties(r, context.peek());
		setEndProperties(r, context.peek());
		
		r.setName(((CommonToken)context.pop()).getText());

		ret.setRole(r);
		
		context.pop(); // 'from'
		
		ret.getMessages().add((Message)context.pop());
		
		while (context.peek() instanceof CommonToken &&
				((CommonToken)context.peek()).getText().equals(",")) {
			context.pop(); // ','
			
			ret.getMessages().add(0, (Message)context.pop());
		}
		
		setStartProperties(ret, context.pop()); // 'catches'

		context.push(ret);
		
		return ret;
	}

}
