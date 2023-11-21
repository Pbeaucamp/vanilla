grammar CalculatedMeasure;

options {
  language = Java;
  output = AST;
}

tokens {
	
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


calculatedmeasure: (LPAREN | operand | OPERATOR | RPAREN)* EOF;

operand : (NUMBER | DECIMAL | UNAME);

OPERATOR : ('+' | '*' | '/' | '-');
DECIMAL : NUMBER (COMMA | DOT) NUMBER;
NUMBER : ('0'..'9')+;
ID :  ('a'..'z'|'A'..'Z'|'_'|'$'|'%') ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'$'|'%')*;
AMP_QUOTED_ID : '[&' (ID ((' ' | '\t')+ ID)* | NUMBER) ']';
QUOTED_ID : ('[' (ID ((' ' | '\t' | DOT) (ID)?)* | NUMBER) ']');
UNAME : QUOTED_ID (DOT (QUOTED_ID | ID)?)*;

WS  :   (   ' '
        |   '\t'
        |   '\r'
        |   '\f'
        |   '\n'
        )+
        { $channel=HIDDEN; }
    ;