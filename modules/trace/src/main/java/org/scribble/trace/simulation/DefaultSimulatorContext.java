/*
 * Copyright 2009-14 www.scribble.org
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
package org.scribble.trace.simulation;

import org.scribble.resources.ResourceLocator;

/**
 * This class provides the default context support implementation for the simulator.
 *
 */
public class DefaultSimulatorContext implements SimulatorContext {
	
	private ResourceLocator _locator;
	
	/**
	 * This constructor initializes the resource locator.
	 * 
	 * @param locator The resource locator
	 */
	public DefaultSimulatorContext(ResourceLocator locator) {
		_locator = locator;
	}

	/**
	 * This method returns the resource locator.
	 * 
	 * @return The resource locator
	 */
	public ResourceLocator getResourceLocator() {
		return (_locator);
	}
	
}
