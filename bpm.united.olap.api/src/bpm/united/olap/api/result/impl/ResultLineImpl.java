/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.result.impl;

import bpm.united.olap.api.result.ResultCell;
import bpm.united.olap.api.result.ResultLine;
import bpm.united.olap.api.result.ResultPackage;

import java.util.Collection;

import java.util.List;
import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Line</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link bpm.united.olap.api.result.impl.ResultLineImpl#getCells <em>Cells</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ResultLineImpl extends EObjectImpl implements ResultLine {
	/**
	 * The cached value of the '{@link #getCells() <em>Cells</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCells()
	 * @generated
	 * @ordered
	 */
	protected EList<ResultCell> cells;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ResultLineImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ResultPackage.Literals.RESULT_LINE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<ResultCell> getCells() {
		if (cells == null) {
			cells = new EObjectResolvingEList<ResultCell>(ResultCell.class, this, ResultPackage.RESULT_LINE__CELLS);
		}
		return cells;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ResultPackage.RESULT_LINE__CELLS:
				return getCells();
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
			case ResultPackage.RESULT_LINE__CELLS:
				getCells().clear();
				getCells().addAll((Collection<? extends ResultCell>)newValue);
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
			case ResultPackage.RESULT_LINE__CELLS:
				getCells().clear();
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
			case ResultPackage.RESULT_LINE__CELLS:
				return cells != null && !cells.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	@Override
	public String getHtml() {
		String html = "<tr>\n";
		
		for(ResultCell cell : cells) {
			html += "			" + cell.getHtml();
		}
		
		html += "		</tr>\n";
		return html;
	}

} //ResultLineImpl
