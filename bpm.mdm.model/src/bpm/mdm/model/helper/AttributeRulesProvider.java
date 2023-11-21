package bpm.mdm.model.helper;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;

import bpm.mdm.model.Attribute;
import bpm.mdm.model.MdmFactory;
import bpm.mdm.model.Rule;
import bpm.mdm.model.rules.DatePatternRule;
import bpm.mdm.model.rules.NumericPatternRule;
import bpm.mdm.model.rules.RangeRule;
import bpm.mdm.model.rules.RegexPatterRule;
import bpm.mdm.model.rules.RulesFactory;
import bpm.mdm.model.rules.RulesPackage;
import bpm.mdm.model.rules.SetValueRule;

/**
 * This class is an helper class to provide authorized Rules class for a given Attribute.
 * Usually, the Attribute type is conditioning whether or not if the Rule extended class
 * is allowed. 
 * @author ludo
 *
 */
public class AttributeRulesProvider {

	public static List<EClass> getAuthorizedRuleClasses(Attribute attribute){
		List<EClass>  res = new ArrayList<EClass> ();
		
		switch (attribute.getDataType()) {
		
		case DATE:
		case LONG:
		case DOUBLE:
			
			res.add(RulesPackage.eINSTANCE.getSetValueRule());
			res.add(RulesPackage.eINSTANCE.getRangeRule());
			break;
		case STRING:
			res.add(RulesPackage.eINSTANCE.getRegexPatterRule());
			res.add(RulesPackage.eINSTANCE.getNumericPatternRule());
			res.add(RulesPackage.eINSTANCE.getDatePatternRule());
			
		}
		
		return res;
	}
	
	
	
}
