package bpm.united.olap.runtime.result;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.united.olap.api.model.ElementDefinition;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.result.OlapResult;
import bpm.united.olap.api.result.ResultCell;
import bpm.united.olap.api.result.ResultFactory;
import bpm.united.olap.api.result.ResultLine;
import bpm.united.olap.api.result.impl.EmptyResultCell;
import bpm.united.olap.api.result.impl.ItemResultCell;
import bpm.united.olap.api.result.impl.ValueResultCell;
import bpm.united.olap.api.runtime.DataCell;
import bpm.united.olap.api.runtime.MdxSet;
import bpm.united.olap.api.runtime.RunResult;
import bpm.united.olap.runtime.formatter.FormatterFactory;

/**
 * A class which hierarchize the result
 * @author Marc Lanquetin
 *
 */
public class HierarchizedOlapResultGenerator implements IOlapResultGenerator {

	private ResultFactory factory;
	
	private List<List<Integer>> shifts;
	private List<Integer> maxShifts;
	
	private int startCol;
	private int startRow;
	
	private RunResult runResult;
	
	@Override
	public OlapResult generateOlapResult(RunResult runResult) {
		this.runResult = runResult;
		factory = ResultFactory.eINSTANCE;
		OlapResult result = factory.createOlapResult();
		
		result.setNbMeasures(runResult.getDataStorage().getMeasures().size());
		
		List<ResultLine> lines = generateRows(runResult);
		
		for(Integer sh : maxShifts) {
			startCol += sh + 1;
		}
		startCol = startCol + 1;
		
		List<ResultLine> cols = generateCols(runResult);
		
		lines.addAll(0, cols);
		
		if(runResult.isIsNonEmpty()) {
			removeEmptyCols(lines);
		}
		
		result.getLines().addAll(lines);
		
		result = removeDoublonLabels(result);
		
		return result;
	}

	private List<ResultLine> generateCols(RunResult runResult) {
		List<ResultLine> result = new ArrayList<ResultLine>();
		
		List<MdxSet> colSets = runResult.getMdxSets().get(1);
		
		findShifts(colSets);
		
		int nbLines = 0;
		for(Integer sh : maxShifts) {
			nbLines += sh + 1;
		}
		
		if(colSets.get(0).getMeasure() != null) {
//			nbLines+=1;
			startCol-=1;
		}
		
		//fill the top left empty cells
		for(int i = 0 ; i < nbLines ; i ++) {
			ResultLine line = factory.createResultLine();
			for(int j = 0 ; j < startCol ; j++) {
				line.getCells().add(new EmptyResultCell());
			}
			result.add(line);
			startRow++;
		}
		
		//fill the elementsDefinitions
		for(int j = 0 ; j < colSets.size() ; j++) {
			
			if(!colSets.get(j).isIsVisible()) {
				continue;
			}
			
			List<Integer> shift = shifts.get(j);
			int decal = 0;
			for(int i = 0 ; i < colSets.get(j).getElements().size() ; i++) {
				
				for(int k = decal ; k < decal + maxShifts.get(i) + 1 ; k++) {
					
					ResultCell cell = null;
					
					if(shift.get(i) + decal == k) {
						String uname = null;
						if(colSets.get(j).getElements().get(i) instanceof Member) {
							uname = ((Member)colSets.get(j).getElements().get(i)).getMemberRelationsUname();
						}
						else {
							uname = colSets.get(j).getElements().get(i).getUname();
						}
						cell = new ItemResultCell(colSets.get(j).getElements().get(i));
					}
					
					else {
						cell = new EmptyResultCell();
					}
					
					result.get(k).getCells().add(cell);
				}
				decal += maxShifts.get(i) + 1;
			}
			
		}
		
		if(colSets.get(0).getMeasure() != null) {
//			startRow++;
			ResultLine line = factory.createResultLine();
			for(int j = 0 ; j < startCol ; j++) {
				line.getCells().add(new EmptyResultCell());
			}
			
			Measure previousMes = null;
			
			for(int j = 0 ; j < colSets.size() ; j++) {
				
				if(colSets.get(j).getMeasure().equals(previousMes)) {
					line.getCells().add(new EmptyResultCell());
				}
				else {
					line.getCells().add(new ItemResultCell(colSets.get(j).getMeasure()));
					previousMes = colSets.get(j).getMeasure();
				}
			}
			//result.add(line);
			//XXX
			result.add(startRow, line);
		}
		
		return result;
	}

	private List<ResultLine> generateRows(RunResult runResult) {
		List<ResultLine> result = new ArrayList<ResultLine>();
		
		List<MdxSet> rowSets = runResult.getMdxSets().get(0);
		int nbCol = runResult.getMdxSets().get(1).size();
		
		findShifts(rowSets);
		
		//create the lineResults
		for(int i = 0 ; i < rowSets.size() ; i++) {
			
			if(!rowSets.get(i).isIsVisible()) {
				continue;
			}
			
			ResultLine line = factory.createResultLine();
			MdxSet set = rowSets.get(i);
			List<Integer> shift = shifts.get(i);
			
			//set the elementsDefinitions
			for(int j = 0 ; j < set.getElements().size() ; j++) {
				
				for(int k = 0 ; k <= maxShifts.get(j) ; k++) {
					
					ResultCell cell = null;
					if(shift.get(j) == k) {
						String uname = null;
						if(set.getElements().get(j) instanceof Member) {
							uname = ((Member)set.getElements().get(j)).getMemberRelationsUname();
						}
						else {
							uname = set.getElements().get(j).getUname();
						}
						cell = new ItemResultCell(set.getElements().get(j));
					}
					else {
						cell = new EmptyResultCell();
					}
					line.getCells().add(cell);
				}
			}
			if(set.getMeasure() != null) {
				line.getCells().add(new ItemResultCell(set.getMeasure()));
			}
			
			//set the values
			boolean asValues = false;
			for(int j = 0 ; j < nbCol ; j++) {
				
				int cellId = j + (i*nbCol);
				ResultCell resCell = null;
				if(runResult.getDataStorage().getDataCells().get(cellId).getResultValue() != null) {
					
					 DataCell cell = runResult.getDataStorage().getDataCells().get(cellId);
					
					 if(cell.getFormat() != null) {
						 resCell = new ValueResultCell(cell.getResultValue(), FormatterFactory.getFormatter(cell.getFormat()).formatValue(cell.getResultValue()), cell.getIdentifier2().getDrillThroughIdentifier(), ""+cellId, cell.getIdentifier2().getExternalQueryIdentifier());						 
					 }
					 else {
						 resCell = new ValueResultCell(cell.getResultValue(), NumberFormat.getInstance().format(cell.getResultValue()), cell.getIdentifier2().getDrillThroughIdentifier(), ""+cellId, cell.getIdentifier2().getExternalQueryIdentifier());
					 }
					 asValues = true;
				}
				else {
					DataCell cell = runResult.getDataStorage().getDataCells().get(cellId);
					resCell = new ValueResultCell(cell.getResultValue(), null, ""+cellId, cell.getIdentifier2().getExternalQueryIdentifier());
				}
				
				line.getCells().add(resCell);
				
			}
			
			if(!runResult.isIsNonEmpty() || (runResult.isIsNonEmpty() && asValues)) {
				result.add(line);
			}
		}
		
		return result;
	}
	
	/**
	 * find the shifts between elements 
	 * @param sets
	 */
	private void findShifts(List<MdxSet> sets) {
		//find the shifts for each element
		shifts = new ArrayList<List<Integer>>();
		maxShifts = new ArrayList<Integer>();
		
		List<Integer> previousSizes = null;
		List<Integer> previousShifts = null;
		
		
		for(MdxSet set : sets) {
			
			List<Integer> shift = new ArrayList<Integer>();
			List<Integer> size = new ArrayList<Integer>();
			
			for(int i = 0 ; i < set.getElements().size() ; i++) {
				
				ElementDefinition elem = set.getElements().get(i);
				
				if(!elem.isIsVisible()) {
					continue;
				}
				
				if(previousShifts == null) {
					shift.add(0);
					if(elem instanceof Member) {
						size.add(((Member)elem).getMemberRelationsUname().split("\\]\\.\\[").length);
					}
					else {
						size.add(elem.getUname().split("\\]\\.\\[").length);
					}
					maxShifts.add(0);					
				}
				
				else {
					if(elem instanceof Member) {
						size.add(((Member)elem).getMemberRelationsUname().split("\\]\\.\\[").length);
					}
					else {
						size.add(elem.getUname().split("\\]\\.\\[").length);
					}
					
					
					if(size.get(i) > previousSizes.get(i)) {
						shift.add(previousShifts.get(i) + (size.get(i) - previousSizes.get(i)));
					}
					else if(size.get(i) < previousSizes.get(i)) {
						shift.add(previousShifts.get(i) + (size.get(i) - previousSizes.get(i)));
					}
					else {
						shift.add(previousShifts.get(i));
					}
					if(maxShifts.get(i) < shift.get(i)) {
						maxShifts.set(i, shift.get(i));
					}
				}
				
			}
			previousShifts = shift;
			previousSizes = size;
			
			shifts.add(shift);
		}
	}

	private void removeEmptyCols(List<ResultLine> lines) {
		int j = startCol;
		int colNumber = lines.get(0).getCells().size();
		while(j < colNumber) {
			boolean asValues = false;
			for(ResultLine line : lines) {
				ResultCell cell = line.getCells().get(j);
				if(cell instanceof ValueResultCell) {
					if(!(((ValueResultCell)cell).getFormatedValue() == null || ((ValueResultCell)cell).getFormatedValue().equalsIgnoreCase(""))) {
						asValues = true;
						break;
					}
				}
			}
			if(!asValues) {
				for(ResultLine line : lines) {
					line.getCells().remove(j);
				}
				colNumber--;
			}
			else {
				j++;
			}
		}
	}

	private OlapResult removeDoublonLabels(OlapResult result) {
		
		HashMap<Integer, String> previousLabel = new HashMap<Integer, String>();
		for(int i = startRow ; i < result.getLines().size() ; i++) {
			for(int j = 0 ; j < startCol ; j++) {
				ResultCell cell = result.getLines().get(i).getCells().get(j);
				if(cell instanceof ItemResultCell) {
					if(previousLabel.get(j) != null && previousLabel.get(j).equals(((ItemResultCell)cell).getName())) {
						result.getLines().get(i).getCells().set(j, new EmptyResultCell());
					}
					previousLabel.put(j, ((ItemResultCell)cell).getName());
				}
				else {
					previousLabel.put(j, "");
				}
			}
		}
		
		for(int i = 0 ; i < startRow ; i++) {
			ResultCell previousCell = null;
			for(int j = startCol ; j < result.getLines().get(i).getCells().size() ; j++) {
				ResultCell cell = result.getLines().get(i).getCells().get(j);
				if(cell instanceof ItemResultCell && previousCell instanceof ItemResultCell) {
					if(previousCell != null && ((ItemResultCell)previousCell).getName().equals(((ItemResultCell)cell).getName())) {
						result.getLines().get(i).getCells().set(j, new EmptyResultCell());
					}
				}
				previousCell = cell;
			}
		}
		
		return result;
	}
}
