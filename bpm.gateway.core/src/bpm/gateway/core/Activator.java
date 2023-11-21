package bpm.gateway.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.SafeRunner;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import bpm.gateway.core.ext.TransformationExtension;
import bpm.vanilla.map.core.design.IFactoryModelObject;
import bpm.vanilla.map.core.design.IMapDefinitionService;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.Customer;



/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "bpm.gateway.core";

	// The shared instance
	private static Activator plugin;
	
	private static List<TransformationExtension> additionalTransformations = new ArrayList<TransformationExtension>();
	private static List<AbrstractDigesterTransformation> additionalDigesters = new ArrayList<AbrstractDigesterTransformation>();
	
	
	private ServiceReference norparenaDaoService;
	private ServiceReference norparenaFactory;
	
	private Customer customer;
	
	private static Logger logger = Logger.getLogger("bpm.gateway.core");
	
	static{
		boolean hasAppender = logger.getAllAppenders().hasMoreElements();
		
		if (!hasAppender){
			ConsoleAppender appender = new ConsoleAppender(new SimpleLayout());
			logger.addAppender(appender);
			logger.setLevel(Level.DEBUG);
		}
		
	}
	public static Logger getLogger(){
		return logger;
	}
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();

		for(IExtensionPoint e : extensionRegistry.getExtensionPoints("bpm.gateway.core")){
			for(IExtension x : e.getExtensions()){
				/*
				 * create all Aditional TransformationExtensions 
				 */
				for(IConfigurationElement c : x.getConfigurationElements()){
					final TransformationExtension ext = new TransformationExtension();
					final IConfigurationElement _c = c;
					
					
					
					ISafeRunnable runnable = new ISafeRunnable() {
						public void handleException(Throwable exception) {
							exception.printStackTrace();
						}

						public void run() throws Exception {
							ext.setTransformationDefinitionName(_c.getAttribute("name"));
							ext.setTransformationDefinitionDescription(_c.getAttribute("description"));
							ext.setCategory(_c.getAttribute("transformationCategory"));
							/*
							 * set Digester Callbacks
							 */
							Object o = _c.createExecutableExtension("digester");
							ext.setCallbackDigester((AbrstractDigesterTransformation)o);
							additionalDigesters.add(ext.getCallbackDigester());
							
							/*
							 * set the Transfo class
							 */
							Class newClass = _c.createExecutableExtension("definitionClass").getClass();
							ext.setTrClass(newClass);
							
							addTransformationExtension(ext);
						}
					};
					SafeRunner.run(runnable);

					
					
					
					
				}
			}
		}
		

	}

	public static List<AbrstractDigesterTransformation> getAdditionalDigesters(){
		return additionalDigesters;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
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
	 * 
	 * @return All additionalDigesters
	 */
	public static List<TransformationExtension> getTransformationExtension(){
		return new ArrayList<TransformationExtension>(additionalTransformations);
	}
	
	
	
	/**
	 * add a transformation extension from plugins
	 * @param ext
	 */
	private static void addTransformationExtension(TransformationExtension ext){
		boolean exist = false;
		for(TransformationExtension x : getTransformationExtension()){
			if (x.equals(ext)){
				exist = true;
				break;
			}
		}
		if (!exist){
			additionalTransformations.add(ext);
		}
		
	}
	
	
	public synchronized IMapDefinitionService getNorparenaMapService() throws Exception{
		try{
			if (norparenaDaoService == null){
				norparenaDaoService = getBundle().getBundleContext().getServiceReference(IMapDefinitionService.class.getName());	
			}
			return (IMapDefinitionService)getBundle().getBundleContext().getService(norparenaDaoService);
		}catch(Throwable ex){
			ex.printStackTrace();
			throw new Exception("Error when getting Norparena Dao Service:\n" + ex.getMessage(), ex);
		}
	}

	public synchronized IFactoryModelObject getNorparenaFactory() throws Exception{
		try{
			if (norparenaFactory == null){
				norparenaFactory = getBundle().getBundleContext().getServiceReference(IFactoryModelObject.class.getName());	
			}
			return (IFactoryModelObject)getBundle().getBundleContext().getService(norparenaFactory);
		}catch(Throwable ex){
			ex.printStackTrace();
			throw new Exception("Error when getting Norparena Factory Service:\n" + ex.getMessage(), ex);
		}
	}
	
	
	public boolean releaseNorparenaFactory(){
		return getBundle().getBundleContext().ungetService(norparenaFactory);
	}
	
	public boolean releaseNorparenaDao(){
		return getBundle().getBundleContext().ungetService(norparenaDaoService);
	}

	public Customer getCurrentCustomer() {
		if (customer == null) {
			this.customer = ConfigurationManager.getInstance().getVanillaConfiguration().getCustomer();
		}
		return customer;
	}
}
