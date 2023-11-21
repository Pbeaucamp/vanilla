package bpm.vanilla.platform.core.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import bpm.vanilla.platform.core.beans.meta.MetaLink;
import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.CkanResource;
import bpm.vanilla.platform.core.beans.resources.D4CItem;
import bpm.vanilla.platform.core.beans.resources.D4CItemMap;
import bpm.vanilla.platform.core.beans.resources.D4CItemTable;
import bpm.vanilla.platform.core.beans.resources.D4CItemVisualization;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

public class D4CHelper {

	public static final String SEARCH_PACKAGE = "/d4c/api/v1/datasets";
	public static final String FIND_PACKAGE = "/d4c/api/v1/dataset/find";
	public static final String REMOVE_PACKAGE = "/d4c/api/v1/dataset/remove";
	public static final String MANAGE_DATASET = "/d4c/api/v1/dataset/manage";
	public static final String REMOVE_RESOURCES = "/d4c/api/v1/dataset/resources_remove";
	public static final String ADD_RESOURCE = "/d4c/api/v1/dataset/resource_add";
	public static final String REMOVE_RESOURCE = "/d4c/api/v1/dataset/resource_remove";
	public static final String GET_THEMES = "/d4c/api/themes";
	public static final String GET_VISUALIZATIONS = "/d4c/api/v1/visualizations";
	public static final String DICTIONNARY = "/d4c/api/v1/dataset/resource/dictionnary";

	private static final List<String> METHOD_WITHOUT_AUTH = Arrays.asList(GET_THEMES, GET_VISUALIZATIONS);

	public static final String[] LICENCE_CODES = { "other-at", "other-pd", "other-closed", "other-nc", "other-open", "cc-by", "cc-by-sa", "cc-zero", "cc-nc", "gfdl", "notspecified", "odc-by", "odc-odbl", "odc-pddl", "uk-ogl" };
	public static final String[] LICENCE_NAMES = { "Autre (Attribution)", "Autre (Domaine Public)", "Autre (Fermé)", "Autre (Non-Commercial)", "Autre (Ouvert)", "Creative Commons Attribution", "Creative Commons Attribution Share-Alike", "Creative Commons CCZero", "Creative Commons Non-Commercial (n'importe laquelle)", "GNU Free Documentation License", "Licence non spécifiée", "Open Data Commons Attribution License", "Open Data Commons Open Database License (ODbL)", "Open Data Commons Public Domain Dedication and License (PDDL)", "UK Open Government Licence (OGL)" };

	public static final String LICENCE_DEFAULT_OPEN = "licence-ouverte";
	public static final String LICENCE_DEFAULT_CLOSE = "other-closed";

	public static final String TAG_OPEN = "opendata";
	public static final String TAG_OPEN_FR = "donnéesouvertes";
	
	public static final String TAG_DESCRIPTION = "description";
	public static final String TAG_PRIVATE = "dataset-private";

	private String d4cUrl;
	private String org;
	private String login;
	private String password;
	private String vanillaFilesPath;

	public D4CHelper() {
		VanillaConfiguration vanillaConfig = ConfigurationManager.getInstance().getVanillaConfiguration();
		this.org = org != null ? org : vanillaConfig.getProperty(VanillaConfiguration.P_CKAN_ORG);
		this.d4cUrl = vanillaConfig.getProperty(VanillaConfiguration.P_D4C_URL);
		this.vanillaFilesPath = vanillaConfig.getProperty(VanillaConfiguration.P_VANILLA_FILES);
		// TODO: Get login / password from config file
		// this.login = login;
		// this.password = password;
	}

	public D4CHelper(String d4cUrl, String org, String login, String password) {
		this(d4cUrl, org, login, password, null);
		VanillaConfiguration vanillaConfig = ConfigurationManager.getInstance().getVanillaConfiguration();
		this.vanillaFilesPath = vanillaConfig.getProperty(VanillaConfiguration.P_VANILLA_FILES);
	}

	public D4CHelper(String d4cUrl, String org, String login, String password, String filePath) {
		this.d4cUrl = d4cUrl;
		this.org = org;
		this.login = login;
		this.password = password;
		this.vanillaFilesPath = filePath;
		
		// Small tricks for the dev environment
		if (d4cUrl.contains("data4citizen-dev") || d4cUrl.contains("data4citizen-9-dev")) {
			this.d4cUrl = "http://localhost:8080";
		}
	}

	public boolean testConnection() throws Exception {
		try {
			int packageNumber = getCkanPackageNumber(null);
			return packageNumber >= 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<Theme> getThemes() throws Exception {
		String result = callD4C(d4cUrl, GET_THEMES, null);
		JSONArray json = new JSONArray(result);
		if (json != null && json.length() > 0) {
			List<Theme> themes = new ArrayList<D4CHelper.Theme>();
			for (int i = 0; i < json.length(); i++) {
				JSONObject item = json.getJSONObject(i);
				String title = !item.isNull("title") ? item.getString("title") : null;
				String label = !item.isNull("label") ? item.getString("label") : null;
				String url = !item.isNull("url") ? item.getString("url") : null;
				String urlLight = !item.isNull("url_light") ? item.getString("url_light") : null;
				if (title != null && !title.isEmpty()) {
					themes.add(new Theme(title, label, url, urlLight));
				}
			}
			return themes;
		}

		throw new Exception("Unable to find themes");
	}

	public CkanPackage findCkanPackage(String datasetId) throws Exception {
		MultipartEntityBuilder multipart = MultipartEntityBuilder.create()
				.addPart("dataset_id", new StringBody(datasetId, ContentType.TEXT_PLAIN.withCharset("UTF-8")));
		HttpEntity entity = multipart.build();

		String result = callD4C(d4cUrl, FIND_PACKAGE, entity);
		JSONObject json = new JSONObject(result);
		String status = !json.isNull("status") ? json.getString("status") : null;

		if (status.equals("success")) {
			JSONObject resultJson = !json.isNull("result") ? json.getJSONObject("result") : null;
			return resultJson != null ? CkanUtils.parsePackage(resultJson, null, false, false) : null;
		}

		JSONObject resultJson = !json.isNull("result") ? json.getJSONObject("result") : null;
		if (resultJson != null) {
			String message = !resultJson.isNull("message") ? resultJson.getString("message") : null;
			throw new Exception("Unable to find dataset (" + message + ")");
		}
		throw new Exception("Unable to find dataset (" + result + ")");
	}

	public int getCkanPackageNumber(String organisation) throws Exception {

		String organisationParam = organisation != null && !organisation.isEmpty() ? "&fq=organization:" + organisation : "";

		MultipartEntityBuilder multipart = MultipartEntityBuilder.create().addPart("q", new StringBody("rows=0&start=0" + organisationParam, ContentType.TEXT_PLAIN));
		HttpEntity entity = multipart.build();

		String result = callD4C(d4cUrl, SEARCH_PACKAGE, entity);
		JSONObject json = new JSONObject(result);
		String status = !json.isNull("status") ? json.getString("status") : null;
		if (status.equals("success")) {
			JSONObject resultJson = !json.isNull("result") ? json.getJSONObject("result") : null;
			return !resultJson.isNull("count") ? resultJson.getInt("count") : 0;
		}

		JSONObject resultJson = !json.isNull("result") ? json.getJSONObject("result") : null;
		if (resultJson != null) {
			String message = !resultJson.isNull("message") ? resultJson.getString("message") : null;
			throw new Exception("Unable to find dataset number (" + message + ")");
		}
		throw new Exception("Unable to find dataset number (" + result + ")");
	}

	public List<CkanPackage> getCkanPackagesByChunk(String organisation, int numberOfDataset, int start) throws Exception {
		String organisationParam = organisation != null && !organisation.isEmpty() ? "&fq=organization:" + organisation : "";

		MultipartEntityBuilder multipart = MultipartEntityBuilder.create().addPart("q", new StringBody("rows=" + numberOfDataset + "&start=" + start + organisationParam, ContentType.TEXT_PLAIN));
		HttpEntity entity = multipart.build();

		String result = callD4C(d4cUrl, SEARCH_PACKAGE, entity);
		JSONObject json = new JSONObject(result);
		String status = !json.isNull("status") ? json.getString("status") : null;

		if (status.equals("success")) {
			List<CkanPackage> packages = new ArrayList<CkanPackage>();

			JSONObject resultJson = !json.isNull("result") ? json.getJSONObject("result") : null;
			JSONArray jsonPackages = !resultJson.isNull("results") ? resultJson.getJSONArray("results") : null;
			if (jsonPackages != null) {
				for (int i = 0; i < jsonPackages.length(); i++) {
					JSONObject jsonPackage = (JSONObject) jsonPackages.get(i);
					CkanPackage pack = CkanUtils.parsePackage(jsonPackage, null, false, false);
					if (pack != null) {
						packages.add(pack);
					}
				}
			}
			return packages;
		}

		JSONObject resultJson = !json.isNull("result") ? json.getJSONObject("result") : null;
		if (resultJson != null) {
			String message = !resultJson.isNull("message") ? resultJson.getString("message") : null;
			throw new Exception("Unable to find dataset (" + message + ")");
		}
		throw new Exception("Unable to find dataset (" + result + ")");
	}
	

	/**
	 * This is the main method to use to upload a resource
	 * 
	 * It update or create a new dataset
	 * It keeps the dictionnary if it exist on the ressource
	 * 
	 * @param organisation
	 * @param datasetName
	 * @param existingPackage
	 * @param uploadUrl
	 * @param fileName
	 * @param inputStream
	 * @throws Exception 
	 */
	public D4CResult uploadFileResourceAndCreatePackageIfNeeded(String organisation, String datasetName, CkanPackage existingPackage, String fileName, 
			InputStream inputStream, List<MetaLink> links, HashMap<String, String> extras,
			boolean isMainResource, boolean isFile, String format) throws Exception {

		CkanPackage pack = existingPackage != null ? clone(existingPackage) : new CkanPackage();
		//We set the datasetName from the CibleActivity if it exist
		pack.setName(datasetName != null && !datasetName.isEmpty() ? datasetName : pack.getName());
		
		if (links != null) {
			for (MetaLink link : links) {
				pack.putExtra(link.getMeta().getKey(), link.getValue() != null ? link.getValue().getValue() : null);
			}
		}
		
		if (extras != null) {
			for (String key : extras.keySet()) {
				pack.putExtra(key, extras.get(key));
			}
		}
		
		HashMap<String, JSONArray> dictionaries = new HashMap<String, JSONArray>();
		CkanPackage existingDataset = findCkanPackage(pack.getName());
		if (existingDataset != null) {
			pack.setId(existingDataset.getId());
			pack.setTitle(existingDataset.getTitle());
			pack.setDescription(existingDataset.getDescription());
			pack.setPrivate(existingDataset.isPrivate());
			pack.setLicenseId(existingDataset.getLicenseId());
			pack.setKeywords(existingDataset.getKeywords());
			
			// Checking if existing ressource match to retrive dictionnary (later we will update ressources)
			CkanResource existingResource = null;
			if (existingDataset.getResources() != null) {
				for (CkanResource resource : existingDataset.getResources()) {
					JSONArray fields = getDictionnary(resource.getId());
					dictionaries.put(resource.getName(), fields);

					String cleanResourceName = cleanResourceName(fileName);
					if (resource.getName().equals(cleanResourceName)) {
						existingResource = resource;
					}
				}
			}

			pack = manageDataset(pack, true);
			
			// We delete only existing resources if it exist (see old code bellow which delete all resources) - Maybe add the ooption in the future
			if (existingResource != null) {
				deleteResource(existingResource.getId());
			}
//			else {
//				// We delete the resources before uploading new ones
//				helper.deleteResources(existingDataset.getId());
//			}
		}
		else {
			pack = manageDataset(pack, false);
		}

		boolean updateResource = false;
		if (isMainResource || isFile) {
			CkanResource resource = new CkanResource(null, fileName, format, null);
//			if (existingResource != null) {
//				resource.setId(existingResource.getId());
//			}
			
			D4CResult result = uploadFileResource(pack.getId(), resource, inputStream, updateResource);
			if (result.getStatus() != Status.ERROR) {
				reuploadDictionnary(this, pack.getId(), dictionaries);
			}
			return result;
		}
		else {
			String json = IOUtils.toString(inputStream);
			JSONObject item = new JSONObject(json);

			String id = !item.isNull("id") ? item.getString("id") : "";
			String name = !item.isNull("name") ? item.getString("name") : "";
			String description = !item.isNull("description") ? item.getString("description") : "";
			format = !item.isNull("format") ? item.getString("format") : "";
			String url = !item.isNull("url") ? item.getString("url") : "";

			CkanResource resource = new CkanResource(id, name, description, format, url);
			D4CResult result = uploadResource(pack.getId(), fileName, format, resource, false, true, updateResource);
			if (result.getStatus() != Status.ERROR) {
				reuploadDictionnary(this, pack.getId(), dictionaries);
			}
			return result;
		}
	}
	
	private CkanPackage clone(CkanPackage ckanPackage) {
		CkanPackage pack = new CkanPackage();
		pack.setDescription(ckanPackage.getDescription());
		pack.setExtras(ckanPackage.getExtras());
		pack.setId(ckanPackage.getId());
		pack.setKeywords(ckanPackage.getKeywords());
		pack.setLicenseId(ckanPackage.getLicenseId());
		pack.setMetadataDate(ckanPackage.getMetadataDate());
		pack.setName(ckanPackage.getName());
		pack.setOrg(ckanPackage.getOrg());
		pack.setPrivate(ckanPackage.isPrivate());
		pack.setResources(ckanPackage.getResources());
		pack.setSelectedResource(ckanPackage.getSelectedResource());
		pack.setTitle(ckanPackage.getTitle());
		return pack;
	}

	private void reuploadDictionnary(D4CHelper helper, String datasetId, HashMap<String, JSONArray> dictionaries) {
		try {
			CkanPackage updatedDataset = helper.findCkanPackage(datasetId);
			if (updatedDataset.getResources() != null) {
				for (CkanResource resource : updatedDataset.getResources()) {
					JSONArray dictionnary = dictionaries.get(resource.getName());
					if (dictionnary != null) {
						helper.updateDictionnary(resource.getId(), dictionnary);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	

	public CkanPackage manageDataset(CkanPackage pack, boolean update) throws Exception {
		String title = pack.getTitle() != null && !pack.getTitle().isEmpty() ? pack.getTitle() : pack.getName();
		
		//We try to get description and if the dataset is private then we remove it from extras
		String description = pack.getDescription() != null && !pack.getDescription().isEmpty() ? pack.getDescription() : pack.getExtras().get(TAG_DESCRIPTION);
		pack.getExtras().remove(TAG_DESCRIPTION);
		boolean isPrivate = pack.getExtras().get(TAG_PRIVATE) != null && (pack.getExtras().get(TAG_PRIVATE).equalsIgnoreCase("true") || pack.getExtras().get(TAG_PRIVATE).equalsIgnoreCase("1")) ? true : pack.isPrivate();
		pack.getExtras().remove(TAG_PRIVATE);
		
		String jsonExtras = new JSONObject(pack.getExtras()).toString();
		String jsonTags = cleanKeyword(pack.getKeywords()).toString();

		MultipartEntityBuilder multipart = MultipartEntityBuilder.create()
				.addPart("name", new StringBody(pack.getName(), ContentType.TEXT_PLAIN.withCharset("UTF-8")))
				.addPart("title", new StringBody(title, ContentType.TEXT_PLAIN.withCharset("UTF-8")))
				.addPart("description", new StringBody(description != null ? description : "", ContentType.TEXT_PLAIN.withCharset("UTF-8")))
				.addPart("selected_private", new StringBody(String.valueOf(isPrivate), ContentType.TEXT_PLAIN))
				.addPart("selected_org", new StringBody(org, ContentType.TEXT_PLAIN))
				.addPart("selected_lic", new StringBody(pack.getLicenseId(), ContentType.TEXT_PLAIN))
				.addPart("extras", new StringBody(jsonExtras, ContentType.APPLICATION_JSON))
				.addPart("tags", new StringBody(jsonTags, ContentType.APPLICATION_JSON));
		if (update) {
			multipart.addPart("dataset_id", new StringBody(pack.getId(), ContentType.TEXT_PLAIN));
		}

		HttpEntity entity = multipart.build();

		String result = callD4C(d4cUrl, MANAGE_DATASET, entity);
		JSONObject json = new JSONObject(result);
		String status = !json.isNull("status") ? json.getString("status") : null;

		if (status.equals("success")) {
			String datasetId = !json.isNull("result") ? json.getString("result") : null;
			if (datasetId == null) {
				throw new Exception("Unable to create dataset. Please contact an administrator of D4C.");
			}

			pack.setId(datasetId);
			return pack;
		}

		JSONObject resultJson = !json.isNull("result") ? json.getJSONObject("result") : null;
		if (resultJson != null) {
			String message = !resultJson.isNull("message") ? resultJson.getString("message") : null;
			throw new Exception("Unable to create dataset (" + message + ")");
		}
		throw new Exception("Unable to create dataset (" + result + ")");
	}

	private JSONArray cleanKeyword(List<String> keywords) {
		JSONArray array = new JSONArray();
		if (keywords != null) {
			for (String keyword : keywords) {
				// We replace weird dash
				String value = keyword.replace("–", "-");
				// We replace ;
				value = keyword.replace(";", " ");
				value = keyword.replace("©", "");
				array.put(value);
			}
		}
		return array;
	}

	public void deleteDataset(String datasetId) throws Exception {
		MultipartEntityBuilder multipart = MultipartEntityBuilder.create().addPart("dataset_id", new StringBody(datasetId, ContentType.TEXT_PLAIN));
		HttpEntity entity = multipart.build();

		String result = callD4C(d4cUrl, REMOVE_PACKAGE, entity);
		JSONObject json = new JSONObject(result);
		String status = !json.isNull("status") ? json.getString("status") : null;

		if (status.equals("success")) {
			return;
		}

		JSONObject resultJson = !json.isNull("result") ? json.getJSONObject("result") : null;
		if (resultJson != null) {
			String message = !resultJson.isNull("message") ? resultJson.getString("message") : null;
			throw new Exception("Unable to remove dataset (" + message + ")");
		}
		throw new Exception("Unable to remove dataset (" + result + ")");
	}

	public void deleteResources(String datasetId) throws Exception {
		MultipartEntityBuilder multipart = MultipartEntityBuilder.create().addPart("dataset_id", new StringBody(datasetId, ContentType.TEXT_PLAIN));
		HttpEntity entity = multipart.build();

		String result = callD4C(d4cUrl, REMOVE_RESOURCES, entity);
		JSONObject json = new JSONObject(result);
		String status = !json.isNull("status") ? json.getString("status") : null;

		if (status.equals("success")) {
			return;
		}

		JSONObject resultJson = !json.isNull("result") ? json.getJSONObject("result") : null;
		if (resultJson != null) {
			String message = !resultJson.isNull("message") ? resultJson.getString("message") : null;
			throw new Exception("Unable to remove resources for dataset (" + message + ")");
		}
		throw new Exception("Unable to remove resources for dataset (" + result + ")");
	}

	public void deleteResource(String resourceId) throws Exception {
		MultipartEntityBuilder multipart = MultipartEntityBuilder.create().addPart("resource_id", new StringBody(resourceId, ContentType.TEXT_PLAIN));
		HttpEntity entity = multipart.build();

		String result = callD4C(d4cUrl, REMOVE_RESOURCE, entity);
		JSONObject json = new JSONObject(result);
		String status = !json.isNull("status") ? json.getString("status") : null;

		if (status.equals("success")) {
			return;
		}

		JSONObject resultJson = !json.isNull("result") ? json.getJSONObject("result") : null;
		if (resultJson != null) {
			String message = !resultJson.isNull("message") ? resultJson.getString("message") : null;
			throw new Exception("Unable to remove resource (" + message + ")");
		}
		throw new Exception("Unable to remove resource (" + result + ")");
	}
	
	public D4CResult uploadFileResource(String datasetId, String fileName, String format, InputStream is) throws Exception {
		CkanResource resource = new CkanResource(null, fileName, format, null);
		return uploadFileResource(datasetId, resource, is, false);
	}

	public D4CResult uploadFileResource(String datasetId, CkanResource resource, InputStream is, boolean updateResource) throws Exception {
		String fileName = resource.getName();
		String format = resource.getFormat();
		
		fileName = removeAccentAndUpperCaseAndSpace(fileName, "-");
		String fileNameWithFormat = fileName;
		if (format != null && !format.isEmpty() && !fileName.endsWith(format)) {
			fileName += "." + format;
		}
		
		ContentType fileType = null;
		if (format.equalsIgnoreCase("csv")) {
			fileType = ContentType.TEXT_PLAIN.withCharset("UTF-8");
		}
		else if (format.equalsIgnoreCase("xml")) {
			fileType = ContentType.TEXT_XML.withCharset("UTF-8");
		}
		else if (format.equalsIgnoreCase("json")) {
			fileType = ContentType.TEXT_PLAIN.withCharset("UTF-8");
		}
		else {
			fileType = ContentType.TEXT_PLAIN.withCharset("UTF-8");
		}

		MultipartEntityBuilder multipart = MultipartEntityBuilder.create()
				.addPart("resource_name", new StringBody(fileName, ContentType.TEXT_PLAIN))
				.addPart("format", new StringBody(format, ContentType.TEXT_PLAIN))
				.addPart("selected_data_id", new StringBody(datasetId, ContentType.TEXT_PLAIN))
				.addPart("unzip_zip", new StringBody("true", ContentType.TEXT_PLAIN))
				// .addPart("upload_file", cbFile);
				.addBinaryBody("upload_file", is, fileType, fileNameWithFormat);
		if (updateResource && resource.getId() != null && !resource.getId().isEmpty()) {
			multipart.addPart("selected_resource_id", new StringBody(resource.getId(), ContentType.TEXT_PLAIN));
		}
		HttpEntity entity = multipart.build();

		String result = callD4C(d4cUrl, ADD_RESOURCE, entity);

		// initialFile.delete();

		JSONObject json = new JSONObject(result);
		String status = !json.isNull("status") ? json.getString("status") : null;

		if (status.equals("success")) {
			JSONArray resultJson = json.optJSONArray("result");
			if (resultJson != null) {
				return new D4CResult(Status.SUCCESS, datasetId, resultJson.toString());
			}
		}

		JSONObject resultJson = !json.isNull("result") ? json.getJSONObject("result") : null;
		if (resultJson != null) {
			String message = !resultJson.isNull("message") ? resultJson.getString("message") : null;
			return new D4CResult(Status.ERROR, datasetId, message);
		}
		return new D4CResult(Status.ERROR, datasetId, result);
	}

	public D4CResult uploadResource(String datasetId, String fileName, String format, CkanResource resource) throws Exception {
		return uploadResource(datasetId, fileName, format, resource, false, true, false);
	}

	public D4CResult uploadResource(String datasetId, String fileName, String format, CkanResource resource, boolean manageFile, boolean unzip, boolean updateResource) throws Exception {
		fileName = removeAccentAndUpperCaseAndSpace(fileName, "-");

		String resourceUrl = URLEncoder.encode(resource.getUrl(), StandardCharsets.UTF_8.toString());

		MultipartEntityBuilder multipart = MultipartEntityBuilder.create()
				.addPart("resource_name", new StringBody(fileName, ContentType.TEXT_PLAIN))
				.addPart("format", new StringBody(format, ContentType.TEXT_PLAIN))
				.addPart("selected_data_id", new StringBody(datasetId, ContentType.TEXT_PLAIN))
				.addPart("manage_file", new StringBody(manageFile ? "true" : "false", ContentType.TEXT_PLAIN))
				.addPart("unzip_zip", new StringBody(unzip ? "true" : "false", ContentType.TEXT_PLAIN))
				.addPart("description", new StringBody(resource.getDescription(), ContentType.TEXT_PLAIN))
				.addPart("resource_url", new StringBody(resourceUrl, ContentType.TEXT_PLAIN));
		if (updateResource && resource.getId() != null && !resource.getId().isEmpty()) {
			multipart.addPart("selected_resource_id", new StringBody(resource.getId(), ContentType.TEXT_PLAIN));
		}
		HttpEntity entity = multipart.build();

		String result = callD4C(d4cUrl, ADD_RESOURCE, entity);

		JSONObject json = new JSONObject(result);
		String status = !json.isNull("status") ? json.getString("status") : null;
		if (status.equals("success")) {
			JSONArray resultJson = json.optJSONArray("result");
			if (resultJson != null) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < resultJson.length(); i++) {
					sb.append(resultJson.get(i).toString() + "\n");
				}

				return new D4CResult(Status.SUCCESS, datasetId, sb.toString());
			}
		}

		JSONObject resultJson = !json.isNull("result") ? json.getJSONObject("result") : null;
		if (resultJson != null) {
			String message = !resultJson.isNull("message") ? resultJson.getString("message") : null;
			return new D4CResult(Status.ERROR, datasetId, message);
		}
		return new D4CResult(Status.ERROR, datasetId, result);
	}
	
	public JSONArray getDictionnary(String resourceId) throws Exception {
		String result = callGetD4C(d4cUrl, DICTIONNARY + "?resourceId=" + resourceId);
		JSONObject json = new JSONObject(result);
		String status = !json.isNull("status") ? json.getString("status") : null;
		if (status.equals("success")) {
			return !json.isNull("result") ? json.optJSONArray("result") : null;
		}

		throw new Exception("Unable to find dictionnary");
	}
	
	public D4CResult updateDictionnary(String resourceId, JSONArray dictionnary) throws Exception {
		JSONObject item = new JSONObject();
		item.put("resourceId", resourceId);
		item.put("fields", dictionnary);

		HttpEntity entity = new StringEntity(item.toString(), ContentType.APPLICATION_JSON);

		String result = callD4C(d4cUrl, DICTIONNARY, entity);
		JSONObject json = new JSONObject(result);
		Boolean status = !json.isNull("success") ? json.getBoolean("success") : null;
		if (status != null && status) {
			return new D4CResult(Status.SUCCESS, null, "");
		}
		return new D4CResult(Status.ERROR, null, result);
	}

	public HashMap<String, HashMap<String, List<D4CItem>>> getVisualizations() throws Exception {
		String result = callD4C(d4cUrl, GET_VISUALIZATIONS, null);
		
		HashMap<String, HashMap<String, List<D4CItem>>> items = new HashMap<String, HashMap<String, List<D4CItem>>>();
		
		JSONObject json = new JSONObject(result);
		String status = !json.isNull("status") ? json.getString("status") : null;
		if (status.equals("success")) {
			JSONArray resultJson = json.optJSONArray("result");
			if (resultJson != null) {
				for (int i = 0; i < resultJson.length(); i++) {
					JSONObject item = resultJson.getJSONObject(i);
					
					String organization = !item.isNull("organization") ? item.getString("organization") : null;
					if (items.get(organization) == null) {
						items.put(organization, new HashMap<String, List<D4CItem>>());
					}
					
					String datasetName = !item.isNull("datasetName") ? item.getString("datasetName") : null;
					if (items.get(organization).get(datasetName) == null) {
						items.get(organization).put(datasetName, new ArrayList<D4CItem>());
					}

					String datasetId = !item.isNull("dataset_id") ? item.getString("dataset_id") : null;
					int userId = !item.isNull("user_id") ? item.getInt("user_id") : -1;
					String type = !item.isNull("type") ? item.getString("type") : null;
					String name = !item.isNull("name") ? item.getString("name") : null;
					String shareUrl = !item.isNull("share_url") ? item.getString("share_url") : null;
//					String iframe = !item.isNull("iframe") ? item.getString("iframe") : null;
//					String widget = !item.isNull("widget") ? item.getString("widget") : null;

					D4CItem d4cItem = null;
					if (type.equals("map")) {
						d4cItem = new D4CItemMap();
						((D4CItemMap) d4cItem).setUrl(shareUrl);
					}
					else if (type.equals("table")) {
						d4cItem = new D4CItemTable();
						((D4CItemTable) d4cItem).setUrl(shareUrl);
					}
					else if (type.equals("analyze")) {
						d4cItem = new D4CItemVisualization();
						((D4CItemVisualization) d4cItem).setUrl(shareUrl);
					}

					d4cItem.setOrganization(organization);
					d4cItem.setDatasetName(datasetName);
					d4cItem.setDatasetId(datasetId);
					d4cItem.setName(name);
					d4cItem.setUserId(userId);
					
					items.get(organization).get(datasetName).add(d4cItem);
				}
			}
			
			return items;
		}

		throw new Exception("Unable to find themes");
	}

	public String removeAccentAndUpperCaseAndSpace(String value, String spaceReplacement) {
		value = value.replace("\uFEFF", "");
		value = value.replaceAll("\\s+", spaceReplacement);
		// value = value.toLowerCase();
		value = value.replaceAll("'", "");
		value = StringUtils.stripAccents(value);
		return value;
	}
	
	public String cleanResourceName(String str) {
		str = str.replace("?", "");
		str = str.replace("`", "_");
		str = str.replace("'", "_");
		str = str.replace("-", "_");
		str = str.replace(" ", "_");
		str = str.replace("%", "1");
		str = str.replace("(", "1");
		str = str.replace(")", "1");
		str = str.replace("*", "1");
		str = str.replace("!", "1");
		str = str.replace("@", "1");
		str = str.replace("#", "1");
		str = str.replace("$", "1");
		str = str.replace("^", "1");
		str = str.replace("&", "1");
		str = str.replace("+", "1");
		str = str.replace(":", "1");
		str = str.replace(">", "1");
		str = str.replace("<", "1");
		str = str.replace("|", "_");
		str = str.toLowerCase();
		str = str.replaceAll("&([A-Za-z])(?:acute|cedil|caron|circ|grave|orn|ring|slash|th|tilde|uml);", "$1");
		str = str.replaceAll("&([A-Za-z]{2})(?:lig);", "$1");
		str = str.replaceAll("&[^;]+;", "");
		str = str.replace("-", "_");
		return str;
	}

	public InputStream getResourceFile(CkanResource resource) throws MalformedURLException, IOException {
		return new URL(resource.getUrl()).openStream();
	}

	public ByteArrayInputStream getResourceFileAsByteArray(CkanResource resource) throws MalformedURLException, IOException {
		InputStream inputStream = getResourceFile(resource);

		byte[] buff = new byte[8000];

		int bytesRead = 0;

		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		while ((bytesRead = inputStream.read(buff)) != -1) {
			bao.write(buff, 0, bytesRead);
		}

		byte[] data = bao.toByteArray();
		return new ByteArrayInputStream(data);
	}

	@SuppressWarnings("deprecation")
	private String callGetD4C(String d4cUrl, String method) throws Exception {
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();

		String encoding = Base64.getEncoder().encodeToString((login + ":" + password).getBytes());

		HttpGet getRequest;
		try {
			getRequest = new HttpGet(d4cUrl + method);

			// We don't apply security if method does not need it
			if (!METHOD_WITHOUT_AUTH.contains(method)) {
				getRequest.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
			}

			HttpResponse response = httpclient.execute(getRequest);
			int statusCode = response.getStatusLine().getStatusCode();
			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

			boolean hasError = false;

			StringBuilder sb = new StringBuilder();
			if (statusCode != 200) {
				hasError = true;
			}

			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}

			httpclient.close();
			if (hasError) {
				throw new Exception(sb.toString());
			}

			return sb.toString();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw ioe;
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
	}

	@SuppressWarnings("deprecation")
	private String callD4C(String d4cUrl, String method, HttpEntity reqEntity) throws Exception {
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();

		String encoding = Base64.getEncoder().encodeToString((login + ":" + password).getBytes());

		HttpPost postRequest;
		try {
			postRequest = new HttpPost(d4cUrl + method);

			// We don't apply security if method does not need it
			if (!METHOD_WITHOUT_AUTH.contains(method)) {
				postRequest.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
			}
			postRequest.setEntity(reqEntity);
			// postRequest.addHeader("X-CKAN-API-Key", apiKey);
			if (reqEntity instanceof StringEntity) {
				postRequest.addHeader("Content-type", "application/json");
			}

			HttpResponse response = httpclient.execute(postRequest);
			int statusCode = response.getStatusLine().getStatusCode();
			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

			boolean hasError = false;

			StringBuilder sb = new StringBuilder();
			if (statusCode != 200) {
				hasError = true;
			}

			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}

			httpclient.close();
			if (hasError) {
				throw new Exception(sb.toString());
			}

			return sb.toString();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw ioe;
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
	}

	public enum Status {
		SUCCESS, ERROR
	}

	public class D4CResult {

		private Status status;
		private String datasetId;
		private String message;

		public D4CResult(Status status, String datasetId, String message) {
			this.status = status;
			this.datasetId = datasetId;
			this.message = message;
		}

		public Status getStatus() {
			return status;
		}
		
		public String getDatasetId() {
			return datasetId;
		}

		public String getMessage() {
			return message;
		}
	}

	public class Dataset {

		private String name;
		private String title;
		private String notes;
		private List<Tag> tags = new ArrayList<Tag>();;
		private String license_id;
		private boolean isPrivate;
		private String state;
		private String owner_org;
		private JSONArray extras;

		public Dataset(String name, String title, String notes, String[] tags, String license_id, boolean isPrivate, String state, String owner_org, JSONArray extras) {
			this.name = name;
			this.title = title;
			this.notes = notes;
			if (tags != null) {
				for (String tag : tags) {
					this.tags.add(new Tag(tag));
				}
			}
			this.license_id = license_id;
			this.isPrivate = isPrivate;
			this.state = state;
			this.owner_org = owner_org;
			this.extras = extras;
		}

		public String getName() {
			return name;
		}

		public String getTitle() {
			return title;
		}

		public String getNotes() {
			return notes;
		}

		public List<Tag> getTags() {
			return tags;
		}

		public String getLicense_id() {
			return license_id;
		}

		public boolean isPrivate() {
			return isPrivate;
		}

		public String getState() {
			return state;
		}

		public String getOwner_org() {
			return owner_org;
		}

		public JSONArray getExtras() {
			return extras;
		}
	}

	public class Tag {

		private String name;

		public Tag(String text) {
			name = text;
		}

		public String getName() {
			return name;
		}
	}

	public class Theme {

		private String title;
		private String label;
		private String url;
		private String urlLight;

		public Theme(String title, String label, String url, String urlLight) {
			this.title = title;
			this.label = label;
			this.url = url;
			this.urlLight = urlLight;
		}

		public String getTitle() {
			return title;
		}

		public String getLabel() {
			return label;
		}

		public String getUrl() {
			return url;
		}

		public String getUrlLight() {
			return urlLight;
		}
	}

}
