package bpm.vanilla.security.preferences;



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import bpm.vanilla.oda.commons.Activator;



public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public PreferenceInitializer() {
		
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		
		Properties p = null;
	    
    	File f = new File(Platform.getInstallLocation().getURL().getPath() + File.separator + "resources"+ File.separator +"vanillaserver.properties");
    	if (f.exists()){		
    		p = new Properties();
    		try { 
    			p.load(new FileInputStream(f)); 
    		} 
    		catch (IOException e) { 
    			e.printStackTrace();
    			p = null;
    		} 
    	}
    	else{
    		File folder = new File(Platform.getInstallLocation().getURL().getPath() + File.separator + "resources");
    		if (folder.isDirectory()){
    			p = new Properties();
    			for(String s : folder.list()){
    				if (s.toLowerCase().endsWith(".properties")){
    					Properties pr = new Properties();
    					pr = new Properties();
    		    		try { 
    		    			pr.load(new FileInputStream(f)); 
    		    			
    		    			for(Object k  : pr.keySet()){
    		    				p.setProperty((String)k, pr.getProperty((String)k));
    		    			}
    		    		} 
    		    		catch (IOException e) { 
    		    			e.printStackTrace();
    		    			p = null;
    		    		} 
    				}
    			}
    		}
    	}
		
    	
    	if (p != null && p.getProperty(CommonPreferenceConstants.P_BPM_VANILLA_URL) != null){
    		store.setDefault(CommonPreferenceConstants.P_BPM_VANILLA_URL, p.getProperty(CommonPreferenceConstants.P_BPM_VANILLA_URL)); //$NON-NLS-1$
    	}
    	else{
    		store.setDefault(CommonPreferenceConstants.P_BPM_VANILLA_URL, "http://localhost:7171/VanillaRuntime"); //$NON-NLS-1$
    	}
    	
		
		
	}

}
