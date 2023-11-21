/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.mdm.model.impl;

import bpm.mdm.model.Attribute;
import bpm.mdm.model.DataType;
import bpm.mdm.model.Entity;
import bpm.mdm.model.MdmPackage;
import bpm.mdm.model.Rule;

import java.util.Collection;
import java.util.UUID;

import java.util.List;
import java.util.logging.Logger;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Attribute</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link bpm.mdm.model.impl.AttributeImpl#getRules <em>Rules</em>}</li>
 *   <li>{@link bpm.mdm.model.impl.AttributeImpl#getName <em>Name</em>}</li>
 *   <li>{@link bpm.mdm.model.impl.AttributeImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link bpm.mdm.model.impl.AttributeImpl#getDataType <em>Data Type</em>}</li>
 *   <li>{@link bpm.mdm.model.impl.AttributeImpl#getDefaultValue <em>Default Value</em>}</li>
 *   <li>{@link bpm.mdm.model.impl.AttributeImpl#isId <em>Id</em>}</li>
 *   <li>{@link bpm.mdm.model.impl.AttributeImpl#isNullAllowed <em>Null Allowed</em>}</li>
 *   <li>{@link bpm.mdm.model.impl.AttributeImpl#getUuid <em>Uuid</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class AttributeImpl extends EObjectImpl implements Attribute {
	/**
	 * The cached value of the '{@link #getRules() <em>Rules</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRules()
	 * @generated
	 * @ordered
	 */
	protected EList<Rule> rules;

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
	 * The default value of the '{@link #getDataType() <em>Data Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDataType()
	 * @generated
	 * @ordered
	 */
	protected static final DataType DATA_TYPE_EDEFAULT = DataType.STRING;

	/**
	 * The cached value of the '{@link #getDataType() <em>Data Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDataType()
	 * @generated
	 * @ordered
	 */
	protected DataType dataType = DATA_TYPE_EDEFAULT;

	/**
	 * The default value of the '{@link #getDefaultValue() <em>Default Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDefaultValue()
	 * @generated
	 * @ordered
	 */
	protected static final String DEFAULT_VALUE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDefaultValue() <em>Default Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDefaultValue()
	 * @generated
	 * @ordered
	 */
	protected String defaultValue = DEFAULT_VALUE_EDEFAULT;

	/**
	 * The default value of the '{@link #isId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isId()
	 * @generated
	 * @ordered
	 */
	protected static final boolean ID_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isId()
	 * @generated
	 * @ordered
	 */
	protected boolean id = ID_EDEFAULT;

	/**
	 * The default value of the '{@link #isNullAllowed() <em>Null Allowed</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isNullAllowed()
	 * @generated
	 * @ordered
	 */
	protected static final boolean NULL_ALLOWED_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isNullAllowed() <em>Null Allowed</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isNullAllowed()
	 * @generated
	 * @ordered
	 */
	protected boolean nullAllowed = NULL_ALLOWED_EDEFAULT;

	/**
	 * The default value of the '{@link #getUuid() <em>Uuid</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUuid()
	 * @generated
	 * @ordered
	 */
	protected static final String UUID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getUuid() <em>Uuid</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUuid()
	 * @generated
	 * @ordered
	 */
	protected String uuid = UUID_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AttributeImpl() {
		super();
	}

	 @Override
	protected void eBasicSetContainer(InternalEObject newContainer,
			int newContainerFeatureID) {
		

		 if (getUuid() == null){
			 if (newContainer instanceof Entity){
				 Entity parent = (Entity)newContainer;
				 try{
					 String uuid = null;
					 boolean uuidUnique = true;
					 do{
						 uuid = UUID.randomUUID().toString();
						 for(Attribute a : parent.getAttributes()){
							 if (a.getUuid() != null && a.getUuid().equals(uuid)){
								 uuidUnique = false;
								 break;
							 }
							 else{
								 uuidUnique = true;
							 }
						 }
						 
					 }while(!uuidUnique);
					 setUuid(uuid);
				 }catch(Exception ex){
					 
				 }
			 }
		}
		
		super.eBasicSetContainer(newContainer, newContainerFeatureID);
	}
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MdmPackage.Literals.ATTRIBUTE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<Rule> getRules() {
		if (rules == null) {
			rules = new EObjectContainmentEList<Rule>(Rule.class, this, MdmPackage.ATTRIBUTE__RULES);
		}
		return rules;
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
			eNotify(new ENotificationImpl(this, Notification.SET, MdmPackage.ATTRIBUTE__NAME, oldName, name));
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
			eNotify(new ENotificationImpl(this, Notification.SET, MdmPackage.ATTRIBUTE__DESCRIPTION, oldDescription, description));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DataType getDataType() {
		return dataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDataType(DataType newDataType) {
		DataType oldDataType = dataType;
		dataType = newDataType == null ? DATA_TYPE_EDEFAULT : newDataType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MdmPackage.ATTRIBUTE__DATA_TYPE, oldDataType, dataType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDefaultValue(String newDefaultValue) {
		String oldDefaultValue = defaultValue;
		defaultValue = newDefaultValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MdmPackage.ATTRIBUTE__DEFAULT_VALUE, oldDefaultValue, defaultValue));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setId(boolean newId) {
		boolean oldId = id;
		id = newId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MdmPackage.ATTRIBUTE__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isNullAllowed() {
		return nullAllowed;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setNullAllowed(boolean newNullAllowed) {
		boolean oldNullAllowed = nullAllowed;
		nullAllowed = newNullAllowed;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MdmPackage.ATTRIBUTE__NULL_ALLOWED, oldNullAllowed, nullAllowed));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setUuid(String newUuid) {
		String oldUuid = uuid;
		uuid = newUuid;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MdmPackage.ATTRIBUTE__UUID, oldUuid, uuid));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MdmPackage.ATTRIBUTE__RULES:
				return ((InternalEList<?>)getRules()).basicRemove(otherEnd, msgs);
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
			case MdmPackage.ATTRIBUTE__RULES:
				return getRules();
			case MdmPackage.ATTRIBUTE__NAME:
				return getName();
			case MdmPackage.ATTRIBUTE__DESCRIPTION:
				return getDescription();
			case MdmPackage.ATTRIBUTE__DATA_TYPE:
				return getDataType();
			case MdmPackage.ATTRIBUTE__DEFAULT_VALUE:
				return getDefaultValue();
			case MdmPackage.ATTRIBUTE__ID:
				return isId();
			case MdmPackage.ATTRIBUTE__NULL_ALLOWED:
				return isNullAllowed();
			case MdmPackage.ATTRIBUTE__UUID:
				return getUuid();
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
			case MdmPackage.ATTRIBUTE__RULES:
				getRules().clear();
				getRules().addAll((Collection<? extends Rule>)newValue);
				return;
			case MdmPackage.ATTRIBUTE__NAME:
				setName((String)newValue);
				return;
			case MdmPackage.ATTRIBUTE__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case MdmPackage.ATTRIBUTE__DATA_TYPE:
				setDataType((DataType)newValue);
				return;
			case MdmPackage.ATTRIBUTE__DEFAULT_VALUE:
				setDefaultValue((String)newValue);
				return;
			case MdmPackage.ATTRIBUTE__ID:
				setId((Boolean)newValue);
				return;
			case MdmPackage.ATTRIBUTE__NULL_ALLOWED:
				setNullAllowed((Boolean)newValue);
				return;
			case MdmPackage.ATTRIBUTE__UUID:
				setUuid((String)newValue);
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
			case MdmPackage.ATTRIBUTE__RULES:
				getRules().clear();
				return;
			case MdmPackage.ATTRIBUTE__NAME:
				setName(NAME_EDEFAULT);
				return;
			case MdmPackage.ATTRIBUTE__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case MdmPackage.ATTRIBUTE__DATA_TYPE:
				setDataType(DATA_TYPE_EDEFAULT);
				return;
			case MdmPackage.ATTRIBUTE__DEFAULT_VALUE:
				setDefaultValue(DEFAULT_VALUE_EDEFAULT);
				return;
			case MdmPackage.ATTRIBUTE__ID:
				setId(ID_EDEFAULT);
				return;
			case MdmPackage.ATTRIBUTE__NULL_ALLOWED:
				setNullAllowed(NULL_ALLOWED_EDEFAULT);
				return;
			case MdmPackage.ATTRIBUTE__UUID:
				setUuid(UUID_EDEFAULT);
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
			case MdmPackage.ATTRIBUTE__RULES:
				return rules != null && !rules.isEmpty();
			case MdmPackage.ATTRIBUTE__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case MdmPackage.ATTRIBUTE__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case MdmPackage.ATTRIBUTE__DATA_TYPE:
				return dataType != DATA_TYPE_EDEFAULT;
			case MdmPackage.ATTRIBUTE__DEFAULT_VALUE:
				return DEFAULT_VALUE_EDEFAULT == null ? defaultValue != null : !DEFAULT_VALUE_EDEFAULT.equals(defaultValue);
			case MdmPackage.ATTRIBUTE__ID:
				return id != ID_EDEFAULT;
			case MdmPackage.ATTRIBUTE__NULL_ALLOWED:
				return nullAllowed != NULL_ALLOWED_EDEFAULT;
			case MdmPackage.ATTRIBUTE__UUID:
				return UUID_EDEFAULT == null ? uuid != null : !UUID_EDEFAULT.equals(uuid);
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
		result.append(", dataType: ");
		result.append(dataType);
		result.append(", defaultValue: ");
		result.append(defaultValue);
		result.append(", id: ");
		result.append(id);
		result.append(", nullAllowed: ");
		result.append(nullAllowed);
		result.append(", uuid: ");
		result.append(uuid);
		result.append(')');
		return result.toString();
	}

} //AttributeImpl
