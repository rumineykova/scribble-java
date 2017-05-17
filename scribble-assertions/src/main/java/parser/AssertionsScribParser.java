package parser;

import java.util.List;
import java.util.stream.Collectors;

import org.antlr.runtime.tree.CommonErrorNode;
import org.antlr.runtime.tree.CommonTree;
import org.scribble.assertions.StmFormula;

import parser.AssertionsAntlrConstants.AssertionNodeType;
import parser.ast.ArithFormulaNode;
import parser.ast.BoolFormulaNode;
import parser.ast.CompFormulaNode;
import parser.ast.ValueNode;
import parser.ast.VarNode;

// ANTLR CommonTree -> ScribNode
// Parses ANTLR nodes into ScribNodes using the parser.ast.Antlr[...] helper classes
public class AssertionsScribParser
{
	protected AssertionsScribParser() {}
	public static AssertionsScribParser getInstance() {
	      if(instance == null) {
	         instance = new AssertionsScribParser();
	      }
	      return instance;
	   }
	
	private static AssertionsScribParser instance = null;

	public StmFormula parse(CommonTree ct) throws AssertionsParseException
	{
		if (ct.getChildCount() > 0)  // getChildren returns null instead of empty list 
		{
			List<CommonErrorNode> errors = ((List<?>) ct.getChildren()).stream()
					.filter((c) -> (c instanceof CommonErrorNode))
					.map((c) -> (CommonErrorNode) c)
					.collect(Collectors.toList());
			if (errors.size() > 0)  // Antlr prints errors to System.err by default, but then attempts to carry on
						// Should never get here now, Antlr displayRecognitionError overridden to force exit: Antlr error recovery means not all errors produce CommonErrorNode
			{
				//throw new ScribParserException("Parsing errors: " + errors);  // FIXME: improve feedback message
				System.err.println("Aborting due to parsing errors.");
				System.exit(1);
			}
		}
		
		AssertionNodeType type = AssertionsScribParserUtil.getAntlrNodeType(ct);
		switch (type)
		{
			case BEXPR:
				return BoolFormulaNode.parseBoolFormula(this, ct);
			case CEXPR:
				return CompFormulaNode.parseCompFormula(this, ct);
			case AEXPR:
				return ArithFormulaNode.parseArithFormula(this, ct);
			case VAR:
				return VarNode.parseVarFormula(this, ct);
			case VALUE:
				return ValueNode.parseValueFormula(this, ct);
			default:
				throw new RuntimeException("Unknown ANTLR node type: " + type);
		}
	}
}
