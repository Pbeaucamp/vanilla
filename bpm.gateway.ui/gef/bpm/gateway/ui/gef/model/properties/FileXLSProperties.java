package bpm.gateway.ui.gef.model.properties;

import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import bpm.gateway.core.DataStream;
import bpm.gateway.core.server.file.AbstractFileServer;
import bpm.gateway.core.server.file.FileXLS;
import bpm.gateway.ui.gef.model.Node;

public class FileXLSProperties extends DataStreamProperties {


	public static final String PROPERTY_SHEET_NAME = "sheetName"; //$NON-NLS-1$

	
	
		
	public FileXLSProperties(Node node) {
		super(node);
		PropertiesTable =new Object[][] 
          { { PROPERTY_NAME, new TextPropertyDescriptor(PROPERTY_NAME,"Name")}, //$NON-NLS-1$
			{ PROPERTY_DESCRIPTION, new PropertyDescriptor(PROPERTY_DESCRIPTION,"Description")}, //$NON-NLS-1$
			{ PROPERTY_SQL_DEFINITION, new PropertyDescriptor(PROPERTY_SQL_DEFINITION,"filePath")}, //$NON-NLS-1$
			{ PROPERTY_SERVER, new PropertyDescriptor(PROPERTY_SERVER,"server")}, //$NON-NLS-1$
			{ PROPERTY_SHEET_NAME , new PropertyDescriptor(PROPERTY_SHEET_NAME ,"sheetName")}, //$NON-NLS-1$
		};
		propertiesNumber = 5;
	}



	
	@Override
	public Object getPropertyValue(Object id) {
		Object o = super.getPropertyValue(id);
		
		if (o != null){
			return o;
		}
		
		if (id.equals(PROPERTY_SERVER)){
			return ((FileXLS)node.getGatewayModel()).getServer();
		}
		if (id.equals(PROPERTY_SQL_DEFINITION)){
			return ((FileXLS)node.getGatewayModel()).getDefinition();
		}
		
		if (id.equals(PROPERTY_SHEET_NAME)){
			return ((FileXLS)node.getGatewayModel()).getSheetName();
		}
		
		return null;
	}



	@Override
	public boolean isPropertySet(Object id) {
		
		if (id.equals(PROPERTY_SERVER)){
			return ((DataStream)node.getGatewayModel()).getServer() == null;
		}
		if (id.equals(PROPERTY_SHEET_NAME)){
			return true;
		}
		
		if (id.equals(PROPERTY_SQL_DEFINITION)){
			return ((FileXLS)node.getGatewayModel()).getDefinition() == null;
		}
		
		return super.isPropertySet(id);
	}



	


	@Override
	public void setPropertyValue(Object id, Object value) {
		if (id.equals(PROPERTY_SERVER)){
			((FileXLS)node.getGatewayModel()).setServer((AbstractFileServer)value);
		}
		if (id.equals(PROPERTY_SHEET_NAME)){
			((FileXLS)node.getGatewayModel()).setSheetName((String)value);
		}
		if (id.equals(PROPERTY_SQL_DEFINITION)){
			((FileXLS)node.getGatewayModel()).setDefinition((String)value);
		}
		
		super.setPropertyValue(id, value);
	}
	
	

}
