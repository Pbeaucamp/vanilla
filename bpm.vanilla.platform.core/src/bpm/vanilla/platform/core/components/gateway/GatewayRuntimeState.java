package bpm.vanilla.platform.core.components.gateway;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.vanilla.platform.core.beans.IRuntimeState;
import bpm.vanilla.platform.core.components.IStep;

/**
 * a utility class to gather information about a GatewayRun
 * 
 * @author ludo
 * 
 */
public class GatewayRuntimeState implements IRuntimeState {

	private static final long serialVersionUID = 1L;
	
	private List<StepInfos> stepsInfo = new ArrayList<StepInfos>();
	private ActivityState state;
	
	private String gatewayName;
	private String failureCause;
	
	private Date startedDate;
	private Date stoppedDate;

	public GatewayRuntimeState() { }
	
	public List<StepInfos> getStepsInfo() {
		return stepsInfo;
	}

	public void setStepsInfo(List<StepInfos> stepsInfo) {
		this.stepsInfo = stepsInfo;
	}

	@Override
	public ActivityState getState() {
		return state;
	}

	public void setState(ActivityState state) {
		this.state = state;
	}

	public void addStepInfo(StepInfos inf) {
		if (stepsInfo == null) {
			stepsInfo = new ArrayList<StepInfos>();
		}
		stepsInfo.add(inf);
	}

	public void setName(String gatewayName) {
		this.gatewayName = gatewayName;
	}

	public void setStoppedDate(Date stoppedDate) {
		this.stoppedDate = stoppedDate;
	}
	
	public void setFailureCause(String failureCause) {
		this.failureCause = failureCause;
	}
	
	public void setStartedDate(Date startedDate) {
		this.startedDate = startedDate;
	}

	@Override
	public Long getDurationTime() {
		if(getStoppedDate() == null) {
			return null;
		}
		
		if(getStartedDate() == null) {
			return null;
		}
		
		return getStoppedDate().getTime() - getStartedDate().getTime();
	}

	@Override
	public String getFailureCause() {
		return failureCause;
	}

	@Override
	public String getName() {
		return gatewayName;
	}

	@Override
	public Date getStartedDate() {
		return startedDate;
	}

	@Override
	public Date getStoppedDate() {
		return stoppedDate;
	}

	/**
	 * a Simple class to hold information on all the steps within a GatewayRun
	 * 
	 * @author ludo
	 * 
	 */
	public static class StepInfos implements IStep, Serializable {

		private static final long serialVersionUID = 1L;
		
		private String stepName;
		private long bufferedRows;
		private long processedRows;
		private long duration;
		private long readRows;
		private int errorNumber;
		private int warningNumber;
		private Date startTime;
		private Date stopTime;
		private String state;
		private String logs;

		public StepInfos() {
		}

		public StepInfos(String stepName, long bufferedRows, long processedRows, long readRows, int errorNumbers, int warningNumber, long duration, Date startTime, Date stopTime, String state, String logs) {
			super();
			this.stepName = stepName;
			this.bufferedRows = bufferedRows;
			this.processedRows = processedRows;
			this.duration = duration;
			this.startTime = startTime;
			this.stopTime = stopTime;
			this.state = state;
			this.errorNumber = errorNumbers;
			this.warningNumber = warningNumber;
			this.readRows = readRows;
			this.logs = logs;
		}

		/**
		 * @param bufferedRows
		 *            the bufferedRows to set
		 */
		public void setBufferedRows(long bufferedRows) {
			this.bufferedRows = bufferedRows;
		}

		/**
		 * @param processedRows
		 *            the processedRows to set
		 */
		public void setProcessedRows(long processedRows) {
			this.processedRows = processedRows;
		}

		/**
		 * @param duration
		 *            the duration to set
		 */
		public void setDuration(long duration) {
			this.duration = duration;
		}

		/**
		 * @param startTime
		 *            the startTime to set
		 */
		public void setStartTime(Date startTime) {
			this.startTime = startTime;
		}

		/**
		 * @param stopTime
		 *            the stopTime to set
		 */
		public void setStopTime(Date stopTime) {
			this.stopTime = stopTime;
		}

		/**
		 * @param state
		 *            the state to set
		 */
		public void setState(String state) {
			this.state = state;
		}
		
		@Override
		public String getStepName() {
			return stepName;
		}
		
		public void setStepName(String stepName) {
			this.stepName = stepName;
		}

		/**
		 * @return the readRows
		 */
		public long getReadRows() {
			return readRows;
		}

		/**
		 * @param readRows
		 *            the readRows to set
		 */
		public void setReadRows(long readRows) {
			this.readRows = readRows;
		}

		/**
		 * @return the errorNumber
		 */
		public int getErrorNumber() {
			return errorNumber;
		}

		/**
		 * @param errorNumber
		 *            the errorNumber to set
		 */
		public void setErrorNumber(int errorNumber) {
			this.errorNumber = errorNumber;
		}

		/**
		 * @return the warningNumber
		 */
		public int getWarningNumber() {
			return warningNumber;
		}

		/**
		 * @param warningNumber
		 *            the warningNumber to set
		 */
		public void setWarningNumber(int warningNumber) {
			this.warningNumber = warningNumber;
		}

		/**
		 * @return the logs
		 */
		public String getLogs() {
			return logs;
		}

		/**
		 * @param logs
		 *            the logs to set
		 */
		public void setLogs(String logs) {
			this.logs = logs;
		}

		/**
		 * @return the bufferedRows
		 */
		public long getBufferedRows() {
			return bufferedRows;
		}

		/**
		 * @return the processedRows
		 */
		public long getProcessedRows() {
			return processedRows;
		}

		/**
		 * @return the duration
		 */
		public long getDuration() {
			return duration;
		}

		/**
		 * @return the startTime
		 */
		public Date getStartTime() {
			return startTime;
		}

		/**
		 * @return the stopTime
		 */
		public Date getStopTime() {
			return stopTime;
		}

		/**
		 * @return the state
		 */
		public String getState() {
			return state;
		}

	}

}
