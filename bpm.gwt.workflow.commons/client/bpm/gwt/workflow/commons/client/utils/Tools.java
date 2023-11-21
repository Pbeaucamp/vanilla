package bpm.gwt.workflow.commons.client.utils;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Tools implements IsSerializable{

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
