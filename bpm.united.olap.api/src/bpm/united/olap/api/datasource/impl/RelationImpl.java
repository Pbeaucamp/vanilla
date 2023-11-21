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

import bpm.united.olap.api.datasource.DataObjectItem;
import bpm.united.olap.api.datasource.DatasourcePackage;
import bpm.united.olap.api.datasource.Operator;
import bpm.united.olap.api.datasource.Relation;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Relation</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link bpm.united.olap.api.datasource.impl.RelationImpl#getOperator <em>Operator</em>}</li>
 *   <li>{@link bpm.united.olap.api.datasource.impl.RelationImpl#getLeftItem <em>Left Item</em>}</li>
 *   <li>{@link bpm.united.olap.api.datasource.impl.RelationImpl#getRightItem <em>Right Item</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RelationImpl extends EObjectImpl implements Relation {
	/**
	 * The cached value of the '{@link #getOperator() <em>Operator</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOperator()
	 * @generated
	 * @ordered
	 */
	protected Operator operator;

	/**
	 * The cached value of the '{@link #getLeftItem() <em>Left Item</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLeftItem()
	 * @generated
	 * @ordered
	 */
	protected DataObjectItem leftItem;

	/**
	 * The cached value of the '{@link #getRightItem() <em>Right Item</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRightItem()
	 * @generated
	 * @ordered
	 */
	protected DataObjectItem rightItem;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected RelationImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DatasourcePackage.Literals.RELATION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Operator getOperator() {
		return operator;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetOperator(Operator newOperator, NotificationChain msgs) {
		Operator oldOperator = operator;
		operator = newOperator;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, DatasourcePackage.RELATION__OPERATOR, oldOperator, newOperator);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setOperator(Operator newOperator) {
		if (newOperator != operator) {
			NotificationChain msgs = null;
			if (operator != null)
				msgs = ((InternalEObject)operator).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - DatasourcePackage.RELATION__OPERATOR, null, msgs);
			if (newOperator != null)
				msgs = ((InternalEObject)newOperator).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - DatasourcePackage.RELATION__OPERATOR, null, msgs);
			msgs = basicSetOperator(newOperator, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DatasourcePackage.RELATION__OPERATOR, newOperator, newOperator));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DataObjectItem getLeftItem() {
		if (leftItem != null && leftItem.eIsProxy()) {
			InternalEObject oldLeftItem = (InternalEObject)leftItem;
			leftItem = (DataObjectItem)eResolveProxy(oldLeftItem);
			if (leftItem != oldLeftItem) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, DatasourcePackage.RELATION__LEFT_ITEM, oldLeftItem, leftItem));
			}
		}
		return leftItem;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DataObjectItem basicGetLeftItem() {
		return leftItem;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLeftItem(DataObjectItem newLeftItem) {
		DataObjectItem oldLeftItem = leftItem;
		leftItem = newLeftItem;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DatasourcePackage.RELATION__LEFT_ITEM, oldLeftItem, leftItem));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DataObjectItem getRightItem() {
		if (rightItem != null && rightItem.eIsProxy()) {
			InternalEObject oldRightItem = (InternalEObject)rightItem;
			rightItem = (DataObjectItem)eResolveProxy(oldRightItem);
			if (rightItem != oldRightItem) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, DatasourcePackage.RELATION__RIGHT_ITEM, oldRightItem, rightItem));
			}
		}
		return rightItem;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DataObjectItem basicGetRightItem() {
		return rightItem;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRightItem(DataObjectItem newRightItem) {
		DataObjectItem oldRightItem = rightItem;
		rightItem = newRightItem;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DatasourcePackage.RELATION__RIGHT_ITEM, oldRightItem, rightItem));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case DatasourcePackage.RELATION__OPERATOR:
				return basicSetOperator(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case DatasourcePackage.RELATION__OPERATOR:
				return getOperator();
			case DatasourcePackage.RELATION__LEFT_ITEM:
				if (resolve) return getLeftItem();
				return basicGetLeftItem();
			case DatasourcePackage.RELATION__RIGHT_ITEM:
				if (resolve) return getRightItem();
				return basicGetRightItem();
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
			case DatasourcePackage.RELATION__OPERATOR:
				setOperator((Operator)newValue);
				return;
			case DatasourcePackage.RELATION__LEFT_ITEM:
				setLeftItem((DataObjectItem)newValue);
				return;
			case DatasourcePackage.RELATION__RIGHT_ITEM:
				setRightItem((DataObjectItem)newValue);
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
			case DatasourcePackage.RELATION__OPERATOR:
				setOperator((Operator)null);
				return;
			case DatasourcePackage.RELATION__LEFT_ITEM:
				setLeftItem((DataObjectItem)null);
				return;
			case DatasourcePackage.RELATION__RIGHT_ITEM:
				setRightItem((DataObjectItem)null);
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
			case DatasourcePackage.RELATION__OPERATOR:
				return operator != null;
			case DatasourcePackage.RELATION__LEFT_ITEM:
				return leftItem != null;
			case DatasourcePackage.RELATION__RIGHT_ITEM:
				return rightItem != null;
		}
		return super.eIsSet(featureID);
	}

} //RelationImpl
