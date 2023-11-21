package bpm.vanilla.platform.core.beans;

/**
 * not used anymore, replaced by the Node and Service model 
 * 
 *
 */
public class Server {

	public static final int TYPE_FAWEB = 0;
	public static final int TYPE_BIRT = 1;
	public static final int TYPE_SCHEDULER = 2;
	public static final int TYPE_GATEWAY = 3;
	public static final int TYPE_ORBEON = 4;
	public static final int TYPE_FMDT = 5;
	public static final int TYPE_SECURITY = 6;
	public static final int TYPE_REPOSITORY = 7;
	public static final int TYPE_FWR = 8;
	public static final int TYPE_FDWEB = 9;
	public static final int TYPE_FMWEB = 10;
//	
//	
	public static final String[] TYPE_NAMES = {
		"FreeAnalysis", "Birt", "Scheduler", "Gateway", "Orbeon","FreeMetaData",
		"Vanilla Security", "Repository", "FreeWebReport", "FreeDashboard", "FreeMetricsWeb"
	}; 
	
	private int id;
	private String name;
	private String description;
	private String url;
	
	private String componentNature;
	private String componentStatus;//just like nature, only for services like reporting and such
	
//	private int type = -1;
	
	/**
	 * @return the componentNature
	 */
	public String getComponentNature() {
		return componentNature;
	}
	/**
	 * @param componentNature the componentNature to set
	 */
	public void setComponentNature(String componentNature) {
		this.componentNature = componentNature;
	}
	
	public String getComponentStatus() {
		return componentStatus;
	}
	
	public void setComponentStatus(String componentStatus) {
		this.componentStatus = componentStatus;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setId(String id) {
		this.id = Integer.parseInt(id);
	}
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
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
//	public int getType() {
//		return type;
//	}
//	public void setType(int type) {
//		this.type = type;
//	}
//	public void setType(String type) {
//		this.type = Integer.parseInt(type);
//	}
	public String getXml(){
		StringBuffer buf = new StringBuffer();
		
		buf.append("    <server>\n");
		buf.append("    	<id>" + id + "</id>\n");
		buf.append("    	<name>" + name + "</name>\n");
		buf.append("    	<description>" + description + "</description>\n");
		buf.append("    	<url>" + url + "</url>\n");
		buf.append("    	<componentNature>" + componentNature + "</componentNature>\n");
		buf.append("    </server>");
		return buf.toString();
	}
	
}
