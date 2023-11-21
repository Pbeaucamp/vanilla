/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.mdm.model.strategies.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;

import bpm.mdm.model.MdmPackage;
import bpm.mdm.model.impl.MdmPackageImpl;
import bpm.mdm.model.rules.RulesPackage;
import bpm.mdm.model.rules.impl.RulesPackageImpl;
import bpm.mdm.model.runtime.RuntimePackage;
import bpm.mdm.model.runtime.impl.RuntimePackageImpl;
import bpm.mdm.model.strategies.DefaultStrategy;
import bpm.mdm.model.strategies.StrategiesFactory;
import bpm.mdm.model.strategies.StrategiesPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class StrategiesPackageImpl extends EPackageImpl implements StrategiesPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass defaultStrategyEClass = null;

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
	 * @see bpm.mdm.model.strategies.StrategiesPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private StrategiesPackageImpl() {
		super(eNS_URI, StrategiesFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link StrategiesPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static StrategiesPackage init() {
		if (isInited) return (StrategiesPackage)EPackage.Registry.INSTANCE.getEPackage(StrategiesPackage.eNS_URI);

		// Obtain or create and register package
		StrategiesPackageImpl theStrategiesPackage = (StrategiesPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof StrategiesPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new StrategiesPackageImpl());

		isInited = true;

		// Obtain or create and register interdependencies
		MdmPackageImpl theMdmPackage = (MdmPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(MdmPackage.eNS_URI) instanceof MdmPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(MdmPackage.eNS_URI) : MdmPackage.eINSTANCE);
		RulesPackageImpl theRulesPackage = (RulesPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(RulesPackage.eNS_URI) instanceof RulesPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(RulesPackage.eNS_URI) : RulesPackage.eINSTANCE);
		RuntimePackageImpl theRuntimePackage = (RuntimePackageImpl)(EPackage.Registry.INSTANCE.getEPackage(RuntimePackage.eNS_URI) instanceof RuntimePackageImpl ? EPackage.Registry.INSTANCE.getEPackage(RuntimePackage.eNS_URI) : RuntimePackage.eINSTANCE);

		// Create package meta-data objects
		theStrategiesPackage.createPackageContents();
		theMdmPackage.createPackageContents();
		theRulesPackage.createPackageContents();
		theRuntimePackage.createPackageContents();

		// Initialize created meta-data
		theStrategiesPackage.initializePackageContents();
		theMdmPackage.initializePackageContents();
		theRulesPackage.initializePackageContents();
		theRuntimePackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theStrategiesPackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(StrategiesPackage.eNS_URI, theStrategiesPackage);
		return theStrategiesPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDefaultStrategy() {
		return defaultStrategyEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public StrategiesFactory getStrategiesFactory() {
		return (StrategiesFactory)getEFactoryInstance();
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

		// Create classes and their features
		defaultStrategyEClass = createEClass(DEFAULT_STRATEGY);
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
		MdmPackage theMdmPackage = (MdmPackage)EPackage.Registry.INSTANCE.getEPackage(MdmPackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		defaultStrategyEClass.getESuperTypes().add(theMdmPackage.getStrategy());

		// Initialize classes and features; add operations and parameters
		initEClass(defaultStrategyEClass, DefaultStrategy.class, "DefaultStrategy", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
	}

} //StrategiesPackageImpl
