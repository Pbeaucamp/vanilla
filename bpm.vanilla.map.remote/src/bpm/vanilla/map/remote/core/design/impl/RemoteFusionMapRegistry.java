package bpm.vanilla.map.remote.core.design.impl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import bpm.vanilla.map.core.communication.xml.XmlAction;
import bpm.vanilla.map.core.communication.xml.XmlArgumentsHolder;
import bpm.vanilla.map.core.communication.xml.XmlAction.ActionType;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapObject;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapRegistry;
import bpm.vanilla.map.model.fusionmap.impl.FusionMapObject;
import bpm.vanilla.map.model.fusionmap.impl.FusionMapSpecificationEntity;
import bpm.vanilla.map.remote.internal.HttpCommunicator;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.extended.EncodedByteArrayConverter;

public class RemoteFusionMapRegistry implements IFusionMapRegistry{
	private HttpCommunicator httpCommunicator;
	private XStream xstream;
	
	public RemoteFusionMapRegistry(){
		httpCommunicator = new HttpCommunicator("");
		init();
	}
	
	public void configure(Object config){
		setVanillaRuntimeUrl((String)config);
	}
	
	public void setVanillaRuntimeUrl(String vanillaRuntimeUrl){
		synchronized (httpCommunicator) {
			httpCommunicator.setUrl(vanillaRuntimeUrl);
		}
		
	}
	
	private void init(){
		xstream = new XStream();
		xstream.registerConverter((SingleValueConverter)new EncodedByteArrayConverter());
	}
	
	
	private XmlArgumentsHolder createArguments(Object...  arguments){
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		for(int i = 0; i < arguments.length; i++){
			args.addArgument(arguments[i]);
		}
		return args;
	}

	@Override
	public IFusionMapObject addFusionMapObject(IFusionMapObject fusionMap, InputStream swfInputStream) throws Exception {
		
		/*
		 * 
		 */
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		
		int sz = 0;
		
		while((sz=swfInputStream.read(buffer, 0, 1024)) != -1){
			bos.write(buffer, 0, sz);
		}
		swfInputStream.close();
		bos.close();
		
		XmlAction op = new XmlAction(createArguments(fusionMap, bos.toByteArray()), ActionType.FUSION_MAP_SAVE);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (IFusionMapObject)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public IFusionMapObject getFusionMapObject(long fusionMapObjectId)	throws Exception {
		XmlAction op = new XmlAction(createArguments(fusionMapObjectId), ActionType.FUSION_MAP_GET);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (IFusionMapObject)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public List<IFusionMapObject> getFusionMapObjects() throws Exception {
		XmlAction op = new XmlAction(createArguments(), ActionType.FUSION_MAP_GET);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (List)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void removeFusionMapObject(IFusionMapObject fusionMap)throws Exception {
		XmlAction op = new XmlAction(createArguments(fusionMap), ActionType.FUSION_MAP_DELETE);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}

	@Override
	public void removeFusionMapObject(long fusionMapObjectId) throws Exception {
		XmlAction op = new XmlAction(createArguments(fusionMapObjectId), ActionType.FUSION_MAP_DELETE);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}

	@Override
	/**
	 * not implemented, this is a no use
	 * only needed for the real implementor
	 */
	public String getMapFolderLocation() {
		
		return null;
	}

	@Override
	public List<IFusionMapObject> getFusionMapObjects(String type) throws Exception {
		XmlAction op = new XmlAction(createArguments(type), ActionType.FUSION_MAP_GET);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (List)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
