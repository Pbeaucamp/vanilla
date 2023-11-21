/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.runtime.impl;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import bpm.united.olap.api.model.ElementDefinition;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.runtime.MdxSet;
import bpm.united.olap.api.runtime.RuntimePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Mdx Set</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link bpm.united.olap.api.runtime.impl.MdxSetImpl#getElements <em>Elements</em>}</li>
 *   <li>{@link bpm.united.olap.api.runtime.impl.MdxSetImpl#isIsVisible <em>Is Visible</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MdxSetImpl extends EObjectImpl implements MdxSet {
	/**
	 * The cached value of the '{@link #getElements() <em>Elements</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getElements()
	 * @generated
	 * @ordered
	 */
	protected EList<ElementDefinition> elements;

	/**
	 * The default value of the '{@link #isIsVisible() <em>Is Visible</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIsVisible()
	 * @ordered
	 */
	protected static final boolean IS_VISIBLE_EDEFAULT = true;
	/**
	 * The cached value of the '{@link #isIsVisible() <em>Is Visible</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIsVisible()
	 * @generated
	 * @ordered
	 */
	protected boolean isVisible = IS_VISIBLE_EDEFAULT;
	
	private Measure measure = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MdxSetImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return RuntimePackage.Literals.MDX_SET;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<ElementDefinition> getElements() {
		if (elements == null) {
			elements = new EObjectResolvingEList<ElementDefinition>(ElementDefinition.class, this, RuntimePackage.MDX_SET__ELEMENTS);
		}
		return elements;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isIsVisible() {
		return isVisible;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setIsVisible(boolean newIsVisible) {
		boolean oldIsVisible = isVisible;
		isVisible = newIsVisible;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, RuntimePackage.MDX_SET__IS_VISIBLE, oldIsVisible, isVisible));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case RuntimePackage.MDX_SET__ELEMENTS:
				return getElements();
			case RuntimePackage.MDX_SET__IS_VISIBLE:
				return isIsVisible();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case RuntimePackage.MDX_SET__ELEMENTS:
				getElements().clear();
				getElements().addAll((Collection<? extends ElementDefinition>)newValue);
				return;
			case RuntimePackage.MDX_SET__IS_VISIBLE:
				setIsVisible((Boolean)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case RuntimePackage.MDX_SET__ELEMENTS:
				getElements().clear();
				return;
			case RuntimePackage.MDX_SET__IS_VISIBLE:
				setIsVisible(IS_VISIBLE_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case RuntimePackage.MDX_SET__ELEMENTS:
				return elements != null && !elements.isEmpty();
			case RuntimePackage.MDX_SET__IS_VISIBLE:
				return isVisible != IS_VISIBLE_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (isVisible: ");
		result.append(isVisible);
		result.append(')');
		return result.toString();
	}

	@Override
	public Measure getMeasure() {
		return measure;
	}

	@Override
	public void setMeasure(Measure measure) {
		this.measure = measure;
		
	}

} //MdxSetImpl
