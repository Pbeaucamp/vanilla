/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.model;

import java.io.Serializable;

import bpm.united.olap.api.datasource.DataObjectItem;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Member Property</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link bpm.united.olap.api.model.MemberProperty#getName <em>Name</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.MemberProperty#getValueItem <em>Value Item</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.MemberProperty#getType <em>Type</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.MemberProperty#getValue <em>Value</em>}</li>
 * </ul>
 * </p>
 *
 * @see bpm.united.olap.api.model.ModelPackage#getMemberProperty()
 * @model
 * @generated
 */
public interface MemberProperty extends EObject, Serializable {
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
	 * @see bpm.united.olap.api.model.ModelPackage#getMemberProperty_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.model.MemberProperty#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Value Item</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Value Item</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Value Item</em>' reference.
	 * @see #setValueItem(DataObjectItem)
	 * @see bpm.united.olap.api.model.ModelPackage#getMemberProperty_ValueItem()
	 * @model
	 * @generated
	 */
	DataObjectItem getValueItem();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.model.MemberProperty#getValueItem <em>Value Item</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value Item</em>' reference.
	 * @see #getValueItem()
	 * @generated
	 */
	void setValueItem(DataObjectItem value);

	/**
	 * Returns the value of the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Value</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Value</em>' attribute.
	 * @see #setValue(String)
	 * @see bpm.united.olap.api.model.ModelPackage#getMemberProperty_Value()
	 * @model
	 * @generated
	 */
	String getValue();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.model.MemberProperty#getValue <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value</em>' attribute.
	 * @see #getValue()
	 * @generated
	 */
	void setValue(String value);

	/**
	 * Returns the value of the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type</em>' attribute.
	 * @see #setType(String)
	 * @see bpm.united.olap.api.model.ModelPackage#getMemberProperty_Type()
	 * @model
	 * @generated
	 */
	String getType();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.model.MemberProperty#getType <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type</em>' attribute.
	 * @see #getType()
	 * @generated
	 */
	void setType(String value);

} // MemberProperty
