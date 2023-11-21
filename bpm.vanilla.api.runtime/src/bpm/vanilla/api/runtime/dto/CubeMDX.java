package bpm.vanilla.api.runtime.dto;

import java.util.ArrayList;
import java.util.List;

import bpm.fa.api.olap.OLAPCube;


public class CubeMDX {
	private String mdxStr;
	private List<CubeOLAPMember> cols;
	private List<CubeOLAPMember> rows;
	private List<CubeOLAPMember> wheres;
	private List<CubeMeasure> measures;
	
	
	public CubeMDX(OLAPCube cube) throws Exception {
		mdxStr = cube.getMdx().getMDX();
		measures = new ArrayList<>();
		cols = loadMembers(cube,cube.getMdx().getCols());
		rows = loadMembers(cube,cube.getMdx().getRows());
		wheres = loadMembers(cube,cube.getMdx().getWhere());
	}
	
	public String getMdxStr() {
		return mdxStr;
	}
	public List<CubeOLAPMember> getCols() {
		return cols;
	}
	public List<CubeOLAPMember> getRows() {
		return rows;
	}
	public List<CubeOLAPMember> getWheres() {
		return wheres;
	}
	public List<CubeMeasure> getMeasures() {
		return measures;
	}
	
	public List<CubeOLAPMember> loadMembers(OLAPCube cube,List<String> memUnames) throws Exception{
		
		if(memUnames != null) {
			List<CubeOLAPMember> memArray = new ArrayList<>();
			
			for(String memUname : memUnames) {
				if(memUname.startsWith("[Measures]")) {
					measures.add(CubeMeasure.findCubeMeasure(cube, memUname));
				}
				else if(memUname.endsWith(".children")){
					memArray.addAll(CubeOLAPMember.findChildrenCubeOLAPMember(cube, memUname.split(".children")[0], false));
				}
				else {
					memArray.add(CubeOLAPMember.findCubeOLAPMember(cube,memUname,false));
				}
			}
			
			return memArray;		
		}
		return null;

	}
	
	

}
