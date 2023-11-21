/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.mdm.model.strategies.impl;

import bpm.mdm.model.strategies.*;

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
public class StrategiesFactoryImpl extends EFactoryImpl implements StrategiesFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static StrategiesFactory init() {
		try {
			StrategiesFactory theStrategiesFactory = (StrategiesFactory)EPackage.Registry.INSTANCE.getEFactory("http://strategies/mdm_model/1.0"); 
			if (theStrategiesFactory != null) {
				return theStrategiesFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new StrategiesFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public StrategiesFactoryImpl() {
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
			case StrategiesPackage.DEFAULT_STRATEGY: return createDefaultStrategy();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DefaultStrategy createDefaultStrategy() {
		DefaultStrategyImpl defaultStrategy = new DefaultStrategyImpl();
		return defaultStrategy;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public StrategiesPackage getStrategiesPackage() {
		return (StrategiesPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static StrategiesPackage getPackage() {
		return StrategiesPackage.eINSTANCE;
	}

} //StrategiesFactoryImpl
