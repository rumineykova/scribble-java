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
package org.scribble.ext.assrt.parser.ast.name;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.ast.AssrtAstFactoryImpl;
import org.scribble.ext.assrt.ast.name.simple.AssrtVarNameNode;
import org.scribble.ext.assrt.sesstype.kind.AssrtAnnotVarNameKind;
import org.scribble.parser.ast.name.AntlrSimpleName;

public class AssrtAntlrSimpleName
{
	public static AssrtVarNameNode toVarName(CommonTree ct)
	{
		return (AssrtVarNameNode) AssrtAstFactoryImpl.FACTORY.SimpleNameNode(ct, AssrtAnnotVarNameKind.KIND, AntlrSimpleName.getName(ct));
	}
}
