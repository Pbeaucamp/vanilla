package bpm.vanilla.platform.core.components.impl.jmx;

import java.util.Iterator;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ReflectionException;

import bpm.vanilla.platform.core.components.IVanillaComponent.Operations;
import bpm.vanilla.platform.core.components.IVanillaComponent.Status;
import bpm.vanilla.platform.core.components.impl.AbstractVanillaComponent;

public class VanillaComponentMBean implements DynamicMBean, IVanillaComponentMBean {

	private static String JMX_VANILLA_COMPONENT_NAME 	= "bpm.vanilla.component.name";
	private static String JMX_VANILLA_COMPONENT_IDENT 	= "bpm.vanilla.component.identifier";
	
	private static String JMX_VANILLA_COMPONENT_STATUS_INT 	= "bpm.vanilla.component.status.int";
	private static String JMX_VANILLA_COMPONENT_STATUS_STR 	= "bpm.vanilla.component.status.string";
	
	private AbstractVanillaComponent component;

	private Properties properties;

	public VanillaComponentMBean(AbstractVanillaComponent component) {
		this.component = component;
		
		properties = createBeanProperties();
	}
	
	@Override
	public String getName() {
		createBeanProperties();
		
		return properties.getProperty(JMX_VANILLA_COMPONENT_NAME);
	}
	
	@Override
	public Status getStatus() {
		return component.getStatus();
	}
	
	/**
	 * Creates bean properties
	 * ere
	 * @return
	 */
	private Properties createBeanProperties() {
		Properties props = new Properties();
		
		
		/**
		 * The component might not have a identifier (at startup and before registering in vnailla)
		 */
		if (component.getIdentifier() != null) {
			props.put(JMX_VANILLA_COMPONENT_IDENT, component.getIdentifier().getComponentId());
			props.put(JMX_VANILLA_COMPONENT_NAME, component.getIdentifier().getComponentNature());
			
		}
		else {
			props.put(JMX_VANILLA_COMPONENT_IDENT, "UNDEFINED");
			props.put(JMX_VANILLA_COMPONENT_NAME, "UNDEFINED");
		}
		
		/**
		 * might not have a status yet either
		 */
		if (component.getStatus() != null) {
			props.put(JMX_VANILLA_COMPONENT_STATUS_INT, 
					"" + component.getStatus().getValue());
			props.put(JMX_VANILLA_COMPONENT_STATUS_STR, 
					"" + component.getStatus().getStatus());
		}
		else {
			
			props.put(JMX_VANILLA_COMPONENT_STATUS_INT, 
					"" + Status.UNDEFINED.getValue());
			props.put(JMX_VANILLA_COMPONENT_STATUS_STR, 
					"" + Status.UNDEFINED.getStatus());
		}
		
		return props;
	}

	@Override
	public Object getAttribute(String attributeName)
			throws AttributeNotFoundException, MBeanException,
			ReflectionException {
		//Refresh
		properties = createBeanProperties();
		
		//try {
			String value = properties.getProperty(attributeName);
//			String strvalue = properties.getProperty(attributeName);
//			Integer value = Integer.parseInt(strvalue);
			
			if (value != null)
				return value;
			else
				throw new AttributeNotFoundException("No such property: "
						+ attributeName);
//		} catch (Exception e) {
//			
//		}
	}

	@Override
	public void setAttribute(Attribute attribute)
			throws AttributeNotFoundException, InvalidAttributeValueException,
			MBeanException, ReflectionException {
		String name = attribute.getName();
		if (properties.getProperty(name) == null)
			throw new AttributeNotFoundException(name);
		Object value = attribute.getValue();
		if (!(value instanceof String)) {
			throw new InvalidAttributeValueException(
					"Attribute value not a string: " + value);
		}
		properties.setProperty(name, (String) value);

	}

	public synchronized AttributeList getAttributes(String[] names) {
		//Refresh
		properties = createBeanProperties();
		
		AttributeList list = new AttributeList();
		for (String name : names) {
			String value = properties.getProperty(name);
			if (value != null)
				list.add(new Attribute(name, value));
		}
		return list;
	}

	public synchronized AttributeList setAttributes(AttributeList list) {
		Attribute[] attrs = (Attribute[]) list.toArray(new Attribute[0]);
		AttributeList retlist = new AttributeList();
		for (Attribute attr : attrs) {
			String name = attr.getName();
			Object value = attr.getValue();
			if (properties.getProperty(name) != null && value instanceof String) {
				properties.setProperty(name, (String) value);
				retlist.add(new Attribute(name, value));
			}
		}

		return retlist;
	}

	@Override
	public synchronized MBeanInfo getMBeanInfo() {
		
		/*
		 * this actually refreshes the all damn thing
		 */
		properties = createBeanProperties();
		
        SortedSet<String> names = new TreeSet<String>();
        
        for (Object name : properties.keySet())
            names.add((String) name);
        
        MBeanAttributeInfo[] attrs = new MBeanAttributeInfo[names.size()];
        Iterator<String> it = names.iterator();
        for (int i = 0; i < attrs.length; i++) {
            String name = it.next();
            attrs[i] = new MBeanAttributeInfo(
                    name,
                    "java.lang.String",
                    "Property " + name,
                    true,   // isReadable
                    true,   // isWritable
                    false); // isIs
        }
        MBeanOperationInfo[] opers = {
            new MBeanOperationInfo(
                    Operations.STOP.getOpName(),
                    Operations.STOP.getOpDesc(),
                    null,   // no parameters
                    "void",
                    MBeanOperationInfo.ACTION),
            new MBeanOperationInfo(
                    Operations.START.getOpName(),
                    Operations.START.getOpDesc(),
                    null,   // no parameters
                    "void",
                    MBeanOperationInfo.ACTION)
        };
        return new MBeanInfo(
                this.getClass().getName(),
                "Vanilla Manager Bean",
                attrs,
                null,  // constructors
                opers, // operations
                null); // notifications
    }

	@Override
	public Object invoke(String actionName, Object[] params, String[] signature)
			throws MBeanException, ReflectionException {
		if (actionName.equals(Operations.STOP.getOpName()) &&
                (params == null || params.length == 0) &&
                (signature == null || signature.length == 0)) {
			try {
				component.stop();
				return null;
			} catch (Exception e) {
				 throw new ReflectionException(e);
			}
		}
		else if (actionName.equals(Operations.START.getOpName()) &&
                (params == null || params.length == 0) &&
                (signature == null || signature.length == 0)) {
			try {
				component.start();
				return null;
			} catch (Exception e) {
				 throw new ReflectionException(e);
			}
		}
        throw new ReflectionException(new NoSuchMethodException(actionName));
	}
}
