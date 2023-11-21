package bpm.swf.wrapper;

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpContext;

public class SwfHttpContext implements HttpContext{

	private BundleContext ctx;
	private SwfChartsProviderComponent component;
	public SwfHttpContext(SwfChartsProviderComponent component){
		this.component = component;
	}
	
	public void setBundleContext(BundleContext context){
		this.ctx = context;
	}
	
	@Override
	public String getMimeType(String name) {
		
		if(name.toLowerCase().endsWith(".swf")){
			return "application/x-shockwave-flash";
		}
		else if (name.toLowerCase().endsWith(".js")){
			return "application/javascript";
		}
		else{
			return "text/plain";
		}
	}

	@Override
	public URL getResource(String name) {
		component.getLogger().debug("asking for " + name);
		URL url = ctx.getBundle().getEntry( name);
		if (url != null){
			try {
				component.getLogger().debug("URL " + url);
				return FileLocator.resolve(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		component.getLogger().warn("Resource " +  name + " not found" );
		return null;
	}

	@Override
	public boolean handleSecurity(HttpServletRequest request,HttpServletResponse response) throws IOException {
		return true;
	}

}
