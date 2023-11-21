package bpm.faweb.server.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXException;

import bpm.faweb.shared.infoscube.ChartInfos;

public class ChartDigester {
	private ChartInfos chartInfos;
	private Digester dig;
	
	
	public ChartDigester(InputStream input) throws IOException, SAXException{
		dig = new Digester();
		createCallbacks(dig);
		chartInfos = (ChartInfos)dig.parse(input);
		
	}
	
	public ChartDigester(File file) throws IOException, SAXException{
		dig = new Digester();
		createCallbacks(dig);
		chartInfos = (ChartInfos)dig.parse(file);	
	}
	
	public ChartDigester(String path) throws IOException, SAXException{
		dig = new Digester();
		createCallbacks(dig);
		File file = new File(path);
		chartInfos = (ChartInfos)dig.parse(file);
		
	}
	
	
	private void createCallbacks(Digester dig){
		dig.setValidating(false);
	
		String root = "view";

		dig.addObjectCreate(root + "/chart", ChartInfos.class);
		
			dig.addCallMethod(root + "/chart/title", "setTitle", 0);
		
			dig.addCallMethod(root + "/chart/groups/group", "addGroup",0);
		
			dig.addCallMethod(root + "/chart/datas/data", "addData",0);
			
			dig.addCallMethod(root + "/chart/filters/filter", "addChartFilter", 0);
			
			dig.addCallMethod(root + "/chart/measure", "setMeasure",0);
		
			dig.addCallMethod(root + "/chart/type", "setType" ,0);
			
			dig.addCallMethod(root + "/chart/renderer", "setRenderer" ,0);
	}

	public ChartInfos getChartInfos() {
		return chartInfos;
	}

	public void setChartInfos(ChartInfos chartInfos) {
		this.chartInfos = chartInfos;
	}
}
