package bpm.united.olap.runtime.result;

import bpm.united.olap.api.result.OlapResult;
import bpm.united.olap.api.runtime.RunResult;

/**
 * Use this to generate the result of the query
 * @author Marc Lanquetin
 *
 */
public interface IOlapResultGenerator {

	/**
	 * Generate the OlapResult object which will be return to the client application
	 * @param runResult
	 * @return
	 */
	OlapResult generateOlapResult(RunResult runResult);
	
}
