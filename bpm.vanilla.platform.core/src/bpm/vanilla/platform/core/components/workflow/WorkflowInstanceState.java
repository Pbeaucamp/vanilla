package bpm.vanilla.platform.core.components.workflow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import bpm.vanilla.platform.core.beans.IRuntimeState;
import bpm.vanilla.platform.core.components.IStep;

public class WorkflowInstanceState implements IRuntimeState {

	private static final long serialVersionUID = 1L;

	public static enum StepNature {
		Automatic, Manuel
	}

	private String workflowName;
	
	private Date startDate;
	private Date stopDate;

	private String failureCause;
	private ActivityState state;
	private ActivityResult result;
	private String processInstanceUUID;
	
	private List<StepInfos> stepInfos = new CopyOnWriteArrayList<StepInfos>();
	
	public WorkflowInstanceState() { }

	public WorkflowInstanceState(String processInstanceUUID, String workflowName) {
		this.processInstanceUUID = processInstanceUUID;
		this.workflowName = workflowName;
		this.startDate = new Date();
		this.result = ActivityResult.UNDEFINED;
	}

	public String getProcessInstanceUUID() {
		return processInstanceUUID;
	}

	public void addStepInfos(StepInfos info) {
		stepInfos.add(info);
	}

	public List<StepInfos> getStepInfos() {
		List<StepInfos> steps = new ArrayList<StepInfos>(stepInfos);
		Collections.sort(steps, new Comparator<StepInfos>() {

			@Override
			public int compare(StepInfos arg0, StepInfos arg1) {
				if (arg0.getStartDate() == null) {
					if (arg1.getStartDate() != null) {
						return 1;
					}
					return arg0.getStepName().compareTo(arg1.getStepName());
				}
				else {
					if (arg1.getStartDate() != null) {
						int i = arg0.getStartDate().compareTo(arg1.getStartDate());
						if (i == 0) {
							return arg0.getStepName().compareTo(arg1.getStepName());
						}
						else {
							return i;
						}
					}
					else {
						return -1;
					}
				}
			}

		});
		stepInfos.clear();
		stepInfos.addAll(steps);
		return stepInfos;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setStopDate(Date stopDate) {
		this.stopDate = stopDate;
	}

	public void setState(ActivityState state) {
		this.state = state;
	}

	@Override
	public ActivityState getState() {
		return state;
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
		return workflowName;
	}

	@Override
	public Date getStartedDate() {
		return startDate;
	}

	@Override
	public Date getStoppedDate() {
		return stopDate;
	}

	public Long getElapsedTime() {
		if (getStartedDate() == null) {
			return null;
		}
		if (isStopped()) {
			return getDurationTime();
		}

		return new Date().getTime() - getStartedDate().getTime();
	}

	public boolean isStopped() {
		return getStoppedDate() != null;
	}
	
	public void setResult(ActivityResult result) {
		this.result = result;
	}

	public ActivityResult getResult() {
		return result;
	}

	public void setFailureCause(String message) {
		failureCause = message;
	}

	public static class StepInfos implements IStep, Serializable {

		private static final long serialVersionUID = 1L;
		
		private Date startDate;
		private Date stopedDate;
		private String stepName;
		private StepNature nature;
		private ActivityState state;
		/**
		 * url of an HTML that should be submited by the task to allow
		 * 
		 */
		private String manualStepUrl;
		private String failureCause = "";
		
		public StepInfos() { }

		public StepInfos(Date startDate, Date stopedDate, String stepName, StepNature nature, ActivityState state, String failureCause) {
			super();
			this.startDate = startDate;
			this.stopedDate = stopedDate;
			this.stepName = stepName;
			this.nature = nature;
			this.state = state;
			this.failureCause = failureCause;
		}

		public String getFailureCause() {
			return failureCause;
		}
		
		/**
		 * @return the state
		 */
		public ActivityState getState() {
			return state;
		}

		/**
		 * @return the startDate
		 */
		public Date getStartDate() {
			return startDate;
		}

		/**
		 * @return the stopedDate
		 */
		public Date getStopedDate() {
			return stopedDate;
		}

		/**
		 * @return the stepName
		 */
		@Override
		public String getStepName() {
			return stepName;
		}

		/**
		 * @return the nature
		 */
		public StepNature getNature() {
			return nature;
		}

		public void setNature(StepNature nature) {
			if (nature != null) {
				this.nature = nature;
			}
		}

		public void setManualStepUrl(String stepUrl) {
			this.manualStepUrl = stepUrl;
		}

		/**
		 * @return the manualStepUrl
		 */
		public String getManualStepUrl() {
			return manualStepUrl;
		}

		public Long getDuration() {
			if(getStopedDate() == null) {
				return null;
			}
			
			if(getStartDate() == null) {
				return null;
			}
			
			return getStopedDate().getTime() - getStartDate().getTime();
		}
	}
}
