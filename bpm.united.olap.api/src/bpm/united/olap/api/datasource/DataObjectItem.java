/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.datasource;

import java.io.Serializable;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Data Object Item</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link bpm.united.olap.api.datasource.DataObjectItem#getName <em>Name</em>}</li>
 *   <li>{@link bpm.united.olap.api.datasource.DataObjectItem#getParent <em>Parent</em>}</li>
 *   <li>{@link bpm.united.olap.api.datasource.DataObjectItem#getId <em>Id</em>}</li>
 *   <li>{@link bpm.united.olap.api.datasource.DataObjectItem#isIsKey <em>Is Key</em>}</li>
 * </ul>
 * </p>
 *
 * @see bpm.united.olap.api.datasource.DatasourcePackage#getDataObjectItem()
 * @model
 * @generated
 */
public interface DataObjectItem extends EObject,Serializable {
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
	 * @see bpm.united.olap.api.datasource.DatasourcePackage#getDataObjectItem_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.datasource.DataObjectItem#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Parent</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link bpm.united.olap.api.datasource.DataObject#getItems <em>Items</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parent</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parent</em>' container reference.
	 * @see #setParent(DataObject)
	 * @see bpm.united.olap.api.datasource.DatasourcePackage#getDataObjectItem_Parent()
	 * @see bpm.united.olap.api.datasource.DataObject#getItems
	 * @model opposite="items" transient="false"
	 * @generated
	 */
	DataObject getParent();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.datasource.DataObjectItem#getParent <em>Parent</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parent</em>' container reference.
	 * @see #getParent()
	 * @generated
	 */
	void setParent(DataObject value);

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
	 * @see bpm.united.olap.api.datasource.DatasourcePackage#getDataObjectItem_Id()
	 * @model
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.datasource.DataObjectItem#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

	/**
	 * Returns the value of the '<em><b>Is Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Is Key</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Is Key</em>' attribute.
	 * @see #setIsKey(boolean)
	 * @see bpm.united.olap.api.datasource.DatasourcePackage#getDataObjectItem_IsKey()
	 * @model
	 * @generated
	 */
	boolean isIsKey();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.datasource.DataObjectItem#isIsKey <em>Is Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Is Key</em>' attribute.
	 * @see #isIsKey()
	 * @generated
	 */
	void setIsKey(boolean value);

} // DataObjectItem
