/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.runtime.util;

import bpm.united.olap.api.runtime.*;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see bpm.united.olap.api.runtime.RuntimePackage
 * @generated
 */
public class RuntimeAdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static RuntimePackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RuntimeAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = RuntimePackage.eINSTANCE;
		}
	}

	/**
	 * Returns whether this factory is applicable for the type of the object.
	 * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
	 * <!-- end-user-doc -->
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object object) {
		if (object == modelPackage) {
			return true;
		}
		if (object instanceof EObject) {
			return ((EObject)object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/**
	 * The switch that delegates to the <code>createXXX</code> methods.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected RuntimeSwitch<Adapter> modelSwitch =
		new RuntimeSwitch<Adapter>() {
			@Override
			public Adapter caseRunResult(RunResult object) {
				return createRunResultAdapter();
			}
			@Override
			public Adapter caseMdxSet(MdxSet object) {
				return createMdxSetAdapter();
			}
			@Override
			public Adapter caseDataStorage(DataStorage object) {
				return createDataStorageAdapter();
			}
			@Override
			public Adapter caseDataCell(DataCell object) {
				return createDataCellAdapter();
			}
//			@Override
//			public Adapter caseDataCellIdentifier(DataCellIdentifier object) {
//				return createDataCellIdentifierAdapter();
//			}
//			@Override
//			public Adapter caseNodeId(NodeId object) {
//				return createNodeIdAdapter();
//			}
			@Override
			public Adapter defaultCase(EObject object) {
				return createEObjectAdapter();
			}
		};

	/**
	 * Creates an adapter for the <code>target</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param target the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
	@Override
	public Adapter createAdapter(Notifier target) {
		return modelSwitch.doSwitch((EObject)target);
	}


	/**
	 * Creates a new adapter for an object of class '{@link bpm.united.olap.api.runtime.RunResult <em>Run Result</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpm.united.olap.api.runtime.RunResult
	 * @generated
	 */
	public Adapter createRunResultAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpm.united.olap.api.runtime.MdxSet <em>Mdx Set</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpm.united.olap.api.runtime.MdxSet
	 * @generated
	 */
	public Adapter createMdxSetAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpm.united.olap.api.runtime.DataStorage <em>Data Storage</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpm.united.olap.api.runtime.DataStorage
	 * @generated
	 */
	public Adapter createDataStorageAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpm.united.olap.api.runtime.DataCell <em>Data Cell</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpm.united.olap.api.runtime.DataCell
	 * @generated
	 */
	public Adapter createDataCellAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpm.united.olap.api.runtime.DataCellIdentifier <em>Data Cell Identifier</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpm.united.olap.api.runtime.DataCellIdentifier
	 * @generated
	 */
	public Adapter createDataCellIdentifierAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpm.united.olap.api.runtime.NodeId <em>Node Id</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpm.united.olap.api.runtime.NodeId
	 * @generated
	 */
	public Adapter createNodeIdAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for the default case.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @generated
	 */
	public Adapter createEObjectAdapter() {
		return null;
	}

} //RuntimeAdapterFactory
