/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.model;

import java.io.Serializable;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Element Definition</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link bpm.united.olap.api.model.ElementDefinition#getName <em>Name</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.ElementDefinition#getUname <em>Uname</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.ElementDefinition#getCaption <em>Caption</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.ElementDefinition#isIsVisible <em>Is Visible</em>}</li>
 * </ul>
 * </p>
 *
 * @see bpm.united.olap.api.model.ModelPackage#getElementDefinition()
 * @model
 * @generated
 */
public interface ElementDefinition extends EObject,Serializable, Comparable<ElementDefinition> {
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
	 * @see bpm.united.olap.api.model.ModelPackage#getElementDefinition_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.model.ElementDefinition#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Uname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Uname</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Uname</em>' attribute.
	 * @see #setUname(String)
	 * @see bpm.united.olap.api.model.ModelPackage#getElementDefinition_Uname()
	 * @model
	 * @generated
	 */
	String getUname();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.model.ElementDefinition#getUname <em>Uname</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Uname</em>' attribute.
	 * @see #getUname()
	 * @generated
	 */
	void setUname(String value);

	/**
	 * Returns the value of the '<em><b>Caption</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Caption</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Caption</em>' attribute.
	 * @see #setCaption(String)
	 * @see bpm.united.olap.api.model.ModelPackage#getElementDefinition_Caption()
	 * @model
	 * @generated
	 */
	String getCaption();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.model.ElementDefinition#getCaption <em>Caption</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Caption</em>' attribute.
	 * @see #getCaption()
	 * @generated
	 */
	void setCaption(String value);

	/**
	 * Returns the value of the '<em><b>Is Visible</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Is Visible</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Is Visible</em>' attribute.
	 * @see #setIsVisible(boolean)
	 * @see bpm.united.olap.api.model.ModelPackage#getElementDefinition_IsVisible()
	 * @model
	 * @generated
	 */
	boolean isIsVisible();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.model.ElementDefinition#isIsVisible <em>Is Visible</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Is Visible</em>' attribute.
	 * @see #isIsVisible()
	 * @generated
	 */
	void setIsVisible(boolean value);

} // ElementDefinition
