package bpm.studio.jdbc.management.model;

/**
 * This class provide informations about a JDBC driver
 * It is used for dynamic loading of jdbcDriver classes
 * @author LCA
 *
 */
public class DriverInfo {
	private String name;
	private String file;
	private String className;
	private String urlPrefix;
	
	public DriverInfo() {
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getUrlPrefix() {
		return urlPrefix;
	}
	
	public void setUrlPrefix(String prefix) {
		 urlPrefix = prefix;
	}
	
	

}
