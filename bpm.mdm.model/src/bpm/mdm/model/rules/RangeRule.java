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
 * A representation of the model object '<em><b>Range Rule</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link bpm.mdm.model.rules.RangeRule#getMinValue <em>Min Value</em>}</li>
 *   <li>{@link bpm.mdm.model.rules.RangeRule#getMaxValue <em>Max Value</em>}</li>
 *   <li>{@link bpm.mdm.model.rules.RangeRule#isIncludeMin <em>Include Min</em>}</li>
 *   <li>{@link bpm.mdm.model.rules.RangeRule#isIncludeMax <em>Include Max</em>}</li>
 * </ul>
 * </p>
 *
 * @see bpm.mdm.model.rules.RulesPackage#getRangeRule()
 * @model
 * @generated
 */
public interface RangeRule extends Rule {
	/**
	 * Returns the value of the '<em><b>Min Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Min Value</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Min Value</em>' attribute.
	 * @see #setMinValue(long)
	 * @see bpm.mdm.model.rules.RulesPackage#getRangeRule_MinValue()
	 * @model
	 * @generated
	 */
	long getMinValue();

	/**
	 * Sets the value of the '{@link bpm.mdm.model.rules.RangeRule#getMinValue <em>Min Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Min Value</em>' attribute.
	 * @see #getMinValue()
	 * @generated
	 */
	void setMinValue(long value);

	/**
	 * Returns the value of the '<em><b>Max Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Max Value</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Max Value</em>' attribute.
	 * @see #setMaxValue(long)
	 * @see bpm.mdm.model.rules.RulesPackage#getRangeRule_MaxValue()
	 * @model
	 * @generated
	 */
	long getMaxValue();

	/**
	 * Sets the value of the '{@link bpm.mdm.model.rules.RangeRule#getMaxValue <em>Max Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Max Value</em>' attribute.
	 * @see #getMaxValue()
	 * @generated
	 */
	void setMaxValue(long value);

	/**
	 * Returns the value of the '<em><b>Include Min</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * This interface is used to restrict a range of values for numerical attributes.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Include Min</em>' attribute.
	 * @see #setIncludeMin(boolean)
	 * @see bpm.mdm.model.rules.RulesPackage#getRangeRule_IncludeMin()
	 * @model
	 * @generated
	 */
	boolean isIncludeMin();

	/**
	 * Sets the value of the '{@link bpm.mdm.model.rules.RangeRule#isIncludeMin <em>Include Min</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Include Min</em>' attribute.
	 * @see #isIncludeMin()
	 * @generated
	 */
	void setIncludeMin(boolean value);

	/**
	 * Returns the value of the '<em><b>Include Max</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Include Max</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Include Max</em>' attribute.
	 * @see #setIncludeMax(boolean)
	 * @see bpm.mdm.model.rules.RulesPackage#getRangeRule_IncludeMax()
	 * @model
	 * @generated
	 */
	boolean isIncludeMax();

	/**
	 * Sets the value of the '{@link bpm.mdm.model.rules.RangeRule#isIncludeMax <em>Include Max</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Include Max</em>' attribute.
	 * @see #isIncludeMax()
	 * @generated
	 */
	void setIncludeMax(boolean value);

} // RangeRule
