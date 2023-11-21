package bpm.vanilla.platform.core.wrapper.servlets40.accessibility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.beans.PublicUrl;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameterCreator;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.impl.RuntimeConfiguration;
import bpm.vanilla.platform.core.remote.impl.components.RemoteFaView;
import bpm.vanilla.platform.core.utils.IOWriter;

public class FaViewRunner implements IPublicUrlRunner{

	private IVanillaComponentProvider component;
	private User user;
	public FaViewRunner(IVanillaComponentProvider component, User user){
		this.component = component;
		this.user = user;
	}
	@Override
	public String runObject(HttpServletResponse resp, PublicUrl publicUrl, IObjectIdentifier identifier, HashMap<String, String> parameters) throws Exception{
		
		//create RunConfig
		List<VanillaGroupParameter> vanillaParameters = VanillaParameterCreator.createVanillaGroupParameters(parameters);
		
		
		RuntimeConfiguration conf = new RuntimeConfiguration(publicUrl.getGroupId(), identifier, vanillaParameters);
		try {
			List<IVanillaComponentIdentifier> ids = component.getVanillaListenerComponent().getRegisteredComponents(VanillaComponentType.COMPONENT_UNITEDOLAP, false);
			
			if (ids.isEmpty()){
				throw new Exception("There is no FreeDashboard Component currently available within the VanillaPlatform");
			}
			
			RemoteFaView remote = new RemoteFaView(ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl(), 
					user.getLogin(), user.getPassword());
			
			
			InputStream is = null;
			try{
				Logger.getLogger(getClass()).info("Launching Dashboard ....");
				is = remote.getFaViewHtml(conf);
				Logger.getLogger(getClass()).info("Dashboard successfully executed");
			}catch(Exception ex){
				Logger.getLogger(getClass()).error("Failed to execute Report - " + ex.getMessage(), ex);
				throw new Exception("Failed to execute Report - " + ex.getMessage(), ex);
			}
			
			
			
			
			StringBuffer buf = new StringBuffer();
			buf.append("<html>\n");
			buf.append("    <head>\n");
			buf.append("    <link rel=\"stylesheet\" type=\"text/css\" href=\"../images/vanillaExternalCss.css\"/>\n");
			buf.append("    </head>\n");
			
			buf.append("    <body>\n");

			
			buf.append("<div class=\"centre\">\n");
			//buf.append("    <div style=\"float:left; margin-top=auto;\"><img src=\"../images/Vanilla_Logo.gif\" /></div>\n");
			buf.append("    <div class=\"entete\"><p>Olap View Content</p></div>\n");
			buf.append("<br>\n");
			
			
			ByteArrayOutputStream viewos = new ByteArrayOutputStream();
			
			IOWriter.write(is, viewos, true, true);
			buf.append("<div style=\"color:black;}\">\n");
			buf.append(viewos.toString());
			buf.append("</div>\n"); 
			
			buf.append("</div>\n");
			buf.append("    </body>\n");
			buf.append("</html>\n");
			
			IOWriter.write(new ByteArrayInputStream(buf.toString().getBytes()), resp.getOutputStream(), true, true);
			
			return buf.toString();
			
		} catch (Exception e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			throw e;
		}
	}

}
