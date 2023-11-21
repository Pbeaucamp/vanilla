grammar Mdx;

options {
	//k = 2;                          // two token lookahead
	//exportVocab=Java;               // Call its vocabulary "Java"
	output = AST;
	//testLiterals=false;    
	//charVocabulary='\u0001';'\uFFFF';
	//codeGenBitsetTestThreshold=20;
	//caseSensitiveLiterals=false;	// uses CommonAST by default
}



tokens {
// Keywords
	AND 			= 'AND';
	AS 				= 'AS';
	CASE 			= 'CASE';
	CELL 			= 'CELL';
	CELL_ORDINAL 	= 'CELL_ORDINAL';
	CREATE			= 'CREATE';
	DIMENSION 		= 'DIMENSION';
	ELSE 			= 'ELSE';
	EMPTY 			= 'EMPTY';
	END 			= 'END';
	FORMATTED_VALUE = 'FORMATTED_VALUE';
	FROM 			= 'FROM';
	GLOBAL			= 'GLOBAL';
	MEMBER 			= 'MEMBER';
	NON 			= 'NON';
	NOT 			= 'NOT';
	ON 				= 'ON';
	OR 				= 'OR';
	PROPERTIES 		= 'PROPERTIES';
	SELECT 			= 'SELECT';
	SESSION			= 'SESSION';
	SET 			= 'SET';
	THEN 			= 'THEN';
	VALUE 			= 'VALUE';
	WHEN 			= 'WHEN';
	WHERE 			= 'WHERE';
	XOR 			= 'XOR';
	WITH 			= 'WITH';
	
	CHILDREN        = 'CHILDREN';
	MEMBERS         = 'MEMBERS';
	
// Functions
	UNION = 'UNION';
	CROSSJOIN='CROSSJOIN';
	HIERARCHIZE = 'HIERARCHIZE';
	AGGREGATE = 'AGGREGATE';
	
	TOPCOUNT = 'TOPCOUNT';
	BOTTOMCOUNT = 'BOTTOMCOUNT';
	
	FORMAT = 'format_string';
	
// Symbols
	QUOTE 		= '\'';
	ASTERISK 	= '*';
	COLON 		= ':';
	SEMICOLON 	= ';';
	COMMA 		= ',';
	CONCAT 		= '||';
	DOT 		= '.';
	EQ 			= '=';
	GE 			= '>=';
	GT 			= '>';
	LBRACE 		= '{';
	LE 			= '<=';
	LPAREN 		= '(';
	LT 			= '<';
	MINUS 		= '-';
	NE 			= '<>';
	PLUS 		= '+';
	RBRACE 		= '}';
	RPAREN 		= ')';
	SOLIDUS 	= '/';
	
}

@header {
package bpm.mdx.parser;
	
import java.io.DataInputStream;
import antlr.CommonAST;
import org.antlr.*;
}

@lexer::header {
  package bpm.mdx.parser;
}

@members {
	
//	public static void main(String[] args) {
//        // Create a scanner that reads from the input stream passed to us
//        panorama.mdx.MDXLexer lexer = new panorama.mdx.MDXLexer(new DataInputStream(System.in));
//            
//        // Create a parser that reads from the scanner
//        panorama.mdx.MDXRecognizer parser = new panorama.mdx.MDXRecognizer(lexer);
//            
//        // start parsing at the compilationUnit rule
//        try {
//            parser.mdx_statement();
//        }
//        catch (RecognitionException e) {
//            e.printStackTrace();
//        }
//        catch (TokenStreamException e) {
//            e.printStackTrace();
//        }
//   		System.out.println("Success");
//	}
	
}


mdx_statement : (select_statement 
			 ) EOF
// This part of MDX language is used rarely:
//			 | drop_formula_statement) EOF
			 ;


select_statement : (WITH formula_specification)? 
				  SELECT axis_specification_list?
				  FROM cube_specification
				  (WHERE slicer_specification)?
				  cell_props?
				;
			


formula_specification :
			  single_formula_specification+
			;
					
single_formula_specification :
			  member_specification
			| set_specification
			;
					
set_specification	: SET set_name AS 
			( QUOTE expression QUOTE | expression )					
			;
					
member_specification :
			  MEMBER member_name AS 
			  (
			  	( QUOTE value_expression QUOTE | value_expression)
			    (COMMA member_property_def_list)?
  			  )
			;				
										
										
axis_specification_list : axis_specification (COMMA axis_specification)* 
			;


member_property_def_list :
			  member_property_definition (COMMA member_property_definition)*
			;

					
member_name : compound_id
			;
					
member_property_definition : identifier EQ value_expression
			;					
					
set_name 	: compound_id 
			;
					
compound_id : (identifier)=>(identifier (DOT (identifier)?)*)
			| 
			;
					
axis_specification	: ((NON EMPTY)? (LBRACE HIERARCHIZE LPAREN))? (expression | mdxFunction) dim_props? (RPAREN RBRACE)? ON axis_name					
			;
					
axis_name 	: identifier
			;
					
dim_props	: DIMENSION? PROPERTIES property_list
			;
			
property_list: property (COMMA property)*
			;

					
property	: compound_id
			;
					
cube_specification: cube_name
			;

cube_name 	: compound_id 
			;

slicer_specification: expression
			;					
					
cell_props	: CELL? PROPERTIES cell_property_list
			;
			
cell_property_list: cell_property COMMA cell_property*
			;

					
cell_property: mandatory_cell_property
			| provider_specific_cell_property
			;
					
			
					
mandatory_cell_property: CELL_ORDINAL 
			| VALUE 
			| FORMATTED_VALUE
			;															
					
provider_specific_cell_property : identifier
			;					
					
expression 	: value_expression (COLON value_expression)*
			;
			
mdxFunction : (LBRACE)? (UNION | CROSSJOIN) LPAREN (expression | mdxFunction)? (COMMA (expression | mdxFunction))* RPAREN (RBRACE)?
			| (LBRACE)? (TOPCOUNT | BOTTOMCOUNT) LPAREN (expression | mdxFunction) COMMA NUMBER (COMMA expression)? RPAREN (RBRACE)?
			;
					
value_expression: term5 
			  (value_xor_expression | value_or_expression)*
			  | aggregate
			;

aggregate
	: AGGREGATE LPAREN (LBRACE)? quoted_identifier (DOT (quoted_identifier)?)* (COMMA quoted_identifier(DOT (quoted_identifier)?)*)* (RBRACE)? RPAREN
	;
					
value_xor_expression: XOR term5
			;

value_or_expression	: OR term5
			;
					
term5 		: term4 
			  (AND term4)*
			;
					
term4 		: NOT term4	
			| term3
			;
					
term3 		: term2 (comp_op term2)*
			;
					
term2 		: term ((CONCAT | PLUS | MINUS) term)*
			;
					
term 		: factor ((SOLIDUS | ASTERISK) factor)*
			;
					
factor 		: MINUS value_expression_primary
			| PLUS value_expression_primary 
			| value_expression_primary
			;

function    : identifier LPAREN (exp_list)? RPAREN
			;
															
value_expression_primary: value_expression_primary0 
			( DOT 
			  (   unquoted_identifier 
				| quoted_identifier 
				| amp_quoted_identifier 
				| function
				| CHILDREN
				| MEMBERS
			  )?
			)* 
			;
										
value_expression_primary0: function
			| (LPAREN exp_list RPAREN)
			| (LBRACE (exp_list)? RBRACE)
			| case_expression
			| STRING
			| NUMBER
			| identifier
			;
									
exp_list 	: expression (COMMA expression)*
			;
									
																											
case_expression: CASE (value_expression)?
			(when_list)?
			(ELSE value_expression)?
			END
			;
															
when_list 	: when_clause (when_clause)*
			;
					
when_clause : WHEN value_expression THEN value_expression
			;
					
comp_op 	: EQ
			| NE
			| LT
			| GT
			| LE
			| GE
			;
					
identifier 	: (unquoted_identifier
    		| quoted_identifier)
			;
					
unquoted_identifier :  ID
			;
					
amp_quoted_identifier: AMP_QUOTED_ID
			;
					
quoted_identifier: QUOTED_ID
			;

keyword 	: DIMENSION
			| PROPERTIES
			;

// MDX Lexical Rules					

FLEXRU : '0'..'9'+ '.' '0'..'9'* ;



// Typed
QUOTE 		: '\'';
ASTERISK 	: '*';
COLON 		: ':';
SEMICOLON 	: ';';
COMMA 		: ',';
CONCAT 		: '||';
DOT 		: '.';
EQ 			: '=';
GE 			: '>=';
GT 			: '>';
LBRACE 		: '{';
LE 			: '<=';
LPAREN 		: '(';
LT 			: '<';
MINUS 		: '-';
NE 			: '<>';
PLUS 		: '+';
RBRACE 		: '}';
RPAREN 		: ')';
SOLIDUS 	: '/';


NUMBER  	: ('0'..'9')+
		;
ID		:  ('a'..'z'|'A'..'Z'|'_'|'$'|'%') ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'$'|'%')*
		;
AMP_QUOTED_ID: '[&' .* ']'
		;
QUOTED_ID: ('[' .* ']')
		;
		
STRING  : '"' (~'"')* '"'
		| '\'' (~'\'')* '\''
		;
		
WS  :   (   ' '
        |   '\t'
        |   '\r'
        |   '\f'
        |   '\n'
        )+
        { $channel=HIDDEN; }
    ;