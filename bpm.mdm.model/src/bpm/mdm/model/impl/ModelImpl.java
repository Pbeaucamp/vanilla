/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.mdm.model.impl;

import java.util.Collection;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import bpm.mdm.model.Entity;
import bpm.mdm.model.MdmPackage;
import bpm.mdm.model.Model;
import bpm.mdm.model.Synchronizer;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Model</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link bpm.mdm.model.impl.ModelImpl#getName <em>Name</em>}</li>
 *   <li>{@link bpm.mdm.model.impl.ModelImpl#getEntities <em>Entities</em>}</li>
 *   <li>{@link bpm.mdm.model.impl.ModelImpl#getSynchronizers <em>Synchronizers</em>}</li>
 *   <li>{@link bpm.mdm.model.impl.ModelImpl#getDataSources <em>Data Sources</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ModelImpl extends EObjectImpl implements Model {
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
	 * The cached value of the '{@link #getEntities() <em>Entities</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEntities()
	 * @generated
	 * @ordered
	 */
	protected EList<Entity> entities;

	/**
	 * The cached value of the '{@link #getSynchronizers() <em>Synchronizers</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSynchronizers()
	 * @generated
	 * @ordered
	 */
	protected EList<Synchronizer> synchronizers;

	/**
	 * The cached value of the '{@link #getDataSources() <em>Data Sources</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDataSources()
	 * @generated
	 * @ordered
	 */
	protected EList<DataSetDesign> dataSources;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ModelImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MdmPackage.Literals.MODEL;
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
			eNotify(new ENotificationImpl(this, Notification.SET, MdmPackage.MODEL__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<Entity> getEntities() {
		if (entities == null) {
			entities = new EObjectContainmentEList<Entity>(Entity.class, this, MdmPackage.MODEL__ENTITIES);
		}
		return entities;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<Synchronizer> getSynchronizers() {
		if (synchronizers == null) {
			synchronizers = new EObjectContainmentEList<Synchronizer>(Synchronizer.class, this, MdmPackage.MODEL__SYNCHRONIZERS);
		}
		return synchronizers;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<DataSetDesign> getDataSources() {
		if (dataSources == null) {
			dataSources = new EDataTypeUniqueEList<DataSetDesign>(DataSetDesign.class, this, MdmPackage.MODEL__DATA_SOURCES);
		}
		return dataSources;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public DataSetDesign getDataSource(String dataSourceName) {
		// TODO: implement this method
		// Ensure that you remove @generated or mark it @generated NOT
		for(DataSetDesign ds : getDataSources()){
			if (ds.getName().equals(dataSourceName)){
				return ds;
			}
		}
//		throw new UnsupportedOperationException();
		return null;
	}

//	/**
//	 * <!-- begin-user-doc -->
//	 * <!-- end-user-doc -->
//	 */
//	public EntityStore getStore(Entity entity) {
//		EntityStore store = null;
//		for(EntityStore s : ((Model)entity.eContainer()).getStores()){
//			if (s.getEntity() == entity){
//				store = s;
//				break;
//			}
//		}
//		
//		if (store == null){
//			store = StorageFactory.eINSTANCE.createEntityStore(); 
//			store.setEntity(entity);
//			getStores().add(store);
//		}
//		return store;
//	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MdmPackage.MODEL__ENTITIES:
				return ((InternalEList<?>)getEntities()).basicRemove(otherEnd, msgs);
			case MdmPackage.MODEL__SYNCHRONIZERS:
				return ((InternalEList<?>)getSynchronizers()).basicRemove(otherEnd, msgs);
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
			case MdmPackage.MODEL__NAME:
				return getName();
			case MdmPackage.MODEL__ENTITIES:
				return getEntities();
			case MdmPackage.MODEL__SYNCHRONIZERS:
				return getSynchronizers();
			case MdmPackage.MODEL__DATA_SOURCES:
				return getDataSources();
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
			case MdmPackage.MODEL__NAME:
				setName((String)newValue);
				return;
			case MdmPackage.MODEL__ENTITIES:
				getEntities().clear();
				getEntities().addAll((Collection<? extends Entity>)newValue);
				return;
			case MdmPackage.MODEL__SYNCHRONIZERS:
				getSynchronizers().clear();
				getSynchronizers().addAll((Collection<? extends Synchronizer>)newValue);
				return;
			case MdmPackage.MODEL__DATA_SOURCES:
				getDataSources().clear();
				getDataSources().addAll((Collection<? extends DataSetDesign>)newValue);
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
			case MdmPackage.MODEL__NAME:
				setName(NAME_EDEFAULT);
				return;
			case MdmPackage.MODEL__ENTITIES:
				getEntities().clear();
				return;
			case MdmPackage.MODEL__SYNCHRONIZERS:
				getSynchronizers().clear();
				return;
			case MdmPackage.MODEL__DATA_SOURCES:
				getDataSources().clear();
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
			case MdmPackage.MODEL__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case MdmPackage.MODEL__ENTITIES:
				return entities != null && !entities.isEmpty();
			case MdmPackage.MODEL__SYNCHRONIZERS:
				return synchronizers != null && !synchronizers.isEmpty();
			case MdmPackage.MODEL__DATA_SOURCES:
				return dataSources != null && !dataSources.isEmpty();
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
		result.append(", dataSources: ");
		result.append(dataSources);
		result.append(')');
		return result.toString();
	}

} //ModelImpl
