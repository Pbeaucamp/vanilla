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
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import bpm.united.olap.api.datasource.DataObject;
import bpm.united.olap.api.model.Dimension;
import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.ModelPackage;
import bpm.united.olap.api.model.Schema;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Dimension</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link bpm.united.olap.api.model.impl.DimensionImpl#getDataObject <em>Data Object</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.impl.DimensionImpl#getHierarchies <em>Hierarchies</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.impl.DimensionImpl#getParentSchema <em>Parent Schema</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.impl.DimensionImpl#isIsOneColumnDate <em>Is One Column Date</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DimensionImpl extends ElementDefinitionImpl implements Dimension {
	/**
	 * The cached value of the '{@link #getDataObject() <em>Data Object</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDataObject()
	 * @generated
	 * @ordered
	 */
	protected DataObject dataObject;

	/**
	 * The cached value of the '{@link #getHierarchies() <em>Hierarchies</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHierarchies()
	 * @generated
	 * @ordered
	 */
	protected EList<Hierarchy> hierarchies;

	/**
	 * The default value of the '{@link #isIsOneColumnDate() <em>Is One Column Date</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIsOneColumnDate()
	 * @generated
	 * @ordered
	 */
	protected static final boolean IS_ONE_COLUMN_DATE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isIsOneColumnDate() <em>Is One Column Date</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIsOneColumnDate()
	 * @generated
	 * @ordered
	 */
	protected boolean isOneColumnDate = IS_ONE_COLUMN_DATE_EDEFAULT;
	protected boolean isGeolocalizable = false;
	
	
	
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DimensionImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ModelPackage.Literals.DIMENSION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DataObject getDataObject() {
		if (dataObject != null && dataObject.eIsProxy()) {
			InternalEObject oldDataObject = (InternalEObject)dataObject;
			dataObject = (DataObject)eResolveProxy(oldDataObject);
			if (dataObject != oldDataObject) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ModelPackage.DIMENSION__DATA_OBJECT, oldDataObject, dataObject));
			}
		}
		return dataObject;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DataObject basicGetDataObject() {
		return dataObject;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDataObject(DataObject newDataObject) {
		DataObject oldDataObject = dataObject;
		dataObject = newDataObject;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.DIMENSION__DATA_OBJECT, oldDataObject, dataObject));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<Hierarchy> getHierarchies() {
		if (hierarchies == null) {
			hierarchies = new EObjectContainmentWithInverseEList<Hierarchy>(Hierarchy.class, this, ModelPackage.DIMENSION__HIERARCHIES, ModelPackage.HIERARCHY__PARENT_DIMENSION);
		}
		return hierarchies;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Schema getParentSchema() {
		if (eContainerFeatureID() != ModelPackage.DIMENSION__PARENT_SCHEMA) return null;
		return (Schema)eContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetParentSchema(Schema newParentSchema, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject)newParentSchema, ModelPackage.DIMENSION__PARENT_SCHEMA, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setParentSchema(Schema newParentSchema) {
		if (newParentSchema != eInternalContainer() || (eContainerFeatureID() != ModelPackage.DIMENSION__PARENT_SCHEMA && newParentSchema != null)) {
			if (EcoreUtil.isAncestor(this, newParentSchema))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newParentSchema != null)
				msgs = ((InternalEObject)newParentSchema).eInverseAdd(this, ModelPackage.SCHEMA__DIMENSIONS, Schema.class, msgs);
			msgs = basicSetParentSchema(newParentSchema, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.DIMENSION__PARENT_SCHEMA, newParentSchema, newParentSchema));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isIsOneColumnDate() {
		return isOneColumnDate;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setIsOneColumnDate(boolean newIsOneColumnDate) {
		boolean oldIsOneColumnDate = isOneColumnDate;
		isOneColumnDate = newIsOneColumnDate;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.DIMENSION__IS_ONE_COLUMN_DATE, oldIsOneColumnDate, isOneColumnDate));
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
			case ModelPackage.DIMENSION__HIERARCHIES:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getHierarchies()).basicAdd(otherEnd, msgs);
			case ModelPackage.DIMENSION__PARENT_SCHEMA:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetParentSchema((Schema)otherEnd, msgs);
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
			case ModelPackage.DIMENSION__HIERARCHIES:
				return ((InternalEList<?>)getHierarchies()).basicRemove(otherEnd, msgs);
			case ModelPackage.DIMENSION__PARENT_SCHEMA:
				return basicSetParentSchema(null, msgs);
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
			case ModelPackage.DIMENSION__PARENT_SCHEMA:
				return eInternalContainer().eInverseRemove(this, ModelPackage.SCHEMA__DIMENSIONS, Schema.class, msgs);
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
			case ModelPackage.DIMENSION__DATA_OBJECT:
				if (resolve) return getDataObject();
				return basicGetDataObject();
			case ModelPackage.DIMENSION__HIERARCHIES:
				return getHierarchies();
			case ModelPackage.DIMENSION__PARENT_SCHEMA:
				return getParentSchema();
			case ModelPackage.DIMENSION__IS_ONE_COLUMN_DATE:
				return isIsOneColumnDate();
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
			case ModelPackage.DIMENSION__DATA_OBJECT:
				setDataObject((DataObject)newValue);
				return;
			case ModelPackage.DIMENSION__HIERARCHIES:
				getHierarchies().clear();
				getHierarchies().addAll((Collection<? extends Hierarchy>)newValue);
				return;
			case ModelPackage.DIMENSION__PARENT_SCHEMA:
				setParentSchema((Schema)newValue);
				return;
			case ModelPackage.DIMENSION__IS_ONE_COLUMN_DATE:
				setIsOneColumnDate((Boolean)newValue);
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
			case ModelPackage.DIMENSION__DATA_OBJECT:
				setDataObject((DataObject)null);
				return;
			case ModelPackage.DIMENSION__HIERARCHIES:
				getHierarchies().clear();
				return;
			case ModelPackage.DIMENSION__PARENT_SCHEMA:
				setParentSchema((Schema)null);
				return;
			case ModelPackage.DIMENSION__IS_ONE_COLUMN_DATE:
				setIsOneColumnDate(IS_ONE_COLUMN_DATE_EDEFAULT);
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
			case ModelPackage.DIMENSION__DATA_OBJECT:
				return dataObject != null;
			case ModelPackage.DIMENSION__HIERARCHIES:
				return hierarchies != null && !hierarchies.isEmpty();
			case ModelPackage.DIMENSION__PARENT_SCHEMA:
				return getParentSchema() != null;
			case ModelPackage.DIMENSION__IS_ONE_COLUMN_DATE:
				return isOneColumnDate != IS_ONE_COLUMN_DATE_EDEFAULT;
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
		result.append(" (isOneColumnDate: ");
		result.append(isOneColumnDate);
		result.append(')');
		return result.toString();
	}

	private boolean isDate;
	
	@Override
	public boolean isDate() {
		return isDate;
	}

	@Override
	public void setIsDate(boolean isDate) {
		this.isDate = isDate;
	}

	@Override
	public boolean isGeolocalizable() {
		return isGeolocalizable;
	}

	@Override
	public void setIsGeolocalizable(boolean isGeolocalizable) {
		this.isGeolocalizable = isGeolocalizable;		
	}

} //DimensionImpl
