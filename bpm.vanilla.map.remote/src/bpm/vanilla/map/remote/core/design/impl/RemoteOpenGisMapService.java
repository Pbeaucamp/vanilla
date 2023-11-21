package bpm.vanilla.map.remote.core.design.impl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import bpm.vanilla.map.core.communication.xml.XmlAction;
import bpm.vanilla.map.core.communication.xml.XmlArgumentsHolder;
import bpm.vanilla.map.core.communication.xml.XmlAction.ActionType;
import bpm.vanilla.map.core.design.IOpenGisMapService;
import bpm.vanilla.map.core.design.opengis.IOpenGisCoordinate;
import bpm.vanilla.map.core.design.opengis.IOpenGisMapEntity;
import bpm.vanilla.map.core.design.opengis.IOpenGisMapObject;
import bpm.vanilla.map.remote.internal.HttpCommunicator;

import com.thoughtworks.xstream.XStream;

public class RemoteOpenGisMapService implements IOpenGisMapService {

	private HttpCommunicator httpCommunicator;
	private XStream xstream;
	
	public RemoteOpenGisMapService(){
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
	}
	
	private XmlArgumentsHolder createArguments(Object...  arguments){
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		for(int i = 0; i < arguments.length; i++){
			args.addArgument(arguments[i]);
		}
		return args;
	}
	
	@Override
	public void addOpenGisMap(IOpenGisMapObject map) throws Exception {
		XmlAction op = new XmlAction(createArguments(map), ActionType.ADD_MAP);
		try {
			httpCommunicator.executeAction(op, xstream.toXML(op));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void deleteOpenGisMap(IOpenGisMapObject map) throws Exception {
		XmlAction op = new XmlAction(createArguments(map), ActionType.REMOVE_MAP);
		try {
			httpCommunicator.executeAction(op, xstream.toXML(op));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public IOpenGisMapObject getOpenGisMapById(int mapId) throws Exception {
		XmlAction op = new XmlAction(createArguments(mapId), ActionType.GET_MAP_BY_ID);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (IOpenGisMapObject)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public List<IOpenGisMapEntity> getOpenGisMapEntities(IOpenGisMapObject map) throws Exception {
		XmlAction op = new XmlAction(createArguments(map), ActionType.GET_MAP_ENTITIES);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (List)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public List<IOpenGisMapObject> getOpenGisMaps() throws Exception {
		XmlAction op = new XmlAction(createArguments(), ActionType.GET_MAPS);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (List)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void updateOpenGisMap(IOpenGisMapObject map) throws Exception {
		XmlAction op = new XmlAction(createArguments(map), ActionType.UPDATE_MAP);
		try {
			httpCommunicator.executeAction(op, xstream.toXML(op));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void addOpenGisMap(IOpenGisMapObject map, InputStream fileShape) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int sz = 0;
		byte[] buf = new byte[1024];
		while((sz = fileShape.read(buf)) >= 0){
			bos.write(buf, 0, sz);
		}
		fileShape.close();
		
		byte[] streamDatas = Base64.encodeBase64(bos.toByteArray());
		
		XmlAction op = new XmlAction(createArguments(map, streamDatas), ActionType.ADD_MAP_SHAPE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<IOpenGisCoordinate> getOpenGisCoordinates(int entityId) throws Exception {
		XmlAction op = new XmlAction(createArguments(entityId), ActionType.GET_ENTITY_COORDINATES);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (List<IOpenGisCoordinate>)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public IOpenGisMapObject getOpenGisMapByDefinitionId(int mapDefinitionId) throws Exception {
		XmlAction op = new XmlAction(createArguments(mapDefinitionId), ActionType.GET_MAP_BY_DEFINITION_ID);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (IOpenGisMapObject)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void saveShapeFile(int openGisMapId, InputStream fileShape) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int sz = 0;
		byte[] buf = new byte[1024];
		while((sz = fileShape.read(buf)) >= 0){
			bos.write(buf, 0, sz);
		}
		fileShape.close();
		
		byte[] streamDatas = Base64.encodeBase64(bos.toByteArray());
		
		XmlAction op = new XmlAction(createArguments(openGisMapId, streamDatas), ActionType.SAVE_SHAPE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

}
