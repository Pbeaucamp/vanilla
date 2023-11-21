package bpm.united.olap.runtime.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.united.olap.api.datasource.DataObject;
import bpm.united.olap.api.datasource.Datasource;
import bpm.united.olap.api.datasource.Relation;
import bpm.united.olap.api.model.Cube;
import bpm.united.olap.api.model.Dimension;
import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.Schema;
import bpm.united.olap.api.model.impl.ClosureHierarchy;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.model.HierarchyExtractor;
import bpm.united.olap.api.runtime.model.ICubeInstance;
import bpm.united.olap.api.runtime.model.IDataLocator;
import bpm.united.olap.runtime.cache.ICacheServer;

public class CubeInstance implements ICubeInstance{
	private static final int DIMENSION_DEGENERATED = 1 ;
	private static final int DIMENSION_STAR = 2 ;
	private static final int DIMENSION_SNOW = 3 ;
	private static final int DIMENSION_CLOSURE = 4;
	private static final int DIMENSION_ONECOLUMNDATE = 5;
	private static final int DIMENSION_ONECOLUMNDATE_DEGENERATED = 6;
	
	private Cube cubeModel;
	private DataObject factTable;
	
	private HashMap<Hierarchy, HierarchyExtractor> extractors =  new HashMap<Hierarchy, HierarchyExtractor>();
	private IDataLocator dataLocator;
	private Schema schema;
	
	private ICacheServer cacheServer;
	private PathFinder pathFinder;
	private int groupId = -1;
	
	public CubeInstance(Schema schema, Cube cubeModel, DataObject factTable, ICacheServer cacheServer, IRuntimeContext ctx){
		this.groupId = ctx.getGroupId();
		this.cubeModel = cubeModel;
		this.factTable = factTable;
		this.cacheServer = cacheServer;
		this.schema = schema;
		HashMap<Hierarchy, Relation> hierachiesRelations = findForeignKeys();
		this.pathFinder = createPathFinder();
		this.dataLocator = new DataLocator(cubeModel, factTable, hierachiesRelations, pathFinder);
		createHierarchyExtractors();
	}
	
	public int getGroupId(){
		return groupId;
	}
	private PathFinder createPathFinder() {
		List<Relation> existingRelations = new ArrayList<Relation>();
		for(Datasource ds : schema.getDatasources()) {
			for(Relation rel : ds.getRelations()) {
				existingRelations.add(rel);
			}
		}
		return new SnowFlakesPathFinder(existingRelations);
	}


	private HashMap<Hierarchy, Relation> findForeignKeys() {
		HashMap<Hierarchy, Relation> foreignKeys = new HashMap<Hierarchy, Relation>();
		
		for(Dimension dim : cubeModel.getDimensions()) {
			for(Hierarchy hiera : dim.getHierarchies()) {
				Level rootLevel = hiera.getLevels().get(0);
				if(rootLevel.getItem().getParent() != factTable) {
					for(Relation rel : rootLevel.getItem().getParent().getParent().getRelations()) {
						if(rel.getLeftItem().getParent() == rootLevel.getItem().getParent() && rel.getRightItem().getParent() == factTable) {
							foreignKeys.put(hiera, rel);
							break;
						}
						else if(rel.getRightItem().getParent() == rootLevel.getItem().getParent() && rel.getLeftItem().getParent() == factTable) {
							foreignKeys.put(hiera, rel);
							break;
						}
					}
					if(foreignKeys.containsKey(hiera)) {
						continue;
					}
				}
			}
		}
		
		return foreignKeys;
	}


	private void createHierarchyExtractors(){
		
		for(Dimension d : cubeModel.getDimensions()){
			
			for(Hierarchy  h : d.getHierarchies()){
				DataObject lastTable = null;
				h.isIsClosureHierarchy();
				/*
				 * detect dimensionType
				 */
				int type = -1;
				for(Level l : h.getLevels()){
					if (lastTable == null){
						lastTable = l.getItem().getParent();
					}
					else if (l.getItem().getParent() != lastTable){
						type = DIMENSION_SNOW;
						break;
					}
					if (lastTable == factTable){
						if(h.getParentDimension().isIsOneColumnDate()) {
							type = DIMENSION_ONECOLUMNDATE_DEGENERATED;
							break;
						}
						else {
							type = DIMENSION_DEGENERATED;
						}
					}
					else if(h.isIsClosureHierarchy()) {
						type = DIMENSION_CLOSURE;
						break;
					}
					else if(h.getParentDimension().isIsOneColumnDate()) {
						type = DIMENSION_ONECOLUMNDATE;
						break;
					}
					else {
						type = DIMENSION_STAR;
					}
				}
				
				switch (type) {
				case DIMENSION_DEGENERATED:
					extractors.put(h, new DegeneratedHierarchyExtractor(h, cacheServer));
					break;
				case DIMENSION_STAR:
					extractors.put(h, new StarHierarchyExtractor(h, cacheServer));
					break;
				case DIMENSION_CLOSURE:
					extractors.put(h, new ClosureHierarchyExtractor((ClosureHierarchy) h, cacheServer));
					break;
				case DIMENSION_ONECOLUMNDATE:
					extractors.put(h, new OneColumnDateHierarchyExtractor(h, cacheServer));
					break;
				case DIMENSION_ONECOLUMNDATE_DEGENERATED:
					extractors.put(h, new DegeneratedDateHierarchyExtractor(h, cacheServer));
					break;
				case DIMENSION_SNOW:
					extractors.put(h, new SnowFlakesHierarchyExtractor(h, cacheServer, pathFinder, factTable));
					break;
				}
				
			}
			
		}
		
	}


	public IDataLocator getDataLocator() {
		return dataLocator;
	}


	

	@Override
	public Cube getCube() {
		return cubeModel;
	}


	@Override
	public DataObject getFactTable() {
		return factTable;
	}


	@Override
	public HierarchyExtractor getHierarchyExtractor(Hierarchy hierarchy) {
		return extractors.get(hierarchy);
	}


	@Override
	public Schema getSchema() {
		return schema;
	}


	@Override
	public void clearLevelDatasInMemory() {
		for(HierarchyExtractor extractor : extractors.values()) {
			extractor.clearLevelDatasInMemory();
		}
	}

	@Override
	public void addHierarchyExtractor(HierarchyExtractor extractor) {
		extractors.put(extractor.getHierarchy(), extractor);
	}
	
}
