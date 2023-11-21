/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see bpm.united.olap.api.ApiPackage
 * @generated
 */
public interface ApiFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ApiFactory eINSTANCE = bpm.united.olap.api.impl.ApiFactoryImpl.init();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	ApiPackage getApiPackage();

} //ApiFactory
