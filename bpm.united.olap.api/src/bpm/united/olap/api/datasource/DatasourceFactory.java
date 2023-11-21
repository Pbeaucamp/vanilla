/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.datasource;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see bpm.united.olap.api.datasource.DatasourcePackage
 * @generated
 */
public interface DatasourceFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	DatasourceFactory eINSTANCE = bpm.united.olap.api.datasource.impl.DatasourceFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Datasource</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Datasource</em>'.
	 * @generated
	 */
	Datasource createDatasource();

	/**
	 * Returns a new object of class '<em>Data Object</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Data Object</em>'.
	 * @generated
	 */
	DataObject createDataObject();

	/**
	 * Returns a new object of class '<em>Data Object Item</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Data Object Item</em>'.
	 * @generated
	 */
	DataObjectItem createDataObjectItem();

	/**
	 * Returns a new object of class '<em>Connection</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Connection</em>'.
	 * @generated
	 */
	Connection createConnection();

	/**
	 * Returns a new object of class '<em>Relation</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Relation</em>'.
	 * @generated
	 */
	Relation createRelation();

	/**
	 * Returns a new object of class '<em>Operator</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Operator</em>'.
	 * @generated
	 */
	Operator createOperator();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	DatasourcePackage getDatasourcePackage();

} //DatasourceFactory
