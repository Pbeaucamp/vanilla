/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.mdm.model.rules;

import bpm.mdm.model.Rule;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Pattern Rule</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link bpm.mdm.model.rules.PatternRule#getPattern <em>Pattern</em>}</li>
 * </ul>
 * </p>
 *
 * @see bpm.mdm.model.rules.RulesPackage#getPatternRule()
 * @model abstract="true"
 * @generated
 */
public interface PatternRule extends Rule {
	/**
	 * Returns the value of the '<em><b>Pattern</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Pattern</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Pattern</em>' attribute.
	 * @see #setPattern(String)
	 * @see bpm.mdm.model.rules.RulesPackage#getPatternRule_Pattern()
	 * @model
	 * @generated
	 */
	String getPattern();

	/**
	 * Sets the value of the '{@link bpm.mdm.model.rules.PatternRule#getPattern <em>Pattern</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Pattern</em>' attribute.
	 * @see #getPattern()
	 * @generated
	 */
	void setPattern(String value);

} // PatternRule
