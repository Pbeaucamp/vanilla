package bpm.vanilla.server.client.ui.clustering.menu.stress;

import java.util.Date;

import bpm.vanilla.platform.core.IVanillaServerManager;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.vanilla.platform.core.beans.tasks.TaskInfo;
import bpm.vanilla.platform.core.remote.impl.components.internal.SimpleRunTaskId;

/**
 * this class provide a convenient way to wait until a Task is
 * ended from a server
 * 
 * it only ping the server until the task is stopped
 * 
 * @author ludo
 *
 */
public class TaskWaiter extends Thread{

	private IVanillaServerManager remote;
	private long delay;
	private int taskId;
	
	private long timeOut;
	private boolean active = false;

	public TaskWaiter(long delay, IVanillaServerManager server, int taskId, long timeOut){
		this.delay = delay;
		this.remote = server;
		this.taskId = taskId;
		this.timeOut = timeOut;
	}
	
	
	/**
	 * check the task with the given id every dely second
	 * until the TaskSate is TaskInfo.STATE_ENDED
	 * @throws Exception :  if an error occur when communnicating with the server
	 */
	public void run(){
		active = true;
		TaskInfo inf = null;
		Date startDate = new Date();
		
		int failedAttempts = 0;
		
		do{
			try{
				Thread.sleep(delay);
			}catch(Exception ex){
				
			}
			
			try{
				inf = remote.getTasksInfo(new SimpleRunTaskId(taskId));
				
				if (inf.getState() == ActivityState.ENDED || inf.getState() == ActivityState.FAILED){
					active = false;
				}
			}catch(Exception ex){
				if(failedAttempts > 5) {
					break;
				}
				failedAttempts++;
			}
			
			
			Date time = new Date();
			if (time.getTime() - startDate.getTime() >= timeOut){
				active = false;
			}
			
		}while(active);
		
	}
}
