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
 * A representation of the model object '<em><b>Relation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link bpm.united.olap.api.datasource.Relation#getOperator <em>Operator</em>}</li>
 *   <li>{@link bpm.united.olap.api.datasource.Relation#getLeftItem <em>Left Item</em>}</li>
 *   <li>{@link bpm.united.olap.api.datasource.Relation#getRightItem <em>Right Item</em>}</li>
 * </ul>
 * </p>
 *
 * @see bpm.united.olap.api.datasource.DatasourcePackage#getRelation()
 * @model
 * @generated
 */
public interface Relation extends EObject, Serializable {
	/**
	 * Returns the value of the '<em><b>Operator</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Operator</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Operator</em>' containment reference.
	 * @see #setOperator(Operator)
	 * @see bpm.united.olap.api.datasource.DatasourcePackage#getRelation_Operator()
	 * @model containment="true"
	 * @generated
	 */
	Operator getOperator();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.datasource.Relation#getOperator <em>Operator</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Operator</em>' containment reference.
	 * @see #getOperator()
	 * @generated
	 */
	void setOperator(Operator value);

	/**
	 * Returns the value of the '<em><b>Left Item</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Left Item</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Left Item</em>' reference.
	 * @see #setLeftItem(DataObjectItem)
	 * @see bpm.united.olap.api.datasource.DatasourcePackage#getRelation_LeftItem()
	 * @model
	 * @generated
	 */
	DataObjectItem getLeftItem();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.datasource.Relation#getLeftItem <em>Left Item</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Left Item</em>' reference.
	 * @see #getLeftItem()
	 * @generated
	 */
	void setLeftItem(DataObjectItem value);

	/**
	 * Returns the value of the '<em><b>Right Item</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Right Item</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Right Item</em>' reference.
	 * @see #setRightItem(DataObjectItem)
	 * @see bpm.united.olap.api.datasource.DatasourcePackage#getRelation_RightItem()
	 * @model
	 * @generated
	 */
	DataObjectItem getRightItem();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.datasource.Relation#getRightItem <em>Right Item</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Right Item</em>' reference.
	 * @see #getRightItem()
	 * @generated
	 */
	void setRightItem(DataObjectItem value);

} // Relation
