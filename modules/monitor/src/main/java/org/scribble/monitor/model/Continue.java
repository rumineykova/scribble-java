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
package org.scribble.monitor.model;

import org.scribble.monitor.runtime.MonitorContext;
import org.scribble.monitor.runtime.SessionScope;

/**
 * This class represents a Continue action.
 *
 */
public class Continue extends Node {
	
	/**
	 * {@inheritDoc}
	 */
	protected void init(MonitorContext context) {
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean evaluate(SessionType type, int index, SessionScope scope) {
		
		handled(type, scope, -1);
		
		return (false);
	}

}
