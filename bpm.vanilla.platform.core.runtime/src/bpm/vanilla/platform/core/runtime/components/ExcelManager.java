package bpm.vanilla.platform.core.runtime.components;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DataStream;
import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.server.file.MdmFileServer;
import bpm.gateway.core.transformations.inputs.FileInputXLS;
import bpm.gateway.core.transformations.mdm.MdmContractFileInput;
import bpm.gateway.core.transformations.outputs.DataBaseOutputStream;
import bpm.gateway.runtime2.RuntimeEngine;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.TransformationLog;
import bpm.mdm.model.api.IMdmProvider;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.Supplier;
import bpm.mdm.remote.MdmRemote;
import bpm.metadata.MetaData;
import bpm.metadata.MetaDataBuilder;
import bpm.metadata.digester.BusinessTableSelect;
import bpm.metadata.digester.MetaDataDigester;
import bpm.metadata.layer.business.BusinessModel;
import bpm.metadata.layer.business.BusinessPackage;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.SQLBusinessTable;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.sql.SQLDataSource;
import bpm.metadata.layer.logical.sql.SQLDataStream;
import bpm.metadata.layer.logical.sql.SQLDataStreamElement;
import bpm.metadata.layer.physical.*;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.studio.jdbc.management.model.DriverInfo;
import bpm.studio.jdbc.management.model.ListDriver;
import bpm.vanilla.platform.core.IExcelManager;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.VanillaConstantsForFMDT;
import bpm.vanilla.platform.core.beans.ged.ComProperties;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.beans.ged.constant.RuntimeFields;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.components.ged.GedIndexRuntimeConfig;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGedComponent;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;



public class ExcelManager extends AbstractVanillaManager implements IExcelManager{

//	private DataBaseConnection socket;
//	private static IVanillaAPI vanillaApi;
//	private IVanillaContext ctx;
//	private IRepositoryApi sock;
//	private IMdmProvider provider ;
//	private IGedComponent gedComponent;
	
	

	@Override
	public String getComponentName() {
		return getClass().getName();
	}

	@Override
	protected void init() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String getListDriver() throws Exception{
		try {
			String jdbcXmlFile = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_JDBC_XML_FILE);
			Collection<DriverInfo> infos = ListDriver.getInstance(jdbcXmlFile).getDriversInfo();
			
			Document doc = DocumentHelper.createDocument();
			Element root = doc.addElement(VanillaConstantsForFMDT.REQUEST_LOADBUSINESSPACKAGE);
			//Element items = root.addElement("items");
			
			for (DriverInfo item: infos){
				Element elemItem = root.addElement("item");
				elemItem.addElement("name").setText(item.getName());
				elemItem.addElement("field").setText(item.getClassName());
				elemItem.addElement("value").setText(item.getUrlPrefix());				
			}
			
			return doc.asXML();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public String createTable (String xmlTable, HashMap<String, String> documentParameters)throws Exception{
		try{
			Connection con = initConnectionDatabase(documentParameters).getSocket(null);
			Statement stmt = con.createStatement();
			String[] requests = xmlTable.split(";");
			for (String request : requests)
				stmt.executeUpdate(request);
			
			return "success";
		}catch(Exception e){
			e.printStackTrace();
			return e.getMessage();
		}	
		
	}
	
	
	@Override
	public String testConnectionDatabase(HashMap<String, String> documentParameters) throws Exception{
		try {
			
			DataBaseConnection socket = initConnectionDatabase(documentParameters);
			
			socket.connect(null);
			socket.disconnect();
			return "success";
		}
		catch(Exception e){
			e.printStackTrace();
		}	
		return "failed";
	}
	
	
	
	@Override
	public String getColumnType(HashMap<String, String> documentParameters) throws Exception{
		try {
			DataBaseConnection socket = initConnectionDatabase(documentParameters);
			
			socket.connect(null);
			
			DatabaseMetaData metadata= socket.getSocket(null).getMetaData();
			ResultSet types = metadata.getTypeInfo();
	
			Document doc = DocumentHelper.createDocument();
			Element root = doc.addElement(VanillaConstantsForFMDT.REQUEST_GETCOLUMNSTYPE);
			
			while (types.next()){
				Element elemItem = root.addElement("item");
				elemItem.addElement("name").setText(types.getString("TYPE_NAME"));			
			}	
			
			socket.disconnect();
			
			return doc.asXML();		
		}
		catch(Exception e){
			e.printStackTrace();
		}	
		return "failed";
	}
	
	
	@Override
	public String getListTables(HashMap<String, String> documentParameters){
		
		try {			
			String tableNameSearched= documentParameters.get(VanillaConstantsForFMDT.PARAMETER_TABLENAME);			
			DataBaseConnection socket = initConnectionDatabase(documentParameters);
			
			socket.connect(null);
			
			DatabaseMetaData metadata= socket.getSocket(null).getMetaData();
			ResultSet schemas = metadata.getSchemas();
			
			List<String> tableNames = new ArrayList<String>();
			
			boolean hasSchema = false;
			try{
				while(schemas.next()){
					hasSchema = true;
					String schemaName = schemas.getString("TABLE_SCHEM");
						
					ResultSet rs = metadata.getTables(socket.getSocket(null).getCatalog(), schemaName, "%"+tableNameSearched+"%", new String[]{"TABLE"});
											
					while (rs.next()){
						tableNames.add(rs.getString("TABLE_NAME"));
					}
				}
			}catch(SQLException e){
				
			}finally{
				schemas.close();
			}
						
			if (!hasSchema){
				ResultSet rs = metadata.getTables(socket.getSocket(null).getCatalog(), null, "%"+tableNameSearched+"%", new String[]{"TABLE"});
				while (rs.next()){
					tableNames.add(rs.getString("TABLE_NAME"));
				}
			}			
						
			socket.disconnect();
			
			Document doc = DocumentHelper.createDocument();
			Element root = doc.addElement(VanillaConstantsForFMDT.REQUEST_LOADBUSINESSPACKAGE);
			//Element items = root.addElement("items");
			
			for (String tableName: tableNames){
				Element elemItem = root.addElement("item");
				elemItem.addElement("name").setText(tableName);						
			}			
			return doc.asXML();
		}
		catch(Exception e){
			e.printStackTrace();
		}	
		return "failed";
	}
	
	
	@Override
	public String getListColumns(HashMap<String, String> documentParameters){
		
		try {	
			String tableName= documentParameters.get(VanillaConstantsForFMDT.PARAMETER_TABLENAME);
			
			DataBaseConnection socket = initConnectionDatabase(documentParameters);
			
			socket.connect(null);
			
			DatabaseMetaData metadata= socket.getSocket(null).getMetaData();
			
			ResultSet rs = metadata.getColumns(socket.getSocket(null).getCatalog(), null, tableName, "%");
			ResultSet pk = metadata.getPrimaryKeys(socket.getSocket(null).getCatalog(), null, tableName);			
			
			//List<String> columnNames = new ArrayList<String>();
			
			
					
			Document doc = DocumentHelper.createDocument();
			Element root = doc.addElement(VanillaConstantsForFMDT.REQUEST_LOADBUSINESSPACKAGE);
			
			List<String> pkNames = new ArrayList<String>();
			while (pk.next()){
				pkNames.add(pk.getString("COLUMN_NAME")) ;
			}
			
			while (rs.next()){
				Element elemItem = root.addElement("item");
				elemItem.addElement("name").setText(rs.getString("COLUMN_NAME"));					
				elemItem.addElement("value").setText(rs.getString("TYPE_NAME"));	
				elemItem.addElement("field").setText(rs.getString("COLUMN_SIZE"));
				elemItem.addElement("isNullable").setText(rs.getString("IS_NULLABLE"));
				elemItem.addElement("isAutoInc").setText(rs.getString("IS_AUTOINCREMENT"));
				boolean isPK =false;
				for(String pkName : pkNames){
					if (pkName.equals(rs.getString("COLUMN_NAME")))
					{
						isPK=true;
						break;
					}
				}
				elemItem.addElement("isPK").setText(String.valueOf(isPK));
			}
			socket.disconnect();

			return doc.asXML();
		}
		catch(Exception e){
			e.printStackTrace();
		}	
		return "failed";
	}
	
	
	

	@Override
	public String loaderExcel(String name, String file, InputStream in, IRepositoryContext ctx, HashMap<String, String> documentParameters) throws Exception{
		try {
			String sheetName = documentParameters.get(VanillaConstantsForFMDT.PARAMETER_SHEETNAME); 	
			String contractName = documentParameters.get(VanillaConstantsForFMDT.PARAMETER_FILENAME)+"_"+documentParameters.get(VanillaConstantsForFMDT.PARAMETER_SHEETNAME)+"_"+documentParameters.get(VanillaConstantsForFMDT.PARAMETER_TABLENAME); 	
			IMdmProvider provider = resetMdmProvider(ctx.getVanillaContext());
			
			List<Supplier> suppliers = new ArrayList<Supplier>();
			
			Supplier supplier = getSupplier(provider, sheetName, contractName);	
			
			suppliers.add(supplier);
			
		/*	
			List<Integer> groupIds = resetMdmProvider(ctx.getVanillaContext()).getSupplierSecurity(supplier.getId());
			if(!groupIds.contains(ctx.getGroup().getId()))
				groupIds.add(ctx.getGroup().getId());
			*/
			suppliers= provider.saveSuppliers(suppliers);
			List<Integer> groupIds = resetMdmProvider(ctx.getVanillaContext()).getSupplierSecurity(supplier.getId());
			if(!groupIds.contains(ctx.getGroup().getId()))
				provider.addSecuredSupplier(supplier.getId(), ctx.getGroup().getId());
			
			Contract contract = null;
			for(Supplier supp : suppliers){
				for (Contract cont : supp.getContracts())
				{
					if (cont.getName().equals(contractName) ){
						linkDocument (file, name, cont, in, ctx);
						contract = cont;
						break;
					}					
				}
			}
	
			suppliers= provider.saveSuppliers(suppliers);
				
			FileInputXLS fileInput = createFileInputXLS(sheetName, contract.getDocId(), ctx, documentParameters.get(VanillaConstantsForFMDT.PARAMETER_SKIPFIRSTROW));
					
			MdmContractFileInput mdmContract = createMdmContract(contract, fileInput);
			
			DocumentGateway doc= createDocument(ctx.getVanillaContext().getLogin(), documentParameters.get(VanillaConstantsForFMDT.PARAMETER_TABLENAME), ctx);
			
			DataBaseOutputStream data = createDataBaseOutput(documentParameters, doc);
			
			List<Integer> colNumber = createListColumn(documentParameters.get(VanillaConstantsForFMDT.PARAMETER_COLNUMBER));
			mapping(data, mdmContract, colNumber);			
			
			doc.addServer(fileInput.getServer());
			
			//doc.setRepositoryContext(ctx);
			
			doc.addTransformation(mdmContract);
			doc.addTransformation(data);
			
			mdmContract.addOutput(data);
			data.addInput(mdmContract);
			
			RuntimeEngine engine = new RuntimeEngine();
			
			engine.init(ctx, doc, new PropertyChangeListener() {
				
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					// TODO Auto-generated method stub
					
				}
			}, new ByteArrayOutputStream());
			
			engine.run(0);
			while (engine.isRunning())
			{
				Thread.sleep(10);
			}		
			String error=null;
			List<RuntimeStep> listStep= engine.getAllRuns();
			for(RuntimeStep step : listStep){
				if(step.containsErrors()){
					for(TransformationLog log : step.getLogs()){
						if (log.priority == Level.ERROR_INT){
							error+= log.message+"\n";
						}
					}					
				}				
			}
			if (error!=null)
				return error;
			//			
			RemoteRepositoryApi api = new RemoteRepositoryApi(ctx);
			
			Repository repo = new Repository (new RemoteRepositoryApi(ctx), IRepositoryApi.FMDT_TYPE);
			Collection <RepositoryItem> metadatas= repo.getAllItems();
			
			String servername =documentParameters.get(VanillaConstantsForFMDT.PARAMETER_SERVERDATABASE);
			String databaseName = (servername!=null && !servername.isEmpty()) ? servername : initConnectionDatabase(documentParameters).getSocket(null).getCatalog();
			
			String metadataName="Excel Loader "+databaseName;
			
			MetaData metadata =null;
			RepositoryItem repoItem =null;
			for(RepositoryItem item : metadatas){
				if (item.getName().equals(metadataName)){
					
					String xmlMetadata= api.getRepositoryService().loadModel(item);
					MetaData current = new MetaDataDigester(IOUtils.toInputStream(xmlMetadata), new MetaDataBuilder(api)).getModel(api, "none") ;
					SQLDataSource dataSource = (SQLDataSource) current.getDataSource("UserDataSource");
					
					if(dataSource!=null && comparConnection((SQLConnection)dataSource.getConnection(), initSQLConnection(documentParameters))){				
						repoItem =item;
						metadata=current;
						break;
					}
				}
			}
			if (metadata==null){
				metadata=createMetadata(documentParameters, ctx.getGroup().getName());
			}
			
			String action = documentParameters.get(VanillaConstantsForFMDT.PARAMETER_ACTIONTYPE);
		//	if(action.equals("CREATE")|| action.equals("UPDATE") || action.equals("REPLACE"))
				addTable(metadata, documentParameters.get(VanillaConstantsForFMDT.PARAMETER_TABLENAME).toLowerCase(), ctx.getGroup().getName());
			
			String xmlMetadata= metadata.getXml(false);
			
			if(repoItem==null){
				VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
				String directory = config.getProperty(VanillaConfiguration.P_LOADER_EXCEL_DIRECTORY);				
				RepositoryDirectory dir = repo.getDirectory(Integer.parseInt(directory));
				api.getAdminService().addGroupForDirectory(ctx.getGroup().getId(), dir.getId());
				repoItem =	api.getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.FMDT_TYPE, -1, dir, metadataName, "", "", "", xmlMetadata, true);

			}	
			
			api.getRepositoryService().updateModel(repoItem, xmlMetadata);

			return "success";
			
		}
		catch(Exception e){
			e.printStackTrace();
			return e.getMessage();
		}	
		//return "failed";
	}
	
	
	
	private Supplier getSupplier (IMdmProvider provider, String sheetName, String contractName){
		Supplier supplier = null;
		try {
			
			String name= "Excel Supplier";		
		
			List<Supplier> existingSuppliers = provider.getSuppliers();
			
			for (Supplier supp : existingSuppliers) {
				if(supp.getName().equals(name))
				{
					supplier= supp;
					break;
				}
			}
						
		if (supplier ==null){
				supplier=new Supplier();
				supplier.setName(name);
				supplier.setExternalId(name+"Id");
				supplier.setExternalSource(name);
		}
				
		for(Contract cont : supplier.getContracts()){
			if(cont.getName().equals(contractName)){
				return supplier;
			}
		}
				
		Contract contract = new Contract();
		contract.setName(contractName);
		contract.setExternalId(contractName+"Id");
		contract.setExternalSource(contractName);
		
		contract.setParent(supplier);			
		supplier.addContract(contract);		
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return supplier;
	}
	
	
	
	private FileInputXLS createFileInputXLS(String sheetName, int docId, IRepositoryContext ctx, String firstRow){
		FileInputXLS fileInput = new FileInputXLS(); 
		fileInput.setDefinition(String.valueOf(docId));
		fileInput.setSheetName(sheetName);
		fileInput.setSkipFirstRow(firstRow);
		MdmFileServer srv = new MdmFileServer("mdmserver", "", ctx.getVanillaContext().getVanillaUrl(), ctx.getVanillaContext().getLogin(),ctx.getVanillaContext().getPassword(), ctx.getRepository().getId() + "");
		fileInput.setServer(srv);
		return fileInput;
	}
	
	
	private MdmContractFileInput createMdmContract (Contract contract, FileInputXLS fileInput){
		MdmContractFileInput mdmContract = new MdmContractFileInput();
		mdmContract.setContract(contract);
		mdmContract.setFileTransfo(fileInput);
		mdmContract.refreshDescriptor();
		
		return mdmContract;
	}
	
	 
	private DataBaseOutputStream createDataBaseOutput( HashMap<String, String> documentParameters, DocumentGateway doc){
		try{
			DataBaseOutputStream data = new DataBaseOutputStream();
			DataBaseServer server = new DataBaseServer();

			String tableName= documentParameters.get(VanillaConstantsForFMDT.PARAMETER_TABLENAME);
			String action= documentParameters.get(VanillaConstantsForFMDT.PARAMETER_ACTIONTYPE);
			
			DataBaseConnection databaseConnection =  initConnectionDatabase (documentParameters);	
			server.addConnection(databaseConnection);
			server.setCurrentConnection(databaseConnection);
			
			data.setServer(server);
			data.setDefinition("select * from "+tableName);
			data.setName(action + tableName);
			if (action.equals("REPLACE"))
				data.setTruncate(true);
			else
				data.setTruncate(false);
			
			doc.addServer(server);
			data.setDocumentGateway(doc);
		
			data.initDescriptor();
			
			return data;
		}
		catch(Exception e){
			e.printStackTrace();
		}	
		return null;
		
	}
	
	
	
protected DocumentGateway createDocument(String author, String name, IRepositoryContext ctx){
		
		DocumentGateway doc = new DocumentGateway();
		doc.setAuthor(author);
		doc.setCreationDate(Calendar.getInstance().getTime());
		doc.setName(name);
		
		doc.setRepositoryContext(ctx);		
		
		return doc;
	}

	
	
	private MetaData createMetadata(HashMap<String, String> documentParameters, String groupName){
		try{					
			//String tableName= documentParameters.get(VanillaConstantsForFMDT.PARAMETER_TABLENAME).toLowerCase();
			//DataBaseConnection databaseConnection =  initConnectionDatabase (documentParameters);
			
			IConnection connection = initSQLConnection(documentParameters);
			SQLDataSource dataSource = new SQLDataSource();
			dataSource.setName("UserDataSource");
			dataSource.addAlternateConnection(connection);
			dataSource.setConnection(0, connection);
							
			MetaData metadata = new MetaData();
			metadata.addDataSource(dataSource);	
			
			BusinessModel model = new BusinessModel();
			model.setName("UserModel");
			model.setModel(metadata);
			model.setGranted(groupName, true);
			
			BusinessPackage bpackage = new BusinessPackage();
			bpackage.setBusinessModel(model);
			bpackage.setName("UserPackage");
			bpackage.setGranted(groupName, true);
			
			
			model.addBusinessPackage(bpackage);
			metadata.addBusinessModel(model);	
						
			return metadata;
		}
		catch(Exception e){
			e.printStackTrace();
		}	
		return null;
	}
	
	
	
	private void addTable(MetaData metadata, String tableName, String groupName) throws Exception{
				
		SQLDataSource dataSource = (SQLDataSource) metadata.getDataSource("UserDataSource");
		
		SQLDataStream dataStreamTable =null;
		
		dataStreamTable = (SQLDataStream)dataSource.getDataStreamNamed(tableName);
		if (dataStreamTable==null){
			for(IDataStream d : dataSource.getDataStreams()){
				if (d.getName().contains(".")){
					String name=d.getName().substring(d.getName().lastIndexOf(".")+1, d.getName().length());
					if(name.equals(tableName))
						dataStreamTable=(SQLDataStream)d;
				}
			}
		}
		
		ITable table = dataSource.getConnection().getTableByName(tableName);
		
		if (dataStreamTable==null){
			dataStreamTable = new SQLDataStream();
			dataStreamTable.setName(tableName);
			dataSource.add(dataSource.getConnection().getTableByName(tableName));
			dataStreamTable.setOrigin(table);
			dataStreamTable.setDataSource(dataSource);	
		} else 
		{
			
			List<IDataStreamElement> cols = new ArrayList<IDataStreamElement>();
		
			for (IDataStreamElement col : dataStreamTable.getElements()){
				cols.add(col);
			}
					
			for (IDataStreamElement col : cols){
				dataStreamTable.removeDataStreamElement((IDataStreamElement)col);
			}
		}
		
		for (IColumn colSource : table.getColumns()){
			SQLDataStreamElement col = new SQLDataStreamElement();
			col.setName(colSource.getName());
			col.setOrigin(colSource);
			col.setOriginName(colSource.getName());
			col.setGranted(groupName, true);
			col.setVisible(groupName, true);
			col.setDataStream(dataStreamTable);
		//	col.isVisibleFor(groupName);		
//			if (dataStreamTable.getElementNamed(col.getName())!=null){
//				dataStreamTable.removeDataStreamElement(dataStreamTable.getElementNamed(col.getName()));
//			}
			dataStreamTable.addColumn(col);
		}
		
		SQLBusinessTable bTable = new SQLBusinessTable(); 
		bTable.setName(tableName);
		bTable.setGranted(groupName, true);
		
		for ( IDataStreamElement column : dataStreamTable.getElements()){
			bTable.addColumn(column);
		}
		
		BusinessModel model =null;
		for(IBusinessModel mod : metadata.getBusinessModels()){
			if (mod.getName().equals("UserModel")){
				model = (BusinessModel)mod;
				break;
			}
		}
		
		if (model!=null){
			BusinessPackage bpackage =null;
			for(IBusinessPackage pack : model.getBusinessPackages(groupName)){
				if (pack.getName().equals("UserPackage")){
					bpackage = (BusinessPackage) pack;
					break;
				}
			}
			//bTable.setBusinessModel(model);
			
			model.addBusinessTable(bTable);
			bpackage.addBusinessTable(bTable);
		}

	}
	
	
	
	private void mapping(DataBaseOutputStream data, MdmContractFileInput mdmContract, List<Integer> colNumber){
		try{
			//AbstractTransformation transfo = mdmContract.getFileTransfo();
			if(colNumber.size()>= data.getDescriptor(null).getStreamElements().size()){
				for(int i =0; i< data.getDescriptor(null).getStreamElements().size(); i++){
					String inputCol= mdmContract.getDescriptor(null).getStreamElements().get(colNumber.get(i)).originTransfo+"::"+ mdmContract.getDescriptor(null).getStreamElements().get(colNumber.get(i)).name ;
					String outputCol = data.getDescriptor(null).getStreamElements().get(i).name;
					data.createBufferMappingName(mdmContract.getName(), inputCol, outputCol); 
				}
			} 
			else{
				for(int i =0; i< data.getDescriptor(null).getStreamElements().size(); i++){
					String inputCol= mdmContract.getDescriptor(null).getStreamElements().get(i).originTransfo+"::"+ mdmContract.getDescriptor(null).getStreamElements().get(i).name ;
					String outputCol = data.getDescriptor(null).getStreamElements().get(i).name;
					data.createBufferMappingName(mdmContract.getName(), inputCol, outputCol); 
				}
			}			
		}
		catch(Exception e){
			e.printStackTrace();
		}			
	}
	

	
	private DataBaseConnection initConnectionDatabase(HashMap<String, String> documentParameters) throws Exception{
		try {
			String name = documentParameters.get(VanillaConstantsForFMDT.PARAMETER_SERVERNAME);
			String login = documentParameters.get(VanillaConstantsForFMDT.PARAMETER_SERVERUSER);
			String password= documentParameters.get(VanillaConstantsForFMDT.PARAMETER_SERVERPASSWORD);;
			String driver= documentParameters.get(VanillaConstantsForFMDT.PARAMETER_SERVERTYPE);
			String host = documentParameters.get(VanillaConstantsForFMDT.PARAMETER_SERVERHOST); 
			String port= documentParameters.get(VanillaConstantsForFMDT.PARAMETER_SERVERPORT); 
			String dataBase = documentParameters.get(VanillaConstantsForFMDT.PARAMETER_SERVERDATABASE); 
			String url = documentParameters.get(VanillaConstantsForFMDT.PARAMETER_SERVERURL); 
			String useCompleteUrl =documentParameters.get(VanillaConstantsForFMDT.PARAMETER_SERVERURLCOMPLETE);		
			
			DataBaseConnection socket = new DataBaseConnection();
			socket.setName(name);
			socket.setLogin(login);
			socket.setPassword(password);
			socket.setDriverName(driver);

			if (!useCompleteUrl.equals("True")){
				socket.setUseFullUrl(false);
				socket.setHost(host);
				socket.setPort(port);
				socket.setDataBaseName(dataBase);
			}
			else{
				socket.setFullUrl(url);
				socket.setUseFullUrl(true);
			}
			return socket;
		}
		catch(Exception e){
			e.printStackTrace();
		}	
		return null;
	}
	
	
	
	private void linkDocument (String file, String name, Contract contract, InputStream in, IRepositoryContext repoCtx){
		try{		
			IVanillaContext ctx = repoCtx.getVanillaContext();
			IGedComponent gedcomponent = getGedComponent(ctx);
			
			if(contract.getDocId()!=null)
				contract.setFileVersions( gedcomponent.getDocumentDefinitionById(contract.getDocId()));
			
			if(contract.getFileVersions() != null) {
				try {					
					DocumentVersion vers = gedcomponent.addVersionToDocument(contract.getFileVersions(),file.substring(file.lastIndexOf("."), file.length()), in);
					contract.getFileVersions().addDocumentVersion(vers);
				} catch (Exception e) {
					DocumentVersion vers =  gedcomponent.addVersionToDocument(contract.getFileVersions(),"", in);
					contract.getFileVersions().addDocumentVersion(vers);
					e.printStackTrace();
				}
			}
			else {
				GedDocument doc = new GedDocument();
				
				doc.setDirectoryId(0);
				doc.setName(name);
				doc.setCreationDate(new Date());
				int userId = getVanillaApi().getVanillaSecurityManager().getUserByLogin(getVanillaApi().getVanillaContext().getLogin()).getId();
				doc.setCreatedBy(userId);
				doc.setMdmAttached(true);
				
				String format = file.substring(file.lastIndexOf(".")+1, file.length());
				
				List<Integer> groupIds = resetMdmProvider(ctx).getSupplierSecurity(contract.getParent().getId());
				
				ComProperties com = new ComProperties();
				
				com.setSimpleProperty(RuntimeFields.TITLE.getName(), name);
				
				GedIndexRuntimeConfig config = new GedIndexRuntimeConfig(com, 
						userId, -1, groupIds, 1, format, null, -1);
				config.setMdmAttached(true);
				//repoCtx.getGroup().getId()
				int id = gedcomponent.index(config, in);
				IRepositoryApi sock = new RemoteRepositoryApi(repoCtx);
				
	//			int id = Activator.getDefault().getGedComponent().addGedDocument(doc, in, format);
				
				for(Integer groupId : groupIds) {
					
					gedcomponent.addAccess(id, groupId, sock.getContext().getRepository().getId());
				}
				
				doc = gedcomponent.getDocumentDefinitionById(id);
				
				contract.setFileVersions(doc);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			return;
	}	
}
	
	private Boolean comparConnection(SQLConnection connectionSource, SQLConnection connectionTest){
		try {
			if (connectionSource.getJdbcConnection().getUrl().equals(connectionTest.getJdbcConnection().getUrl()))
				return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	private SQLConnection initSQLConnection(HashMap<String, String> documentParameters) throws Exception{
		try {
			String name = documentParameters.get(VanillaConstantsForFMDT.PARAMETER_SERVERNAME);
			String login = documentParameters.get(VanillaConstantsForFMDT.PARAMETER_SERVERUSER);
			String password= documentParameters.get(VanillaConstantsForFMDT.PARAMETER_SERVERPASSWORD);;
			String driver= documentParameters.get(VanillaConstantsForFMDT.PARAMETER_SERVERTYPE);
			String host = documentParameters.get(VanillaConstantsForFMDT.PARAMETER_SERVERHOST); 
			String port= documentParameters.get(VanillaConstantsForFMDT.PARAMETER_SERVERPORT); 
			String dataBase = documentParameters.get(VanillaConstantsForFMDT.PARAMETER_SERVERDATABASE); 
			String url = documentParameters.get(VanillaConstantsForFMDT.PARAMETER_SERVERURL); 
			String useCompleteUrl =documentParameters.get(VanillaConstantsForFMDT.PARAMETER_SERVERURLCOMPLETE);		
			
			SQLConnection socket = new SQLConnection();
			socket.setName(name);
			socket.setUsername(login);
			socket.setPassword(password);
			socket.setDriverName(driver);

			if (!useCompleteUrl.equals("True")){
				socket.setUseFullUrl(false);
				socket.setHost(host);
				socket.setPortNumber(port);
				socket.setDataBaseName(dataBase);
			}
			else{
				socket.setFullUrl(url);
				socket.setUseFullUrl(true);
			}
			return socket;
		}
		catch(Exception e){
			e.printStackTrace();
		}	
		return null;
	}
		
	
	private static List<Integer> createListColumn(String colNums){
		 List<Integer> lisColNum = new ArrayList<Integer>();
		 if (colNums!=null){
			 String[] splitColNums= colNums.split(";");
			 for(String colNum : splitColNums){
				 lisColNum.add(Integer.parseInt(colNum)-1);
			 }
		 }
		 return lisColNum;
	}
	
	
	private static IVanillaAPI getVanillaApi() {
		
		IVanillaAPI	vanillaApi = new RemoteVanillaPlatform(
					ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL), 
					ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), 
					ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));		
		
		return vanillaApi;
	}
	
	
	
	private IMdmProvider resetMdmProvider(IVanillaContext ctx){
		try{			
			 IMdmProvider provider = new MdmRemote(ctx.getLogin(), ctx.getPassword(), ctx.getVanillaUrl(), null, null);
			 return provider;
		} catch (Exception e) {
			e.printStackTrace();
			return  null;			
			//MessageDialog.openError(getWorkbench().getActiveWorkbenchWindow().getShell(), "Loading Mdm Model", "Unable to load model : " + e.getMessage());
		}
	}
	
	
	
	private IGedComponent getGedComponent(IVanillaContext ctx) {
		
		IGedComponent gedComponent = new RemoteGedComponent(ctx);
		
		return gedComponent;
	}
	
	/*
	private String getType(int  type ){
		switch (type) {
		
		case java.sql.Types.ARRAY:
			return "ARRAY";		
		case java.sql.Types.BIGINT:
			return "BIGINT";
		case java.sql.Types.BINARY:
			return "BINARY";
		case java.sql.Types.BIT:
			return "BIT";
		case java.sql.Types.BLOB:
			return "BLOB";
		case java.sql.Types.BOOLEAN:
			return "BOOLEAN";
		case java.sql.Types.CHAR:
			return "CHAR";
		case java.sql.Types.DATALINK:
			return "DATALINK";
		case java.sql.Types.CLOB:
			return "TEXT";
		case java.sql.Types.DATE:
			return "DATE";
		case java.sql.Types.DECIMAL:
			return "DECIMAL";
		case java.sql.Types.DISTINCT:
			return "DISTINCT";
		case java.sql.Types.DOUBLE:
			return "DOUBLE";
		case java.sql.Types.FLOAT:
			return "FLOAT";
		case java.sql.Types.INTEGER:
			return "INTEGER";
		case java.sql.Types.LONGNVARCHAR:
			return "LONGTEXT";
		case java.sql.Types.ARRAY:
			return "ARRAY";

		default:
			break;
		}
	}
	
	/*
	private IMdmProvider getMdmProvider(){
		if(provider == null) {
			resetMdmProvider();
		}
		return provider;
	}
	
	
	

	/*
	@Override
	public void setCtx(IVanillaContext ctx) throws Exception {
		this.ctx = ctx;
	}
	
	@Override
	public void setRepositoryApi(IRepositoryContext ctx) throws Exception {
		this.sock = new RemoteRepositoryApi(ctx) ;
	}
*/
}
