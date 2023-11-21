package bpm.vanilla.platform.core.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Transient;

public class SecurityLog implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum TypeSecurityLog {
		LOGIN(0), LOGOUT(1), WRONG_PASSWORD(2), TOO_MANY_TRIES(3), PASSWORD_CHANGE(4);

		private int type;

		private static Map<Integer, TypeSecurityLog> map = new HashMap<Integer, TypeSecurityLog>();
		static {
			for (TypeSecurityLog actionType : TypeSecurityLog.values()) {
				map.put(actionType.getType(), actionType);
			}
		}
		
		private TypeSecurityLog(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static TypeSecurityLog valueOf(int actionType) {
			return map.get(actionType);
		}
	}

	private int id;
	private TypeSecurityLog type;
	private int userId;
	private int groupId;
	private String clientIp;
	private Date date;
	
	@Transient
	private User user;
	
	@Transient
	private Group group;

	public SecurityLog() {
	}

	public SecurityLog(TypeSecurityLog type, int userId, String clientIp) {
		this.type = type;
		this.userId = userId;
		this.clientIp = clientIp;
		this.groupId = 0;
		this.date = new Date();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public TypeSecurityLog getTypeSecurityLog() {
		return type;
	}

	public void setTypeSecurityLog(TypeSecurityLog type) {
		this.type = type;
	}
	
	public int getType() {
		return type.getType();
	}
	
	public void setType(int type) {
		this.type = TypeSecurityLog.valueOf(type);
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

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public Group getGroup() {
		return group;
	}
	
	public void setGroup(Group group) {
		this.group = group;
	}
}
