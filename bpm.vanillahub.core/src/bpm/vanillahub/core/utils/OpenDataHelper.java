package bpm.vanillahub.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bpm.vanillahub.core.beans.activities.attributes.DataGouvDataset;
import bpm.vanillahub.core.beans.activities.attributes.DataGouvResource;
import bpm.vanillahub.core.beans.activities.attributes.DataGouvSummary;

public class OpenDataHelper {

	private static final String DATA = "data";
	private static final String TOTAL = "total";
	private static final String ID = "id";
	private static final String TITLE = "title";
	private static final String DESCRIPTION = "description";
	private static final String CREATED_AT = "created_at";
	private static final String LAST_MODIFIED = "last_modified";
	private static final String URL = "url";
	private static final String FORMAT = "format";
	private static final String RESOURCES = "resources";

	public static DataGouvSummary getDatasets(String openDataUrl) throws IOException, JSONException {
		List<DataGouvDataset> datasets = new ArrayList<>();

		String result = sendMessage(openDataUrl);
		if (result != null && !result.isEmpty()) {
			JSONObject jsonData = new JSONObject(result);
			int nbPage = !jsonData.isNull(TOTAL) ? jsonData.getInt(TOTAL) : 0;
			JSONArray jsonDatasets = !jsonData.isNull(DATA) ? jsonData.getJSONArray(DATA) : null;
			for (int i = 0; i < jsonDatasets.length(); i++) {
				DataGouvDataset dataset = convert(jsonDatasets.getJSONObject(i));
				if (dataset != null) {
					datasets.add(dataset);
				}
			}
			
			return new DataGouvSummary(nbPage, datasets);
		}

		return null;
	}

	public static DataGouvResource getResource(String datasetUrl, String resourceId) throws IOException, JSONException {
		String result = sendMessage(datasetUrl);
		if (result != null && !result.isEmpty()) {
			JSONObject jsonDataset = new JSONObject(result);
			DataGouvDataset dataset = convert(jsonDataset);

			if (dataset != null && dataset.getResources() != null) {
				for (DataGouvResource resource : dataset.getResources()) {
					if (resource.getId() != null && resource.getId().equals(resourceId)) {
						return resource;
					}
				}
			}
		}

		return null;
	}

	private static DataGouvDataset convert(JSONObject jsonDataset) throws JSONException {
		String id = !jsonDataset.isNull(ID) ? jsonDataset.getString(ID) : "";
		String title = !jsonDataset.isNull(TITLE) ? jsonDataset.getString(TITLE) : "";
		String description = !jsonDataset.isNull(DESCRIPTION) ? jsonDataset.getString(DESCRIPTION) : "";
		String createdAt = !jsonDataset.isNull(CREATED_AT) ? jsonDataset.getString(CREATED_AT) : "";
		String lastModified = !jsonDataset.isNull(LAST_MODIFIED) ? jsonDataset.getString(LAST_MODIFIED) : "";

		List<DataGouvResource> resources = new ArrayList<DataGouvResource>();
		JSONArray tagsJson = !jsonDataset.isNull(RESOURCES) ? jsonDataset.getJSONArray(RESOURCES) : null;
		if (tagsJson != null) {
			for (int i = 0, count = tagsJson.length(); i < count; i++) {
				JSONObject jsonResource = tagsJson.getJSONObject(i);

				String resourceId = !jsonResource.isNull(ID) ? jsonResource.getString(ID) : "";
				String resourceTitle = !jsonResource.isNull(TITLE) ? jsonResource.getString(TITLE) : "";
				String resourceDescription = !jsonResource.isNull(DESCRIPTION) ? jsonResource.getString(DESCRIPTION) : "";
				String resourceCreatedAt = !jsonResource.isNull(CREATED_AT) ? jsonResource.getString(CREATED_AT) : "";
				String resourceLastModified = !jsonResource.isNull(LAST_MODIFIED) ? jsonResource.getString(LAST_MODIFIED) : "";
				String resourceFormat = !jsonResource.isNull(FORMAT) ? jsonResource.getString(FORMAT) : "";
				String resourceUrl = !jsonResource.isNull(URL) ? jsonResource.getString(URL) : "";

				resources.add(new DataGouvResource(resourceId, resourceTitle, resourceDescription, resourceCreatedAt, resourceLastModified, resourceFormat, resourceUrl));
			}
		}

		return new DataGouvDataset(id, title, description, createdAt, lastModified, resources);
	}

	private static String sendMessage(String openDataUrl) throws IOException {
		URL url = new URL(openDataUrl);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
		httpConn.setRequestMethod("GET");
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);

		InputStream is = httpConn.getInputStream();

		String result = IOUtils.toString(is, "UTF-8");
		is.close();
		httpConn.disconnect();

		return result;
	}
}
