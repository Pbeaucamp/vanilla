package bpm.united.olap.runtime.result;

import bpm.united.olap.api.runtime.RunResult;

/**
 * Used to remove elements who don't have to be in the results
 * @author Marc Lanquetin
 *
 */
public interface IResultCleaner {

	/**
	 * Clean the runResult object
	 * @param result
	 * @return
	 */
	RunResult cleanResults(RunResult result);
	
}
