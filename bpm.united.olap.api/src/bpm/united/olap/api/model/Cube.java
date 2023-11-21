/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.model;

import java.io.Serializable;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import bpm.united.olap.api.datasource.DataObject;
import bpm.united.olap.api.model.impl.SchemaImpl;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Cube</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link bpm.united.olap.api.model.Cube#getMeasures <em>Measures</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.Cube#getDimensions <em>Dimensions</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.Cube#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see bpm.united.olap.api.model.ModelPackage#getCube()
 * @model
 * @generated
 */
public interface Cube extends EObject, Serializable {
	/**
	 * Returns the value of the '<em><b>Measures</b></em>' reference list.
	 * The list contents are of type {@link bpm.united.olap.api.model.Measure}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Measures</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Measures</em>' reference list.
	 * @see bpm.united.olap.api.model.ModelPackage#getCube_Measures()
	 * @model
	 * @generated
	 */
	List<Measure> getMeasures();

	/**
	 * Returns the value of the '<em><b>Dimensions</b></em>' reference list.
	 * The list contents are of type {@link bpm.united.olap.api.model.Dimension}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Dimensions</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Dimensions</em>' reference list.
	 * @see bpm.united.olap.api.model.ModelPackage#getCube_Dimensions()
	 * @model
	 * @generated
	 */
	List<Dimension> getDimensions();

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see bpm.united.olap.api.model.ModelPackage#getCube_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.model.Cube#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);
	
	Schema getParentSchema();

	void setParentSchema(Schema schema);
	
	DataObject getFactTable();
	
	void setFactTable(DataObject factTable);

} // Cube
