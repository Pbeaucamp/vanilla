package bpm.workflow.commons.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import bpm.vanilla.platform.core.beans.resources.ISchedule;
import bpm.vanilla.platform.core.beans.resources.Parameter;

@Entity
@Table(name = "schedule")
public class Schedule implements Serializable, ISchedule {

	private static final long serialVersionUID = 1L;

	public enum Period {
		YEAR(0), MONTH(1), WEEK(2), DAY(3), HOUR(4), MINUTE(5);

		private int type;

		private static Map<Integer, Period> map = new HashMap<Integer, Period>();
		static {
			for (Period period : Period.values()) {
				map.put(period.getType(), period);
			}
		}

		private Period(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static Period valueOf(int type) {
			return map.get(type);
		}
	}

	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name="id")
	private int id;

	@Column (name="workflowId")
	private int workflowId;

	@Column (name="beginDate")
	private Date beginDate;

	@Column (name="period")
	private Period period;
	@Column (name="schedule_interval")
	private int interval;

	@Column (name="stopDate")
	private Date stopDate;

	@Column (name="schedule_on")
	private boolean on;

	@Column (name="userId")
	private int userId;

	@Transient
	private List<Parameter> parameters;

	public Schedule() {
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getWorkflowId() {
		return workflowId;
	}
	
	public void setWorkflowId(int workflowId) {
		this.workflowId = workflowId;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public Date getStopDate() {
		return stopDate;
	}

	public void setStopDate(Date stopDate) {
		this.stopDate = stopDate;
	}

	public void setOn(boolean on) {
		this.on = on;
	}

	public boolean isOn() {
		return on;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public int getUserId() {
		return userId;
	}
	
	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}
	
	public List<Parameter> getParameters() {
		return parameters;
	}
}
