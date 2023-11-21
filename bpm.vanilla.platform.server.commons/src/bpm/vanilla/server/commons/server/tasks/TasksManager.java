package bpm.vanilla.server.commons.server.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.tasks.ITaskState;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.server.commons.server.Server;
import bpm.vanilla.server.commons.server.commands.CreateTaskCommand;
import bpm.vanilla.server.commons.server.historizer.HistoricSerializer;


public class TasksManager {
	private Logger logger;
	
	private IVanillaAPI vanillaApi;
	private int maximumRunningTasks = -1;
	
	private LinkedList<CreateTaskCommand> queue = new LinkedList<CreateTaskCommand>();
	private List<ITask> runningTasks ;
	
	protected HistoricSerializer serializer;
	
	private boolean stopped = false;

	/**
	 * 
	 * @param queueSize : intial Size of the queue
	 * @param maximumeRunningTasks : maximum task running at the same time
	 * @param refreshingIntervalTime : time between check in milliseconds
	 * @throws Exception
	 */
	public TasksManager(ClassLoader serverClassLoader, int queueSize, Server server, Logger logger) throws Exception{
		this.logger = logger;
		int maximumeRunningTasks = server.getConfig().getMaxTasks();
		serializer = new HistoricSerializer(new File(server.getConfig().getHistorizationFolder()));
		if (queueSize <= 0){
			throw new Exception("TaskManager queueSize must be higher than 0");
		}
		
		if (maximumeRunningTasks <= 0){
			throw new Exception("TaskManager maximumeRunningTasks must be higher than 0");
		}
		
		queue = new LinkedList<CreateTaskCommand>();
		
		this.maximumRunningTasks = maximumeRunningTasks;
		runningTasks = new ArrayList<ITask>(maximumeRunningTasks);
		
		logger.info("TaskManager created");
	}
	
	/**
	 * add the given task in the queue
	 * @param task
	 * @throws Exception
	 */
	public void addTaskToQueue(CreateTaskCommand cmd) throws Exception{
		synchronized(queue){
			queue.add(cmd);
		}
		logger.info("TaskManager added task with id " + cmd.getTaskId());
		runNextTask();
	}
	
	/**
	 * @return the head of the queue 
	 */
	protected CreateTaskCommand getQueueHeadTask(){
		return queue.poll();
		
	}
	
	/**
	 * remove the Head task from the queue add it to the running tasks and start it
	 * only if the queue is not empty
	 * and start it
	 * @param task
	 */
	public void runNextTask(){
		
		pool.execute(new Nexter());
	}
	
	private final ExecutorService pool =  Executors.newFixedThreadPool(20);
	
	protected List<CreateTaskCommand> transitory = Collections.synchronizedList(new ArrayList<CreateTaskCommand>());
	
	private class Nexter implements  Runnable{
		public void run(){
			if (stopped){
				return ;
			}
			ITask task = null;
			
			CreateTaskCommand cmd = null;
			if (isRunningTasksFull()){
				return ;
			}
			cmd = getQueueHeadTask();
			
			if (cmd == null){
				return ;
			}
			transitory.add(cmd);
			try{
				task = cmd.createTask();
				
			}catch(Exception ex){
				logger.error("Error while creating task " + cmd.getTaskId() + ":" + ex.getMessage(), ex);
				transitory.remove(cmd);
				return;
			}
			//removeHead();
			synchronized (runningTasks) {
				runningTasks.add(task);
			
			}
			transitory.remove(cmd);
			logger.info("start task id=" + task.getId() + " type=" + task.getClass().getName());
			
			task.startTask();
		}
	}
	
	protected void removeHead(){
		synchronized(queue){
			queue.poll();
		}
	}
	
	/**
	 * 
	 * @return true is the runningTasks is not empty
	 */
	protected boolean hasRunningTasks(){
		return runningTasks.isEmpty();
	}
	
	/**
	 * 
	 * @return true if the queue is empty
	 */
	public boolean isQueueEmpty(){
		return queue.isEmpty();
	}
	
	/**
	 * 
	 * @param taskId
	 * @return the task for the given task Id
	 * @throws : is the task do not belong in the queue or the runningtasks, or the historic
	 */
	public ITaskState getState(long taskId) throws Exception{
		return getTask(taskId).getTaskState();
	}

	public boolean isRunningTasksFull() {
		
		return runningTasks.size()>= maximumRunningTasks;
	}
	
	/**
	 * remove the ITask returning true on isStopped
	 * and put them in the historicTaskList
	 * 
	 * (runnningtasks and historicTask are synchronizd)
	 */
	public void removeFinishedTasksFromRunningList(ITask task){
		boolean removed = false;
		synchronized(runningTasks){
			
			try {
				serializer.addTask(task);
			} catch (Exception e) {
				Logger.getLogger(getClass()).error(e.getMessage(), e);
			}
			removed = runningTasks.remove(task);
		}
		
		if (removed){
			Logger.getLogger(getClass()).info("Removed from running Task id="+task.getId() );
			pool.execute(new EventFirer(task));
		}
		
		
		//launch the next task if possible
		runNextTask();
	}
	
	private class EventFirer implements Runnable {
		private ITask task;
		public EventFirer (ITask task){
			this.task = task;
		}
		public void run() {
//			IVanillaEvent event = new ObjectExecutedEvent(
//					task.getComponentIdentifier(),
//					task.getSessionId(),
//					task.getObjectIdentifier(),
//					task.getGroupId(),
//					task.getTaskState());
//			
//			
//			try {
//				getVanillaApi(false).getListenerService().fireEvent(event);
//			} catch (Exception e) {
//				Logger.getLogger(getClass()).warn("Failed to fire event - " + e.getMessage());
//				Logger.getLogger(getClass()).warn("Retry to refresh the VanillaAPI");
//				try{
//					getVanillaApi(true).getListenerService().fireEvent(event);
//				}catch(Exception ex){
//					Logger.getLogger(getClass()).warn("Failed to fire event - " + ex.getMessage(), ex);
//					
//				}
//			}
			
		}
	};
	
	private IVanillaAPI getVanillaApi(boolean recreate){
		if (recreate || vanillaApi == null){
			VanillaConfiguration conf = ConfigurationManager.getInstance().getVanillaConfiguration();
			vanillaApi = new RemoteVanillaPlatform(
					conf.getVanillaServerUrl(), 
					conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), 
					conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
			
		}
		return vanillaApi;
	}
	
	/*
	public List<ITask> clearHistoric(){
		synchronized(taskHistoric){
			List<ITask> l = new ArrayList<ITask>(taskHistoric);
			
			taskHistoric.clear();
			return l;
		}
		
	}
	*/

	
	 
	
	/**
	 * 
	 * @return the current Tasks that are running
	 */
	public List<ITask> getRunningTasks(){
		return new ArrayList<ITask>(runningTasks);
	}
	
	/**
	 * 
	 * @return the current Tasks that are queuing
	 */
	public List<CreateTaskCommand> getWaitingTasks(){
		return new ArrayList<CreateTaskCommand>(queue);
	}
	
/*	public List<ITask> getHistorizedTasks(){
		return readHistoric();
//		return new ArrayList<ITask>(taskHistoric);
	}
*/
	public void removeWaitingTask(CreateTaskCommand task) throws Exception{
		synchronized(queue){
			queue.remove(task);
		}
		
		
	}

	/**
	 * 
	 * @return all tasks present in Historic, Running, Queueing
	 */
	public List<ITask> getAllTasks() {
		List<ITask> l = new ArrayList<ITask>();
		for(CreateTaskCommand c : getWaitingTasks()){
			l.add(c.getDummy());
		}
		l.addAll(runningTasks);
		try {
			l.addAll(serializer.readHistorizedTasks());
		} catch (Exception e) {
			Logger.getLogger(getClass()).error("Failed to read historized files - " + e.getMessage(), e);
		}
		return l;
	}

	public void reset(int maxTasks){
		this.maximumRunningTasks = maxTasks;
	}
	public void start() {
		this.stopped = false;
	}
	public void stop(boolean stopRunning) {
		this.stopped = true;
		
		if (stopRunning){
			
			synchronized (runningTasks) {
				for(ITask t : runningTasks){
					try {
						t.stopTask();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				
			}
		}
		else{
			for(ITask t : runningTasks){
				try {
					t.join();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		//taskHistoric.addAll(runningTasks);
		runningTasks.clear();
		
		
		
		this.stopped = false;
		runNextTask();
		
	}

	public ITask getTask(long taskId) throws Exception{
//		logger.debug("Looking for task with id = " + taskId);
		synchronized (transitory) {
			for(CreateTaskCommand t : transitory){
				if (t.getTaskId() == taskId){
//					logger.debug("finded task with id = " + taskId + " in the transitories.");
					return t.getDummy();
				}
			}
		}
		
		for(CreateTaskCommand t : getWaitingTasks()){
			if (t.getTaskId() == taskId){
//				logger.debug("finded task with id = " + taskId + " in the waitings.");
				return t.getDummy();
			}
		}

		for(ITask t : getRunningTasks()){
			if (t.getId() == taskId){
//				logger.debug("finded task with id = " + taskId + " in the runnings.");
				return t;
			}
		}
		
		for(ITask t : serializer.readHistorizedTasks()){
			if (t.getId() == taskId){
//				logger.debug("finded task with id = " + taskId + " in the historic.");
				return t;
			}
		}
		throw new Exception("No Task with id=" + taskId + " found, it may have been purged from the historic");
	}

	/*
	 * force historization
	 */
	public void historize() throws Exception{
		serializer.flush();
		
	}

//	public void removeFinishedTasksFromHistoric(ITask t) throws Exception {
//		synchronized(taskHistoric){
//			List<ITask> l = new ArrayList<ITask>();
//			l.add(t);
//			logger.info("Removing task from historic " + t.getId());
//			
//			HistorizerXml.historize(historicFolder, l);
//			taskHistoric.remove(t);
//		}
//	}

//	public void setManageHistoric(boolean manageHistoric) {
//		this.manageHistoric = manageHistoric;
//	}
	
	
}
