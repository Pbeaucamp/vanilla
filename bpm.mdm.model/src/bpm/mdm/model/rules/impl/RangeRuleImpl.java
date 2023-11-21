/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.mdm.model.rules.impl;

import bpm.mdm.model.rules.RangeRule;
import bpm.mdm.model.rules.RulesPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Range Rule</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link bpm.mdm.model.rules.impl.RangeRuleImpl#getName <em>Name</em>}</li>
 *   <li>{@link bpm.mdm.model.rules.impl.RangeRuleImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link bpm.mdm.model.rules.impl.RangeRuleImpl#isActive <em>Active</em>}</li>
 *   <li>{@link bpm.mdm.model.rules.impl.RangeRuleImpl#getMinValue <em>Min Value</em>}</li>
 *   <li>{@link bpm.mdm.model.rules.impl.RangeRuleImpl#getMaxValue <em>Max Value</em>}</li>
 *   <li>{@link bpm.mdm.model.rules.impl.RangeRuleImpl#isIncludeMin <em>Include Min</em>}</li>
 *   <li>{@link bpm.mdm.model.rules.impl.RangeRuleImpl#isIncludeMax <em>Include Max</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RangeRuleImpl extends EObjectImpl implements RangeRule {
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
	 * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected static final String DESCRIPTION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected String description = DESCRIPTION_EDEFAULT;

	/**
	 * The default value of the '{@link #isActive() <em>Active</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isActive()
	 * @generated
	 * @ordered
	 */
	protected static final boolean ACTIVE_EDEFAULT = true;

	/**
	 * The cached value of the '{@link #isActive() <em>Active</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isActive()
	 * @generated
	 * @ordered
	 */
	protected boolean active = ACTIVE_EDEFAULT;

	/**
	 * The default value of the '{@link #getMinValue() <em>Min Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMinValue()
	 * @generated
	 * @ordered
	 */
	protected static final long MIN_VALUE_EDEFAULT = 0L;

	/**
	 * The cached value of the '{@link #getMinValue() <em>Min Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMinValue()
	 * @generated
	 * @ordered
	 */
	protected long minValue = MIN_VALUE_EDEFAULT;

	/**
	 * The default value of the '{@link #getMaxValue() <em>Max Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMaxValue()
	 * @generated
	 * @ordered
	 */
	protected static final long MAX_VALUE_EDEFAULT = 0L;

	/**
	 * The cached value of the '{@link #getMaxValue() <em>Max Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMaxValue()
	 * @generated
	 * @ordered
	 */
	protected long maxValue = MAX_VALUE_EDEFAULT;

	/**
	 * The default value of the '{@link #isIncludeMin() <em>Include Min</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIncludeMin()
	 * @generated
	 * @ordered
	 */
	protected static final boolean INCLUDE_MIN_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isIncludeMin() <em>Include Min</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIncludeMin()
	 * @generated
	 * @ordered
	 */
	protected boolean includeMin = INCLUDE_MIN_EDEFAULT;

	/**
	 * The default value of the '{@link #isIncludeMax() <em>Include Max</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIncludeMax()
	 * @generated
	 * @ordered
	 */
	protected static final boolean INCLUDE_MAX_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isIncludeMax() <em>Include Max</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIncludeMax()
	 * @generated
	 * @ordered
	 */
	protected boolean includeMax = INCLUDE_MAX_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected RangeRuleImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return RulesPackage.Literals.RANGE_RULE;
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
			eNotify(new ENotificationImpl(this, Notification.SET, RulesPackage.RANGE_RULE__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDescription(String newDescription) {
		String oldDescription = description;
		description = newDescription;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, RulesPackage.RANGE_RULE__DESCRIPTION, oldDescription, description));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setActive(boolean newActive) {
		boolean oldActive = active;
		active = newActive;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, RulesPackage.RANGE_RULE__ACTIVE, oldActive, active));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public long getMinValue() {
		return minValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMinValue(long newMinValue) {
		long oldMinValue = minValue;
		minValue = newMinValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, RulesPackage.RANGE_RULE__MIN_VALUE, oldMinValue, minValue));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public long getMaxValue() {
		return maxValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMaxValue(long newMaxValue) {
		long oldMaxValue = maxValue;
		maxValue = newMaxValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, RulesPackage.RANGE_RULE__MAX_VALUE, oldMaxValue, maxValue));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isIncludeMin() {
		return includeMin;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setIncludeMin(boolean newIncludeMin) {
		boolean oldIncludeMin = includeMin;
		includeMin = newIncludeMin;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, RulesPackage.RANGE_RULE__INCLUDE_MIN, oldIncludeMin, includeMin));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isIncludeMax() {
		return includeMax;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setIncludeMax(boolean newIncludeMax) {
		boolean oldIncludeMax = includeMax;
		includeMax = newIncludeMax;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, RulesPackage.RANGE_RULE__INCLUDE_MAX, oldIncludeMax, includeMax));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public boolean evaluate(Object val) {
		
		Double value = null;
		
		
		
		if (val == null){
			return false;
		}
		
		try{
			if (val instanceof String){
				value = Double.valueOf((String)val);
			}
			else{
				value = Double.valueOf(val.toString());
			}
		}catch(Exception ex){
			if (value== null){
				return false;
			}
		}
		
		
		
		if (includeMax){
			if (value > getMaxValue()){
				return false;
			}
		}
		else{
			if (value >= getMaxValue()){
				return false;
			}
		}
		
		if (includeMin){
			if (value < getMinValue()){
				return false;
			}
		}
		else{
			if (value <= getMinValue()){
				return false;
			}
		}
		
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public String getRuleTypeName() {
		return getClass().getSimpleName();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case RulesPackage.RANGE_RULE__NAME:
				return getName();
			case RulesPackage.RANGE_RULE__DESCRIPTION:
				return getDescription();
			case RulesPackage.RANGE_RULE__ACTIVE:
				return isActive();
			case RulesPackage.RANGE_RULE__MIN_VALUE:
				return getMinValue();
			case RulesPackage.RANGE_RULE__MAX_VALUE:
				return getMaxValue();
			case RulesPackage.RANGE_RULE__INCLUDE_MIN:
				return isIncludeMin();
			case RulesPackage.RANGE_RULE__INCLUDE_MAX:
				return isIncludeMax();
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
			case RulesPackage.RANGE_RULE__NAME:
				setName((String)newValue);
				return;
			case RulesPackage.RANGE_RULE__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case RulesPackage.RANGE_RULE__ACTIVE:
				setActive((Boolean)newValue);
				return;
			case RulesPackage.RANGE_RULE__MIN_VALUE:
				setMinValue((Long)newValue);
				return;
			case RulesPackage.RANGE_RULE__MAX_VALUE:
				setMaxValue((Long)newValue);
				return;
			case RulesPackage.RANGE_RULE__INCLUDE_MIN:
				setIncludeMin((Boolean)newValue);
				return;
			case RulesPackage.RANGE_RULE__INCLUDE_MAX:
				setIncludeMax((Boolean)newValue);
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
			case RulesPackage.RANGE_RULE__NAME:
				setName(NAME_EDEFAULT);
				return;
			case RulesPackage.RANGE_RULE__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case RulesPackage.RANGE_RULE__ACTIVE:
				setActive(ACTIVE_EDEFAULT);
				return;
			case RulesPackage.RANGE_RULE__MIN_VALUE:
				setMinValue(MIN_VALUE_EDEFAULT);
				return;
			case RulesPackage.RANGE_RULE__MAX_VALUE:
				setMaxValue(MAX_VALUE_EDEFAULT);
				return;
			case RulesPackage.RANGE_RULE__INCLUDE_MIN:
				setIncludeMin(INCLUDE_MIN_EDEFAULT);
				return;
			case RulesPackage.RANGE_RULE__INCLUDE_MAX:
				setIncludeMax(INCLUDE_MAX_EDEFAULT);
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
			case RulesPackage.RANGE_RULE__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case RulesPackage.RANGE_RULE__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case RulesPackage.RANGE_RULE__ACTIVE:
				return active != ACTIVE_EDEFAULT;
			case RulesPackage.RANGE_RULE__MIN_VALUE:
				return minValue != MIN_VALUE_EDEFAULT;
			case RulesPackage.RANGE_RULE__MAX_VALUE:
				return maxValue != MAX_VALUE_EDEFAULT;
			case RulesPackage.RANGE_RULE__INCLUDE_MIN:
				return includeMin != INCLUDE_MIN_EDEFAULT;
			case RulesPackage.RANGE_RULE__INCLUDE_MAX:
				return includeMax != INCLUDE_MAX_EDEFAULT;
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
		result.append(", description: ");
		result.append(description);
		result.append(", active: ");
		result.append(active);
		result.append(", minValue: ");
		result.append(minValue);
		result.append(", maxValue: ");
		result.append(maxValue);
		result.append(", includeMin: ");
		result.append(includeMin);
		result.append(", includeMax: ");
		result.append(includeMax);
		result.append(')');
		return result.toString();
	}

} //RangeRuleImpl
