/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.result;

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
 * @see bpm.united.olap.api.result.ResultFactory
 * @model kind="package"
 * @generated
 */
public interface ResultPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "result";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://api/result/1.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "result";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ResultPackage eINSTANCE = bpm.united.olap.api.result.impl.ResultPackageImpl.init();

	/**
	 * The meta object id for the '{@link bpm.united.olap.api.result.impl.OlapResultImpl <em>Olap Result</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.united.olap.api.result.impl.OlapResultImpl
	 * @see bpm.united.olap.api.result.impl.ResultPackageImpl#getOlapResult()
	 * @generated
	 */
	int OLAP_RESULT = 0;

	/**
	 * The feature id for the '<em><b>Lines</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OLAP_RESULT__LINES = 0;

	/**
	 * The number of structural features of the '<em>Olap Result</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OLAP_RESULT_FEATURE_COUNT = 1;


	/**
	 * The meta object id for the '{@link bpm.united.olap.api.result.impl.ResultLineImpl <em>Line</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.united.olap.api.result.impl.ResultLineImpl
	 * @see bpm.united.olap.api.result.impl.ResultPackageImpl#getResultLine()
	 * @generated
	 */
	int RESULT_LINE = 1;

	/**
	 * The feature id for the '<em><b>Cells</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESULT_LINE__CELLS = 0;

	/**
	 * The number of structural features of the '<em>Line</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESULT_LINE_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link bpm.united.olap.api.result.impl.ResultCellImpl <em>Cell</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.united.olap.api.result.impl.ResultCellImpl
	 * @see bpm.united.olap.api.result.impl.ResultPackageImpl#getResultCell()
	 * @generated
	 */
	int RESULT_CELL = 2;

	/**
	 * The number of structural features of the '<em>Cell</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESULT_CELL_FEATURE_COUNT = 0;

	/**
	 * Returns the meta object for class '{@link bpm.united.olap.api.result.OlapResult <em>Olap Result</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Olap Result</em>'.
	 * @see bpm.united.olap.api.result.OlapResult
	 * @generated
	 */
	EClass getOlapResult();

	/**
	 * Returns the meta object for the reference list '{@link bpm.united.olap.api.result.OlapResult#getLines <em>Lines</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Lines</em>'.
	 * @see bpm.united.olap.api.result.OlapResult#getLines()
	 * @see #getOlapResult()
	 * @generated
	 */
	EReference getOlapResult_Lines();

	/**
	 * Returns the meta object for class '{@link bpm.united.olap.api.result.ResultLine <em>Line</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Line</em>'.
	 * @see bpm.united.olap.api.result.ResultLine
	 * @generated
	 */
	EClass getResultLine();

	/**
	 * Returns the meta object for the reference list '{@link bpm.united.olap.api.result.ResultLine#getCells <em>Cells</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Cells</em>'.
	 * @see bpm.united.olap.api.result.ResultLine#getCells()
	 * @see #getResultLine()
	 * @generated
	 */
	EReference getResultLine_Cells();

	/**
	 * Returns the meta object for class '{@link bpm.united.olap.api.result.ResultCell <em>Cell</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Cell</em>'.
	 * @see bpm.united.olap.api.result.ResultCell
	 * @generated
	 */
	EClass getResultCell();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	ResultFactory getResultFactory();

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
		 * The meta object literal for the '{@link bpm.united.olap.api.result.impl.OlapResultImpl <em>Olap Result</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.united.olap.api.result.impl.OlapResultImpl
		 * @see bpm.united.olap.api.result.impl.ResultPackageImpl#getOlapResult()
		 * @generated
		 */
		EClass OLAP_RESULT = eINSTANCE.getOlapResult();
		/**
		 * The meta object literal for the '<em><b>Lines</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OLAP_RESULT__LINES = eINSTANCE.getOlapResult_Lines();
		/**
		 * The meta object literal for the '{@link bpm.united.olap.api.result.impl.ResultLineImpl <em>Line</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.united.olap.api.result.impl.ResultLineImpl
		 * @see bpm.united.olap.api.result.impl.ResultPackageImpl#getResultLine()
		 * @generated
		 */
		EClass RESULT_LINE = eINSTANCE.getResultLine();
		/**
		 * The meta object literal for the '<em><b>Cells</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RESULT_LINE__CELLS = eINSTANCE.getResultLine_Cells();
		/**
		 * The meta object literal for the '{@link bpm.united.olap.api.result.impl.ResultCellImpl <em>Cell</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.united.olap.api.result.impl.ResultCellImpl
		 * @see bpm.united.olap.api.result.impl.ResultPackageImpl#getResultCell()
		 * @generated
		 */
		EClass RESULT_CELL = eINSTANCE.getResultCell();

	}

} //ResultPackage
