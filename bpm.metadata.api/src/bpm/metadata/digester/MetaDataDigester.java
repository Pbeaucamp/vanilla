package bpm.metadata.digester;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.digester3.Digester;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import bpm.metadata.BuilderException;
import bpm.metadata.DocumentProperties;
import bpm.metadata.MetaData;
import bpm.metadata.MetaDataBuilder;
import bpm.metadata.layer.business.AbstractBusinessTable;
import bpm.metadata.layer.business.BusinessModel;
import bpm.metadata.layer.business.BusinessPackage;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.business.RelationStrategy;
import bpm.metadata.layer.business.SQLBusinessTable;
import bpm.metadata.layer.business.UnitedOlapBusinessPackage;
import bpm.metadata.layer.business.UnitedOlapBusinessTable;
import bpm.metadata.layer.logical.AbstractDataSource;
import bpm.metadata.layer.logical.ICalculatedElement;
import bpm.metadata.layer.logical.IDataSource;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.Join;
import bpm.metadata.layer.logical.MultiDSRelation;
import bpm.metadata.layer.logical.Relation;
import bpm.metadata.layer.logical.olap.UnitedOlapDataStream;
import bpm.metadata.layer.logical.olap.UnitedOlapDataStreamElement;
import bpm.metadata.layer.logical.olap.UnitedOlapDatasource;
import bpm.metadata.layer.logical.sql.SQLDataSource;
import bpm.metadata.layer.logical.sql.SQLDataStream;
import bpm.metadata.layer.logical.sql.SQLDataStreamElement;
import bpm.metadata.layer.logical.sql.SQLRelation;
import bpm.metadata.layer.physical.olap.UnitedOlapConnection;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.metadata.misc.Type;
import bpm.metadata.query.SavedQuery;
import bpm.metadata.resource.ComplexFilter;
import bpm.metadata.resource.FileResource;
import bpm.metadata.resource.Filter;
import bpm.metadata.resource.IFilter;
import bpm.metadata.resource.IResource;
import bpm.metadata.resource.ImageResource;
import bpm.metadata.resource.ListOfValue;
import bpm.metadata.resource.Prompt;
import bpm.metadata.resource.SqlQueryFilter;
import bpm.metadata.resource.complex.FmdtDimension;
import bpm.metadata.resource.complex.FmdtMeasure;
import bpm.metadata.scripting.Script;
import bpm.metadata.scripting.Variable;
import bpm.metadata.tools.Log;
import bpm.vanilla.platform.core.IRepositoryApi;

public class MetaDataDigester {
	protected class MyErrorHandler implements ErrorHandler{

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
	

	protected MetaData model;
	protected Digester dig;
	protected StringBuffer errorBuffer = new StringBuffer();
	
	protected MetaDataBuilder builder;
	
	public MetaDataDigester(InputStream input, MetaDataBuilder builder) throws IOException, SAXException, DocumentException{
		this.builder = builder;
		
		dig = new Digester();
		dig.setValidating(false);
		dig.setErrorHandler(new MyErrorHandler());
		createCallbacks(dig);
		
		String xml = IOUtils.toString(input, "UTF-8");
		Document doc = DocumentHelper.parseText(xml);

		boolean changed = false;
		try{
			for(Element e : (List<Element>)doc.selectNodes("//sqlConnection/schemaName")){
				changed = true;
				
				for(Element dataStream : (List<Element>)e.getParent().getParent().element("dataStream")){
					dataStream.element("origin").setText(e.getText() + "." + dataStream.element("origin").getText());
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		model = (MetaData)dig.parse(IOUtils.toInputStream(doc.asXML(), "UTF-8"));
		
	}
	
	/**
	 * @deprecated
	 * @param file
	 * @throws IOException
	 * @throws SAXException
	 * @throws DocumentException
	 */
	public MetaDataDigester(File file) throws IOException, SAXException, DocumentException{
		this.builder = builder;
		
		dig = new Digester();
		dig.setErrorHandler(new MyErrorHandler());
		createCallbacks(dig);
		
		FileInputStream fis = new FileInputStream(file);
		
		Document doc = DocumentHelper.parseText(IOUtils.toString(fis, "UTF-8"));

		boolean changed = false;
		try{
			for(Element e : (List<Element>)doc.selectNodes("//sqlConnection/schemaName")){
				changed = true;
				
				for(Element dataStream : (List<Element>)e.getParent().getParent().element("dataStream")){
					dataStream.element("origin").setText(e.getText() + "." + dataStream.element("origin").getText());
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			fis.close();
		}catch(IOException e){
			
		}
		if (changed){
			PrintWriter fw = new PrintWriter(file, "UTF-8");
			String s = doc.asXML();
			fw.write(s);
			fw.close();
		}
		
		
		model = (MetaData)dig.parse(file);
		
	}
	
	public MetaDataDigester(String path, MetaDataBuilder builder) throws IOException, SAXException, DocumentException{
		this.builder = builder;
		
		dig = new Digester();
		dig.setErrorHandler(new MyErrorHandler());
		createCallbacks(dig);
		File file = new File(path);
		
		FileInputStream fis = new FileInputStream(path);
		
		Document doc = DocumentHelper.parseText(IOUtils.toString(fis, "UTF-8"));
		boolean changed = false;
		try{
			for(Element e : (List<Element>)doc.selectNodes("//sqlConnection/schemaName")){
				changed = true;
				
				for(Element dataStream : (List<Element>)e.getParent().getParent().element("dataStream")){
					dataStream.element("origin").setText(e.getText() + "." + dataStream.element("origin").getText());
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			fis.close();
		}catch(IOException e){
			
		}
		
		if (changed){
			PrintWriter fw = new PrintWriter(file, "UTF-8");
			String s = doc.asXML();
			fw.write(s);
			fw.close();
		}

		
		model = (MetaData)dig.parse(file);
		
	}
	
	
	public void setBuilder(MetaDataBuilder builder){
		this.builder = builder;
	}
	
	private void createCallbacks(Digester dig){
		dig.setValidating(false);
		
		String root = "freeMetaData";
		
		dig.addObjectCreate(root, MetaData.class);

		dig.addCallMethod(root + "/d4cServerId", "setD4cServerId", 0);
		dig.addCallMethod(root + "/d4cOrganisation", "setD4cOrganisation", 0);
		
		//properties
		dig.addObjectCreate(root + "/document-properties", DocumentProperties.class);
		dig.addCallMethod(root + "/document-properties/id", "setId", 0);
		dig.addCallMethod(root + "/document-properties/name", "setName", 0);
		dig.addCallMethod(root + "/document-properties/author", "setAuthor", 0);
		dig.addCallMethod(root + "/document-properties/description", "setDescription", 0);
		dig.addCallMethod(root + "/document-properties/creation", "setCreation", 0);
		dig.addCallMethod(root + "/document-properties/fmdtType", "setFmdtType", 0);
		dig.addCallMethod(root + "/document-properties/projectVersion", "setProjectVersion", 0);
		dig.addCallMethod(root + "/document-properties/projectName", "setProjectName", 0);
		dig.addCallMethod(root + "/document-properties/version", "setVersion", 0);
		
		dig.addSetNext(root + "/document-properties", "setProperties");
		
		dig.addCallMethod(root + "/language", "addLocale", 0);
		dig.addCallMethod(root + "/dependantItemId", "addDependencies", 0);
		
		//image
		dig.addObjectCreate(root + "/image", ImageResource.class);
			dig.addCallMethod(root + "/image/name", "setName", 0);
			dig.addCallMethod(root + "/image/path", "setPath", 0);
		dig.addSetNext(root + "/image", "addResource");
		//file
		dig.addObjectCreate(root + "/file", FileResource.class);
			dig.addCallMethod(root + "/file/name", "setName", 0);
			dig.addCallMethod(root + "/file/path", "setPath", 0);
		dig.addSetNext(root + "/file", "addResource");
		
		//LoV
		dig.addObjectCreate(root + "/lov", ListOfValue.class);
			dig.addCallMethod(root + "/lov/name", "setName", 0);
			dig.addCallMethod(root + "/lov/dataStream", "setDataStreamName", 0);
			dig.addCallMethod(root + "/lov/dataStreamElement","setDataStreamElementName" ,0);
	
			//grants
			dig.addCallMethod(root + "/lov/grant", "setGranted", 2);
			dig.addCallParam(root + "/lov/grant/groupName", 0);
			dig.addCallParam(root + "/lov/grant/isGranted", 1);
			
			dig.addCallMethod(root + "/lov/groupNames", "setGroupsGranted", 0);

			dig.addCallMethod(root + "/lov/outputname", "setOutputName", 3);
			dig.addCallParam(root + "/lov/outputname/country", 0);
			dig.addCallParam(root + "/lov/outputname/language", 1);
			dig.addCallParam(root + "/lov/outputname/value", 2);
		dig.addSetNext(root + "/lov", "addResource");

		//sqlFiler
		dig.addObjectCreate(root + "/sqlFilter", SqlQueryFilter.class);
			dig.addCallMethod(root + "/sqlFilter/name", "setName", 0);
			dig.addCallMethod(root + "/sqlFilter/dataStream", "setDataStreamName", 0);
			dig.addCallMethod(root + "/sqlFilter/dataStreamElement","setDataStreamElementName" ,0);
			dig.addCallMethod(root + "/sqlFilter/query","setQuery" ,0);
			
			//grants
			dig.addCallMethod(root + "/sqlFilter/grant", "setGranted", 2);
			dig.addCallParam(root + "/sqlFilter/grant/groupName", 0);
			dig.addCallParam(root + "/sqlFilter/grant/isGranted", 1);
			
			dig.addCallMethod(root + "/sqlFilter/groupNames", "setGroupsGranted", 0);
			
			dig.addCallMethod(root + "/sqlFilter/outputname", "setOutputName", 3);
			dig.addCallParam(root + "/sqlFilter/outputname/country", 0);
			dig.addCallParam(root + "/sqlFilter/outputname/language", 1);
			dig.addCallParam(root + "/sqlFilter/outputname/value", 2);
		
		dig.addSetNext(root + "/sqlFilter", "addResource");
		//Filter
		dig.addObjectCreate(root + "/filter", Filter.class);
			dig.addCallMethod(root + "/filter/name", "setName", 0);
			dig.addCallMethod(root + "/filter/dataStream", "setDataStreamName", 0);
			dig.addCallMethod(root + "/filter/dataStreamElement","setDataStreamElementName" ,0);
			dig.addCallMethod(root + "/filter/value","addValue" ,0);
			
			//grants
			dig.addCallMethod(root + "/filter/grant", "setGranted", 2);
			dig.addCallParam(root + "/filter/grant/groupName", 0);
			dig.addCallParam(root + "/filter/grant/isGranted", 1);
			
			dig.addCallMethod(root + "/filter/groupNames", "setGroupsGranted", 0);

			dig.addCallMethod(root + "/filter/outputname", "setOutputName", 3);
			dig.addCallParam(root + "/filter/outputname/country", 0);
			dig.addCallParam(root + "/filter/outputname/language", 1);
			dig.addCallParam(root + "/filter/outputname/value", 2);
		dig.addSetNext(root + "/filter", "addResource");
		
		
		//Dimension
		dig.addObjectCreate(root + "/dimension", FmdtDimension.class);
			dig.addCallMethod(root + "/dimension/name", "setName", 0);
			dig.addCallMethod(root + "/dimension/geolocalizable", "setGeolocalizable", 0);
			dig.addCallMethod(root + "/dimension/dataSource", "setDataSourceName", 0);
			dig.addCallMethod(root + "/dimension/level", "addLevel", 2);
			dig.addCallParam(root + "/dimension/level/dataStream", 0);
			dig.addCallParam(root + "/dimension/level/dataStreamElement", 1);
			
			//grants
			dig.addCallMethod(root + "/dimension/grant", "setGranted", 2);
			dig.addCallParam(root + "/dimension/grant/groupName", 0);
			dig.addCallParam(root + "/dimension/grant/isGranted", 1);
			
			dig.addCallMethod(root + "/dimension/groupNames", "setGroupsGranted", 0);

		dig.addSetNext(root + "/dimension", "addResource");
		
		//MEASURE
		dig.addObjectCreate(root + "/measure", FmdtMeasure.class);
			dig.addCallMethod(root + "/measure/name", "setName", 0);
			dig.addCallMethod(root + "/measure/dataSource", "setDataSourceName", 0);
			dig.addCallMethod(root + "/measure/script", "setScript", 0);
			
			//grants
			dig.addCallMethod(root + "/measure/grant", "setGranted", 2);
			dig.addCallParam(root + "/measure/grant/groupName", 0);
			dig.addCallParam(root + "/measure/grant/isGranted", 1);
			
			dig.addCallMethod(root + "/measure/groupNames", "setGroupsGranted", 0);

		dig.addSetNext(root + "/measure", "addResource");
		
		//Prompt
		dig.addObjectCreate(root + "/prompt", Prompt.class);
			dig.addCallMethod(root + "/prompt/name", "setName", 0);
			dig.addCallMethod(root + "/prompt/question", "setQuestion", 0);
			dig.addCallMethod(root + "/prompt/operator", "setOperator", 0);
			
			dig.addCallMethod(root + "/prompt/originDataStream", "setOriginDataStreamName", 0);
			dig.addCallMethod(root + "/prompt/originDataStreamElement","setOriginDataStreamElementName" ,0);
			
			dig.addCallMethod(root + "/prompt/destinationDataStream", "setGotoDataStreamName", 0);
			dig.addCallMethod(root + "/prompt/destinationDataStreamElement","setGotoDataStreamElementName" ,0);

			dig.addCallMethod(root + "/prompt/destinationSql", "setGotoSql", 0);
			dig.addCallMethod(root + "/prompt/destinationType","setPromptType" ,0);
			
			//grants
			dig.addCallMethod(root + "/prompt/grant", "setGranted", 2);
			dig.addCallParam(root + "/prompt/grant/groupName", 0);
			dig.addCallParam(root + "/prompt/grant/isGranted", 1);
			
			dig.addCallMethod(root + "/prompt/groupNames", "setGroupsGranted", 0);

			
			dig.addCallMethod(root + "/prompt/outputname", "setOutputName", 3);
			dig.addCallParam(root + "/prompt/outputname/country", 0);
			dig.addCallParam(root + "/prompt/outputname/language", 1);
			dig.addCallParam(root + "/prompt/outputname/value", 2);
			
			dig.addCallMethod(root + "/prompt/parentprompt", "setParentPromptName", 0);
			
		dig.addSetNext(root + "/prompt", "addResource");
		
		//COmplexFilter
		dig.addObjectCreate(root + "/complexFilter", ComplexFilter.class);
			dig.addCallMethod(root + "/complexFilter/name", "setName", 0);
			dig.addCallMethod(root + "/complexFilter/dataStream", "setDataStreamName", 0);
			dig.addCallMethod(root + "/complexFilter/dataStreamElement","setDataStreamElementName" ,0);
			dig.addCallMethod(root + "/complexFilter/value","setValue" ,0);
			dig.addCallMethod(root + "/complexFilter/operator","setOperator" ,0);
			
			//grants
			dig.addCallMethod(root + "/complexFilter/grant", "setGranted", 2);
			dig.addCallParam(root + "/complexFilter/grant/groupName", 0);
			dig.addCallParam(root + "/complexFilter/grant/isGranted", 1);
			
			dig.addCallMethod(root + "/complexFilter/groupNames", "setGroupsGranted", 0);

			
			dig.addCallMethod(root + "/complexFilter/outputname", "setOutputName", 3);
			dig.addCallParam(root + "/complexFilter/outputname/country", 0);
			dig.addCallParam(root + "/complexFilter/outputname/language", 1);
			dig.addCallParam(root + "/complexFilter/outputname/value", 2);
		dig.addSetNext(root + "/complexFilter", "addResource");



		// olap DataSource
		dig.addObjectCreate(root + "/unitedOlapDatasource", UnitedOlapDatasource.class);
			dig.addCallMethod(root + "/unitedOlapDatasource/name", "setName", 0);
			
//			//Connection
			dig.addObjectCreate(root + "/unitedOlapDatasource/unitedOlapConnection", UnitedOlapConnection.class);
				dig.addCallMethod(root + "/unitedOlapDatasource/unitedOlapConnection/name", "setName", 0);
				dig.addCallMethod(root + "/unitedOlapDatasource/unitedOlapConnection/cubename", "setCubeName", 0);
				
				dig.addCallMethod(root + "/unitedOlapDatasource/unitedOlapConnection/runtimeContext", "setRuntimeContext", 4);
					dig.addCallParam(root + "/unitedOlapDatasource/unitedOlapConnection/runtimeContext/user", 0);
					dig.addCallParam(root + "/unitedOlapDatasource/unitedOlapConnection/runtimeContext/password", 1);
					dig.addCallParam(root + "/unitedOlapDatasource/unitedOlapConnection/runtimeContext/groupname", 2);
					dig.addCallParam(root + "/unitedOlapDatasource/unitedOlapConnection/runtimeContext/groupid", 3);
					
				dig.addCallMethod(root + "/unitedOlapDatasource/unitedOlapConnection/identifier", "setIdentifier", 2);
					dig.addCallParam(root + "/unitedOlapDatasource/unitedOlapConnection/identifier/repositoryid", 0);
					dig.addCallParam(root + "/unitedOlapDatasource/unitedOlapConnection/identifier/fasdid", 1);
					
				
				dig.addCallMethod(root + "/unitedOlapDatasource/grant", "securizeConnection", 2, new Class[]{String.class, ArrayList.class});
					dig.addCallParam(root + "/unitedOlapDatasource/grant/connectionName", 0);
					
				dig.addObjectCreate(root + "/unitedOlapDatasource/grant/grantedFor", ArrayList.class);
					dig.addCallMethod(root + "/unitedOlapDatasource/grant/grantedFor/group", "add", 0);
						dig.addCallParam(root + "/unitedOlapDatasource/grant/grantedFor/group", 1, true);
					
			dig.addSetNext(root + "/unitedOlapDatasource/unitedOlapConnection", "addAlternateConnection");
			
			
			
			
		
			//DataStream
			dig.addObjectCreate(root + "/unitedOlapDatasource/dataStream", UnitedOlapDataStream.class);
				dig.addCallMethod(root + "/unitedOlapDatasource/dataStream/name", "setName", 0);
				dig.addCallMethod(root + "/unitedOlapDatasource/dataStream/description", "setDescription", 0);
				dig.addCallMethod(root + "/unitedOlapDatasource/dataStream/outputLength", "setOutputLength", 0);
				dig.addCallMethod(root + "/unitedOlapDatasource/dataStream/weight", "setWeight", 0);
				dig.addCallMethod(root + "/unitedOlapDatasource/dataStream/type", "setType", 0);
				dig.addCallMethod(root + "/unitedOlapDatasource/dataStream/origin", "setOriginName", 0);
				dig.addCallMethod(root + "/unitedOlapDatasource/dataStream/positionX", "setPositionX", 0);
				dig.addCallMethod(root + "/unitedOlapDatasource/dataStream/positionY", "setPositionY", 0);
//				dig.addCallMethod(root + "/unitedOlapDatasource/dataStream/sql", "setSql", 0);
			
				//dataStreamElement
				dig.addObjectCreate(root + "/unitedOlapDatasource/dataStream/dataStreamElement", UnitedOlapDataStreamElement.class);
					dig.addCallMethod(root + "/unitedOlapDatasource/dataStream/dataStreamElement/name", "setName", 0);
					//dig.addCallMethod(root + "/unitedOlapDatasource/dataStream/dataStreamElement/isVisible", "setVisible", 0);
					dig.addCallMethod(root + "/unitedOlapDatasource/dataStream/dataStreamElement/description", "setDescription", 0);
					dig.addCallMethod(root + "/unitedOlapDatasource/dataStream/dataStreamElement/fontName", "setFontName", 0);
					dig.addCallMethod(root + "/unitedOlapDatasource/dataStream/dataStreamElement/textColor", "setTextColor", 0);
					dig.addCallMethod(root + "/unitedOlapDatasource/dataStream/dataStreamElement/backgroundColor", "setBackgroundColor", 0);
					dig.addCallMethod(root + "/unitedOlapDatasource/dataStream/dataStreamElement/origin", "setOriginName", 0);
					dig.addCallMethod(root + "/unitedOlapDatasource/dataStream/dataStreamElement/type", "setType", 0);
					dig.addCallMethod(root + "/unitedOlapDatasource/dataStream/dataStreamElement/parentDimension", "setParentDimension", 0);
					dig.addCallMethod(root + "/unitedOlapDatasource/dataStream/dataStreamElement/isKpi", "setIsKpi", 0);
					dig.addCallMethod(root + "/unitedOlapDatasource/dataStream/dataStreamElement/indexable", "setIndexable", 0);
					
										
				dig.addSetNext(root + "/unitedOlapDatasource/dataStream/dataStreamElement", "addColumn");				
				
			dig.addSetNext(root + "/unitedOlapDatasource/dataStream", "add");
			
			
		dig.addSetNext(root + "/unitedOlapDatasource", "addDataSource");
		
		
		// SQL DataSource
		dig.addObjectCreate(root + "/sqlDataSource", SQLDataSource.class);
			dig.addCallMethod(root + "/sqlDataSource/name", "setName", 0);
			
			//Connection
			dig.addObjectCreate(root + "/sqlDataSource/sqlConnection", SQLConnection.class);
				dig.addCallMethod(root + "/sqlDataSource/sqlConnection/name", "setName", 0);
				dig.addCallMethod(root + "/sqlDataSource/sqlConnection/driverName", "setDriverName", 0);
				dig.addCallMethod(root + "/sqlDataSource/sqlConnection/methodName", "setMethodName", 0);
				dig.addCallMethod(root + "/sqlDataSource/sqlConnection/host", "setHost", 0);
				dig.addCallMethod(root + "/sqlDataSource/sqlConnection/useFullUrl", "setUseFullUrl", 0);
				dig.addCallMethod(root + "/sqlDataSource/sqlConnection/fullUrl", "setFullUrl", 0);
				dig.addCallMethod(root + "/sqlDataSource/sqlConnection/dataBaseName", "setDataBaseName", 0);
				dig.addCallMethod(root + "/sqlDataSource/sqlConnection/portNumber", "setPortNumber", 0);
				dig.addCallMethod(root + "/sqlDataSource/sqlConnection/username", "setUsername", 0);
				dig.addCallMethod(root + "/sqlDataSource/sqlConnection/password", "setPassword", 0);
//				dig.addCallMethod(root + "/sqlDataSource/sqlConnection/schemaName", "setSchemaName", 0);
				
				
				//Grants
				
				dig.addCallMethod(root + "/sqlDataSource/grant", "securizeConnection", 2, new Class[]{String.class, ArrayList.class});
				dig.addCallParam(root + "/sqlDataSource/grant/connectionName", 0);
				
				dig.addObjectCreate(root + "/sqlDataSource/grant/grantedFor", ArrayList.class);
				dig.addCallMethod(root + "/sqlDataSource/grant/grantedFor/group", "add", 0);
				
				dig.addCallParam(root + "/sqlDataSource/grant/grantedFor/group", 1, true);
				
				
				
			dig.addSetNext(root + "/sqlDataSource/sqlConnection", "addAlternateConnection");
		
			//DataSTream
			dig.addObjectCreate(root + "/sqlDataSource/dataStream", SQLDataStream.class);
				dig.addCallMethod(root + "/sqlDataSource/dataStream/name", "setName", 0);
				dig.addCallMethod(root + "/sqlDataSource/dataStream/description", "setDescription", 0);
				dig.addCallMethod(root + "/sqlDataSource/dataStream/outputLength", "setOutputLength", 0);
				dig.addCallMethod(root + "/sqlDataSource/dataStream/weight", "setWeight", 0);
				dig.addCallMethod(root + "/sqlDataSource/dataStream/type", "setType", 0);
				dig.addCallMethod(root + "/sqlDataSource/dataStream/origin", "setOriginName", 0);
				dig.addCallMethod(root + "/sqlDataSource/dataStream/sql", "setSql", 0);
				dig.addCallMethod(root + "/sqlDataSource/dataStream/positionX", "setPositionX", 0);
				dig.addCallMethod(root + "/sqlDataSource/dataStream/positionY", "setPositionY", 0);
				
				dig.addObjectCreate(root  +"/sqlDataSource/dataStream/customtype", Type.class);
				dig.addCallMethod(root + "/sqlDataSource/dataStream/customtype/label", "setLabel", 0);
				dig.addSetNext(root + "/sqlDataSource/dataStream/customtype", "setCustomType");
				

				//genericFilters
				
				dig.addObjectCreate(root + "/sqlDataSource/dataStream/genericFilters/filter", Filter.class);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/genericFilters/filter/name", "setName", 0);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/genericFilters/filter/dataStream", "setDataStreamName", 0);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/genericFilters/filter/dataStreamElement","setDataStreamElementName" ,0);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/genericFilters/filter/value","addValue" ,0);
				dig.addSetNext(root + "/sqlDataSource/dataStream/genericFilters/filter", "addGenericFilter");
				
				dig.addObjectCreate(root + "/sqlDataSource/dataStream/genericFilters/complexFilter", ComplexFilter.class);	
					dig.addCallMethod(root + "/sqlDataSource/dataStream/genericFilters/complexFilter/name", "setName", 0);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/genericFilters/complexFilter/dataStream", "setDataStreamName", 0);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/genericFilters/complexFilter/dataStreamElement","setDataStreamElementName" ,0);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/genericFilters/complexFilter/value","setValue" ,0);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/genericFilters/complexFilter/operator","setOperator" ,0);
				dig.addSetNext(root + "/sqlDataSource/dataStream/genericFilters/complexFilter", "addGenericFilter");	
					
				dig.addObjectCreate(root + "/sqlDataSource/dataStream/genericFilters/sqlFilter", SqlQueryFilter.class);	
					dig.addCallMethod(root + "/sqlDataSource/dataStream/genericFilters/sqlFilter/name", "setName", 0);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/genericFilters/sqlFilter/dataStream", "setDataStreamName", 0);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/genericFilters/sqlFilter/dataStreamElement","setDataStreamElementName" ,0);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/genericFilters/sqlFilter/query","setQuery" ,0);
				dig.addSetNext(root + "/sqlDataSource/dataStream/genericFilters/sqlFilter", "addGenericFilter");	
	

				//filters
				//filters
				dig.addCallMethod(root + "/sqlDataSource/dataStream/filters", "addFilter", 2, new Class[]{ArrayList.class, IFilter.class});
				dig.addObjectCreate(root + "/sqlDataSource/dataStream/filters", ArrayList.class);
				dig.addCallMethod(root + "/sqlDataSource/dataStream/filters/groupName", "add", 0);
				
				
				
				
				dig.addObjectCreate(root + "/sqlDataSource/dataStream/filters/filter", Filter.class);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/filters/filter/name", "setName", 0);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/filters/filter/dataStream", "setDataStreamName", 0);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/filters/filter/dataStreamElement","setDataStreamElementName" ,0);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/filters/filter/value","addValue" ,0);

				
				dig.addObjectCreate(root + "/sqlDataSource/dataStream/filters/complexFilter", ComplexFilter.class);	
					dig.addCallMethod(root + "/sqlDataSource/dataStream/filters/complexFilter/name", "setName", 0);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/filters/complexFilter/dataStream", "setDataStreamName", 0);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/filters/complexFilter/dataStreamElement","setDataStreamElementName" ,0);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/filters/complexFilter/value","setValue" ,0);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/filters/complexFilter/operator","setOperator" ,0);
					
					
				dig.addObjectCreate(root + "/sqlDataSource/dataStream/filters/sqlFilter", SqlQueryFilter.class);	
					dig.addCallMethod(root + "/sqlDataSource/dataStream/filters/sqlFilter/name", "setName", 0);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/filters/sqlFilter/dataStream", "setDataStreamName", 0);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/filters/sqlFilter/dataStreamElement","setDataStreamElementName" ,0);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/filters/sqlFilter/query","setQuery" ,0);
					
	
					
				dig.addCallParam(root + "/sqlDataSource/dataStream/filters", 0, true);
				dig.addCallParam(root + "/sqlDataSource/dataStream/filters/filter", 1, true);
				dig.addCallParam(root + "/sqlDataSource/dataStream/filters/complexFilter", 1, true);
				dig.addCallParam(root + "/sqlDataSource/dataStream/filters/sqlFilter", 1, true);

				
//				dig.addCallMethod(root + "/sqlDataSource/dataStream/filters", "addFilter", 2, new Class[]{String.class, IFilter.class});
//				
//				dig.addObjectCreate(root + "/sqlDataSource/dataStream/filters/filter", Filter.class);
//					dig.addCallMethod(root + "/sqlDataSource/dataStream/filters/filter/name", "setName", 0);
//					dig.addCallMethod(root + "/sqlDataSource/dataStream/filters/filter/dataStream", "setDataStreamName", 0);
//					dig.addCallMethod(root + "/sqlDataSource/dataStream/filters/filter/dataStreamElement","setDataStreamElementName" ,0);
//					dig.addCallMethod(root + "/sqlDataSource/dataStream/filters/filter/value","addValue" ,0);
//					
//				dig.addObjectCreate(root + "/sqlDataSource/dataStream/filters/complexFilter", ComplexFilter.class);	
//					dig.addCallMethod(root + "/sqlDataSource/dataStream/filters/complexFilter/name", "setName", 0);
//					dig.addCallMethod(root + "/sqlDataSource/dataStream/filters/complexFilter/dataStream", "setDataStreamName", 0);
//					dig.addCallMethod(root + "/sqlDataSource/dataStream/filters/complexFilter/dataStreamElement","setDataStreamElementName" ,0);
//					dig.addCallMethod(root + "/sqlDataSource/dataStream/filters/complexFilter/value","setValue" ,0);
//					dig.addCallMethod(root + "/sqlDataSource/dataStream/filters/complexFilter/operator","setOperator" ,0);
//					
//					
//				dig.addObjectCreate(root + "/sqlDataSource/dataStream/filters/sqlFilter", SqlQueryFilter.class);	
//					dig.addCallMethod(root + "/sqlDataSource/dataStream/filters/sqlFilter/name", "setName", 0);
//					dig.addCallMethod(root + "/sqlDataSource/dataStream/filters/sqlFilter/dataStream", "setDataStreamName", 0);
//					dig.addCallMethod(root + "/sqlDataSource/dataStream/filters/sqlFilter/dataStreamElement","setDataStreamElementName" ,0);
//					dig.addCallMethod(root + "/sqlDataSource/dataStream/filters/sqlFilter/query","setQuery" ,0);
//					
//	
//					
//				dig.addCallParam(root + "/sqlDataSource/dataStream/filters/groupName", 0);
//				dig.addCallParam(root + "/sqlDataSource/dataStream/filters/filter", 1, true);
//				dig.addCallParam(root + "/sqlDataSource/dataStream/filters/complexFilter", 1, true);
//				dig.addCallParam(root + "/sqlDataSource/dataStream/filters/sqlFilter", 1, true);

				
				
				
			
				
				//dataStreamElement
				dig.addObjectCreate(root + "/sqlDataSource/dataStream/dataStreamElement", SQLDataStreamElement.class);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/dataStreamElement/name", "setName", 0);
//					dig.addCallMethod(root + "/sqlDataSource/dataStream/dataStreamElement/isVisible", "setVisible", 0);
					
					dig.addCallMethod(root + "/sqlDataSource/dataStream/dataStreamElement/customSecurity", "setCustomSecurity", 0);
					
					dig.addCallMethod(root + "/sqlDataSource/dataStream/dataStreamElement/visibility", "setVisible", 2);
					dig.addCallParam(root + "/sqlDataSource/dataStream/dataStreamElement/visibility/groupName", 0);
					dig.addCallParam(root + "/sqlDataSource/dataStream/dataStreamElement/visibility/isVisible", 1);
					
					dig.addCallMethod(root + "/sqlDataSource/dataStream/dataStreamElement/visibleGroups", "setGroupsVisible", 0);
					
					dig.addCallMethod(root + "/sqlDataSource/dataStream/dataStreamElement/isKpi", "setIsKpi", 0);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/dataStreamElement/indexable", "setIndexable", 0);
					
					dig.addCallMethod(root + "/sqlDataSource/dataStream/dataStreamElement/type", "setType", 0);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/dataStreamElement/d4cTypes", "setD4CTypes", 0);
//					dig.addCallMethod(root + "/sqlDataSource/dataStream/dataStreamElement/defaultBehavior", "setDefaultMeasureBehavior", 0);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/dataStreamElement/parentDimension", "setParentDimension", 0);
					
					dig.addCallMethod(root + "/sqlDataSource/dataStream/dataStreamElement/grant", "setGranted", 2);
					dig.addCallParam(root + "/sqlDataSource/dataStream/dataStreamElement/grant/groupName", 0);
					dig.addCallParam(root + "/sqlDataSource/dataStream/dataStreamElement/grant/isGranted", 1);
					
					dig.addCallMethod(root + "/sqlDataSource/dataStream/dataStreamElement/groupNames", "setGroupsGranted", 0);

					dig.addCallMethod(root + "/sqlDataSource/dataStream/dataStreamElement/outputname", "setOutputName", 3);
					dig.addCallParam(root + "/sqlDataSource/dataStream/dataStreamElement/outputname/country", 0);
					dig.addCallParam(root + "/sqlDataSource/dataStream/dataStreamElement/outputname/language", 1);
					dig.addCallParam(root + "/sqlDataSource/dataStream/dataStreamElement/outputname/value", 2);

					
					dig.addCallMethod(root + "/sqlDataSource/dataStream/dataStreamElement/description", "setDescription", 0);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/dataStreamElement/fontName", "setFontName", 0);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/dataStreamElement/textColor", "setTextColor", 0);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/dataStreamElement/backgroundColor", "setBackgroundColor", 0);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/dataStreamElement/origin", "setOriginName", 0);
				dig.addSetNext(root + "/sqlDataSource/dataStream/dataStreamElement", "addColumn");
				
				//formula
				dig.addObjectCreate(root + "/sqlDataSource/dataStream/formulaElement", ICalculatedElement.class);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/formulaElement/name", "setName", 0);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/formulaElement/description", "setDescription", 0);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/formulaElement/fontName", "setFontName", 0);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/formulaElement/textColor", "setTextColor", 0);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/formulaElement/backgroundColor", "setBackgroundColor", 0);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/formulaElement/formula", "setFormula", 0);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/formulaElement/classType", "setClassType", 0);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/formulaElement/type", "setType", 0);
					dig.addCallMethod(root + "/sqlDataSource/dataStream/formulaElement/isKpi", "setIsKpi", 0);
					
					dig.addCallMethod(root + "/sqlDataSource/dataStream/formulaElement/outputname", "setOutputName", 3);
					dig.addCallParam(root + "/sqlDataSource/dataStream/formulaElement/outputname/country", 0);
					dig.addCallParam(root + "/sqlDataSource/dataStream/formulaElement/outputname/language", 1);
					dig.addCallParam(root + "/sqlDataSource/dataStream/formulaElement/outputname/value", 2);
				dig.addSetNext(root + "/sqlDataSource/dataStream/formulaElement", "addCalculatedElement");

				
				
			dig.addSetNext(root + "/sqlDataSource/dataStream", "add");
			
			//relation
			dig.addObjectCreate(root + "/sqlDataSource/relation", SQLRelation.class);
				dig.addCallMethod(root + "/sqlDataSource/relation/leftDataStream", "setLeftTableName", 0);
				dig.addCallMethod(root + "/sqlDataSource/relation/rightDataStream", "setRightTableName", 0);
				dig.addCallMethod(root + "/sqlDataSource/relation/cardinality", "setCardinality", 0);
				dig.addCallMethod(root + "/sqlDataSource/relation/finalRelation", "setFinalRelation", 0);

				
				dig.addObjectCreate(root + "/sqlDataSource/relation/join", Join.class);
					dig.addCallMethod(root + "/sqlDataSource/relation/join/left", "setLeftName", 0);
					dig.addCallMethod(root + "/sqlDataSource/relation/join/right", "setRightName", 0);
					dig.addCallMethod(root + "/sqlDataSource/relation/join/outer", "setOuter", 0);
					dig.addCallMethod(root + "/sqlDataSource/relation/join/onStatement", "setOnStatement", 0);
					
				dig.addSetNext(root + "/sqlDataSource/relation/join", "addJoin");
				
			dig.addSetNext(root + "/sqlDataSource/relation", "addRelation");

		dig.addSetNext(root + "/sqlDataSource", "addDataSource");
		
		
		//multiDataSourceRelations
		dig.addObjectCreate(root + "/multiRelation", MultiDSRelation.class);
			dig.addCallMethod(root + "/multiRelation/leftDataSource", "setLeftDataSourceName", 0);
			dig.addCallMethod(root + "/multiRelation/leftDataStream", "setLeftDataStreamName", 0);
			dig.addCallMethod(root + "/multiRelation/rightDataSource", "setRightDataSourceName", 0);
			dig.addCallMethod(root + "/multiRelation/rightDataStream", "setRightDataStreamName", 0);
			dig.addCallMethod(root + "/multiRelation/cardinality", "setCardinality", 0);
			dig.addObjectCreate(root + "/multiRelation/join", Join.class);
				dig.addCallMethod(root + "/multiRelation/join/left", "setLeftName", 0);
				dig.addCallMethod(root + "/multiRelation/join/right", "setRightName", 0);
				dig.addCallMethod(root + "/multiRelation/join/outer", "setOuter", 0);
			dig.addSetNext(root + "/multiRelation/join", "addJoin");

		dig.addSetNext(root + "/multiRelation", "addMultiRelation");
		
		//business Table businessModel
		dig.addObjectCreate(root + "/businessModel", BusinessModel.class);
			dig.addCallMethod(root + "/businessModel/name", "setName", 0);
			
			dig.addCallMethod(root + "/businessModel/description", "setDescription", 0);
			//grants
			dig.addCallMethod(root + "/businessModel/grant", "setGranted", 2);
			dig.addCallParam(root + "/businessModel/grant/groupName", 0);
			dig.addCallParam(root + "/businessModel/grant/isGranted", 1);
			
			dig.addCallMethod(root + "/businessModel/groupNames", "setGroupsGranted", 0);

			dig.addCallMethod(root + "/businessModel/outputname", "setOutputName", 3);
			dig.addCallParam(root + "/businessModel/outputname/country", 0);
			dig.addCallParam(root + "/businessModel/outputname/language", 1);
			dig.addCallParam(root + "/businessModel/outputname/value", 2);

			
			dig.addObjectCreate(root + "/businessModel/businessTable", SQLBusinessTable.class);
				dig.addCallMethod(root + "/businessModel/businessTable/drillable", "setIsDrillable", 0);
				dig.addCallMethod(root + "/businessModel/businessTable/editable", "setEditable", 0);
				dig.addCallMethod(root + "/businessModel/businessTable/name", "setName", 0);
				dig.addCallMethod(root + "/businessModel/businessTable/parent", "setParentName", 0);
				dig.addCallMethod(root + "/businessModel/businessTable/description", "setDescription", 0);
				dig.addCallMethod(root + "/businessModel/businessTable/positionX", "setPositionX", 0);
				dig.addCallMethod(root + "/businessModel/businessTable/positionY", "setPositionY", 0);
				
				dig.addCallMethod(root + "/businessModel/businessTable/column", "addColumnName", 2);
				dig.addCallParam(root + "/businessModel/businessTable/column/dataStreamName", 0);
				dig.addCallParam(root + "/businessModel/businessTable/column/dataStreamElementName", 1);
				
				dig.addCallMethod(root + "/businessModel/businessTable/order", "addOrderName", 3);
				dig.addCallParam(root + "/businessModel/businessTable/order/dataStreamName", 0);
				dig.addCallParam(root + "/businessModel/businessTable/order/dataStreamElementName", 1);
				dig.addCallParam(root + "/businessModel/businessTable/order/position", 2);

				
				//grants
				dig.addCallMethod(root + "/businessModel/businessTable/grant", "setGranted", 2);
				dig.addCallParam(root + "/businessModel/businessTable/grant/groupName", 0);
				dig.addCallParam(root + "/businessModel/businessTable/grant/isGranted", 1);
				
				dig.addCallMethod(root + "/businessModel/businessTable/groupNames", "setGroupsGranted", 0);

				//locales Names
				dig.addCallMethod(root + "/businessModel/businessTable/outputname", "setOutputName", 3);
				dig.addCallParam(root + "/businessModel/businessTable/outputname/country", 0);
				dig.addCallParam(root + "/businessModel/businessTable/outputname/language", 1);
				dig.addCallParam(root + "/businessModel/businessTable/outputname/value", 2);

				
				//filters
				dig.addCallMethod(root + "/businessModel/businessTable/filters", "addFilter", 2, new Class[]{ArrayList.class, IFilter.class});
				dig.addObjectCreate(root + "/businessModel/businessTable/filters", ArrayList.class);
				dig.addCallMethod(root + "/businessModel/businessTable/filters/groupName", "add", 0);
				
				
				
				
				dig.addObjectCreate(root + "/businessModel/businessTable/filters/filter", Filter.class);
					dig.addCallMethod(root + "/businessModel/businessTable/filters/filter/name", "setName", 0);
					dig.addCallMethod(root + "/businessModel/businessTable/filters/filter/dataStream", "setDataStreamName", 0);
					dig.addCallMethod(root + "/businessModel/businessTable/filters/filter/dataStreamElement","setDataStreamElementName" ,0);
					dig.addCallMethod(root + "/businessModel/businessTable/filters/filter/value","addValue" ,0);
					
				dig.addObjectCreate(root + "/businessModel/businessTable/filters/complexFilter", ComplexFilter.class);	
					dig.addCallMethod(root + "/businessModel/businessTable/filters/complexFilter/name", "setName", 0);
					dig.addCallMethod(root + "/businessModel/businessTable/filters/complexFilter/dataStream", "setDataStreamName", 0);
					dig.addCallMethod(root + "/businessModel/businessTable/filters/complexFilter/dataStreamElement","setDataStreamElementName" ,0);
					dig.addCallMethod(root + "/businessModel/businessTable/filters/complexFilter/value","setValue" ,0);
					dig.addCallMethod(root + "/businessModel/businessTable/filters/complexFilter/operator","setOperator" ,0);
					
					
				dig.addObjectCreate(root + "/businessModel/businessTable/filters/sqlFilter", SqlQueryFilter.class);	
					dig.addCallMethod(root + "/businessModel/businessTable/filters/sqlFilter/name", "setName", 0);
					dig.addCallMethod(root + "/businessModel/businessTable/filters/sqlFilter/dataStream", "setDataStreamName", 0);
					dig.addCallMethod(root + "/businessModel/businessTable/filters/sqlFilter/dataStreamElement","setDataStreamElementName" ,0);
					dig.addCallMethod(root + "/businessModel/businessTable/filters/sqlFilter/query","setQuery" ,0);
					
	
					
				dig.addCallParam(root + "/businessModel/businessTable/filters", 0, true);
				dig.addCallParam(root + "/businessModel/businessTable/filters/filter", 1, true);
				dig.addCallParam(root + "/businessModel/businessTable/filters/complexFilter", 1, true);
				dig.addCallParam(root + "/businessModel/businessTable/filters/sqlFilter", 1, true);

				
				
			dig.addSetNext(root + "/businessModel/businessTable", "addBusinessTable");
			
			
			
			dig.addObjectCreate(root + "/businessModel/unitedOlapBusinessTable", UnitedOlapBusinessTable.class);
			dig.addCallMethod(root + "/businessModel/unitedOlapBusinessTable/drillable", "setIsDrillable", 0);
			dig.addCallMethod(root + "/businessModel/unitedOlapBusinessTable/editable", "setEditable", 0);
			dig.addCallMethod(root + "/businessModel/unitedOlapBusinessTable/name", "setName", 0);
			dig.addCallMethod(root + "/businessModel/unitedOlapBusinessTable/parent", "setParentName", 0);
			dig.addCallMethod(root + "/businessModel/unitedOlapBusinessTable/description", "setDescription", 0);
			dig.addCallMethod(root + "/businessModel/unitedOlapBusinessTable/positionX", "setPositionX", 0);
			dig.addCallMethod(root + "/businessModel/unitedOlapBusinessTable/positionY", "setPositionY", 0);
			
			dig.addCallMethod(root + "/businessModel/unitedOlapBusinessTable/column", "addColumnName", 2);
			dig.addCallParam(root + "/businessModel/unitedOlapBusinessTable/column/dataStreamName", 0);
			dig.addCallParam(root + "/businessModel/unitedOlapBusinessTable/column/dataStreamElementName", 1);
			
			dig.addCallMethod(root + "/businessModel/unitedOlapBusinessTable/order", "addOrderName", 3);
			dig.addCallParam(root + "/businessModel/unitedOlapBusinessTable/order/dataStreamName", 0);
			dig.addCallParam(root + "/businessModel/unitedOlapBusinessTable/order/dataStreamElementName", 1);
			dig.addCallParam(root + "/businessModel/unitedOlapBusinessTable/order/position", 2);

			
			//grants
			dig.addCallMethod(root + "/businessModel/unitedOlapBusinessTable/grant", "setGranted", 2);
			dig.addCallParam(root + "/businessModel/unitedOlapBusinessTable/grant/groupName", 0);
			dig.addCallParam(root + "/businessModel/unitedOlapBusinessTable/grant/isGranted", 1);
			
			dig.addCallMethod(root + "/businessModel/unitedOlapBusinessTable/groupNames", "setGroupsGranted", 0);

			//locales Names
			dig.addCallMethod(root + "/businessModel/unitedOlapBusinessTable/outputname", "setOutputName", 3);
			dig.addCallParam(root + "/businessModel/unitedOlapBusinessTable/outputname/country", 0);
			dig.addCallParam(root + "/businessModel/unitedOlapBusinessTable/outputname/language", 1);
			dig.addCallParam(root + "/businessModel/unitedOlapBusinessTable/outputname/value", 2);

			
			//filters
			dig.addCallMethod(root + "/businessModel/unitedOlapBusinessTable/filters", "addFilter", 2, new Class[]{ArrayList.class, IFilter.class});
			dig.addObjectCreate(root + "/businessModel/unitedOlapBusinessTable/filters", ArrayList.class);
			dig.addCallMethod(root + "/businessModel/unitedOlapBusinessTable/filters/groupName", "add", 0);
			
			
			
			
			dig.addObjectCreate(root + "/businessModel/unitedOlapBusinessTable/filters/filter", Filter.class);
				dig.addCallMethod(root + "/businessModel/unitedOlapBusinessTable/filters/filter/name", "setName", 0);
				dig.addCallMethod(root + "/businessModel/unitedOlapBusinessTable/filters/filter/dataStream", "setDataStreamName", 0);
				dig.addCallMethod(root + "/businessModel/unitedOlapBusinessTable/filters/filter/dataStreamElement","setDataStreamElementName" ,0);
				dig.addCallMethod(root + "/businessModel/unitedOlapBusinessTable/filters/filter/value","addValue" ,0);
				
			dig.addObjectCreate(root + "/businessModel/unitedOlapBusinessTable/filters/complexFilter", ComplexFilter.class);	
				dig.addCallMethod(root + "/businessModel/unitedOlapBusinessTable/filters/complexFilter/name", "setName", 0);
				dig.addCallMethod(root + "/businessModel/unitedOlapBusinessTable/filters/complexFilter/dataStream", "setDataStreamName", 0);
				dig.addCallMethod(root + "/businessModel/unitedOlapBusinessTable/filters/complexFilter/dataStreamElement","setDataStreamElementName" ,0);
				dig.addCallMethod(root + "/businessModel/unitedOlapBusinessTable/filters/complexFilter/value","setValue" ,0);
				dig.addCallMethod(root + "/businessModel/unitedOlapBusinessTable/filters/complexFilter/operator","setOperator" ,0);
				
				
			dig.addObjectCreate(root + "/businessModel/unitedOlapBusinessTable/filters/sqlFilter", SqlQueryFilter.class);	
				dig.addCallMethod(root + "/businessModel/unitedOlapBusinessTable/filters/sqlFilter/name", "setName", 0);
				dig.addCallMethod(root + "/businessModel/unitedOlapBusinessTable/filters/sqlFilter/dataStream", "setDataStreamName", 0);
				dig.addCallMethod(root + "/businessModel/unitedOlapBusinessTable/filters/sqlFilter/dataStreamElement","setDataStreamElementName" ,0);
				dig.addCallMethod(root + "/businessModel/unitedOlapBusinessTable/filters/sqlFilter/query","setQuery" ,0);
				

				
			dig.addCallParam(root + "/businessModel/unitedOlapBusinessTable/filters", 0, true);
			dig.addCallParam(root + "/businessModel/unitedOlapBusinessTable/filters/filter", 1, true);
			dig.addCallParam(root + "/businessModel/unitedOlapBusinessTable/filters/complexFilter", 1, true);
			dig.addCallParam(root + "/businessModel/unitedOlapBusinessTable/filters/sqlFilter", 1, true);

			
			
		dig.addSetNext(root + "/businessModel/unitedOlapBusinessTable", "addBusinessTable");
			
			
			
			
			
			
//			dig.addObjectCreate(root + "/businessModel/olapBusinessTable", OLAPBusinessTable.class);
//				dig.addCallMethod(root + "/businessModel/olapBusinessTable/name", "setName", 0);
//				dig.addCallMethod(root + "/businessModel/olapBusinessTable/description", "setDescription", 0);
//
//				dig.addCallMethod(root + "/businessModel/olapBusinessTable/olapDataSourceName", "setDataSourceName", 0);
//				//grants
//				dig.addCallMethod(root + "/businessModel/olapBusinessTable/grant", "setGranted", 2);
//				dig.addCallParam(root + "/businessModel/olapBusinessTable/grant/groupName", 0);
//				dig.addCallParam(root + "/businessModel/olapBusinessTable/grant/isGranted", 1);
//
//			
//			dig.addSetNext(root + "/businessModel/olapBusinessTable", "addBusinessTable");
			
			

		
			//LoV
			dig.addObjectCreate(root + "/businessModel/lov", ListOfValue.class);
				dig.addCallMethod(root + "/businessModel/lov/name", "setName", 0);
				dig.addCallMethod(root + "/businessModel/lov/dataStream", "setDataStreamName", 0);
				dig.addCallMethod(root + "/businessModel/lov/dataStreamElement","setDataStreamElementName" ,0);
				
				//grants
				dig.addCallMethod(root + "/businessModel/lov/grant", "setGranted", 2);
				dig.addCallParam(root + "/businessModel/lov/grant/groupName", 0);
				dig.addCallParam(root + "/businessModel/lov/grant/isGranted", 1);
				
				dig.addCallMethod(root + "/businessModel/lov/groupNames", "setGroupsGranted", 0);
				
				dig.addCallMethod(root + "/businessModel/lov/outputname", "setOutputName", 3);
				dig.addCallParam(root + "/businessModel/lov/outputname/country", 0);
				dig.addCallParam(root + "/businessModel/lov/outputname/language", 1);
				dig.addCallParam(root + "/businessModel/lov/outputname/value", 2);
			dig.addSetNext(root + "/businessModel/lov", "addResource");
			
			
			//sqlFiler
			dig.addObjectCreate(root + "/businessModel/sqlFilter", SqlQueryFilter.class);
				dig.addCallMethod(root + "/businessModel/sqlFilter/name", "setName", 0);
				dig.addCallMethod(root + "/businessModel/sqlFilter/dataStream", "setDataStreamName", 0);
				dig.addCallMethod(root + "/businessModel/sqlFilter/dataStreamElement","setDataStreamElementName" ,0);
				dig.addCallMethod(root + "/businessModel/sqlFilter/query","setQuery" ,0);
				
				//grants
				dig.addCallMethod(root + "/businessModel/sqlFilter/grant", "setGranted", 2);
				dig.addCallParam(root + "/businessModel/sqlFilter/grant/groupName", 0);
				dig.addCallParam(root + "/businessModel/sqlFilter/grant/isGranted", 1);
				
				dig.addCallMethod(root + "/businessModel/sqlFilter/groupNames", "setGroupsGranted", 0);
				
				dig.addCallMethod(root + "/businessModel/sqlFilter/outputname", "setOutputName", 3);
				dig.addCallParam(root + "/businessModel/sqlFilter/outputname/country", 0);
				dig.addCallParam(root + "/businessModel/sqlFilter/outputname/language", 1);
				dig.addCallParam(root + "/businessModel/sqlFilter/outputname/value", 2);
			
			dig.addSetNext(root + "/businessModel/sqlFilter", "addResource");
			
			//Filter
			dig.addObjectCreate(root + "/businessModel/filter", Filter.class);
				dig.addCallMethod(root + "/businessModel/filter/name", "setName", 0);
				dig.addCallMethod(root + "/businessModel/filter/dataStream", "setDataStreamName", 0);
				dig.addCallMethod(root + "/businessModel/filter/dataStreamElement","setDataStreamElementName" ,0);
				dig.addCallMethod(root + "/businessModel/filter/value","addValue" ,0);
				
				//grants
				dig.addCallMethod(root + "/businessModel/filter/grant", "setGranted", 2);
				dig.addCallParam(root + "/businessModel/filter/grant/groupName", 0);
				dig.addCallParam(root + "/businessModel/filter/grant/isGranted", 1);
				
				dig.addCallMethod(root + "/businessModel/filter/groupNames", "setGroupsGranted", 0);
				
				dig.addCallMethod(root + "/businessModel/filter/outputname", "setOutputName", 3);
				dig.addCallParam(root + "/businessModel/filter/outputname/country", 0);
				dig.addCallParam(root + "/businessModel/filter/outputname/language", 1);
				dig.addCallParam(root + "/businessModel/filter/outputname/value", 2);
				
			dig.addSetNext(root + "/businessModel/filter", "addResource");
			//Dimension
			dig.addObjectCreate(root + "/businessModel/dimension", FmdtDimension.class);
				dig.addCallMethod(root + "/businessModel/dimension/name", "setName", 0);
				dig.addCallMethod(root + "/businessModel/dimension/dataSource", "setDataSourceName", 0);
				dig.addCallMethod(root + "/businessModel/dimension/level", "addLevel", 2);
				dig.addCallParam(root + "/businessModel/dimension/level/dataStream", 0);
				dig.addCallParam(root + "/businessModel/dimension/level/dataStreamElement", 1);
			dig.addSetNext(root + "/businessModel/dimension", "addResource");
			
			//MEASURE
			dig.addObjectCreate(root + "/businessModel/measure", FmdtMeasure.class);
				dig.addCallMethod(root + "/businessModel/measure/name", "setName", 0);
				dig.addCallMethod(root + "/businessModel/measure/dataSource", "setDataSourceName", 0);
				dig.addCallMethod(root + "/businessModel/measure/script", "setScript", 0);
				
				//grants
				dig.addCallMethod(root + "/businessModel/measure/grant", "setGranted", 2);
				dig.addCallParam(root + "/businessModel/measure/grant/groupName", 0);
				dig.addCallParam(root + "/businessModel/measure/grant/isGranted", 1);
				
				dig.addCallMethod(root + "/businessModel/measure/groupNames", "setGroupsGranted", 0);

			dig.addSetNext(root + "/businessModel/measure", "addResource");
			
			//Prompt
			dig.addObjectCreate(root + "/businessModel/prompt", Prompt.class);
				dig.addCallMethod(root + "/businessModel/prompt/name", "setName", 0);
				dig.addCallMethod(root + "/businessModel/prompt/question", "setQuestion", 0);
				dig.addCallMethod(root + "/businessModel/prompt/operator", "setOperator", 0);
				
				dig.addCallMethod(root + "/businessModel/prompt/originDataStream", "setOriginDataStreamName", 0);
				dig.addCallMethod(root + "/businessModel/prompt/originDataStreamElement","setOriginDataStreamElementName" ,0);
				
				dig.addCallMethod(root + "/businessModel/prompt/destinationDataStream", "setGotoDataStreamName", 0);
				dig.addCallMethod(root + "/businessModel/prompt/destinationDataStreamElement","setGotoDataStreamElementName" ,0);
				
				dig.addCallMethod(root + "/businessModel/prompt/destinationSql", "setGotoSql", 0);
				dig.addCallMethod(root + "/businessModel/prompt/destinationType","setPromptType" ,0);
				
				//grants
				dig.addCallMethod(root + "/businessModel/prompt/grant", "setGranted", 2);
				dig.addCallParam(root + "/businessModel/prompt/grant/groupName", 0);
				dig.addCallParam(root + "/businessModel/prompt/grant/isGranted", 1);
				
				dig.addCallMethod(root + "/businessModel/prompt/groupNames", "setGroupsGranted", 0);

				
				dig.addCallMethod(root + "/businessModel/prompt/outputname", "setOutputName", 3);
				dig.addCallParam(root + "/businessModel/prompt/outputname/country", 0);
				dig.addCallParam(root + "/businessModel/prompt/outputname/language", 1);
				dig.addCallParam(root + "/businessModel/prompt/outputname/value", 2);
				
				dig.addCallMethod(root + "/businessModel/prompt/parentprompt", "setParentPromptName", 0);
				
			dig.addSetNext(root + "/businessModel/prompt", "addResource");

			dig.addObjectCreate(root + "/businessModel/complexFilter", ComplexFilter.class);	
			dig.addCallMethod(root + "/businessModel/complexFilter/name", "setName", 0);
			dig.addCallMethod(root + "/businessModel/complexFilter/dataStream", "setDataStreamName", 0);
			dig.addCallMethod(root + "/businessModel/complexFilter/dataStreamElement","setDataStreamElementName" ,0);
			dig.addCallMethod(root + "/businessModel/complexFilter/value","setValue" ,0);
			dig.addCallMethod(root + "/businessModel/complexFilter/operator","setOperator" ,0);
			//grants
			dig.addCallMethod(root + "/businessModel/complexFilter/grant", "setGranted", 2);
			dig.addCallParam(root + "/businessModel/complexFilter/grant/groupName", 0);
			dig.addCallParam(root + "/businessModel/complexFilter/grant/isGranted", 1);
			
			dig.addCallMethod(root + "/businessModel/complexFilter/groupNames", "setGroupsGranted", 0);

			dig.addCallMethod(root + "/businessModel/complexFilter/outputname", "setOutputName", 3);
			dig.addCallParam(root + "/businessModel/complexFilter/outputname/country", 0);
			dig.addCallParam(root + "/businessModel/complexFilter/outputname/language", 1);
			dig.addCallParam(root + "/businessModel/complexFilter/outputname/value", 2);
			
		dig.addSetNext(root + "/businessModel/complexFilter", "addResource");
		
		
		dig.addObjectCreate(root + "/businessModel/relationStrategy", RelationStrategy.class);	
		
		dig.addCallMethod(root + "/businessModel/relationStrategy/name", "setName", 0);
		dig.addCallMethod(root + "/businessModel/relationStrategy/table", "addTableName", 0);
		dig.addCallMethod(root + "/businessModel/relationStrategy/relationKey", "addRelationKey", 0);
		
		dig.addSetNext(root + "/businessModel/relationStrategy", "addRelationStrategy");
		
			
			//businessPackage
			dig.addObjectCreate(root + "/businessModel/businessPackage", BusinessPackage.class);
				dig.addCallMethod(root + "/businessModel/businessPackage/name", "setName", 0);
				dig.addCallMethod(root + "/businessModel/businessPackage/explorableTables", "addAccessible", 0);
				
				dig.addCallMethod(root + "/businessModel/businessPackage/explorable", "setExplorable", 0);
				dig.addCallMethod(root + "/businessModel/businessPackage/description", "setDescription", 0);
				dig.addCallMethod(root + "/businessModel/businessPackage/businessTableName", "addBusinessTableName", 0);
				dig.addCallMethod(root + "/businessModel/businessPackage/resourceName","addResourceName" ,0);
				
				//grants
				dig.addCallMethod(root + "/businessModel/businessPackage/grant", "setGranted", 2);
				dig.addCallParam(root + "/businessModel/businessPackage/grant/groupName", 0);
				dig.addCallParam(root + "/businessModel/businessPackage/grant/isGranted", 1);
				
				dig.addCallMethod(root + "/businessModel/businessPackage/groupNames", "setGroupsGranted", 0);

				
				dig.addCallMethod(root + "/businessModel/businessPackage/outputname", "setOutputName", 3);
				dig.addCallParam(root + "/businessModel/businessPackage/outputname/country", 0);
				dig.addCallParam(root + "/businessModel/businessPackage/outputname/language", 1);
				dig.addCallParam(root + "/businessModel/businessPackage/outputname/value", 2);
				
				
				//order
				dig.addCallMethod(root + "/businessModel/businessPackage/order", "order", 2);
				dig.addCallParam(root + "/businessModel/businessPackage/order/businessTableName", 0);
				dig.addCallParam(root + "/businessModel/businessPackage/order/position", 1);
				
				dig.addObjectCreate(root + "/businessModel/businessPackage/savedQueries/savedQuery", SavedQuery.class);
					dig.addCallMethod(root + "/businessModel/businessPackage/savedQueries/savedQuery/name", "setName", 0);
					dig.addCallMethod(root + "/businessModel/businessPackage/savedQueries/savedQuery/description", "setDescription", 0);
					dig.addCallMethod(root + "/businessModel/businessPackage/savedQueries/savedQuery/queryXml", "setQuery", 0);
					dig.addCallMethod(root + "/businessModel/businessPackage/savedQueries/savedQuery/chartXml", "setChartXml", 0);
				dig.addSetNext(root + "/businessModel/businessPackage/savedQueries/savedQuery", "addSavedQuery");
		
				
			dig.addSetNext(root + "/businessModel/businessPackage", "addBusinessPackage");
			
			
		dig.addObjectCreate(root + "/businessModel/unitedOlapBusinessPackage", UnitedOlapBusinessPackage.class);
			dig.addCallMethod(root + "/businessModel/unitedOlapBusinessPackage/name", "setName", 0);
			dig.addCallMethod(root + "/businessModel/unitedOlapBusinessPackage/explorableTables", "addAccessible", 0);
			
			dig.addCallMethod(root + "/businessModel/unitedOlapBusinessPackage/explorable", "setExplorable", 0);
			dig.addCallMethod(root + "/businessModel/unitedOlapBusinessPackage/description", "setDescription", 0);
			dig.addCallMethod(root + "/businessModel/unitedOlapBusinessPackage/businessTableName", "addBusinessTableName", 0);
			dig.addCallMethod(root + "/businessModel/unitedOlapBusinessPackage/resourceName","addResourceName" ,0);
			
			//grants
			dig.addCallMethod(root + "/businessModel/unitedOlapBusinessPackage/grant", "setGranted", 2);
			dig.addCallParam(root + "/businessModel/unitedOlapBusinessPackage/grant/groupName", 0);
			dig.addCallParam(root + "/businessModel/unitedOlapBusinessPackage/grant/isGranted", 1);
			
			dig.addCallMethod(root + "/businessModel/unitedOlapBusinessPackage/groupNames", "setGroupsGranted", 0);

			
			dig.addCallMethod(root + "/businessModel/unitedOlapBusinessPackage/outputname", "setOutputName", 3);
			dig.addCallParam(root + "/businessModel/unitedOlapBusinessPackage/outputname/country", 0);
			dig.addCallParam(root + "/businessModel/unitedOlapBusinessPackage/outputname/language", 1);
			dig.addCallParam(root + "/businessModel/unitedOlapBusinessPackage/outputname/value", 2);
			
			
			//order
			dig.addCallMethod(root + "/businessModel/unitedOlapBusinessPackage/order", "order", 2);
			dig.addCallParam(root + "/businessModel/unitedOlapBusinessPackage/order/businessTableName", 0);
			dig.addCallParam(root + "/businessModel/unitedOlapBusinessPackage/order/position", 1);
	
			
		dig.addSetNext(root + "/businessModel/unitedOlapBusinessPackage", "addBusinessPackage");
			
			
			
			//relation
			dig.addObjectCreate(root + "/businessModel/relation", SQLRelation.class);
				dig.addCallMethod(root + "/businessModel/relation/leftDataStream", "setLeftTableName", 0);
				dig.addCallMethod(root + "/businessModel/relation/rightDataStream", "setRightTableName", 0);
				dig.addCallMethod(root + "/businessModel/relation/cardinality", "setCardinality", 0);
				dig.addCallMethod(root + "/businessModel/relation/finalRelation", "setFinalRelation", 0);
				dig.addObjectCreate(root + "/businessModel/relation/join", Join.class);
					dig.addCallMethod(root + "/businessModel/relation/join/left", "setLeftName", 0);
					dig.addCallMethod(root + "/businessModel/relation/join/right", "setRightName", 0);
					dig.addCallMethod(root + "/businessModel/relation/join/outer", "setOuter", 0);
					dig.addCallMethod(root + "/businessModel/relation/join/onStatement", "setOnStatement", 0);
				dig.addSetNext(root + "/businessModel/relation/join", "addJoin");
				
			dig.addSetNext(root + "/businessModel/relation", "addRelation");

		dig.addSetNext(root + "/businessModel", "addBusinessModel");
		
		
		dig.addObjectCreate(root  +"/variable", Variable.class);
		dig.addCallMethod(root + "/variable/name", "setName", 0);
		dig.addCallMethod(root + "/variable/type", "setType", 0);
		dig.addSetNext(root + "/variable", "addVariable");
		
		dig.addObjectCreate(root  +"/script", Script.class);
		dig.addCallMethod(root + "/script/name", "setName", 0);
		dig.addCallMethod(root + "/script/description", "setDescription", 0);
		dig.addCallMethod(root + "/script/definition", "setDefinition", 0);
		dig.addCallMethod(root + "/script/datasource", "setDatasource", 0);
		dig.addSetNext(root + "/script", "addScript");
		
		dig.addObjectCreate(root  +"/customtype", Type.class);
		dig.addCallMethod(root + "/customtype/label", "setLabel", 0);
		dig.addSetNext(root + "/customtype", "addType");
	}
	
	
	@Deprecated
	public MetaData getModel(IRepositoryApi sock) throws BuilderException, Exception{
		return getModel(sock, null);
	}
	
	public MetaData getModel(IRepositoryApi sock, String groupName) throws BuilderException, Exception{
		
		/*
		 * rebuild references between objects
		 */
		try{
			
			errorBuffer = new StringBuffer();
			// set the DataStream Origins
			for(AbstractDataSource ds : model.getDataSources()){
				for(Relation r : ds.getRelations()){
					r.setDataStreams(ds);
					
					for(Join j : r.getJoins()){
						j.setElements(ds.getDataStreamNamed(r.getLeftTableName()),
										ds.getDataStreamNamed(r.getRightTableName()));
					}
				}
				
			}
			
			//Relation extra DS
			for(MultiDSRelation r : model.getMultiDataSourceRelations()){
				r.setDatas(model);
			}
			
			

			for(IBusinessModel m : model.getBusinessModels()){
				//set the BusinessTables
				for(IBusinessTable t : ((BusinessModel)m).getBusinessTables()){
					//find the dataStream
					if (t instanceof AbstractBusinessTable){
						AbstractBusinessTable bT = (AbstractBusinessTable)t;
						for(String tName : bT.getColumnsNames().keySet()){
							IDataStream table = findDataStream(model, tName);
							if (table == null){
								errorBuffer.append("-Error for BusinessTable " +  t.getName() + " in Model " + m.getName() + " contains columns based on the  DataStream " + tName + " which does not exist\n");
								continue;
							}
							for(String cName : bT.getColumnsNames().get(tName)){
								try{
									Integer i = bT.getOrder(tName, cName);
									
									bT.addColumn(table.getElementNamed(cName));
									bT.order(table.getElementNamed(cName), i == null ? -1 : i );
									if (table.getElementNamed(cName) == null){
										errorBuffer.append("-Error for BusinessTable " +  t.getName() + " in Model " + m.getName() + " cant find column " + cName + " from datatstream" + tName + " \n");
									}
								}catch(Exception ex){
									ex.printStackTrace();
									if (table.getElementNamed(cName) == null){
										errorBuffer.append("-Error for BusinessTable " +  t.getName() + " in Model " + m.getName() + " cant find column " + cName + " from datatstream" + tName + " \n");
									}
								}
								
								
								
							}
						}
					}
					
				}
				//looking for the DataSource coming from the businessModel
				//businessTables may be empty
				IDataSource ds = null;
				for(IBusinessTable t : ((BusinessModel)m).getBusinessTables()){
					for(IDataStreamElement c : t.getColumns("none")){
						ds = c.getDataStream().getDataSource();
						if (ds != null){
							break;
						}
					}
					if (ds != null){
						break;
					}
					else if (!t.getChilds("none").isEmpty()){
						ds = ((SQLBusinessTable)t).getDataSource();
						if (ds != null){
							break;
						}
					}
				}
				for(Relation r : ((BusinessModel)m).getRelations()){
					
					try{
						r.setDataStreams(ds);
						
						for(Join j : r.getJoins()){
							j.setElements(ds.getDataStreamNamed(r.getLeftTableName()),
											ds.getDataStreamNamed(r.getRightTableName()));
						}
					}catch(Exception e){
						e.printStackTrace();
						errorBuffer.append("-Error for Relation in Model " + m.getName() + " where between" + r.getLeftTableName() + " and " + r.getRightTableName() + " : " + e.getMessage() + "\n");
					}
					
				}
				
				//set the listsOfValues
				for(IResource l : ((BusinessModel)m).getResources()){
					
					model.addResource(l);
				}
				
				//set the packages
				for(IBusinessPackage p : m.getBusinessPackages("none")){
					for(String s : p.getBusinessTableName()){
						p.addBusinessTable(((BusinessModel)m).getBusinessTable(s));
						
					}
					p.cleanBusinessTableContent();
					/*
					 * order
					 */
					for(String s : p.getBusinessTableName()){
						Integer i = p.getOrderPosition(s); 
						p.order(s, i);
					}
					for(String s : p.getResourceName()){
						
						p.addResource(model.getResource(s));
					}
				}	
			}
			
			
			//find the relations between businessTables
			for(IBusinessModel m : model.getBusinessModels()){
				((BusinessModel)m).updateRelations(true);
			}


			
			
			//build tree structure of businesstables

			for(IBusinessModel m : model.getBusinessModels()){
				List<IBusinessTable> orphans = getOrphans(((BusinessModel)m).getBusinessTables());
				
				for(IBusinessTable t : orphans){
					((BusinessModel)m).removeBusinessTable(t);
				}
				
				for(IBusinessTable t : orphans){
					IBusinessTable parent = ((BusinessModel)m).getBusinessTable(((AbstractBusinessTable)t).getParentName());
					if (parent != null){
						((AbstractBusinessTable)parent).addChild(t);
					}
					else{
						setParent((AbstractBusinessTable)t, orphans);
					}
				}
				
				
			}
		
			try{
				
				if (builder != null){
					builder.build(model, sock, groupName);
					errorBuffer = new StringBuffer();
					errorBuffer.append(builder.getErrorsBuffer());
				}
				else{
					builder = new MetaDataBuilder(sock);
					builder.clean(model);
					errorBuffer = new StringBuffer();
					errorBuffer.append(builder.getErrorsBuffer());
					
				}
				
				
				return model;
			}catch(Exception e){
				e.printStackTrace();
				Log.error("Unable to rebuild datasource ", e);
				throw e;
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}
	
	protected void setParent(AbstractBusinessTable orphan, List<IBusinessTable> list){
		IBusinessTable parent = null;
		
		for(IBusinessTable t : list){
			if (t.getName().equals(orphan.getParentName())){
				((AbstractBusinessTable)t).addChild(orphan);
				return;
			}
			else{
				setParent(orphan, t.getChilds("none"));
			}
		}
		
//		if (orphan.getParent() == null){
//			System.err.println("unable to find parent for a businesstable (tableName = " +orphan.getName() + " parentName=" + orphan.getParentName());
//		}
	}
	
	protected List<IBusinessTable> getOrphans(Collection<IBusinessTable> col){
		List<IBusinessTable> orphans = new ArrayList<IBusinessTable>();
		for(IBusinessTable t : col){
				if (t instanceof AbstractBusinessTable && ((AbstractBusinessTable)t).getParentName() != null){
					orphans.add(t);
				}
		}
		
		return orphans;
		
	}
	
	protected IDataStream findDataStream(MetaData model, String name){
		for(IDataSource ds : model.getDataSources()){
			for(IDataStream s : ds.getDataStreams()){
				if (s.getName().equals(name)){
					return s;
				}
			}
		}
		return null;
	}
	
	public String getErrorsBuffer(){
		return errorBuffer.toString();
	}
}
