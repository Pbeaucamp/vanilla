package bpm.fd.runtime.model.ui;

import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.fd.runtime.model.Component;

public interface UIGenerator {

	public Object generateUi(IResultSet datas, Component component) throws Exception;
	
}
