package bpm.fd.api.core.model.resources;

import java.io.File;

import org.dom4j.Element;

public class FileProperties implements IResource{
	private static final String smallName = "properties";
	private String localeName;
	private String name;
	private File file;
	
	public FileProperties(String name, String locale, File file){
		this.name = name;
		this.localeName = locale;
		this.file = file;
	}
	
	
	public String getLocaleName(){
		return localeName;
	}
	
	public boolean isDefault(){
		return localeName == null;
	}

	public Element getElement() {
		
		return null;
	}

	public String getName() {
		return name;
	}
	
	public File getFile(){
		return file;
	}
	
	public void setFile(File file) {
		this.file = file;
		
	}
	public String getSmallNameType() {
		return smallName;
	}
}
