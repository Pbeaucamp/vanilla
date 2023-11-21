package bpm.fasd.expressions.ui.model;

import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;

import bpm.fasd.expressions.api.model.FormatingOperator;
import bpm.studio.expressions.core.model.IField;
import bpm.studio.expressions.core.model.StructureDimension;
import bpm.studio.expressions.ui.measure.MeasureScanner;

public class FasdScanner extends MeasureScanner{

	public FasdScanner(List<IField> measuresCandidates, 	List<StructureDimension> dimensions) {
		super(measuresCandidates, dimensions);
		Token other = new Token(new TextAttribute(COLOR_OTHER, null, SWT.NORMAL));
		Token keyword = new Token(new TextAttribute(COLOR_KEYWORD, null, SWT.BOLD));
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
		
		for(FormatingOperator op : FormatingOperator.operators){
			
			wordRule.addWord(op.getSymbol(),keyword );
			keyWords.add(op.getSymbol());
		}
		
		addRule(wordRule);
	}

}
