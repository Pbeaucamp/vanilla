package bpm.vanilla.platform.core.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.CkanResource;

public class CkanUtils {
	
	public static final SimpleDateFormat longDf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	public static final SimpleDateFormat shortDf = new SimpleDateFormat("yyyy-MM-dd");
	
	private static final String KEY_DATASET_TYPE = "resource-type";
	private static final String VALUE_SERVICE_TYPE = "service";
	
	public static final String KEY_DATASET_DATE = "dataset-reference-date";
	public static final String KEY_METADATA_DATE = "metadata-date";
	public static final String KEY_TYPE = "type";
	public static final String KEY_VALUE = "value";
	public static final String VALUE_DATASET_CREATION = "creation";
	public static final String VALUE_DATASET_REVISION = "revision";
	public static final String VALUE_DATASET_PUBLICATION = "publication";
	public static final String VALUE_DATASET_EDITION = "edition";
	
	public static final String KEY_LICENCE = "licence";
	
	public static final String CODE_ID_DATASET_SERVICE = "id_dataset_code_service";
	public static final String CODE_ID_DATASET_UP_TO_DATE = "id_dataset_up_to_date";

	public static final String STATE_DELETED = "deleted";
	
	public static final String KEY_SOURCE = "FTP_API";
	public static final String VALUE_HUB = "hub";
	
	public static final String KEY_THEMES = "themes";

	public static CkanPackage parsePackage(JSONObject resultJson, Date lastHarvestDate, boolean filterUpToDate, boolean filterService) throws JSONException {
		String id = !resultJson.isNull("id") ? resultJson.getString("id") : null;

		if (id == null) {
			return null;
		}
		
		String state = !resultJson.isNull("state") ? resultJson.getString("state") : null;
		if (state != null && state.equals(STATE_DELETED)) {
			return null;
		}
		
		String name = !resultJson.isNull("name") ? resultJson.getString("name") : null;
		boolean isPrivate = !resultJson.isNull("private") ? resultJson.getBoolean("private") : false;
		String title = !resultJson.isNull("title") ? resultJson.getString("title") : null;
		String description = !resultJson.isNull("notes") ? resultJson.getString("notes") : null;
		String licenseId = !resultJson.isNull("license_id") ? resultJson.getString("license_id") : null;

		JSONObject organisationObject = !resultJson.isNull("organization") ? resultJson.getJSONObject("organization") : null;
		String orgName = !organisationObject.isNull("name") ? organisationObject.getString("name") : null;

		CkanPackage pack = new CkanPackage(id, name);
		pack.setTitle(title);
		pack.setPrivate(isPrivate);
		pack.setDescription(description);
		pack.setOrg(orgName);
		pack.setLicenseId(licenseId);
		
		List<CkanResource> resources = new ArrayList<CkanResource>();
		JSONArray jsonResources = !resultJson.isNull("resources") ? resultJson.getJSONArray("resources") : null;
		if (jsonResources != null) {
			for (int i = 0; i < jsonResources.length(); i++) {
				try {
					JSONObject jsonResource = (JSONObject) jsonResources.get(i);
					String resourceId = !jsonResource.isNull("id") ? jsonResource.getString("id") : null;
					String resourceFormat = !jsonResource.isNull("format") ? jsonResource.getString("format") : null;
					String resourceName = !jsonResource.isNull("name") ? jsonResource.getString("name") : null;
					String resourceUrl = !jsonResource.isNull("url") ? jsonResource.getString("url") : null;
					String resourceDescription = !jsonResource.isNull("description") ? jsonResource.getString("description") : null;
					boolean datastoreActive = !jsonResource.isNull("datastore_active") ? jsonResource.getBoolean("datastore_active") : false;
					
					//We set the name with the description if null
					if (resourceName == null) {
						// We try to set the name with the URL
						// If it match only the format, we set format.format
						String resourceNameFromUrl = resourceUrl != null && resourceUrl.lastIndexOf("/") >= 0 ? resourceUrl.substring(resourceUrl.lastIndexOf("/") + 1) : null;
						if (resourceNameFromUrl.equalsIgnoreCase(resourceFormat)) {
							resourceNameFromUrl = name + "." + resourceFormat;
						}
						else if (!resourceNameFromUrl.contains(resourceFormat)) {
							resourceNameFromUrl = resourceNameFromUrl + "." + resourceFormat;
						}
						resourceName = resourceNameFromUrl;
					}
					
					if (resourceFormat == null || resourceFormat.isEmpty() 
							|| resourceFormat.equalsIgnoreCase("WMS") || resourceFormat.equalsIgnoreCase("WFS")) {
						//If format is empty or null, we check if there is a property 'resource_locator_protocol' which can help define the resource format
						String resourceLocator = !jsonResource.isNull("resource_locator_protocol") ? jsonResource.getString("resource_locator_protocol") : null;
						if (resourceLocator != null && resourceLocator.equals("OGC:WFS")) {
							resourceFormat = D4CFormat.WFS.name();
						}
						else if (resourceLocator != null && resourceLocator.equals("OGC:WMS")) {
							resourceFormat = D4CFormat.WMS.name();
						}
					}
					
					String creationDate = !jsonResource.isNull("created") ? jsonResource.getString("created") : null;
					String lastModificationDate = !jsonResource.isNull("last_modified") ? jsonResource.getString("last_modified") : null;
					
					CkanResource resource = new CkanResource(resourceId, resourceName, resourceDescription, resourceFormat, resourceUrl, creationDate, lastModificationDate);
					resource.setDatastoreActive(datastoreActive);
					resources.add(resource);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		pack.setResources(resources);
		
		JSONArray jsonExtras = !resultJson.isNull("extras") ? resultJson.getJSONArray("extras") : null;
		if (jsonExtras != null) {
			for (int i = 0; i < jsonExtras.length(); i++) {
				JSONObject jsonExtra = (JSONObject) jsonExtras.get(i);
				String key = !jsonExtra.isNull("key") ? jsonExtra.getString("key") : null;
				String value = !jsonExtra.isNull("value") ? jsonExtra.getString("value") : null;
				
				if (filterService && key.equals(KEY_DATASET_TYPE) && value.equals(VALUE_SERVICE_TYPE)) {
					return new CkanPackage(CODE_ID_DATASET_SERVICE, CODE_ID_DATASET_SERVICE);
				}
				
				
				if (key.equals(KEY_METADATA_DATE) && value != null && !value.isEmpty()) {
					try {
						Date metadataDate = value != null && !value.isEmpty() ? longDf.parse(value) : null;
						pack.setMetadataDate(metadataDate);
						
						if (filterUpToDate && metadataDate != null && lastHarvestDate != null && metadataDate.before(lastHarvestDate)) {
							return new CkanPackage(CODE_ID_DATASET_UP_TO_DATE, CODE_ID_DATASET_UP_TO_DATE);
						}
					} catch(Exception e) {
						try {
							Date metadataDate = value != null && !value.isEmpty() ? shortDf.parse(value) : null;
							pack.setMetadataDate(metadataDate);
							
							if (filterUpToDate && metadataDate != null && lastHarvestDate != null && metadataDate.before(lastHarvestDate)) {
								return new CkanPackage(CODE_ID_DATASET_UP_TO_DATE, CODE_ID_DATASET_UP_TO_DATE);
							}
						} catch(Exception e1) {
							e1.printStackTrace();
						}
					}
				}
				
				pack.putExtra(key, value);
			}
		}

		JSONArray jsonTags = !resultJson.isNull("tags") ? resultJson.getJSONArray("tags") : null;
		if (jsonTags != null) {
			for (int i = 0; i < jsonTags.length(); i++) {
				JSONObject jsonTag = (JSONObject) jsonTags.get(i);
				String tagName = !jsonTag.isNull("name") ? jsonTag.getString("name") : null;
				pack.putKeyword(tagName);
			}
		}
		
		return pack;
	}
	
	public static String clearValue(String value, String spaceReplacement) {
		if (value == null) {
			return value;
		}
		
		value = value.replace("\uFEFF", "");
		value = value.replaceAll("\\s+", spaceReplacement);
		value = value.toLowerCase();
		value = value.replaceAll("'", "");
		value = StringUtils.stripAccents(value);
		return value;
	}
}
