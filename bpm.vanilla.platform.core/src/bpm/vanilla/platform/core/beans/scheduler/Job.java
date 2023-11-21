package bpm.vanilla.platform.core.beans.scheduler;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class Job implements Serializable {

	private static final long serialVersionUID = 5002416006652589733L;

	private int id;
	private String detailXml;

	private JobDetail detail;

	@XStreamOmitField
	private List<JobInstance> instances;

	@XStreamOmitField
	private Date nextExecution;
	
	@Transient
	private RepositoryItem item;

	public Job() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return detail != null ? detail.getName() : null;
	}

	public String getDescription() {
		return detail != null ? detail.getDescription() : null;
	}

	public Integer getRepositoryId() {
		return detail != null ? detail.getRepositoryId() : null;
	}

	public Integer getItemId() {
		return detail != null ? detail.getItemId() : null;
	}

	public List<VanillaGroupParameter> getParameters() {
		return detail != null ? detail.getParameters() : null;
	}

	public boolean isOn() {
		return detail != null ? detail.isOn() : false;
	}

	public Date getStopDate() {
		return detail != null ? detail.getStopDate() : null;
	}

	public Date getBeginDate() {
		return detail != null ? detail.getBeginDate() : null;
	}

	public boolean isNeedToBeLaunch() {
		return detail != null ? detail.isNeedToBeLaunch() : false;
	}

	public void setNeedToBeLaunch(boolean needToBeLaunch) {
		if (detail != null) {
			detail.setNeedToBeLaunch(needToBeLaunch);
		}
	}

	public JobDetail getDetail() {
		return detail;
	}

	public void setDetail(JobDetail detail) {
		this.detail = detail;
	}

	public void setInstances(List<JobInstance> instances) {
		this.instances = instances;
	}

	public List<JobInstance> getInstances() {
		return instances;
	}

	public String getDetailXml() {
		return detailXml;
	}

	public void setDetailXml(String detailXml) {
		this.detailXml = detailXml;
	}

	public JobInstance getLastRun() {
		JobInstance lastRun = null;
		if (instances != null && !instances.isEmpty()) {
			for (JobInstance instance : instances) {
				if (lastRun == null) {
					lastRun = instance;
				}
				else if (instance.getStartDate().after(lastRun.getStartDate())) {
					lastRun = instance;
				}
			}
		}
		return lastRun;
	}

	public Date getNextExecution() {
		return nextExecution;
	}

	public void setNextExecution(Date nextExecution) {
		this.nextExecution = nextExecution;
	}

	public RepositoryItem getItem() {
		return item;
	}

	public void setItem(RepositoryItem item) {
		this.item = item;
	}
	
}
