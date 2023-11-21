/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.runtime;

import java.io.Serializable;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Run Result</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link bpm.united.olap.api.runtime.RunResult#getDataStorage <em>Data Storage</em>}</li>
 *   <li>{@link bpm.united.olap.api.runtime.RunResult#isIsNonEmpty <em>Is Non Empty</em>}</li>
 * </ul>
 * </p>
 *
 * @see bpm.united.olap.api.runtime.RuntimePackage#getRunResult()
 * @model
 * @generated
 */
public interface RunResult extends EObject,Serializable {
	/**
	 * Returns the value of the '<em><b>Data Storage</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Data Storage</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Data Storage</em>' reference.
	 * @see #setDataStorage(DataStorage)
	 * @see bpm.united.olap.api.runtime.RuntimePackage#getRunResult_DataStorage()
	 * @model
	 * @generated
	 */
	DataStorage getDataStorage();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.runtime.RunResult#getDataStorage <em>Data Storage</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Data Storage</em>' reference.
	 * @see #getDataStorage()
	 * @generated
	 */
	void setDataStorage(DataStorage value);
	
	/**
	 * Returns the value of the '<em><b>Is Non Empty</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Is Non Empty</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Is Non Empty</em>' attribute.
	 * @see #setIsNonEmpty(boolean)
	 * @see bpm.united.olap.api.runtime.RuntimePackage#getRunResult_IsNonEmpty()
	 * @model
	 * @generated
	 */
	boolean isIsNonEmpty();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.runtime.RunResult#isIsNonEmpty <em>Is Non Empty</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Is Non Empty</em>' attribute.
	 * @see #isIsNonEmpty()
	 * @generated
	 */
	void setIsNonEmpty(boolean value);

	void setMdxSets(List<List<MdxSet>> sets);
	
	List<List<MdxSet>> getMdxSets();
	
	void setWhereSets(List<MdxSet> sets);
	
	List<MdxSet> getWhereSets();
	
	void setIsMeasureRow(boolean isRow);
	
	boolean getIsMeasureRow();

} // RunResult
