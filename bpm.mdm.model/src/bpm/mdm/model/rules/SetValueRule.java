/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.mdm.model.rules;

import bpm.mdm.model.Rule;

import java.util.List;
import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Set Value Rule</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * This Rule is to use for Attributes that must belong to an Enumeration.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link bpm.mdm.model.rules.SetValueRule#getAuthorizedValues <em>Authorized Values</em>}</li>
 * </ul>
 * </p>
 *
 * @see bpm.mdm.model.rules.RulesPackage#getSetValueRule()
 * @model
 * @generated
 */
public interface SetValueRule extends Rule {
	/**
	 * Returns the value of the '<em><b>Authorized Values</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.Object}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Authorized Values</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Authorized Values</em>' attribute list.
	 * @see bpm.mdm.model.rules.RulesPackage#getSetValueRule_AuthorizedValues()
	 * @model
	 * @generated
	 */
	List<Object> getAuthorizedValues();

} // SetValueRule
