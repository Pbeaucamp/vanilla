package bpm.gateway.runtime2.transformation.sorting;

import java.util.Comparator;
import java.util.List;

import bpm.gateway.core.transformations.SortElement;
import bpm.gateway.core.transformations.SortTransformation;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.StepEndedException;
import bpm.gateway.runtime2.internal.Row;



public class RunSort extends RuntimeStep{
	
	private int bufferSize = 5000;
	
	/*
	 * null if not sort on this index
	 * true if sort ASC
	 * false if sort DESC
	 */
	private List<SortElement> sorts;
	
	private Comparator<Row> comparator;
	
	private Sorter sorter;	


	public RunSort(SortTransformation transformation, int bufferSize) {
		super(null, transformation, bufferSize);
		
	}

	

	@Override
	public void init(Object adapter) throws Exception {
		SortTransformation sort = (SortTransformation)getTransformation();
		
		sorts = sort.getSorts();
		
		comparator = new Comparator<Row>() {

			public int compare(Row o1, Row o2) {
				if (o1 == null){
					return 1;
				}
				else if (o2 == null){
					return -1;
				}
				
				SortTransformation sort = (SortTransformation)getTransformation();
				
				for(SortElement se : sorts){
					
					Integer index = sort.getIndiceForColumn(se.getColumnSort());
					
					if (se.isType()){
						if (o1.get(index) == null){
							if (o2.get(index) != null){
								return -1;
							}
							continue;
						}
						
						if (o2.get(index) == null){
							return 1;
						}
						
						int r =  (o1.get(index)).toString().compareTo(o2.get(index).toString());
						if (r == 0){
							continue;
						}
						else{
							return r;
						}
						
					}else{
						if (o1.get(index) == null){
							if (o2.get(index) != null){
								return 1;
							}
							continue;
						}
						
						if (o2.get(index) == null){
							return 1;
						}
						
						int r =  ((Comparable)o1.get(index)).compareTo((Comparable)o2.get(index));
						if (r == 0){
							continue;
						}
						else{
							return -r;
						}

					}
				}
				return 0;
			}
		};
		
		sorter = new Sorter(100000, comparator, this, getLogger());
		
		info(" inited");
		isInited = true;
	}

		
	
	
	
	

	@Override
	public void performRow() throws Exception {
//		if (!areInputStepAllProcessed()){
//			Thread.sleep(1000);
//			return;
//		}
		
		while(!(inputs.get(0).isEnd() && inputs.get(0).inputEmpty())){
			try{
				Thread.sleep(50);
			}catch(Exception e){
				
			}
			
		}
				
		
		
		Row r = readRow();
		
		if (r == null){
			setEnd();
			return;
		}
		writeRow(r);
	}
	
	

	@Override
	public void releaseResources() {
		sorter.releaseResources();
		sorts = null;
		sorter = null;
		comparator = null;
		info(" resources released");
	}



	@Override
	synchronized public void insertRow(Row data, RuntimeStep caller)
			throws InterruptedException {
		try{
			sorter.addRow(data);
		}catch(Exception e){
			error(" insert error ", e);
			e.printStackTrace();
		}
	}



	@Override
	protected Row readRow() throws InterruptedException, StepEndedException {
		try {
			return sorter.getMinRow();
		} catch (Exception e) {
			error("error on read row", e);
//			e.printStackTrace();
			throw new InterruptedException("error on read row");
		}
		
	}

	
	
	
}
