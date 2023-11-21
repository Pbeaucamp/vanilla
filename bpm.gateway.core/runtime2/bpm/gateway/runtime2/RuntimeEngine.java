package bpm.gateway.runtime2;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.Transformation;
import bpm.gateway.runtime2.tools.RuntimeAppender;
import bpm.vanilla.platform.core.IRepositoryContext;


public class RuntimeEngine {
	
	public static int MAX_ROWS = 10000;
	
	public Logger logger = Logger.getLogger(RuntimeEngine.class);
		
	public static int logLevel = Level.INFO_INT;
	
	private int state = 0;
	
	private static final int READY = 1;
	private static final int BUSY = 2;
	private static final int AVAILABLE = 0;
	
	private RuntimeAppender appender;
	private DailyRollingFileAppender fileappender;
	private PropertyChangeListener listener;
	
	private Integer directoryItemId;
	
	private Thread mainThread;

	private List<RuntimeStep> allRuns = new ArrayList<RuntimeStep>();
	
	public void init(IRepositoryContext repContext, DocumentGateway doc, PropertyChangeListener listener, OutputStream consoleOutputStream) throws GatewayRuntimeException{

		directoryItemId = doc.getId();
		
		if (state == BUSY){
			throw new GatewayRuntimeException("The runtime engine is already running. Stop the current execution before init a new operation.");
		}
		this.listener = listener;
		
		List<Appender> apps = new ArrayList<Appender>();
		Enumeration e = logger.getAllAppenders();
		
		boolean hasRuntimeeAppender = false;
		boolean hasWriterAppender = false;
		
		while(e.hasMoreElements()){
			Object o = e.nextElement();
			if (o instanceof RuntimeAppender){
				hasRuntimeeAppender = true;
				appender = (RuntimeAppender)o;
			}
			else{
				hasWriterAppender = true;
			}
		}
		
				
		
		if(fileappender != null && !fileappender.getFile().contains(doc.getProjectName())){
			hasRuntimeeAppender = false;
			logger.removeAppender(fileappender);
		}
		
		if (hasRuntimeeAppender == false){
			
			try {
				if(doc.getProjectName() != null && !doc.getProjectName().isEmpty()){
					fileappender = new DailyRollingFileAppender(new SimpleLayout(), "Logs/Transformations_"+doc.getProjectName()+"/"+doc.getProjectName()+".log", ".dd-MM-yyyy");		
				}else{
					fileappender = new DailyRollingFileAppender(new SimpleLayout(), "Logs/Transformations_"+doc.getName()+"/"+doc.getName()+".log", ".dd-MM-yyyy");

				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			if(!logger.isAttached(appender)){
				appender = new RuntimeAppender(new SimpleLayout(), System.out);		
				appender.addPropertyChangeListener(listener);
				
				logger.addAppender(appender);	
			}
			
			logger.addAppender(fileappender);
		}
		
		
		//add console
		
		if (consoleOutputStream != null && hasWriterAppender == false){
			WriterAppender wa = new WriterAppender(new SimpleLayout(), consoleOutputStream);
			logger.addAppender(wa);
		}
			
		allRuns.clear();
		for(Transformation t : doc.getTransformations()){
			if(((AbstractTransformation)t).getContainer() == null || ((AbstractTransformation)t).getContainer().isEmpty()) {
				RuntimeStep r = t.getExecutioner(repContext, MAX_ROWS);
				if (r == null){
					throw new GatewayRuntimeException("The step " + t.getName() + " cannot be used at rntime because it is still under implementation.");
				}
				r.setLogger(logger);
				allRuns.add(r);
			}
		}
		
		for(RuntimeStep r : allRuns){
			
			Transformation t = r.getTransformation();
			
			for(Transformation out : t.getOutputs()){
				for(RuntimeStep _r : allRuns){
					
					if (_r.getTransformation() == out){
						r.addOutput(_r);
						_r.addInput(r);
						break;
					}
				}
				
			}
			
		}
		
		
		state = READY;
		appender.fireEgineStateEvent("Ready to Run");
	}
	
	public  void interrupt() throws GatewayRuntimeException{
	
		logger.info("Interrupting current runtime");
		for(RuntimeStep t : allRuns){
			if (! t.isAlive()){
				continue;
			}
			logger.info("Interrupting " + t.getName());
			try{
				t.interrupt();
				
			}catch(Throwable e){
				try{
					t.interrupt();
				}catch(Throwable ex){
					
				}
			}finally{
				t.releaseResources();
			}
			logger.warn(t.getName() + " interrupted");
		}
		
		try{
			mainThread.interrupt();
		}catch(Throwable e){
			try{
				mainThread.interrupt();
			}catch(Throwable ex){
				
			}
		}finally{
			state = AVAILABLE;
			logger.info("current Runtime interrupted");
		}
		
	}
	
	
	private  boolean isStillRunning(){
		for(RuntimeStep t : allRuns){
			
			if (t.isAlive() && !t.isInterrupted()){
				return true;
			}
		}
		return false;
	}
	
	public  void run(int level) throws GatewayRuntimeException{
		
		if (state == BUSY){
			throw new GatewayRuntimeException("The runtime engine is already running. Stop the current execution before run operation.");
			
		}
		
		if (state == AVAILABLE){
			throw new GatewayRuntimeException("No operation loaded.");
			
		}
		logger.info("\n" +
					 "***************************************\n"
					+"* START RUN						*\n"
					+"***************************************");
		state = BUSY;
		appender.fireEgineStateEvent("Running");

		logLevel = level;
		logger.setLevel(Level.toLevel(level));
		
		
		mainThread = new RuntimeThread();
		mainThread.setName("Gateway Runtime Engine");
		mainThread.start();
	}
	
	

	
	public  List<RuntimeStep> getAllRuns(){
		return allRuns;
	}
	
	
	
	public  String getState(){
		switch(state){
		case BUSY : 
			return "running";
		case READY : 
			return "ready to run";
		case AVAILABLE : 
			return "available";
		}
		
		return "";
	}

	public boolean isRunning() {
		return state == BUSY;
	}

	public  boolean isReady() {
		return state == READY;
	}

	public  boolean isAvailable() {
		return state == AVAILABLE;
	}
	
	
	private class RuntimeThread extends Thread{
		public void run(){
			logger.info("Start Running Gateway Transformation at : "+new Date().toString());
			for(RuntimeStep tr : allRuns){
				try {
					tr.init(null);
				} catch (Exception e1) {
					e1.printStackTrace();
					logger.error("error during initing " + tr.getName(), e1);
					for(RuntimeStep r : allRuns){
						r.releaseResources();
						try{
							
							r.interrupt();
						}catch (Exception e) {
							try{
								r.interrupt();
							}catch (Exception ex) {
								
							}
						}
						r.error("Interrupted because an error occured when initing " + tr.getTransformation().getName());
					}
					return;
				}
			}
			
			for(RuntimeStep tr : allRuns){
				tr.start();
			}
			while(isStillRunning()){
				try {
					sleep(2000);
					
					appender.fireStatEvent("");
				} catch (InterruptedException e) {
					
//					e.printStackTrace();
					interrupt();
				}
			}
			
			for(RuntimeStep tr : allRuns){
				tr.releaseResources();
				
			}
			
			for(RuntimeStep tr : allRuns){
				logger.info(tr.getName() + " readed:" + tr.getStatsReadedRows() + " processed:" + tr.getStatsProcessedRows());
			}
			logger.info("Gateway Transformation Execution Ended at : "+new Date().toString());
			
			
			List<Appender> apps = new ArrayList<Appender>();
			Enumeration e = logger.getAllAppenders();
			
			state = READY;
			appender.fireEgineStateEvent("Ready to Run");
		}
	}
	
}
