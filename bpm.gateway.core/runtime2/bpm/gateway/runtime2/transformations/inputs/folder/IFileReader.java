package bpm.gateway.runtime2.transformations.inputs.folder;

import bpm.gateway.runtime2.internal.Row;

public interface IFileReader {

	public Row readRow() throws Exception;
	
	
	public void releaseResources();
	
	public boolean isAllRead();
}
