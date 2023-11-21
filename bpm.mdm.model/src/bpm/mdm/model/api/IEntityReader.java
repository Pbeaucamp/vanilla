package bpm.mdm.model.api;

import bpm.mdm.model.runtime.Row;

public interface IEntityReader {
	public void open() throws Exception;
	public boolean hasNext()throws Exception;
	public Row next()throws Exception;
	public void close() throws Exception;
}
