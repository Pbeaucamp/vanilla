package bpm.gwt.aklabox.commons.client.utils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.IsSerializable;

public class ToolsGWT implements IsSerializable{

	public static String getRightPath(String path) {
		boolean hostedMode = !GWT.isScript() && GWT.isClient();
		
		path = path.replace("\\", "/");
		if (hostedMode) {
			return path;
		}
		else {
			return path.replace("webapps/", "../");
		}
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
