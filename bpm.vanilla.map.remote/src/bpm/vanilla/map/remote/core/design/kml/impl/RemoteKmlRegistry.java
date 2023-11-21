package bpm.vanilla.map.remote.core.design.kml.impl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import bpm.vanilla.map.core.communication.xml.XmlAction;
import bpm.vanilla.map.core.communication.xml.XmlArgumentsHolder;
import bpm.vanilla.map.core.communication.xml.XmlAction.ActionType;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;
import bpm.vanilla.map.core.design.kml.IKmlManipulator;
import bpm.vanilla.map.core.design.kml.IKmlObject;
import bpm.vanilla.map.core.design.kml.IKmlRegistry;
import bpm.vanilla.map.core.design.kml.KmlColoringDatas;
import bpm.vanilla.map.model.kml.impl.KmlObject;
import bpm.vanilla.map.model.kml.impl.KmlSpecificationEntity;
import bpm.vanilla.map.remote.internal.HttpCommunicator;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.extended.EncodedByteArrayConverter;

public class RemoteKmlRegistry implements IKmlRegistry, IKmlManipulator{
	private HttpCommunicator httpCommunicator;
	private XStream xstream;
	
	public RemoteKmlRegistry(){
		httpCommunicator = new HttpCommunicator("");
		init();
	}
	
	@Override
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
	public IKmlObject addKmlObject(IKmlObject kmlObject, InputStream kmlInputStream)
			throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		
		int sz = 0;
		
		while((sz=kmlInputStream.read(buffer, 0, 1024)) != -1){
			bos.write(buffer, 0, sz);
		}
		kmlInputStream.close();
		bos.close();
		
		XmlAction op = new XmlAction(createArguments(kmlObject, bos.toByteArray()), ActionType.KML_SAVE);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (IKmlObject)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public IKmlObject getKmlObject(Integer kmlObjectId) throws Exception {
		XmlAction op = new XmlAction(createArguments(kmlObjectId), ActionType.KML_GET);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (IKmlObject)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public List<IKmlObject> getKmlObjects() throws Exception {
		XmlAction op = new XmlAction(createArguments(), ActionType.KML_GET);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (List)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void removeKmlObject(IKmlObject kmlObject) throws Exception {
		XmlAction op = new XmlAction(createArguments(kmlObject), ActionType.KML_DELETE);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void removeKmlObject(Integer kmlObjectId) throws Exception {
		XmlAction op = new XmlAction(createArguments(kmlObjectId), ActionType.KML_DELETE);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	/**
	 * Not implemented because we don't need it here
	 * 
	 */
	public String getKmlFolderLocation() {
		
		return null;
	}

	@Override
	public String generateKml(String originalKmlUrl, KmlColoringDatas coloringDatas) throws Exception {
		XmlAction op = new XmlAction(createArguments(originalKmlUrl, coloringDatas), ActionType.KML_GENERATE);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (String)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}
