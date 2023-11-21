package bpm.fd.jsp.wrapper.deployer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.api.core.model.parsers.DictionaryParser;
import bpm.fd.api.core.model.parsers.FdModelParser;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.FileCSS;
import bpm.fd.api.core.model.resources.FileImage;
import bpm.fd.api.core.model.resources.FileJavaScript;
import bpm.fd.api.core.model.resources.FileProperties;
import bpm.fd.api.core.model.resources.IResource;
import bpm.fd.api.core.model.structure.FactoryStructure;
import bpm.fd.api.core.model.structure.Folder;
import bpm.fd.api.core.model.structure.FolderPage;
import bpm.fd.jsp.wrapper.Activator;
import bpm.fd.runtime.engine.deployer.ProjectDeployer;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

//TODO : replace by GenerateFromFaWebServlet
public class DeployFromFdWebServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8762168064646355069L;
	private static String MODEL_XML = "model.xml";
	private static String DICO_XML = "dico.xml";

	private String vanillaUrl;
	private String resourcesDirectory;
	
	public DeployFromFdWebServlet(String vanillaUrl) {
		super();
		this.vanillaUrl = vanillaUrl;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ServletInputStream input = req.getInputStream();
		ZipInputStream zipInput = new ZipInputStream(input);
		
		Bundle bpmFdJspWrapperBundle = Platform.getBundle(Activator.ID);
		
		String bundleLocation = null;
		
		try{
			bundleLocation = FileLocator.getBundleFile(bpmFdJspWrapperBundle).getAbsolutePath();//Platform.getInstallLocation().getURL().getPath() + "/plugins/" + Activator.ID +  "_" + bpmFdJspWrapperBundle.getHeaders().get("Bundle-Version") + "/"; ;
		}catch(Exception ex){
			ex.printStackTrace();
			bundleLocation =Platform.getInstallLocation().getURL().getPath() + "/plugins/" + Activator.ID +  "_" + bpmFdJspWrapperBundle.getHeaders().get("Bundle-Version") + "/"; ;
		}
		String generationPath = bundleLocation + "/generation/";
		
//			String generationPath = Platform.getInstallLocation().getURL().getPath() + "/plugins/" + Activator.ID +  "_" + bpmFdJspWrapperBundle.getHeaders().get("Bundle-Version") + "/" + "/generation/";

		resourcesDirectory = Platform.getInstallLocation().getURL().getPath() + "/plugins/" + Activator.ID +  "_" + bpmFdJspWrapperBundle.getHeaders().get("Bundle-Version") + "/" + "/fdwebtemp/";
		
		File ressDir = new File(resourcesDirectory);
		ressDir.mkdir();
		
		Dictionary dico = null;
		
		FdProject project = null;
		
		
		//unzip files
		ZipEntry zipEntry = zipInput.getNextEntry();
		while (zipEntry != null) {
			
			//unzip the file
			byte[] buffer = new byte[2048];
			FileOutputStream fileoutputstream = new FileOutputStream(resourcesDirectory + zipEntry.getName());
		    int n;

		    while ((n = zipInput.read(buffer, 0, 2048)) > -1) {
		        fileoutputstream.write(buffer, 0, n);
		    }

//		    if(zipEntry.getName().equalsIgnoreCase(MODEL_XML) || (zipEntry.getName().contains(".xml") && !zipEntry.getName().equalsIgnoreCase(DICO_XML))) {
//		    	nbModels++;
//		    }
//		    
		    fileoutputstream.close();
		    zipInput.closeEntry();
		    
		    zipEntry = zipInput.getNextEntry();
		    
		   
		}
		try {
			zipInput.close();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		//get the dictionary
		
		FilenameFilter filterDico = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.equalsIgnoreCase(DICO_XML);
			}
		};
		
		File fileDico = ressDir.listFiles(filterDico)[0];
		
		DictionaryParser parser = new DictionaryParser();
		try {
			FileInputStream dicoStream = new FileInputStream(fileDico);
			dico = parser.parse(dicoStream);
			dicoStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		//get the model
		FilenameFilter filterModel = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.equalsIgnoreCase(MODEL_XML);
			}
		};
		
		File fileModel = ressDir.listFiles(filterModel)[0];
		FdModelParser modelParser = null;
		FactoryStructure factory = new FactoryStructure();
//		if(nbModels > 1) {
//			modelParser = new FdModelParser(factory, dico, true);
//		}
//		else {
//			
//		}
		modelParser = new FdModelParser(factory, dico);
		try {
			FileInputStream modelStream = new FileInputStream(fileModel);
			modelParser.parse(modelStream);
			modelStream.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			project = modelParser.getProject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		project.setDictionary(dico);
		
		//get the subsModels
		FilenameFilter filterSubModel = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.contains(".xml") && !name.equals(MODEL_XML) && !name.equals(DICO_XML);
			}
		};
		
		for(File fileSubModel : ressDir.listFiles(filterSubModel)) {
			try {
				FileInputStream subStream = new FileInputStream(fileSubModel);
				FdProject proj = modelParser.parse(subStream);
				
				FdModel subMod = proj.getFdModel();
				((MultiPageFdProject)project).addPageModel(subMod);
				
				for(IBaseElement elem : ((Folder)(project.getFdModel().getContent().get(0))).getContent()) {
					if(elem instanceof FolderPage) {
						FolderPage page = (FolderPage) elem;
						if(subMod.getName().split("_").length < 2) {
							if(page.getName().equals(subMod.getName().split("_")[0] + "page")) {
			    				page.addToContent(subMod);
			    				break;
							}
						}
						else {
							if(page.getName().equals(subMod.getName().split("_")[1] + "page")) {
			    				page.addToContent(subMod);
			    				break;
							}
						}
					}
				}
				subStream.close();
			} catch (Exception e1) {
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error when parsing Dashboard PageModel from file " + fileSubModel.getAbsolutePath()  + e1.getMessage());
				return;
			}
			
		}
		
		
		
		
		//get resources
		List<IResource> projectResources = new ArrayList<IResource>();
		
		for(File resource : ressDir.listFiles()) {
			if(resource.getName().equalsIgnoreCase(DICO_XML) || resource.getName().equalsIgnoreCase(MODEL_XML) || resource.getName().equalsIgnoreCase("test.zip") || (resource.getName().contains("model") && resource.getName().contains(".xml"))) {
				continue;
			}
			
			String extension = resource.getName().substring(resource.getName().lastIndexOf("."));
			
			if(extension.contains("properties")) {
				FileProperties prop = new FileProperties(resource.getName(), null, resource);
				project.addResource(prop);
			}
			
			else if(extension.contains("css")) {
				FileCSS css = new FileCSS(resource.getName(), resource);
				project.addResource(css);
			}
			
			else if(extension.contains("js")) {
				FileJavaScript js = new FileJavaScript(resource.getName(), resource);
				project.addResource(js);
			}
			
			else {
				FileImage img = new FileImage(resource.getName(), resource);
				project.addResource(img);
			}
			
		}
		
		
		//deploy the dashboard
		String url = "";
		try {
			
			String user = req.getParameter("_user");
			String password = req.getParameter("_password");
			int repId = Integer.parseInt(req.getParameter("_repid"));
			int groupId = Integer.parseInt(req.getParameter("_group"));
			boolean encrypted = Boolean.parseBoolean(req.getParameter("_encrypted"));
			
			IVanillaContext ctx = new BaseVanillaContext(vanillaUrl, 
					ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), 
					ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
			
			IVanillaAPI api = new RemoteVanillaPlatform(ctx);
			Repository def = api.getVanillaRepositoryManager().getRepositoryById(repId);
			Group group = api.getVanillaSecurityManager().getGroupById(groupId);
			
			IRepositoryContext repCtx = new BaseRepositoryContext(ctx, group, def);
			
//			VanillaProfil profil = new VanillaProfil(repCtx,null);
//			String relativePath = ProjectDeployer.deploy(
//					project.getFdModel().getName() + new Object().hashCode() + "_", generationPath, 
//					project, 
//					profil);
			
			String relativePath = ProjectDeployer.deploy(
					new ObjectIdentifier(repId, -2),
					repCtx,
					api.getVanillaSecurityManager().authentify("", ctx.getLogin(), ctx.getPassword(), false),
					project,
					req.getLocale().getLanguage(), true, new HashMap<String, String>());
			
			
			url = "/generation/" + relativePath;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//delete the temp files
		for(File f : ressDir.listFiles()) {
			f.delete();
		}
		ressDir.delete();
		
		PrintWriter writer = new PrintWriter(resp.getOutputStream());
		writer.append(url);
		writer.close();
	}
	
	
	
}
