/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.datasource.impl;

import bpm.united.olap.api.datasource.Connection;
import bpm.united.olap.api.datasource.DataObject;
import bpm.united.olap.api.datasource.Datasource;
import bpm.united.olap.api.datasource.DatasourcePackage;
import bpm.united.olap.api.datasource.Relation;

import bpm.united.olap.api.model.ModelPackage;
import bpm.united.olap.api.model.Schema;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Datasource</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link bpm.united.olap.api.datasource.impl.DatasourceImpl#getName <em>Name</em>}</li>
 *   <li>{@link bpm.united.olap.api.datasource.impl.DatasourceImpl#getId <em>Id</em>}</li>
 *   <li>{@link bpm.united.olap.api.datasource.impl.DatasourceImpl#getConnections <em>Connections</em>}</li>
 *   <li>{@link bpm.united.olap.api.datasource.impl.DatasourceImpl#getRelations <em>Relations</em>}</li>
 *   <li>{@link bpm.united.olap.api.datasource.impl.DatasourceImpl#getDataObjects <em>Data Objects</em>}</li>
 *   <li>{@link bpm.united.olap.api.datasource.impl.DatasourceImpl#getPublicProperties <em>Public Properties</em>}</li>
 *   <li>{@link bpm.united.olap.api.datasource.impl.DatasourceImpl#getDatasourceExtensionId <em>Datasource Extension Id</em>}</li>
 *   <li>{@link bpm.united.olap.api.datasource.impl.DatasourceImpl#getPrivateProperties <em>Private Properties</em>}</li>
 *   <li>{@link bpm.united.olap.api.datasource.impl.DatasourceImpl#getParent <em>Parent</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DatasourceImpl extends EObjectImpl implements Datasource {
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
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected String id = ID_EDEFAULT;

	/**
	 * The cached value of the '{@link #getConnections() <em>Connections</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConnections()
	 * @generated
	 * @ordered
	 */
	protected EList<Connection> connections;

	/**
	 * The cached value of the '{@link #getRelations() <em>Relations</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRelations()
	 * @generated
	 * @ordered
	 */
	protected EList<Relation> relations;

	/**
	 * The cached value of the '{@link #getDataObjects() <em>Data Objects</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDataObjects()
	 * @generated
	 * @ordered
	 */
	protected EList<DataObject> dataObjects;

	/**
	 * The default value of the '{@link #getPublicProperties() <em>Public Properties</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPublicProperties()
	 * @ordered
	 */
	protected static final Properties PUBLIC_PROPERTIES_EDEFAULT = new Properties();

	/**
	 * The cached value of the '{@link #getPublicProperties() <em>Public Properties</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPublicProperties()
	 * @generated
	 * @ordered
	 */
	protected Properties publicProperties = PUBLIC_PROPERTIES_EDEFAULT;

	/**
	 * The default value of the '{@link #getDatasourceExtensionId() <em>Datasource Extension Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDatasourceExtensionId()
	 * @generated
	 * @ordered
	 */
	protected static final String DATASOURCE_EXTENSION_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDatasourceExtensionId() <em>Datasource Extension Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDatasourceExtensionId()
	 * @generated
	 * @ordered
	 */
	protected String datasourceExtensionId = DATASOURCE_EXTENSION_ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getPrivateProperties() <em>Private Properties</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPrivateProperties()
	 * @ordered
	 */
	protected static final Properties PRIVATE_PROPERTIES_EDEFAULT = new Properties();

	/**
	 * The cached value of the '{@link #getPrivateProperties() <em>Private Properties</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPrivateProperties()
	 * @generated
	 * @ordered
	 */
	protected Properties privateProperties = PRIVATE_PROPERTIES_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DatasourceImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DatasourcePackage.Literals.DATASOURCE;
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
			eNotify(new ENotificationImpl(this, Notification.SET, DatasourcePackage.DATASOURCE__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setId(String newId) {
		String oldId = id;
		id = newId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DatasourcePackage.DATASOURCE__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<Connection> getConnections() {
		if (connections == null) {
			connections = new EObjectContainmentEList<Connection>(Connection.class, this, DatasourcePackage.DATASOURCE__CONNECTIONS);
		}
		return connections;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<Relation> getRelations() {
		if (relations == null) {
			relations = new EObjectContainmentEList<Relation>(Relation.class, this, DatasourcePackage.DATASOURCE__RELATIONS);
		}
		return relations;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<DataObject> getDataObjects() {
		if (dataObjects == null) {
			dataObjects = new EObjectContainmentWithInverseEList<DataObject>(DataObject.class, this, DatasourcePackage.DATASOURCE__DATA_OBJECTS, DatasourcePackage.DATA_OBJECT__PARENT);
		}
		return dataObjects;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Properties getPublicProperties() {
		return publicProperties;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPublicProperties(Properties newPublicProperties) {
		Properties oldPublicProperties = publicProperties;
		publicProperties = newPublicProperties;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DatasourcePackage.DATASOURCE__PUBLIC_PROPERTIES, oldPublicProperties, publicProperties));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getDatasourceExtensionId() {
		return datasourceExtensionId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDatasourceExtensionId(String newDatasourceExtensionId) {
		String oldDatasourceExtensionId = datasourceExtensionId;
		datasourceExtensionId = newDatasourceExtensionId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DatasourcePackage.DATASOURCE__DATASOURCE_EXTENSION_ID, oldDatasourceExtensionId, datasourceExtensionId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Properties getPrivateProperties() {
		return privateProperties;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPrivateProperties(Properties newPrivateProperties) {
		Properties oldPrivateProperties = privateProperties;
		privateProperties = newPrivateProperties;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DatasourcePackage.DATASOURCE__PRIVATE_PROPERTIES, oldPrivateProperties, privateProperties));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Schema getParent() {
		if (eContainerFeatureID() != DatasourcePackage.DATASOURCE__PARENT) return null;
		return (Schema)eContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetParent(Schema newParent, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject)newParent, DatasourcePackage.DATASOURCE__PARENT, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setParent(Schema newParent) {
		if (newParent != eInternalContainer() || (eContainerFeatureID() != DatasourcePackage.DATASOURCE__PARENT && newParent != null)) {
			if (EcoreUtil.isAncestor(this, newParent))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newParent != null)
				msgs = ((InternalEObject)newParent).eInverseAdd(this, ModelPackage.SCHEMA__DATASOURCES, Schema.class, msgs);
			msgs = basicSetParent(newParent, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DatasourcePackage.DATASOURCE__PARENT, newParent, newParent));
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
			case DatasourcePackage.DATASOURCE__DATA_OBJECTS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getDataObjects()).basicAdd(otherEnd, msgs);
			case DatasourcePackage.DATASOURCE__PARENT:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetParent((Schema)otherEnd, msgs);
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
			case DatasourcePackage.DATASOURCE__CONNECTIONS:
				return ((InternalEList<?>)getConnections()).basicRemove(otherEnd, msgs);
			case DatasourcePackage.DATASOURCE__RELATIONS:
				return ((InternalEList<?>)getRelations()).basicRemove(otherEnd, msgs);
			case DatasourcePackage.DATASOURCE__DATA_OBJECTS:
				return ((InternalEList<?>)getDataObjects()).basicRemove(otherEnd, msgs);
			case DatasourcePackage.DATASOURCE__PARENT:
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
			case DatasourcePackage.DATASOURCE__PARENT:
				return eInternalContainer().eInverseRemove(this, ModelPackage.SCHEMA__DATASOURCES, Schema.class, msgs);
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
			case DatasourcePackage.DATASOURCE__NAME:
				return getName();
			case DatasourcePackage.DATASOURCE__ID:
				return getId();
			case DatasourcePackage.DATASOURCE__CONNECTIONS:
				return getConnections();
			case DatasourcePackage.DATASOURCE__RELATIONS:
				return getRelations();
			case DatasourcePackage.DATASOURCE__DATA_OBJECTS:
				return getDataObjects();
			case DatasourcePackage.DATASOURCE__PUBLIC_PROPERTIES:
				return getPublicProperties();
			case DatasourcePackage.DATASOURCE__DATASOURCE_EXTENSION_ID:
				return getDatasourceExtensionId();
			case DatasourcePackage.DATASOURCE__PRIVATE_PROPERTIES:
				return getPrivateProperties();
			case DatasourcePackage.DATASOURCE__PARENT:
				return getParent();
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
			case DatasourcePackage.DATASOURCE__NAME:
				setName((String)newValue);
				return;
			case DatasourcePackage.DATASOURCE__ID:
				setId((String)newValue);
				return;
			case DatasourcePackage.DATASOURCE__CONNECTIONS:
				getConnections().clear();
				getConnections().addAll((Collection<? extends Connection>)newValue);
				return;
			case DatasourcePackage.DATASOURCE__RELATIONS:
				getRelations().clear();
				getRelations().addAll((Collection<? extends Relation>)newValue);
				return;
			case DatasourcePackage.DATASOURCE__DATA_OBJECTS:
				getDataObjects().clear();
				getDataObjects().addAll((Collection<? extends DataObject>)newValue);
				return;
			case DatasourcePackage.DATASOURCE__PUBLIC_PROPERTIES:
				setPublicProperties((Properties)newValue);
				return;
			case DatasourcePackage.DATASOURCE__DATASOURCE_EXTENSION_ID:
				setDatasourceExtensionId((String)newValue);
				return;
			case DatasourcePackage.DATASOURCE__PRIVATE_PROPERTIES:
				setPrivateProperties((Properties)newValue);
				return;
			case DatasourcePackage.DATASOURCE__PARENT:
				setParent((Schema)newValue);
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
			case DatasourcePackage.DATASOURCE__NAME:
				setName(NAME_EDEFAULT);
				return;
			case DatasourcePackage.DATASOURCE__ID:
				setId(ID_EDEFAULT);
				return;
			case DatasourcePackage.DATASOURCE__CONNECTIONS:
				getConnections().clear();
				return;
			case DatasourcePackage.DATASOURCE__RELATIONS:
				getRelations().clear();
				return;
			case DatasourcePackage.DATASOURCE__DATA_OBJECTS:
				getDataObjects().clear();
				return;
			case DatasourcePackage.DATASOURCE__PUBLIC_PROPERTIES:
				setPublicProperties(PUBLIC_PROPERTIES_EDEFAULT);
				return;
			case DatasourcePackage.DATASOURCE__DATASOURCE_EXTENSION_ID:
				setDatasourceExtensionId(DATASOURCE_EXTENSION_ID_EDEFAULT);
				return;
			case DatasourcePackage.DATASOURCE__PRIVATE_PROPERTIES:
				setPrivateProperties(PRIVATE_PROPERTIES_EDEFAULT);
				return;
			case DatasourcePackage.DATASOURCE__PARENT:
				setParent((Schema)null);
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
			case DatasourcePackage.DATASOURCE__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case DatasourcePackage.DATASOURCE__ID:
				return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
			case DatasourcePackage.DATASOURCE__CONNECTIONS:
				return connections != null && !connections.isEmpty();
			case DatasourcePackage.DATASOURCE__RELATIONS:
				return relations != null && !relations.isEmpty();
			case DatasourcePackage.DATASOURCE__DATA_OBJECTS:
				return dataObjects != null && !dataObjects.isEmpty();
			case DatasourcePackage.DATASOURCE__PUBLIC_PROPERTIES:
				return PUBLIC_PROPERTIES_EDEFAULT == null ? publicProperties != null : !PUBLIC_PROPERTIES_EDEFAULT.equals(publicProperties);
			case DatasourcePackage.DATASOURCE__DATASOURCE_EXTENSION_ID:
				return DATASOURCE_EXTENSION_ID_EDEFAULT == null ? datasourceExtensionId != null : !DATASOURCE_EXTENSION_ID_EDEFAULT.equals(datasourceExtensionId);
			case DatasourcePackage.DATASOURCE__PRIVATE_PROPERTIES:
				return PRIVATE_PROPERTIES_EDEFAULT == null ? privateProperties != null : !PRIVATE_PROPERTIES_EDEFAULT.equals(privateProperties);
			case DatasourcePackage.DATASOURCE__PARENT:
				return getParent() != null;
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
		result.append(", id: ");
		result.append(id);
		result.append(", publicProperties: ");
		result.append(publicProperties);
		result.append(", datasourceExtensionId: ");
		result.append(datasourceExtensionId);
		result.append(", privateProperties: ");
		result.append(privateProperties);
		result.append(')');
		return result.toString();
	}

} //DatasourceImpl
