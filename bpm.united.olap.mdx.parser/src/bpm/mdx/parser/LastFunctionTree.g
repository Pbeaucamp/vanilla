tree grammar LastFunctionTree;

options {
  language = Java;
  tokenVocab = LastFunction;
  ASTLabelType = CommonTree;
}

@header {
	package bpm.mdx.parser;
	import bpm.mdx.parser.result.*;
}

result returns [PeriodFunctionItem res] : 
	{res=new PeriodFunctionItem();} 
	LPAREN (measure=uname COMMA)? {res.setMeasure(measure);}
	type=(CLOSINGPERIOD {res.setLast(true);} | OPENINGPERIOD {res.setLast(false);}) 
	LPAREN level=uname COMMA {res.setLevel(level);}
	member=uname RPAREN RPAREN EOF {res.setMember(member);}
	;
	
uname returns [TermItem res] :
	un=UNAME {res = new TermItem(); res.setUname(un.getText());}
	;