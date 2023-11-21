package bpm.android.vanilla.core.beans.cube;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AndroidMeasureGroup implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String uniqueName;
	private String name;
	private List<MeasureAndCol> measuresAndCol;

	public AndroidMeasureGroup(){}
	
	public AndroidMeasureGroup(String uniqueName, String name){
		this.uniqueName = uniqueName;
		this.name = name;
	}
	
	public String getUniqueName(){
		return uniqueName;
	}
	
	public String getName(){
		return name;
	}
	
	public List<MeasureAndCol> getMeasuresAndCol(){
		return measuresAndCol;
	}
	
	public void setUniqueName(String uniqueName){
		this.uniqueName = uniqueName;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setMeasuresAndCol(List<MeasureAndCol> measuresAndCol){
		this.measuresAndCol = measuresAndCol;
	}
	
	public void addMeasuresAndCol(MeasureAndCol measureAndCol){
		if(measuresAndCol == null) {
			this.measuresAndCol = new ArrayList<MeasureAndCol>();
		}
		this.measuresAndCol.add(measureAndCol);
	}
}
