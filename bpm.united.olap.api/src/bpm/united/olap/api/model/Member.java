/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.model;

import java.util.List;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Member</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link bpm.united.olap.api.model.Member#getParentMember <em>Parent Member</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.Member#getSubMembers <em>Sub Members</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.Member#getParentLevel <em>Parent Level</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.Member#getMemberRelationsUname <em>Member Relations Uname</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.Member#getProperties <em>Properties</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.Member#getOrderValue <em>Order Value</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.Member#getOrderUname <em>Order Uname</em>}</li>
 * </ul>
 * </p>
 *
 * @see bpm.united.olap.api.model.ModelPackage#getMember()
 * @model
 * @generated
 */
public interface Member extends ElementDefinition {
	/**
	 * Returns the value of the '<em><b>Parent Member</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link bpm.united.olap.api.model.Member#getSubMembers <em>Sub Members</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parent Member</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parent Member</em>' reference.
	 * @see #setParentMember(Member)
	 * @see bpm.united.olap.api.model.ModelPackage#getMember_ParentMember()
	 * @see bpm.united.olap.api.model.Member#getSubMembers
	 * @model opposite="subMembers"
	 * @generated
	 */
	Member getParentMember();
	
	
	
	/**
	 * Sets the value of the '{@link bpm.united.olap.api.model.Member#getParentMember <em>Parent Member</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parent Member</em>' reference.
	 * @see #getParentMember()
	 * @generated
	 */
	void setParentMember(Member value);

	/**
	 * Returns the value of the '<em><b>Sub Members</b></em>' reference list.
	 * The list contents are of type {@link bpm.united.olap.api.model.Member}.
	 * It is bidirectional and its opposite is '{@link bpm.united.olap.api.model.Member#getParentMember <em>Parent Member</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sub Members</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sub Members</em>' reference list.
	 * @see bpm.united.olap.api.model.ModelPackage#getMember_SubMembers()
	 * @see bpm.united.olap.api.model.Member#getParentMember
	 * @model opposite="parentMember"
	 * @generated
	 */
	List<Member> getSubMembers();

	/**
	 * Returns the value of the '<em><b>Parent Level</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link bpm.united.olap.api.model.Level#getMembers <em>Members</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parent Level</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parent Level</em>' container reference.
	 * @see #setParentLevel(Level)
	 * @see bpm.united.olap.api.model.ModelPackage#getMember_ParentLevel()
	 * @see bpm.united.olap.api.model.Level#getMembers
	 * @model opposite="members" transient="false"
	 * @generated
	 */
	Level getParentLevel();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.model.Member#getParentLevel <em>Parent Level</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parent Level</em>' container reference.
	 * @see #getParentLevel()
	 * @generated
	 */
	void setParentLevel(Level value);

	/**
	 * Returns the value of the '<em><b>Member Relations Uname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Member Relations Uname</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Member Relations Uname</em>' attribute.
	 * @see #setMemberRelationsUname(String)
	 * @see bpm.united.olap.api.model.ModelPackage#getMember_MemberRelationsUname()
	 * @model
	 * @generated
	 */
	String getMemberRelationsUname();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.model.Member#getMemberRelationsUname <em>Member Relations Uname</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Member Relations Uname</em>' attribute.
	 * @see #getMemberRelationsUname()
	 * @generated
	 */
	void setMemberRelationsUname(String value);

	/**
	 * Returns the value of the '<em><b>Properties</b></em>' containment reference list.
	 * The list contents are of type {@link bpm.united.olap.api.model.MemberProperty}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Properties</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Properties</em>' containment reference list.
	 * @see bpm.united.olap.api.model.ModelPackage#getMember_Properties()
	 * @model containment="true"
	 * @generated
	 */
	List<MemberProperty> getProperties();

	/**
	 * Returns the value of the '<em><b>Order Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Order Value</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Order Value</em>' attribute.
	 * @see #setOrderValue(String)
	 * @see bpm.united.olap.api.model.ModelPackage#getMember_OrderValue()
	 * @model
	 * @generated
	 */
	String getOrderValue();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.model.Member#getOrderValue <em>Order Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Order Value</em>' attribute.
	 * @see #getOrderValue()
	 * @generated
	 */
	void setOrderValue(String value);

	/**
	 * Returns the value of the '<em><b>Order Uname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Order Uname</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Order Uname</em>' attribute.
	 * @see #setOrderUname(String)
	 * @see bpm.united.olap.api.model.ModelPackage#getMember_OrderUname()
	 * @model
	 * @generated
	 */
	String getOrderUname();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.model.Member#getOrderUname <em>Order Uname</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Order Uname</em>' attribute.
	 * @see #getOrderUname()
	 * @generated
	 */
	void setOrderUname(String value);
	
	public static final String NULL_VALUE = "NULL";
	
	boolean isChildsLoaded();
	
	void setChildsLoaded(boolean childsLoaded);
	
	void setParentHierarchy(Hierarchy hierarchy);
	
	Hierarchy getParentHierarchy();
	
	boolean hasProjectionMembers();
	
	void setHasProjectionMembers(boolean hasProjectionMembers);
	
	boolean isProjectionMember();
	
	void setProjectionMember(boolean isProjectionMember);
	
//	/**
//	 * return the unique key in the data's(DB,..) to retrieve this member
//	 * this key is supposed to be the one stored inn the factTable
//	 * @return
//	 */
//	public Object getUniqueKey();

} // Member
