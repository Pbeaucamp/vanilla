package bpm.united.olap.wrapper.servlet.excel;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fa.api.olap.OLAPCube;
import bpm.fa.api.olap.OLAPResult;
import bpm.fa.api.olap.unitedolap.UnitedOlapLoaderFactory;
import bpm.fa.api.repository.FaApiHelper;
import bpm.fa.api.repository.RepositoryCubeView;
import bpm.fa.api.utils.parse.DigesterCubeView;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.impl.RuntimeContext;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.components.ged.GedLoadRuntimeConfig;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGedComponent;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.mdm.model.api.IMdmProvider;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.Supplier;
import bpm.mdm.remote.MdmRemote;

public class ConnectionHelper {

	private static IVanillaAPI vanillaApi;
	private static FaApiHelper apiHelper;
	
	public static String loadGroupAndRepositories(String user, String pass, ExcelSession session) throws Exception {
		
		IVanillaAPI api = getVanillaApi();
		
		User vanillaUser = api.getVanillaSecurityManager().authentify("", user, pass, false);
		
		List<Repository> repositories = api.getVanillaRepositoryManager().getUserRepositories(user);
		List<Group> groups = api.getVanillaSecurityManager().getGroups(vanillaUser);
		
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(ExcelConstantes.REQUEST_LOADGRPREP);
		
		Element reps = root.addElement("repositories");
		for(Repository rep : repositories) {
			Element repo = reps.addElement("repository");
			repo.addElement("name").setText(rep.getName());
			repo.addElement("id").setText(rep.getId()+"");
		}
		
		Element elemGrps = root.addElement("groups");
		for(Group grp : groups) {
			Element elemGrp = elemGrps.addElement("group");
			elemGrp.addElement("name").setText(grp.getName());
			elemGrp.addElement("id").setText(grp.getId()+"");
		}
		
		session.setUser(user);
		session.setPassword(pass);
		
		return doc.asXML();
	}
	
	
	
public static String saveGroupAndRepositories(String user, String pass, String repId, String groupId, ExcelSession session) throws Exception {
		
	IVanillaAPI api = getVanillaApi();
	Repository repo = api.getVanillaRepositoryManager().getRepositoryById(Integer.parseInt(repId));
	Group grp = api.getVanillaSecurityManager().getGroupById(Integer.parseInt(groupId));
	
	IVanillaContext vanillaCtx = new BaseVanillaContext(api.getVanillaUrl(), user, pass);
	IRepositoryContext ctx = new BaseRepositoryContext(vanillaCtx, grp, repo);
	
	IRepositoryApi sock = new RemoteRepositoryApi(ctx);
	
		session.setUser(user);
		session.setPassword(pass);
		
		session.setRepository(repo);
		session.setGroup(grp);
		session.setSock(sock);
		
		return "success";		
	}

	

public static String loadFasdItems(ExcelSession session) throws Exception {
	
	
	IRepositoryApi sock = session.getSock();
	
	IRepository rep = new bpm.vanilla.platform.core.repository.Repository(sock, IRepositoryApi.FASD_TYPE);
	
	List<RepositoryItem> fasds = new ArrayList<RepositoryItem>();
	
	findChilds(rep.getRootDirectories(), fasds, rep);
	
	Collections.sort(fasds, new Comparator<RepositoryItem>() {
		@Override
		public int compare(RepositoryItem o1, RepositoryItem o2) {
			return o1.getItemName().compareTo(o2.getItemName());
		}
	});
	
	Document doc = DocumentHelper.createDocument();
	Element root = doc.addElement(ExcelConstantes.REQUEST_LOADFASD);
	Element items = root.addElement("items");
	for(RepositoryItem item : fasds) {
		Element elemItem = items.addElement("item");
		elemItem.addElement("id").setText(item.getId()+"");
		elemItem.addElement("name").setText(item.getItemName());
	}
	
	session.setSock(sock);
	
	return doc.asXML();
}

/*
	public static String loadFasdItems(String repId, String groupId, ExcelSession session) throws Exception {
		
		IVanillaAPI api = getVanillaApi();
		
		Repository repo = api.getVanillaRepositoryManager().getRepositoryById(Integer.parseInt(repId));
		Group grp = api.getVanillaSecurityManager().getGroupById(Integer.parseInt(groupId));
		
		IVanillaContext vanillaCtx = new BaseVanillaContext(api.getVanillaUrl(), session.getUser(), session.getPassword());
		IRepositoryContext ctx = new BaseRepositoryContext(vanillaCtx, grp, repo);
		
		IRepositoryApi sock = new RemoteRepositoryApi(ctx);
		
		IRepository rep = new bpm.vanilla.platform.core.repository.Repository(sock, IRepositoryApi.FASD_TYPE);
		
		List<RepositoryItem> fasds = new ArrayList<RepositoryItem>();
		
		findChilds(rep.getRootDirectories(), fasds, rep);
		
		Collections.sort(fasds, new Comparator<RepositoryItem>() {
			@Override
			public int compare(RepositoryItem o1, RepositoryItem o2) {
				return o1.getItemName().compareTo(o2.getItemName());
			}
		});
		
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(ExcelConstantes.REQUEST_LOADFASD);
		Element items = root.addElement("items");
		for(RepositoryItem item : fasds) {
			Element elemItem = items.addElement("item");
			elemItem.addElement("id").setText(item.getId()+"");
			elemItem.addElement("name").setText(item.getItemName());
		}
		
		session.setRepository(repo);
		session.setGroup(grp);
		session.setSock(sock);
		
		return doc.asXML();
	}
	*/

	private static void findChilds(List<RepositoryDirectory> directories, List<RepositoryItem> fasds, IRepository rep) throws Exception {
	
		if(directories == null) {
			return;
		}
		
		for(RepositoryDirectory dir : directories) {
			findChilds(rep.getChildDirectories(dir), fasds, rep);
			
			for(RepositoryItem item : rep.getItems(dir)) {
				fasds.add(item);
			}
		}
		
	}

	public static String loadCubeNames(String fasdId, ExcelSession session) throws Exception {
		int id = Integer.parseInt(fasdId);
		
		IObjectIdentifier ident = new ObjectIdentifier(session.getRepository().getId(), id);
		IRuntimeContext ctx = new RuntimeContext(session.getUser(), session.getPassword(), session.getGroup().getName(), session.getGroup().getId());
		
		Collection<String> names = getApiHelper().getCubeNames(ident, ctx);
		
		Document doc = DocumentHelper.createDocument();
		
		Element root = doc.addElement(ExcelConstantes.REQUEST_LOADCUBENAME);
		Element elemNames = root.addElement("cubes");
		
		for(String name : names) {
			Element elemCube = elemNames.addElement("cube");
			elemCube.addElement("name").setText(name);
		}
		
		session.setFasdId(id);
		session.setFasdItem(session.getSock().getRepositoryService().getDirectoryItem(id));
		
		return doc.asXML();
	}
	
	
	public static String loadViewNames(String cubeName, String fasdId, ExcelSession session) throws Exception {
		
		int id = Integer.parseInt(fasdId);
		//session.setFasdId(id);
		List<RepositoryItem> views= session.getSock().getRepositoryService().getCubeViews(cubeName, session.getSock().getRepositoryService().getDirectoryItem(id));

		Document doc = DocumentHelper.createDocument();
		
		Element root = doc.addElement(ExcelConstantes.REQUEST_LOADVIEWNAME);
		Element elemNames = root.addElement("views");
		
		for(RepositoryItem view : views) {
			Element elemCube = elemNames.addElement("view");
			elemCube.addElement("name").setText(view.getName());
			elemCube.addElement("id").setText(Integer.toString(view.getId()));	
		}
		
		
		return doc.asXML();
	}
	
	

	public static FaApiHelper getApiHelper() {
		if(apiHelper == null) {
			apiHelper = new FaApiHelper(getVanillaApi().getVanillaUrl(), UnitedOlapLoaderFactory.getLoader());
		}
		return apiHelper;
	}

	public static String loadCube(String cubeName, String fasdId, ExcelSession session) throws Exception {
		int id = Integer.parseInt(fasdId);
		session.setFasdId(id);
		session.setFasdItem(session.getSock().getRepositoryService().getDirectoryItem(id));
		
		IObjectIdentifier ident = new ObjectIdentifier(session.getRepository().getId(), session.getFasdId());
		IRuntimeContext ctx = new RuntimeContext(session.getUser(), session.getPassword(), session.getGroup().getName(), session.getGroup().getId());
		
		OLAPCube cube = getApiHelper().getCube(ident, ctx, cubeName);
		
		OLAPResult res = cube.doQuery();
		
		session.setCubeName(cubeName);
		session.setCube(cube);
		
		return CubeHelper.generateResultXml(res, false);
	}
	
	
	public static String loadView(String viewId, String fasdId, String cubeName, ExcelSession session) throws Exception {
		
		int id = Integer.parseInt(fasdId);
		session.setFasdId(id);
		session.setFasdItem(session.getSock().getRepositoryService().getDirectoryItem(id));
		
		IObjectIdentifier ident = new ObjectIdentifier(session.getRepository().getId(), session.getFasdId());
		IRuntimeContext ctx = new RuntimeContext(session.getUser(), session.getPassword(), session.getGroup().getName(), session.getGroup().getId());
		
		OLAPCube cube = getApiHelper().getCube(ident, ctx, cubeName);		
		
		session.setCubeName(cubeName);
		session.setCube(cube);
				
		int directoryItemId = Integer.parseInt(viewId);
		
		RepositoryItem icube= session.getSock().getRepositoryService().getDirectoryItem(directoryItemId);
		
		String fav = session.getSock().getRepositoryService().loadModel(icube);
		
		Document document = DocumentHelper.parseText(fav);
	    Element root = document.getRootElement();
	    String view = null;
	    view = root.element("view").asXML();
	    
		DigesterCubeView dig = new DigesterCubeView(view);
		RepositoryCubeView repcubeview =  dig.getCubeView();
		
		//OLAPCube cube = infos.getCube();
		session.getCube().setView(repcubeview);
		
		OLAPResult res = session.getCube().doQuery();
			
		return CubeHelper.generateResultXml(res, false);
	}
	
	
public static String loadCurrentView(String view, ExcelSession session) throws Exception {
		
		DigesterCubeView dig = new DigesterCubeView(view);
		RepositoryCubeView repcubeview =  dig.getCubeView();
		
		session.getCube().setView(repcubeview);
		
		OLAPResult res = session.getCube().doQuery();
			
		return CubeHelper.generateResultXml(res, false);
	}
	
	
	
	public static String loadFilters(ExcelSession session) throws Exception {
		Collection<String> filters= session.getCube().getFilters();
		
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(ExcelConstantes.REQUEST_LOADFILTERS);
		Element items = root.addElement("filters");
		for(String filter : filters) {
			Element elemItem = items.addElement("filter");
			elemItem.addElement("uname").setText(filter);
		}
		return doc.asXML();
	}
	
	
	public static void saveCubeView(String name, String comment, String internal, String publicc, int groupId, String groupName, ExcelSession session) throws Exception {
		
		String cubeViewXml = CubeViewHelper.generateCubeViewXml(name,session,vanillaApi);
		cubeViewXml = "<fav><name>" + name + "</name><cubename>" + session.getCubeName() + "</cubename><fasdid>" + session.getFasdId() + "</fasdid>" + cubeViewXml + "</fav>";
		int id =session.getFasdItem().getDirectoryId();
		session.getSock().getRepositoryService().addDirectoryItemWithDisplay(
				IRepositoryApi.FAV_TYPE, 
				-1, 
				session.getSock().getRepositoryService().getDirectory(session.getFasdItem().getDirectoryId()), 
				name, 
				comment, 
				internal, 
				publicc, 
				cubeViewXml, 
				true);		
	}
	

	public static String loadDirectories(ExcelSession session) throws Exception {
		
		IRepository rep = new bpm.vanilla.platform.core.repository.Repository(session.getSock(),IRepositoryApi.FASD_TYPE);
		
		Document doc = DocumentHelper.createDocument();
		
		Element root = doc.addElement("loaddirectories");
		
		for(RepositoryDirectory dir : rep.getRootDirectories()) {
			Element dirElem = createDirectoryElement(root, dir, rep);
		}
		
		return doc.asXML();
	}
	

	private static Element createDirectoryElement(Element root, RepositoryDirectory dir, IRepository rep) throws Exception {
		
		Element dirEl = root.addElement("directory");
		dirEl.addElement("name").setText(dir.getName());
		dirEl.addElement("id").setText(dir.getId()+"");
		
		for(RepositoryDirectory subDir : rep.getChildDirectories(dir)) {
			Element e = createDirectoryElement(dirEl, subDir, rep);
		}
		
		return dirEl;
	}

	
	
	
	public static IVanillaAPI getVanillaApi() {
		if(vanillaApi == null) {
			vanillaApi = new RemoteVanillaPlatform(
					ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL), 
					ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), 
					ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
		}
		return vanillaApi;
	}
	
	
public static String loadConSup(String user, String pass, DataPrepSession session) throws Exception {
		
	IVanillaAPI api = getVanillaApi();
	
	api.getVanillaSecurityManager().authentify("", user, pass, false);
	
	IVanillaContext vanillaCtx = new BaseVanillaContext(api.getVanillaUrl(), user, pass);
	IMdmProvider provider = new MdmRemote(vanillaCtx.getLogin(), vanillaCtx.getPassword(), vanillaCtx.getVanillaUrl(), null, null);
	
	List<Supplier> suppliers = provider.getSuppliers();
	List<List<Contract>> contracts = new ArrayList<List<Contract>>();
	for(Supplier s: suppliers) {
		contracts.add(s.getContracts());
	}
	
	Document doc = DocumentHelper.createDocument();
	
	Element root = doc.addElement(DataPrepExcelConstants.REQUEST_LOADCONSUP);
		Element sups = root.addElement("suppliers");
		for(Supplier sup : suppliers) {
			Element supo = sups.addElement("supplier");
			supo.addElement("name").setText(sup.getName());
			supo.addElement("id").setText(sup.getId()+"");
			
			Element cons = supo.addElement("contracts");
			for(Contract c: sup.getContracts()) {
				Element con = cons.addElement("contract");
				con.addElement("name").setText(c.getName());
				con.addElement("id").setText(c.getId()+"");
				con.addElement("supplierId").setText(sup.getId()+"");
			}
		}
	session.setUser(user);
	session.setPassword(pass);
	
	return doc.asXML();
	}

public static String connect(String user, String pass, DataPrepSession session) throws Exception {
	
	IVanillaAPI api = getVanillaApi();
	api.getVanillaSecurityManager().authentify("", user, pass, false);
	
	Document doc = DocumentHelper.createDocument();
	doc.addElement(DataPrepExcelConstants.REQUEST_CONNECT);

	session.setUser(user);
	session.setPassword(pass);
	
	return doc.asXML();
	}

public static String saveDataPrep(String user, String pass, DataPrepSession session) throws Exception {

		session.setUser(user);
		session.setPassword(pass);

		return "success";		
	}

public static String saveDataPrepData(String user, String pass, DataPrepSession session) throws Exception {

	
	String fileId = "";
String contractId = fileId.substring(fileId.lastIndexOf(",") + 1);
		String format = "xlsx";
		IVanillaAPI api = getVanillaApi();
		String url = api.getVanillaUrl();
		int groupId = -1;
		int userId = 1;
		int repositoryId = 1;
		
		InputStream is = null;
		
		MdmRemote mdmRemote = new MdmRemote(user, pass, url);
		RemoteGedComponent gedComponent = new RemoteGedComponent(url, user, pass);
		
		try {
			Contract contract = mdmRemote.getContract(Integer.parseInt(contractId));
			if (contract.getDocId() != null) {
				GedDocument doc = gedComponent.getDocumentDefinitionById(contract.getDocId());
				contract.setFileVersions(doc);
			}
			GedDocument doc = contract.getFileVersions();

			if (doc != null) {
				gedComponent.addVersionToDocumentThroughServlet(doc.getId(), format, is);
			}
			else {
				List<Integer> groupIds = mdmRemote.getSupplierSecurity(contract.getParent().getId());
				doc = gedComponent.createDocumentThroughServlet("New Document", format, userId, groupIds, repositoryId, is);
				contract.setFileVersions(doc);
			}

			contract.setVersionId(null);
			mdmRemote.addContract(contract);

		} catch (Exception e) {
			e.printStackTrace();
		}
	session.setUser(user);
	session.setPassword(pass);

	return "success";		
}

public static InputStream loadData(String user, String pass, String contractId, DataPrepSession session) throws Exception {
	
	IVanillaAPI api = getVanillaApi();
	String url = api.getVanillaUrl();
	int groupId = -1;
	int userId = 1;
	
	MdmRemote mdmRemote = new MdmRemote(user, pass, url);
	RemoteGedComponent ged = new RemoteGedComponent(url, user, pass);
	
	try {
		Contract contract = mdmRemote.getContract(Integer.parseInt(contractId));
		GedDocument doc = ged.getDocumentDefinitionById(contract.getDocId());
		GedLoadRuntimeConfig config = new GedLoadRuntimeConfig(doc, userId);

		if (contract.getVersionId() != null) {
			DocumentVersion docVersion = ged.getDocumentVersionById(contract.getVersionId());
			config = new GedLoadRuntimeConfig(doc, userId, docVersion.getVersion());
		}
		InputStream is = ged.loadGedDocument(config);
		
		return is;
	} catch (Exception e) {
		e.printStackTrace();
		return null;
	}
	}
}
