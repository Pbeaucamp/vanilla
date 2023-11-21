package bpm.gateway.core;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.digester.Digester;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import bpm.gateway.core.forms.Form;
import bpm.gateway.core.internal.GraphNodeBuilder;
import bpm.gateway.core.internal.ModelGraphNode;
import bpm.gateway.core.server.d4c.D4CConnection;
import bpm.gateway.core.server.d4c.D4CServer;
import bpm.gateway.core.server.database.DBColumn;
import bpm.gateway.core.server.database.DBSchema;
import bpm.gateway.core.server.database.DBTable;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.server.database.dwhview.DwhDbConnection;
import bpm.gateway.core.server.database.freemetrics.FreemetricServer;
import bpm.gateway.core.server.database.nosql.cassandra.CassandraConnection;
import bpm.gateway.core.server.database.nosql.cassandra.CassandraServer;
import bpm.gateway.core.server.database.nosql.hbase.HBaseConnection;
import bpm.gateway.core.server.database.nosql.hbase.HBaseServer;
import bpm.gateway.core.server.database.nosql.mongodb.MongoDbConnection;
import bpm.gateway.core.server.database.nosql.mongodb.MongoDbServer;
import bpm.gateway.core.server.ldap.LdapConnection;
import bpm.gateway.core.server.ldap.LdapServer;
import bpm.gateway.core.server.userdefined.Parameter;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.core.transformations.AggregateTransformation;
import bpm.gateway.core.transformations.AlterationInfo;
import bpm.gateway.core.transformations.AlterationStream;
import bpm.gateway.core.transformations.CartesianProduct;
import bpm.gateway.core.transformations.ConsistencyMapping;
import bpm.gateway.core.transformations.ConsitencyTransformation;
import bpm.gateway.core.transformations.DeleteRows;
import bpm.gateway.core.transformations.EncryptTransformation;
import bpm.gateway.core.transformations.FieldSplitter;
import bpm.gateway.core.transformations.Filter;
import bpm.gateway.core.transformations.FilterDimension;
import bpm.gateway.core.transformations.Geoloc;
import bpm.gateway.core.transformations.GlobalDefinitionInput;
import bpm.gateway.core.transformations.Lookup;
import bpm.gateway.core.transformations.MergeStreams;
import bpm.gateway.core.transformations.Normalize;
import bpm.gateway.core.transformations.NullTransformation;
import bpm.gateway.core.transformations.RowsFieldSplitter;
import bpm.gateway.core.transformations.SelectDistinct;
import bpm.gateway.core.transformations.SelectionTransformation;
import bpm.gateway.core.transformations.Sequence;
import bpm.gateway.core.transformations.SimpleMappingTransformation;
import bpm.gateway.core.transformations.SortTransformation;
import bpm.gateway.core.transformations.SplitedField;
import bpm.gateway.core.transformations.SqlLookup;
import bpm.gateway.core.transformations.SqlScript;
import bpm.gateway.core.transformations.SubTransformation;
import bpm.gateway.core.transformations.SurrogateKey;
import bpm.gateway.core.transformations.TopXTransformation;
import bpm.gateway.core.transformations.UnduplicateRows;
import bpm.gateway.core.transformations.calcul.Calculation;
import bpm.gateway.core.transformations.calcul.GeoCondition;
import bpm.gateway.core.transformations.calcul.GeoDispatch;
import bpm.gateway.core.transformations.calcul.GeoFilter;
import bpm.gateway.core.transformations.calcul.GeoFilter.PointGeo;
import bpm.gateway.core.transformations.calcul.GeoFilter.Polygon;
import bpm.gateway.core.transformations.calcul.Range;
import bpm.gateway.core.transformations.calcul.RangingTransformation;
import bpm.gateway.core.transformations.calcul.Script;
import bpm.gateway.core.transformations.cleansing.ValidationCleansing;
import bpm.gateway.core.transformations.cleansing.ValidationCleansing.ValidationOutput;
import bpm.gateway.core.transformations.cleansing.ValidationCleansing.Validator;
import bpm.gateway.core.transformations.files.FileFolderReader;
import bpm.gateway.core.transformations.freemetrics.KPIList;
import bpm.gateway.core.transformations.freemetrics.KPIOutput;
import bpm.gateway.core.transformations.googleanalytics.VanillaAnalyticsGoogle;
import bpm.gateway.core.transformations.inputs.CassandraInputStream;
import bpm.gateway.core.transformations.inputs.D4CInput;
import bpm.gateway.core.transformations.inputs.DataBaseInputStream;
import bpm.gateway.core.transformations.inputs.DataPreparationInput;
import bpm.gateway.core.transformations.inputs.FMDTInput;
import bpm.gateway.core.transformations.inputs.FileInputCSV;
import bpm.gateway.core.transformations.inputs.FileInputPDFForm;
import bpm.gateway.core.transformations.inputs.FileInputShape;
import bpm.gateway.core.transformations.inputs.FileInputVCL;
import bpm.gateway.core.transformations.inputs.FileInputXLS;
import bpm.gateway.core.transformations.inputs.FileInputXML;
import bpm.gateway.core.transformations.inputs.HbaseInputStream;
import bpm.gateway.core.transformations.inputs.LdapInput;
import bpm.gateway.core.transformations.inputs.LdapMultipleMembers;
import bpm.gateway.core.transformations.inputs.MongoDbInputStream;
import bpm.gateway.core.transformations.inputs.NagiosDb;
import bpm.gateway.core.transformations.inputs.OdaInput;
import bpm.gateway.core.transformations.inputs.OdaInputWithParameters;
import bpm.gateway.core.transformations.inputs.OracleXmlView;
import bpm.gateway.core.transformations.kml.KMLInput;
import bpm.gateway.core.transformations.kml.KMLOutput;
import bpm.gateway.core.transformations.mdm.MdmContractFileInput;
import bpm.gateway.core.transformations.mdm.MdmInput;
import bpm.gateway.core.transformations.mdm.MdmOutput;
import bpm.gateway.core.transformations.normalisation.Denormalisation;
import bpm.gateway.core.transformations.nosql.SqoopTransformation;
import bpm.gateway.core.transformations.olap.DimensionFilter;
import bpm.gateway.core.transformations.olap.FilterClause;
import bpm.gateway.core.transformations.olap.OlapDimensionExtractor;
import bpm.gateway.core.transformations.olap.OlapFactExtractorTranformation;
import bpm.gateway.core.transformations.olap.OlapOutput;
import bpm.gateway.core.transformations.outputs.CassandraOutputStream;
import bpm.gateway.core.transformations.outputs.CielComptaOutput;
import bpm.gateway.core.transformations.outputs.DataBaseOutputStream;
import bpm.gateway.core.transformations.outputs.FMDTOutput;
import bpm.gateway.core.transformations.outputs.FileOutputCSV;
import bpm.gateway.core.transformations.outputs.FileOutputVCL;
import bpm.gateway.core.transformations.outputs.FileOutputWeka;
import bpm.gateway.core.transformations.outputs.FileOutputXLS;
import bpm.gateway.core.transformations.outputs.FileOutputXML;
import bpm.gateway.core.transformations.outputs.FreemetricsKPI;
import bpm.gateway.core.transformations.outputs.GeoJsonOutput;
import bpm.gateway.core.transformations.outputs.HBaseOutputStream;
import bpm.gateway.core.transformations.outputs.InfoBrightInjector;
import bpm.gateway.core.transformations.outputs.InsertOrUpdate;
import bpm.gateway.core.transformations.outputs.MongoDbOutputStream;
import bpm.gateway.core.transformations.outputs.MultiFolderXLS;
import bpm.gateway.core.transformations.outputs.SlowChangingDimension2;
import bpm.gateway.core.transformations.outputs.SubTransformationFinalStep;
import bpm.gateway.core.transformations.utils.Condition;
import bpm.gateway.core.transformations.utils.ConditionNull;
import bpm.gateway.core.transformations.utils.PreviousValueTransformation;
import bpm.gateway.core.transformations.vanilla.FreedashFormInput;
import bpm.gateway.core.transformations.vanilla.VanillaCommentExtraction;
import bpm.gateway.core.transformations.vanilla.VanillaCreateGroup;
import bpm.gateway.core.transformations.vanilla.VanillaCreateUser;
import bpm.gateway.core.transformations.vanilla.VanillaGroupUser;
import bpm.gateway.core.transformations.vanilla.VanillaLdapSynchro;
import bpm.gateway.core.transformations.vanilla.VanillaRoleGroupAssocaition;
import bpm.gateway.core.transformations.vanillamaps.VanillaMapAddressInput;
import bpm.gateway.core.transformations.vanillamaps.VanillaMapAddressOutput;
import bpm.gateway.core.transformations.webservice.WebServiceInput;
import bpm.gateway.core.transformations.webservice.WebServiceParameter;
import bpm.gateway.core.transformations.webservice.WebServiceVanillaInput;
import bpm.gateway.core.tsbn.ConnectorAffaireXML;
import bpm.gateway.core.tsbn.ConnectorAppelXML;
import bpm.gateway.core.tsbn.ConnectorReferentielXML;
import bpm.gateway.core.tsbn.rpu.RpuConnector;
import bpm.gateway.core.tsbn.syrius.SyriusConnector;
import bpm.gateway.core.veolia.ConnectorAbonneXML;
import bpm.gateway.core.veolia.ConnectorPatrimoineXML;
import bpm.vanilla.platform.core.IRepositoryContext;



/**
 * This class can read a xml doc and restore a GatewayDocument
 * @author LCA
 *
 */
public class GatewayDigester {
	private class MyErrorHandler implements ErrorHandler{

		public void error(SAXParseException arg0) throws SAXException {
			arg0.printStackTrace();
			throw new SAXException("erreur de parse", arg0);
		}

		public void fatalError(SAXParseException arg0) throws SAXException {
			arg0.printStackTrace();
			throw new SAXException("fatal error", arg0);
		}


		public void warning(SAXParseException arg0) throws SAXException {
			arg0.printStackTrace();
			throw new SAXException("warning", arg0);
		}
		
	}

	private Digester dig;
	private DocumentGateway doc ; 
	
	public GatewayDigester() {
		
	}
	
	
	/**
	 * set the digester to parse a file containing the XML definition
	 * @param file
	 * @throws IOException
	 * @throws SAXException
	 */
	public GatewayDigester(File file, List<AbrstractDigesterTransformation> additionalParsers) throws IOException, SAXException{
		dig = new Digester();
		
		
		dig.setErrorHandler(new MyErrorHandler());
		createCallbacks("gatewayDocument");
		for(AbrstractDigesterTransformation d : additionalParsers){
			d.createCallbacks(dig, "gatewayDocument");
		}
		
//		dig.setUseContextClassLoader(true);
		doc = (DocumentGateway)dig.parse(file);
		
		
		
	}
	
	
	public void clean(IRepositoryContext repContext, DocumentGateway previousDocument){
		if(previousDocument != null) {
			doc = previousDocument;
		}
		doc.setRepositoryContext(repContext);
		ModelGraphNode graph = new GraphNodeBuilder().createTree(doc);
		
		Activator.getLogger().debug("Model graph \n" + graph.dump(""));;
		try{
			graph.initNumberOfRefresh();
			graph.initTransfo();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		
		
		for(Transformation t : doc.getTransformations()){
			
			AbstractTransformation tr = (AbstractTransformation)t;
			
			int i =0;
			tr.setInited();
			if (t instanceof SimpleMappingTransformation){
				((SimpleMappingTransformation)t).setInited();
			}
			
			try{
				for(StreamElement f : tr.getDescriptor(null).getStreamElements()){
					if (tr.getLoadedFields() != null){
						if (i < tr.getLoadedFields().size()){
							String s = tr.getLoadedFields().get(i).name;
							boolean exists = false;
							for(StreamElement _f : tr.getDescriptor(null).getStreamElements()){
								if (_f.name.equals(s)){
									exists = true;
									break;
								}
							}
							if (!exists){
								f.name = s;
							}
							
							
						}
					}
					
					i++;
				}
			}catch(Exception e){
				e.printStackTrace();
				
			}
			
			
		}
		
	}
	
	/**
	 * 
	 * @return the model onc ethe digester has parsed it
	 */
	public DocumentGateway getDocument(IRepositoryContext repContext){
		clean(repContext, null);
		doc.setRepositoryContext(repContext);
		return doc;
	}
	
	public DocumentGateway getDocumentWithoutClean(IRepositoryContext repContext){
		doc.setRepositoryContext(repContext);
		return doc;
	}
	
	
	/**
	 * set the digester to parse from an InputStream containing the XML definition
	 * @param inputStream
	 * @throws IOException
	 * @throws SAXException
	 */
	public GatewayDigester(InputStream inputStream, List<AbrstractDigesterTransformation> additionalParsers) throws IOException, SAXException{
		dig = new Digester();
		dig.setErrorHandler(new MyErrorHandler());
		createCallbacks("gatewayDocument");
		
		for(AbrstractDigesterTransformation d : additionalParsers){
			d.createCallbacks(dig, "gatewayDocument");
		}
		
		doc = (DocumentGateway)dig.parse(inputStream);
		
	}

	private void createCallbacks(String root){
		dig.setValidating(false);
		

		
		dig.addObjectCreate(root, DocumentGateway.class);
		dig.addCallMethod(root + "/name", "setName", 0);
		dig.addCallMethod(root + "/description", "setDescription", 0);
		dig.addCallMethod(root + "/author", "setAuthor", 0);
		dig.addCallMethod(root + "/creationDate", "setCreationDate", 0);
		dig.addCallMethod(root + "/mode", "setMode", 0);
		dig.addCallMethod(root + "/projectName", "setProjectName", 0);
		dig.addCallMethod(root + "/projectVersion", "setProjectVersion", 0);
		dig.addCallMethod(root + "/lastModificationDate", "setLastModificationDate", 0);
		dig.addCallMethod(root + "/id", "setId", 0);
		
		/*
		 * Variable
		 */
		dig.addObjectCreate(root + "/localVariables/variable", Variable.class);
			dig.addCallMethod(root + "/localVariables/variable/name", "setName", 0);
			dig.addCallMethod(root + "/localVariables/variable/value", "setValue", 0);
			dig.addCallMethod(root + "/localVariables/variable/scope", "setScope", 0);
			dig.addCallMethod(root + "/localVariables/variable/dataType", "setType", 0);
		dig.addSetNext(root + "/localVariables/variable", "addVariable");
		
		dig.addObjectCreate(root + "/annote", Comment.class);
			dig.addCallMethod(root + "/annote/content", "setContent", 0);
			dig.addCallMethod(root + "/annote/positionX", "setX", 0);
			dig.addCallMethod(root + "/annote/positionY", "setY", 0);
			dig.addCallMethod(root + "/annote/width", "setWidth", 0);
			dig.addCallMethod(root + "/annote/height", "setHeight", 0);
			dig.addCallMethod(root + "/annote/typeNote", "setTypeNote", 0);
		dig.addSetNext(root + "/annote", "addAnnote");
		
		
		dig.addObjectCreate(root + "/parameters/parameter", Parameter.class);
			dig.addCallMethod(root + "/parameters/parameter/name", "setName", 0);
			dig.addCallMethod(root + "/parameters/parameter/type", "setType", 0);
			dig.addCallMethod(root + "/parameters/parameter/defaultValue", "setDefaultValue", 0);
		dig.addSetNext(root + "/parameters/parameter", "addParameter");
		
		/*
		 * LDAP server and connections
		 */
		dig.addObjectCreate(root + "/servers/ldapServer", LdapServer.class);
		dig.addCallMethod(root + "/servers/ldapServer/name", "setName", 0);
		dig.addCallMethod(root + "/servers/ldapServer/description", "setDescription", 0);
			dig.addObjectCreate(root + "/servers/ldapServer/ldapConnection", LdapConnection.class);
			dig.addCallMethod(root + "/servers/ldapServer/ldapConnection/name", "setName", 0);
			dig.addCallMethod(root + "/servers/ldapServer/ldapConnection/description", "setDescription", 0);
			dig.addCallMethod(root + "/servers/ldapServer/ldapConnection/host", "setHost", 0);
			dig.addCallMethod(root + "/servers/ldapServer/ldapConnection/port", "setPort", 0);
			dig.addCallMethod(root + "/servers/ldapServer/ldapConnection/base", "setBase", 0);
			dig.addCallMethod(root + "/servers/ldapServer/ldapConnection/userDn", "setUserDn", 0);
			dig.addCallMethod(root + "/servers/ldapServer/ldapConnection/password", "setPassword", 0);
			dig.addSetNext(root + "/servers/ldapServer/ldapConnection", "addConnection");
		dig.addSetNext(root + "/servers/ldapServer", "addServer");
		
		
//		/*
//		 * vanillaServer
//		 */
//		dig.addObjectCreate(root + "/servers/vanillaServer", VanillaServer.class);
//		dig.addCallMethod(root + "/servers/vanillaServer/name", "setName", 0);
//		dig.addCallMethod(root + "/servers/vanillaServer/description", "setDescription", 0);
//		dig.addCallMethod(root + "/servers/vanillaServer/vanillaConnection/login", "setLogin", 0);
//		dig.addCallMethod(root + "/servers/vanillaServer/vanillaConnection/password", "setPassword", 0);
//		dig.addCallMethod(root + "/servers/vanillaServer/vanillaConnection/url", "setUrl", 0);
//		dig.addSetNext(root + "/servers/vanillaServer", "addServer");

		
		
		/*
		 * DataBase server and connections
		 */
		dig.addObjectCreate(root + "/servers/dataBaseServer", DataBaseServer.class);
		dig.addCallMethod(root + "/servers/dataBaseServer/name", "setName", 0);
		dig.addCallMethod(root + "/servers/dataBaseServer/description", "setDescription", 0);
			dig.addObjectCreate(root + "/servers/dataBaseServer/dataBaseConnection", DataBaseConnection.class);
			dig.addCallMethod(root + "/servers/dataBaseServer/dataBaseConnection/name", "setName", 0);
			dig.addCallMethod(root + "/servers/dataBaseServer/dataBaseConnection/description", "setDescription", 0);
			dig.addCallMethod(root + "/servers/dataBaseServer/dataBaseConnection/host", "setHost", 0);
			dig.addCallMethod(root + "/servers/dataBaseServer/dataBaseConnection/port", "setPort", 0);
			dig.addCallMethod(root + "/servers/dataBaseServer/dataBaseConnection/dataBaseName", "setDataBaseName", 0);
			dig.addCallMethod(root + "/servers/dataBaseServer/dataBaseConnection/login", "setLogin", 0);
			dig.addCallMethod(root + "/servers/dataBaseServer/dataBaseConnection/password", "setPassword", 0);
			dig.addCallMethod(root + "/servers/dataBaseServer/dataBaseConnection/driverName", "setDriverName", 0);
			
			dig.addCallMethod(root + "/servers/dataBaseServer/dataBaseConnection/useFullUrl", "setUseFullUrl", 0);
			dig.addCallMethod(root + "/servers/dataBaseServer/dataBaseConnection/fullUrl", "setFullUrl", 0);
			
			dig.addSetNext(root + "/servers/dataBaseServer/dataBaseConnection", "addConnection");
			
			
			
			dig.addObjectCreate(root + "/servers/dataBaseServer/dwhDatabaseConnection", DwhDbConnection.class);
			dig.addCallMethod(root + "/servers/dataBaseServer/dwhDatabaseConnection/name", "setName", 0);
			dig.addCallMethod(root + "/servers/dataBaseServer/dwhDatabaseConnection/description", "setDescription", 0);
			dig.addCallMethod(root + "/servers/dataBaseServer/dwhDatabaseConnection/host", "setHost", 0);
			dig.addCallMethod(root + "/servers/dataBaseServer/dwhDatabaseConnection/port", "setPort", 0);
			dig.addCallMethod(root + "/servers/dataBaseServer/dwhDatabaseConnection/dataBaseName", "setDataBaseName", 0);
			dig.addCallMethod(root + "/servers/dataBaseServer/dwhDatabaseConnection/login", "setLogin", 0);
			dig.addCallMethod(root + "/servers/dataBaseServer/dwhDatabaseConnection/password", "setPassword", 0);
			dig.addCallMethod(root + "/servers/dataBaseServer/dwhDatabaseConnection/driverName", "setDriverName", 0);
			
			dig.addCallMethod(root + "/servers/dataBaseServer/dwhDatabaseConnection/useFullUrl", "setUseFullUrl", 0);
			dig.addCallMethod(root + "/servers/dataBaseServer/dwhDatabaseConnection/fullUrl", "setFullUrl", 0);
			
			dig.addObjectCreate(root + "/servers/dataBaseServer/dwhDatabaseConnection/schema", DBSchema.class);
			dig.addCallMethod(root + "/servers/dataBaseServer/dwhDatabaseConnection/schema/name", "setName", 0);
			dig.addCallMethod(root + "/servers/dataBaseServer/dwhDatabaseConnection/schema/noSchema", "setNoSchema", 0);
			
				dig.addObjectCreate(root + "/servers/dataBaseServer/dwhDatabaseConnection/schema/table", DBTable.class);
					dig.addCallMethod(root + "/servers/dataBaseServer/dwhDatabaseConnection/schema/table/name", "setName", 0);
					
					dig.addObjectCreate(root + "/servers/dataBaseServer/dwhDatabaseConnection/schema/table/column", DBColumn.class);
					dig.addCallMethod(root + "/servers/dataBaseServer/dwhDatabaseConnection/schema/table/column/name", "setName", 0);
					dig.addCallMethod(root + "/servers/dataBaseServer/dwhDatabaseConnection/schema/table/column/primaryKey", "setPrimaryKey", 0);
					dig.addSetNext(root + "/servers/dataBaseServer/dwhDatabaseConnection/schema/table/column", "addColumn");
				
				dig.addSetNext(root + "/servers/dataBaseServer/dwhDatabaseConnection/schema/table", "addDBTable");
			
			dig.addSetNext(root + "/servers/dataBaseServer/dwhDatabaseConnection/schema", "setSchema");
			
			
			
			dig.addSetNext(root + "/servers/dataBaseServer/dwhDatabaseConnection", "addConnection");
			
			
			
		dig.addSetNext(root + "/servers/dataBaseServer", "addServer");
		
		
		/*
		 * Cassandra server and connections
		 */
		dig.addObjectCreate(root + "/servers/cassandraServer", CassandraServer.class);
		dig.addCallMethod(root + "/servers/cassandraServer/name", "setName", 0);
		dig.addCallMethod(root + "/servers/cassandraServer/description", "setDescription", 0);
			dig.addObjectCreate(root + "/servers/cassandraServer/cassandraConnection", CassandraConnection.class);
			dig.addCallMethod(root + "/servers/cassandraServer/cassandraConnection/name", "setName", 0);
			dig.addCallMethod(root + "/servers/cassandraServer/cassandraConnection/host", "setHost", 0);
			dig.addCallMethod(root + "/servers/cassandraServer/cassandraConnection/port", "setPort", 0);
			dig.addCallMethod(root + "/servers/cassandraServer/cassandraConnection/keyspace", "setKeyspace", 0);
			dig.addCallMethod(root + "/servers/cassandraServer/cassandraConnection/login", "setUsername", 0);
			dig.addCallMethod(root + "/servers/cassandraServer/cassandraConnection/password", "setPassword", 0);
			dig.addSetNext(root + "/servers/cassandraServer/cassandraConnection", "setCurrentConnection");
		dig.addSetNext(root + "/servers/cassandraServer", "addServer");
		
		/*
		 * HBase server and connections
		 */
		dig.addObjectCreate(root + "/servers/hbaseServer", HBaseServer.class);
		dig.addCallMethod(root + "/servers/hbaseServer/name", "setName", 0);
		dig.addCallMethod(root + "/servers/hbaseServer/description", "setDescription", 0);
			dig.addObjectCreate(root + "/servers/hbaseServer/hbaseConnection", HBaseConnection.class);
			dig.addCallMethod(root + "/servers/hbaseServer/hbaseConnection/name", "setName", 0);
			dig.addCallMethod(root + "/servers/hbaseServer/hbaseConnection/configurationFile", "setConfigurationFileUrl", 0);
//			dig.addCallMethod(root + "/servers/hbaseServer/hbaseConnection/login", "setLogin", 0);
//			dig.addCallMethod(root + "/servers/hbaseServer/hbaseConnection/password", "setPassword", 0);
			dig.addSetNext(root + "/servers/hbaseServer/hbaseConnection", "setCurrentConnection");
		dig.addSetNext(root + "/servers/hbaseServer", "addServer");
		
		/*
		 * MongoDb server and connections
		 */
		dig.addObjectCreate(root + "/servers/mongoDbServer", MongoDbServer.class);
		dig.addCallMethod(root + "/servers/mongoDbServer/name", "setName", 0);
		dig.addCallMethod(root + "/servers/mongoDbServer/description", "setDescription", 0);
			dig.addObjectCreate(root + "/servers/mongoDbServer/mongoDbConnection", MongoDbConnection.class);
			dig.addCallMethod(root + "/servers/mongoDbServer/mongoDbConnection/name", "setName", 0);
			dig.addCallMethod(root + "/servers/mongoDbServer/mongoDbConnection/host", "setHost", 0);
			dig.addCallMethod(root + "/servers/mongoDbServer/mongoDbConnection/port", "setPort", 0);
			dig.addCallMethod(root + "/servers/mongoDbServer/mongoDbConnection/login", "setLogin", 0);
			dig.addCallMethod(root + "/servers/mongoDbServer/mongoDbConnection/password", "setPassword", 0);
			dig.addSetNext(root + "/servers/mongoDbServer/mongoDbConnection", "setCurrentConnection");
		dig.addSetNext(root + "/servers/mongoDbServer", "addServer");
		
		/*
		 * DataBase server and connections
		 */
		dig.addObjectCreate(root + "/servers/freemetricsServer", FreemetricServer.class);
		dig.addCallMethod(root + "/servers/freemetricsServer/name", "setName", 0);
		dig.addCallMethod(root + "/servers/freemetricsServer/description", "setDescription", 0);
		dig.addCallMethod(root + "/servers/freemetricsServer/freemtricsLogin", "setFmLogin", 0);
		dig.addCallMethod(root + "/servers/freemetricsServer/freemtricsPassword", "setFmPassword", 0);
		
		
		
			dig.addObjectCreate(root + "/servers/freemetricsServer/dataBaseConnection", DataBaseConnection.class);
			dig.addCallMethod(root + "/servers/freemetricsServer/dataBaseConnection/name", "setName", 0);
			dig.addCallMethod(root + "/servers/freemetricsServer/dataBaseConnection/description", "setDescription", 0);
			dig.addCallMethod(root + "/servers/freemetricsServer/dataBaseConnection/host", "setHost", 0);
			dig.addCallMethod(root + "/servers/freemetricsServer/dataBaseConnection/port", "setPort", 0);
			dig.addCallMethod(root + "/servers/freemetricsServer/dataBaseConnection/dataBaseName", "setDataBaseName", 0);
			dig.addCallMethod(root + "/servers/freemetricsServer/dataBaseConnection/login", "setLogin", 0);
			dig.addCallMethod(root + "/servers/freemetricsServer/dataBaseConnection/password", "setPassword", 0);
			dig.addCallMethod(root + "/servers/freemetricsServer/dataBaseConnection/driverName", "setDriverName", 0);
			dig.addSetNext(root + "/servers/freemetricsServer/dataBaseConnection", "addConnection");
		dig.addSetNext(root + "/servers/freemetricsServer", "addServer");
		
		/*
		 * D4C server and connections
		 */
		dig.addObjectCreate(root + "/servers/d4cServer", D4CServer.class);
		dig.addCallMethod(root + "/servers/d4cServer/name", "setName", 0);
		dig.addCallMethod(root + "/servers/d4cServer/description", "setDescription", 0);
			dig.addObjectCreate(root + "/servers/d4cServer/d4cConnection", D4CConnection.class);
			dig.addCallMethod(root + "/servers/d4cServer/d4cConnection/name", "setName", 0);
			dig.addCallMethod(root + "/servers/d4cServer/d4cConnection/url", "setUrl", 0);
			dig.addCallMethod(root + "/servers/d4cServer/d4cConnection/org", "setOrg", 0);
			dig.addCallMethod(root + "/servers/d4cServer/d4cConnection/login", "setLogin", 0);
			dig.addCallMethod(root + "/servers/d4cServer/d4cConnection/password", "setPassword", 0);
			dig.addSetNext(root + "/servers/d4cServer/d4cConnection", "setCurrentConnection");
		dig.addSetNext(root + "/servers/d4cServer", "addServer");
		
		/*
		 * forms
		 */
		dig.addObjectCreate(root + "/forms/form", Form.class);
		dig.addCallMethod(root + "/forms/form/name", "setName", 0);
		dig.addCallMethod(root + "/forms/form/directoryItem", "setDirectoryItemId", 0);
		
		dig.addCallMethod(root + "/forms/form/map", "map", 2);
		dig.addCallParam(root + "/forms/form/map/parameterName", 0);
		dig.addCallParam(root + "/forms/form/map/parameterId", 1);
		
		dig.addSetNext(root + "/forms/form", "addForm");
		
		
		/*
		 * MdmInput
		 */
		dig.addObjectCreate(root + "/mdmInput", MdmInput.class);
		dig.addCallMethod(root + "/mdmInput/name", "setName", 0);
		dig.addCallMethod(root + "/mdmInput/description", "setDescription", 0);
		dig.addCallMethod(root + "/mdmInput/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/mdmInput/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/mdmInput/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/mdmInput/temporaryFileName", "setTemporaryFilename", 0);
		dig.addCallMethod(root + "/mdmInput/entityName", "setEntityName", 0);
		dig.addCallMethod(root + "/mdmInput/entityUuid", "setEntityUuid", 0);

		dig.addSetNext(root + "/mdmInput", "addTransformation");
		
		/*
		 * Mdm file input
		 */
		dig.addObjectCreate(root + "/mdmfileinput", MdmContractFileInput.class);
		dig.addCallMethod(root + "/mdmfileinput/name", "setName", 0);
		dig.addCallMethod(root + "/mdmfileinput/description", "setDescription", 0);
		dig.addCallMethod(root + "/mdmfileinput/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/mdmfileinput/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/mdmfileinput/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/mdmfileinput/temporaryFileName", "setTemporaryFilename", 0);
		dig.addCallMethod(root + "/mdmfileinput/supplierId", "setSupplierId", 0);
		dig.addCallMethod(root + "/mdmfileinput/contractId", "setContractId", 0);
		
		dig.addCallMethod(root + "/mdmfileinput/metadata", "addMeta", 3);
		dig.addCallParam(root + "/mdmfileinput/metadata/metaKey", 0);
		dig.addCallParam(root + "/mdmfileinput/metadata/metaLabel", 1);
		dig.addCallParam(root + "/mdmfileinput/metadata/metaType", 2);

		dig.addSetNext(root + "/mdmfileinput", "addTransformation");
		
		dig.addObjectCreate(root + "/mdmfileinput/fileInputCSV", FileInputCSV.class);
		dig.addCallMethod(root + "/mdmfileinput/fileInputCSV/name", "setName", 0);
		dig.addCallMethod(root + "/mdmfileinput/fileInputCSV/skipFirstRow", "setSkipFirstRow", 0);
		dig.addCallMethod(root + "/mdmfileinput/fileInputCSV/description", "setDescription", 0);
		dig.addCallMethod(root + "/mdmfileinput/fileInputCSV/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/mdmfileinput/fileInputCSV/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/mdmfileinput/fileInputCSV/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/mdmfileinput/fileInputCSV/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/mdmfileinput/fileInputCSV/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/mdmfileinput/fileInputCSV/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/mdmfileinput/fileInputCSV/separator", "setSeparator", 0);
		dig.addCallMethod(root + "/mdmfileinput/fileInputCSV/trashOuput", "setTrashTransformation", 0);
		dig.addCallMethod(root + "/mdmfileinput/fileInputCSV/encoding", "setEncoding", 0);
		dig.addCallMethod(root + "/mdmfileinput/fileInputCSV/isJson", "setIsJson", 0);
		dig.addCallMethod(root + "/mdmfileinput/fileInputCSV/jsonRootItem", "setJsonRootItem", 0);
		dig.addCallMethod(root + "/mdmfileinput/fileInputCSV/jsonDepth", "setJsonDepth", 0);
		dig.addSetNext(root + "/mdmfileinput/fileInputCSV", "setFileTransfo");
		
		dig.addObjectCreate(root + "/mdmfileinput/fileInputXLS", FileInputXLS.class);
		dig.addCallMethod(root + "/mdmfileinput/fileInputXLS/name", "setName", 0);
		dig.addCallMethod(root + "/mdmfileinput/fileInputXLS/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/mdmfileinput/fileInputXLS/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/mdmfileinput/fileInputXLS/sheetName", "setSheetName", 0);
		dig.addCallMethod(root + "/mdmfileinput/fileInputXLS/description", "setDescription", 0);
		dig.addCallMethod(root + "/mdmfileinput/fileInputXLS/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/mdmfileinput/fileInputXLS/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/mdmfileinput/fileInputXLS/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/mdmfileinput/fileInputXLS/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/mdmfileinput/fileInputXLS/skipFirstRow", "setSkipFirstRow", 0);
		dig.addCallMethod(root + "/mdmfileinput/fileInputXLS/skipLines", "setSkipLines", 0);
		dig.addSetNext(root + "/mdmfileinput/fileInputXLS", "setFileTransfo");
		
		/*
		 * MdmOutput
		 */
		dig.addObjectCreate(root + "/mdmOutput", MdmOutput.class);
		dig.addCallMethod(root + "/mdmOutput/name", "setName", 0);
		dig.addCallMethod(root + "/mdmOutput/description", "setDescription", 0);
		dig.addCallMethod(root + "/mdmOutput/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/mdmOutput/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/mdmOutput/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/mdmOutput/temporaryFileName", "setTemporaryFilename", 0);
		dig.addCallMethod(root + "/mdmOutput/entityName", "setEntityName", 0);
		dig.addCallMethod(root + "/mdmOutput/entityUuid", "setEntityUuid", 0);
		dig.addCallMethod(root + "/mdmOutput/trashOutput", "setTrashTransformation", 0);
		dig.addCallMethod(root + "/mdmOutput/updateExisting", "setUpdateExisting", 0);

		dig.addCallMethod(root + "/mdmOutput/inputMapping", "createBufferMapping", 3);
		dig.addCallParam(root + "/mdmOutput/inputMapping/transformationRef", 0);
		dig.addCallParam(root + "/mdmOutput/inputMapping/inputIndex", 1);
		dig.addCallParam(root + "/mdmOutput/inputMapping/outputIndex", 2);

		dig.addCallMethod(root + "/mdmOutput/inputMappingName", "createBufferMappingName", 3);
		dig.addCallParam(root + "/mdmOutput/inputMappingName/transformationRef", 0);
		dig.addCallParam(root + "/mdmOutput/inputMappingName/inputName", 1);
		dig.addCallParam(root + "/mdmOutput/inputMappingName/outputName", 2);
		
		dig.addSetNext(root + "/mdmOutput", "addTransformation");
		

		/*
		 * D4C input
		 */
		dig.addObjectCreate(root + "/d4cinput", D4CInput.class);
		dig.addCallMethod(root + "/d4cinput/name", "setName", 0);
		dig.addCallMethod(root + "/d4cinput/description", "setDescription", 0);
		dig.addCallMethod(root + "/d4cinput/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/d4cinput/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/d4cinput/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/d4cinput/temporaryFileName", "setTemporaryFilename", 0);
		dig.addCallMethod(root + "/d4cinput/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/d4cinput/packageName", "setPackageName", 0);
		dig.addCallMethod(root + "/d4cinput/resourceId", "setResourceId", 0);

		dig.addSetNext(root + "/d4cinput", "addTransformation");
		
		dig.addObjectCreate(root + "/d4cinput/fileInputCSV", FileInputCSV.class);
		dig.addCallMethod(root + "/d4cinput/fileInputCSV/name", "setName", 0);
		dig.addCallMethod(root + "/d4cinput/fileInputCSV/skipFirstRow", "setSkipFirstRow", 0);
		dig.addCallMethod(root + "/d4cinput/fileInputCSV/description", "setDescription", 0);
		dig.addCallMethod(root + "/d4cinput/fileInputCSV/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/d4cinput/fileInputCSV/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/d4cinput/fileInputCSV/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/d4cinput/fileInputCSV/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/d4cinput/fileInputCSV/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/d4cinput/fileInputCSV/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/d4cinput/fileInputCSV/separator", "setSeparator", 0);
		dig.addCallMethod(root + "/d4cinput/fileInputCSV/trashOuput", "setTrashTransformation", 0);
		dig.addCallMethod(root + "/d4cinput/fileInputCSV/encoding", "setEncoding", 0);
		dig.addSetNext(root + "/d4cinput/fileInputCSV", "setFileTransfo");
		
		dig.addObjectCreate(root + "/d4cinput/fileInputXLS", FileInputXLS.class);
		dig.addCallMethod(root + "/d4cinput/fileInputXLS/name", "setName", 0);
		dig.addCallMethod(root + "/d4cinput/fileInputXLS/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/d4cinput/fileInputXLS/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/d4cinput/fileInputXLS/sheetName", "setSheetName", 0);
		dig.addCallMethod(root + "/d4cinput/fileInputXLS/description", "setDescription", 0);
		dig.addCallMethod(root + "/d4cinput/fileInputXLS/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/d4cinput/fileInputXLS/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/d4cinput/fileInputXLS/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/d4cinput/fileInputXLS/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/d4cinput/fileInputXLS/skipFirstRow", "setSkipFirstRow", 0);
		dig.addSetNext(root + "/d4cinput/fileInputXLS", "setFileTransfo");
		
		/*
		 * VanillaMapAddressInput
		 */
		dig.addObjectCreate(root + "/vanillaMapAddressInput", VanillaMapAddressInput.class);
		dig.addCallMethod(root + "/vanillaMapAddressInput/name", "setName", 0);
		dig.addCallMethod(root + "/vanillaMapAddressInput/description", "setDescription", 0);
		dig.addCallMethod(root + "/vanillaMapAddressInput/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/vanillaMapAddressInput/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/vanillaMapAddressInput/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/vanillaMapAddressInput/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/vanillaMapAddressInput/temporaryFileName", "setTemporaryFilename", 0);
		dig.addSetNext(root + "/vanillaMapAddressInput", "addTransformation");
		
		
		
		/*
		 * vanillaMapAddressOutput
		 */
		dig.addObjectCreate(root + "/vanillaMapAddressOutput", VanillaMapAddressOutput.class);
		dig.addCallMethod(root + "/vanillaMapAddressOutput/name", "setName", 0);
		dig.addCallMethod(root + "/vanillaMapAddressOutput/description", "setDescription", 0);
		dig.addCallMethod(root + "/vanillaMapAddressOutput/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/vanillaMapAddressOutput/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/vanillaMapAddressOutput/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/vanillaMapAddressOutput/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/vanillaMapAddressOutput/temporaryFileName", "setTemporaryFilename", 0);
		
		dig.addCallMethod(root + "/vanillaMapAddressOutput/arrondissementIndex", "setInputArrondissementIndex", 0);
		dig.addCallMethod(root + "/vanillaMapAddressOutput/blocIndex", "setInputBlocIndex", 0);
		dig.addCallMethod(root + "/vanillaMapAddressOutput/cityIndex", "setInputCityIndex", 0);
		dig.addCallMethod(root + "/vanillaMapAddressOutput/countryIndex", "setInputCountryIndex", 0);
		dig.addCallMethod(root + "/vanillaMapAddressOutput/idIndex", "setInputIdIndex", 0);
		dig.addCallMethod(root + "/vanillaMapAddressOutput/labelIndex", "setInputLabelIndex", 0);
		dig.addCallMethod(root + "/vanillaMapAddressOutput/inseeCodeIndex", "setInputInseeCodeIndex", 0);
		dig.addCallMethod(root + "/vanillaMapAddressOutput/street1Index", "setInputStreet1Index", 0);
		dig.addCallMethod(root + "/vanillaMapAddressOutput/street2Index", "setInputStreet2Index", 0);
		dig.addCallMethod(root + "/vanillaMapAddressOutput/zipCodeIndex", "setInputZipcodeIndex", 0);
		dig.addSetNext(root + "/vanillaMapAddressOutput", "addTransformation");

		
		
		
		/*
		 * vanilla's LDAP Synchro
		 */
		dig.addObjectCreate(root + "/vanillaLdapSynchro", VanillaLdapSynchro.class);
		dig.addCallMethod(root + "/vanillaLdapSynchro/name", "setName", 0);
		dig.addCallMethod(root + "/vanillaLdapSynchro/description", "setDescription", 0);
		dig.addCallMethod(root + "/vanillaLdapSynchro/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/vanillaLdapSynchro/positionY", "setPositionY", 0);
		
		dig.addCallMethod(root + "/vanillaLdapSynchro/ldapServerRef", "setLdapServer", 0);

		dig.addCallMethod(root + "/vanillaLdapSynchro/ldapGroupDn", "setLdapGroupDn", 0);
		dig.addCallMethod(root + "/vanillaLdapSynchro/ldapGroupNodeName", "setLdapGroupNodeName", 0);
		dig.addCallMethod(root + "/vanillaLdapSynchro/ldapUserNodeName", "setLdapUserNodeName", 0);
		dig.addCallMethod(root + "/vanillaLdapSynchro/ldapUsersDn", "setLdapUsersDn", 0);
		dig.addCallMethod(root + "/vanillaLdapSynchro/groupFilter", "setGroupFilter", 0);
		dig.addCallMethod(root + "/vanillaLdapSynchro/groupAttribute", "setGroupAttribute", 0);		
		dig.addCallMethod(root + "/vanillaLdapSynchro/userMemberNodeName", "setUserMemberNodeName", 0);	
		dig.addCallMethod(root + "/vanillaLdapSynchro/groupMemberNodeName", "setLdapGroupMemberNodeName", 0);
		dig.addCallMethod(root + "/vanillaLdapSynchro/userAttribute", "setUserAttribute", 2);
		dig.addCallParam(root + "/vanillaLdapSynchro/userAttribute/userTableField", 0);
		dig.addCallParam(root + "/vanillaLdapSynchro/userAttribute/attributeName", 1);

		
		
		dig.addSetNext(root + "/vanillaLdapSynchro", "addTransformation");
		
		/*
		 * OlapFactExtractorTransformation
		 */
		dig.addObjectCreate(root + "/olapInput", OlapFactExtractorTranformation.class);
		dig.addCallMethod(root + "/olapInput/name", "setName", 0);
		dig.addCallMethod(root + "/olapInput/description", "setDescription", 0);
		dig.addCallMethod(root + "/olapInput/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/olapInput/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/olapInput/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/olapInput/temporaryFileName", "setTemporaryFilename", 0);
		dig.addCallMethod(root + "/olapInput/directoryItemId", "setDirectoryItemId", 0);
		dig.addCallMethod(root + "/olapInput/directoryItemName", "setDirectoryItemName", 0);
		dig.addCallMethod(root + "/olapInput/cubeName", "setCubeName", 0);
		dig.addCallMethod(root + "/olapInput/trashRef", "setTrashTransformation", 0);
		dig.addCallMethod(root + "/olapInput/dimensionName", "setDimensionName", 0);
		dig.addCallMethod(root + "/olapInput/hierarchieName", "setHierarchieName", 0);
		
		
		dig.addObjectCreate(root + "/olapInput/olapDimensionFilter", DimensionFilter.class);
		dig.addCallMethod(root + "/olapInput/olapDimensionFilter/name", "setName", 0);
		dig.addCallMethod(root + "/olapInput/olapDimensionFilter/outputName", "setOutputTransformation", 0);
		
		dig.addObjectCreate(root + "/olapInput/olapDimensionFilter/olapFilterClause", FilterClause.class);
		dig.addCallMethod(root + "/olapInput/olapDimensionFilter/olapFilterClause/levelName", "setLevelName", 0);
		dig.addCallMethod(root + "/olapInput/olapDimensionFilter/olapFilterClause/value", "setValue", 0);
		dig.addSetNext(root + "/olapInput/olapDimensionFilter/olapFilterClause", "addLevel");
		
		dig.addSetNext(root + "/olapInput/olapDimensionFilter", "addDimensionFilter");
		
		
		dig.addSetNext(root + "/olapInput", "addTransformation");
		
		
		/*
		 * OlapDimensionTransformation
		 */
		dig.addObjectCreate(root + "/freedashFormInput", FreedashFormInput.class);
		dig.addCallMethod(root + "/freedashFormInput/name", "setName", 0);
		dig.addCallMethod(root + "/freedashFormInput/description", "setDescription", 0);
		dig.addCallMethod(root + "/freedashFormInput/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/freedashFormInput/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/freedashFormInput/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/freedashFormInput/temporaryFileName", "setTemporaryFilename", 0);
		dig.addCallMethod(root + "/freedashFormInput/directoryItemId", "setDirectoryItemId", 0);
		dig.addSetNext(root + "/freedashFormInput", "addTransformation");

		
		/*
		 * OlapDimensionTransformation
		 */
		dig.addObjectCreate(root + "/olapDimensionInput", OlapDimensionExtractor.class);
		dig.addCallMethod(root + "/olapDimensionInput/name", "setName", 0);
		dig.addCallMethod(root + "/olapDimensionInput/description", "setDescription", 0);
		dig.addCallMethod(root + "/olapDimensionInput/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/olapDimensionInput/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/olapDimensionInput/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/olapDimensionInput/temporaryFileName", "setTemporaryFilename", 0);
		dig.addCallMethod(root + "/olapDimensionInput/directoryItemId", "setDirectoryItemId", 0);
		dig.addCallMethod(root + "/olapDimensionInput/directoryItemName", "setDirectoryItemName", 0);
		dig.addCallMethod(root + "/olapDimensionInput/cubeName", "setCubeName", 0);
		dig.addCallMethod(root + "/olapDimensionInput/dimensionName", "setDimensionName", 0);
		dig.addCallMethod(root + "/olapDimensionInput/hierarchieName", "setHierarchieName", 0);
		dig.addCallMethod(root + "/olapDimensionInput/levelName", "addLevel", 0);
		dig.addSetNext(root + "/olapDimensionInput", "addTransformation");
		
		/*
		 * OlapOutput
		 */
		dig.addObjectCreate(root + "/olapOutput", OlapOutput.class);
		dig.addCallMethod(root + "/olapOutput/name", "setName", 0);
		dig.addCallMethod(root + "/olapOutput/description", "setDescription", 0);
		dig.addCallMethod(root + "/olapOutput/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/olapOutput/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/olapOutput/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/olapOutput/temporaryFileName", "setTemporaryFilename", 0);
		dig.addCallMethod(root + "/olapOutput/directoryItemId", "setDirectoryItemId", 0);
		dig.addCallMethod(root + "/olapOutput/directoryItemName", "setDirectoryItemName", 0);
		dig.addCallMethod(root + "/olapOutput/cubeName", "setCubeName", 0);
		
		dig.addCallMethod(root + "/olapOutput/inputMapping", "createBufferMapping", 3);
		dig.addCallParam(root + "/olapOutput/inputMapping/transformationRef", 0);
		dig.addCallParam(root + "/olapOutput/inputMapping/inputIndex", 1);
		dig.addCallParam(root + "/olapOutput/inputMapping/outputIndex", 2);

		dig.addCallMethod(root + "/olapOutput/inputMappingName", "createBufferMappingName", 3);
		dig.addCallParam(root + "/olapOutput/inputMappingName/transformationRef", 0);
		dig.addCallParam(root + "/olapOutput/inputMappingName/inputName", 1);
		dig.addCallParam(root + "/olapOutput/inputMappingName/outputName", 2);

		dig.addSetNext(root + "/olapOutput", "addTransformation");
		
		/*
		 * denormalize
		 */
		dig.addObjectCreate(root + "/denormalize", Denormalisation.class);
		dig.addCallMethod(root + "/denormalize/name", "setName", 0);
		dig.addCallMethod(root + "/denormalize/description", "setDescription", 0);
		dig.addCallMethod(root + "/denormalize/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/denormalize/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/denormalize/inputKeyFieldIndex", "setInputKeyField", 0);
		dig.addCallMethod(root + "/denormalize/pivotFieldFieldIndex", "addGroupFieldIndex", 0);
		
		dig.addCallMethod(root + "/denormalize/normalisationField", "addField", 3);
		dig.addCallParam(root + "/denormalize/normalisationField/fieldName", 0);
		dig.addCallParam(root + "/denormalize/normalisationField/inputFieldValueIndex", 1);
		dig.addCallParam(root + "/denormalize/normalisationField/value", 2);
		
		
		dig.addSetNext(root + "/denormalize", "addTransformation");
		
		
		
		/*
		 * Normalize
		 */
		dig.addObjectCreate(root + "/normalize", Normalize.class);
		dig.addCallMethod(root + "/normalize/name", "setName", 0);
		dig.addCallMethod(root + "/normalize/description", "setDescription", 0);
		dig.addCallMethod(root + "/normalize/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/normalize/positionY", "setPositionY", 0);
				
		dig.addCallMethod(root + "/normalize/level", "setInputLevelIndex", 2);
		dig.addCallParam(root + "/normalize/level/number", 0);
		dig.addCallParam(root + "/normalize/level/inputFieldIndex", 1);
		
		
		
		dig.addSetNext(root + "/normalize", "addTransformation");
		
		/*
		 * 
		 */
		dig.addObjectCreate(root + "/validationCleansing", ValidationCleansing.class);
		dig.addCallMethod(root + "/validationCleansing/name", "setName", 0);
		dig.addCallMethod(root + "/validationCleansing/description", "setDescription", 0);
		dig.addCallMethod(root + "/validationCleansing/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/validationCleansing/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/validationCleansing/temporarySeparator", "setTemporarySpliterChar", 0);
		
		dig.addCallMethod(root + "/validationCleansing/trashOutput", "setTrashTransformation", 0);
		dig.addCallMethod(root + "/validationCleansing/fieldValidators", "setFieldValidators", 2, new Class[]{String.class, Object.class});
		
		dig.addCallParam(root + "/validationCleansing/fieldValidators/fieldIndex", 0);
		
		
		dig.addObjectCreate(root + "/validationCleansing/fieldValidators/validationOutputs", ArrayList.class);
		dig.addObjectCreate(root + "/validationCleansing/fieldValidators/validationOutputs/validationOutput", ValidationOutput.class);
		dig.addCallMethod(root + "/validationCleansing/fieldValidators/validationOutputs/validationOutput/description", "setComment", 0);
		dig.addCallMethod(root + "/validationCleansing/fieldValidators/validationOutputs/validationOutput/outputStepName", "setOutputName", 0);
		
		dig.addObjectCreate(root + "/validationCleansing/fieldValidators/validationOutputs/validationOutput/validator",Validator.class);
		dig.addCallMethod(root + "/validationCleansing/fieldValidators/validationOutputs/validationOutput/validator/type", "setType", 0);
		dig.addCallMethod(root + "/validationCleansing/fieldValidators/validationOutputs/validationOutput/validator/name", "setName", 0);
		dig.addCallMethod(root + "/validationCleansing/fieldValidators/validationOutputs/validationOutput/validator/pattern", "setRegex", 0);
		
		
		dig.addSetNext(root + "/validationCleansing/fieldValidators/validationOutputs/validationOutput/validator", "setValidator");
		
		dig.addSetNext(root + "/validationCleansing/fieldValidators/validationOutputs/validationOutput", "add");
		
		dig.addCallParam(root + "/validationCleansing/fieldValidators/validationOutputs", 1, true);
		
		
	
		
		
		dig.addSetNext(root + "/validationCleansing", "addTransformation");

		
		/*
		 * FilterDimension
		 */
		dig.addObjectCreate(root + "/filterDimension", FilterDimension.class);
		dig.addCallMethod(root + "/filterDimension/name", "setName", 0);
		dig.addCallMethod(root + "/filterDimension/description", "setDescription", 0);
		dig.addCallMethod(root + "/filterDimension/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/filterDimension/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/filterDimension/trashOuput", "setTrashTransformation", 0);
		
		dig.addCallMethod(root + "/filterDimension/mapping", "setMapping", 2);
		dig.addCallParam(root + "/filterDimension/mapping/pos", 0);
		dig.addCallParam(root + "/filterDimension/mapping/inputIndex", 1);
		
		
		
		dig.addSetNext(root + "/filterDimension", "addTransformation");

		
		/*
		 * DataBaseInputStreams
		 */
		dig.addObjectCreate(root + "/dataBaseInputStream", DataBaseInputStream.class);
		dig.addCallMethod(root + "/dataBaseInputStream/name", "setName", 0);
		dig.addCallMethod(root + "/dataBaseInputStream/description", "setDescription", 0);
		dig.addCallMethod(root + "/dataBaseInputStream/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/dataBaseInputStream/positionY", "setPositionY", 0);
		dig.addCallMethod("*/container-ref", "setContainer", 0);
		dig.addCallMethod(root + "/dataBaseInputStream/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/dataBaseInputStream/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/dataBaseInputStream/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/dataBaseInputStream/temporaryFileName", "setTemporaryFilename", 0);
		
		dig.addCallMethod(root + "/dataBaseInputStream/outputVariables/variableInit", "addOutputVariable", 2);
		dig.addCallParam(root + "/dataBaseInputStream/outputVariables/variableInit/fieldIndex", 0);
		dig.addCallParam(root + "/dataBaseInputStream/outputVariables/variableInit/variableName", 1);

		dig.addSetNext(root + "/dataBaseInputStream", "addTransformation");
		
		/*
		 * DataPreparationInput
		 */
		dig.addObjectCreate(root + "/datapreparationinput", DataPreparationInput.class);
		dig.addCallMethod(root + "/datapreparationinput/name", "setName", 0);
		dig.addCallMethod(root + "/datapreparationinput/description", "setDescription", 0);
		dig.addCallMethod(root + "/datapreparationinput/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/datapreparationinput/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/datapreparationinput/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/datapreparationinput/temporaryFileName", "setTemporaryFilename", 0);
		dig.addCallMethod(root + "/datapreparationinput/dataPrepId", "setDataPrepId", 0);

		dig.addSetNext(root + "/datapreparationinput", "addTransformation");
		
		/*
		 * TopX
		 */
		dig.addObjectCreate(root + "/topX", TopXTransformation.class);
		dig.addCallMethod(root + "/topX/name", "setName", 0);
		dig.addCallMethod(root + "/topX/description", "setDescription", 0);
		dig.addCallMethod(root + "/topX/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/topX/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/topX/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/topX/temporaryFileName", "setTemporaryFilename", 0);
		
		dig.addCallMethod(root + "/topX/fieldIndex", "setField", 0);
		dig.addCallMethod(root + "/topX/sorting", "setSorting", 0);
		dig.addCallMethod(root + "/topX/topX", "setX", 0);
		dig.addSetNext(root + "/topX", "addTransformation");
		
		/*
		 * ScriptSql
		 */
		dig.addObjectCreate(root + "/sqlScript", SqlScript.class);
		dig.addCallMethod(root + "/sqlScript/name", "setName", 0);
		dig.addCallMethod(root + "/sqlScript/description", "setDescription", 0);
		dig.addCallMethod(root + "/sqlScript/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/sqlScript/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/sqlScript/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/sqlScript/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/sqlScript/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/sqlScript/temporaryFileName", "setTemporaryFilename", 0);
		dig.addSetNext(root + "/sqlScript", "addTransformation");
		

		/*
		 * OdaInput	
		 */
		dig.addObjectCreate(root + "/odaInput", OdaInput.class);
		dig.addCallMethod(root + "/odaInput/name", "setName", 0);
		dig.addCallMethod(root + "/odaInput/description", "setDescription", 0);
		dig.addCallMethod(root + "/odaInput/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/odaInput/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/odaInput/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/odaInput/temporaryFileName", "setTemporaryFilename", 0);
		
		dig.addCallMethod(root + "/odaInput/odaExtensionId", "setOdaExtensionId", 0);
		dig.addCallMethod(root + "/odaInput/odaExtensionDataSourceId", "setOdaExtensionDataSourceId", 0);
		
		
		dig.addCallMethod(root + "/odaInput/privateDataSource/property", "setDatasourcePrivateProperty", 2);
		dig.addCallParam(root + "/odaInput/privateDataSource/property/name", 0);
		dig.addCallParam(root + "/odaInput/privateDataSource/property/value", 1);

		
		
		dig.addCallMethod(root + "/odaInput/publicDataSource/property", "setDatasourcePublicProperty", 2);
		dig.addCallParam(root + "/odaInput/publicDataSource/property/name", 0);
		dig.addCallParam(root + "/odaInput/publicDataSource/property/value", 1);
		

		
		dig.addCallMethod(root + "/odaInput/privateDataSet/property", "setDatasetPrivateProperty", 2);
		dig.addCallParam(root + "/odaInput/privateDataSet/property/name", 0);
		dig.addCallParam(root + "/odaInput/privateDataSet/property/value", 1);
		
		
		dig.addCallMethod(root + "/odaInput/publicDataSet/property", "setDatasetPublicProperty", 2);
		dig.addCallParam(root + "/odaInput/publicDataSet/property/name", 0);
		dig.addCallParam(root + "/odaInput/publicDataSet/property/value", 1);	
		
		
		dig.addCallMethod(root + "/odaInput/queryText", "setQueryText", 0);
		
		dig.addCallMethod(root + "/odaInput/parameter", "setParameter", 2);
		dig.addCallParam(root + "/odaInput/parameter/name", 0);
		dig.addCallParam(root + "/odaInput/parameter/value", 1);
		
		
		dig.addSetNext(root + "/odaInput", "addTransformation");

		
		/*
		 * OdaInputWithParamaters
		 */
		dig.addObjectCreate(root + "/odaInputWithParameters", OdaInputWithParameters.class);
		dig.addCallMethod(root + "/odaInputWithParameters/name", "setName", 0);
		dig.addCallMethod(root + "/odaInputWithParameters/description", "setDescription", 0);
		dig.addCallMethod(root + "/odaInputWithParameters/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/odaInputWithParameters/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/odaInputWithParameters/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/odaInputWithParameters/temporaryFileName", "setTemporaryFilename", 0);
		
		dig.addCallMethod(root + "/odaInputWithParameters/odaExtensionId", "setOdaExtensionId", 0);
		dig.addCallMethod(root + "/odaInputWithParameters/odaExtensionDataSourceId", "setOdaExtensionDataSourceId", 0);
		
		
		dig.addCallMethod(root + "/odaInputWithParameters/privateDataSource/property", "setDatasourcePrivateProperty", 2);
		dig.addCallParam(root + "/odaInputWithParameters/privateDataSource/property/name", 0);
		dig.addCallParam(root + "/odaInputWithParameters/privateDataSource/property/value", 1);

		
		
		dig.addCallMethod(root + "/odaInputWithParameters/publicDataSource/property", "setDatasourcePublicProperty", 2);
		dig.addCallParam(root + "/odaInputWithParameters/publicDataSource/property/name", 0);
		dig.addCallParam(root + "/odaInputWithParameters/publicDataSource/property/value", 1);
		

		
		dig.addCallMethod(root + "/odaInputWithParameters/privateDataSet/property", "setDatasetPrivateProperty", 2);
		dig.addCallParam(root + "/odaInputWithParameters/privateDataSet/property/name", 0);
		dig.addCallParam(root + "/odaInputWithParameters/privateDataSet/property/value", 1);
		
		
		dig.addCallMethod(root + "/odaInputWithParameters/publicDataSet/property", "setDatasetPublicProperty", 2);
		dig.addCallParam(root + "/odaInputWithParameters/publicDataSet/property/name", 0);
		dig.addCallParam(root + "/odaInputWithParameters/publicDataSet/property/value", 1);	
		
		
		dig.addCallMethod(root + "/odaInputWithParameters/queryText", "setQueryText", 0);
		
		dig.addCallMethod(root + "/odaInputWithParameters/parameter", "setParameter", 2);
		dig.addCallParam(root + "/odaInputWithParameters/parameter/name", 0);
		dig.addCallParam(root + "/odaInputWithParameters/parameter/value", 1);
		
		
		dig.addSetNext(root + "/odaInputWithParameters", "addTransformation");
		
		/*
		 * CartesianProduct
		 */
		dig.addObjectCreate(root + "/cartesianProduct", CartesianProduct.class);
		dig.addCallMethod(root + "/cartesianProduct/name", "setName", 0);
		dig.addCallMethod(root + "/cartesianProduct/description", "setDescription", 0);
		dig.addCallMethod(root + "/cartesianProduct/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/cartesianProduct/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/cartesianProduct/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/cartesianProduct/temporaryFileName", "setTemporaryFilename", 0);
		dig.addSetNext(root + "/cartesianProduct", "addTransformation");

		
		/*
		 * SubTransformationFinalStep
		 */
		dig.addObjectCreate(root + "/subTransformationFinalStep", SubTransformationFinalStep.class);
		dig.addCallMethod(root + "/subTransformationFinalStep/name", "setName", 0);
		dig.addCallMethod(root + "/subTransformationFinalStep/description", "setDescription", 0);
		dig.addCallMethod(root + "/subTransformationFinalStep/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/subTransformationFinalStep/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/subTransformationFinalStep/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/subTransformationFinalStep/temporaryFileName", "setTemporaryFilename", 0);
		dig.addSetNext(root + "/subTransformationFinalStep", "addTransformation");
		
		/*
		 * LdapInput
		 */
		dig.addObjectCreate(root + "/ldapInput", LdapInput.class);
		dig.addCallMethod(root + "/ldapInput/name", "setName", 0);
		dig.addCallMethod(root + "/ldapInput/description", "setDescription", 0);
		dig.addCallMethod(root + "/ldapInput/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/ldapInput/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/ldapInput/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/ldapInput/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/ldapInput/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/ldapInput/temporaryFileName", "setTemporaryFilename", 0);
		dig.addObjectCreate(root + "/ldapInput/attributeMappings", LinkedHashMap.class);
		dig.addCallMethod(root + "/ldapInput/attributeMappings/attributeMapping", "put", 2);
		dig.addCallParam(root + "/ldapInput/attributeMappings/attributeMapping/fieldName",0);
		dig.addCallParam(root + "/ldapInput/attributeMappings/attributeMapping/attributeName",1);
		dig.addSetNext(root + "/ldapInput/attributeMappings", "setAttributeMapping");
		dig.addSetNext(root + "/ldapInput", "addTransformation");
		
		/*
		 * LdapMultipleMembers
		 */
		dig.addObjectCreate(root + "/ldapMultipleMembers", LdapMultipleMembers.class);
		dig.addCallMethod(root + "/ldapMultipleMembers/name", "setName", 0);
		dig.addCallMethod(root + "/ldapMultipleMembers/description", "setDescription", 0);
		dig.addCallMethod(root + "/ldapMultipleMembers/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/ldapMultipleMembers/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/ldapMultipleMembers/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/ldapMultipleMembers/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/ldapMultipleMembers/attributeName", "setAttributeName", 0);
		dig.addCallMethod(root + "/ldapMultipleMembers/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/ldapMultipleMembers/temporaryFileName", "setTemporaryFilename", 0);
		dig.addSetNext(root + "/ldapMultipleMembers", "addTransformation");
		
		
		/*
		 * DataBaseOutputStreams
		 */
		dig.addObjectCreate(root + "/dataBaseOutputStream", DataBaseOutputStream.class);
		dig.addCallMethod(root + "/dataBaseOutputStream/name", "setName", 0);
		dig.addCallMethod(root + "/dataBaseOutputStream/description", "setDescription", 0);
		dig.addCallMethod(root + "/dataBaseOutputStream/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/dataBaseOutputStream/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/dataBaseOutputStream/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/dataBaseOutputStream/temporaryFileName", "setTemporaryFilename", 0);
		
		dig.addCallMethod(root + "/dataBaseOutputStream/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/dataBaseOutputStream/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/dataBaseOutputStream/truncate", "setTruncate", 0);
		
		dig.addCallMethod(root + "/dataBaseOutputStream/inputMapping", "createBufferMapping", 3);
		dig.addCallParam(root + "/dataBaseOutputStream/inputMapping/transformationRef", 0);
		dig.addCallParam(root + "/dataBaseOutputStream/inputMapping/inputIndex", 1);
		dig.addCallParam(root + "/dataBaseOutputStream/inputMapping/outputIndex", 2);

		dig.addCallMethod(root + "/dataBaseOutputStream/inputMappingName", "createBufferMappingName", 3);
		dig.addCallParam(root + "/dataBaseOutputStream/inputMappingName/transformationRef", 0);
		dig.addCallParam(root + "/dataBaseOutputStream/inputMappingName/inputName", 1);
		dig.addCallParam(root + "/dataBaseOutputStream/inputMappingName/outputName", 2);
		
		
		dig.addCallMethod(root + "/dataBaseOutputStream/fmdtDestinationServer-ref", "setFmdtDestinationServer", 0);
		dig.addCallMethod(root + "/dataBaseOutputStream/destinationFolder", "setDestinationFolder", 0);
		dig.addCallMethod(root + "/dataBaseOutputStream/destinationName", "setDestinationName", 0);
		dig.addCallMethod(root + "/dataBaseOutputStream/fmdtGroup", "addSecurizedGroup", 0);
		dig.addCallMethod(root + "/dataBaseOutputStream/trashOuput", "setTrashTransformation", 0);
		dig.addSetNext(root + "/dataBaseOutputStream", "addTransformation");
		
		
		/*
		 * CielOutput
		 */
		dig.addObjectCreate(root + "/cielComptaOutput", CielComptaOutput.class);
		dig.addCallMethod(root + "/cielComptaOutput/name", "setName", 0);
		dig.addCallMethod(root + "/cielComptaOutput/description", "setDescription", 0);
		dig.addCallMethod(root + "/cielComptaOutput/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/cielComptaOutput/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/cielComptaOutput/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/cielComptaOutput/temporaryFileName", "setTemporaryFilename", 0);
		
		dig.addCallMethod(root + "/cielComptaOutput/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/cielComptaOutput/definition", "setDefinition", 0);
		
		dig.addCallMethod(root + "/cielComptaOutput/inputMapping", "createBufferMapping", 3);
		dig.addCallParam(root + "/cielComptaOutput/inputMapping/transformationRef", 0);
		dig.addCallParam(root + "/cielComptaOutput/inputMapping/inputIndex", 1);
		dig.addCallParam(root + "/cielComptaOutput/inputMapping/outputIndex", 2);

		dig.addCallMethod(root + "/cielComptaOutput/inputMappingName", "createBufferMappingName", 3);
		dig.addCallParam(root + "/cielComptaOutput/inputMappingName/transformationRef", 0);
		dig.addCallParam(root + "/cielComptaOutput/inputMappingName/inputName", 1);
		dig.addCallParam(root + "/cielComptaOutput/inputMappingName/outputName", 2);

		dig.addSetNext(root + "/cielComptaOutput", "addTransformation");
		
		
		/*
		 * InfoBrightInjector
		 */
		dig.addObjectCreate(root + "/infobrightInjector", InfoBrightInjector.class);
		dig.addCallMethod(root + "/infobrightInjector/name", "setName", 0);
		dig.addCallMethod(root + "/infobrightInjector/description", "setDescription", 0);
		dig.addCallMethod(root + "/infobrightInjector/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/infobrightInjector/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/infobrightInjector/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/infobrightInjector/temporaryFileName", "setTemporaryFilename", 0);
		
		dig.addCallMethod(root + "/infobrightInjector/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/infobrightInjector/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/infobrightInjector/truncate", "setTruncate", 0);
		
		dig.addCallMethod(root + "/infobrightInjector/inputMapping", "createBufferMapping", 3);
		dig.addCallParam(root + "/infobrightInjector/inputMapping/transformationRef", 0);
		dig.addCallParam(root + "/infobrightInjector/inputMapping/inputIndex", 1);
		dig.addCallParam(root + "/infobrightInjector/inputMapping/outputIndex", 2);
		
		
		dig.addCallMethod(root + "/infobrightInjector/fmdtDestinationServer-ref", "setFmdtDestinationServer", 0);
		dig.addCallMethod(root + "/infobrightInjector/destinationFolder", "setDestinationFolder", 0);
		dig.addCallMethod(root + "/infobrightInjector/destinationName", "setDestinationName", 0);
		dig.addCallMethod(root + "/infobrightInjector/fmdtGroup", "addSecurizedGroup", 0);
		
		dig.addSetNext(root + "/infobrightInjector", "addTransformation");
		
		
		/*
		 * vanillaRoleGroupAssociation
		 */
		dig.addObjectCreate(root + "/vanillaRoleGroupAssociation", VanillaRoleGroupAssocaition.class);
		dig.addCallMethod(root + "/vanillaRoleGroupAssociation/name", "setName", 0);
		dig.addCallMethod(root + "/vanillaRoleGroupAssociation/description", "setDescription", 0);
		dig.addCallMethod(root + "/vanillaRoleGroupAssociation/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/vanillaRoleGroupAssociation/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/vanillaRoleGroupAssociation/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/vanillaRoleGroupAssociation/temporaryFileName", "setTemporaryFilename", 0);
		
		
		dig.addCallMethod(root + "/vanillaRoleGroupAssociation/groupIdIndex", "setBufferGroupIdIndex", 0);
		dig.addCallMethod(root + "/vanillaRoleGroupAssociation/groupIdName", "setGroupIdName", 0);
		dig.addCallMethod(root + "/vanillaRoleGroupAssociation/roleId", "addRole", 0);

				
		dig.addSetNext(root + "/vanillaRoleGroupAssociation", "addTransformation");

		
		/*
		 * VanillaGroupUser
		 */
		dig.addObjectCreate(root + "/vanillaGroupInsertion", VanillaGroupUser.class);
		dig.addCallMethod(root + "/vanillaGroupInsertion/name", "setName", 0);
		dig.addCallMethod(root + "/vanillaGroupInsertion/description", "setDescription", 0);
		dig.addCallMethod(root + "/vanillaGroupInsertion/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/vanillaGroupInsertion/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/vanillaGroupInsertion/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/vanillaGroupInsertion/temporaryFileName", "setTemporaryFilename", 0);
		
		
		dig.addCallMethod(root + "/vanillaGroupInsertion/inputMapping", "createBufferMapping", 2);
		dig.addCallParam(root + "/vanillaGroupInsertion/inputMapping/inputIndex", 0);
		dig.addCallParam(root + "/vanillaGroupInsertion/inputMapping/outputIndex", 1);
		
		dig.addCallMethod(root + "/vanillaGroupInsertion/inputMappingName", "createMappingName", 2);
		dig.addCallParam(root + "/vanillaGroupInsertion/inputMappingName/inputName", 0);
		dig.addCallParam(root + "/vanillaGroupInsertion/inputMappingName/outputName", 1);
				
		dig.addSetNext(root + "/vanillaGroupInsertion", "addTransformation");

		
		/*
		 * VanillaCreateUser
		 */
		dig.addObjectCreate(root + "/vanillaCreateUser", VanillaCreateUser.class);
		dig.addCallMethod(root + "/vanillaCreateUser/name", "setName", 0);
		dig.addCallMethod(root + "/vanillaCreateUser/description", "setDescription", 0);
		dig.addCallMethod(root + "/vanillaCreateUser/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/vanillaCreateUser/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/vanillaCreateUser/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/vanillaCreateUser/temporaryFileName", "setTemporaryFilename", 0);
		dig.addCallMethod(root + "/vanillaCreateUser/updateExisting", "setUpdateExisting", 0);
		
		
		dig.addCallMethod(root + "/vanillaCreateUser/inputMapping", "createBufferMapping", 2);
		dig.addCallParam(root + "/vanillaCreateUser/inputMapping/inputIndex", 0);
		dig.addCallParam(root + "/vanillaCreateUser/inputMapping/outputIndex", 1);
		
		
		dig.addCallMethod(root + "/vanillaCreateUser/inputMappingName", "createMappingName", 2);
		dig.addCallParam(root + "/vanillaCreateUser/inputMappingName/inputName", 0);
		dig.addCallParam(root + "/vanillaCreateUser/inputMappingName/outputName", 1);
				
		dig.addSetNext(root + "/vanillaCreateUser", "addTransformation");
		
		/*
		 * VanillaCreateUser
		 */
		dig.addObjectCreate(root + "/vanillaCreateGroup", VanillaCreateGroup.class);
		dig.addCallMethod(root + "/vanillaCreateGroup/name", "setName", 0);
		dig.addCallMethod(root + "/vanillaCreateGroup/description", "setDescription", 0);
		dig.addCallMethod(root + "/vanillaCreateGroup/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/vanillaCreateGroup/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/vanillaCreateGroup/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/vanillaCreateGroup/temporaryFileName", "setTemporaryFilename", 0);
		
		
		
		dig.addCallMethod(root + "/vanillaCreateGroup/inputMapping", "createBufferMapping", 2);
		dig.addCallParam(root + "/vanillaCreateGroup/inputMapping/inputIndex", 0);
		dig.addCallParam(root + "/vanillaCreateGroup/inputMapping/outputIndex", 1);
		
		
		dig.addCallMethod(root + "/vanillaCreateGroup/inputMappingName", "createMappingName", 2);
		dig.addCallParam(root + "/vanillaCreateGroup/inputMappingName/inputName", 0);
		dig.addCallParam(root + "/vanillaCreateGroup/inputMappingName/outputName", 1);
				
		dig.addSetNext(root + "/vanillaCreateGroup", "addTransformation");

		
		/*
		 * DeleteRows
		 */
		dig.addObjectCreate(root + "/deleteRows", DeleteRows.class);
		dig.addCallMethod(root + "/deleteRows/name", "setName", 0);
		dig.addCallMethod(root + "/deleteRows/description", "setDescription", 0);
		dig.addCallMethod(root + "/deleteRows/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/deleteRows/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/deleteRows/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/deleteRows/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/deleteRows/keepDistinct", "setKeepDistinctRows", 0);
		dig.addCallMethod(root + "/deleteRows/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/deleteRows/temporaryFileName", "setTemporaryFilename", 0);
		dig.addCallMethod(root + "/deleteRows/truncate", "setTruncate", 0);
		
		dig.addCallMethod(root + "/deleteRows/inputMapping", "createBufferMapping", 3);
		dig.addCallParam(root + "/deleteRows/inputMapping/transformationRef", 0);
		dig.addCallParam(root + "/deleteRows/inputMapping/inputIndex", 1);
		dig.addCallParam(root + "/deleteRows/inputMapping/outputIndex", 2);
		
		dig.addCallMethod(root + "/deleteRows/inputMappingName", "createBufferMappingName", 3);
		dig.addCallParam(root + "/deleteRows/inputMappingName/transformationRef", 0);
		dig.addCallParam(root + "/deleteRows/inputMappingName/inputName", 1);
		dig.addCallParam(root + "/deleteRows/inputMappingName/outputName", 2);
		
		dig.addSetNext(root + "/deleteRows", "addTransformation");

		/*
		 * SimpleMappingTransformation
		 */
		dig.addObjectCreate(root + "/simpleMappingTransformation", SimpleMappingTransformation.class);
		dig.addCallMethod(root + "/simpleMappingTransformation/name", "setName", 0);
		dig.addCallMethod(root + "/simpleMappingTransformation/description", "setDescription", 0);
		dig.addCallMethod(root + "/simpleMappingTransformation/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/simpleMappingTransformation/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/simpleMappingTransformation/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/simpleMappingTransformation/temporaryFileName", "setTemporaryFilename", 0);
		dig.addCallMethod(root + "/simpleMappingTransformation/firstInput", "setFirstInputName", 0);
		
		dig.addCallMethod(root + "/simpleMappingTransformation/inputMap", "createBufferMapping", 2);
		dig.addCallParam(root + "/simpleMappingTransformation/inputMap/indice", 0);
		dig.addCallParam(root + "/simpleMappingTransformation/inputMap/value", 1);
		
		dig.addCallMethod(root + "/simpleMappingTransformation/inputMappingName", "createBufferMappingName", 2);
		dig.addCallParam(root + "/simpleMappingTransformation/inputMappingName/inputName", 0);
		dig.addCallParam(root + "/simpleMappingTransformation/inputMappingName/outputName", 1);
		
		dig.addCallMethod(root + "/simpleMappingTransformation/masterName", "setMasterInput", 0);
		
		
		dig.addSetNext(root + "/simpleMappingTransformation", "addTransformation");
		
		/*
		 * ConsistencyTransformation
		 */
		dig.addObjectCreate(root + "/consistencyTransformation", ConsitencyTransformation.class);
		dig.addCallMethod(root + "/consistencyTransformation/name", "setName", 0);
		dig.addCallMethod(root + "/consistencyTransformation/description", "setDescription", 0);
		dig.addCallMethod(root + "/consistencyTransformation/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/consistencyTransformation/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/consistencyTransformation/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/consistencyTransformation/temporaryFileName", "setTemporaryFilename", 0);
		
			dig.addObjectCreate(root + "/consistencyTransformation/mapping", ConsistencyMapping.class);
			dig.addCallMethod(root + "/consistencyTransformation/mapping/inputName", "setInputName", 0);
			dig.addCallMethod(root + "/consistencyTransformation/mapping/mappingValues", "addMapping", 2);
			dig.addCallParam(root + "/consistencyTransformation/mapping/mappingValues/mappingkey", 0);
			dig.addCallParam(root + "/consistencyTransformation/mapping/mappingValues/mappingvalue", 1);
		
		dig.addSetNext(root + "/consistencyTransformation/mapping", "addMapping");
		
		dig.addCallMethod(root + "/consistencyTransformation/masterName", "setMasterName", 0);
		dig.addCallMethod(root + "/consistencyTransformation/trashName", "setTrashName", 0);
		
		
		dig.addSetNext(root + "/consistencyTransformation", "addTransformation");
		

		/*
		 * SelectionTransformation
		 */
		dig.addObjectCreate(root + "/selectionTransformation", SelectionTransformation.class);
		dig.addCallMethod(root + "/selectionTransformation/name", "setName", 0);
		dig.addCallMethod(root + "/selectionTransformation/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/selectionTransformation/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/selectionTransformation/description", "setDescription", 0);
		dig.addCallMethod(root + "/selectionTransformation/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/selectionTransformation/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/selectionTransformation/selectedIndex", "initOutputed", 0);
		dig.addCallMethod(root + "/selectionTransformation/selectedNames", "initOutputedNames", 0);
		dig.addSetNext(root + "/selectionTransformation", "addTransformation");
		
		/*
		 * PreviousValueTransformation
		 */
		dig.addObjectCreate(root + "/previousValueTransformation", PreviousValueTransformation.class);
		dig.addCallMethod(root + "/previousValueTransformation/name", "setName", 0);
		dig.addCallMethod(root + "/previousValueTransformation/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/previousValueTransformation/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/previousValueTransformation/description", "setDescription", 0);
		dig.addCallMethod(root + "/previousValueTransformation/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/previousValueTransformation/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/previousValueTransformation/key", "addKeyDigester", 0);
		dig.addCallMethod(root + "/previousValueTransformation/previous", "addPreviousDigester", 0);
		dig.addSetNext(root + "/previousValueTransformation", "addTransformation");
		

		/*
		 * AnonymeTransformation
		 */
		dig.addObjectCreate(root + "/anonymeTransformation", EncryptTransformation.class);
		dig.addCallMethod(root + "/anonymeTransformation/name", "setName", 0);
		dig.addCallMethod(root + "/anonymeTransformation/publicKey", "setPublicKey", 0);
		dig.addCallMethod(root + "/anonymeTransformation/encrypt", "setEncrypt", 0);
		dig.addCallMethod(root + "/anonymeTransformation/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/anonymeTransformation/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/anonymeTransformation/description", "setDescription", 0);
		dig.addCallMethod(root + "/anonymeTransformation/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/anonymeTransformation/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/anonymeTransformation/selectedIndex", "initOutputed", 0);
		dig.addCallMethod(root + "/anonymeTransformation/selectedNames", "initOutputedNames", 0);
		dig.addSetNext(root + "/anonymeTransformation", "addTransformation");
		
		/*
		 * Link
		 */
		dig.addCallMethod(root + "/link", "createLink", 3, new Class[]{String.class, String.class, ArrayList.class});
		dig.addCallParam(root + "/link/from", 0);
		dig.addCallParam(root + "/link/to", 1);
		
		dig.addObjectCreate(root + "/link/bendPoints", ArrayList.class);
		
		dig.addObjectCreate(root + "/link/bendPoints/point", Point.class);
		dig.addCallMethod(root + "/link/bendPoints/point","setLocation", 2, new Class[]{int.class, int.class});
		dig.addCallParam(root + "/link/bendPoints/point/x", 0);
		dig.addCallParam(root + "/link/bendPoints/point/y", 1);
		
		dig.addSetNext(root +"/link/bendPoints/point", "add");
		dig.addCallParam(root + "/link/bendPoints", 2, true);
		/*
		 * FileOutputCSV
		 */
		dig.addObjectCreate(root + "/fileOutputCSV", FileOutputCSV.class);
		dig.addCallMethod(root + "/fileOutputCSV/name", "setName", 0);
		dig.addCallMethod(root + "/fileOutputCSV/description", "setDescription", 0);
		dig.addCallMethod(root + "/fileOutputCSV/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/fileOutputCSV/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/fileOutputCSV/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/fileOutputCSV/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/fileOutputCSV/encoding", "setEncoding", 0);
		dig.addCallMethod(root + "/fileOutputCSV/delete", "setDelete", 0);
		dig.addCallMethod(root + "/fileOutputCSV/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/fileOutputCSV/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/fileOutputCSV/separator", "setSeparator", 0);
		dig.addCallMethod(root + "/fileOutputCSV/append", "setAppend", 0);
		dig.addCallMethod(root + "/fileOutputCSV/useMdm", "setUseMdm", 0);
		dig.addCallMethod(root + "/fileOutputCSV/supplierId", "setSupplierId", 0);
		dig.addCallMethod(root + "/fileOutputCSV/contractId", "setContractId", 0);
		dig.addCallMethod(root + "/fileOutputCSV/useD4C", "setUseD4C", 0);
		dig.addCallMethod(root + "/fileOutputCSV/containsHeaders", "setContainHeaders", 0);
		dig.addSetNext(root + "/fileOutputCSV", "addTransformation");
		
		
		/*
		 * geojsonoutput
		 */
		dig.addObjectCreate(root + "/geojsonoutput", GeoJsonOutput.class);
		dig.addCallMethod(root + "/geojsonoutput/name", "setName", 0);
		dig.addCallMethod(root + "/geojsonoutput/description", "setDescription", 0);
		dig.addCallMethod(root + "/geojsonoutput/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/geojsonoutput/positionY", "setPositionY", 0);

		dig.addCallMethod(root + "/geojsonoutput/filepath", "setFilePath", 0);
		dig.addCallMethod(root + "/geojsonoutput/latitude", "setLatitudeColumn", 0);
		dig.addCallMethod(root + "/geojsonoutput/longitude", "setLongitudeColumn", 0);
		dig.addCallMethod(root + "/geojsonoutput/geotype", "setGeometryType", 0);
		dig.addSetNext(root + "/geojsonoutput", "addTransformation");
		
		/*
		 * FileOutputWeka
		 */
		dig.addObjectCreate(root + "/fileOutputWeka", FileOutputWeka.class);
		dig.addCallMethod(root + "/fileOutputWeka/name", "setName", 0);
		dig.addCallMethod(root + "/fileOutputWeka/description", "setDescription", 0);
		dig.addCallMethod(root + "/fileOutputWeka/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/fileOutputWeka/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/fileOutputWeka/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/fileOutputWeka/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/fileOutputWeka/encoding", "setEncoding", 0);
		dig.addCallMethod(root + "/fileOutputWeka/delete", "setDelete", 0);
		dig.addCallMethod(root + "/fileOutputWeka/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/fileOutputWeka/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/fileOutputWeka/separator", "setSeparator", 0);
		dig.addSetNext(root + "/fileOutputWeka", "addTransformation");

		
		
		/*
		 * FileInputCSV
		 */
		dig.addObjectCreate(root + "/fileInputCSV", FileInputCSV.class);
		dig.addCallMethod(root + "/fileInputCSV/name", "setName", 0);
		dig.addCallMethod(root + "/fileInputCSV/skipFirstRow", "setSkipFirstRow", 0);
		dig.addCallMethod(root + "/fileInputCSV/description", "setDescription", 0);
		dig.addCallMethod(root + "/fileInputCSV/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/fileInputCSV/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/fileInputCSV/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/fileInputCSV/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/fileInputCSV/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/fileInputCSV/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/fileInputCSV/fromUrl", "setFromUrl", 0);
		dig.addCallMethod(root + "/fileInputCSV/separator", "setSeparator", 0);
		dig.addCallMethod(root + "/fileInputCSV/trashOuput", "setTrashTransformation", 0);
		dig.addCallMethod(root + "/fileInputCSV/encoding", "setEncoding", 0);
		dig.addCallMethod(root + "/fileInputCSV/isJson", "setIsJson", 0);
		dig.addCallMethod(root + "/fileInputCSV/jsonRootItem", "setJsonRootItem", 0);
		dig.addCallMethod(root + "/fileInputCSV/jsonDepth", "setJsonDepth", 0);
		dig.addSetNext(root + "/fileInputCSV", "addTransformation");
		
		
		/*
		 * KMLInput
		 */
		dig.addObjectCreate(root + "/kmlInput", KMLInput.class);
		dig.addCallMethod(root + "/kmlInput/name", "setName", 0);
		dig.addCallMethod(root + "/kmlInput/description", "setDescription", 0);
		dig.addCallMethod(root + "/kmlInput/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/kmlInput/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/kmlInput/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/kmlInput/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/kmlInput/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/kmlInput/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/kmlInput/objectType", "setKmlObjectType", 0);
		dig.addCallMethod(root + "/kmlInput/generateId", "setGenerateIdPerFoundObject", 0);
		dig.addCallMethod(root + "/kmlInput/useMdm", "setUseMdm", 0);
		dig.addCallMethod(root + "/kmlInput/supplierId", "setSupplierId", 0);
		dig.addCallMethod(root + "/kmlInput/contractId", "setContractId", 0);
		dig.addSetNext(root + "/kmlInput", "addTransformation");
		
		/*
		 * FileInputPDFForm
		 */
		dig.addObjectCreate(root + "/fileInputPDFForm", FileInputPDFForm.class);
		dig.addCallMethod(root + "/fileInputPDFForm/name", "setName", 0);
		dig.addCallMethod(root + "/fileInputPDFForm/description", "setDescription", 0);
		dig.addCallMethod(root + "/fileInputPDFForm/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/fileInputPDFForm/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/fileInputPDFForm/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/fileInputPDFForm/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/fileInputPDFForm/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/fileInputPDFForm/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/fileInputPDFForm/propertiesFilePath", "setPropertiesFilePath", 0);
		dig.addSetNext(root + "/fileInputPDFForm", "addTransformation");
		
		/*
		 * KMLOutput
		 */
		dig.addObjectCreate(root + "/kmlOutput", KMLOutput.class);
		dig.addCallMethod(root + "/kmlOutput/name", "setName", 0);
		dig.addCallMethod(root + "/kmlOutput/description", "setDescription", 0);
		dig.addCallMethod(root + "/kmlOutput/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/kmlOutput/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/kmlOutput/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/kmlOutput/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/kmlOutput/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/kmlOutput/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/kmlOutput/objectType", "setKmlObjectType", 0);
		dig.addCallMethod(root + "/kmlOutput/longitudeIndex", "setCoordinateLongitudeIndex", 0);
		dig.addCallMethod(root + "/kmlOutput/latitudeIndex", "setCoordinateLatitudeIndex", 0);
		dig.addCallMethod(root + "/kmlOutput/altitudeIndex", "setCoordinateAltitudeIndex", 0);
		dig.addCallMethod(root + "/kmlOutput/nameIndex", "setNameIndex", 0);
		dig.addCallMethod(root + "/kmlOutput/descriptionIndex", "setDescriptionIndex", 0);
		
		dig.addSetNext(root + "/kmlOutput", "addTransformation");
		
		/*
		 * FileInputVCL
		 */
		dig.addObjectCreate(root + "/fileInputVCL", FileInputVCL.class);
		dig.addCallMethod(root + "/fileInputVCL/name", "setName", 0);
		dig.addCallMethod(root + "/fileInputVCL/description", "setDescription", 0);
		dig.addCallMethod(root + "/fileInputVCL/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/fileInputVCL/temporaryFileName", "setTemporaryFilename", 0);
		dig.addCallMethod(root + "/fileInputVCL/encoding", "setEncoding", 0);
		dig.addCallMethod(root + "/fileInputVCL/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/fileInputVCL/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/fileInputVCL/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/fileInputVCL/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/fileInputVCL/trashOuput", "setTrashTransformation", 0);
		dig.addCallMethod(root + "/fileInputVCL/numberOfRowToSkip", "setSkipFirstRow", 0);
		dig.addSetNext(root + "/fileInputVCL", "addTransformation");
		
		
		/*
		 * FileOutputVCL
		 */
		dig.addObjectCreate(root + "/fileOutputVCL", FileOutputVCL.class);
		dig.addCallMethod(root + "/fileOutputVCL/name", "setName", 0);
		dig.addCallMethod(root + "/fileOutputVCL/description", "setDescription", 0);
		dig.addCallMethod(root + "/fileOutputVCL/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/fileOutputVCL/temporaryFileName", "setTemporaryFilename", 0);
		dig.addCallMethod(root + "/fileOutputVCL/encoding", "setEncoding", 0);
		dig.addCallMethod(root + "/fileOutputVCL/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/fileOutputVCL/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/fileOutputVCL/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/fileOutputVCL/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/fileOutputVCL/trashOuput", "setTrashTransformation", 0);
		dig.addCallMethod(root + "/fileOutputVCL/numberOfRowToSkip", "setSkipFirstRow", 0);
		
		dig.addCallMethod(root + "/fileOutputVCL/append", "setAppend", 0);
		dig.addCallMethod(root + "/fileOutputVCL/delete", "setDelete", 0);
		dig.addCallMethod(root + "/fileOutputVCL/containsHeaders", "setContainHeaders", 0);
		dig.addCallMethod(root + "/fileOutputVCL/truncateField", "setTruncateField", 0);
		
		dig.addCallMethod(root + "/fileOutputVCL/columnLength/", "setColumnSize", 2);
		dig.addCallParam(root + "/fileOutputVCL/columnLength/columnIndex", 0);
		dig.addCallParam(root + "/fileOutputVCL/columnLength/length", 1);
		
		dig.addSetNext(root + "/fileOutputVCL", "addTransformation");

		
		
		/*
		 * FileFolderReader
		 */
		dig.addObjectCreate(root + "/fileFolderReader", FileFolderReader.class);
		dig.addCallMethod(root + "/fileFolderReader/name", "setName", 0);
		dig.addCallMethod(root + "/fileFolderReader/skipFirstRow", "setSkipFirstRow", 0);
		dig.addCallMethod(root + "/fileFolderReader/description", "setDescription", 0);
		dig.addCallMethod(root + "/fileFolderReader/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/fileFolderReader/temporaryFileName", "setTemporaryFilename", 0);
		
		dig.addCallMethod(root + "/fileFolderReader/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/fileFolderReader/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/fileFolderReader/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/fileFolderReader/separator", "setCsvSeparator", 0);
		dig.addCallMethod(root + "/fileFolderReader/encoding", "setEncoding", 0);

		dig.addCallMethod(root + "/fileFolderReader/fileNamePattern", "setFileNamePattern", 0);
		dig.addCallMethod(root + "/fileFolderReader/folderPath", "setFolderPath", 0);
		dig.addCallMethod(root + "/fileFolderReader/fileType", "setFileType", 0);
		dig.addCallMethod(root + "/fileFolderReader/xlsSheetName", "setXlsSheetName", 0);
		dig.addCallMethod(root + "/fileFolderReader/xmlRootTag", "setXmlRootTag", 0);
		dig.addCallMethod(root + "/fileFolderReader/xmlRowTag", "setXmlRowTag", 0);
		
		dig.addSetNext(root + "/fileFolderReader", "addTransformation");
		
		/*
		 * FMDTInput
		 */
		dig.addObjectCreate(root + "/fmdtInput", FMDTInput.class);
		dig.addCallMethod(root + "/fmdtInput/name", "setName", 0);
		dig.addCallMethod(root + "/fmdtInput/description", "setDescription", 0);
		dig.addCallMethod(root + "/fmdtInput/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/fmdtInput/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/fmdtInput/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/fmdtInput/positionY", "setPositionY", 0);
		
		dig.addCallMethod(root + "/fmdtInput/connectionName", "setConnectionName", 0);
		dig.addCallMethod(root + "/fmdtInput/repositoryItemId", "setRepositoryItemId", 0);
		dig.addCallMethod(root + "/fmdtInput/businessModelName", "setBusinessModelName", 0);
		dig.addCallMethod(root + "/fmdtInput/businessPackageName", "setBusinessPackageName", 0);
		
		
		dig.addCallMethod(root + "/fmdtInput/prompt", "setPromptValue", 2);
		dig.addCallParam(root + "/fmdtInput/prompt/promptName", 0);
		dig.addCallParam(root + "/fmdtInput/prompt/promptValue", 1);
		
		
		dig.addCallMethod(root + "/fmdtInput/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/fmdtInput/separator", "setSeparator", 0);
		dig.addSetNext(root + "/fmdtInput", "addTransformation");
		
		
		
		/*
		 * FMDTOutput
		 */
		dig.addObjectCreate(root + "/fmdtOutput", FMDTOutput.class);
		dig.addCallMethod(root + "/fmdtOutput/name", "setName", 0);
		dig.addCallMethod(root + "/fmdtOutput/description", "setDescription", 0);
		dig.addCallMethod(root + "/fmdtOutput/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/fmdtOutput/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/fmdtOutput/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/fmdtOutput/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/fmdtOutput/repositoryItemId", "setRepositoryItemId", 0);
		dig.addCallMethod(root + "/fmdtOutput/businessModelName", "setBusinessModelName", 0);
		dig.addCallMethod(root + "/fmdtOutput/businessPackageName", "setBusinessPackageName", 0);
		
		dig.addCallMethod(root + "/fmdtOutput/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/fmdtOutput/separator", "setSeparator", 0);
		dig.addSetNext(root + "/fmdtOutput", "addTransformation");

		
		
		/*
		 * Lookup
		 */
		dig.addObjectCreate(root + "/lookup", Lookup.class);
		dig.addCallMethod(root + "/lookup/name", "setName", 0);
		dig.addCallMethod(root + "/lookup/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/lookup/temporaryFileName", "setTemporaryFilename", 0);
		dig.addCallMethod(root + "/lookup/firstInput", "setFirstInputName", 0);
		dig.addCallMethod(root + "/lookup/description", "setDescription", 0);
		dig.addCallMethod(root + "/lookup/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/lookup/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/lookup/masterName", "setMasterInput", 0);
		
		dig.addCallMethod(root + "/lookup/inputMap", "createBufferMapping", 2);
		dig.addCallParam(root + "/lookup/inputMap/indice", 0);
		dig.addCallParam(root + "/lookup/inputMap/value", 1);
		
		dig.addCallMethod(root + "/lookup/inputMappingName", "createBufferMappingName", 2);
		dig.addCallParam(root + "/lookup/inputMappingName/inputName", 0);
		dig.addCallParam(root + "/lookup/inputMappingName/outputName", 1);
		
		dig.addCallMethod(root + "/lookup/removeRowsWithoutMatch", "setRemoveRowsWithoutLookupMatching" , 0);
		dig.addCallMethod(root + "/lookup/trashRowsWithoutMatch", "setTrashRowsWithoutLookupMatching", 0);
		dig.addCallMethod(root + "/lookup/trashOuput", "setTrashTransformation", 0);
		
		
		dig.addSetNext(root + "/lookup", "addTransformation");

		/*
		 *SQLLookup 
		 */
		
		dig.addObjectCreate(root + "/sqlLookup", SqlLookup.class);
		dig.addCallMethod(root + "/sqlLookup/name", "setName", 0);
		dig.addCallMethod(root + "/sqlLookup/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/sqlLookup/temporaryFileName", "setTemporaryFilename", 0);
		dig.addCallMethod(root + "/sqlLookup/firstInput", "setFirstInputName", 0);
		dig.addCallMethod(root + "/sqlLookup/description", "setDescription", 0);
		dig.addCallMethod(root + "/sqlLookup/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/sqlLookup/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/sqlLookup/masterName", "setMasterInput", 0);
		dig.addCallMethod(root + "/sqlLookup/inputMappingName", "createBufferMappingName", 2);
		dig.addCallParam(root + "/sqlLookup/inputMappingName/inputName", 0);
		dig.addCallParam(root + "/sqlLookup/inputMappingName/outputName", 1);
		dig.addCallMethod(root + "/sqlLookup/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/sqlLookup/definition", "setDefinition", 0);

		dig.addCallParam(root + "/sqlLookup/inputMap/value", 1);

		
		dig.addCallMethod(root + "/sqlLookup/removeRowsWithoutMatch", "setRemoveRowsWithoutLookupMatching" , 0);
		dig.addCallMethod(root + "/sqlLookup/trashRowsWithoutMatch", "setTrashRowsWithoutLookupMatching", 0);
		dig.addCallMethod(root + "/sqlLookup/trashOuput", "setTrashTransformation", 0);
		
		
		dig.addSetNext(root + "/sqlLookup", "addTransformation");
		
		/*
		 * FileOutputXML
		 */
		dig.addObjectCreate(root + "/fileOutputXML", FileOutputXML.class);
		dig.addCallMethod(root + "/fileOutputXML/name", "setName", 0);
		dig.addCallMethod(root + "/fileOutputXML/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/fileOutputXML/temporaryFileName", "setTemporaryFilename", 0);
		dig.addCallMethod(root + "/fileOutputXML/encoding", "setEncoding", 0);
		dig.addCallMethod(root + "/fileOutputXML/description", "setDescription", 0);
		dig.addCallMethod(root + "/fileOutputXML/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/fileOutputXML/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/fileOutputXML/delete", "setDelete", 0);
		dig.addCallMethod(root + "/fileOutputXML/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/fileOutputXML/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/fileOutputXML/rootTag", "setRootTag", 0);
		dig.addCallMethod(root + "/fileOutputXML/rowTag", "setRowTag", 0);
		dig.addCallMethod(root + "/fileOutputXML/encrypting", "setEncrypting", 0);
		dig.addCallMethod(root + "/fileOutputXML/publicKeyPath", "setPublicKeyPath", 0);
		dig.addCallMethod(root + "/fileOutputXML/fromXSD", "setFromXSD", 0);
		dig.addCallMethod(root + "/fileOutputXML/xsdFilePath", "setXsdFilePath", 0);
		dig.addCallMethod(root + "/fileOutputXML/rootElement", "setRootElementFromDigester", 0);
		dig.addSetNext(root + "/fileOutputXML", "addTransformation");
		
		/*
		 * FileInputXML
		 */
		dig.addObjectCreate(root + "/fileInputXML", FileInputXML.class);
		dig.addCallMethod(root + "/fileInputXML/name", "setName", 0);
		dig.addCallMethod(root + "/fileInputXML/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/fileInputXML/temporaryFileName", "setTemporaryFilename", 0);
		dig.addCallMethod(root + "/fileInputXML/encoding", "setEncoding", 0);
		dig.addCallMethod(root + "/fileInputXML/description", "setDescription", 0);
		dig.addCallMethod(root + "/fileInputXML/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/fileInputXML/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/fileInputXML/fromUrl", "setFromUrl", 0);
		dig.addCallMethod(root + "/fileInputXML/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/fileInputXML/rootTag", "setRootTag", 0);
		dig.addCallMethod(root + "/fileInputXML/rowTag", "setRowTag", 0);
		dig.addCallMethod(root + "/fileInputXML/encrypting", "setEncrypting", 0);
		dig.addCallMethod(root + "/fileInputXML/privateKeyPath", "setPrivateKeyPath", 0);
		dig.addCallMethod(root + "/fileInputXML/password", "setPassword", 0);
		dig.addCallMethod(root + "/fileInputXML/fromXSD", "setFromXSD", 0);
		dig.addCallMethod(root + "/fileInputXML/xsdFilePath", "setXsdFilePath", 0);
		dig.addCallMethod(root + "/fileInputXML/rootElement", "setRootElementFromDigester", 0);
		dig.addCallMethod(root + "/fileInputXML/defaultAttributeName", "setDefaultAttributeName", 0);
		dig.addSetNext(root + "/fileInputXML", "addTransformation");

		
		
		/*
		 * FileOutputXLS
		 */
		dig.addObjectCreate(root + "/fileOutputXLS", FileOutputXLS.class);
		dig.addCallMethod(root + "/fileOutputXLS/name", "setName", 0);
		dig.addCallMethod(root + "/fileOutputXLS/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/fileOutputXLS/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/fileOutputXLS/description", "setDescription", 0);
		dig.addCallMethod(root + "/fileOutputXLS/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/fileOutputXLS/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/fileOutputXLS/delete", "setDelete", 0);
		dig.addCallMethod(root + "/fileOutputXLS/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/fileOutputXLS/sheetName", "setSheetName", 0);
		dig.addCallMethod(root + "/fileOutputXLS/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/fileOutputXLS/useMdm", "setUseMdm", 0);
		dig.addCallMethod(root + "/fileOutputXLS/supplierId", "setSupplierId", 0);
		dig.addCallMethod(root + "/fileOutputXLS/contractId", "setContractId", 0);
		dig.addSetNext(root + "/fileOutputXLS", "addTransformation");
		
		
		
		
		
		/*
		 * MultiFolderXLS
		 */
		dig.addObjectCreate(root + "/multiOutputXLS", MultiFolderXLS.class);
		dig.addCallMethod(root + "/multiOutputXLS/name", "setName", 0);
		dig.addCallMethod(root + "/multiOutputXLS/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/multiOutputXLS/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/multiOutputXLS/description", "setDescription", 0);
		dig.addCallMethod(root + "/multiOutputXLS/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/multiOutputXLS/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/multiOutputXLS/delete", "setDelete", 0);
		dig.addCallMethod(root + "/multiOutputXLS/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/multiOutputXLS/append", "setAppend", 0);
		
		dig.addCallMethod(root + "/multiOutputXLS/inputMapping", "adaptInput", 2);
		dig.addCallParam(root + "/multiOutputXLS/inputMapping/inputName", 0);
		dig.addCallParam(root + "/multiOutputXLS/inputMapping/sheetName", 1);
		
		
		dig.addSetNext(root + "/multiOutputXLS", "addTransformation");
		
		/*
		 * FileInputXLS
		 */
		dig.addObjectCreate(root + "/fileInputXLS", FileInputXLS.class);
		dig.addCallMethod(root + "/fileInputXLS/name", "setName", 0);
		dig.addCallMethod(root + "/fileInputXLS/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/fileInputXLS/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/fileInputXLS/sheetName", "setSheetName", 0);
		dig.addCallMethod(root + "/fileInputXLS/description", "setDescription", 0);
		dig.addCallMethod(root + "/fileInputXLS/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/fileInputXLS/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/fileInputXLS/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/fileInputXLS/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/fileInputXLS/skipFirstRow", "setSkipFirstRow", 0);
		dig.addSetNext(root + "/fileInputXLS", "addTransformation");
		
		/*
		 * FileInputShape
		 */
		dig.addObjectCreate(root + "/fileInputShape", FileInputShape.class);
		dig.addCallMethod(root + "/fileInputShape/name", "setName", 0);
		dig.addCallMethod(root + "/fileInputShape/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/fileInputShape/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/fileInputShape/description", "setDescription", 0);
		dig.addCallMethod(root + "/fileInputShape/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/fileInputShape/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/fileInputShape/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/fileInputShape/definition", "setDefinition", 0);
		
		dig.addCallMethod(root + "/fileInputShape/saveInNorparena", "setSaveInNorparena", 0);
		dig.addCallMethod(root + "/fileInputShape/fromNewMap", "setFromNewMap", 0);
		dig.addCallMethod(root + "/fileInputShape/newMapName", "setNewMapName", 0);
		dig.addCallMethod(root + "/fileInputShape/existingMapId", "setExistingMapId", 0);
		dig.addCallMethod(root + "/fileInputShape/useMdm", "setUseMdm", 0);
		dig.addCallMethod(root + "/fileInputShape/supplierId", "setSupplierId", 0);
		dig.addCallMethod(root + "/fileInputShape/contractId", "setContractId", 0);
		
		dig.addSetNext(root + "/fileInputShape", "addTransformation");
		
		
		/*
		 * FreemetricsKPI
		 */
		dig.addObjectCreate(root + "/freemetricsKPI", FreemetricsKPI.class);
		dig.addCallMethod(root + "/freemetricsKPI/name", "setName", 0);
		dig.addCallMethod(root + "/freemetricsKPI/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/freemetricsKPI/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/freemetricsKPI/description", "setDescription", 0);
		dig.addCallMethod(root + "/freemetricsKPI/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/freemetricsKPI/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/freemetricsKPI/serverRef", "setServer", 0);
		dig.addSetNext(root + "/freemetricsKPI", "addTransformation");


		
		/*
		 * Filter
		 */
		dig.addObjectCreate(root + "/filter", Filter.class);
		dig.addCallMethod(root + "/filter/name", "setName", 0);
		dig.addCallMethod(root + "/filter/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/filter/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/filter/description", "setDescription", 0);
		dig.addCallMethod(root + "/filter/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/filter/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/filter/exclusive", "setExclusive", 0);
		dig.addCallMethod(root + "/filter/trashRef", "setTrashTransformation", 0);
			
			dig.addObjectCreate(root + "/filter/condition", Condition.class);
			dig.addCallMethod(root + "/filter/condition/elementNumber", "setStreamElementNumber", 0);
			dig.addCallMethod(root + "/filter/condition/elementName", "setStreamElementName", 0);
			dig.addCallMethod(root + "/filter/condition/operator", "setOperator", 0);
			dig.addCallMethod(root + "/filter/condition/outputRef", "setOutput", 0);
			dig.addCallMethod(root + "/filter/condition/value", "setValue", 0);
			dig.addCallMethod(root + "/filter/condition/logical", "setLogical", 0);
			dig.addSetNext(root + "/filter/condition", "addCondition");
		dig.addSetNext(root + "/filter", "addTransformation");
		
		
		/*
		 * Aggregation
		 */
		dig.addObjectCreate(root + "/aggregation", AggregateTransformation.class);
		dig.addCallMethod(root + "/aggregation/name", "setName", 0);
		dig.addCallMethod(root + "/aggregation/nullMode", "setNullMode", 0);
		dig.addCallMethod(root + "/aggregation/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/aggregation/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/aggregation/description", "setDescription", 0);
		dig.addCallMethod(root + "/aggregation/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/aggregation/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/aggregation/groupByIndex", "addGroupBy", 0);
		dig.addCallMethod(root + "/aggregation/groupByNames", "addGroupByNames", 0);
		
			dig.addCallMethod(root + "/aggregation/aggregate", "addAggregate", 2);
			dig.addCallParam(root + "/aggregation/aggregate/columnIndex", 0);
			dig.addCallParam(root + "/aggregation/aggregate/function", 1);
		
			dig.addCallMethod(root + "/aggregation/aggregateNew", "addAggregateNew", 2);
			dig.addCallParam(root + "/aggregation/aggregateNew/columnName", 0);
			dig.addCallParam(root + "/aggregation/aggregateNew/function", 1);
		dig.addSetNext(root + "/aggregation", "addTransformation");

		
		
		/*
		 * UnduplicateRows
		 */
		dig.addObjectCreate(root + "/unduplicateRows", UnduplicateRows.class);
		dig.addCallMethod(root + "/unduplicateRows/name", "setName", 0);
		dig.addCallMethod(root + "/unduplicateRows/testedField", "addFieldBuffer", 0);
		dig.addCallMethod(root + "/unduplicateRows/testedFieldName", "addFieldName", 0);
		dig.addCallMethod(root + "/unduplicateRows/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/unduplicateRows/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/unduplicateRows/description", "setDescription", 0);
		dig.addCallMethod(root + "/unduplicateRows/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/unduplicateRows/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/unduplicateRows/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/unduplicateRows/trashRef", "setTrashTransformation", 0);
		dig.addSetNext(root + "/unduplicateRows", "addTransformation");
		
		
		
		
		/*
		 * SelectDistinct
		 */
		dig.addObjectCreate(root + "/selectDistinct", SelectDistinct.class);
		dig.addCallMethod(root + "/selectDistinct/name", "setName", 0);
		dig.addCallMethod(root + "/selectDistinct/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/selectDistinct/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/selectDistinct/description", "setDescription", 0);
		dig.addCallMethod(root + "/selectDistinct/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/selectDistinct/positionY", "setPositionY", 0);
		dig.addSetNext(root + "/selectDistinct", "addTransformation");

		
		
		/*
		 * FieldSplitter
		 */
		dig.addObjectCreate(root + "/fieldSpliter", FieldSplitter.class);
		dig.addCallMethod(root + "/fieldSpliter/name", "setName", 0);
		dig.addCallMethod(root + "/fieldSpliter/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/fieldSpliter/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/fieldSpliter/description", "setDescription", 0);
		dig.addCallMethod(root + "/fieldSpliter/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/fieldSpliter/positionY", "setPositionY", 0);

		dig.addObjectCreate(root + "/fieldSpliter/split", SplitedField.class);
		dig.addCallMethod(root + "/fieldSpliter/split/splitSequence", "setSpliter", 0);
		dig.addCallMethod(root + "/fieldSpliter/split/streamElementIndex", "setSplitedIndex", 0);
		dig.addCallMethod(root + "/fieldSpliter/split/colName", "addColumn", 0);
		
		dig.addSetNext(root + "/fieldSpliter/split", "addSpliter");
		
		dig.addSetNext(root + "/fieldSpliter", "addTransformation");
		
		/*
		 * RowsFieldSplitter
		 */
		dig.addObjectCreate(root + "/rowsFieldSpliter", RowsFieldSplitter.class);
		dig.addCallMethod(root + "/rowsFieldSpliter/name", "setName", 0);
		dig.addCallMethod(root + "/rowsFieldSpliter/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/rowsFieldSpliter/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/rowsFieldSpliter/description", "setDescription", 0);
		dig.addCallMethod(root + "/rowsFieldSpliter/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/rowsFieldSpliter/positionY", "setPositionY", 0);

		dig.addCallMethod(root + "/rowsFieldSpliter/splitSequence", "setSplitSequence", 0);
		dig.addCallMethod(root + "/rowsFieldSpliter/inputFieldIndexToSplit", "setInputFieldIndexToSplit", 0);
		dig.addCallMethod(root + "/rowsFieldSpliter/keepOrignalFieldInOutput", "setKeepOriginalFieldInOuput", 0);
		dig.addCallMethod(root + "/rowsFieldSpliter/trim", "setTrim", 0);
		
		dig.addSetNext(root + "/rowsFieldSpliter", "addTransformation");

		
		
		/*
		 * Calculation
		 */
		dig.addObjectCreate(root + "/calculation", Calculation.class);
		dig.addCallMethod(root + "/calculation/name", "setName", 0);
		dig.addCallMethod(root + "/calculation/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/calculation/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/calculation/description", "setDescription", 0);
		dig.addCallMethod(root + "/calculation/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/calculation/positionY", "setPositionY", 0);
		
		dig.addObjectCreate(root + "/calculation/script", Script.class);
		dig.addCallMethod(root + "/calculation/script/name", "setName", 0);
		dig.addCallMethod(root + "/calculation/script/expression", "setScriptFunction", 0);
		dig.addCallMethod(root + "/calculation/script/type", "setType", 0);
		dig.addSetNext(root + "/calculation/script", "addScript");
		
		
		
		dig.addSetNext(root + "/calculation", "addTransformation");
		
		
		
		
		/*
		 * Ranging
		 */
		dig.addObjectCreate(root + "/rangingTransformation", RangingTransformation.class);
		dig.addCallMethod(root + "/rangingTransformation/name", "setName", 0);
		dig.addCallMethod(root + "/rangingTransformation/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/rangingTransformation/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/rangingTransformation/description", "setDescription", 0);
		dig.addCallMethod(root + "/rangingTransformation/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/rangingTransformation/positionY", "setPositionY", 0);

		dig.addCallMethod(root + "/rangingTransformation/targetIndex", "setTarget", 0);
		dig.addCallMethod(root + "/rangingTransformation/outputFieldName", "setOutputFieldName", 0);
		dig.addCallMethod(root + "/rangingTransformation/type", "setType", 0);
		
		
		dig.addObjectCreate(root + "/rangingTransformation/range", Range.class);
		dig.addCallMethod(root + "/rangingTransformation/range/firstValue", "setFirstValue", 0);
		dig.addCallMethod(root + "/rangingTransformation/range/secondValue", "setSecondValue", 0);
		dig.addCallMethod(root + "/rangingTransformation/range/intervalType", "setIntervalType", 0);
		dig.addCallMethod(root + "/rangingTransformation/range/output", "setOutput", 0);
		
		dig.addSetNext(root + "/rangingTransformation/range", "addRange");
		
		dig.addSetNext(root + "/rangingTransformation", "addTransformation");
		
		/*
		 * GeoFilter
		 */
		dig.addObjectCreate(root + "/geoFilter", GeoFilter.class);
		dig.addCallMethod(root + "/geoFilter/name", "setName", 0);
		dig.addCallMethod(root + "/geoFilter/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/geoFilter/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/geoFilter/description", "setDescription", 0);
		dig.addCallMethod(root + "/geoFilter/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/geoFilter/positionY", "setPositionY", 0);

		dig.addCallMethod(root + "/geoFilter/targetIndex", "setTarget", 0);
		dig.addCallMethod(root + "/geoFilter/outputFieldName", "setOutputFieldName", 0);
		dig.addCallMethod(root + "/geoFilter/type", "setType", 0);
		
		dig.addCallMethod(root + "/geoFilter/geoElementName", "setGeoElementName", 0);
		dig.addCallMethod(root + "/geoFilter/geoShape", "setGeoShape", 0);
		
		dig.addCallMethod(root + "/geoFilter/trashOuput", "setTrashTransformation", 0);
		
		dig.addObjectCreate(root + "/geoFilter/condition", GeoCondition.class);
		dig.addCallMethod(root + "/geoFilter/condition/targetName", "setTargetName", 0);
		dig.addCallMethod(root + "/geoFilter/condition/inputKml", "setInputKml", 0);
		dig.addCallMethod(root + "/geoFilter/condition/placeMarkName", "setPlaceMarkName", 0);
		dig.addCallMethod(root + "/geoFilter/condition/geoShape", "setGeoShape", 0);
		
		dig.addSetNext(root + "/geoFilter/condition", "addCondition");
		
		dig.addObjectCreate(root + "/geoFilter/polygon", Polygon.class);
		dig.addCallMethod(root + "/geoFilter/polygon/name", "setName", 0);
		
		dig.addObjectCreate(root + "/geoFilter/polygon/point", PointGeo.class);
		dig.addCallMethod(root + "/geoFilter/polygon/point/x", "setX", 0);
		dig.addCallMethod(root + "/geoFilter/polygon/point/y", "setY", 0);
		dig.addSetNext(root + "/geoFilter/polygon/point", "addPoint");
		
		dig.addSetNext(root + "/geoFilter/polygon", "addPolygon");
		
		dig.addSetNext(root + "/geoFilter", "addTransformation");
		
		/*
		 * GeoDispatch
		 */
		dig.addObjectCreate(root + "/geoDispatch", GeoDispatch.class);
		dig.addCallMethod(root + "/geoDispatch/name", "setName", 0);
		dig.addCallMethod(root + "/geoDispatch/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/geoDispatch/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/geoDispatch/description", "setDescription", 0);
		dig.addCallMethod(root + "/geoDispatch/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/geoDispatch/positionY", "setPositionY", 0);

		dig.addCallMethod(root + "/geoDispatch/targetIndex", "setTarget", 0);
		dig.addCallMethod(root + "/geoDispatch/outputFieldName", "setOutputFieldName", 0);
		dig.addCallMethod(root + "/geoDispatch/type", "setType", 0);
		
		dig.addCallMethod(root + "/geoDispatch/trashOuput", "setTrashTransformation", 0);
		
		dig.addCallMethod(root + "/geoDispatch/onlyOneColumnGeoloc", "setOnlyOneColumnGeoloc", 0);
		dig.addCallMethod(root + "/geoDispatch/latitudeIndex", "setLatitudeIndex", 0);
		dig.addCallMethod(root + "/geoDispatch/longitudeIndex", "setLongitudeIndex", 0);

		dig.addCallMethod(root + "/geoDispatch/placemarkIdIndex", "setPlacemarkIdIndex", 0);
		dig.addCallMethod(root + "/geoDispatch/inputReferenceLatitudeIndex", "setInputReferenceLatitudeIndex", 0);
		dig.addCallMethod(root + "/geoDispatch/inputReferenceLongitudeIndex", "setInputReferenceLongitudeIndex", 0);
		dig.addCallMethod(root + "/geoDispatch/defaultPlacemarkId", "setDefaultPlacemarkId", 0);
		
		dig.addSetNext(root + "/geoDispatch", "addTransformation");
		
		/*
		 * mergeStreams
		 */
		dig.addObjectCreate(root + "/mergeStreams", MergeStreams.class);
		dig.addCallMethod(root + "/mergeStreams/name", "setName", 0);
		dig.addCallMethod(root + "/mergeStreams/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/mergeStreams/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/mergeStreams/description", "setDescription", 0);
		dig.addCallMethod(root + "/mergeStreams/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/mergeStreams/positionY", "setPositionY", 0);
		dig.addSetNext(root + "/mergeStreams", "addTransformation");
		
		
		
		/*
		 * mergeStreams
		 */
		dig.addObjectCreate(root + "/sortStream", SortTransformation.class);
		dig.addCallMethod(root + "/sortStream/name", "setName", 0);
		dig.addCallMethod(root + "/sortStream/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/sortStream/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/sortStream/description", "setDescription", 0);
		dig.addCallMethod(root + "/sortStream/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/sortStream/positionY", "setPositionY", 0);
		
		dig.addCallMethod(root + "/sortStream/sort", "setOrder", 2);
		dig.addCallParam(root + "/sortStream/sort/indice", 0);
		dig.addCallParam(root + "/sortStream/sort/type", 1);
		
		dig.addCallMethod(root + "/sortStream/sortElements", "setOrderElements", 2);
		dig.addCallParam(root + "/sortStream/sortElements/columnSort", 0);
		dig.addCallParam(root + "/sortStream/sortElements/typeSort", 1);

		dig.addSetNext(root + "/sortStream", "addTransformation");
		
		
		/*
		 * Sequence
		 */
		dig.addObjectCreate(root + "/sequence", Sequence.class);
		dig.addCallMethod(root + "/sequence/name", "setName", 0);
		dig.addCallMethod(root + "/sequence/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/sequence/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/sequence/description", "setDescription", 0);
		dig.addCallMethod(root + "/sequence/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/sequence/positionY", "setPositionY", 0);
		
		dig.addCallMethod(root + "/sequence/minFieldIndex", "setMinField", 0);
		dig.addCallMethod(root + "/sequence/minValue", "setMinValue", 0);
		dig.addCallMethod(root + "/sequence/maxValue", "setMaxValue", 0);
		dig.addCallMethod(root + "/sequence/step", "setStep", 0);
		dig.addCallMethod(root + "/sequence/fieldName", "setFieldName", 0);
		dig.addSetNext(root + "/sequence", "addTransformation");
		
		/*
		 * SurrogateKey
		 */
		dig.addObjectCreate(root + "/surrogateKey", SurrogateKey.class);
		dig.addCallMethod(root + "/surrogateKey/name", "setName", 0);
		dig.addCallMethod(root + "/surrogateKey/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/surrogateKey/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/surrogateKey/description", "setDescription", 0);
		dig.addCallMethod(root + "/surrogateKey/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/surrogateKey/positionY", "setPositionY", 0);
		
		dig.addCallMethod(root + "/surrogateKey/fieldName", "setFieldName", 0);
		dig.addCallMethod(root + "/surrogateKey/keyIndex", "addFieldKeyIndex", 0);
		dig.addCallMethod(root + "/surrogateKey/keyName", "addFieldKeyName", 0);
		
		dig.addSetNext(root + "/surrogateKey", "addTransformation");
		
		/*
		 * SubTransformation
		 */
		dig.addObjectCreate(root + "/subTransformation", SubTransformation.class);
		dig.addCallMethod(root + "/subTransformation/name", "setName", 0);
		dig.addCallMethod(root + "/subTransformation/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/subTransformation/temporaryFileName", "setTemporaryFilename", 0);
		
		dig.addCallMethod(root + "/subTransformation/description", "setDescription", 0);
		
		dig.addCallMethod(root + "/subTransformation/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/subTransformation/positionY", "setPositionY", 0);
		
		dig.addCallMethod(root + "/subTransformation/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/subTransformation/finalStep", "setFinalStep", 0);
//		dig.addCallMethod(root + "/subTransformation/server-ref", "setServer", 0);
		dig.addCallMethod(root + "/subTransformation/mapping", "map", 2);
		dig.addCallParam(root + "/subTransformation/mapping/parmeterName", 0);
		dig.addCallParam(root + "/subTransformation/mapping/fieldNumber", 1);

		dig.addSetNext(root + "/subTransformation", "addTransformation");
		
		
		
		/*
		 * SlowChangingDimension2
		 */
		dig.addObjectCreate(root + "/slowChangingDimension2", SlowChangingDimension2.class);
		dig.addCallMethod(root + "/slowChangingDimension2/name", "setName", 0);
		dig.addCallMethod(root + "/slowChangingDimension2/description", "setDescription", 0);
		dig.addCallMethod(root + "/slowChangingDimension2/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/slowChangingDimension2/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/slowChangingDimension2/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/slowChangingDimension2/temporaryFileName", "setTemporaryFilename", 0);
		
		dig.addCallMethod(root + "/slowChangingDimension2/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/slowChangingDimension2/definition", "setDefinition", 0);
		
		dig.addCallMethod(root + "/slowChangingDimension2/sequenceType", "setSequenceType", 0);
		dig.addCallMethod(root + "/slowChangingDimension2/maxYear", "setMaximumYear", 0);
		dig.addCallMethod(root + "/slowChangingDimension2/inactiveVal", "setInactiveValue", 0);
		dig.addCallMethod(root + "/slowChangingDimension2/activeVal", "setActiveValue", 0);
		
		dig.addCallMethod(root + "/slowChangingDimension2/inputDateFieldIndex", "createBufferInputDateIndex", 0);
		dig.addCallMethod(root + "/slowChangingDimension2/targetKeyIndex", "createBufferTargetKeyIndex", 0);
		dig.addCallMethod(root + "/slowChangingDimension2/targetStartDateIndex", "createBufferTargetStartDateIndex", 0);
		dig.addCallMethod(root + "/slowChangingDimension2/targetStopDateIndex", "createBufferTargetStopDateIndex", 0);
		dig.addCallMethod(root + "/slowChangingDimension2/targetVersionIndex", "createBufferTargetVersionIndex", 0);
		dig.addCallMethod(root + "/slowChangingDimension2/targetActiveIndex", "createBufferTargetActiveIndex", 0);
		dig.addCallMethod(root + "/slowChangingDimension2/scdType", "setScdType", 0);
		
		dig.addCallMethod(root + "/slowChangingDimension2/inputDateField", "createInputDateField", 0);
		dig.addCallMethod(root + "/slowChangingDimension2/targetKeyField", "createTargetKeyField", 0);
		dig.addCallMethod(root + "/slowChangingDimension2/targetStartDateField", "createTargetStartDateField", 0);
		dig.addCallMethod(root + "/slowChangingDimension2/targetStopDateField", "createTargetStopDateField", 0);
		dig.addCallMethod(root + "/slowChangingDimension2/targetVersionField", "createTargetVersionField", 0);
		dig.addCallMethod(root + "/slowChangingDimension2/targetActiveField", "createTargetActiveField", 0);
		dig.addCallMethod(root + "/slowChangingDimension2/scdType", "setScdType", 0);
		
		
		
		
		dig.addCallMethod(root + "/slowChangingDimension2/mappingKey", "createBufferKeys", 2);
		dig.addCallParam(root + "/slowChangingDimension2/mappingKey/index", 0);
		dig.addCallParam(root + "/slowChangingDimension2/mappingKey/inputIndex", 1);
		
		dig.addCallMethod(root + "/slowChangingDimension2/mappingKeyName", "createBufferKeyName", 2);
		dig.addCallParam(root + "/slowChangingDimension2/mappingKeyName/inputName", 0);
		dig.addCallParam(root + "/slowChangingDimension2/mappingKeyName/outputName", 1);
		
		
		dig.addCallMethod(root + "/slowChangingDimension2/mappingFields", "createBufferFields", 2);
		dig.addCallParam(root + "/slowChangingDimension2/mappingFields/index", 0);
		dig.addCallParam(root + "/slowChangingDimension2/mappingFields/inputIndex", 1);
		
		dig.addCallMethod(root + "/slowChangingDimension2/mappingFieldsName", "createBufferFieldName", 2);
		dig.addCallParam(root + "/slowChangingDimension2/mappingFieldsName/inputName", 0);
		dig.addCallParam(root + "/slowChangingDimension2/mappingFieldsName/outputName", 1);
		
		dig.addCallMethod(root + "/slowChangingDimension2/ignoreFields", "createIgnoreField", 2);
		dig.addCallParam(root + "/slowChangingDimension2/ignoreFields/inputName", 0);
		dig.addCallParam(root + "/slowChangingDimension2/ignoreFields/ignore", 1);
		
		dig.addCallMethod(root + "/slowChangingDimension2/trashTransformation", "setTrashTransformation", 0);
		dig.addSetNext(root + "/slowChangingDimension2", "addTransformation");

		
		/*
		 * insertOrUpdate
		 */
		dig.addObjectCreate(root + "/insertOrUpdate", InsertOrUpdate.class);
		dig.addCallMethod(root + "/insertOrUpdate/name", "setName", 0);
		dig.addCallMethod(root + "/insertOrUpdate/description", "setDescription", 0);
		dig.addCallMethod(root + "/insertOrUpdate/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/insertOrUpdate/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/insertOrUpdate/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/insertOrUpdate/temporaryFileName", "setTemporaryFilename", 0);
		
		dig.addCallMethod(root + "/insertOrUpdate/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/insertOrUpdate/definition", "setDefinition", 0);
		
		dig.addCallMethod(root + "/insertOrUpdate/trashTransformation", "setTrashTransformation", 0);
			
		dig.addCallMethod(root + "/insertOrUpdate/mappingKey", "createBufferKeys", 2);
		dig.addCallParam(root + "/insertOrUpdate/mappingKey/index", 0);
		dig.addCallParam(root + "/insertOrUpdate/mappingKey/inputIndex", 1);
			
		dig.addCallMethod(root + "/insertOrUpdate/mappingKeyName", "createBufferKeyName", 2);
		dig.addCallParam(root + "/insertOrUpdate/mappingKeyName/inputName", 0);
		dig.addCallParam(root + "/insertOrUpdate/mappingKeyName/outputName", 1);
		
		
		dig.addCallMethod(root + "/insertOrUpdate/mappingFields", "createBufferFields", 2);
		dig.addCallParam(root + "/insertOrUpdate/mappingFields/index", 0);
		dig.addCallParam(root + "/insertOrUpdate/mappingFields/inputIndex", 1);
		
		
		dig.addCallMethod(root + "/insertOrUpdate/mappingFieldsName", "createBufferFieldName", 2);
		dig.addCallParam(root + "/insertOrUpdate/mappingFieldsName/inputName", 0);
		dig.addCallParam(root + "/insertOrUpdate/mappingFieldsName/outputName", 1);
		
		dig.addCallMethod(root + "/insertOrUpdate/ignoreFields", "createIgnoreField", 2);
		dig.addCallParam(root + "/insertOrUpdate/ignoreFields/inputName", 0);
		dig.addCallParam(root + "/insertOrUpdate/ignoreFields/ignore", 1);
		
				
		dig.addSetNext(root + "/insertOrUpdate", "addTransformation");

		
		
		/*
		 * GlobalDefinitionInput
		 */
		dig.addObjectCreate(root + "/globalInputDefinition", GlobalDefinitionInput.class);
		dig.addCallMethod(root + "/globalInputDefinition/name", "setName", 0);
		dig.addCallMethod(root + "/globalInputDefinition/description", "setDescription", 0);
		dig.addCallMethod(root + "/globalInputDefinition/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/globalInputDefinition/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/globalInputDefinition/width", "setWidth", 0);
		dig.addCallMethod(root + "/globalInputDefinition/height", "setHeight", 0);
		dig.addCallMethod(root + "/globalInputDefinition/customQuery", "setCustomQuery", 0);
		
		dig.addCallMethod(root + "/globalInputDefinition/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/globalInputDefinition/temporaryFileName", "setTemporaryFilename", 0);
		dig.addSetNext(root + "/globalInputDefinition", "addTransformation");


		/*
		 * KPIOutput
		 */
		dig.addObjectCreate(root + "/kpiOutput", KPIOutput.class);
		dig.addCallMethod(root + "/kpiOutput/name", "setName", 0);
		dig.addCallMethod(root + "/kpiOutput/description", "setDescription", 0);
		dig.addCallMethod(root + "/kpiOutput/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/kpiOutput/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/kpiOutput/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/kpiOutput/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/kpiOutput/temporaryFileName", "setTemporaryFilename", 0);
		dig.addCallMethod(root + "/kpiOutput/inputApplicationIndex", "setInputApplicationIndex",0);
		dig.addCallMethod(root + "/kpiOutput/inputDateIndex", "setInputDateIndex",0);
		dig.addCallMethod(root + "/kpiOutput/inputMetricIndex", "setInputMetricIndex",0);
		dig.addCallMethod(root + "/kpiOutput/inputValueIndex", "setInputValueIndex",0);
		dig.addCallMethod(root + "/kpiOutput/inputAssocIndex", "setInputAssocIndex",0);
		dig.addCallMethod(root + "/kpiOutput/dateFormat", "setDateFormat",0);
		dig.addCallMethod(root + "/kpiOutput/isMetricName", "setMetricName",0);
		dig.addCallMethod(root + "/kpiOutput/isApplicationName", "setApplicationName",0);
		dig.addCallMethod(root + "/kpiOutput/performUpdateOnExistingValues", "setPerformUdpateOnOldValues",0);
		dig.addSetNext(root + "/kpiOutput", "addTransformation");

		
		
		
		/*
		 * KPIList
		 */
		dig.addObjectCreate(root + "/kpiList", KPIList.class);
		dig.addCallMethod(root + "/kpiList/name", "setName", 0);
		dig.addCallMethod(root + "/kpiList/description", "setDescription", 0);
		dig.addCallMethod(root + "/kpiList/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/kpiList/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/kpiList/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/kpiList/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/kpiList/temporaryFileName", "setTemporaryFilename", 0);
		
		dig.addCallMethod(root + "/kpiList/metricNameFieldName", "setMetricNameFieldName",0);
		dig.addCallMethod(root + "/kpiList/associationIdFieldName", "setAssociationIdFieldName",0);
		
		dig.addCallMethod(root + "/kpiList/applicationNameFieldName", "setApplicationNameFieldName",0);
		
		
		dig.addCallMethod(root + "/kpiList/associationMetAppId", "addAssociationId",0);
		dig.addSetNext(root + "/kpiList", "addTransformation");

		

		/*
		 * AlterationStream
		 */
		dig.addObjectCreate(root + "/alterationStream", AlterationStream.class);
		dig.addCallMethod(root + "/alterationStream/name", "setName", 0);
		dig.addCallMethod(root + "/alterationStream/description", "setDescription", 0);
		dig.addCallMethod(root + "/alterationStream/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/alterationStream/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/alterationStream/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/alterationStream/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/alterationStream/performUpdateOnExistingValues", "setPerformUdpateOnOldValues", 0);
		
		dig.addObjectCreate(root + "/alterationStream/alteration", AlterationInfo.class);
		dig.addCallMethod(root + "/alterationStream/alteration/className", "setClassName", 0);
		dig.addCallMethod(root + "/alterationStream/alteration/format", "setFormat", 0);
		dig.addSetNext(root + "/alterationStream/alteration", "addAlterationInfo");
		
		dig.addSetNext(root + "/alterationStream", "addTransformation");
		

		/*
		 * Vanilla Google Analytics
		 */
		String googleAnalytics = root + "/vanillaAnalyticsGoogle";
		dig.addObjectCreate(root + "/vanillaAnalyticsGoogle", VanillaAnalyticsGoogle.class);
		dig.addCallMethod(googleAnalytics + "/name", "setName", 0);
		dig.addCallMethod(googleAnalytics + "/description", "setDescription", 0);
		dig.addCallMethod(googleAnalytics + "/positionX", "setPositionX", 0);
		dig.addCallMethod(googleAnalytics + "/positionY", "setPositionY", 0);
		dig.addCallMethod(googleAnalytics + "/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(googleAnalytics + "/temporaryFileName", "setTemporaryFilename", 0);
		dig.addCallMethod(googleAnalytics + "/username", "setUsername", 0);
		dig.addCallMethod(googleAnalytics + "/password", "setPassword", 0);
		dig.addCallMethod(googleAnalytics + "/tableId", "setTableId", 0);
		dig.addCallMethod(googleAnalytics + "/beginDate", "setBeginDate", 0);
		dig.addCallMethod(googleAnalytics + "/endDate", "setEndDate", 0);
		dig.addCallMethod(googleAnalytics + "/groupByDate", "setGroupByDate", 0);
		dig.addCallMethod(googleAnalytics + "/dimensions/dimension", "addDimension", 0);
		dig.addCallMethod(googleAnalytics + "/metrics/metric", "addMetric", 0);
		dig.addSetNext(googleAnalytics, "addTransformation");
		
		/*
		 * Vanilla comments extraction
		 */
		
		String commentsExtraction = root + "/vanillacommentextraction";
			dig.addObjectCreate(root + "/vanillacommentextraction", VanillaCommentExtraction.class);
			dig.addCallMethod(commentsExtraction + "/name", "setName", 0);
			dig.addCallMethod(commentsExtraction + "/description", "setDescription", 0);
			dig.addCallMethod(commentsExtraction + "/positionX", "setPositionX", 0);
			dig.addCallMethod(commentsExtraction + "/positionY", "setPositionY", 0);
			dig.addCallMethod(commentsExtraction + "/temporarySeparator", "setTemporarySpliterChar", 0);
			dig.addCallMethod(commentsExtraction + "/temporaryFileName", "setTemporaryFilename", 0);
			dig.addCallMethod(commentsExtraction + "/type", "setType", 0);
			dig.addCallMethod(commentsExtraction + "/itemId", "setItemId", 0);
			dig.addSetNext(commentsExtraction, "addTransformation");
			
		/*
		 * NagiosDB
		 */
		dig.addObjectCreate(root + "/nagiosDb", NagiosDb.class);
		dig.addCallMethod(root + "/nagiosDb/name", "setName", 0);
		dig.addCallMethod(root + "/nagiosDb/description", "setDescription", 0);
		dig.addCallMethod(root + "/nagiosDb/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/nagiosDb/positionY", "setPositionY", 0);
		dig.addCallMethod("*/container-ref", "setContainer", 0);
		dig.addCallMethod(root + "/nagiosDb/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/nagiosDb/request", "setRequest", 0);
		dig.addCallMethod(root + "/nagiosDb/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/nagiosDb/temporaryFileName", "setTemporaryFilename", 0);
		
		dig.addCallMethod(root + "/nagiosDb/outputVariables/variableInit", "addOutputVariable", 2);
		dig.addCallParam(root + "/nagiosDb/outputVariables/variableInit/fieldIndex", 0);
		dig.addCallParam(root + "/nagiosDb/outputVariables/variableInit/variableName", 1);
		
		dig.addSetNext(root + "/nagiosDb", "addTransformation");

		
		/*
		 * CassandraInputStream
		 */
		dig.addObjectCreate(root + "/cassandraInputStream", CassandraInputStream.class);
		dig.addCallMethod(root + "/cassandraInputStream/name", "setName", 0);
		dig.addCallMethod(root + "/cassandraInputStream/description", "setDescription", 0);
		dig.addCallMethod(root + "/cassandraInputStream/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/cassandraInputStream/positionY", "setPositionY", 0);
		dig.addCallMethod("*/container-ref", "setContainer", 0);
		dig.addCallMethod(root + "/cassandraInputStream/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/cassandraInputStream/cqlDefinition", "setCQLDefinition", 0);
		dig.addCallMethod(root + "/cassandraInputStream/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/cassandraInputStream/temporaryFileName", "setTemporaryFilename", 0);
		
		dig.addCallMethod(root + "/cassandraInputStream/outputVariables/variableInit", "addOutputVariable", 2);
		dig.addCallParam(root + "/cassandraInputStream/outputVariables/variableInit/fieldIndex", 0);
		dig.addCallParam(root + "/cassandraInputStream/outputVariables/variableInit/variableName", 1);

		dig.addCallMethod(root + "/cassandraInputStream/columnTypes/columnType", "addColumnType", 2);
		dig.addCallParam(root + "/cassandraInputStream/columnTypes/columnType/column", 0);
		dig.addCallParam(root + "/cassandraInputStream/columnTypes/columnType/value", 1);
		
		dig.addSetNext(root + "/cassandraInputStream", "addTransformation");
		
		
		/*
		 * CassandraOutputStreams
		 */
		dig.addObjectCreate(root + "/cassandraOutputStream", CassandraOutputStream.class);
		dig.addCallMethod(root + "/cassandraOutputStream/name", "setName", 0);
		dig.addCallMethod(root + "/cassandraOutputStream/description", "setDescription", 0);
		dig.addCallMethod(root + "/cassandraOutputStream/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/cassandraOutputStream/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/cassandraOutputStream/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/cassandraOutputStream/temporaryFileName", "setTemporaryFilename", 0);
		
		dig.addCallMethod(root + "/cassandraOutputStream/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/cassandraOutputStream/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/cassandraOutputStream/truncate", "setTruncate", 0);

		dig.addCallMethod(root + "/cassandraOutputStream/inputMappingName", "createBufferMappingName", 3);
		dig.addCallParam(root + "/cassandraOutputStream/inputMappingName/transformationRef", 0);
		dig.addCallParam(root + "/cassandraOutputStream/inputMappingName/inputName", 1);
		dig.addCallParam(root + "/cassandraOutputStream/inputMappingName/outputName", 2);
		
		dig.addCallMethod(root + "/cassandraOutputStream/trashOuput", "setTrashTransformation", 0);
		
		dig.addSetNext(root + "/cassandraOutputStream", "addTransformation");

		
		/*
		 * HBase Input Stream
		 */
		dig.addObjectCreate(root + "/hbaseInputStream", HbaseInputStream.class);
		dig.addCallMethod(root + "/hbaseInputStream/name", "setName", 0);
		dig.addCallMethod(root + "/hbaseInputStream/description", "setDescription", 0);
		dig.addCallMethod(root + "/hbaseInputStream/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/hbaseInputStream/positionY", "setPositionY", 0);
		dig.addCallMethod("*/container-ref", "setContainer", 0);
		dig.addCallMethod(root + "/hbaseInputStream/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/hbaseInputStream/tableName", "setTableName", 0);
		dig.addCallMethod(root + "/hbaseInputStream/columnFamily", "setColumnFamily", 0);
		dig.addCallMethod(root + "/hbaseInputStream/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/hbaseInputStream/temporaryFileName", "setTemporaryFilename", 0);
		
		dig.addCallMethod(root + "/hbaseInputStream/outputVariables/variableInit", "addOutputVariable", 2);
		dig.addCallParam(root + "/hbaseInputStream/outputVariables/variableInit/fieldIndex", 0);
		dig.addCallParam(root + "/hbaseInputStream/outputVariables/variableInit/variableName", 1);

		dig.addCallMethod(root + "/hbaseInputStream/columnTypes/columnType", "addColumnType", 2);
		dig.addCallParam(root + "/hbaseInputStream/columnTypes/columnType/column", 0);
		dig.addCallParam(root + "/hbaseInputStream/columnTypes/columnType/value", 1);
		
		dig.addSetNext(root + "/hbaseInputStream", "addTransformation");
		
		
		/*
		 * HBase Output Streams
		 */
		dig.addObjectCreate(root + "/hbaseOutputStream", HBaseOutputStream.class);
		dig.addCallMethod(root + "/hbaseOutputStream/name", "setName", 0);
		dig.addCallMethod(root + "/hbaseOutputStream/description", "setDescription", 0);
		dig.addCallMethod(root + "/hbaseOutputStream/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/hbaseOutputStream/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/hbaseOutputStream/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/hbaseOutputStream/temporaryFileName", "setTemporaryFilename", 0);
		
		dig.addCallMethod(root + "/hbaseOutputStream/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/hbaseOutputStream/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/hbaseOutputStream/columnFamily", "setColumnFamily", 0);
		dig.addCallMethod(root + "/hbaseOutputStream/truncate", "setTruncate", 0);

		dig.addCallMethod(root + "/hbaseOutputStream/inputMappingName", "createBufferMappingName", 3);
		dig.addCallParam(root + "/hbaseOutputStream/inputMappingName/transformationRef", 0);
		dig.addCallParam(root + "/hbaseOutputStream/inputMappingName/inputName", 1);
		dig.addCallParam(root + "/hbaseOutputStream/inputMappingName/outputName", 2);
		
		dig.addCallMethod(root + "/hbaseOutputStream/trashOuput", "setTrashTransformation", 0);
		
		dig.addSetNext(root + "/hbaseOutputStream", "addTransformation");
		
		/*
		 * MongoDb Input Streams
		 */
		
		dig.addObjectCreate(root + "/mongoDbInputStream", MongoDbInputStream.class);
		dig.addCallMethod(root + "/mongoDbInputStream/name", "setName", 0);
		dig.addCallMethod(root + "/mongoDbInputStream/description", "setDescription", 0);
		dig.addCallMethod(root + "/mongoDbInputStream/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/mongoDbInputStream/positionY", "setPositionY", 0);
		dig.addCallMethod("*/container-ref", "setContainer", 0);
		dig.addCallMethod(root + "/mongoDbInputStream/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/mongoDbInputStream/tableName", "setTableName", 0);
		dig.addCallMethod(root + "/mongoDbInputStream/columnFamily", "setColumnFamily", 0);
		dig.addCallMethod(root + "/mongoDbInputStream/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/mongoDbInputStream/temporaryFileName", "setTemporaryFilename", 0);
		
		dig.addCallMethod(root + "/mongoDbInputStream/outputVariables/variableInit", "addOutputVariable", 2);
		dig.addCallParam(root + "/mongoDbInputStream/outputVariables/variableInit/fieldIndex", 0);
		dig.addCallParam(root + "/mongoDbInputStream/outputVariables/variableInit/variableName", 1);

		dig.addCallMethod(root + "/mongoDbInputStream/columnTypes/columnType", "addColumnType", 2);
		dig.addCallParam(root + "/mongoDbInputStream/columnTypes/columnType/column", 0);
		dig.addCallParam(root + "/mongoDbInputStream/columnTypes/columnType/value", 1);
		
		dig.addSetNext(root + "/mongoDbInputStream", "addTransformation");
		
		/*
		 * MongoDb Output Streams
		 */
		
		dig.addObjectCreate(root + "/mongoDbOutputStream", MongoDbOutputStream.class);
		dig.addCallMethod(root + "/mongoDbOutputStream/name", "setName", 0);
		dig.addCallMethod(root + "/mongoDbOutputStream/description", "setDescription", 0);
		dig.addCallMethod(root + "/mongoDbOutputStream/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/mongoDbOutputStream/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/mongoDbOutputStream/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/mongoDbOutputStream/temporaryFileName", "setTemporaryFilename", 0);
		
		dig.addCallMethod(root + "/mongoDbOutputStream/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/mongoDbOutputStream/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/mongoDbOutputStream/columnFamily", "setColumnFamily", 0);
		dig.addCallMethod(root + "/mongoDbOutputStream/truncate", "setTruncate", 0);

		dig.addCallMethod(root + "/mongoDbOutputStream/inputMappingName", "createBufferMappingName", 3);
		dig.addCallParam(root + "/mongoDbOutputStream/inputMappingName/transformationRef", 0);
		dig.addCallParam(root + "/mongoDbOutputStream/inputMappingName/inputName", 1);
		dig.addCallParam(root + "/mongoDbOutputStream/inputMappingName/outputName", 2);
		
		dig.addCallMethod(root + "/mongoDbOutputStream/trashOuput", "setTrashTransformation", 0);
		
		dig.addSetNext(root + "/mongoDbOutputStream", "addTransformation");
		
		/*
		 * Transfo Null
		 */
		dig.addObjectCreate(root + "/nullTransformation", NullTransformation.class);
		dig.addCallMethod(root + "/nullTransformation/name", "setName", 0);
		dig.addCallMethod(root + "/nullTransformation/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/nullTransformation/temporaryFileName", "setTemporaryFilename", 0);

		dig.addCallMethod(root + "/nullTransformation/description", "setDescription", 0);
		dig.addCallMethod(root + "/nullTransformation/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/nullTransformation/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/nullTransformation/exclusive", "setExclusive", 0);
		dig.addCallMethod(root + "/nullTransformation/trashRef", "setTrashTransformation", 0);
			
			dig.addObjectCreate(root + "/nullTransformation/condition", ConditionNull.class);
			dig.addCallMethod(root + "/nullTransformation/condition/elementNumber", "setStreamElementNumber", 0);
			dig.addCallMethod(root + "/nullTransformation/condition/elementName", "setStreamElementName", 0);
			dig.addCallMethod(root + "/nullTransformation/condition/operator", "setOperator", 0);
			dig.addCallMethod(root + "/nullTransformation/condition/outputRef", "setOutput", 0);
			dig.addCallMethod(root + "/nullTransformation/condition/value", "setValue", 0);
			dig.addSetNext(root + "/nullTransformation/condition", "addCondition");
		dig.addSetNext(root + "/nullTransformation", "addTransformation");
		
		/*
		 * WebServiceInput
		 */
		dig.addObjectCreate(root + "/webServiceInput", WebServiceInput.class);
		dig.addCallMethod(root + "/webServiceInput/name", "setName", 0);
		dig.addCallMethod(root + "/webServiceInput/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/webServiceInput/temporaryFileName", "setTemporaryFilename", 0);
		dig.addCallMethod(root + "/webServiceInput/description", "setDescription", 0);
		dig.addCallMethod(root + "/webServiceInput/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/webServiceInput/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/webServiceInput/webServiceUrl", "setWebServiceUrl", 0);
		dig.addCallMethod(root + "/webServiceInput/methodName", "setMethodName", 0);
		dig.addCallMethod(root + "/webServiceInput/login", "setLogin", 0);
		dig.addCallMethod(root + "/webServiceInput/password", "setPassword", 0);
		
		dig.addObjectCreate(root + "/webServiceInput/parameters/parameter", WebServiceParameter.class);
		dig.addCallMethod(root + "/webServiceInput/parameters/parameter/name", "setName", 0);
		dig.addCallMethod(root + "/webServiceInput/parameters/parameter/type", "setType", 0);
		dig.addCallMethod(root + "/webServiceInput/parameters/parameter/value", "setValue", 0);
		dig.addSetNext(root + "/webServiceInput/parameters/parameter", "addParameter");
		
		dig.addSetNext(root + "/webServiceInput", "addTransformation");
		
		/*
		 * WebServiceInput
		 */
		dig.addObjectCreate(root + "/webServiceVanillaInput", WebServiceVanillaInput.class);
		dig.addCallMethod(root + "/webServiceVanillaInput/name", "setName", 0);
		dig.addCallMethod(root + "/webServiceVanillaInput/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/webServiceVanillaInput/temporaryFileName", "setTemporaryFilename", 0);
		dig.addCallMethod(root + "/webServiceVanillaInput/description", "setDescription", 0);
		dig.addCallMethod(root + "/webServiceVanillaInput/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/webServiceVanillaInput/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/webServiceVanillaInput/webServiceDefinitionId", "setWebServiceDefinitionId", 0);
		dig.addCallMethod(root + "/webServiceVanillaInput/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/webServiceVanillaInput/definition", "setDefinition", 0);
		
		dig.addSetNext(root + "/webServiceVanillaInput", "addTransformation");

		
		/*
		 * Sqoop Transformation
		 */
		dig.addObjectCreate(root + "/sqoop", SqoopTransformation.class);
		dig.addCallMethod(root + "/sqoop/name", "setName", 0);
		dig.addCallMethod(root + "/sqoop/description", "setDescription", 0);
		dig.addCallMethod(root + "/sqoop/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/sqoop/positionY", "setPositionY", 0);
		dig.addCallMethod("*/container-ref", "setContainer", 0);
		dig.addCallMethod(root + "/sqoop/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/sqoop/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/sqoop/sqoopUrl", "setSqoopUrl", 0);
		dig.addCallMethod(root + "/sqoop/hdfsDirectory", "setHdfsDirectory", 0);
		dig.addCallMethod(root + "/sqoop/partitionColumn", "setPartitionColumn", 0);
		dig.addCallMethod(root + "/sqoop/selectedColunm", "addSelectedColumn", 0);
		dig.addCallMethod(root + "/sqoop/isImport", "setImport", 0);
		dig.addCallMethod(root + "/sqoop/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/sqoop/temporaryFileName", "setTemporaryFilename", 0);

		dig.addSetNext(root + "/sqoop", "addTransformation");
		
		/*
		 * DataBaseInputStreams
		 */
		dig.addObjectCreate(root + "/oracleXmlView", OracleXmlView.class);
		dig.addCallMethod(root + "/oracleXmlView/name", "setName", 0);
		dig.addCallMethod(root + "/oracleXmlView/description", "setDescription", 0);
		dig.addCallMethod(root + "/oracleXmlView/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/oracleXmlView/positionY", "setPositionY", 0);
		dig.addCallMethod("*/container-ref", "setContainer", 0);
		dig.addCallMethod(root + "/oracleXmlView/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/oracleXmlView/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/oracleXmlView/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/oracleXmlView/temporaryFileName", "setTemporaryFilename", 0);
		dig.addCallMethod(root + "/oracleXmlView/rootElement", "setRootElementFromDigester", 0);
		
		dig.addCallMethod(root + "/oracleXmlView/outputVariables/variableInit", "addOutputVariable", 2);
		dig.addCallParam(root + "/oracleXmlView/outputVariables/variableInit/fieldIndex", 0);
		dig.addCallParam(root + "/oracleXmlView/outputVariables/variableInit/variableName", 1);

		dig.addSetNext(root + "/oracleXmlView", "addTransformation");
		
		/*
		 * Connector Abonne XML Transformation
		 */
		dig.addObjectCreate(root + "/connectorAbonneXML", ConnectorAbonneXML.class);
		dig.addCallMethod(root + "/connectorAbonneXML/name", "setName", 0);
		dig.addCallMethod(root + "/connectorAbonneXML/description", "setDescription", 0);
		dig.addCallMethod(root + "/connectorAbonneXML/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/connectorAbonneXML/temporaryFileName", "setTemporaryFilename", 0);
		dig.addCallMethod(root + "/connectorAbonneXML/encoding", "setEncoding", 0);
		dig.addCallMethod(root + "/connectorAbonneXML/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/connectorAbonneXML/positionY", "setPositionY", 0);
		dig.addCallMethod("*/container-ref", "setContainer", 0);
		dig.addCallMethod(root + "/connectorAbonneXML/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/connectorAbonneXML/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/connectorAbonneXML/isInput", "setInput", 0);
		dig.addCallMethod(root + "/connectorAbonneXML/isOds", "setODS", 0);
		dig.addCallMethod(root + "/connectorAbonneXML/beginDate", "setBeginDate", 0);
		dig.addCallMethod(root + "/connectorAbonneXML/endDate", "setEndDate", 0);
		dig.addCallMethod(root + "/connectorAbonneXML/query", "setQuery", 0);
		dig.addCallMethod(root + "/connectorAbonneXML/query2", "setQuery2", 0);
		dig.addCallMethod(root + "/connectorAbonneXML/useMdm", "setUseMdm", 0);
		dig.addCallMethod(root + "/connectorAbonneXML/supplierId", "setSupplierId", 0);
		dig.addCallMethod(root + "/connectorAbonneXML/contractId", "setContractId", 0);

		dig.addSetNext(root + "/connectorAbonneXML", "addTransformation");
		
		/*
		 * Connector Patrimoine XML Transformation
		 */
		dig.addObjectCreate(root + "/connectorPatrimoineXML", ConnectorPatrimoineXML.class);
		dig.addCallMethod(root + "/connectorPatrimoineXML/name", "setName", 0);
		dig.addCallMethod(root + "/connectorPatrimoineXML/description", "setDescription", 0);
		dig.addCallMethod(root + "/connectorPatrimoineXML/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/connectorPatrimoineXML/temporaryFileName", "setTemporaryFilename", 0);
		dig.addCallMethod(root + "/connectorPatrimoineXML/encoding", "setEncoding", 0);
		dig.addCallMethod(root + "/connectorPatrimoineXML/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/connectorPatrimoineXML/positionY", "setPositionY", 0);
		dig.addCallMethod("*/container-ref", "setContainer", 0);
		dig.addCallMethod(root + "/connectorPatrimoineXML/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/connectorPatrimoineXML/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/connectorPatrimoineXML/isInput", "setInput", 0);
		dig.addCallMethod(root + "/connectorPatrimoineXML/isOds", "setODS", 0);
		dig.addCallMethod(root + "/connectorPatrimoineXML/beginDate", "setBeginDate", 0);
		dig.addCallMethod(root + "/connectorPatrimoineXML/endDate", "setEndDate", 0);
		dig.addCallMethod(root + "/connectorPatrimoineXML/query", "setQuery", 0);
		dig.addCallMethod(root + "/connectorPatrimoineXML/query2", "setQuery2", 0);
		dig.addCallMethod(root + "/connectorPatrimoineXML/useMdm", "setUseMdm", 0);
		dig.addCallMethod(root + "/connectorPatrimoineXML/supplierId", "setSupplierId", 0);
		dig.addCallMethod(root + "/connectorPatrimoineXML/contractId", "setContractId", 0);

		dig.addSetNext(root + "/connectorPatrimoineXML", "addTransformation");
		
		/*
		 * Connector Affaire XML Transformation
		 */
		dig.addObjectCreate(root + "/connectorAffaireXML", ConnectorAffaireXML.class);
		dig.addCallMethod(root + "/connectorAffaireXML/name", "setName", 0);
		dig.addCallMethod(root + "/connectorAffaireXML/description", "setDescription", 0);
		dig.addCallMethod(root + "/connectorAffaireXML/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/connectorAffaireXML/temporaryFileName", "setTemporaryFilename", 0);
		dig.addCallMethod(root + "/connectorAffaireXML/encoding", "setEncoding", 0);
		dig.addCallMethod(root + "/connectorAffaireXML/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/connectorAffaireXML/positionY", "setPositionY", 0);
		dig.addCallMethod("*/container-ref", "setContainer", 0);
		dig.addCallMethod(root + "/connectorAffaireXML/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/connectorAffaireXML/definition", "setDefinition", 0);

		dig.addSetNext(root + "/connectorAffaireXML", "addTransformation");
		
		/*
		 * Connector Appel XML Transformation
		 */
		dig.addObjectCreate(root + "/connectorAppelXML", ConnectorAppelXML.class);
		dig.addCallMethod(root + "/connectorAppelXML/name", "setName", 0);
		dig.addCallMethod(root + "/connectorAppelXML/description", "setDescription", 0);
		dig.addCallMethod(root + "/connectorAppelXML/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/connectorAppelXML/temporaryFileName", "setTemporaryFilename", 0);
		dig.addCallMethod(root + "/connectorAppelXML/encoding", "setEncoding", 0);
		dig.addCallMethod(root + "/connectorAppelXML/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/connectorAppelXML/positionY", "setPositionY", 0);
		dig.addCallMethod("*/container-ref", "setContainer", 0);
		dig.addCallMethod(root + "/connectorAppelXML/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/connectorAppelXML/definition", "setDefinition", 0);

		dig.addSetNext(root + "/connectorAppelXML", "addTransformation");
		
		/*
		 * Connector Referentiel XML Transformation
		 */
		dig.addObjectCreate(root + "/connectorReferentielXML", ConnectorReferentielXML.class);
		dig.addCallMethod(root + "/connectorReferentielXML/name", "setName", 0);
		dig.addCallMethod(root + "/connectorReferentielXML/description", "setDescription", 0);
		dig.addCallMethod(root + "/connectorReferentielXML/temporarySeparator", "setTemporarySpliterChar", 0);
		dig.addCallMethod(root + "/connectorReferentielXML/temporaryFileName", "setTemporaryFilename", 0);
		dig.addCallMethod(root + "/connectorReferentielXML/encoding", "setEncoding", 0);
		dig.addCallMethod(root + "/connectorReferentielXML/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/connectorReferentielXML/positionY", "setPositionY", 0);
		dig.addCallMethod("*/container-ref", "setContainer", 0);
		dig.addCallMethod(root + "/connectorReferentielXML/serverRef", "setServer", 0);
		dig.addCallMethod(root + "/connectorReferentielXML/definition", "setDefinition", 0);

		dig.addSetNext(root + "/connectorReferentielXML", "addTransformation");
		
		
		dig.addObjectCreate(root + "/syriusConnector", SyriusConnector.class);
		dig.addCallMethod(root + "/syriusConnector/name", "setName", 0);
		dig.addCallMethod(root + "/syriusConnector/description", "setDescription", 0);
		dig.addCallMethod(root + "/syriusConnector/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/syriusConnector/positionY", "setPositionY", 0);
		dig.addCallMethod("*/container-ref", "setContainer", 0);
		dig.addCallMethod(root + "/syriusConnector/userId", "setUserId", 0);
		dig.addCallMethod(root + "/syriusConnector/password", "setPassword", 0);
		dig.addCallMethod(root + "/syriusConnector/year", "setYear", 0);
		dig.addCallMethod(root + "/syriusConnector/period", "setPeriod", 0);
		dig.addCallMethod(root + "/syriusConnector/serviceUrl", "setServiceUrl", 0);
		dig.addCallMethod(root + "/syriusConnector/finessFilter", "setFinessFilter", 0);
		dig.addCallMethod(root + "/syriusConnector/orderFilter", "setOrderFilter", 0);
		dig.addCallMethod(root + "/syriusConnector/server", "setServer", 0);
		dig.addCallMethod(root + "/syriusConnector/sqlGeo", "setSqlGeo", 0);
		dig.addCallMethod(root + "/syriusConnector/sqlDiag", "setSqlDiag", 0);
		dig.addCallMethod(root + "/syriusConnector/sqlActes", "setSqlActes", 0);
		dig.addCallMethod(root + "/syriusConnector/sqlPatient", "setSqlPatient", 0);
		dig.addCallMethod(root + "/syriusConnector/columnCp", "setColumnCp", 0);
		dig.addCallMethod(root + "/syriusConnector/columnCodeGeo", "setColumnCodeGeo", 0);

		dig.addSetNext(root + "/syriusConnector", "addTransformation");
		
		
		dig.addObjectCreate(root + "/rpuConnector", RpuConnector.class);
		dig.addCallMethod(root + "/rpuConnector/name", "setName", 0);
		dig.addCallMethod(root + "/rpuConnector/description", "setDescription", 0);
		dig.addCallMethod(root + "/rpuConnector/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/rpuConnector/positionY", "setPositionY", 0);
		dig.addCallMethod("*/container-ref", "setContainer", 0);
		dig.addCallMethod(root + "/rpuConnector/dateDebut", "setDateDebut", 0);
		dig.addCallMethod(root + "/rpuConnector/dateFin", "setDateFin", 0);
		dig.addCallMethod(root + "/rpuConnector/finessFilter", "setFinessFilter", 0);
		dig.addCallMethod(root + "/rpuConnector/orderFilter", "setOrderFilter", 0);
		dig.addCallMethod(root + "/rpuConnector/server", "setServer", 0);
		dig.addCallMethod(root + "/rpuConnector/sqlDiag", "setSqlDiag", 0);
		dig.addCallMethod(root + "/rpuConnector/sqlActes", "setSqlActes", 0);
		dig.addCallMethod(root + "/rpuConnector/sqlPatient", "setSqlPatient", 0);
		dig.addCallMethod(root + "/rpuConnector/outputFile", "setOutputFile", 0);

		dig.addSetNext(root + "/rpuConnector", "addTransformation");
		
		/*
		 * Geoloc
		 */
		dig.addObjectCreate(root + "/geoloc", Geoloc.class);
		dig.addCallMethod(root + "/geoloc/name", "setName", 0);
		dig.addCallMethod(root + "/geoloc/description", "setDescription", 0);
		dig.addCallMethod(root + "/geoloc/positionX", "setPositionX", 0);
		dig.addCallMethod(root + "/geoloc/positionY", "setPositionY", 0);
		dig.addCallMethod(root + "/geoloc/onlyOneColumnAdress", "setOnlyOneColumnAdress", 0);
		dig.addCallMethod(root + "/geoloc/libelleIndex", "setLibelleIndex", 0);
		dig.addCallMethod(root + "/geoloc/postalCodeIndex", "setPostalCodeIndex", 0);
		dig.addCallMethod(root + "/geoloc/score", "setScore", 0);
		dig.addCallMethod(root + "/geoloc/onlyOneColumnOutput", "setOnlyOneColumnOutput", 0);
		dig.addCallMethod(root + "/geoloc/firstColunmName", "setFirstColunmName", 0);
		dig.addCallMethod(root + "/geoloc/secondColunmName", "setSecondColunmName", 0);
//		dig.addCallMethod(root + "/geoloc/scoreColumnName", "setScoreColumnName", 0);
		dig.addCallMethod(root + "/geoloc/field", "addField", 0);
		dig.addSetNext(root + "/geoloc", "addTransformation");
	}
}


