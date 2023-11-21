package bpm.vanilla.repository.services.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.repository.beans.RepositoryRuntimeComponent;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ExternalServlet extends HttpServlet {

	private RepositoryRuntimeComponent component;

	public ExternalServlet(RepositoryRuntimeComponent component) {
		this.component = component;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.addHeader("Access-Control-Allow-Origin", "*");
		String login = req.getParameter("login");
		String pass = req.getParameter("pass");
		String groupName = req.getParameter("group");
		String repositoryName = req.getParameter("repository");
		
		try {
//			User user = component.getVanillaRootApi().getVanillaSecurityManager().authentify(req.getRemoteAddr(), login, pass, false);
			IVanillaAPI userApi = new RemoteVanillaPlatform(ConfigurationManager.getProperty(VanillaConfiguration.P_VANILLA_URL), login, pass);
			
			IRepositoryApi api = new RemoteRepositoryApi(new BaseRepositoryContext(userApi.getVanillaContext(), userApi.getVanillaSecurityManager().getGroupByName(groupName), userApi.getVanillaRepositoryManager().getRepositoryByName(repositoryName)));

			Repository r = new Repository(api, IRepositoryApi.FMDT_TYPE);
			List<RepositoryDirectory> tree = r.getRepositoryTree();
			
			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(resp.getOutputStream(), tree);
			
		} catch(Exception e) {
			throw new ServletException(e);
		}
		
		
	}
	
}
