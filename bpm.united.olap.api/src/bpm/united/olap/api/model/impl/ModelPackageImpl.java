/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.model.impl;

import bpm.united.olap.api.ApiPackage;

import bpm.united.olap.api.datasource.DatasourcePackage;

import bpm.united.olap.api.datasource.impl.DatasourcePackageImpl;

import bpm.united.olap.api.impl.ApiPackageImpl;

import bpm.united.olap.api.model.Cube;
import bpm.united.olap.api.model.Dimension;
import bpm.united.olap.api.model.ElementDefinition;
import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.model.MemberProperty;
import bpm.united.olap.api.model.ModelFactory;
import bpm.united.olap.api.model.ModelPackage;
import bpm.united.olap.api.model.Schema;

import bpm.united.olap.api.result.ResultPackage;

import bpm.united.olap.api.result.impl.ResultPackageImpl;

import bpm.united.olap.api.runtime.RuntimePackage;

import bpm.united.olap.api.runtime.impl.RuntimePackageImpl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ModelPackageImpl extends EPackageImpl implements ModelPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass schemaEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass cubeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dimensionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass hierarchyEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass levelEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass memberEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass measureEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass elementDefinitionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass memberPropertyEClass = null;

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
	 * @see bpm.united.olap.api.model.ModelPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private ModelPackageImpl() {
		super(eNS_URI, ModelFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link ModelPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static ModelPackage init() {
		if (isInited) return (ModelPackage)EPackage.Registry.INSTANCE.getEPackage(ModelPackage.eNS_URI);

		// Obtain or create and register package
		ModelPackageImpl theModelPackage = (ModelPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof ModelPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new ModelPackageImpl());

		isInited = true;

		// Obtain or create and register interdependencies
		ApiPackageImpl theApiPackage = (ApiPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(ApiPackage.eNS_URI) instanceof ApiPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(ApiPackage.eNS_URI) : ApiPackage.eINSTANCE);
		DatasourcePackageImpl theDatasourcePackage = (DatasourcePackageImpl)(EPackage.Registry.INSTANCE.getEPackage(DatasourcePackage.eNS_URI) instanceof DatasourcePackageImpl ? EPackage.Registry.INSTANCE.getEPackage(DatasourcePackage.eNS_URI) : DatasourcePackage.eINSTANCE);
		RuntimePackageImpl theRuntimePackage = (RuntimePackageImpl)(EPackage.Registry.INSTANCE.getEPackage(RuntimePackage.eNS_URI) instanceof RuntimePackageImpl ? EPackage.Registry.INSTANCE.getEPackage(RuntimePackage.eNS_URI) : RuntimePackage.eINSTANCE);
		ResultPackageImpl theResultPackage = (ResultPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(ResultPackage.eNS_URI) instanceof ResultPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(ResultPackage.eNS_URI) : ResultPackage.eINSTANCE);

		// Create package meta-data objects
		theModelPackage.createPackageContents();
		theApiPackage.createPackageContents();
		theDatasourcePackage.createPackageContents();
		theRuntimePackage.createPackageContents();
		theResultPackage.createPackageContents();

		// Initialize created meta-data
		theModelPackage.initializePackageContents();
		theApiPackage.initializePackageContents();
		theDatasourcePackage.initializePackageContents();
		theRuntimePackage.initializePackageContents();
		theResultPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theModelPackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(ModelPackage.eNS_URI, theModelPackage);
		return theModelPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSchema() {
		return schemaEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getSchema_Cubes() {
		return (EReference)schemaEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getSchema_Datasources() {
		return (EReference)schemaEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSchema_Name() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getSchema_Dimensions() {
		return (EReference)schemaEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getSchema_Measures() {
		return (EReference)schemaEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSchema_Id() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSchema_LastModificationDate() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getCube() {
		return cubeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getCube_Measures() {
		return (EReference)cubeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getCube_Dimensions() {
		return (EReference)cubeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCube_Name() {
		return (EAttribute)cubeEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDimension() {
		return dimensionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDimension_DataObject() {
		return (EReference)dimensionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDimension_Hierarchies() {
		return (EReference)dimensionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDimension_ParentSchema() {
		return (EReference)dimensionEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDimension_IsOneColumnDate() {
		return (EAttribute)dimensionEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getHierarchy() {
		return hierarchyEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getHierarchy_Levels() {
		return (EReference)hierarchyEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getHierarchy_ParentDimension() {
		return (EReference)hierarchyEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getHierarchy_IsClosureHierarchy() {
		return (EAttribute)hierarchyEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getLevel() {
		return levelEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getLevel_Item() {
		return (EReference)levelEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getLevel_ParentLevel() {
		return (EReference)levelEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getLevel_SubLevel() {
		return (EReference)levelEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getLevel_Members() {
		return (EReference)levelEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getLevel_ParentHierarchy() {
		return (EReference)levelEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getLevel_MemberProperties() {
		return (EReference)levelEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getLevel_OrderItem() {
		return (EReference)levelEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getMember() {
		return memberEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getMember_ParentMember() {
		return (EReference)memberEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getMember_SubMembers() {
		return (EReference)memberEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getMember_ParentLevel() {
		return (EReference)memberEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMember_MemberRelationsUname() {
		return (EAttribute)memberEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getMember_Properties() {
		return (EReference)memberEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMember_OrderValue() {
		return (EAttribute)memberEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMember_OrderUname() {
		return (EAttribute)memberEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getMeasure() {
		return measureEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getMeasure_Item() {
		return (EReference)measureEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMeasure_CalculationType() {
		return (EAttribute)measureEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getMeasure_ParentSchema() {
		return (EReference)measureEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMeasure_CalculatdFormula() {
		return (EAttribute)measureEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getElementDefinition() {
		return elementDefinitionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getElementDefinition_Name() {
		return (EAttribute)elementDefinitionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getElementDefinition_Uname() {
		return (EAttribute)elementDefinitionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getElementDefinition_Caption() {
		return (EAttribute)elementDefinitionEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getElementDefinition_IsVisible() {
		return (EAttribute)elementDefinitionEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getMemberProperty() {
		return memberPropertyEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMemberProperty_Name() {
		return (EAttribute)memberPropertyEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getMemberProperty_ValueItem() {
		return (EReference)memberPropertyEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMemberProperty_Value() {
		return (EAttribute)memberPropertyEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMemberProperty_Type() {
		return (EAttribute)memberPropertyEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ModelFactory getModelFactory() {
		return (ModelFactory)getEFactoryInstance();
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
		schemaEClass = createEClass(SCHEMA);
		createEReference(schemaEClass, SCHEMA__CUBES);
		createEReference(schemaEClass, SCHEMA__DATASOURCES);
		createEAttribute(schemaEClass, SCHEMA__NAME);
		createEReference(schemaEClass, SCHEMA__DIMENSIONS);
		createEReference(schemaEClass, SCHEMA__MEASURES);
		createEAttribute(schemaEClass, SCHEMA__ID);
		createEAttribute(schemaEClass, SCHEMA__LAST_MODIFICATION_DATE);

		cubeEClass = createEClass(CUBE);
		createEReference(cubeEClass, CUBE__MEASURES);
		createEReference(cubeEClass, CUBE__DIMENSIONS);
		createEAttribute(cubeEClass, CUBE__NAME);

		dimensionEClass = createEClass(DIMENSION);
		createEReference(dimensionEClass, DIMENSION__DATA_OBJECT);
		createEReference(dimensionEClass, DIMENSION__HIERARCHIES);
		createEReference(dimensionEClass, DIMENSION__PARENT_SCHEMA);
		createEAttribute(dimensionEClass, DIMENSION__IS_ONE_COLUMN_DATE);

		hierarchyEClass = createEClass(HIERARCHY);
		createEReference(hierarchyEClass, HIERARCHY__LEVELS);
		createEReference(hierarchyEClass, HIERARCHY__PARENT_DIMENSION);
		createEAttribute(hierarchyEClass, HIERARCHY__IS_CLOSURE_HIERARCHY);

		levelEClass = createEClass(LEVEL);
		createEReference(levelEClass, LEVEL__ITEM);
		createEReference(levelEClass, LEVEL__PARENT_LEVEL);
		createEReference(levelEClass, LEVEL__SUB_LEVEL);
		createEReference(levelEClass, LEVEL__MEMBERS);
		createEReference(levelEClass, LEVEL__PARENT_HIERARCHY);
		createEReference(levelEClass, LEVEL__MEMBER_PROPERTIES);
		createEReference(levelEClass, LEVEL__ORDER_ITEM);

		memberEClass = createEClass(MEMBER);
		createEReference(memberEClass, MEMBER__PARENT_MEMBER);
		createEReference(memberEClass, MEMBER__SUB_MEMBERS);
		createEReference(memberEClass, MEMBER__PARENT_LEVEL);
		createEAttribute(memberEClass, MEMBER__MEMBER_RELATIONS_UNAME);
		createEReference(memberEClass, MEMBER__PROPERTIES);
		createEAttribute(memberEClass, MEMBER__ORDER_VALUE);
		createEAttribute(memberEClass, MEMBER__ORDER_UNAME);

		measureEClass = createEClass(MEASURE);
		createEReference(measureEClass, MEASURE__ITEM);
		createEAttribute(measureEClass, MEASURE__CALCULATION_TYPE);
		createEReference(measureEClass, MEASURE__PARENT_SCHEMA);
		createEAttribute(measureEClass, MEASURE__CALCULATD_FORMULA);

		elementDefinitionEClass = createEClass(ELEMENT_DEFINITION);
		createEAttribute(elementDefinitionEClass, ELEMENT_DEFINITION__NAME);
		createEAttribute(elementDefinitionEClass, ELEMENT_DEFINITION__UNAME);
		createEAttribute(elementDefinitionEClass, ELEMENT_DEFINITION__CAPTION);
		createEAttribute(elementDefinitionEClass, ELEMENT_DEFINITION__IS_VISIBLE);

		memberPropertyEClass = createEClass(MEMBER_PROPERTY);
		createEAttribute(memberPropertyEClass, MEMBER_PROPERTY__NAME);
		createEReference(memberPropertyEClass, MEMBER_PROPERTY__VALUE_ITEM);
		createEAttribute(memberPropertyEClass, MEMBER_PROPERTY__TYPE);
		createEAttribute(memberPropertyEClass, MEMBER_PROPERTY__VALUE);
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
		DatasourcePackage theDatasourcePackage = (DatasourcePackage)EPackage.Registry.INSTANCE.getEPackage(DatasourcePackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		dimensionEClass.getESuperTypes().add(this.getElementDefinition());
		hierarchyEClass.getESuperTypes().add(this.getElementDefinition());
		levelEClass.getESuperTypes().add(this.getElementDefinition());
		memberEClass.getESuperTypes().add(this.getElementDefinition());
		measureEClass.getESuperTypes().add(this.getElementDefinition());

		// Initialize classes and features; add operations and parameters
		initEClass(schemaEClass, Schema.class, "Schema", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getSchema_Cubes(), this.getCube(), null, "cubes", null, 0, -1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSchema_Datasources(), theDatasourcePackage.getDatasource(), theDatasourcePackage.getDatasource_Parent(), "datasources", null, 0, -1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_Name(), ecorePackage.getEString(), "name", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSchema_Dimensions(), this.getDimension(), this.getDimension_ParentSchema(), "dimensions", null, 0, -1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSchema_Measures(), this.getMeasure(), this.getMeasure_ParentSchema(), "measures", null, 0, -1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_Id(), ecorePackage.getEIntegerObject(), "id", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_LastModificationDate(), ecorePackage.getEDate(), "lastModificationDate", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(cubeEClass, Cube.class, "Cube", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getCube_Measures(), this.getMeasure(), null, "measures", null, 0, -1, Cube.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCube_Dimensions(), this.getDimension(), null, "dimensions", null, 0, -1, Cube.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCube_Name(), ecorePackage.getEString(), "name", null, 0, 1, Cube.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(dimensionEClass, Dimension.class, "Dimension", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getDimension_DataObject(), theDatasourcePackage.getDataObject(), null, "dataObject", null, 0, 1, Dimension.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDimension_Hierarchies(), this.getHierarchy(), this.getHierarchy_ParentDimension(), "hierarchies", null, 0, -1, Dimension.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDimension_ParentSchema(), this.getSchema(), this.getSchema_Dimensions(), "parentSchema", null, 0, 1, Dimension.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDimension_IsOneColumnDate(), ecorePackage.getEBoolean(), "isOneColumnDate", null, 0, 1, Dimension.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(hierarchyEClass, Hierarchy.class, "Hierarchy", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getHierarchy_Levels(), this.getLevel(), this.getLevel_ParentHierarchy(), "levels", null, 0, -1, Hierarchy.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getHierarchy_ParentDimension(), this.getDimension(), this.getDimension_Hierarchies(), "parentDimension", null, 0, 1, Hierarchy.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getHierarchy_IsClosureHierarchy(), ecorePackage.getEBoolean(), "isClosureHierarchy", null, 0, 1, Hierarchy.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(levelEClass, Level.class, "Level", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getLevel_Item(), theDatasourcePackage.getDataObjectItem(), null, "item", null, 0, 1, Level.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getLevel_ParentLevel(), this.getLevel(), this.getLevel_SubLevel(), "parentLevel", null, 0, 1, Level.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getLevel_SubLevel(), this.getLevel(), this.getLevel_ParentLevel(), "subLevel", null, 0, 1, Level.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getLevel_Members(), this.getMember(), this.getMember_ParentLevel(), "members", null, 0, -1, Level.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getLevel_ParentHierarchy(), this.getHierarchy(), this.getHierarchy_Levels(), "parentHierarchy", null, 0, 1, Level.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getLevel_MemberProperties(), this.getMemberProperty(), null, "memberProperties", null, 0, -1, Level.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getLevel_OrderItem(), theDatasourcePackage.getDataObjectItem(), null, "orderItem", null, 0, 1, Level.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(memberEClass, Member.class, "Member", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getMember_ParentMember(), this.getMember(), this.getMember_SubMembers(), "parentMember", null, 0, 1, Member.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getMember_SubMembers(), this.getMember(), this.getMember_ParentMember(), "subMembers", null, 0, -1, Member.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getMember_ParentLevel(), this.getLevel(), this.getLevel_Members(), "parentLevel", null, 0, 1, Member.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMember_MemberRelationsUname(), ecorePackage.getEString(), "memberRelationsUname", null, 0, 1, Member.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getMember_Properties(), this.getMemberProperty(), null, "properties", null, 0, -1, Member.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMember_OrderValue(), ecorePackage.getEString(), "orderValue", null, 0, 1, Member.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMember_OrderUname(), ecorePackage.getEString(), "orderUname", null, 0, 1, Member.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(measureEClass, Measure.class, "Measure", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getMeasure_Item(), theDatasourcePackage.getDataObjectItem(), null, "item", null, 0, 1, Measure.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMeasure_CalculationType(), ecorePackage.getEString(), "calculationType", null, 0, 1, Measure.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getMeasure_ParentSchema(), this.getSchema(), this.getSchema_Measures(), "parentSchema", null, 0, 1, Measure.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMeasure_CalculatdFormula(), ecorePackage.getEString(), "calculatdFormula", null, 0, 1, Measure.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(elementDefinitionEClass, ElementDefinition.class, "ElementDefinition", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getElementDefinition_Name(), ecorePackage.getEString(), "name", null, 0, 1, ElementDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getElementDefinition_Uname(), ecorePackage.getEString(), "uname", null, 0, 1, ElementDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getElementDefinition_Caption(), ecorePackage.getEString(), "caption", null, 0, 1, ElementDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getElementDefinition_IsVisible(), ecorePackage.getEBoolean(), "isVisible", null, 0, 1, ElementDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(memberPropertyEClass, MemberProperty.class, "MemberProperty", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getMemberProperty_Name(), ecorePackage.getEString(), "name", null, 0, 1, MemberProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getMemberProperty_ValueItem(), theDatasourcePackage.getDataObjectItem(), null, "valueItem", null, 0, 1, MemberProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMemberProperty_Type(), ecorePackage.getEString(), "type", null, 0, 1, MemberProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMemberProperty_Value(), ecorePackage.getEString(), "value", null, 0, 1, MemberProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
	}

} //ModelPackageImpl
