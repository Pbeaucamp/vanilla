/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.mdm.model.rules.impl;

import java.text.SimpleDateFormat;

import bpm.mdm.model.rules.DatePatternRule;
import bpm.mdm.model.rules.RulesPackage;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Date Pattern Rule</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class DatePatternRuleImpl extends PatternRuleImpl implements DatePatternRule {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DatePatternRuleImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return RulesPackage.Literals.DATE_PATTERN_RULE;
	}
	public boolean evaluate(Object value) {
		if (value != null){
			try{
				String v = value.toString();
				if (v.length() > 8){
				}
				SimpleDateFormat sdf = new SimpleDateFormat(getPattern());
				
				sdf.parse(v);
				return true;
				
			}catch(Exception ex){
				return false;
			}
		
		}
		//the null conversion should be handle by the attribute property nullallowed
		return true;
	}

} //DatePatternRuleImpl
