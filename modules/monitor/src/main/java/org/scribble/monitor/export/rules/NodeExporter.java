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
package org.scribble.monitor.export.rules;

import org.scribble.context.ModuleContext;
import org.scribble.model.ModelObject;
import org.scribble.monitor.model.SessionType;

/**
 * This class exports local protocol objects into a session type
 * to be monitored.
 *
 */
public interface NodeExporter {

	/**
	 * This method exports the model object to the session type.
	 * 
	 * @param context The module context
	 * @param state The export state
	 * @param mobj The local protocol object
	 * @param type The session type
	 */
	public void export(ModuleContext context, ExportState state, ModelObject mobj, SessionType type);
	
}
