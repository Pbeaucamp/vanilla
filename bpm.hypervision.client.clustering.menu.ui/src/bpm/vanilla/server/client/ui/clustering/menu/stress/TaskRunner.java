package bpm.vanilla.server.client.ui.clustering.menu.stress;

import java.util.Date;

import bpm.vanilla.platform.core.IVanillaServerManager;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.tasks.TaskInfo;
import bpm.vanilla.platform.core.components.GatewayComponent;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.ReportingComponent;
import bpm.vanilla.platform.core.components.gateway.IGatewayRuntimeConfig;
import bpm.vanilla.platform.core.components.report.IReportRuntimeConfig;
import bpm.vanilla.platform.core.remote.impl.components.internal.SimpleRunTaskId;
import bpm.vanilla.server.client.ui.clustering.menu.Messages;

public class TaskRunner extends Thread{
	private IVanillaServerManager remoteClient;
	private IRuntimeConfig config;
	private User user;
	
	private TaskWaiter waiter;
	private int taskNumber;
	private Integer taskId;
	
	private boolean failed = false;
	private TaskInfo taskInfo;
	
	
	private Date submitStart;
	private Date submitEnd;
	
	
	public TaskRunner(IVanillaServerManager remoteClient, IRuntimeConfig config, int taskNumber, User user){
		this.remoteClient = remoteClient;
		this.config = config;
		this.taskNumber = taskNumber;
		this.user = user;
	}
	
	public String getNumber(){
		return "" + taskNumber; //$NON-NLS-1$
	}
	
	public String getTaskId(){
		return "" + taskId; //$NON-NLS-1$
	}
	
	public String getIdentification(){
		return Messages.TaskRunner_2 + taskNumber + Messages.TaskRunner_3 + taskId;
	}
	

	public void run(){
		try {
			System.out.println(Messages.TaskRunner_4 + taskNumber + Messages.TaskRunner_5 );
			submitStart = new Date();
			if(remoteClient instanceof ReportingComponent) {
				taskId = ((ReportingComponent)remoteClient).runReportAsynch((IReportRuntimeConfig)config, user).getTaskId();
			}
			else if(remoteClient instanceof GatewayComponent) {
				taskId = ((GatewayComponent)remoteClient).runGatewayAsynch((IGatewayRuntimeConfig)config, user).getTaskId();
			}
			submitEnd = new Date();
			System.out.println(Messages.TaskRunner_6 + taskNumber + Messages.TaskRunner_7 + taskId);
		} catch (Exception e) {
			System.err.println(Messages.TaskRunner_8 + taskNumber + " : " + e.getMessage());//$NON-NLS-1$
			e.printStackTrace();
			failed = true;
			return;
			//throw new RuntimeException(e.getMessage(), e);
		}
		
		waiter = new TaskWaiter(1500, remoteClient, taskId, 1000 * 60 * 5);
		waiter.start();
		try {
			System.out.println(Messages.TaskRunner_10 + taskNumber + Messages.TaskRunner_11 + taskId + " ...");//$NON-NLS-1$
			waiter.join();
			System.out.println(Messages.TaskRunner_13 + taskNumber + Messages.TaskRunner_14 + taskId + Messages.TaskRunner_15);
		} catch (Exception e) {
			System.out.println(Messages.TaskRunner_16 + taskNumber + Messages.TaskRunner_17 + taskId + " : " + e.getMessage());//$NON-NLS-1$
			e.printStackTrace();
			failed = true;
			gatherInfo(taskId);
			
		}
		
		gatherInfo(taskId);
		
	}
	
	@Override
	public void interrupt() {
		if (waiter != null ){
			try{
				waiter.interrupt();
			}catch(Throwable t){
				
				try{
					waiter.interrupt();
				}catch(Throwable tt){
					
				}
				waiter = null;
			}
		}
		super.interrupt();
	}
	
	
	private void gatherInfo(int taskId){
		int trials = 0;
		
		while (trials < 3) {
			try {
				taskInfo = remoteClient.getTasksInfo(new SimpleRunTaskId(taskId));
				if (taskInfo != null) {
					return;
				}
			} catch (Exception e) {
				System.out.println(Messages.TaskRunner_22 + trials + Messages.TaskRunner_23 + taskNumber);
			}
			
			try {
				this.sleep(1000);//=1sec
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			trials++;
			System.out.println(Messages.TaskRunner_24 + trials + "...");//$NON-NLS-1$
			
		}
	}
	
	public TaskInfo getTaskInfo(){
		return taskInfo;
	}
	
	public boolean hasProfilingSucceed(){
		return !failed;
	}
	
	public long getSubmissionTime(){
		return submitEnd.getTime() - submitStart.getTime();
	}
}
