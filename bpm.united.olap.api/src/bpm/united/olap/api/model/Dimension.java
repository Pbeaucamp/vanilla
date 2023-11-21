/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.model;

import java.util.List;

import bpm.united.olap.api.datasource.DataObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Dimension</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link bpm.united.olap.api.model.Dimension#getDataObject <em>Data Object</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.Dimension#getHierarchies <em>Hierarchies</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.Dimension#getParentSchema <em>Parent Schema</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.Dimension#isIsOneColumnDate <em>Is One Column Date</em>}</li>
 * </ul>
 * </p>
 *
 * @see bpm.united.olap.api.model.ModelPackage#getDimension()
 * @model
 * @generated
 */
public interface Dimension extends ElementDefinition {
	/**
	 * Returns the value of the '<em><b>Data Object</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Data Object</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Data Object</em>' reference.
	 * @see #setDataObject(DataObject)
	 * @see bpm.united.olap.api.model.ModelPackage#getDimension_DataObject()
	 * @model
	 * @generated
	 */
	DataObject getDataObject();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.model.Dimension#getDataObject <em>Data Object</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Data Object</em>' reference.
	 * @see #getDataObject()
	 * @generated
	 */
	void setDataObject(DataObject value);

	/**
	 * Returns the value of the '<em><b>Hierarchies</b></em>' containment reference list.
	 * The list contents are of type {@link bpm.united.olap.api.model.Hierarchy}.
	 * It is bidirectional and its opposite is '{@link bpm.united.olap.api.model.Hierarchy#getParentDimension <em>Parent Dimension</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Hierarchies</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Hierarchies</em>' containment reference list.
	 * @see bpm.united.olap.api.model.ModelPackage#getDimension_Hierarchies()
	 * @see bpm.united.olap.api.model.Hierarchy#getParentDimension
	 * @model opposite="parentDimension" containment="true"
	 * @generated
	 */
	List<Hierarchy> getHierarchies();

	/**
	 * Returns the value of the '<em><b>Parent Schema</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link bpm.united.olap.api.model.Schema#getDimensions <em>Dimensions</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parent Schema</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parent Schema</em>' container reference.
	 * @see #setParentSchema(Schema)
	 * @see bpm.united.olap.api.model.ModelPackage#getDimension_ParentSchema()
	 * @see bpm.united.olap.api.model.Schema#getDimensions
	 * @model opposite="dimensions" transient="false"
	 * @generated
	 */
	Schema getParentSchema();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.model.Dimension#getParentSchema <em>Parent Schema</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parent Schema</em>' container reference.
	 * @see #getParentSchema()
	 * @generated
	 */
	void setParentSchema(Schema value);

	/**
	 * Returns the value of the '<em><b>Is One Column Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Is One Column Date</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Is One Column Date</em>' attribute.
	 * @see #setIsOneColumnDate(boolean)
	 * @see bpm.united.olap.api.model.ModelPackage#getDimension_IsOneColumnDate()
	 * @model
	 * @generated
	 */
	boolean isIsOneColumnDate();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.model.Dimension#isIsOneColumnDate <em>Is One Column Date</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Is One Column Date</em>' attribute.
	 * @see #isIsOneColumnDate()
	 * @generated
	 */
	void setIsOneColumnDate(boolean value);
	
	boolean isDate();
	
	void setIsDate(boolean isDate);
	
	
	void setIsGeolocalizable(boolean isGeolocalizable);
	boolean isGeolocalizable();

} // Dimension
