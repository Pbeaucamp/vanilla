/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.mdm.model.runtime;

import org.eclipse.emf.ecore.EObject;

import bpm.mdm.model.Synchronizer;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Synchro Performer</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see bpm.mdm.model.runtime.RuntimePackage#getSynchroPerformer()
 * @model
 * @generated
 */
public interface SynchroPerformer extends EObject {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	DiffResult performDiff(Synchronizer synchronizer);

//	/**
//	 * <!-- begin-user-doc -->
//	 * <!-- end-user-doc -->
//	 * @model entityStoreType="bpm.mdm.model.storage.EntityStore"
//	 */
//	DiffResult performDiff(Synchronizer synchronizer, IEntityStore entityStore)  throws Exception;


} // SynchroPerformer
