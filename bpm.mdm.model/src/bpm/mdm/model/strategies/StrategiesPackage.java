/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.mdm.model.strategies;

import bpm.mdm.model.MdmPackage;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

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
 * @see bpm.mdm.model.strategies.StrategiesFactory
 * @model kind="package"
 * @generated
 */
public interface StrategiesPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "strategies";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://strategies/mdm_model/1.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "strategies";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	StrategiesPackage eINSTANCE = bpm.mdm.model.strategies.impl.StrategiesPackageImpl.init();

	/**
	 * The meta object id for the '{@link bpm.mdm.model.strategies.impl.DefaultStrategyImpl <em>Default Strategy</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.mdm.model.strategies.impl.DefaultStrategyImpl
	 * @see bpm.mdm.model.strategies.impl.StrategiesPackageImpl#getDefaultStrategy()
	 * @generated
	 */
	int DEFAULT_STRATEGY = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEFAULT_STRATEGY__NAME = MdmPackage.STRATEGY__NAME;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEFAULT_STRATEGY__DESCRIPTION = MdmPackage.STRATEGY__DESCRIPTION;

	/**
	 * The number of structural features of the '<em>Default Strategy</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEFAULT_STRATEGY_FEATURE_COUNT = MdmPackage.STRATEGY_FEATURE_COUNT + 0;


	/**
	 * Returns the meta object for class '{@link bpm.mdm.model.strategies.DefaultStrategy <em>Default Strategy</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Default Strategy</em>'.
	 * @see bpm.mdm.model.strategies.DefaultStrategy
	 * @generated
	 */
	EClass getDefaultStrategy();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	StrategiesFactory getStrategiesFactory();

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
		 * The meta object literal for the '{@link bpm.mdm.model.strategies.impl.DefaultStrategyImpl <em>Default Strategy</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.mdm.model.strategies.impl.DefaultStrategyImpl
		 * @see bpm.mdm.model.strategies.impl.StrategiesPackageImpl#getDefaultStrategy()
		 * @generated
		 */
		EClass DEFAULT_STRATEGY = eINSTANCE.getDefaultStrategy();

	}

} //StrategiesPackage
