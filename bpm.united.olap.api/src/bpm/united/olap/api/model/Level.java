/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.model;

import java.util.List;

import bpm.united.olap.api.datasource.DataObjectItem;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Level</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link bpm.united.olap.api.model.Level#getItem <em>Item</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.Level#getParentLevel <em>Parent Level</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.Level#getSubLevel <em>Sub Level</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.Level#getMembers <em>Members</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.Level#getParentHierarchy <em>Parent Hierarchy</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.Level#getMemberProperties <em>Member Properties</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.Level#getOrderItem <em>Order Item</em>}</li>
 * </ul>
 * </p>
 *
 * @see bpm.united.olap.api.model.ModelPackage#getLevel()
 * @model
 * @generated
 */
public interface Level extends ElementDefinition {
	/**
	 * Returns the value of the '<em><b>Item</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Item</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Item</em>' reference.
	 * @see #setItem(DataObjectItem)
	 * @see bpm.united.olap.api.model.ModelPackage#getLevel_Item()
	 * @model
	 * @generated
	 */
	DataObjectItem getItem();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.model.Level#getItem <em>Item</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Item</em>' reference.
	 * @see #getItem()
	 * @generated
	 */
	void setItem(DataObjectItem value);

	/**
	 * Returns the value of the '<em><b>Parent Level</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link bpm.united.olap.api.model.Level#getSubLevel <em>Sub Level</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parent Level</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parent Level</em>' reference.
	 * @see #setParentLevel(Level)
	 * @see bpm.united.olap.api.model.ModelPackage#getLevel_ParentLevel()
	 * @see bpm.united.olap.api.model.Level#getSubLevel
	 * @model opposite="subLevel"
	 * @generated
	 */
	Level getParentLevel();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.model.Level#getParentLevel <em>Parent Level</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parent Level</em>' reference.
	 * @see #getParentLevel()
	 * @generated
	 */
	void setParentLevel(Level value);

	/**
	 * Returns the value of the '<em><b>Sub Level</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link bpm.united.olap.api.model.Level#getParentLevel <em>Parent Level</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sub Level</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sub Level</em>' reference.
	 * @see #setSubLevel(Level)
	 * @see bpm.united.olap.api.model.ModelPackage#getLevel_SubLevel()
	 * @see bpm.united.olap.api.model.Level#getParentLevel
	 * @model opposite="parentLevel"
	 * @generated
	 */
	Level getSubLevel();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.model.Level#getSubLevel <em>Sub Level</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Sub Level</em>' reference.
	 * @see #getSubLevel()
	 * @generated
	 */
	void setSubLevel(Level value);

	/**
	 * Returns the value of the '<em><b>Members</b></em>' containment reference list.
	 * The list contents are of type {@link bpm.united.olap.api.model.Member}.
	 * It is bidirectional and its opposite is '{@link bpm.united.olap.api.model.Member#getParentLevel <em>Parent Level</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Members</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Members</em>' containment reference list.
	 * @see bpm.united.olap.api.model.ModelPackage#getLevel_Members()
	 * @see bpm.united.olap.api.model.Member#getParentLevel
	 * @model opposite="parentLevel" containment="true"
	 * @generated
	 */
	List<Member> getMembers();

	/**
	 * Returns the value of the '<em><b>Parent Hierarchy</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link bpm.united.olap.api.model.Hierarchy#getLevels <em>Levels</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parent Hierarchy</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parent Hierarchy</em>' container reference.
	 * @see #setParentHierarchy(Hierarchy)
	 * @see bpm.united.olap.api.model.ModelPackage#getLevel_ParentHierarchy()
	 * @see bpm.united.olap.api.model.Hierarchy#getLevels
	 * @model opposite="levels" transient="false"
	 * @generated
	 */
	Hierarchy getParentHierarchy();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.model.Level#getParentHierarchy <em>Parent Hierarchy</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parent Hierarchy</em>' container reference.
	 * @see #getParentHierarchy()
	 * @generated
	 */
	void setParentHierarchy(Hierarchy value);

	/**
	 * Returns the value of the '<em><b>Member Properties</b></em>' containment reference list.
	 * The list contents are of type {@link bpm.united.olap.api.model.MemberProperty}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Member Properties</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Member Properties</em>' containment reference list.
	 * @see bpm.united.olap.api.model.ModelPackage#getLevel_MemberProperties()
	 * @model containment="true"
	 * @generated
	 */
	List<MemberProperty> getMemberProperties();

	/**
	 * Returns the value of the '<em><b>Order Item</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Order Item</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Order Item</em>' reference.
	 * @see #setOrderItem(DataObjectItem)
	 * @see bpm.united.olap.api.model.ModelPackage#getLevel_OrderItem()
	 * @model
	 * @generated
	 */
	DataObjectItem getOrderItem();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.model.Level#getOrderItem <em>Order Item</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Order Item</em>' reference.
	 * @see #getOrderItem()
	 * @generated
	 */
	void setOrderItem(DataObjectItem value);
	
	void setLabelItem(DataObjectItem value);
	
	DataObjectItem getLabelItem();

} // Level
