package bpm.gateway.ui.views.property.sections;

import java.util.HashMap;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.DataStream;
import bpm.gateway.core.ISCD;
import bpm.gateway.core.Server;
import bpm.gateway.core.ServerHostable;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.d4c.D4CServer;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.server.database.freemetrics.FreemetricServer;
import bpm.gateway.core.server.database.nosql.cassandra.CassandraServer;
import bpm.gateway.core.server.database.nosql.hbase.HBaseServer;
import bpm.gateway.core.server.database.nosql.mongodb.MongoDbServer;
import bpm.gateway.core.server.file.AbstractFileServer;
import bpm.gateway.core.server.file.FileCSV;
import bpm.gateway.core.server.file.FileVCL;
import bpm.gateway.core.server.file.FileXLS;
import bpm.gateway.core.server.file.FileXML;
import bpm.gateway.core.server.ldap.LdapServer;
import bpm.gateway.core.transformations.SqlLookup;
import bpm.gateway.core.transformations.SqlScript;
import bpm.gateway.core.transformations.inputs.CassandraInputStream;
import bpm.gateway.core.transformations.inputs.D4CInput;
import bpm.gateway.core.transformations.inputs.DataBaseInputStream;
import bpm.gateway.core.transformations.inputs.DbAccessInput;
import bpm.gateway.core.transformations.inputs.HbaseInputStream;
import bpm.gateway.core.transformations.inputs.LdapInput;
import bpm.gateway.core.transformations.inputs.LdapMultipleMembers;
import bpm.gateway.core.transformations.inputs.MongoDbInputStream;
import bpm.gateway.core.transformations.inputs.NagiosDb;
import bpm.gateway.core.transformations.inputs.OracleXmlView;
import bpm.gateway.core.transformations.nosql.SqoopTransformation;
import bpm.gateway.core.transformations.outputs.CassandraOutputStream;
import bpm.gateway.core.transformations.outputs.CielComptaOutput;
import bpm.gateway.core.transformations.outputs.DataBaseOutputStream;
import bpm.gateway.core.transformations.outputs.FreemetricsKPI;
import bpm.gateway.core.transformations.outputs.HBaseOutputStream;
import bpm.gateway.core.transformations.outputs.MongoDbOutputStream;
import bpm.gateway.core.tsbn.ConnectorAffaireXML;
import bpm.gateway.core.tsbn.ConnectorAppelXML;
import bpm.gateway.core.tsbn.ConnectorReferentielXML;
import bpm.gateway.core.veolia.ConnectorAbonneXML;
import bpm.gateway.core.veolia.ConnectorPatrimoineXML;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.model.properties.DataStreamProperties;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.studio.jdbc.management.model.ListDriver;

public class ServerSection extends AbstractPropertySection {

	private CCombo serverCbo;
	private Node node;
	
	
	private HashMap<String, Server> map = new HashMap<String, Server>();
	
	public ServerSection() {
		
		
		
	}

	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		serverCbo = getWidgetFactory().createCCombo(composite, SWT.READ_ONLY);
		
		FormData data = new FormData();
        data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
        data.right = new FormAttachment(100, 0);
        data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
        serverCbo.setLayoutData(data);
        serverCbo.addSelectionListener(this.listener);
        
      
        
        CLabel labelLabel = getWidgetFactory().createCLabel(composite, "Server"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(serverCbo,ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(serverCbo, 0, SWT.CENTER);
		labelLabel.setLayoutData(data);

	}
	
	
	private SelectionListener listener = new SelectionAdapter() {

        @Override
		public void widgetSelected(SelectionEvent e) {
        	
        	if( node.getGatewayModel() instanceof FreemetricsKPI){
        		for(String key : map.keySet()){
            		if (key.equals(serverCbo.getText())){
            			((FreemetricsKPI)node.getGatewayModel()).setServer((FreemetricServer)map.get(key));
            			return;
            		}
            	}
        		
        	}
        	else if (node.getGatewayModel() instanceof ServerHostable){
        		for(String key : map.keySet()){
            		if (key.equals(serverCbo.getText())){
            			((ServerHostable)node.getGatewayModel()).setServer(map.get(key));
            			return;
            		}
            	}
        	}
        	else{
        		DataStreamProperties properties = (DataStreamProperties) node.getAdapter(IPropertySource.class);
            	
            	for(String key : map.keySet()){
            		if (key.equals(serverCbo.getText())){
            			properties.setPropertyValue(DataStreamProperties.PROPERTY_SERVER, map.get(key));
            			return;
            		}
            	}
        	}
        		
        	
        	
        	
			
		}

    };
    
    @Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.node = (Node)((NodePart) input).getModel();
        initMap();
	}

    
	@Override
	public void refresh() {
		serverCbo.removeSelectionListener(this.listener);
		if (node.getGatewayModel() instanceof FreemetricsKPI){
			for(int i = 0; i < serverCbo.getItemCount(); i++){
				Server server = ((FreemetricsKPI)node.getGatewayModel()).getServer();
				
				if (server != null && server.getName().equals(serverCbo.getItem(i))){
					serverCbo.select(i);
					break;
				}
			}
			
		}
		
		else if (node.getGatewayModel() instanceof ServerHostable){
			for(int i = 0; i < serverCbo.getItemCount(); i++){
				Server server = ((ServerHostable)node.getGatewayModel()).getServer();
				
				if (server != null && server.getName().equals(serverCbo.getItem(i))){
					serverCbo.select(i);
					break;
				}
			}
			
		}
		else{
			DataStreamProperties properties = (DataStreamProperties) node.getAdapter(IPropertySource.class);
			
			
			
			
			for(int i = 0; i < serverCbo.getItemCount(); i++){
				Server server = (Server) properties.getPropertyValue(DataStreamProperties.PROPERTY_SERVER);
				
				if (server != null && server.getName().equals(serverCbo.getItem(i))){
					serverCbo.select(i);
					break;
				}
			}
			Server server = (Server)properties.getPropertyValue(DataStreamProperties.PROPERTY_SERVER);
			for(String key : map.keySet()){
				if (server == map.get(key)){
					serverCbo.setText(key);
				}
			}
		}
		
		
		
		
		
		serverCbo.addSelectionListener(this.listener);
		

	}
    
    
    private void initMap(){
    	Server server = null;
    	map.clear();
    	if (node.getGatewayModel() instanceof FreemetricsKPI){
    		server = ((FreemetricsKPI)node.getGatewayModel()).getServer();
    	}else if (node.getGatewayModel() instanceof DataStream){
    		server = ((DataStream)node.getGatewayModel()).getServer();
    	}
    	else if (node.getGatewayModel() instanceof ServerHostable){
    		server = ((ServerHostable)node.getGatewayModel()).getServer();
    	}
    	
		
		Class<?> serverClass = null;
		
		if (server == null){
			if (node.getGatewayModel() instanceof FreemetricsKPI){
				serverClass = FreemetricServer.class;
			}
			
			else if (node.getGatewayModel() instanceof ServerHostable){
				serverClass = ((ServerHostable)node.getGatewayModel()).getServerClass();
			}
			else{
				DataStream ds = (DataStream)node.getGatewayModel();
	
				
				if (ds instanceof DataBaseInputStream  || ds instanceof DataBaseOutputStream 
						|| ds instanceof ISCD || ds instanceof SqlScript || ds instanceof SqlLookup
						|| ds instanceof NagiosDb || ds instanceof SqoopTransformation || ds instanceof OracleXmlView
						|| ds instanceof ConnectorAbonneXML || ds instanceof ConnectorPatrimoineXML
						|| ds instanceof ConnectorAffaireXML || ds instanceof ConnectorAppelXML || ds instanceof ConnectorReferentielXML) {

					serverClass = DataBaseServer.class;
				}
				else if (ds instanceof FileXML || ds instanceof FileCSV || ds instanceof FileXLS || ds instanceof FileVCL || ds instanceof CielComptaOutput){
					serverClass = AbstractFileServer.class;
				}
				else if (ds instanceof LdapInput || ds instanceof LdapMultipleMembers){
					serverClass = LdapServer.class;
				}
				else if (ds instanceof CassandraInputStream || ds instanceof CassandraOutputStream){
					serverClass = CassandraServer.class;
				}
				else if (ds instanceof HbaseInputStream || ds instanceof HBaseOutputStream){
					serverClass = HBaseServer.class;
				}
				else if (ds instanceof MongoDbInputStream || ds instanceof MongoDbOutputStream){
					serverClass = MongoDbServer.class;
				}
				else if (ds instanceof D4CInput){
					serverClass = D4CServer.class;
				}
			}
			
		}
		else{
			serverClass = server.getClass();
			
		}

		
		if (serverClass != null){
			for(Server s : ResourceManager.getInstance().getServers(serverClass)){
				if (node.getGatewayModel() instanceof DbAccessInput){
					if (s.getCurrentConnection(null) instanceof DataBaseConnection){
						if (((DataBaseConnection)s.getCurrentConnection(null)).getDriverName().equals(ListDriver.MS_ACCESS)){
							map.put(s.getName(), s);
						}
					}
				}
				else{
					map.put(s.getName(), s);
				}
				
			}
		}
		
		
		serverCbo.setItems(map.keySet().toArray(new String[map.size()]));
		  
		  
    }
}
