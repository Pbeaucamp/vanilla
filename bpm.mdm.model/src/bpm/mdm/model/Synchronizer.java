/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.mdm.model;

import java.util.List;
import java.util.Map;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;

import bpm.vanilla.platform.core.beans.data.OdaInput;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Synchronizer</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link bpm.mdm.model.Synchronizer#getDataSourceName <em>Data Source Name</em>}</li>
 *   <li>{@link bpm.mdm.model.Synchronizer#getEntity <em>Entity</em>}</li>
 *   <li>{@link bpm.mdm.model.Synchronizer#getStrategies <em>Strategies</em>}</li>
 *   <li>{@link bpm.mdm.model.Synchronizer#getName <em>Name</em>}</li>
 *   <li>{@link bpm.mdm.model.Synchronizer#getDescription <em>Description</em>}</li>
 *   <li>{@link bpm.mdm.model.Synchronizer#getMapping <em>Mapping</em>}</li>
 * </ul>
 * </p>
 *
 * @see bpm.mdm.model.MdmPackage#getSynchronizer()
 * @model
 * @generated
 */
public interface Synchronizer extends Mapper {
	/**
	 * Returns the value of the '<em><b>Data Source Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Data Source Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Data Source Name</em>' attribute.
	 * @see #setDataSourceName(String)
	 * @see bpm.mdm.model.MdmPackage#getSynchronizer_DataSourceName()
	 * @model
	 * @generated
	 */
	String getDataSourceName();

	/**
	 * Sets the value of the '{@link bpm.mdm.model.Synchronizer#getDataSourceName <em>Data Source Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Data Source Name</em>' attribute.
	 * @see #getDataSourceName()
	 * @generated
	 */
	void setDataSourceName(String value);

	/**
	 * Returns the value of the '<em><b>Entity</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Entity</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Entity</em>' reference.
	 * @see #setEntity(Entity)
	 * @see bpm.mdm.model.MdmPackage#getSynchronizer_Entity()
	 * @model
	 * @generated
	 */
	Entity getEntity();

	/**
	 * Sets the value of the '{@link bpm.mdm.model.Synchronizer#getEntity <em>Entity</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Entity</em>' reference.
	 * @see #getEntity()
	 * @generated
	 */
	void setEntity(Entity value);

	/**
	 * Returns the value of the '<em><b>Strategies</b></em>' reference list.
	 * The list contents are of type {@link bpm.mdm.model.Strategy}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Strategies</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Strategies</em>' reference list.
	 * @see bpm.mdm.model.MdmPackage#getSynchronizer_Strategies()
	 * @model
	 * @generated
	 */
	List<Strategy> getStrategies();

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
	 * @see bpm.mdm.model.MdmPackage#getSynchronizer_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link bpm.mdm.model.Synchronizer#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Description</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see bpm.mdm.model.MdmPackage#getSynchronizer_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link bpm.mdm.model.Synchronizer#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Mapping</b></em>' map.
	 * The key is of type {@link bpm.mdm.model.Attribute},
	 * and the value is of type {@link java.lang.Integer},
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Mapping</em>' map isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mapping</em>' map.
	 * @see bpm.mdm.model.MdmPackage#getSynchronizer_Mapping()
	 * @model mapType="bpm.mdm.model.AttributeToIntegerMap<bpm.mdm.model.Attribute, org.eclipse.emf.ecore.EIntegerObject>"
	 * @generated
	 */
	Map<Attribute, Integer> getMapping();

} // Synchronizer
