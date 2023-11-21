package bpm.norparena.mapmanager;

import java.lang.reflect.Field;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import bpm.norparena.mapmanager.icons.Icons;
import bpm.vanilla.map.core.design.IFactoryModelObject;
import bpm.vanilla.map.core.design.IMapDefinitionService;
import bpm.vanilla.map.core.design.IOpenGisMapService;
import bpm.vanilla.map.core.design.fusionmap.IFactoryFusionMap;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapObject;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapRegistry;
import bpm.vanilla.map.core.design.kml.IFactoryKml;
import bpm.vanilla.map.core.design.kml.IKmlObject;
import bpm.vanilla.map.core.design.kml.IKmlRegistry;
import bpm.vanilla.map.model.fusionmap.impl.FactoryFusionMap;
import bpm.vanilla.map.model.impl.FactoryMapModel;
import bpm.vanilla.map.model.kml.impl.FactoryKml;
import bpm.vanilla.map.remote.core.design.fusionmap.impl.RemoteMapDefinitionService;
import bpm.vanilla.map.remote.core.design.impl.RemoteFusionMapRegistry;
import bpm.vanilla.map.remote.core.design.impl.RemoteOpenGisMapService;
import bpm.vanilla.map.remote.core.design.kml.impl.RemoteKmlRegistry;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "bpm.norparena.mapmanager"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	

	private IFactoryModelObject factoryMapModel;
	private IMapDefinitionService mapDefinitionService;
	private IFusionMapRegistry fusionMapRegistry;
	private IFactoryFusionMap factoryFusionMap;
	private IKmlRegistry kmlRegistry;
	private IFactoryKml factoryKml;
	private IOpenGisMapService openGisService;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
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

	
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		for(Field f : Icons.class.getFields()){
			
			try {
				reg.put((String)f.get(null), ImageDescriptor.createFromFile(Activator.class, "icons/" + f.get(null))); //$NON-NLS-1$
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public IRepositoryApi getSocket(){
		return bpm.norparena.ui.menu.Activator.getDefault().getSocket();
	}
	
	public int getRepositoryId() {
		return bpm.norparena.ui.menu.Activator.getDefault().getRepositoryContext().getRepository().getId();
	}
	
	public IVanillaAPI getVanillaApi(){
		return bpm.norparena.ui.menu.Activator.getDefault().getRemote();
	}

	public IMapDefinitionService getDefinitionService() throws Exception{
		if (mapDefinitionService == null){
			try {
				mapDefinitionService = new RemoteMapDefinitionService();
			}catch(Exception ex){
				ex.printStackTrace();
				throw new Exception(Messages.Activator_2 + ex.getMessage(), ex);
			}
			
		}
		mapDefinitionService.configure(bpm.norparena.ui.menu.Activator.getDefault().getRepositoryContext().getVanillaContext().getVanillaUrl());
		return mapDefinitionService;
	}
	
	public IFactoryModelObject getFactoryMap() throws Exception{
		if (factoryMapModel == null){
			try {
				factoryMapModel = new FactoryMapModel();
			}catch(Exception ex){
				ex.printStackTrace();
				throw new Exception(Messages.Activator_3 + ex.getMessage(), ex);
			}
		}
		
		return factoryMapModel;
		
	}
	
	
	public IFusionMapRegistry getFusionMapRegistry() throws Exception{
		if (fusionMapRegistry == null){
			try {
				fusionMapRegistry = new RemoteFusionMapRegistry();
			}catch(Exception ex){
				ex.printStackTrace();
				throw new Exception(Messages.Activator_4 + ex.getMessage(), ex);
			}
		}
		fusionMapRegistry.configure(bpm.norparena.ui.menu.Activator.getDefault().getRepositoryContext().getVanillaContext().getVanillaUrl());
		return fusionMapRegistry;
		
	}
	
	public IFactoryFusionMap getFactoryFusionMap() throws Exception{
		if (factoryFusionMap == null){
			try {
				factoryFusionMap = new FactoryFusionMap();
			}catch(Exception ex){
				ex.printStackTrace();
				throw new Exception(Messages.Activator_5 + ex.getMessage(), ex);
			}
		}
		
		return factoryFusionMap;
		
	}
	
	public IKmlRegistry getKmlRegistry() throws Exception{
		if (kmlRegistry == null){
			try {
				kmlRegistry = new RemoteKmlRegistry();
			}catch(Exception ex){
				ex.printStackTrace();
				throw new Exception(Messages.Activator_6 + ex.getMessage(), ex);
			}
		}
		kmlRegistry.configure(bpm.norparena.ui.menu.Activator.getDefault().getRepositoryContext().getVanillaContext().getVanillaUrl());
		return kmlRegistry;
	}

	public IFactoryKml getFactoryKml() throws Exception{
		if (factoryKml == null){
			try {
				factoryKml = new FactoryKml();
			}catch(Exception ex){
				ex.printStackTrace();
				throw new Exception(Messages.Activator_7 + ex.getMessage(), ex);
			}
		}
		
		return factoryKml;
	}
	
	public String getKmlPathUrl(){
		return bpm.norparena.ui.menu.Activator.getDefault().getRepositoryContext().getVanillaContext().getVanillaUrl() + IKmlObject.KML_PATH;
	}
	
	public String getFusionMapPathUrl(){
		return bpm.norparena.ui.menu.Activator.getDefault().getRepositoryContext().getVanillaContext().getVanillaUrl() + IFusionMapObject.FUSION_MAP_PATH;
	}
	
	public IOpenGisMapService getOpenGisService() {
		if(openGisService == null) {
			openGisService = new RemoteOpenGisMapService();
			((RemoteOpenGisMapService)openGisService).setVanillaRuntimeUrl(bpm.norparena.ui.menu.Activator.getDefault().getRepositoryContext().getVanillaContext().getVanillaUrl());
		}
		
		return openGisService;
	}
}
