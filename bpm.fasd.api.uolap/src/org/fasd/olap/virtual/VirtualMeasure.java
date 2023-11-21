package org.fasd.olap.virtual;

import org.fasd.olap.OLAPCube;
import org.fasd.olap.OLAPElement;
import org.fasd.olap.OLAPMeasure;

public class VirtualMeasure extends OLAPElement {
	private String cubeName = "";
	private String measureName = "";
	
	private OLAPCube cube;
	private OLAPMeasure mes;
	
	public VirtualMeasure(){
		
	}
	
	public VirtualMeasure(OLAPCube c, OLAPMeasure m){
		cube = c;
		mes = m;
		cubeName = c.getName();
		measureName = mes.getName();
		setName("[" + cube.getName() + "].[" + mes.getName() + "]");
	}

	public OLAPCube getCube() {
		return cube;
	}

	public void setCube(OLAPCube cube) {
		this.cube = cube;
	}

	public String getCubeName() {
		return cubeName;
	}

	public void setCubeName(String cubeName) {
		this.cubeName = cubeName;
	}

	public String getMeasureName() {
		return measureName;
	}

	public void setMeasureName(String measureName) {
		if (measureName.startsWith("[Measures].[")){
			this.measureName = measureName.substring(12,measureName.lastIndexOf("]") );
		}
		else
			this.measureName = measureName;
	}

	public OLAPMeasure getMes() {
		return mes;
	}

	public void setMes(OLAPMeasure mes) {
		this.mes = mes;
	}
	
	public String getXML(){
		StringBuffer buf = new StringBuffer();
		
		if (cube == null || mes == null)
			buf.append("            <VirtualCubeMeasure cubeName=\"" + cubeName + "\" name=\"[Measures].[" + measureName + "]\"/>\n");
		else
			buf.append("            <VirtualCubeMeasure cubeName=\"" + cube.getName() + "\" name=\"[Measures].[" + mes.getName() + "]\"/>\n");

		return buf.toString();
	}

	public String getFAXML(){
		StringBuffer buf = new StringBuffer();
		
		buf.append("            <VirtualMeasure-item>\n");
		
		if (cube != null)
			buf.append("                 <Cube-id>" + cube.getId() + "</Cube-id>\n");
		
		buf.append("               <Measure-id>" + mes.getId() + "</Measure-id>\n");
		buf.append("            </VirtualMeasure-item>\n");
		
		return buf.toString();
	}
	
	public String getName(){
		if (mes != null)
			return mes.getName();
		else
			return measureName;
	}
}
