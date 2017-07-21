package org.scribble.ext.assrt.parser.scribble;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.ScribNode;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.ext.assrt.parser.assertions.AssrtAssertParser;
import org.scribble.ext.assrt.parser.scribble.ast.AssrtAntlrPayloadElemList;
import org.scribble.ext.assrt.parser.scribble.ast.global.AssrtAntlrGConnect;
import org.scribble.ext.assrt.parser.scribble.ast.global.AssrtAntlrGMessageTransfer;
import org.scribble.parser.scribble.AntlrConstants;
import org.scribble.parser.scribble.ScribParser;
import org.scribble.util.ScribParserException;

public class AssrtScribParser extends ScribParser
{
	// FIXME: refactor pattern (cf. AntlrConstants) -- cannot extend existing node type enum though
	public static final String ASSRT_GLOBALMESSAGETRANSFER_NODE_TYPE = "ASSRT_GLOBALMESSAGETRANSFER";
	public static final String ASSRT_GLOBALCONNECT_NODE_TYPE = "ASSRT_GLOBALCONNECT";
	public static final String ASSRT_ANNOTPAYLOADELEM_NODE_TYPE = "ASSRT_ANNOTPAYLOADELEM";
	
	public final AssrtAssertParser ap = AssrtAssertParser.getInstance();

	public AssrtScribParser()
	{

	}

	@Override
	public ScribNode parse(CommonTree ct, AstFactory af) throws ScribParserException
	{
		ScribParser.checkForAntlrErrors(ct);
		
		AssrtAstFactory aaf = (AssrtAstFactory) af;
		String type = ct.getToken().getText();  // Duplicated from ScribParserUtil.getAntlrNodeType  // FIXME: factor out with AssrtAntlrPayloadElemList.parsePayloadElem
		switch (type)
		{
			// N.B. will "override" base payload parsing in super -- FIXME: hacky
			// AssrtAntlrPayloadElemList used to parse both annotated and non-annotated payload elems -- i.e., original AntlrPayloadElemList is now redundant
			case AntlrConstants.PAYLOAD_NODE_TYPE:      return AssrtAntlrPayloadElemList.parsePayloadElemList(this, ct, aaf);
			
			// N.B. AssrtScribble.g parses this as a separate syntactic category than GLOBALMESSAGETRANSFER (cf. PAYLOAD)
			case ASSRT_GLOBALMESSAGETRANSFER_NODE_TYPE: return AssrtAntlrGMessageTransfer.parseAssrtGMessageTransfer(this, ct, aaf);
			case ASSRT_GLOBALCONNECT_NODE_TYPE:         return AssrtAntlrGConnect.parseAssrtGConnect(this, ct, aaf);

			default: return super.parse(ct, af);
		}
	}
}
