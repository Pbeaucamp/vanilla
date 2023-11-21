package bpm.gateway.runtime2.transformation.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bpm.gateway.core.transformations.ConsistencyMapping;
import bpm.gateway.core.transformations.ConsitencyTransformation;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

public class RunConsistency extends RuntimeStep{

	private ConsitencyTransformation transfo;
	
	private boolean sortedData = false;

	private RuntimeStep trashOutput;
	
	private int processedRows = 0;

	public RunConsistency(ConsitencyTransformation transfo, int bufferSize) {
		super(null, transfo, bufferSize);
		this.transfo = transfo;
	}
	
	@Override
	public void performRow() throws Exception {
		//check if all the inputs are done
		if (areInputStepAllProcessed()){
			if (inputEmpty()){
				setEnd();
			}
		}
		
		if (isEnd() && inputEmpty()){
			return;
		}
		
		if (!isEnd() && inputEmpty()){
			try{
				Thread.sleep(10);
				return;
			}catch(Exception e){
				
			}
		}
		
		//we wait for the mapping inputs to finish
		//we need all the data to lookup
		while(!inputFinished()) {
			try{
				Thread.sleep(50);
			}catch(Exception e){
				
			}
		}
		
		Row row = readRow();
		
		//if it's the first line from the master data, we need to sort the mapping data
		if(!sortedData) {
			for(ConsistencyMapping mapping : transfo.getConsistencyMappings()) {
				if(mapping.getValues() == null || mapping.getValues().isEmpty()) {
					mapping.sortValues();
				}
			}
			sortedData = true;
		}
		
		//we look for a match for all mapping and keep the mapping values if needed
		boolean found = true;
		List<Row> foundRows = new ArrayList<Row>();
		for(ConsistencyMapping mapping : transfo.getConsistencyMappings()) {
			int foundIndex = Collections.binarySearch(mapping.getValues(), row, mapping.getMappingComparator());
			if(foundIndex >= 0 && mapping.isKeepInOutput()) {
				foundRows.add(mapping.getValues().get(foundIndex));
			}
			else {
				found = false;
				break;
			}
		} 
		
		//if there's a match respecting all mappings, we create the output row
		if(found) {
			Row newRow = RowFactory.createRow(this);
			int rowIndex = 0;
			for(;rowIndex < row.getMeta().getSize() ; rowIndex++) {
				newRow.set(rowIndex, row.get(rowIndex));
			}
			int i = 0;
			for(ConsistencyMapping mapping : transfo.getConsistencyMappings()) {
				if(mapping.isKeepInOutput()) {
					Row mappingRow = foundRows.get(i);
					for(int k = 0 ; k < mappingRow.getMeta().getSize() ; k++) {
						newRow.set(rowIndex, mappingRow.get(k));
						rowIndex++;
					}
					i++;
				}
			}
			for(RuntimeStep r : getOutputs()){
				if (r != trashOutput){
					r.insertRow(row, this);
				}
			}
			processedRows++;
		}
		
		//if there's no match
		else {
			trashOutput.insertRow(row, this);
			processedRows++;
		}
	}

	private boolean inputFinished() throws InterruptedException {
		for(RuntimeStep i : inputs){
			if ((!i.isEnd() || !i.inputEmpty()) && !i.getTransformation().equals(transfo.getMasterInput())){
				return false;
			}
		}
		return true;
	}

	@Override
	public void releaseResources() {
		for(ConsistencyMapping mapping : transfo.getConsistencyMappings()) {
			mapping.clearValues();
		}
		
	}

	@Override
	public void init(Object adapter) throws Exception {
		processedRows = 0;
		for(RuntimeStep rs : getOutputs()){
			if (rs.getTransformation() == transfo.getTrashTransformation()){
				trashOutput = rs;
			}
		}
	}
	
	@Override
	public void insertRow(final Row data, final RuntimeStep caller) throws InterruptedException {
		boolean mapTransfo = false;
		for(ConsistencyMapping mapping : transfo.getConsistencyMappings()) {
			if(mapping.getInput().getName().equals(caller.getTransformation().getName())) {
				mapping.addValue(data);
				mapTransfo = true;
				break;
			}
		}
		
		if(!mapTransfo) {
			super.insertRow(data, caller);
		}
	}

	@Override
	public long getStatsProcessedRows() {
		return processedRows;
	}
}
