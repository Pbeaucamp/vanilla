package bpm.united.olap.runtime.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.united.olap.api.model.ElementDefinition;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.model.impl.ClosureLevel;
import bpm.united.olap.api.runtime.DataCell;
import bpm.united.olap.api.runtime.DataCellIdentifier2;
import bpm.united.olap.api.runtime.DataStorage;
import bpm.united.olap.api.runtime.MdxSet;
import bpm.united.olap.api.runtime.RunResult;
import bpm.united.olap.api.runtime.RuntimeFactory;
import bpm.united.olap.api.runtime.calculation.ICalculation;
import bpm.united.olap.runtime.parser.calculation.CalculationHelper;
import bpm.vanilla.platform.logging.IVanillaLogger;

public class DataStorageGenerator implements IDataStorageGenerator {
	private RuntimeFactory factory = RuntimeFactory.eINSTANCE;
//	private List<List<List<NodeId>>> possibleIds = new ArrayList<List<List<NodeId>>>();
	
	private DataStorage dataStorage;
	private IVanillaLogger logger;
	
	@Override
	public DataStorage generateDataStorage(RunResult result, IVanillaLogger logger) {
		this.logger = logger;
		dataStorage = factory.createDataStorage();
		dataStorage.setIsMeasureRow(result.getIsMeasureRow());
		
		List<List<MdxSet>> axis = result.getMdxSets();
		List<MdxSet> wheres = result.getWhereSets();
		if(wheres != null && wheres.size() > 1) {
			dataStorage.setAsMultipleWhere(true);
		}
		
		dataStorage.setMaxNbCol(axis.get(1).size());
		dataStorage.initSize(axis.size(), axis.get(1).size());
		//cross between rows and cols
		for(int i = 0 ; i < axis.get(0).size() ; i++ ) {
			MdxSet rowSet = axis.get(0).get(i);
			
			for(int j = 0 ; j < axis.get(1).size() ; j++) {
				MdxSet colSet = axis.get(1).get(j);
				Measure actualMes = null;
				
				//create the cell identifier
				DataCellIdentifier2 identifier2 = factory.createDataCellIdentifier2();
				
//				//create the id's map
//				List<List<NodeId>> ids = new ArrayList<List<NodeId>>();
				List<ElementDefinition> defs = new ArrayList<ElementDefinition>();
				defs.addAll(rowSet.getElements());
				defs.addAll(colSet.getElements());
//				int k = 0;
//				int mesIndex = 0;
				if (rowSet.getMeasure() != null){
					actualMes = rowSet.getMeasure();
				}
				else if (colSet.getMeasure() != null){
					actualMes = colSet.getMeasure();
				}
				
				if(!dataStorage.getMeasures().contains(actualMes)) {
					dataStorage.getMeasures().add(actualMes);
				}
				identifier2.setMeasure(actualMes);
				
				identifier2.setIntersections(defs);
				
//				for(ElementDefinition el : defs) {
//					
//					if (el instanceof Measure){
//						actualMes = (Measure)el;
//						if(!dataStorage.getMeasures().contains(actualMes)) {
//							dataStorage.getMeasures().add(actualMes);
//						}
//						identifier2.setMeasure(actualMes);
//					}
//					else {
//						identifier2.addIntersection(el);
//					}
//				}
				if(wheres != null && wheres.size() > 0) {
					for(MdxSet where : wheres) {
						for(ElementDefinition el : where.getElements()) {
							identifier2.addWhereMember((Member) el);
						}
					}
				
				}
				
				
				
				//create the cell and add it to the dataStorage
				DataCell cell = factory.createDataCell();
				cell.setIdentifier2(identifier2);
				cell.setRow(i);
				cell.setCol(j);
				
				ICalculation calculation = CalculationHelper.getCalculation(cell, actualMes, defs, logger);
				
//				createPossibleId(ids);
				
				cell.setCalculation(calculation);
				
				dataStorage.add(cell);
			}
		}
		
		return dataStorage;
	}



	private boolean isIdsEquals(DataCellIdentifier2 id1, DataCellIdentifier2 id2) {
		
		if(id1.getIntersections().size() == id2.getIntersections().size()) {
			for(int i = 0 ; i < id1.getIntersections().size() ; i++) {
				if(!(id1.getIntersections().get(i).getUname().split("\\.").length == id2.getIntersections().get(i).getUname().split("\\.").length)) {
					return false;
				}
			}
		}
		else {
			return false;
		}
		
		if(id1.getWhereMembers().size() == id2.getWhereMembers().size()) {
			for(int i = 0 ; i < id1.getWhereMembers().size() ; i++) {
				if(!(id1.getWhereMembers().get(i).getUname().split("\\.").length == id2.getWhereMembers().get(i).getUname().split("\\.").length)) {
					return false;
				}
			}
		}
		else {
			return false;
		}
		
		if(!(id1.getMeasure().getUname().equals(id2.getMeasure().getUname()))) {
			return false;
		}
		
		return true;
	}


	private boolean matchStructure(DataCellIdentifier2 id1, DataCellIdentifier2 id2){
		if(id1.getIntersections().size() == id2.getIntersections().size()) {
			for(int i = 0 ; i < id1.getIntersections().size() ; i++) {
				if(((Member)id1.getIntersections().get(i)).getParentLevel() instanceof ClosureLevel && ((Member)id2.getIntersections().get(i)).getParentLevel() instanceof ClosureLevel){
					int p1 = ((Member)id1.getIntersections().get(i)).getUname().split("\\.").length;
					int p2 = ((Member)id2.getIntersections().get(i)).getUname().split("\\.").length;
					if (p1 != p2){
						return false;
					}
				}
				if (((Member)id1.getIntersections().get(i)).getParentLevel() != ((Member)id2.getIntersections().get(i)).getParentLevel()){
					return false;
				}
			}
		}
		else{
			return false;
		}
		
		if(id1.getWhereMembers().size() == id2.getWhereMembers().size()) {
			for(int i = 0 ; i < id1.getWhereMembers().size() ; i++) {
				if(((Member)id1.getWhereMembers().get(i)).getParentLevel() instanceof ClosureLevel && ((Member)id2.getWhereMembers().get(i)).getParentLevel() instanceof ClosureLevel){
					int p1 = ((Member)id1.getWhereMembers().get(i)).getUname().split("\\.").length;
					int p2 = ((Member)id2.getWhereMembers().get(i)).getUname().split("\\.").length;
					if (p1 != p2){
						return false;
					}
				}
				if(!(id1.getWhereMembers().get(i).getUname().split("\\.").length == id2.getWhereMembers().get(i).getUname().split("\\.").length)) {
					return false;
				}
			}
		}
		else {
			return false;
		}
		
		if(!(id1.getMeasure().getUname().equals(id2.getMeasure().getUname()))) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * create all the possible ids
	 *  the key is used to know the CrossStructure (aka Measure + Levels + wheres)
	 *  the value contains all identifiers that match this structure
	 * @param allNodeIds
	 * @return
	 */
	@Override
	public HashMap<DataCellIdentifier2, List<DataCellIdentifier2>> createPossibleId(List<DataCell> cells) {
		HashMap<DataCellIdentifier2, List<DataCellIdentifier2>> possibleIds = new HashMap<DataCellIdentifier2, List<DataCellIdentifier2>>();
		for(DataCell cell : cells) {
			boolean exists = false;
			DataCellIdentifier2 key = null;
			
			DataCellIdentifier2 id = cell.getIdentifier2();
			
						
			for(DataCellIdentifier2 posId : possibleIds.keySet()) {
				if(matchStructure(id, posId)) {
					exists = true;
					key = posId;
					break;
				}
			}
			
			if(!exists) {
				DataCellIdentifier2 possibleId = factory.createDataCellIdentifier2();
				
				for(ElementDefinition def : id.getIntersections()) {
					ElementDefinition posElem = def;
					possibleId.addIntersection(posElem);
				}
				for(Member mem : id.getWhereMembers()) {
					Member posWhere = mem;
					possibleId.addWhereMember(posWhere);
				}
				possibleId.setMeasure(id.getMeasure());
				possibleIds.put(possibleId, new ArrayList<DataCellIdentifier2>());
				possibleIds.get(possibleId).add(possibleId);
			}
			else{
				 
				possibleIds.get(key).add(id);
			}
			
		}
		return possibleIds;
	}


	@Override
	public int findNbCol(List<DataCell> dataCells) {
		int count = 1;
		if(dataCells != null && dataCells.size() > 0) {
			int minCol = dataCells.get(0).getCol();
			for(int i = 1 ; i < dataCells.size() ; i++) {
				int colNumber = dataCells.get(i).getCol();
				if(colNumber > minCol) {
					count++;
				}
				else {
					return count;
				}
			}
		}
		return count;
	}

}
