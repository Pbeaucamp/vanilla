package bpm.gwt.commons.client.utils;

import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.user.client.rpc.IsSerializable;

public class ToolsGWT implements IsSerializable{
	
	public static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
	public static final String DEFAULT_DATE_TIME_FORMAT = "dd/MM/yyyy - HH:mm:ss";
	public static final String DEFAULT_TIME_FORMAT = "HH:mm";
	public static final String DEFAULT_HOUR_FORMAT = "HH";
	public static final String DEFAULT_MIN_FORMAT = "mm";

	public enum TypeCollaboration implements IsSerializable{
		DIRECTORY_NOTE,
		ITEM_NOTE,
		DOCUMENT_VERSION
	}
	
	public static void addScriptFiles(List<String> paths) {
		for(String path : paths) {
			if(path.endsWith(".js")){
				addScriptFile(path);
			} else if(path.endsWith(".css")){
				addScriptCSSFile(path);
			}
			
		}
	}
	
	public static void addScriptFile(String path) {
		
		ScriptInjector.fromUrl(path).setWindow(ScriptInjector.TOP_WINDOW).inject();
		
//		Element elem = Document.get().getElementsByTagName("head").getItem(0);
//		ScriptElement script = Document.get().createScriptElement();
//		script.setAttribute("language", "javascript"); 
//		script.setSrc(path);
//		
//		elem.appendChild(script);
	}
	
	public static void addScriptCSSFile(String path) {
			
		Element elem = Document.get().getElementsByTagName("head").getItem(0);
		LinkElement script = Document.get().createLinkElement();
		script.setRel("stylesheet");
		script.setType("text/css"); 
		script.setHref(path);
		
		elem.appendChild(script);
	}
	
	
	public static void injectScripts(List<String> js) {
		for(String j : js) {
			injectScript(j);
		}
	}
	
	public static void injectScript(String js) {
		Element elem = Document.get().getElementsByTagName("head").getItem(0);
		ScriptElement script = Document.get().createScriptElement();
		script.setAttribute("language", "javascript"); 
		script.setText(js);
		
		elem.appendChild(script);
	}

	/**
	 * Get URL and chops it into: Key/Values bpm.vanilla.sessionId/session
	 * bpm.vanilla.groupId/groupId bpm.vanilla.repositoryId/repositoryId
	 * 
	 * @param location
	 * @return properties
	 * 
	 */
	public static HashMap<String, String> parseUrl(String url) {
		HashMap<String, String> props = new HashMap<String, String>();
		try {
			url = url.split("\\?")[1];
			String[] couples = url.split("&");

			String[] tmp;

			for (int i = 0; i < couples.length; i++) {
				tmp = couples[i].split("=");
				props.put(tmp[0], tmp[1]);
			}
		} catch (Exception e) {
			// do nothing
		}

		return props;
	}

	/**
	 * 
	 * @param linkElementId
	 * @param url
	 */
	public static native void setLinkHref(String linkElementId, String url) /*-{ 
    	var link = $doc.getElementById(linkElementId); 
    	if (link != null && link != undefined) { 
			link.href = url; 
		} 
	}-*/;


	/**This method aim to open a new page with the given url as target
	 * @param url the target 
	 */
	public static native void doRedirect(String url)/*-{
		$wnd.open(url);
	}-*/;

	/**This method aim to dynamically change the current URL 
	 * @param url the new URL string
	 */
	public static native void changeCurrURL(String url)/*-{
    	$wnd.location.replace(url);
	}-*/;
}
