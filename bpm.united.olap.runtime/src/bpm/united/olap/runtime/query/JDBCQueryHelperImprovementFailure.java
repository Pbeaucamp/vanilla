package bpm.united.olap.runtime.query;

import java.util.List;

import bpm.united.olap.api.runtime.DataCellIdentifier2;
import bpm.united.olap.api.runtime.DataStorage;

public class JDBCQueryHelperImprovementFailure extends Exception {
	private List<DataCellIdentifier2> failurePossibleIds;
	private DataStorage storage;
	public JDBCQueryHelperImprovementFailure(List<DataCellIdentifier2> failurePossibleIds, DataStorage storage) {
		
	}
	/**
	 * @return the failurePossibleIds
	 */
	public List<DataCellIdentifier2> getFailurePossibleIds() {
		return failurePossibleIds;
	}
	
	/**
	 * @return the storage
	 */
	public DataStorage getStorage() {
		return storage;
	}
	
}
