package bpm.vanilla.platform.core.beans.scheduler;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JobInstance implements Serializable {

	private static final long serialVersionUID = -4327628997092227363L;

	public enum Result {
		SUCCESS(0), 
		ERROR(1), 
		RUNNING(2), 
		UNKNOWN(3);
		
		private int type;
		
		private static Map<Integer, Result> map = new HashMap<Integer, Result>();
		static {
			for (Result result : Result.values()) {
				map.put(result.getType(), result);
			}
		}
		
		private Result(int type) {
			this.type = type;
		}
		
		public int getType() {
			return type;
		}

		public static Result valueOf(int type) {
			return map.get(type);
		}
	}
	
	private Result result;
	
	private int id;
	private int jobId;
	
	private Date startDate;
	private Date endDate;
	
	private String message;
	
	public JobInstance() { }
	
	public JobInstance(int jobId) {
		this.jobId = jobId;
		this.startDate = new Date();
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getJobId() {
		return jobId;
	}

	public void setJobId(int jobId) {
		this.jobId = jobId;
	}

	public void setResult(Result result) {
		this.result = result;
	}
	
	public Result getResult() {
		return result;
	}
	
	public void setResultType(int typeResult) {
		this.result = Result.valueOf(typeResult);
	}
	
	public int getResultType() {
		return result.getType();
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public long getDuration() {
		if(startDate == null || endDate == null) {
			return -1;
		}
		return endDate.getTime() - startDate.getTime();
	}

	public String getDurationAsString() {
		long duration = getDuration();
		if(duration == -1) {
			return "";
		}
		
		int milliseconds =  (int) duration % 1000;
		int seconds = (int) (duration / 1000) % 60 ;
		int minutes = (int) ((duration / (1000*60)) % 60);
		int hours   = (int) ((duration / (1000*60*60)) % 24);
		return (hours != 0 ? hours + "h" : "") + (minutes != 0 ? minutes + "m" : "") + seconds + "s " + milliseconds;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
