/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.model.impl;

import bpm.united.olap.api.model.ElementDefinition;
import bpm.united.olap.api.model.ModelPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Element Definition</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link bpm.united.olap.api.model.impl.ElementDefinitionImpl#getName <em>Name</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.impl.ElementDefinitionImpl#getUname <em>Uname</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.impl.ElementDefinitionImpl#getCaption <em>Caption</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.impl.ElementDefinitionImpl#isIsVisible <em>Is Visible</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ElementDefinitionImpl extends EObjectImpl implements ElementDefinition {
	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getUname() <em>Uname</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUname()
	 * @generated
	 * @ordered
	 */
	protected static final String UNAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getUname() <em>Uname</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUname()
	 * @generated
	 * @ordered
	 */
	protected String uname = UNAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getCaption() <em>Caption</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCaption()
	 * @generated
	 * @ordered
	 */
	protected static final String CAPTION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getCaption() <em>Caption</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCaption()
	 * @generated
	 * @ordered
	 */
	protected String caption = CAPTION_EDEFAULT;

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

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ElementDefinitionImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ModelPackage.Literals.ELEMENT_DEFINITION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public void setName(String newName) {
		String oldName = name;
//		newName = newName;//.replace(".", "_");
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.ELEMENT_DEFINITION__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getUname() {
		return uname;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setUname(String newUname) {
		String oldUname = uname;
		uname = newUname;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.ELEMENT_DEFINITION__UNAME, oldUname, uname));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCaption(String newCaption) {
		String oldCaption = caption;
		caption = newCaption;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.ELEMENT_DEFINITION__CAPTION, oldCaption, caption));
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
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.ELEMENT_DEFINITION__IS_VISIBLE, oldIsVisible, isVisible));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ModelPackage.ELEMENT_DEFINITION__NAME:
				return getName();
			case ModelPackage.ELEMENT_DEFINITION__UNAME:
				return getUname();
			case ModelPackage.ELEMENT_DEFINITION__CAPTION:
				return getCaption();
			case ModelPackage.ELEMENT_DEFINITION__IS_VISIBLE:
				return isIsVisible();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case ModelPackage.ELEMENT_DEFINITION__NAME:
				setName((String)newValue);
				return;
			case ModelPackage.ELEMENT_DEFINITION__UNAME:
				setUname((String)newValue);
				return;
			case ModelPackage.ELEMENT_DEFINITION__CAPTION:
				setCaption((String)newValue);
				return;
			case ModelPackage.ELEMENT_DEFINITION__IS_VISIBLE:
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
			case ModelPackage.ELEMENT_DEFINITION__NAME:
				setName(NAME_EDEFAULT);
				return;
			case ModelPackage.ELEMENT_DEFINITION__UNAME:
				setUname(UNAME_EDEFAULT);
				return;
			case ModelPackage.ELEMENT_DEFINITION__CAPTION:
				setCaption(CAPTION_EDEFAULT);
				return;
			case ModelPackage.ELEMENT_DEFINITION__IS_VISIBLE:
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
			case ModelPackage.ELEMENT_DEFINITION__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case ModelPackage.ELEMENT_DEFINITION__UNAME:
				return UNAME_EDEFAULT == null ? uname != null : !UNAME_EDEFAULT.equals(uname);
			case ModelPackage.ELEMENT_DEFINITION__CAPTION:
				return CAPTION_EDEFAULT == null ? caption != null : !CAPTION_EDEFAULT.equals(caption);
			case ModelPackage.ELEMENT_DEFINITION__IS_VISIBLE:
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
		result.append(" (name: ");
		result.append(name);
		result.append(", uname: ");
		result.append(uname);
		result.append(", caption: ");
		result.append(caption);
		result.append(", isVisible: ");
		result.append(isVisible);
		result.append(')');
		return result.toString();
	}

	@Override
	public int compareTo(ElementDefinition arg0) {
		return getUname().compareTo(arg0.getUname());
	}

} //ElementDefinitionImpl
