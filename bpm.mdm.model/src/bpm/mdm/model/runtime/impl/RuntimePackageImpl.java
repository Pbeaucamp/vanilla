/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.mdm.model.runtime.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;

import bpm.mdm.model.MdmPackage;
import bpm.mdm.model.impl.MdmPackageImpl;
import bpm.mdm.model.rules.RulesPackage;
import bpm.mdm.model.rules.impl.RulesPackageImpl;
import bpm.mdm.model.runtime.DiffResult;
import bpm.mdm.model.runtime.Row;
import bpm.mdm.model.runtime.RowState;
import bpm.mdm.model.runtime.RuntimeFactory;
import bpm.mdm.model.runtime.RuntimePackage;
import bpm.mdm.model.runtime.SynchroPerformer;
import bpm.mdm.model.strategies.StrategiesPackage;
import bpm.mdm.model.strategies.impl.StrategiesPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class RuntimePackageImpl extends EPackageImpl implements RuntimePackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass rowEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass synchroPerformerEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass diffResultEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum rowStateEEnum = null;

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
	 * @see bpm.mdm.model.runtime.RuntimePackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private RuntimePackageImpl() {
		super(eNS_URI, RuntimeFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link RuntimePackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static RuntimePackage init() {
		if (isInited) return (RuntimePackage)EPackage.Registry.INSTANCE.getEPackage(RuntimePackage.eNS_URI);

		// Obtain or create and register package
		RuntimePackageImpl theRuntimePackage = (RuntimePackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof RuntimePackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new RuntimePackageImpl());

		isInited = true;

		// Obtain or create and register interdependencies
		MdmPackageImpl theMdmPackage = (MdmPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(MdmPackage.eNS_URI) instanceof MdmPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(MdmPackage.eNS_URI) : MdmPackage.eINSTANCE);
		RulesPackageImpl theRulesPackage = (RulesPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(RulesPackage.eNS_URI) instanceof RulesPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(RulesPackage.eNS_URI) : RulesPackage.eINSTANCE);
		StrategiesPackageImpl theStrategiesPackage = (StrategiesPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(StrategiesPackage.eNS_URI) instanceof StrategiesPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(StrategiesPackage.eNS_URI) : StrategiesPackage.eINSTANCE);

		// Create package meta-data objects
		theRuntimePackage.createPackageContents();
		theMdmPackage.createPackageContents();
		theRulesPackage.createPackageContents();
		theStrategiesPackage.createPackageContents();

		// Initialize created meta-data
		theRuntimePackage.initializePackageContents();
		theMdmPackage.initializePackageContents();
		theRulesPackage.initializePackageContents();
		theStrategiesPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theRuntimePackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(RuntimePackage.eNS_URI, theRuntimePackage);
		return theRuntimePackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getRow() {
		return rowEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getRowState() {
		return rowStateEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSynchroPerformer() {
		return synchroPerformerEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDiffResult() {
		return diffResultEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RuntimeFactory getRuntimeFactory() {
		return (RuntimeFactory)getEFactoryInstance();
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
		rowEClass = createEClass(ROW);

		synchroPerformerEClass = createEClass(SYNCHRO_PERFORMER);

		diffResultEClass = createEClass(DIFF_RESULT);

		// Create enums
		rowStateEEnum = createEEnum(ROW_STATE);
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

		// Initialize classes and features; add operations and parameters
		initEClass(rowEClass, Row.class, "Row", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		EOperation op = addEOperation(rowEClass, ecorePackage.getEJavaObject(), "getValue", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMdmPackage.getAttribute(), "attribute", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(rowEClass, null, "setValue", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMdmPackage.getAttribute(), "attribute", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEJavaObject(), "value", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(synchroPerformerEClass, SynchroPerformer.class, "SynchroPerformer", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		op = addEOperation(synchroPerformerEClass, this.getDiffResult(), "performDiff", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMdmPackage.getSynchronizer(), "synchronizer", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(diffResultEClass, DiffResult.class, "DiffResult", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		op = addEOperation(diffResultEClass, this.getRowState(), "getState", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getRow(), "row", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(diffResultEClass, null, "addRow", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getRow(), "row", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getRowState(), "state", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMdmPackage.getRule(), "insatisfiedRules", 0, -1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getRow(), "originalRow", 0, 1, IS_UNIQUE, IS_ORDERED);

		addEOperation(diffResultEClass, this.getRow(), "getRows", 0, -1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(diffResultEClass, this.getRow(), "getOriginalRow", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getRow(), "row", 0, 1, IS_UNIQUE, IS_ORDERED);

		addEOperation(diffResultEClass, ecorePackage.getEInt(), "getNewNumber", 0, 1, IS_UNIQUE, IS_ORDERED);

		addEOperation(diffResultEClass, ecorePackage.getEInt(), "getUpdateNumber", 0, 1, IS_UNIQUE, IS_ORDERED);

		addEOperation(diffResultEClass, ecorePackage.getEInt(), "getDiscardNumber", 0, 1, IS_UNIQUE, IS_ORDERED);

		addEOperation(diffResultEClass, ecorePackage.getEInt(), "getReadNumber", 0, 1, IS_UNIQUE, IS_ORDERED);

		addEOperation(diffResultEClass, null, "incrementRead", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(diffResultEClass, theMdmPackage.getRule(), "getUnsatisfiedRules", 0, -1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getRow(), "row", 0, 1, IS_UNIQUE, IS_ORDERED);

		// Initialize enums and add enum literals
		initEEnum(rowStateEEnum, RowState.class, "RowState");
		addEEnumLiteral(rowStateEEnum, RowState.NEW);
		addEEnumLiteral(rowStateEEnum, RowState.DISCARD);
		addEEnumLiteral(rowStateEEnum, RowState.UPDATE);
	}

} //RuntimePackageImpl
