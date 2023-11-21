/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.result.impl;

import bpm.united.olap.api.ApiPackage;

import bpm.united.olap.api.datasource.DatasourcePackage;

import bpm.united.olap.api.datasource.impl.DatasourcePackageImpl;

import bpm.united.olap.api.impl.ApiPackageImpl;

import bpm.united.olap.api.model.ModelPackage;

import bpm.united.olap.api.model.impl.ModelPackageImpl;

import bpm.united.olap.api.result.OlapResult;
import bpm.united.olap.api.result.ResultCell;
import bpm.united.olap.api.result.ResultFactory;
import bpm.united.olap.api.result.ResultLine;
import bpm.united.olap.api.result.ResultPackage;

import bpm.united.olap.api.runtime.RuntimePackage;

import bpm.united.olap.api.runtime.impl.RuntimePackageImpl;

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
public class ResultPackageImpl extends EPackageImpl implements ResultPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass olapResultEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass resultLineEClass = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass resultCellEClass = null;

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
	 * @see bpm.united.olap.api.result.ResultPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private ResultPackageImpl() {
		super(eNS_URI, ResultFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link ResultPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static ResultPackage init() {
		if (isInited) return (ResultPackage)EPackage.Registry.INSTANCE.getEPackage(ResultPackage.eNS_URI);

		// Obtain or create and register package
		ResultPackageImpl theResultPackage = (ResultPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof ResultPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new ResultPackageImpl());

		isInited = true;

		// Obtain or create and register interdependencies
		ApiPackageImpl theApiPackage = (ApiPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(ApiPackage.eNS_URI) instanceof ApiPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(ApiPackage.eNS_URI) : ApiPackage.eINSTANCE);
		DatasourcePackageImpl theDatasourcePackage = (DatasourcePackageImpl)(EPackage.Registry.INSTANCE.getEPackage(DatasourcePackage.eNS_URI) instanceof DatasourcePackageImpl ? EPackage.Registry.INSTANCE.getEPackage(DatasourcePackage.eNS_URI) : DatasourcePackage.eINSTANCE);
		ModelPackageImpl theModelPackage = (ModelPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(ModelPackage.eNS_URI) instanceof ModelPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(ModelPackage.eNS_URI) : ModelPackage.eINSTANCE);
		RuntimePackageImpl theRuntimePackage = (RuntimePackageImpl)(EPackage.Registry.INSTANCE.getEPackage(RuntimePackage.eNS_URI) instanceof RuntimePackageImpl ? EPackage.Registry.INSTANCE.getEPackage(RuntimePackage.eNS_URI) : RuntimePackage.eINSTANCE);

		// Create package meta-data objects
		theResultPackage.createPackageContents();
		theApiPackage.createPackageContents();
		theDatasourcePackage.createPackageContents();
		theModelPackage.createPackageContents();
		theRuntimePackage.createPackageContents();

		// Initialize created meta-data
		theResultPackage.initializePackageContents();
		theApiPackage.initializePackageContents();
		theDatasourcePackage.initializePackageContents();
		theModelPackage.initializePackageContents();
		theRuntimePackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theResultPackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(ResultPackage.eNS_URI, theResultPackage);
		return theResultPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getOlapResult() {
		return olapResultEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getOlapResult_Lines() {
		return (EReference)olapResultEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getResultLine() {
		return resultLineEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getResultLine_Cells() {
		return (EReference)resultLineEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getResultCell() {
		return resultCellEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ResultFactory getResultFactory() {
		return (ResultFactory)getEFactoryInstance();
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
		olapResultEClass = createEClass(OLAP_RESULT);
		createEReference(olapResultEClass, OLAP_RESULT__LINES);

		resultLineEClass = createEClass(RESULT_LINE);
		createEReference(resultLineEClass, RESULT_LINE__CELLS);

		resultCellEClass = createEClass(RESULT_CELL);
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

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes

		// Initialize classes and features; add operations and parameters
		initEClass(olapResultEClass, OlapResult.class, "OlapResult", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getOlapResult_Lines(), this.getResultLine(), null, "lines", null, 0, -1, OlapResult.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(resultLineEClass, ResultLine.class, "ResultLine", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getResultLine_Cells(), this.getResultCell(), null, "cells", null, 0, -1, ResultLine.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(resultCellEClass, ResultCell.class, "ResultCell", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
	}

} //ResultPackageImpl
