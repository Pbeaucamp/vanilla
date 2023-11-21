package bpm.vanilla.platform.core.beans.service;

public interface IService {

	public void setName(String name);
	public String getName();
	
	public void setType(int type);
	public int getType();
	
	public void setValue(String value);
	public String getValue();
}
