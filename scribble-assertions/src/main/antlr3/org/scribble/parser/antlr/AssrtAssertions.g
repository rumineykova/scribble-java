//$ java -cp scribble-parser/lib/antlr-3.5.2-complete.jar org.antlr.Tool -o scribble-assertions/target/generated-sources/antlr3 scribble-assertions/src/main/antlr3/org/scribble/parser/antlr/AssrtAssertions.g

// Windows:
//$ java -cp scribble-parser/lib/antlr-3.5.2-complete.jar org.antlr.Tool -o scribble-assertions/target/generated-sources/antlr3/org/scribble/parser/antlr scribble-assertions/src/main/antlr3/org/scribble/parser/antlr/AssrtAssertions.g
//$ mv scribble-assertions/target/generated-sources/antlr3/org/scribble/parser/antlr/AssrtAssertions.tokens scribble-assertions/target/generated-sources/antlr3/


grammar AssrtAssertions;

options
{
	language = Java;
	output = AST;
	ASTLabelType = CommonTree;
}

tokens {
	TRUE_KW = 'True';
	FALSE_KW = 'False';

	ROOT  = 'root-node'; 
	BEXPR = 'binary-expr-node'; 
	CEXPR = 'compare-expr-node'; 
	AEXPR = 'arithmetic-expr'; 
	VAR   = 'var-node'; 
	VALUE = 'value-node'; 

	TRUE_FORMULA  = 'formula-true';
	FALSE_FORMULA = 'formula-false';
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
  
	public static CommonTree antlrParse(String source) throws RecognitionException
	{
		source = source.substring(1, source.length());
		AssrtAssertionsLexer lexer = new AssrtAssertionsLexer(new ANTLRStringStream(source));
		AssrtAssertionsParser parser = new AssrtAssertionsParser(new CommonTokenStream(lexer));
		return (CommonTree) parser.parse().getTree();
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
	^(VAR IDENTIFIER)
; 	  

num: 
	NUMBER
->
	^(VALUE NUMBER)	   
; 

parse:  
	bool_expr
->
	^(ROOT bool_expr)
;

bool_expr:
	bin_bool_expr
|
	unary_bool_expr
;
	
bin_bool_expr:
	'(' unary_bool_expr BIN_BOOL_OP bool_expr ')'
->
	^(BEXPR unary_bool_expr BIN_BOOL_OP bool_expr)
;

unary_bool_expr:
	TRUE_KW
->
	^(TRUE_FORMULA)
|
	FALSE_KW
->
	^(FALSE_FORMULA)
|
	bin_comp_expr
; 

bin_comp_expr:
	'(' arith_expr BIN_COMP_OP arith_expr ')'
-> 
	^(CEXPR arith_expr BIN_COMP_OP arith_expr)
; 

arith_expr: 
	unary_arith_expr
|
	binary_arith_expr
; 

unary_arith_expr: 
	variable
|
	num
;
 
binary_arith_expr:
	'(' unary_arith_expr BIN_ARITH_OP arith_expr ')'
->
	^(AEXPR unary_arith_expr BIN_ARITH_OP arith_expr)
;
