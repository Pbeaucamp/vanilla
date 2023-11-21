package bpm.profiling.ui;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import bpm.metadata.MetaData;
import bpm.metadata.MetaDataBuilder;
import bpm.metadata.digester.MetaDataDigester;
import bpm.metadata.layer.logical.IDataSource;
import bpm.metadata.layer.logical.sql.SQLDataSource;
import bpm.metadata.layer.physical.IConnection;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.profiling.database.Helper;
import bpm.profiling.runtime.core.Connection;
import bpm.profiling.ui.preferences.PreferenceConstants;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryItem;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "bpm.profiling.ui";

	// The shared instance
	private static Activator plugin;
	
	
	public static Helper helper;
	
	public List<Connection> connections = new ArrayList<Connection>();
	
	/**
	 * The constructor
	 */
	public Activator() {
//		IConstants.init(Platform.getInstallLocation().getURL().toString().substring(6) );
	
		
//		Helper.init("resources/profilingContext.xml");
		try {
			helper = Helper.getInstance();
			
		} catch (Throwable e) {
			e.printStackTrace();
			
		}
		
		
		try {
			for(Connection c : helper.getConnectionManager().getConnections()){
				try {
					if (!c.getIsFromRepository()){
						c.connect();
					}
					else{
						rebuildFromFmdt(c);
					}
					connections.add(c);
//				model.addConnection(c);
				} catch (Exception e) {
					e.printStackTrace();
				
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		

		
	}

	private void rebuildFromFmdt(Connection c) throws Exception{
		
		IVanillaContext vanillaContext = new BaseVanillaContext(c.getHost(), c.getLogin(), c.getPassword()); 
		
		IVanillaAPI api = new RemoteVanillaPlatform(vanillaContext);
		bpm.vanilla.platform.core.beans.Repository repository = api.getVanillaRepositoryManager().getRepositoryFromUrl(c.getHost());
		
		IRepositoryContext ctx = new BaseRepositoryContext(vanillaContext, null, repository);
		
		IRepositoryApi sock = new RemoteRepositoryApi(ctx);
		
		RepositoryItem it = sock.getRepositoryService().getDirectoryItem(c.getDirectoryItemId());
		
		if (it == null){
			throw new Exception("The DirectoryItem Specified is no more available in the repository");
		}
		
		String xml = sock.getRepositoryService().loadModel(it);
		
		
		MetaDataDigester dig = new MetaDataDigester(IOUtils.toInputStream(xml), new MetaDataBuilder(sock));
		MetaData fmdtModel = dig.getModel(sock);
		
		
		for(IDataSource ds : fmdtModel.getDataSources()){
			if (ds.getName().equals(c.getFmdtDataSourceName())){
				for(IConnection con : ds.getConnections(null)){
					if (con.getName().equals(c.getFmdtConnectionName())){
						c.connect((SQLDataSource)ds);
						c.setDriverName(((SQLConnection)con).getDriverName());
						c.setHost(((SQLConnection)con).getHost());
						c.setPort(((SQLConnection)con).getPortNumber());
						c.setPassword(((SQLConnection)con).getPassword());
						c.setLogin(((SQLConnection)con).getUsername());
						c.setDatabaseName(((SQLConnection)con).getDataBaseName());
						c.setName(ds.getName());
						break;
					}
				}
				break;
			}
		}
		
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		Activator .getDefault().getPreferenceStore().setValue(PreferenceConstants.P_INSTALLATION_FOLDER, Platform.getInstallLocation().getURL().toString().substring(6));

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	
	
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
//		String root = Platform.getInstallLocation().getURL().getPath();

		reg.put("new", ImageDescriptor.createFromFile(Activator.class, "icons/new.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("fmdt", ImageDescriptor.createFromFile(Activator.class, "icons/fmdt.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("delete", ImageDescriptor.createFromFile(Activator.class, "icons/del.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("directory", ImageDescriptor.createFromFile(Activator.class, "icons/folder.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("condition", ImageDescriptor.createFromFile(Activator.class, "icons/filter.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("table", ImageDescriptor.createFromFile(Activator.class, "icons/physical_table.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("connection", ImageDescriptor.createFromFile(Activator.class, "icons/datasource.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("column", ImageDescriptor.createFromFile(Activator.class, "icons/column.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("schedule", ImageDescriptor.createFromFile(Activator.class, "icons/scheduled.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("evolution", ImageDescriptor.createFromFile(Activator.class, "icons/stock_chart-edit-type-16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("run", ImageDescriptor.createFromFile(Activator.class, "icons/resume.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("add", ImageDescriptor.createFromFile(Activator.class, "icons/add.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("save", ImageDescriptor.createFromFile(Activator.class, "icons/save.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("xls", ImageDescriptor.createFromFile(Activator.class, "icons/xls.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("designer", ImageDescriptor.createFromFile(Activator.class, "icons/designer.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("refresh", ImageDescriptor.createFromFile(Activator.class, "icons/refresh.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("analysis", ImageDescriptor.createFromFile(Activator.class, "icons/cog.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("ruleSet", ImageDescriptor.createFromFile(Activator.class, "icons/ruleset.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("run", ImageDescriptor.createFromFile(Activator.class, "icons/cog_go.png")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public List<Connection> getConnections() {
		return connections;
	}
	
	public Connection getConnection(int id) throws Exception{
		for(Connection c : connections){
			if (c.getId() == id){
				return c;
			}
		}
		
		Connection c = helper.getConnectionManager().getConnection(id);
		
		if (c != null){
			if (c.getIsFromRepository()){
				try{
					rebuildFromFmdt(c);
				}catch(Exception e){
					throw new Exception("Unable to rebuild from FMDT : "+ e.getMessage(), e);
				}
				
			}
			else{
				try{
					c.connect();
				}catch(Exception e){
					throw new Exception("Unable to connect to database", e);
				}
				
			}
			
			connections.add(c);
			
		}
		return c;
	}
}
