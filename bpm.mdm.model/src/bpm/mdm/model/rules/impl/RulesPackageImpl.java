/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.mdm.model.rules.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

import bpm.mdm.model.MdmPackage;
import bpm.mdm.model.impl.MdmPackageImpl;
import bpm.mdm.model.rules.DatePatternRule;
import bpm.mdm.model.rules.EntityLinkRule;
import bpm.mdm.model.rules.NumericPatternRule;
import bpm.mdm.model.rules.PatternRule;
import bpm.mdm.model.rules.RangeRule;
import bpm.mdm.model.rules.RegexPatterRule;
import bpm.mdm.model.rules.RulesFactory;
import bpm.mdm.model.rules.RulesPackage;
import bpm.mdm.model.rules.SetValueRule;
import bpm.mdm.model.runtime.RuntimePackage;
import bpm.mdm.model.runtime.impl.RuntimePackageImpl;
import bpm.mdm.model.strategies.StrategiesPackage;
import bpm.mdm.model.strategies.impl.StrategiesPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class RulesPackageImpl extends EPackageImpl implements RulesPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass rangeRuleEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass setValueRuleEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass patternRuleEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass numericPatternRuleEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass datePatternRuleEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass regexPatterRuleEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass entityLinkRuleEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see bpm.mdm.model.rules.RulesPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private RulesPackageImpl() {
		super(eNS_URI, RulesFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 * 
	 * <p>This method is used to initialize {@link RulesPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static RulesPackage init() {
		if (isInited) return (RulesPackage)EPackage.Registry.INSTANCE.getEPackage(RulesPackage.eNS_URI);

		// Obtain or create and register package
		RulesPackageImpl theRulesPackage = (RulesPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof RulesPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new RulesPackageImpl());

		isInited = true;

		// Obtain or create and register interdependencies
		MdmPackageImpl theMdmPackage = (MdmPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(MdmPackage.eNS_URI) instanceof MdmPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(MdmPackage.eNS_URI) : MdmPackage.eINSTANCE);
		StrategiesPackageImpl theStrategiesPackage = (StrategiesPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(StrategiesPackage.eNS_URI) instanceof StrategiesPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(StrategiesPackage.eNS_URI) : StrategiesPackage.eINSTANCE);
		RuntimePackageImpl theRuntimePackage = (RuntimePackageImpl)(EPackage.Registry.INSTANCE.getEPackage(RuntimePackage.eNS_URI) instanceof RuntimePackageImpl ? EPackage.Registry.INSTANCE.getEPackage(RuntimePackage.eNS_URI) : RuntimePackage.eINSTANCE);

		// Create package meta-data objects
		theRulesPackage.createPackageContents();
		theMdmPackage.createPackageContents();
		theStrategiesPackage.createPackageContents();
		theRuntimePackage.createPackageContents();

		// Initialize created meta-data
		theRulesPackage.initializePackageContents();
		theMdmPackage.initializePackageContents();
		theStrategiesPackage.initializePackageContents();
		theRuntimePackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theRulesPackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(RulesPackage.eNS_URI, theRulesPackage);
		return theRulesPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getRangeRule() {
		return rangeRuleEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRangeRule_MinValue() {
		return (EAttribute)rangeRuleEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRangeRule_MaxValue() {
		return (EAttribute)rangeRuleEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRangeRule_IncludeMin() {
		return (EAttribute)rangeRuleEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRangeRule_IncludeMax() {
		return (EAttribute)rangeRuleEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSetValueRule() {
		return setValueRuleEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSetValueRule_AuthorizedValues() {
		return (EAttribute)setValueRuleEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getPatternRule() {
		return patternRuleEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPatternRule_Pattern() {
		return (EAttribute)patternRuleEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getNumericPatternRule() {
		return numericPatternRuleEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDatePatternRule() {
		return datePatternRuleEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getRegexPatterRule() {
		return regexPatterRuleEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEntityLinkRule() {
		return entityLinkRuleEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEntityLinkRule_LinkedAttribute() {
		return (EReference)entityLinkRuleEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RulesFactory getRulesFactory() {
		return (RulesFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		rangeRuleEClass = createEClass(RANGE_RULE);
		createEAttribute(rangeRuleEClass, RANGE_RULE__MIN_VALUE);
		createEAttribute(rangeRuleEClass, RANGE_RULE__MAX_VALUE);
		createEAttribute(rangeRuleEClass, RANGE_RULE__INCLUDE_MIN);
		createEAttribute(rangeRuleEClass, RANGE_RULE__INCLUDE_MAX);

		setValueRuleEClass = createEClass(SET_VALUE_RULE);
		createEAttribute(setValueRuleEClass, SET_VALUE_RULE__AUTHORIZED_VALUES);

		patternRuleEClass = createEClass(PATTERN_RULE);
		createEAttribute(patternRuleEClass, PATTERN_RULE__PATTERN);

		numericPatternRuleEClass = createEClass(NUMERIC_PATTERN_RULE);

		datePatternRuleEClass = createEClass(DATE_PATTERN_RULE);

		regexPatterRuleEClass = createEClass(REGEX_PATTER_RULE);

		entityLinkRuleEClass = createEClass(ENTITY_LINK_RULE);
		createEReference(entityLinkRuleEClass, ENTITY_LINK_RULE__LINKED_ATTRIBUTE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		MdmPackage theMdmPackage = (MdmPackage)EPackage.Registry.INSTANCE.getEPackage(MdmPackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		rangeRuleEClass.getESuperTypes().add(theMdmPackage.getRule());
		setValueRuleEClass.getESuperTypes().add(theMdmPackage.getRule());
		patternRuleEClass.getESuperTypes().add(theMdmPackage.getRule());
		numericPatternRuleEClass.getESuperTypes().add(this.getPatternRule());
		datePatternRuleEClass.getESuperTypes().add(this.getPatternRule());
		regexPatterRuleEClass.getESuperTypes().add(this.getPatternRule());
		entityLinkRuleEClass.getESuperTypes().add(theMdmPackage.getRule());

		// Initialize classes and features; add operations and parameters
		initEClass(rangeRuleEClass, RangeRule.class, "RangeRule", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getRangeRule_MinValue(), ecorePackage.getELong(), "minValue", null, 0, 1, RangeRule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRangeRule_MaxValue(), ecorePackage.getELong(), "maxValue", null, 0, 1, RangeRule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRangeRule_IncludeMin(), ecorePackage.getEBoolean(), "includeMin", null, 0, 1, RangeRule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRangeRule_IncludeMax(), ecorePackage.getEBoolean(), "includeMax", null, 0, 1, RangeRule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(setValueRuleEClass, SetValueRule.class, "SetValueRule", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSetValueRule_AuthorizedValues(), ecorePackage.getEJavaObject(), "authorizedValues", null, 0, -1, SetValueRule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(patternRuleEClass, PatternRule.class, "PatternRule", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getPatternRule_Pattern(), ecorePackage.getEString(), "pattern", null, 0, 1, PatternRule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(numericPatternRuleEClass, NumericPatternRule.class, "NumericPatternRule", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(datePatternRuleEClass, DatePatternRule.class, "DatePatternRule", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(regexPatterRuleEClass, RegexPatterRule.class, "RegexPatterRule", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(entityLinkRuleEClass, EntityLinkRule.class, "EntityLinkRule", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getEntityLinkRule_LinkedAttribute(), theMdmPackage.getAttribute(), null, "linkedAttribute", null, 0, 1, EntityLinkRule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
	}

} //RulesPackageImpl
