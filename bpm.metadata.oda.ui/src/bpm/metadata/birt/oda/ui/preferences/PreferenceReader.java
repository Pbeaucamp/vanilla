package bpm.metadata.birt.oda.ui.preferences;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.eclipse.core.runtime.Platform;



public class PreferenceReader {

	
	public static HashMap<String, String> readCommonsProperties() throws IOException{
		String userLocation = Platform.getUserLocation().getURL().getPath().substring(1);
		
		if (userLocation.contains(".bpmconseil")){ //$NON-NLS-1$
			userLocation = userLocation.substring(0, userLocation.indexOf("/serie1")); //$NON-NLS-1$
		}
		else{
			int i = userLocation.lastIndexOf("/"); //$NON-NLS-1$
			i = userLocation.substring(0, i).lastIndexOf("/"); //$NON-NLS-1$
			userLocation = userLocation.substring(0, i) + "/.bpmconseil";  //$NON-NLS-1$
		}
		
		
		
		
		String applicationFilePath = userLocation  + "/serie1/shared"; //$NON-NLS-1$
		applicationFilePath += "/.metadata/.plugins/org.eclipse.core.runtime/.settings/"; //$NON-NLS-1$
		applicationFilePath +=  "bpm_launcher.prefs"; //$NON-NLS-1$
		
		BufferedReader br = new BufferedReader(new FileReader(applicationFilePath));
		String line = ""; //$NON-NLS-1$
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		while((line = br.readLine()) != null){
			if(line.contains("=")){ //$NON-NLS-1$
				int i = line.indexOf("="); //$NON-NLS-1$
				map.put(line.substring(0, i), line.substring(i + 1));
			}
		}
		
		return map;
	}

	
	
}
