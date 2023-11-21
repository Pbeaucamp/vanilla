/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.model;

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
 * @see bpm.united.olap.api.model.ModelFactory
 * @model kind="package"
 * @generated
 */
public interface ModelPackage extends EPackage {
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
	String eNS_URI = "http://api/model/1.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "model";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ModelPackage eINSTANCE = bpm.united.olap.api.model.impl.ModelPackageImpl.init();

	/**
	 * The meta object id for the '{@link bpm.united.olap.api.model.impl.SchemaImpl <em>Schema</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.united.olap.api.model.impl.SchemaImpl
	 * @see bpm.united.olap.api.model.impl.ModelPackageImpl#getSchema()
	 * @generated
	 */
	int SCHEMA = 0;

	/**
	 * The feature id for the '<em><b>Cubes</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__CUBES = 0;

	/**
	 * The feature id for the '<em><b>Datasources</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__DATASOURCES = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__NAME = 2;

	/**
	 * The feature id for the '<em><b>Dimensions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__DIMENSIONS = 3;

	/**
	 * The feature id for the '<em><b>Measures</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__MEASURES = 4;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__ID = 5;

	/**
	 * The feature id for the '<em><b>Last Modification Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__LAST_MODIFICATION_DATE = 6;

	/**
	 * The number of structural features of the '<em>Schema</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA_FEATURE_COUNT = 7;

	/**
	 * The meta object id for the '{@link bpm.united.olap.api.model.impl.CubeImpl <em>Cube</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.united.olap.api.model.impl.CubeImpl
	 * @see bpm.united.olap.api.model.impl.ModelPackageImpl#getCube()
	 * @generated
	 */
	int CUBE = 1;

	/**
	 * The feature id for the '<em><b>Measures</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CUBE__MEASURES = 0;

	/**
	 * The feature id for the '<em><b>Dimensions</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CUBE__DIMENSIONS = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CUBE__NAME = 2;

	/**
	 * The number of structural features of the '<em>Cube</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CUBE_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link bpm.united.olap.api.model.impl.ElementDefinitionImpl <em>Element Definition</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.united.olap.api.model.impl.ElementDefinitionImpl
	 * @see bpm.united.olap.api.model.impl.ModelPackageImpl#getElementDefinition()
	 * @generated
	 */
	int ELEMENT_DEFINITION = 7;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_DEFINITION__NAME = 0;

	/**
	 * The feature id for the '<em><b>Uname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_DEFINITION__UNAME = 1;

	/**
	 * The feature id for the '<em><b>Caption</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_DEFINITION__CAPTION = 2;

	/**
	 * The feature id for the '<em><b>Is Visible</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_DEFINITION__IS_VISIBLE = 3;

	/**
	 * The number of structural features of the '<em>Element Definition</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_DEFINITION_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link bpm.united.olap.api.model.impl.DimensionImpl <em>Dimension</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.united.olap.api.model.impl.DimensionImpl
	 * @see bpm.united.olap.api.model.impl.ModelPackageImpl#getDimension()
	 * @generated
	 */
	int DIMENSION = 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DIMENSION__NAME = ELEMENT_DEFINITION__NAME;

	/**
	 * The feature id for the '<em><b>Uname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DIMENSION__UNAME = ELEMENT_DEFINITION__UNAME;

	/**
	 * The feature id for the '<em><b>Caption</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DIMENSION__CAPTION = ELEMENT_DEFINITION__CAPTION;

	/**
	 * The feature id for the '<em><b>Is Visible</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DIMENSION__IS_VISIBLE = ELEMENT_DEFINITION__IS_VISIBLE;

	/**
	 * The feature id for the '<em><b>Data Object</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DIMENSION__DATA_OBJECT = ELEMENT_DEFINITION_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Hierarchies</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DIMENSION__HIERARCHIES = ELEMENT_DEFINITION_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Parent Schema</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DIMENSION__PARENT_SCHEMA = ELEMENT_DEFINITION_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Is One Column Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DIMENSION__IS_ONE_COLUMN_DATE = ELEMENT_DEFINITION_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Dimension</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DIMENSION_FEATURE_COUNT = ELEMENT_DEFINITION_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link bpm.united.olap.api.model.impl.HierarchyImpl <em>Hierarchy</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.united.olap.api.model.impl.HierarchyImpl
	 * @see bpm.united.olap.api.model.impl.ModelPackageImpl#getHierarchy()
	 * @generated
	 */
	int HIERARCHY = 3;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HIERARCHY__NAME = ELEMENT_DEFINITION__NAME;

	/**
	 * The feature id for the '<em><b>Uname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HIERARCHY__UNAME = ELEMENT_DEFINITION__UNAME;

	/**
	 * The feature id for the '<em><b>Caption</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HIERARCHY__CAPTION = ELEMENT_DEFINITION__CAPTION;

	/**
	 * The feature id for the '<em><b>Is Visible</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HIERARCHY__IS_VISIBLE = ELEMENT_DEFINITION__IS_VISIBLE;

	/**
	 * The feature id for the '<em><b>Levels</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HIERARCHY__LEVELS = ELEMENT_DEFINITION_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Parent Dimension</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HIERARCHY__PARENT_DIMENSION = ELEMENT_DEFINITION_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Is Closure Hierarchy</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HIERARCHY__IS_CLOSURE_HIERARCHY = ELEMENT_DEFINITION_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Hierarchy</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HIERARCHY_FEATURE_COUNT = ELEMENT_DEFINITION_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link bpm.united.olap.api.model.impl.LevelImpl <em>Level</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.united.olap.api.model.impl.LevelImpl
	 * @see bpm.united.olap.api.model.impl.ModelPackageImpl#getLevel()
	 * @generated
	 */
	int LEVEL = 4;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LEVEL__NAME = ELEMENT_DEFINITION__NAME;

	/**
	 * The feature id for the '<em><b>Uname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LEVEL__UNAME = ELEMENT_DEFINITION__UNAME;

	/**
	 * The feature id for the '<em><b>Caption</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LEVEL__CAPTION = ELEMENT_DEFINITION__CAPTION;

	/**
	 * The feature id for the '<em><b>Is Visible</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LEVEL__IS_VISIBLE = ELEMENT_DEFINITION__IS_VISIBLE;

	/**
	 * The feature id for the '<em><b>Item</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LEVEL__ITEM = ELEMENT_DEFINITION_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Parent Level</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LEVEL__PARENT_LEVEL = ELEMENT_DEFINITION_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Sub Level</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LEVEL__SUB_LEVEL = ELEMENT_DEFINITION_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Members</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LEVEL__MEMBERS = ELEMENT_DEFINITION_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Parent Hierarchy</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LEVEL__PARENT_HIERARCHY = ELEMENT_DEFINITION_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Member Properties</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LEVEL__MEMBER_PROPERTIES = ELEMENT_DEFINITION_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Order Item</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LEVEL__ORDER_ITEM = ELEMENT_DEFINITION_FEATURE_COUNT + 6;

	/**
	 * The number of structural features of the '<em>Level</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LEVEL_FEATURE_COUNT = ELEMENT_DEFINITION_FEATURE_COUNT + 7;

	/**
	 * The meta object id for the '{@link bpm.united.olap.api.model.impl.MemberImpl <em>Member</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.united.olap.api.model.impl.MemberImpl
	 * @see bpm.united.olap.api.model.impl.ModelPackageImpl#getMember()
	 * @generated
	 */
	int MEMBER = 5;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEMBER__NAME = ELEMENT_DEFINITION__NAME;

	/**
	 * The feature id for the '<em><b>Uname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEMBER__UNAME = ELEMENT_DEFINITION__UNAME;

	/**
	 * The feature id for the '<em><b>Caption</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEMBER__CAPTION = ELEMENT_DEFINITION__CAPTION;

	/**
	 * The feature id for the '<em><b>Is Visible</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEMBER__IS_VISIBLE = ELEMENT_DEFINITION__IS_VISIBLE;

	/**
	 * The feature id for the '<em><b>Parent Member</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEMBER__PARENT_MEMBER = ELEMENT_DEFINITION_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Sub Members</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEMBER__SUB_MEMBERS = ELEMENT_DEFINITION_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Parent Level</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEMBER__PARENT_LEVEL = ELEMENT_DEFINITION_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Member Relations Uname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEMBER__MEMBER_RELATIONS_UNAME = ELEMENT_DEFINITION_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Properties</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEMBER__PROPERTIES = ELEMENT_DEFINITION_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Order Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEMBER__ORDER_VALUE = ELEMENT_DEFINITION_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Order Uname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEMBER__ORDER_UNAME = ELEMENT_DEFINITION_FEATURE_COUNT + 6;

	/**
	 * The number of structural features of the '<em>Member</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEMBER_FEATURE_COUNT = ELEMENT_DEFINITION_FEATURE_COUNT + 7;

	/**
	 * The meta object id for the '{@link bpm.united.olap.api.model.impl.MeasureImpl <em>Measure</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.united.olap.api.model.impl.MeasureImpl
	 * @see bpm.united.olap.api.model.impl.ModelPackageImpl#getMeasure()
	 * @generated
	 */
	int MEASURE = 6;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEASURE__NAME = ELEMENT_DEFINITION__NAME;

	/**
	 * The feature id for the '<em><b>Uname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEASURE__UNAME = ELEMENT_DEFINITION__UNAME;

	/**
	 * The feature id for the '<em><b>Caption</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEASURE__CAPTION = ELEMENT_DEFINITION__CAPTION;

	/**
	 * The feature id for the '<em><b>Is Visible</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEASURE__IS_VISIBLE = ELEMENT_DEFINITION__IS_VISIBLE;

	/**
	 * The feature id for the '<em><b>Item</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEASURE__ITEM = ELEMENT_DEFINITION_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Calculation Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEASURE__CALCULATION_TYPE = ELEMENT_DEFINITION_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Parent Schema</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEASURE__PARENT_SCHEMA = ELEMENT_DEFINITION_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Calculatd Formula</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEASURE__CALCULATD_FORMULA = ELEMENT_DEFINITION_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Measure</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEASURE_FEATURE_COUNT = ELEMENT_DEFINITION_FEATURE_COUNT + 4;


	/**
	 * The meta object id for the '{@link bpm.united.olap.api.model.impl.MemberPropertyImpl <em>Member Property</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpm.united.olap.api.model.impl.MemberPropertyImpl
	 * @see bpm.united.olap.api.model.impl.ModelPackageImpl#getMemberProperty()
	 * @generated
	 */
	int MEMBER_PROPERTY = 8;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEMBER_PROPERTY__NAME = 0;

	/**
	 * The feature id for the '<em><b>Value Item</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEMBER_PROPERTY__VALUE_ITEM = 1;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEMBER_PROPERTY__TYPE = 2;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEMBER_PROPERTY__VALUE = 3;

	/**
	 * The number of structural features of the '<em>Member Property</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEMBER_PROPERTY_FEATURE_COUNT = 4;


	/**
	 * Returns the meta object for class '{@link bpm.united.olap.api.model.Schema <em>Schema</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Schema</em>'.
	 * @see bpm.united.olap.api.model.Schema
	 * @generated
	 */
	EClass getSchema();

	/**
	 * Returns the meta object for the containment reference list '{@link bpm.united.olap.api.model.Schema#getCubes <em>Cubes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Cubes</em>'.
	 * @see bpm.united.olap.api.model.Schema#getCubes()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_Cubes();

	/**
	 * Returns the meta object for the containment reference list '{@link bpm.united.olap.api.model.Schema#getDatasources <em>Datasources</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Datasources</em>'.
	 * @see bpm.united.olap.api.model.Schema#getDatasources()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_Datasources();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.model.Schema#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see bpm.united.olap.api.model.Schema#getName()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_Name();

	/**
	 * Returns the meta object for the containment reference list '{@link bpm.united.olap.api.model.Schema#getDimensions <em>Dimensions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Dimensions</em>'.
	 * @see bpm.united.olap.api.model.Schema#getDimensions()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_Dimensions();

	/**
	 * Returns the meta object for the containment reference list '{@link bpm.united.olap.api.model.Schema#getMeasures <em>Measures</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Measures</em>'.
	 * @see bpm.united.olap.api.model.Schema#getMeasures()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_Measures();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.model.Schema#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see bpm.united.olap.api.model.Schema#getId()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_Id();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.model.Schema#getLastModificationDate <em>Last Modification Date</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Last Modification Date</em>'.
	 * @see bpm.united.olap.api.model.Schema#getLastModificationDate()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_LastModificationDate();

	/**
	 * Returns the meta object for class '{@link bpm.united.olap.api.model.Cube <em>Cube</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Cube</em>'.
	 * @see bpm.united.olap.api.model.Cube
	 * @generated
	 */
	EClass getCube();

	/**
	 * Returns the meta object for the reference list '{@link bpm.united.olap.api.model.Cube#getMeasures <em>Measures</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Measures</em>'.
	 * @see bpm.united.olap.api.model.Cube#getMeasures()
	 * @see #getCube()
	 * @generated
	 */
	EReference getCube_Measures();

	/**
	 * Returns the meta object for the reference list '{@link bpm.united.olap.api.model.Cube#getDimensions <em>Dimensions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Dimensions</em>'.
	 * @see bpm.united.olap.api.model.Cube#getDimensions()
	 * @see #getCube()
	 * @generated
	 */
	EReference getCube_Dimensions();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.model.Cube#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see bpm.united.olap.api.model.Cube#getName()
	 * @see #getCube()
	 * @generated
	 */
	EAttribute getCube_Name();

	/**
	 * Returns the meta object for class '{@link bpm.united.olap.api.model.Dimension <em>Dimension</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Dimension</em>'.
	 * @see bpm.united.olap.api.model.Dimension
	 * @generated
	 */
	EClass getDimension();

	/**
	 * Returns the meta object for the reference '{@link bpm.united.olap.api.model.Dimension#getDataObject <em>Data Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Data Object</em>'.
	 * @see bpm.united.olap.api.model.Dimension#getDataObject()
	 * @see #getDimension()
	 * @generated
	 */
	EReference getDimension_DataObject();

	/**
	 * Returns the meta object for the containment reference list '{@link bpm.united.olap.api.model.Dimension#getHierarchies <em>Hierarchies</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Hierarchies</em>'.
	 * @see bpm.united.olap.api.model.Dimension#getHierarchies()
	 * @see #getDimension()
	 * @generated
	 */
	EReference getDimension_Hierarchies();

	/**
	 * Returns the meta object for the container reference '{@link bpm.united.olap.api.model.Dimension#getParentSchema <em>Parent Schema</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Parent Schema</em>'.
	 * @see bpm.united.olap.api.model.Dimension#getParentSchema()
	 * @see #getDimension()
	 * @generated
	 */
	EReference getDimension_ParentSchema();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.model.Dimension#isIsOneColumnDate <em>Is One Column Date</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Is One Column Date</em>'.
	 * @see bpm.united.olap.api.model.Dimension#isIsOneColumnDate()
	 * @see #getDimension()
	 * @generated
	 */
	EAttribute getDimension_IsOneColumnDate();

	/**
	 * Returns the meta object for class '{@link bpm.united.olap.api.model.Hierarchy <em>Hierarchy</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Hierarchy</em>'.
	 * @see bpm.united.olap.api.model.Hierarchy
	 * @generated
	 */
	EClass getHierarchy();

	/**
	 * Returns the meta object for the containment reference list '{@link bpm.united.olap.api.model.Hierarchy#getLevels <em>Levels</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Levels</em>'.
	 * @see bpm.united.olap.api.model.Hierarchy#getLevels()
	 * @see #getHierarchy()
	 * @generated
	 */
	EReference getHierarchy_Levels();

	/**
	 * Returns the meta object for the container reference '{@link bpm.united.olap.api.model.Hierarchy#getParentDimension <em>Parent Dimension</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Parent Dimension</em>'.
	 * @see bpm.united.olap.api.model.Hierarchy#getParentDimension()
	 * @see #getHierarchy()
	 * @generated
	 */
	EReference getHierarchy_ParentDimension();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.model.Hierarchy#isIsClosureHierarchy <em>Is Closure Hierarchy</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Is Closure Hierarchy</em>'.
	 * @see bpm.united.olap.api.model.Hierarchy#isIsClosureHierarchy()
	 * @see #getHierarchy()
	 * @generated
	 */
	EAttribute getHierarchy_IsClosureHierarchy();

	/**
	 * Returns the meta object for class '{@link bpm.united.olap.api.model.Level <em>Level</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Level</em>'.
	 * @see bpm.united.olap.api.model.Level
	 * @generated
	 */
	EClass getLevel();

	/**
	 * Returns the meta object for the reference '{@link bpm.united.olap.api.model.Level#getItem <em>Item</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Item</em>'.
	 * @see bpm.united.olap.api.model.Level#getItem()
	 * @see #getLevel()
	 * @generated
	 */
	EReference getLevel_Item();

	/**
	 * Returns the meta object for the reference '{@link bpm.united.olap.api.model.Level#getParentLevel <em>Parent Level</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Parent Level</em>'.
	 * @see bpm.united.olap.api.model.Level#getParentLevel()
	 * @see #getLevel()
	 * @generated
	 */
	EReference getLevel_ParentLevel();

	/**
	 * Returns the meta object for the reference '{@link bpm.united.olap.api.model.Level#getSubLevel <em>Sub Level</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Sub Level</em>'.
	 * @see bpm.united.olap.api.model.Level#getSubLevel()
	 * @see #getLevel()
	 * @generated
	 */
	EReference getLevel_SubLevel();

	/**
	 * Returns the meta object for the containment reference list '{@link bpm.united.olap.api.model.Level#getMembers <em>Members</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Members</em>'.
	 * @see bpm.united.olap.api.model.Level#getMembers()
	 * @see #getLevel()
	 * @generated
	 */
	EReference getLevel_Members();

	/**
	 * Returns the meta object for the container reference '{@link bpm.united.olap.api.model.Level#getParentHierarchy <em>Parent Hierarchy</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Parent Hierarchy</em>'.
	 * @see bpm.united.olap.api.model.Level#getParentHierarchy()
	 * @see #getLevel()
	 * @generated
	 */
	EReference getLevel_ParentHierarchy();

	/**
	 * Returns the meta object for the containment reference list '{@link bpm.united.olap.api.model.Level#getMemberProperties <em>Member Properties</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Member Properties</em>'.
	 * @see bpm.united.olap.api.model.Level#getMemberProperties()
	 * @see #getLevel()
	 * @generated
	 */
	EReference getLevel_MemberProperties();

	/**
	 * Returns the meta object for the reference '{@link bpm.united.olap.api.model.Level#getOrderItem <em>Order Item</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Order Item</em>'.
	 * @see bpm.united.olap.api.model.Level#getOrderItem()
	 * @see #getLevel()
	 * @generated
	 */
	EReference getLevel_OrderItem();

	/**
	 * Returns the meta object for class '{@link bpm.united.olap.api.model.Member <em>Member</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Member</em>'.
	 * @see bpm.united.olap.api.model.Member
	 * @generated
	 */
	EClass getMember();

	/**
	 * Returns the meta object for the reference '{@link bpm.united.olap.api.model.Member#getParentMember <em>Parent Member</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Parent Member</em>'.
	 * @see bpm.united.olap.api.model.Member#getParentMember()
	 * @see #getMember()
	 * @generated
	 */
	EReference getMember_ParentMember();

	/**
	 * Returns the meta object for the reference list '{@link bpm.united.olap.api.model.Member#getSubMembers <em>Sub Members</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Sub Members</em>'.
	 * @see bpm.united.olap.api.model.Member#getSubMembers()
	 * @see #getMember()
	 * @generated
	 */
	EReference getMember_SubMembers();

	/**
	 * Returns the meta object for the container reference '{@link bpm.united.olap.api.model.Member#getParentLevel <em>Parent Level</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Parent Level</em>'.
	 * @see bpm.united.olap.api.model.Member#getParentLevel()
	 * @see #getMember()
	 * @generated
	 */
	EReference getMember_ParentLevel();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.model.Member#getMemberRelationsUname <em>Member Relations Uname</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Member Relations Uname</em>'.
	 * @see bpm.united.olap.api.model.Member#getMemberRelationsUname()
	 * @see #getMember()
	 * @generated
	 */
	EAttribute getMember_MemberRelationsUname();

	/**
	 * Returns the meta object for the containment reference list '{@link bpm.united.olap.api.model.Member#getProperties <em>Properties</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Properties</em>'.
	 * @see bpm.united.olap.api.model.Member#getProperties()
	 * @see #getMember()
	 * @generated
	 */
	EReference getMember_Properties();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.model.Member#getOrderValue <em>Order Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Order Value</em>'.
	 * @see bpm.united.olap.api.model.Member#getOrderValue()
	 * @see #getMember()
	 * @generated
	 */
	EAttribute getMember_OrderValue();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.model.Member#getOrderUname <em>Order Uname</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Order Uname</em>'.
	 * @see bpm.united.olap.api.model.Member#getOrderUname()
	 * @see #getMember()
	 * @generated
	 */
	EAttribute getMember_OrderUname();

	/**
	 * Returns the meta object for class '{@link bpm.united.olap.api.model.Measure <em>Measure</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Measure</em>'.
	 * @see bpm.united.olap.api.model.Measure
	 * @generated
	 */
	EClass getMeasure();

	/**
	 * Returns the meta object for the reference '{@link bpm.united.olap.api.model.Measure#getItem <em>Item</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Item</em>'.
	 * @see bpm.united.olap.api.model.Measure#getItem()
	 * @see #getMeasure()
	 * @generated
	 */
	EReference getMeasure_Item();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.model.Measure#getCalculationType <em>Calculation Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Calculation Type</em>'.
	 * @see bpm.united.olap.api.model.Measure#getCalculationType()
	 * @see #getMeasure()
	 * @generated
	 */
	EAttribute getMeasure_CalculationType();

	/**
	 * Returns the meta object for the container reference '{@link bpm.united.olap.api.model.Measure#getParentSchema <em>Parent Schema</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Parent Schema</em>'.
	 * @see bpm.united.olap.api.model.Measure#getParentSchema()
	 * @see #getMeasure()
	 * @generated
	 */
	EReference getMeasure_ParentSchema();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.model.Measure#getCalculatdFormula <em>Calculatd Formula</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Calculatd Formula</em>'.
	 * @see bpm.united.olap.api.model.Measure#getCalculatdFormula()
	 * @see #getMeasure()
	 * @generated
	 */
	EAttribute getMeasure_CalculatdFormula();

	/**
	 * Returns the meta object for class '{@link bpm.united.olap.api.model.ElementDefinition <em>Element Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Element Definition</em>'.
	 * @see bpm.united.olap.api.model.ElementDefinition
	 * @generated
	 */
	EClass getElementDefinition();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.model.ElementDefinition#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see bpm.united.olap.api.model.ElementDefinition#getName()
	 * @see #getElementDefinition()
	 * @generated
	 */
	EAttribute getElementDefinition_Name();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.model.ElementDefinition#getUname <em>Uname</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Uname</em>'.
	 * @see bpm.united.olap.api.model.ElementDefinition#getUname()
	 * @see #getElementDefinition()
	 * @generated
	 */
	EAttribute getElementDefinition_Uname();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.model.ElementDefinition#getCaption <em>Caption</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Caption</em>'.
	 * @see bpm.united.olap.api.model.ElementDefinition#getCaption()
	 * @see #getElementDefinition()
	 * @generated
	 */
	EAttribute getElementDefinition_Caption();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.model.ElementDefinition#isIsVisible <em>Is Visible</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Is Visible</em>'.
	 * @see bpm.united.olap.api.model.ElementDefinition#isIsVisible()
	 * @see #getElementDefinition()
	 * @generated
	 */
	EAttribute getElementDefinition_IsVisible();

	/**
	 * Returns the meta object for class '{@link bpm.united.olap.api.model.MemberProperty <em>Member Property</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Member Property</em>'.
	 * @see bpm.united.olap.api.model.MemberProperty
	 * @generated
	 */
	EClass getMemberProperty();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.model.MemberProperty#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see bpm.united.olap.api.model.MemberProperty#getName()
	 * @see #getMemberProperty()
	 * @generated
	 */
	EAttribute getMemberProperty_Name();

	/**
	 * Returns the meta object for the reference '{@link bpm.united.olap.api.model.MemberProperty#getValueItem <em>Value Item</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Value Item</em>'.
	 * @see bpm.united.olap.api.model.MemberProperty#getValueItem()
	 * @see #getMemberProperty()
	 * @generated
	 */
	EReference getMemberProperty_ValueItem();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.model.MemberProperty#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see bpm.united.olap.api.model.MemberProperty#getValue()
	 * @see #getMemberProperty()
	 * @generated
	 */
	EAttribute getMemberProperty_Value();

	/**
	 * Returns the meta object for the attribute '{@link bpm.united.olap.api.model.MemberProperty#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see bpm.united.olap.api.model.MemberProperty#getType()
	 * @see #getMemberProperty()
	 * @generated
	 */
	EAttribute getMemberProperty_Type();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	ModelFactory getModelFactory();

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
		 * The meta object literal for the '{@link bpm.united.olap.api.model.impl.SchemaImpl <em>Schema</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.united.olap.api.model.impl.SchemaImpl
		 * @see bpm.united.olap.api.model.impl.ModelPackageImpl#getSchema()
		 * @generated
		 */
		EClass SCHEMA = eINSTANCE.getSchema();

		/**
		 * The meta object literal for the '<em><b>Cubes</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__CUBES = eINSTANCE.getSchema_Cubes();

		/**
		 * The meta object literal for the '<em><b>Datasources</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__DATASOURCES = eINSTANCE.getSchema_Datasources();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__NAME = eINSTANCE.getSchema_Name();

		/**
		 * The meta object literal for the '<em><b>Dimensions</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__DIMENSIONS = eINSTANCE.getSchema_Dimensions();

		/**
		 * The meta object literal for the '<em><b>Measures</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__MEASURES = eINSTANCE.getSchema_Measures();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__ID = eINSTANCE.getSchema_Id();

		/**
		 * The meta object literal for the '<em><b>Last Modification Date</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__LAST_MODIFICATION_DATE = eINSTANCE.getSchema_LastModificationDate();

		/**
		 * The meta object literal for the '{@link bpm.united.olap.api.model.impl.CubeImpl <em>Cube</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.united.olap.api.model.impl.CubeImpl
		 * @see bpm.united.olap.api.model.impl.ModelPackageImpl#getCube()
		 * @generated
		 */
		EClass CUBE = eINSTANCE.getCube();

		/**
		 * The meta object literal for the '<em><b>Measures</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CUBE__MEASURES = eINSTANCE.getCube_Measures();

		/**
		 * The meta object literal for the '<em><b>Dimensions</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CUBE__DIMENSIONS = eINSTANCE.getCube_Dimensions();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CUBE__NAME = eINSTANCE.getCube_Name();

		/**
		 * The meta object literal for the '{@link bpm.united.olap.api.model.impl.DimensionImpl <em>Dimension</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.united.olap.api.model.impl.DimensionImpl
		 * @see bpm.united.olap.api.model.impl.ModelPackageImpl#getDimension()
		 * @generated
		 */
		EClass DIMENSION = eINSTANCE.getDimension();

		/**
		 * The meta object literal for the '<em><b>Data Object</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DIMENSION__DATA_OBJECT = eINSTANCE.getDimension_DataObject();

		/**
		 * The meta object literal for the '<em><b>Hierarchies</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DIMENSION__HIERARCHIES = eINSTANCE.getDimension_Hierarchies();

		/**
		 * The meta object literal for the '<em><b>Parent Schema</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DIMENSION__PARENT_SCHEMA = eINSTANCE.getDimension_ParentSchema();

		/**
		 * The meta object literal for the '<em><b>Is One Column Date</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DIMENSION__IS_ONE_COLUMN_DATE = eINSTANCE.getDimension_IsOneColumnDate();

		/**
		 * The meta object literal for the '{@link bpm.united.olap.api.model.impl.HierarchyImpl <em>Hierarchy</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.united.olap.api.model.impl.HierarchyImpl
		 * @see bpm.united.olap.api.model.impl.ModelPackageImpl#getHierarchy()
		 * @generated
		 */
		EClass HIERARCHY = eINSTANCE.getHierarchy();

		/**
		 * The meta object literal for the '<em><b>Levels</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference HIERARCHY__LEVELS = eINSTANCE.getHierarchy_Levels();

		/**
		 * The meta object literal for the '<em><b>Parent Dimension</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference HIERARCHY__PARENT_DIMENSION = eINSTANCE.getHierarchy_ParentDimension();

		/**
		 * The meta object literal for the '<em><b>Is Closure Hierarchy</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute HIERARCHY__IS_CLOSURE_HIERARCHY = eINSTANCE.getHierarchy_IsClosureHierarchy();

		/**
		 * The meta object literal for the '{@link bpm.united.olap.api.model.impl.LevelImpl <em>Level</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.united.olap.api.model.impl.LevelImpl
		 * @see bpm.united.olap.api.model.impl.ModelPackageImpl#getLevel()
		 * @generated
		 */
		EClass LEVEL = eINSTANCE.getLevel();

		/**
		 * The meta object literal for the '<em><b>Item</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LEVEL__ITEM = eINSTANCE.getLevel_Item();

		/**
		 * The meta object literal for the '<em><b>Parent Level</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LEVEL__PARENT_LEVEL = eINSTANCE.getLevel_ParentLevel();

		/**
		 * The meta object literal for the '<em><b>Sub Level</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LEVEL__SUB_LEVEL = eINSTANCE.getLevel_SubLevel();

		/**
		 * The meta object literal for the '<em><b>Members</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LEVEL__MEMBERS = eINSTANCE.getLevel_Members();

		/**
		 * The meta object literal for the '<em><b>Parent Hierarchy</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LEVEL__PARENT_HIERARCHY = eINSTANCE.getLevel_ParentHierarchy();

		/**
		 * The meta object literal for the '<em><b>Member Properties</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LEVEL__MEMBER_PROPERTIES = eINSTANCE.getLevel_MemberProperties();

		/**
		 * The meta object literal for the '<em><b>Order Item</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LEVEL__ORDER_ITEM = eINSTANCE.getLevel_OrderItem();

		/**
		 * The meta object literal for the '{@link bpm.united.olap.api.model.impl.MemberImpl <em>Member</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.united.olap.api.model.impl.MemberImpl
		 * @see bpm.united.olap.api.model.impl.ModelPackageImpl#getMember()
		 * @generated
		 */
		EClass MEMBER = eINSTANCE.getMember();

		/**
		 * The meta object literal for the '<em><b>Parent Member</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MEMBER__PARENT_MEMBER = eINSTANCE.getMember_ParentMember();

		/**
		 * The meta object literal for the '<em><b>Sub Members</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MEMBER__SUB_MEMBERS = eINSTANCE.getMember_SubMembers();

		/**
		 * The meta object literal for the '<em><b>Parent Level</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MEMBER__PARENT_LEVEL = eINSTANCE.getMember_ParentLevel();

		/**
		 * The meta object literal for the '<em><b>Member Relations Uname</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MEMBER__MEMBER_RELATIONS_UNAME = eINSTANCE.getMember_MemberRelationsUname();

		/**
		 * The meta object literal for the '<em><b>Properties</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MEMBER__PROPERTIES = eINSTANCE.getMember_Properties();

		/**
		 * The meta object literal for the '<em><b>Order Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MEMBER__ORDER_VALUE = eINSTANCE.getMember_OrderValue();

		/**
		 * The meta object literal for the '<em><b>Order Uname</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MEMBER__ORDER_UNAME = eINSTANCE.getMember_OrderUname();

		/**
		 * The meta object literal for the '{@link bpm.united.olap.api.model.impl.MeasureImpl <em>Measure</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.united.olap.api.model.impl.MeasureImpl
		 * @see bpm.united.olap.api.model.impl.ModelPackageImpl#getMeasure()
		 * @generated
		 */
		EClass MEASURE = eINSTANCE.getMeasure();

		/**
		 * The meta object literal for the '<em><b>Item</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MEASURE__ITEM = eINSTANCE.getMeasure_Item();

		/**
		 * The meta object literal for the '<em><b>Calculation Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MEASURE__CALCULATION_TYPE = eINSTANCE.getMeasure_CalculationType();

		/**
		 * The meta object literal for the '<em><b>Parent Schema</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MEASURE__PARENT_SCHEMA = eINSTANCE.getMeasure_ParentSchema();

		/**
		 * The meta object literal for the '<em><b>Calculatd Formula</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MEASURE__CALCULATD_FORMULA = eINSTANCE.getMeasure_CalculatdFormula();

		/**
		 * The meta object literal for the '{@link bpm.united.olap.api.model.impl.ElementDefinitionImpl <em>Element Definition</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.united.olap.api.model.impl.ElementDefinitionImpl
		 * @see bpm.united.olap.api.model.impl.ModelPackageImpl#getElementDefinition()
		 * @generated
		 */
		EClass ELEMENT_DEFINITION = eINSTANCE.getElementDefinition();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELEMENT_DEFINITION__NAME = eINSTANCE.getElementDefinition_Name();

		/**
		 * The meta object literal for the '<em><b>Uname</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELEMENT_DEFINITION__UNAME = eINSTANCE.getElementDefinition_Uname();

		/**
		 * The meta object literal for the '<em><b>Caption</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELEMENT_DEFINITION__CAPTION = eINSTANCE.getElementDefinition_Caption();

		/**
		 * The meta object literal for the '<em><b>Is Visible</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELEMENT_DEFINITION__IS_VISIBLE = eINSTANCE.getElementDefinition_IsVisible();

		/**
		 * The meta object literal for the '{@link bpm.united.olap.api.model.impl.MemberPropertyImpl <em>Member Property</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpm.united.olap.api.model.impl.MemberPropertyImpl
		 * @see bpm.united.olap.api.model.impl.ModelPackageImpl#getMemberProperty()
		 * @generated
		 */
		EClass MEMBER_PROPERTY = eINSTANCE.getMemberProperty();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MEMBER_PROPERTY__NAME = eINSTANCE.getMemberProperty_Name();

		/**
		 * The meta object literal for the '<em><b>Value Item</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MEMBER_PROPERTY__VALUE_ITEM = eINSTANCE.getMemberProperty_ValueItem();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MEMBER_PROPERTY__VALUE = eINSTANCE.getMemberProperty_Value();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MEMBER_PROPERTY__TYPE = eINSTANCE.getMemberProperty_Type();

	}

} //ModelPackage
