//$ java -cp scribble-parser/lib/antlr-3.5.2-complete.jar org.antlr.Tool -o scribble-assertions/target/generated-sources/antlr3 scribble-assertions/src/main/antlr3/org/scribble/parser/antlr/Assertions.g

// Windows:
//$ java -cp scribble-parser/lib/antlr-3.5.2-complete.jar org.antlr.Tool -o scribble-assertions/target/generated-sources/antlr3/org/scribble/parser/antlr scribble-assertions/src/main/antlr3/org/scribble/parser/antlr/Assertions.g
//$ mv scribble-assertions/target/generated-sources/antlr3/org/scribble/parser/antlr/Assertions.tokens scribble-assertions/target/generated-sources/antlr3/


grammar Assertions;  // TODO: rename AssrtExt(Id), or AssrtAnnotation

options
{
	language = Java;
	output = AST;
	ASTLabelType = CommonTree;
}

tokens
{
	/*
	 * Parser input constants (lexer output; keywords, Section 2.4)
	 */
	TRUE_KW = 'True';
	FALSE_KW = 'False';


	/*
	 * Parser output "node types" (corresponding to the various syntactic
	 * categories) i.e. the labels used to distinguish resulting AST nodes.
	 * The value of these token variables doesn't matter, only the token
	 * (i.e. variable) names themselves are used (for AST node root text
	 * field)
	 */
	//EMPTY_LIST = 'EMPTY_LIST';
	
	// TODO: rename EXT_... (or ANNOT_...)
	ROOT; 
	
	BOOLEXPR; 
	COMPEXPR; 
	ARITHEXPR; 
	NEGEXPR;
	
	UNFUN;
	UNFUNARGLIST;

	INTVAR; 
	INTVAL; 
	NEGINTVAL; 

	TRUE;
	FALSE;
	
	ASSRT_STATEVARDECLLIST;
	ASSRT_STATEVARDECL;
	ASSRT_STATEVARDECLLISTASSERTION;
	ASSRT_STATEVARARGLIST;
	
	ASSRT_EMPTYASS;
}


@parser::header
{
	package org.scribble.parser.antlr;
	
	import org.scribble.ext.assrt.ast.AssrtStateVarAnnotNode;
	import org.scribble.ext.assrt.core.type.formula.AssrtAFormula;
	import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;
	import org.scribble.ext.assrt.core.type.formula.AssrtSmtFormula;
	import org.scribble.ext.assrt.parser.assertions.AssrtAntlrToFormulaParser;
}

@lexer::header
{
	package org.scribble.parser.antlr;
}

@parser::members
{
	@Override    
	public void displayRecognitionError(String[] tokenNames, 
			RecognitionException e)
	{
		super.displayRecognitionError(tokenNames, e);
  	System.exit(1);
	}
  
	public static AssrtBFormula parseAssertion(String source) 
			throws RecognitionException
	{
		source = source.substring(1, source.length()-1);  // Remove enclosing quotes -- cf. AssrtScribble.g EXTID
		AssertionsLexer lexer = new AssertionsLexer(new ANTLRStringStream(source));
		AssertionsParser parser = new AssertionsParser(
				new CommonTokenStream(lexer));
		AssrtSmtFormula<?> res = AssrtAntlrToFormulaParser
				.getInstance().parse((CommonTree) parser.bool_root().getTree());
		if (!(res instanceof AssrtBFormula))
		{
			System.out.println("Invalid assertion syntax: " + source);
			System.exit(1);
		}
		return (AssrtBFormula) res;
	}

	public static AssrtAFormula parseArithAnnotation(String source) 
			throws RecognitionException
	{
		source = source.substring(1, source.length()-1);  // Remove enclosing quotes -- cf. AssrtScribble.g EXTID
		AssertionsLexer lexer = new AssertionsLexer(new ANTLRStringStream(source));
		AssertionsParser parser = new AssertionsParser(new CommonTokenStream(lexer));
		//return (CommonTree) parser.arith_expr().getTree();
		AssrtAFormula res = (AssrtAFormula) AssrtAntlrToFormulaParser
				.getInstance().parse((CommonTree) parser.arith_root().getTree());
		return res;
	}
}


// Not referred to explicitly, deals with whitespace implicitly (don't delete this)
WHITESPACE:
	('\t' | ' ' | '\r' | '\n'| '\u000C')+
	{
		$channel = HIDDEN;
	}
;


fragment LETTER:
	'a'..'z' | 'A'..'Z'
;

fragment DIGIT:
	'0'..'9'
;

IDENTIFIER:
	LETTER (LETTER | DIGIT)*
;  

NUMBER: 
	(DIGIT)+
; 


variable: 
	IDENTIFIER -> ^(INTVAR IDENTIFIER)
; 	  

num: 
	NUMBER -> ^(INTVAL NUMBER)	   
|
	'-' NUMBER -> ^(NEGINTVAL NUMBER)
; 

	
root:  // Seems mandatory?
	bool_root -> bool_root
;

bool_root:  // EOF useful?
	bool_expr EOF -> bool_expr
;

arith_root:  // EOF useful?
	arith_expr EOF -> arith_expr
;


expr:
	bool_expr
;
	
bool_expr:
	bool_or_expr
;

bool_or_expr:
	bool_and_expr (op=('||') bool_and_expr)*
->
	^(BOOLEXPR bool_and_expr ($op bool_and_expr)*)  // ops a bit redundant, but currently using old, shared (and/or) AssrtAntlrBoolExpr parsing routine
;
// ANTLR seems to engender a pattern where expr "kinds" are nested under a single expr
// Cf. https://github.com/antlr/grammars-v3/blob/master/Java1.6/Java.g#L943
// ^Expr categories are all "nested", bottoming out at primary which recursively contains `parExpression`
// Precedence follows the nesting order, e.g., 1+2*3 -> 1+(2*3); o/w left-assoc (preserved by AssrtAntlr... routines)

bool_and_expr:
	comp_expr (op=('&&') comp_expr)*
->
	^(BOOLEXPR comp_expr ($op comp_expr)*)
;

comp_expr:  // "relational" expr
	arith_expr (op=('=' | '<' | '<=' | '>' | '>=') arith_expr)?
->
	^(COMPEXPR arith_expr $op? arith_expr?)
;
	
arith_expr:
	arith_add_expr
;

arith_add_expr:
	arith_mul_expr (arith_addsub_op arith_mul_expr)*
->
	^(ARITHEXPR arith_mul_expr (arith_addsub_op arith_mul_expr)*)  // Cannot distinguish the ops args?  Always the last one?
;

arith_addsub_op:
	'+' | '-'
;

arith_mul_expr:
	arith_unary_expr (op=('*') arith_unary_expr)*
->
	^(ARITHEXPR arith_unary_expr ($op arith_unary_expr)*)
;
	
arith_unary_expr:
	primary_expr
|
	'!' bool_expr -> ^(NEGEXPR bool_expr)  // Highly binding, so nest deeply
;
// '¬' doesn't seem to work

primary_expr:
	paren_expr
|
	literal
|
	variable
/*|
	unint_fun*/
;
	
paren_expr:
	'(' expr ')' -> expr
;

literal:
	TRUE_KW -> ^(TRUE)
|
	FALSE_KW -> ^(FALSE)
|
	num
;

/*
unint_fun:
	IDENTIFIER unint_fun_arg_list
->
	^(UNFUN IDENTIFIER unint_fun_arg_list)
; 
	
unint_fun_arg_list:
	'(' (arith_expr (',' arith_expr )*)? ')'
->
	^(UNFUNARGLIST arith_expr*)
;
*/
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

/*
	public static AssrtStateVarAnnotNode parseStateVarAnnot(String source) 
			throws RecognitionException
	{
		source = source.substring(1, source.length()-1);  // Remove enclosing quotes -- cf. AssrtScribble.g EXTID
		AssertionsLexer lexer = new AssertionsLexer(new ANTLRStringStream(source));
		AssertionsParser parser = new AssertionsParser(
				new CommonTokenStream(lexer));
		CommonTree res = (CommonTree) parser.annot_statevardecls().getTree();
		AssrtStateVarAnnotNode n = new AssrtStateVarAnnotNode(res.getToken());
		//n.addScribChildren(...);
		return n;
	}

	public static CommonTree parseStateVarDeclList(String source) 
			throws RecognitionException
	{
		source = source.substring(1, source.length()-1);  // Remove enclosing quotes -- cf. AssrtScribble.g EXTID
		AssertionsLexer lexer = new AssertionsLexer(new ANTLRStringStream(source));
		AssertionsParser parser = new AssertionsParser(
				new CommonTokenStream(lexer));
		return (CommonTree) parser.statevardecllist().getTree();
	}

	public static CommonTree parseStateVarArgList(String source) throws RecognitionException
	{
		source = source.substring(1, source.length()-1);  // Remove enclosing quotes -- cf. AssrtScribble.g EXTID
		AssertionsLexer lexer = new AssertionsLexer(new ANTLRStringStream(source));
		AssertionsParser parser = new AssertionsParser(new CommonTokenStream(lexer));
		return (CommonTree) parser.statevararglist().getTree();
	}
	
// statevars -- TODO: refactor to AssrtScribble.g -- no: it's all inside the EXTID annot -- but: doesn't have to be...
	
annot_statevardecls: statevardecllist EOF;
	
statevardecllist:
/*	'<' statevardecl (',' statevardecl)* '>'
->
	^(ASSRT_STATEVARDECLLIST ^(ASSRT_EMPTYASS) statevardecl+)* /
|
	'<' statevardecl (',' statevardecl)* '>' bool_expr?
->
	^(ASSRT_STATEVARDECLLIST ^(ASSRT_STATEVARDECLLISTASSERTION bool_expr?) statevardecl+)
|
	bool_expr
->
	^(ASSRT_STATEVARDECLLIST ^(ASSRT_STATEVARDECLLISTASSERTION bool_expr)) 
;
	
statevardecl:
	variable ':=' arith_expr
->
	^(ASSRT_STATEVARDECL variable arith_expr)
;
	
statevararglist:
	'<' arith_expr (',' arith_expr)* '>'
->
	^(ASSRT_STATEVARARGLIST arith_expr+)
;
//*/
	
