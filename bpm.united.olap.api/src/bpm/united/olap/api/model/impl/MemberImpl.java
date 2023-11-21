/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.model.impl;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.model.MemberProperty;
import bpm.united.olap.api.model.ModelPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Member</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link bpm.united.olap.api.model.impl.MemberImpl#getParentMember <em>Parent Member</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.impl.MemberImpl#getSubMembers <em>Sub Members</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.impl.MemberImpl#getParentLevel <em>Parent Level</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.impl.MemberImpl#getMemberRelationsUname <em>Member Relations Uname</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.impl.MemberImpl#getProperties <em>Properties</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.impl.MemberImpl#getOrderValue <em>Order Value</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.impl.MemberImpl#getOrderUname <em>Order Uname</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MemberImpl extends ElementDefinitionImpl implements Member {
	/**
	 * The cached value of the '{@link #getParentMember() <em>Parent Member</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getParentMember()
	 * @generated
	 * @ordered
	 */
	protected Member parentMember;

	/**
	 * The cached value of the '{@link #getSubMembers() <em>Sub Members</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSubMembers()
	 * @generated
	 * @ordered
	 */
	protected EList<Member> subMembers;

	/**
	 * The default value of the '{@link #getMemberRelationsUname() <em>Member Relations Uname</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMemberRelationsUname()
	 * @generated
	 * @ordered
	 */
	protected static final String MEMBER_RELATIONS_UNAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMemberRelationsUname() <em>Member Relations Uname</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMemberRelationsUname()
	 * @generated
	 * @ordered
	 */
	protected String memberRelationsUname = MEMBER_RELATIONS_UNAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getProperties() <em>Properties</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProperties()
	 * @generated
	 * @ordered
	 */
	protected EList<MemberProperty> properties;

	/**
	 * The default value of the '{@link #getOrderValue() <em>Order Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOrderValue()
	 * @generated
	 * @ordered
	 */
	protected static final String ORDER_VALUE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getOrderValue() <em>Order Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOrderValue()
	 * @generated
	 * @ordered
	 */
	protected String orderValue = ORDER_VALUE_EDEFAULT;

	/**
	 * The default value of the '{@link #getOrderUname() <em>Order Uname</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOrderUname()
	 * @generated
	 * @ordered
	 */
	protected static final String ORDER_UNAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getOrderUname() <em>Order Uname</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOrderUname()
	 * @generated
	 * @ordered
	 */
	protected String orderUname = ORDER_UNAME_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MemberImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ModelPackage.Literals.MEMBER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Member getParentMember() {
		if (parentMember != null && parentMember.eIsProxy()) {
			InternalEObject oldParentMember = (InternalEObject)parentMember;
			parentMember = (Member)eResolveProxy(oldParentMember);
			if (parentMember != oldParentMember) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ModelPackage.MEMBER__PARENT_MEMBER, oldParentMember, parentMember));
			}
		}
		return parentMember;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Member basicGetParentMember() {
		return parentMember;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetParentMember(Member newParentMember, NotificationChain msgs) {
		Member oldParentMember = parentMember;
		parentMember = newParentMember;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ModelPackage.MEMBER__PARENT_MEMBER, oldParentMember, newParentMember);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setParentMember(Member newParentMember) {
		if (newParentMember != parentMember) {
			NotificationChain msgs = null;
			if (parentMember != null)
				msgs = ((InternalEObject)parentMember).eInverseRemove(this, ModelPackage.MEMBER__SUB_MEMBERS, Member.class, msgs);
			if (newParentMember != null)
				msgs = ((InternalEObject)newParentMember).eInverseAdd(this, ModelPackage.MEMBER__SUB_MEMBERS, Member.class, msgs);
			msgs = basicSetParentMember(newParentMember, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.MEMBER__PARENT_MEMBER, newParentMember, newParentMember));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<Member> getSubMembers() {
		if (subMembers == null) {
			subMembers = new EObjectWithInverseResolvingEList<Member>(Member.class, this, ModelPackage.MEMBER__SUB_MEMBERS, ModelPackage.MEMBER__PARENT_MEMBER);
		}
		return subMembers;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Level getParentLevel() {
		if (eContainerFeatureID() != ModelPackage.MEMBER__PARENT_LEVEL) return null;
		return (Level)eContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetParentLevel(Level newParentLevel, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject)newParentLevel, ModelPackage.MEMBER__PARENT_LEVEL, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setParentLevel(Level newParentLevel) {
		if (newParentLevel != eInternalContainer() || (eContainerFeatureID() != ModelPackage.MEMBER__PARENT_LEVEL && newParentLevel != null)) {
			if (EcoreUtil.isAncestor(this, newParentLevel))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newParentLevel != null)
				msgs = ((InternalEObject)newParentLevel).eInverseAdd(this, ModelPackage.LEVEL__MEMBERS, Level.class, msgs);
			msgs = basicSetParentLevel(newParentLevel, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.MEMBER__PARENT_LEVEL, newParentLevel, newParentLevel));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getMemberRelationsUname() {
		return memberRelationsUname;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMemberRelationsUname(String newMemberRelationsUname) {
		String oldMemberRelationsUname = memberRelationsUname;
		memberRelationsUname = newMemberRelationsUname;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.MEMBER__MEMBER_RELATIONS_UNAME, oldMemberRelationsUname, memberRelationsUname));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<MemberProperty> getProperties() {
		if (properties == null) {
			properties = new EObjectContainmentEList<MemberProperty>(MemberProperty.class, this, ModelPackage.MEMBER__PROPERTIES);
		}
		return properties;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getOrderValue() {
		return orderValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setOrderValue(String newOrderValue) {
		String oldOrderValue = orderValue;
		orderValue = newOrderValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.MEMBER__ORDER_VALUE, oldOrderValue, orderValue));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getOrderUname() {
		return orderUname;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setOrderUname(String newOrderUname) {
		String oldOrderUname = orderUname;
		orderUname = newOrderUname;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.MEMBER__ORDER_UNAME, oldOrderUname, orderUname));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ModelPackage.MEMBER__PARENT_MEMBER:
				if (parentMember != null)
					msgs = ((InternalEObject)parentMember).eInverseRemove(this, ModelPackage.MEMBER__SUB_MEMBERS, Member.class, msgs);
				return basicSetParentMember((Member)otherEnd, msgs);
			case ModelPackage.MEMBER__SUB_MEMBERS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getSubMembers()).basicAdd(otherEnd, msgs);
			case ModelPackage.MEMBER__PARENT_LEVEL:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetParentLevel((Level)otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ModelPackage.MEMBER__PARENT_MEMBER:
				return basicSetParentMember(null, msgs);
			case ModelPackage.MEMBER__SUB_MEMBERS:
				return ((InternalEList<?>)getSubMembers()).basicRemove(otherEnd, msgs);
			case ModelPackage.MEMBER__PARENT_LEVEL:
				return basicSetParentLevel(null, msgs);
			case ModelPackage.MEMBER__PROPERTIES:
				return ((InternalEList<?>)getProperties()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
		switch (eContainerFeatureID()) {
			case ModelPackage.MEMBER__PARENT_LEVEL:
				return eInternalContainer().eInverseRemove(this, ModelPackage.LEVEL__MEMBERS, Level.class, msgs);
		}
		return super.eBasicRemoveFromContainerFeature(msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ModelPackage.MEMBER__PARENT_MEMBER:
				if (resolve) return getParentMember();
				return basicGetParentMember();
			case ModelPackage.MEMBER__SUB_MEMBERS:
				return getSubMembers();
			case ModelPackage.MEMBER__PARENT_LEVEL:
				return getParentLevel();
			case ModelPackage.MEMBER__MEMBER_RELATIONS_UNAME:
				return getMemberRelationsUname();
			case ModelPackage.MEMBER__PROPERTIES:
				return getProperties();
			case ModelPackage.MEMBER__ORDER_VALUE:
				return getOrderValue();
			case ModelPackage.MEMBER__ORDER_UNAME:
				return getOrderUname();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case ModelPackage.MEMBER__PARENT_MEMBER:
				setParentMember((Member)newValue);
				return;
			case ModelPackage.MEMBER__SUB_MEMBERS:
				getSubMembers().clear();
				getSubMembers().addAll((Collection<? extends Member>)newValue);
				return;
			case ModelPackage.MEMBER__PARENT_LEVEL:
				setParentLevel((Level)newValue);
				return;
			case ModelPackage.MEMBER__MEMBER_RELATIONS_UNAME:
				setMemberRelationsUname((String)newValue);
				return;
			case ModelPackage.MEMBER__PROPERTIES:
				getProperties().clear();
				getProperties().addAll((Collection<? extends MemberProperty>)newValue);
				return;
			case ModelPackage.MEMBER__ORDER_VALUE:
				setOrderValue((String)newValue);
				return;
			case ModelPackage.MEMBER__ORDER_UNAME:
				setOrderUname((String)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case ModelPackage.MEMBER__PARENT_MEMBER:
				setParentMember((Member)null);
				return;
			case ModelPackage.MEMBER__SUB_MEMBERS:
				getSubMembers().clear();
				return;
			case ModelPackage.MEMBER__PARENT_LEVEL:
				setParentLevel((Level)null);
				return;
			case ModelPackage.MEMBER__MEMBER_RELATIONS_UNAME:
				setMemberRelationsUname(MEMBER_RELATIONS_UNAME_EDEFAULT);
				return;
			case ModelPackage.MEMBER__PROPERTIES:
				getProperties().clear();
				return;
			case ModelPackage.MEMBER__ORDER_VALUE:
				setOrderValue(ORDER_VALUE_EDEFAULT);
				return;
			case ModelPackage.MEMBER__ORDER_UNAME:
				setOrderUname(ORDER_UNAME_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case ModelPackage.MEMBER__PARENT_MEMBER:
				return parentMember != null;
			case ModelPackage.MEMBER__SUB_MEMBERS:
				return subMembers != null && !subMembers.isEmpty();
			case ModelPackage.MEMBER__PARENT_LEVEL:
				return getParentLevel() != null;
			case ModelPackage.MEMBER__MEMBER_RELATIONS_UNAME:
				return MEMBER_RELATIONS_UNAME_EDEFAULT == null ? memberRelationsUname != null : !MEMBER_RELATIONS_UNAME_EDEFAULT.equals(memberRelationsUname);
			case ModelPackage.MEMBER__PROPERTIES:
				return properties != null && !properties.isEmpty();
			case ModelPackage.MEMBER__ORDER_VALUE:
				return ORDER_VALUE_EDEFAULT == null ? orderValue != null : !ORDER_VALUE_EDEFAULT.equals(orderValue);
			case ModelPackage.MEMBER__ORDER_UNAME:
				return ORDER_UNAME_EDEFAULT == null ? orderUname != null : !ORDER_UNAME_EDEFAULT.equals(orderUname);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (memberRelationsUname: ");
		result.append(memberRelationsUname);
		result.append(", orderValue: ");
		result.append(orderValue);
		result.append(", orderUname: ");
		result.append(orderUname);
		result.append(')');
		return result.toString();
	}
	
	private boolean isChildsLoaded = false;

	@Override
	public boolean isChildsLoaded() {
		return isChildsLoaded;
	}

	@Override
	public void setChildsLoaded(boolean childsLoaded) {
		this.isChildsLoaded = childsLoaded;
	}

	private Hierarchy hierarchy;
	
	@Override
	public Hierarchy getParentHierarchy() {
		return hierarchy;
	}

	@Override
	public void setParentHierarchy(Hierarchy hierarchy) {
		this.hierarchy = hierarchy;
	}

	private boolean hasProjectionMembers;
	
	@Override
	public boolean hasProjectionMembers() {
		return hasProjectionMembers;
	}

	@Override
	public void setHasProjectionMembers(boolean hasProjectionMembers) {
		this.hasProjectionMembers = hasProjectionMembers;
	}

	private boolean isProjectionMember;
	
	@Override
	public boolean isProjectionMember() {
		return isProjectionMember;
	}

	@Override
	public void setProjectionMember(boolean isProjectionMember) {
		this.isProjectionMember = isProjectionMember;
	}

} //MemberImpl
