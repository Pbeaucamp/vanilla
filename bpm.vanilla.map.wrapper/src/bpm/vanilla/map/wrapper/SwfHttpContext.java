package bpm.vanilla.map.wrapper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.http.HttpContext;

/**
 * Look for resources from the file system if not found by the ClassLoader
 * @author ludo
 *
 */
public class SwfHttpContext implements HttpContext{

	private VanillaMapComponent component;
	
	public SwfHttpContext(VanillaMapComponent component){
		this.component = component;
	}
	
	@Override
	public String getMimeType(String name) {
		if(name.contains(".swf")){
			return "application/x-shockwave-flash";
		}
		else{
			return "application/vnd.google-earth.kml+xml";
		}
	}

	@Override
	public URL getResource(String name) {
		component.getLogger().debug("Asking " + name);
		URL url = getClass().getResource(name);
		
		if (name.endsWith("FusionMaps.js")){
			url = getClass().getClassLoader().getResource("/bpm/vanilla/map/wrapper/FusionMaps.js");
		}
		
		
		if (url == null){

			try {
				url = new URL("file:///" + name);
			} catch (MalformedURLException e) {
				
				e.printStackTrace();
			}
		}
		
		component.getLogger().debug("returning " + url.toString());
		return url;
	}

	@Override
	public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		return true;
	}

}
