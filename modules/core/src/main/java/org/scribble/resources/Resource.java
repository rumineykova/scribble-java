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
package org.scribble.resources;

/**
 * This class represents the resource.
 *
 */
public interface Resource {
	
	/**
	 * This method returns the resource path.
	 * 
	 * @return The resource path
	 */
	public String getPath();

	/**
	 * This method returns an input stream containing the
	 * resource content.
	 * 
	 * @return The input stream
	 */
	public java.io.InputStream getInputStream();
	
}
