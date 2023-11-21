package bpm.vanillahub.runtime.run;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;

import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.CkanResource;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.utils.CkanHelper;
import bpm.vanilla.platform.core.utils.CkanUtils;
import bpm.vanilla.platform.core.utils.D4CFormat;
import bpm.vanilla.platform.core.utils.D4CHelper;
import bpm.vanilla.platform.core.utils.D4CHelper.D4CResult;
import bpm.vanilla.platform.core.utils.D4CHelper.Status;
import bpm.vanilla.platform.core.utils.D4CHelper.Theme;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanillahub.core.beans.activities.OpenDataCrawlActivity;
import bpm.vanillahub.core.beans.activities.TypeOpenData;
import bpm.vanillahub.core.beans.activities.attributes.CkanProperties;
import bpm.vanillahub.core.beans.activities.attributes.IOpenDataProperties;
import bpm.vanillahub.core.beans.activities.attributes.ODSProperties;
import bpm.vanillahub.runtime.WorkflowProgress;
import bpm.vanillahub.runtime.i18N.Labels;
import bpm.vanillahub.runtime.utils.MapFluxHelper;
import bpm.vanillahub.runtime.utils.MapFluxHelper.WFSFormat;
import bpm.vanillahub.runtime.utils.ODSHelper;
import bpm.workflow.commons.beans.Result;
import bpm.workflow.commons.resources.Cible;

public class OpenDataCrawlActivityRunner extends ActivityRunner<OpenDataCrawlActivity> {

	private List<Cible> cibles;

	public OpenDataCrawlActivityRunner(WorkflowProgress workflowProgress, IVanillaLogger logger, String workflowName, String launcher, OpenDataCrawlActivity activity, List<Parameter> parameters, List<Variable> variables, boolean isLoop, List<Cible> cibles) {
		super(workflowProgress, logger, workflowName, launcher, activity, parameters, variables, isLoop);
		this.cibles = cibles;
	}

	@Override
	public void run(Locale locale) {
		TypeOpenData type = activity.getTypeOpenData();
		String url = activity.getUrl();
		boolean addPrefix = activity.addPrefix();
		IOpenDataProperties properties = activity.getProperties();

		Cible cible = (Cible) activity.getResource(cibles);

		switch (type) {
		case CKAN:
		case D4C:
			crawlCkan(locale, url, addPrefix, (CkanProperties) properties, cible);
			break;
		case DATA_GOUV:
			addError(type.toString() + " " + Labels.getLabel(locale, Labels.isNotSupported));
			result.setResult(Result.ERROR);
		case ODS:
			crawlODS(locale, url, addPrefix, (ODSProperties) properties, cible);
			break;
		default:
			break;
		}

		clearResources();
		if (getResult().getResult() != Result.ERROR) {
			result.setResult(Result.SUCCESS);
		}
	}

	private void crawlCkan(Locale locale, String url, boolean addPrefix, CkanProperties properties, Cible cible) {
		try {
			String org = properties.getOrganisation();
			String apiKey = properties.getApiKey();

			CkanHelper ckanHelper = new CkanHelper(url, org, apiKey);
			int numberOfDatasets = ckanHelper.getCkanPackageNumberByOrganisation(org);

			logger.info("Start scrawling '" + url + "' for org '" + org + "'");
			logger.info("Found " + numberOfDatasets + " datasets.");

			addInfo(Labels.getLabel(locale, Labels.NumberOfDatasets) + " " + numberOfDatasets);
			
			setNumberTotalOfFiles(numberOfDatasets);

			String targetUrl = cible.getUrl(parameters, variables);
			String targetOrg = cible.getOrg();
			String login = cible.getLogin();
			String password = cible.getPassword();

			D4CHelper d4cHelper = new D4CHelper(targetUrl, targetOrg, login, password);
			
			// Getting list of themes
			List<Theme> themes = d4cHelper.getThemes();
			
			//We manage deleted datasets
			try {
				manageDeletedDatasets(locale, d4cHelper, ckanHelper, targetOrg);
			} catch (Exception e) {
				e.printStackTrace();
				addError(e.getMessage());
				
				setResult(Result.ERROR);
				return;
			}

			int nbDatasetServiceSkip = 0;
			boolean filterService = true;
	
			int nbDatasetUpToDateSkip = 0;
			Date lastHarvestDate = null;
			
			String lastHarvestDateVariable = activity.getLastHarvestDate(parameters, variables);
			if (lastHarvestDateVariable != null && !lastHarvestDateVariable.equals("-1")) {
				try {
					lastHarvestDate = !lastHarvestDateVariable.isEmpty() ? CkanUtils.shortDf.parse(lastHarvestDateVariable) : null;
					
					logger.info("Last harvest date used is " + lastHarvestDate + ".");
				} catch (Exception e) {
					e.printStackTrace();
	
					setResult(Result.ERROR);
					return;
				}
			}
			boolean filterUpToDate = activity.isUpdateDataset();

			// We manage packages by chunk
			int chunkNumber = numberOfDatasets / 50 + (numberOfDatasets % 50 == 0 ? 0 : 1);

			boolean firstLoop = true;
			for (int chunk = 0; chunk < chunkNumber; chunk++) {
				logger.info("Start checking chunk " + (chunk + 1) + " of " + chunkNumber + " chunks.");
				
				List<CkanPackage> packs = ckanHelper.getCkanPackagesByChunk(org, 50, chunk * 50, lastHarvestDate, filterUpToDate, filterService);
				for (CkanPackage pack : packs) {
					
					// We check if the user has stop the process
					if (isStopByUser()) {
						logger.info("Process stop by user.");
						
						setResult(Result.ERROR);
						return;
					}

					try {
						logger.debug("Managing dataset with ID '" + pack.getId() + "' and name '" + pack.getName() + "'");

						if (filterService && pack.getId().equals(CkanUtils.CODE_ID_DATASET_SERVICE)) {
							logger.debug("Dataset is a service, we skip it.");

							// We skip the dataset because it is a service
							nbDatasetServiceSkip++;
							if (!firstLoop) {
								iterateLoop();
							}
							firstLoop = false;
							continue;
						}

						if (filterUpToDate && pack.getId().equals(CkanUtils.CODE_ID_DATASET_UP_TO_DATE)) {
							logger.debug("Dataset is up to date, we skip it.");
							
							// We skip the dataset because it is a up to date
							nbDatasetUpToDateSkip++;
							if (!firstLoop) {
								iterateLoop();
							}
							firstLoop = false;
							continue;
						}
						
						//Check licence
						String licence = pack.getExtras().get(CkanUtils.KEY_LICENCE);
						
						logger.debug("Licence is '" + licence + "'");
						
						//If the licence is not clearly define in the list of licence we try to see if it is OpenData
						if (!checkLicence(licence)) {
							boolean isOpen = isOpen(pack.getKeywords());
							
							String newLicence = isOpen ? D4CHelper.LICENCE_DEFAULT_OPEN : D4CHelper.LICENCE_DEFAULT_CLOSE;
							logger.debug("Licence was not found, we check tags and define the licence as '" + newLicence + "'");
							
							pack.getExtras().put(CkanUtils.KEY_LICENCE, newLicence);
						}
						
						//We try to find the theme in the keywords
						JSONArray selectedThemes = findThemes(themes, pack.getKeywords());
						pack.putExtra(CkanUtils.KEY_THEMES, selectedThemes.toString());

						boolean uploadResources = false;
						
						//Settings hub as source
						pack.putExtra(CkanUtils.KEY_SOURCE, CkanUtils.VALUE_HUB);

						String datasetId = null;
						
						// If update is set, we have to check if the dataset exist and update it if we need to
						if (activity.isUpdateDataset()) {
							if (pack.getName().equals("budget-comptes-administratifs-de-la-region-grand-est")) {
								System.out.println("Test");
							}
							
							CkanPackage existingDataset = d4cHelper.findCkanPackage(pack.getName());
							if (existingDataset != null) {
								boolean datasetIsOutdated = isDatasetOutdated(pack, existingDataset);
								if (!datasetIsOutdated) {
									logger.debug("Dataset is up to date, we skip it.");
									
									// We skip the dataset because it is up to date
									nbDatasetUpToDateSkip++;
									if (!firstLoop) {
										iterateLoop();
									}
									firstLoop = false;
									continue;
								}
								else {
									logger.debug("Metadata is outdated, we update the dataset.");
									
									datasetId = existingDataset.getId();
									pack.setId(datasetId);
									
									manageMetadataToUpdate(pack, existingDataset);

									CkanPackage dataset = d4cHelper.manageDataset(pack, true);
									datasetId = dataset.getId();
									
									logger.debug("Resources are outdated, we delete the previous resources.");
									
									d4cHelper.deleteResources(existingDataset.getId());
									uploadResources = true;
								}
							}
							else {
								logger.debug("Dataset does not exist, we add it.");
								
								CkanPackage dataset = d4cHelper.manageDataset(pack, false);
								datasetId = dataset.getId();

								uploadResources = true;
							}
						}
						else {
							logger.debug("Dataset does not exist, we add it.");
							
							CkanPackage dataset = d4cHelper.manageDataset(pack, false);
							datasetId = dataset.getId();

							uploadResources = true;
						}

						if (pack.getResources() != null && uploadResources) {
							CkanResource resourceToDisplay = checkResourceToDisplay(pack.getResources());
							for (CkanResource resource : pack.getResources()) {

								resource.setId(null);

								if (resource != resourceToDisplay) {
									logger.debug("Uploading resource '" + resource.getName() + "'");
									
									D4CResult result = d4cHelper.uploadResource(datasetId, resource.getName(), resource.getFormat(), resource, false, false, uploadResources);
									if (result.getStatus() == Status.ERROR) {
										logger.debug("Ressource '" + resource.getName() + "' not added: " + result.getMessage());
										
										addError("Ressource '" + resource.getName() + "' not added: " + result.getMessage());
									}
								}
							}

							// We upload the resource to display
							if (resourceToDisplay != null) {
								D4CResult result = null;
								
								//If the format is WFS, we need to get the data from the server if possible
								D4CFormat format = getD4CFormat(resourceToDisplay);
								if (format == D4CFormat.WFS) {
									logger.debug("Retrieving data from WFS '" + resourceToDisplay.getUrl() + "'");
									WFSFormat selectedFormat = MapFluxHelper.getSelectedFormat(resourceToDisplay);
									logger.debug("Found format '" + selectedFormat + "'");
									try(InputStream is = MapFluxHelper.retrieveJSONFromWFS(resourceToDisplay, selectedFormat.getValue())) {

										logger.debug("Uploading data from WFS for resource '" + resourceToDisplay.getName() + "'");
										D4CFormat resourceToDisplayFormat = getD4CFormat(selectedFormat);
										
										result = d4cHelper.uploadFileResource(datasetId, resourceToDisplay.getName(), resourceToDisplayFormat.name(), is);
										if (result.getStatus() == Status.ERROR) {
											logger.debug("Ressource '" + resourceToDisplay.getName() + "' not added: " + result.getMessage());
											addError("Ressource '" + resourceToDisplay.getName() + "' not added: " + result.getMessage());
										}
									} catch (Exception e) {
										e.printStackTrace();
										logger.debug("Could not retrive WFS data from ressource '" + resourceToDisplay.getName() + "': " + e.getMessage());
										addError("Could not retrive WFS data from ressource '" + resourceToDisplay.getName() + "': " + e.getMessage());
									}
									logger.debug("Uploading resource '" + resourceToDisplay.getName() + "'");
									result = d4cHelper.uploadResource(datasetId, resourceToDisplay.getName(), resourceToDisplay.getFormat(), resourceToDisplay, false, false, uploadResources);
								}
								else {
									boolean manageFile = needManageFile(format);
									
									logger.debug("Uploading resource '" + resourceToDisplay.getName() + "'");
									result = d4cHelper.uploadResource(datasetId, resourceToDisplay.getName(), resourceToDisplay.getFormat(), resourceToDisplay, manageFile, true, uploadResources);
								
									//We check if the file was too big, we put a warning and upload the resource without managing it
									if (result.getStatus() == Status.ERROR && result.getMessage().contains("The file is too big")) {
										logger.debug("Ressource '" + resourceToDisplay.getName() + "' not added: " + result.getMessage());
										addWarning("Ressource '" + resourceToDisplay.getName() + "' not added: " + result.getMessage());
									
										result = d4cHelper.uploadResource(datasetId, resourceToDisplay.getName(), resourceToDisplay.getFormat(), resourceToDisplay, false, false, uploadResources);
									}
								}
								if (result.getStatus() == Status.ERROR) {
									logger.debug("Ressource '" + resourceToDisplay.getName() + "' not added: " + result.getMessage());
									addError("Ressource '" + resourceToDisplay.getName() + "' not added: " + result.getMessage());
								}
							}
						}

						if (!firstLoop) {
							iterateLoop();
						}
						firstLoop = false;
					} catch (Exception e) {
						e.printStackTrace();
						addError(e.getMessage());

						if (!firstLoop) {
							iterateLoop();
						}
					}
				}
			}

			addWarning(Labels.getLabel(locale, Labels.NumberOfSkipServiceDataset) + " " + nbDatasetServiceSkip);
			addWarning(Labels.getLabel(locale, Labels.NumberOfSkipUpToDateDataset) + " " + nbDatasetUpToDateSkip);
			logger.info("Process stop with " + nbDatasetServiceSkip + " services skipped and " + nbDatasetUpToDateSkip + " datasets up to date.");
		} catch (Exception e) {
			e.printStackTrace();

			addError(Labels.getLabel(locale, Labels.UnableToCrawlOpenData));
			addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

			setResult(Result.ERROR);
			logger.info("Process stop with error '" + e.getLocalizedMessage() + "'");
		}
	}

	private void manageMetadataToUpdate(CkanPackage newDataset, CkanPackage existingDataset) {
		Map<String, String> newExtras = newDataset.getExtras();
		
		if (existingDataset.getExtras() != null) {
			if (newExtras == null) {
				newDataset.setExtras(existingDataset.getExtras());
			}
			else {
				for (String keyExtra : existingDataset.getExtras().keySet()) {
					String newExtra = newExtras.get(keyExtra);
					if (newExtra == null) {
						//If the extra does not exist, we put it back in the updated dataset
						String value = existingDataset.getExtras().get(keyExtra);
						newDataset.putExtra(keyExtra, value);
					}
				}
			}
		}
	}

	/**
	 * Check if the licence is valid
	 * 
	 * @param licence
	 * @return true if valid
	 */
	private boolean checkLicence(String licence) {
		if (licence != null && !licence.isEmpty()) {
			for (String code : D4CHelper.LICENCE_CODES) {
				if (licence.equalsIgnoreCase(code)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Check if there is a tag OpenData or Données Ouvertes
	 * 
	 * @param keywords
	 * @return true if open
	 */
	private boolean isOpen(List<String> keywords) {
		if (keywords != null) {
			for (String tag : keywords) {
				String cleanTag = tag.replaceAll("\\s+", "").toLowerCase();
				if (cleanTag.contains(D4CHelper.TAG_OPEN) || cleanTag.contains(D4CHelper.TAG_OPEN_FR)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Check if there is tag link to D4C themes
	 * 
	 * @param keywords
	 * @return list of themes
	 */
	private JSONArray findThemes(List<Theme> themes, List<String> keywords) {
		JSONArray selectedThemes = new JSONArray();
		if (themes != null && keywords != null) {
			for (String tag : keywords) {
				for (Theme theme : themes) {
					if (tag.equalsIgnoreCase(theme.getLabel())) {
						selectedThemes.put(theme.getTitle());
						break;
					}
				}
			}
		}
		return selectedThemes;
	}

	private void manageDeletedDatasets(Locale locale, D4CHelper d4cHelper, CkanHelper ckanHelper, String org) throws Exception {
		logger.info("Start managing deleted datasets.");

		int numberOfDatasets = d4cHelper.getCkanPackageNumber(org);
		// We manage packages by chunk
		int chunkNumber = numberOfDatasets / 50 + (numberOfDatasets % 50 == 0 ? 0 : 1);
		
		int nbDatasetDeleted = 0;
		
		for (int chunk = 0; chunk < chunkNumber; chunk++) {
			List<CkanPackage> packs = d4cHelper.getCkanPackagesByChunk(org, 50, chunk * 50);
			for (CkanPackage pack : packs) {
				
				//We only check dataset from hub
				String source = pack.getExtras() != null ? pack.getExtras().get(CkanUtils.KEY_SOURCE) : null;
				if (source == null || !source.equals(CkanUtils.VALUE_HUB)) {
					continue;
				}
				
				// We check if the user has stop the process
				if (isStopByUser()) {
					logger.info("Process stop by user.");
					
					setResult(Result.ERROR);
					return;
				}

				try {
					//Check if dataset exist
					String packageName = "&fq=name:" + pack.getName();
					int nbDatasets = ckanHelper.getCkanPackageNumber(packageName);
					if (nbDatasets <= 0) {
						logger.debug("Deleting dataset '" + pack.getId() + "' with name '" + pack.getName() + "'.");
						
						//We need to delete the dataset
						d4cHelper.deleteDataset(pack.getId());
						
						nbDatasetDeleted++;
					}
				} catch (Exception e) {
					e.printStackTrace();

					logger.debug("Deleting dataset '" + pack.getId() + "' with name '" + pack.getName() + "'.");
					
					//We need to delete the dataset
					d4cHelper.deleteDataset(pack.getId());
					
					nbDatasetDeleted++;
				}
			}
		}
		
		addWarning(Labels.getLabel(locale, Labels.NumberOfDeletedDatasets) + " " + nbDatasetDeleted);
		logger.info(nbDatasetDeleted + " datasets deleted.");
	}

	// Find resource to update
//	private CkanResource findCkanResource(List<CkanResource> existingResources, CkanResource resource) {
//		if (existingResources != null) {
//			for (CkanResource existingResource : existingResources) {
//				if (existingResource.getName().equals(CkanUtils.clearValue(resource.getName(), "-"))) {
//					return existingResource;
//				}
//			}
//		}
//		return null;
//	}
//
	private boolean isDatasetOutdated(CkanPackage pack, CkanPackage existingDataset) {
		Date packDate = pack.getMetadataDate();
		Date existingDatasetDate = existingDataset.getMetadataDate();
		if (packDate == null || existingDatasetDate == null) {

			//We check if there is other date available (for example DCAT)
			try {
				String packDcatDateValue = pack.getExtras().get("dcat_modified") != null ? pack.getExtras().get("dcat_modified") : pack.getExtras().get("dcat_issued");
				String existingDcatDateValue = existingDataset.getExtras().get("dcat_modified") != null ? existingDataset.getExtras().get("dcat_modified") : existingDataset.getExtras().get("dcat_issued");
				Date metadataDate = packDcatDateValue != null && !packDcatDateValue.isEmpty() ? CkanUtils.longDf.parse(packDcatDateValue) : null;
				Date existingDcatDate = existingDcatDateValue != null && !existingDcatDateValue.isEmpty() ? CkanUtils.longDf.parse(existingDcatDateValue) : null;

				return existingDcatDate.before(metadataDate);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return true;
		}
		
		return existingDatasetDate.before(packDate);
	}
//	
//	private boolean checkResourcesDate(Date lastHarvestDate, CkanPackage pack) {
//		if (pack.getResources() != null) {
//			for (CkanResource resource : pack.getResources()) {
//				try {
//					Date creationDate = resource.getCreationDate() != null && !resource.getCreationDate().isEmpty() ? CkanUtils.shortDf.parse(resource.getCreationDate()) : null;
//					if (!creationDate.before(lastHarvestDate)) {
//						return true;
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//					
//					return true;
//				}
//				
//				try {
//					Date lastModificationDate = resource.getLastModificationDate() != null && !resource.getLastModificationDate().isEmpty() ? CkanUtils.shortDf.parse(resource.getLastModificationDate()) : null;
//					if (lastModificationDate != null && !lastModificationDate.before(lastHarvestDate)) {
//						return true;
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//					
//					return true;
//				}
//			}
//		}
//		
//		return false;
//	}
//
//	Not used anymore
//	private boolean checkDatasetDate(Date lastHarvestDate, CkanPackage pack) {
//		try {
//			String value = pack.getExtras().get(CkanUtils.KEY_DATASET_DATE);
//			JSONArray jsonDatasetDates = new JSONArray(value);
//
//			Date creationDate = null;
//			Date revisionDate = null;
//			Date publicationDate = null;
//			Date editionDate = null;
//
//			for (int j = 0; j < jsonDatasetDates.length(); j++) {
//				JSONObject jsonDatasetDate = (JSONObject) jsonDatasetDates.get(j);
//				if (!jsonDatasetDate.isNull(CkanUtils.KEY_TYPE) && jsonDatasetDate.getString(CkanUtils.KEY_TYPE).equals(CkanUtils.VALUE_DATASET_REVISION)) {
//					String jsonRevisionDate = jsonDatasetDate.getString(CkanUtils.KEY_VALUE);
//					try {
//						revisionDate = !jsonRevisionDate.isEmpty() ? CkanUtils.shortDf.parse(jsonRevisionDate) : null;
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//
//				if (!jsonDatasetDate.isNull(CkanUtils.KEY_TYPE) && jsonDatasetDate.getString(CkanUtils.KEY_TYPE).equals(CkanUtils.VALUE_DATASET_CREATION)) {
//					String jsonCreationDate = jsonDatasetDate.getString(CkanUtils.KEY_VALUE);
//					try {
//						creationDate = !jsonCreationDate.isEmpty() ? CkanUtils.shortDf.parse(jsonCreationDate) : null;
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//
//				if (!jsonDatasetDate.isNull(CkanUtils.KEY_TYPE) && jsonDatasetDate.getString(CkanUtils.KEY_TYPE).equals(CkanUtils.VALUE_DATASET_PUBLICATION)) {
//					String jsonPublicationDate = jsonDatasetDate.getString(CkanUtils.KEY_VALUE);
//					try {
//						publicationDate = !jsonPublicationDate.isEmpty() ? CkanUtils.shortDf.parse(jsonPublicationDate) : null;
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//
//				if (!jsonDatasetDate.isNull(CkanUtils.KEY_TYPE) && jsonDatasetDate.getString(CkanUtils.KEY_TYPE).equals(CkanUtils.VALUE_DATASET_EDITION)) {
//					String jsonEditionDate = jsonDatasetDate.getString(CkanUtils.KEY_VALUE);
//					try {
//						editionDate = !jsonEditionDate.isEmpty() ? CkanUtils.shortDf.parse(jsonEditionDate) : null;
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
//			
//			if (creationDate == null && revisionDate == null && publicationDate == null && editionDate == null) {
//				return true;
//			}
//
//			// Check - We need to update if
//			// 	- creation date != null && creation date is equal or after the lastharvestdate
//			// 	- revisionDate date != null && revisionDate date is equal or after the lastharvestdate
//			// 	- publication date != null && publication date is equal or after the lastharvestdate
//			if (creationDate != null && !creationDate.before(lastHarvestDate)
//					|| revisionDate != null && !revisionDate.before(lastHarvestDate)
//					|| publicationDate != null && !publicationDate.before(lastHarvestDate)
//					|| editionDate != null && !editionDate.before(lastHarvestDate)) {
//				return true;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			
//			//If there is a problem, we need to update the dataset
//			return true;
//		}
//
//		return false;
//	}

	/**
	 * This method check the ressources from a crawl to define the one which
	 * will be unzip or add at the end to display the data on D4C
	 * 
	 * We need to define a choice order
	 * 
	 * 1. Geojson 2. Zip geojson 3. Shape 4. Zip shape 5. KML 6. Zip KML 7. CSV
	 * 8. XLSX
	 * 
	 * @param resources
	 * @return
	 */
	private CkanResource checkResourceToDisplay(List<CkanResource> resources) {
		CkanResource resourceToDisplay = null;
		D4CFormat currentFormat = D4CFormat.UNKNOWN;
		for (CkanResource resource : resources) {
			D4CFormat newFormat = getD4CFormat(resource);
			if (isPriority(newFormat, currentFormat)) {
				resourceToDisplay = resource;
				currentFormat = newFormat;
			}
		}
		return resourceToDisplay;
	}

	private D4CFormat getD4CFormat(CkanResource resource) {
		if (resource.getFormat() != null) {
			if (resource.getFormat().equalsIgnoreCase("geojson")) {
				return D4CFormat.GEOJSON;
			}

			if (resource.getFormat().equalsIgnoreCase("shp") || resource.getFormat().equalsIgnoreCase("shape")) {
				return D4CFormat.SHAPE;
			}

			if (resource.getFormat().equalsIgnoreCase("kml")) {
				return D4CFormat.KML;
			}

			if (resource.getFormat().equalsIgnoreCase("csv")) {
				return D4CFormat.CSV;
			}

			if (resource.getFormat().equalsIgnoreCase("xlsx")) {
				return D4CFormat.XLSX;
			}

			if (resource.getFormat().equalsIgnoreCase("wms")) {
				return D4CFormat.WMS;
			}

			if (resource.getFormat().equalsIgnoreCase("wfs")) {
				return D4CFormat.WFS;
			}

			if (resource.getFormat().equalsIgnoreCase("gml")) {
				return D4CFormat.GML;
			}

			if (resource.getFormat().equalsIgnoreCase("zip")) {
				if (resource.getName().toLowerCase().contains("geojson")) {
					return D4CFormat.ZIP_GEOJSON;
				}

				if (resource.getName().toLowerCase().contains("shp") || resource.getName().toLowerCase().equalsIgnoreCase("shape")) {
					return D4CFormat.ZIP_SHAPE;
				}

				if (resource.getName().toLowerCase().contains("kml")) {
					return D4CFormat.ZIP_KML;
				}

				return D4CFormat.ZIP;
			}
		}

		return D4CFormat.UNKNOWN;
	}

	private D4CFormat getD4CFormat(WFSFormat format) {
		switch (format) {
		case FORMAT_GML3:
			return D4CFormat.GML;
		case FORMAT_JSON:
			return D4CFormat.GEOJSON;
		case FORMAT_JSON_SUBTYPE_GEOJSON:
			return D4CFormat.GEOJSON;
		default:
			break;
		}
		return D4CFormat.GEOJSON;
	}

	private boolean needManageFile(D4CFormat format) {
		boolean manageResource = activity.isManageResource();
		if (!manageResource) {
			return false;
		}
		
		if (format == null) {
			return true;
		}
		
		switch (format) {
		case CSV:
		case GEOJSON:
		case KML:
		case SHAPE:
		case XLSX:
		case ZIP_GEOJSON:
		case ZIP_KML:
		case ZIP_SHAPE:
		case ZIP:
		case UNKNOWN:
			return true;
		case WMS:
		case WFS:
			return false;
		default:
			break;
		}
		
		return false;
	}

	private boolean isPriority(D4CFormat newFormat, D4CFormat currentFormat) {
		return newFormat.getOrder() <= currentFormat.getOrder() && newFormat != D4CFormat.UNKNOWN;
	}

	private void crawlODS(Locale locale, String url, boolean addPrefix, ODSProperties properties, Cible cible) {
		try {
			boolean crawlOneDataset = properties.isCrawlOneDataset();

			String targetUrl = cible.getUrl(parameters, variables);
			String targetOrg = cible.getOrg();
			String targetApiKey = cible.getApiKey();
			Integer limit = properties != null ? properties.getLimit() : null;

			ODSHelper helper = new ODSHelper(url);
			if (crawlOneDataset) {
				boolean updateDataset = activity.isUpdateDataset();
				CkanPackage selectedPack = activity.getSelectedPackage();

				String datasetId = properties.getDatasetId();
				String query = properties.getQuery();

				helper.crawlOneDataset(targetUrl, targetOrg, targetApiKey, selectedPack, datasetId, query, null, limit, addPrefix, updateDataset);
			}
			else {
				helper.crawl(targetUrl, targetOrg, targetApiKey, null, limit, addPrefix);
			}
		} catch (Exception e) {
			e.printStackTrace();

			addError(Labels.getLabel(locale, Labels.UnableToCrawlOpenData));
			addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

			setResult(Result.ERROR);
		}
	}

	@Override
	public List<Variable> getVariables() {
		return activity.getVariables(null);
	}

	@Override
	public List<Parameter> getParameters() {
		return activity.getParameters(null);
	}

	@Override
	protected void clearResources() {
	}
}
