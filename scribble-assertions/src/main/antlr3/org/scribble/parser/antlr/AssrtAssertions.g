grammar AssrtAssertions;

options
{
	language = Java;
	output = AST;
	ASTLabelType = CommonTree;
	//backtrack = true;  // backtracking disabled by default? Is it bad to require this option?
	//memoize = true;
}

tokens {
	TRUE_KW = 'True';
	FALSE_KW = 'False';

	ROOT = 'root-node'; 
	BEXPR = 'binary-expr-node'; 
	AEXPR = 'arithmetic-expr'; 
	BOPNODE = 'binary-op-node'; 
	CEXPR = 'compare-expr-node'; 
	VAR = 'var-node'; 
	VALUE = 'value-node'; 
	TRUE_FORMULA = 'formula-true';
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


@members {
  public static CommonTree ast(String source) throws RecognitionException {
    AssrtAssertionsLexer lexer = new AssrtAssertionsLexer(new ANTLRStringStream(source));
    AssrtAssertionsParser parser = new AssrtAssertionsParser(new CommonTokenStream(lexer));
    return (CommonTree)parser.parse().getTree();
  }
}

// Not referred to explicitly, deals with whitespace implicitly (don't delete this)
WHITESPACE:
	('\t' | ' ' | '\r' | '\n'| '\u000C')+
	{
		$channel = HIDDEN;
	}
;

/*fragment OPSYMBOL: 
	'=' | '>' | '<'  | '||' | '&&'
;*/ 

fragment LETTER:
	'a'..'z' | 'A'..'Z'
;

fragment DIGIT:
	'0'..'9'
;

IDENTIFIER:
	(LETTER)* 
;  

NUMBER: 
	(DIGIT)*	 
; 

START_TOKEN: '['; 
END_TOKEN: ']'; 
//START_TOKEN: '\"'; 
//END_TOKEN: '\"'; 

/*ASSERTION: 
	(LETTER | DIGIT | OPSYMBOL | WHITESPACE)*
; */

COMP:
    '>' | '<' | '='		 
; 

OP:	 
  '+' | '-' | '*' 	
; 

BOP:
   '||' | '&&'
; 	 	

variable: 
	IDENTIFIER -> ^(VAR IDENTIFIER)
; 	  


num: 
	NUMBER -> ^(VALUE NUMBER)	   
; 

parse:  
  START_TOKEN assertion END_TOKEN -> ^(ROOT assertion)
;

assertion: 
	boolexpr
;

boolexpr:
	bin_bool_expr
|
	unary_bool_expr
;

bin_bool_expr:
	'(' unary_bool_expr BOP boolexpr ')'
->
	^(BEXPR unary_bool_expr BOP boolexpr)
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
	compexpr
; 

	

compexpr:
	expr COMP expr
-> 
	^(CEXPR expr COMP expr)
; 

expr: 
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
	'(' unary_arith_expr OP expr ')'
->
	^(AEXPR unary_arith_expr OP expr)
;
