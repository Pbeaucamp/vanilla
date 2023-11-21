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
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import bpm.united.olap.api.datasource.DataObject;
import bpm.united.olap.api.model.Cube;
import bpm.united.olap.api.model.Dimension;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.model.ModelPackage;
import bpm.united.olap.api.model.Schema;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Cube</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link bpm.united.olap.api.model.impl.CubeImpl#getMeasures <em>Measures</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.impl.CubeImpl#getDimensions <em>Dimensions</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.impl.CubeImpl#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class CubeImpl extends EObjectImpl implements Cube {
	
	
	
	protected Schema parentSchema;
	
	
	
	/**
	 * @return the parentSchema
	 */
	public Schema getParentSchema() {
		return parentSchema;
	}

	/**
	 * @param parentSchema the parentSchema to set
	 */
	public void setParentSchema(Schema parentSchema) {
		this.parentSchema = parentSchema;
	}

	/**
	 * The cached value of the '{@link #getMeasures() <em>Measures</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMeasures()
	 * @generated
	 * @ordered
	 */
	protected EList<Measure> measures;

	/**
	 * The cached value of the '{@link #getDimensions() <em>Dimensions</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDimensions()
	 * @generated
	 * @ordered
	 */
	protected EList<Dimension> dimensions;

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
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CubeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ModelPackage.Literals.CUBE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<Measure> getMeasures() {
		if (measures == null) {
			measures = new EObjectResolvingEList<Measure>(Measure.class, this, ModelPackage.CUBE__MEASURES);
		}
		return measures;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<Dimension> getDimensions() {
		if (dimensions == null) {
			dimensions = new EObjectResolvingEList<Dimension>(Dimension.class, this, ModelPackage.CUBE__DIMENSIONS);
		}
		return dimensions;
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
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.CUBE__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ModelPackage.CUBE__MEASURES:
				return getMeasures();
			case ModelPackage.CUBE__DIMENSIONS:
				return getDimensions();
			case ModelPackage.CUBE__NAME:
				return getName();
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
			case ModelPackage.CUBE__MEASURES:
				getMeasures().clear();
				getMeasures().addAll((Collection<? extends Measure>)newValue);
				return;
			case ModelPackage.CUBE__DIMENSIONS:
				getDimensions().clear();
				getDimensions().addAll((Collection<? extends Dimension>)newValue);
				return;
			case ModelPackage.CUBE__NAME:
				setName((String)newValue);
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
			case ModelPackage.CUBE__MEASURES:
				getMeasures().clear();
				return;
			case ModelPackage.CUBE__DIMENSIONS:
				getDimensions().clear();
				return;
			case ModelPackage.CUBE__NAME:
				setName(NAME_EDEFAULT);
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
			case ModelPackage.CUBE__MEASURES:
				return measures != null && !measures.isEmpty();
			case ModelPackage.CUBE__DIMENSIONS:
				return dimensions != null && !dimensions.isEmpty();
			case ModelPackage.CUBE__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
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
		result.append(')');
		return result.toString();
	}

	private DataObject factTable;
	
	@Override
	public DataObject getFactTable() {
		return factTable;
	}

	@Override
	public void setFactTable(DataObject factTable) {
		this.factTable = factTable;
	}

} //CubeImpl
