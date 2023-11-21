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

import bpm.united.olap.api.datasource.Datasource;
import java.util.Date;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Schema</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link bpm.united.olap.api.model.Schema#getCubes <em>Cubes</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.Schema#getDatasources <em>Datasources</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.Schema#getName <em>Name</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.Schema#getDimensions <em>Dimensions</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.Schema#getMeasures <em>Measures</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.Schema#getId <em>Id</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.Schema#getLastModificationDate <em>Last Modification Date</em>}</li>
 * </ul>
 * </p>
 *
 * @see bpm.united.olap.api.model.ModelPackage#getSchema()
 * @model
 * @generated
 */
public interface Schema extends EObject, Serializable {
	/**
	 * Returns the value of the '<em><b>Cubes</b></em>' containment reference list.
	 * The list contents are of type {@link bpm.united.olap.api.model.Cube}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Cubes</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Cubes</em>' containment reference list.
	 * @see bpm.united.olap.api.model.ModelPackage#getSchema_Cubes()
	 * @model containment="true"
	 * @generated
	 */
	List<Cube> getCubes();

	/**
	 * Returns the value of the '<em><b>Datasources</b></em>' containment reference list.
	 * The list contents are of type {@link bpm.united.olap.api.datasource.Datasource}.
	 * It is bidirectional and its opposite is '{@link bpm.united.olap.api.datasource.Datasource#getParent <em>Parent</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Datasources</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Datasources</em>' containment reference list.
	 * @see bpm.united.olap.api.model.ModelPackage#getSchema_Datasources()
	 * @see bpm.united.olap.api.datasource.Datasource#getParent
	 * @model opposite="parent" containment="true"
	 * @generated
	 */
	List<Datasource> getDatasources();

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
	 * @see bpm.united.olap.api.model.ModelPackage#getSchema_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.model.Schema#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Dimensions</b></em>' containment reference list.
	 * The list contents are of type {@link bpm.united.olap.api.model.Dimension}.
	 * It is bidirectional and its opposite is '{@link bpm.united.olap.api.model.Dimension#getParentSchema <em>Parent Schema</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Dimensions</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Dimensions</em>' containment reference list.
	 * @see bpm.united.olap.api.model.ModelPackage#getSchema_Dimensions()
	 * @see bpm.united.olap.api.model.Dimension#getParentSchema
	 * @model opposite="parentSchema" containment="true"
	 * @generated
	 */
	List<Dimension> getDimensions();

	/**
	 * Returns the value of the '<em><b>Measures</b></em>' containment reference list.
	 * The list contents are of type {@link bpm.united.olap.api.model.Measure}.
	 * It is bidirectional and its opposite is '{@link bpm.united.olap.api.model.Measure#getParentSchema <em>Parent Schema</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Measures</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Measures</em>' containment reference list.
	 * @see bpm.united.olap.api.model.ModelPackage#getSchema_Measures()
	 * @see bpm.united.olap.api.model.Measure#getParentSchema
	 * @model opposite="parentSchema" containment="true"
	 * @generated
	 */
	List<Measure> getMeasures();

	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(Integer)
	 * @see bpm.united.olap.api.model.ModelPackage#getSchema_Id()
	 * @model
	 */
	String getId();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.model.Schema#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 */
	void setId(String value);

	/**
	 * Returns the value of the '<em><b>Last Modification Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Last Modification Date</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Last Modification Date</em>' attribute.
	 * @see #setLastModificationDate(Date)
	 * @see bpm.united.olap.api.model.ModelPackage#getSchema_LastModificationDate()
	 * @model
	 * @generated
	 */
	Date getLastModificationDate();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.model.Schema#getLastModificationDate <em>Last Modification Date</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Last Modification Date</em>' attribute.
	 * @see #getLastModificationDate()
	 * @generated
	 */
	void setLastModificationDate(Date value);

} // Schema
