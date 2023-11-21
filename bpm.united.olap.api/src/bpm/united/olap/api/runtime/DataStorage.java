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

import bpm.united.olap.api.model.Dimension;
import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.runtime.calculation.ICalculation;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Data Storage</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link bpm.united.olap.api.runtime.DataStorage#getDataCells <em>Data Cells</em>}</li>
 *   <li>{@link bpm.united.olap.api.runtime.DataStorage#getSets <em>Sets</em>}</li>
 *   <li>{@link bpm.united.olap.api.runtime.DataStorage#getMeasures <em>Measures</em>}</li>
 *   <li>{@link bpm.united.olap.api.runtime.DataStorage#getUsedDimensions <em>Used Dimensions</em>}</li>
 *   <li>{@link bpm.united.olap.api.runtime.DataStorage#isAsMultipleWhere <em>As Multiple Where</em>}</li>
 * </ul>
 * </p>
 *
 * @see bpm.united.olap.api.runtime.RuntimePackage#getDataStorage()
 * @model
 * @generated
 */
public interface DataStorage extends EObject, Serializable {
	/**
	 * Returns the value of the '<em><b>Data Cells</b></em>' reference list.
	 * The list contents are of type {@link bpm.united.olap.api.runtime.DataCell}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Data Cells</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Data Cells</em>' reference list.
	 * @see bpm.united.olap.api.runtime.RuntimePackage#getDataStorage_DataCells()
	 * @model
	 * @generated
	 */
	List<DataCell> getDataCells();

	/**
	 * Returns the value of the '<em><b>Sets</b></em>' reference list.
	 * The list contents are of type {@link bpm.united.olap.api.runtime.MdxSet}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sets</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sets</em>' reference list.
	 * @see bpm.united.olap.api.runtime.RuntimePackage#getDataStorage_Sets()
	 * @model
	 * @generated
	 */
	List<MdxSet> getSets();

	/**
	 * Returns the value of the '<em><b>Measures</b></em>' reference list.
	 * The list contents are of type {@link bpm.united.olap.api.model.Measure}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Measures</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Measures</em>' reference list.
	 * @see bpm.united.olap.api.runtime.RuntimePackage#getDataStorage_Measures()
	 * @model
	 * @generated
	 */
	List<Measure> getMeasures();
	
	/**
	 * Returns the value of the '<em><b>Used Dimensions</b></em>' reference list.
	 * The list contents are of type {@link bpm.united.olap.api.model.Dimension}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Used Dimensions</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Used Dimensions</em>' reference list.
	 * @see bpm.united.olap.api.runtime.RuntimePackage#getDataStorage_UsedDimensions()
	 * @model
	 * @generated
	 */
//	List<Dimension> getUsedDimensions();

	/**
	 * Returns the value of the '<em><b>As Multiple Where</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>As Multiple Where</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>As Multiple Where</em>' attribute.
	 * @see #setAsMultipleWhere(boolean)
	 * @see bpm.united.olap.api.runtime.RuntimePackage#getDataStorage_AsMultipleWhere()
	 * @model
	 * @generated
	 */
	boolean isAsMultipleWhere();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.runtime.DataStorage#isAsMultipleWhere <em>As Multiple Where</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>As Multiple Where</em>' attribute.
	 * @see #isAsMultipleWhere()
	 * @generated
	 */
	void setAsMultipleWhere(boolean value);

	void makeCalcul(RunResult runResult);
	
	HashMap<DataCellIdentifier2, List<DataCellIdentifier2>> getPossibleIds();
	
	void setPossibleIds(HashMap<DataCellIdentifier2, List<DataCellIdentifier2>> possibleIds);
	
	List<ICalculation> getCalculations();

	/**
	 * Get the order value for this node
	 * @param node
	 * @return
	 */
//	String getOrderValue(NodeId node);
	
	/**
	 * Set the orderValue for this node
	 * @param node
	 * @param orderValue
	 */
//	void setOrderValue(NodeId node);
	
	void setIsMeasureRow(boolean isRow);
	
	boolean getIsMeasureRow();
	
	void setDimensionsDrilled(List<Dimension> dims);
	
	void addDimensionDrilled(Dimension dim);
	
	List<Dimension> getDimensionsDrilled();
	
	void setUsedHierarchies(List<Hierarchy> hierarchies);
	
	void addUsedHierarchy(Hierarchy hierachy);
	
	List<Hierarchy> getUsedHierarchies();
	
	void setDrilledHierarchies(List<Hierarchy> hierarchies);
	
	void addDrilledHierarchy(Hierarchy hierarchy);
	
	List<Hierarchy> getDrilledHierarchies();
	
	int getNbCol();
	
	void setNbCol(int nbCol);

	void setMaxNbCol(int size);
	
	int getMaxNbCol();

	void add(DataCell cell);

	void initSize(int rowNumber, int colNumber);
} // DataStorage
