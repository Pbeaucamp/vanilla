/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.datasource.impl;

import bpm.united.olap.api.datasource.DataObject;
import bpm.united.olap.api.datasource.DataObjectItem;
import bpm.united.olap.api.datasource.Datasource;
import bpm.united.olap.api.datasource.DatasourcePackage;

import java.util.Collection;

import java.util.List;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Data Object</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link bpm.united.olap.api.datasource.impl.DataObjectImpl#getName <em>Name</em>}</li>
 *   <li>{@link bpm.united.olap.api.datasource.impl.DataObjectImpl#getItems <em>Items</em>}</li>
 *   <li>{@link bpm.united.olap.api.datasource.impl.DataObjectImpl#getQueryText <em>Query Text</em>}</li>
 *   <li>{@link bpm.united.olap.api.datasource.impl.DataObjectImpl#getParent <em>Parent</em>}</li>
 *   <li>{@link bpm.united.olap.api.datasource.impl.DataObjectImpl#isIsFact <em>Is Fact</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DataObjectImpl extends EObjectImpl implements DataObject {
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
	 * The cached value of the '{@link #getItems() <em>Items</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getItems()
	 * @generated
	 * @ordered
	 */
	protected EList<DataObjectItem> items;

	/**
	 * The default value of the '{@link #getQueryText() <em>Query Text</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getQueryText()
	 * @generated
	 * @ordered
	 */
	protected static final String QUERY_TEXT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getQueryText() <em>Query Text</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getQueryText()
	 * @generated
	 * @ordered
	 */
	protected String queryText = QUERY_TEXT_EDEFAULT;

	/**
	 * The default value of the '{@link #isIsFact() <em>Is Fact</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIsFact()
	 * @generated
	 * @ordered
	 */
	protected static final boolean IS_FACT_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isIsFact() <em>Is Fact</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIsFact()
	 * @generated
	 * @ordered
	 */
	protected boolean isFact = IS_FACT_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DataObjectImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DatasourcePackage.Literals.DATA_OBJECT;
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
			eNotify(new ENotificationImpl(this, Notification.SET, DatasourcePackage.DATA_OBJECT__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<DataObjectItem> getItems() {
		if (items == null) {
			items = new EObjectContainmentWithInverseEList<DataObjectItem>(DataObjectItem.class, this, DatasourcePackage.DATA_OBJECT__ITEMS, DatasourcePackage.DATA_OBJECT_ITEM__PARENT);
		}
		return items;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getQueryText() {
		return queryText;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setQueryText(String newQueryText) {
		String oldQueryText = queryText;
		queryText = newQueryText;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DatasourcePackage.DATA_OBJECT__QUERY_TEXT, oldQueryText, queryText));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Datasource getParent() {
		if (eContainerFeatureID() != DatasourcePackage.DATA_OBJECT__PARENT) return null;
		return (Datasource)eContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetParent(Datasource newParent, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject)newParent, DatasourcePackage.DATA_OBJECT__PARENT, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setParent(Datasource newParent) {
		if (newParent != eInternalContainer() || (eContainerFeatureID() != DatasourcePackage.DATA_OBJECT__PARENT && newParent != null)) {
			if (EcoreUtil.isAncestor(this, newParent))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newParent != null)
				msgs = ((InternalEObject)newParent).eInverseAdd(this, DatasourcePackage.DATASOURCE__DATA_OBJECTS, Datasource.class, msgs);
			msgs = basicSetParent(newParent, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DatasourcePackage.DATA_OBJECT__PARENT, newParent, newParent));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isIsFact() {
		return isFact;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setIsFact(boolean newIsFact) {
		boolean oldIsFact = isFact;
		isFact = newIsFact;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DatasourcePackage.DATA_OBJECT__IS_FACT, oldIsFact, isFact));
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
			case DatasourcePackage.DATA_OBJECT__ITEMS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getItems()).basicAdd(otherEnd, msgs);
			case DatasourcePackage.DATA_OBJECT__PARENT:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetParent((Datasource)otherEnd, msgs);
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
			case DatasourcePackage.DATA_OBJECT__ITEMS:
				return ((InternalEList<?>)getItems()).basicRemove(otherEnd, msgs);
			case DatasourcePackage.DATA_OBJECT__PARENT:
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
			case DatasourcePackage.DATA_OBJECT__PARENT:
				return eInternalContainer().eInverseRemove(this, DatasourcePackage.DATASOURCE__DATA_OBJECTS, Datasource.class, msgs);
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
			case DatasourcePackage.DATA_OBJECT__NAME:
				return getName();
			case DatasourcePackage.DATA_OBJECT__ITEMS:
				return getItems();
			case DatasourcePackage.DATA_OBJECT__QUERY_TEXT:
				return getQueryText();
			case DatasourcePackage.DATA_OBJECT__PARENT:
				return getParent();
			case DatasourcePackage.DATA_OBJECT__IS_FACT:
				return isIsFact();
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
			case DatasourcePackage.DATA_OBJECT__NAME:
				setName((String)newValue);
				return;
			case DatasourcePackage.DATA_OBJECT__ITEMS:
				getItems().clear();
				getItems().addAll((Collection<? extends DataObjectItem>)newValue);
				return;
			case DatasourcePackage.DATA_OBJECT__QUERY_TEXT:
				setQueryText((String)newValue);
				return;
			case DatasourcePackage.DATA_OBJECT__PARENT:
				setParent((Datasource)newValue);
				return;
			case DatasourcePackage.DATA_OBJECT__IS_FACT:
				setIsFact((Boolean)newValue);
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
			case DatasourcePackage.DATA_OBJECT__NAME:
				setName(NAME_EDEFAULT);
				return;
			case DatasourcePackage.DATA_OBJECT__ITEMS:
				getItems().clear();
				return;
			case DatasourcePackage.DATA_OBJECT__QUERY_TEXT:
				setQueryText(QUERY_TEXT_EDEFAULT);
				return;
			case DatasourcePackage.DATA_OBJECT__PARENT:
				setParent((Datasource)null);
				return;
			case DatasourcePackage.DATA_OBJECT__IS_FACT:
				setIsFact(IS_FACT_EDEFAULT);
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
			case DatasourcePackage.DATA_OBJECT__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case DatasourcePackage.DATA_OBJECT__ITEMS:
				return items != null && !items.isEmpty();
			case DatasourcePackage.DATA_OBJECT__QUERY_TEXT:
				return QUERY_TEXT_EDEFAULT == null ? queryText != null : !QUERY_TEXT_EDEFAULT.equals(queryText);
			case DatasourcePackage.DATA_OBJECT__PARENT:
				return getParent() != null;
			case DatasourcePackage.DATA_OBJECT__IS_FACT:
				return isFact != IS_FACT_EDEFAULT;
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
		result.append(", queryText: ");
		result.append(queryText);
		result.append(", isFact: ");
		result.append(isFact);
		result.append(')');
		return result.toString();
	}
	
	private String id;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

} //DataObjectImpl
