/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.mdm.model.runtime;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

import bpm.mdm.model.Attribute;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Row</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see bpm.mdm.model.runtime.RuntimePackage#getRow()
 * @model
 * @generated
 */
public interface Row extends EObject {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	Object getValue(Attribute attribute);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	void setValue(Attribute attribute, Object value);

	/**
	 * 
	 * @param pkAttribute : the primaryKey attributes to perform the comparison
	 * @param row : the row to compare
	 * @return if this has the same id as the given row
	 * 
	 * if pkAttribute is empty, it should always return false
	 */
	boolean match(List<Attribute> pkAttribute, Row row);

} // Row
