package bpm.vanilla.platform.core.beans.scheduler;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;

public class JobDetail implements Serializable {

	private static final long serialVersionUID = -8147406024806933540L;

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
	
	private String name;
	private String description;
	
	private Date beginDate;

	private Period period;
	private int interval;

	private Date stopDate;

	private boolean on = true;
	private boolean needToBeLaunch;
	
	private int userId;
	private int groupId;
	private int repositoryId;
	private int itemId;
	
	private String format;
	
	private List<VanillaGroupParameter> parameters;
	
	//Mail informations
	private String subject;
	private String content;
	private List<Integer> subscribers;
	
	public JobDetail() { }
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(int repositoryId) {
		this.repositoryId = repositoryId;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
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

	public boolean isNeedToBeLaunch() {
		return needToBeLaunch;
	}

	public void setNeedToBeLaunch(boolean needToBeLaunch) {
		this.needToBeLaunch = needToBeLaunch;
	}

	public List<VanillaGroupParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<VanillaGroupParameter> parameters) {
		this.parameters = parameters;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public List<Integer> getSubscribers() {
		return subscribers;
	}
	
	public void setSubscribers(List<Integer> subscribers) {
		this.subscribers = subscribers;
	}
}
