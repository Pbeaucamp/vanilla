package bpm.gateway.ui.gef.model;

import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import bpm.gateway.core.transformations.SimpleMappingTransformation;

public class MapperTransformationProperties extends GatewayObjectProperties {

	public static final String PROPERTY_INPUT_MAP = "inputMap"; //$NON-NLS-1$
	public static final String PROPERTY_OUTPUT_MAP = "outputMap"; //$NON-NLS-1$
	public static final String PROPERTY_OUTPUT = "output"; //$NON-NLS-1$
	public static final String PROPERTY_INPUT = "input"; //$NON-NLS-1$
		
	public MapperTransformationProperties(Node node) {
		super(node);
		PropertiesTable =new Object[][] 
          { { PROPERTY_NAME, new TextPropertyDescriptor(PROPERTY_NAME,"Name")}, //$NON-NLS-1$
			{ PROPERTY_DESCRIPTION, new PropertyDescriptor(PROPERTY_DESCRIPTION,"Description")}, //$NON-NLS-1$
			{ PROPERTY_INPUT_MAP, new PropertyDescriptor(PROPERTY_INPUT_MAP,"inputMap")}, //$NON-NLS-1$
			{ PROPERTY_OUTPUT_MAP, new PropertyDescriptor(PROPERTY_OUTPUT_MAP,"outputMap")}, //$NON-NLS-1$
			{ PROPERTY_OUTPUT_MAP, new PropertyDescriptor(PROPERTY_OUTPUT,"output")}, //$NON-NLS-1$
			{ PROPERTY_OUTPUT_MAP, new PropertyDescriptor(PROPERTY_INPUT,"input")} //$NON-NLS-1$
		};
		propertiesNumber = 6;
	}
	
	@Override
	public Object getPropertyValue(Object id) {
		Object o = super.getPropertyValue(id);
		
		if (o != null){
			return o;
		}
		
		if (id.equals(PROPERTY_INPUT)){
			return ((SimpleMappingTransformation)node.getGatewayModel()).getInputs();
		}
		if (id.equals(PROPERTY_OUTPUT)){
			return ((SimpleMappingTransformation)node.getGatewayModel()).getOutputs();
		}
		if (id.equals(PROPERTY_OUTPUT_MAP)){
			return ((SimpleMappingTransformation)node.getGatewayModel()).getOutputMap();
		}
		if (id.equals(PROPERTY_INPUT_MAP)){
			return ((SimpleMappingTransformation)node.getGatewayModel()).getInputMap();
		}
		return null;
	}
	
	@Override
	public boolean isPropertySet(Object id) {
		
		if (id.equals(PROPERTY_INPUT)){
			return ((SimpleMappingTransformation)node.getGatewayModel()).getInputs() == null;
		}
		if (id.equals(PROPERTY_OUTPUT)){
			return ((SimpleMappingTransformation)node.getGatewayModel()).getOutputs() == null;
		}
		if (id.equals(PROPERTY_OUTPUT_MAP)){
			return ((SimpleMappingTransformation)node.getGatewayModel()).getOutputMap() == null;
		}
		if (id.equals(PROPERTY_INPUT_MAP)){
			return ((SimpleMappingTransformation)node.getGatewayModel()).getInputMap() == null;
		}
		
		return super.isPropertySet(id);
	}

}
