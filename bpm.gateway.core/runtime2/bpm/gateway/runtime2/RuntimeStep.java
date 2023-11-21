package bpm.gateway.runtime2;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import bpm.gateway.core.Transformation;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.TransformationLog;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaContext;
/**
 * Class for all running step
 * 
 * -override init() to perform initialisation like resource allocation
 * -override releaseResource() to release all allocated resources
 * -override performRow to perform operation on the row :
 *    - get the available row by calling readRow();
 *    - once the row new row is ready push it the outputs by calling writeRow()
 * 
 * - the method init(Object) call the init() method by default
 * to have a custom behavior, you must override it
 * 
 * @author LCA
 *
 */
public abstract class RuntimeStep extends Thread {
	public static final String EVENT_LOG = "log added";
	
	
	protected class Waiter extends Thread{
		private RuntimeStep r ;
		public Waiter(RuntimeStep r){this.r = r;}
		public void run(){
			try {
				while (!r.isEnd() && inputEmpty()){
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static final int STARTED = 0;
	public static final int FINISHED = 1;
	public static final int INITED = 2;
	public static final int WAITING = 3;
	public static final int UNDEFINED = 4;
	private static final String[] STATES_NAMES = new String[]{
		"STARTED",
		"FINISHED",
		"INITED",
		"WAITING",
		"UNDEFINED"
	};
	//held datas 
	private BlockingQueue<Row> inputDatas ;
	protected Transformation transformation;
	private int bufferSize;
//	protected Semaphore semaphore = new Semaphore(1);
	
	//structure
	protected List<RuntimeStep> inputs = new ArrayList<RuntimeStep>();
	private List<RuntimeStep> outputs = new ArrayList<RuntimeStep>();
	
	//state Datas
	protected boolean isInited = false;
	private int state = UNDEFINED;
	protected long readedRows = 0;
	protected long writedRows = 0;
	protected long bufferedbRows = 0;
	private boolean end = false;
	private Date startTime = null;
	private Date stopTime = null;
	
	//logs
	private Logger logger;
	protected List<bpm.gateway.runtime2.internal.TransformationLog> logs = new ArrayList<bpm.gateway.runtime2.internal.TransformationLog>();
	
	protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	
	private IRepositoryContext repositoryCtx;
	
	public void setLogger(Logger logger){
		this.logger = logger;
	}
	
	protected IRepositoryContext getRepositoryContext(){
		return repositoryCtx;
	}
	
	public RuntimeStep() {
		
	}
	
	public RuntimeStep(IRepositoryContext repositoryCtx, Transformation transformation, int bufferSize){
		this.repositoryCtx = repositoryCtx;
		this.bufferSize = bufferSize;
		//TODO: remove the if
		if (transformation == null){
			setName(this.getClass().getName());
		}
		else{
			setName(transformation.getName());
		}
		
		this.transformation = transformation;
		this.inputDatas = new ArrayBlockingQueue<Row>(bufferSize);
	}
	
	
	protected int getBufferSize(){
		return bufferSize;
	}
	
	public void addLogListener(PropertyChangeListener listener){
		listeners.addPropertyChangeListener(listener);
	}
	
	public void removeLogListeners(PropertyChangeListener listener){
		listeners.removePropertyChangeListener(listener);
	}
	

	
	protected boolean inputFull() {
		return this.inputDatas.size() != bufferSize;
	}
	/**
	 * The execution of the runtime
	 * @throws Exception
	 */
	abstract public void performRow() throws Exception;
	
	/**
	 * add the given step to this inputs
	 * @param step
	 */
	public void addInput(RuntimeStep step){
		if (inputs.contains(step)){
			return;
		}
		inputs.add(step);
		
	}
	/**
	 * add the given step to this outputs
	 * @param step
	 */
	public void addOutput(RuntimeStep step){
		if (outputs.contains(step)){
			return;
		}
		outputs.add(step);
	}
	
	/**
	 * remove the Head of the InputsData
	 * @return the Data from the head in the inputs
	 * @throws InterruptedException
	 */
	protected Row readRow() throws InterruptedException, StepEndedException{ 
//		semaphore.acquire();
		if (isEnd() && !inputEmpty()){
			throw new StepEndedException("No More data");
		}
		Row row =  inputDatas.poll(100, TimeUnit.MILLISECONDS);
		readedRows ++;
//		semaphore.release();
//		logger.info(getName() + " inputSZ=" + inputDatas.size());
		
		return row;
	}
	
	/**
	 * write the given data in the all the outputs
	 * @param data
	 * @throws InterruptedException
	 */
	protected void writeRow(Row row) throws InterruptedException{
		
		for(RuntimeStep r : outputs){
			r.insertRow(row, this);
			
		}

		writedRows++;
	}
	
	/**
	 * write the given data in the all the outputs
	 * @param data
	 * @throws InterruptedException
	 */
	protected void writeRow(Row row, Transformation transfo) throws InterruptedException{
		for(RuntimeStep r : outputs){
			if(r.getTransformation().getName().equals(transfo.getName())) {
				r.insertRow(row, this);
			}
		}

		writedRows++;
	}
	
	/**
	 * insert the given Row in the inputs Datas
	 */
	 public void insertRow(Row data, RuntimeStep caller) throws InterruptedException {
//		semaphore.acquire();
		data.setTransformation(caller.getTransformation());
		inputDatas.put(data);
//		semaphore.release();
//		logger.info(getName() + " inputSZ=" + inputDatas.size());
	}
	
	
	/**
	 * set the flag to end Thread execution at true
	 */
	synchronized protected void setEnd(){
		end = true;
		logger.info(getName()  + " END");
	}
	
	/**
	 * 
	 * @return true if the current Thread stop processing rows
	 */
	public boolean isEnd(){
		return end;
	}
	/**
	 * release the Resources at the end of the execution
	 * should be overrided if necessary
	 * 
	 */
	public abstract void releaseResources();
	
//	/**
//	 * method to override to perform some Resources assigment or 
//	 * internal initialization
//	 * @throws Exception 
//	 * @deprecated use init(Object adapter) instead
//	 */
//	public abstract void init() throws Exception;
	
	/**
	 * Allow to init the Step by passing an adapter that can be used to change
	 * some Object properties
	 * 
	 * for now, used to be able to get the alternateConnections to use
	 * @param adapter
	 * @throws Exception
	 */
	public abstract void init(Object adapter) throws Exception;
	
	/**
	 * shouldn't be overriden
	 * int the start Time, 
	 * launch init()
	 * process thes rows
	 * release resources
	 * 
	 */
	public void run(){
	
		startTime = Calendar.getInstance().getTime();
		logger.info( getName() + " start");
		setState(STARTED);
		while(!isEnd()){
			
			
			try {
				performRow();
				
			} catch (Throwable e) {
				raiseException(e);
			}
		
			
		}
		
		stopTime = Calendar.getInstance().getTime();
		setState(FINISHED);
		info(" FINISHED");
		
	}
	
	public void raiseException(Throwable e) {
		error(" problem when inserting row", e);
		
		for (RuntimeStep rs : outputs) {
			if (rs.isAlive()) {
				try {
					rs.interrupt();
				} catch (Throwable ex) {
					rs.error("interrupted because an error occured on " + getTransformation().getName());
				}
				rs.setEnd();
			}
		}
		for (RuntimeStep rs : inputs) {
			if (rs.isAlive()) {
				try {
					rs.interrupt();
				} catch (Throwable ex) {
					rs.error("interrupted because an error occured on " + getTransformation().getName());
				}
				rs.setEnd();
			}
		}
		setEnd();
	}
	
	public long getStatsBufferedRows(){
		return bufferedbRows;
	}
	
	/**
	 * 
	 * @return the number of readed rows
	 */
	public long getStatsReadedRows(){
		return readedRows;
	}
	
	/**
	 * 
	 * @return the number of writed rows 
	 */
	public long getStatsProcessedRows(){
		return writedRows;
	}

	
	protected BlockingQueue<Row> getInputDatas(){
		return inputDatas;
	}
	
	public boolean isFinished(){
		return state == FINISHED;
	}
	protected void setState(int value){
		state = value;
		if (value == STARTED){
			startTime = Calendar.getInstance().getTime();
		}
		
	}
	
	public int getTransformationState(){
		return state;
	}
	
	public String getTransformationStateName(){
		return STATES_NAMES[state];
	}
	
	public Date getStartTime(){
		return startTime;
	}
	
	public Date getStopTime(){
		return stopTime;
	}
	
	public Long getDuration(){
		if (startTime != null && stopTime != null){
			return stopTime.getTime() - startTime.getTime();
		}
		
		return null;
	}
	
	/**
	 * 
	 * @return the transformation associated to this StepRun
	 */
	public Transformation getTransformation(){
		return transformation;
	}
	public boolean inputEmpty() throws InterruptedException {
//		semaphore.acquire();
		boolean b =inputDatas.isEmpty();
//		semaphore.release();
		return b;
	}

	
	protected boolean areInputsAlive(){
		for(RuntimeStep i : inputs){
			if (i.isAlive()){
				return true;
			}
		}
		return false;
	}
	
	protected boolean areInputStepAllProcessed()throws Exception{
		for(RuntimeStep i : inputs){
			if (!i.isEnd() || !i.inputEmpty() || !i.areInputStepAllProcessed()){
				return false;
			}
		}
		return true;
	}
	
	protected List<RuntimeStep> getOutputs(){
		//XXX : maybe not a good idea to instanciate new List, but is safer
		return new ArrayList<RuntimeStep>(outputs);
	}
	
	
	/**
	 * using at the start of the performRow
	 * setEnd on the current Step if needed
	 * if the Step is not ended and its input is empty, sleep for 10 ms
	 * @return true if the perform can continue, false otherwises
	 * @throws Exception
	 */
	protected boolean delay() throws Exception{
		if (areInputStepAllProcessed()){
			if (inputEmpty()){
				setEnd();
			}
		}
		
		if (isEnd() && inputEmpty()){
			return false;
		}
		
		if (!isEnd() && inputEmpty()){
			Thread.sleep(10);
			return false;
		}
		return true;
	}
	public void incrementBuffer() {
		bufferedbRows++;
		
	}
	
	
	
	private void addLog(String message, int priority){
		
		TransformationLog log = new TransformationLog(this, priority);
		log.message = getTransformation().getName() + " : "  + message ;
		synchronized (logs) {
			
			
			
			
			switch(priority){
			case Level.INFO_INT:
				log.priority = Level.INFO_INT;
				if (RuntimeEngine.logLevel == Level.DEBUG_INT){
					return;
				}
				
				logger.info(log.message);
				logs.add(log);
				break;
			case Level.DEBUG_INT:
				log.priority = Level.DEBUG_INT;
				if (RuntimeEngine.logLevel != Level.DEBUG_INT){
					return;
				}
				
				logger.debug(log.message);
				logs.add(log);
				break;
			case Level.ERROR_INT:
				log.priority = Level.ERROR_INT;
				logger.error(log.message);
				logs.add(log);
				break;
			case Level.WARN_INT:
				log.priority = Level.WARN_INT;
				if (RuntimeEngine.logLevel == Level.ERROR_INT ){
					return;
				}
				
				logger.warn(log.message);
				logs.add(log);
				break;
				
			}
		}
		listeners.firePropertyChange(EVENT_LOG, null, log);
	}
	
	public List<TransformationLog> getLogs(){
		return logs;
	}

	public boolean containsErrors() {
		synchronized(logs){
			for(TransformationLog l : logs){
				if (l.priority == Level.ERROR_INT){
					return true;
				}
			}
		}
		
		return false;
	}

	public boolean containsWarnings() {
		synchronized(logs){
			for(TransformationLog l : logs){
				if (l.priority == Level.WARN_INT){
					return true;
				}
			}
		}
		
		
		return false;
	}
	
	protected void info(String message){
		addLog(message, Level.INFO_INT);
	}
	protected void error(String message){
		addLog(message, Level.ERROR_INT);
	}
//	protected void error(String message, Exception ex){
//		addLog(message, Level.ERROR_INT);
//	}
	protected void debug(String message){
		addLog(message, Level.DEBUG_INT);
	}
	protected void warn(String message){
		addLog(message, Level.WARN_INT);
	}
	protected void warn(String message, Throwable e){
		TransformationLog log = new TransformationLog(this, Level.WARN_INT);
		log.message = getTransformation().getName() + " : "  + message + ";" + e.getMessage() ;
		logs.add(log);
		logger.warn(getName() + " " + message, e);
	}
	protected void error(String message, Throwable e){
		TransformationLog log = new TransformationLog(this, Level.ERROR_INT);
		log.message = getTransformation().getName() + " : "  + message + ";" + e.getMessage() ;
		logs.add(log);
		logger.error(getName() + " " + message, e);
	}
	protected Logger getLogger(){
		return logger;
	}
	public void removeInput(RuntimeStep step) {
		inputs.remove(step);
		
	}
}
