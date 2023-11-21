/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.mdm.model.rules.impl;

import bpm.mdm.model.rules.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class RulesFactoryImpl extends EFactoryImpl implements RulesFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static RulesFactory init() {
		try {
			RulesFactory theRulesFactory = (RulesFactory)EPackage.Registry.INSTANCE.getEFactory("http://rules/mdm_model/1.0"); 
			if (theRulesFactory != null) {
				return theRulesFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new RulesFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RulesFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case RulesPackage.RANGE_RULE: return createRangeRule();
			case RulesPackage.SET_VALUE_RULE: return createSetValueRule();
			case RulesPackage.NUMERIC_PATTERN_RULE: return createNumericPatternRule();
			case RulesPackage.DATE_PATTERN_RULE: return createDatePatternRule();
			case RulesPackage.REGEX_PATTER_RULE: return createRegexPatterRule();
			case RulesPackage.ENTITY_LINK_RULE: return createEntityLinkRule();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RangeRule createRangeRule() {
		RangeRuleImpl rangeRule = new RangeRuleImpl();
		return rangeRule;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SetValueRule createSetValueRule() {
		SetValueRuleImpl setValueRule = new SetValueRuleImpl();
		return setValueRule;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NumericPatternRule createNumericPatternRule() {
		NumericPatternRuleImpl numericPatternRule = new NumericPatternRuleImpl();
		return numericPatternRule;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DatePatternRule createDatePatternRule() {
		DatePatternRuleImpl datePatternRule = new DatePatternRuleImpl();
		return datePatternRule;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RegexPatterRule createRegexPatterRule() {
		RegexPatterRuleImpl regexPatterRule = new RegexPatterRuleImpl();
		return regexPatterRule;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EntityLinkRule createEntityLinkRule() {
		EntityLinkRuleImpl entityLinkRule = new EntityLinkRuleImpl();
		return entityLinkRule;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RulesPackage getRulesPackage() {
		return (RulesPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static RulesPackage getPackage() {
		return RulesPackage.eINSTANCE;
	}

} //RulesFactoryImpl
