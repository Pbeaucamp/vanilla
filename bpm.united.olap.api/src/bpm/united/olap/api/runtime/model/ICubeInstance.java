package bpm.united.olap.api.runtime.model;

import bpm.united.olap.api.datasource.DataObject;
import bpm.united.olap.api.model.Cube;
import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Schema;

public interface ICubeInstance {
	
	public static final int DIMENSION_DEGENERATED = 1 ;
	public static final int DIMENSION_STAR = 2 ;
	public static final int DIMENSION_SNOW = 3 ;
	public static final int DIMENSION_CLOSURE = 4 ;
	
	public IDataLocator getDataLocator();
	
//	public IMemberChecker getMemberChecker(Member member);
	
	public HierarchyExtractor getHierarchyExtractor(Hierarchy hierarchy);
	
	public DataObject getFactTable();
	
	public Cube getCube();

	public Schema getSchema();

	public void clearLevelDatasInMemory();
	
	public int getGroupId();

	/**
	 * Be careful with this method.
	 * Just there for projection/forecast to create the new date hierarchy.
	 * @param extractor
	 */
	public void addHierarchyExtractor(HierarchyExtractor extractor);
//	public IMemberChecker getMemberChecker(Member eDef); 
}
