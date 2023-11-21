package bpm.fd.jsp.wrapper.deployer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
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
import bpm.fd.api.core.model.structure.FactoryStructure;
import bpm.fd.api.core.model.structure.Folder;
import bpm.fd.api.core.model.structure.FolderPage;
import bpm.fd.jsp.wrapper.Activator;
import bpm.fd.jsp.wrapper.ComponentFd;
import bpm.fd.runtime.engine.deployer.ProjectDeployer;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class GenerateFromFaweb extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8762168064646355069L;
	private static String MODEL_XML = "model.xml";
	private static String DICO_XML = "dico.xml";

	private String vanillaUrl;
	private String generationPath;
	
//	private File resourcesFolder;
	private String bundleInstallationPath;
	
	
	private ComponentFd component;
	
	public GenerateFromFaweb(ComponentFd component, String vanillaUrl) {
		super();
		this.vanillaUrl = vanillaUrl;
		this.component = component;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	
	@Override
	public void init() throws ServletException {
		super.init();
		
		Bundle bpmFdJspWrapperBundle = Platform.getBundle(Activator.ID);
		if (bpmFdJspWrapperBundle != null){
			component.getLogger().info("bpmFdJspWrapperBundle found");
		}
		else{
			component.getLogger().error("bpmFdJspWrapperBundle not found");
		}

		try{
			bundleInstallationPath = FileLocator.getBundleFile(bpmFdJspWrapperBundle).getAbsolutePath();
		}catch(Exception ex){
			ex.printStackTrace();
			bundleInstallationPath = Platform.getInstallLocation().getURL().getPath() + Activator.ID +  "_" + bpmFdJspWrapperBundle.getHeaders().get("Bundle-Version") + "/";
		}

    	generationPath = bundleInstallationPath  + "/generation";
		component.getLogger().info("GenerateFromFaweb servlet generationPath=" + generationPath);
		
		File f = new File(generationPath);
		if (!f.exists() || !f.isDirectory()){
			f.mkdirs();
			component.getLogger().info("GenerateFromFaweb created Folder for generationPath=" + generationPath);
		}
		
	}
	
	
	private File createUnzipFolder(){
		File resourcesDirectory = new File(bundleInstallationPath + "/fdwebtemp");
		if (!resourcesDirectory.exists() || !resourcesDirectory.isDirectory()){
			resourcesDirectory.mkdirs();
			component.getLogger().info("created Folder " + resourcesDirectory.getAbsolutePath());
		}
		
		
		File f = null;
		
		while(f == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
			f = new File(resourcesDirectory, "unzipped_" + new Object().hashCode() + "_" + sdf.format(Calendar.getInstance().getTime()));
			
			if (f.exists()){
				f = null;
			}
		}
		
		f.mkdirs();
		component.getLogger().info("created Folder " + f.getAbsolutePath()); 
		return f;
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ServletInputStream input = req.getInputStream();
		
		
		
//		String resourcesDirectory = Platform.getInstallLocation().getURL().getPath() + "/plugins/" + Activator.ID +  "_" + bpmFdJspWrapperBundle.getHeaders().get("Bundle-Version") + "/" + "/fdwebtemp/";
		
		File resourcesFolder = createUnzipFolder();
		component.getLogger().info("unzipping into " + resourcesFolder.getAbsolutePath() + " ...");
		
		ZipInputStream zipInput = new ZipInputStream(input);
		Dictionary dico = null;
		
		FdProject project = null;
		
		//unzip files
		ZipEntry zipEntry = null;
		File fileDico = null;
		File fileModel = null;
		
        while ((zipEntry = zipInput.getNextEntry()) != null) {
        	
        	
           	byte[] buffer = new byte[2048];
        	File f = new File(resourcesFolder,zipEntry.getName());
        	component.getLogger().info("copying " + zipEntry.getName());
        	FileOutputStream fileoutputstream = new FileOutputStream(f);
        	component.getLogger().info("copying " + zipEntry.getName() + " to file " + f.getAbsolutePath() + "...");
        	
            int n;

            while ((n = zipInput.read(buffer, 0, 2048)) > -1) {
                fileoutputstream.write(buffer, 0, n);
            }

            fileoutputstream.close();
            zipInput.closeEntry();
            
            
            
            if (zipEntry.getName().equalsIgnoreCase(DICO_XML)){
            	fileDico = f;
            }
            if (zipEntry.getName().equalsIgnoreCase(MODEL_XML)){
            	fileModel = f;
            }
        }
        
      //parse Dictionary
        DictionaryParser parser = new DictionaryParser();
     	try {
     		if (fileDico == null){
     			throw new Exception("FreeDashoard Dictionary file not found within the zip");
     		}
 			dico = parser.parse(new FileInputStream(fileDico));
 		} catch (Exception e) {
 			component.getLogger().error("Error when parsing Dashboard Dictionary from file " + fileDico.getAbsolutePath() + " - " + e.getMessage(), e);
 			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error when parsing Dashboard Dictionary  from file " + fileDico.getAbsolutePath() + " - " + e.getMessage());
 			return;
 		}
        
        
       //parse model		
		FdModelParser modelParser = new FdModelParser(new FactoryStructure(), dico);
    	try {
    		if (fileModel == null){
     			throw new Exception("FreeDashoard Main model file not found within the zip");
     		}
    		modelParser.parse(new FileInputStream(fileModel));
		} catch (Exception e1) {
			component.getLogger().error("Error when parsing Dashboard Model from file " + fileModel.getAbsolutePath() + " - " + e1.getMessage(), e1);
 			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error when parsing Dashboard Model from file " + fileModel.getAbsolutePath()  + e1.getMessage());
 			return;
		}
		
    	try {
			project = modelParser.getProject();
		} catch (Exception e) {
			// never occur
		}
		
		project.setDictionary(dico);
		
		//get the subsModels
		FilenameFilter filterSubModel = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.contains("model") && !name.equals(MODEL_XML);
			}
		};
		
		for(File fileSubModel : resourcesFolder.listFiles(filterSubModel)) {
	    	try {
	    		
	    		FdProject proj = modelParser.parse(new FileInputStream(fileSubModel));
	    		FdModel subMod = proj.getFdModel();
	    		((MultiPageFdProject)project).addPageModel(subMod);
	    		
	    		for(IBaseElement elem : ((Folder)(project.getFdModel().getContent().get(0))).getContent()) {
	    			if(elem instanceof FolderPage) {
	    				FolderPage page = (FolderPage) elem;
	    				if(page.getName().equals(subMod.getName().split("_")[1])) {
		    				page.addToContent(subMod);
		    				break;
	    				}
	    			}
	    		}
	    		
			} catch (Exception e1) {
				component.getLogger().error("Error when parsing Dashboard PageModel from file " + fileSubModel.getAbsolutePath() + " - " + e1.getMessage(), e1);
	 			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error when parsing Dashboard PageModel from file " + fileSubModel.getAbsolutePath()  + e1.getMessage());
	 			return;
			}
			
		}

		
        for(File resource : resourcesFolder.listFiles()) {
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
        	String group = req.getParameter("_group");
       	
        	IVanillaContext vCtx = new BaseVanillaContext(vanillaUrl, user, password);
        	IVanillaAPI api = new RemoteVanillaPlatform(vCtx);
        	IRepositoryContext repCtx = new BaseRepositoryContext(vCtx, 
        			api.getVanillaSecurityManager().getGroupByName(group), 
        			api.getVanillaRepositoryManager().getRepositoryById(repId));
        	
        	
			String relativePath = ProjectDeployer.deploy(
					new ObjectIdentifier(repId, -1),
					repCtx,
					api.getVanillaSecurityManager().authentify("", 
							vCtx.getLogin(), vCtx.getPassword(), false),
					project, req.getLocale().getLanguage(), true, new HashMap<String, String>());
			
			url = "/generation/" + relativePath;
			
		} catch (Exception e) {
			component.getLogger().error("Error generating Dashboard - " + e.getMessage(), e);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating Dashboard - " + e.getMessage());
		}
		
		
		
		
		//delete the temp files
		component.getLogger().info("deleting generated files....");
		for(File f : resourcesFolder.listFiles()) {
			if (f.delete()){
				component.getLogger().info("deleted generated file " + f.getAbsolutePath());
			}
		}
		if (resourcesFolder.delete()){
			component.getLogger().info("deleted generated folder " + resourcesFolder.getAbsolutePath());
		}
		
		PrintWriter writer = new PrintWriter(resp.getOutputStream());
		writer.append(url);
		writer.close();
	}
	
	
	
}
