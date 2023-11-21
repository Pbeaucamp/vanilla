package bpm.united.olap.runtime.parser.calculation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import bpm.united.olap.api.datasource.DataObjectItem;
import bpm.united.olap.api.model.ElementDefinition;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.runtime.DataCell;
import bpm.united.olap.api.runtime.MdxSet;
import bpm.united.olap.api.runtime.RunResult;
import bpm.united.olap.api.runtime.calculation.ICalculation;

public class TopCountCalculation implements ICalculation {

	private List<MdxSet> sets;
	private int count;
	private Measure measure;
	private boolean isTop;
	private RunResult runResult;
	private boolean isPercentile = false;
	
	public TopCountCalculation(List<MdxSet> sets, int count, Measure measure, boolean isTop) {
		this.count = count;
		this.sets = sets;
		this.measure = measure;
		this.isTop = isTop;
	}
	
	@Override
	public void makeCalcul() {
		
		List<MdxSet> axis = null;
		
		List<List<MdxSet>> correspondingSets = new ArrayList<List<MdxSet>>();
		List<List<DataCell>> correspondingCells = new ArrayList<List<DataCell>>();
		
		HashMap<String, Integer> mappingSets = new HashMap<String, Integer>();
		
		for(int r = 0 ; r < runResult.getMdxSets().size() ; r++) {
			
			boolean isAxis = false;
			
			List<MdxSet> ax = runResult.getMdxSets().get(r);
			//find concerned sets
			for(int i = 0 ; i < runResult.getMdxSets().get(r).size() ; i++) {
				
				for(MdxSet set : this.sets) {
					
					boolean isIn = isCorrespondingSets(set, ax.get(i), mappingSets, correspondingSets, correspondingCells, r == 0 ? true : false, i);
					
					if(isIn) {
						isAxis = true;
						axis = ax;
					}
				}
			}
			
			
			if(isAxis) {
				break;
			}
		}
		
		//find the items to remove
		List<Integer> keepIndex = findTopCount(correspondingCells);
		for(int i = 0 ; i < correspondingSets.size() ; i++) {
			if(!keepIndex.contains(i)) {
				axis.removeAll(correspondingSets.get(i));
				runResult.getDataStorage().getDataCells().removeAll(correspondingCells.get(i));
			}
		}
		
	}

	private List<Integer> findTopCount(List<List<DataCell>> correspondingCells) {
		
		List<Integer> elementToKeep = new ArrayList<Integer>();
		HashMap<Integer, Double> mapValueIndex = new HashMap<Integer, Double>();
		for(int i = 0 ; i < correspondingCells.size() ; i++) {
			
			List<DataCell> cells = correspondingCells.get(i);
			double value = 0.0;
			for(DataCell cell : cells) {
				if(cell.getResultValue() != null) {
					value += cell.getResultValue();
				}
			}
			
			mapValueIndex.put(i, value);
			
		}
		
		if(count > correspondingCells.size()) {
			elementToKeep.addAll(mapValueIndex.keySet());
			return elementToKeep;
		}
			
		int nbElemToRmove = correspondingCells.size() - count;
		if(isTop) {
			for(int i = 0 ; i < nbElemToRmove ; i++) {
				double min = Collections.min(mapValueIndex.values());
				for(Integer key : mapValueIndex.keySet()) {
					if(mapValueIndex.get(key).equals(min)) {
						mapValueIndex.remove(key);
						break;
					}
				}
				
			}
		}
		
		else {
			for(int i = 0 ; i < nbElemToRmove ; i++) {
				double max = Collections.max(mapValueIndex.values());
				for(Integer key : mapValueIndex.keySet()) {
					if(mapValueIndex.get(key).equals(max)) {
						mapValueIndex.remove(key);
						break;
					}
				}
			}
		}
		
		elementToKeep.addAll(mapValueIndex.keySet());
		return elementToKeep;
	}

	private boolean isCorrespondingSets(MdxSet calculSet, MdxSet resultSet, HashMap<String, Integer> mappingSets, List<List<MdxSet>> correspondingSets, List<List<DataCell>> correspondingCells, boolean isRow, int index) {
		
		String resSetUname= "";
		String calculSetUname = "";
		for(ElementDefinition def : resultSet.getElements()) {
			resSetUname += def.getUname();
		}
		if (resultSet.getMeasure() != null){
			resSetUname += resultSet.getMeasure().getUname();
		}
		
		
		for(ElementDefinition def : calculSet.getElements()) {
			calculSetUname += def.getUname();
		}
		if (calculSet.getMeasure() !=null){
			calculSetUname += calculSet.getMeasure().getUname();
		}
		
		
		//if its the right measure
		if(!runResult.getDataStorage().getDataCells().get(index).getCalculation().getMeasure().getName().equals(measure.getName())) {
			return false;
		}
		
		//if its corresponds
		if(resSetUname.contains(calculSetUname)) {
			int mappingIndex;
			if(mappingSets.containsKey(calculSetUname)) {
				mappingIndex = mappingSets.get(calculSetUname);
			}
			else {
				mappingIndex = mappingSets.size();
				mappingSets.put(calculSetUname, mappingSets.size());
			}
			
			List<DataCell> cells = new ArrayList<DataCell>();
			
			//find cells
			if(isRow) {
				for(int i = 0 ; i < runResult.getMdxSets().get(1).size() ; i++) {
					cells.add(runResult.getDataStorage().getDataCells().get(i + (runResult.getMdxSets().get(1).size() * index)));
				}
			}
			
			else {
				for(int i = 0 ; i < runResult.getMdxSets().get(0).size() ; i++) {
					cells.add(runResult.getDataStorage().getDataCells().get(index + (runResult.getMdxSets().get(1).size() * i)));
				}
			}
			
			//put elements in listes
			if(correspondingSets.size() > mappingIndex) {
				correspondingSets.get(mappingIndex).add(resultSet);
				correspondingCells.get(mappingIndex).addAll(cells);
			}
			
			else {
				List<MdxSet> set = new ArrayList<MdxSet>();
				set.add(resultSet);
				correspondingSets.add(set);
				correspondingCells.add(cells);
			}
			return true;
		}
		
		return false;
	}

	public void setRunResult(RunResult runResult) {
		this.runResult = runResult;
	}

	@Override
	public Measure getMeasure() {
		return this.measure;
	}

	@Override
	public void makeCalculDuringQuery(boolean isOnImprovedQuery) {
		
		
	}

	@Override
	public DataCell getDataCell() {
		return null;
	}

	@Override
	public boolean isPercentile() {
		return isPercentile;
	}

	@Override
	public void setIsPercentile(boolean isPercentile) {
		this.isPercentile = isPercentile;
	}
	
	public RunResult getRunResult() {
		return runResult;
	}

	@Override
	public void setItem(DataObjectItem item) {
		
		
	}
}