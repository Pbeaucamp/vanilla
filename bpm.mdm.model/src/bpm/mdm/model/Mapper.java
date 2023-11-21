/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.mdm.model;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Mapper</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see bpm.mdm.model.MdmPackage#getMapper()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface Mapper extends EObject {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	Attribute getAttribute(int dataSourceField);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	boolean mapAttributeWithField(Attribute attribute, int dataSourceField);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	int getDataSourceField(Attribute attribute);

} // Mapper
