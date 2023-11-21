package bpm.vanilla.map.core.design.kml;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.map.core.design.fusionmap.ColorRange;

public class KmlColoringDatas {

	private List<ColorRange> colorRanges = new ArrayList<ColorRange>();
	
	private List<Object[]> datas;
	
	
	public KmlColoringDatas(List<ColorRange> colorRanges, List<Object[]> datas){
		this.datas = datas;
		this.colorRanges = colorRanges;
	}
	
	public List<ColorRange> getColorRanges(){
		return colorRanges;
	}
	
	public List<Object[]> getDatas(){
		return datas;
	}
	
	
}
