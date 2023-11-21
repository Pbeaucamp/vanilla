/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.mdm.model.rules;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see bpm.mdm.model.rules.RulesPackage
 * @generated
 */
public interface RulesFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	RulesFactory eINSTANCE = bpm.mdm.model.rules.impl.RulesFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Range Rule</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Range Rule</em>'.
	 * @generated
	 */
	RangeRule createRangeRule();

	/**
	 * Returns a new object of class '<em>Set Value Rule</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Set Value Rule</em>'.
	 * @generated
	 */
	SetValueRule createSetValueRule();

	/**
	 * Returns a new object of class '<em>Numeric Pattern Rule</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Numeric Pattern Rule</em>'.
	 * @generated
	 */
	NumericPatternRule createNumericPatternRule();

	/**
	 * Returns a new object of class '<em>Date Pattern Rule</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Date Pattern Rule</em>'.
	 * @generated
	 */
	DatePatternRule createDatePatternRule();

	/**
	 * Returns a new object of class '<em>Regex Patter Rule</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Regex Patter Rule</em>'.
	 * @generated
	 */
	RegexPatterRule createRegexPatterRule();

	/**
	 * Returns a new object of class '<em>Entity Link Rule</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Entity Link Rule</em>'.
	 * @generated
	 */
	EntityLinkRule createEntityLinkRule();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	RulesPackage getRulesPackage();

} //RulesFactory
