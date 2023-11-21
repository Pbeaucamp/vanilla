package bpm.united.olap.api.preload;

import java.util.HashMap;

import bpm.united.olap.api.preload.IPreloadConfig;

public class PreloadConfig implements IPreloadConfig{

	private HashMap<String, Integer> levels = new HashMap<String, Integer>();

	@Override
	public Integer getLevelNumber(String hierarchyUniqueName) {
		
		return levels.get(hierarchyUniqueName);
	}

	@Override
	public void setHierarchyLevel(String hierarchyUniqueName, Integer levelNumber) {
		levels.put(hierarchyUniqueName, levelNumber);
		
	}

	@Override
	public String getXml() {
		StringBuffer buf = new StringBuffer();
		buf.append("<preloadconfig>\n");
		
		buf.append("	<levels>\n");
		
		for(String hiera : levels.keySet()) {
			buf.append("		<level>\n");
			buf.append("			<hierarchy>" + hiera + "</hierarchy>\n");
			buf.append("			<levelnumber>" + levels.get(hiera) + "</levelnumber>\n");
			buf.append("		</level>\n");
		}
		
		buf.append("	</levels>\n");
		
		buf.append("</preloadconfig>\n");
		
		return buf.toString();
	}
	
	public void setHierarchyLevel(String hierarchyUniqueName, String levelNumber) {
		levels.put(hierarchyUniqueName, Integer.parseInt(levelNumber));
		
	}
	

}
