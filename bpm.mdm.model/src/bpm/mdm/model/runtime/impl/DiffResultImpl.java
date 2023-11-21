/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.mdm.model.runtime.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import bpm.mdm.model.Rule;
import bpm.mdm.model.runtime.DiffResult;
import bpm.mdm.model.runtime.Row;
import bpm.mdm.model.runtime.RowState;
import bpm.mdm.model.runtime.RuntimePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Diff Result</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class DiffResultImpl extends EObjectImpl implements DiffResult {
	
	private class RowInfo{
		private RowState state;
		private Row originalRow;
		private List<Rule> unsatisfiedRules;
		public RowInfo(RowState state, List<Rule> unsatisfiedRules, Row originalRow){
			this.state = state;
			this.unsatisfiedRules = unsatisfiedRules;
			this.originalRow = originalRow;
		}
	}
	
	
	private HashMap<Row, RowInfo> rows = new HashMap<Row, RowInfo>();
	
	private int readNumber = 0;
	private int discardedNumber = 0;
	private int newNumber = 0;
	private int updateNumber = 0;
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DiffResultImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return RuntimePackage.Literals.DIFF_RESULT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public RowState getState(Row row) {
		RowInfo r = rows.get(row);
		if (r==null){
			return null;
		}
		return r.state;
	}



	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public void addRow(Row row, RowState state, List<Rule> unsatisfiedRules, Row originalRow) {
		switch (state) {
		case DISCARD:
			discardedNumber++;
			break;
		case NEW:
			newNumber++;
			break;
		case UPDATE:
			updateNumber++;
			break;
		default:
			return;
		}
		rows.put(row, new RowInfo(state, unsatisfiedRules, originalRow));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public List<Row> getRows() {
		return new ArrayList<Row>(rows.keySet());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public Row getOriginalRow(Row row) {
		RowInfo r = rows.get(row);
		if (r == null){
			return null;
		}
		return r.originalRow;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public int getNewNumber() {
		return newNumber;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public int getUpdateNumber() {
		return updateNumber;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public int getDiscardNumber() {
		return discardedNumber;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public int getReadNumber() {
		return readNumber;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public void incrementRead() {
		readNumber++;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public List<Rule> getUnsatisfiedRules(Row row) {
		RowInfo r = rows.get(row);
		if (r==null){
			return null;
		}
		return r.unsatisfiedRules;
	}

} //DiffResultImpl
