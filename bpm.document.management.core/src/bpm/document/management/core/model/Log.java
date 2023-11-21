package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Log<T extends ILog> implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum LogType {
		CHECK_CPP(0),
		CHECK_COMPTA(1),
		UNKNOWN(2),
		INSERT_AKLABOX(3),
		INSERT_CEGID(4),
		INSERT_AKLADEMAT(5),
		UPDATE(6),
		INSERT_COCKTAIL(7),
		CHECK_BL(8),
		REJECT_CPP(9);

		private int type;

		private static Map<Integer, LogType> map = new HashMap<Integer, LogType>();
		static {
			for (LogType type : LogType.values()) {
				map.put(type.getType(), type);
			}
		}

		private LogType(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static LogType valueOf(int type) {
			return map.get(type);
		}
	}

	public enum LogStatus {
		SUCCESS(0), ERROR(1), RUNNING(2), UNKNOWN(3);

		private int type;

		private static Map<Integer, LogStatus> map = new HashMap<Integer, LogStatus>();
		static {
			for (LogStatus statusType : LogStatus.values()) {
				map.put(statusType.getType(), statusType);
			}
		}

		private LogStatus(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static LogStatus valueOf(int statusType) {
			return map.get(statusType);
		}
	}

	private int id;
	private LogType type;
	private LogStatus status;
	
	private int userId;
	private Integer akladematEntityId;

	private Date startDate;
	private Date endDate;

	private String message;

	/* Transient */
	private T log;
	private String logModel;

	public Log() {
		this.startDate = new Date();
	}

	public Log(LogType type, LogStatus status) {
		this();
		this.type = type;
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LogType getLogType() {
		return type;
	}

	public int getType() {
		return type.getType();
	}

	public void setType(LogType type) {
		this.type = type;
	}

	public void setType(int type) {
		this.type = LogType.valueOf(type);
	}

	public LogStatus getLogStatus() {
		return status;
	}

	public int getStatus() {
		return status.getType();
	}

	public void setStatus(LogStatus status) {
		this.status = status;
	}

	public void setStatus(int status) {
		this.status = LogStatus.valueOf(status);
	}
	
	public int getUserId() {
		return userId;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public Integer getAkladematEntityId() {
		return akladematEntityId;
	}
	
	public void setAkladematEntityId(Integer akladematEntityId) {
		this.akladematEntityId = akladematEntityId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public long getDuration() {
		if (startDate == null || endDate == null) {
			return -1;
		}
		return endDate.getTime() - startDate.getTime();
	}

	public String getDurationAsString() {
		long duration = getDuration();
		if (duration == -1) {
			return "";
		}

		int milliseconds = (int) duration % 1000;
		int seconds = (int) (duration / 1000) % 60;
		int minutes = (int) ((duration / (1000 * 60)) % 60);
		int hours = (int) ((duration / (1000 * 60 * 60)) % 24);
		return (hours != 0 ? hours + "h" : "") + (minutes != 0 ? minutes + "m" : "") + seconds + "s " + milliseconds;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getLog() {
		return log;
	}

	public void setLog(T log) {
		this.log = log;
	}

	public String getLogModel() {
		return logModel;
	}

	public void setLogModel(String logModel) {
		this.logModel = logModel;
	}
}
