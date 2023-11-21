package bpm.gateway.runtime2.internal;

import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.runtime2.RuntimeStep;

/**
 * create Row from a Transformation
 * @author LCA
 *
 */
public class RowFactory {
	
	/**
	 * create a Row matching to the RuntimeStep.getTransformation.getDescriptor informations
	 * @param run
	 * @return
	 * @throws Exception
	 */
	public static Row createRow(RuntimeStep run) throws Exception{
		try {
			RowMeta meta = new RowMeta(run.getTransformation().getDescriptor(run.getTransformation()));
			return new Row(meta);
		} catch (Exception e) {
			throw new Exception("cannot create Row ", e);
		}
	}
	
	/**
	 * create a Row matching to the RuntimeStep.getTransformation.getDescriptor informations
	 * @param run
	 * @return
	 * @throws Exception
	 */
	public static Row createRow(RuntimeStep run, Transformation transfo) throws Exception{
		try {
			StreamDescriptor desc = run.getTransformation().getDescriptor(transfo);
			RowMeta meta = new RowMeta(desc);
			return new Row(meta);
		} catch (Exception e) {
			throw new Exception("cannot create Row ", e);
		}
	}
	
	/**
	 * create a Row matching to the RuntimeStep.getTransformation.getDescriptor informations
	 * and init the row.getSize() first value with the given row ones 
	 * @param runFieldSpliter
	 * @param row
	 * @return
	 */
	public static Row createRow(RuntimeStep run, Row row) throws Exception{
		Row newRow = createRow(run);
		for(int i = 0; i < row.getMeta().getSize(); i++){
			newRow.set(i, row.get(i));
		}
		
		return newRow;
	}
	
	/**
	 * create a Row matching to the RuntimeStep.getTransformation.getDescriptor informations
	 * and init the row.getSize() first value with the given row ones 
	 * @param runFieldSpliter
	 * @param row
	 * @param lastIndex : copy from Row.get(0) to Row.get(lastIndex)
	 * @return
	 */
	public static Row createRow(RuntimeStep run, Row row, int lastIndex) throws Exception{
		Row newRow = createRow(run);
		for(int i = 0; i < lastIndex || i < row.getMeta().getSize(); i++){
			newRow.set(i, row.get(i));
		}
		
		return newRow;
	}
	
	
}
