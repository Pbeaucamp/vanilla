package bpm.vanilla.platform.core.beans;



//import org.dom4j.Element;

public class VanillaLogsProps {

	private int id;
	private int vlogId;
	private String name;
	private String value;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getVlogId() {
		return vlogId;
	}

	public void setVlogId(int vlogId) {
		this.vlogId = vlogId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getXml(){
		StringBuffer buf = new StringBuffer();
		
		buf.append("    <vlogprops>\n");
		buf.append("         <id>" +  this.id + "</id>\n");
		buf.append("         <vlogId>" +  this.vlogId + "</vlogId>\n");
		buf.append("         <name>" +  this.name + "</name>\n");
		buf.append("         <value><![CDATA[" +  this.value + "]]></value>\n"); //encapsulated in cdata
		buf.append("    </vlogprops>\n");
		
		return buf.toString();
	}
	
//	@SuppressWarnings("unchecked")
//	public static List<VanillaLogsProps> parseXml(Element root) {
//		List<Element> eleLogs = root.elements("vlogprops");
//		
//		List<VanillaLogsProps> props = new ArrayList<VanillaLogsProps>();
//		
//		for (Element eleLog : eleLogs) {
//			try {
//				VanillaLogsProps prop = new VanillaLogsProps();
//				prop.setId(Integer.parseInt(eleLog.elementText("id")));
//				prop.setVlogId(Integer.parseInt(eleLog.elementText("vlogId")));
//				prop.setName(eleLog.elementText("name"));
//				prop.setValue(eleLog.elementText("value"));
//				props.add(prop);
//			} catch (Exception e) {
//				System.err.println("Error: Failed to parse: \n" +
//						eleLog.asXML() +
//						"\nReason: " + e.getMessage() +
//						"\nSkipping element");
//			}
//		}
//		
//		return props;
//	}
	
}
