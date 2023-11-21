package bpm.vanilla.platform.core.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.CkanResource;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

public class CkanHelper {

	public static final String GET_ORGANISATIONS = "/api/3/action/organization_list";
	public static final String GET_ORGANISATION_INFO = "/api/3/action/organization_show?include_datasets=true&id=";
	public static final String GET_PACKAGES = "/api/3/action/package_list";
	public static final String GET_PACKAGE_INFO = "/api/3/action/package_show?id=";
	public static final String CREATE_PACKAGE = "/api/3/action/package_create";
	public static final String UPDATE_PACKAGE = "/api/3/action/package_update";
	public static final String CREATE_RESOURCE = "/api/3/action/resource_create";
	public static final String UPDATE_RESOURCE = "/api/3/action/resource_update";
	public static final String DATASTORE_SEARCH = "/api/3/action/datastore_search";
	public static final String DATASTORE_UPDATE = "/api/3/action/datastore_create";
	public static final String CALCULATE_VISU = "/api/datasets/calculatevisu/";
	public static final String PUSH_DATAPUSHER = "/api/datapusher/";
	public static final String SEARCH_PACKAGE = "/api/3/action/package_search";
	
//	private static final String DEFAULT_DATASTORE_URL = "http://127.0.0.1:8800/job";

	private static final SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");

	public static final String[] LICENCE_CODES = { "other-at", "other-pd", "other-closed", "other-nc", "other-open", "cc-by", "cc-by-sa", "cc-zero", "cc-nc", "gfdl", "notspecified", "odc-by", "odc-odbl", "odc-pddl", "uk-ogl" };
	public static final String[] LICENCE_NAMES = { "Autre (Attribution)", "Autre (Domaine Public)", "Autre (Fermé)", "Autre (Non-Commercial)", "Autre (Ouvert)", "Creative Commons Attribution", "Creative Commons Attribution Share-Alike", "Creative Commons CCZero", "Creative Commons Non-Commercial (n'importe laquelle)", "GNU Free Documentation License", "Licence non spécifiée", "Open Data Commons Attribution License", "Open Data Commons Open Database License (ODbL)", "Open Data Commons Public Domain Dedication and License (PDDL)", "UK Open Government Licence (OGL)" };
	
	private String ckanUrl;
	private String d4cUrl;
	private String org;
	private String apiKey;
	private String vanillaFilesPath;

	/**
	 * If ckanUrl is null, we use the value in the properties
	 * 
	 * @param ckanUrl
	 */
	public CkanHelper(String ckanUrl, String org, String apiKey) {
		this(ckanUrl, org, apiKey, null, true);
	}

	public CkanHelper(String ckanUrl, String org, String apiKey, String filePath, boolean checkProperties) {
		if (checkProperties) {
			VanillaConfiguration vanillaConfig = ConfigurationManager.getInstance().getVanillaConfiguration();
			this.apiKey = apiKey != null ? apiKey : vanillaConfig.getProperty(VanillaConfiguration.P_CKAN_API_KEY);
			this.org = org != null ? org : vanillaConfig.getProperty(VanillaConfiguration.P_CKAN_ORG);
			this.ckanUrl = ckanUrl != null ? ckanUrl : vanillaConfig.getProperty(VanillaConfiguration.P_CKAN_URL);
			this.d4cUrl = vanillaConfig.getProperty(VanillaConfiguration.P_D4C_URL);
			this.vanillaFilesPath = vanillaConfig.getProperty(VanillaConfiguration.P_VANILLA_FILES);
		}
		else {
			this.apiKey = apiKey;
			this.org = org;
			this.ckanUrl = ckanUrl;
			this.vanillaFilesPath = filePath;
			
			
//			try {
//				VanillaConfiguration vanillaConfig = ConfigurationManager.getInstance().getVanillaConfiguration();
//				this.d4cUrl = vanillaConfig.getProperty(VanillaConfiguration.P_D4C_URL);
//				this.vanillaFilesPath = filePath != null && !filePath.isEmpty() ? filePath : vanillaConfig.getProperty(VanillaConfiguration.P_VANILLA_FILES);
//			} catch(Exception e) { }
		}
	}

	public CkanHelper() {
		this(null, null, null);
	}

	public boolean testConnection() throws Exception {
		String jsonStatus;
		try {
			jsonStatus = CommunicatorHelper.sendGetMessage(ckanUrl, GET_PACKAGES);
			if (jsonStatus == null || jsonStatus.isEmpty()) {
				return false;
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public List<String> getOrganisations() throws Exception {
		String jsonStatus = CommunicatorHelper.sendGetMessage(ckanUrl, GET_ORGANISATIONS);
		if (jsonStatus == null || jsonStatus.isEmpty()) {
			throw new Exception("Unable to get organisations for this url " + ckanUrl + ".");
		}

		JSONObject jsonObject = new JSONObject(jsonStatus);
		JSONArray resultJson = !jsonObject.isNull("result") ? jsonObject.getJSONArray("result") : null;

		List<String> organisations = new ArrayList<>();
		if (resultJson.length() > 0) {
			for (int i = 0; i < resultJson.length(); i++) {
				String organisation = resultJson.getString(i);
				organisations.add(organisation);
			}
		}
		return organisations;
	}

	public List<CkanPackage> getCkanPackages(String organisation) throws Exception {
		return getCkanPackages(null, organisation);
	}

	public List<CkanPackage> getCkanPackages(String ckanUrl, String organisation) throws Exception {
		ckanUrl = ckanUrl != null && !ckanUrl.isEmpty() ? ckanUrl : this.ckanUrl;

		String jsonStatus = CommunicatorHelper.sendGetMessage(ckanUrl, GET_ORGANISATION_INFO + organisation);
		if (jsonStatus == null || jsonStatus.isEmpty()) {
			throw new Exception("Unable to get the packages for this organisation " + organisation + ".");
		}

		JSONObject jsonObject = new JSONObject(jsonStatus);
		JSONObject resultJson = !jsonObject.isNull("result") ? jsonObject.getJSONObject("result") : null;

		List<CkanPackage> packages = new ArrayList<CkanPackage>();
		if (resultJson != null) {
			JSONArray jsonPackages = !resultJson.isNull("packages") ? resultJson.getJSONArray("packages") : null;
			if (jsonPackages != null) {
				for (int i = 0; i < jsonPackages.length(); i++) {
					try {
						JSONObject jsonPackage = (JSONObject) jsonPackages.get(i);
						String packId = !jsonPackage.isNull("id") ? jsonPackage.getString("id") : null;

						CkanPackage pack = getCkanPackage(ckanUrl, packId);
						if (pack != null) {
							packages.add(pack);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		return packages;
	}

	public CkanPackage getCkanPackage(String packageName) throws Exception {
		return getCkanPackage(null, packageName);
	}

	public int getCkanPackageNumberByOrganisation(String organisation) throws Exception {
		String organisationParam = organisation != null && !organisation.isEmpty() ? "&fq=organization:" + organisation : "";
		return getCkanPackageNumber(organisationParam);
	}

	public int getCkanPackageNumber(String parameters) throws Exception {
		parameters = "rows=0&start=0" + parameters;
		String jsonStatus = CommunicatorHelper.sendGetMessage(ckanUrl, SEARCH_PACKAGE + "?" + parameters, true, false);
		if (jsonStatus == null || jsonStatus.isEmpty()) {
			throw new Exception("Unable to get the packages for this parameters " + parameters + ".");
		}

		JSONObject jsonObject = new JSONObject(jsonStatus);
		boolean success = !jsonObject.isNull("success") ? jsonObject.getBoolean("success") : false;
		if (!success) {
			String errorMessage = !jsonObject.isNull("error") ? jsonObject.getJSONObject("error").getString("message") : "An unknown error occured.";
			throw new Exception(errorMessage);
		}
		
		JSONObject resultJson = !jsonObject.isNull("result") ? jsonObject.getJSONObject("result") : null;
		return !resultJson.isNull("count") ? resultJson.getInt("count") : 0;
	}

	public List<CkanPackage> getCkanPackagesByChunk(String organisation, int numberOfDataset, int start, Date lastHarvestDate, boolean filterUpToDate, boolean filterService) throws Exception {
		String organisationParam = organisation != null && !organisation.isEmpty() ? "&fq=organization:" + organisation : "";
		
		String jsonStatus = CommunicatorHelper.sendGetMessage(ckanUrl, SEARCH_PACKAGE + "?rows=" + numberOfDataset + "&start=" + start + organisationParam, true, false);
		if (jsonStatus == null || jsonStatus.isEmpty()) {
			throw new Exception("Unable to get the packages for this organisation " + organisation + ".");
		}

		JSONObject jsonObject = new JSONObject(jsonStatus);
		String status = !jsonObject.isNull("success") ? jsonObject.getString("success") : null;
		if (status == null || status.equals("error")) {
			String errorMessage = !jsonObject.isNull("error") ? jsonObject.getJSONObject("error").getString("message") : "An unknown error occured.";
			throw new Exception(errorMessage);
		}
		
		List<CkanPackage> packages = new ArrayList<CkanPackage>();
		
		JSONObject resultJson = !jsonObject.isNull("result") ? jsonObject.getJSONObject("result") : null;
		JSONArray jsonPackages = !resultJson.isNull("results") ? resultJson.getJSONArray("results") : null;
		if (jsonPackages != null) {
			for (int i = 0; i < jsonPackages.length(); i++) {
				JSONObject jsonPackage = (JSONObject) jsonPackages.get(i);
				CkanPackage pack = CkanUtils.parsePackage(jsonPackage, lastHarvestDate, filterUpToDate, filterService);
				if (pack != null) {
					packages.add(pack);
				}
			} 
		}
		return packages;
	}

	public CkanPackage getCkanPackage(String ckanUrl, String packageName) throws Exception {
		ckanUrl = ckanUrl != null && !ckanUrl.isEmpty() ? ckanUrl : this.ckanUrl;
		
		String jsonStatus = CommunicatorHelper.sendGetMessage(ckanUrl, GET_PACKAGE_INFO + packageName, apiKey);
		if (jsonStatus == null || jsonStatus.isEmpty()) {
			throw new Exception("Unable to get informations for this dataset " + packageName + ".");
		}

		JSONObject jsonObject = new JSONObject(jsonStatus);
		JSONObject resultJson = !jsonObject.isNull("result") ? jsonObject.getJSONObject("result") : null;
		return CkanUtils.parsePackage(resultJson, null, false, false);
	}

	public List<CkanPackage> getCkanPackages() throws Exception {
		String jsonStatus;
		try {
			jsonStatus = CommunicatorHelper.sendGetMessage(ckanUrl, GET_PACKAGES);
			if (jsonStatus == null || jsonStatus.isEmpty()) {
				throw new Exception("CKAN is not available.");
			}

			JSONObject jsonObject = new JSONObject(jsonStatus);
			JSONArray packagesJson = !jsonObject.isNull("result") ? jsonObject.getJSONArray("result") : null;

			List<CkanPackage> packages = new ArrayList<CkanPackage>();
			if (packagesJson != null) {
				for (int i = 0; i < packagesJson.length(); i++) {
					try {
						String packakgeName = packagesJson.get(i).toString();
						CkanPackage pack = getCkanPackage(ckanUrl, packakgeName);
						if (pack != null) {
							packages.add(pack);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			return packages;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to get packages from CKAN at url '" + ckanUrl + "': " + e.getMessage());
		}
	}
	
	public String createCkanDataset(String ckanUrl, String apiKey, CkanPackage pack) throws Exception {
		return createCkanDataset(ckanUrl, apiKey, pack, false);
	}

	public String createCkanDataset(String ckanUrl, String apiKey, CkanPackage pack, boolean ignoreCertificates) throws Exception {
		ckanUrl = ckanUrl != null && !ckanUrl.isEmpty() ? ckanUrl : this.ckanUrl;
		apiKey = apiKey != null && !apiKey.isEmpty() ? apiKey : this.apiKey;
		
		JSONArray extras = null;
		if (pack.getExtras().keySet() != null && !pack.getExtras().keySet().isEmpty()) {
			extras = new JSONArray();
			for(String key : pack.getExtras().keySet()) {
				JSONObject f = new JSONObject();
				f.put("key", key);
				f.put("value", pack.getExtras().get(key));
				extras.put(f);
			}
		}
		
		String name = pack.getName();
		String title = pack.getTitle() != null && !pack.getTitle().isEmpty() ? pack.getTitle() : pack.getName();
		String notes = pack.getDescription();
		
		String[] tags = new String[pack.getKeywords().size()];
		tags = pack.getKeywords().toArray(tags);
		String licenseId = pack.getLicenseId();
		boolean isPrivate = pack.isPrivate();
		String state = "active";
		String org = pack.getOrg();

		//We remove space and accent to the name
		if (name == null) {
			throw new Exception("Name cannot be empty.");
		}
		
		name = removeAccentAndUpperCaseAndSpace(name, "-");

		Dataset dataset = new Dataset(name, title, notes, tags, licenseId, isPrivate, state, org, extras);

		JSONObject jsonObject = new JSONObject(dataset);
		if (pack.getExtras().keySet() == null || pack.getExtras().keySet().isEmpty()) {
			jsonObject.remove("extras");
		}
		String json = jsonObject.toString();

		HttpEntity reqEntity = new StringEntity(json.toString(), "UTF-8");
		return callCkan(ckanUrl, CREATE_PACKAGE, apiKey, reqEntity, ignoreCertificates);
	}

//	private String updateCkanDataset(String ckanUrl, String apiKey, CkanPackage oldP, CkanPackage newP) throws Exception {
//		JSONArray extras = new JSONArray();
//		//extras.put("type_map", "osm");
//		oldP.getExtras().putAll(newP.getExtras());
//		for(String key : oldP.getExtras().keySet()) {
//			JSONObject f = new JSONObject();
//			f.put("key", key);
//			f.put("value", oldP.getExtras().get(key));
//			extras.put(f);
//		}
//		JSONObject json = new JSONObject();
//		json.put("id", oldP.getName());
//		//json.put("notes", oldP.getDescription());
//		//json.put("state", "active");
//		//json.put("owner_org", oldP.getOrg());
//		json.put("extras", extras);
///*		HttpEntity reqEntity = MultipartEntityBuilder.create()
//				.addPart("name", new StringBody(pack.getName(), ContentType.TEXT_PLAIN))
//				.addPart("notes", new StringBody(pack.getDescription(), ContentType.TEXT_PLAIN))
//				.addPart("state", new StringBody("active", ContentType.TEXT_PLAIN))
//				.addPart("owner_org", new StringBody(pack.getOrg(), ContentType.TEXT_PLAIN))
//				.addPart("extras", new StringBody(extras.toString(), ContentType.TEXT_PLAIN))
//				.build();
//*/
//		
//		HttpEntity reqEntity = new StringEntity(json.toString(), "UTF-8");
//		return callCkan(ckanUrl, UPDATE_PACKAGE, apiKey, reqEntity, false);
//	}

	private String extractId(String ckanResult, String packageName) throws JSONException {
		if (ckanResult.contains("{")) {
			ckanResult = ckanResult.substring(ckanResult.indexOf("{"));
			
			JSONObject item = new JSONObject(ckanResult);
			if (!item.isNull("result")) {
				JSONObject result = item.getJSONObject("result");
				if (!result.isNull("id")) {
					return result.getString("id");
				}
			}
		}
		
		return removeAccentAndUpperCaseAndSpace(packageName, "-");
	}

	public String extractFormat(CkanResource res) {
		return extractFormat(res, null);
	}

	public String extractFormat(CkanResource res, String fileName) {
		if (res.getUrl() != null && !res.getUrl().isEmpty()) {
			if (res.getUrl().lastIndexOf(".") > 0) {
				return res.getUrl().substring(res.getUrl().lastIndexOf(".") + 1);
			}
		}

		if (res.getFormat() != null && !res.getFormat().isEmpty()) {
			return res.getFormat().toLowerCase();
		}
		
		if (fileName != null && !fileName.isEmpty()) {
			if (fileName.lastIndexOf(".") > 0) {
				return fileName.substring(fileName.lastIndexOf(".") + 1);
			}
		}

		return "csv";
	}

	private void deleteFile(String filePath) {
		File file = new File(filePath);
		file.delete();
	}

//	private byte[] read(ByteArrayInputStream bais) throws IOException {
//		byte[] array = new byte[bais.available()];
//		bais.read(array);
//
//		return array;
//	}

	public CkanPackage uploadCkanFile(String resourceName, CkanPackage pack, ByteArrayInputStream is) throws Exception {
		String format = extractFormat(new CkanResource(null, resourceName, null, null, null));
		return uploadCkanFile(null, resourceName, format, pack, is);
	}

	public CkanPackage uploadCkanFile(String fileName, String resourceName, String format, CkanPackage pack, ByteArrayInputStream is) throws Exception {
		if (fileName == null) {
			fileName = resourceName;
		}

		pack.setOrg(org);

		String filePath = vanillaFilesPath + "temp/" + new Object().hashCode() + "." + format;
		Files.copy(is, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
		
		try {
			if (pack.getId() == null) {
				String result = createCkanDataset(ckanUrl, apiKey, pack);
				String id = extractId(result, pack.getName());
				
				pack.setId(id);
				
				pack = getCkanPackage(ckanUrl, pack.getId());

				String resourceId = pack.getSelectedResource() != null ? pack.getSelectedResource().getId() : null;
				uploadCkanFile(ckanUrl, apiKey, pack, resourceName, format, filePath, resourceId);
			}
			else {
				String resourceId = pack.getSelectedResource() != null ? pack.getSelectedResource().getId() : null;

				uploadCkanFile(ckanUrl, apiKey, pack, resourceName, format, filePath, resourceId);
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to upload the dataset to CKAN: " + e.getMessage());
		}

//		deleteFile(filePath);

		return pack;
	}

	public String uploadCkanFile(String ckanUrl, String apiKey, CkanPackage pack, String fileName, String format, String filePath, String resourceId) throws Exception {
		return uploadCkanFile(ckanUrl, apiKey, pack, fileName, format, filePath, resourceId, false);
	}
	
	public CkanPackage uploadCkanFile(String fileName, String resourceName, String format, CkanPackage pack, String filePath) throws Exception {
		if (fileName == null) {
			fileName = resourceName;
		}

		pack.setOrg(org);

		if (pack.getId() == null) {
			String result = createCkanDataset(ckanUrl, apiKey, pack);
			String id = extractId(result, pack.getName());
			
			pack.setId(id);
			
			pack = getCkanPackage(ckanUrl, pack.getId());

			String resourceId = pack.getSelectedResource() != null ? pack.getSelectedResource().getId() : null;
			uploadCkanFile(ckanUrl, apiKey, pack, resourceName, format, filePath, resourceId);
		}
		else {
			String resourceId = pack.getSelectedResource() != null ? pack.getSelectedResource().getId() : null;

			uploadCkanFile(ckanUrl, apiKey, pack, resourceName, format, filePath, resourceId);
		}

		return pack;
	}

	public String uploadCkanFile(String ckanUrl, String apiKey, CkanPackage pack, String fileName, String format, String filePath, String resourceId, boolean ignoreCertificates) throws Exception {
		ckanUrl = ckanUrl != null && !ckanUrl.isEmpty() ? ckanUrl : this.ckanUrl;
		apiKey = apiKey != null && !apiKey.isEmpty() ? apiKey : this.apiKey;

		SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String date = dateFormatGmt.format(new Date());
		String action = null;
		HttpEntity entity = null;

		File initialFile = new File(filePath);
		ContentBody cbFile = new FileBody(initialFile, ContentType.TEXT_PLAIN.withCharset("UTF-8"));
		
		boolean deleteFile = false;
		if (format.equalsIgnoreCase("csv") || format.equalsIgnoreCase("xls") || format.equalsIgnoreCase("xlsx")) {
			
			if (format.equalsIgnoreCase("csv")) {
				filePath = removeAccentAndUpperCaseAndSpaceForFile(filePath, format);
				
				initialFile = new File(filePath);
				cbFile = new FileBody(initialFile, ContentType.TEXT_PLAIN.withCharset("UTF-8"));
			
				deleteFile = true;
			}
			
			if (resourceId != null && !resourceId.isEmpty()) {

				MultipartEntityBuilder multipart = MultipartEntityBuilder.create().addPart("resource_id", new StringBody(resourceId, ContentType.TEXT_PLAIN)).addPart("limit", new StringBody("0", ContentType.TEXT_PLAIN));
				HttpEntity reqEntity = multipart.build();

				JSONArray fields = null;
				try {
					action = DATASTORE_SEARCH;

					String res = callCkan(ckanUrl, action, apiKey, reqEntity, ignoreCertificates);

					System.out.println(res);

					JSONObject json = new JSONObject(res.split("\n")[1]);
					fields = json.getJSONObject("result").getJSONArray("fields");
				} catch (Exception e) {
					e.printStackTrace();
				}

				multipart = MultipartEntityBuilder.create()
						.addPart("key", new StringBody(fileName + "_" + date, ContentType.TEXT_PLAIN))
						.addPart("name", new StringBody(fileName, ContentType.TEXT_PLAIN))
						.addPart("format", new StringBody(format, ContentType.TEXT_PLAIN))
						.addPart("package_id", new StringBody(pack.getId(), ContentType.TEXT_PLAIN))
						.addPart("url", new StringBody("path/to/save/dir", ContentType.TEXT_PLAIN))
						.addPart("file", cbFile)
						.addPart("upload", cbFile);
				multipart.addPart("id", new StringBody(resourceId, ContentType.TEXT_PLAIN));
				reqEntity = multipart.build();

				action = UPDATE_RESOURCE;

				String result = callCkan(ckanUrl, action, apiKey, reqEntity, ignoreCertificates);

				System.out.println(result);
				TimeUnit.SECONDS.sleep(2);

				if (result.split("\n")[0].equals("200") && fields != null) {
					if (action.equals(UPDATE_RESOURCE)) {
						updateDatastore(apiKey, ckanUrl, resourceId);
					}
					
					JSONArray filtered = new JSONArray();
					for (int i = 0; i < fields.length(); i++) {
						JSONObject f = fields.getJSONObject(i);
						if (f.getString("id").equals("_id")) {
							continue;
						}
						filtered.put(f);
					}

					JSONObject json = new JSONObject();
					json.put("resource_id", resourceId);
					json.put("force", "true");
					json.put("fields", filtered);

					action = DATASTORE_UPDATE;
					System.out.println(json.toString());

					TimeUnit.SECONDS.sleep(15);
					
					entity = new StringEntity(json.toString(), "UTF-8");
				}
				else {
					if (action.equals(UPDATE_RESOURCE)) {
						updateDatastore(apiKey, ckanUrl, resourceId);
					}
					
					if (d4cUrl != null && !d4cUrl.isEmpty()) {
						TimeUnit.SECONDS.sleep(20);
						CommunicatorHelper.sendGetMessage(d4cUrl, CALCULATE_VISU + pack.getId());
					}
					
					return result;
				}
			}
			else {
				action = CREATE_RESOURCE;

				MultipartEntityBuilder multipart = MultipartEntityBuilder.create()
						.addPart("key", new StringBody(fileName + "_" + date, ContentType.TEXT_PLAIN))
						.addPart("name", new StringBody(fileName, ContentType.TEXT_PLAIN))
						.addPart("format", new StringBody(format, ContentType.TEXT_PLAIN))
						.addPart("package_id", new StringBody(pack.getId(), ContentType.TEXT_PLAIN))
						.addPart("url", new StringBody("path/to/save/dir", ContentType.TEXT_PLAIN))
						.addPart("file", cbFile)
						.addPart("upload", cbFile);
				if (resourceId != null && !resourceId.isEmpty()) {
					multipart.addPart("id", new StringBody(resourceId, ContentType.TEXT_PLAIN));

					action = UPDATE_RESOURCE;
				}

				entity = multipart.build();
			}
		}
		else {
			action = CREATE_RESOURCE;
			System.out.println("Create resource format : " + format);

			MultipartEntityBuilder multipart = MultipartEntityBuilder.create()
					.addPart("key", new StringBody(fileName + "_" + date, ContentType.TEXT_PLAIN))
					.addPart("name", new StringBody(fileName, ContentType.TEXT_PLAIN))
					.addPart("format", new StringBody(format, ContentType.TEXT_PLAIN))
					.addPart("package_id", new StringBody(pack.getId(), ContentType.TEXT_PLAIN))
					.addPart("url", new StringBody("path/to/save/dir", ContentType.TEXT_PLAIN))
					.addPart("file", cbFile)
					.addPart("upload", cbFile);
			if (resourceId != null && !resourceId.isEmpty()) {
				multipart.addPart("id", new StringBody(resourceId, ContentType.TEXT_PLAIN));

				action = UPDATE_RESOURCE;
			}
			entity = multipart.build();
		}

		HttpEntity reqEntity = entity;
		String res = callCkan(ckanUrl, action, apiKey, reqEntity, ignoreCertificates);
		System.out.println(res);
		
		if (deleteFile) {
			initialFile.delete();
		}
		
		if (d4cUrl != null && !d4cUrl.isEmpty()) {
			TimeUnit.SECONDS.sleep(20);
			CommunicatorHelper.sendGetMessage(d4cUrl, CALCULATE_VISU + pack.getId());
		}
		
		return res;
	}

	private void updateDatastore(String apiKey, String ckanUrl, String resourceId) {
		try {

			JSONObject metadata = new JSONObject();
			metadata.put("ckan_url", ckanUrl);
			metadata.put("resource_id", resourceId);
			
			JSONObject item = new JSONObject();
			item.put("api_key", apiKey);
			item.put("job_type", "push_to_datastore");
			item.put("metadata", metadata);
			
//			boolean isAvailable = CkanHelper.pingUrl(DEFAULT_DATASTORE_URL);
			if (d4cUrl != null && !d4cUrl.isEmpty()) {
				HttpEntity reqEntity = new StringEntity(item.toString(), "UTF-8");
				String result = callCkan(d4cUrl, PUSH_DATAPUSHER + resourceId, apiKey, reqEntity, false);
				System.out.println("Datastore result: " + result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method allows to test the availability of an URL
	 * 
	 * @param host
	 * @param port
	 * @param timeout
	 * @return
	 */
	public static boolean pingUrl(String myUrl) {
		try {
			URL url = new URL(myUrl);
			HttpURLConnection huc = (HttpURLConnection) url.openConnection();
			huc.setRequestMethod("HEAD");
			  
			int responseCode = huc.getResponseCode();
			return responseCode == HttpURLConnection.HTTP_OK;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
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
	private String callCkan(String ckanUrl, String method, String apiKey, HttpEntity reqEntity, boolean ignoreCertificates) throws Exception {
		CloseableHttpClient httpclient = null;
		if (ignoreCertificates) {
			final SSLConnectionSocketFactory sslsf;
			try {
			    sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault(),
			            NoopHostnameVerifier.INSTANCE);
			} catch (NoSuchAlgorithmException e) {
			    throw new RuntimeException(e);
			}
	
			final Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
			        .register("http", new PlainConnectionSocketFactory())
			        .register("https", sslsf)
			        .build();
	
			final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
			cm.setMaxTotal(100);
			httpclient = HttpClients.custom()
			        .setSSLSocketFactory(sslsf)
			        .setConnectionManager(cm)
			        .build();
		}
		else {
			httpclient = HttpClientBuilder.create().build();
		}
		
		HttpPost postRequest;
		try {
			postRequest = new HttpPost(ckanUrl + method);
			postRequest.setEntity(reqEntity);
			postRequest.addHeader("X-CKAN-API-Key", apiKey);
			if (reqEntity instanceof StringEntity) {
				postRequest.addHeader("Content-type", "application/json");
			}

			HttpResponse response = httpclient.execute(postRequest);
			int statusCode = response.getStatusLine().getStatusCode();
			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

			boolean hasError = false;

			StringBuilder sb = new StringBuilder();
			sb.append(statusCode + "\n");
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

	public String getResourceFields(String id) throws Exception {
		MultipartEntityBuilder multipart = MultipartEntityBuilder.create()
				.addPart("resource_id", new StringBody(id, ContentType.TEXT_PLAIN))
				.addPart("limit", new StringBody("0", ContentType.TEXT_PLAIN));
		HttpEntity entity = multipart.build();
		String res = callCkan(ckanUrl, DATASTORE_SEARCH, apiKey, entity, false);
		return res;
	}

	public void updateResourceDatastore(JSONObject json) throws Exception {
		HttpEntity entity = new StringEntity(json.toString(), "UTF-8");
		callCkan(ckanUrl, DATASTORE_UPDATE, apiKey, entity, false);
	}
	
//	private static class DefaultTrustManager implements X509TrustManager {
//
//        @Override
//        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
//
//        @Override
//        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
//
//        @Override
//        public X509Certificate[] getAcceptedIssuers() {
//            return null;
//        }
//    }

	public void crawl(String targetUrl, String targetOrg, String targetApiKey, boolean addPrefix) throws Exception {
		String datePrefix = addPrefix ? df.format(new Date()) + "-" : "";

		List<CkanPackage> packs = null;
		if (org != null && !org.isEmpty()) {
			packs = getCkanPackages(org);
		}
		else {
			packs = getCkanPackages();
		}
		if (packs != null) {
			for (CkanPackage pack : packs) {
				if (org == null || (pack.getOrg() != null && pack.getOrg().equals(org))) {
					uploadCkanPack(datePrefix, targetUrl, targetOrg, targetApiKey, pack);
				}
			}
		}
	}

	private void uploadCkanPack(String prefix, String targetUrl, String targetOrg, String targetApiKey, CkanPackage pack) throws Exception {
		pack.setOrg(targetOrg);

		pack.setName(prefix + pack.getName());

		createCkanDataset(targetUrl, targetApiKey, pack);
		CkanPackage targetPack = getCkanPackage(targetUrl, pack.getName());

		if (pack.getResources() != null) {
			for (CkanResource res : pack.getResources()) {

				String format = extractFormat(res);
				String filePath = vanillaFilesPath + "temp/" + new Object().hashCode() + "." + format;

				// TODO: Only CSV and JSON for now (add as an option later)
//				if (format.equalsIgnoreCase("csv") && !format.equalsIgnoreCase("json")) {
//					continue;
//				}

				try {
					getResourceAsFile(res, filePath);

					if (format.equalsIgnoreCase("csv")) {
						//We comment this for now
//						filePath = removeAccentAndUpperCaseAndSpaceForFile(filePath, format);
					}

					uploadCkanFile(targetUrl, targetApiKey, targetPack, res.getName(), format, filePath, null);

					deleteFile(filePath);
				} catch (Exception e) {
					e.printStackTrace();

					deleteFile(filePath);

					//We let it go, we have to work on this
//					throw new Exception("Unable to upload the dataset to CKAN: " + e.getMessage());
				}
			}
		}
	}

	private String removeAccentAndUpperCaseAndSpaceForFile(String filePath, String format) {
		String newFile = vanillaFilesPath != null && !vanillaFilesPath.isEmpty() ? vanillaFilesPath + "temp/" + new Object().hashCode() + "." + format : filePath.substring(0, filePath.lastIndexOf("/") + 1) + new Object().hashCode() + "." + format;
		// Path newPath = Paths.get(newFile);

		try (Scanner scanner = new Scanner(new File(filePath), "UTF-8"); Writer writer = new OutputStreamWriter(new FileOutputStream(newFile), StandardCharsets.UTF_8)) {
			boolean firstLine = true;

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (firstLine) {
					firstLine = false;
					
					line = line.replace("\uFEFF", "");
					line = line.replaceAll("\\s+","_");
					line = line.toLowerCase();
					line = line.replaceAll("'","_");
					line = line.replaceAll("\\.", "_");
					line = StringUtils.stripAccents(line);

					line = removeAccentAndUpperCaseAndSpace(line, "");

				}

				writer.write(line + "\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newFile;
	}

	private String removeAccentAndUpperCaseAndSpace(String value, String spaceReplacement) {
		value = value.replace("\uFEFF", "");
		value = value.replaceAll("\\s+", spaceReplacement);
		value = value.toLowerCase();
		value = value.replaceAll("'", "");
		value = StringUtils.stripAccents(value);
		return value;
	}

	public void getResourceAsFile(CkanResource res, String filePath) throws MalformedURLException, IOException {
		File targetFile = new File(filePath);
		InputStream inputStream = getResourceFile(res);
		java.nio.file.Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

		IOUtils.closeQuietly(inputStream);
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
}
