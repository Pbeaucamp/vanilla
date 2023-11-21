package bpm.vanilla.platform.core.remote.impl;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import bpm.vanilla.platform.core.IExcelManager;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IRepositoryManager;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteExcelManager implements IExcelManager{
	private HttpCommunicator httpCommunicator;
	private static XStream xstream;
	
	static{
		xstream = new XStream();
	}
	public RemoteExcelManager(HttpCommunicator httpCommunicator){
		this.httpCommunicator = httpCommunicator;
	}
	
	private XmlArgumentsHolder createArguments(Object...  arguments){
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		for(int i = 0; i < arguments.length; i++){
			args.addArgument(arguments[i]);
		}
		return args;
	}
	
	@Override
	public String getColumnType(HashMap<String, String> documentParameters)throws Exception{
	XmlAction op = new XmlAction(createArguments(documentParameters), IExcelManager.ActionType.GET_COLUMNTYPE);
	String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
	return (String)xstream.fromXML(xml);
}

	@Override
	public String getListDriver() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IExcelManager.ActionType.GET_DRIVERS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (String)xstream.fromXML(xml);
	}
	
	@Override
	public String getListTables(HashMap<String, String> documentParameters) throws Exception {
		XmlAction op = new XmlAction(createArguments(documentParameters), IExcelManager.ActionType.GET_TABLES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (String)xstream.fromXML(xml);
	}
	
	@Override
	public String getListColumns(HashMap<String, String> documentParameters) throws Exception {
		XmlAction op = new XmlAction(createArguments(documentParameters), IExcelManager.ActionType.GET_COLUMNS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (String)xstream.fromXML(xml);
	}
	
	@Override
	public String createTable(String xmlTable, HashMap<String, String> documentParameters) throws Exception {
		XmlAction op = new XmlAction(createArguments(xmlTable, documentParameters), IExcelManager.ActionType.CREATE_TABLE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (String)xstream.fromXML(xml);
		
	}
	@Override
	public String testConnectionDatabase(HashMap<String, String> documentParameters) throws Exception {
		XmlAction op = new XmlAction(createArguments(documentParameters), IExcelManager.ActionType.TESTCONNECT_SERVER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (String)xstream.fromXML(xml);
		
	}

	@Override
	public String loaderExcel(String name, String file,  InputStream in, IRepositoryContext ctx, HashMap<String, String> documentParameters)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(name, file, in, ctx, documentParameters), IExcelManager.ActionType.ADD_CONTRACT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (String)xstream.fromXML(xml);
	}


	

}
