/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.runtime;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see bpm.united.olap.api.runtime.RuntimePackage
 * @generated
 */
public interface RuntimeFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	RuntimeFactory eINSTANCE = bpm.united.olap.api.runtime.impl.RuntimeFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Run Result</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Run Result</em>'.
	 * @generated
	 */
	RunResult createRunResult();

	/**
	 * Returns a new object of class '<em>Mdx Set</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Mdx Set</em>'.
	 * @generated
	 */
	MdxSet createMdxSet();

	/**
	 * Returns a new object of class '<em>Data Storage</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Data Storage</em>'.
	 * @generated
	 */
	DataStorage createDataStorage();

	/**
	 * Returns a new object of class '<em>Data Cell</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Data Cell</em>'.
	 * @generated
	 */
	DataCell createDataCell();


	
	DataCellIdentifier2 createDataCellIdentifier2();



	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	RuntimePackage getRuntimePackage();

} //RuntimeFactory
