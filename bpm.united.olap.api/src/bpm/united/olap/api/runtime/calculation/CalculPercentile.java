package bpm.united.olap.api.runtime.calculation;

import java.util.List;

import bpm.united.olap.api.model.ElementDefinition;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.runtime.DataCell;
import bpm.united.olap.api.runtime.DataStorage;
import bpm.united.olap.api.runtime.MdxSet;
import bpm.united.olap.api.runtime.RunResult;

/**
 * A tool class for percentile calculations
 * @author Marc Lanquetin
 *
 */
public class CalculPercentile {

	/**
	 * Find the percentile value for a cell
	 * @param cell
	 * @param runResult
	 * @return
	 */
	public static Double getPercentileValue(DataCell cell, RunResult runResult) {
		
		DataStorage ds = runResult.getDataStorage();
		
		int measureSize = ds.getMeasures().size();
		
		//find the total for this cell
		int cellIndex = cell.getRow() * runResult.getMdxSets().get(0).size() + cell.getCol();
		
		boolean onCol = false;
		List<MdxSet> sets = runResult.getMdxSets().get(1);
		for(MdxSet set : sets) {
//			for(ElementDefinition el : set.getElements()) {
//				if(el instanceof Measure) {
//					onCol = true;
//					break;
//				}
//			}
			if (set.getMeasure() != null){
				onCol = true;
				break;
			}
			break;
		}
		DataCell totalCell = null;
		if(onCol) {
			totalCell = ds.getDataCells().get(cellIndex % measureSize);
		}
		else {
			totalCell = ds.getDataCells().get((cell.getRow() % measureSize) * runResult.getMdxSets().get(1).size());
		}
		
		//make the calcul
		if(totalCell.getResultValue() != null && cell.getResultValue() != null) {
			if(totalCell.getPersistedValue() == null) {
				totalCell.persistValue(totalCell.getResultValue().doubleValue());
			}
			Double cellValue = null;
			if(cell.getPersistedValue() != null) {
				cellValue = cell.getPersistedValue();
			}
			else {
				cellValue = cell.getResultValue();
			}
			Double res = cellValue / totalCell.getPersistedValue();
			
			return res;
		}
		
		return null;
	}
	
}
