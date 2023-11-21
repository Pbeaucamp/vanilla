package bpm.metadata.resource;

public class ImageResource extends FileResource {

	

	public String getXml() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("    <image>\n");
		buf.append("        <name>" + getName() + "</name>\n");
		buf.append("        <path>" + getPath() + "</path>\n");
		buf.append("    </image>\n");
		
		return buf.toString();
	}
}
