package bpm.vanilla.platform.core.components.impl.jmx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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

import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;

public class VanillaCoreMBean implements DynamicMBean, IVanillaCoreMBean {

	/** 
	 * exposed props
	 */
	private static String JMX_VANILLA_CORE_STATUS = "vanillaStatus";
	
	
	private IVanillaComponentProvider vanillaCore;
	
	private HashMap<String, IVanillaComponentMBean> registeredComponentMBean;

	private Properties properties;

	public VanillaCoreMBean(IVanillaComponentProvider vanillaCore) {
		this.vanillaCore = vanillaCore;

		registeredComponentMBean = new HashMap<String, IVanillaComponentMBean>();
		
		loadProperties();
	}
	
	@Override
	public void registerBean(IVanillaComponentMBean componentBean) {
		registeredComponentMBean.put(componentBean.getName(), componentBean);
	}
	
	@Override
	public List<IVanillaComponentMBean> listRegisteredBeans() {
		return new ArrayList<IVanillaComponentMBean>(registeredComponentMBean.values());
	}
	
	private void loadProperties() {
		properties = new Properties();
		properties.put(JMX_VANILLA_CORE_STATUS, "" + vanillaCore.getStatus());
	}

	@Override
	public Object getAttribute(String attributeName)
			throws AttributeNotFoundException, MBeanException,
			ReflectionException {
		loadProperties();
		
		if (registeredComponentMBean.containsKey(attributeName)) {
			return registeredComponentMBean.get(attributeName).getStatus().getStatus();
			//return registeredComponentMBean.get(attributeName);
		}
		
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
		loadProperties();
		AttributeList list = new AttributeList();
		for (String name : names) {
			if (registeredComponentMBean.containsKey(name)) {
				//list.add(new Attribute(name, name));
				list.add(new Attribute(name, registeredComponentMBean.get(name).getStatus().getStatus()));
			}
			else {
				String value = properties.getProperty(name);
				if (value != null)
					list.add(new Attribute(name, value));
			}
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
		
		loadProperties();
		
        SortedSet<String> names = new TreeSet<String>();
        
        for (Object name : properties.keySet())
            names.add((String) name);
        
        //MBeanAttributeInfo[] attrs = new MBeanAttributeInfo[names.size() + registeredComponentMBean.size()];
//        Iterator<String> it = names.iterator();
//        for (int i = 0; i < attrs.length; i++) {
//            String name = it.next();
//            attrs[i] = new MBeanAttributeInfo(
//                    name,
//                    "java.lang.String",
//                    "Property " + name,
//                    true,   // isReadable
//                    true,   // isWritable
//                    false); // isIs
//        }
        List<MBeanAttributeInfo> attrs = new ArrayList<MBeanAttributeInfo>();
        for (String name : names) {
          attrs.add(new MBeanAttributeInfo(
	          name,
	          "java.lang.String",
	          "Property " + name,
	          true,   // isReadable
	          false,   // isWritable
	          false)); // isIs
        }

        for (String key : registeredComponentMBean.keySet()) {
            attrs.add(new MBeanAttributeInfo(
      	          key,
      	          "java.lang.String",
      	          "Component with key" + key,
      	          true,   // isReadable
      	          false,   // isWritable
      	          false)); // isIs
        }
        MBeanOperationInfo[] opers = {
//            new MBeanOperationInfo(
//                    "operation_1",
//                    "operation_1",
//                    null,   // no parameters
//                    "void",
//                    MBeanOperationInfo.ACTION)
        };
        return new MBeanInfo(
                this.getClass().getName(),
                "Vanilla Manager Bean",
                attrs.toArray(new MBeanAttributeInfo[0]),
                null,  // constructors
                opers, // operations
                null); // notifications
    }

	@Override
	public Object invoke(String actionName, Object[] params, String[] signature)
			throws MBeanException, ReflectionException {
//		if (actionName.equals("reload") &&
//                (params == null || params.length == 0) &&
//                (signature == null || signature.length == 0)) {
//      }
        throw new ReflectionException(new NoSuchMethodException(actionName));
	}
}
