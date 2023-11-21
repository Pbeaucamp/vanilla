/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.model.impl;

import bpm.united.olap.api.datasource.Datasource;

import bpm.united.olap.api.datasource.DatasourcePackage;
import bpm.united.olap.api.model.Cube;
import bpm.united.olap.api.model.Dimension;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.model.ModelPackage;
import bpm.united.olap.api.model.Schema;

import java.util.Collection;

import java.util.Date;
import java.util.List;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Schema</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link bpm.united.olap.api.model.impl.SchemaImpl#getCubes <em>Cubes</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.impl.SchemaImpl#getDatasources <em>Datasources</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.impl.SchemaImpl#getName <em>Name</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.impl.SchemaImpl#getDimensions <em>Dimensions</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.impl.SchemaImpl#getMeasures <em>Measures</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.impl.SchemaImpl#getId <em>Id</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.impl.SchemaImpl#getLastModificationDate <em>Last Modification Date</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SchemaImpl extends EObjectImpl implements Schema {
	/**
	 * The cached value of the '{@link #getCubes() <em>Cubes</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCubes()
	 * @generated
	 * @ordered
	 */
	protected EList<Cube> cubes;

	/**
	 * The cached value of the '{@link #getDatasources() <em>Datasources</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDatasources()
	 * @generated
	 * @ordered
	 */
	protected EList<Datasource> datasources;

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
	 * The cached value of the '{@link #getDimensions() <em>Dimensions</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDimensions()
	 * @generated
	 * @ordered
	 */
	protected EList<Dimension> dimensions;

	/**
	 * The cached value of the '{@link #getMeasures() <em>Measures</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMeasures()
	 * @generated
	 * @ordered
	 */
	protected EList<Measure> measures;

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
	 * The default value of the '{@link #getLastModificationDate() <em>Last Modification Date</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLastModificationDate()
	 * @generated
	 * @ordered
	 */
	protected static final Date LAST_MODIFICATION_DATE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getLastModificationDate() <em>Last Modification Date</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLastModificationDate()
	 * @generated
	 * @ordered
	 */
	protected Date lastModificationDate = LAST_MODIFICATION_DATE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SchemaImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ModelPackage.Literals.SCHEMA;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<Cube> getCubes() {
		if (cubes == null) {
			cubes = new EObjectContainmentEList<Cube>(Cube.class, this, ModelPackage.SCHEMA__CUBES);
		}
		return cubes;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<Datasource> getDatasources() {
		if (datasources == null) {
			datasources = new EObjectContainmentWithInverseEList<Datasource>(Datasource.class, this, ModelPackage.SCHEMA__DATASOURCES, DatasourcePackage.DATASOURCE__PARENT);
		}
		return datasources;
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
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.SCHEMA__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<Dimension> getDimensions() {
		if (dimensions == null) {
			dimensions = new EObjectContainmentWithInverseEList<Dimension>(Dimension.class, this, ModelPackage.SCHEMA__DIMENSIONS, ModelPackage.DIMENSION__PARENT_SCHEMA);
		}
		return dimensions;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<Measure> getMeasures() {
		if (measures == null) {
			measures = new EObjectContainmentWithInverseEList<Measure>(Measure.class, this, ModelPackage.SCHEMA__MEASURES, ModelPackage.MEASURE__PARENT_SCHEMA);
		}
		return measures;
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
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.SCHEMA__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Date getLastModificationDate() {
		return lastModificationDate;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLastModificationDate(Date newLastModificationDate) {
		Date oldLastModificationDate = lastModificationDate;
		lastModificationDate = newLastModificationDate;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.SCHEMA__LAST_MODIFICATION_DATE, oldLastModificationDate, lastModificationDate));
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
			case ModelPackage.SCHEMA__DATASOURCES:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getDatasources()).basicAdd(otherEnd, msgs);
			case ModelPackage.SCHEMA__DIMENSIONS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getDimensions()).basicAdd(otherEnd, msgs);
			case ModelPackage.SCHEMA__MEASURES:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getMeasures()).basicAdd(otherEnd, msgs);
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
			case ModelPackage.SCHEMA__CUBES:
				return ((InternalEList<?>)getCubes()).basicRemove(otherEnd, msgs);
			case ModelPackage.SCHEMA__DATASOURCES:
				return ((InternalEList<?>)getDatasources()).basicRemove(otherEnd, msgs);
			case ModelPackage.SCHEMA__DIMENSIONS:
				return ((InternalEList<?>)getDimensions()).basicRemove(otherEnd, msgs);
			case ModelPackage.SCHEMA__MEASURES:
				return ((InternalEList<?>)getMeasures()).basicRemove(otherEnd, msgs);
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
			case ModelPackage.SCHEMA__CUBES:
				return getCubes();
			case ModelPackage.SCHEMA__DATASOURCES:
				return getDatasources();
			case ModelPackage.SCHEMA__NAME:
				return getName();
			case ModelPackage.SCHEMA__DIMENSIONS:
				return getDimensions();
			case ModelPackage.SCHEMA__MEASURES:
				return getMeasures();
			case ModelPackage.SCHEMA__ID:
				return getId();
			case ModelPackage.SCHEMA__LAST_MODIFICATION_DATE:
				return getLastModificationDate();
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
			case ModelPackage.SCHEMA__CUBES:
				getCubes().clear();
				getCubes().addAll((Collection<? extends Cube>)newValue);
				
				for(Cube c : getCubes()){
					c.setParentSchema(this);
				}
				return;
			case ModelPackage.SCHEMA__DATASOURCES:
				getDatasources().clear();
				getDatasources().addAll((Collection<? extends Datasource>)newValue);
				return;
			case ModelPackage.SCHEMA__NAME:
				setName((String)newValue);
				return;
			case ModelPackage.SCHEMA__DIMENSIONS:
				getDimensions().clear();
				getDimensions().addAll((Collection<? extends Dimension>)newValue);
				return;
			case ModelPackage.SCHEMA__MEASURES:
				getMeasures().clear();
				getMeasures().addAll((Collection<? extends Measure>)newValue);
				return;
			case ModelPackage.SCHEMA__ID:
				setId((String)newValue);
				return;
			case ModelPackage.SCHEMA__LAST_MODIFICATION_DATE:
				setLastModificationDate((Date)newValue);
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
			case ModelPackage.SCHEMA__CUBES:
				getCubes().clear();
				return;
			case ModelPackage.SCHEMA__DATASOURCES:
				getDatasources().clear();
				return;
			case ModelPackage.SCHEMA__NAME:
				setName(NAME_EDEFAULT);
				return;
			case ModelPackage.SCHEMA__DIMENSIONS:
				getDimensions().clear();
				return;
			case ModelPackage.SCHEMA__MEASURES:
				getMeasures().clear();
				return;
			case ModelPackage.SCHEMA__ID:
				setId(ID_EDEFAULT);
				return;
			case ModelPackage.SCHEMA__LAST_MODIFICATION_DATE:
				setLastModificationDate(LAST_MODIFICATION_DATE_EDEFAULT);
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
			case ModelPackage.SCHEMA__CUBES:
				return cubes != null && !cubes.isEmpty();
			case ModelPackage.SCHEMA__DATASOURCES:
				return datasources != null && !datasources.isEmpty();
			case ModelPackage.SCHEMA__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case ModelPackage.SCHEMA__DIMENSIONS:
				return dimensions != null && !dimensions.isEmpty();
			case ModelPackage.SCHEMA__MEASURES:
				return measures != null && !measures.isEmpty();
			case ModelPackage.SCHEMA__ID:
				return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
			case ModelPackage.SCHEMA__LAST_MODIFICATION_DATE:
				return LAST_MODIFICATION_DATE_EDEFAULT == null ? lastModificationDate != null : !LAST_MODIFICATION_DATE_EDEFAULT.equals(lastModificationDate);
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
		result.append(", lastModificationDate: ");
		result.append(lastModificationDate);
		result.append(')');
		return result.toString();
	}

} //SchemaImpl
