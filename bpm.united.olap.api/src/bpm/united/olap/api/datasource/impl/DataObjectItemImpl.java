/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.datasource.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

import bpm.united.olap.api.datasource.DataObject;
import bpm.united.olap.api.datasource.DataObjectItem;
import bpm.united.olap.api.datasource.DatasourcePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Data Object Item</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link bpm.united.olap.api.datasource.impl.DataObjectItemImpl#getName <em>Name</em>}</li>
 *   <li>{@link bpm.united.olap.api.datasource.impl.DataObjectItemImpl#getParent <em>Parent</em>}</li>
 *   <li>{@link bpm.united.olap.api.datasource.impl.DataObjectItemImpl#getId <em>Id</em>}</li>
 *   <li>{@link bpm.united.olap.api.datasource.impl.DataObjectItemImpl#isIsKey <em>Is Key</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DataObjectItemImpl extends EObjectImpl implements DataObjectItem {
	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected String id = ID_EDEFAULT;

	/**
	 * The default value of the '{@link #isIsKey() <em>Is Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIsKey()
	 * @generated
	 * @ordered
	 */
	protected static final boolean IS_KEY_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isIsKey() <em>Is Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIsKey()
	 * @generated
	 * @ordered
	 */
	protected boolean isKey = IS_KEY_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DataObjectItemImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DatasourcePackage.Literals.DATA_OBJECT_ITEM;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DatasourcePackage.DATA_OBJECT_ITEM__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DataObject getParent() {
		if (eContainerFeatureID() != DatasourcePackage.DATA_OBJECT_ITEM__PARENT) return null;
		return (DataObject)eContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetParent(DataObject newParent, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject)newParent, DatasourcePackage.DATA_OBJECT_ITEM__PARENT, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setParent(DataObject newParent) {
		if (newParent != eInternalContainer() || (eContainerFeatureID() != DatasourcePackage.DATA_OBJECT_ITEM__PARENT && newParent != null)) {
			if (EcoreUtil.isAncestor(this, newParent))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newParent != null)
				msgs = ((InternalEObject)newParent).eInverseAdd(this, DatasourcePackage.DATA_OBJECT__ITEMS, DataObject.class, msgs);
			msgs = basicSetParent(newParent, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DatasourcePackage.DATA_OBJECT_ITEM__PARENT, newParent, newParent));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setId(String newId) {
		String oldId = id;
		id = newId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DatasourcePackage.DATA_OBJECT_ITEM__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isIsKey() {
		return isKey;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setIsKey(boolean newIsKey) {
		boolean oldIsKey = isKey;
		isKey = newIsKey;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DatasourcePackage.DATA_OBJECT_ITEM__IS_KEY, oldIsKey, isKey));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case DatasourcePackage.DATA_OBJECT_ITEM__PARENT:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetParent((DataObject)otherEnd, msgs);
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
			case DatasourcePackage.DATA_OBJECT_ITEM__PARENT:
				return basicSetParent(null, msgs);
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
			case DatasourcePackage.DATA_OBJECT_ITEM__PARENT:
				return eInternalContainer().eInverseRemove(this, DatasourcePackage.DATA_OBJECT__ITEMS, DataObject.class, msgs);
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
			case DatasourcePackage.DATA_OBJECT_ITEM__NAME:
				return getName();
			case DatasourcePackage.DATA_OBJECT_ITEM__PARENT:
				return getParent();
			case DatasourcePackage.DATA_OBJECT_ITEM__ID:
				return getId();
			case DatasourcePackage.DATA_OBJECT_ITEM__IS_KEY:
				return isIsKey();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case DatasourcePackage.DATA_OBJECT_ITEM__NAME:
				setName((String)newValue);
				return;
			case DatasourcePackage.DATA_OBJECT_ITEM__PARENT:
				setParent((DataObject)newValue);
				return;
			case DatasourcePackage.DATA_OBJECT_ITEM__ID:
				setId((String)newValue);
				return;
			case DatasourcePackage.DATA_OBJECT_ITEM__IS_KEY:
				setIsKey((Boolean)newValue);
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
			case DatasourcePackage.DATA_OBJECT_ITEM__NAME:
				setName(NAME_EDEFAULT);
				return;
			case DatasourcePackage.DATA_OBJECT_ITEM__PARENT:
				setParent((DataObject)null);
				return;
			case DatasourcePackage.DATA_OBJECT_ITEM__ID:
				setId(ID_EDEFAULT);
				return;
			case DatasourcePackage.DATA_OBJECT_ITEM__IS_KEY:
				setIsKey(IS_KEY_EDEFAULT);
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
			case DatasourcePackage.DATA_OBJECT_ITEM__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case DatasourcePackage.DATA_OBJECT_ITEM__PARENT:
				return getParent() != null;
			case DatasourcePackage.DATA_OBJECT_ITEM__ID:
				return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
			case DatasourcePackage.DATA_OBJECT_ITEM__IS_KEY:
				return isKey != IS_KEY_EDEFAULT;
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
		result.append(" (name: ");
		result.append(name);
		result.append(", id: ");
		result.append(id);
		result.append(", isKey: ");
		result.append(isKey);
		result.append(')');
		return result.toString();
	}

} //DataObjectItemImpl
