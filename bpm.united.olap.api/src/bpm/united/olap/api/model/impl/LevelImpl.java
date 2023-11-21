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
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import bpm.united.olap.api.datasource.DataObjectItem;
import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.model.MemberProperty;
import bpm.united.olap.api.model.ModelPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Level</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link bpm.united.olap.api.model.impl.LevelImpl#getItem <em>Item</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.impl.LevelImpl#getParentLevel <em>Parent Level</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.impl.LevelImpl#getSubLevel <em>Sub Level</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.impl.LevelImpl#getMembers <em>Members</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.impl.LevelImpl#getParentHierarchy <em>Parent Hierarchy</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.impl.LevelImpl#getMemberProperties <em>Member Properties</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.impl.LevelImpl#getOrderItem <em>Order Item</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class LevelImpl extends ElementDefinitionImpl implements Level {
	/**
	 * The cached value of the '{@link #getItem() <em>Item</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getItem()
	 * @generated
	 * @ordered
	 */
	protected DataObjectItem item;

	/**
	 * The cached value of the '{@link #getParentLevel() <em>Parent Level</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getParentLevel()
	 * @generated
	 * @ordered
	 */
	protected Level parentLevel;

	/**
	 * The cached value of the '{@link #getSubLevel() <em>Sub Level</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSubLevel()
	 * @generated
	 * @ordered
	 */
	protected Level subLevel;

	/**
	 * The cached value of the '{@link #getMembers() <em>Members</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMembers()
	 * @generated
	 * @ordered
	 */
	protected EList<Member> members;

	/**
	 * The cached value of the '{@link #getMemberProperties() <em>Member Properties</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMemberProperties()
	 * @generated
	 * @ordered
	 */
	protected EList<MemberProperty> memberProperties;

	/**
	 * The cached value of the '{@link #getOrderItem() <em>Order Item</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOrderItem()
	 * @generated
	 * @ordered
	 */
	protected DataObjectItem orderItem;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected LevelImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ModelPackage.Literals.LEVEL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DataObjectItem getItem() {
		if (item != null && item.eIsProxy()) {
			InternalEObject oldItem = (InternalEObject)item;
			item = (DataObjectItem)eResolveProxy(oldItem);
			if (item != oldItem) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ModelPackage.LEVEL__ITEM, oldItem, item));
			}
		}
		return item;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DataObjectItem basicGetItem() {
		return item;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setItem(DataObjectItem newItem) {
		DataObjectItem oldItem = item;
		item = newItem;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.LEVEL__ITEM, oldItem, item));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Level getParentLevel() {
		if (parentLevel != null && parentLevel.eIsProxy()) {
			InternalEObject oldParentLevel = (InternalEObject)parentLevel;
			parentLevel = (Level)eResolveProxy(oldParentLevel);
			if (parentLevel != oldParentLevel) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ModelPackage.LEVEL__PARENT_LEVEL, oldParentLevel, parentLevel));
			}
		}
		return parentLevel;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Level basicGetParentLevel() {
		return parentLevel;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetParentLevel(Level newParentLevel, NotificationChain msgs) {
		Level oldParentLevel = parentLevel;
		parentLevel = newParentLevel;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ModelPackage.LEVEL__PARENT_LEVEL, oldParentLevel, newParentLevel);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setParentLevel(Level newParentLevel) {
		if (newParentLevel != parentLevel) {
			NotificationChain msgs = null;
			if (parentLevel != null)
				msgs = ((InternalEObject)parentLevel).eInverseRemove(this, ModelPackage.LEVEL__SUB_LEVEL, Level.class, msgs);
			if (newParentLevel != null)
				msgs = ((InternalEObject)newParentLevel).eInverseAdd(this, ModelPackage.LEVEL__SUB_LEVEL, Level.class, msgs);
			msgs = basicSetParentLevel(newParentLevel, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.LEVEL__PARENT_LEVEL, newParentLevel, newParentLevel));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Level getSubLevel() {
		if (subLevel != null && subLevel.eIsProxy()) {
			InternalEObject oldSubLevel = (InternalEObject)subLevel;
			subLevel = (Level)eResolveProxy(oldSubLevel);
			if (subLevel != oldSubLevel) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ModelPackage.LEVEL__SUB_LEVEL, oldSubLevel, subLevel));
			}
		}
		return subLevel;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Level basicGetSubLevel() {
		return subLevel;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetSubLevel(Level newSubLevel, NotificationChain msgs) {
		Level oldSubLevel = subLevel;
		subLevel = newSubLevel;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ModelPackage.LEVEL__SUB_LEVEL, oldSubLevel, newSubLevel);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSubLevel(Level newSubLevel) {
		if (newSubLevel != subLevel) {
			NotificationChain msgs = null;
			if (subLevel != null)
				msgs = ((InternalEObject)subLevel).eInverseRemove(this, ModelPackage.LEVEL__PARENT_LEVEL, Level.class, msgs);
			if (newSubLevel != null)
				msgs = ((InternalEObject)newSubLevel).eInverseAdd(this, ModelPackage.LEVEL__PARENT_LEVEL, Level.class, msgs);
			msgs = basicSetSubLevel(newSubLevel, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.LEVEL__SUB_LEVEL, newSubLevel, newSubLevel));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<Member> getMembers() {
		if (members == null) {
			members = new EObjectContainmentWithInverseEList<Member>(Member.class, this, ModelPackage.LEVEL__MEMBERS, ModelPackage.MEMBER__PARENT_LEVEL);
		}
		return members;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Hierarchy getParentHierarchy() {
		if (eContainerFeatureID() != ModelPackage.LEVEL__PARENT_HIERARCHY) return null;
		return (Hierarchy)eContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetParentHierarchy(Hierarchy newParentHierarchy, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject)newParentHierarchy, ModelPackage.LEVEL__PARENT_HIERARCHY, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setParentHierarchy(Hierarchy newParentHierarchy) {
		if (newParentHierarchy != eInternalContainer() || (eContainerFeatureID() != ModelPackage.LEVEL__PARENT_HIERARCHY && newParentHierarchy != null)) {
			if (EcoreUtil.isAncestor(this, newParentHierarchy))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newParentHierarchy != null)
				msgs = ((InternalEObject)newParentHierarchy).eInverseAdd(this, ModelPackage.HIERARCHY__LEVELS, Hierarchy.class, msgs);
			msgs = basicSetParentHierarchy(newParentHierarchy, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.LEVEL__PARENT_HIERARCHY, newParentHierarchy, newParentHierarchy));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<MemberProperty> getMemberProperties() {
		if (memberProperties == null) {
			memberProperties = new EObjectContainmentEList<MemberProperty>(MemberProperty.class, this, ModelPackage.LEVEL__MEMBER_PROPERTIES);
		}
		return memberProperties;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DataObjectItem getOrderItem() {
		if (orderItem != null && orderItem.eIsProxy()) {
			InternalEObject oldOrderItem = (InternalEObject)orderItem;
			orderItem = (DataObjectItem)eResolveProxy(oldOrderItem);
			if (orderItem != oldOrderItem) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ModelPackage.LEVEL__ORDER_ITEM, oldOrderItem, orderItem));
			}
		}
		return orderItem;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DataObjectItem basicGetOrderItem() {
		return orderItem;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setOrderItem(DataObjectItem newOrderItem) {
		DataObjectItem oldOrderItem = orderItem;
		orderItem = newOrderItem;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.LEVEL__ORDER_ITEM, oldOrderItem, orderItem));
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
			case ModelPackage.LEVEL__PARENT_LEVEL:
				if (parentLevel != null)
					msgs = ((InternalEObject)parentLevel).eInverseRemove(this, ModelPackage.LEVEL__SUB_LEVEL, Level.class, msgs);
				return basicSetParentLevel((Level)otherEnd, msgs);
			case ModelPackage.LEVEL__SUB_LEVEL:
				if (subLevel != null)
					msgs = ((InternalEObject)subLevel).eInverseRemove(this, ModelPackage.LEVEL__PARENT_LEVEL, Level.class, msgs);
				return basicSetSubLevel((Level)otherEnd, msgs);
			case ModelPackage.LEVEL__MEMBERS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getMembers()).basicAdd(otherEnd, msgs);
			case ModelPackage.LEVEL__PARENT_HIERARCHY:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetParentHierarchy((Hierarchy)otherEnd, msgs);
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
			case ModelPackage.LEVEL__PARENT_LEVEL:
				return basicSetParentLevel(null, msgs);
			case ModelPackage.LEVEL__SUB_LEVEL:
				return basicSetSubLevel(null, msgs);
			case ModelPackage.LEVEL__MEMBERS:
				return ((InternalEList<?>)getMembers()).basicRemove(otherEnd, msgs);
			case ModelPackage.LEVEL__PARENT_HIERARCHY:
				return basicSetParentHierarchy(null, msgs);
			case ModelPackage.LEVEL__MEMBER_PROPERTIES:
				return ((InternalEList<?>)getMemberProperties()).basicRemove(otherEnd, msgs);
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
			case ModelPackage.LEVEL__PARENT_HIERARCHY:
				return eInternalContainer().eInverseRemove(this, ModelPackage.HIERARCHY__LEVELS, Hierarchy.class, msgs);
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
			case ModelPackage.LEVEL__ITEM:
				if (resolve) return getItem();
				return basicGetItem();
			case ModelPackage.LEVEL__PARENT_LEVEL:
				if (resolve) return getParentLevel();
				return basicGetParentLevel();
			case ModelPackage.LEVEL__SUB_LEVEL:
				if (resolve) return getSubLevel();
				return basicGetSubLevel();
			case ModelPackage.LEVEL__MEMBERS:
				return getMembers();
			case ModelPackage.LEVEL__PARENT_HIERARCHY:
				return getParentHierarchy();
			case ModelPackage.LEVEL__MEMBER_PROPERTIES:
				return getMemberProperties();
			case ModelPackage.LEVEL__ORDER_ITEM:
				if (resolve) return getOrderItem();
				return basicGetOrderItem();
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
			case ModelPackage.LEVEL__ITEM:
				setItem((DataObjectItem)newValue);
				return;
			case ModelPackage.LEVEL__PARENT_LEVEL:
				setParentLevel((Level)newValue);
				return;
			case ModelPackage.LEVEL__SUB_LEVEL:
				setSubLevel((Level)newValue);
				return;
			case ModelPackage.LEVEL__MEMBERS:
				getMembers().clear();
				getMembers().addAll((Collection<? extends Member>)newValue);
				return;
			case ModelPackage.LEVEL__PARENT_HIERARCHY:
				setParentHierarchy((Hierarchy)newValue);
				return;
			case ModelPackage.LEVEL__MEMBER_PROPERTIES:
				getMemberProperties().clear();
				getMemberProperties().addAll((Collection<? extends MemberProperty>)newValue);
				return;
			case ModelPackage.LEVEL__ORDER_ITEM:
				setOrderItem((DataObjectItem)newValue);
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
			case ModelPackage.LEVEL__ITEM:
				setItem((DataObjectItem)null);
				return;
			case ModelPackage.LEVEL__PARENT_LEVEL:
				setParentLevel((Level)null);
				return;
			case ModelPackage.LEVEL__SUB_LEVEL:
				setSubLevel((Level)null);
				return;
			case ModelPackage.LEVEL__MEMBERS:
				getMembers().clear();
				return;
			case ModelPackage.LEVEL__PARENT_HIERARCHY:
				setParentHierarchy((Hierarchy)null);
				return;
			case ModelPackage.LEVEL__MEMBER_PROPERTIES:
				getMemberProperties().clear();
				return;
			case ModelPackage.LEVEL__ORDER_ITEM:
				setOrderItem((DataObjectItem)null);
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
			case ModelPackage.LEVEL__ITEM:
				return item != null;
			case ModelPackage.LEVEL__PARENT_LEVEL:
				return parentLevel != null;
			case ModelPackage.LEVEL__SUB_LEVEL:
				return subLevel != null;
			case ModelPackage.LEVEL__MEMBERS:
				return members != null && !members.isEmpty();
			case ModelPackage.LEVEL__PARENT_HIERARCHY:
				return getParentHierarchy() != null;
			case ModelPackage.LEVEL__MEMBER_PROPERTIES:
				return memberProperties != null && !memberProperties.isEmpty();
			case ModelPackage.LEVEL__ORDER_ITEM:
				return orderItem != null;
		}
		return super.eIsSet(featureID);
	}

	private DataObjectItem labelItem;
	
	@Override
	public DataObjectItem getLabelItem() {
		return labelItem;
	}

	@Override
	public void setLabelItem(DataObjectItem value) {
		this.labelItem = value;
	}

} //LevelImpl
