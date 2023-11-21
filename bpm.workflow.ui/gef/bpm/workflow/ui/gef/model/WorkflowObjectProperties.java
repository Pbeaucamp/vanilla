package bpm.workflow.ui.gef.model;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import bpm.workflow.ui.Messages;

public class WorkflowObjectProperties implements IPropertySource {

	public static final String PROPERTY_NAME = "name"; //$NON-NLS-1$
	public static final String PROPERTY_DESCRIPTION = "description"; //$NON-NLS-1$

	protected Object PropertiesTable[][];
	protected int propertiesNumber = 2;

	protected Node node;

	public WorkflowObjectProperties(Node node) {
		PropertiesTable = new Object[][] { { PROPERTY_NAME, new TextPropertyDescriptor(PROPERTY_NAME, Messages.WorkflowObjectProperties_2) }, { PROPERTY_DESCRIPTION, new PropertyDescriptor(PROPERTY_DESCRIPTION, Messages.WorkflowObjectProperties_3) },

		};
		this.node = node;
	}

	public Object getEditableValue() {
		return this;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		IPropertyDescriptor[] propertyDescriptors = new IPropertyDescriptor[PropertiesTable.length];

		for(int i = 0; i < propertiesNumber; i++) {
			// Add each property supported.

			PropertyDescriptor descriptor;

			descriptor = (PropertyDescriptor) PropertiesTable[i][1];
			propertyDescriptors[i] = (IPropertyDescriptor) descriptor;
			descriptor.setCategory(Messages.WorkflowObjectProperties_4);
		}

		// Return it.
		return propertyDescriptors;
	}

	public Object getPropertyValue(Object id) {
		if(id.equals(PROPERTY_NAME)) {
			return node.getName();
		}
		if(id.equals(PROPERTY_DESCRIPTION)) {
			return node.getDescription();
		}
		return null;
	}

	public boolean isPropertySet(Object id) {
		if(id.equals(PROPERTY_NAME)) {
			return node.getName() == null;
		}
		if(id.equals(PROPERTY_DESCRIPTION)) {
			return node.getDescription() == null;
		}

		return false;
	}

	public void resetPropertyValue(Object id) {

	}

	public void setPropertyValue(Object id, Object value) {
		if(id.equals(PROPERTY_NAME)) {
			node.setName((String) value);
		}
		if(id.equals(PROPERTY_DESCRIPTION)) {
			node.setDescription((String) value);
		}

	}

}
