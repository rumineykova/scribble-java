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
import org.scribble.model.MessageSignature;

/**
 * This class provides the model adapter for the 'messageSignature' parser rule.
 *
 */
public class ArgumentModelAdaptor extends AbstractModelAdaptor {

	/**
	 * {@inheritDoc}
	 */
	public Object createModelObject(ParserContext context) {
		
		Argument ret=new Argument();
		
		if (context.peek() instanceof CommonToken) {
			CommonToken end=(CommonToken)context.pop();
			
			ret.setAlias(end.getText());
			
			setEndProperties(ret, end);

			if (context.peek() instanceof CommonToken &&
					((CommonToken)context.peek()).getText().equals("as")) {
				context.pop(); // consume 'as'
				
				if (context.peek() instanceof MessageSignature) {
					ret.setMessageSignature((MessageSignature)context.pop());
					
					setStartProperties(ret, ret.getMessageSignature());
				} else {
					CommonToken nameToken=(CommonToken)context.pop();
					
					ret.setName(nameToken.getText());
					
					setStartProperties(ret, nameToken);
				}
			} else {
				ret.setName(ret.getAlias());
				ret.setAlias(null);
				
				setStartProperties(ret, end);
			}
		} else if (context.peek() instanceof MessageSignature) {
			ret.setMessageSignature((MessageSignature)context.pop());
			
			setStartProperties(ret, ret.getMessageSignature());
			setEndProperties(ret, ret.getMessageSignature());
		}
		
		context.push(ret);
			
		return ret;
	}

}
