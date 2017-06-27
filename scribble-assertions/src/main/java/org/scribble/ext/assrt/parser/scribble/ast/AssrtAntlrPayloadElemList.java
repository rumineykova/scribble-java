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
package org.scribble.ext.assrt.parser.scribble.ast;

import java.util.List;
import java.util.stream.Collectors;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.PayloadElem;
import org.scribble.ast.PayloadElemList;
import org.scribble.ast.name.qualified.DataTypeNode;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.ext.assrt.ast.name.simple.AssrtVarNameNode;
import org.scribble.ext.assrt.parser.scribble.AssrtScribParser;
import org.scribble.ext.assrt.parser.scribble.ast.name.AssrtAntlrSimpleName;
import org.scribble.parser.scribble.ScribParser;
import org.scribble.parser.scribble.ast.AntlrPayloadElemList;
import org.scribble.parser.scribble.ast.name.AntlrQualifiedName;

public class AssrtAntlrPayloadElemList
{
	public static final int VAR_NAME_CHILD_INDEX = 0;
	public static final int DATA_TYPE_CHILD_INDEX = 1;
	
	public static PayloadElemList parsePayloadElemList(ScribParser parser, CommonTree ct, AssrtAstFactory af)
	{
		List<PayloadElem<?>> pes = AntlrPayloadElemList.getPayloadElements(ct).stream()
				.map(pe -> parsePayloadElem(pe, af)).collect(Collectors.toList());
		return af.PayloadElemList(ct, pes);
	}

	protected static PayloadElem<?> parsePayloadElem(CommonTree ct, AssrtAstFactory af)
	{
		String type = ct.getToken().getText();  // Duplicated from ScribParserUtil.getAntlrNodeType  // FIXME: factor out with AssrtScribParser.parse
		switch (type)
		{
			case AssrtScribParser.ASSRTPAYLOADELEM_NODE_TYPE:
			{
				AssrtVarNameNode var = AssrtAntlrSimpleName.toVarName(getVarNameChild(ct), af);
				DataTypeNode dt = AntlrQualifiedName.toDataTypeNameNode(getDataTypeChild(ct), af);
				return ((AssrtAstFactory) af).AnnotPayloadElem(ct, var, dt); 
			}
			default: return AntlrPayloadElemList.parsePayloadElem(ct, af);
		}
	}

	public static CommonTree getVarNameChild(CommonTree ct)
	{
		return (CommonTree) ct.getChild(VAR_NAME_CHILD_INDEX);
	}

	public static CommonTree getDataTypeChild(CommonTree ct)
	{
		return (CommonTree) ct.getChild(DATA_TYPE_CHILD_INDEX);
	}
}