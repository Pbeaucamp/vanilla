/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.model;

import java.util.List;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Hierarchy</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link bpm.united.olap.api.model.Hierarchy#getLevels <em>Levels</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.Hierarchy#getParentDimension <em>Parent Dimension</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.Hierarchy#isIsClosureHierarchy <em>Is Closure Hierarchy</em>}</li>
 * </ul>
 * </p>
 *
 * @see bpm.united.olap.api.model.ModelPackage#getHierarchy()
 * @model
 * @generated
 */
public interface Hierarchy extends ElementDefinition {
	/**
	 * Returns the value of the '<em><b>Levels</b></em>' containment reference list.
	 * The list contents are of type {@link bpm.united.olap.api.model.Level}.
	 * It is bidirectional and its opposite is '{@link bpm.united.olap.api.model.Level#getParentHierarchy <em>Parent Hierarchy</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Levels</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Levels</em>' containment reference list.
	 * @see bpm.united.olap.api.model.ModelPackage#getHierarchy_Levels()
	 * @see bpm.united.olap.api.model.Level#getParentHierarchy
	 * @model opposite="parentHierarchy" containment="true"
	 * @generated
	 */
	List<Level> getLevels();

	/**
	 * Returns the value of the '<em><b>Parent Dimension</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link bpm.united.olap.api.model.Dimension#getHierarchies <em>Hierarchies</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parent Dimension</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parent Dimension</em>' container reference.
	 * @see #setParentDimension(Dimension)
	 * @see bpm.united.olap.api.model.ModelPackage#getHierarchy_ParentDimension()
	 * @see bpm.united.olap.api.model.Dimension#getHierarchies
	 * @model opposite="hierarchies" transient="false"
	 * @generated
	 */
	Dimension getParentDimension();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.model.Hierarchy#getParentDimension <em>Parent Dimension</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parent Dimension</em>' container reference.
	 * @see #getParentDimension()
	 * @generated
	 */
	void setParentDimension(Dimension value);

	/**
	 * Returns the value of the '<em><b>Is Closure Hierarchy</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Is Closure Hierarchy</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Is Closure Hierarchy</em>' attribute.
	 * @see #setIsClosureHierarchy(boolean)
	 * @see bpm.united.olap.api.model.ModelPackage#getHierarchy_IsClosureHierarchy()
	 * @model
	 * @generated
	 */
	boolean isIsClosureHierarchy();

	/**
	 * Sets the value of the '{@link bpm.united.olap.api.model.Hierarchy#isIsClosureHierarchy <em>Is Closure Hierarchy</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Is Closure Hierarchy</em>' attribute.
	 * @see #isIsClosureHierarchy()
	 * @generated
	 */
	void setIsClosureHierarchy(boolean value);
	
	void setAllMember(String allMember);
	
	String getAllMember();

} // Hierarchy
