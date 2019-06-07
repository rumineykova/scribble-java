//$ java -cp scribble-parser/lib/antlr-3.5.2-complete.jar org.antlr.Tool -o scribble-assertions/target/generated-sources/antlr3 scribble-assertions/src/main/antlr3/org/scribble/parser/antlr/AssrtScribble.g

// Cygwin/Windows:
//$ java -cp scribble-parser/lib/antlr-3.5.2-complete.jar org.antlr.Tool -o scribble-assertions/target/generated-sources/antlr3/org/scribble/parser/antlr scribble-assertions/src/main/antlr3/org/scribble/parser/antlr/AssrtScribble.g
//$ mv scribble-assertions/target/generated-sources/antlr3/org/scribble/parser/antlr/AssrtScribble.tokens scribble-assertions/target/generated-sources/antlr3/


grammar AssrtScribble;


options
{
	language = Java;
	output = AST;
	ASTLabelType = ScribNodeBase;
}


tokens
{
  /* Parser "input" constants (lexer output; keywords, Section 2.4)
   */
  MODULE_KW = 'module';
  IMPORT_KW = 'import';
	DATA_KW = 'data';
  SIG_KW = 'sig';
  TYPE_KW = 'type';
  PROTOCOL_KW = 'protocol';
  AS_KW = 'as';

  GLOBAL_KW = 'global';
  LOCAL_KW = 'local';  // Currently not parsed, but may be generated
  EXPLICIT_KW = 'explicit';
  AUX_KW = 'aux';

  ROLE_KW = 'role';
  SELF_KW = 'self';  // Currently not parsed, but may be generated

  FROM_KW = 'from';
  TO_KW = 'to';
  CONNECT_KW = 'connect';
  WRAP_KW = 'wrap';

  DISCONNECT_KW = 'disconnect';
  AND_KW = 'and';

  CHOICE_KW = 'choice';
  AT_KW = 'at';
  OR_KW = 'or';

  REC_KW = 'rec';
  CONTINUE_KW = 'continue';
  DO_KW = 'do';

  // Assrt
	ASSERT_KW = 'assert';
	
	
  /* Scribble AST token types (corresponding to the Scribble BNF).  
   * These token types are used by ScribTreeAdaptor to create the output nodes
   * using the org.scribble.ast classes.
   * (Trying to construct those classes directly from here doesn't seem to work
   * well for most cases.)
   * These tokens are ANTLR "imaginary tokens": they are derived by the ANTLR
   * "rewrite rules" on the actual source tokens.
   * The specific value of these tokens aren't important (the constants are
   * accessed via fields of ScribbleParser).
   * As a naming convention, we use a few "_" suffixes: _KW, _NAME, _LIT and
   * _LIST.
   */

  // Special cases
  EMPTY_OP;

	// Simple names "constructed directly", e.g., t=ID -> ID<...Node>[$t] 

	// Compound names
  GPROTO_NAME;  // Parse specifically as GProto, for ScribTreeAdaptor.create
  LPROTO_NAME;
  MODULE_NAME;
  DATA_NAME;   // N.B. distinct from DATAPARAM_NAME
  SIG_NAME;   // N.B. distinct from SIGPARAM_NAME

	// Sig literals
  SIG_LIT;
  PAYELEM_LIST;
  UNARY_PAYELEM;
  GDELEG_PAYELEM;

	// Scribble "language" nodes, i.e., the nodes that are not "session nodes" (see below)
  MODULE;
  MODULEDECL;
  IMPORTMODULE;

  DATADECL;
  SIGDECL;
  GPROTODECL;
  PROTOMOD_LIST;

  GPROTOHEADER;
  ROLEDECL_LIST;
  ROLEDECL;
  PARAMDECL_LIST;
  DATAPARAMDECL;
  SIGPARAMDECL;
  
  GPROTODEF;
  GPROTOBLOCK;
  
 	// Scribble "session nodes" -- cf. org.scribble.core.type.session vs. org.scribble.core.lang
  GINTERSEQ;

  GMSGTRANSFER;
  GCONNECT;
  GDCONN;  // TODO: rename GDISCONN
  GWRAP;

  GCONTINUE;
  GDO;

  ROLEARG_LIST;  // Cf. ROLEDECL
  ROLEARG;
  NONROLEARG_LIST;  // Cf. ...PARAMDECL
  NONROLEARG;

  GCHOICE;
  GRECURSION;

  // Locals: currently not directly parsed, but needed for, e.g., projection
  LPROTODECL;

  LPROTOHEADER;
  LSELFROLEDECL;
  
  LPROTODEF;
  LPROTOBLOCK;
  
  LINTERSEQ;

  LSEND;
  LRECV;
  LACC;
  LREQ;
  LDCONN;
  LCLIENTWRAP;
  LSERVERWRAP;

  LCONTINUE;
  LDO;

  LCHOICE;
  LRECURSION;


  /*
   * Assrt
   */
  
  ASSRT_MODULE;

	ASSRT_ANNOTDATAELEM; 

  // Empty assertions first parsed as original (not Assert) categories -- later translated to null assertion Assrts via AssrtAntlrToScribParser
	ASSRT_GPROTOHEADER;
	ASSRT_STATEVARANNOTNODE;
	//ASSRT_SVAR_ANNOT;
	ASSRT_STATEVARDECL_LIST;
	ASSRT_STATEVARDECL;

	ASSRT_GMSGTRANSFER;
	ASSRT_GCONNECT;

	ASSRT_GDO;
	
	ASSRT_ASSERT;
	ASSRT_UNINTFUNARGLIST;
	ASSRT_UNINTFUNARG;

	ASSRT_LPROTOHEADER;

	ASSRT_LSEND;
	ASSRT_LREQ;

	ASSRT_LDO;
}


@lexer::header
{
  package org.scribble.parser.antlr;
}


@lexer::members
{
  @Override    
  public void displayRecognitionError(String[] tokenNames, 
  		RecognitionException e)
  {
    super.displayRecognitionError(tokenNames, e);
    System.exit(1);
  }
}


// Must come after tokens?
@parser::header
{
  package org.scribble.parser.antlr;
  
  import org.scribble.ast.NonRoleArg;
  import org.scribble.ast.ScribNodeBase;
  import org.scribble.ast.UnaryPayElem;
  import org.scribble.ast.name.qualified.DataNameNode;
  import org.scribble.ast.name.simple.AmbigNameNode;
  import org.scribble.ast.name.simple.DataParamNode;
  import org.scribble.ast.name.simple.IdNode;
  import org.scribble.ast.name.simple.OpNode;
  import org.scribble.ast.name.simple.RecVarNode;
  import org.scribble.ast.name.simple.RoleNode;
  import org.scribble.ast.name.simple.SigParamNode;

  import org.scribble.ext.assrt.ast.AssrtAExprNode;
  import org.scribble.ext.assrt.ast.AssrtBExprNode;
  import org.scribble.ext.assrt.ast.name.simple.AssrtIntVarNameNode;
}


@parser::members
{
  // Abort tool run on parsing errors (and display user-friendly message) -- obsoletes CommonErrorNode check?
  @Override    
  public void displayRecognitionError(String[] tokenNames, 
  		RecognitionException e)
  {
    super.displayRecognitionError(tokenNames, e);
    System.exit(1);
  }

	// Currently unused -- TODO: check later in intermed translation, instead of parsing
  public static CommonTree checkId(CommonTree id)
  {
  	if (id.getText().contains("__"))
  	{
			System.err.println("Double underscores are reserved: " + id);
			System.exit(1);
  	}
  	return id;
  }
}


/****************************************************************************
 * Chapter 2 Lexical Structure (Lexer rules)
 ***************************************************************************/

/* *  // Double star here not accepted by ANTLR...
 * Section 2.1 White space (Section 2.1)
 */
// Not referred to explicitly, deals with whitespace implicitly (don't delete this)
WHITESPACE:
	('\t' | ' ' | '\r' | '\n'| '\u000C')+
	{
		$channel = HIDDEN;
	}
;


/**
 * Section 2.2 Comments
 */
COMMENT:
	'/*' .* '*/'
	{
		$channel=HIDDEN;
	}
;

LINE_COMMENT:
	'//' ~('\n'|'\r')* '\r'? '\n'
  {
		$channel=HIDDEN;
	}
;


/**
 * Section 2.3 Identifiers
 */
ID:
	(LETTER | DIGIT | UNDERSCORE)*  
      /* Underscore currently can cause ambiguities in the API generation naming
       * scheme But maybe only consecutive underscores are the problem -- cannot
       * completely disallow underscores as needed for projection naming scheme
       * Or disallow underscores only for role/op/messagesig names
       */
;

fragment SYMBOL:
	'{' | '}' | '(' | ')' | '[' | ']' | ':' | '/' | '\\' | '.' | '\#'
|
	'&' | '?' | '!'  | UNDERSCORE
|
	'|' | '¬' | ',' | '=' | '<' | '>' | '+' | '-' | '*'  // Assrt
;

// Comes after SYMBOL due to an ANTLR syntax highlighting issue involving quotes.
// CHECKME: parser doesn't work without locating the quotes here? (e.g. if inlined into parser rules)
EXTID:
	'\"' (LETTER | DIGIT | SYMBOL | WHITESPACE)* '\"'  // N.B. WHITESPACE, for assertions white space
;

fragment LETTER:
	'a'..'z' | 'A'..'Z'
;

fragment DIGIT:
	'0'..'9'
;

fragment UNDERSCORE:
	'_'
;


/*
// Assertion formula
ASSRT_EXPR: 
	(LETTER | DIGIT | ASSRT_SYMBOL | WHITESPACE)*
; 
	'@' (LETTER | DIGIT | ASSRT_SYMBOL | WHITESPACE)* ';'

fragment ASSRT_SYMBOL: 
	'=' | '>' | '<'  | '||' | '&&' | '+' | '-' | '*' | '(' | ')' | ','
;  
*/

 
/****************************************************************************
 * Chapter 3 Syntax (Parser rules)
 ***************************************************************************/

/* * // Double star here not accepted by ANTLR...
 * Section 3.1 Primitive Names
 */
//simplename: id=ID -> { checkId($id.tree) } ;  // How to integrate with ID<RoleNode>[$t] ?

// "The TreeAdaptor is not called; instead [the] constructors are invoked directly."
// "Note that parameters are not allowed on token references to the left of ->:"
// "Use imaginary nodes as you normally would, but with the addition of the node type:"  // But currently, ID token itself unchanged and ttype int ends up discarded
ambigname: t=ID -> ID<AmbigNameNode>[$t] ;
dataparamname: t=ID -> ID<DataParamNode>[$t] ; 
opname: -> ^(EMPTY_OP) | t=ID -> ID<OpNode>[$t] ;
recvarname: t=ID -> ID<RecVarNode>[$t] ;
rolename: t=ID -> ID<RoleNode>[$t] ;
sigparamname: t=ID -> ID<SigParamNode>[$t] ;

// Assrt
assrt_intvarname: t=ID -> ID<AssrtIntVarNameNode>[$t] ;  // N.B. Specifically int


/**
 * Section 3.2.1 Package, Module and Module Member Names
 */
// May be compound or simple
gprotoname: t=ID ('.' ID)* -> ^(GPROTO_NAME ID+) ;
modulename: t=ID ('.' ID)* -> ^(MODULE_NAME ID+) ;

// Compound only (cf., e.g., gprotoname; cf. simpledataname)
qualifieddataname: t=ID '.' ID ('.' ID)* -> ^(DATA_NAME ID+) ;

// Cf. primitive names, above
simpledataname: t=ID -> ^(DATA_NAME ID) ;
simplegprotoname: t=ID -> ^(GPROTO_NAME ID) ;
simplemodulename: t=ID -> ^(MODULE_NAME ID) ;
simplesigname: t=ID -> ^(SIG_NAME ID) ;


/**
 * Section 3.2.2 Top-level Module Structure
 * Section 3.2.3 Module Declarations
 */
// "References to tokens with rewrite not found on left of -> are imaginary tokens."
// Inlined moduledecl to make token label work
module:  // Must be "module" (cf. "assrt_module")
	t=MODULE_KW modulename ';' importmodule* nonprotodecl* assert_fundecl*  // Assrt  
	protodecl* EOF
->
	^(ASSRT_MODULE ^(MODULEDECL modulename) importmodule* nonprotodecl*
			assert_fundecl* protodecl*)
;
// moduledecl: MODULE_KW<ModuleDecl>^ modulename ';'  
		// "Become root" ^ cannot be on rhs? -- so "manually" rewrite to Scribble AST token types


/**
 * Section 3.3 Import Declarations
 */
importmodule:
	t=IMPORT_KW modulename (AS_KW alias=simplemodulename)? ';'
->
	^(IMPORTMODULE modulename $alias?)
;


/**
 * Section 3.4 "Non Protocol" Declarations 
 */
nonprotodecl:
	datadecl | sigdecl ;

datadecl:
	// Deprecate TYPE_KW ?
	t=TYPE_KW '<' schema=ID '>' extName=EXTID FROM_KW
	extSource=EXTID AS_KW alias=simpledataname ';'
->
	// alias first to be uniform with other NameDeclNode (getRawNameNodeChild)
	^(DATADECL $alias $schema $extName $extSource)
|
	// CHECKME: duplicated above, because t=(TYPE_KW | DATA_KW) *sometimes* causes null token NPEs... 
	t=DATA_KW '<' schema=ID '>' extName=EXTID FROM_KW
	extSource=EXTID AS_KW alias=simpledataname ';'
->
	// alias first to be uniform with other NameDeclNode (getRawNameNodeChild)
	^(DATADECL $alias $schema $extName $extSource)
;

sigdecl:
	t=SIG_KW '<' schema=ID '>' extName=EXTID FROM_KW extSource=EXTID AS_KW
	alias=simplesigname ';'
->
	// alias first to be uniform with other NameDeclNode (getRawNameNodeChild)
	^(SIGDECL $alias $schema $extName $extSource)
;


// Assrt  // Currently "deprecated"
assert_fundecl:
	ASSERT_KW ID unintfunarglist simpledataname '=' EXTID ';'
/*->
	^(...)  // TODO*/
;

unintfunarglist:
	'(' (unintfunarg (',' unintfunarg)*)? ')'
->
	^(ASSRT_UNINTFUNARGLIST unintfunarg*)
;

unintfunarg:
	assrt_intvarname ':' simpledataname
-> 
	^(ASSRT_UNINTFUNARG assrt_intvarname simpledataname)
;


/**
 * Section 3.5 Message Signatures
 */
siglit:
	opname '(' payelems ')' -> ^(SIG_LIT opname payelems)
;
// CHECKME: how to apply  in such situations?

payelems:
	-> ^(PAYELEM_LIST)
|
	payelem (',' payelem)* -> ^(PAYELEM_LIST payelem+)
;
	
//	{parsePayloadElem($qualifiedname.tree)}  // Use ".text" instead of ".tree" for token String 
payelem:
	// Payload element must be a data kind, cannot be a sig name
	// Qualified name must be a data type name
	// Also subsumes simple names, could be a data *param*
	gprotoname '@' rolename -> ^(GDELEG_PAYELEM gprotoname rolename)
|
	ambigname -> ^(UNARY_PAYELEM ambigname)
|
	qualifieddataname -> ^(UNARY_PAYELEM qualifieddataname)	

// Assrt
|
	assrt_intvarname ':' ambigname
->
	^(ASSRT_ANNOTDATAELEM assrt_intvarname ambigname)
|
	assrt_intvarname ':' qualifieddataname
-> 
	^(ASSRT_ANNOTDATAELEM assrt_intvarname qualifieddataname)
;
// TODO: assrt_intvarname ':' ambigname -- for simplenames


/**
 * Section 3.6 Protocol Declarations
 */
protodecl:
	gprotodecl
;


/**
 * Section 3.7 Global Protocol Declarations
 */
gprotodecl:
	protomods assrt_gprotoheader gprotodef
->
	^(GPROTODECL protomods assrt_gprotoheader gprotodef)
;
  
// "aux" must come before "explicit"
protomods:
                       -> ^(PROTOMOD_LIST)
| t=AUX_KW             -> ^(PROTOMOD_LIST AUX_KW)
| t=AUX_KW EXPLICIT_KW -> ^(PROTOMOD_LIST AUX_KW EXPLICIT_KW)
| t=EXPLICIT_KW        -> ^(PROTOMOD_LIST EXPLICIT_KW)
;

// N.B. intermed translation uses full proto name

assrt_gprotoheader:
// Assrt
	GLOBAL_KW PROTOCOL_KW simplegprotoname paramdecls roledecls
->
	^(ASSRT_GPROTOHEADER simplegprotoname paramdecls roledecls   // CHECKME: non-empty paramdecls currently broken for assrt?
			^(ASSRT_STATEVARDECL_LIST))
|
	GLOBAL_KW PROTOCOL_KW simplegprotoname roledecls '@' assrt_statevardecls 
			assrt_statevarassrt? 
			//assrt_statevar_annot
			// TODO: paramdecls
->
	^(ASSRT_GPROTOHEADER simplegprotoname ^(PARAMDECL_LIST) roledecls 
			//assrt_statevar_annot) 
			assrt_statevardecls
			assrt_statevarassrt?)
			// use ".tree" for Tree instead of .text String
|
	GLOBAL_KW PROTOCOL_KW simplegprotoname roledecls '@' assrt_statevarassrt
			// TODO: paramdecls
->
	^(ASSRT_GPROTOHEADER simplegprotoname ^(PARAMDECL_LIST) roledecls 
			//{null}  // ANTLR is filtering this somewhere?  added by the parser, but not present in getChildren
			^(ASSRT_STATEVARDECL_LIST)  // Cf. ^(PARAMDECL_LIST)
			assrt_statevarassrt)
;
// Following same pattern as gmsgtransfer: explicitly invoke AssertionsParser, and extra assertion element only for new category
// -- later translation by AssrtAntlrToScribParser converts original nodes to empty-assertion new nodes
	
assrt_statevarassrt:  // FIXME: refactor with assrt_gmsgtransfer_annot
	t=EXTID
->
	EXTID<AssrtBExprNode>[$t, AssertionsParser.parseAssertion($t.text)]
;

assrt_statevardecls:
	'<' assrt_statevardecl (',' assrt_statevardecl)* '>'
->
	^(ASSRT_STATEVARDECL_LIST assrt_statevardecl+)
;

assrt_statevardecl:
	assrt_intvarname ':=' id=EXTID
->
	^(ASSRT_STATEVARDECL assrt_intvarname 
			EXTID<AssrtAExprNode>[$id, AssertionsParser.parseArithAnnotation($id.text)])
;
	
/*assrt_statevar_annot:
	'@' t=EXTID
->
	////{AssertionsParser.parseStateVarAnnot($EXTID.text)}
	//EXTID<AssrtBExprNode>[$t, AssertionsParser.parseAssertion($t.text)]
	^(ASSRT_STATEVARANNOTNODE EXTID<AssrtBExprNode>[$t, AssertionsParser.parseAssertion($t.text)])
;*/

roledecls: 
	'(' roledecl (',' roledecl)* ')' -> ^(ROLEDECL_LIST roledecl+)
;

roledecl:
	t=ROLE_KW rolename -> ^(ROLEDECL rolename)
;

paramdecls:
	-> ^(PARAMDECL_LIST)
|
	t='<' (paramdecl (',' paramdecl)*)? '>' -> ^(PARAMDECL_LIST paramdecl*)
;

paramdecl: dataparamdecl | sigparamdecl ;

dataparamdecl: 
	t=TYPE_KW dataparamname -> ^(DATAPARAMDECL dataparamname)
|
	t=DATA_KW dataparamname -> ^(DATAPARAMDECL dataparamname)
			// TODO: refactor -- cf. datadecl
;

sigparamdecl:  
	t=SIG_KW sigparamname -> ^(SIGPARAMDECL sigparamname)
;


/**
 * Section 3.7.1 Global Protocol Definitions
 */
gprotodef:
	gprotoblock -> ^(GPROTODEF gprotoblock) ;


/**
 * Section 3.7.3 Global Interaction Blocks and Sequences
 */
gprotoblock:
	t='{' gseq '}' -> ^(GPROTOBLOCK gseq)
;

gseq:
	ginteraction* -> ^(GINTERSEQ ginteraction*)
;

ginteraction:
	// Simple session node: directed interaction
	assrt_gconnect | assrt_gmsgtransfer
|
	// Simple session node: basic interaction
	gwrap | gdisconnect 
|
	// Simple session node: other
	gcontinue | gdo 
|
	// Compound session node
	gchoice | grecursion
; 


/**
 * Section 3.7.4 Global Message Transfer
 */
message:
	siglit | ambigname  // ambigname = sig name or sig param name
;  

// TODO: qualified (sig)names -- although qualified signame subsumes param name case
assrt_gmsgtransfer:
	//message FROM_KW rolename TO_KW rolename (',' rolename )* ';'
	message FROM_KW rolename TO_KW rolename ';'
->
	//^(GMSGTRANSFER message rolename rolename+)
	^(ASSRT_GMSGTRANSFER message rolename rolename)// {null})
	// Return base GLOBALMESSAGETRANSFER (i.e., no ASSRT_EMPTY_ASSERTION)
	// Rely on AssrtAntlrToScribParser to use AssrtAstFactory to create AssrtGMsgTransfer with empty assertion -- CHECKME: create empty/true assertion here (and "deprecate" the base methods?)

// Assrt
| 
	//message FROM_KW rolename TO_KW rolename (',' rolename )* ';' '@' EXTID
	message FROM_KW rolename TO_KW rolename ';' assrt_gmsgtransfer_annot
->
	^(ASSRT_GMSGTRANSFER message rolename rolename assrt_gmsgtransfer_annot)
			//{AssertionsParser.parseAssertion($EXTID.text)})
			// N.B. calling a separate parser this way loses line/char number information
;
// TODO: multisend
	
assrt_gmsgtransfer_annot:
	'@' t=EXTID
->
	EXTID<AssrtBExprNode>[$t, AssertionsParser.parseAssertion($t.text)]
;	
	
assrt_gconnect:
	message CONNECT_KW rolename TO_KW rolename ';'
->
	//^(GCONNECT message rolename rolename)
	^(ASSRT_GCONNECT message rolename rolename)// {null})
|
	t=CONNECT_KW rolename TO_KW rolename ';'
->
	//^(GCONNECT ^(SIG_LIT ^(EMPTY_OP) ^(PAYELEM_LIST)) rolename rolename)
	^(GCONNECT ^(SIG_LIT ^(EMPTY_OP) ^(PAYELEM_LIST)) rolename rolename)// {null})
      // CHECKME: deprecate? i.e., require "()" as for message transfers?  i.e., simply delete this rule?

// Assrt
|
	//ASSRT_EXPR message CONNECT_KW rolename TO_KW rolename ';'
	message CONNECT_KW rolename TO_KW rolename ';' '@' EXTID
->
	^(ASSRT_GCONNECT message rolename rolename 
			{AssertionsParser.parseAssertion($EXTID.text)})
;
/*
|
	//ASSRT_EXPR CONNECT_KW rolename TO_KW rolename ';'
	t=CONNECT_KW rolename TO_KW rolename ';' '@' EXTID
->
	^(ASSRT_GCONNECT {AssertionsParser.parseAssertion($EXTID.text)} 
			rolename rolename ^(MESSAGESIGNATURE EMPTY_OPERATOR ^(PAYLOAD)))  // Empty message sig duplicated from messagesignature
;
*/

gdisconnect:
	t=DISCONNECT_KW rolename AND_KW rolename ';'
->
	^(GDCONN rolename rolename)
;

gwrap:
	t=WRAP_KW rolename TO_KW rolename ';'
->
	^(GWRAP rolename rolename)
;


/**
 * Section 3.7.5 Global Choice
 */
gchoice:
	t=CHOICE_KW AT_KW rolename gprotoblock (OR_KW gprotoblock)*
->
	^(GCHOICE rolename gprotoblock+)
;


/**
 * Section 3.7.6 Global Recursion
 */
grecursion:
	t=REC_KW recvarname gprotoblock
->
	^(GRECURSION recvarname gprotoblock)
;

gcontinue:
	t=CONTINUE_KW recvarname ';'
->
	^(GCONTINUE recvarname)
;


/**
 * Section 3.7.9 Global Do
 */
gdo:
	DO_KW gprotoname nonroleargs roleargs ';'
->
	^(GDO gprotoname nonroleargs roleargs)

// Assrt
|
	DO_KW simplegprotoname roleargs ';' '@' //assrt_statevarargs
			'<' assrt_statevararg (',' assrt_statevararg)* '>'
->
	^(ASSRT_GDO simplegprotoname ^(NONROLEARG_LIST) roleargs assrt_statevararg+)
;
// TODO: non-role args, annot

/*assrt_statevarargs:
	'<' assrt_statevararg (',' assrt_statevararg)* '>'
->
	^(ASSRT_... assrt_statevararg+)
;*/

// TODO: refactor with assrt_statevardecl
assrt_statevararg:  // ScribNode "wrappers" (for EXTID/Assertions.g), cf. simple names (for ID)
	id=EXTID
->
	EXTID<AssrtAExprNode>[$id, AssertionsParser.parseArithAnnotation($id.text)]
;

roleargs:
	t='(' rolearg (',' rolearg)* ')' -> ^(ROLEARG_LIST rolearg+)
;

rolearg:
	rolename -> ^(ROLEARG rolename) ;

nonroleargs:
	-> ^(NONROLEARG_LIST)
|
	t='<' (nonrolearg (',' nonrolearg)*)? '>' -> ^(NONROLEARG_LIST nonrolearg*)
;

// Grammatically same as message, but qualifiedname case may also be a payload type
// {parseNonRoleArg($qualifiedname.tree)}  // Like payelem, simple names need disambiguation
nonrolearg:
	siglit -> ^(NONROLEARG siglit)
|
	ambigname -> ^(NONROLEARG ambigname)
|
	qualifieddataname -> ^(NONROLEARG qualifieddataname)  // FIXME: sig name -- need an ambig qualified name
;

