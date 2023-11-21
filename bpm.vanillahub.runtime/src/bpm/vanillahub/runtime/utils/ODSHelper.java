package bpm.vanillahub.runtime.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.CkanResource;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.utils.CkanHelper;
import bpm.vanilla.platform.core.utils.CommunicatorHelper;
import bpm.vanillahub.core.beans.activities.attributes.ODSPackage;
import bpm.vanillahub.core.beans.activities.attributes.ODSProperties.Format;

public class ODSHelper {

	public static final String SEARCH = "/api/datasets/1.0/search/?rows=-1&q=";
	public static final String DATASET = "/api/datasets/1.0/";
	public static final String RECORDS = "/api/records/1.0/download/?rows=-1&dataset=";
	public static final String RECORDS_FORMAT = "&format=";
	public static final String RECORDS_QUERY = "&q=";
	public static final String DOWNLOAD_PREFIX = "/explore/dataset/";
	public static final String DOWNLOAD_SUFIX = "/download?format=";
	
//	private static final int DEFAULT_RECORD = 20000;

	private static final SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");

	private String odsUrl;
	private String vanillaFilesPath;

	public ODSHelper(String odsUrl) {
		VanillaConfiguration vanillaConfig = ConfigurationManager.getInstance().getVanillaConfiguration();
		this.odsUrl = odsUrl;
		this.vanillaFilesPath = vanillaConfig.getProperty(VanillaConfiguration.P_VANILLA_FILES);
	}

	public List<ODSPackage> getODSPackages() throws Exception {
		// TODO: Attention il faut activer TLSv1.2 (pas nécessaire en Java 8)
		// -Dhttps.protocols=TLSv1,TLSv1.1,TLSv1.2

		String jsonStatus;
		try {
			jsonStatus = CommunicatorHelper.sendGetMessage(odsUrl, SEARCH);
			if (jsonStatus == null || jsonStatus.isEmpty()) {
				throw new Exception(odsUrl + " is not available.");
			}

			JSONObject jsonObject = new JSONObject(jsonStatus);
			JSONArray packagesJson = !jsonObject.isNull("datasets") ? jsonObject.getJSONArray("datasets") : null;

			List<ODSPackage> packages = new ArrayList<ODSPackage>();
			if (packagesJson != null) {
				for (int i = 0; i < packagesJson.length(); i++) {
					try {
						String packJson = packagesJson.get(i).toString();
						ODSPackage pack = parseODSPackage(packJson);
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
			throw new Exception("Unable to get packages from ODS at url '" + odsUrl + "': " + e.getMessage());
		}
	}

	public ODSPackage getODSPackage(String datasetId) throws Exception {
		// TODO: Attention il faut activer TLSv1.2 (pas nécessaire en Java 8)
		// -Dhttps.protocols=TLSv1,TLSv1.1,TLSv1.2

		String jsonStatus;
		try {
			jsonStatus = CommunicatorHelper.sendGetMessage(odsUrl, DATASET + datasetId);
			if (jsonStatus == null || jsonStatus.isEmpty()) {
				throw new Exception(odsUrl + " is not available.");
			}
			
			ODSPackage pack = parseODSPackage(jsonStatus);
			if (pack != null) {
				return pack;
			}
			
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to get package with id " + datasetId + " from ODS at url '" + odsUrl + "': " + e.getMessage());
		}
	}

	private ODSPackage parseODSPackage(String packJson) throws Exception {
		if (packJson == null || packJson.isEmpty()) {
			throw new Exception("Unable to get informations for one dataset.");
		}

		JSONObject resultJson = new JSONObject(packJson);
		String id = !resultJson.isNull("datasetid") ? resultJson.getString("datasetid") : null;

		if (id == null) {
			return null;
		}

		JSONObject metaObject = !resultJson.isNull("metas") ? resultJson.getJSONObject("metas") : null;
		String title = !metaObject.isNull("title") ? metaObject.getString("title") : null;
		String description = !metaObject.isNull("description") ? metaObject.getString("description") : null;
		String publisher = !metaObject.isNull("publisher") ? metaObject.getString("publisher") : null;
		String domain = !metaObject.isNull("domain") ? metaObject.getString("domain") : null;
		String parentDomain = !metaObject.isNull("parent_domain") ? metaObject.getString("parent_domain") : null;
		String theme = !metaObject.isNull("theme") ? metaObject.getString("theme") : null;
		String records = !metaObject.isNull("records_count") ? metaObject.getString("records_count") : null;

		return new ODSPackage(id, title, description, publisher, domain, parentDomain, theme, records);
	}

	public void crawl(String targetUrl, String targetOrg, String targetApiKey, List<Format> formats, Integer limit, boolean addPrefix) throws Exception {
		String datePrefix = addPrefix ? df.format(new Date()) + "-" : "";
//		String datePrefix = "ic_";

		formats = formats != null && !formats.isEmpty() ? formats : new ArrayList<>(Arrays.asList(Format.values()));

		List<ODSPackage> packs = getODSPackages();
		if (packs != null) {
			CkanHelper ckanHelper = new CkanHelper();
			for (ODSPackage pack : packs) {
				// No filter to push datasets for now
				uploadCkanPack(ckanHelper, datePrefix, targetUrl, targetOrg, targetApiKey, pack, formats, limit);
			}
		}
	}

	public void crawlOneDataset(String targetUrl, String targetOrg, String targetApiKey, CkanPackage selectedPack, String datasetId, String parameters, List<Format> formats, Integer limit, boolean addPrefix, boolean updateDataset) throws Exception {
		String datePrefix = addPrefix ? df.format(new Date()) + "-" : "";

		formats = formats != null && !formats.isEmpty() ? formats : new ArrayList<>(Arrays.asList(Format.values()));
		
		ODSPackage pack = getODSPackage(datasetId);
		
		CkanHelper ckanHelper = new CkanHelper(targetUrl, targetOrg, targetApiKey);
		CkanPackage targetPack = updateDataset ? ckanHelper.getCkanPackage(selectedPack.getId()) : createCkanPack(ckanHelper, targetUrl, targetOrg, targetApiKey, pack, datePrefix);
		
		uploadCkanFile(ckanHelper, pack, targetUrl, targetApiKey, targetPack, formats, parameters, false);
	}

	private void uploadCkanPack(CkanHelper ckanHelper, String prefix, String targetUrl, String targetOrg, String targetApiKey, ODSPackage pack, List<Format> formats, Integer limit) throws Exception {

		System.out.println("Uploading " + pack.getId());

		CkanPackage targetPack = createCkanPack(ckanHelper, targetUrl, targetOrg, targetApiKey, pack, prefix);

		if (pack.getRecords() != null && !pack.getRecords().isEmpty() && limit != null && Integer.parseInt(pack.getRecords()) > limit) {
			System.out.println("Limit set to " + limit + ", we do not upload the resources.");
			return;
		}
		
		uploadCkanFile(ckanHelper, pack, targetUrl, targetApiKey, targetPack, formats, null, true);
	}
	
	private CkanPackage createCkanPack(CkanHelper ckanHelper, String targetUrl, String targetOrg, String targetApiKey, ODSPackage pack, String prefix) throws Exception {
		CkanPackage ckanPack = new CkanPackage(pack.getId(), pack.getId());
		ckanPack.setTitle(pack.getTitle());
		ckanPack.setDescription(pack.getDescription());
		ckanPack.setOrg(targetOrg);

		ckanPack.setName(prefix + ckanPack.getName());

		ckanHelper.createCkanDataset(targetUrl, targetApiKey, ckanPack);
		return ckanHelper.getCkanPackage(targetUrl, ckanPack.getName());
	}
	
	private void uploadCkanFile(CkanHelper ckanHelper, ODSPackage pack, String targetUrl, String targetApiKey, CkanPackage targetPack, List<Format> formats, String parameters, boolean downloadFile) {
		for (Format format : formats) {
			if (format == Format.XLS) {
				continue;
			}
			
			String filePath = vanillaFilesPath + "temp/" + new Object().hashCode() + "." + format.toString();

			try {
				String resourceId = null;
				if (downloadFile) {
					getResourceAsFile(pack, format, filePath);
				}
				else {
					getRecordsAsFile(pack, format, parameters, filePath);
					
					if (targetPack.getResources() != null) {
						for (CkanResource resource : targetPack.getResources()) {
							if (resource.getName().equals(pack.getId()) && resource.getFormat().equalsIgnoreCase(format.toString())) {
								resourceId = resource.getId();
								break;
							}
						}
					}
				}
				
				ckanHelper.uploadCkanFile(targetUrl, targetApiKey, targetPack, pack.getId(), format.toString().toLowerCase(), filePath, resourceId);

				deleteFile(filePath);
			} catch (Exception e) {
				e.printStackTrace();

				deleteFile(filePath);

				System.out.println("Unable to upload the dataset to CKAN: " + e.getMessage());
			}
		}
	}

	public void getResourceAsFile(ODSPackage pack, Format format, String filePath) throws MalformedURLException, IOException {
		String url = odsUrl.endsWith("/") ? odsUrl.substring(0, odsUrl.length() - 1) + DOWNLOAD_PREFIX : odsUrl + DOWNLOAD_PREFIX;
		url = url + pack.getId() + DOWNLOAD_SUFIX + format.toString().toLowerCase();

		File targetFile = new File(filePath);
		InputStream inputStream = getResourceFile(url);
		java.nio.file.Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

		IOUtils.closeQuietly(inputStream);
	}

	public void getRecordsAsFile(ODSPackage pack, Format format, String parameters, String filePath) throws MalformedURLException, IOException {
		String url = odsUrl.endsWith("/") ? odsUrl.substring(0, odsUrl.length() - 1) + RECORDS : odsUrl + RECORDS;
		url = url + pack.getId() + RECORDS_FORMAT + format.toString().toLowerCase() + "&" + parameters;

		File targetFile = new File(filePath);
		InputStream inputStream = getResourceFile(url);
		java.nio.file.Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

		IOUtils.closeQuietly(inputStream);
	}

	public InputStream getResourceFile(String url) throws MalformedURLException, IOException {
		return new URL(url).openStream();
	}

	private void deleteFile(String filePath) {
		File file = new File(filePath);
		file.delete();
	}
}
