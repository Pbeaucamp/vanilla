package bpm.vanilla.server.ui.internal;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import bpm.birep.admin.connection.AdminAccess;
import bpm.birep.admin.datas.vanilla.Group;
import bpm.ged.api.commons.constants.AdminConstants;
import bpm.ged.server.client.GedAdmin;
import bpm.ged.server.client.interfaces.GedManager;
import bpm.ged.server.client.model.FactoryGedManager;
import bpm.ged.server.client.properties.ComProperties;
import bpm.ged.server.client.properties.Security;
import bpm.repository.api.model.DirectoryItem;
import bpm.repository.api.model.IDirectoryItem;
import bpm.repository.api.model.IRepositoryConnection;
import bpm.vanilla.server.client.ReportingServer;
import bpm.vanilla.server.client.communicators.TaskInfo;
import bpm.vanilla.server.ui.Activator;

public class GedStoreTool extends Thread{

	private HashMap<String, Integer> taskIdByGroup;
	private String outputBaseName;
	private String vanillaUrl;
	private String repositoryId;
	private String outputFormat;
	private ReportingServer reportingRuntimeServer;
	private int itemType, itemSubtype, itemId;
	private IRepositoryConnection sock;
	
	private List<Integer> storedTaskGeneratedModels = new ArrayList<Integer>();
	private List<Integer> failedTaskGeneratedModels = new ArrayList<Integer>();
	
	
	/**
	 * @param taskIdByGroup
	 * @param outputBaseName
	 * @param vanillaUrl
	 * @param repositoryId
	 * @param outputFormat
	 * @param reportingRuntimeServer
	 * @param itemType
	 * @param itemSubtype
	 * @param itemId
	 */
	public GedStoreTool(HashMap<String, Integer> taskIdByGroup,
			String outputBaseName, String vanillaUrl, String repositoryId,
			String outputFormat, ReportingServer reportingRuntimeServer,
			int itemType, int itemSubtype, int itemId, IRepositoryConnection sock) {
		
		this.taskIdByGroup = taskIdByGroup;
		this.outputBaseName = outputBaseName;
		this.vanillaUrl = vanillaUrl;
		this.repositoryId = repositoryId;
		this.outputFormat = outputFormat;
		this.reportingRuntimeServer = reportingRuntimeServer;
		this.itemType = itemType;
		this.itemSubtype = itemSubtype;
		this.itemId = itemId;
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
						TaskInfo info = reportingRuntimeServer.getTaskInfo(taskId);
						if (info.getState() ==  TaskInfo.STATE_ENDED ){
							
							if (info.getResult() != TaskInfo.RESULT_SUCCEED){
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
						is = reportingRuntimeServer.loadGeneratedReport(taskId);
					}catch(Exception ex){
						ex.printStackTrace();
			
					}
					
										
					
					Security secu = new Security();
					try {
						secu.setRepositoryId(repositoryId);
					} catch (Exception e) {
						
						e.printStackTrace();
					}
				
					/*
					 * look for GroupId
					 */
					Group group = null;
					try{
						group = new AdminAccess(vanillaUrl).getGroupByName(key);
					}catch(Exception ex){
						ex.printStackTrace();
					
						return;
					}
					
					try {
						secu.addGroup(group.getId() + "");
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}
					
					try{
						GedAdmin gedAdmin = new GedAdmin();
						gedAdmin.init(sock, reportingRuntimeServer.getUrl(), secu);
						gedAdmin.saveInGedOrHistorize(is, itemId, group.getName(), outputBaseName, outputFormat);
						storedTaskGeneratedModels.add(taskId);
						break;
					}catch(Exception ex){
						ex.printStackTrace();
						failedTaskGeneratedModels.add(taskId);
						
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
					MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), "File Storage Result", storedTaskGeneratedModels.size() + " files stored on " + taskIdByGroup.size());
				}
			});
		}
		
	}
	
}
