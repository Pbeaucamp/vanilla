/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.result;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see bpm.united.olap.api.result.ResultPackage
 * @generated
 */
public interface ResultFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ResultFactory eINSTANCE = bpm.united.olap.api.result.impl.ResultFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Olap Result</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Olap Result</em>'.
	 * @generated
	 */
	OlapResult createOlapResult();

	/**
	 * Returns a new object of class '<em>Line</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Line</em>'.
	 * @generated
	 */
	ResultLine createResultLine();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	ResultPackage getResultPackage();

} //ResultFactory
