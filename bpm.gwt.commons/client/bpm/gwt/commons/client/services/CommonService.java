package bpm.gwt.commons.client.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import bpm.document.management.core.model.Tree;
import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.utils.AxisInfo;
import bpm.gwt.commons.client.services.exception.ServiceException;
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

@RemoteServiceRelativePath("commonService")
public interface CommonService extends RemoteService {
	
	public static class Connect{
		private static CommonServiceAsync instance;
		public static CommonServiceAsync getInstance(){
			if(instance == null){
				instance = (CommonServiceAsync) GWT.create(CommonService.class);
			}
			return instance;
		}
	}
	
	public Boolean ping() throws ServiceException;

	/**
	 * Gives a url that contains security in the form of
	 * url +=
	 * ?bpm.vanilla.sessionId=whateva
	 * &bpm.vanilla.groupId=1
	 * &bpm.vanilla.repositoryId=1
	 * 
	 * @param url
	 * @return
	 * @throws ServiceException
	 */
	public String forwardSecurityUrl(String url, String paramLocal) throws ServiceException;
	
	public List<Comment> getComments(int objectId, int type) throws ServiceException;
	
	public void deleteComment(Comment comment) throws ServiceException;
	
	public void addComment(Comment comment, List<Integer> groupIds) throws ServiceException;
	
	public void editComment(Comment comment, List<Integer> groupIds) throws ServiceException;
	
	public List<RepositoryDirectory> getRepositoryTree(int typeRepository) throws ServiceException;
	
	public List<Tree> getAklaboxTree() throws ServiceException;
	
	public List<String> getCommonJavascript() throws ServiceException;
	
	public List<String> getCommonJavascriptFiles() throws ServiceException;
	
	public List<Datasource> getDatasources() throws Exception;
	
	public void deleteDatasource(Datasource datasource) throws Exception;
	
	public Datasource addDatasource(Datasource datasource) throws Exception;
	
	public void updateDatasource(Datasource datasource) throws Exception;
	
	public List<String> getJdbcDrivers() throws Exception;

	public UpdateInformations checkUpdates() throws ServiceException;
	
	public List<Dataset> getDatasets() throws Exception;
	
	public void deleteDataset(Dataset dataset) throws Exception;
	
	public Dataset addDataset(Dataset dataset) throws Exception;
	
	public void updateDataset(Dataset dataset) throws Exception;
	
	public void addDatasourceAndDataset(int metadataId, String modelName, String packageName, String datasourceName, String datasetName, Dataset dataset) throws Exception;

	public ArrayList<DataColumn> getDataSetMetaData(DatasourceJdbc dts, String query) throws ServiceException;

	public RScriptModel runRScript(RScriptModel box) throws Exception;

	public ArrayList<DataColumn> getRDataSetMetaData(Dataset dataset, String userEnv) throws Exception;
	
	public void setFeedback(boolean allowFeedback) throws ServiceException;
	
	public void sendFeedBackMessage(String mail, String message, boolean isSupport) throws ServiceException;
	
	public List<DataColumn> getDatasetCsvMetadata(Datasource datasourceCsv) throws ServiceException;

	public List<HbaseTable> getDatasourceHBaseMetadataListTables(Datasource datasourceHBase) throws ServiceException;

	public List<DataColumn> getDatasetHBaseMetadata(String tableName, Datasource datasourceHBase) throws ServiceException;
	
	public List<DataColumn> getDatasetArchitectMetadata(String tableName, Datasource datasourceArchitect) throws ServiceException;

	public List<String> getDatasetCsvVanillaMetadata(Datasource datasourceCsvVanilla) throws ServiceException;

	public String getHdfsFile(String text) throws ServiceException;

	public String generateCube(List<FmdtDimension> dimensions, List<FmdtData> measures, String url, String request, String title, String description) throws ServiceException;

	public String generateCSVinR(Dataset dts) throws ServiceException;
	
	public String testJdbcDatasource(DatasourceJdbc datasource) throws ServiceException;

	public void deleteFile(String path) throws ServiceException;

	public void saveAirCube(AirCube airCube) throws ServiceException;

	public List<AirCube> getCubesbyDataset(int id) throws ServiceException;

	public HashMap<String, Serializable> loadCube(String xmlModel) throws ServiceException;

	public List<Dataset> getPermittedDatasets() throws Exception;

	public List<Datasource> getPermittedDatasources() throws Exception;

	public List<RepositoryDirectory> getRepositoryTree(String url, String login, String pass, Group group, Repository repo, int typeRepository) throws ServiceException;

	public RepositoryItem saveItem(SaveItemInformations itemInfo) throws ServiceException;

	public RScript getRScriptbyId(int idScript) throws Exception;
	
	public List<String> getIcons() throws ServiceException;

	public RepositoryItem getItemById(int itemId, String url, String login, String pass, Group group, Repository repo) throws ServiceException;

	public ArrayList<DataColumn> getSocialDataSetMetaData(Dataset dataset, Datasource datasource, String string) throws Exception;

	public RepositoryItem saveItem(String url, String login, String pass, Group group, Repository repo, SaveItemInformations itemInfo) throws ServiceException;

	public RepositoryItem getItemById(int itemId) throws ServiceException;
	
	public void addDirectory(String name, String description, RepositoryDirectory parent) throws ServiceException;
	
	public void deleteDirectory(RepositoryDirectory selectedDirectory) throws ServiceException;

	public List<User> getUsers() throws ServiceException;

	public List<Metric> getAllMetrics() throws ServiceException;

	public AxisInfo browseAxis(Axis axis) throws Exception;

	public void indexFile(GedInformations gedInfos) throws ServiceException;

	public List<DocumentDefinitionDTO> getAllGEDDocuments() throws ServiceException;

	public List<VanillaImage> getImages() throws ServiceException;
	
	public VanillaImage getImage(int imageId) throws ServiceException;

	public MetadataData getJdbcData(int datasourceId, int datasetId, int limit) throws ServiceException;

	public PublicUrl addPublicUrl(PublicUrl publicUrl, HashMap<String, String> parameters) throws ServiceException;

	public void deletePublicUrl(PublicUrl publicUrl) throws ServiceException;

	public List<PublicUrl> getPublicUrls(int itemId, TypeURL typeUrl) throws ServiceException;

	public List<CkanPackage> getCkanDatasets(String ckanUrl, String organisation) throws ServiceException;
	
	public List<RepositoryItem> getItemsByType(int itemType, int subtype) throws ServiceException;

	public Form loadForm(RepositoryItem repositoryItem) throws ServiceException;

	public DatasetResultQuery executeDataset(Dataset dataset) throws ServiceException;

	public void saveFormValues(Form form) throws ServiceException;

	public List<Map<String, FormField>> executeFormSearchQuery(Form form) throws ServiceException;

	public void updateFormValues(Form form, Map<String, FormField> editedLine) throws ServiceException;

	public void updateForm(Form form) throws ServiceException;

	public List<Supplier> getSuppliers() throws ServiceException;

	public List<Supplier> getSuppliersByServer(String login, String pass, String url) throws ServiceException;

	public List<DocumentItem> getLinkedItems(int contractId) throws ServiceException;
	
	public void createDataPreparation(String name, Contract contract, String separator) throws ServiceException;

	public Contract addContract(Supplier supplier, Contract contract) throws ServiceException;

	public void confirmUpload(Contract contract, String name, String filePath) throws ServiceException;
	
	public List<MapServer> getMapServers() throws ServiceException;
	
	public MapServer manageMapServer(MapServer server, ManageAction action) throws ServiceException;
	
	public List<MapLayer> loadLayers(MapServer mapServer) throws ServiceException;
	
	public List<MapServer> getArcgisServices(MapServer server) throws ServiceException;

	public String getFormat(Contract contract) throws ServiceException;
	
	public List<MetaLink> getMetaLinks(VanillaServerInformations server, int itemId, TypeMetaLink type, boolean loadValue) throws ServiceException;

	public List<Meta> getMetaByForm(VanillaServerInformations server, int formId) throws ServiceException;
	
	public void manageMetaValues(VanillaServerInformations server, List<MetaLink> values) throws ServiceException;

	public Datasource getDatasourceById(int datasourceId) throws ServiceException;
	
	public List<ItemMetadataTableLink> getMetadataLinks(int itemId) throws ServiceException;
	
	public List<MetaForm> getMetaForms() throws ServiceException;

	public void addItemMetadataTableLink(ItemMetadataTableLink link) throws ServiceException;
	
	public void deleteItemMetadataTableLink(ItemMetadataTableLink link) throws ServiceException;
	
	public ArrayList<DataColumn> getDataSetMetaData(DatabaseServer server) throws ServiceException;
	
	public List<ClassDefinition> loadSchemaValidations() throws ServiceException;
	
	public List<D4C> getD4CDefinitions() throws ServiceException;
	
	public HashMap<String, HashMap<String, List<D4CItem>>> getD4CItems(int parentId, TypeD4CItem type) throws ServiceException;
	
	public D4C manageD4CDefinition(D4C item, bpm.vanilla.platform.core.ManageAction action) throws ServiceException;
	
	public D4CItem manageD4CItem(D4CItem item, bpm.vanilla.platform.core.ManageAction action) throws ServiceException;

	public List<MdmDirectory> getMdmDirectories() throws ServiceException;

	public MdmDirectory saveMdmDirectory(MdmDirectory directory) throws ServiceException; 

	public void deleteMdmDirectory(MdmDirectory selectedDirectory) throws ServiceException;

	public List<DocumentSchema> getLinkedSchemas(int contractId) throws ServiceException;
}


