/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.mdm.model.impl;

import bpm.mdm.model.Attribute;
import bpm.mdm.model.Entity;
import bpm.mdm.model.MdmPackage;
import bpm.mdm.model.Strategy;
import bpm.mdm.model.Synchronizer;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Synchronizer</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link bpm.mdm.model.impl.SynchronizerImpl#getDataSourceName <em>Data Source Name</em>}</li>
 *   <li>{@link bpm.mdm.model.impl.SynchronizerImpl#getEntity <em>Entity</em>}</li>
 *   <li>{@link bpm.mdm.model.impl.SynchronizerImpl#getStrategies <em>Strategies</em>}</li>
 *   <li>{@link bpm.mdm.model.impl.SynchronizerImpl#getName <em>Name</em>}</li>
 *   <li>{@link bpm.mdm.model.impl.SynchronizerImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link bpm.mdm.model.impl.SynchronizerImpl#getMapping <em>Mapping</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SynchronizerImpl extends EObjectImpl implements Synchronizer {
	/**
	 * The default value of the '{@link #getDataSourceName() <em>Data Source Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDataSourceName()
	 * @generated
	 * @ordered
	 */
	protected static final String DATA_SOURCE_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDataSourceName() <em>Data Source Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDataSourceName()
	 * @generated
	 * @ordered
	 */
	protected String dataSourceName = DATA_SOURCE_NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getEntity() <em>Entity</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEntity()
	 * @generated
	 * @ordered
	 */
	protected Entity entity;

	/**
	 * The cached value of the '{@link #getStrategies() <em>Strategies</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStrategies()
	 * @generated
	 * @ordered
	 */
	protected EList<Strategy> strategies;

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
	 * The cached value of the '{@link #getMapping() <em>Mapping</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMapping()
	 * @generated
	 * @ordered
	 */
	protected EMap<Attribute, Integer> mapping;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SynchronizerImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MdmPackage.Literals.SYNCHRONIZER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getDataSourceName() {
		return dataSourceName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDataSourceName(String newDataSourceName) {
		String oldDataSourceName = dataSourceName;
		dataSourceName = newDataSourceName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MdmPackage.SYNCHRONIZER__DATA_SOURCE_NAME, oldDataSourceName, dataSourceName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Entity getEntity() {
		if (entity != null && entity.eIsProxy()) {
			InternalEObject oldEntity = (InternalEObject)entity;
			entity = (Entity)eResolveProxy(oldEntity);
			if (entity != oldEntity) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, MdmPackage.SYNCHRONIZER__ENTITY, oldEntity, entity));
			}
		}
		return entity;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Entity basicGetEntity() {
		return entity;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setEntity(Entity newEntity) {
		Entity oldEntity = entity;
		entity = newEntity;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MdmPackage.SYNCHRONIZER__ENTITY, oldEntity, entity));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<Strategy> getStrategies() {
		if (strategies == null) {
			strategies = new EObjectResolvingEList<Strategy>(Strategy.class, this, MdmPackage.SYNCHRONIZER__STRATEGIES);
		}
		return strategies;
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
			eNotify(new ENotificationImpl(this, Notification.SET, MdmPackage.SYNCHRONIZER__NAME, oldName, name));
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
			eNotify(new ENotificationImpl(this, Notification.SET, MdmPackage.SYNCHRONIZER__DESCRIPTION, oldDescription, description));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map<Attribute, Integer> getMapping() {
		if (mapping == null) {
			mapping = new EcoreEMap<Attribute,Integer>(MdmPackage.Literals.ATTRIBUTE_TO_INTEGER_MAP, AttributeToIntegerMapImpl.class, this, MdmPackage.SYNCHRONIZER__MAPPING);
		}
		return mapping.map();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public Attribute getAttribute(int dataSourceField) {
		for(Attribute a : getMapping().keySet()){
			if (getMapping().get(a) != null && getMapping().get(a) == dataSourceField){
				return a;
			}
		}
		return null;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public boolean mapAttributeWithField(Attribute attribute, int dataSourceField) {
		getMapping().put(attribute, dataSourceField);
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public int getDataSourceField(Attribute attribute) {
		Integer i =  getMapping().get(attribute);
		if (i == null){
			return -1;
		}
		return i;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MdmPackage.SYNCHRONIZER__MAPPING:
				return ((InternalEList<?>)((EMap.InternalMapView<Attribute, Integer>)getMapping()).eMap()).basicRemove(otherEnd, msgs);
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
			case MdmPackage.SYNCHRONIZER__DATA_SOURCE_NAME:
				return getDataSourceName();
			case MdmPackage.SYNCHRONIZER__ENTITY:
				if (resolve) return getEntity();
				return basicGetEntity();
			case MdmPackage.SYNCHRONIZER__STRATEGIES:
				return getStrategies();
			case MdmPackage.SYNCHRONIZER__NAME:
				return getName();
			case MdmPackage.SYNCHRONIZER__DESCRIPTION:
				return getDescription();
			case MdmPackage.SYNCHRONIZER__MAPPING:
				if (coreType) return ((EMap.InternalMapView<Attribute, Integer>)getMapping()).eMap();
				else return getMapping();
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
			case MdmPackage.SYNCHRONIZER__DATA_SOURCE_NAME:
				setDataSourceName((String)newValue);
				return;
			case MdmPackage.SYNCHRONIZER__ENTITY:
				setEntity((Entity)newValue);
				return;
			case MdmPackage.SYNCHRONIZER__STRATEGIES:
				getStrategies().clear();
				getStrategies().addAll((Collection<? extends Strategy>)newValue);
				return;
			case MdmPackage.SYNCHRONIZER__NAME:
				setName((String)newValue);
				return;
			case MdmPackage.SYNCHRONIZER__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case MdmPackage.SYNCHRONIZER__MAPPING:
				((EStructuralFeature.Setting)((EMap.InternalMapView<Attribute, Integer>)getMapping()).eMap()).set(newValue);
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
			case MdmPackage.SYNCHRONIZER__DATA_SOURCE_NAME:
				setDataSourceName(DATA_SOURCE_NAME_EDEFAULT);
				return;
			case MdmPackage.SYNCHRONIZER__ENTITY:
				setEntity((Entity)null);
				return;
			case MdmPackage.SYNCHRONIZER__STRATEGIES:
				getStrategies().clear();
				return;
			case MdmPackage.SYNCHRONIZER__NAME:
				setName(NAME_EDEFAULT);
				return;
			case MdmPackage.SYNCHRONIZER__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case MdmPackage.SYNCHRONIZER__MAPPING:
				getMapping().clear();
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
			case MdmPackage.SYNCHRONIZER__DATA_SOURCE_NAME:
				return DATA_SOURCE_NAME_EDEFAULT == null ? dataSourceName != null : !DATA_SOURCE_NAME_EDEFAULT.equals(dataSourceName);
			case MdmPackage.SYNCHRONIZER__ENTITY:
				return entity != null;
			case MdmPackage.SYNCHRONIZER__STRATEGIES:
				return strategies != null && !strategies.isEmpty();
			case MdmPackage.SYNCHRONIZER__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case MdmPackage.SYNCHRONIZER__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case MdmPackage.SYNCHRONIZER__MAPPING:
				return mapping != null && !mapping.isEmpty();
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
		result.append(" (dataSourceName: ");
		result.append(dataSourceName);
		result.append(", name: ");
		result.append(name);
		result.append(", description: ");
		result.append(description);
		result.append(')');
		return result.toString();
	}

} //SynchronizerImpl
