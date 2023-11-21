/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.mdm.model.rules.impl;

import bpm.mdm.model.rules.RulesPackage;
import bpm.mdm.model.rules.SetValueRule;

import java.util.Collection;

import java.util.List;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Set Value Rule</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link bpm.mdm.model.rules.impl.SetValueRuleImpl#getName <em>Name</em>}</li>
 *   <li>{@link bpm.mdm.model.rules.impl.SetValueRuleImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link bpm.mdm.model.rules.impl.SetValueRuleImpl#isActive <em>Active</em>}</li>
 *   <li>{@link bpm.mdm.model.rules.impl.SetValueRuleImpl#getAuthorizedValues <em>Authorized Values</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SetValueRuleImpl extends EObjectImpl implements SetValueRule {
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
	 * The cached value of the '{@link #getAuthorizedValues() <em>Authorized Values</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAuthorizedValues()
	 * @generated
	 * @ordered
	 */
	protected EList<Object> authorizedValues;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SetValueRuleImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return RulesPackage.Literals.SET_VALUE_RULE;
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
			eNotify(new ENotificationImpl(this, Notification.SET, RulesPackage.SET_VALUE_RULE__NAME, oldName, name));
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
			eNotify(new ENotificationImpl(this, Notification.SET, RulesPackage.SET_VALUE_RULE__DESCRIPTION, oldDescription, description));
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
			eNotify(new ENotificationImpl(this, Notification.SET, RulesPackage.SET_VALUE_RULE__ACTIVE, oldActive, active));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<Object> getAuthorizedValues() {
		if (authorizedValues == null) {
			authorizedValues = new EDataTypeUniqueEList<Object>(Object.class, this, RulesPackage.SET_VALUE_RULE__AUTHORIZED_VALUES);
		}
		return authorizedValues;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public boolean evaluate(Object value) {
		for(Object o : getAuthorizedValues()){
			if (o != null ){
				if (o.equals(value)){
					return true;
				}
			}
			else{
				if (value == null){
					return true;
				}
			}
		}
		
		return false;
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
			case RulesPackage.SET_VALUE_RULE__NAME:
				return getName();
			case RulesPackage.SET_VALUE_RULE__DESCRIPTION:
				return getDescription();
			case RulesPackage.SET_VALUE_RULE__ACTIVE:
				return isActive();
			case RulesPackage.SET_VALUE_RULE__AUTHORIZED_VALUES:
				return getAuthorizedValues();
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
			case RulesPackage.SET_VALUE_RULE__NAME:
				setName((String)newValue);
				return;
			case RulesPackage.SET_VALUE_RULE__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case RulesPackage.SET_VALUE_RULE__ACTIVE:
				setActive((Boolean)newValue);
				return;
			case RulesPackage.SET_VALUE_RULE__AUTHORIZED_VALUES:
				getAuthorizedValues().clear();
				getAuthorizedValues().addAll((Collection<? extends Object>)newValue);
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
			case RulesPackage.SET_VALUE_RULE__NAME:
				setName(NAME_EDEFAULT);
				return;
			case RulesPackage.SET_VALUE_RULE__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case RulesPackage.SET_VALUE_RULE__ACTIVE:
				setActive(ACTIVE_EDEFAULT);
				return;
			case RulesPackage.SET_VALUE_RULE__AUTHORIZED_VALUES:
				getAuthorizedValues().clear();
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
			case RulesPackage.SET_VALUE_RULE__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case RulesPackage.SET_VALUE_RULE__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case RulesPackage.SET_VALUE_RULE__ACTIVE:
				return active != ACTIVE_EDEFAULT;
			case RulesPackage.SET_VALUE_RULE__AUTHORIZED_VALUES:
				return authorizedValues != null && !authorizedValues.isEmpty();
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
		result.append(", authorizedValues: ");
		result.append(authorizedValues);
		result.append(')');
		return result.toString();
	}

} //SetValueRuleImpl
