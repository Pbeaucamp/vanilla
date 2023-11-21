package bpm.vanilla.platform.core.wrapper.servlets40;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.PublicUrl;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.runtime.components.ExternalAccessibilityManager;

public class ExternalVanillaAccessServlet extends HttpServlet {

	public static final String ITEM_TYPE = "itemtype";
	public static final String OBJECT_TYPE = "objecttype";
	
	private IVanillaComponentProvider component;

	public ExternalVanillaAccessServlet(IVanillaComponentProvider component) {
		this.component = component;
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		try {
			String itemType = req.getParameter(ITEM_TYPE);
			String objectType = req.getParameter(OBJECT_TYPE);
			
			BaseVanillaContext vanillaCtx = new BaseVanillaContext(ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl(), ConfigurationManager.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), ConfigurationManager.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
			
			if(objectType.equals("url")) {
				List<PublicUrl> urls =((ExternalAccessibilityManager)component.getExternalAccessibilityManager()).getAllPublicUrls();
				JSONArray json = new JSONArray();
				for(PublicUrl url : urls) {
					try {
						if(url.getEndDate().before(new Date()) || !url.getOutputFormat().equalsIgnoreCase("html")) {
							continue;
						}
						JSONObject urlJson = new JSONObject();
						String start = ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaExternalUrl() + "/vanilla40/accessibility?publickey=";
						urlJson.put("url", start + url.getPublicKey());
						
						Repository rep = component.getRepositoryManager().getRepositoryById(url.getRepositoryId());
						Group group = component.getSecurityManager().getGroupById(url.getGroupId());
						IRepositoryApi sock = new RemoteRepositoryApi(new BaseRepositoryContext(vanillaCtx, group, rep));
						
						urlJson.put("name", sock.getRepositoryService().getDirectoryItem(url.getItemId()).getName());
						json.put(urlJson);
					} catch(Exception e) {
						continue;
					}
				}
				resp.getWriter().write(json.toString());
				resp.getWriter().flush();
				resp.getWriter().close();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		
	}
	
}
