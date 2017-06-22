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
package parser;

public class AssertionsAntlrConstants {
		public static final String BEXPR_NODE_TYPE = "BEXPR";  
		public static final String CEXPR_NODE_TYPE = "CEXPR";   
		public static final String AEXPR_NODE_TYPE = "AEXPR";  
		public static final String VAR_NODE_TYPE = "VAR";  
		public static final String VALUE_NODE_TYPE = "VALUE"; 
		
		public enum AssertionNodeType 
		{
			BEXPR, 
			CEXPR,  
			AEXPR, 
			VAR, 
			VALUE
		}
}
