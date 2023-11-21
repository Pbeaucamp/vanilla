package bpm.birt.fusioncharts.core;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;

import bpm.birt.fusioncharts.core.swf.Charts;


/**
 * The activator class controls the plug-in life cycle
 * 
 * To make this plugins working, you need to add 
 *  <init-param>
 * 	  <param-name>bpm.vanilla.server.runtime.url</param-name>
 *    <param-value>http://localhost:7171/VanillaRuntime</param-value>
 *  </init-param>
 *  
 *  int Vanilla Tomcat -> webapps -> VanillaRuntime -> WEB-INF -> Web.xml
 */
public class Activator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "bpm.birt.fusioncharts.core";
	private ServiceReference httpServiceRef;

	// The shared instance
	private static Activator plugin;
	/**
	 * Url of the vanilla server to allow to use use adminbirep-api 
	 */
	private static final String VANILLA_SERVER_RUNTIME_URL  = "bpm.vanilla.server.runtime.url"; 

	private String vanillaRuntimeUrl;
	
	private HashMap<String, String> chartsMap;
	
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
		
		//We set the vanillaRuntimeUrl if a vanilla server is running
		httpServiceRef = context.getServiceReference( HttpService.class.getName() );
        if( httpServiceRef != null ) {
            vanillaRuntimeUrl = ((String)httpServiceRef.getProperty(VANILLA_SERVER_RUNTIME_URL));
        }
        
		initializeCharts();
	}
	
	private void initializeCharts(){
		chartsMap = new HashMap<String, String>();
		try {
			for(Field f : Charts.class.getFields()){
				URL resource = Activator.class.getResource("swf/" + f.get(null));
				try {
					chartsMap.put(String.valueOf(f.get(null)), new Path(FileLocator.toFileURL(resource).getPath()).toString());
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			}
		} catch (IllegalArgumentException e) {
			
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			
			e.printStackTrace();
		}
	}
	
	public String getChart(String name){
		return chartsMap.get(name);
	}
	
	public String getFusionChartJS(){
		return chartsMap.get(Charts.FUSION_CHART_JS);
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

	public String getVanillaRuntimeUrl() {
		return vanillaRuntimeUrl;
	}
}
