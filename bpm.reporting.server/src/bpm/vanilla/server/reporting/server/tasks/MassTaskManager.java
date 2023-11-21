package bpm.vanilla.server.reporting.server.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.utils.IOWriter;
import bpm.vanilla.server.commons.server.Server;
import bpm.vanilla.server.commons.server.commands.CreateTaskCommand;
import bpm.vanilla.server.commons.server.tasks.ITask;
import bpm.vanilla.server.commons.server.tasks.TasksManager;

import com.thoughtworks.xstream.XStream;

public class MassTaskManager extends TasksManager{

	private volatile File queueFolder;
	private Server server;
	private ReentrantLock locker = new ReentrantLock(true);
	
	public MassTaskManager(ClassLoader serverClassLoader, int queueSize,
			Server server, Logger logger) throws Exception {
		super(serverClassLoader, queueSize, server, logger);
		
		queueFolder = new File(server.getConfig().getHistorizationFolder() + "/../queue");
		if (!queueFolder.exists()){
			queueFolder.mkdirs();
		}
		
		this.server = server;
	}
	
	@Override
	public void addTaskToQueue(CreateTaskCommand cmd) throws Exception {

		locker.lock();
		try{
			FileOutputStream fos = null; 
			
			if (!queueFolder.exists()){
				queueFolder.mkdirs();
			}
			File f = new File(queueFolder, cmd.getTaskId() + ".tskcmd");
			try{
				fos = new FileOutputStream(f) ;
				
				String xml = new XStream().toXML(cmd);
				IOWriter.write(IOUtils.toInputStream(xml, "UTF-8"), fos, true, true);
				Logger.getLogger(getClass()).info("added task with id " + cmd.getTaskId() + " in " + f.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}finally{
			locker.unlock();
		}

		runNextTask();
	}
	
	@Override
	public boolean isQueueEmpty() {
		locker.lock();
		try{
			return queueFolder.list().length == 0;
		}finally{
			locker.unlock();
		}
	}
	
	@Override
	public List<CreateTaskCommand> getWaitingTasks() {
		
		
		List<CreateTaskCommand> l = new ArrayList<CreateTaskCommand>();
		locker.lock();
		try{
			if(queueFolder.list() != null) {
				for(String s : queueFolder.list()){
					
					CreateTaskCommand cmd = readCommandFromFile(new File(queueFolder, s), false);
					l.add(cmd);
				}
				return l;
			}
			return l;
		} catch (Exception e) {
			return new ArrayList<CreateTaskCommand>();
		}finally{
			locker.unlock();
		}
	}
	
	@Override
	public void removeWaitingTask(CreateTaskCommand task) throws Exception {
		
		locker.lock();
		try{
			for(String s : queueFolder.list()){
				if (s.equals(task.getTaskId() + "")){
					new File(queueFolder, s).delete();
					break;
				}
			}
		}finally{
			locker.unlock();
		}
		
	}
	
	
	private CreateTaskCommand readCommandFromFile(File f, boolean deleteFile){
		
		FileInputStream fis = null;
		try{
			fis = new FileInputStream(f);
			
			CreateTaskCommand cmd = (CreateTaskCommand)new XStream().fromXML(fis);
			cmd.setServer(server);
			return cmd;
		}catch(Exception ex){
			Logger.getLogger(getClass()).error("Command deserialization error - "+ ex.getMessage(), ex);
		}finally{
			try{
				if (fis != null){
					fis.close();
				}
			}catch(Exception ex){
				Logger.getLogger(getClass()).error(ex.getMessage(), ex);	
			}
			if (deleteFile){
				f.delete();
			}
		}
		return null;
	}
	protected void removeHead(){
		locker.lock();
		try{
			String[] filesN = queueFolder.list();
			if (filesN.length == 0){
				return ;
			}
			Arrays.sort(filesN, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					int i1 = Integer.parseInt(o1.substring(0, o1.indexOf(".")));
					int i2 = Integer.parseInt(o2.substring(0, o2.indexOf(".")));
					return i1-i2;
				}
			});
			new File(queueFolder, filesN[0]).delete();
			Logger.getLogger(getClass()).info("removed head queue ");
		}finally{
			locker.unlock();
			
		}
	}
	@Override
	protected CreateTaskCommand getQueueHeadTask() {
		locker.lock();
		try{
			String[] filesN = queueFolder.list();
			if (filesN.length == 0){
				return null;
			}
			Arrays.sort(filesN, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					int i1 = Integer.parseInt(o1.substring(0, o1.indexOf(".")));
					int i2 = Integer.parseInt(o2.substring(0, o2.indexOf(".")));
					return i1-i2;
				}
			});
			
			return readCommandFromFile(new File(queueFolder, filesN[0]), true);

		} catch (Exception e) {
			return null;
		}finally{
			locker.unlock();
			Logger.getLogger(getClass()).info("removed wiating ");
		}
		
	}
	
	public ITask getTask(long taskId) throws Exception{
		return getTask(taskId, false);
	}
	
	public ITask getTask(long taskId, boolean isRetrying) throws Exception{
		synchronized (transitory) {
			for(CreateTaskCommand t : transitory){
				if (t.getTaskId() == taskId){
					return t.getDummy();
				}
			}
		}
		

		for(ITask t : getRunningTasks()){
			if (t.getId() == taskId){
				return t;
			}
		}
		
		locker.lock();
		try{
			for(String s : queueFolder.list()){
				
				if (s.equals(taskId + ".tskcmd")){
					return readCommandFromFile(new File(queueFolder, s), false).getDummy();
				}
			}
		}finally{
			locker.unlock();
		}
		
		if(serializer.readHistorizedTasks() != null) {
			for(ITask t : serializer.readHistorizedTasks()){
				if (t.getId() == taskId){
					return t;
				}
			}
		}
		
		//retry
		if(!isRetrying) {
			return retryGettingTask(taskId);
		}
		
		throw new Exception("No Task with id=" + taskId + " found, it may have been purged from the historic");

	}

	/**
	 * This is here because there's a short time when the task is in no state which will throw an exception 
	 * to the client if it's trying to get the task at this right time.
	 * @param taskId
	 * @return the task
	 * @throws Exception
	 */
	private ITask retryGettingTask(long taskId) throws Exception {
		
		int essai = 1;
		while (essai <= 5) {
			Thread.sleep(1000);
			
			try {
				ITask t = getTask(taskId, true);
				return t;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			essai++;
		}
		
		throw new Exception("No Task with id=" + taskId + " found, it may have been purged from the historic");
	}
}
