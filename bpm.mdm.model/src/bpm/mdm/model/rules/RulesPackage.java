/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.mdm.model.rules;

import bpm.mdm.model.MdmPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see bpm.mdm.model.rules.RulesFactory
 * @model kind="package"
 * @generated
 */
public interface RulesPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "rules";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://rules/mdm_model/1.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "rules";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	RulesPackage eINSTANCE = bpm.mdm.model.rules.impl.RulesPackageImpl.init();

	/**
	 * The meta object id for the '{@link bpm.mdm.model.rules.impl.RangeRuleImpl <em>Range Rule</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.mdm.model.rules.impl.RangeRuleImpl
	 * @see bpm.mdm.model.rules.impl.RulesPackageImpl#getRangeRule()
	 * @generated
	 */
	int RANGE_RULE = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RANGE_RULE__NAME = MdmPackage.RULE__NAME;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RANGE_RULE__DESCRIPTION = MdmPackage.RULE__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RANGE_RULE__ACTIVE = MdmPackage.RULE__ACTIVE;

	/**
	 * The feature id for the '<em><b>Min Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RANGE_RULE__MIN_VALUE = MdmPackage.RULE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Max Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RANGE_RULE__MAX_VALUE = MdmPackage.RULE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Include Min</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RANGE_RULE__INCLUDE_MIN = MdmPackage.RULE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Include Max</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RANGE_RULE__INCLUDE_MAX = MdmPackage.RULE_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Range Rule</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RANGE_RULE_FEATURE_COUNT = MdmPackage.RULE_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link bpm.mdm.model.rules.impl.SetValueRuleImpl <em>Set Value Rule</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.mdm.model.rules.impl.SetValueRuleImpl
	 * @see bpm.mdm.model.rules.impl.RulesPackageImpl#getSetValueRule()
	 * @generated
	 */
	int SET_VALUE_RULE = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SET_VALUE_RULE__NAME = MdmPackage.RULE__NAME;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SET_VALUE_RULE__DESCRIPTION = MdmPackage.RULE__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SET_VALUE_RULE__ACTIVE = MdmPackage.RULE__ACTIVE;

	/**
	 * The feature id for the '<em><b>Authorized Values</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SET_VALUE_RULE__AUTHORIZED_VALUES = MdmPackage.RULE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Set Value Rule</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SET_VALUE_RULE_FEATURE_COUNT = MdmPackage.RULE_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link bpm.mdm.model.rules.impl.PatternRuleImpl <em>Pattern Rule</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.mdm.model.rules.impl.PatternRuleImpl
	 * @see bpm.mdm.model.rules.impl.RulesPackageImpl#getPatternRule()
	 * @generated
	 */
	int PATTERN_RULE = 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATTERN_RULE__NAME = MdmPackage.RULE__NAME;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATTERN_RULE__DESCRIPTION = MdmPackage.RULE__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATTERN_RULE__ACTIVE = MdmPackage.RULE__ACTIVE;

	/**
	 * The feature id for the '<em><b>Pattern</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATTERN_RULE__PATTERN = MdmPackage.RULE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Pattern Rule</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATTERN_RULE_FEATURE_COUNT = MdmPackage.RULE_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link bpm.mdm.model.rules.impl.NumericPatternRuleImpl <em>Numeric Pattern Rule</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.mdm.model.rules.impl.NumericPatternRuleImpl
	 * @see bpm.mdm.model.rules.impl.RulesPackageImpl#getNumericPatternRule()
	 * @generated
	 */
	int NUMERIC_PATTERN_RULE = 3;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NUMERIC_PATTERN_RULE__NAME = PATTERN_RULE__NAME;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NUMERIC_PATTERN_RULE__DESCRIPTION = PATTERN_RULE__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NUMERIC_PATTERN_RULE__ACTIVE = PATTERN_RULE__ACTIVE;

	/**
	 * The feature id for the '<em><b>Pattern</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NUMERIC_PATTERN_RULE__PATTERN = PATTERN_RULE__PATTERN;

	/**
	 * The number of structural features of the '<em>Numeric Pattern Rule</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NUMERIC_PATTERN_RULE_FEATURE_COUNT = PATTERN_RULE_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link bpm.mdm.model.rules.impl.DatePatternRuleImpl <em>Date Pattern Rule</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.mdm.model.rules.impl.DatePatternRuleImpl
	 * @see bpm.mdm.model.rules.impl.RulesPackageImpl#getDatePatternRule()
	 * @generated
	 */
	int DATE_PATTERN_RULE = 4;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATE_PATTERN_RULE__NAME = PATTERN_RULE__NAME;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATE_PATTERN_RULE__DESCRIPTION = PATTERN_RULE__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATE_PATTERN_RULE__ACTIVE = PATTERN_RULE__ACTIVE;

	/**
	 * The feature id for the '<em><b>Pattern</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATE_PATTERN_RULE__PATTERN = PATTERN_RULE__PATTERN;

	/**
	 * The number of structural features of the '<em>Date Pattern Rule</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATE_PATTERN_RULE_FEATURE_COUNT = PATTERN_RULE_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link bpm.mdm.model.rules.impl.RegexPatterRuleImpl <em>Regex Patter Rule</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.mdm.model.rules.impl.RegexPatterRuleImpl
	 * @see bpm.mdm.model.rules.impl.RulesPackageImpl#getRegexPatterRule()
	 * @generated
	 */
	int REGEX_PATTER_RULE = 5;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REGEX_PATTER_RULE__NAME = PATTERN_RULE__NAME;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REGEX_PATTER_RULE__DESCRIPTION = PATTERN_RULE__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REGEX_PATTER_RULE__ACTIVE = PATTERN_RULE__ACTIVE;

	/**
	 * The feature id for the '<em><b>Pattern</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REGEX_PATTER_RULE__PATTERN = PATTERN_RULE__PATTERN;

	/**
	 * The number of structural features of the '<em>Regex Patter Rule</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REGEX_PATTER_RULE_FEATURE_COUNT = PATTERN_RULE_FEATURE_COUNT + 0;


	/**
	 * The meta object id for the '{@link bpm.mdm.model.rules.impl.EntityLinkRuleImpl <em>Entity Link Rule</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.mdm.model.rules.impl.EntityLinkRuleImpl
	 * @see bpm.mdm.model.rules.impl.RulesPackageImpl#getEntityLinkRule()
	 * @generated
	 */
	int ENTITY_LINK_RULE = 6;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_LINK_RULE__NAME = MdmPackage.RULE__NAME;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_LINK_RULE__DESCRIPTION = MdmPackage.RULE__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_LINK_RULE__ACTIVE = MdmPackage.RULE__ACTIVE;

	/**
	 * The feature id for the '<em><b>Linked Attribute</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_LINK_RULE__LINKED_ATTRIBUTE = MdmPackage.RULE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Entity Link Rule</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_LINK_RULE_FEATURE_COUNT = MdmPackage.RULE_FEATURE_COUNT + 1;


	/**
	 * Returns the meta object for class '{@link bpm.mdm.model.rules.RangeRule <em>Range Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Range Rule</em>'.
	 * @see bpm.mdm.model.rules.RangeRule
	 * @generated
	 */
	EClass getRangeRule();

	/**
	 * Returns the meta object for the attribute '{@link bpm.mdm.model.rules.RangeRule#getMinValue <em>Min Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Min Value</em>'.
	 * @see bpm.mdm.model.rules.RangeRule#getMinValue()
	 * @see #getRangeRule()
	 * @generated
	 */
	EAttribute getRangeRule_MinValue();

	/**
	 * Returns the meta object for the attribute '{@link bpm.mdm.model.rules.RangeRule#getMaxValue <em>Max Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Max Value</em>'.
	 * @see bpm.mdm.model.rules.RangeRule#getMaxValue()
	 * @see #getRangeRule()
	 * @generated
	 */
	EAttribute getRangeRule_MaxValue();

	/**
	 * Returns the meta object for the attribute '{@link bpm.mdm.model.rules.RangeRule#isIncludeMin <em>Include Min</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Include Min</em>'.
	 * @see bpm.mdm.model.rules.RangeRule#isIncludeMin()
	 * @see #getRangeRule()
	 * @generated
	 */
	EAttribute getRangeRule_IncludeMin();

	/**
	 * Returns the meta object for the attribute '{@link bpm.mdm.model.rules.RangeRule#isIncludeMax <em>Include Max</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Include Max</em>'.
	 * @see bpm.mdm.model.rules.RangeRule#isIncludeMax()
	 * @see #getRangeRule()
	 * @generated
	 */
	EAttribute getRangeRule_IncludeMax();

	/**
	 * Returns the meta object for class '{@link bpm.mdm.model.rules.SetValueRule <em>Set Value Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Set Value Rule</em>'.
	 * @see bpm.mdm.model.rules.SetValueRule
	 * @generated
	 */
	EClass getSetValueRule();

	/**
	 * Returns the meta object for the attribute list '{@link bpm.mdm.model.rules.SetValueRule#getAuthorizedValues <em>Authorized Values</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Authorized Values</em>'.
	 * @see bpm.mdm.model.rules.SetValueRule#getAuthorizedValues()
	 * @see #getSetValueRule()
	 * @generated
	 */
	EAttribute getSetValueRule_AuthorizedValues();

	/**
	 * Returns the meta object for class '{@link bpm.mdm.model.rules.PatternRule <em>Pattern Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Pattern Rule</em>'.
	 * @see bpm.mdm.model.rules.PatternRule
	 * @generated
	 */
	EClass getPatternRule();

	/**
	 * Returns the meta object for the attribute '{@link bpm.mdm.model.rules.PatternRule#getPattern <em>Pattern</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Pattern</em>'.
	 * @see bpm.mdm.model.rules.PatternRule#getPattern()
	 * @see #getPatternRule()
	 * @generated
	 */
	EAttribute getPatternRule_Pattern();

	/**
	 * Returns the meta object for class '{@link bpm.mdm.model.rules.NumericPatternRule <em>Numeric Pattern Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Numeric Pattern Rule</em>'.
	 * @see bpm.mdm.model.rules.NumericPatternRule
	 * @generated
	 */
	EClass getNumericPatternRule();

	/**
	 * Returns the meta object for class '{@link bpm.mdm.model.rules.DatePatternRule <em>Date Pattern Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Date Pattern Rule</em>'.
	 * @see bpm.mdm.model.rules.DatePatternRule
	 * @generated
	 */
	EClass getDatePatternRule();

	/**
	 * Returns the meta object for class '{@link bpm.mdm.model.rules.RegexPatterRule <em>Regex Patter Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Regex Patter Rule</em>'.
	 * @see bpm.mdm.model.rules.RegexPatterRule
	 * @generated
	 */
	EClass getRegexPatterRule();

	/**
	 * Returns the meta object for class '{@link bpm.mdm.model.rules.EntityLinkRule <em>Entity Link Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Entity Link Rule</em>'.
	 * @see bpm.mdm.model.rules.EntityLinkRule
	 * @generated
	 */
	EClass getEntityLinkRule();

	/**
	 * Returns the meta object for the reference '{@link bpm.mdm.model.rules.EntityLinkRule#getLinkedAttribute <em>Linked Attribute</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Linked Attribute</em>'.
	 * @see bpm.mdm.model.rules.EntityLinkRule#getLinkedAttribute()
	 * @see #getEntityLinkRule()
	 * @generated
	 */
	EReference getEntityLinkRule_LinkedAttribute();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	RulesFactory getRulesFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link bpm.mdm.model.rules.impl.RangeRuleImpl <em>Range Rule</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.mdm.model.rules.impl.RangeRuleImpl
		 * @see bpm.mdm.model.rules.impl.RulesPackageImpl#getRangeRule()
		 * @generated
		 */
		EClass RANGE_RULE = eINSTANCE.getRangeRule();

		/**
		 * The meta object literal for the '<em><b>Min Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RANGE_RULE__MIN_VALUE = eINSTANCE.getRangeRule_MinValue();

		/**
		 * The meta object literal for the '<em><b>Max Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RANGE_RULE__MAX_VALUE = eINSTANCE.getRangeRule_MaxValue();

		/**
		 * The meta object literal for the '<em><b>Include Min</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RANGE_RULE__INCLUDE_MIN = eINSTANCE.getRangeRule_IncludeMin();

		/**
		 * The meta object literal for the '<em><b>Include Max</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RANGE_RULE__INCLUDE_MAX = eINSTANCE.getRangeRule_IncludeMax();

		/**
		 * The meta object literal for the '{@link bpm.mdm.model.rules.impl.SetValueRuleImpl <em>Set Value Rule</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.mdm.model.rules.impl.SetValueRuleImpl
		 * @see bpm.mdm.model.rules.impl.RulesPackageImpl#getSetValueRule()
		 * @generated
		 */
		EClass SET_VALUE_RULE = eINSTANCE.getSetValueRule();

		/**
		 * The meta object literal for the '<em><b>Authorized Values</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SET_VALUE_RULE__AUTHORIZED_VALUES = eINSTANCE.getSetValueRule_AuthorizedValues();

		/**
		 * The meta object literal for the '{@link bpm.mdm.model.rules.impl.PatternRuleImpl <em>Pattern Rule</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.mdm.model.rules.impl.PatternRuleImpl
		 * @see bpm.mdm.model.rules.impl.RulesPackageImpl#getPatternRule()
		 * @generated
		 */
		EClass PATTERN_RULE = eINSTANCE.getPatternRule();

		/**
		 * The meta object literal for the '<em><b>Pattern</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PATTERN_RULE__PATTERN = eINSTANCE.getPatternRule_Pattern();

		/**
		 * The meta object literal for the '{@link bpm.mdm.model.rules.impl.NumericPatternRuleImpl <em>Numeric Pattern Rule</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.mdm.model.rules.impl.NumericPatternRuleImpl
		 * @see bpm.mdm.model.rules.impl.RulesPackageImpl#getNumericPatternRule()
		 * @generated
		 */
		EClass NUMERIC_PATTERN_RULE = eINSTANCE.getNumericPatternRule();

		/**
		 * The meta object literal for the '{@link bpm.mdm.model.rules.impl.DatePatternRuleImpl <em>Date Pattern Rule</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.mdm.model.rules.impl.DatePatternRuleImpl
		 * @see bpm.mdm.model.rules.impl.RulesPackageImpl#getDatePatternRule()
		 * @generated
		 */
		EClass DATE_PATTERN_RULE = eINSTANCE.getDatePatternRule();

		/**
		 * The meta object literal for the '{@link bpm.mdm.model.rules.impl.RegexPatterRuleImpl <em>Regex Patter Rule</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.mdm.model.rules.impl.RegexPatterRuleImpl
		 * @see bpm.mdm.model.rules.impl.RulesPackageImpl#getRegexPatterRule()
		 * @generated
		 */
		EClass REGEX_PATTER_RULE = eINSTANCE.getRegexPatterRule();

		/**
		 * The meta object literal for the '{@link bpm.mdm.model.rules.impl.EntityLinkRuleImpl <em>Entity Link Rule</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.mdm.model.rules.impl.EntityLinkRuleImpl
		 * @see bpm.mdm.model.rules.impl.RulesPackageImpl#getEntityLinkRule()
		 * @generated
		 */
		EClass ENTITY_LINK_RULE = eINSTANCE.getEntityLinkRule();

		/**
		 * The meta object literal for the '<em><b>Linked Attribute</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ENTITY_LINK_RULE__LINKED_ATTRIBUTE = eINSTANCE.getEntityLinkRule_LinkedAttribute();

	}

} //RulesPackage
