package bpm.vanilla.map.remote.internal;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import bpm.vanilla.map.core.communication.xml.XmlAction;

public class HttpCommunicator {

	private String url;
	
	public HttpCommunicator(String url){
		this.url = url;
	}
	
	
	public String executeAction(XmlAction action, String message) throws Exception{
		switch(action.getActionType()){
		case DEF_DELETE:
		case DEF_SAVE:
		case DEF_UPDATE:
		case GET_ADDRESS:
		case GET_BUILDING:
		case GET_CELL:
		case GET_FLOOR:
		case GET_IMAGE:
		case GET_MAP_DEFINITION:
		case GET_ADDRESS_ZONE:
		case GET_ADDRESS_MAP_DEFINITION_RELATION:
		case GET_ZTMAPPING:
		case SAVE_ZTMAPPING:
		case DEL_ZTMAPPING:
		case GET_OLMO:
		case SAVE_OLMO:
		case DEL_OLMO:
		case DELETE_MAP_DATASET:
		case GET_MAPS_DATASET:
		case SAVE_MAP_DATASET:
		case SAVE_MAP_DATASOURCE:
		case GET_MAPS_DATASOURCE:
		case GET_MAP_DATASOURCE_BY_NAME:
		case UPDATE_MAP_VANILLA:
		case SAVE_MAP_VANILLA:
		case GET_MAPS_VANILLA_LIST:
		case GET_MAP_VANILLA_BY_ID:
		case DELETE_MAP_VANILLA:
		case GET_MAP_DATASET_BY_ID:
		case GET_MAPDATASET_BY_ID:
		case GET_MAP_DATASOURCE_BY_ID:
		case UPDATE_MAP_DATASOURCE:
		case UPDATE_MAP_DATASET:
		case GET_MAP_BY_TYPE:
		case GET_MAP_SERVERS:
		case MANAGE_MAP_SERVER:
		case GET_MAP_LAYERS:
		case GET_ARCGIS_SERVICES:
		case GET_METADATA_MAPPINGS:
		case SAVE_METADATA_MAPPINGS:
			return sendMessage("vanillaMapDefinition", message);
		case FUSION_MAP_DELETE:
		case FUSION_MAP_GET:
		case FUSION_MAP_SAVE:
			return sendMessage("fusionMap/fusionMapRegistry", message);
		case KML_DELETE:
		case KML_GET:
		case KML_SAVE:
			return sendMessage("Kml/kmlFileRegistry", message);
			
		case KML_GENERATE:
			return sendMessage("Kml/kmlGenerate", message);
		case ADD_MAP:
		case ADD_MAP_SHAPE:
		case UPDATE_MAP:
		case REMOVE_MAP:
		case GET_MAP_BY_ID:
		case GET_MAPS:
		case GET_MAP_ENTITIES:
		case GET_MAP_BY_DEFINITION_ID:
		case SAVE_SHAPE:
			return sendMessage("openGisMapServlet", message);
		}
		
		throw new Exception("XMLActionType " + action.getActionType().name() + " not supported") ;
	}
	
	
	private String sendMessage(String servlet, String message) throws Exception{
		URL url = this.url.endsWith("/") ? new URL(this.url +  servlet) : new URL(this.url + "/" + servlet);
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();
		sock.setDoInput(true);
		sock.setDoOutput(true);
		sock.setRequestMethod("POST");
		sock.setRequestProperty("Content-type", "text/xml;charset=UTF-8");
			
		//send datas
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(), "UTF-8"));
		StringBuffer toSend = new StringBuffer();
		toSend.append(message);
		
		pw.write(toSend.toString());
		pw.close();
		String result = null;
		try{
			InputStream is = sock.getInputStream();
		
			result = IOUtils.toString(is, "UTF-8");
			is.close();
			
			//error catching
			if (result.contains("<error>")){
				throw new Exception(result.substring(result.indexOf("<error>") + 7, result.indexOf("</error>")));
			}
			return result;
		}catch(Exception ex){
			ex.printStackTrace();
			
			result = IOUtils.toString(sock.getErrorStream(), "UTF-8"); 
			throw new Exception( result.substring(result.indexOf("<body>") + 6, result.indexOf("</body>")));
		}
		
		
		
		
	}


	public void setUrl(String vanillaRuntimeUrl) {
		this.url = vanillaRuntimeUrl;
//		this.url = "http://localhost:9090";
	}
}
