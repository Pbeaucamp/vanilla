package bpm.gateway.ui.gef.model.properties;

import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import bpm.gateway.core.DataStream;
import bpm.gateway.core.server.file.AbstractFileServer;
import bpm.gateway.core.server.file.FileXML;
import bpm.gateway.ui.gef.model.Node;

public class FileXMLProperties extends DataStreamProperties {


	public static final String PROPERTY_ROOT_TAG = "rootTag"; //$NON-NLS-1$
	public static final String PROPERTY_ROW_TAG = "rowTag"; //$NON-NLS-1$
	
	
		
	public FileXMLProperties(Node node) {
		super(node);
		PropertiesTable =new Object[][] 
          { { PROPERTY_NAME, new TextPropertyDescriptor(PROPERTY_NAME,"Name")}, //$NON-NLS-1$
			{ PROPERTY_DESCRIPTION, new PropertyDescriptor(PROPERTY_DESCRIPTION,"Description")}, //$NON-NLS-1$
			{ PROPERTY_SQL_DEFINITION, new PropertyDescriptor(PROPERTY_SQL_DEFINITION,"filePath")}, //$NON-NLS-1$
			{ PROPERTY_SERVER, new PropertyDescriptor(PROPERTY_SERVER,"server")}, //$NON-NLS-1$
			{ PROPERTY_ROOT_TAG , new PropertyDescriptor(PROPERTY_ROOT_TAG ,"rootTag")}, //$NON-NLS-1$
			{ PROPERTY_ROW_TAG , new PropertyDescriptor(PROPERTY_ROW_TAG ,"rowTag")} //$NON-NLS-1$
		};
		propertiesNumber = 6;
	}



	
	@Override
	public Object getPropertyValue(Object id) {
		Object o = super.getPropertyValue(id);
		
		if (o != null){
			return o;
		}
		
		if (id.equals(PROPERTY_SERVER)){
			return ((FileXML)node.getGatewayModel()).getServer();
		}
		if (id.equals(PROPERTY_SQL_DEFINITION)){
			return ((FileXML)node.getGatewayModel()).getDefinition();
		}
		
		if (id.equals(PROPERTY_ROOT_TAG)){
			return ((FileXML)node.getGatewayModel()).getRootTag();
		}
		if (id.equals(PROPERTY_ROW_TAG)){
			return ((FileXML)node.getGatewayModel()).getRowTag();
		}
		return null;
	}



	@Override
	public boolean isPropertySet(Object id) {
		
		if (id.equals(PROPERTY_SERVER)){
			return ((DataStream)node.getGatewayModel()).getServer() == null;
		}
		if (id.equals(PROPERTY_ROOT_TAG)){
			return true;
		}
		if (id.equals(PROPERTY_ROW_TAG)){
			return true;
		}
		if (id.equals(PROPERTY_SQL_DEFINITION)){
			return ((FileXML)node.getGatewayModel()).getDefinition() == null;
		}
		
		return super.isPropertySet(id);
	}



	


	@Override
	public void setPropertyValue(Object id, Object value) {
		if (id.equals(PROPERTY_SERVER)){
			((FileXML)node.getGatewayModel()).setServer((AbstractFileServer)value);
		}
		if (id.equals(PROPERTY_ROOT_TAG)){
			((FileXML)node.getGatewayModel()).setRootTag((String)value);
		}
		if (id.equals(PROPERTY_SQL_DEFINITION)){
			((FileXML)node.getGatewayModel()).setDefinition((String)value);
		}
		if (id.equals(PROPERTY_ROW_TAG)){
			((FileXML)node.getGatewayModel()).setRowTag((String)value);
		}
		super.setPropertyValue(id, value);
	}
	
	

}
