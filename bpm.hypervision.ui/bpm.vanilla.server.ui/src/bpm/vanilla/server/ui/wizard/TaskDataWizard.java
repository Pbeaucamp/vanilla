package bpm.vanilla.server.ui.wizard;

import java.util.Enumeration;
import java.util.Properties;

import bpm.repository.api.model.IDirectoryItem;
import bpm.repository.api.model.Parameter;
import bpm.vanilla.server.client.communicators.TaskDatas;
import bpm.vanilla.server.client.communicators.TaskList;

public class TaskDataWizard extends RunReportWizard {
	private TaskList list;
	public TaskDataWizard(TaskList list) {
		this.list = list;
	}
	
	
	
	
	/* (non-Javadoc)
	 * @see bpm.vanilla.server.ui.wizard.RunReportWizard#addPages()
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

		runPage = new RunningOptionPage("run");
		addPage(runPage);
	}




	/* (non-Javadoc)
	 * @see bpm.vanilla.server.ui.wizard.RunReportWizard#performFinish()
	 */
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
		if ( _p.getProperty("outputFormat") != null){
			taskProperties.put("outputFormat", _p.getProperty("outputFormat"));
		}
		
		taskProperties.put("taskPriority", _p.getProperty("taskPriority"));
		
		String itemName = ((IDirectoryItem)contentPage.getDirectoryItem().get(0)).getName();
		IDirectoryItem it = (IDirectoryItem)contentPage.getDirectoryItem().get(0);

		TaskDatas td = new TaskDatas(itemName, "", list, taskProperties, parameters, it.getType(), it.getSubTypeConstante());
		list.addTask(td);
		return true;
	}

}
