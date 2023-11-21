package bpm.mdm.ui;

import java.lang.reflect.Field;

import org.eclipse.datatools.connectivity.oda.design.DesignPackage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import bpm.mdm.model.MdmPackage;
import bpm.mdm.model.api.IMdmProvider;
import bpm.mdm.remote.MdmRemote;
import bpm.mdm.ui.adapters.CollectionAdapter;
import bpm.mdm.ui.adapters.PropertyAdapter;
import bpm.mdm.ui.icons.IconNames;
import bpm.mdm.ui.model.composites.SupplierDetailComposite;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGedComponent;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "bpm.mdm.ui"; //$NON-NLS-1$
	public static final String SOFT_ID = IRepositoryApi.MDM;

	// The shared instance
	private static Activator plugin;

	private static IRepositoryApi repositoryApi;
	
	private ModelControler modelControler = new ModelControler();
	
	public ModelControler getControler(){
		return modelControler;
	}

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
		MdmPackage.eINSTANCE.eClass();
		DesignPackage.eINSTANCE.eClass();

	
		
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

	
	protected void resetMdmProvider(){
		try{
			IVanillaContext ctx = repositoryApi.getContext().getVanillaContext();
			provider = new MdmRemote(ctx.getLogin(), ctx.getPassword(), ctx.getVanillaUrl(), new PropertyAdapter(), new CollectionAdapter());
			provider.loadModel();
			modelControler.setModelReloaded();
		} catch (Exception e) {
			provider = null;
			e.printStackTrace();
			//MessageDialog.openError(getWorkbench().getActiveWorkbenchWindow().getShell(), "Loading Mdm Model", "Unable to load model : " + e.getMessage());
		}
	}
	
	public IMdmProvider getMdmProvider(){
		if(provider == null) {
			resetMdmProvider();
		}
		return provider;
	}
	private IMdmProvider provider ;
	
	
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		for(Field f : IconNames.class.getFields()){
			
			try {
				reg.put((String)f.get(null), ImageDescriptor.createFromFile(Activator.class, "icons/" + f.get(null))); //$NON-NLS-1$
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			
		}
	}

	private SupplierDetailComposite detailComposite;
	
	public void setSupplierDetail(SupplierDetailComposite masterDetails) {
		this.detailComposite = masterDetails;
	}
	
	public SupplierDetailComposite getSupplierDetail() {
		return detailComposite;
	}
	
	private IGedComponent gedComponent;

	private RemoteVanillaPlatform vanillaApi;
	public IGedComponent getGedComponent() {
		if(gedComponent == null) {
			gedComponent = new RemoteGedComponent(repositoryApi.getContext().getVanillaContext());
		}
		return gedComponent;
	}

	public static void setRepositoryApi(IRepositoryApi sock) {
		repositoryApi = sock;
		
	}
	
	public IRepositoryApi getRepositoryApi() {
		return repositoryApi;
	}

	public RemoteVanillaPlatform getVanillaApi() {
		if(vanillaApi == null) {
			vanillaApi = new RemoteVanillaPlatform(repositoryApi.getContext().getVanillaContext());
		}
		return vanillaApi;
	}
}
