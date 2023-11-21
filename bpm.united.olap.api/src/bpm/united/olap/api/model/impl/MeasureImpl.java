/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.model.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

import bpm.united.olap.api.datasource.DataObjectItem;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.model.ModelPackage;
import bpm.united.olap.api.model.Schema;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Measure</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link bpm.united.olap.api.model.impl.MeasureImpl#getItem <em>Item</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.impl.MeasureImpl#getCalculationType <em>Calculation Type</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.impl.MeasureImpl#getParentSchema <em>Parent Schema</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.impl.MeasureImpl#getCalculatdFormula <em>Calculatd Formula</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MeasureImpl extends ElementDefinitionImpl implements Measure {
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
	 * The default value of the '{@link #getCalculationType() <em>Calculation Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCalculationType()
	 * @generated
	 * @ordered
	 */
	protected static final String CALCULATION_TYPE_EDEFAULT = null;
	/**
	 * The cached value of the '{@link #getCalculationType() <em>Calculation Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCalculationType()
	 * @generated
	 * @ordered
	 */
	protected String calculationType = CALCULATION_TYPE_EDEFAULT;

	/**
	 * The default value of the '{@link #getCalculatdFormula() <em>Calculatd Formula</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCalculatdFormula()
	 * @generated
	 * @ordered
	 */
	protected static final String CALCULATD_FORMULA_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getCalculatdFormula() <em>Calculatd Formula</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCalculatdFormula()
	 * @generated
	 * @ordered
	 */
	protected String calculatdFormula = CALCULATD_FORMULA_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MeasureImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ModelPackage.Literals.MEASURE;
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
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ModelPackage.MEASURE__ITEM, oldItem, item));
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
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.MEASURE__ITEM, oldItem, item));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getCalculationType() {
		return calculationType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCalculationType(String newCalculationType) {
		String oldCalculationType = calculationType;
		calculationType = newCalculationType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.MEASURE__CALCULATION_TYPE, oldCalculationType, calculationType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Schema getParentSchema() {
		if (eContainerFeatureID() != ModelPackage.MEASURE__PARENT_SCHEMA) return null;
		return (Schema)eContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetParentSchema(Schema newParentSchema, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject)newParentSchema, ModelPackage.MEASURE__PARENT_SCHEMA, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setParentSchema(Schema newParentSchema) {
		if (newParentSchema != eInternalContainer() || (eContainerFeatureID() != ModelPackage.MEASURE__PARENT_SCHEMA && newParentSchema != null)) {
			if (EcoreUtil.isAncestor(this, newParentSchema))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newParentSchema != null)
				msgs = ((InternalEObject)newParentSchema).eInverseAdd(this, ModelPackage.SCHEMA__MEASURES, Schema.class, msgs);
			msgs = basicSetParentSchema(newParentSchema, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.MEASURE__PARENT_SCHEMA, newParentSchema, newParentSchema));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getCalculatdFormula() {
		return calculatdFormula;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCalculatdFormula(String newCalculatdFormula) {
		String oldCalculatdFormula = calculatdFormula;
		calculatdFormula = newCalculatdFormula;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.MEASURE__CALCULATD_FORMULA, oldCalculatdFormula, calculatdFormula));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ModelPackage.MEASURE__PARENT_SCHEMA:
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
			case ModelPackage.MEASURE__PARENT_SCHEMA:
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
			case ModelPackage.MEASURE__PARENT_SCHEMA:
				return eInternalContainer().eInverseRemove(this, ModelPackage.SCHEMA__MEASURES, Schema.class, msgs);
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
			case ModelPackage.MEASURE__ITEM:
				if (resolve) return getItem();
				return basicGetItem();
			case ModelPackage.MEASURE__CALCULATION_TYPE:
				return getCalculationType();
			case ModelPackage.MEASURE__PARENT_SCHEMA:
				return getParentSchema();
			case ModelPackage.MEASURE__CALCULATD_FORMULA:
				return getCalculatdFormula();
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
			case ModelPackage.MEASURE__ITEM:
				setItem((DataObjectItem)newValue);
				return;
			case ModelPackage.MEASURE__CALCULATION_TYPE:
				setCalculationType((String)newValue);
				return;
			case ModelPackage.MEASURE__PARENT_SCHEMA:
				setParentSchema((Schema)newValue);
				return;
			case ModelPackage.MEASURE__CALCULATD_FORMULA:
				setCalculatdFormula((String)newValue);
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
			case ModelPackage.MEASURE__ITEM:
				setItem((DataObjectItem)null);
				return;
			case ModelPackage.MEASURE__CALCULATION_TYPE:
				setCalculationType(CALCULATION_TYPE_EDEFAULT);
				return;
			case ModelPackage.MEASURE__PARENT_SCHEMA:
				setParentSchema((Schema)null);
				return;
			case ModelPackage.MEASURE__CALCULATD_FORMULA:
				setCalculatdFormula(CALCULATD_FORMULA_EDEFAULT);
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
			case ModelPackage.MEASURE__ITEM:
				return item != null;
			case ModelPackage.MEASURE__CALCULATION_TYPE:
				return CALCULATION_TYPE_EDEFAULT == null ? calculationType != null : !CALCULATION_TYPE_EDEFAULT.equals(calculationType);
			case ModelPackage.MEASURE__PARENT_SCHEMA:
				return getParentSchema() != null;
			case ModelPackage.MEASURE__CALCULATD_FORMULA:
				return CALCULATD_FORMULA_EDEFAULT == null ? calculatdFormula != null : !CALCULATD_FORMULA_EDEFAULT.equals(calculatdFormula);
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
		result.append(" (calculationType: ");
		result.append(calculationType);
		result.append(", calculatdFormula: ");
		result.append(calculatdFormula);
		result.append(')');
		return result.toString();
	}

	private String lastDimensionName;
	
	@Override
	public String getLastDimensionName() {
		return lastDimensionName;
	}

	@Override
	public void setLastDimensionName(String dimensionName) {
		this.lastDimensionName = dimensionName;
	}

} //MeasureImpl
