package bpm.vanilla.platform.core.beans.resources;

import java.io.Serializable;
import java.util.List;

public interface IClassItem extends Serializable {

	public String getName();
	
	public List<ClassRule> getRules();
	
	public void addRule(ClassRule rule);
	
	public void removeRule(ClassRule rule);
	
	public String getMainClassIdentifiant();

	public String getPath();
}
