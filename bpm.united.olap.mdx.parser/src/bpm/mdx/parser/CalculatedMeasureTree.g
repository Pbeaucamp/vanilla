tree grammar CalculatedMeasureTree;

options {
  language = Java;
  tokenVocab = CalculatedMeasure;
  ASTLabelType = CommonTree;
}

@header {
	package bpm.mdx.parser;
	import bpm.mdx.parser.result.*;
}

result returns [CalculatedItem res] : 
	{res = new CalculatedItem();}
	(LPAREN | op=operand | unop=unameoperand {res.addItem(unop);} | OPERATOR | RPAREN)* EOF
	;
	
operand : (NUMBER | DECIMAL);

unameoperand returns [NodeEvaluator node] : res=uname {node = res;};
	
uname returns [TermItem res] :
	un=UNAME {res = new TermItem(); res.setUname(un.getText());}
	;
