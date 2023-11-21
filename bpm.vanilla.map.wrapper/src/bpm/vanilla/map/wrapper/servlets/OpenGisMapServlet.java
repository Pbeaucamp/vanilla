package bpm.vanilla.map.wrapper.servlets;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

import bpm.vanilla.map.core.communication.xml.XmlAction;
import bpm.vanilla.map.core.communication.xml.XmlArgumentsHolder;
import bpm.vanilla.map.core.design.IMapDefinition;
import bpm.vanilla.map.core.design.opengis.IOpenGisCoordinate;
import bpm.vanilla.map.core.design.opengis.IOpenGisMapEntity;
import bpm.vanilla.map.core.design.opengis.IOpenGisMapObject;
import bpm.vanilla.map.model.impl.MapDefinition;
import bpm.vanilla.map.wrapper.VanillaMapComponent;

import com.thoughtworks.xstream.XStream;

public class OpenGisMapServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3757612772262620368L;

	
	private static XStream xstream;
	static{
		xstream = new XStream();
	}
	
	private VanillaMapComponent component;
	
	public OpenGisMapServlet(VanillaMapComponent component) {
		this.component = component;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		
		try{
			
			resp.setCharacterEncoding("UTF-8"); 
			XmlAction action = (XmlAction)xstream.fromXML(req.getInputStream());
			
			XmlArgumentsHolder args = action.getArguments();
			
			
			Object actionResult = null;
			
			switch(action.getActionType()){
			case ADD_MAP:
				addMap(args);
				break;
			case ADD_MAP_SHAPE:
				addMapShape(args);
				break;
			case REMOVE_MAP:
				removeMap(args);
				break;
			case UPDATE_MAP:
				updateMap(args);
				break;
			case GET_MAPS:
				actionResult = getMaps(args);
				break;
			case GET_MAP_BY_ID:
				actionResult = getMapById(args);
				break;
			case GET_MAP_ENTITIES:
				actionResult = getMapEntities(args);
				break;
			case SAVE_SHAPE:
				saveShapeFile(args);
				break;
			case GET_ENTITY_COORDINATES:
				actionResult = getOpenGisCoordinates(args);
				break;
			case GET_MAP_BY_DEFINITION_ID:
				actionResult = getMapByDefinitionId(args);
				break;
			default:
				throw new Exception("Unknown action " + action.getActionType().name());
			}
			
			if (actionResult != null){
				xstream.toXML(actionResult, resp.getWriter());
			}
			
			
			resp.getWriter().close();
		}catch(Exception ex){
			ex.printStackTrace();
			resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, ex.getMessage());
			component.getLogger().error("An error occured", ex);

		}
		
		
		
		
	}

	private void addMapShape(XmlArgumentsHolder args) throws Exception {
		IOpenGisMapObject map = (IOpenGisMapObject) args.getArguments().get(0);
		byte[] bytes = (byte[]) args.getArguments().get(1);
		
		byte[] decodedBytes = Base64.decodeBase64(bytes);
		InputStream is = new ByteArrayInputStream(decodedBytes);
		
		IMapDefinition definition = new MapDefinition();
		definition.setLabel(map.getName());
		definition.setMapType(IMapDefinition.MAP_TYPE_OPEN_GIS);
		
		IMapDefinition def = component.getMapDefinitionDao().saveMapDefinition(definition);
		map.setMapDefinitionId(def.getId());
		component.getOpenGisService().addOpenGisMap(map, is);
	}

	private IOpenGisMapObject getMapByDefinitionId(XmlArgumentsHolder args) throws Exception {
		int mapDefinitionId = (Integer) args.getArguments().get(0);
		return component.getOpenGisService().getOpenGisMapByDefinitionId(mapDefinitionId);
	}

	private List<IOpenGisCoordinate> getOpenGisCoordinates(XmlArgumentsHolder args) throws Exception {
		Integer entityId = (Integer) args.getArguments().get(0);
		return component.getOpenGisService().getOpenGisCoordinates(entityId);
	}

	private void saveShapeFile(XmlArgumentsHolder args) throws Exception {
		int openGisMapId = (Integer) args.getArguments().get(0);
		byte[] bytes = (byte[]) args.getArguments().get(1);
		
		byte[] decodedBytes = Base64.decodeBase64(bytes);
		InputStream is = new ByteArrayInputStream(decodedBytes);
		
		component.getOpenGisService().saveShapeFile(openGisMapId, is);
	}

	private List<IOpenGisMapEntity> getMapEntities(XmlArgumentsHolder args) throws Exception {
		IOpenGisMapObject map = (IOpenGisMapObject) args.getArguments().get(0);
		return component.getOpenGisService().getOpenGisMapEntities(map);
	}

	private IOpenGisMapObject getMapById(XmlArgumentsHolder args) throws Exception {
		int mapId = (Integer) args.getArguments().get(0);
		return component.getOpenGisService().getOpenGisMapById(mapId);
	}

	private List<IOpenGisMapObject> getMaps(XmlArgumentsHolder args) throws Exception {
		return component.getOpenGisService().getOpenGisMaps();
	}

	private void updateMap(XmlArgumentsHolder args) throws Exception {
		IOpenGisMapObject map = (IOpenGisMapObject) args.getArguments().get(0);
		component.getOpenGisService().updateOpenGisMap(map);
	}

	private void removeMap(XmlArgumentsHolder args) throws Exception {
		IOpenGisMapObject map = (IOpenGisMapObject) args.getArguments().get(0);
		component.getOpenGisService().deleteOpenGisMap(map);
	}

	private void addMap(XmlArgumentsHolder args) throws Exception {
		IOpenGisMapObject map = (IOpenGisMapObject) args.getArguments().get(0);
		
		IMapDefinition definition = new MapDefinition();
		definition.setLabel(map.getName());
		definition.setMapType(IMapDefinition.MAP_TYPE_OPEN_GIS);
		
		IMapDefinition def = component.getMapDefinitionDao().saveMapDefinition(definition);
		map.setMapDefinitionId(def.getId());
		component.getOpenGisService().addOpenGisMap(map);
		
		//TODO do something with the file.....
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

	
	
	
}
