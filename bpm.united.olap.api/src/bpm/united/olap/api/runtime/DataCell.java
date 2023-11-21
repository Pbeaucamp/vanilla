/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.runtime;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.eclipse.emf.ecore.EObject;


import bpm.united.olap.api.runtime.calculation.ICalculation;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Data Cell</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link bpm.united.olap.api.runtime.DataCell#getIdentifier <em>Identifier</em>}</li>
 *   <li>{@link bpm.united.olap.api.runtime.DataCell#getRow <em>Row</em>}</li>
 *   <li>{@link bpm.united.olap.api.runtime.DataCell#getCol <em>Col</em>}</li>
 *   <li>{@link bpm.united.olap.api.runtime.DataCell#getResultValue <em>Result Value</em>}</li>
 *   <li>{@link bpm.united.olap.api.runtime.DataCell#getFormat <em>Format</em>}</li>
 * </ul>
 * </p>
 *
 * @see bpm.united.olap.api.runtime.RuntimePackage#getDataCell()
 * @model
 * @generated
 */
public interface DataCell extends EObject,Serializable {
	/**
	 * Returns the value of the '<em><b>Identifier</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Identifier</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Identifier</em>' reference.
	 * @see #setIdentifier(DataCellIdentifier)
	 * @see bpm.united.olap.api.runtime.RuntimePackage#getDataCell_Identifier()
	 * @model
	 * @generated
	 */
	DataCellIdentifier2 getIdentifier2();
//	DataCellIdentifier getIdentifier();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.runtime.DataCell#getIdentifier <em>Identifier</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Identifier</em>' reference.
	 * @see #getIdentifier()
	 * @generated
	 */
	void setIdentifier2(DataCellIdentifier2 value);
//	void setIdentifier(DataCellIdentifier value);

	/**
	 * Returns the value of the '<em><b>Row</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Row</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Row</em>' attribute.
	 * @see #setRow(int)
	 * @see bpm.united.olap.api.runtime.RuntimePackage#getDataCell_Row()
	 * @model
	 * @generated
	 */
	int getRow();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.runtime.DataCell#getRow <em>Row</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Row</em>' attribute.
	 * @see #getRow()
	 * @generated
	 */
	void setRow(int value);

	/**
	 * Returns the value of the '<em><b>Col</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Col</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Col</em>' attribute.
	 * @see #setCol(int)
	 * @see bpm.united.olap.api.runtime.RuntimePackage#getDataCell_Col()
	 * @model
	 * @generated
	 */
	int getCol();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.runtime.DataCell#getCol <em>Col</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Col</em>' attribute.
	 * @see #getCol()
	 * @generated
	 */
	void setCol(int value);

	/**
	 * Returns the value of the '<em><b>Result Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Result Value</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Result Value</em>' attribute.
	 * @see #setResultValue(Double)
	 * @see bpm.united.olap.api.runtime.RuntimePackage#getDataCell_ResultValue()
	 * @model
	 * @generated
	 */
	Double getResultValue();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.runtime.DataCell#getResultValue <em>Result Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Result Value</em>' attribute.
	 * @see #getResultValue()
	 * @generated
	 */
	void setResultValue(Double value);

	/**
	 * Returns the value of the '<em><b>Format</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Format</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Format</em>' attribute.
	 * @see #setFormat(String)
	 * @see bpm.united.olap.api.runtime.RuntimePackage#getDataCell_Format()
	 * @model
	 * @generated
	 */
	String getFormat();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.runtime.DataCell#getFormat <em>Format</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Format</em>' attribute.
	 * @see #getFormat()
	 * @generated
	 */
	void setFormat(String value);

	void setCalculation(ICalculation calculation);
	
	ICalculation getCalculation();
	
	void addValue(String id, Double value);
	
	HashMap<String, List<Double>> getValues();
	
	List<Double> getValuesByKey(String key);

	void setValues(HashMap<String, List<Double>> values);
	
	void persistValue(Double value);
	
	Double getPersistedValue();

	void clean();
	
//	/**
//	 * set the keys for cache System
//	 * @param parentKey
//	 * @param key
//	 */
//	public void setCacheKeys(String parentKey, String key);
	
	boolean isCalculated();
	
	void setCalculated(boolean isCalculated);
} // DataCell
