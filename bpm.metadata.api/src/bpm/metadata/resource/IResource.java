package bpm.metadata.resource;

import java.util.Locale;

public interface IResource {
	public String getName();
	
	public String getXml();
	
	public boolean isGrantedFor(String groupName);
	
	public String getOutputName();
	
	public String getOutputName(Locale l);
}
