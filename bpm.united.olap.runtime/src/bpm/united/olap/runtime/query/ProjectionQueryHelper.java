package bpm.united.olap.runtime.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.united.olap.api.model.Dimension;
import bpm.united.olap.api.model.ElementDefinition;
import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.model.impl.ClosureLevel;
import bpm.united.olap.api.model.impl.Projection;
import bpm.united.olap.api.model.impl.ProjectionMeasure;
import bpm.united.olap.api.result.DrillThroughIdentifier;
import bpm.united.olap.api.result.OlapResult;
import bpm.united.olap.api.result.ResultFactory;
import bpm.united.olap.api.result.ResultLine;
import bpm.united.olap.api.result.impl.ValueResultCell;
import bpm.united.olap.api.runtime.DataCell;
import bpm.united.olap.api.runtime.DataCellIdentifier2;
import bpm.united.olap.api.runtime.DataStorage;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.RuntimeFactory;
import bpm.united.olap.api.runtime.model.ICubeInstance;
import bpm.united.olap.api.runtime.model.IDataLocator;
import bpm.united.olap.runtime.data.IDataMapper;
import bpm.united.olap.runtime.model.ProjectionHierarchyExtractor;
import bpm.united.olap.runtime.projection.CrossMembersValues;
import bpm.united.olap.runtime.projection.ProjectionDataLocator;
import bpm.united.olap.runtime.projection.ProjectionDataMapper;
import bpm.united.olap.runtime.projection.ProjectionResultSet;
import bpm.united.olap.runtime.projection.ProjectionSerializationHelper;
import bpm.vanilla.platform.logging.IVanillaLogger;

public class ProjectionQueryHelper extends QueryHelper {

	private Projection projection;
	private IQueryHelper originalHelper;
	
	public ProjectionQueryHelper(IVanillaLogger logger, IRuntimeContext runtimeContext, Projection projection, IQueryHelper projectionHelper) {
		super(logger, runtimeContext);
		this.projection = projection;
		originalHelper = projectionHelper;
	}

	@Override
	public DataStorage executeQuery(ICubeInstance cubeInstance, DataStorage storage, int limit) throws Exception {
		
		//execute the original query first
		storage = originalHelper.executeQuery(cubeInstance, storage, limit);
		
		//find the used measures to load only the needed files
		List<String> usedMeasures = new ArrayList<String>();
		for(DataCellIdentifier2 possibleId : storage.getPossibleIds().keySet()) {
			if(!usedMeasures.contains(possibleId.getMeasure().getUname())) {
				usedMeasures.add(possibleId.getMeasure().getUname());
			}
		}
		
		for(String messs : usedMeasures) {
		
			for(int i = 1 ; ; i++) {
				
				List<CrossMembersValues> values = ProjectionSerializationHelper.deserialize(projection, i, messs);
				if(values == null) {
					break;
				}
				
				//Create the dataLocator, v is a sample value to find element indexes
				CrossMembersValues v = values.get(0);
				IDataLocator dataLocator = new ProjectionDataLocator(v, cubeInstance, projection);
				
				//Create the resultSet
				IResultSet rs = new ProjectionResultSet(values);
				
				//Create the dataMapper
				IDataMapper mapper = new ProjectionDataMapper(storage, cubeInstance, dataLocator, runtimeContext);
				
				while(rs.next()) {
					
					//test if the where clause is respected
					boolean whereRespected = false;
						List<Member> wheres = storage.getDataCells().get(0).getIdentifier2().getWhereMembers();
					if(wheres != null && wheres.size() > 0) {
						whereRespected = mapper.checkWhereClause(rs, wheres);
					}
					else {
						whereRespected = true;
					}
						
					//map values if the where clause is respected
					if(whereRespected) {
						for(DataCellIdentifier2 possibleId : storage.getPossibleIds().keySet()) {
							
							if(rs.getString(v.getMemberUnames().size()).equals(possibleId.getMeasure().getUname())) {
								
		//						if(isPossibleId(possibleId, cubeInstance, rs)) {
									DataCell cell = mapper.mapData(rs, possibleId);
																
									if(cell != null && !cell.isCalculated()) {
										Measure mes = cell.getIdentifier2().getMeasure();
										
										//FIXME : Find the measureValues, may need some changes for last and calculated measures
										HashMap<String, Double> vals = findMeasureValues(mes, cell, rs, cubeInstance, cubeInstance.getDataLocator());
										
										if(values != null) {
											for(String uname : vals.keySet()) {
												cell.addValue(uname, vals.get(uname));
											}
											cell.getCalculation().makeCalculDuringQuery(false);
										}
									}
		//						}
							}
						}
					}
				}
			}
		}
		
		clearTablesInMemory();
		
		return storage;
		
	}
	
	@Override
	public OlapResult advancedDrillthrough(ICubeInstance cubeInstance, DrillThroughIdentifier dataCellId, Projection projection) throws Exception {
		
		//FIXME : may need to execute normal, projection or both drillThrough
		DataCellIdentifier2 identifier = recreateDataCellIdentifier(cubeInstance, dataCellId);
		
		//find if the original drillthrough is needed
		boolean doOriginalQuery = false;
		boolean doProjectionQuery = false;
		for(ElementDefinition elem : identifier.getIntersections()) {
			if(elem instanceof Member) {
				Member member = (Member) elem;
				if(member.getParentHierarchy().getParentDimension().isDate()) {
					if(member.isProjectionMember()) {
						doProjectionQuery = true;
						break;
					}
					else {
						int projectionLevelIndex = -1;
						int i = 0;
						for(Level l : member.getParentHierarchy().getLevels()) {
							if(l.getUname().equals(projection.getProjectionLevel())) {
								projectionLevelIndex = i;
								break;
							}
							i++;
						}
						int memberLevelIndex = member.getParentHierarchy().getLevels().indexOf(member.getParentLevel());
						if(projectionLevelIndex <= memberLevelIndex) {
							doOriginalQuery = true;
							break;
						}
						else {
							doOriginalQuery = true;
							if(member.hasProjectionMembers()) {
								doProjectionQuery = true;
							}
							
							break;
						}
						
					}
				}
			}
		}
		
		OlapResult resulta = ResultFactory.eINSTANCE.createOlapResult();
		if(doOriginalQuery) {
			resulta = super.advancedDrillthrough(cubeInstance, dataCellId, projection); 
		}
		
		if(doProjectionQuery) {
			
			if(!doOriginalQuery ) {
				ResultLine firstLine = ResultFactory.eINSTANCE.createResultLine();
				
				for(ElementDefinition elem : identifier.getIntersections()) {
					
					if(elem instanceof Member) {
						Member actualMember = (Member) elem;
						Hierarchy hiera = actualMember.getParentHierarchy();
						
						for(int i = 0 ; i < hiera.getLevels().size() ; i++) {
							String lvlName = hiera.getLevels().get(i).getName();
							ValueResultCell firstLineCell = new ValueResultCell(null, lvlName, "");
							firstLine.getCells().add(firstLineCell);
						}
					}
				}
				
				ValueResultCell firstLineCell = new ValueResultCell(null, identifier.getMeasure().getName(), "");
				firstLine.getCells().add(firstLineCell);
				
				resulta.getLines().add(firstLine);
			}
			
			Hierarchy hierarchy = null;
			for(Dimension dim : cubeInstance.getCube().getDimensions()) {
				if(dim.isDate()) {
					hierarchy = dim.getHierarchies().get(0);
					break;
				}
			}
			
			ProjectionHierarchyExtractor extr = new ProjectionHierarchyExtractor(hierarchy, projection, server);
			
			String measureUname = identifier.getMeasure().getUname();
			
			for(int i = 1 ;  ; i++) {
				
				List<CrossMembersValues> values = ProjectionSerializationHelper.deserialize(projection, i, measureUname);
				if(values == null) {
					break;
				}
				
				//Create the dataLocator, v is a sample value to find element indexes
				CrossMembersValues v = values.get(0);
				IDataLocator dataLocator = new ProjectionDataLocator(v, cubeInstance, projection);
				
				//Create the resultSet
				IResultSet rs = new ProjectionResultSet(values);
				
				//Create the dataMapper
				IDataMapper mapper = new ProjectionDataMapper(null, cubeInstance, dataLocator, runtimeContext);
				
				while(rs.next()) {
					
					if(mapper.checkWhereClause(rs, identifier.getWhereMembers())) {
						
						//test if the line is valid
						boolean isValidLine = true;
						for(ElementDefinition elem : identifier.getIntersections()) {
							
							if(elem instanceof Member && ((Member)elem).getParentLevel() != null) {
								Member actualMember = (Member) elem;
								
								if(actualMember.getParentLevel() instanceof ClosureLevel) {
									String[] memberParts = actualMember.getUname().split("\\.");
									String[] hieraParts = actualMember.getParentLevel().getParentHierarchy().getUname().split("\\.");
									
									int levelIndex = memberParts.length - hieraParts.length - 2;
									for(int j = 0 ; j < levelIndex + 1 ; j++) {
										int res = mapper.compareClosureMemberWithResultSetValue(actualMember, rs, levelIndex);
										if(res != 0) {
											isValidLine = false;
											break;
										}
									}
								}
								
								else {
									int levelIndex = actualMember.getParentLevel().getParentHierarchy().getLevels().indexOf(actualMember.getParentLevel());
									
									for(int j = 0 ; j < levelIndex + 1 ; j++) {
										int res = mapper.compareMemberWithResultSetValue(actualMember, j, rs);
										if(res != 0) {
											isValidLine = false;
											break;
										}
									}
								}
								if(!isValidLine) {
									break;
								}
							}
						}
						
						//create the result line
						if(isValidLine) {
							ResultLine line = ResultFactory.eINSTANCE.createResultLine();
							
							for(ElementDefinition elem : identifier.getIntersections()) {
								if(elem instanceof Member) {
								
									Member member = (Member) elem;
									Hierarchy hiera = member.getParentHierarchy();
									
									for(int j = 0 ; j < hiera.getLevels().size() ; j++) {
										
										int fkIndex = dataLocator.getResultSetIndex(hiera.getLevels().get(j));
										String fk = rs.getString(fkIndex + 1);
										
										String lvlValue = null;
										String[] unamePart = fk.split("\\]\\.\\[");
										if(member.getParentHierarchy().getParentDimension().isDate()) {
											lvlValue = extr.getMemberName(fk, j, runtimeContext);
										}
										else {
											lvlValue = unamePart[j+2].replace("[","").replace("]", "");
//											lvlValue = cubeInstance.getHierarchyExtractor(hiera).getMemberName(fk, j, runtimeContext);
										}
										
										ValueResultCell lineCell = new ValueResultCell(null, lvlValue, "");
										line.getCells().add(lineCell);
									}
									
								}
							}
				
							
							
	
							Double mesVal = rs.getDouble(1);
						
							if(mesVal != null) {
								ValueResultCell lineCell = new ValueResultCell(null, mesVal.toString(), "");
								line.getCells().add(lineCell);
							}
							
							else {
								continue;
							}
							
	
							resulta.getLines().add(line);
						}
					}
					
				}
			}
		}
		return resulta;
	}
	
	@Override	
	protected DataCellIdentifier2 recreateDataCellIdentifier(ICubeInstance cubeInstance, DrillThroughIdentifier drillThroughId) throws Exception {
		DataCellIdentifier2 identifier = RuntimeFactory.eINSTANCE.createDataCellIdentifier2();
		
		//find the intersection members
		LOOK:for(String inter : drillThroughId.getIntersections()) {
			for(Dimension dim : cubeInstance.getCube().getDimensions()) {
				for(Hierarchy hiera : dim.getHierarchies()) {
					if(inter.startsWith(hiera.getUname())) {
						Member mem = cubeInstance.getHierarchyExtractor(hiera).getMember(inter, runtimeContext);
						if(mem == null && dim.isDate()) {
							ProjectionHierarchyExtractor extr = new ProjectionHierarchyExtractor(hiera, projection, server);
							mem = extr.getMember(inter, runtimeContext);
						}
						identifier.addIntersection(mem);
						continue LOOK;
					}
				}
			}
		}
		
		//find the measure
		for(Measure mes : cubeInstance.getCube().getMeasures()) {
			if(mes.getUname().equals(drillThroughId.getMeasure())) {
				identifier.setMeasure(mes);
				break;
			}
		}
		
		//find where elements
		LOOK:for(String where : drillThroughId.getWheres()) {
			for(Dimension dim : cubeInstance.getCube().getDimensions()) {
				for(Hierarchy hiera : dim.getHierarchies()) {
					if(where.startsWith(hiera.getUname())) {
						Member mem = cubeInstance.getHierarchyExtractor(hiera).getMember(where, runtimeContext);
						identifier.addWhereMember(mem);
						continue LOOK;
					}
				}
			}
		}
		
		return identifier;
	}
}
