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
package org.scribble.parser.ast;

import java.util.List;
import java.util.stream.Collectors;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AAstFactoryImpl;
import org.scribble.ast.AstFactoryImpl;
import org.scribble.ast.PayloadElem;
import org.scribble.ast.PayloadElemList;
import org.scribble.ast.name.qualified.DataTypeNode;
import org.scribble.ast.name.simple.AVarNameNode;
import org.scribble.parser.AntlrConstants.AntlrNodeType;
import org.scribble.parser.ScribParser;
import org.scribble.parser.ast.name.AAntlrSimpleName;
import org.scribble.parser.ast.name.AntlrQualifiedName;
import org.scribble.parser.util.ScribParserUtil;

public class AAntlrPayloadElemList
{
	public static PayloadElemList parsePayloadElemList(ScribParser parser, CommonTree ct)
	{
		List<PayloadElem<?>> pes = AntlrPayloadElemList.getPayloadElements(ct).stream().map((pe) -> parsePayloadElem(pe)).collect(Collectors.toList());
		return AstFactoryImpl.FACTORY.PayloadElemList(ct, pes);
	}

	protected static PayloadElem<?> parsePayloadElem(CommonTree ct)
	{
		AntlrNodeType type = ScribParserUtil.getAntlrNodeType(ct);
		if (type == AntlrNodeType.ANNOTPAYLOAD)
		{
			AVarNameNode var = AAntlrSimpleName.toVarName(((CommonTree) ct.getChild(0)));
			DataTypeNode dt = AntlrQualifiedName.toDataTypeNameNode((CommonTree)ct.getChild(1));
			return AAstFactoryImpl.FACTORY.AnnotPayloadElem(ct, var, dt); 
		}
		else
		{
			return AntlrPayloadElemList.parsePayloadElem(ct);
		}
	}
}
