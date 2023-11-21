/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.model.impl;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import bpm.united.olap.api.model.Dimension;
import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.ModelPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Hierarchy</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link bpm.united.olap.api.model.impl.HierarchyImpl#getLevels <em>Levels</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.impl.HierarchyImpl#getParentDimension <em>Parent Dimension</em>}</li>
 *   <li>{@link bpm.united.olap.api.model.impl.HierarchyImpl#isIsClosureHierarchy <em>Is Closure Hierarchy</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class HierarchyImpl extends ElementDefinitionImpl implements Hierarchy {
	/**
	 * The cached value of the '{@link #getLevels() <em>Levels</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLevels()
	 * @generated
	 * @ordered
	 */
	protected EList<Level> levels;

	/**
	 * The default value of the '{@link #isIsClosureHierarchy() <em>Is Closure Hierarchy</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIsClosureHierarchy()
	 * @generated
	 * @ordered
	 */
	protected static final boolean IS_CLOSURE_HIERARCHY_EDEFAULT = false;
	/**
	 * The cached value of the '{@link #isIsClosureHierarchy() <em>Is Closure Hierarchy</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIsClosureHierarchy()
	 * @generated
	 * @ordered
	 */
	protected boolean isClosureHierarchy = IS_CLOSURE_HIERARCHY_EDEFAULT;

	private String allMember;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected HierarchyImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ModelPackage.Literals.HIERARCHY;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<Level> getLevels() {
		if (levels == null) {
			levels = new EObjectContainmentWithInverseEList<Level>(Level.class, this, ModelPackage.HIERARCHY__LEVELS, ModelPackage.LEVEL__PARENT_HIERARCHY);
		}
		return levels;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Dimension getParentDimension() {
		if (eContainerFeatureID() != ModelPackage.HIERARCHY__PARENT_DIMENSION) return null;
		return (Dimension)eContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetParentDimension(Dimension newParentDimension, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject)newParentDimension, ModelPackage.HIERARCHY__PARENT_DIMENSION, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setParentDimension(Dimension newParentDimension) {
		if (newParentDimension != eInternalContainer() || (eContainerFeatureID() != ModelPackage.HIERARCHY__PARENT_DIMENSION && newParentDimension != null)) {
			if (EcoreUtil.isAncestor(this, newParentDimension))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newParentDimension != null)
				msgs = ((InternalEObject)newParentDimension).eInverseAdd(this, ModelPackage.DIMENSION__HIERARCHIES, Dimension.class, msgs);
			msgs = basicSetParentDimension(newParentDimension, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.HIERARCHY__PARENT_DIMENSION, newParentDimension, newParentDimension));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isIsClosureHierarchy() {
		return isClosureHierarchy;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setIsClosureHierarchy(boolean newIsClosureHierarchy) {
		boolean oldIsClosureHierarchy = isClosureHierarchy;
		isClosureHierarchy = newIsClosureHierarchy;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.HIERARCHY__IS_CLOSURE_HIERARCHY, oldIsClosureHierarchy, isClosureHierarchy));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ModelPackage.HIERARCHY__LEVELS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getLevels()).basicAdd(otherEnd, msgs);
			case ModelPackage.HIERARCHY__PARENT_DIMENSION:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetParentDimension((Dimension)otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ModelPackage.HIERARCHY__LEVELS:
				return ((InternalEList<?>)getLevels()).basicRemove(otherEnd, msgs);
			case ModelPackage.HIERARCHY__PARENT_DIMENSION:
				return basicSetParentDimension(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
		switch (eContainerFeatureID()) {
			case ModelPackage.HIERARCHY__PARENT_DIMENSION:
				return eInternalContainer().eInverseRemove(this, ModelPackage.DIMENSION__HIERARCHIES, Dimension.class, msgs);
		}
		return super.eBasicRemoveFromContainerFeature(msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ModelPackage.HIERARCHY__LEVELS:
				return getLevels();
			case ModelPackage.HIERARCHY__PARENT_DIMENSION:
				return getParentDimension();
			case ModelPackage.HIERARCHY__IS_CLOSURE_HIERARCHY:
				return isIsClosureHierarchy();
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
			case ModelPackage.HIERARCHY__LEVELS:
				getLevels().clear();
				getLevels().addAll((Collection<? extends Level>)newValue);
				return;
			case ModelPackage.HIERARCHY__PARENT_DIMENSION:
				setParentDimension((Dimension)newValue);
				return;
			case ModelPackage.HIERARCHY__IS_CLOSURE_HIERARCHY:
				setIsClosureHierarchy((Boolean)newValue);
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
			case ModelPackage.HIERARCHY__LEVELS:
				getLevels().clear();
				return;
			case ModelPackage.HIERARCHY__PARENT_DIMENSION:
				setParentDimension((Dimension)null);
				return;
			case ModelPackage.HIERARCHY__IS_CLOSURE_HIERARCHY:
				setIsClosureHierarchy(IS_CLOSURE_HIERARCHY_EDEFAULT);
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
			case ModelPackage.HIERARCHY__LEVELS:
				return levels != null && !levels.isEmpty();
			case ModelPackage.HIERARCHY__PARENT_DIMENSION:
				return getParentDimension() != null;
			case ModelPackage.HIERARCHY__IS_CLOSURE_HIERARCHY:
				return isClosureHierarchy != IS_CLOSURE_HIERARCHY_EDEFAULT;
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
		result.append(" (isClosureHierarchy: ");
		result.append(isClosureHierarchy);
		result.append(')');
		return result.toString();
	}

	@Override
	public void setAllMember(String allMember) {
		this.allMember = allMember;
	}

	@Override
	public String getAllMember() {
		return allMember;
	}

} //HierarchyImpl
