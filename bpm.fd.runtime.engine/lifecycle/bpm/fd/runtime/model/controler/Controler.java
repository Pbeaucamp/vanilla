package bpm.fd.runtime.model.controler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import bpm.fd.api.core.model.FdProject;
import bpm.fd.runtime.model.DashBoard;
import bpm.fd.runtime.model.DashInstance;
import bpm.fd.runtime.model.FormDashBoard;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;

public class Controler {
	private static Controler instance;

	public static Controler getInstance(){
		if (instance == null){
			instance = new Controler();
		}
		return instance;
	}
	
	private static File deploymentFolder;
	
	public static void initGenerationFolder(String folderPath){
		deploymentFolder = new File(folderPath);
		if (!deploymentFolder.exists()){
			deploymentFolder.mkdirs();
		}
		getInstance();
	}
	
	private Controler(){
		if (deploymentFolder == null){
			throw new RuntimeException("The Dashboard Controler deployment folder has not been inited, call in your code COntroler.init(path)");
		}
	}
	
	private List<DashBoard> dashboards = new ArrayList<DashBoard>();
	
	
	public DashInstance getInstance(String uuid) throws Exception{
		for(DashBoard db : dashboards){
			for(String k : db.getInstancesUuids()){
				if (k.equals(uuid)){
					return db.getInstance(uuid);
				}
			}
			
		}
		throw new InstanceException("No Dashboard instance has been created with this id");
		
	}
	
	private DashBoard findDashBoard(String metaDashIdentifier){
		for(DashBoard d : dashboards){
			if (d.getMeta().getIdentifierString().equals(metaDashIdentifier)){
				
				//Check dashboard update dates
								
				return d;
			}
		}
		return null;
	}
	
	public DashInstance createInstance(IObjectIdentifier identifier, Group group, User user, String localeLanguage, DashBoard d) throws Exception{
		if (d == null){
			throw new Exception("The dashboard has not been deployed");
		}
		DashInstance i = d.createInstance(group, user, localeLanguage);
		return i;
	}


	/**
	 * 
	 * @param id
	 * @param project
	 * @param override if the dashboard has no object identifier (like preview FDWeb) set it to true
	 * @param ctx 
	 * @throws Exception
	 */
	public DashBoard deployDashBoard(IObjectIdentifier id, FdProject project, boolean override, IRepositoryContext ctx) throws Exception{
		
		DashBoard d = null;
		if(!override){
			d = findDashBoard(createDashMetaIdentifierString(id, project));
			
			
		}
		
		if (d == null){
			Logger.getLogger(getClass()).info("Dashboard " + id.toString() + " not yet deployed...");
			
			d = new DashBoard(id, deploymentFolder, project, ctx);
			d.init(override);
			synchronized(dashboards){
				dashboards.add(d);
			}
			Logger.getLogger(getClass()).info("Dashboard " + id.toString() + " deployed");
		}
		else{
			//check update dates
			d.performUpdate(project);
		}
		
		return d;
		
	}
	
	
	private String createDashMetaIdentifierString(IObjectIdentifier identifier, FdProject project){
		StringBuilder b = new StringBuilder();
		b.append(identifier.getRepositoryId());
		b.append("_");
		b.append(identifier.getDirectoryItemId());
		b.append("_");
		b.append(project.getProjectDescriptor().getProjectName());
		return b.toString();
	}
	
	public DashBoard deployForm(boolean isValidation, IObjectIdentifier id, FdProject project, boolean override, HashMap<String, String> hiddenParameters) throws Exception{
		DashBoard d = null;
		if(!override){
			String uuid = createDashMetaIdentifierString(id, project) + "-" + isValidation;
			d = findDashBoard(uuid);
		}
		
		if (d == null){
			Logger.getLogger(getClass()).info("Dashboard " + id.toString() + " not yet deployed...");
			
			d = new FormDashBoard(isValidation, id, deploymentFolder, project, hiddenParameters);
			d.init(override);
			synchronized(dashboards){
				dashboards.add(d);
			}
			Logger.getLogger(getClass()).info("Dashboard " + id.toString() + " deployed");
		}
		
		return d;
	}
	
	
	public void undeployDashBoard(IObjectIdentifier id){
		List<DashBoard> l = new ArrayList<DashBoard>();
		for(DashBoard d : dashboards){
			if (d.getMeta().getIdentifier().equals(id)){
				l.add(d);
			}
		}

		synchronized (dashboards) {
			dashboards.removeAll(l);
		}
	}

	public void destroyAll() {
		dashboards.clear();
		Logger.getLogger(getClass()).info("All Dasboards have destroyed");
		
	}
}
