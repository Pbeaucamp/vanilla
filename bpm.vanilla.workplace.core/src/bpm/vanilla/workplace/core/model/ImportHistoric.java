package bpm.vanilla.workplace.core.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ImportHistoric {
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private String fileName;
	private String packageName;
	private Date date;
	private String logs;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setDate(String date) {
		try {
			this.date = sdf.parse(date);
		} catch (Exception e) {

		}
	}

	public String getLogs() {
		return logs;
	}

	public void setLogs(String logs) {
		this.logs = logs;
	}

	public String getXml() {
		StringBuffer buf = new StringBuffer("<importHistoric>");
		buf.append("    <date>");
		buf.append(sdf.format(date));
		buf.append("    </date>");
		buf.append("    <fileName>");
		buf.append(fileName);
		buf.append("    </fileName>");
		buf.append("    <packageName>");
		buf.append(packageName);
		buf.append("    </packageName>");
		buf.append("    <logs>");
		buf.append(logs);
		buf.append("    </logs>");
		buf.append("</importHistoric>");
		return buf.toString();
	}

}
