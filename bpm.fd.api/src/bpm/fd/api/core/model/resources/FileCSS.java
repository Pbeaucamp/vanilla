package bpm.fd.api.core.model.resources;

import java.io.File;

import org.dom4j.Element;

public class FileCSS implements IResource{
	private static final String smallName = "css";
	private String name;
	private File file;
	
	public FileCSS(String name, File file){
		this.name = name;
		this.file = file;
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
