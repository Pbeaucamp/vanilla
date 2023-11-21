/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.datasource;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see bpm.united.olap.api.datasource.DatasourceFactory
 * @model kind="package"
 * @generated
 */
public interface DatasourcePackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "datasource";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://api/datasource/1.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "datasource";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	DatasourcePackage eINSTANCE = bpm.united.olap.api.datasource.impl.DatasourcePackageImpl.init();

	/**
	 * The meta object id for the '{@link bpm.united.olap.api.datasource.impl.DatasourceImpl <em>Datasource</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.united.olap.api.datasource.impl.DatasourceImpl
	 * @see bpm.united.olap.api.datasource.impl.DatasourcePackageImpl#getDatasource()
	 * @generated
	 */
	int DATASOURCE = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATASOURCE__NAME = 0;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATASOURCE__ID = 1;

	/**
	 * The feature id for the '<em><b>Connections</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATASOURCE__CONNECTIONS = 2;

	/**
	 * The feature id for the '<em><b>Relations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATASOURCE__RELATIONS = 3;

	/**
	 * The feature id for the '<em><b>Data Objects</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATASOURCE__DATA_OBJECTS = 4;

	/**
	 * The feature id for the '<em><b>Public Properties</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATASOURCE__PUBLIC_PROPERTIES = 5;

	/**
	 * The feature id for the '<em><b>Datasource Extension Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATASOURCE__DATASOURCE_EXTENSION_ID = 6;

	/**
	 * The feature id for the '<em><b>Private Properties</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATASOURCE__PRIVATE_PROPERTIES = 7;

	/**
	 * The feature id for the '<em><b>Parent</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATASOURCE__PARENT = 8;

	/**
	 * The number of structural features of the '<em>Datasource</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATASOURCE_FEATURE_COUNT = 9;

	/**
	 * The meta object id for the '{@link bpm.united.olap.api.datasource.impl.DataObjectImpl <em>Data Object</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.united.olap.api.datasource.impl.DataObjectImpl
	 * @see bpm.united.olap.api.datasource.impl.DatasourcePackageImpl#getDataObject()
	 * @generated
	 */
	int DATA_OBJECT = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_OBJECT__NAME = 0;

	/**
	 * The feature id for the '<em><b>Items</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_OBJECT__ITEMS = 1;

	/**
	 * The feature id for the '<em><b>Query Text</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_OBJECT__QUERY_TEXT = 2;

	/**
	 * The feature id for the '<em><b>Parent</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_OBJECT__PARENT = 3;

	/**
	 * The feature id for the '<em><b>Is Fact</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_OBJECT__IS_FACT = 4;

	/**
	 * The number of structural features of the '<em>Data Object</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_OBJECT_FEATURE_COUNT = 5;

	/**
	 * The meta object id for the '{@link bpm.united.olap.api.datasource.impl.DataObjectItemImpl <em>Data Object Item</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.united.olap.api.datasource.impl.DataObjectItemImpl
	 * @see bpm.united.olap.api.datasource.impl.DatasourcePackageImpl#getDataObjectItem()
	 * @generated
	 */
	int DATA_OBJECT_ITEM = 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_OBJECT_ITEM__NAME = 0;

	/**
	 * The feature id for the '<em><b>Parent</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_OBJECT_ITEM__PARENT = 1;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_OBJECT_ITEM__ID = 2;

	/**
	 * The feature id for the '<em><b>Is Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_OBJECT_ITEM__IS_KEY = 3;

	/**
	 * The number of structural features of the '<em>Data Object Item</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_OBJECT_ITEM_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link bpm.united.olap.api.datasource.impl.ConnectionImpl <em>Connection</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.united.olap.api.datasource.impl.ConnectionImpl
	 * @see bpm.united.olap.api.datasource.impl.DatasourcePackageImpl#getConnection()
	 * @generated
	 */
	int CONNECTION = 3;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONNECTION__NAME = 0;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONNECTION__ID = 1;

	/**
	 * The number of structural features of the '<em>Connection</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONNECTION_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link bpm.united.olap.api.datasource.impl.RelationImpl <em>Relation</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.united.olap.api.datasource.impl.RelationImpl
	 * @see bpm.united.olap.api.datasource.impl.DatasourcePackageImpl#getRelation()
	 * @generated
	 */
	int RELATION = 4;

	/**
	 * The feature id for the '<em><b>Operator</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATION__OPERATOR = 0;

	/**
	 * The feature id for the '<em><b>Left Item</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATION__LEFT_ITEM = 1;

	/**
	 * The feature id for the '<em><b>Right Item</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATION__RIGHT_ITEM = 2;

	/**
	 * The number of structural features of the '<em>Relation</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATION_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link bpm.united.olap.api.datasource.impl.OperatorImpl <em>Operator</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.united.olap.api.datasource.impl.OperatorImpl
	 * @see bpm.united.olap.api.datasource.impl.DatasourcePackageImpl#getOperator()
	 * @generated
	 */
	int OPERATOR = 5;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATOR__TYPE = 0;

	/**
	 * The number of structural features of the '<em>Operator</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATOR_FEATURE_COUNT = 1;


	/**
	 * Returns the meta object for class '{@link bpm.united.olap.api.datasource.Datasource <em>Datasource</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Datasource</em>'.
	 * @see bpm.united.olap.api.datasource.Datasource
	 * @generated
	 */
	EClass getDatasource();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.datasource.Datasource#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see bpm.united.olap.api.datasource.Datasource#getName()
	 * @see #getDatasource()
	 * @generated
	 */
	EAttribute getDatasource_Name();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.datasource.Datasource#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see bpm.united.olap.api.datasource.Datasource#getId()
	 * @see #getDatasource()
	 * @generated
	 */
	EAttribute getDatasource_Id();

	/**
	 * Returns the meta object for the containment reference list '{@link bpm.united.olap.api.datasource.Datasource#getConnections <em>Connections</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Connections</em>'.
	 * @see bpm.united.olap.api.datasource.Datasource#getConnections()
	 * @see #getDatasource()
	 * @generated
	 */
	EReference getDatasource_Connections();

	/**
	 * Returns the meta object for the containment reference list '{@link bpm.united.olap.api.datasource.Datasource#getRelations <em>Relations</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Relations</em>'.
	 * @see bpm.united.olap.api.datasource.Datasource#getRelations()
	 * @see #getDatasource()
	 * @generated
	 */
	EReference getDatasource_Relations();

	/**
	 * Returns the meta object for the containment reference list '{@link bpm.united.olap.api.datasource.Datasource#getDataObjects <em>Data Objects</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Data Objects</em>'.
	 * @see bpm.united.olap.api.datasource.Datasource#getDataObjects()
	 * @see #getDatasource()
	 * @generated
	 */
	EReference getDatasource_DataObjects();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.datasource.Datasource#getPublicProperties <em>Public Properties</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Public Properties</em>'.
	 * @see bpm.united.olap.api.datasource.Datasource#getPublicProperties()
	 * @see #getDatasource()
	 * @generated
	 */
	EAttribute getDatasource_PublicProperties();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.datasource.Datasource#getDatasourceExtensionId <em>Datasource Extension Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Datasource Extension Id</em>'.
	 * @see bpm.united.olap.api.datasource.Datasource#getDatasourceExtensionId()
	 * @see #getDatasource()
	 * @generated
	 */
	EAttribute getDatasource_DatasourceExtensionId();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.datasource.Datasource#getPrivateProperties <em>Private Properties</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Private Properties</em>'.
	 * @see bpm.united.olap.api.datasource.Datasource#getPrivateProperties()
	 * @see #getDatasource()
	 * @generated
	 */
	EAttribute getDatasource_PrivateProperties();

	/**
	 * Returns the meta object for the container reference '{@link bpm.united.olap.api.datasource.Datasource#getParent <em>Parent</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Parent</em>'.
	 * @see bpm.united.olap.api.datasource.Datasource#getParent()
	 * @see #getDatasource()
	 * @generated
	 */
	EReference getDatasource_Parent();

	/**
	 * Returns the meta object for class '{@link bpm.united.olap.api.datasource.DataObject <em>Data Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Data Object</em>'.
	 * @see bpm.united.olap.api.datasource.DataObject
	 * @generated
	 */
	EClass getDataObject();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.datasource.DataObject#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see bpm.united.olap.api.datasource.DataObject#getName()
	 * @see #getDataObject()
	 * @generated
	 */
	EAttribute getDataObject_Name();

	/**
	 * Returns the meta object for the containment reference list '{@link bpm.united.olap.api.datasource.DataObject#getItems <em>Items</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Items</em>'.
	 * @see bpm.united.olap.api.datasource.DataObject#getItems()
	 * @see #getDataObject()
	 * @generated
	 */
	EReference getDataObject_Items();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.datasource.DataObject#getQueryText <em>Query Text</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Query Text</em>'.
	 * @see bpm.united.olap.api.datasource.DataObject#getQueryText()
	 * @see #getDataObject()
	 * @generated
	 */
	EAttribute getDataObject_QueryText();

	/**
	 * Returns the meta object for the container reference '{@link bpm.united.olap.api.datasource.DataObject#getParent <em>Parent</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Parent</em>'.
	 * @see bpm.united.olap.api.datasource.DataObject#getParent()
	 * @see #getDataObject()
	 * @generated
	 */
	EReference getDataObject_Parent();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.datasource.DataObject#isIsFact <em>Is Fact</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Is Fact</em>'.
	 * @see bpm.united.olap.api.datasource.DataObject#isIsFact()
	 * @see #getDataObject()
	 * @generated
	 */
	EAttribute getDataObject_IsFact();

	/**
	 * Returns the meta object for class '{@link bpm.united.olap.api.datasource.DataObjectItem <em>Data Object Item</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Data Object Item</em>'.
	 * @see bpm.united.olap.api.datasource.DataObjectItem
	 * @generated
	 */
	EClass getDataObjectItem();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.datasource.DataObjectItem#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see bpm.united.olap.api.datasource.DataObjectItem#getName()
	 * @see #getDataObjectItem()
	 * @generated
	 */
	EAttribute getDataObjectItem_Name();

	/**
	 * Returns the meta object for the container reference '{@link bpm.united.olap.api.datasource.DataObjectItem#getParent <em>Parent</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Parent</em>'.
	 * @see bpm.united.olap.api.datasource.DataObjectItem#getParent()
	 * @see #getDataObjectItem()
	 * @generated
	 */
	EReference getDataObjectItem_Parent();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.datasource.DataObjectItem#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see bpm.united.olap.api.datasource.DataObjectItem#getId()
	 * @see #getDataObjectItem()
	 * @generated
	 */
	EAttribute getDataObjectItem_Id();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.datasource.DataObjectItem#isIsKey <em>Is Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Is Key</em>'.
	 * @see bpm.united.olap.api.datasource.DataObjectItem#isIsKey()
	 * @see #getDataObjectItem()
	 * @generated
	 */
	EAttribute getDataObjectItem_IsKey();

	/**
	 * Returns the meta object for class '{@link bpm.united.olap.api.datasource.Connection <em>Connection</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Connection</em>'.
	 * @see bpm.united.olap.api.datasource.Connection
	 * @generated
	 */
	EClass getConnection();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.datasource.Connection#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see bpm.united.olap.api.datasource.Connection#getName()
	 * @see #getConnection()
	 * @generated
	 */
	EAttribute getConnection_Name();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.datasource.Connection#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see bpm.united.olap.api.datasource.Connection#getId()
	 * @see #getConnection()
	 * @generated
	 */
	EAttribute getConnection_Id();

	/**
	 * Returns the meta object for class '{@link bpm.united.olap.api.datasource.Relation <em>Relation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Relation</em>'.
	 * @see bpm.united.olap.api.datasource.Relation
	 * @generated
	 */
	EClass getRelation();

	/**
	 * Returns the meta object for the containment reference '{@link bpm.united.olap.api.datasource.Relation#getOperator <em>Operator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Operator</em>'.
	 * @see bpm.united.olap.api.datasource.Relation#getOperator()
	 * @see #getRelation()
	 * @generated
	 */
	EReference getRelation_Operator();

	/**
	 * Returns the meta object for the reference '{@link bpm.united.olap.api.datasource.Relation#getLeftItem <em>Left Item</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Left Item</em>'.
	 * @see bpm.united.olap.api.datasource.Relation#getLeftItem()
	 * @see #getRelation()
	 * @generated
	 */
	EReference getRelation_LeftItem();

	/**
	 * Returns the meta object for the reference '{@link bpm.united.olap.api.datasource.Relation#getRightItem <em>Right Item</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Right Item</em>'.
	 * @see bpm.united.olap.api.datasource.Relation#getRightItem()
	 * @see #getRelation()
	 * @generated
	 */
	EReference getRelation_RightItem();

	/**
	 * Returns the meta object for class '{@link bpm.united.olap.api.datasource.Operator <em>Operator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Operator</em>'.
	 * @see bpm.united.olap.api.datasource.Operator
	 * @generated
	 */
	EClass getOperator();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.datasource.Operator#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see bpm.united.olap.api.datasource.Operator#getType()
	 * @see #getOperator()
	 * @generated
	 */
	EAttribute getOperator_Type();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	DatasourceFactory getDatasourceFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link bpm.united.olap.api.datasource.impl.DatasourceImpl <em>Datasource</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.united.olap.api.datasource.impl.DatasourceImpl
		 * @see bpm.united.olap.api.datasource.impl.DatasourcePackageImpl#getDatasource()
		 * @generated
		 */
		EClass DATASOURCE = eINSTANCE.getDatasource();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DATASOURCE__NAME = eINSTANCE.getDatasource_Name();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DATASOURCE__ID = eINSTANCE.getDatasource_Id();

		/**
		 * The meta object literal for the '<em><b>Connections</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DATASOURCE__CONNECTIONS = eINSTANCE.getDatasource_Connections();

		/**
		 * The meta object literal for the '<em><b>Relations</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DATASOURCE__RELATIONS = eINSTANCE.getDatasource_Relations();

		/**
		 * The meta object literal for the '<em><b>Data Objects</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DATASOURCE__DATA_OBJECTS = eINSTANCE.getDatasource_DataObjects();

		/**
		 * The meta object literal for the '<em><b>Public Properties</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DATASOURCE__PUBLIC_PROPERTIES = eINSTANCE.getDatasource_PublicProperties();

		/**
		 * The meta object literal for the '<em><b>Datasource Extension Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DATASOURCE__DATASOURCE_EXTENSION_ID = eINSTANCE.getDatasource_DatasourceExtensionId();

		/**
		 * The meta object literal for the '<em><b>Private Properties</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DATASOURCE__PRIVATE_PROPERTIES = eINSTANCE.getDatasource_PrivateProperties();

		/**
		 * The meta object literal for the '<em><b>Parent</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DATASOURCE__PARENT = eINSTANCE.getDatasource_Parent();

		/**
		 * The meta object literal for the '{@link bpm.united.olap.api.datasource.impl.DataObjectImpl <em>Data Object</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.united.olap.api.datasource.impl.DataObjectImpl
		 * @see bpm.united.olap.api.datasource.impl.DatasourcePackageImpl#getDataObject()
		 * @generated
		 */
		EClass DATA_OBJECT = eINSTANCE.getDataObject();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DATA_OBJECT__NAME = eINSTANCE.getDataObject_Name();

		/**
		 * The meta object literal for the '<em><b>Items</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DATA_OBJECT__ITEMS = eINSTANCE.getDataObject_Items();

		/**
		 * The meta object literal for the '<em><b>Query Text</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DATA_OBJECT__QUERY_TEXT = eINSTANCE.getDataObject_QueryText();

		/**
		 * The meta object literal for the '<em><b>Parent</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DATA_OBJECT__PARENT = eINSTANCE.getDataObject_Parent();

		/**
		 * The meta object literal for the '<em><b>Is Fact</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DATA_OBJECT__IS_FACT = eINSTANCE.getDataObject_IsFact();

		/**
		 * The meta object literal for the '{@link bpm.united.olap.api.datasource.impl.DataObjectItemImpl <em>Data Object Item</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.united.olap.api.datasource.impl.DataObjectItemImpl
		 * @see bpm.united.olap.api.datasource.impl.DatasourcePackageImpl#getDataObjectItem()
		 * @generated
		 */
		EClass DATA_OBJECT_ITEM = eINSTANCE.getDataObjectItem();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DATA_OBJECT_ITEM__NAME = eINSTANCE.getDataObjectItem_Name();

		/**
		 * The meta object literal for the '<em><b>Parent</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DATA_OBJECT_ITEM__PARENT = eINSTANCE.getDataObjectItem_Parent();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DATA_OBJECT_ITEM__ID = eINSTANCE.getDataObjectItem_Id();

		/**
		 * The meta object literal for the '<em><b>Is Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DATA_OBJECT_ITEM__IS_KEY = eINSTANCE.getDataObjectItem_IsKey();

		/**
		 * The meta object literal for the '{@link bpm.united.olap.api.datasource.impl.ConnectionImpl <em>Connection</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.united.olap.api.datasource.impl.ConnectionImpl
		 * @see bpm.united.olap.api.datasource.impl.DatasourcePackageImpl#getConnection()
		 * @generated
		 */
		EClass CONNECTION = eINSTANCE.getConnection();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONNECTION__NAME = eINSTANCE.getConnection_Name();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONNECTION__ID = eINSTANCE.getConnection_Id();

		/**
		 * The meta object literal for the '{@link bpm.united.olap.api.datasource.impl.RelationImpl <em>Relation</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.united.olap.api.datasource.impl.RelationImpl
		 * @see bpm.united.olap.api.datasource.impl.DatasourcePackageImpl#getRelation()
		 * @generated
		 */
		EClass RELATION = eINSTANCE.getRelation();

		/**
		 * The meta object literal for the '<em><b>Operator</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RELATION__OPERATOR = eINSTANCE.getRelation_Operator();

		/**
		 * The meta object literal for the '<em><b>Left Item</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RELATION__LEFT_ITEM = eINSTANCE.getRelation_LeftItem();

		/**
		 * The meta object literal for the '<em><b>Right Item</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RELATION__RIGHT_ITEM = eINSTANCE.getRelation_RightItem();

		/**
		 * The meta object literal for the '{@link bpm.united.olap.api.datasource.impl.OperatorImpl <em>Operator</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.united.olap.api.datasource.impl.OperatorImpl
		 * @see bpm.united.olap.api.datasource.impl.DatasourcePackageImpl#getOperator()
		 * @generated
		 */
		EClass OPERATOR = eINSTANCE.getOperator();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute OPERATOR__TYPE = eINSTANCE.getOperator_Type();

	}

} //DatasourcePackage
