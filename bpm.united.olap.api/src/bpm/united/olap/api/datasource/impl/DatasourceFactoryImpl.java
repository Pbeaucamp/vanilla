/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.datasource.impl;

import bpm.united.olap.api.datasource.*;

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
public class DatasourceFactoryImpl extends EFactoryImpl implements DatasourceFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static DatasourceFactory init() {
		try {
			DatasourceFactory theDatasourceFactory = (DatasourceFactory)EPackage.Registry.INSTANCE.getEFactory("http://api/datasource/1.0"); 
			if (theDatasourceFactory != null) {
				return theDatasourceFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new DatasourceFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DatasourceFactoryImpl() {
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
			case DatasourcePackage.DATASOURCE: return createDatasource();
			case DatasourcePackage.DATA_OBJECT: return createDataObject();
			case DatasourcePackage.DATA_OBJECT_ITEM: return createDataObjectItem();
			case DatasourcePackage.CONNECTION: return createConnection();
			case DatasourcePackage.RELATION: return createRelation();
			case DatasourcePackage.OPERATOR: return createOperator();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Datasource createDatasource() {
		DatasourceImpl datasource = new DatasourceImpl();
		return datasource;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DataObject createDataObject() {
		DataObjectImpl dataObject = new DataObjectImpl();
		return dataObject;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DataObjectItem createDataObjectItem() {
		DataObjectItemImpl dataObjectItem = new DataObjectItemImpl();
		return dataObjectItem;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Connection createConnection() {
		ConnectionImpl connection = new ConnectionImpl();
		return connection;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Relation createRelation() {
		RelationImpl relation = new RelationImpl();
		return relation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Operator createOperator() {
		OperatorImpl operator = new OperatorImpl();
		return operator;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DatasourcePackage getDatasourcePackage() {
		return (DatasourcePackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static DatasourcePackage getPackage() {
		return DatasourcePackage.eINSTANCE;
	}

} //DatasourceFactoryImpl
