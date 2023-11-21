package bpm.fd.jsp.wrapper.deployer;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class BirtPregenerationServlet extends HttpServlet{
	private static final String P_LOGIN = "_login";
	private static final String P_PASSWORD = "_password";
	private static final String P_DIRECTORY_ITEM_ID = "_directoryItemId";
	private static final String P_REPOSITORY_ID = "_repositoryId";
	private static final String P_GROUP_ID = "_groupId";
	
	
	private String vanillaUrl;
	
	
	public BirtPregenerationServlet(String vanillaUrl) {
		super();
		this.vanillaUrl = vanillaUrl;

	}
	
	@Override
	public void init() throws ServletException {
		super.init();
//		FactoryRepositoryServicesStub.setAxisModulesPath(Platform.getInstallLocation().getURL().getPath() + "/resources");
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);

		
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String login = req.getParameter(P_LOGIN);
		String password = req.getParameter(P_PASSWORD);
		String directoryItem = req.getParameter(P_DIRECTORY_ITEM_ID);
		String repository = req.getParameter(P_REPOSITORY_ID);
		String group = req.getParameter(P_GROUP_ID);
		
		PrintWriter pw = new PrintWriter(resp.getOutputStream());
		String path = null;
		
		IVanillaContext vCtx = new BaseVanillaContext(vanillaUrl, login, password);
		IVanillaAPI api = new RemoteVanillaPlatform(vCtx);
		
		try{
			int directoryItemId = Integer.parseInt(directoryItem);
			int groupId = Integer.parseInt(group);
			Group _group = api.getVanillaSecurityManager().getGroupById(groupId);
			Repository repDef = api.getVanillaRepositoryManager().getRepositoryById(Integer.parseInt(repository));
			
			//create IRepositoryContext
			
			IRepositoryContext repCtx = new BaseRepositoryContext(vCtx, _group, repDef);
			
			IRepositoryApi sock = getRepositoryConnection(repCtx, api);
			
			RepositoryItem repItem = findRepositoryItem(sock, directoryItemId);
			
			Document xmlModelDocument = loadXml(sock, repItem);

			BirtOverrider ovvrider = new BirtOverrider(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_BIRT_VIEWER_PATH));
			path = ovvrider.generateBirt(repCtx, xmlModelDocument, directoryItemId, repItem.getItemName());
			pw.write(path);
		}catch(Exception ex){
			ex.printStackTrace();
			pw.write("<error>Error when pregenerating Birt : " + ex.getMessage() + "</error>");
		}
		pw.close();

	}
	
	
	private RepositoryItem findRepositoryItem(IRepositoryApi sock, int directoryItemId) throws Exception{
		
		RepositoryItem repItem = null;
		try{
			repItem = sock.getRepositoryService().getDirectoryItem(directoryItemId);
		}catch(Exception ex){
			throw new Exception("Unable to find RepositoryItem with id = " + directoryItemId + "\n" + ex.getMessage(), ex);
		}
		
		return repItem;
		
		
	}
	
	private Document loadXml(IRepositoryApi sock, RepositoryItem repItem)throws Exception{
		String xml = null;
		
		try{
			xml = sock.getRepositoryService().loadModel(repItem);
		}catch(Exception ex){
			throw new Exception("Unable to load XmlModel fro  RepositoryItem with id = " + repItem.getId() + "\n" + ex.getMessage(), ex);
		}
		
		try{
			return DocumentHelper.parseText(xml);
		}catch(Exception ex){
			throw new Exception("Unable to convert XmlModelDefinition to Dom4J document for  RepositoryItem with id = " + repItem.getId() + "\n" + ex.getMessage(), ex);
		}
	}
	
	
	private IRepositoryApi getRepositoryConnection(IRepositoryContext repCtx, IVanillaAPI api) throws Exception{
		Repository repDef = api.getVanillaRepositoryManager().getRepositoryById(repCtx.getRepository().getId());
		
		if (repDef == null){
			throw new Exception("The repository with id = " + repCtx.getRepository().getId() + " is not registered in the VanillaPlatform");
		}
		IRepositoryApi sock = new RemoteRepositoryApi(repCtx);

	
		return sock;
	}
}
