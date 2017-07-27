package org.scribble.ext.assrt.parser.scribble;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.ScribNode;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.ext.assrt.parser.assertions.AssrtAntlrToFormulaParser;
import org.scribble.ext.assrt.parser.scribble.ast.AssrtAntlrPayloadElemList;
import org.scribble.ext.assrt.parser.scribble.ast.global.AssrtAntlrGConnect;
import org.scribble.ext.assrt.parser.scribble.ast.global.AssrtAntlrGMessageTransfer;
import org.scribble.parser.scribble.ScribbleAntlrConstants;
import org.scribble.parser.scribble.AntlrToScribParser;
import org.scribble.util.ScribParserException;

public class AssrtAntlrToScribParser extends AntlrToScribParser
{
	// FIXME: refactor pattern (cf. AntlrConstants) -- cannot extend existing node type enum though
	public static final String ASSRT_GLOBALMESSAGETRANSFER_NODE_TYPE = "ASSRT_GLOBALMESSAGETRANSFER";
	public static final String ASSRT_GLOBALCONNECT_NODE_TYPE = "ASSRT_GLOBALCONNECT";
	public static final String ASSRT_ANNOTPAYLOADELEM_NODE_TYPE = "ASSRT_ANNOTPAYLOADELEM";
	
	public final AssrtAntlrToFormulaParser ap = AssrtAntlrToFormulaParser.getInstance();

	public AssrtAntlrToScribParser()
	{

	}

	@Override
	public ScribNode parse(CommonTree ct, AstFactory af) throws ScribParserException
	{
		AntlrToScribParser.checkForAntlrErrors(ct);
		
		AssrtAstFactory aaf = (AssrtAstFactory) af;
		String type = ct.getToken().getText();  // Duplicated from ScribParserUtil.getAntlrNodeType  // FIXME: factor out with AssrtAntlrPayloadElemList.parsePayloadElem
		switch (type)
		{
			// N.B. will "override" base payload parsing in super -- FIXME: hacky
			// AssrtAntlrPayloadElemList used to parse both annotated and non-annotated payload elems -- i.e., original AntlrPayloadElemList is now redundant
			case ScribbleAntlrConstants.PAYLOAD_NODE_TYPE:      return AssrtAntlrPayloadElemList.parsePayloadElemList(this, ct, aaf);
			
			// N.B. AssrtScribble.g parses this as a separate syntactic category than GLOBALMESSAGETRANSFER (cf. PAYLOAD)
			case ASSRT_GLOBALMESSAGETRANSFER_NODE_TYPE: return AssrtAntlrGMessageTransfer.parseAssrtGMessageTransfer(this, ct, aaf);
			case ASSRT_GLOBALCONNECT_NODE_TYPE:         return AssrtAntlrGConnect.parseAssrtGConnect(this, ct, aaf);

			default: return super.parse(ct, af);
		}
	}
}
