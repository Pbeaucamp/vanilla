/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.runtime;

import java.io.Serializable;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import bpm.united.olap.api.model.ElementDefinition;
import bpm.united.olap.api.model.Measure;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Mdx Set</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link bpm.united.olap.api.runtime.MdxSet#getElements <em>Elements</em>}</li>
 *   <li>{@link bpm.united.olap.api.runtime.MdxSet#isIsVisible <em>Is Visible</em>}</li>
 * </ul>
 * </p>
 *
 * @see bpm.united.olap.api.runtime.RuntimePackage#getMdxSet()
 * @model
 * @generated
 */
public interface MdxSet extends EObject, Serializable {
	/**
	 * Returns the value of the '<em><b>Elements</b></em>' reference list.
	 * The list contents are of type {@link bpm.united.olap.api.model.ElementDefinition}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Elements</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Elements</em>' reference list.
	 * @see bpm.united.olap.api.runtime.RuntimePackage#getMdxSet_Elements()
	 * @model
	 * @generated
	 */
	List<ElementDefinition> getElements();
	
	Measure getMeasure();

	void setMeasure(Measure measure);
	
	/**
	 * Returns the value of the '<em><b>Is Visible</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Is Visible</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Is Visible</em>' attribute.
	 * @see #setIsVisible(boolean)
	 * @see bpm.united.olap.api.runtime.RuntimePackage#getMdxSet_IsVisible()
	 * @model
	 * @generated
	 */
	boolean isIsVisible();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.runtime.MdxSet#isIsVisible <em>Is Visible</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Is Visible</em>' attribute.
	 * @see #isIsVisible()
	 * @generated
	 */
	void setIsVisible(boolean value);

} // MdxSet
