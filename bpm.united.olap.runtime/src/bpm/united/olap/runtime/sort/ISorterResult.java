package bpm.united.olap.runtime.sort;

import java.util.List;

import bpm.united.olap.api.runtime.MdxSet;
import bpm.united.olap.api.runtime.RunResult;

/**
 * Sort the results and cross axis to have the dataStorageItem
 * @author Marc Lanquetin
 *
 */
public interface ISorterResult {
	
	/**
	 * Sort and cross axis 
	 * @param axis
	 * @return a runResult item
	 */
	RunResult sortResult(List<List<MdxSet>> axis);
	
}
