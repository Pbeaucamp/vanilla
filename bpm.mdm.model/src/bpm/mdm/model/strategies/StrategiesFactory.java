/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.mdm.model.strategies;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see bpm.mdm.model.strategies.StrategiesPackage
 * @generated
 */
public interface StrategiesFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	StrategiesFactory eINSTANCE = bpm.mdm.model.strategies.impl.StrategiesFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Default Strategy</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Default Strategy</em>'.
	 * @generated
	 */
	DefaultStrategy createDefaultStrategy();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	StrategiesPackage getStrategiesPackage();

} //StrategiesFactory
