/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.mdm.model.impl;

import bpm.mdm.model.*;
import java.util.Map;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.datatools.connectivity.oda.design.DataSourceDesign;
import org.eclipse.datatools.connectivity.oda.design.DesignFactory;
import org.eclipse.datatools.connectivity.oda.design.Property;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

import bpm.dataprovider.odainput.OdaInputDigester;
import bpm.mdm.model.Attribute;
import bpm.mdm.model.DataType;
import bpm.mdm.model.Entity;
import bpm.mdm.model.MdmFactory;
import bpm.mdm.model.MdmPackage;
import bpm.mdm.model.Model;
import bpm.mdm.model.Synchronizer;
import bpm.mdm.model.helper.DataSetDesignConverter;
import bpm.vanilla.platform.core.beans.data.OdaInput;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class MdmFactoryImpl extends EFactoryImpl implements MdmFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static MdmFactory init() {
		try {
			MdmFactory theMdmFactory = (MdmFactory)EPackage.Registry.INSTANCE.getEFactory("http://mdm_model/1.0"); 
			if (theMdmFactory != null) {
				return theMdmFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new MdmFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MdmFactoryImpl() {
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
			case MdmPackage.ATTRIBUTE: return createAttribute();
			case MdmPackage.ENTITY: return createEntity();
			case MdmPackage.SYNCHRONIZER: return createSynchronizer();
			case MdmPackage.MODEL: return createModel();
			case MdmPackage.ATTRIBUTE_TO_INTEGER_MAP: return (EObject)createAttributeToIntegerMap();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
			case MdmPackage.DATA_TYPE:
				return createDataTypeFromString(eDataType, initialValue);
			case MdmPackage.DATA_SET:
				return createDataSetFromString(eDataType, initialValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
			case MdmPackage.DATA_TYPE:
				return convertDataTypeToString(eDataType, instanceValue);
			case MdmPackage.DATA_SET:
				return convertDataSetToString(eDataType, instanceValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Attribute createAttribute() {
		AttributeImpl attribute = new AttributeImpl();
		return attribute;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Entity createEntity() {
		EntityImpl entity = new EntityImpl();
		return entity;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Synchronizer createSynchronizer() {
		SynchronizerImpl synchronizer = new SynchronizerImpl();
		return synchronizer;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Model createModel() {
		ModelImpl model = new ModelImpl();
		return model;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<Attribute, Integer> createAttributeToIntegerMap() {
		AttributeToIntegerMapImpl attributeToIntegerMap = new AttributeToIntegerMapImpl();
		return attributeToIntegerMap;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DataType createDataTypeFromString(EDataType eDataType, String initialValue) {
		DataType result = DataType.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertDataTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public DataSetDesign createDataSetFromString(EDataType eDataType, String initialValue) {
//		return (DataSetDesign)super.createFromString(eDataType, initialValue);
//		URI uri = URI.createURI("mdm:mdm-model/dataset");
//		Resource r = new DesignXMLProcessor().createResource(uri);
//		ByteArrayInputStream bis = new ByteArrayInputStream(initialValue.getBytes());
//				
//		HashMap opts = new HashMap();
//		opts.put(XMLResource.OPTION_DECLARE_XML, Boolean.FALSE);
//		
//		try {
//			r.load(bis, opts);
//		} catch (IOException e) {
//			
//			e.printStackTrace();
//		}
//		
//		return (DataSetDesign)r.getContents().get(0);
		
		
		//return (DataSetDesign)super.createFromString(eDataType, initialValue);
		OdaInput oda = null;
		try{
			oda = new OdaInputDigester(initialValue).getOdaInput();
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
		
		
		
		
		
		return DataSetDesignConverter.convert(oda);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public String convertDataSetToString(EDataType eDataType, Object instanceValue) {
		
		try{
//			URI uri = URI.createURI("mdm:mdm-model/dataset");
//			Resource r = new DesignXMLProcessor().createResource(uri);
//			ByteArrayOutputStream bos = new ByteArrayOutputStream();
//			r.getContents().add((EObject)instanceValue);
//			
//			HashMap opts = new HashMap();
//			opts.put(XMLResource.OPTION_DECLARE_XML, Boolean.FALSE);
//			
//			r.save(bos, opts);
//			return bos.toString();
			
			
			DataSetDesign ds = (DataSetDesign)instanceValue;

			OdaInput oda = DataSetDesignConverter.convert(ds);
			return oda.getElementAsXml();
		}catch(Exception ex){
			ex.printStackTrace();
			return super.convertToString(eDataType, instanceValue);
		}
//		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MdmPackage getMdmPackage() {
		return (MdmPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static MdmPackage getPackage() {
		return MdmPackage.eINSTANCE;
	}

	
} //MdmFactoryImpl
