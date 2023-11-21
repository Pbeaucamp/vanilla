package bpm.gateway.ui.gef.model.properties;

import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import bpm.gateway.core.DataStream;
import bpm.gateway.core.server.file.AbstractFileServer;
import bpm.gateway.core.server.file.FileCSV;
import bpm.gateway.core.transformations.outputs.FileOutputCSV;
import bpm.gateway.ui.gef.model.Node;

public class FileCSVProperties extends DataStreamProperties {


	public static final String PROPERTY_SEPARATOR = "separator"; //$NON-NLS-1$
	public static final String PROPERTY_APPEND = "append"; //$NON-NLS-1$
	
	
		
	public FileCSVProperties(Node node) {
		super(node);
		PropertiesTable =new Object[][] 
          { { PROPERTY_NAME, new TextPropertyDescriptor(PROPERTY_NAME,"Name")}, //$NON-NLS-1$
			{ PROPERTY_DESCRIPTION, new PropertyDescriptor(PROPERTY_DESCRIPTION,"Description")}, //$NON-NLS-1$
			{ PROPERTY_SQL_DEFINITION, new PropertyDescriptor(PROPERTY_SQL_DEFINITION,"filePath")}, //$NON-NLS-1$
			{ PROPERTY_SERVER, new PropertyDescriptor(PROPERTY_SERVER,"server")}, //$NON-NLS-1$
			{ PROPERTY_SEPARATOR , new PropertyDescriptor(PROPERTY_SEPARATOR ,"separator")}, //$NON-NLS-1$
			{ PROPERTY_APPEND , new PropertyDescriptor(PROPERTY_APPEND ,"append")},			 //$NON-NLS-1$
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
			return ((FileCSV)node.getGatewayModel()).getServer();
		}
		if (id.equals(PROPERTY_SQL_DEFINITION)){
			return ((FileCSV)node.getGatewayModel()).getDefinition();
		}
		
		if (id.equals(PROPERTY_SEPARATOR)){
			return ((FileCSV)node.getGatewayModel()).getSeparator();
		}
		if (id.equals(PROPERTY_APPEND)){
			return ((FileOutputCSV)node.getGatewayModel()).isAppend();
		}
		return null;
	}



	@Override
	public boolean isPropertySet(Object id) {
		
		if (id.equals(PROPERTY_SERVER)){
			return ((DataStream)node.getGatewayModel()).getServer() == null;
		}
		if (id.equals(PROPERTY_SEPARATOR)){
			return true;
		}
		if (id.equals(PROPERTY_APPEND)){
			return true;
		}
		if (id.equals(PROPERTY_SQL_DEFINITION)){
			return ((FileOutputCSV)node.getGatewayModel()).getDefinition() == null;
		}
		
		return super.isPropertySet(id);
	}



	


	@Override
	public void setPropertyValue(Object id, Object value) {
		if (id.equals(PROPERTY_SERVER)){
			((FileCSV)node.getGatewayModel()).setServer((AbstractFileServer)value);
		}
		if (id.equals(PROPERTY_APPEND)){
			((FileOutputCSV)node.getGatewayModel()).setAppend((Boolean)value);
		}
		if (id.equals(PROPERTY_SQL_DEFINITION)){
			((FileCSV)node.getGatewayModel()).setDefinition((String)value);
		}
		if (id.equals(PROPERTY_SEPARATOR)){
			((FileCSV)node.getGatewayModel()).setSeparator((String)value);
		}
		super.setPropertyValue(id, value);
	}
	
	

}
