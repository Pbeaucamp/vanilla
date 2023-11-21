package bpm.workflow.commons.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import bpm.workflow.commons.beans.activity.StartActivity;
import bpm.workflow.commons.beans.activity.StopActivity;

@Entity
@Table(name = "workflow")
public class Workflow implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column(name = "id")
	private int id;

	@Column(name = "name")
	private String name;
	@Column(name = "description")
	private String description;

	@Column(name = "creationDate")
	private Date creationDate;
	@Column(name = "modificationDate")
	private Date modificationDate;

	@Column(name = "authorId")
	private int authorId;
	@Column(name = "authorName")
	private String authorName;
	@Column(name = "modificatorId")
	private int modificatorId;
	@Column(name = "modificatorName")
	private String modificatorName;

	@Transient
	private WorkflowModel workflowModel;
	@Column(name = "model", length = 10000000)
	private String model;

	@Transient
	private Schedule schedule;

	@Transient
	private Date nextExecution;
	@Transient
	private List<WorkflowInstance> runs;
	@Transient
	private WorkflowInstance lastRun;
	@Transient
	private List<WorkflowInstance> runningRuns;

	public Workflow() {
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	public int getAuthorId() {
		return authorId;
	}

	public void setAuthorId(int authorId) {
		this.authorId = authorId;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public int getModificatorId() {
		return modificatorId;
	}

	public void setModificatorId(int modificatorId) {
		this.modificatorId = modificatorId;
	}

	public String getModificatorName() {
		return modificatorName;
	}

	public void setModificatorName(String modificatorName) {
		this.modificatorName = modificatorName;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setAuthor(int userId, String userName) {
		this.authorId = userId;
		this.authorName = userName;
		this.creationDate = new Date();
	}

	public void setModificator(int userId, String userName) {
		this.modificatorId = userId;
		this.modificatorName = userName;
		this.modificationDate = new Date();
	}

	public boolean isScheduleDefine() {
		return schedule != null;// && schedule.isOn();
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public boolean scheduleStillValid() {
		return (nextExecution != null && (schedule == null || schedule.getStopDate() == null)) || (nextExecution != null && schedule != null && schedule.getStopDate() != null && nextExecution.before(schedule.getStopDate()));
	}

	public Date getNextExecution() {
		return nextExecution;
	}

	public void setNextExecution(Date nextExecution) {
		this.nextExecution = nextExecution;
	}
	
	public List<WorkflowInstance> getRuns() {
		return runs;
	}
	
	public void setRuns(List<WorkflowInstance> runs) {
		this.runs = runs;
	}

	public WorkflowInstance getLastRun() {
		return lastRun;
	}
	
	public void setLastRun(WorkflowInstance lastRun) {
		this.lastRun = lastRun;
	}

	public List<WorkflowInstance> getRunningRuns() {
		return runningRuns;
	}

	public void setRunningRuns(List<WorkflowInstance> runningRuns) {
		this.runningRuns = runningRuns;
	}

	public boolean isRunning() {
		return runningRuns != null && !runningRuns.isEmpty();
	}

	public WorkflowModel getWorkflowModel() {
		if (this.workflowModel == null) {
			this.workflowModel = new WorkflowModel();
		}
		return workflowModel;
	}

	public void setWorkflowModel(WorkflowModel workflowModel) {
		this.workflowModel = workflowModel;
	}

	@Column(name = "model")
	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public boolean isValid() {
		if (getWorkflowModel().getActivities() != null) {
			for (Activity activity : getWorkflowModel().getActivities()) {
				if (!activity.isValid()) {
					return false;
				}
				else if (!(activity instanceof StopActivity)) {
					if (!buildChildActivity(activity)) {
						return false;
					}
				}
			}
			return true;
		}
		else {
			return false;
		}
	}

	public StartActivity orderActivities() {
		StartActivity startActivity = null;
		for (Activity activity : getWorkflowModel().getActivities()) {
			if (activity instanceof StartActivity) {
				startActivity = (StartActivity) activity;
			}
			else if (activity instanceof StopActivity) {
				continue;
			}

			buildChildActivity(activity);
		}

		return startActivity;
	}

	private boolean buildChildActivity(Activity activity) {
		for (Link link : getWorkflowModel().getLinks()) {
			if (activity.getName().equals(link.getStartActivity().getName())) {

				for (Activity endActivity : getWorkflowModel().getActivities()) {
					if (endActivity.getName().equals(link.getEndActivity().getName())) {
						activity.setChildActivity(endActivity);
						return true;
					}
				}
			}
		}

		return false;
	}

	public List<String> getOutputs() {
		List<String> outputs = new ArrayList<String>();
		
		for(Activity act : workflowModel.getActivities()) {
			for(String out : act.getOutputs()) {
				if(!outputs.contains(out)) {
					outputs.add(out);
				}
			}
		}
		
		return outputs;
	}
}
