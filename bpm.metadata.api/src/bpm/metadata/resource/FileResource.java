package bpm.metadata.resource;

import java.util.Locale;


public class FileResource implements IResource{

	private String name;
	private String path;

	
	public String getName() {
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getPath(){
		return path;
	}

	public void setPath(String path){
		this.path = path;
	}
	
	public String getXml() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("    <file>\n");
		buf.append("        <name>" + name + "</name>\n");
		buf.append("        <path>" + path + "</path>\n");
		buf.append("    </file>\n");
		
		return buf.toString();
	}

	public boolean isGrantedFor(String groupName) {
		
		return true;
	}

	public String getOutputName() {
		
		return getName();
	}

	public String getOutputName(Locale l) {
		
		return getName();
	}

}

