/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.datasource;

import java.io.Serializable;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Data Object</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link bpm.united.olap.api.datasource.DataObject#getName <em>Name</em>}</li>
 *   <li>{@link bpm.united.olap.api.datasource.DataObject#getItems <em>Items</em>}</li>
 *   <li>{@link bpm.united.olap.api.datasource.DataObject#getQueryText <em>Query Text</em>}</li>
 *   <li>{@link bpm.united.olap.api.datasource.DataObject#getParent <em>Parent</em>}</li>
 *   <li>{@link bpm.united.olap.api.datasource.DataObject#isIsFact <em>Is Fact</em>}</li>
 * </ul>
 * </p>
 *
 * @see bpm.united.olap.api.datasource.DatasourcePackage#getDataObject()
 * @model
 * @generated
 */
public interface DataObject extends EObject, Serializable {
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
	 * @see bpm.united.olap.api.datasource.DatasourcePackage#getDataObject_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.datasource.DataObject#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Items</b></em>' containment reference list.
	 * The list contents are of type {@link bpm.united.olap.api.datasource.DataObjectItem}.
	 * It is bidirectional and its opposite is '{@link bpm.united.olap.api.datasource.DataObjectItem#getParent <em>Parent</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Items</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Items</em>' containment reference list.
	 * @see bpm.united.olap.api.datasource.DatasourcePackage#getDataObject_Items()
	 * @see bpm.united.olap.api.datasource.DataObjectItem#getParent
	 * @model opposite="parent" containment="true"
	 * @generated
	 */
	List<DataObjectItem> getItems();

	/**
	 * Returns the value of the '<em><b>Query Text</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Query Text</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Query Text</em>' attribute.
	 * @see #setQueryText(String)
	 * @see bpm.united.olap.api.datasource.DatasourcePackage#getDataObject_QueryText()
	 * @model
	 * @generated
	 */
	String getQueryText();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.datasource.DataObject#getQueryText <em>Query Text</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Query Text</em>' attribute.
	 * @see #getQueryText()
	 * @generated
	 */
	void setQueryText(String value);

	/**
	 * Returns the value of the '<em><b>Parent</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link bpm.united.olap.api.datasource.Datasource#getDataObjects <em>Data Objects</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parent</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parent</em>' container reference.
	 * @see #setParent(Datasource)
	 * @see bpm.united.olap.api.datasource.DatasourcePackage#getDataObject_Parent()
	 * @see bpm.united.olap.api.datasource.Datasource#getDataObjects
	 * @model opposite="dataObjects" transient="false"
	 * @generated
	 */
	Datasource getParent();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.datasource.DataObject#getParent <em>Parent</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parent</em>' container reference.
	 * @see #getParent()
	 * @generated
	 */
	void setParent(Datasource value);

	/**
	 * Returns the value of the '<em><b>Is Fact</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Is Fact</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Is Fact</em>' attribute.
	 * @see #setIsFact(boolean)
	 * @see bpm.united.olap.api.datasource.DatasourcePackage#getDataObject_IsFact()
	 * @model
	 * @generated
	 */
	boolean isIsFact();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.datasource.DataObject#isIsFact <em>Is Fact</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Is Fact</em>' attribute.
	 * @see #isIsFact()
	 * @generated
	 */
	void setIsFact(boolean value);
	
	String getId();
	
	void setId(String id);

} // DataObject
