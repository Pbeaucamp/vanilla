/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.mdm.model.runtime;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
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
 * @see bpm.mdm.model.runtime.RuntimeFactory
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
	String eNS_URI = "http://runtime/mdm_model/1.0";

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
	RuntimePackage eINSTANCE = bpm.mdm.model.runtime.impl.RuntimePackageImpl.init();

	/**
	 * The meta object id for the '{@link bpm.mdm.model.runtime.impl.RowImpl <em>Row</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.mdm.model.runtime.impl.RowImpl
	 * @see bpm.mdm.model.runtime.impl.RuntimePackageImpl#getRow()
	 * @generated
	 */
	int ROW = 0;

	/**
	 * The number of structural features of the '<em>Row</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ROW_FEATURE_COUNT = 0;

	/**
	 * The meta object id for the '{@link bpm.mdm.model.runtime.RowState <em>Row State</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.mdm.model.runtime.RowState
	 * @see bpm.mdm.model.runtime.impl.RuntimePackageImpl#getRowState()
	 * @generated
	 */
	int ROW_STATE = 3;

	/**
	 * The meta object id for the '{@link bpm.mdm.model.runtime.impl.SynchroPerformerImpl <em>Synchro Performer</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.mdm.model.runtime.impl.SynchroPerformerImpl
	 * @see bpm.mdm.model.runtime.impl.RuntimePackageImpl#getSynchroPerformer()
	 * @generated
	 */
	int SYNCHRO_PERFORMER = 1;

	/**
	 * The number of structural features of the '<em>Synchro Performer</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SYNCHRO_PERFORMER_FEATURE_COUNT = 0;

	/**
	 * The meta object id for the '{@link bpm.mdm.model.runtime.impl.DiffResultImpl <em>Diff Result</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.mdm.model.runtime.impl.DiffResultImpl
	 * @see bpm.mdm.model.runtime.impl.RuntimePackageImpl#getDiffResult()
	 * @generated
	 */
	int DIFF_RESULT = 2;

	/**
	 * The number of structural features of the '<em>Diff Result</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DIFF_RESULT_FEATURE_COUNT = 0;


	/**
	 * Returns the meta object for class '{@link bpm.mdm.model.runtime.Row <em>Row</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Row</em>'.
	 * @see bpm.mdm.model.runtime.Row
	 * @generated
	 */
	EClass getRow();

	/**
	 * Returns the meta object for enum '{@link bpm.mdm.model.runtime.RowState <em>Row State</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Row State</em>'.
	 * @see bpm.mdm.model.runtime.RowState
	 * @generated
	 */
	EEnum getRowState();

	/**
	 * Returns the meta object for class '{@link bpm.mdm.model.runtime.SynchroPerformer <em>Synchro Performer</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Synchro Performer</em>'.
	 * @see bpm.mdm.model.runtime.SynchroPerformer
	 * @generated
	 */
	EClass getSynchroPerformer();

	/**
	 * Returns the meta object for class '{@link bpm.mdm.model.runtime.DiffResult <em>Diff Result</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Diff Result</em>'.
	 * @see bpm.mdm.model.runtime.DiffResult
	 * @generated
	 */
	EClass getDiffResult();

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
		 * The meta object literal for the '{@link bpm.mdm.model.runtime.impl.RowImpl <em>Row</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.mdm.model.runtime.impl.RowImpl
		 * @see bpm.mdm.model.runtime.impl.RuntimePackageImpl#getRow()
		 * @generated
		 */
		EClass ROW = eINSTANCE.getRow();

		/**
		 * The meta object literal for the '{@link bpm.mdm.model.runtime.RowState <em>Row State</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.mdm.model.runtime.RowState
		 * @see bpm.mdm.model.runtime.impl.RuntimePackageImpl#getRowState()
		 * @generated
		 */
		EEnum ROW_STATE = eINSTANCE.getRowState();

		/**
		 * The meta object literal for the '{@link bpm.mdm.model.runtime.impl.SynchroPerformerImpl <em>Synchro Performer</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.mdm.model.runtime.impl.SynchroPerformerImpl
		 * @see bpm.mdm.model.runtime.impl.RuntimePackageImpl#getSynchroPerformer()
		 * @generated
		 */
		EClass SYNCHRO_PERFORMER = eINSTANCE.getSynchroPerformer();

		/**
		 * The meta object literal for the '{@link bpm.mdm.model.runtime.impl.DiffResultImpl <em>Diff Result</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.mdm.model.runtime.impl.DiffResultImpl
		 * @see bpm.mdm.model.runtime.impl.RuntimePackageImpl#getDiffResult()
		 * @generated
		 */
		EClass DIFF_RESULT = eINSTANCE.getDiffResult();

	}

} //RuntimePackage
