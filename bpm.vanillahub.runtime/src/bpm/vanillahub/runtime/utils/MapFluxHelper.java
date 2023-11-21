package bpm.vanillahub.runtime.utils;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;
import org.springframework.web.util.UriComponentsBuilder;

import bpm.vanilla.platform.core.beans.resources.CkanResource;

public class MapFluxHelper {
	
	public enum WFSFormat {
		FORMAT_JSON("application%2Fjson"),
		FORMAT_GML3("GML3"),
		FORMAT_JSON_SUBTYPE_GEOJSON("application%2Fjson;%20subtype=geojson");
		
		private String value;
		
		private WFSFormat(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

	// private static final String KEY_URL = "{{URL}}";
	private static final String KEY_TYPE_NAME = "{{name}}";
	private static final String KEY_MAX_FEATURES = "{{maxFeatures}}";
	private static final String KEY_PROJECTION = "{{projection}}";
	private static final String KEY_FORMAT = "{{format}}";
	
	private static final String WFS_PARAM_SERVICE = "service=WFS";
	private static final String WFS_PARAM_VERSION_1_0_0 = "version=1.0.0";
	private static final String WFS_PARAM_VERSION_1_1_0 = "version=1.1.0";
	private static final String WFS_PARAM_REQUEST_GET_FEATURE = "request=GetFeature";
	private static final String WFS_PARAM_REQUEST_GET_CAPABILITIES = "request=GetCapabilities";
	private static final String WFS_PARAM_TYPE_NAME = "typeName=" + KEY_TYPE_NAME;
	private static final String WFS_PARAM_MAX_FEATURES = "maxFeatures=" + KEY_MAX_FEATURES;
	private static final String WFS_PARAM_OUTPUT_FORMAT = "outputFormat=" + KEY_FORMAT;
	private static final String WFS_PARAM_PROJECTION = "srsName=urn:ogc:def:crs:EPSG::" + KEY_PROJECTION;
	
//	private static final String WFS_DEFAULT_URL_GET_FEATURE = "service=WFS&version=1.0.0&request=GetFeature&typeName=" + KEY_TYPE_NAME + "&maxFeatures=" + KEY_MAX_FEATURES + "&outputFormat=" + KEY_FORMAT + "&srsName=urn:ogc:def:crs:EPSG::" + KEY_PROJECTION;
//	private static final String WFS_DEFAULT_URL_GET_CAPABILITIES = "service=WFS&version=1.1.0&request=GetCapabilities";

	// private static final String FEATURES_DEFAULT_URL = KEY_URL +
	// "?service=WFS&version=1.1.0&request=GetFeature&typeName=" + KEY_TYPE_NAME
	// + "&resultType=hits";

//	private static final int DEFAULT_MAX_FEATURES = 100;
	private static final int DEFAULT_PROJECTION = 4326;

	private static final String FORMAT_JSON[] = { "application/json" };
	private static final String FORMAT_GEO_JSON[] = { "application/json; subtype=geojson" };
	private static final String FORMAT_GML[] = { "text/xml; subtype=gml/3.1.1", "application/gml+xml; version=3.2", "gml3" };

//	private static final String TEST_URL = "https://www.datagrandest.fr/geoserver/region-grand-est/wfs";
//	private static final String TEST_URL = "https://www.datagrandest.fr/geoserver/region-grand-est/wfs?SERVICE=WFS&REQUEST=GetCapabilities";

//	public static void main(String[] args) throws Exception {
//		CkanResource resource = new CkanResource("test", "SAGE_Hydrographie_phreatiques_2010_CC48", "WFS", TEST_URL);
//
//		WFSFormat format = MapFluxHelper.getSelectedFormat(resource);
//		MapFluxHelper.retrieveJSONFromWFS(resource, format.getValue());
//	}

	public static InputStream retrieveJSONFromWFS(CkanResource resource, String format) throws Exception {
		return retrieveJSONFromWFS(resource, format, null);
	}

	public static InputStream retrieveJSONFromWFS(CkanResource resource, String format, Integer maxFeatures) throws Exception {
		String wfsUrl = buildWFSUrl(resource, format, maxFeatures);
		if (wfsUrl == null || wfsUrl.isEmpty()) {
			throw new Exception("Unable to build WFS URL from resource");
		}

		return new URL(wfsUrl).openStream();
	}

	private static String buildWFSUrl(CkanResource resource, String format, Integer maxFeatures) {
		String wfsUrl = extractWFSUrl(resource.getUrl());
		String typeName = resource.getName();
		// String maxFeaturesValue = maxFeatures != null ?
		// String.valueOf(maxFeatures) : String.valueOf(DEFAULT_MAX_FEATURES);
		String projection = String.valueOf(DEFAULT_PROJECTION);

		// try {
		// maxFeaturesValue = retrieveNbFeatures(wfsUrl, typeName);
		// } catch (Exception e) {
		// e.printStackTrace();
		// maxFeaturesValue = maxFeatures != null ? String.valueOf(maxFeatures)
		// : String.valueOf(DEFAULT_MAX_FEATURES);
		// }
		//
		// if (wfsUrl == null || wfsUrl.isEmpty() || typeName == null ||
		// typeName.isEmpty()) {
		// return null;
		// }

		boolean hasParameters = wfsUrl.indexOf("?") > 0;
		String url = wfsUrl;
		url += (hasParameters ? "&" : "?") + WFS_PARAM_SERVICE;
		url += "&" + WFS_PARAM_VERSION_1_1_0;
		url += "&" + WFS_PARAM_REQUEST_GET_FEATURE;
		url += "&" + WFS_PARAM_TYPE_NAME;
		url += "&" + WFS_PARAM_MAX_FEATURES;
		url += "&" + WFS_PARAM_OUTPUT_FORMAT;
		url += "&" + WFS_PARAM_PROJECTION;
		
		url = url.replace(KEY_TYPE_NAME, typeName);
		url = url.replace(KEY_FORMAT, format);
		// url = url.replace(KEY_MAX_FEATURES, maxFeaturesValue);
		// Temp
		url = url.replace("&maxFeatures=" + KEY_MAX_FEATURES, "");
		url = url.replace(KEY_PROJECTION, projection);
		return url;
	}

	// For now we support only json and GML
	public static WFSFormat getSelectedFormat(CkanResource resource) {
		try {
			boolean supportGML = false;
			
			List<String> availableFormats = getWFSAvailableFormats(resource);
			if (availableFormats != null) {
				for (String format : availableFormats) {
					if (isInArray(FORMAT_JSON, format.toLowerCase())) {
						return WFSFormat.FORMAT_JSON;
					}
					else  if (isInArray(FORMAT_GEO_JSON, format.toLowerCase())) {
						return WFSFormat.FORMAT_JSON_SUBTYPE_GEOJSON;
					}
					else if (isInArray(FORMAT_GML, format.toLowerCase())) {
						supportGML = true;
					}
				}
			}

			return supportGML ? WFSFormat.FORMAT_GML3 : WFSFormat.FORMAT_JSON;
		} catch (Exception e) {
			e.printStackTrace();
			return WFSFormat.FORMAT_JSON;
		}
	}
	
	private static boolean isInArray(String[] array, String value) {
		if (value != null && !value.isEmpty() && array != null) {
			for (String val : array) {
				if (value.equals(val)) {
					return true;
				}
			}
		}
		return false;
	}

	private static List<String> getWFSAvailableFormats(CkanResource resource) throws Exception {
		String wfsUrl = buildGetFeatureWFS(resource);
		if (wfsUrl == null || wfsUrl.isEmpty()) {
			throw new Exception("Unable to build WFS URL from resource");
		}

	    List<String> formats = new ArrayList<String>();
		try (InputStream is = new URL(wfsUrl).openStream()) {
			String xml = IOUtils.toString(is);

			Document doc = DocumentHelper.parseText(xml);
			
			XPath xpath = new Dom4jXPath("//*[local-name() = 'OperationsMetadata']/*[local-name() = 'Operation'][@name=\"GetFeature\"]/*[local-name() = 'Parameter'][@name=\"outputFormat\"]/*[local-name() = 'Value']");
			List<Node> inputElements = xpath.selectNodes(doc);
			
			for(int i = 0 ; i < inputElements.size() ; i++) {
				Node node = inputElements.get(i);
		    	formats.add(node.getText());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return formats;
	}

	private static String buildGetFeatureWFS(CkanResource resource) {
		String wfsUrl = extractWFSUrl(resource.getUrl());
		boolean hasParameters = wfsUrl.indexOf("?") > 0;
		
		String url = wfsUrl;
		url += (hasParameters ? "&" : "?") + WFS_PARAM_SERVICE;
		url += "&" + WFS_PARAM_VERSION_1_1_0;
		url += "&" + WFS_PARAM_REQUEST_GET_CAPABILITIES;
		
		return url;
	}
	
	private static String removeWFSUnwantedParameters(String url) {
		if (url == null || url.isEmpty()) {
			return url;
		}
		
		try {
			final URI uri = UriComponentsBuilder.fromHttpUrl(url)
			  .replaceQueryParam("service")
			  .replaceQueryParam("SERVICE")
			  .replaceQueryParam("request")
			  .replaceQueryParam("REQUEST")
			  .build().toUri();
			return uri.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return url;
		}
	}

	// private static String retrieveNbFeatures(String wfsUrl, String typeName)
	// throws Exception {
	// String nbFeaturesUrl = FEATURES_DEFAULT_URL;
	// nbFeaturesUrl = nbFeaturesUrl.replace(KEY_URL, wfsUrl);
	// nbFeaturesUrl = nbFeaturesUrl.replace(KEY_TYPE_NAME, typeName);
	//
	// try (InputStream inputStream = new URL(nbFeaturesUrl).openStream()) {
	// String response =
	// IOUtils.toString(Utils.getResourceAsStream(inputStream));
	//
	// System.out.println(response);
	// return String.valueOf(DEFAULT_MAX_FEATURES);
	// } catch (Exception e) {
	// e.printStackTrace();
	// throw e;
	// }
	// }

	private static String extractWFSUrl(String url) {
		if (url != null && url.isEmpty()) {
			return null;
		}
		
		return removeWFSUnwantedParameters(url);
	}

}
