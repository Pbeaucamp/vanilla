package bpm.gateway.runtime2.transformation.selection;

import java.util.Arrays;
import java.util.Comparator;

import bpm.gateway.core.transformations.TopXTransformation;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;

public class RunTopX extends RuntimeStep{
	
	private Row[] topRows;
	private Comparator<Row> comparator;
	private int fieldIndex = -1;
	private int currentIndex = -1;
	
	private boolean asc = false;
	private boolean sort = false;
	
	public RunTopX(TopXTransformation transformation, int bufferSize) {
		super(null, transformation, bufferSize);
		
	}

	@Override
	public void init(Object adapter) throws Exception {
		TopXTransformation select = (TopXTransformation)getTransformation();
		if (select.getInputs().size() != 1){
			String message = " TopXTransformation cannot have more or less than one Input at runtime";
			error(message);
			throw new Exception(message);
		}
		try{
			fieldIndex = select.getField();
			if (fieldIndex < 0){
				throw new Exception("");
			}
		}catch(Exception ex){
			String message = " TopXTransformation need a Defined Field";
			error(message);
			throw new Exception(message);
		}
		
		topRows = new Row[select.getX()];
		
		sort = !select.getSorting().equals(TopXTransformation.NONE);
		if (sort){
			asc = select.getSorting().equals(TopXTransformation.ASC);
		}
		
		comparator = new Comparator<Row>() {

			public int compare(Row row1, Row row2) {
				
				int multiplier = 1;
				
				if (sort && !asc){
					multiplier = -1;
				}
				
				if (row1 == null){
					if (row2 == null){
						return 0;
					}
					return multiplier * 1;
				}
				else if (row2 == null){
					return multiplier * -1;
				}
				if (row1.get(fieldIndex) == null){
					if (row2.get(fieldIndex) == null){
						return 0;
					}
					else{
						return multiplier * -1;
					}
				}
				else if (row2.get(fieldIndex) == null){
					return multiplier * 1;
				}
				else{
					return multiplier * ((Comparable)row1.get(fieldIndex)).compareTo((Comparable)row2.get(fieldIndex));
				}

			}
		};
		info(" inited");
		
	}
	
	/*
	 * return the index of the minimum value on topRows
	 */
	public int getMinIndex(){
		for(int i = 0; i < topRows.length; i++){
			if (topRows[i] == null){
				return i;
			}
		}
		
		int min = -1;
		for(int i = 0; i < topRows.length; i++){
			if (min == -1){
				min = i;
				continue;
			}
			if (((Comparable)topRows[min].get(fieldIndex)).compareTo((Comparable)topRows[i].get(fieldIndex)) > 0){
				min = i;
			}
		}
		
		return min;
	}

	@Override
	public void performRow() throws Exception {
		
		if (!areInputStepAllProcessed()){
			Thread.sleep(1000);
			return;
		}
		
//		if (inputEmpty()){
//			
//			setEnd();
//		}
//		else{
			if (++currentIndex < topRows.length){
				writeRow(topRows[currentIndex]);
			}
			else{
				setEnd();
			}
//		}
		
		
		
		
	}

	
	
	/* (non-Javadoc)
	 * @see bpm.gateway.runtime2.RuntimeStep#insertRow(bpm.gateway.runtime2.internal.Row, bpm.gateway.runtime2.RuntimeStep)
	 */
	@Override
	synchronized public void insertRow(Row data, RuntimeStep caller)	throws InterruptedException {
		System.out.println( data.get(fieldIndex));
		int index = getMinIndex();
		int multiplier = 1;
		
		if (sort && !asc){
			multiplier = -1;
		}
		if (topRows[index] == null || multiplier * comparator.compare(data, topRows[index]) > 0){
			
//			if (topRows[index] != null){
//				System.out.println("replace " + topRows[index].get(fieldIndex) + " by " + data.get(fieldIndex));
//			}
			topRows[index] = data;
		}
		else{
//			System.out.println(" not replace " + topRows[index].get(fieldIndex) + " by " + data.get(fieldIndex));
		}
		
		if (sort){
			Arrays.sort(topRows, comparator);
		}
		
	
		readedRows ++;
	}

	@Override
	public void releaseResources() {
		topRows = null;
		info( " resources released");
		
	}

}
