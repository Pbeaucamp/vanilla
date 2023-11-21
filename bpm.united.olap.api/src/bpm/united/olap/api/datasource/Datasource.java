/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.datasource;

import bpm.united.olap.api.model.Schema;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Datasource</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link bpm.united.olap.api.datasource.Datasource#getName <em>Name</em>}</li>
 *   <li>{@link bpm.united.olap.api.datasource.Datasource#getId <em>Id</em>}</li>
 *   <li>{@link bpm.united.olap.api.datasource.Datasource#getConnections <em>Connections</em>}</li>
 *   <li>{@link bpm.united.olap.api.datasource.Datasource#getRelations <em>Relations</em>}</li>
 *   <li>{@link bpm.united.olap.api.datasource.Datasource#getDataObjects <em>Data Objects</em>}</li>
 *   <li>{@link bpm.united.olap.api.datasource.Datasource#getPublicProperties <em>Public Properties</em>}</li>
 *   <li>{@link bpm.united.olap.api.datasource.Datasource#getDatasourceExtensionId <em>Datasource Extension Id</em>}</li>
 *   <li>{@link bpm.united.olap.api.datasource.Datasource#getPrivateProperties <em>Private Properties</em>}</li>
 *   <li>{@link bpm.united.olap.api.datasource.Datasource#getParent <em>Parent</em>}</li>
 * </ul>
 * </p>
 *
 * @see bpm.united.olap.api.datasource.DatasourcePackage#getDatasource()
 * @model
 * @generated
 */
public interface Datasource extends EObject, Serializable {
	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see bpm.united.olap.api.datasource.DatasourcePackage#getDatasource_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.datasource.Datasource#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(String)
	 * @see bpm.united.olap.api.datasource.DatasourcePackage#getDatasource_Id()
	 * @model
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.datasource.Datasource#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

	/**
	 * Returns the value of the '<em><b>Connections</b></em>' containment reference list.
	 * The list contents are of type {@link bpm.united.olap.api.datasource.Connection}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Connections</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Connections</em>' containment reference list.
	 * @see bpm.united.olap.api.datasource.DatasourcePackage#getDatasource_Connections()
	 * @model containment="true"
	 * @generated
	 */
	List<Connection> getConnections();

	/**
	 * Returns the value of the '<em><b>Relations</b></em>' containment reference list.
	 * The list contents are of type {@link bpm.united.olap.api.datasource.Relation}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Relations</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Relations</em>' containment reference list.
	 * @see bpm.united.olap.api.datasource.DatasourcePackage#getDatasource_Relations()
	 * @model containment="true"
	 * @generated
	 */
	List<Relation> getRelations();

	/**
	 * Returns the value of the '<em><b>Data Objects</b></em>' containment reference list.
	 * The list contents are of type {@link bpm.united.olap.api.datasource.DataObject}.
	 * It is bidirectional and its opposite is '{@link bpm.united.olap.api.datasource.DataObject#getParent <em>Parent</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Data Objects</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Data Objects</em>' containment reference list.
	 * @see bpm.united.olap.api.datasource.DatasourcePackage#getDatasource_DataObjects()
	 * @see bpm.united.olap.api.datasource.DataObject#getParent
	 * @model opposite="parent" containment="true"
	 * @generated
	 */
	List<DataObject> getDataObjects();

	/**
	 * Returns the value of the '<em><b>Public Properties</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Public Properties</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Public Properties</em>' attribute.
	 * @see #setPublicProperties(Properties)
	 * @see bpm.united.olap.api.datasource.DatasourcePackage#getDatasource_PublicProperties()
	 * @model dataType="bpm.united.olap.api.Properties"
	 * @generated
	 */
	Properties getPublicProperties();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.datasource.Datasource#getPublicProperties <em>Public Properties</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Public Properties</em>' attribute.
	 * @see #getPublicProperties()
	 * @generated
	 */
	void setPublicProperties(Properties value);

	/**
	 * Returns the value of the '<em><b>Datasource Extension Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Datasource Extension Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Datasource Extension Id</em>' attribute.
	 * @see #setDatasourceExtensionId(String)
	 * @see bpm.united.olap.api.datasource.DatasourcePackage#getDatasource_DatasourceExtensionId()
	 * @model
	 * @generated
	 */
	String getDatasourceExtensionId();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.datasource.Datasource#getDatasourceExtensionId <em>Datasource Extension Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Datasource Extension Id</em>' attribute.
	 * @see #getDatasourceExtensionId()
	 * @generated
	 */
	void setDatasourceExtensionId(String value);

	/**
	 * Returns the value of the '<em><b>Private Properties</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Private Properties</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Private Properties</em>' attribute.
	 * @see #setPrivateProperties(Properties)
	 * @see bpm.united.olap.api.datasource.DatasourcePackage#getDatasource_PrivateProperties()
	 * @model dataType="bpm.united.olap.api.Properties"
	 * @generated
	 */
	Properties getPrivateProperties();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.datasource.Datasource#getPrivateProperties <em>Private Properties</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Private Properties</em>' attribute.
	 * @see #getPrivateProperties()
	 * @generated
	 */
	void setPrivateProperties(Properties value);

	/**
	 * Returns the value of the '<em><b>Parent</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link bpm.united.olap.api.model.Schema#getDatasources <em>Datasources</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parent</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parent</em>' container reference.
	 * @see #setParent(Schema)
	 * @see bpm.united.olap.api.datasource.DatasourcePackage#getDatasource_Parent()
	 * @see bpm.united.olap.api.model.Schema#getDatasources
	 * @model opposite="datasources" transient="false"
	 * @generated
	 */
	Schema getParent();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.datasource.Datasource#getParent <em>Parent</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parent</em>' container reference.
	 * @see #getParent()
	 * @generated
	 */
	void setParent(Schema value);

} // Datasource
