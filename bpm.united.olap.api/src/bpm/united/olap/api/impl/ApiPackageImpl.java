/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.impl;

import bpm.united.olap.api.ApiFactory;
import bpm.united.olap.api.ApiPackage;

import bpm.united.olap.api.datasource.DatasourcePackage;

import bpm.united.olap.api.datasource.impl.DatasourcePackageImpl;

import bpm.united.olap.api.model.ModelPackage;

import bpm.united.olap.api.model.impl.ModelPackageImpl;

import bpm.united.olap.api.result.ResultPackage;

import bpm.united.olap.api.result.impl.ResultPackageImpl;

import bpm.united.olap.api.runtime.RuntimePackage;

import bpm.united.olap.api.runtime.impl.RuntimePackageImpl;

import java.util.Properties;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ApiPackageImpl extends EPackageImpl implements ApiPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType propertiesEDataType = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see bpm.united.olap.api.ApiPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private ApiPackageImpl() {
		super(eNS_URI, ApiFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 * 
	 * <p>This method is used to initialize {@link ApiPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static ApiPackage init() {
		if (isInited) return (ApiPackage)EPackage.Registry.INSTANCE.getEPackage(ApiPackage.eNS_URI);

		// Obtain or create and register package
		ApiPackageImpl theApiPackage = (ApiPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof ApiPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new ApiPackageImpl());

		isInited = true;

		// Obtain or create and register interdependencies
		DatasourcePackageImpl theDatasourcePackage = (DatasourcePackageImpl)(EPackage.Registry.INSTANCE.getEPackage(DatasourcePackage.eNS_URI) instanceof DatasourcePackageImpl ? EPackage.Registry.INSTANCE.getEPackage(DatasourcePackage.eNS_URI) : DatasourcePackage.eINSTANCE);
		ModelPackageImpl theModelPackage = (ModelPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(ModelPackage.eNS_URI) instanceof ModelPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(ModelPackage.eNS_URI) : ModelPackage.eINSTANCE);
		RuntimePackageImpl theRuntimePackage = (RuntimePackageImpl)(EPackage.Registry.INSTANCE.getEPackage(RuntimePackage.eNS_URI) instanceof RuntimePackageImpl ? EPackage.Registry.INSTANCE.getEPackage(RuntimePackage.eNS_URI) : RuntimePackage.eINSTANCE);
		ResultPackageImpl theResultPackage = (ResultPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(ResultPackage.eNS_URI) instanceof ResultPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(ResultPackage.eNS_URI) : ResultPackage.eINSTANCE);

		// Create package meta-data objects
		theApiPackage.createPackageContents();
		theDatasourcePackage.createPackageContents();
		theModelPackage.createPackageContents();
		theRuntimePackage.createPackageContents();
		theResultPackage.createPackageContents();

		// Initialize created meta-data
		theApiPackage.initializePackageContents();
		theDatasourcePackage.initializePackageContents();
		theModelPackage.initializePackageContents();
		theRuntimePackage.initializePackageContents();
		theResultPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theApiPackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(ApiPackage.eNS_URI, theApiPackage);
		return theApiPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getProperties() {
		return propertiesEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ApiFactory getApiFactory() {
		return (ApiFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create data types
		propertiesEDataType = createEDataType(PROPERTIES);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		DatasourcePackage theDatasourcePackage = (DatasourcePackage)EPackage.Registry.INSTANCE.getEPackage(DatasourcePackage.eNS_URI);
		ModelPackage theModelPackage = (ModelPackage)EPackage.Registry.INSTANCE.getEPackage(ModelPackage.eNS_URI);
		RuntimePackage theRuntimePackage = (RuntimePackage)EPackage.Registry.INSTANCE.getEPackage(RuntimePackage.eNS_URI);
		ResultPackage theResultPackage = (ResultPackage)EPackage.Registry.INSTANCE.getEPackage(ResultPackage.eNS_URI);

		// Add subpackages
		getESubpackages().add(theDatasourcePackage);
		getESubpackages().add(theModelPackage);
		getESubpackages().add(theRuntimePackage);
		getESubpackages().add(theResultPackage);

		// Initialize data types
		initEDataType(propertiesEDataType, Properties.class, "Properties", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

		// Create resource
		createResource(eNS_URI);
	}

} //ApiPackageImpl
