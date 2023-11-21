package bpm.vanillahub.runtime.specifics;

import java.util.HashMap;
import java.util.List;

public interface IExportCSV {
	
	public String getClassKeyID();

	public void buildCSV(String parentId, HashMap<String, List<List<Object>>> items);
}
