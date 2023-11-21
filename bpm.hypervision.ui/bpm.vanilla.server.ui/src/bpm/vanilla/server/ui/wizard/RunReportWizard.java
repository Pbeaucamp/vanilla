package bpm.vanilla.server.ui.wizard;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;

import bpm.birep.admin.connection.AdminAccess;
import bpm.repository.api.model.IDirectoryItem;
import bpm.repository.api.model.IRepository;
import bpm.repository.api.model.IRepositoryConnection;
import bpm.repository.api.model.Parameter;
import bpm.vanilla.server.client.ReportingServer;
import bpm.vanilla.server.client.communicators.TaskDatas;
import bpm.vanilla.server.ui.Activator;
import bpm.vanilla.server.ui.internal.GedStoreTool;

public class RunReportWizard extends Wizard{

	protected RepositoryWizardPage connectionPage;
	protected RepositoryContentWizardPage contentPage;
	protected DirectoryItemParameterPage parameterPage;
	protected VanillaGroupsWizardPage groupPage;
	protected RunningOptionPage runPage;
	
	
	/*
	 * Datas
	 */
	protected IRepositoryConnection sock;
	protected IRepository repository;
	protected IDirectoryItem item;
	protected List<Parameter> params;
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		try {
			connectionPage = new RepositoryWizardPage("connection");
			addPage(connectionPage);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		
		contentPage = new RepositoryContentWizardPage("content");
		addPage(contentPage);
		
		parameterPage = new DirectoryItemParameterPage("parameters");
		addPage(parameterPage);
		
		
		groupPage = new VanillaGroupsWizardPage("goup");
		addPage(groupPage);
		
		runPage = new RunningOptionPage("run");
		addPage(runPage);
		
		super.addPages();
	}



	@Override
	public boolean performFinish() {
		Properties taskProperties = new Properties();
		Properties _p = connectionPage.getProperties();
		
		Enumeration e = _p.propertyNames();
		while(e.hasMoreElements()){
			String pName = (String)e.nextElement();
			taskProperties.put(pName, _p.getProperty(pName));
			
		}
		
		_p = contentPage.getProperties();
		e = _p.propertyNames();
		while(e.hasMoreElements()){
			String pName = (String)e.nextElement();
			taskProperties.put(pName, _p.getProperty(pName));
			
		}
		
		
		
		
		//TODO : getParameters
		Properties parameters = new Properties();
		for(Parameter p : parameterPage.getParametersValues().keySet()){
			parameters.put(p.getName(), parameterPage.getParametersValues().get(p));
		}
		
		
		_p = runPage.getRunProperties();
		
//		int runNumber = Integer.parseInt(_p.getProperty("runNumber"));
		if (_p.getProperty("outputFormat") != null){
			taskProperties.put("outputFormat", _p.getProperty("outputFormat"));
		}
		
		taskProperties.put("taskPriority", _p.getProperty("taskPriority"));
		
		
		HashMap<String, Integer> taskIdByGroup = new HashMap<String, Integer>();
		
		
		for(String s : groupPage.getGroups()){
			try {
				taskProperties.setProperty("groupName", s);
				String itemName = ((IDirectoryItem)contentPage.getDirectoryItem().get(0)).getName();
				IDirectoryItem it = (IDirectoryItem)contentPage.getDirectoryItem().get(0);

				taskIdByGroup.put(s, Activator.getDefault().getServerRemote().launchTask(new TaskDatas("", "", null, taskProperties, parameters, it.getType(), it.getSubTypeConstante())));
				
			} catch (Exception e1) {
				
				e1.printStackTrace();
			}
		}
		String vanillaUrl = "";
		try{
			vanillaUrl = Activator.getDefault().getServerRemote().getServerConfig().getValue("url");
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		if (_p.getProperty("outputName") != null){
			GedStoreTool thread = new GedStoreTool(
					taskIdByGroup, 
					_p.getProperty("outputName"), 
					vanillaUrl, 
					connectionPage.getProperties().getProperty("repositoryId"), 
					_p.getProperty("outputFormat"), 
					(ReportingServer)Activator.getDefault().getServerRemote(), 
					item.getType(), 
					item.getSubTypeConstante(), 
					item.getId(), sock);
			
			thread.start();
		}
		
		return true;
	}



	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
	 */
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (!page.isPageComplete()){
			return super.getNextPage(page);
		}
		
		if (page == connectionPage){
			
			
			boolean loadRep = false;
			if (sock == null || sock.equals(connectionPage.getRepositoryConnection())){
				sock = connectionPage.getRepositoryConnection();
				loadRep = true;
			}
			
			if (loadRep){
				
				BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
					
					public void run() {
						try {
							repository = sock.getRepository();
						} catch (Exception e) {
							repository = null;
							e.printStackTrace();
							MessageDialog.openError(getShell(), "Problem", "Unable to load reposiory content:\n" + e.getCause().getMessage());
							
						}
						contentPage.fillContent(repository);
						if (groupPage != null){
							try{
								String url = Activator.getDefault().getServerRemote().getServerConfig().getValue("url");
								AdminAccess a = new AdminAccess(url);
								List<String> l = new ArrayList<String>();
								for(bpm.birep.admin.datas.vanilla.Group g : a.getGroups()){
									l.add(g.getName());
								}
								groupPage.fillContent(l);
							}catch(Exception ex){
								ex.printStackTrace();
								groupPage.fillContent(sock.getGroups());
							}
							
							
						}
						
					}
				});
				
				
				
			}
			
			if (repository == null){
				return page;
			}
			return contentPage;
		}
		
		
		if (page == contentPage){
			boolean loadParams = false;
			
			if (item == null || item != contentPage.getDirectoryItem()){
				loadParams = true;
				
				for(Object o :  contentPage.getDirectoryItem()){
					if (o instanceof IDirectoryItem){
						item = (IDirectoryItem)o;
					}
				}
				
				
				try{
					if (loadParams){
						params = sock.getParametersFor(item);
						parameterPage.setParameters(params);
						
					}
					return parameterPage;
				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(getShell(), "Problem", "Unable to load item parameters:\n" + ex.getCause().getMessage());
					return page;

				}
				
			}
		}
		
		return super.getNextPage(page);
	}



	
	
	
	
	
	
	
}
