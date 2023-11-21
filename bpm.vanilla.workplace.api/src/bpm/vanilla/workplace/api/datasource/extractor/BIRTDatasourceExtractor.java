package bpm.vanilla.workplace.api.datasource.extractor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.vanilla.workplace.core.datasource.IDatasource;
import bpm.vanilla.workplace.core.datasource.IDatasourceExtractor;
import bpm.vanilla.workplace.core.datasource.IDatasourceType;
import bpm.vanilla.workplace.core.datasource.ModelDatasourceJDBC;
import bpm.vanilla.workplace.core.datasource.ModelDatasourceRepository;
import bpm.vanilla.workplace.core.datasource.IDatasourceType.DatasourceType;
import bpm.vanilla.workplace.core.disco.DisconnectedBackupConnection;
import bpm.vanilla.workplace.core.disco.DisconnectedDataset;

public class BIRTDatasourceExtractor implements IDatasourceExtractor {

	private static final String NODE_DATASOURCES = "data-sources";
	private static final String NODE_DATASOURCE = "oda-data-source";

	private static final String NODE_DATASETS = "data-sets";
	private static final String NODE_DATASET = "oda-data-set";
	private static final String NODE_PROPERTY = "property";
	private static final String NODE_DATASET_DATASOURCE = "dataSource";
	private static final String NODE_XML_PROPERTY = "xml-property";
	private static final String NODE_QUERY_TEXT = "queryText";

	private static final String ATTRIBUTE_DATASOURCE_EXTENSION = "extensionID";

	@Override
	public List<IDatasource> extractDatasources(String xml) throws Exception {
		List<IDatasource> datasources = new ArrayList<IDatasource>();

		Document document = DocumentHelper.parseText(xml);
		Element ds = document.getRootElement().element(NODE_DATASOURCES);

		if (ds != null) {
			for (Object o : ds.elements(NODE_DATASOURCE)) {
				String datasourceName = ((Element) o).attribute("name").getText();
				String extId = ((Element) o).attribute(ATTRIBUTE_DATASOURCE_EXTENSION).getText();
				if (extId.equals(IDatasourceType.METADATA) || extId.equals(IDatasourceType.METADATA_OLAP)) {

					String user = null;
					String password = null;
					String url = null;
					String dirId = null;
					String groupName = null;
					String repositoryId = null;
					String vanillaRuntimeUrl = null;
					String connectionName = null;
					String businessPackage = null;
					String businessModel = null;
					for (Object p : ((Element) o).elements("property")) {
						if (((Element) p).attribute("name").getText().equals(IDatasourceType.METADATA_USER)) {
							user = ((Element) p).getText();
						}
						else if (((Element) p).attribute("name").getText().equals(IDatasourceType.METADATA_PASSWORD)) {
							password = ((Element) p).getText();
						}
						else if (((Element) p).attribute("name").getText().equals(IDatasourceType.METADATA_URL)) {
							url = ((Element) p).getText();
						}
						else if (((Element) p).attribute("name").getText().equals(IDatasourceType.METADATA_DIRECTORY_ITEM_ID)) {
							dirId = ((Element) p).getText();
						}
						else if (((Element) p).attribute("name").getText().equals(IDatasourceType.GROUP_NAME)) {
							groupName = ((Element) p).getText();
						}
						else if (((Element) p).attribute("name").getText().equals(IDatasourceType.METADATA_REPOSITORY_ID)) {
							repositoryId = ((Element) p).getText();
						}
						else if (((Element) p).attribute("name").getText().equals(IDatasourceType.METADATA_VANILLA_URL)) {
							vanillaRuntimeUrl = ((Element) p).getText();
						}
						else if (((Element) p).attribute("name").getText().equals(IDatasourceType.BUSINESS_PACKAGE)) {
							businessPackage = ((Element) p).getText();
						}
						else if (((Element) p).attribute("name").getText().equals(IDatasourceType.BUSINESS_MODEL)) {
							businessModel = ((Element) p).getText();
						}
					}
					
					for (Object p : ((Element) o).elements("list-property")) {
						String priv = ((Element) p).attribute("name").getText();
						if (priv.equals("privateDriverProperties")) {
							for (Object e : ((Element) p).elements("ex-property")) {
								if (((Element) e).element("name").getText().equals(IDatasourceType.CONNECTION_NAME)) {
									connectionName = ((Element) e).element("value").getText();
									break;
								}
							}
						}
					}

					ModelDatasourceRepository datasource = new ModelDatasourceRepository();
					datasource.setName(datasourceName);
					datasource.setExtensionId(extId);
					datasource.setType(DatasourceType.DATASOURCE_REPOSITORY);
					datasource.setUser(user);
					datasource.setPassword(password);
					datasource.setRepositoryUrl(url);
					datasource.setDirId(dirId);
					datasource.setGroupName(groupName);
					datasource.setRepositoryId(repositoryId);
					datasource.setVanillaRuntimeUrl(vanillaRuntimeUrl);
					datasource.setConnectionName(connectionName);
					datasource.setBusinessPackage(businessPackage);
					datasource.setBusinessModel(businessModel);

					datasources.add(datasource);
				}
				else if (extId.equals(IDatasourceType.CSV_ODA) || extId.equals(IDatasourceType.EXCEL_ODA)) {

					for (Object p : ((Element) o).elements("list-property")) {
						String priv = ((Element) p).attribute("name").getText();
						if (priv.equals("privateDriverProperties")) {

							String user = null;
							String password = null;
							String repositoryId = null;
							String repositoryItemId = null;
							String groupId = null;
							for (Object e : ((Element) p).elements("ex-property")) {
								if (((Element) e).element("name").getText().equals(IDatasourceType.CSV_REPOSITORY_ITEM_ID)) {
									repositoryItemId = ((Element) e).element("value").getText();
								}
								else if (((Element) e).element("name").getText().equals(IDatasourceType.CSV_REPOSITORY_ID)) {
									repositoryId = ((Element) e).element("value").getText();
								}
								else if (((Element) e).element("name").getText().equals(IDatasourceType.CSV_GROUP_ID)) {
									groupId = ((Element) e).element("value").getText();
								}
								else if (((Element) e).element("name").getText().equals(IDatasourceType.CSV_USER)) {
									user = ((Element) e).element("value").getText();
								}
								else if (((Element) e).element("name").getText().equals(IDatasourceType.CSV_PASSWORD)) {
									password = ((Element) e).element("value").getText();
								}
							}

							ModelDatasourceRepository datasource = new ModelDatasourceRepository();
							datasource.setName(datasourceName);
							datasource.setExtensionId(extId);
							datasource.setType(DatasourceType.DATASOURCE_REPOSITORY);
							datasource.setUser(user);
							datasource.setPassword(password);
							datasource.setDirId(repositoryItemId);
							datasource.setGroupId(groupId);
							datasource.setRepositoryId(repositoryId);

							datasources.add(datasource);
						}
					}
				}
				else if (extId.equals(IDatasourceType.FWR_ODA)) {

					String user = null;
					String password = null;
					String repositoryUrl = null;
					String url = null;
					String dirId = null;
					String groupName = null;
					String groupId = null;
					for (Object p : ((Element) o).elements("property")) {
						if (((Element) p).attribute("name").getText().equals(IDatasourceType.FWR_USER)) {
							user = ((Element) p).getText();
						}
						else if (((Element) p).attribute("name").getText().equals(IDatasourceType.FWR_PASSWORD)) {
							password = ((Element) p).getText();
						}
						else if (((Element) p).attribute("name").getText().equals(IDatasourceType.FWR_URL)) {
							url = ((Element) p).getText();
						}
						else if (((Element) p).attribute("name").getText().equals(IDatasourceType.FWR_REPOSITORY_URL)) {
							repositoryUrl = ((Element) p).getText();
						}
						else if (((Element) p).attribute("name").getText().equals(IDatasourceType.FWR_GROUP_ID)) {
							groupId = ((Element) p).getText();
						}
						else if (((Element) p).attribute("name").getText().equals(IDatasourceType.FWR_GROUP_NAME)) {
							groupName = ((Element) p).getText();
						}
					}

					for (Object p : ((Element) o).elements("list-property")) {
						String priv = ((Element) p).attribute("name").getText();
						if (priv.equals("privateDriverProperties")) {
							for (Object e : ((Element) p).elements("ex-property")) {
								if (((Element) e).element("name").getText().equals(IDatasourceType.FWR_REPORT_ID)) {
									dirId = ((Element) e).element("value").getText();
								}
							}
						}
					}

					ModelDatasourceRepository datasource = new ModelDatasourceRepository();
					datasource.setName(datasourceName);
					datasource.setExtensionId(extId);
					datasource.setType(DatasourceType.DATASOURCE_REPOSITORY);
					datasource.setUser(user);
					datasource.setPassword(password);
					datasource.setRepositoryUrl(repositoryUrl);
					datasource.setVanillaRuntimeUrl(url);
					datasource.setDirId(dirId);
					datasource.setGroupName(groupName);
					datasource.setGroupId(groupId);

					datasources.add(datasource);
				}
				else if (extId.equals(IDatasourceType.JDBC)) {

					String driver = null;
					String url = null;
					String user = null;
					String password = null;
					boolean isPasswordEncrypted = false;
					for (Object p : ((Element) o).elements("property")) {
						if (((Element) p).attribute("name").getText().equals(IDatasourceType.ODA_DRIVER)) {
							driver = ((Element) p).getText();
						}
						else if (((Element) p).attribute("name").getText().equals(IDatasourceType.ODA_URL)) {
							url = ((Element) p).getText();
						}
						else if (((Element) p).attribute("name").getText().equals(IDatasourceType.ODA_USER)) {
							user = ((Element) p).getText();
						}
					}

					for (Object p : ((Element) o).elements("encrypted-property")) {
						if (((Element) p).attribute("name").getText().equals(IDatasourceType.ODA_PASSWORD)) {
							String encryption = ((Element) p).attributeValue("encryptionID");
							if (encryption.equalsIgnoreCase("base64")) {
								password = new String(Base64.decodeBase64(((Element) p).getText()));
							}
							else {
								password = ((Element) p).getText();
							}
						}
					}

					ModelDatasourceJDBC datasource = new ModelDatasourceJDBC();
					datasource.setName(datasourceName);
					datasource.setExtensionId(extId);
					datasource.setType(DatasourceType.DATASOURCE_JDBC);
					datasource.setUser(user);
					datasource.setPassword(password);
					datasource.setDriver(driver);
					datasource.setFullUrl(url);
					datasource.setUseFullUrl(true);
					datasource.setIsPasswordEncrypted(isPasswordEncrypted);

					datasources.add(datasource);
				}
				else if (extId.equals(IDatasourceType.FM)) {

					String fmUser = null;
					String fmPassword = null;
					for (Object p : ((Element) o).elements("property")) {
						if (((Element) p).attribute("name").getText().equals(IDatasourceType.FM_USER)) {
							fmUser = ((Element) p).getText();
						}
					}

					for (Object p : ((Element) o).elements("encrypted-property")) {
						if (((Element) p).attribute("name").getText().equals(IDatasourceType.FM_PASSWORD)) {
							String encryption = ((Element) p).attributeValue("encryptionID");
							if (encryption.equalsIgnoreCase("base64")) {
								fmPassword = new String(Base64.decodeBase64(((Element) p).getText()));
							}
							else {
								fmPassword = ((Element) p).getText();
							}
						}
					}

					ModelDatasourceJDBC datasource = new ModelDatasourceJDBC();
					datasource.setName(datasourceName);
					datasource.setExtensionId(extId);
					datasource.setType(DatasourceType.DATASOURCE_JDBC);
					datasource.setFmUser(fmUser);
					datasource.setFmPassword(fmPassword);

					datasources.add(datasource);
				}
				else if (extId.equals(IDatasourceType.LIST_DATA)) {

					String user = null;
					String password = null;
					String repositoryId = null;
					String vanillaRuntimeUrl = null;
					for (Object p : ((Element) o).elements("property")) {
						if (((Element) p).attribute("name").getText().equals(IDatasourceType.LIST_DATA_USER)) {
							user = ((Element) p).getText();
						}
						else if (((Element) p).attribute("name").getText().equals(IDatasourceType.LIST_DATA_RUNTIME_URL)) {
							vanillaRuntimeUrl = ((Element) p).getText();
						}
						else if (((Element) p).attribute("name").getText().equals(IDatasourceType.LIST_DATA_REPOSITORY_ID)) {
							repositoryId = ((Element) p).getText();
						}
					}

					for (Object p : ((Element) o).elements("encrypted-property")) {
						if (((Element) p).attribute("name").getText().equals(IDatasourceType.LIST_DATA_PASSWORD)) {
							String encryption = ((Element) p).attributeValue("encryptionID");
							if (encryption.equalsIgnoreCase("base64")) {
								password = new String(Base64.decodeBase64(((Element) p).getText()));
							}
							else {
								password = ((Element) p).getText();
							}
						}
					}

					ModelDatasourceRepository datasource = new ModelDatasourceRepository();
					datasource.setName(datasourceName);
					datasource.setExtensionId(extId);
					datasource.setType(DatasourceType.DATASOURCE_REPOSITORY);
					datasource.setUser(user);
					datasource.setPassword(password);
					datasource.setVanillaRuntimeUrl(vanillaRuntimeUrl);
					datasource.setRepositoryId(repositoryId);

					datasources.add(datasource);
				}
			}
		}

		return datasources;
	}

	@Override
	public DisconnectedBackupConnection extractBackupConnection(int itemId, String xml) throws Exception {
		DisconnectedBackupConnection backupConnection = new DisconnectedBackupConnection();
		backupConnection.setItemId(itemId);

		Document document = DocumentHelper.parseText(xml);

		Element ds = document.getRootElement().element(NODE_DATASOURCES);
		if (ds != null) {
			backupConnection.setDatasourcesXML(ds.asXML());
		}

		Element datasetsEl = document.getRootElement().element(NODE_DATASETS);
		if (datasetsEl != null) {
			for (Object o : datasetsEl.elements(NODE_DATASET)) {
				String datasetName = ((Element) o).attribute("name").getText();
				String queryText = null;
				String datasourceName = null;

				for (Object p : ((Element) o).elements(NODE_PROPERTY)) {
					if (((Element) p).attribute("name").getText().equals(NODE_DATASET_DATASOURCE)) {
						datasourceName = ((Element) p).getText();
						break;
					}
				}

				for (Object p : ((Element) o).elements(NODE_XML_PROPERTY)) {
					if (((Element) p).attribute("name").getText().equals(NODE_QUERY_TEXT)) {
						queryText = ((Element) p).getText();
						break;
					}
				}

				DisconnectedDataset discoDataset = new DisconnectedDataset();
				discoDataset.setName(datasetName);
				discoDataset.setDatasourceName(datasourceName);
				discoDataset.setQueryText(queryText);

				backupConnection.addDataset(discoDataset);
			}
		}

		return backupConnection;
	}
}
