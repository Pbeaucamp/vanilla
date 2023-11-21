/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.runtime.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import bpm.united.olap.api.runtime.DataCell;
import bpm.united.olap.api.runtime.DataCellIdentifier2;
import bpm.united.olap.api.runtime.RuntimePackage;
import bpm.united.olap.api.runtime.calculation.ICalculation;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Data Cell</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link bpm.united.olap.api.runtime.impl.DataCellImpl#getIdentifier <em>Identifier</em>}</li>
 *   <li>{@link bpm.united.olap.api.runtime.impl.DataCellImpl#getRow <em>Row</em>}</li>
 *   <li>{@link bpm.united.olap.api.runtime.impl.DataCellImpl#getCol <em>Col</em>}</li>
 *   <li>{@link bpm.united.olap.api.runtime.impl.DataCellImpl#getResultValue <em>Result Value</em>}</li>
 *   <li>{@link bpm.united.olap.api.runtime.impl.DataCellImpl#getFormat <em>Format</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DataCellImpl extends EObjectImpl implements DataCell {
	/**
	 * The cached value of the '{@link #getIdentifier() <em>Identifier</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIdentifier()
	 * @generated
	 * @ordered
	 */
	protected DataCellIdentifier2 identifier2;
//	protected DataCellIdentifier identifier;

	/**
	 * The default value of the '{@link #getRow() <em>Row</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRow()
	 * @generated
	 * @ordered
	 */
	protected static final int ROW_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getRow() <em>Row</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRow()
	 * @generated
	 * @ordered
	 */
	protected int row = ROW_EDEFAULT;

	/**
	 * The default value of the '{@link #getCol() <em>Col</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCol()
	 * @generated
	 * @ordered
	 */
	protected static final int COL_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getCol() <em>Col</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCol()
	 * @generated
	 * @ordered
	 */
	protected int col = COL_EDEFAULT;

	/**
	 * The default value of the '{@link #getResultValue() <em>Result Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResultValue()
	 * @generated
	 * @ordered
	 */
	protected static final Double RESULT_VALUE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getResultValue() <em>Result Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResultValue()
	 * @generated
	 * @ordered
	 */
	protected Double resultValue = RESULT_VALUE_EDEFAULT;

	/**
	 * The default value of the '{@link #getFormat() <em>Format</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFormat()
	 * @generated
	 * @ordered
	 */
	protected static final String FORMAT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getFormat() <em>Format</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFormat()
	 * @generated
	 * @ordered
	 */
	protected String format = FORMAT_EDEFAULT;

	
	protected String cacheKey;
	protected String parentKey;
	
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DataCellImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return RuntimePackage.Literals.DATA_CELL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DataCellIdentifier2 getIdentifier2() {
		if (identifier2 != null && identifier2.eIsProxy()) {
			InternalEObject oldIdentifier = (InternalEObject)identifier2;
			identifier2 = (DataCellIdentifier2)eResolveProxy(oldIdentifier);
			if (identifier2 != oldIdentifier) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, RuntimePackage.DATA_CELL__IDENTIFIER, oldIdentifier, identifier2));
			}
		}
		return identifier2;
	}
//	public DataCellIdentifier getIdentifier() {
//		if (identifier != null && identifier.eIsProxy()) {
//			InternalEObject oldIdentifier = (InternalEObject)identifier;
//			identifier = (DataCellIdentifier)eResolveProxy(oldIdentifier);
//			if (identifier != oldIdentifier) {
//				if (eNotificationRequired())
//					eNotify(new ENotificationImpl(this, Notification.RESOLVE, RuntimePackage.DATA_CELL__IDENTIFIER, oldIdentifier, identifier));
//			}
//		}
//		return identifier;
//	}
//
//	/**
//	 * <!-- begin-user-doc -->
//	 * <!-- end-user-doc -->
//	 * @generated
//	 */
//	public DataCellIdentifier basicGetIdentifier() {
//		return identifier;
//	}

//	/**
//	 * <!-- begin-user-doc -->
//	 * <!-- end-user-doc -->
//	 * @generated
//	 */
//	public void setIdentifier(DataCellIdentifier newIdentifier) {
//		DataCellIdentifier oldIdentifier = identifier;
//		identifier = newIdentifier;
//		if (eNotificationRequired())
//			eNotify(new ENotificationImpl(this, Notification.SET, RuntimePackage.DATA_CELL__IDENTIFIER, oldIdentifier, identifier));
//	}
	
	public void setIdentifier2(DataCellIdentifier2 newIdentifier) {
		DataCellIdentifier2 oldIdentifier = identifier2;
		identifier2 = newIdentifier;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, RuntimePackage.DATA_CELL__IDENTIFIER, oldIdentifier, identifier2));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getRow() {
		return row;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRow(int newRow) {
		int oldRow = row;
		row = newRow;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, RuntimePackage.DATA_CELL__ROW, oldRow, row));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getCol() {
		return col;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCol(int newCol) {
		int oldCol = col;
		col = newCol;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, RuntimePackage.DATA_CELL__COL, oldCol, col));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Double getResultValue() {
		return resultValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setResultValue(Double newResultValue) {
		Double oldResultValue = resultValue;
		resultValue = newResultValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, RuntimePackage.DATA_CELL__RESULT_VALUE, oldResultValue, resultValue));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setFormat(String newFormat) {
		String oldFormat = format;
		format = newFormat;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, RuntimePackage.DATA_CELL__FORMAT, oldFormat, format));
	}

	public DataCellIdentifier2 basicGetIdentifier() {
		return identifier2;
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case RuntimePackage.DATA_CELL__IDENTIFIER:
				if (resolve) return getIdentifier2();
				return basicGetIdentifier();
			case RuntimePackage.DATA_CELL__ROW:
				return getRow();
			case RuntimePackage.DATA_CELL__COL:
				return getCol();
			case RuntimePackage.DATA_CELL__RESULT_VALUE:
				return getResultValue();
			case RuntimePackage.DATA_CELL__FORMAT:
				return getFormat();
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
			case RuntimePackage.DATA_CELL__IDENTIFIER:
				setIdentifier2((DataCellIdentifier2)newValue);
				return;
			case RuntimePackage.DATA_CELL__ROW:
				setRow((Integer)newValue);
				return;
			case RuntimePackage.DATA_CELL__COL:
				setCol((Integer)newValue);
				return;
			case RuntimePackage.DATA_CELL__RESULT_VALUE:
				setResultValue((Double)newValue);
				return;
			case RuntimePackage.DATA_CELL__FORMAT:
				setFormat((String)newValue);
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
			case RuntimePackage.DATA_CELL__IDENTIFIER:
				setIdentifier2(null);
				return;
			case RuntimePackage.DATA_CELL__ROW:
				setRow(ROW_EDEFAULT);
				return;
			case RuntimePackage.DATA_CELL__COL:
				setCol(COL_EDEFAULT);
				return;
			case RuntimePackage.DATA_CELL__RESULT_VALUE:
				setResultValue(RESULT_VALUE_EDEFAULT);
				return;
			case RuntimePackage.DATA_CELL__FORMAT:
				setFormat(FORMAT_EDEFAULT);
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
			case RuntimePackage.DATA_CELL__IDENTIFIER:
				return identifier2 != null;
			case RuntimePackage.DATA_CELL__ROW:
				return row != ROW_EDEFAULT;
			case RuntimePackage.DATA_CELL__COL:
				return col != COL_EDEFAULT;
			case RuntimePackage.DATA_CELL__RESULT_VALUE:
				return RESULT_VALUE_EDEFAULT == null ? resultValue != null : !RESULT_VALUE_EDEFAULT.equals(resultValue);
			case RuntimePackage.DATA_CELL__FORMAT:
				return FORMAT_EDEFAULT == null ? format != null : !FORMAT_EDEFAULT.equals(format);
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
		result.append(" (row: ");
		result.append(row);
		result.append(", col: ");
		result.append(col);
		result.append(", resultValue: ");
		result.append(resultValue);
		result.append(", format: ");
		result.append(format);
		result.append(')');
		return result.toString();
	}

	private ICalculation calculation;
	
	@Override
	public ICalculation getCalculation() {
		return calculation;
	}

	@Override
	public void setCalculation(ICalculation calculation) {
		this.calculation = calculation;
	}

	private HashMap<String, List<Double>> values;
	
	@Override
	public void addValue(String id, Double value) {
		if(values == null) {
			values = new HashMap<String, List<Double>>();
		}
		
		if(values.get(id) != null) {
			values.get(id).add(value);
		}
		else {
			List<Double> vals = new ArrayList<Double>();
			vals.add(value);
			values.put(id, vals);
		}
	}

	@Override
	public HashMap<String, List<Double>> getValues() {
		return values;
	}

	@Override
	public List<Double> getValuesByKey(String key) {
		if(values != null) {
			return values.get(key);
		}
		return null;
	}

	@Override
	public void setValues(HashMap<String, List<Double>> values) {
		this.values = values;
	}

	private Double persistedValue;
	
	@Override
	public Double getPersistedValue() {
		return persistedValue;
	}

	@Override
	public void persistValue(Double value) {
		this.persistedValue = value;
	}

	@Override
	public void clean() {
//		persistedValue = null;
		if(values != null) {
			values.clear();
		}
		resultValue = null;
	}

//	@Override
//	public String getKey() {
//		return cacheKey;
//	}
//
//	@Override
//	public String getObjectName() {
//		return col + ", " + row + " = " + persistedValue;
//	}
//
//	@Override
//	public String getParentKey() {
//		return parentKey;
//	}
//
//	public void setCacheKeys(String parentKey, String key){
//		setParentKey(parentKey);
//		this.cacheKey = key;
//	}
//
//	@Override
//	public void setParentKey(String parentKey) {
//		this.parentKey = parentKey;
//		
//	}

	private boolean isCalculated = false;
	
	@Override
	public boolean isCalculated() {
		return isCalculated;
	}

	@Override
	public void setCalculated(boolean isCalculated) {
		this.isCalculated = isCalculated;
	}
	
} //DataCellImpl
