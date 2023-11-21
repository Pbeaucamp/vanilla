package bpm.gateway.ui.gef.model.properties;

import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import bpm.gateway.core.DataStream;
import bpm.gateway.core.Server;
import bpm.gateway.core.server.file.FileCSV;
import bpm.gateway.core.server.file.FileVCL;
import bpm.gateway.core.server.file.FileXLS;
import bpm.gateway.core.server.file.FileXML;
import bpm.gateway.ui.gef.model.GatewayObjectProperties;
import bpm.gateway.ui.gef.model.Node;

public class DataStreamProperties extends GatewayObjectProperties {

	public static final String PROPERTY_SERVER = "server"; //$NON-NLS-1$
	public static final String PROPERTY_SQL_DEFINITION = "sqlDefinition"; //$NON-NLS-1$
	public static final String PROPERTY_ENCODING = "encoding"; //$NON-NLS-1$
	
			
	public DataStreamProperties(Node node) {
		super(node);
		PropertiesTable =new Object[][] 
          { { PROPERTY_NAME, new TextPropertyDescriptor(PROPERTY_NAME,"Name")}, //$NON-NLS-1$
			{ PROPERTY_DESCRIPTION, new PropertyDescriptor(PROPERTY_DESCRIPTION,"Description")}, //$NON-NLS-1$
			{ PROPERTY_SERVER, new PropertyDescriptor(PROPERTY_SERVER,"server")}, //$NON-NLS-1$
			{ PROPERTY_SQL_DEFINITION, new PropertyDescriptor(PROPERTY_SQL_DEFINITION,"sqlDefinition")}, //$NON-NLS-1$
			{ PROPERTY_ENCODING, new PropertyDescriptor(PROPERTY_ENCODING,"encoding")} //$NON-NLS-1$
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
			return ((DataStream)node.getGatewayModel()).getServer();
		}
		if (id.equals(PROPERTY_SQL_DEFINITION)){
			return ((DataStream)node.getGatewayModel()).getDefinition();
		}
		if (id.equals(PROPERTY_ENCODING)){
			
			if (node.getGatewayModel() instanceof FileCSV){
				return ((FileCSV)node.getGatewayModel()).getEncoding();
			}
			if (node.getGatewayModel() instanceof FileXML){
				return ((FileXML)node.getGatewayModel()).getEncoding();		
			}
			if (node.getGatewayModel() instanceof FileVCL){
				return ((FileVCL)node.getGatewayModel()).getEncoding();
			}
			if (node.getGatewayModel() instanceof FileXLS){
				return ((FileXLS)node.getGatewayModel()).getEncoding();
			}
		}
		return null;
	}



	@Override
	public boolean isPropertySet(Object id) {
		
		if (id.equals(PROPERTY_SERVER)){
			return ((DataStream)node.getGatewayModel()).getServer() == null;
		}
		if (id.equals(PROPERTY_SQL_DEFINITION)){
			return ((DataStream)node.getGatewayModel()).getDefinition() == null;
		}
		if (id.equals(PROPERTY_ENCODING)){
			return true;
		}
		
		return super.isPropertySet(id);
	}



	


	@Override
	public void setPropertyValue(Object id, Object value) {
		if (id.equals(PROPERTY_SERVER)){
			((DataStream)node.getGatewayModel()).setServer((Server)value);
		}
		if (id.equals(PROPERTY_SQL_DEFINITION)){
			((DataStream)node.getGatewayModel()).setDefinition((String)value);
		}
		if (id.equals(PROPERTY_ENCODING)){
			if (node.getGatewayModel() instanceof FileCSV){
				 ((FileCSV)node.getGatewayModel()).setEncoding((String)value);
			}
			if (node.getGatewayModel() instanceof FileXML){
				 ((FileXML)node.getGatewayModel()).setEncoding((String)value);		
			}
			if (node.getGatewayModel() instanceof FileVCL){
				((FileVCL)node.getGatewayModel()).setEncoding((String)value);
			}
			if (node.getGatewayModel() instanceof FileXLS){
				((FileXLS)node.getGatewayModel()).setEncoding((String)value);
			}
		}
		
		super.setPropertyValue(id, value);
	}
	
	

}
