/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.runtime.impl;

import bpm.united.olap.api.runtime.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class RuntimeFactoryImpl extends EFactoryImpl implements RuntimeFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static RuntimeFactory init() {
		try {
			RuntimeFactory theRuntimeFactory = (RuntimeFactory)EPackage.Registry.INSTANCE.getEFactory("http://api/runtime/1.0"); 
			if (theRuntimeFactory != null) {
				return theRuntimeFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new RuntimeFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RuntimeFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case RuntimePackage.RUN_RESULT: return createRunResult();
			case RuntimePackage.MDX_SET: return createMdxSet();
			case RuntimePackage.DATA_STORAGE: return createDataStorage();
			case RuntimePackage.DATA_CELL: return createDataCell();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RunResult createRunResult() {
		RunResultImpl runResult = new RunResultImpl();
		return runResult;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MdxSet createMdxSet() {
		MdxSetImpl mdxSet = new MdxSetImpl();
		return mdxSet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DataStorage createDataStorage() {
		DataStorageImpl dataStorage = new DataStorageImpl();
		return dataStorage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DataCell createDataCell() {
		DataCellImpl dataCell = new DataCellImpl();
		return dataCell;
	}





	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RuntimePackage getRuntimePackage() {
		return (RuntimePackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static RuntimePackage getPackage() {
		return RuntimePackage.eINSTANCE;
	}

	@Override
	public DataCellIdentifier2 createDataCellIdentifier2() {
		DataCellIdentifierImpl2 dataCellIdentifier = new DataCellIdentifierImpl2();
		return dataCellIdentifier;
	}

} //RuntimeFactoryImpl
