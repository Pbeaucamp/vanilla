/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.mdm.model.impl;

import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import bpm.mdm.model.Attribute;
import bpm.mdm.model.Mapper;
import bpm.mdm.model.MdmPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Mapper</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class MapperImpl extends EObjectImpl implements Mapper {
	
	private EMap<Attribute, Integer> mapping = new BasicEMap<Attribute, Integer>();
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MapperImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MdmPackage.Literals.MAPPER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Attribute getAttribute(int dataSourceField) {
		for(Attribute k : mapping.keySet() ){
			if (mapping.get(k) != null && mapping.get(k).intValue() == dataSourceField){
				return k;
			}
		}
		return null;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean mapAttributeWithField(Attribute attribute, int dataSourceField) {
		Integer old = mapping.put(attribute, dataSourceField);
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getDataSourceField(Attribute attribute) {
		return mapping.get(attribute);
	}

} //MapperImpl
