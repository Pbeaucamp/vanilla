package bpm.vanilla.platform.core.wrapper.pictures;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.service.http.HttpContext;



public class PictureHttpContext implements HttpContext{

	@Override
	public String getMimeType(String name) {
		
		return null;
	}

	@Override
	public URL getResource(String name) {
		String pictureName = name.substring(name.lastIndexOf("/") + 1);
		URL url = PictureHttpContext.class.getClassLoader().getResource("/bpm/vanilla/platform/core/wrapper/pictures/" + pictureName);
		
		/*
		 * just to return the picture used by the Vanilla Login JSP
		 * 
		 */
		if (url == null && name.contains("/logos/")){
			try{
				Bundle wrapperBundle = Platform.getBundle("bpm.vanilla.platform.core.wrapper");	
				File file = wrapperBundle.getBundleContext().getDataFile("images/" + pictureName);
				if (file.exists() && file.isFile()){
					url =  file.toURI().toURL();
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
		}
		
		return url;
	}

	@Override
	public boolean handleSecurity(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		
		return true;
	}

}
