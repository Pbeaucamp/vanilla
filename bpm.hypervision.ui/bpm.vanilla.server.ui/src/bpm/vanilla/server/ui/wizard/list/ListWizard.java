package bpm.vanilla.server.ui.wizard.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import bpm.repository.api.model.IDirectoryItem;
import bpm.repository.api.model.IRepositoryConnection;
import bpm.repository.api.model.Parameter;
import bpm.vanilla.server.client.communicators.TaskDatas;
import bpm.vanilla.server.client.communicators.TaskList;
import bpm.vanilla.server.ui.Activator;
import bpm.vanilla.server.ui.wizard.RepositoryContentWizardPage;
import bpm.vanilla.server.ui.wizard.RepositoryWizardPage;
import bpm.vanilla.server.ui.wizard.RunningOptionPage;

public class ListWizard extends Wizard {

	private ListInfoPage infoPage;
	private RepositoryContentWizardPage contentPage;
	private RepositoryWizardPage repositoryPage;
	private ListPage listPage;
	private RunningOptionPage runPage;
	
	


	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		infoPage = new ListInfoPage("infoPage");
		addPage(infoPage);
		
		
		try {
			repositoryPage = new RepositoryWizardPage("repositoryPage");
			addPage(repositoryPage);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		
		contentPage = new RepositoryContentWizardPage("contentPage");
		addPage(contentPage);
		
		runPage = new RunningOptionPage("runPage");
		addPage(runPage);
		
		listPage = new ListPage("listPage");
		addPage(listPage);
		
		
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
	 */
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == repositoryPage){
			
			IRepositoryConnection sock = repositoryPage.getRepositoryConnection();
			
			try {
				contentPage.fillContent(sock.getRepository());
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		else if (page == contentPage){
			IRepositoryConnection sock = repositoryPage.getRepositoryConnection();
			List<IDirectoryItem> selItems = new ArrayList<IDirectoryItem>();
			
			for(Object o : contentPage.getDirectoryItem()){
				if (o instanceof IDirectoryItem){
					selItems.add((IDirectoryItem)o);
				}
			}
			
			listPage.setInput(selItems, sock);
		}
		
		return super.getNextPage(page);
	}
	
	
	@Override
	public boolean performFinish() {
		TaskList taskList = new TaskList(Activator.getDefault().getServerRemote().getServerType());
		
		taskList.setName(infoPage.getListName());
		taskList.setRepositoryId(repositoryPage.getProperties().getProperty("repositoryId"));
		
		List<IDirectoryItem> dirIt = (List<IDirectoryItem>)contentPage.getDirectoryItem();
		HashMap<IDirectoryItem, HashMap<Parameter, String>> params = listPage.getParameters();
		
		for(IDirectoryItem it : dirIt){
			Properties prop = new Properties();
			Properties prm = new Properties();
			
			for(Parameter p : params.get(it).keySet()){
				prm.setProperty(p.getName(), params.get(it).get(p));
			}
			
			prop.setProperty("directoryItemId", it.getId() + "");
			prop.putAll(repositoryPage.getProperties());
			if (runPage.getRunProperties().getProperty("outputFormat") != null){
				prop.setProperty("outputFormat", runPage.getRunProperties().getProperty("outputFormat"));
			}
			
			prop.setProperty("taskPriority", runPage.getRunProperties().getProperty("taskPriority"));
			
			
			TaskDatas td = new TaskDatas(it.getName(), "", taskList, prop, prm, it.getType(), it.getSubTypeConstante());
			taskList.addTask(td);
		}
		Activator.getDefault().getTaskListManager().addList(taskList);
		
		
		
		return true;
	}
	
}
