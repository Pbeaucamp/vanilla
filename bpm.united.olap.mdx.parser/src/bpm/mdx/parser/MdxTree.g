tree grammar MdxTree;

options {
  language = Java;
  tokenVocab = Mdx;
  ASTLabelType = CommonTree;
}

@header {
	package bpm.mdx.parser;
	import bpm.mdx.parser.result.*;
}

resultTree returns [RootItem result]
	: e=rootTree EOF { result = e; }
	;

rootTree returns [RootItem root]
	: {root = new RootItem();} ((WITH)? withItem=with {((RootItem)root).addWithItem(withItem);})* SELECT (NON EMPTY {root.setNonEmpty(true);})? row=axis ON ax1=ID ',' (NON EMPTY {root.setNonEmpty(true);})? col=axis ON ax2=ID FROM from=fromItem (WHERE where=axis)? {if(ax1.getText().equalsIgnoreCase("ROWS")) {root.setRow(row); root.setCol(col);}else {root.setRow(col); root.setCol(row);} root.setFrom(from); root.setWhere(where);}
	;
	
with returns [NodeEvaluator node]
	: {node = new WithItem();} (MEMBER | SET) name=uname AS value=formula (op=(PLUS | ASTERISK | MINUS | SOLIDUS | COMMA) value2=formula)? (COMMA form=format{((WithItem)node).setFormat(form);})? {((WithItem)node).setUname(((TermItem)name).getUname()); if(value2 != null) {BasicFormulaItem val = new BasicFormulaItem(); ((BasicFormulaItem)val).setLeftItem(value); ((BasicFormulaItem)val).setRightItem(value2); ((BasicFormulaItem)val).setOperator(op.getText()); ((WithItem)node).setValue(val);} else {((WithItem)node).setValue(value);}}
	;
	
format returns [String form]
	: ID EQ a=ID {form=a.getText();}
	;
	
formula returns [NodeEvaluator node]
	: //t=term (op=(PLUS | ASTERISK | MINUS | SOLIDUS | COMMA) f2=basicformula)? {if(f2 != null) {node = new BasicFormulaItem(); ((BasicFormulaItem)node).setLeftItem(t); ((BasicFormulaItem)node).setRightItem(f2); ((BasicFormulaItem)node).setOperator(op.getText());} else {node=t;}}
	 //t=term op=(PLUS | ASTERISK | MINUS | SOLIDUS | COMMA) (t1=term | t2=basicformula) {if(t1 != null) {node = new BasicFormulaItem(); ((BasicFormulaItem)node).setLeftItem(t); ((BasicFormulaItem)node).setRightItem(t1); ((BasicFormulaItem)node).setOperator(op.getText());} else {node = new BasicFormulaItem(); ((BasicFormulaItem)node).setLeftItem(t); ((BasicFormulaItem)node).setRightItem(t2); ((BasicFormulaItem)node).setOperator(op.getText());}}
	| t=basicformula {node=t;}
	| t=term {node=t;}
	| NUMBER {node = new BasicItem(); ((BasicItem)node).setValue($NUMBER.text);}
	| t=aggregate {node=t;}
	;
	
aggregate returns [NodeEvaluator node]
	: AGGREGATE {node = new AggregateItem();} LPAREN t=term {((AggregateItem)node).addNode(t);} (COMMA t1=term {((AggregateItem)node).addNode(t1);})* RPAREN
	;
	
basicformula returns [NodeEvaluator node]
	: {BasicFormulaItem temp = null;}
	LPAREN f1=formula 
	(op=(PLUS | ASTERISK | MINUS | SOLIDUS | COMMA) 
	f2=formula {
	if(temp == null) 
	{temp = new BasicFormulaItem(); 
	temp.setLeftItem(f2); } 
	else {temp.setRightItem(f2); 
	temp.setOperator(op.getText());}})* RPAREN
	 
	{node = new BasicFormulaItem(); 
	((BasicFormulaItem)node).setLeftItem(f1); 
	if(temp.getRightItem() != null) {((BasicFormulaItem)node).setRightItem(temp);}
	else {((BasicFormulaItem)node).setRightItem(f2);}
	((BasicFormulaItem)node).setOperator(op.getText());}
	;
	
fromItem returns [String from]
	: val=ID {from=val.getText();}
	| val=QUOTED_ID {from=val.getText().replace("[","").replace("]","");}
	;
	
axis returns [AxisItem axis]
	: (LPAREN)? t=term (',' t2=term)* (RPAREN)? {axis = new AxisItem(); axis.addItem(t); axis.addItem(t2);}
	| (LPAREN)? LBRACE HIERARCHIZE LPAREN t=term (',' t2=term)* RPAREN RBRACE (RPAREN)? {axis = new AxisItem(); axis.addItem(t); axis.addItem(t2);}
	;
	
term returns [NodeEvaluator node]
	: (LBRACE)? item=union {node=item;} (RBRACE)?
	| (LBRACE)? item=crossjoin {node=item;} (RBRACE)?
	| (LBRACE)? item=topcount {node=item;} (RBRACE)?
	| (LBRACE)? item=uname {node=item;} (RBRACE)?
	;
	
topcount returns [NodeEvaluator topcount]
	: {topcount = new TopCountItem();} (TOPCOUNT {((TopCountItem)topcount).setTop(true);} | BOTTOMCOUNT {((TopCountItem)topcount).setTop(false);} )  '(' t1=term ',' t2=NUMBER (',' t3=term)? ')' {((TopCountItem)topcount).setSet(t1); ((TopCountItem)topcount).setCount(Integer.parseInt(t2.getText())); if(t3 != null) {((TopCountItem)topcount).setMeasure(t3);}}
	;
	
union returns [NodeEvaluator union]
	: UNION '(' t1=term ',' t2=term ')' {union = new UnionItem(); ((UnionItem)union).setLeftItem(t1); ((UnionItem)union).setRightItem(t2);}
	;
	
crossjoin returns [NodeEvaluator cross]
	: CROSSJOIN '(' t1=term ',' t2=term ')' {cross = new CrossjoinItem(); ((CrossjoinItem)cross).setLeftItem(t1); ((CrossjoinItem)cross).setRightItem(t2);}
	;
	
uname returns [NodeEvaluator uname]
	: p1=unamePart ('.' (p2=unamePart)? {p1+="."+p2;})* {uname = new TermItem();((TermItem)uname).setUname(p1);}
	;
	
unamePart returns [String part]
	: QUOTED_ID {part=$QUOTED_ID.text;}
	| CHILDREN {part=$CHILDREN.text;}
	| MEMBERS {part=$MEMBERS.text;}
	;