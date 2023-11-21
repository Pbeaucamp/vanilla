package bpm.es.datasource.analyzer.ui;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import bpm.birep.admin.client.content.views.ViewTree;
import bpm.es.datasource.analyzer.parsers.BIGAnalyzer;
import bpm.es.datasource.analyzer.parsers.BIRTAnalyzer;
import bpm.es.datasource.analyzer.parsers.BIWAnalyzer;
import bpm.es.datasource.analyzer.parsers.FASDAnalyzer;
import bpm.es.datasource.analyzer.parsers.FDDicoAnalyzer;
import bpm.es.datasource.analyzer.parsers.FMDTAnalyser;
import bpm.es.datasource.analyzer.parsers.IAnalyzer;
import bpm.es.datasource.analyzer.parsers.ModelXmlLoader;
import bpm.es.datasource.analyzer.ui.icons.IconsNames;
import bpm.es.datasource.audit.ui.AuditPerspectiveFactory;
import bpm.vanilla.platform.core.IRepositoryApi;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "bpm.es.datasource.analyzer.ui"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	private List<IAnalyzer> analyzer;
	private ModelXmlLoader xmlLoader;
	
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
		
		getWorkbench().getActiveWorkbenchWindow().addPerspectiveListener(new IPerspectiveListener() {
			
			@Override
			public void perspectiveChanged(IWorkbenchPage page,
					IPerspectiveDescriptor perspective, String changeId) {
				
				
			}
			
			@Override
			public void perspectiveActivated(IWorkbenchPage page,
					IPerspectiveDescriptor perspective) {
				
				ViewTree v = (ViewTree)page.findView(ViewTree.ID);
				
				if (v != null ){
					if (perspective.getId().equals(AuditPerspectiveFactory.ID)){
						v.filterForAnalycticSupport();
					}
					else{
						v.removeFilters();
					}
				}
				
				
			}
		});
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
	 * @param type
	 * @param subtype
	 * @return
	 * @throws Exception if type is not supported
	 */
	public IAnalyzer getAnalyzers(int type, int subtype) throws Exception{
		
		switch (type){
			case IRepositoryApi.FD_DICO_TYPE:
				return new FDDicoAnalyzer();
			case IRepositoryApi.FWR_TYPE:
				throw new Exception("Analyzer : Unsupported type " + type); //$NON-NLS-1$
			case IRepositoryApi.FASD_TYPE:
				return new FASDAnalyzer();
			case IRepositoryApi.CUST_TYPE:
				switch(subtype){
					case IRepositoryApi.BIRT_REPORT_SUBTYPE:
						return new BIRTAnalyzer();
					default :
						throw new Exception("Analyzer : Unsupported type " + type + " and subtype " + subtype); //$NON-NLS-1$ //$NON-NLS-2$
						
				}
			default :
				throw new Exception("Analyzer : Unsupported type " + type); //$NON-NLS-1$
		}
	}
	
	public void setAnalyzers(Point[] types){
		this.analyzer = new ArrayList<IAnalyzer>();
		
		for(int i =0; i < types.length; i++){
			switch(types[i].x){
			case IRepositoryApi.GTW_TYPE:
				this.analyzer.add(new BIGAnalyzer());
				break;
			case IRepositoryApi.FD_DICO_TYPE:
				this.analyzer.add(new FDDicoAnalyzer());
				break;
			case IRepositoryApi.FMDT_TYPE:
				this.analyzer.add(new FMDTAnalyser());
				break;
			case IRepositoryApi.FASD_TYPE:
				this.analyzer.add(new FASDAnalyzer());
				break;
			case IRepositoryApi.BIW_TYPE:
				this.analyzer.add(new BIWAnalyzer());
				break;
			case IRepositoryApi.CUST_TYPE:
				
				switch(types[i].y){
				case IRepositoryApi.BIRT_REPORT_SUBTYPE:
					this.analyzer.add(new BIRTAnalyzer());
					break;
				case IRepositoryApi.JASPER_REPORT_SUBTYPE:
					break;
				}
				break;
			}
		}
	}
	
	public List<IAnalyzer> getAnalyzers(){
		return analyzer;
	}
	
	
	
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		for(Field f : IconsNames.class.getFields()){
			
			try {
				reg.put((String)f.get(null), ImageDescriptor.createFromFile(Activator.class, "icons/" + f.get(null))); //$NON-NLS-1$
			} catch (IllegalArgumentException e) {
				
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				
				e.printStackTrace();
			}
			
		}
		
		
	}
	

}
