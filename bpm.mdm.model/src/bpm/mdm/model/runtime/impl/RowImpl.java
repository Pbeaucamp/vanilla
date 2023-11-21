/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.mdm.model.runtime.impl;

import bpm.mdm.model.Attribute;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;

import bpm.mdm.model.runtime.Row;
import bpm.mdm.model.runtime.RuntimePackage;
import bpm.mdm.model.runtime.exception.AttributeTypeException;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Row</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class RowImpl extends EObjectImpl implements Row {
	
	
	private HashMap<String, Object> values = new HashMap<String, Object>();
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected RowImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return RuntimePackage.Literals.ROW;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object getValue(Attribute attribute) {
		return values.get(attribute.getUuid());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public void setValue(Attribute attribute, Object value) {
		if (value != null){
			try{
				Class<?> cl = attribute.getDataType().getJavaClass();
				try{
					values.put(attribute.getUuid(),cl.cast(value));
				}catch(ClassCastException ex){
					Constructor<?> constructor = cl.getConstructor(String.class);
					if (constructor != null){
						values.put(attribute.getUuid(),constructor.newInstance(value.toString()));
						
					}
				}
			}catch(Exception ex){
				values.put(attribute.getUuid(), value);
			}
			
			
		}
		
	}

	@Override
	public boolean match(List<Attribute> pkAttribute, Row row) {
		if (pkAttribute == null || pkAttribute.isEmpty() || row == null){
			return false;
		}
		
		for(Attribute a : pkAttribute){
			Object v = getValue(a) ;
			Object o = row.getValue(a);
			
			if ((v == null && o != null) || 
				 (v != null && o == null)){
				return false;
			}
			if (!v.equals(o)){
				return false;
			}
		}
		return true;
	}

//	/**
//	 * <!-- begin-user-doc -->
//	 * <!-- end-user-doc -->
//	 */
//	public Object getValue(String attributeName) {
//		return values.get(attributeName);
//	}

//	/**
//	 * <!-- begin-user-doc -->
//	 * <!-- end-user-doc -->
//	 */
//	public void setValue(String attributeName, Object value) {
//		values.put(attributeName, value);
//	}

} //RowImpl
