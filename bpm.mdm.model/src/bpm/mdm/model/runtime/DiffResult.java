/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.mdm.model.runtime;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

import bpm.mdm.model.Rule;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Diff Result</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see bpm.mdm.model.runtime.RuntimePackage#getDiffResult()
 * @model
 * @generated
 */
public interface DiffResult extends EObject {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	RowState getState(Row row);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model insatisfiedRulesMany="true"
	 * @generated
	 */
	void addRow(Row row, RowState state, List<Rule> insatisfiedRules, Row originalRow);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	List<Row> getRows();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	Row getOriginalRow(Row row);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	int getNewNumber();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	int getUpdateNumber();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	int getDiscardNumber();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	int getReadNumber();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	void incrementRead();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	List<Rule> getUnsatisfiedRules(Row row);

} // DiffResult
