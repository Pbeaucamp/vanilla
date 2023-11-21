/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.datasource.impl;

import bpm.united.olap.api.ApiPackage;

import bpm.united.olap.api.datasource.Connection;
import bpm.united.olap.api.datasource.DataObject;
import bpm.united.olap.api.datasource.DataObjectItem;
import bpm.united.olap.api.datasource.Datasource;
import bpm.united.olap.api.datasource.DatasourceFactory;
import bpm.united.olap.api.datasource.DatasourcePackage;
import bpm.united.olap.api.datasource.Operator;
import bpm.united.olap.api.datasource.Relation;

import bpm.united.olap.api.impl.ApiPackageImpl;

import bpm.united.olap.api.model.ModelPackage;

import bpm.united.olap.api.model.impl.ModelPackageImpl;

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
public class DatasourcePackageImpl extends EPackageImpl implements DatasourcePackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass datasourceEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dataObjectEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dataObjectItemEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass connectionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass relationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass operatorEClass = null;

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
	 * @see bpm.united.olap.api.datasource.DatasourcePackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private DatasourcePackageImpl() {
		super(eNS_URI, DatasourceFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link DatasourcePackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static DatasourcePackage init() {
		if (isInited) return (DatasourcePackage)EPackage.Registry.INSTANCE.getEPackage(DatasourcePackage.eNS_URI);

		// Obtain or create and register package
		DatasourcePackageImpl theDatasourcePackage = (DatasourcePackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof DatasourcePackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new DatasourcePackageImpl());

		isInited = true;

		// Obtain or create and register interdependencies
		ApiPackageImpl theApiPackage = (ApiPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(ApiPackage.eNS_URI) instanceof ApiPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(ApiPackage.eNS_URI) : ApiPackage.eINSTANCE);
		ModelPackageImpl theModelPackage = (ModelPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(ModelPackage.eNS_URI) instanceof ModelPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(ModelPackage.eNS_URI) : ModelPackage.eINSTANCE);
		RuntimePackageImpl theRuntimePackage = (RuntimePackageImpl)(EPackage.Registry.INSTANCE.getEPackage(RuntimePackage.eNS_URI) instanceof RuntimePackageImpl ? EPackage.Registry.INSTANCE.getEPackage(RuntimePackage.eNS_URI) : RuntimePackage.eINSTANCE);
		ResultPackageImpl theResultPackage = (ResultPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(ResultPackage.eNS_URI) instanceof ResultPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(ResultPackage.eNS_URI) : ResultPackage.eINSTANCE);

		// Create package meta-data objects
		theDatasourcePackage.createPackageContents();
		theApiPackage.createPackageContents();
		theModelPackage.createPackageContents();
		theRuntimePackage.createPackageContents();
		theResultPackage.createPackageContents();

		// Initialize created meta-data
		theDatasourcePackage.initializePackageContents();
		theApiPackage.initializePackageContents();
		theModelPackage.initializePackageContents();
		theRuntimePackage.initializePackageContents();
		theResultPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theDatasourcePackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(DatasourcePackage.eNS_URI, theDatasourcePackage);
		return theDatasourcePackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDatasource() {
		return datasourceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDatasource_Name() {
		return (EAttribute)datasourceEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDatasource_Id() {
		return (EAttribute)datasourceEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDatasource_Connections() {
		return (EReference)datasourceEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDatasource_Relations() {
		return (EReference)datasourceEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDatasource_DataObjects() {
		return (EReference)datasourceEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDatasource_PublicProperties() {
		return (EAttribute)datasourceEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDatasource_DatasourceExtensionId() {
		return (EAttribute)datasourceEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDatasource_PrivateProperties() {
		return (EAttribute)datasourceEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDatasource_Parent() {
		return (EReference)datasourceEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDataObject() {
		return dataObjectEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDataObject_Name() {
		return (EAttribute)dataObjectEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDataObject_Items() {
		return (EReference)dataObjectEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDataObject_QueryText() {
		return (EAttribute)dataObjectEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDataObject_Parent() {
		return (EReference)dataObjectEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDataObject_IsFact() {
		return (EAttribute)dataObjectEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDataObjectItem() {
		return dataObjectItemEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDataObjectItem_Name() {
		return (EAttribute)dataObjectItemEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDataObjectItem_Parent() {
		return (EReference)dataObjectItemEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDataObjectItem_Id() {
		return (EAttribute)dataObjectItemEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDataObjectItem_IsKey() {
		return (EAttribute)dataObjectItemEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getConnection() {
		return connectionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getConnection_Name() {
		return (EAttribute)connectionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getConnection_Id() {
		return (EAttribute)connectionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getRelation() {
		return relationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getRelation_Operator() {
		return (EReference)relationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getRelation_LeftItem() {
		return (EReference)relationEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getRelation_RightItem() {
		return (EReference)relationEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getOperator() {
		return operatorEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getOperator_Type() {
		return (EAttribute)operatorEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DatasourceFactory getDatasourceFactory() {
		return (DatasourceFactory)getEFactoryInstance();
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
		datasourceEClass = createEClass(DATASOURCE);
		createEAttribute(datasourceEClass, DATASOURCE__NAME);
		createEAttribute(datasourceEClass, DATASOURCE__ID);
		createEReference(datasourceEClass, DATASOURCE__CONNECTIONS);
		createEReference(datasourceEClass, DATASOURCE__RELATIONS);
		createEReference(datasourceEClass, DATASOURCE__DATA_OBJECTS);
		createEAttribute(datasourceEClass, DATASOURCE__PUBLIC_PROPERTIES);
		createEAttribute(datasourceEClass, DATASOURCE__DATASOURCE_EXTENSION_ID);
		createEAttribute(datasourceEClass, DATASOURCE__PRIVATE_PROPERTIES);
		createEReference(datasourceEClass, DATASOURCE__PARENT);

		dataObjectEClass = createEClass(DATA_OBJECT);
		createEAttribute(dataObjectEClass, DATA_OBJECT__NAME);
		createEReference(dataObjectEClass, DATA_OBJECT__ITEMS);
		createEAttribute(dataObjectEClass, DATA_OBJECT__QUERY_TEXT);
		createEReference(dataObjectEClass, DATA_OBJECT__PARENT);
		createEAttribute(dataObjectEClass, DATA_OBJECT__IS_FACT);

		dataObjectItemEClass = createEClass(DATA_OBJECT_ITEM);
		createEAttribute(dataObjectItemEClass, DATA_OBJECT_ITEM__NAME);
		createEReference(dataObjectItemEClass, DATA_OBJECT_ITEM__PARENT);
		createEAttribute(dataObjectItemEClass, DATA_OBJECT_ITEM__ID);
		createEAttribute(dataObjectItemEClass, DATA_OBJECT_ITEM__IS_KEY);

		connectionEClass = createEClass(CONNECTION);
		createEAttribute(connectionEClass, CONNECTION__NAME);
		createEAttribute(connectionEClass, CONNECTION__ID);

		relationEClass = createEClass(RELATION);
		createEReference(relationEClass, RELATION__OPERATOR);
		createEReference(relationEClass, RELATION__LEFT_ITEM);
		createEReference(relationEClass, RELATION__RIGHT_ITEM);

		operatorEClass = createEClass(OPERATOR);
		createEAttribute(operatorEClass, OPERATOR__TYPE);
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
		ApiPackage theApiPackage = (ApiPackage)EPackage.Registry.INSTANCE.getEPackage(ApiPackage.eNS_URI);
		ModelPackage theModelPackage = (ModelPackage)EPackage.Registry.INSTANCE.getEPackage(ModelPackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes

		// Initialize classes and features; add operations and parameters
		initEClass(datasourceEClass, Datasource.class, "Datasource", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDatasource_Name(), ecorePackage.getEString(), "name", null, 0, 1, Datasource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDatasource_Id(), ecorePackage.getEString(), "id", null, 0, 1, Datasource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDatasource_Connections(), this.getConnection(), null, "connections", null, 0, -1, Datasource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDatasource_Relations(), this.getRelation(), null, "relations", null, 0, -1, Datasource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDatasource_DataObjects(), this.getDataObject(), this.getDataObject_Parent(), "dataObjects", null, 0, -1, Datasource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDatasource_PublicProperties(), theApiPackage.getProperties(), "publicProperties", null, 0, 1, Datasource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDatasource_DatasourceExtensionId(), ecorePackage.getEString(), "datasourceExtensionId", null, 0, 1, Datasource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDatasource_PrivateProperties(), theApiPackage.getProperties(), "privateProperties", null, 0, 1, Datasource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDatasource_Parent(), theModelPackage.getSchema(), theModelPackage.getSchema_Datasources(), "parent", null, 0, 1, Datasource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(dataObjectEClass, DataObject.class, "DataObject", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDataObject_Name(), ecorePackage.getEString(), "name", null, 0, 1, DataObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDataObject_Items(), this.getDataObjectItem(), this.getDataObjectItem_Parent(), "items", null, 0, -1, DataObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDataObject_QueryText(), ecorePackage.getEString(), "queryText", null, 0, 1, DataObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDataObject_Parent(), this.getDatasource(), this.getDatasource_DataObjects(), "parent", null, 0, 1, DataObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDataObject_IsFact(), ecorePackage.getEBoolean(), "isFact", null, 0, 1, DataObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(dataObjectItemEClass, DataObjectItem.class, "DataObjectItem", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDataObjectItem_Name(), ecorePackage.getEString(), "name", null, 0, 1, DataObjectItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDataObjectItem_Parent(), this.getDataObject(), this.getDataObject_Items(), "parent", null, 0, 1, DataObjectItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDataObjectItem_Id(), ecorePackage.getEString(), "id", null, 0, 1, DataObjectItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDataObjectItem_IsKey(), ecorePackage.getEBoolean(), "isKey", null, 0, 1, DataObjectItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(connectionEClass, Connection.class, "Connection", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getConnection_Name(), ecorePackage.getEString(), "name", null, 0, 1, Connection.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getConnection_Id(), ecorePackage.getEString(), "id", null, 0, 1, Connection.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(relationEClass, Relation.class, "Relation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getRelation_Operator(), this.getOperator(), null, "operator", null, 0, 1, Relation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getRelation_LeftItem(), this.getDataObjectItem(), null, "leftItem", null, 0, 1, Relation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getRelation_RightItem(), this.getDataObjectItem(), null, "rightItem", null, 0, 1, Relation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(operatorEClass, Operator.class, "Operator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getOperator_Type(), ecorePackage.getEString(), "type", null, 0, 1, Operator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
	}

} //DatasourcePackageImpl
