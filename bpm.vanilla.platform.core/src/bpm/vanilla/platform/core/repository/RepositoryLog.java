package bpm.vanilla.platform.core.repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "rpy_log")
public class RepositoryLog {

	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column(name = "id")
	private int id;
	
	@Column(name = "app_name")
	private String appName;
	
	@Column(name = "`function`")
	private String function;
	
	@Column(name = "operation")
	private String operation;
	
	@Column(name = "item_id")
	private Integer objectId;
	
	@Column(name = "LOG_USER_ID")
	private Integer userId;
	
	@Column(name = "client_id")
	private String clientIp;
	
	@Column(name = "server_ip")
	private String serverIp;
	
	@Column(name = "group_id")
	private Integer groupId;
	
	@Column(name = "log_time")
	private Date time;
	
	@Column(name = "delay")
	private long delay;
	
	@Column(name = "query")
	private String query;
	
	@Column(name = "output_type")
	private String outputType;
	
	@Column(name = "`xml`")
	private String xml;
	
	@Column(name = "user_defined_1")
	private String userDefined1;
	
	@Column(name = "user_defined_2")
	private String userDefined2;
	
	@Column(name = "user_defined_3")
	private String userDefined3;

	@Transient
	private String userName;
	
	@Transient
	private String groupName;
	
	@Transient
	private String objectName;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setId(String id) {
		this.id = Integer.parseInt(id);
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getFunction() {
		return function;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public Integer getObjectId() {
		return objectId;
	}

	public void setObjectId(Integer objectId) {
		this.objectId = objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = Integer.parseInt(objectId);
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public void setUserId(String userId) {
		this.userId = Integer.parseInt(userId);
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(String id) {
		this.groupId = Integer.parseInt(id);
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public void setTime(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("hhhh-MM-dd:hh:mm:ss");
		try {
			this.time = sdf.parse(time);
		} catch (ParseException e) {

			e.printStackTrace();
		}
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public void setDelay(String delay) {
		this.delay = Long.parseLong(delay);
	}

	public String getOutputType() {
		return outputType;
	}

	public void setOutputType(String outputType) {
		this.outputType = outputType;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getUserDefined1() {
		return userDefined1;
	}

	public void setUserDefined1(String userDefined1) {
		this.userDefined1 = userDefined1;
	}

	public String getUserDefined2() {
		return userDefined2;
	}

	public void setUserDefined2(String userDefined2) {
		this.userDefined2 = userDefined2;
	}

	public String getUserDefined3() {
		return userDefined3;
	}

	public void setUserDefined3(String userDefined3) {
		this.userDefined3 = userDefined3;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getXML() {
		StringBuffer buf = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");

		buf.append("<log>\n");
		buf.append("    <id>" + id + "</id>\n");
		buf.append("    <appName>" + appName + "</appName>\n");
		buf.append("    <operation>" + operation + "</operation>\n");
		buf.append("    <objectId>" + objectId + "</objectId>\n");
		buf.append("    <userId>" + objectId + "</userId>\n");
		buf.append("    <clientIp>" + clientIp + "</clientIp>\n");

		if (groupId != null) {
			buf.append("    <groupId>" + groupId + "</groupId>\n");
		}

		if (time != null) {
			buf.append("    <time>" + sdf.format(time) + "</time>\n");
		}
		buf.append("    <delay>" + delay + "</delay>\n");
		if (groupName != null) {
			buf.append("    <groupName>" + groupName + "</groupName>\n");
		}

		buf.append("</log>\n");

		return buf.toString();
	}

}
