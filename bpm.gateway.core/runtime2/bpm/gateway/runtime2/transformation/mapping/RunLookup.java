package bpm.gateway.runtime2.transformation.mapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bpm.gateway.core.transformations.Lookup;
import bpm.gateway.core.transformations.SimpleMappingTransformation;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;
import bpm.gateway.runtime2.tools.LookUpRow;
import bpm.gateway.runtime2.tools.LookupDataSorter;

public class RunLookup extends RunMapping {

	private List<Row> matchedRows = new ArrayList<Row>();
	private RuntimeStep trashOutput;
	private boolean keepNonMatchRows = false;
	private boolean trashNonMatchRows = false;
	private Integer[] masterMap;

	public RunLookup() {}

	public RunLookup(Lookup transformation, int bufferSize) {
		super(transformation, bufferSize);
	}

	@Override
	public void releaseResources() {
		matchedRows.clear();
		matchedRows = null;
		super.releaseResources();
	}

	@Override
	public void init(Object adapter) throws Exception {
		keepNonMatchRows = !((Lookup) getTransformation()).isRemoveRowsWithoutLookupMatching();
		trashNonMatchRows = ((Lookup) getTransformation()).isTrashRowsWithoutLookupMatching();
		for(RuntimeStep rs : getOutputs()) {
			if(rs.getTransformation() == ((Lookup) getTransformation()).getTrashTransformation()) {
				trashOutput = rs;
			}
		}

		if(inputs.size() != 2) {
			throw new Exception(getName() + " have less than two inputs");
		}
		else if(inputs.get(0) == null || inputs.get(1) == null) {
			throw new Exception(getName() + " one of two requested input is null");
		}

		/*
		 * read this reckless youngster before making any changes there maps is the mapping between the 2 SimpleMappingTransformation's inputs It has the following structure key = SimpleMappingTransformation.getInput(0) streamelement name; value = SimpleMappingTransformation.getInput(1) streamelement name The SimpleMappingTransformation output fields are SimpleMappingTransformation's masterStep fields + SimpleMappingTransformation's other step fields The masterStep can be either of the SimpleMappingTransformation's inputs. The masterMap[] is efined as follow : - index : the position of the field from the primaryStep - value : the position of the field from the secondaryStep(lookup table) or null The boolean isPrimaryFirstInput is at true if the primaryStep is the one coming from SimpleTransformation.getInput(0) step definition. For some reason we can't assume that the this.getInputs(0).getTransformation() == this.getTransformation().getInputs().get(0) (if you want to kno you will have to dig into the RuntimeStep tree construction)
		 */
		boolean isPrimaryFirstInput = false;

		if(((SimpleMappingTransformation) getTransformation()).isMaster(inputs.get(0).getTransformation())) {
			secondaryStep = inputs.get(1);
			primaryStep = inputs.get(0);
		}
		else {
			secondaryStep = inputs.get(0);
			primaryStep = inputs.get(1);
		}
		if(getTransformation().getInputs().get(0) == primaryStep.getTransformation()) {
			isPrimaryFirstInput = true;
		}
		masterMap = new Integer[primaryStep.getTransformation().getDescriptor(getTransformation()).getColumnCount()];
		HashMap<String, String> maps = ((SimpleMappingTransformation) getTransformation()).getMappings();

		for(String s : maps.keySet()) {
			Integer pos = null;
			Integer value = null;

			if(maps.get(s) == null) {
				continue;
			}

			String primaryColName = null;
			String secondaryName = null;

			if(isPrimaryFirstInput) {
				primaryColName = s;
				secondaryName = maps.get(s);
			}
			else {
				primaryColName = maps.get(s);
				secondaryName = s;
			}

			for(int i = 0; i < primaryStep.getTransformation().getDescriptor(getTransformation()).getColumnCount(); i++) {
				if(primaryStep.getTransformation().getDescriptor(getTransformation()).getColumnName(i).equals(primaryColName)) {
					pos = i;
					break;
				}
			}
			for(int i = 0; i < secondaryStep.getTransformation().getDescriptor(getTransformation()).getColumnCount(); i++) {
				if(secondaryStep.getTransformation().getDescriptor(getTransformation()).getColumnName(i).equals(secondaryName)) {
					value = i;
					break;
				}
			}

			if(pos != null && value != null) {
				masterMap[pos] = value;
			}
		}

		isInited = true;
		info(" inited");

	}

	private HashMap<Integer, List<LookUpRow>> sortedMap;
	private List<Row> sortedRows;

	private int rechercheDichotomique(Object val, int indDebut, int indFin, List<Row> zone, int index) {
		if(indDebut > indFin) {
			return -1;
		}
		else {
			try {
				int pivot = indDebut + ((indFin - indDebut) / 2);
				if(val.toString().equals(zone.get(pivot).get(index).toString()))
					return pivot;
				else {
					if(val.toString().compareTo(zone.get(pivot).get(index).toString()) < 0) {
						return rechercheDichotomique(val, indDebut, pivot - 1, zone, index);
					}
					else {
						return rechercheDichotomique(val, pivot + 1, indFin, zone, index);
					}
				}
			} catch(Exception e) {
				return -1;
			}
		}
	}

	@Override
	public void performRow() throws Exception {
		while(!(secondaryStep.isEnd() && secondaryStep.inputEmpty())) {
			try {
				Thread.sleep(50);
			} catch(Exception e) {

			}

		}
		if(areInputStepAllProcessed()) {
			if(inputEmpty()) {
				setEnd();
			}
		}

		if(isEnd() && inputEmpty()) {
			return;
		}

		if(!isEnd() && inputEmpty()) {
			try {
				Thread.sleep(10);
				return;
			} catch(Exception e) {

			}
		}

		Row row = readRow();
//		Date start = new Date();
		// sort datas
		if(sortedRows == null || sortedRows.isEmpty()) {
			sortedRows = LookupDataSorter.sortDatas(secondaryInputDatas, masterMap);
		}
		
		List<Row> currentRows = new ArrayList<>(sortedRows);

		for(int j = 0; j < row.getMeta().getSize(); j++) {
			if(masterMap[j] != null) {
				currentRows = searchMatches(row, currentRows, j);
			}
		}
		
		if(currentRows != null && !currentRows.isEmpty()) {
			Row _row = currentRows.get(0);
			Row newRow = RowFactory.createRow(this);

			for(int i = 0; i < row.getMeta().getSize(); i++) {
				newRow.set(i, row.get(i));
			}
			for(int i = row.getMeta().getSize(); i < _row.getMeta().getSize() + row.getMeta().getSize(); i++) {
				newRow.set(i, _row.get(i - row.getMeta().getSize()));
			}

			writeRow(newRow);
//			System.out.println("time searched = " + (new Date().getTime() - start.getTime()));
			return;
		}
//		System.out.println("time searched not found = " + (new Date().getTime() - start.getTime()));
		// XXX
//		if(((SimpleMappingTransformation) getTransformation()).getMappings().size() == 1) {
//
//			Row _row = null;
//
//			if(sortedMap == null) {
//				sortedMap = LookupDataSorter.sortData(secondaryInputDatas, masterMap);
//			}
//			boolean match = true;
//			for(int i = 0; i < row.getMeta().getSize(); i++) {
//				if(masterMap[i] == null) {
//					continue;
//				}
//
//				Object masterValue = row.get(i);
//				int index = rechercheDichotomique(masterValue, 0, sortedMap.get(masterMap[i]).size() - 1, sortedMap.get(masterMap[i]));
//				if(index > -1) {
//					match = match && true;
//					_row = sortedMap.get(masterMap[i]).get(index).getRow();
//				}
//				else {
//					match = false;
//					break;
//				}
//
//				// Object slaveValue = _row.get(masterMap.get(i));
//				//
//				// if ( (masterValue == null && slaveValue == null)||
//				// (masterValue != null && (slaveValue != null && masterValue.toString().equals(slaveValue.toString())))){
//				// match = match && true;
//				//
//				// }
//				// else{
//				// match = false;
//				// break;
//				// }
//			}
//			if(match) {
//
//				Row newRow = RowFactory.createRow(this);
//
//				// if (primaryStep.getTransformation() == getTransformation().getInputs().get(0)){
//				for(int i = 0; i < row.getMeta().getSize(); i++) {
//					newRow.set(i, row.get(i));
//				}
//				for(int i = row.getMeta().getSize(); i < _row.getMeta().getSize() + row.getMeta().getSize(); i++) {
//					newRow.set(i, _row.get(i - row.getMeta().getSize()));
//				}
//				// }
//				// else{
//				// for(int i = 0; i < _row.getMeta().getSize(); i++){
//				// newRow.set(i, _row.get(i));
//				// }
//				// for(int i = _row.getMeta().getSize(); i < _row.getMeta().getSize() + row.getMeta().getSize(); i++){
//				// newRow.set(i, row.get(i - _row.getMeta().getSize() ));
//				// }
//				// }
//
//				writeRow(newRow);
//
//				return;
//			}
//		}
//
//		else {
//			for(Row _row : secondaryInputDatas) {
//				boolean match = true;
//
//				for(int i = 0; i < row.getMeta().getSize(); i++) {
//					if(masterMap[i] == null) {
//						continue;
//					}
//					Object masterValue = row.get(i);
//					Object slaveValue = _row.get(masterMap[i]);
//
//					if((masterValue == null && slaveValue == null) || (masterValue != null && (slaveValue != null && masterValue.toString().equals(slaveValue.toString())))) {
//						match = match && true;
//
//					}
//					else {
//						match = false;
//						break;
//					}
//				}
//				if(match) {
//
//					Row newRow = RowFactory.createRow(this);
//
//					int counter = 0;
//					for(int i = 0; i < row.getMeta().getSize(); i++) {
//						newRow.set(counter++, row.get(i));
//					}
//					for(int i = 0; i < _row.getMeta().getSize(); i++) {
//						newRow.set(counter++, _row.get(i));
//					}
//
//					// if (primaryStep.getTransformation() == getTransformation().getInputs().get(0)){
//					// for(int i = 0; i < row.getMeta().getSize(); i++){
//					// newRow.set(i, row.get(i));
//					// }
//					// for(int i = row.getMeta().getSize(); i < _row.getMeta().getSize() + row.getMeta().getSize(); i++){
//					// newRow.set(i, _row.get(i - row.getMeta().getSize() ));
//					// }
//					// }
//					// else{
//					// for(int i = 0; i < _row.getMeta().getSize(); i++){
//					// newRow.set(i, _row.get(i));
//					// }
//					// for(int i = _row.getMeta().getSize(); i < _row.getMeta().getSize() + row.getMeta().getSize(); i++){
//					// newRow.set(i, row.get(i - _row.getMeta().getSize() ));
//					// }
//					// }
//
//					writeRow(newRow);
//
//					return;
//				}
//
//			}
//		}
		// XXX
		/*
		 * create a new Row
		 */
		Row newRow = RowFactory.createRow(this);

		// if (primaryStep.getTransformation() == getTransformation().getInputs().get(0)){
		for(int i = 0; i < row.getMeta().getSize(); i++) {
			newRow.set(i, row.get(i));
		}

		// }
		// else{
		// for(int i = secondaryStep.getTransformation().getDescriptor(getTransformation()).getColumnCount(); i < newRow.getMeta().getSize(); i++){
		// newRow.set(i, row.get(i - secondaryStep.getTransformation().getDescriptor(getTransformation()).getColumnCount() ));
		// }
		//
		// }
		if(keepNonMatchRows) {
			writeRow(newRow);
		}
		else if(trashOutput != null) {
			trashRow(newRow);
		}
		else {
			throw new Exception("Cannot find a matching for the Row " + newRow.dump());
			// Exception ?
		}

		// if (keepNonMatchRows || (trashOutput != null && trashNonMatchRows)){
		//
		// if (keepNonMatchRows){
		// writeRow(newRow);
		// break;
		// }
		// else if (trashNonMatchRows){
		// trashRow(newRow);
		// break;
		// }
		// }

	}

	private List<Row> searchMatches(Row row, List<Row> sortedRows, int index) {
		int indexRow = rechercheDichotomique(row.get(index), 0, sortedRows.size() - 1, sortedRows, masterMap[index]);
		if(indexRow >= 0) {
			Object value = sortedRows.get(indexRow).get(masterMap[index]);
			int fromIndex = indexRow;
			int toIndex = indexRow + 1;
			for(int i = indexRow ; i > 0 ; i--) {
				if(sortedRows.get(i).get(masterMap[index]) == null || !sortedRows.get(i).get(masterMap[index]).equals(value)) {
					fromIndex = i + 1;
					break;
				}
			}
			for(int i = indexRow ; i < sortedRows.size() ; i++) {
				if(sortedRows.get(i).get(masterMap[index]) == null || !sortedRows.get(i).get(masterMap[index]).equals(value)) {
					toIndex = i;
					break;
				}
			}
			return new ArrayList<Row>(sortedRows.subList(fromIndex, toIndex));
		}
		
		return new ArrayList<>();
	}

	private void trashRow(Row row) throws InterruptedException {
		if(trashOutput != null) {
			trashOutput.insertRow(row, this);
			writedRows++;
		}
	}

	@Override
	protected void writeRow(Row row) throws InterruptedException {
		for(RuntimeStep r : getOutputs()) {
			if(r != trashOutput) {
				r.insertRow(row, this);
			}
		}
		writedRows++;
	}

}
