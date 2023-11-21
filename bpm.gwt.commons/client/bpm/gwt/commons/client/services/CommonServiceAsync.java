package bpm.gwt.commons.client.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

import bpm.document.management.core.model.Tree;
import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.utils.AxisInfo;
import bpm.gwt.commons.shared.GedInformations;
import bpm.gwt.commons.shared.VanillaServerInformations;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataData;
import bpm.gwt.commons.shared.repository.DocumentDefinitionDTO;
import bpm.gwt.commons.shared.repository.SaveItemInformations;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.DocumentItem;
import bpm.mdm.model.supplier.MdmDirectory;
import bpm.mdm.model.supplier.Supplier;
import bpm.smart.core.model.AirCube;
import bpm.smart.core.model.RScript;
import bpm.smart.core.model.RScriptModel;
import bpm.update.manager.api.beans.UpdateInformations;
import bpm.vanilla.map.core.design.IMapDefinitionService.ManageAction;
import bpm.vanilla.map.core.design.MapLayer;
import bpm.vanilla.map.core.design.MapServer;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.PublicUrl;
import bpm.vanilla.platform.core.beans.PublicUrl.TypeURL;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaImage;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.DatasetResultQuery;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceJdbc;
import bpm.vanilla.platform.core.beans.data.HbaseTable;
import bpm.vanilla.platform.core.beans.fmdt.FmdtData;
import bpm.vanilla.platform.core.beans.fmdt.FmdtDimension;
import bpm.vanilla.platform.core.beans.forms.Form;
import bpm.vanilla.platform.core.beans.forms.FormField;
import bpm.vanilla.platform.core.beans.meta.Meta;
import bpm.vanilla.platform.core.beans.meta.MetaForm;
import bpm.vanilla.platform.core.beans.meta.MetaLink;
import bpm.vanilla.platform.core.beans.meta.MetaLink.TypeMetaLink;
import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.ClassDefinition;
import bpm.vanilla.platform.core.beans.resources.D4C;
import bpm.vanilla.platform.core.beans.resources.D4CItem;
import bpm.vanilla.platform.core.beans.resources.D4CItem.TypeD4CItem;
import bpm.vanilla.platform.core.beans.resources.DatabaseServer;
import bpm.vanilla.platform.core.beans.resources.DocumentSchema;
import bpm.vanilla.platform.core.repository.Comment;
import bpm.vanilla.platform.core.repository.ItemMetadataTableLink;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public interface CommonServiceAsync {

	public void ping(AsyncCallback<Boolean> callback);
	
	public void forwardSecurityUrl(String url, String paramLocal, AsyncCallback<String> callback);

	public void getComments(int objectId, int type, AsyncCallback<List<Comment>> callback);
	
	public void deleteComment(Comment comment, AsyncCallback<Void> callback);
	
	public void addComment(Comment comment, List<Integer> groupIds, AsyncCallback<Void> callback);
	
	public void editComment(Comment comment, List<Integer> groupIds, AsyncCallback<Void> callback);
	
	public void getRepositoryTree(int typeRepository, AsyncCallback<List<RepositoryDirectory>> callback);
	
	public void getAklaboxTree(AsyncCallback<List<Tree>> callback);

	public void getCommonJavascript(AsyncCallback<List<String>> callback);

	public void getCommonJavascriptFiles(AsyncCallback<List<String>> callback);
	
	public void getDatasources(AsyncCallback<List<Datasource>> callback);

	public void deleteDatasource(Datasource datasource, AsyncCallback<Void> callback);

	public void addDatasource(Datasource datasource, AsyncCallback<Datasource> callback);

	public void updateDatasource(Datasource datasource, AsyncCallback<Void> callback);

	public void getJdbcDrivers(AsyncCallback<List<String>> callback);

	public void checkUpdates(AsyncCallback<UpdateInformations> asyncCallback);
	
	public void getDatasets(AsyncCallback<List<Dataset>> callback);
	
	public void deleteDataset(Dataset dataset, AsyncCallback<Void> callback);

	public void addDataset(Dataset dataset, AsyncCallback<Dataset> callback);

	public void updateDataset(Dataset dataset, AsyncCallback<Void> callback);
	
	public void addDatasourceAndDataset(int metadataId, String modelName, String packageName, String datasourceName, String datasetName, Dataset dataset, AsyncCallback<Void> callback);

	public void getDataSetMetaData(DatasourceJdbc dts, String query, AsyncCallback<ArrayList<DataColumn>> callback);

	public void runRScript(RScriptModel box, AsyncCallback<RScriptModel> callback);

	public void getRDataSetMetaData(Dataset dataset, String userEnv, AsyncCallback<ArrayList<DataColumn>> asyncCallback);
	
	public void setFeedback(boolean allowFeedback, AsyncCallback<Void> callback);
	
	public void sendFeedBackMessage(String mail, String message, boolean isSupport, AsyncCallback<Void> callback);

	public void getDatasetCsvMetadata(Datasource datasourceCsv, AsyncCallback<List<DataColumn>> callback);

	public void getDatasourceHBaseMetadataListTables(Datasource datasourceHBase, AsyncCallback<List<HbaseTable>> callback);

	public void getDatasetHBaseMetadata(String tableName, Datasource datasourceHBase, AsyncCallback<List<DataColumn>> callback);

	public void getDatasetCsvVanillaMetadata(Datasource datasourceCsvVanilla, AsyncCallback<List<String>> callback);
	
	public void getDatasetArchitectMetadata(String tableName, Datasource datasourceArchitect, AsyncCallback<List<DataColumn>> callback);

	public void getHdfsFile(String text, AsyncCallback<String> asyncCallback);

	public void generateCube(List<FmdtDimension> dimensions, List<FmdtData> measures, String url, String request, String title, String description, AsyncCallback<String> callback);

	public void generateCSVinR(Dataset dts, AsyncCallback<String> asyncCallback);

	public void testJdbcDatasource(DatasourceJdbc jdbc, AsyncCallback<String> callback);

	public void deleteFile(String path, AsyncCallback<Void> callback);

	public void saveAirCube(AirCube airCube, AsyncCallback<Void> callback);

	public void getCubesbyDataset(int id, AsyncCallback<List<AirCube>> asyncCallback);

	public void loadCube(String xmlModel, AsyncCallback<HashMap<String, Serializable>> callback);

	public void getPermittedDatasets(AsyncCallback<List<Dataset>> callback);

	public void getPermittedDatasources(AsyncCallback<List<Datasource>> asyncCallback);

	public void getRepositoryTree(String url, String login, String pass, Group group, Repository repo, int typeRepository, AsyncCallback<List<RepositoryDirectory>> callback);
	
	public void saveItem(SaveItemInformations itemInformations, AsyncCallback<RepositoryItem> asyncCallback);

	public void getRScriptbyId(int idScript, AsyncCallback<RScript> asyncCallback);

	public void getIcons(AsyncCallback<List<String>> callback);

	public void getItemById(int itemId, String url, String login, String pass, Group group, Repository repo, AsyncCallback<RepositoryItem> callback);
	
	public void getItemById(int itemId, AsyncCallback<RepositoryItem> callback);

	public void getSocialDataSetMetaData(Dataset dataset, Datasource datasource, String string, AsyncCallback<ArrayList<DataColumn>> asyncCallback);

	public void saveItem(String url, String login, String pass, Group group, Repository repo, SaveItemInformations itemInfo, AsyncCallback<RepositoryItem> callback);
	
	public void addDirectory(String name, String description, RepositoryDirectory parent, AsyncCallback<Void> callback);
	
	public void deleteDirectory(RepositoryDirectory selectedDirectory, AsyncCallback<Void> callback);

	public void getUsers(AsyncCallback<List<User>> callback);

	public void getAllMetrics(AsyncCallback<List<Metric>> callback);

	public void browseAxis(Axis axis, AsyncCallback<AxisInfo> callback);

	public void indexFile(GedInformations gedInfos, AsyncCallback<Void> callback);

	public void getAllGEDDocuments(AsyncCallback<List<DocumentDefinitionDTO>> callback);

	public void getImages(AsyncCallback<List<VanillaImage>> callback);
	
	public void getImage(int imageId, AsyncCallback<VanillaImage> callback);
	
	public void getJdbcData(int datasourceId, int datasetId, int limit, AsyncCallback<MetadataData> callback);

	public void addPublicUrl(PublicUrl publicUrl, HashMap<String, String> parameters, AsyncCallback<PublicUrl> callback);
	
	public void deletePublicUrl(PublicUrl publicUrl, AsyncCallback<Void> callback);
	
	public void getPublicUrls(int itemId, TypeURL typeUrl, AsyncCallback<List<PublicUrl>> callback);
	
	public void getCkanDatasets(String ckanUrl, String organisation, AsyncCallback<List<CkanPackage>> callback);

	public void getItemsByType(int itemType, int subtype, AsyncCallback<List<RepositoryItem>> callback);

	public void loadForm(RepositoryItem repositoryItem, AsyncCallback<Form> asyncCallback);

	public void executeDataset(Dataset dataset, AsyncCallback<DatasetResultQuery> asyncCallback);

	public void saveFormValues(Form form, AsyncCallback<Void> asyncCallback);

	public void executeFormSearchQuery(Form form, AsyncCallback<List<Map<String, FormField>>> asyncCallback);

	public void updateFormValues(Form form, Map<String, FormField> editedLine, AsyncCallback<Void> asyncCallback);

	public void updateForm(Form form, AsyncCallback<Void> asyncCallback);

	public void getSuppliers(AsyncCallback<List<Supplier>> asyncCallback);
	
	public void getSuppliersByServer(String login, String pass, String url, AsyncCallback<List<Supplier>> callback);

	public void getLinkedItems(int contractId, AsyncCallback<List<DocumentItem>> callback);
	
	public void createDataPreparation(String name, Contract contract, String separator, AsyncCallback<Void> callback);

	public void addContract(Supplier supplier, Contract contract, AsyncCallback<Contract> asyncCallback);

	public void confirmUpload(Contract contract, String name, String filePath, AsyncCallback<Void> asyncCallback);
	
	public void getMapServers(AsyncCallback<List<MapServer>> asyncCallback);
	
	public void manageMapServer(MapServer server, ManageAction action, AsyncCallback<MapServer> asyncCallback);
	
	public void loadLayers(MapServer mapServer, AsyncCallback<List<MapLayer>> asyncCallback);
	
	public void getArcgisServices(MapServer server, AsyncCallback<List<MapServer>> asyncCallback);

	public void getFormat(Contract contract, AsyncCallback<String> callback);
	
	public void getMetaLinks(VanillaServerInformations server, int itemId, TypeMetaLink type, boolean loadValue, AsyncCallback<List<MetaLink>> callback);
	
	public void getMetaByForm(VanillaServerInformations server, int formId, AsyncCallback<List<Meta>> callback);
	
	public void manageMetaValues(VanillaServerInformations server, List<MetaLink> values, AsyncCallback<Void> callback);
	
	public void getDatasourceById(int datasourceId, AsyncCallback<Datasource> callback);
	
	public void getMetadataLinks(int itemId, AsyncCallback<List<ItemMetadataTableLink>> callback);
	
	public void getMetaForms(AsyncCallback<List<MetaForm>> callback);

	public void addItemMetadataTableLink(ItemMetadataTableLink link, AsyncCallback<Void> callback);
	
	public void deleteItemMetadataTableLink(ItemMetadataTableLink link, AsyncCallback<Void> callback);

	public void getDataSetMetaData(DatabaseServer server, AsyncCallback<ArrayList<DataColumn>> callback);

	public void loadSchemaValidations(AsyncCallback<List<ClassDefinition>> callback);

	public void getD4CDefinitions(AsyncCallback<List<D4C>> callback);
	
	public void getD4CItems(int parentId, TypeD4CItem type, AsyncCallback<HashMap<String, HashMap<String, List<D4CItem>>>> callback);
	
	public void manageD4CDefinition(D4C d4c, bpm.vanilla.platform.core.ManageAction action, AsyncCallback<D4C> callback);
	
	public void manageD4CItem(D4CItem item, bpm.vanilla.platform.core.ManageAction action, AsyncCallback<D4CItem> callback);

	public void getMdmDirectories(AsyncCallback<List<MdmDirectory>> callback);

	public void saveMdmDirectory(MdmDirectory directory, AsyncCallback<MdmDirectory> callback);

	public void deleteMdmDirectory(MdmDirectory selectedDirectory, AsyncCallback<Void> callback);

	public void getLinkedSchemas(int contractId, AsyncCallback<List<DocumentSchema>> callback);
}
