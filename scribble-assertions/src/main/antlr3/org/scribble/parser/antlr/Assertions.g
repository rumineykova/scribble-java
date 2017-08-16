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
	//EMPTY_LIST = 'EMPTY_LIST';
	
	ROOT = 'ROOT'; 
	
	BOOLEXPR  = 'BOOLEXPR'; 
	COMPEXPR  = 'COMPEXPR'; 
	ARITHEXPR = 'ARITHEXPR'; 
	
	UNFUN        = 'UNFUN';
	UNFUNARGLIST = 'UNFUNARGLIST';

	INTVAR = 'INTVAR'; 
	INTVAL = 'INTVAL'; 

	TRUE  = 'TRUE';
	FALSE = 'FALSE';
	
	ASSRT_STATEVARDECLLIST          = 'ASSRT_STATEVARDECLLIST';
	ASSRT_STATEVARDECL              = 'ASSRT_STATEVARDECL';
	ASSRT_STATEVARDECLLISTASSERTION = 'ASSRT_STATEVARDECLLISTASSERTION';
	ASSRT_STATEVARARGLIST           = 'ASSRT_STATEVARARGLIST';
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

	public static CommonTree parseStateVarArgList(String source) throws RecognitionException
	{
		source = source.substring(1, source.length()-1);  // Remove enclosing quotes -- cf. AssrtScribble.g EXTIDENTIFIER
		AssertionsLexer lexer = new AssertionsLexer(new ANTLRStringStream(source));
		AssertionsParser parser = new AssertionsParser(new CommonTokenStream(lexer));
		return (CommonTree) parser.statevararglist().getTree();
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
	'(' statevardecl (',' statevardecl)* ')' (bool_expr)?
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
	
	
// root	
	
root:  
	expr EOF
->
	^(ROOT expr)
;
// ANTLR seems to force a pattern where expr "kinds" are nested under a single expr

expr:
	bool_expr
;
	
bool_expr:
	bool_unary_expr (op=('||' | '&&') bool_unary_expr)?
->
	^(BOOLEXPR bool_unary_expr $op? bool_unary_expr?)
;
	
bool_unary_expr:
	TRUE_KW
->
	^(TRUE)
|
	FALSE_KW
->
	^(FALSE)
|
	comp_expr
;

comp_expr:
	arith_expr (op=('<' | '>' | '=') arith_expr)?
->
	^(COMPEXPR arith_expr $op? arith_expr?)
;
	
arith_expr:
	arith_unary_expr (op=('+' | '-') arith_unary_expr)?
->
	^(ARITHEXPR arith_unary_expr $op? arith_unary_expr?)
;
	
arith_unary_expr:
	variable
|
	num
|
	par_expr
|
	unint_fun
;
	
par_expr:
	'(' expr ')'
->
	expr
;

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
	
	
	
	
	
	
	
	
	
/*bool_expr:
	bin_bool_expr
|
	par_unary_bool_expr
;
	
bin_bool_expr:
	'(' par_unary_bool_expr BIN_BOOL_OP bool_expr ')'
->
	^(BOOLEXPR par_unary_bool_expr BIN_BOOL_OP bool_expr)
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
	^(UNFUN IDENTIFIER unint_fun_arg_list)
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
	^(UNFUNARGLIST arith_expr+)
;


bin_comp_expr:
	'(' arith_expr BIN_COMP_OP arith_expr ')'
-> 
	^(COMPEXPR arith_expr BIN_COMP_OP arith_expr)
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
	^(ARITHEXPR par_unary_arith_expr BIN_ARITH_OP arith_expr)
;
*/
