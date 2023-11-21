/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.mdm.model;

import java.util.List;

import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Model</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link bpm.mdm.model.Model#getName <em>Name</em>}</li>
 *   <li>{@link bpm.mdm.model.Model#getEntities <em>Entities</em>}</li>
 *   <li>{@link bpm.mdm.model.Model#getSynchronizers <em>Synchronizers</em>}</li>
 *   <li>{@link bpm.mdm.model.Model#getDataSources <em>Data Sources</em>}</li>
 * </ul>
 * </p>
 *
 * @see bpm.mdm.model.MdmPackage#getModel()
 * @model
 * @generated
 */
public interface Model extends EObject {
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
	 * @see bpm.mdm.model.MdmPackage#getModel_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link bpm.mdm.model.Model#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Entities</b></em>' containment reference list.
	 * The list contents are of type {@link bpm.mdm.model.Entity}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Entities</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Entities</em>' containment reference list.
	 * @see bpm.mdm.model.MdmPackage#getModel_Entities()
	 * @model containment="true"
	 * @generated
	 */
	List<Entity> getEntities();

	/**
	 * Returns the value of the '<em><b>Synchronizers</b></em>' containment reference list.
	 * The list contents are of type {@link bpm.mdm.model.Synchronizer}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Synchronizers</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Synchronizers</em>' containment reference list.
	 * @see bpm.mdm.model.MdmPackage#getModel_Synchronizers()
	 * @model containment="true"
	 * @generated
	 */
	List<Synchronizer> getSynchronizers();

	/**
	 * Returns the value of the '<em><b>Data Sources</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.datatools.connectivity.oda.design.DataSetDesign}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Data Sources</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Data Sources</em>' attribute list.
	 * @see bpm.mdm.model.MdmPackage#getModel_DataSources()
	 * @model dataType="bpm.mdm.model.DataSet"
	 * @generated
	 */
	List<DataSetDesign> getDataSources();


	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model dataType="bpm.mdm.model.DataSet"
	 * @generated
	 */
	DataSetDesign getDataSource(String dataSourceName);

} // Model
