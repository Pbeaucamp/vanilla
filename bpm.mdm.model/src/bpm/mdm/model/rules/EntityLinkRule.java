/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.mdm.model.rules;

import bpm.mdm.model.Attribute;
import bpm.mdm.model.Rule;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Entity Link Rule</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link bpm.mdm.model.rules.EntityLinkRule#getLinkedAttribute <em>Linked Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @see bpm.mdm.model.rules.RulesPackage#getEntityLinkRule()
 * @model
 * @generated
 */
public interface EntityLinkRule extends Rule {
	/**
	 * Returns the value of the '<em><b>Linked Attribute</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Linked Attribute</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Linked Attribute</em>' reference.
	 * @see #setLinkedAttribute(Attribute)
	 * @see bpm.mdm.model.rules.RulesPackage#getEntityLinkRule_LinkedAttribute()
	 * @model
	 * @generated
	 */
	Attribute getLinkedAttribute();

	/**
	 * Sets the value of the '{@link bpm.mdm.model.rules.EntityLinkRule#getLinkedAttribute <em>Linked Attribute</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Linked Attribute</em>' reference.
	 * @see #getLinkedAttribute()
	 * @generated
	 */
	void setLinkedAttribute(Attribute value);

} // EntityLinkRule
