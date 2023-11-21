grammar LastFunction;

options {
	output = AST;
}



tokens {
	//function names
	CLOSINGPERIOD = 'CLOSINGPERIOD';
	OPENINGPERIOD = 'OPENINGPERIOD';
	
	//elements
	LPAREN = '(';
	RPAREN = ')';
	DOT = '.';
	COMMA = ',';
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

parseFunction : LPAREN (UNAME COMMA)? (closingPeriod | openingPeriod) RPAREN EOF;

closingPeriod : CLOSINGPERIOD LPAREN UNAME COMMA UNAME RPAREN;

openingPeriod : OPENINGPERIOD LPAREN UNAME COMMA UNAME RPAREN;

NUMBER  	: ('0'..'9')+;
ID		:  ('a'..'z'|'A'..'Z'|'_'|'$'|'%') ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'$'|'%')*;
AMP_QUOTED_ID: '[&' (ID ((' ' | '\t')+ ID)* | NUMBER) ']';
QUOTED_ID: ('[' (ID ((' ' | '\t' | DOT) (ID)?)* | NUMBER) ']');
UNAME : QUOTED_ID (DOT (QUOTED_ID | ID)?)*;