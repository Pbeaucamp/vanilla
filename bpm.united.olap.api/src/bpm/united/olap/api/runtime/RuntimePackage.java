/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.runtime;

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
 * @see bpm.united.olap.api.runtime.RuntimeFactory
 * @model kind="package"
 * @generated
 */
public interface RuntimePackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "runtime";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://api/runtime/1.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "runtime";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	RuntimePackage eINSTANCE = bpm.united.olap.api.runtime.impl.RuntimePackageImpl.init();

	/**
	 * The meta object id for the '{@link bpm.united.olap.api.runtime.impl.RunResultImpl <em>Run Result</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.united.olap.api.runtime.impl.RunResultImpl
	 * @see bpm.united.olap.api.runtime.impl.RuntimePackageImpl#getRunResult()
	 * @generated
	 */
	int RUN_RESULT = 0;

	/**
	 * The feature id for the '<em><b>Data Storage</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RUN_RESULT__DATA_STORAGE = 0;

	/**
	 * The feature id for the '<em><b>Is Non Empty</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RUN_RESULT__IS_NON_EMPTY = 1;

	/**
	 * The number of structural features of the '<em>Run Result</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RUN_RESULT_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link bpm.united.olap.api.runtime.impl.MdxSetImpl <em>Mdx Set</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.united.olap.api.runtime.impl.MdxSetImpl
	 * @see bpm.united.olap.api.runtime.impl.RuntimePackageImpl#getMdxSet()
	 * @generated
	 */
	int MDX_SET = 1;

	/**
	 * The feature id for the '<em><b>Elements</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MDX_SET__ELEMENTS = 0;

	/**
	 * The feature id for the '<em><b>Is Visible</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MDX_SET__IS_VISIBLE = 1;

	/**
	 * The number of structural features of the '<em>Mdx Set</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MDX_SET_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link bpm.united.olap.api.runtime.impl.DataStorageImpl <em>Data Storage</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.united.olap.api.runtime.impl.DataStorageImpl
	 * @see bpm.united.olap.api.runtime.impl.RuntimePackageImpl#getDataStorage()
	 * @generated
	 */
	int DATA_STORAGE = 2;

	/**
	 * The feature id for the '<em><b>Data Cells</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_STORAGE__DATA_CELLS = 0;

	/**
	 * The feature id for the '<em><b>Sets</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_STORAGE__SETS = 1;

	/**
	 * The feature id for the '<em><b>Measures</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_STORAGE__MEASURES = 2;

	/**
	 * The feature id for the '<em><b>Used Dimensions</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_STORAGE__USED_DIMENSIONS = 3;

	/**
	 * The feature id for the '<em><b>As Multiple Where</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_STORAGE__AS_MULTIPLE_WHERE = 4;

	/**
	 * The number of structural features of the '<em>Data Storage</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_STORAGE_FEATURE_COUNT = 5;

	/**
	 * The meta object id for the '{@link bpm.united.olap.api.runtime.impl.DataCellImpl <em>Data Cell</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.united.olap.api.runtime.impl.DataCellImpl
	 * @see bpm.united.olap.api.runtime.impl.RuntimePackageImpl#getDataCell()
	 * @generated
	 */
	int DATA_CELL = 3;

	/**
	 * The feature id for the '<em><b>Identifier</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_CELL__IDENTIFIER = 0;

	/**
	 * The feature id for the '<em><b>Row</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_CELL__ROW = 1;

	/**
	 * The feature id for the '<em><b>Col</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_CELL__COL = 2;

	/**
	 * The feature id for the '<em><b>Result Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_CELL__RESULT_VALUE = 3;

	/**
	 * The feature id for the '<em><b>Format</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_CELL__FORMAT = 4;

	/**
	 * The number of structural features of the '<em>Data Cell</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_CELL_FEATURE_COUNT = 5;

	/**
	 * The meta object id for the '{@link bpm.united.olap.api.runtime.impl.DataCellIdentifierImpl <em>Data Cell Identifier</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.united.olap.api.runtime.impl.DataCellIdentifierImpl
	 * @see bpm.united.olap.api.runtime.impl.RuntimePackageImpl#getDataCellIdentifier()
	 * @generated
	 */
	int DATA_CELL_IDENTIFIER = 4;

	/**
	 * The number of structural features of the '<em>Data Cell Identifier</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_CELL_IDENTIFIER_FEATURE_COUNT = 0;


	/**
	 * The meta object id for the '{@link bpm.united.olap.api.runtime.impl.NodeIdImpl <em>Node Id</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.united.olap.api.runtime.impl.NodeIdImpl
	 * @see bpm.united.olap.api.runtime.impl.RuntimePackageImpl#getNodeId()
	 * @generated
	 */
	int NODE_ID = 5;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NODE_ID__ID = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NODE_ID__VALUE = 1;

	/**
	 * The feature id for the '<em><b>Is Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NODE_ID__IS_VALUE = 2;

	/**
	 * The feature id for the '<em><b>Is Where</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NODE_ID__IS_WHERE = 3;

	/**
	 * The feature id for the '<em><b>Order Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NODE_ID__ORDER_VALUE = 4;

	/**
	 * The feature id for the '<em><b>Date Column Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NODE_ID__DATE_COLUMN_NAME = 5;

	/**
	 * The number of structural features of the '<em>Node Id</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NODE_ID_FEATURE_COUNT = 6;


	/**
	 * Returns the meta object for class '{@link bpm.united.olap.api.runtime.RunResult <em>Run Result</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Run Result</em>'.
	 * @see bpm.united.olap.api.runtime.RunResult
	 * @generated
	 */
	EClass getRunResult();

	/**
	 * Returns the meta object for the reference '{@link bpm.united.olap.api.runtime.RunResult#getDataStorage <em>Data Storage</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Data Storage</em>'.
	 * @see bpm.united.olap.api.runtime.RunResult#getDataStorage()
	 * @see #getRunResult()
	 * @generated
	 */
	EReference getRunResult_DataStorage();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.runtime.RunResult#isIsNonEmpty <em>Is Non Empty</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Is Non Empty</em>'.
	 * @see bpm.united.olap.api.runtime.RunResult#isIsNonEmpty()
	 * @see #getRunResult()
	 * @generated
	 */
	EAttribute getRunResult_IsNonEmpty();

	/**
	 * Returns the meta object for class '{@link bpm.united.olap.api.runtime.MdxSet <em>Mdx Set</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Mdx Set</em>'.
	 * @see bpm.united.olap.api.runtime.MdxSet
	 * @generated
	 */
	EClass getMdxSet();

	/**
	 * Returns the meta object for the reference list '{@link bpm.united.olap.api.runtime.MdxSet#getElements <em>Elements</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Elements</em>'.
	 * @see bpm.united.olap.api.runtime.MdxSet#getElements()
	 * @see #getMdxSet()
	 * @generated
	 */
	EReference getMdxSet_Elements();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.runtime.MdxSet#isIsVisible <em>Is Visible</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Is Visible</em>'.
	 * @see bpm.united.olap.api.runtime.MdxSet#isIsVisible()
	 * @see #getMdxSet()
	 * @generated
	 */
	EAttribute getMdxSet_IsVisible();

	/**
	 * Returns the meta object for class '{@link bpm.united.olap.api.runtime.DataStorage <em>Data Storage</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Data Storage</em>'.
	 * @see bpm.united.olap.api.runtime.DataStorage
	 * @generated
	 */
	EClass getDataStorage();

	/**
	 * Returns the meta object for the reference list '{@link bpm.united.olap.api.runtime.DataStorage#getDataCells <em>Data Cells</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Data Cells</em>'.
	 * @see bpm.united.olap.api.runtime.DataStorage#getDataCells()
	 * @see #getDataStorage()
	 * @generated
	 */
	EReference getDataStorage_DataCells();

	/**
	 * Returns the meta object for the reference list '{@link bpm.united.olap.api.runtime.DataStorage#getSets <em>Sets</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Sets</em>'.
	 * @see bpm.united.olap.api.runtime.DataStorage#getSets()
	 * @see #getDataStorage()
	 * @generated
	 */
	EReference getDataStorage_Sets();

	/**
	 * Returns the meta object for the reference list '{@link bpm.united.olap.api.runtime.DataStorage#getMeasures <em>Measures</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Measures</em>'.
	 * @see bpm.united.olap.api.runtime.DataStorage#getMeasures()
	 * @see #getDataStorage()
	 * @generated
	 */
	EReference getDataStorage_Measures();

	/**
	 * Returns the meta object for the reference list '{@link bpm.united.olap.api.runtime.DataStorage#getUsedDimensions <em>Used Dimensions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Used Dimensions</em>'.
	 * @see bpm.united.olap.api.runtime.DataStorage#getUsedDimensions()
	 * @see #getDataStorage()
	 * @generated
	 */
	EReference getDataStorage_UsedDimensions();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.runtime.DataStorage#isAsMultipleWhere <em>As Multiple Where</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>As Multiple Where</em>'.
	 * @see bpm.united.olap.api.runtime.DataStorage#isAsMultipleWhere()
	 * @see #getDataStorage()
	 * @generated
	 */
	EAttribute getDataStorage_AsMultipleWhere();

	/**
	 * Returns the meta object for class '{@link bpm.united.olap.api.runtime.DataCell <em>Data Cell</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Data Cell</em>'.
	 * @see bpm.united.olap.api.runtime.DataCell
	 * @generated
	 */
	EClass getDataCell();

	/**
	 * Returns the meta object for the reference '{@link bpm.united.olap.api.runtime.DataCell#getIdentifier <em>Identifier</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Identifier</em>'.
	 * @see bpm.united.olap.api.runtime.DataCell#getIdentifier()
	 * @see #getDataCell()
	 * @generated
	 */
	EReference getDataCell_Identifier();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.runtime.DataCell#getRow <em>Row</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Row</em>'.
	 * @see bpm.united.olap.api.runtime.DataCell#getRow()
	 * @see #getDataCell()
	 * @generated
	 */
	EAttribute getDataCell_Row();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.runtime.DataCell#getCol <em>Col</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Col</em>'.
	 * @see bpm.united.olap.api.runtime.DataCell#getCol()
	 * @see #getDataCell()
	 * @generated
	 */
	EAttribute getDataCell_Col();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.runtime.DataCell#getResultValue <em>Result Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Result Value</em>'.
	 * @see bpm.united.olap.api.runtime.DataCell#getResultValue()
	 * @see #getDataCell()
	 * @generated
	 */
	EAttribute getDataCell_ResultValue();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.runtime.DataCell#getFormat <em>Format</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Format</em>'.
	 * @see bpm.united.olap.api.runtime.DataCell#getFormat()
	 * @see #getDataCell()
	 * @generated
	 */
	EAttribute getDataCell_Format();

	/**
	 * Returns the meta object for class '{@link bpm.united.olap.api.runtime.DataCellIdentifier <em>Data Cell Identifier</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Data Cell Identifier</em>'.
	 * @see bpm.united.olap.api.runtime.DataCellIdentifier
	 * @generated
	 */
	EClass getDataCellIdentifier();

//	/**
//	 * Returns the meta object for class '{@link bpm.united.olap.api.runtime.NodeId <em>Node Id</em>}'.
//	 * <!-- begin-user-doc -->
//	 * <!-- end-user-doc -->
//	 * @return the meta object for class '<em>Node Id</em>'.
//	 * @see bpm.united.olap.api.runtime.NodeId
//	 * @generated
//	 */
//	EClass getNodeId();

//	/**
//	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.runtime.NodeId#getId <em>Id</em>}'.
//	 * <!-- begin-user-doc -->
//	 * <!-- end-user-doc -->
//	 * @return the meta object for the attribute '<em>Id</em>'.
//	 * @see bpm.united.olap.api.runtime.NodeId#getId()
//	 * @see #getNodeId()
//	 * @generated
//	 */
//	EAttribute getNodeId_Id();
//
//	/**
//	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.runtime.NodeId#getValue <em>Value</em>}'.
//	 * <!-- begin-user-doc -->
//	 * <!-- end-user-doc -->
//	 * @return the meta object for the attribute '<em>Value</em>'.
//	 * @see bpm.united.olap.api.runtime.NodeId#getValue()
//	 * @see #getNodeId()
//	 * @generated
//	 */
//	EAttribute getNodeId_Value();
//
//	/**
//	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.runtime.NodeId#isIsValue <em>Is Value</em>}'.
//	 * <!-- begin-user-doc -->
//	 * <!-- end-user-doc -->
//	 * @return the meta object for the attribute '<em>Is Value</em>'.
//	 * @see bpm.united.olap.api.runtime.NodeId#isIsValue()
//	 * @see #getNodeId()
//	 * @generated
//	 */
//	EAttribute getNodeId_IsValue();
//
//	/**
//	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.runtime.NodeId#isIsWhere <em>Is Where</em>}'.
//	 * <!-- begin-user-doc -->
//	 * <!-- end-user-doc -->
//	 * @return the meta object for the attribute '<em>Is Where</em>'.
//	 * @see bpm.united.olap.api.runtime.NodeId#isIsWhere()
//	 * @see #getNodeId()
//	 * @generated
//	 */
//	EAttribute getNodeId_IsWhere();

//	/**
//	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.runtime.NodeId#getOrderValue <em>Order Value</em>}'.
//	 * <!-- begin-user-doc -->
//	 * <!-- end-user-doc -->
//	 * @return the meta object for the attribute '<em>Order Value</em>'.
//	 * @see bpm.united.olap.api.runtime.NodeId#getOrderValue()
//	 * @see #getNodeId()
//	 * @generated
//	 */
//	EAttribute getNodeId_OrderValue();

//	/**
//	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.runtime.NodeId#getDateColumnName <em>Date Column Name</em>}'.
//	 * <!-- begin-user-doc -->
//	 * <!-- end-user-doc -->
//	 * @return the meta object for the attribute '<em>Date Column Name</em>'.
//	 * @see bpm.united.olap.api.runtime.NodeId#getDateColumnName()
//	 * @see #getNodeId()
//	 * @generated
//	 */
//	EAttribute getNodeId_DateColumnName();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	RuntimeFactory getRuntimeFactory();

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
		 * The meta object literal for the '{@link bpm.united.olap.api.runtime.impl.RunResultImpl <em>Run Result</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.united.olap.api.runtime.impl.RunResultImpl
		 * @see bpm.united.olap.api.runtime.impl.RuntimePackageImpl#getRunResult()
		 * @generated
		 */
		EClass RUN_RESULT = eINSTANCE.getRunResult();

		/**
		 * The meta object literal for the '<em><b>Data Storage</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RUN_RESULT__DATA_STORAGE = eINSTANCE.getRunResult_DataStorage();

		/**
		 * The meta object literal for the '<em><b>Is Non Empty</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RUN_RESULT__IS_NON_EMPTY = eINSTANCE.getRunResult_IsNonEmpty();

		/**
		 * The meta object literal for the '{@link bpm.united.olap.api.runtime.impl.MdxSetImpl <em>Mdx Set</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.united.olap.api.runtime.impl.MdxSetImpl
		 * @see bpm.united.olap.api.runtime.impl.RuntimePackageImpl#getMdxSet()
		 * @generated
		 */
		EClass MDX_SET = eINSTANCE.getMdxSet();

		/**
		 * The meta object literal for the '<em><b>Elements</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MDX_SET__ELEMENTS = eINSTANCE.getMdxSet_Elements();

		/**
		 * The meta object literal for the '<em><b>Is Visible</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MDX_SET__IS_VISIBLE = eINSTANCE.getMdxSet_IsVisible();

		/**
		 * The meta object literal for the '{@link bpm.united.olap.api.runtime.impl.DataStorageImpl <em>Data Storage</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.united.olap.api.runtime.impl.DataStorageImpl
		 * @see bpm.united.olap.api.runtime.impl.RuntimePackageImpl#getDataStorage()
		 * @generated
		 */
		EClass DATA_STORAGE = eINSTANCE.getDataStorage();

		/**
		 * The meta object literal for the '<em><b>Data Cells</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DATA_STORAGE__DATA_CELLS = eINSTANCE.getDataStorage_DataCells();

		/**
		 * The meta object literal for the '<em><b>Sets</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DATA_STORAGE__SETS = eINSTANCE.getDataStorage_Sets();

		/**
		 * The meta object literal for the '<em><b>Measures</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DATA_STORAGE__MEASURES = eINSTANCE.getDataStorage_Measures();

		/**
		 * The meta object literal for the '<em><b>Used Dimensions</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DATA_STORAGE__USED_DIMENSIONS = eINSTANCE.getDataStorage_UsedDimensions();

		/**
		 * The meta object literal for the '<em><b>As Multiple Where</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DATA_STORAGE__AS_MULTIPLE_WHERE = eINSTANCE.getDataStorage_AsMultipleWhere();

		/**
		 * The meta object literal for the '{@link bpm.united.olap.api.runtime.impl.DataCellImpl <em>Data Cell</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.united.olap.api.runtime.impl.DataCellImpl
		 * @see bpm.united.olap.api.runtime.impl.RuntimePackageImpl#getDataCell()
		 * @generated
		 */
		EClass DATA_CELL = eINSTANCE.getDataCell();

		/**
		 * The meta object literal for the '<em><b>Identifier</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DATA_CELL__IDENTIFIER = eINSTANCE.getDataCell_Identifier();

		/**
		 * The meta object literal for the '<em><b>Row</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DATA_CELL__ROW = eINSTANCE.getDataCell_Row();

		/**
		 * The meta object literal for the '<em><b>Col</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DATA_CELL__COL = eINSTANCE.getDataCell_Col();

		/**
		 * The meta object literal for the '<em><b>Result Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DATA_CELL__RESULT_VALUE = eINSTANCE.getDataCell_ResultValue();

		/**
		 * The meta object literal for the '<em><b>Format</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DATA_CELL__FORMAT = eINSTANCE.getDataCell_Format();

		/**
		 * The meta object literal for the '{@link bpm.united.olap.api.runtime.impl.DataCellIdentifierImpl <em>Data Cell Identifier</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.united.olap.api.runtime.impl.DataCellIdentifierImpl
		 * @see bpm.united.olap.api.runtime.impl.RuntimePackageImpl#getDataCellIdentifier()
		 * @generated
		 */
		EClass DATA_CELL_IDENTIFIER = eINSTANCE.getDataCellIdentifier();

//		/**
//		 * The meta object literal for the '{@link bpm.united.olap.api.runtime.impl.NodeIdImpl <em>Node Id</em>}' class.
//		 * <!-- begin-user-doc -->
//		 * <!-- end-user-doc -->
//		 * @see bpm.united.olap.api.runtime.impl.NodeIdImpl
//		 * @see bpm.united.olap.api.runtime.impl.RuntimePackageImpl#getNodeId()
//		 * @generated
//		 */
//		EClass NODE_ID = eINSTANCE.getNodeId();
//
//		/**
//		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
//		 * <!-- begin-user-doc -->
//		 * <!-- end-user-doc -->
//		 * @generated
//		 */
//		EAttribute NODE_ID__ID = eINSTANCE.getNodeId_Id();
//
//		/**
//		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
//		 * <!-- begin-user-doc -->
//		 * <!-- end-user-doc -->
//		 * @generated
//		 */
//		EAttribute NODE_ID__VALUE = eINSTANCE.getNodeId_Value();
//
//		/**
//		 * The meta object literal for the '<em><b>Is Value</b></em>' attribute feature.
//		 * <!-- begin-user-doc -->
//		 * <!-- end-user-doc -->
//		 * @generated
//		 */
//		EAttribute NODE_ID__IS_VALUE = eINSTANCE.getNodeId_IsValue();
//
//		/**
//		 * The meta object literal for the '<em><b>Is Where</b></em>' attribute feature.
//		 * <!-- begin-user-doc -->
//		 * <!-- end-user-doc -->
//		 * @generated
//		 */
//		EAttribute NODE_ID__IS_WHERE = eINSTANCE.getNodeId_IsWhere();
//
//		/**
//		 * The meta object literal for the '<em><b>Order Value</b></em>' attribute feature.
//		 * <!-- begin-user-doc -->
//		 * <!-- end-user-doc -->
//		 * @generated
//		 */
//		EAttribute NODE_ID__ORDER_VALUE = eINSTANCE.getNodeId_OrderValue();
//
//		/**
//		 * The meta object literal for the '<em><b>Date Column Name</b></em>' attribute feature.
//		 * <!-- begin-user-doc -->
//		 * <!-- end-user-doc -->
//		 * @generated
//		 */
//		EAttribute NODE_ID__DATE_COLUMN_NAME = eINSTANCE.getNodeId_DateColumnName();

	}

} //RuntimePackage
