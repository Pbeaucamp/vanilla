/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.mdm.model;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
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
 * @see bpm.mdm.model.MdmFactory
 * @model kind="package"
 * @generated
 */
public interface MdmPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "model";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://mdm_model/1.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "mdm_model";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	MdmPackage eINSTANCE = bpm.mdm.model.impl.MdmPackageImpl.init();

	/**
	 * The meta object id for the '{@link bpm.mdm.model.Rule <em>Rule</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.mdm.model.Rule
	 * @see bpm.mdm.model.impl.MdmPackageImpl#getRule()
	 * @generated
	 */
	int RULE = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RULE__NAME = 0;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RULE__DESCRIPTION = 1;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RULE__ACTIVE = 2;

	/**
	 * The number of structural features of the '<em>Rule</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RULE_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link bpm.mdm.model.impl.AttributeImpl <em>Attribute</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.mdm.model.impl.AttributeImpl
	 * @see bpm.mdm.model.impl.MdmPackageImpl#getAttribute()
	 * @generated
	 */
	int ATTRIBUTE = 1;

	/**
	 * The feature id for the '<em><b>Rules</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE__RULES = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE__NAME = 1;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE__DESCRIPTION = 2;

	/**
	 * The feature id for the '<em><b>Data Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE__DATA_TYPE = 3;

	/**
	 * The feature id for the '<em><b>Default Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE__DEFAULT_VALUE = 4;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE__ID = 5;

	/**
	 * The feature id for the '<em><b>Null Allowed</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE__NULL_ALLOWED = 6;

	/**
	 * The feature id for the '<em><b>Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE__UUID = 7;

	/**
	 * The number of structural features of the '<em>Attribute</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_FEATURE_COUNT = 8;

	/**
	 * The meta object id for the '{@link bpm.mdm.model.impl.EntityImpl <em>Entity</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.mdm.model.impl.EntityImpl
	 * @see bpm.mdm.model.impl.MdmPackageImpl#getEntity()
	 * @generated
	 */
	int ENTITY = 2;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__ATTRIBUTES = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__NAME = 1;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__DESCRIPTION = 2;

	/**
	 * The feature id for the '<em><b>Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__UUID = 3;

	/**
	 * The number of structural features of the '<em>Entity</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link bpm.mdm.model.Mapper <em>Mapper</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.mdm.model.Mapper
	 * @see bpm.mdm.model.impl.MdmPackageImpl#getMapper()
	 * @generated
	 */
	int MAPPER = 3;

	/**
	 * The number of structural features of the '<em>Mapper</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPER_FEATURE_COUNT = 0;

	/**
	 * The meta object id for the '{@link bpm.mdm.model.impl.SynchronizerImpl <em>Synchronizer</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.mdm.model.impl.SynchronizerImpl
	 * @see bpm.mdm.model.impl.MdmPackageImpl#getSynchronizer()
	 * @generated
	 */
	int SYNCHRONIZER = 4;

	/**
	 * The feature id for the '<em><b>Data Source Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SYNCHRONIZER__DATA_SOURCE_NAME = MAPPER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Entity</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SYNCHRONIZER__ENTITY = MAPPER_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Strategies</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SYNCHRONIZER__STRATEGIES = MAPPER_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SYNCHRONIZER__NAME = MAPPER_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SYNCHRONIZER__DESCRIPTION = MAPPER_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Mapping</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SYNCHRONIZER__MAPPING = MAPPER_FEATURE_COUNT + 5;

	/**
	 * The number of structural features of the '<em>Synchronizer</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SYNCHRONIZER_FEATURE_COUNT = MAPPER_FEATURE_COUNT + 6;

	/**
	 * The meta object id for the '{@link bpm.mdm.model.Strategy <em>Strategy</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.mdm.model.Strategy
	 * @see bpm.mdm.model.impl.MdmPackageImpl#getStrategy()
	 * @generated
	 */
	int STRATEGY = 5;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRATEGY__NAME = 0;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRATEGY__DESCRIPTION = 1;

	/**
	 * The number of structural features of the '<em>Strategy</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRATEGY_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link bpm.mdm.model.impl.ModelImpl <em>Model</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.mdm.model.impl.ModelImpl
	 * @see bpm.mdm.model.impl.MdmPackageImpl#getModel()
	 * @generated
	 */
	int MODEL = 6;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL__NAME = 0;

	/**
	 * The feature id for the '<em><b>Entities</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL__ENTITIES = 1;

	/**
	 * The feature id for the '<em><b>Synchronizers</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL__SYNCHRONIZERS = 2;

	/**
	 * The feature id for the '<em><b>Data Sources</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL__DATA_SOURCES = 3;

	/**
	 * The number of structural features of the '<em>Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link bpm.mdm.model.impl.AttributeToIntegerMapImpl <em>Attribute To Integer Map</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.mdm.model.impl.AttributeToIntegerMapImpl
	 * @see bpm.mdm.model.impl.MdmPackageImpl#getAttributeToIntegerMap()
	 * @generated
	 */
	int ATTRIBUTE_TO_INTEGER_MAP = 7;

	/**
	 * The feature id for the '<em><b>Key</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_TO_INTEGER_MAP__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_TO_INTEGER_MAP__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Attribute To Integer Map</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_TO_INTEGER_MAP_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link bpm.mdm.model.DataType <em>Data Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.mdm.model.DataType
	 * @see bpm.mdm.model.impl.MdmPackageImpl#getDataType()
	 * @generated
	 */
	int DATA_TYPE = 8;

	/**
	 * The meta object id for the '<em>Data Set</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.datatools.connectivity.oda.design.DataSetDesign
	 * @see bpm.mdm.model.impl.MdmPackageImpl#getDataSet()
	 * @generated
	 */
	int DATA_SET = 9;

	/**
	 * The meta object id for the '<em>Exception</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.lang.Exception
	 * @see bpm.mdm.model.impl.MdmPackageImpl#getException()
	 * @generated
	 */
	int EXCEPTION = 10;

	/**
	 * Returns the meta object for class '{@link bpm.mdm.model.Rule <em>Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Rule</em>'.
	 * @see bpm.mdm.model.Rule
	 * @generated
	 */
	EClass getRule();

	/**
	 * Returns the meta object for the attribute '{@link bpm.mdm.model.Rule#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see bpm.mdm.model.Rule#getName()
	 * @see #getRule()
	 * @generated
	 */
	EAttribute getRule_Name();

	/**
	 * Returns the meta object for the attribute '{@link bpm.mdm.model.Rule#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see bpm.mdm.model.Rule#getDescription()
	 * @see #getRule()
	 * @generated
	 */
	EAttribute getRule_Description();

	/**
	 * Returns the meta object for the attribute '{@link bpm.mdm.model.Rule#isActive <em>Active</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Active</em>'.
	 * @see bpm.mdm.model.Rule#isActive()
	 * @see #getRule()
	 * @generated
	 */
	EAttribute getRule_Active();

	/**
	 * Returns the meta object for class '{@link bpm.mdm.model.Attribute <em>Attribute</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Attribute</em>'.
	 * @see bpm.mdm.model.Attribute
	 * @generated
	 */
	EClass getAttribute();

	/**
	 * Returns the meta object for the containment reference list '{@link bpm.mdm.model.Attribute#getRules <em>Rules</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Rules</em>'.
	 * @see bpm.mdm.model.Attribute#getRules()
	 * @see #getAttribute()
	 * @generated
	 */
	EReference getAttribute_Rules();

	/**
	 * Returns the meta object for the attribute '{@link bpm.mdm.model.Attribute#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see bpm.mdm.model.Attribute#getName()
	 * @see #getAttribute()
	 * @generated
	 */
	EAttribute getAttribute_Name();

	/**
	 * Returns the meta object for the attribute '{@link bpm.mdm.model.Attribute#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see bpm.mdm.model.Attribute#getDescription()
	 * @see #getAttribute()
	 * @generated
	 */
	EAttribute getAttribute_Description();

	/**
	 * Returns the meta object for the attribute '{@link bpm.mdm.model.Attribute#getDataType <em>Data Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Data Type</em>'.
	 * @see bpm.mdm.model.Attribute#getDataType()
	 * @see #getAttribute()
	 * @generated
	 */
	EAttribute getAttribute_DataType();

	/**
	 * Returns the meta object for the attribute '{@link bpm.mdm.model.Attribute#getDefaultValue <em>Default Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Default Value</em>'.
	 * @see bpm.mdm.model.Attribute#getDefaultValue()
	 * @see #getAttribute()
	 * @generated
	 */
	EAttribute getAttribute_DefaultValue();

	/**
	 * Returns the meta object for the attribute '{@link bpm.mdm.model.Attribute#isId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see bpm.mdm.model.Attribute#isId()
	 * @see #getAttribute()
	 * @generated
	 */
	EAttribute getAttribute_Id();

	/**
	 * Returns the meta object for the attribute '{@link bpm.mdm.model.Attribute#isNullAllowed <em>Null Allowed</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Null Allowed</em>'.
	 * @see bpm.mdm.model.Attribute#isNullAllowed()
	 * @see #getAttribute()
	 * @generated
	 */
	EAttribute getAttribute_NullAllowed();

	/**
	 * Returns the meta object for the attribute '{@link bpm.mdm.model.Attribute#getUuid <em>Uuid</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Uuid</em>'.
	 * @see bpm.mdm.model.Attribute#getUuid()
	 * @see #getAttribute()
	 * @generated
	 */
	EAttribute getAttribute_Uuid();

	/**
	 * Returns the meta object for class '{@link bpm.mdm.model.Entity <em>Entity</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Entity</em>'.
	 * @see bpm.mdm.model.Entity
	 * @generated
	 */
	EClass getEntity();

	/**
	 * Returns the meta object for the containment reference list '{@link bpm.mdm.model.Entity#getAttributes <em>Attributes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Attributes</em>'.
	 * @see bpm.mdm.model.Entity#getAttributes()
	 * @see #getEntity()
	 * @generated
	 */
	EReference getEntity_Attributes();

	/**
	 * Returns the meta object for the attribute '{@link bpm.mdm.model.Entity#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see bpm.mdm.model.Entity#getName()
	 * @see #getEntity()
	 * @generated
	 */
	EAttribute getEntity_Name();

	/**
	 * Returns the meta object for the attribute '{@link bpm.mdm.model.Entity#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see bpm.mdm.model.Entity#getDescription()
	 * @see #getEntity()
	 * @generated
	 */
	EAttribute getEntity_Description();

	/**
	 * Returns the meta object for the attribute '{@link bpm.mdm.model.Entity#getUuid <em>Uuid</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Uuid</em>'.
	 * @see bpm.mdm.model.Entity#getUuid()
	 * @see #getEntity()
	 * @generated
	 */
	EAttribute getEntity_Uuid();

	/**
	 * Returns the meta object for class '{@link bpm.mdm.model.Mapper <em>Mapper</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Mapper</em>'.
	 * @see bpm.mdm.model.Mapper
	 * @generated
	 */
	EClass getMapper();

	/**
	 * Returns the meta object for class '{@link bpm.mdm.model.Synchronizer <em>Synchronizer</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Synchronizer</em>'.
	 * @see bpm.mdm.model.Synchronizer
	 * @generated
	 */
	EClass getSynchronizer();

	/**
	 * Returns the meta object for the attribute '{@link bpm.mdm.model.Synchronizer#getDataSourceName <em>Data Source Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Data Source Name</em>'.
	 * @see bpm.mdm.model.Synchronizer#getDataSourceName()
	 * @see #getSynchronizer()
	 * @generated
	 */
	EAttribute getSynchronizer_DataSourceName();

	/**
	 * Returns the meta object for the reference '{@link bpm.mdm.model.Synchronizer#getEntity <em>Entity</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Entity</em>'.
	 * @see bpm.mdm.model.Synchronizer#getEntity()
	 * @see #getSynchronizer()
	 * @generated
	 */
	EReference getSynchronizer_Entity();

	/**
	 * Returns the meta object for the reference list '{@link bpm.mdm.model.Synchronizer#getStrategies <em>Strategies</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Strategies</em>'.
	 * @see bpm.mdm.model.Synchronizer#getStrategies()
	 * @see #getSynchronizer()
	 * @generated
	 */
	EReference getSynchronizer_Strategies();

	/**
	 * Returns the meta object for the attribute '{@link bpm.mdm.model.Synchronizer#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see bpm.mdm.model.Synchronizer#getName()
	 * @see #getSynchronizer()
	 * @generated
	 */
	EAttribute getSynchronizer_Name();

	/**
	 * Returns the meta object for the attribute '{@link bpm.mdm.model.Synchronizer#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see bpm.mdm.model.Synchronizer#getDescription()
	 * @see #getSynchronizer()
	 * @generated
	 */
	EAttribute getSynchronizer_Description();

	/**
	 * Returns the meta object for the map '{@link bpm.mdm.model.Synchronizer#getMapping <em>Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Mapping</em>'.
	 * @see bpm.mdm.model.Synchronizer#getMapping()
	 * @see #getSynchronizer()
	 * @generated
	 */
	EReference getSynchronizer_Mapping();

	/**
	 * Returns the meta object for class '{@link bpm.mdm.model.Strategy <em>Strategy</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Strategy</em>'.
	 * @see bpm.mdm.model.Strategy
	 * @generated
	 */
	EClass getStrategy();

	/**
	 * Returns the meta object for the attribute '{@link bpm.mdm.model.Strategy#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see bpm.mdm.model.Strategy#getName()
	 * @see #getStrategy()
	 * @generated
	 */
	EAttribute getStrategy_Name();

	/**
	 * Returns the meta object for the attribute '{@link bpm.mdm.model.Strategy#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see bpm.mdm.model.Strategy#getDescription()
	 * @see #getStrategy()
	 * @generated
	 */
	EAttribute getStrategy_Description();

	/**
	 * Returns the meta object for class '{@link bpm.mdm.model.Model <em>Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Model</em>'.
	 * @see bpm.mdm.model.Model
	 * @generated
	 */
	EClass getModel();

	/**
	 * Returns the meta object for the attribute '{@link bpm.mdm.model.Model#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see bpm.mdm.model.Model#getName()
	 * @see #getModel()
	 * @generated
	 */
	EAttribute getModel_Name();

	/**
	 * Returns the meta object for the containment reference list '{@link bpm.mdm.model.Model#getEntities <em>Entities</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Entities</em>'.
	 * @see bpm.mdm.model.Model#getEntities()
	 * @see #getModel()
	 * @generated
	 */
	EReference getModel_Entities();

	/**
	 * Returns the meta object for the containment reference list '{@link bpm.mdm.model.Model#getSynchronizers <em>Synchronizers</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Synchronizers</em>'.
	 * @see bpm.mdm.model.Model#getSynchronizers()
	 * @see #getModel()
	 * @generated
	 */
	EReference getModel_Synchronizers();

	/**
	 * Returns the meta object for the attribute list '{@link bpm.mdm.model.Model#getDataSources <em>Data Sources</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Data Sources</em>'.
	 * @see bpm.mdm.model.Model#getDataSources()
	 * @see #getModel()
	 * @generated
	 */
	EAttribute getModel_DataSources();

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>Attribute To Integer Map</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Attribute To Integer Map</em>'.
	 * @see java.util.Map.Entry
	 * @model keyType="bpm.mdm.model.Attribute"
	 *        valueDataType="org.eclipse.emf.ecore.EIntegerObject"
	 * @generated
	 */
	EClass getAttributeToIntegerMap();

	/**
	 * Returns the meta object for the reference '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getAttributeToIntegerMap()
	 * @generated
	 */
	EReference getAttributeToIntegerMap_Key();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getAttributeToIntegerMap()
	 * @generated
	 */
	EAttribute getAttributeToIntegerMap_Value();

	/**
	 * Returns the meta object for enum '{@link bpm.mdm.model.DataType <em>Data Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Data Type</em>'.
	 * @see bpm.mdm.model.DataType
	 * @generated
	 */
	EEnum getDataType();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.datatools.connectivity.oda.design.DataSetDesign <em>Data Set</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Data Set</em>'.
	 * @see org.eclipse.datatools.connectivity.oda.design.DataSetDesign
	 * @model instanceClass="org.eclipse.datatools.connectivity.oda.design.DataSetDesign"
	 * @generated
	 */
	EDataType getDataSet();

	/**
	 * Returns the meta object for data type '{@link java.lang.Exception <em>Exception</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Exception</em>'.
	 * @see java.lang.Exception
	 * @model instanceClass="java.lang.Exception" serializeable="false"
	 * @generated
	 */
	EDataType getException();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	MdmFactory getMdmFactory();

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
		 * The meta object literal for the '{@link bpm.mdm.model.Rule <em>Rule</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.mdm.model.Rule
		 * @see bpm.mdm.model.impl.MdmPackageImpl#getRule()
		 * @generated
		 */
		EClass RULE = eINSTANCE.getRule();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RULE__NAME = eINSTANCE.getRule_Name();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RULE__DESCRIPTION = eINSTANCE.getRule_Description();

		/**
		 * The meta object literal for the '<em><b>Active</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RULE__ACTIVE = eINSTANCE.getRule_Active();

		/**
		 * The meta object literal for the '{@link bpm.mdm.model.impl.AttributeImpl <em>Attribute</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.mdm.model.impl.AttributeImpl
		 * @see bpm.mdm.model.impl.MdmPackageImpl#getAttribute()
		 * @generated
		 */
		EClass ATTRIBUTE = eINSTANCE.getAttribute();

		/**
		 * The meta object literal for the '<em><b>Rules</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ATTRIBUTE__RULES = eINSTANCE.getAttribute_Rules();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTRIBUTE__NAME = eINSTANCE.getAttribute_Name();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTRIBUTE__DESCRIPTION = eINSTANCE.getAttribute_Description();

		/**
		 * The meta object literal for the '<em><b>Data Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTRIBUTE__DATA_TYPE = eINSTANCE.getAttribute_DataType();

		/**
		 * The meta object literal for the '<em><b>Default Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTRIBUTE__DEFAULT_VALUE = eINSTANCE.getAttribute_DefaultValue();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTRIBUTE__ID = eINSTANCE.getAttribute_Id();

		/**
		 * The meta object literal for the '<em><b>Null Allowed</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTRIBUTE__NULL_ALLOWED = eINSTANCE.getAttribute_NullAllowed();

		/**
		 * The meta object literal for the '<em><b>Uuid</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTRIBUTE__UUID = eINSTANCE.getAttribute_Uuid();

		/**
		 * The meta object literal for the '{@link bpm.mdm.model.impl.EntityImpl <em>Entity</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.mdm.model.impl.EntityImpl
		 * @see bpm.mdm.model.impl.MdmPackageImpl#getEntity()
		 * @generated
		 */
		EClass ENTITY = eINSTANCE.getEntity();

		/**
		 * The meta object literal for the '<em><b>Attributes</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ENTITY__ATTRIBUTES = eINSTANCE.getEntity_Attributes();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ENTITY__NAME = eINSTANCE.getEntity_Name();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ENTITY__DESCRIPTION = eINSTANCE.getEntity_Description();

		/**
		 * The meta object literal for the '<em><b>Uuid</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ENTITY__UUID = eINSTANCE.getEntity_Uuid();

		/**
		 * The meta object literal for the '{@link bpm.mdm.model.Mapper <em>Mapper</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.mdm.model.Mapper
		 * @see bpm.mdm.model.impl.MdmPackageImpl#getMapper()
		 * @generated
		 */
		EClass MAPPER = eINSTANCE.getMapper();

		/**
		 * The meta object literal for the '{@link bpm.mdm.model.impl.SynchronizerImpl <em>Synchronizer</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.mdm.model.impl.SynchronizerImpl
		 * @see bpm.mdm.model.impl.MdmPackageImpl#getSynchronizer()
		 * @generated
		 */
		EClass SYNCHRONIZER = eINSTANCE.getSynchronizer();

		/**
		 * The meta object literal for the '<em><b>Data Source Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SYNCHRONIZER__DATA_SOURCE_NAME = eINSTANCE.getSynchronizer_DataSourceName();

		/**
		 * The meta object literal for the '<em><b>Entity</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SYNCHRONIZER__ENTITY = eINSTANCE.getSynchronizer_Entity();

		/**
		 * The meta object literal for the '<em><b>Strategies</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SYNCHRONIZER__STRATEGIES = eINSTANCE.getSynchronizer_Strategies();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SYNCHRONIZER__NAME = eINSTANCE.getSynchronizer_Name();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SYNCHRONIZER__DESCRIPTION = eINSTANCE.getSynchronizer_Description();

		/**
		 * The meta object literal for the '<em><b>Mapping</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SYNCHRONIZER__MAPPING = eINSTANCE.getSynchronizer_Mapping();

		/**
		 * The meta object literal for the '{@link bpm.mdm.model.Strategy <em>Strategy</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.mdm.model.Strategy
		 * @see bpm.mdm.model.impl.MdmPackageImpl#getStrategy()
		 * @generated
		 */
		EClass STRATEGY = eINSTANCE.getStrategy();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STRATEGY__NAME = eINSTANCE.getStrategy_Name();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STRATEGY__DESCRIPTION = eINSTANCE.getStrategy_Description();

		/**
		 * The meta object literal for the '{@link bpm.mdm.model.impl.ModelImpl <em>Model</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.mdm.model.impl.ModelImpl
		 * @see bpm.mdm.model.impl.MdmPackageImpl#getModel()
		 * @generated
		 */
		EClass MODEL = eINSTANCE.getModel();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MODEL__NAME = eINSTANCE.getModel_Name();

		/**
		 * The meta object literal for the '<em><b>Entities</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MODEL__ENTITIES = eINSTANCE.getModel_Entities();

		/**
		 * The meta object literal for the '<em><b>Synchronizers</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MODEL__SYNCHRONIZERS = eINSTANCE.getModel_Synchronizers();

		/**
		 * The meta object literal for the '<em><b>Data Sources</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MODEL__DATA_SOURCES = eINSTANCE.getModel_DataSources();

		/**
		 * The meta object literal for the '{@link bpm.mdm.model.impl.AttributeToIntegerMapImpl <em>Attribute To Integer Map</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.mdm.model.impl.AttributeToIntegerMapImpl
		 * @see bpm.mdm.model.impl.MdmPackageImpl#getAttributeToIntegerMap()
		 * @generated
		 */
		EClass ATTRIBUTE_TO_INTEGER_MAP = eINSTANCE.getAttributeToIntegerMap();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ATTRIBUTE_TO_INTEGER_MAP__KEY = eINSTANCE.getAttributeToIntegerMap_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTRIBUTE_TO_INTEGER_MAP__VALUE = eINSTANCE.getAttributeToIntegerMap_Value();

		/**
		 * The meta object literal for the '{@link bpm.mdm.model.DataType <em>Data Type</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.mdm.model.DataType
		 * @see bpm.mdm.model.impl.MdmPackageImpl#getDataType()
		 * @generated
		 */
		EEnum DATA_TYPE = eINSTANCE.getDataType();

		/**
		 * The meta object literal for the '<em>Data Set</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.datatools.connectivity.oda.design.DataSetDesign
		 * @see bpm.mdm.model.impl.MdmPackageImpl#getDataSet()
		 * @generated
		 */
		EDataType DATA_SET = eINSTANCE.getDataSet();

		/**
		 * The meta object literal for the '<em>Exception</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.lang.Exception
		 * @see bpm.mdm.model.impl.MdmPackageImpl#getException()
		 * @generated
		 */
		EDataType EXCEPTION = eINSTANCE.getException();

	}

} //MdmPackage
