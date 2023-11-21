package bpm.vanilla.platform.core.beans;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VanillaLogs {

	public enum Level {
		DEBUG("DEBUG", 0), INFO("INFO", 1), ERROR("ERROR", 3), WARN("WARN", 2);
		private String level;
		private int levelId;

		private Level(String level, int levelId) {
			this.level = level;
			this.levelId = levelId;
		}

		public String getLevel() {
			return level;
		}

		public int getLevelId() {
			return levelId;
		}
	}

	private int id;
	private String level;
	private String application;
	private String operation;
	private long delay;
	private int repositoryId;
	private int directoryItemId;
	private Date date;
	private String clientIp;
	private int userId;
	private int groupId = 0;
	private String message;
	private String objectType;

	private transient String objectName;

	private static transient SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	private List<VanillaLogsProps> properties = new ArrayList<VanillaLogsProps>();

	public VanillaLogs(Level level, String application, String operation, Date date, int userId, int groupId, int repId, int itemId, String ipAddress, String message, long delay) {
		this.level = level.level;
		this.application = application;
		this.operation = operation;
		this.date = date;
		this.userId = userId;
		this.groupId = groupId;
		this.repositoryId = repId;
		this.directoryItemId = itemId;
		this.clientIp = ipAddress;
		this.message = message;
		this.delay = delay;
	}

	public VanillaLogs(Level level, String application, String operation, Date date, int userId, String ipAddress) {
		this.level = level.level;
		this.application = application;
		this.operation = operation;
		this.date = date;
		this.userId = userId;
		this.clientIp = ipAddress;
	}

	public VanillaLogs() {
	}

	public List<VanillaLogsProps> getProperties() {
		return properties;
	}

	public void addProperties(VanillaLogsProps property) {
		properties.add(property);
	}

	public void setProperties(List<VanillaLogsProps> properties) {
		this.properties = properties;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIdString(int id) {
		this.id = id;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public int getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(int repositoryId) {
		this.repositoryId = repositoryId;
	}

	public int getDirectoryItemId() {
		return directoryItemId;
	}

	public void setDirectoryItemId(int directoryItemId) {
		this.directoryItemId = directoryItemId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
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

	public String getXml() {
		StringBuffer buf = new StringBuffer();

		buf.append("    <vlog>\n");
		buf.append("         <id>" + this.id + "</id>\n");
		buf.append("         <level>" + this.level + "</level>\n");
		buf.append("         <application>" + this.application + "</application>\n");
		buf.append("         <operation>" + this.operation + "</operation>\n");
		buf.append("         <delay>" + this.delay + "</delay>\n");
		buf.append("         <repositoryId>" + this.repositoryId + "</repositoryId>\n");
		buf.append("         <directoryItemId>" + this.directoryItemId + "</directoryItemId>\n");
		if (this.date != null)
			buf.append("         <date>" + sdf.format(this.date) + "</date>\n");
		else
			buf.append("         <date>" + "" + "</date>\n");
		buf.append("         <clientIp>" + this.clientIp + "</clientIp>\n");
		buf.append("         <userId>" + this.userId + "</userId>\n");
		buf.append("         <groupId>" + this.groupId + "</groupId>\n");
		buf.append("    </vlog>\n");

		return buf.toString();
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getObjectName() {
		return objectName;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
}
