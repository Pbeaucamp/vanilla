package bpm.studio.expressions.ui.measure;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import bpm.studio.expressions.core.measures.impl.AggregationOperator;
import bpm.studio.expressions.core.measures.impl.ConditionOperator;
import bpm.studio.expressions.core.measures.impl.DimensionFunctionOperator;
import bpm.studio.expressions.core.measures.impl.MathOperator;
import bpm.studio.expressions.core.model.IField;
import bpm.studio.expressions.core.model.StructureDimension;

public class MeasureScanner extends RuleBasedScanner {
	protected static Color COLOR_KEYWORD = new Color(Display.getDefault(), 155, 0, 155);
	protected static Color COLOR_OTHER = new Color(Display.getDefault(), 0, 0, 0);
	protected static Color COLOR_MEASURE = new Color(Display.getDefault(), 0, 200, 0);
	protected static Color COLOR_DIMENSION = new Color(Display.getDefault(), 0, 0, 190);
	protected static Color COLOR_LITERAL = new Color(Display.getDefault(), 255, 139, 23);
	
	
	protected List<String> keyWords =  new ArrayList<String>();
	
	private List<IRule> rules = new ArrayList<IRule>();
	
	private List<IField> measuresCandidates;
	
	public MeasureScanner(List<IField> measuresCandidates, List<StructureDimension> dimensions){
		
		Token keyword = new Token(new TextAttribute(COLOR_KEYWORD, null, SWT.BOLD));
		Token measures = new Token(new TextAttribute(COLOR_MEASURE, null, SWT.BOLD));
		Token other = new Token(new TextAttribute(COLOR_OTHER, null, SWT.NORMAL));
		Token exp = new Token(new TextAttribute(COLOR_OTHER, null, SWT.ITALIC));
		Token dim = new Token(new TextAttribute(COLOR_DIMENSION, null, SWT.BOLD));
		Token lvl = new Token(new TextAttribute(COLOR_DIMENSION, null, SWT.BOLD | SWT.ITALIC));
		Token dimFunc = new Token(new TextAttribute(COLOR_DIMENSION, null, SWT.ITALIC));
		Token literal = new Token(new TextAttribute(COLOR_LITERAL, null, SWT.ITALIC));
		
		WordRule wordRule = new WordRule(new IWordDetector() {
			
			public boolean isWordStart(char c) {
				for(String k : keyWords){
					if (k.length() == 1 && k.charAt(0) == c){
						return true;
					}
					else if (k.length() == 2 && k.charAt(1) == '='){
						return true;
					}
				}
				return Character.isJavaIdentifierStart(c);
			}
			
			public boolean isWordPart(char c) {
				return Character.isJavaIdentifierPart(c);
			}
		}, other);
		
		for(MathOperator op : MathOperator.operators){
			wordRule.addWord(op.getSymbol(),keyword );
			keyWords.add(op.getSymbol());
		}
		for(AggregationOperator op : AggregationOperator.operators){
			wordRule.addWord(op.getSymbol(),keyword );
			keyWords.add(op.getSymbol());
		}

		
		for(DimensionFunctionOperator op : DimensionFunctionOperator.operators){
			wordRule.addWord(op.getSymbol(),dimFunc );
			keyWords.add(op.getSymbol());
		}
		wordRule.addWord(ConditionOperator.operators[ConditionOperator.IF].getSymbol(), keyword);
		keyWords.add(ConditionOperator.operators[ConditionOperator.IF].getSymbol());

		this.measuresCandidates = measuresCandidates;
		
		
		
		WordRule mesRule = new WordRule(new IWordDetector() {
			
			public boolean isWordStart(char c) {
				return  c == '[' ;
			}
			
			public boolean isWordPart(char c) {
				return Character.isJavaIdentifierPart(c) || c == ']' || c == '[' || c == '.';
			}
		}, other);
		
		
		for(IField e : measuresCandidates ){
			mesRule.addWord("[" + e.getName() + "]", measures);
		}
		
		WordRule dimRule = new WordRule(new IWordDetector() {
			
			public boolean isWordStart(char c) {
				return  c == '{' ;
			}
			
			public boolean isWordPart(char c) {
				return Character.isJavaIdentifierPart(c) || c == ']' || c == '[' || c == '.'|| c == '}';
			}
		}, other);
		
		
		
		
		for(StructureDimension e : dimensions ){
			dimRule.addWord("{" + e.getName() + "}", dim);
			
			for(int i = 0; i < e.getLevels().size(); i++){
				dimRule.addWord("{[" +e.getName() + "].[" + i + "]}" , lvl); 
			}
		}
		
		
		
		
		
		
		PatternRule r = new PatternRule("<", ">", exp, '\\', true);
		PatternRule litteralRule = new PatternRule("'", "'", literal, '\\', true);
		setRules(new IRule[]{wordRule, r, mesRule, dimRule, litteralRule});
		rules.add(wordRule);
		rules.add(r);
		rules.add(mesRule);
		rules.add(dimRule);
		rules.add(litteralRule);
	}
	
	protected void addRule(IRule rule){
		rules.add(rule);
		setRules(rules.toArray(new IRule[rules.size()]));
	}
}
