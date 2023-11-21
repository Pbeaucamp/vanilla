/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.runtime.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import bpm.united.olap.api.model.Dimension;
import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.runtime.DataCell;
import bpm.united.olap.api.runtime.DataCellIdentifier2;
import bpm.united.olap.api.runtime.DataStorage;
import bpm.united.olap.api.runtime.MdxSet;
//import bpm.united.olap.api.runtime.NodeId;
import bpm.united.olap.api.runtime.RunResult;
import bpm.united.olap.api.runtime.RuntimePackage;
import bpm.united.olap.api.runtime.calculation.CalculPercentile;
import bpm.united.olap.api.runtime.calculation.ICalculation;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Data Storage</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link bpm.united.olap.api.runtime.impl.DataStorageImpl#getDataCells <em>Data Cells</em>}</li>
 *   <li>{@link bpm.united.olap.api.runtime.impl.DataStorageImpl#getSets <em>Sets</em>}</li>
 *   <li>{@link bpm.united.olap.api.runtime.impl.DataStorageImpl#getMeasures <em>Measures</em>}</li>
 *   <li>{@link bpm.united.olap.api.runtime.impl.DataStorageImpl#getUsedDimensions <em>Used Dimensions</em>}</li>
 *   <li>{@link bpm.united.olap.api.runtime.impl.DataStorageImpl#isAsMultipleWhere <em>As Multiple Where</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DataStorageImpl extends EObjectImpl implements DataStorage {
	/**
	 * The cached value of the '{@link #getDataCells() <em>Data Cells</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDataCells()
	 * @ordered
	 */
	protected List<DataCell> dataCells;

	/**
	 * The cached value of the '{@link #getSets() <em>Sets</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSets()
	 * @generated
	 * @ordered
	 */
	protected EList<MdxSet> sets;

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
	 * The cached value of the '{@link #getUsedDimensions() <em>Used Dimensions</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUsedDimensions()
	 * @generated
	 * @ordered
	 */
	protected EList<Dimension> usedDimensions;

	/**
	 * The default value of the '{@link #isAsMultipleWhere() <em>As Multiple Where</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isAsMultipleWhere()
	 * @generated
	 * @ordered
	 */
	protected static final boolean AS_MULTIPLE_WHERE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isAsMultipleWhere() <em>As Multiple Where</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isAsMultipleWhere()
	 * @generated
	 * @ordered
	 */
	protected boolean asMultipleWhere = AS_MULTIPLE_WHERE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DataStorageImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return RuntimePackage.Literals.DATA_STORAGE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<DataCell> getDataCells() {
		if (dataCells == null) {
			dataCells = new EObjectResolvingEList<DataCell>(DataCell.class, this, RuntimePackage.DATA_STORAGE__DATA_CELLS);
		}
		return dataCells;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<MdxSet> getSets() {
		if (sets == null) {
			sets = new EObjectResolvingEList<MdxSet>(MdxSet.class, this, RuntimePackage.DATA_STORAGE__SETS);
		}
		return sets;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<Measure> getMeasures() {
		if (measures == null) {
			measures = new EObjectResolvingEList<Measure>(Measure.class, this, RuntimePackage.DATA_STORAGE__MEASURES);
		}
		return measures;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<Dimension> getUsedDimensions() {
		if (usedDimensions == null) {
			usedDimensions = new EObjectResolvingEList<Dimension>(Dimension.class, this, RuntimePackage.DATA_STORAGE__USED_DIMENSIONS);
		}
		return usedDimensions;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isAsMultipleWhere() {
		return asMultipleWhere;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAsMultipleWhere(boolean newAsMultipleWhere) {
		boolean oldAsMultipleWhere = asMultipleWhere;
		asMultipleWhere = newAsMultipleWhere;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, RuntimePackage.DATA_STORAGE__AS_MULTIPLE_WHERE, oldAsMultipleWhere, asMultipleWhere));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case RuntimePackage.DATA_STORAGE__DATA_CELLS:
				return getDataCells();
			case RuntimePackage.DATA_STORAGE__SETS:
				return getSets();
			case RuntimePackage.DATA_STORAGE__MEASURES:
				return getMeasures();
			case RuntimePackage.DATA_STORAGE__USED_DIMENSIONS:
				return getUsedDimensions();
			case RuntimePackage.DATA_STORAGE__AS_MULTIPLE_WHERE:
				return isAsMultipleWhere();
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
			case RuntimePackage.DATA_STORAGE__DATA_CELLS:
				getDataCells().clear();
				getDataCells().addAll((Collection<? extends DataCell>)newValue);
				return;
			case RuntimePackage.DATA_STORAGE__SETS:
				getSets().clear();
				getSets().addAll((Collection<? extends MdxSet>)newValue);
				return;
			case RuntimePackage.DATA_STORAGE__MEASURES:
				getMeasures().clear();
				getMeasures().addAll((Collection<? extends Measure>)newValue);
				return;
			case RuntimePackage.DATA_STORAGE__USED_DIMENSIONS:
				getUsedDimensions().clear();
				getUsedDimensions().addAll((Collection<? extends Dimension>)newValue);
				return;
			case RuntimePackage.DATA_STORAGE__AS_MULTIPLE_WHERE:
				setAsMultipleWhere((Boolean)newValue);
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
			case RuntimePackage.DATA_STORAGE__DATA_CELLS:
				getDataCells().clear();
				return;
			case RuntimePackage.DATA_STORAGE__SETS:
				getSets().clear();
				return;
			case RuntimePackage.DATA_STORAGE__MEASURES:
				getMeasures().clear();
				return;
			case RuntimePackage.DATA_STORAGE__USED_DIMENSIONS:
				getUsedDimensions().clear();
				return;
			case RuntimePackage.DATA_STORAGE__AS_MULTIPLE_WHERE:
				setAsMultipleWhere(AS_MULTIPLE_WHERE_EDEFAULT);
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
			case RuntimePackage.DATA_STORAGE__DATA_CELLS:
				return dataCells != null && !dataCells.isEmpty();
			case RuntimePackage.DATA_STORAGE__SETS:
				return sets != null && !sets.isEmpty();
			case RuntimePackage.DATA_STORAGE__MEASURES:
				return measures != null && !measures.isEmpty();
			case RuntimePackage.DATA_STORAGE__USED_DIMENSIONS:
				return usedDimensions != null && !usedDimensions.isEmpty();
			case RuntimePackage.DATA_STORAGE__AS_MULTIPLE_WHERE:
				return asMultipleWhere != AS_MULTIPLE_WHERE_EDEFAULT;
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
		result.append(" (asMultipleWhere: ");
		result.append(asMultipleWhere);
		result.append(')');
		return result.toString();
	}

	@Override
	public void makeCalcul(RunResult runResult) {
		for(DataCell cell : dataCells) {
			if(!cell.isCalculated()) {
				cell.getCalculation().makeCalcul();
				if(cell.getCalculation().isPercentile()) {
					cell.setResultValue(CalculPercentile.getPercentileValue(cell, runResult));
				}
			}
		}
		for(ICalculation calc : calculcations) {
			calc.makeCalcul();
		}
	}

	private HashMap<DataCellIdentifier2, List<DataCellIdentifier2>>  possibleIds;
	
	@Override
	public HashMap<DataCellIdentifier2, List<DataCellIdentifier2>>  getPossibleIds() {
		return possibleIds;
	}

	@Override
	public void setPossibleIds(HashMap<DataCellIdentifier2, List<DataCellIdentifier2>> possibleIds) {
		this.possibleIds = possibleIds;
	}

	private List<ICalculation> calculcations = new ArrayList<ICalculation>();
	
	@Override
	public List<ICalculation> getCalculations() {
		return calculcations;
	}

//	private HashMap<String, List<NodeId>> orderValues = new HashMap<String, List<NodeId>>();
//	
//	@Override
//	public String getOrderValue(NodeId searchedNode) {
//		if(orderValues.containsKey(searchedNode.getId())) {
//			List<NodeId> nodes = orderValues.get(searchedNode.getId());
//			for(NodeId node : nodes) {
//				if(node.getValue().equals(searchedNode.getValue())) {
//					return node.getOrderValue();
//				}
//			}
//		}
//		return null;
//	}
//
//	@Override
//	public void setOrderValue(NodeId searchedNode) {
//		if(searchedNode.getOrderValue() == null) {
//			return;
//		}
//		
//		if(orderValues.containsKey(searchedNode.getId())) {
//			orderValues.get(searchedNode.getId()).add(searchedNode);
//		}
//		else {
//			List<NodeId> nodes = new ArrayList<NodeId>();
//			nodes.add(searchedNode);
//			orderValues.put(searchedNode.getId(), nodes);
//		}
//	}

	private boolean isMeasureRow;
	
	@Override
	public boolean getIsMeasureRow() {
		return isMeasureRow;
	}

	@Override
	public void setIsMeasureRow(boolean isRow) {
		isMeasureRow = isRow;
	}

	private List<Dimension> dimensionDrilled = new ArrayList<Dimension>();
	
	@Override
	public void addDimensionDrilled(Dimension dim) {
		dimensionDrilled.add(dim);
	}

	@Override
	public List<Dimension> getDimensionsDrilled() {
		return dimensionDrilled;
	}

	@Override
	public void setDimensionsDrilled(List<Dimension> dims) {
		dimensionDrilled = dims;
	}

	private List<Hierarchy> drilledHierarchies = new ArrayList<Hierarchy>();
	
//	private List<Hierarchy> usedHierarchies = new ArrayList<Hierarchy>();
	
	@Override
	public void addDrilledHierarchy(Hierarchy hierarchy) {
		if(drilledHierarchies == null) {
			drilledHierarchies = new ArrayList<Hierarchy>();
		}
		drilledHierarchies.add(hierarchy);
	}

	@Override
	public void addUsedHierarchy(Hierarchy hierachy) {
//		if(usedHierarchies == null) {
//			usedHierarchies = new ArrayList<Hierarchy>();
//		}
//		usedHierarchies.add(hierachy);
	}

	@Override
	public List<Hierarchy> getDrilledHierarchies() {
		return drilledHierarchies;
	}

	@Override
	public List<Hierarchy> getUsedHierarchies() {
		return null;
	}

	@Override
	public void setDrilledHierarchies(List<Hierarchy> hierarchies) {
		drilledHierarchies = hierarchies;
	}

	@Override
	public void setUsedHierarchies(List<Hierarchy> hierarchies) {
//		usedHierarchies = hierarchies;
	}

	private int nbCol;
	
	@Override
	public int getNbCol() {
		return nbCol;
	}

	@Override
	public void setNbCol(int nbCol) {
		this.nbCol = nbCol;
	}

	private int maxNbCol;
	
	@Override
	public int getMaxNbCol() {
		return maxNbCol;
	}

	@Override
	public void setMaxNbCol(int size) {
		this.maxNbCol = size;
	}

	@Override
	public void add(DataCell cell) {
		dataCells.add(cell);
		
	}

	@Override
	public void initSize(int rowNumber, int colNumber) {
		dataCells = new ArrayList<DataCell>(rowNumber * colNumber);
		
	}
} //DataStorageImpl
