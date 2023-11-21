/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.runtime.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

import bpm.united.olap.api.ApiPackage;
import bpm.united.olap.api.datasource.DatasourcePackage;
import bpm.united.olap.api.datasource.impl.DatasourcePackageImpl;
import bpm.united.olap.api.impl.ApiPackageImpl;
import bpm.united.olap.api.model.ModelPackage;
import bpm.united.olap.api.model.impl.ModelPackageImpl;
import bpm.united.olap.api.result.ResultPackage;
import bpm.united.olap.api.result.impl.ResultPackageImpl;
import bpm.united.olap.api.runtime.DataCell;
import bpm.united.olap.api.runtime.DataStorage;
import bpm.united.olap.api.runtime.MdxSet;
import bpm.united.olap.api.runtime.RunResult;
import bpm.united.olap.api.runtime.RuntimeFactory;
import bpm.united.olap.api.runtime.RuntimePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class RuntimePackageImpl extends EPackageImpl implements RuntimePackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass runResultEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass mdxSetEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dataStorageEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dataCellEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dataCellIdentifierEClass = null;



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
	 * @see bpm.united.olap.api.runtime.RuntimePackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private RuntimePackageImpl() {
		super(eNS_URI, RuntimeFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link RuntimePackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static RuntimePackage init() {
		if (isInited) return (RuntimePackage)EPackage.Registry.INSTANCE.getEPackage(RuntimePackage.eNS_URI);

		// Obtain or create and register package
		RuntimePackageImpl theRuntimePackage = (RuntimePackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof RuntimePackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new RuntimePackageImpl());

		isInited = true;

		// Obtain or create and register interdependencies
		ApiPackageImpl theApiPackage = (ApiPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(ApiPackage.eNS_URI) instanceof ApiPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(ApiPackage.eNS_URI) : ApiPackage.eINSTANCE);
		DatasourcePackageImpl theDatasourcePackage = (DatasourcePackageImpl)(EPackage.Registry.INSTANCE.getEPackage(DatasourcePackage.eNS_URI) instanceof DatasourcePackageImpl ? EPackage.Registry.INSTANCE.getEPackage(DatasourcePackage.eNS_URI) : DatasourcePackage.eINSTANCE);
		ModelPackageImpl theModelPackage = (ModelPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(ModelPackage.eNS_URI) instanceof ModelPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(ModelPackage.eNS_URI) : ModelPackage.eINSTANCE);
		ResultPackageImpl theResultPackage = (ResultPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(ResultPackage.eNS_URI) instanceof ResultPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(ResultPackage.eNS_URI) : ResultPackage.eINSTANCE);

		// Create package meta-data objects
		theRuntimePackage.createPackageContents();
		theApiPackage.createPackageContents();
		theDatasourcePackage.createPackageContents();
		theModelPackage.createPackageContents();
		theResultPackage.createPackageContents();

		// Initialize created meta-data
		theRuntimePackage.initializePackageContents();
		theApiPackage.initializePackageContents();
		theDatasourcePackage.initializePackageContents();
		theModelPackage.initializePackageContents();
		theResultPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theRuntimePackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(RuntimePackage.eNS_URI, theRuntimePackage);
		return theRuntimePackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getRunResult() {
		return runResultEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getRunResult_DataStorage() {
		return (EReference)runResultEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRunResult_IsNonEmpty() {
		return (EAttribute)runResultEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getMdxSet() {
		return mdxSetEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getMdxSet_Elements() {
		return (EReference)mdxSetEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMdxSet_IsVisible() {
		return (EAttribute)mdxSetEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDataStorage() {
		return dataStorageEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDataStorage_DataCells() {
		return (EReference)dataStorageEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDataStorage_Sets() {
		return (EReference)dataStorageEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDataStorage_Measures() {
		return (EReference)dataStorageEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDataStorage_UsedDimensions() {
		return (EReference)dataStorageEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDataStorage_AsMultipleWhere() {
		return (EAttribute)dataStorageEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDataCell() {
		return dataCellEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDataCell_Identifier() {
		return (EReference)dataCellEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDataCell_Row() {
		return (EAttribute)dataCellEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDataCell_Col() {
		return (EAttribute)dataCellEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDataCell_ResultValue() {
		return (EAttribute)dataCellEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDataCell_Format() {
		return (EAttribute)dataCellEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDataCellIdentifier() {
		return dataCellIdentifierEClass;
	}






	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RuntimeFactory getRuntimeFactory() {
		return (RuntimeFactory)getEFactoryInstance();
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
		runResultEClass = createEClass(RUN_RESULT);
		createEReference(runResultEClass, RUN_RESULT__DATA_STORAGE);
		createEAttribute(runResultEClass, RUN_RESULT__IS_NON_EMPTY);

		mdxSetEClass = createEClass(MDX_SET);
		createEReference(mdxSetEClass, MDX_SET__ELEMENTS);
		createEAttribute(mdxSetEClass, MDX_SET__IS_VISIBLE);

		dataStorageEClass = createEClass(DATA_STORAGE);
		createEReference(dataStorageEClass, DATA_STORAGE__DATA_CELLS);
		createEReference(dataStorageEClass, DATA_STORAGE__SETS);
		createEReference(dataStorageEClass, DATA_STORAGE__MEASURES);
		createEReference(dataStorageEClass, DATA_STORAGE__USED_DIMENSIONS);
		createEAttribute(dataStorageEClass, DATA_STORAGE__AS_MULTIPLE_WHERE);

		dataCellEClass = createEClass(DATA_CELL);
		createEReference(dataCellEClass, DATA_CELL__IDENTIFIER);
		createEAttribute(dataCellEClass, DATA_CELL__ROW);
		createEAttribute(dataCellEClass, DATA_CELL__COL);
		createEAttribute(dataCellEClass, DATA_CELL__RESULT_VALUE);
		createEAttribute(dataCellEClass, DATA_CELL__FORMAT);

		dataCellIdentifierEClass = createEClass(DATA_CELL_IDENTIFIER);


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
		ModelPackage theModelPackage = (ModelPackage)EPackage.Registry.INSTANCE.getEPackage(ModelPackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes

		// Initialize classes and features; add operations and parameters
		initEClass(runResultEClass, RunResult.class, "RunResult", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getRunResult_DataStorage(), this.getDataStorage(), null, "dataStorage", null, 0, 1, RunResult.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRunResult_IsNonEmpty(), ecorePackage.getEBoolean(), "isNonEmpty", null, 0, 1, RunResult.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(mdxSetEClass, MdxSet.class, "MdxSet", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getMdxSet_Elements(), theModelPackage.getElementDefinition(), null, "elements", null, 0, -1, MdxSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMdxSet_IsVisible(), ecorePackage.getEBoolean(), "isVisible", null, 0, 1, MdxSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(dataStorageEClass, DataStorage.class, "DataStorage", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getDataStorage_DataCells(), this.getDataCell(), null, "dataCells", null, 0, -1, DataStorage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDataStorage_Sets(), this.getMdxSet(), null, "sets", null, 0, -1, DataStorage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDataStorage_Measures(), theModelPackage.getMeasure(), null, "measures", null, 0, -1, DataStorage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDataStorage_UsedDimensions(), theModelPackage.getDimension(), null, "usedDimensions", null, 0, -1, DataStorage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDataStorage_AsMultipleWhere(), ecorePackage.getEBoolean(), "asMultipleWhere", null, 0, 1, DataStorage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(dataCellEClass, DataCell.class, "DataCell", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getDataCell_Identifier(), this.getDataCellIdentifier(), null, "identifier", null, 0, 1, DataCell.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDataCell_Row(), ecorePackage.getEInt(), "row", null, 0, 1, DataCell.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDataCell_Col(), ecorePackage.getEInt(), "col", null, 0, 1, DataCell.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDataCell_ResultValue(), ecorePackage.getEDoubleObject(), "resultValue", null, 0, 1, DataCell.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDataCell_Format(), ecorePackage.getEString(), "format", null, 0, 1, DataCell.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

//		initEClass(dataCellIdentifierEClass, DataCellIdentifier.class, "DataCellIdentifier", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

	}

} //RuntimePackageImpl
