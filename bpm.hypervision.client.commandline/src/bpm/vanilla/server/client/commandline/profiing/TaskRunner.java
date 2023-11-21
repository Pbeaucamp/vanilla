package bpm.vanilla.server.client.commandline.profiing;

import java.util.Date;

import bpm.vanilla.server.client.ServerClient;
import bpm.vanilla.server.client.communicators.TaskDatas;
import bpm.vanilla.server.client.communicators.TaskInfo;
import bpm.vanilla.server.client.utils.TaskWaiter;

public class TaskRunner extends Thread{
	private TaskDatas taskDatas;
	private ServerClient remoteClient;
	
	private TaskWaiter waiter;
	private int taskNumber;
	private Integer taskId;
	
	private boolean failed = false;
	private TaskInfo taskInfo;
	
	
	private Date submitStart;
	private Date submitEnd;
	
	
	public TaskRunner(ServerClient remoteClient, TaskDatas taskDatas, int taskNumber){
		this.remoteClient = remoteClient;
		this.taskDatas = taskDatas;
		this.taskNumber = taskNumber;
	}
	
	public String getNumber(){
		return "" + taskNumber;
	}
	
	public String getTaskId(){
		return "" + taskId;
	}
	
	public String getIdentification(){
		return "Task " + taskNumber + " with taskId=" + taskId;
	}
	
	
	public void run(){
		try {
			System.out.println("Submiting report " + taskNumber + " on Vanilla Runtime Server ..." );
			submitStart = new Date();
			taskId = remoteClient.launchTask(taskDatas);
			submitEnd = new Date();
			System.out.println("Report " + taskNumber + " submited as Task with id=" + taskId);
		} catch (Exception e) {
			System.err.println("Error when submiting report " + taskNumber + " : " + e.getMessage());
			e.printStackTrace();
			failed = true;
			return;
		}
		
		waiter = new TaskWaiter(1500, remoteClient, taskId, 1000 * 60 * 60);
		waiter.start();
		try {
			System.out.println("Waiting Report " + taskNumber + " submited as Task with id=" + taskId + " ...");
			waiter.join();
			System.out.println("Report " + taskNumber + " submited as Task with id=" + taskId + " has run.");
		} catch (Exception e) {
			System.out.println("Error while Waiting Report " + taskNumber + " submited as Task with id=" + taskId + " : " + e.getMessage());
			e.printStackTrace();
			failed = true;
			gatherInfo(taskId);
			return;
		}
		
		gatherInfo(taskId);
		
	}
	
	
	private void gatherInfo(int taskId){
		System.out.println("Gather runtime information on Report " + taskNumber + " submited as Task with id=" + taskId + " ...");
		
		int trials = 0;
		
		while (trials < 3) {
			try {
				taskInfo = remoteClient.getTaskInfo(taskId);
				if (taskInfo != null) {
					return;
				}
			} catch (Exception e) {
				System.out.println("Trial nb " + trials + " failed. Waiting 1s more for task " + taskNumber);
			}
			
			try {
				this.sleep(1000);//=1sec
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			trials++;
			System.out.println("Trial " + trials + "...");
			
		}
//		try {
//			System.out.println("Gather runtime information on Report " + taskNumber + " submited as Task with id=" + taskId + " ...");
//			
//			taskInfo = remoteClient.getTaskInfo(taskId);
//			System.out.println("Runtime information gathered on Report " + taskNumber + " submited as Task with id=" + taskId);
//			
//		} catch (Exception e) {
//			try {
//				System.out.println("Retrying...");
//				this.sleep(1000);//1000ms, = 1s
//				taskInfo = remoteClient.getTaskInfo(taskId);
//			} catch (Exception ee) {
//				System.out.println("Error while Gathering runtime information on  Report " + taskNumber + " submited as Task with id=" + taskId + " : " + e.getMessage());
//				ee.printStackTrace();
//				return;
//			}
//		}
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
