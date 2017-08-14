//$ java -cp scribble-parser/lib/antlr-3.5.2-complete.jar org.antlr.Tool -o scribble-assertions/target/generated-sources/antlr3 scribble-assertions/src/main/antlr3/org/scribble/parser/antlr/Assertions.g

// Windows:
//$ java -cp scribble-parser/lib/antlr-3.5.2-complete.jar org.antlr.Tool -o scribble-assertions/target/generated-sources/antlr3/org/scribble/parser/antlr scribble-assertions/src/main/antlr3/org/scribble/parser/antlr/Assertions.g
//$ mv scribble-assertions/target/generated-sources/antlr3/org/scribble/parser/antlr/Assertions.tokens scribble-assertions/target/generated-sources/antlr3/


grammar Assertions;

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
	EMPTY_LIST = 'EMPTY_LIST';
	
	ROOT = 'ROOT'; 
	
	BINBOOLEXPR = 'BINBOOLEXPR'; 
	BINCOMPEXPR = 'BINCOMPEXPR'; 
	BINARITHEXPR = 'BINARITHEXPR'; 
	
	UNPRED = 'UNPRED';
	ARITH_EXPR_LIST = 'ARITH_EXPR_LIST';

	INTVAR  = 'INTVAR'; 
	INTVAL = 'INTVAL'; 

	TRUE = 'TRUE';
	FALSE = 'FALSE';
	
	ASSRT_STATEVARDECLLIST = 'ASSRT_STATEVARDECLLIST';
	ASSRT_STATEVARDECL = 'ASSRT_STATEVARDECL';
	ASSRT_STATEVARARGLIST = 'ASSRT_STATEVARARGLIST';
	ASSRT_STATEVARARG = 'ASSRT_STATEVARARG';
}

@parser::header
{
	package org.scribble.parser.antlr;
}

@lexer::header
{
	package org.scribble.parser.antlr;
}

@parser::members
{
	@Override    
	public void displayRecognitionError(String[] tokenNames, RecognitionException e)
	{
		super.displayRecognitionError(tokenNames, e);
  	System.exit(1);
	}
  
	// Takes the whole AssrtScribble.g ASSRT_EXPPR
	public static CommonTree parseAssertion(String source) throws RecognitionException
	{
		source = source.substring(1, source.length()-1);  // Remove enclosing quotes -- cf. AssrtScribble.g EXTIDENTIFIER
		AssertionsLexer lexer = new AssertionsLexer(new ANTLRStringStream(source));
		AssertionsParser parser = new AssertionsParser(new CommonTokenStream(lexer));
		return (CommonTree) parser.root().getTree();
	}

	public static CommonTree parseArithAnnotation(String source) throws RecognitionException
	{
		source = source.substring(1, source.length()-1);  // Remove enclosing quotes -- cf. AssrtScribble.g EXTIDENTIFIER
		AssertionsLexer lexer = new AssertionsLexer(new ANTLRStringStream(source));
		AssertionsParser parser = new AssertionsParser(new CommonTokenStream(lexer));
		return (CommonTree) parser.arith_expr().getTree();
	}

	public static CommonTree parseStateVarDeclList(String source) throws RecognitionException
	{
		source = source.substring(1, source.length()-1);  // Remove enclosing quotes -- cf. AssrtScribble.g EXTIDENTIFIER
		AssertionsLexer lexer = new AssertionsLexer(new ANTLRStringStream(source));
		AssertionsParser parser = new AssertionsParser(new CommonTokenStream(lexer));
		return (CommonTree) parser.statevardecllist().getTree();
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
//	LETTER (LETTER | DIGIT)* 

NUMBER: 
	(DIGIT)* 
; 
	//(DIGIT)+  // Doesn't work -- why? (and why does above work?)


BIN_COMP_OP:
    '>' | '<' | '='		 
; 

BIN_ARITH_OP:	 
  '+' | '-' | '*' 	
; 

BIN_BOOL_OP:
   '||' | '&&'
; 	 	


variable: 
	IDENTIFIER
->
	^(INTVAR IDENTIFIER)
; 	  

num: 
	NUMBER
->
	^(INTVAL NUMBER)	   
; 

	
// statevars
	
statevardecllist:
	'(' statevardecl (',' statevardecl)* ')'
->
	^(ASSRT_STATEVARDECLLIST statevardecl+)
;
	
statevardecl:
	variable ':=' arith_expr
->
	^(ASSRT_STATEVARDECL variable arith_expr)
;
	
	
// root	
	
root:  
	bool_expr
->
	^(ROOT bool_expr)
;

bool_expr:
	bin_bool_expr
|
	par_unary_bool_expr
;
	
bin_bool_expr:
	'(' par_unary_bool_expr BIN_BOOL_OP bool_expr ')'
->
	^(BINBOOLEXPR par_unary_bool_expr BIN_BOOL_OP bool_expr)
;

par_unary_bool_expr:
	unary_bool_expr
|
	'(' unary_bool_expr ')'
-> 	
	unary_bool_expr
|
	IDENTIFIER unint_fun_arg_list
->
	^(UNPRED IDENTIFIER unint_fun_arg_list)
|
	bin_comp_expr
; 

unary_bool_expr:
	TRUE_KW
->
	^(TRUE)
|
	FALSE_KW
->
	^(FALSE)
;
	
unint_fun_arg_list:
	'(' ')'
->
	^(EMPTY_LIST)
|
	'(' arith_expr (',' arith_expr )* ')'
->
	^(ARITH_EXPR_LIST arith_expr+)
;


bin_comp_expr:
	'(' arith_expr BIN_COMP_OP arith_expr ')'
-> 
	^(BINCOMPEXPR arith_expr BIN_COMP_OP arith_expr)
; 

arith_expr: 
	bin_arith_expr
|
	par_unary_arith_expr
; 

par_unary_arith_expr: 
	unary_arith_expr
|
	'(' unary_arith_expr ')'
->
	unary_arith_expr
;

unary_arith_expr: 
	variable
|
	num
;
 
bin_arith_expr:
	'(' par_unary_arith_expr BIN_ARITH_OP arith_expr ')'
->
	^(BINARITHEXPR par_unary_arith_expr BIN_ARITH_OP arith_expr)
;
