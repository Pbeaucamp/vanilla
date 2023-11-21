/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.runtime.util;

import bpm.united.olap.api.runtime.*;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see bpm.united.olap.api.runtime.RuntimePackage
 * @generated
 */
public class RuntimeSwitch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static RuntimePackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RuntimeSwitch() {
		if (modelPackage == null) {
			modelPackage = RuntimePackage.eINSTANCE;
		}
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	public T doSwitch(EObject theEObject) {
		return doSwitch(theEObject.eClass(), theEObject);
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected T doSwitch(EClass theEClass, EObject theEObject) {
		if (theEClass.eContainer() == modelPackage) {
			return doSwitch(theEClass.getClassifierID(), theEObject);
		}
		else {
			List<EClass> eSuperTypes = theEClass.getESuperTypes();
			return
				eSuperTypes.isEmpty() ?
					defaultCase(theEObject) :
					doSwitch(eSuperTypes.get(0), theEObject);
		}
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected T doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
			case RuntimePackage.RUN_RESULT: {
				RunResult runResult = (RunResult)theEObject;
				T result = caseRunResult(runResult);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case RuntimePackage.MDX_SET: {
				MdxSet mdxSet = (MdxSet)theEObject;
				T result = caseMdxSet(mdxSet);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case RuntimePackage.DATA_STORAGE: {
				DataStorage dataStorage = (DataStorage)theEObject;
				T result = caseDataStorage(dataStorage);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case RuntimePackage.DATA_CELL: {
				DataCell dataCell = (DataCell)theEObject;
				T result = caseDataCell(dataCell);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
//			case RuntimePackage.DATA_CELL_IDENTIFIER: {
//				DataCellIdentifier dataCellIdentifier = (DataCellIdentifier)theEObject;
//				T result = caseDataCellIdentifier(dataCellIdentifier);
//				if (result == null) result = defaultCase(theEObject);
//				return result;
//			}
//			case RuntimePackage.NODE_ID: {
//				NodeId nodeId = (NodeId)theEObject;
//				T result = caseNodeId(nodeId);
//				if (result == null) result = defaultCase(theEObject);
//				return result;
//			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Run Result</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Run Result</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseRunResult(RunResult object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Mdx Set</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Mdx Set</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMdxSet(MdxSet object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Data Storage</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Data Storage</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDataStorage(DataStorage object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Data Cell</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Data Cell</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDataCell(DataCell object) {
		return null;
	}

//	/**
//	 * Returns the result of interpreting the object as an instance of '<em>Data Cell Identifier</em>'.
//	 * <!-- begin-user-doc -->
//	 * This implementation returns null;
//	 * returning a non-null result will terminate the switch.
//	 * <!-- end-user-doc -->
//	 * @param object the target of the switch.
//	 * @return the result of interpreting the object as an instance of '<em>Data Cell Identifier</em>'.
//	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
//	 * @generated
//	 */
//	public T caseDataCellIdentifier(DataCellIdentifier object) {
//		return null;
//	}

//	/**
//	 * Returns the result of interpreting the object as an instance of '<em>Node Id</em>'.
//	 * <!-- begin-user-doc -->
//	 * This implementation returns null;
//	 * returning a non-null result will terminate the switch.
//	 * <!-- end-user-doc -->
//	 * @param object the target of the switch.
//	 * @return the result of interpreting the object as an instance of '<em>Node Id</em>'.
//	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
//	 * @generated
//	 */
//	public T caseNodeId(NodeId object) {
//		return null;
//	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	public T defaultCase(EObject object) {
		return null;
	}

} //RuntimeSwitch
