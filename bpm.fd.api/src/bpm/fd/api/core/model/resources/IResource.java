package bpm.fd.api.core.model.resources;

import java.io.File;

import org.dom4j.Element;

public interface IResource {
	public String getName();
	public File getFile();
	public Element getElement();
	public void setFile(File file);
	public String getSmallNameType();
}
