package org.fasd.olap.virtual;

import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPElement;

public class VirtualDimension extends OLAPElement {
	private String cubeName = "";
	private String dimName = "";
	private boolean visible = true;
	
	//private boolean shared = false;
	
	//private OLAPCube cube;
	private OLAPDimension dim;
	
	public VirtualDimension(){
		
	}
	
	public VirtualDimension(OLAPDimension d){
		dim = d;
		dimName = dim.getName();
		setName(dim.getName());
	}

//	public OLAPCube getCube() {
//		return cube;
//	}
//
//	public void setCube(OLAPCube cube) {
//		this.cube = cube;
//	}

	public String getCubeName() {
		return cubeName;
	}

	public void setCubeName(String cubeName) {
		this.cubeName = cubeName;
	}

	public OLAPDimension getDim() {
		return dim;
	}

	public void setDim(OLAPDimension dim) {
		this.dim = dim;
	}

	public String getDimName() {
		return dimName;
	}

	public void setDimName(String dimName) {
		this.dimName = dimName;
	}

//	public boolean isShared() {
//		return shared;
//	}
//
//	public void setShared(boolean shared) {
//		this.shared = shared;
//	}


	public String getFAXML(){
		StringBuffer buf = new StringBuffer();
		
		buf.append("            <VirtualDimension-item>\n");
		
//		if (cube != null)
//			buf.append("                <Cube-id>" + cube.getId() + "</Cube-id>\n");
		buf.append("                <visible>" + visible + "</visible>\n");
		buf.append("                <Dimension-id>" + dim.getId() + "</Dimension-id>\n");
		buf.append("            </VirtualDimension-item>\n");
		
		return buf.toString();
	}
	
	public String getXML(){
		StringBuffer buf = new StringBuffer();
		
		if (/*cube == null || */dim == null){
			buf.append("            <VirtualCubeDimension ");
			
			if (!visible)
				buf.append("visible=\"false\" ");
//			if (!shared){
//				buf.append(/*"cubeName=\"" + cubeName + "\" */ "name=\"" + dimName + "\"/>\n");
//			}
//			else{
				buf.append("name=\"" + dimName + "\"/>\n");
//			}
		}
			
		else{
			//if (!shared){
			//	buf.append("            <VirtualCubeDimension cubeName=\"" + cube.getName() + "\" name=\"" + dim.getName() + "\"/>\n");
		//	}
			//else{
				buf.append("            <VirtualCubeDimension name=\"" + dim.getName() + "\"/>\n");
			//}
			
		}
			

		return buf.toString();
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public void setVisible(String visible) {
		this.visible = Boolean.parseBoolean(visible);
	}
}
