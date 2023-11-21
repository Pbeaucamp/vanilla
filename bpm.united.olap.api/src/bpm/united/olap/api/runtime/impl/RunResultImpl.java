/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.runtime.impl;

import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import bpm.united.olap.api.runtime.DataStorage;
import bpm.united.olap.api.runtime.MdxSet;
import bpm.united.olap.api.runtime.RunResult;
import bpm.united.olap.api.runtime.RuntimePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Run Result</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link bpm.united.olap.api.runtime.impl.RunResultImpl#getDataStorage <em>Data Storage</em>}</li>
 *   <li>{@link bpm.united.olap.api.runtime.impl.RunResultImpl#isIsNonEmpty <em>Is Non Empty</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RunResultImpl extends EObjectImpl implements RunResult {
	
	protected List<List<MdxSet>> mdxSets;
	protected List<MdxSet> whereSets;
	/**
	 * The cached value of the '{@link #getDataStorage() <em>Data Storage</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDataStorage()
	 * @generated
	 * @ordered
	 */
	protected DataStorage dataStorage;

	/**
	 * The default value of the '{@link #isIsNonEmpty() <em>Is Non Empty</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIsNonEmpty()
	 * @generated
	 * @ordered
	 */
	protected static final boolean IS_NON_EMPTY_EDEFAULT = false;
	/**
	 * The cached value of the '{@link #isIsNonEmpty() <em>Is Non Empty</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIsNonEmpty()
	 * @generated
	 * @ordered
	 */
	protected boolean isNonEmpty = IS_NON_EMPTY_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected RunResultImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return RuntimePackage.Literals.RUN_RESULT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DataStorage getDataStorage() {
		if (dataStorage != null && dataStorage.eIsProxy()) {
			InternalEObject oldDataStorage = (InternalEObject)dataStorage;
			dataStorage = (DataStorage)eResolveProxy(oldDataStorage);
			if (dataStorage != oldDataStorage) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, RuntimePackage.RUN_RESULT__DATA_STORAGE, oldDataStorage, dataStorage));
			}
		}
		return dataStorage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DataStorage basicGetDataStorage() {
		return dataStorage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDataStorage(DataStorage newDataStorage) {
		DataStorage oldDataStorage = dataStorage;
		dataStorage = newDataStorage;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, RuntimePackage.RUN_RESULT__DATA_STORAGE, oldDataStorage, dataStorage));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isIsNonEmpty() {
		return isNonEmpty;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setIsNonEmpty(boolean newIsNonEmpty) {
		boolean oldIsNonEmpty = isNonEmpty;
		isNonEmpty = newIsNonEmpty;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, RuntimePackage.RUN_RESULT__IS_NON_EMPTY, oldIsNonEmpty, isNonEmpty));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case RuntimePackage.RUN_RESULT__DATA_STORAGE:
				if (resolve) return getDataStorage();
				return basicGetDataStorage();
			case RuntimePackage.RUN_RESULT__IS_NON_EMPTY:
				return isIsNonEmpty();
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
			case RuntimePackage.RUN_RESULT__DATA_STORAGE:
				setDataStorage((DataStorage)newValue);
				return;
			case RuntimePackage.RUN_RESULT__IS_NON_EMPTY:
				setIsNonEmpty((Boolean)newValue);
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
			case RuntimePackage.RUN_RESULT__DATA_STORAGE:
				setDataStorage((DataStorage)null);
				return;
			case RuntimePackage.RUN_RESULT__IS_NON_EMPTY:
				setIsNonEmpty(IS_NON_EMPTY_EDEFAULT);
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
			case RuntimePackage.RUN_RESULT__DATA_STORAGE:
				return dataStorage != null;
			case RuntimePackage.RUN_RESULT__IS_NON_EMPTY:
				return isNonEmpty != IS_NON_EMPTY_EDEFAULT;
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
		result.append(" (isNonEmpty: ");
		result.append(isNonEmpty);
		result.append(')');
		return result.toString();
	}

	@Override
	public List<List<MdxSet>> getMdxSets() {
		return mdxSets;
	}

	@Override
	public void setMdxSets(List<List<MdxSet>> sets) {
		mdxSets = sets;
	}

	@Override
	public List<MdxSet> getWhereSets() {
		return whereSets;
	}

	@Override
	public void setWhereSets(List<MdxSet> sets) {
		whereSets = sets;
	}

	private boolean isMeasureRow;
	
	@Override
	public boolean getIsMeasureRow() {
		return isMeasureRow;
	}

	@Override
	public void setIsMeasureRow(boolean isRow) {
		isMeasureRow = isRow;
	}

} //RunResultImpl
