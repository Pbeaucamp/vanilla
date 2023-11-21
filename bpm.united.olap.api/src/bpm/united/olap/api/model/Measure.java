/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.model;

import bpm.united.olap.api.datasource.DataObjectItem;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Measure</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link bpm.united.olap.api.model.Measure#getItem <em>Item</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.Measure#getCalculationType <em>Calculation Type</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.Measure#getParentSchema <em>Parent Schema</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.Measure#getCalculatdFormula <em>Calculatd Formula</em>}</li>
 * </ul>
 * </p>
 *
 * @see bpm.united.olap.api.model.ModelPackage#getMeasure()
 * @model
 * @generated
 */
public interface Measure extends ElementDefinition {
	/**
	 * Returns the value of the '<em><b>Item</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Item</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Item</em>' reference.
	 * @see #setItem(DataObjectItem)
	 * @see bpm.united.olap.api.model.ModelPackage#getMeasure_Item()
	 * @model
	 * @generated
	 */
	DataObjectItem getItem();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.model.Measure#getItem <em>Item</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Item</em>' reference.
	 * @see #getItem()
	 * @generated
	 */
	void setItem(DataObjectItem value);

	/**
	 * Returns the value of the '<em><b>Calculation Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Calculation Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Calculation Type</em>' attribute.
	 * @see #setCalculationType(String)
	 * @see bpm.united.olap.api.model.ModelPackage#getMeasure_CalculationType()
	 * @model
	 * @generated
	 */
	String getCalculationType();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.model.Measure#getCalculationType <em>Calculation Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Calculation Type</em>' attribute.
	 * @see #getCalculationType()
	 * @generated
	 */
	void setCalculationType(String value);

	/**
	 * Returns the value of the '<em><b>Parent Schema</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link bpm.united.olap.api.model.Schema#getMeasures <em>Measures</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parent Schema</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parent Schema</em>' container reference.
	 * @see #setParentSchema(Schema)
	 * @see bpm.united.olap.api.model.ModelPackage#getMeasure_ParentSchema()
	 * @see bpm.united.olap.api.model.Schema#getMeasures
	 * @model opposite="measures" transient="false"
	 * @generated
	 */
	Schema getParentSchema();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.model.Measure#getParentSchema <em>Parent Schema</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parent Schema</em>' container reference.
	 * @see #getParentSchema()
	 * @generated
	 */
	void setParentSchema(Schema value);

	/**
	 * Returns the value of the '<em><b>Calculatd Formula</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Calculatd Formula</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Calculatd Formula</em>' attribute.
	 * @see #setCalculatdFormula(String)
	 * @see bpm.united.olap.api.model.ModelPackage#getMeasure_CalculatdFormula()
	 * @model
	 * @generated
	 */
	String getCalculatdFormula();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.model.Measure#getCalculatdFormula <em>Calculatd Formula</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Calculatd Formula</em>' attribute.
	 * @see #getCalculatdFormula()
	 * @generated
	 */
	void setCalculatdFormula(String value);

	String getLastDimensionName();
	
	void setLastDimensionName(String dimensionName);
	
} // Measure
