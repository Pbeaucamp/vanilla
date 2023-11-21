package bpm.es.dndserver.api.fmdt.replacers;

import org.dom4j.DocumentException;

import bpm.es.dndserver.api.fmdt.FMDTDataSource;

public interface IFMDTReplacer {

	public String replace(String modelXml, FMDTDataSource orig, 	FMDTDataSource toreplace) throws Exception ;
}
