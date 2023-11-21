package bpm.vanilla.server.ui.internal;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityResult;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.vanilla.platform.core.beans.tasks.TaskInfo;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.components.ReportHistoricComponent;
import bpm.vanilla.platform.core.components.ReportingComponent;
import bpm.vanilla.platform.core.components.historic.HistoricRuntimeConfiguration;
import bpm.vanilla.platform.core.components.historic.HistorizationConfig.HistorizationTarget;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGedComponent;
import bpm.vanilla.platform.core.remote.impl.components.RemoteHistoricReportComponent;
import bpm.vanilla.platform.core.remote.impl.components.internal.SimpleRunTaskId;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.server.ui.Activator;
import bpm.vanilla.server.ui.Messages;

public class GedStoreTool extends Thread{

	private HashMap<String, Integer> taskIdByGroup;
	private String outputBaseName;
	private String repositoryId;
	private String outputFormat;
	private ReportingComponent reportingRuntimeServer;
	private IRepositoryApi sock;
	private RepositoryItem item;
	
	private List<Integer> storedTaskGeneratedModels = new ArrayList<Integer>();
	private List<Integer> failedTaskGeneratedModels = new ArrayList<Integer>();
	
	
	/**
	 * @param taskIdByGroup
	 * @param outputBaseName
	 * @param vanillaUrl
	 * @param repositoryId
	 * @param outputFormat
	 * @param reportingRuntimeServer
	 */
	public GedStoreTool(HashMap<String, Integer> taskIdByGroup,
			String outputBaseName, String vanillaUrl, String repositoryId,
			String outputFormat, ReportingComponent reportingRuntimeServer,
			RepositoryItem item, IRepositoryApi sock) {
		
		this.taskIdByGroup = taskIdByGroup;
		this.outputBaseName = outputBaseName;
		this.repositoryId = repositoryId;
		this.outputFormat = outputFormat;
		this.reportingRuntimeServer = reportingRuntimeServer;
		this.item = item;
		this.sock = sock;
	}

	public void run(){
		try{
			while(failedTaskGeneratedModels.size() + failedTaskGeneratedModels.size() < taskIdByGroup.size() && taskIdByGroup.size() > 0){
				for(String key : taskIdByGroup.keySet()){
					
					
					int taskId = taskIdByGroup.get(key);
					
					boolean needStorage = true;
					
					/*
					 * check the taskstate 
					 */
					try{
						TaskInfo info = reportingRuntimeServer.getTasksInfo(new SimpleRunTaskId(taskId));
						if (info.getState() ==  ActivityState.ENDED){
							
							if (info.getResult() != ActivityResult.SUCCEED){
								failedTaskGeneratedModels.add(taskId);
							}
														
						}
						else{
							needStorage = false;
						}
					}catch(Exception ex){
						ex.printStackTrace();
						return;
					}
					
					
					for(Integer i : storedTaskGeneratedModels){
						if (i.intValue() == taskId){
							needStorage = false;
							break;
						}
					}
					for(Integer i : failedTaskGeneratedModels){
						if (i.intValue() == taskId){
							needStorage = false;
							break;
						}
					}
					
					
					if (!needStorage){
						continue;
					}
					
					InputStream is = null;
					try{
						is = reportingRuntimeServer.loadGeneratedReport(new SimpleRunTaskId(taskId));
					}catch(Exception ex){
						ex.printStackTrace();
			
					}
					
					ReportHistoricComponent historicComponent = new RemoteHistoricReportComponent(bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault().getVanillaContext());
					
					
					
					IObjectIdentifier identifier = new ObjectIdentifier(Integer.parseInt(repositoryId), item.getId());
					Group group = bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroupByName(key);
					List<Integer> targetIds = new ArrayList<Integer>();
					targetIds.add(group.getId());
					
					HistoricRuntimeConfiguration conf = new HistoricRuntimeConfiguration(
							identifier, 
							group.getId(), 
							HistorizationTarget.Group, 
							targetIds, 
							outputBaseName, 
							outputFormat,
							bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getUserByLogin(bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault().getVanillaContext().getLogin()).getId(),
							null);
					conf.setDirectoryTargetId(item.getDirectoryId() > 0 ? item.getDirectoryId() : null);

					int docId = historicComponent.historizeReport(conf, is);
					
					if(sock.getRepositoryService().getDirectoryItem(item.getId()).isRealtimeGed()) {
						IGedComponent gedComponent = new RemoteGedComponent(bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault().getVanillaContext());
						gedComponent.indexExistingFile(conf, docId, item.isCreateEntry());
					}
					
				}
				try{
					Thread.sleep(2000);
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			
			
			
		}catch(Throwable t){
			t.printStackTrace();
			Display.getDefault().asyncExec(new Runnable(){
				public void run(){
					MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.GedStoreTool_1, storedTaskGeneratedModels.size() + Messages.GedStoreTool_2 + taskIdByGroup.size());
				}
			});
		}
		
	}
	
}
