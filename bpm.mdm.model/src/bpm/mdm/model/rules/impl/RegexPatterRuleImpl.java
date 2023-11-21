/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.mdm.model.rules.impl;

import java.util.regex.Pattern;

import bpm.mdm.model.rules.RegexPatterRule;
import bpm.mdm.model.rules.RulesPackage;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Regex Patter Rule</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class RegexPatterRuleImpl extends PatternRuleImpl implements RegexPatterRule {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected RegexPatterRuleImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return RulesPackage.Literals.REGEX_PATTER_RULE;
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public boolean evaluate(Object value) {
		if (value == null){
			return false;
		}
		return Pattern.matches(getPattern(), value.toString());
	}

} //RegexPatterRuleImpl
