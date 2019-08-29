package org.scribble.ext.assrt.del.global;

import org.scribble.del.global.GProtoBlockDel;
import org.scribble.ext.assrt.del.AssrtICompoundInteractionDel;

public class AssrtGProtoBlockDel extends GProtoBlockDel
		implements AssrtICompoundInteractionDel
{
	
	/*// Cf. GProtocolBlockDel.project?
	@Override
	public void enterAnnotCheck(ScribNode parent, ScribNode child, AssrtAnnotationChecker checker) throws ScribbleException
	{
		// CHECKME: why only for GChoice? -- Should be left to AssrtGChoiceDel? cf. GChoiceDel.enterInlinedWFChoiceCheck
		if (parent instanceof GChoice)
		{
			ScribDelBase.pushVisitorEnv(this, checker);
		}
	}
	
	@Override
	public GProtocolBlock leaveAnnotCheck(ScribNode parent, ScribNode child, AssrtAnnotationChecker checker, ScribNode visited) throws ScribbleException
	{
		// CHECKME: why only for GChoice?
		return (parent instanceof GChoice)
				? (GProtocolBlock) ScribDelBase.popAndSetVisitorEnv(this, checker, visited)
				: (GProtocolBlock) visited; 
	}*/
}
