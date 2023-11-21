/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.mdm.model.rules.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import bpm.mdm.model.Attribute;
import bpm.mdm.model.api.IEntityReadable;
import bpm.mdm.model.api.IEntityReader;
import bpm.mdm.model.rules.EntityLinkRule;
import bpm.mdm.model.rules.RulesPackage;
import bpm.mdm.model.runtime.Row;
import bpm.mdm.model.storage.IEntityStorage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Entity Link Rule</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link bpm.mdm.model.rules.impl.EntityLinkRuleImpl#getName <em>Name</em>}</li>
 *   <li>{@link bpm.mdm.model.rules.impl.EntityLinkRuleImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link bpm.mdm.model.rules.impl.EntityLinkRuleImpl#isActive <em>Active</em>}</li>
 *   <li>{@link bpm.mdm.model.rules.impl.EntityLinkRuleImpl#getLinkedAttribute <em>Linked Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EntityLinkRuleImpl extends EObjectImpl implements EntityLinkRule, IEntityReadable {
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
	 * The cached value of the '{@link #getLinkedAttribute() <em>Linked Attribute</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLinkedAttribute()
	 * @generated
	 * @ordered
	 */
	protected Attribute linkedAttribute;

	
	private transient IEntityReader entityReader;
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EntityLinkRuleImpl() {
		super();
	}

	
	public void setEntityStore(IEntityReader entityReader){
		this.entityReader = entityReader;
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return RulesPackage.Literals.ENTITY_LINK_RULE;
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
			eNotify(new ENotificationImpl(this, Notification.SET, RulesPackage.ENTITY_LINK_RULE__NAME, oldName, name));
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
			eNotify(new ENotificationImpl(this, Notification.SET, RulesPackage.ENTITY_LINK_RULE__DESCRIPTION, oldDescription, description));
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
			eNotify(new ENotificationImpl(this, Notification.SET, RulesPackage.ENTITY_LINK_RULE__ACTIVE, oldActive, active));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Attribute getLinkedAttribute() {
		if (linkedAttribute != null && linkedAttribute.eIsProxy()) {
			InternalEObject oldLinkedAttribute = (InternalEObject)linkedAttribute;
			linkedAttribute = (Attribute)eResolveProxy(oldLinkedAttribute);
			if (linkedAttribute != oldLinkedAttribute) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, RulesPackage.ENTITY_LINK_RULE__LINKED_ATTRIBUTE, oldLinkedAttribute, linkedAttribute));
			}
		}
		return linkedAttribute;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Attribute basicGetLinkedAttribute() {
		return linkedAttribute;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLinkedAttribute(Attribute newLinkedAttribute) {
		Attribute oldLinkedAttribute = linkedAttribute;
		linkedAttribute = newLinkedAttribute;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, RulesPackage.ENTITY_LINK_RULE__LINKED_ATTRIBUTE, oldLinkedAttribute, linkedAttribute));
	}


	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public boolean evaluate(Object value) {
		try{
			
			entityReader.open();
			while(entityReader.hasNext()){
				Row row = entityReader.next();
				Object _value = row.getValue(getLinkedAttribute());
				if (_value != null && _value.equals(value)){
					return true;
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if (entityReader != null){
				try {
					entityReader.close();
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			}
		}
		
		
		return false;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case RulesPackage.ENTITY_LINK_RULE__NAME:
				return getName();
			case RulesPackage.ENTITY_LINK_RULE__DESCRIPTION:
				return getDescription();
			case RulesPackage.ENTITY_LINK_RULE__ACTIVE:
				return isActive();
			case RulesPackage.ENTITY_LINK_RULE__LINKED_ATTRIBUTE:
				if (resolve) return getLinkedAttribute();
				return basicGetLinkedAttribute();
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
			case RulesPackage.ENTITY_LINK_RULE__NAME:
				setName((String)newValue);
				return;
			case RulesPackage.ENTITY_LINK_RULE__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case RulesPackage.ENTITY_LINK_RULE__ACTIVE:
				setActive((Boolean)newValue);
				return;
			case RulesPackage.ENTITY_LINK_RULE__LINKED_ATTRIBUTE:
				setLinkedAttribute((Attribute)newValue);
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
			case RulesPackage.ENTITY_LINK_RULE__NAME:
				setName(NAME_EDEFAULT);
				return;
			case RulesPackage.ENTITY_LINK_RULE__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case RulesPackage.ENTITY_LINK_RULE__ACTIVE:
				setActive(ACTIVE_EDEFAULT);
				return;
			case RulesPackage.ENTITY_LINK_RULE__LINKED_ATTRIBUTE:
				setLinkedAttribute((Attribute)null);
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
			case RulesPackage.ENTITY_LINK_RULE__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case RulesPackage.ENTITY_LINK_RULE__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case RulesPackage.ENTITY_LINK_RULE__ACTIVE:
				return active != ACTIVE_EDEFAULT;
			case RulesPackage.ENTITY_LINK_RULE__LINKED_ATTRIBUTE:
				return linkedAttribute != null;
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
		result.append(')');
		return result.toString();
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public String getRuleTypeName() {
		return getClass().getSimpleName();
	}


	@Override
	public IEntityReader getEntityReader() {
		return entityReader;
	}


	@Override
	public void setEntityReader(IEntityReader reader) {
		this.entityReader = reader;
		
	}

} //EntityLinkRuleImpl
