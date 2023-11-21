package org.fasd.olap;

import java.util.ArrayList;
import java.util.List;

public class DrillCube extends Drill {

	private int fasdId;
	private String cubeName;
//	private List<String> dimensions;
	
	public DrillCube() {}
	
	public DrillCube(int fasdId, String cubeName) {
		this.fasdId = fasdId;
		this.cubeName = cubeName;
	}
	
//	public DrillCube(int fasdId, String cubeName, List<String> dimensions) {
//		this.fasdId = fasdId;
//		this.cubeName = cubeName;
//		this.dimensions = dimensions;
//	}

	public int getFasdId() {
		return fasdId;
	}

	public void setFasdId(int fasdId) {
		this.fasdId = fasdId;
	}

	public String getCubeName() {
		return cubeName;
	}

	public void setCubeName(String cubeName) {
		this.cubeName = cubeName;
	}

//	public List<String> getDimensions() {
//		if(dimensions == null) {
//			dimensions = new ArrayList<String>();
//		}
//		return dimensions;
//	}
//
//	public void setDimensions(List<String> dimensions) {
//		this.dimensions = dimensions;
//	}
//	
//	public void addDimension(String dimension) {
//		if(dimensions == null) {
//			dimensions = new ArrayList<String>();
//		}
//		dimensions.add(dimension);
//	}

	@Override
	public String getXml() {
		StringBuilder buf = new StringBuilder();
		buf.append("<drillcube>\n");
		
		buf.append("	<name>"+drillName+"</name>\n");
		buf.append("	<fasd>"+fasdId+"</fasd>\n");
		buf.append("	<cubename>"+cubeName+"</cubename>\n");
//		buf.append("	<dimensions>\n");
//		
//		for(String dim : dimensions) {
//			buf.append("		<dimension>"+dim+"</dimension>\n");
//		}
//		
//		buf.append("	</dimensions>\n");
		
		buf.append("</drillcube>\n");
		return buf.toString();
	}
	
	public void setFasdId(String id) {
		fasdId = Integer.parseInt(id);
	}
}
