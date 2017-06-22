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
package org.scribble.ext.assrt.parser.ast;

import java.util.List;
import java.util.stream.Collectors;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.PayloadElem;
import org.scribble.ast.PayloadElemList;
import org.scribble.ast.name.qualified.DataTypeNode;
import org.scribble.ext.assrt.ast.AssrtAstFactoryImpl;
import org.scribble.ext.assrt.ast.name.simple.AssrtVarNameNode;
import org.scribble.ext.assrt.parser.ast.name.AssrtAntlrSimpleName;
import org.scribble.parser.AntlrConstants.AntlrNodeType;
import org.scribble.parser.ScribParser;
import org.scribble.parser.ast.AntlrPayloadElemList;
import org.scribble.parser.ast.name.AntlrQualifiedName;
import org.scribble.parser.util.ScribParserUtil;

public class AssrtAntlrPayloadElemList
{
	public static PayloadElemList parsePayloadElemList(ScribParser parser, CommonTree ct, AstFactory af)
	{
		List<PayloadElem<?>> pes = AntlrPayloadElemList.getPayloadElements(ct).stream().map(pe -> parsePayloadElem(pe, af)).collect(Collectors.toList());
		return af.PayloadElemList(ct, pes);
	}

	protected static PayloadElem<?> parsePayloadElem(CommonTree ct, AstFactory af)
	{
		AntlrNodeType type = ScribParserUtil.getAntlrNodeType(ct);
		if (type == AntlrNodeType.ANNOTPAYLOAD)
		{
			AssrtVarNameNode var = AssrtAntlrSimpleName.toVarName(((CommonTree) ct.getChild(0)));
			DataTypeNode dt = AntlrQualifiedName.toDataTypeNameNode((CommonTree)ct.getChild(1), af);
			return AssrtAstFactoryImpl.FACTORY.AnnotPayloadElem(ct, var, dt); 
		}
		else
		{
			return AntlrPayloadElemList.parsePayloadElem(ct, af);
		}
	}
}
