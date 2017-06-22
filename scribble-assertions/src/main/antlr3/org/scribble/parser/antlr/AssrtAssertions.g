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
	ROOT = 'root-node'; 
	BEXPR = 'binary-expr-node'; 
	AEXPR = 'arithmetic-expr'; 
	BOPNODE = 'binary-op-node'; 
	CEXPR = 'compare-expr-node'; 
	VAR = 'var-node'; 
	VALUE = 'value-node'; 
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

WHITESPACE:
	('\t' | ' ' | '\r' | '\n'| '\u000C')+
	{
		$channel = HIDDEN;
	}
;

fragment OPSYMBOL: 
	'=' | '>' | '<'  | '||' | '&&'
;  

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
	bexpr -> bexpr
; 
	
bexpr:	 
   compexpr BOP bexpr 
   -> 	^(BEXPR compexpr BOP bexpr)
   | compexpr -> compexpr
;

compexpr: expr COMP expr -> 
	^(CEXPR expr COMP expr)
	| expr -> expr
; 

expr: 
	variable OP num -> ^(AEXPR OP variable num)
|	variable -> variable
|	num -> num
; 
 