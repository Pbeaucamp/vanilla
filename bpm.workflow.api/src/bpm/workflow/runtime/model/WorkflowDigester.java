package bpm.workflow.runtime.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.digester3.Digester;
import org.apache.commons.io.IOUtils;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import bpm.workflow.runtime.model.activities.AirActivity;
import bpm.workflow.runtime.model.activities.BiWorkFlowActivity;
import bpm.workflow.runtime.model.activities.CalculationActivity;
import bpm.workflow.runtime.model.activities.CancelActivity;
import bpm.workflow.runtime.model.activities.CheckColumnActivity;
import bpm.workflow.runtime.model.activities.CheckPathActivity;
import bpm.workflow.runtime.model.activities.CheckTableActivity;
import bpm.workflow.runtime.model.activities.CommandFTPActivity;
import bpm.workflow.runtime.model.activities.Comment;
import bpm.workflow.runtime.model.activities.CompterActivity;
import bpm.workflow.runtime.model.activities.ConpensationActivity;
import bpm.workflow.runtime.model.activities.DeleteFileActivity;
import bpm.workflow.runtime.model.activities.DeleteFolderActivity;
import bpm.workflow.runtime.model.activities.DummyActivity;
import bpm.workflow.runtime.model.activities.ErrorActivity;
import bpm.workflow.runtime.model.activities.ExcelAggregateActivity;
import bpm.workflow.runtime.model.activities.GetMailServActivity;
import bpm.workflow.runtime.model.activities.InterfaceActivity;
import bpm.workflow.runtime.model.activities.InterfaceGoogleActivity;
import bpm.workflow.runtime.model.activities.KPIActivity;
import bpm.workflow.runtime.model.activities.KettleActivity;
import bpm.workflow.runtime.model.activities.LinkActivity;
import bpm.workflow.runtime.model.activities.LoopGlobale;
import bpm.workflow.runtime.model.activities.MacroProcess;
import bpm.workflow.runtime.model.activities.MailActivity;
import bpm.workflow.runtime.model.activities.PingHostActivity;
import bpm.workflow.runtime.model.activities.ProfilingActivity;
import bpm.workflow.runtime.model.activities.PutMailServActivity;
import bpm.workflow.runtime.model.activities.SignalActivity;
import bpm.workflow.runtime.model.activities.SqlActivity;
import bpm.workflow.runtime.model.activities.StartActivity;
import bpm.workflow.runtime.model.activities.StarterActivity;
import bpm.workflow.runtime.model.activities.StopActivity;
import bpm.workflow.runtime.model.activities.TalendActivity;
import bpm.workflow.runtime.model.activities.TaskListActivity;
import bpm.workflow.runtime.model.activities.TimerActivity;
import bpm.workflow.runtime.model.activities.XorActivity;
import bpm.workflow.runtime.model.activities.filemanagement.BrowseContentActivity;
import bpm.workflow.runtime.model.activities.filemanagement.ConcatExcelActivity;
import bpm.workflow.runtime.model.activities.filemanagement.ConcatPDFActivity;
import bpm.workflow.runtime.model.activities.filemanagement.EncryptFileActivity;
import bpm.workflow.runtime.model.activities.filemanagement.GetFTPActivity;
import bpm.workflow.runtime.model.activities.filemanagement.GetHardDActivity;
import bpm.workflow.runtime.model.activities.filemanagement.GetSFTPActivity;
import bpm.workflow.runtime.model.activities.filemanagement.PutFTPActivity;
import bpm.workflow.runtime.model.activities.filemanagement.PutHardDActivity;
import bpm.workflow.runtime.model.activities.filemanagement.PutSFTPActivity;
import bpm.workflow.runtime.model.activities.filemanagement.PutSSHActivity;
import bpm.workflow.runtime.model.activities.reporting.BurstActivity;
import bpm.workflow.runtime.model.activities.reporting.ReportActivity;
import bpm.workflow.runtime.model.activities.vanilla.GatewayActivity;
import bpm.workflow.runtime.model.activities.vanilla.GedActivity;
import bpm.workflow.runtime.model.activities.vanilla.MetadataToD4CActivity;
import bpm.workflow.runtime.model.activities.vanilla.ResetGedIndexActivity;
import bpm.workflow.runtime.model.activities.vanilla.ResetUolapCacheActivity;
import bpm.workflow.runtime.model.activities.vanilla.StartStopItemActivity;
import bpm.workflow.runtime.resources.BIWObject;
import bpm.workflow.runtime.resources.BiRepositoryObject;
import bpm.workflow.runtime.resources.BirtReport;
import bpm.workflow.runtime.resources.FwrObject;
import bpm.workflow.runtime.resources.GatewayObject;
import bpm.workflow.runtime.resources.InterfaceObject;
import bpm.workflow.runtime.resources.JrxmlReport;
import bpm.workflow.runtime.resources.ProfilingQueryObject;
import bpm.workflow.runtime.resources.Script;
import bpm.workflow.runtime.resources.TaskListObject;
import bpm.workflow.runtime.resources.servers.DataBaseServer;
import bpm.workflow.runtime.resources.servers.FileServer;
import bpm.workflow.runtime.resources.servers.FreemetricServer;
import bpm.workflow.runtime.resources.servers.ServerMail;
import bpm.workflow.runtime.resources.variables.Variable;

public class WorkflowDigester {
	
	private Digester dig = new Digester();
	private StringBuffer error=   new StringBuffer();
	
	
	private WorkflowDigester(){
		dig.setValidating(false);
		dig.setErrorHandler(new MyErrorHandler());
		createCallbacks();
	}
	
	
	private WorkflowDigester(ClassLoader classLoader){
		if (classLoader != null){
			dig.setClassLoader(classLoader);
		}
		
		dig.setValidating(false);
		dig.setErrorHandler(new MyErrorHandler());
		createCallbacks();
	}
	
	
	private void createCallbacks(){
		String root = "workflowModel";
		try{
		dig.addObjectCreate(root, WorkflowModel.class);
			dig.addCallMethod(root + "/name", "setName", 0);
			dig.addCallMethod(root + "/id", "setId", 0);
			
			
			dig.addObjectCreate(root + "/document-properties", DocumentProperties.class);
				dig.addCallMethod(root + "/document-properties/id", "setId", 0);
				dig.addCallMethod(root + "/document-properties/name", "setName", 0);
				dig.addCallMethod(root + "/document-properties/description", "setDescription", 0);
				dig.addCallMethod(root + "/document-properties/author", "setAuthor", 0);
				dig.addCallMethod(root + "/document-properties/creation", "setCreation", 0);
				dig.addCallMethod(root + "/document-properties/modification", "setModification", 0);
				dig.addCallMethod(root + "/document-properties/version", "setVersion", 0);
				dig.addCallMethod(root + "/document-properties/icon", "setIconPath", 0);
				dig.addCallMethod(root + "/document-properties/path", "setPath", 0);
				dig.addCallMethod(root + "/document-properties/flyover", "setFlyOverIconPath", 0);
			dig.addSetNext(root + "/document-properties", "setProperties");

			dig.addObjectCreate(root + "/workflowModelParameter", WorkfowModelParameter.class);
				dig.addCallMethod(root + "/workflowModelParameter/defaultValue", "setDefaultValue", 0);
				dig.addCallMethod(root + "/workflowModelParameter/name", "setName", 0);
			dig.addSetNext(root + "/workflowModelParameter", "addParameter");
			
			dig.addObjectCreate(root + "/variable", Variable.class);
				dig.addCallMethod(root + "/variable/id", "setId", 0);
				dig.addCallMethod(root + "/variable/name", "setName", 0);
				dig.addCallMethod(root + "/variable/type", "setType", 0);
				dig.addCallMethod(root + "/variable/values/value", "addValue", 0);	
			dig.addSetNext(root + "/variable", "addResource");

			dig.addObjectCreate(root + "/dataBaseServer", DataBaseServer.class);
				dig.addCallMethod(root + "/dataBaseServer/id", "setId", 0);
				dig.addCallMethod(root + "/dataBaseServer/name", "setName", 0);
				dig.addCallMethod(root + "/dataBaseServer/url", "setUrl", 0);
				dig.addCallMethod(root + "/dataBaseServer/login", "setLogin", 0);
				dig.addCallMethod(root + "/dataBaseServer/password", "setPassword", 0);
				dig.addCallMethod(root + "/dataBaseServer/dataBaseName", "setDataBaseName", 0);
				dig.addCallMethod(root + "/dataBaseServer/jdbcDriver", "setJdbcDriver", 0);
				dig.addCallMethod(root + "/dataBaseServer/port", "setPort", 0);
			dig.addSetNext(root + "/dataBaseServer", "addResource");

			dig.addObjectCreate(root + "/freemetricsServer", FreemetricServer.class);
			dig.addCallMethod(root + "/freemetricsServer/id", "setId", 0);
			dig.addCallMethod(root + "/freemetricsServer/name", "setName", 0);
			dig.addCallMethod(root + "/freemetricsServer/url", "setUrl", 0);
			dig.addCallMethod(root + "/freemetricsServer/login", "setLogin", 0);
			dig.addCallMethod(root + "/freemetricsServer/password", "setPassword", 0);
			dig.addCallMethod(root + "/freemetricsServer/dataBaseName", "setDataBaseName", 0);
			dig.addCallMethod(root + "/freemetricsServer/jdbcDriver", "setJdbcDriver", 0);
			dig.addCallMethod(root + "/freemetricsServer/port", "setPort", 0);
			dig.addCallMethod(root + "/freemetricsServer/freemtricsLogin", "setFmLogin", 0);
			dig.addCallMethod(root + "/freemetricsServer/freemtricsPassword", "setFmPassword", 0);
		dig.addSetNext(root + "/freemetricsServer", "addResource");
			
			dig.addObjectCreate(root + "/mailServer", ServerMail.class);
				dig.addCallMethod(root + "/mailServer/id", "setId", 0);
				dig.addCallMethod(root + "/mailServer/name", "setName", 0);
				dig.addCallMethod(root + "/mailServer/url", "setUrl", 0);
				dig.addCallMethod(root + "/mailServer/login", "setLogin", 0);
				dig.addCallMethod(root + "/mailServer/password", "setPassword", 0);
				dig.addCallMethod(root + "/mailServer/port", "setPort", 0);
			dig.addSetNext(root + "/mailServer", "addResource");
			
			dig.addObjectCreate(root + "/fileserver", FileServer.class);
			dig.addCallMethod(root + "/fileserver/id", "setId", 0);
			dig.addCallMethod(root + "/fileserver/name", "setName", 0);
			dig.addCallMethod(root + "/fileserver/url", "setUrl", 0);
			dig.addCallMethod(root + "/fileserver/login", "setLogin", 0);
			dig.addCallMethod(root + "/fileserver/password", "setPassword", 0);
			dig.addCallMethod(root + "/fileserver/typeserver", "setTypeServ", 0);
			dig.addCallMethod(root + "/fileserver/repertoiredef", "setRepertoireDef", 0);
			dig.addCallMethod(root + "/fileserver/typefichier", "setTypeFichier", 0);
			dig.addCallMethod(root + "/fileserver/port", "setPort", 0);
			dig.addCallMethod(root + "/fileserver/keypath", "setKeyPath", 0);
		dig.addSetNext(root + "/fileserver", "addResource");
			
			dig.addObjectCreate(root + "/pool", PoolModel.class);
				dig.addCallMethod(root + "/pool/name", "setName", 0);
				dig.addCallMethod(root + "/pool/description", "setDescription", 0);
				dig.addCallMethod(root + "/pool/type", "setType", 0);
				dig.addCallMethod(root + "/pool/xPos", "setPositionX", 0);
				dig.addCallMethod(root + "/pool/yPos", "setPositionY", 0);
				dig.addCallMethod(root + "/pool/width", "setPositionWidth", 0);
				dig.addCallMethod(root + "/pool/height", "setPositionHeight", 0);
				//dig.addCallMethod(root + "/pool/activities/activityid", "addReference", 0);
			dig.addSetNext(root + "/pool", "addPool");
			
			
			dig.addObjectCreate(root + "/loop", LoopGlobale.class);
			dig.addCallMethod(root + "/loop/name", "setName", 0);
			dig.addCallMethod(root + "/loop/xPos", "setPositionX", 0);
			dig.addCallMethod(root + "/loop/yPos", "setPositionY", 0);
			dig.addCallMethod(root + "/loop/yRel", "setRelativePositionY", 0);
			dig.addCallMethod(root + "/loop/width", "setPositionWidth", 0);
			dig.addCallMethod(root + "/loop/height", "setPositionHeight", 0);
			dig.addCallMethod(root + "/loop/parent", "setParent", 0);
			dig.addSetNext(root + "/loop", "addActivity");
		
			dig.addObjectCreate(root + "/startActivity", StartActivity.class);
				dig.addCallMethod(root + "/startActivity/name", "setName", 0);
				dig.addCallMethod(root + "/startActivity/xPos", "setPositionX", 0);
				dig.addCallMethod(root + "/startActivity/yPos", "setPositionY", 0);
				dig.addCallMethod(root + "/startActivity/yRel", "setRelativePositionY", 0);
				dig.addCallMethod(root + "/startActivity/width", "setPositionWidth", 0);
				dig.addCallMethod(root + "/startActivity/height", "setPositionHeight", 0);
				dig.addCallMethod(root + "/startActivity/parent", "setParent", 0);
			dig.addSetNext(root + "/startActivity", "addActivity");
			
			dig.addObjectCreate(root + "/dummyActivity", DummyActivity.class);
			dig.addCallMethod(root + "/dummyActivity/name", "setName", 0);
			dig.addCallMethod(root + "/dummyActivity/xPos", "setPositionX", 0);
			dig.addCallMethod(root + "/dummyActivity/yPos", "setPositionY", 0);
			dig.addCallMethod(root + "/dummyActivity/yRel", "setRelativePositionY", 0);
			dig.addCallMethod(root + "/dummyActivity/width", "setPositionWidth", 0);
			dig.addCallMethod(root + "/dummyActivity/height", "setPositionHeight", 0);
			dig.addCallMethod(root + "/dummyActivity/parent", "setParent", 0);
			dig.addSetNext(root + "/dummyActivity", "addActivity");
			
			dig.addObjectCreate(root + "/calculationActivity", CalculationActivity.class);
			dig.addCallMethod(root + "/calculationActivity/name", "setName", 0);
			dig.addCallMethod(root + "/calculationActivity/xPos", "setPositionX", 0);
			dig.addCallMethod(root + "/calculationActivity/yPos", "setPositionY", 0);
			dig.addCallMethod(root + "/calculationActivity/yRel", "setRelativePositionY", 0);
			dig.addCallMethod(root + "/calculationActivity/width", "setPositionWidth", 0);
			dig.addCallMethod(root + "/calculationActivity/height", "setPositionHeight", 0);
			dig.addCallMethod(root + "/calculationActivity/parent", "setParent", 0);
			
			dig.addObjectCreate(root + "/calculationActivity/script", Script.class);
			dig.addCallMethod(root + "/calculationActivity/script/name", "setName", 0);
			dig.addCallMethod(root + "/calculationActivity/script/expression", "setScriptFunction", 0);
			dig.addCallMethod(root + "/calculationActivity/script/type", "setType", 0);
			dig.addSetNext(root + "/calculationActivity/script", "addScript");
			
			dig.addSetNext(root + "/calculationActivity", "addActivity");
			
			
			dig.addObjectCreate(root + "/excelAggregateActivity", ExcelAggregateActivity.class);
			dig.addCallMethod(root + "/excelAggregateActivity/name", "setName", 0);
			dig.addCallMethod(root + "/excelAggregateActivity/xPos", "setPositionX", 0);
			dig.addCallMethod(root + "/excelAggregateActivity/yPos", "setPositionY", 0);
			dig.addCallMethod(root + "/excelAggregateActivity/yRel", "setRelativePositionY", 0);
			dig.addCallMethod(root + "/excelAggregateActivity/width", "setPositionWidth", 0);
			dig.addCallMethod(root + "/excelAggregateActivity/height", "setPositionHeight", 0);
			dig.addCallMethod(root + "/excelAggregateActivity/parent", "setParent", 0);
			dig.addCallMethod(root + "/excelAggregateActivity/path", "setPath", 0);
			dig.addCallMethod(root + "/excelAggregateActivity/mapping/map", "addMapping", 2);
			dig.addCallParam(root + "/excelAggregateActivity/mapping/map/key", 0);
			dig.addCallParam(root + "/excelAggregateActivity/mapping/map/value", 1);
			dig.addSetNext(root + "/excelAggregateActivity", "addActivity");
			
			dig.addObjectCreate(root + "/concatExcelActivity", ConcatExcelActivity.class);
				dig.addCallMethod(root + "/concatExcelActivity/name", "setName", 0);
				dig.addCallMethod(root + "/concatExcelActivity/comment", "setComment", 0);
				dig.addCallMethod(root + "/concatExcelActivity/xPos", "setPositionX", 0);
				dig.addCallMethod(root + "/concatExcelActivity/yPos", "setPositionY", 0);
				dig.addCallMethod(root + "/concatExcelActivity/yRel", "setRelativePositionY", 0);
				dig.addCallMethod(root + "/concatExcelActivity/width", "setPositionWidth", 0);
				dig.addCallMethod(root + "/concatExcelActivity/height", "setPositionHeight", 0);
				dig.addCallMethod(root + "/concatExcelActivity/parent", "setParent", 0);
				dig.addCallMethod(root + "/concatExcelActivity/varRefName", "setVariable", 0);
				dig.addCallMethod(root + "/concatExcelActivity/filestoconcat/path", "addInput", 0);
				dig.addCallMethod(root + "/concatExcelActivity/fileserver", "setServer", 0);
				dig.addCallMethod(root + "/concatExcelActivity/pathout", "setOutputName", 0);
				dig.addCallMethod(root + "/concatExcelActivity/pathtostore", "setPathToStore", 0);
			dig.addSetNext(root + "/concatExcelActivity", "addActivity");
		
			dig.addObjectCreate(root + "/concatPDFActivity", ConcatPDFActivity.class);
				dig.addCallMethod(root + "/concatPDFActivity/name", "setName", 0);
				dig.addCallMethod(root + "/concatPDFActivity/comment", "setComment", 0);
				dig.addCallMethod(root + "/concatPDFActivity/xPos", "setPositionX", 0);
				dig.addCallMethod(root + "/concatPDFActivity/yPos", "setPositionY", 0);
				dig.addCallMethod(root + "/concatPDFActivity/yRel", "setRelativePositionY", 0);
				dig.addCallMethod(root + "/concatPDFActivity/width", "setPositionWidth", 0);
				dig.addCallMethod(root + "/concatPDFActivity/height", "setPositionHeight", 0);
				dig.addCallMethod(root + "/concatPDFActivity/parent", "setParent", 0);
				dig.addCallMethod(root + "/concatPDFActivity/varRefName", "setVariable", 0);
				dig.addCallMethod(root + "/concatPDFActivity/filestoconcat/path", "addInput", 0);
				dig.addCallMethod(root + "/concatPDFActivity/fileserver", "setServer", 0);
				dig.addCallMethod(root + "/concatPDFActivity/pathout", "setOutputName", 0);
				dig.addCallMethod(root + "/concatPDFActivity/pathtostore", "setPathToStore", 0);
			dig.addSetNext(root + "/concatPDFActivity", "addActivity");
			

			dig.addObjectCreate(root + "/gedactivity", GedActivity.class);
				dig.addCallMethod(root + "/gedactivity/name", "setName", 0);
				dig.addCallMethod(root + "/gedactivity/comment", "setComment", 0);
				dig.addCallMethod(root + "/gedactivity/xPos", "setPositionX", 0);
				dig.addCallMethod(root + "/gedactivity/yPos", "setPositionY", 0);
				dig.addCallMethod(root + "/gedactivity/yRel", "setRelativePositionY", 0);
				dig.addCallMethod(root + "/gedactivity/width", "setPositionWidth", 0);
				dig.addCallMethod(root + "/gedactivity/height", "setPositionHeight", 0);
				dig.addCallMethod(root + "/gedactivity/parent", "setParent", 0);
				dig.addCallMethod(root + "/gedactivity/filestotreat/path", "addInput", 0);
				dig.addCallMethod(root + "/gedactivity/addToMDM", "setAddToMDM", 0);
				dig.addCallMethod(root + "/gedactivity/supplierId", "setSupplierId", 0);
				dig.addCallMethod(root + "/gedactivity/contractId", "setContractId", 0);
				dig.addCallMethod(root + "/gedactivity/repositoryid", "setRepositoryId", 0);
				
				
				dig.addCallMethod(root + "/gedactivity/itemsids/item", "addItemIdForFile", 2);
				dig.addCallParam(root + "/gedactivity/itemsids/item/itemid", 0);
				dig.addCallParam(root + "/gedactivity/itemsids/item/activityoutput", 1);
				
		dig.addSetNext(root + "/gedactivity", "addActivity");
			
			dig.addObjectCreate(root + "/metadataD4CActivity", MetadataToD4CActivity.class);
			dig.addCallMethod(root + "/metadataD4CActivity/name", "setName", 0);
			dig.addCallMethod(root + "/metadataD4CActivity/comment", "setComment", 0);
			dig.addCallMethod(root + "/metadataD4CActivity/xPos", "setPositionX", 0);
			dig.addCallMethod(root + "/metadataD4CActivity/yPos", "setPositionY", 0);
			dig.addCallMethod(root + "/metadataD4CActivity/yRel", "setRelativePositionY", 0);
			dig.addCallMethod(root + "/metadataD4CActivity/width", "setPositionWidth", 0);
			dig.addCallMethod(root + "/metadataD4CActivity/height", "setPositionHeight", 0);
			dig.addCallMethod(root + "/metadataD4CActivity/parent", "setParent", 0);
			dig.addCallMethod(root + "/metadataD4CActivity/metadataid", "setMetadataId", 0);
			dig.addCallMethod(root + "/metadataD4CActivity/metadataName", "setMetadataName", 0);
			dig.addCallMethod(root + "/metadataD4CActivity/query", "setQueryName", 0);
			dig.addCallMethod(root + "/metadataD4CActivity/resource", "setResourceName", 0);
			dig.addCallMethod(root + "/metadataD4CActivity/resourceId", "setResourceId", 0);
			
			
			dig.addCallMethod(root + "/metadataD4CActivity/package", "setPackage", 3);
			dig.addCallParam(root + "/metadataD4CActivity/package/id", 0);
			dig.addCallParam(root + "/metadataD4CActivity/package/name", 1);
			dig.addCallParam(root + "/metadataD4CActivity/package/title", 2);
			
		dig.addSetNext(root + "/metadataD4CActivity", "addActivity");
		
			
			dig.addObjectCreate(root + "/MacroProcess", MacroProcess.class);
			dig.addCallMethod(root + "/MacroProcess/name", "setName", 0);
			dig.addCallMethod(root + "/MacroProcess/xPos", "setPositionX", 0);
			dig.addCallMethod(root + "/MacroProcess/yPos", "setPositionY", 0);
			dig.addCallMethod(root + "/MacroProcess/yRel", "setRelativePositionY", 0);
			dig.addCallMethod(root + "/MacroProcess/width", "setPositionWidth", 0);
			dig.addCallMethod(root + "/MacroProcess/height", "setPositionHeight", 0);
			dig.addCallMethod(root + "/MacroProcess/parent", "setParent", 0);
			dig.addCallMethod(root + "/MacroProcess/type", "setType", 0);
			dig.addSetNext(root + "/MacroProcess", "addActivity");
			
			dig.addObjectCreate(root + "/cancelActivity", CancelActivity.class);
			dig.addCallMethod(root + "/cancelActivity/name", "setName", 0);
			dig.addCallMethod(root + "/cancelActivity/xPos", "setPositionX", 0);
			dig.addCallMethod(root + "/cancelActivity/yPos", "setPositionY", 0);
			dig.addCallMethod(root + "/cancelActivity/yRel", "setRelativePositionY", 0);
			dig.addCallMethod(root + "/cancelActivity/width", "setPositionWidth", 0);
			dig.addCallMethod(root + "/cancelActivity/height", "setPositionHeight", 0);
			dig.addCallMethod(root + "/cancelActivity/parent", "setParent", 0);
			dig.addCallMethod(root + "/cancelActivity/typeact", "setTypeact", 0);
			dig.addSetNext(root + "/cancelActivity", "addActivity");
			
			dig.addObjectCreate(root + "/conpensActivity", ConpensationActivity.class);
			dig.addCallMethod(root + "/conpensActivity/name", "setName", 0);
			dig.addCallMethod(root + "/conpensActivity/xPos", "setPositionX", 0);
			dig.addCallMethod(root + "/conpensActivity/yPos", "setPositionY", 0);
			dig.addCallMethod(root + "/conpensActivity/yRel", "setRelativePositionY", 0);
			dig.addCallMethod(root + "/conpensActivity/width", "setPositionWidth", 0);
			dig.addCallMethod(root + "/conpensActivity/height", "setPositionHeight", 0);
			dig.addCallMethod(root + "/conpensActivity/parent", "setParent", 0);
			dig.addCallMethod(root + "/conpensActivity/typeact", "setTypeact", 0);
			dig.addSetNext(root + "/conpensActivity", "addActivity");
			
			dig.addObjectCreate(root + "/erroractivity", ErrorActivity.class);
			dig.addCallMethod(root + "/erroractivity/name", "setName", 0);
			dig.addCallMethod(root + "/erroractivity/xPos", "setPositionX", 0);
			dig.addCallMethod(root + "/erroractivity/yPos", "setPositionY", 0);
			dig.addCallMethod(root + "/erroractivity/yRel", "setRelativePositionY", 0);
			dig.addCallMethod(root + "/erroractivity/width", "setPositionWidth", 0);
			dig.addCallMethod(root + "/erroractivity/height", "setPositionHeight", 0);
			dig.addCallMethod(root + "/erroractivity/parent", "setParent", 0);
			dig.addCallMethod(root + "/erroractivity/typeact", "setTypeact", 0);
			dig.addSetNext(root + "/erroractivity", "addActivity");
			
			dig.addObjectCreate(root + "/linkActivity", LinkActivity.class);
			dig.addCallMethod(root + "/linkActivity/name", "setName", 0);
			dig.addCallMethod(root + "/linkActivity/xPos", "setPositionX", 0);
			dig.addCallMethod(root + "/linkActivity/yPos", "setPositionY", 0);
			dig.addCallMethod(root + "/linkActivity/yRel", "setRelativePositionY", 0);
			dig.addCallMethod(root + "/linkActivity/width", "setPositionWidth", 0);
			dig.addCallMethod(root + "/linkActivity/height", "setPositionHeight", 0);
			dig.addCallMethod(root + "/linkActivity/parent", "setParent", 0);
			dig.addCallMethod(root + "/linkActivity/typeact", "setTypeact", 0);
			dig.addSetNext(root + "/linkActivity", "addActivity");
			
			dig.addObjectCreate(root + "/signalActivity", SignalActivity.class);
			dig.addCallMethod(root + "/signalActivity/name", "setName", 0);
			dig.addCallMethod(root + "/signalActivity/xPos", "setPositionX", 0);
			dig.addCallMethod(root + "/signalActivity/yPos", "setPositionY", 0);
			dig.addCallMethod(root + "/signalActivity/yRel", "setRelativePositionY", 0);
			dig.addCallMethod(root + "/signalActivity/width", "setPositionWidth", 0);
			dig.addCallMethod(root + "/signalActivity/height", "setPositionHeight", 0);
			dig.addCallMethod(root + "/signalActivity/parent", "setParent", 0);
			dig.addCallMethod(root + "/signalActivity/typeact", "setTypeact", 0);
			dig.addSetNext(root + "/signalActivity", "addActivity");
			
			
			dig.addObjectCreate(root + "/starterEXEActivity", StarterActivity.class);
			dig.addCallMethod(root + "/starterEXEActivity/name", "setName", 0);
			dig.addCallMethod(root + "/starterEXEActivity/comment", "setComment", 0);
			dig.addCallMethod(root + "/starterEXEActivity/xPos", "setPositionX", 0);
			dig.addCallMethod(root + "/starterEXEActivity/yPos", "setPositionY", 0);
			dig.addCallMethod(root + "/starterEXEActivity/yRel", "setRelativePositionY", 0);
			dig.addCallMethod(root + "/starterEXEActivity/width", "setPositionWidth", 0);
			dig.addCallMethod(root + "/starterEXEActivity/height", "setPositionHeight", 0);
			dig.addCallMethod(root + "/starterEXEActivity/parent", "setParent", 0);
			dig.addCallMethod(root + "/starterEXEActivity/path", "setPath", 0);
			dig.addCallMethod(root + "/starterEXEActivity/typeExe", "setTYPEExec", 0);
			dig.addCallMethod(root + "/starterEXEActivity/plateforme", "setPlateforme", 0);
			dig.addSetNext(root + "/starterEXEActivity", "addActivity");
			
			dig.addObjectCreate(root + "/timerActivity", TimerActivity.class);
			dig.addCallMethod(root + "/timerActivity/name", "setName", 0);
			dig.addCallMethod(root + "/timerActivity/xPos", "setPositionX", 0);
			dig.addCallMethod(root + "/timerActivity/yPos", "setPositionY", 0);
			dig.addCallMethod(root + "/timerActivity/yRel", "setRelativePositionY", 0);
			dig.addCallMethod(root + "/timerActivity/width", "setPositionWidth", 0);
			dig.addCallMethod(root + "/timerActivity/height", "setPositionHeight", 0);
			dig.addCallMethod(root + "/timerActivity/parent", "setParent", 0);
			dig.addCallMethod(root + "/timerActivity/timeH", "setTimeH", 0);
			dig.addSetNext(root + "/timerActivity", "addActivity");
			
			dig.addObjectCreate(root + "/compterActivity", CompterActivity.class);
			dig.addCallMethod(root + "/compterActivity/name", "setName", 0);
			dig.addCallMethod(root + "/compterActivity/xPos", "setPositionX", 0);
			dig.addCallMethod(root + "/compterActivity/yPos", "setPositionY", 0);
			dig.addCallMethod(root + "/compterActivity/yRel", "setRelativePositionY", 0);
			dig.addCallMethod(root + "/compterActivity/width", "setPositionWidth", 0);
			dig.addCallMethod(root + "/compterActivity/height", "setPositionHeight", 0);
			dig.addCallMethod(root + "/compterActivity/parent", "setParent", 0);
			dig.addCallMethod(root + "/compterActivity/nbLoop", "setnbLoop", 0);
			dig.addSetNext(root + "/compterActivity", "addActivity");
			
			dig.addObjectCreate(root + "/stopActivity", StopActivity.class);
				dig.addCallMethod(root + "/stopActivity/name", "setName", 0);
				dig.addCallMethod(root + "/stopActivity/xPos", "setPositionX", 0);
				dig.addCallMethod(root + "/stopActivity/yPos", "setPositionY", 0);
				dig.addCallMethod(root + "/stopActivity/yRel", "setRelativePositionY", 0);
				dig.addCallMethod(root + "/stopActivity/width", "setPositionWidth", 0);
				dig.addCallMethod(root + "/stopActivity/height", "setPositionHeight", 0);
				dig.addCallMethod(root + "/stopActivity/parent", "setParent", 0);
			dig.addSetNext(root + "/stopActivity", "addActivity");
			
			dig.addObjectCreate(root + "/xorActivity", XorActivity.class);
				dig.addCallMethod(root + "/xorActivity/name", "setName", 0);
				dig.addCallMethod(root + "/xorActivity/comment", "setComment", 0);
				dig.addCallMethod(root + "/xorActivity/type", "setType", 0);
				dig.addCallMethod(root + "/xorActivity/xPos", "setPositionX", 0);
				dig.addCallMethod(root + "/xorActivity/yPos", "setPositionY", 0);
				dig.addCallMethod(root + "/xorpActivity/yRel", "setRelativePositionY", 0);
				dig.addCallMethod(root + "/xorActivity/width", "setPositionWidth", 0);
				dig.addCallMethod(root + "/xorActivity/height", "setPositionHeight", 0);
				dig.addCallMethod(root + "/xorActivity/parent", "setParent", 0);
			dig.addSetNext(root + "/xorActivity", "addActivity");
			
			dig.addObjectCreate(root + "/BIWActivity", BiWorkFlowActivity.class);
			dig.addCallMethod(root + "/BIWActivity/name", "setName", 0);
			dig.addCallMethod(root + "/BIWActivity/comment", "setComment", 0);
			dig.addCallMethod(root + "/BIWActivity/xPos", "setPositionX", 0);
			dig.addCallMethod(root + "/BIWActivity/yPos", "setPositionY", 0);
			dig.addCallMethod(root + "/BIWActivity/yRel", "setRelativePositionY", 0);
			dig.addCallMethod(root + "/BIWActivity/width", "setPositionWidth", 0);
			dig.addCallMethod(root + "/BIWActivity/height", "setPositionHeight", 0);
			dig.addCallMethod(root + "/BIWActivity/parent", "setParent", 0);
			dig.addCallMethod(root + "/BIWActivity/idBIW", "setIdBIW",0);
			
			dig.addObjectCreate(root + "/BIWActivity/BIWObject", BIWObject.class);
			dig.addCallMethod(root + "/BIWActivity/BIWObject/id", "setId", 0);
//			dig.addCallMethod(root + "/BIWActivity/BIWObject/name", "setName", 0);
			
			dig.addCallMethod(root + "/BIWActivity/mapping/map", "addParameterLink", 2);
			dig.addCallParam(root + "/BIWActivity/mapping/map/key", 0);
			dig.addCallParam(root + "/BIWActivity/mapping/map/value", 1);
			dig.addCallMethod(root + "/BIWActivity/BIWObject/itemname", "setItemName", 0);
		dig.addSetNext(root + "/BIWActivity/BIWObject", "setBIWObject");
		
			dig.addSetNext(root + "/BIWActivity", "addActivity");
			
			
			dig.addObjectCreate(root + "/startstopactivity", StartStopItemActivity.class);
			
				dig.addCallMethod(root + "/startstopactivity/name", "setName", 0);
				dig.addCallMethod(root + "/startstopactivity/comment", "setComment", 0);
				dig.addCallMethod(root + "/startstopactivity/xPos", "setPositionX", 0);
				dig.addCallMethod(root + "/startstopactivity/yPos", "setPositionY", 0);
				dig.addCallMethod(root + "/startstopactivity/yRel", "setRelativePositionY", 0);
				dig.addCallMethod(root + "/startstopactivity/width", "setPositionWidth", 0);
				dig.addCallMethod(root + "/startstopactivity/height", "setPositionHeight", 0);
				dig.addCallMethod(root + "/startstopactivity/parent", "setParent", 0);
				dig.addCallMethod(root + "/startstopactivity/start", "setStart", 0);
			
				dig.addObjectCreate(root + "/startstopactivity/biRepositoryObject", BiRepositoryObject.class);
					dig.addCallMethod(root + "/startstopactivity/biRepositoryObject/id", "setId", 0);
					dig.addCallMethod(root + "/startstopactivity/biRepositoryObject/itemname", "setItemName", 0);
				dig.addSetNext(root + "/startstopactivity/biRepositoryObject", "setItem");
				
			dig.addSetNext(root + "/startstopactivity", "addActivity");
			
		dig.addObjectCreate(root + "/resetgedactivity", ResetGedIndexActivity.class);
			
			dig.addCallMethod(root + "/resetgedactivity/name", "setName", 0);
			dig.addCallMethod(root + "/resetgedactivity/comment", "setComment", 0);
			dig.addCallMethod(root + "/resetgedactivity/xPos", "setPositionX", 0);
			dig.addCallMethod(root + "/resetgedactivity/yPos", "setPositionY", 0);
			dig.addCallMethod(root + "/resetgedactivity/yRel", "setRelativePositionY", 0);
			dig.addCallMethod(root + "/resetgedactivity/width", "setPositionWidth", 0);
			dig.addCallMethod(root + "/resetgedactivity/height", "setPositionHeight", 0);
			dig.addCallMethod(root + "/resetgedactivity/parent", "setParent", 0);
			dig.addCallMethod(root + "/resetgedactivity/rebuild", "setRebuild", 0);
			
		dig.addSetNext(root + "/resetgedactivity", "addActivity");
		
		
		
	dig.addObjectCreate(root + "/resetuolapcacheactivity", ResetUolapCacheActivity.class);
		
		dig.addCallMethod(root + "/resetuolapcacheactivity/name", "setName", 0);
		dig.addCallMethod(root + "/resetuolapcacheactivity/comment", "setComment", 0);
		dig.addCallMethod(root + "/resetuolapcacheactivity/xPos", "setPositionX", 0);
		dig.addCallMethod(root + "/resetuolapcacheactivity/yPos", "setPositionY", 0);
		dig.addCallMethod(root + "/resetuolapcacheactivity/yRel", "setRelativePositionY", 0);
		dig.addCallMethod(root + "/resetuolapcacheactivity/width", "setPositionWidth", 0);
		dig.addCallMethod(root + "/resetuolapcacheactivity/height", "setPositionHeight", 0);
		dig.addCallMethod(root + "/resetuolapcacheactivity/parent", "setParent", 0);
		dig.addCallMethod(root + "/resetuolapcacheactivity/reload", "setReloadCache", 0);
		dig.addCallMethod(root + "/resetuolapcacheactivity/cubename", "setCubeName", 0);
	
		dig.addObjectCreate(root + "/resetuolapcacheactivity/biRepositoryObject", BiRepositoryObject.class);
			dig.addCallMethod(root + "/resetuolapcacheactivity/biRepositoryObject/id", "setId", 0);
			dig.addCallMethod(root + "/resetuolapcacheactivity/biRepositoryObject/itemname", "setItemName", 0);
		dig.addSetNext(root + "/resetuolapcacheactivity/biRepositoryObject", "setItem");
		
	dig.addSetNext(root + "/resetuolapcacheactivity", "addActivity");
		
		
			
			
			dig.addObjectCreate(root + "/reportActivity", ReportActivity.class);
				dig.addCallMethod(root + "/reportActivity/name", "setName", 0);
				dig.addCallMethod(root + "/reportActivity/comment", "setComment", 0);
				dig.addCallMethod(root + "/reportActivity/pathtostore", "setPathToStore", 0);
				dig.addCallMethod(root + "/reportActivity/addtoged", "setAddToGed", 0);
				dig.addCallMethod(root + "/reportActivity/massreporting", "setMassReporting", 0);
				dig.addCallMethod(root + "/reportActivity/securityprovider", "setSecurityProvider", 0);
				dig.addCallMethod(root + "/reportActivity/xPos", "setPositionX", 0);
				dig.addCallMethod(root + "/reportActivity/yPos", "setPositionY", 0);
				dig.addCallMethod(root + "/reportActivity/yRel", "setRelativePositionY", 0);
				dig.addCallMethod(root + "/reportActivity/width", "setPositionWidth", 0);
				dig.addCallMethod(root + "/reportActivity/height", "setPositionHeight", 0);
				dig.addCallMethod(root + "/reportActivity/parent", "setParent", 0);
				dig.addCallMethod(root + "/reportActivity/mapping/map", "addParameterLink", 2);
				dig.addCallParam(root + "/reportActivity/mapping/map/key", 0);
				dig.addCallParam(root + "/reportActivity/mapping/map/value", 1);
				
			
				
				
				
			dig.addObjectCreate(root + "/burstActivity", BurstActivity.class);
				dig.addCallMethod(root + "/burstActivity/name", "setName", 0);
				dig.addCallMethod(root + "/burstActivity/comment", "setComment", 0);
				dig.addCallMethod(root + "/burstActivity/pathtostore", "setPathToStore", 0);
				dig.addCallMethod(root + "/burstActivity/addtoged", "setAddToGed", 0);
				dig.addCallMethod(root + "/burstActivity/xPos", "setPositionX", 0);
				dig.addCallMethod(root + "/burstActivity/yPos", "setPositionY", 0);
				dig.addCallMethod(root + "/burstActivity/yRel", "setRelativePositionY", 0);
				dig.addCallMethod(root + "/burstActivity/width", "setPositionWidth", 0);
				dig.addCallMethod(root + "/burstActivity/height", "setPositionHeight", 0);
				dig.addCallMethod(root + "/burstActivity/parent", "setParent", 0);
				
				
				
				dig.addObjectCreate(root + "/burstActivity/fwrObject", FwrObject.class);
				dig.addObjectCreate(root + "/reportActivity/fwrObject", FwrObject.class);
					dig.addCallMethod("*/fwrObject/id", "setId", 0);
//					dig.addCallMethod("*/fwrObject/name", "setName", 0);
					
					dig.addCallMethod("*/fwrObject/outputType", "setOutputType", 0);
					dig.addCallMethod("*/fwrObject/outputName", "setOutputName", 0);
					dig.addCallMethod("*/fwrObject/outputFormat", "setOutputFormat", 0);
					dig.addCallMethod("*/fwrObject/itemname", "setItemName", 0);
					dig.addCallMethod("*/fwrObject/parameters/parameter", "addParameter", 0);
				dig.addSetNext(root + "/reportActivity/fwrObject", "setReportObject");
				dig.addSetNext(root + "/burstActivity/fwrObject", "setReportObject");
				
				dig.addObjectCreate(root + "/reportActivity/birtObject", BirtReport.class);
				dig.addObjectCreate(root + "/burstActivity/birtObject", BirtReport.class);
					dig.addCallMethod("*/birtObject/id", "setId", 0);
//					dig.addCallMethod("*/birtObject/name", "setName", 0);
					
					dig.addCallMethod("*/birtObject/outputType", "setOutputType", 0);
					dig.addCallMethod("*/birtObject/outputName", "setOutputName", 0);
					dig.addCallMethod("*/birtObject/outputFormat", "setOutputFormat", 0);
					dig.addCallMethod("*/birtObject/itemname", "setItemName", 0);
					dig.addCallMethod("*/birtObject/parameters/parameter", "addParameter", 0);
				dig.addSetNext(root + "/reportActivity/birtObject", "setReportObject");
				dig.addSetNext(root + "/burstActivity/birtObject", "setReportObject");
				
				dig.addObjectCreate(root + "/burstActivity/jrxmlObject", JrxmlReport.class);
				dig.addObjectCreate(root + "/reportActivity/jrxmlObject", JrxmlReport.class);
					dig.addCallMethod("*/jrxmlObject/id", "setId", 0);
//					dig.addCallMethod("*/jrxmlObject/name", "setName", 0);
					
					dig.addCallMethod("*/jrxmlObject/outputType", "setOutputType", 0);
					dig.addCallMethod("*/jrxmlObject/outputName", "setOutputName", 0);
					dig.addCallMethod("*/jrxmlObject/outputFormat", "setOutputFormat", 0);
					dig.addCallMethod("*/jrxmlObject/itemname", "setItemName", 0);
					dig.addCallMethod("*/jrxmlObject/datasourceid", "setDataSourceId", 0);
					dig.addCallMethod("*/jrxmlObject/parameters/parameter", "addParameter", 0);
				dig.addSetNext(root + "/reportActivity/jrxmlObject", "setReportObject");
				dig.addSetNext(root + "/burstActivity/jrxmlObject", "setReportObject");

			dig.addSetNext(root + "/reportActivity", "addActivity");
			
				dig.addCallMethod(root + "/burstActivity/groups/group", "addGroup", 0);
			
			dig.addSetNext(root + "/burstActivity", "addActivity");
			
		
		dig.addObjectCreate(root + "/interfaceActivity", InterfaceActivity.class);
			dig.addCallMethod(root + "/interfaceActivity/name", "setName", 0);
			dig.addCallMethod(root + "/interfaceActivity/comment", "setComment", 0);
			dig.addCallMethod(root + "/interfaceActivity/xPos", "setPositionX", 0);
			dig.addCallMethod(root + "/interfaceActivity/yPos", "setPositionY", 0);
			dig.addCallMethod(root + "/interfaceActivity/yRel", "setRelativePositionY", 0);
			dig.addCallMethod(root + "/interfaceActivity/width", "setPositionWidth", 0);
			dig.addCallMethod(root + "/interfaceActivity/height", "setPositionHeight", 0);
			dig.addCallMethod(root + "/interfaceActivity/parent", "setParent", 0);
			dig.addCallMethod(root + "/interfaceActivity/group", "setGroupForValidation", 0);
			dig.addCallMethod(root + "/interfaceActivity/type", "setType", 0);
			
			dig.addCallMethod(root + "/interfaceActivity/mapping/map", "addParameterMapping", 2);
			dig.addCallParam(root + "/interfaceActivity/mapping/map/key", 0);
			dig.addCallParam(root + "/interfaceActivity/mapping/map/value", 1);
		
			dig.addObjectCreate(root + "/interfaceActivity/interfaceObject", InterfaceObject.class);
				dig.addCallMethod(root + "/interfaceActivity/interfaceObject/id", "setId", 0);
				dig.addCallMethod(root + "/interfaceActivity/interfaceObject/name", "setName", 0);
				
				dig.addCallMethod(root + "/interfaceActivity/interfaceObject/itemname", "setItemName", 0);
				dig.addCallMethod("/interfaceActivity/interfaceObject/parameters/parameter", "addParameter", 0);
			dig.addSetNext(root + "/interfaceActivity/interfaceObject", "setInterface");
			
		dig.addSetNext(root + "/interfaceActivity", "addActivity");
		
		
		
		dig.addObjectCreate(root + "/interfaceGoogleActivity", InterfaceGoogleActivity.class);
		dig.addCallMethod(root + "/interfaceGoogleActivity/name", "setName", 0);
		dig.addCallMethod(root + "/interfaceGoogleActivity/comment", "setComment", 0);
		dig.addCallMethod(root + "/interfaceGoogleActivity/xPos", "setPositionX", 0);
		dig.addCallMethod(root + "/interfaceGoogleActivity/yPos", "setPositionY", 0);
		dig.addCallMethod(root + "/interfaceGoogleActivity/yRel", "setRelativePositionY", 0);
		dig.addCallMethod(root + "/interfaceGoogleActivity/width", "setPositionWidth", 0);
		dig.addCallMethod(root + "/interfaceGoogleActivity/height", "setPositionHeight", 0);
		dig.addCallMethod(root + "/interfaceGoogleActivity/parent", "setParent", 0);
		dig.addCallMethod(root + "/interfaceGoogleActivity/group", "setGroupForValidation", 0);
		
		dig.addObjectCreate(root + "/interfaceGoogleActivity/interfaceObject", InterfaceObject.class);
			dig.addCallMethod(root + "/interfaceGoogleActivity/interfaceObject/id", "setId", 0);
			dig.addCallMethod(root + "/interfaceGoogleActivity/interfaceObject/name", "setName", 0);
			
			dig.addCallMethod(root + "/interfaceGoogleActivity/interfaceObject/itemname", "setItemName", 0);
			dig.addCallMethod("/interfaceGoogleActivity/interfaceObject/parameters/parameter", "addParameter", 0);
		dig.addSetNext(root + "/interfaceGoogleActivity/interfaceObject", "setInterface");
		
	dig.addSetNext(root + "/interfaceGoogleActivity", "addActivity");
		
		dig.addObjectCreate(root + "/gatewayActivity", GatewayActivity.class);
			dig.addCallMethod(root + "/gatewayActivity/name", "setName", 0);
			dig.addCallMethod(root + "/gatewayActivity/comment", "setComment", 0);
			dig.addCallMethod(root + "/gatewayActivity/xPos", "setPositionX", 0);
			dig.addCallMethod(root + "/gatewayActivity/yPos", "setPositionY", 0);
			dig.addCallMethod(root + "/gatewayActivity/yRel", "setRelativePositionY", 0);
			dig.addCallMethod(root + "/gatewayActivity/width", "setPositionWidth", 0);
			dig.addCallMethod(root + "/gatewayActivity/height", "setPositionHeight", 0);
			dig.addCallMethod(root + "/gatewayActivity/parent", "setParent", 0);
//			dig.addCallMethod(root + "/gatewayActivity/prefServerRef", "setPreferenceServer", 0);
			dig.addCallMethod(root + "/gatewayActivity/numberOfAttempts", "setNumberOfAttempts", 0);
			dig.addCallMethod(root + "/gatewayActivity/mapping/map", "addParameterLink", 2);
			dig.addCallParam(root + "/gatewayActivity/mapping/map/key", 0);
			dig.addCallParam(root + "/gatewayActivity/mapping/map/value", 1);
			dig.addObjectCreate(root + "/gatewayActivity/gatewayObject", GatewayObject.class);
				dig.addCallMethod(root + "/gatewayActivity/gatewayObject/id", "setId", 0);
				dig.addCallMethod(root + "/gatewayActivity/gatewayObject/name", "setName", 0);
				dig.addCallMethod(root + "/gatewayActivity/gatewayObject/itemname", "setItemName", 0);
				dig.addCallMethod("/gatewayActivity/gatewayObject/parameters/parameter", "addParameter", 0);
			dig.addSetNext(root + "/gatewayActivity/gatewayObject", "setBiObject");
		
		dig.addSetNext(root + "/gatewayActivity", "addActivity");
		
		dig.addObjectCreate(root + "/kettleActivity", KettleActivity.class);
			dig.addCallMethod(root + "/kettleActivity/name", "setName", 0);
			dig.addCallMethod(root + "/kettleActivity/comment", "setComment", 0);
			dig.addCallMethod(root + "/kettleActivity/xPos", "setPositionX", 0);
			dig.addCallMethod(root + "/kettleActivity/yPos", "setPositionY", 0);
			dig.addCallMethod(root + "/kettleActivity/yRel", "setRelativePositionY", 0);
			dig.addCallMethod(root + "/kettleActivity/width", "setPositionWidth", 0);
			dig.addCallMethod(root + "/kettleActivity/height", "setPositionHeight", 0);
			dig.addCallMethod(root + "/kettleActivity/parent", "setParent", 0);
			dig.addCallMethod(root + "/kettleActivity/repositoryName", "setRepositoryName", 0);
			dig.addCallMethod(root + "/kettleActivity/repositoryUser", "setRepositoryUser", 0);
			dig.addCallMethod(root + "/kettleActivity/repositoryPassword", "setRepositoryPassword", 0);
			dig.addCallMethod(root + "/kettleActivity/jobName", "setJobName", 0);
		
		dig.addSetNext(root + "/kettleActivity", "addActivity");
		
		dig.addObjectCreate(root + "/talendActivity", TalendActivity.class);
		dig.addCallMethod(root + "/talendActivity/name", "setName", 0);
		dig.addCallMethod(root + "/talendActivity/comment", "setComment", 0);
		dig.addCallMethod(root + "/talendActivity/xPos", "setPositionX", 0);
		dig.addCallMethod(root + "/talendActivity/yPos", "setPositionY", 0);
		dig.addCallMethod(root + "/talendActivity/yRel", "setRelativePositionY", 0);
		dig.addCallMethod(root + "/talendActivity/width", "setPositionWidth", 0);
		dig.addCallMethod(root + "/talendActivity/height", "setPositionHeight", 0);
		dig.addCallMethod(root + "/talendActivity/parent", "setParent", 0);
		dig.addCallMethod(root + "/talendActivity/fileName", "setFileName", 0);
		dig.addCallMethod(root + "/talendActivity/className", "setClassName", 0);
	
	dig.addSetNext(root + "/talendActivity", "addActivity");
		
			dig.addObjectCreate(root + "/deleteFileActivity", DeleteFileActivity.class);
			dig.addCallMethod(root + "/deleteFileActivity/name", "setName", 0);
			dig.addCallMethod(root + "/deleteFileActivity/comment", "setComment", 0);
			dig.addCallMethod(root + "/deleteFileActivity/xPos", "setPositionX", 0);
			dig.addCallMethod(root + "/deleteFileActivity/yPos", "setPositionY", 0);
			dig.addCallMethod(root + "/deleteFileActivity/yRel", "setRelativePositionY", 0);
			dig.addCallMethod(root + "/deleteFileActivity/width", "setPositionWidth", 0);
			dig.addCallMethod(root + "/deleteFileActivity/height", "setPositionHeight", 0);
			dig.addCallMethod(root + "/deleteFileActivity/parent", "setParent", 0);
			dig.addCallMethod(root + "/deleteFileActivity/varRefName", "setVariable", 0);
			dig.addCallMethod(root + "/deleteFileActivity/path", "setPath", 0);
			dig.addCallMethod(root + "/deleteFileActivity/fileserver", "setServer", 0);
		dig.addSetNext(root + "/deleteFileActivity", "addActivity");
	
		
			dig.addObjectCreate(root + "/deleteFolderActivity", DeleteFolderActivity.class);
			dig.addCallMethod(root + "/deleteFolderActivity/name", "setName", 0);
			dig.addCallMethod(root + "/deleteFolderActivity/comment", "setComment", 0);
			dig.addCallMethod(root + "/deleteFolderActivity/xPos", "setPositionX", 0);
			dig.addCallMethod(root + "/deleteFolderActivity/yPos", "setPositionY", 0);
			dig.addCallMethod(root + "/deleteFolderActivity/yRel", "setRelativePositionY", 0);
			dig.addCallMethod(root + "/deleteFolderActivity/width", "setPositionWidth", 0);
			dig.addCallMethod(root + "/deleteFolderActivity/height", "setPositionHeight", 0);
			dig.addCallMethod(root + "/deleteFolderActivity/parent", "setParent", 0);
			dig.addCallMethod(root + "/deleteFolderActivity/varRefName", "setVariable", 0);
			dig.addCallMethod(root + "/deleteFolderActivity/path", "setPath", 0);
			dig.addCallMethod(root + "/deleteFolderActivity/fileserver", "setServer", 0);
		dig.addSetNext(root + "/deleteFolderActivity", "addActivity");

		
			dig.addObjectCreate(root + "/pingHostActivity", PingHostActivity.class);
			dig.addCallMethod(root + "/pingHostActivity/name", "setName", 0);
			dig.addCallMethod(root + "/pingHostActivity/comment", "setComment", 0);
			dig.addCallMethod(root + "/pingHostActivity/xPos", "setPositionX", 0);
			dig.addCallMethod(root + "/pingHostActivity/yPos", "setPositionY", 0);
			dig.addCallMethod(root + "/pingHostActivity/yRel", "setRelativePositionY", 0);
			dig.addCallMethod(root + "/pingHostActivity/width", "setPositionWidth", 0);
			dig.addCallMethod(root + "/pingHostActivity/height", "setPositionHeight", 0);
			dig.addCallMethod(root + "/pingHostActivity/parent", "setParent", 0);
			dig.addCallMethod(root + "/pingHostActivity/varRefName", "setVariable", 0);
			dig.addCallMethod(root + "/pingHostActivity/path", "setPath", 0);
			
		dig.addSetNext(root + "/pingHostActivity", "addActivity");

		
		dig.addObjectCreate(root + "/checkPathActivity", CheckPathActivity.class);
			dig.addCallMethod(root + "/checkPathActivity/name", "setName", 0);
			dig.addCallMethod(root + "/checkPathActivity/comment", "setComment", 0);
			dig.addCallMethod(root + "/checkPathActivity/xPos", "setPositionX", 0);
			dig.addCallMethod(root + "/checkPathActivity/yPos", "setPositionY", 0);
			dig.addCallMethod(root + "/checkPathActivity/yRel", "setRelativePositionY", 0);
			dig.addCallMethod(root + "/checkPathActivity/width", "setPositionWidth", 0);
			dig.addCallMethod(root + "/checkPathActivity/height", "setPositionHeight", 0);
			dig.addCallMethod(root + "/checkPathActivity/parent", "setParent", 0);
			dig.addCallMethod(root + "/checkPathActivity/varRefName", "setVariable", 0);
			dig.addCallMethod(root + "/checkPathActivity/path", "setPath", 0);
			dig.addCallMethod(root + "/checkPathActivity/fileserver", "setServer", 0);
		dig.addSetNext(root + "/checkPathActivity", "addActivity");
		
			dig.addObjectCreate(root + "/checkTableActivity", CheckTableActivity.class);
			dig.addCallMethod(root + "/checkTableActivity/name", "setName", 0);
			dig.addCallMethod(root + "/checkTableActivity/comment", "setComment", 0);
			dig.addCallMethod(root + "/checkTableActivity/xPos", "setPositionX", 0);
			dig.addCallMethod(root + "/checkTableActivity/yPos", "setPositionY", 0);
			dig.addCallMethod(root + "/checkTableActivity/yRel", "setRelativePositionY", 0);
			dig.addCallMethod(root + "/checkTableActivity/width", "setPositionWidth", 0);
			dig.addCallMethod(root + "/checkTableActivity/height", "setPositionHeight", 0);
			dig.addCallMethod(root + "/checkTableActivity/parent", "setParent", 0);
			dig.addCallMethod(root + "/checkTableActivity/varRefName", "setVariable", 0);
			dig.addCallMethod(root + "/checkTableActivity/path", "setPath", 0);
			dig.addCallMethod(root + "/checkTableActivity/fileserver", "setServer", 0);
		dig.addSetNext(root + "/checkTableActivity", "addActivity");
	
			dig.addObjectCreate(root + "/checkColumnActivity", CheckColumnActivity.class);
			dig.addCallMethod(root + "/checkColumnActivity/name", "setName", 0);
			dig.addCallMethod(root + "/checkColumnActivity/comment", "setComment", 0);
			dig.addCallMethod(root + "/checkColumnActivity/xPos", "setPositionX", 0);
			dig.addCallMethod(root + "/checkColumnActivity/yPos", "setPositionY", 0);
			dig.addCallMethod(root + "/checkColumnActivity/yRel", "setRelativePositionY", 0);
			dig.addCallMethod(root + "/checkColumnActivity/width", "setPositionWidth", 0);
			dig.addCallMethod(root + "/checkColumnActivity/height", "setPositionHeight", 0);
			dig.addCallMethod(root + "/checkColumnActivity/parent", "setParent", 0);
			dig.addCallMethod(root + "/checkColumnActivity/varRefName", "setVariable", 0);
			dig.addCallMethod(root + "/checkColumnActivity/path", "setTable", 0);
			dig.addCallMethod(root + "/checkColumnActivity/fileserver", "setServer", 0);
			dig.addCallMethod(root + "/checkColumnActivity/columnName", "setColumnName", 0);
			dig.addCallMethod(root + "/checkColumnActivity/typeOfColumn", "setTypeOfColumn", 0);
		dig.addSetNext(root + "/checkColumnActivity", "addActivity");

			
			dig.addObjectCreate(root + "/mailActivity", MailActivity.class);
				dig.addCallMethod(root + "/mailActivity/name", "setName", 0);
				dig.addCallMethod(root + "/mailActivity/comment", "setComment", 0);
				dig.addCallMethod(root + "/mailActivity/xPos", "setPositionX", 0);
				dig.addCallMethod(root + "/mailActivity/yPos", "setPositionY", 0);
				dig.addCallMethod(root + "/mailActivity/yRel", "setRelativePositionY", 0);
				dig.addCallMethod(root + "/mailActivity/width", "setPositionWidth", 0);
				dig.addCallMethod(root + "/mailActivity/height", "setPositionHeight", 0);
				dig.addCallMethod(root + "/mailActivity/parent", "setParent", 0);
				dig.addCallMethod(root + "/mailActivity/typeact", "setTypeact", 0);
				
				dig.addCallMethod(root + "/mailActivity/object", "setObject", 0);
				dig.addCallMethod(root + "/mailActivity/serverRefName", "setServer", 0);
				dig.addCallMethod(root + "/mailActivity/content", "setContent", 0);
				dig.addCallMethod(root + "/mailActivity/destination", "setDestination", 0);
				dig.addCallMethod(root + "/mailActivity/from", "setFrom", 0);
//				dig.addCallMethod(root + "/mailActivity/variable", "setAdressVar", 0);
				dig.addCallMethod(root + "/mailActivity/varRef", "setVarRef", 0);
				
				dig.addCallMethod(root + "/mailActivity/attachment", "addInput", 0);
			dig.addSetNext(root + "/mailActivity", "addActivity");
			
			dig.addObjectCreate(root + "/sqlActivity", SqlActivity.class);
				dig.addCallMethod(root + "/sqlActivity/name", "setName", 0);
				dig.addCallMethod(root + "/sqlActivity/comment", "setComment", 0);
				dig.addCallMethod(root + "/sqlActivity/xPos", "setPositionX", 0);
				dig.addCallMethod(root + "/sqlActivity/yPos", "setPositionY", 0);
				dig.addCallMethod(root + "/sqlActivity/yRel", "setRelativePositionY", 0);
				dig.addCallMethod(root + "/sqlActivity/width", "setPositionWidth", 0);
				dig.addCallMethod(root + "/sqlActivity/height", "setPositionHeight", 0);
				dig.addCallMethod(root + "/sqlActivity/parent", "setParent", 0);
				dig.addCallMethod(root + "/sqlActivity/serverRefName", "setServer", 0);
//				dig.addCallMethod(root + "/sqlActivity/varRefName", "setVariable", 0);
				dig.addCallMethod(root + "/sqlActivity/query", "setQuery", 0);
				
				dig.addCallMethod(root + "/sqlActivity/links/link", "linkFieldToVariable", 2);
				dig.addCallParam(root + "/sqlActivity/links/link/sqlfield", 0);
				dig.addCallParam(root + "/sqlActivity/links/link/variable", 1);

			dig.addSetNext(root + "/sqlActivity", "addActivity");
			
			dig.addObjectCreate(root + "/GetFTPActivity", GetFTPActivity.class);
				dig.addCallMethod(root + "/GetFTPActivity/name", "setName", 0);
				dig.addCallMethod(root + "/GetFTPActivity/outputname", "setOutputName", 0);
				dig.addCallMethod(root + "/GetFTPActivity/pathtostore", "setPathToStore", 0);
				dig.addCallMethod(root + "/GetFTPActivity/comment", "setComment", 0);
				dig.addCallMethod(root + "/GetFTPActivity/xPos", "setPositionX", 0);
				dig.addCallMethod(root + "/GetFTPActivity/yPos", "setPositionY", 0);
				dig.addCallMethod(root + "/GetFTPActivity/yRel", "setRelativePositionY", 0);
				dig.addCallMethod(root + "/GetFTPActivity/width", "setPositionWidth", 0);
				dig.addCallMethod(root + "/GetFTPActivity/height", "setPositionHeight", 0);
				dig.addCallMethod(root + "/GetFTPActivity/parent", "setParent", 0);
				dig.addCallMethod(root + "/GetFTPActivity/fileserver", "setServer", 0);
				dig.addCallMethod(root + "/GetFTPActivity/filestotreat", "setFilesToTreat", 0);
			dig.addSetNext(root + "/GetFTPActivity", "addActivity");
			
			
			dig.addObjectCreate(root + "/GetSFTPActivity", GetSFTPActivity.class);
				dig.addCallMethod(root + "/GetSFTPActivity/name", "setName", 0);
				dig.addCallMethod(root + "/GetSFTPActivity/outputname", "setOutputName", 0);
				dig.addCallMethod(root + "/GetSFTPActivity/pathtostore", "setPathToStore", 0);
				dig.addCallMethod(root + "/GetSFTPActivity/comment", "setComment", 0);
				dig.addCallMethod(root + "/GetSFTPActivity/xPos", "setPositionX", 0);
				dig.addCallMethod(root + "/GetSFTPActivity/yPos", "setPositionY", 0);
				dig.addCallMethod(root + "/GetSFTPActivity/yRel", "setRelativePositionY", 0);
				dig.addCallMethod(root + "/GetSFTPActivity/width", "setPositionWidth", 0);
				dig.addCallMethod(root + "/GetSFTPActivity/height", "setPositionHeight", 0);
				dig.addCallMethod(root + "/GetSFTPActivity/parent", "setParent", 0);
				dig.addCallMethod(root + "/GetSFTPActivity/fileserver", "setServer", 0);
				dig.addCallMethod(root + "/GetSFTPActivity/filestotreat", "setFilesToTreat", 0);
			dig.addSetNext(root + "/GetSFTPActivity", "addActivity");
		
				dig.addObjectCreate(root + "/GetHardDActivity", GetHardDActivity.class);
				dig.addCallMethod(root + "/GetHardDActivity/name", "setName", 0);
				dig.addCallMethod(root + "/GetHardDActivity/comment", "setComment", 0);
				dig.addCallMethod(root + "/GetHardDActivity/xPos", "setPositionX", 0);
				dig.addCallMethod(root + "/GetHardDActivity/yPos", "setPositionY", 0);
				dig.addCallMethod(root + "/GetHardDActivity/yRel", "setRelativePositionY", 0);
				dig.addCallMethod(root + "/GetHardDActivity/width", "setPositionWidth", 0);
				dig.addCallMethod(root + "/GetHardDActivity/height", "setPositionHeight", 0);
				dig.addCallMethod(root + "/GetHardDActivity/parent", "setParent", 0);
				dig.addCallMethod(root + "/GetHardDActivity/fileserver", "setServer", 0);
				dig.addCallMethod(root + "/GetHardDActivity/variable", "setVariable", 0);
				dig.addCallMethod(root + "/GetHardDActivity/repository", "setRepository", 0);
				dig.addCallMethod(root + "/GetHardDActivity/filestotreat", "setFilesToTreat", 0);
				dig.addSetNext(root + "/GetHardDActivity", "addActivity");
	
			
			dig.addObjectCreate(root + "/PutHardDActivity", PutHardDActivity.class);
				dig.addCallMethod(root + "/PutHardDActivity/name", "setName", 0);
				dig.addCallMethod(root + "/PutHardDActivity/comment", "setComment", 0);
				dig.addCallMethod(root + "/PutHardDActivity/xPos", "setPositionX", 0);
				dig.addCallMethod(root + "/PutHardDActivity/yPos", "setPositionY", 0);
				dig.addCallMethod(root + "/PutHardDActivity/yRel", "setRelativePositionY", 0);
				dig.addCallMethod(root + "/PutHardDActivity/width", "setPositionWidth", 0);
				dig.addCallMethod(root + "/PutHardDActivity/height", "setPositionHeight", 0);
				dig.addCallMethod(root + "/PutHardDActivity/parent", "setParent", 0);
				dig.addCallMethod(root + "/PutHardDActivity/fileserver", "setServer", 0);
				dig.addCallMethod(root + "/PutHardDActivity/variable", "setVariable", 0);
				dig.addCallMethod(root + "/PutHardDActivity/repository", "setRepository", 0);
			dig.addSetNext(root + "/PutHardDActivity", "addActivity");
			
			dig.addObjectCreate(root + "/GetMailServActivity", GetMailServActivity.class);
				dig.addCallMethod(root + "/GetMailServActivity/name", "setName", 0);
				dig.addCallMethod(root + "/GetMailServActivity/comment", "setComment", 0);
				dig.addCallMethod(root + "/GetMailServActivity/xPos", "setPositionX", 0);
				dig.addCallMethod(root + "/GetMailServActivity/yPos", "setPositionY", 0);
				dig.addCallMethod(root + "/GetMailServActivity/yRel", "setRelativePositionY", 0);
				dig.addCallMethod(root + "/GetMailServActivity/width", "setPositionWidth", 0);
				dig.addCallMethod(root + "/GetMailServActivity/height", "setPositionHeight", 0);
				dig.addCallMethod(root + "/GetMailServActivity/parent", "setParent", 0);
				dig.addCallMethod(root + "/GetMailServActivity/fileserver", "setServer", 0);
				dig.addCallMethod(root + "/GetMailServActivity/varRefName", "setVariable", 0);
			dig.addSetNext(root + "/GetMailServActivity", "addActivity");

			dig.addObjectCreate(root + "/PutSFTPActivity", PutSFTPActivity.class);
				dig.addCallMethod(root + "/PutSFTPActivity/name", "setName", 0);
				dig.addCallMethod(root + "/PutSFTPActivity/comment", "setComment", 0);
				dig.addCallMethod(root + "/PutSFTPActivity/xPos", "setPositionX", 0);
				dig.addCallMethod(root + "/PutSFTPActivity/yPos", "setPositionY", 0);
				dig.addCallMethod(root + "/PutSFTPActivity/yRel", "setRelativePositionY", 0);
				dig.addCallMethod(root + "/PutSFTPActivity/width", "setPositionWidth", 0);
				dig.addCallMethod(root + "/PutSFTPActivity/height", "setPositionHeight", 0);
				dig.addCallMethod(root + "/PutSFTPActivity/parent", "setParent", 0);
				dig.addCallMethod(root + "/PutSFTPActivity/fileserver", "setServer", 0);
				dig.addCallMethod(root + "/PutSFTPActivity/varRefName", "setVariable", 0);
				dig.addCallMethod(root + "/PutSFTPActivity/filestomove", "addInput", 0);
			dig.addSetNext(root + "/PutSFTPActivity", "addActivity");
			
			dig.addObjectCreate(root + "/PutSSHActivity", PutSSHActivity.class);
			dig.addCallMethod(root + "/PutSSHActivity/name", "setName", 0);
			dig.addCallMethod(root + "/PutSSHActivity/comment", "setComment", 0);
			dig.addCallMethod(root + "/PutSSHActivity/xPos", "setPositionX", 0);
			dig.addCallMethod(root + "/PutSSHActivity/yPos", "setPositionY", 0);
			dig.addCallMethod(root + "/PutSSHActivity/yRel", "setRelativePositionY", 0);
			dig.addCallMethod(root + "/PutSSHActivity/width", "setPositionWidth", 0);
			dig.addCallMethod(root + "/PutSSHActivity/height", "setPositionHeight", 0);
			dig.addCallMethod(root + "/PutSSHActivity/parent", "setParent", 0);
			dig.addCallMethod(root + "/PutSSHActivity/fileserver", "setServer", 0);
			dig.addCallMethod(root + "/PutSSHActivity/varRefName", "setVariable", 0);
			dig.addCallMethod(root + "/PutSSHActivity/filestomove", "addInput", 0);
		dig.addSetNext(root + "/PutSSHActivity", "addActivity");
				
			dig.addObjectCreate(root + "/PutFTPActivity", PutFTPActivity.class);
				dig.addCallMethod(root + "/PutFTPActivity/name", "setName", 0);
				dig.addCallMethod(root + "/PutFTPActivity/comment", "setComment", 0);
				dig.addCallMethod(root + "/PutFTPActivity/xPos", "setPositionX", 0);
				dig.addCallMethod(root + "/PutFTPActivity/yPos", "setPositionY", 0);
				dig.addCallMethod(root + "/PutFTPActivity/yRel", "setRelativePositionY", 0);
				dig.addCallMethod(root + "/PutFTPActivity/width", "setPositionWidth", 0);
				dig.addCallMethod(root + "/PutFTPActivity/height", "setPositionHeight", 0);
				dig.addCallMethod(root + "/PutFTPActivity/parent", "setParent", 0);
				dig.addCallMethod(root + "/PutFTPActivity/fileserver", "setServer", 0);
				dig.addCallMethod(root + "/PutFTPActivity/varRefName", "setVariable", 0);
				dig.addCallMethod(root + "/PutFTPActivity/filestomove", "addInput", 0);
			dig.addSetNext(root + "/PutFTPActivity", "addActivity");
				
			dig.addObjectCreate(root + "/CommandFTPActivity", CommandFTPActivity.class);
				dig.addCallMethod(root + "/CommandFTPActivity/name", "setName", 0);
				dig.addCallMethod(root + "/CommandFTPActivity/comment", "setComment", 0);
				dig.addCallMethod(root + "/CommandFTPActivity/xPos", "setPositionX", 0);
				dig.addCallMethod(root + "/CommandFTPActivity/yPos", "setPositionY", 0);
				dig.addCallMethod(root + "/CommandFTPActivity/yRel", "setRelativePositionY", 0);
				dig.addCallMethod(root + "/CommandFTPActivity/width", "setPositionWidth", 0);
				dig.addCallMethod(root + "/CommandFTPActivity/height", "setPositionHeight", 0);
				dig.addCallMethod(root + "/CommandFTPActivity/parent", "setParent", 0);
				dig.addCallMethod(root + "/CommandFTPActivity/fileserver", "setServer", 0);
				dig.addCallMethod(root + "/CommandFTPActivity/varRefName", "setVariable", 0);
				dig.addCallMethod(root + "/CommandFTPActivity/command", "setCommand", 0);
			dig.addSetNext(root + "/CommandFTPActivity", "addActivity");
				
				

			dig.addObjectCreate(root + "/PutMailServActivity", PutMailServActivity.class);
				dig.addCallMethod(root + "/PutMailServActivity/name", "setName", 0);
				dig.addCallMethod(root + "/PutMailServActivity/comment", "setComment", 0);
				dig.addCallMethod(root + "/PutMailServActivity/xPos", "setPositionX", 0);
				dig.addCallMethod(root + "/PutMailServActivity/yPos", "setPositionY", 0);
				dig.addCallMethod(root + "/PutMailServActivity/yRel", "setRelativePositionY", 0);
				dig.addCallMethod(root + "/PutMailServActivity/width", "setPositionWidth", 0);
				dig.addCallMethod(root + "/PutMailServActivity/height", "setPositionHeight", 0);
				dig.addCallMethod(root + "/PutMailServActivity/parent", "setParent", 0);
				dig.addCallMethod(root + "/PutMailServActivity/fileserver", "setServer", 0);
				dig.addCallMethod(root + "/PutMailServActivity/varRefName", "setVariable", 0);
			dig.addSetNext(root + "/PutMailServActivity", "addActivity");

			
			dig.addObjectCreate(root + "/KPIActivity", KPIActivity.class);
			dig.addCallMethod(root + "/KPIActivity/name", "setName", 0);
			dig.addCallMethod(root + "/KPIActivity/comment", "setComment", 0);
			dig.addCallMethod(root + "/KPIActivity/xPos", "setPositionX", 0);
			dig.addCallMethod(root + "/KPIActivity/yPos", "setPositionY", 0);
			dig.addCallMethod(root + "/KPIActivity/yRel", "setRelativePositionY", 0);
			dig.addCallMethod(root + "/KPIActivity/width", "setPositionWidth", 0);
			dig.addCallMethod(root + "/KPIActivity/height", "setPositionHeight", 0);
			dig.addCallMethod(root + "/KPIActivity/parent", "setParent", 0);
			dig.addCallMethod(root + "/KPIActivity/serverRefName", "setServer", 0);
			dig.addCallMethod(root + "/KPIActivity/idAssoc", "setAssocid", 0);
			dig.addCallMethod(root + "/KPIActivity/comparator", "setComparator", 0);
		dig.addSetNext(root + "/KPIActivity", "addActivity");
		
			dig.addObjectCreate(root + "/browsecontent", BrowseContentActivity.class);
			dig.addCallMethod(root + "/browsecontent/name", "setName", 0);
			dig.addCallMethod(root + "/browsecontent/outputname", "setOutputName", 0);
			dig.addCallMethod(root + "/browsecontent/pathtostore", "setPathToStore", 0);
			dig.addCallMethod(root + "/browsecontent/comment", "setComment", 0);
			dig.addCallMethod(root + "/browsecontent/xPos", "setPositionX", 0);
			dig.addCallMethod(root + "/browsecontent/yPos", "setPositionY", 0);
			dig.addCallMethod(root + "/browsecontent/yRel", "setRelativePositionY", 0);
			dig.addCallMethod(root + "/browsecontent/width", "setPositionWidth", 0);
			dig.addCallMethod(root + "/browsecontent/height", "setPositionHeight", 0);
			dig.addCallMethod(root + "/browsecontent/parent", "setParent", 0);
			dig.addCallMethod(root + "/browsecontent/fileserver", "setServer", 0);
			dig.addCallMethod(root + "/browsecontent/filestotreat", "setFilesToTreat", 0);
			dig.addCallMethod(root + "/browsecontent/vartofill", "setVarToFill", 0);
			dig.addCallMethod(root + "/browsecontent/parameter", "setParameter", 0);
		dig.addSetNext(root + "/browsecontent", "addActivity");

			dig.addObjectCreate(root + "/transition", Transition.class);
				dig.addCallMethod(root + "/transition/sourceRef", "setSourceRef", 0);
				dig.addCallMethod(root + "/transition/targetRef", "setTargetRef", 0);
				dig.addCallMethod(root + "/transition/color", "setColor",0);
				
				dig.addObjectCreate(root + "/transition/condition", Condition.class);
					dig.addCallMethod(root + "/transition/condition/variable", "setVariable", 0);
					dig.addCallMethod(root + "/transition/condition/operator", "setOperator", 0);
					dig.addCallMethod(root + "/transition/condition/value", "setValue", 0);
				dig.addSetNext(root + "/transition/condition", "setCondition");
			dig.addSetNext(root + "/transition", "addTransition");

			
		dig.addObjectCreate(root + "/profilingActivity", ProfilingActivity.class);
			dig.addCallMethod(root + "/profilingActivity/name", "setName", 0);
			dig.addCallMethod(root + "/profilingActivity/xPos", "setPositionX", 0);
			dig.addCallMethod(root + "/profilingActivity/yPos", "setPositionY", 0);
			dig.addCallMethod(root + "/profilingActivity/yRel", "setRelativePositionY", 0);
			dig.addCallMethod(root + "/profilingActivity/width", "setPositionWidth", 0);
			dig.addCallMethod(root + "/profilingActivity/height", "setPositionHeight", 0);
			dig.addCallMethod(root + "/profilingActivity/parent", "setParent", 0);
			
			dig.addObjectCreate(root + "/profilingActivity/profilingQuery", ProfilingQueryObject.class);
				dig.addCallMethod(root + "/profilingActivity/profilingQuery/id", "setId", 0);
				dig.addCallMethod(root + "/profilingActivity/profilingQuery/name", "setName", 0);
				dig.addCallMethod(root + "/profilingActivity/profilingQuery/dataBaseRef", "setDataBaseRef", 0);
			dig.addSetNext(root + "/profilingActivity/profilingQuery", "setBiObject");
		dig.addSetNext(root + "/profilingActivity", "addActivity");
		
		
		dig.addObjectCreate(root + "/taskListActivity", TaskListActivity.class);
		dig.addCallMethod(root + "/taskListActivity/name", "setName", 0);
		dig.addCallMethod(root + "/taskListActivity/comment", "setComment", 0);
		dig.addCallMethod(root + "/taskListActivity/xPos", "setPositionX", 0);
		dig.addCallMethod(root + "/taskListActivity/yPos", "setPositionY", 0);
		dig.addCallMethod(root + "/taskListActivity/yRel", "setRelativePositionY", 0);
		dig.addCallMethod(root + "/taskListActivity/width", "setPositionWidth", 0);
		dig.addCallMethod(root + "/taskListActivity/height", "setPositionHeight", 0);
		dig.addCallMethod(root + "/taskListActivity/parent", "setParent", 0);

		
			dig.addObjectCreate(root + "/taskListActivity/biRepositoryObject", TaskListObject.class);
				dig.addCallMethod(root + "/taskListActivity/biRepositoryObject/id", "setId", 0);
				dig.addCallMethod(root + "/taskListActivity/biRepositoryObject/name", "setName", 0);
				
				dig.addCallMethod(root + "/taskListActivity/biRepositoryObject/itemname", "setItemName", 0);
			dig.addSetNext(root + "/taskListActivity/biRepositoryObject", "setTaskListObject");
		dig.addSetNext(root + "/taskListActivity", "addActivity");
		
		dig.addObjectCreate(root + "/comment", Comment.class);
			dig.addCallMethod(root + "/comment/id", "setId", 0);
			dig.addCallMethod(root + "/comment/name", "setName", 0);
			dig.addCallMethod(root + "/comment/comment", "setComment", 0);
			dig.addCallMethod(root + "/comment/type", "setType", 0);
			dig.addCallMethod(root + "/comment/xPos", "setPositionX", 0);
			dig.addCallMethod(root + "/comment/yPos", "setPositionY", 0);
			dig.addCallMethod(root + "/comment/yRel", "setRelativePositionY", 0);
			dig.addCallMethod(root + "/comment/width", "setPositionWidth", 0);
			dig.addCallMethod(root + "/comment/height", "setPositionHeight", 0);
			dig.addCallMethod(root + "/comment/parent", "setParent", 0);
		dig.addSetNext(root + "/comment", "addActivity");

		
		dig.addObjectCreate(root + "/encryptFileActivity", EncryptFileActivity.class);
			dig.addCallMethod(root + "/encryptFileActivity/name", "setName", 0);
			dig.addCallMethod(root + "/encryptFileActivity/xPos", "setPositionX", 0);
			dig.addCallMethod(root + "/encryptFileActivity/yPos", "setPositionY", 0);
			dig.addCallMethod(root + "/encryptFileActivity/yRel", "setRelativePositionY", 0);
			dig.addCallMethod(root + "/encryptFileActivity/width", "setPositionWidth", 0);
			dig.addCallMethod(root + "/encryptFileActivity/height", "setPositionHeight", 0);
			dig.addCallMethod(root + "/encryptFileActivity/parent", "setParent", 0);
			
			dig.addCallMethod(root + "/encryptFileActivity/pathtostore", "setPathToStore", 0);
			dig.addCallMethod(root + "/encryptFileActivity/pathtoencrypt", "addInput", 0);
			dig.addCallMethod(root + "/encryptFileActivity/outputName", "setOutputName", 0);
			dig.addCallMethod(root + "/encryptFileActivity/encrypt", "setEncrypt", 0);
			dig.addCallMethod(root + "/encryptFileActivity/publicKeyPath", "setPublicKeyPath", 0);
			dig.addCallMethod(root + "/encryptFileActivity/privateKeyPath", "setPrivateKeyPath", 0);
			dig.addCallMethod(root + "/encryptFileActivity/publicKeyName", "setPublicKeyName", 0);
			dig.addCallMethod(root + "/encryptFileActivity/privateKeyName", "setPrivateKeyName", 0);
			dig.addCallMethod(root + "/encryptFileActivity/password", "setPassword", 0);
			
		dig.addSetNext(root + "/encryptFileActivity", "addActivity");
		
		dig.addObjectCreate(root + "/AirActivity", AirActivity.class);
			dig.addCallMethod(root + "/AirActivity/name", "setName", 0);
			dig.addCallMethod(root + "/AirActivity/comment", "setComment", 0);
			dig.addCallMethod(root + "/AirActivity/xPos", "setPositionX", 0);
			dig.addCallMethod(root + "/AirActivity/yPos", "setPositionY", 0);
			dig.addCallMethod(root + "/AirActivity/yRel", "setRelativePositionY", 0);
			dig.addCallMethod(root + "/AirActivity/width", "setPositionWidth", 0);
			dig.addCallMethod(root + "/AirActivity/height", "setPositionHeight", 0);
			dig.addCallMethod(root + "/AirActivity/parent", "setParent", 0);
			
			dig.addCallMethod(root + "/AirActivity/RId", "setRId", 0);
			dig.addCallMethod(root + "/AirActivity/RSModelId", "setRSModelId", 0);
			dig.addCallMethod(root + "/AirActivity/NameR", "setNameR", 0);
			dig.addCallMethod(root + "/AirActivity/path", "setPath", 0);
			dig.addCallMethod(root + "/AirActivity/RChoice", "setRChoice", 0);
			dig.addCallMethod(root + "/AirActivity/choice", "setChoice", 0);
			dig.addCallMethod(root + "/AirActivity/saveScript", "setSaveScript", 0);
			dig.addCallMethod(root + "/AirActivity/script", "setScripText", 0);
		
		dig.addSetNext(root + "/AirActivity", "addActivity");
		
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the WorkflowModel from a path
	 * @param classLoader
	 * @param filePath
	 * @return the workflowmodel contained in the file located thanks to the filePath
	 * @throws IOException
	 * @throws SAXException
	 */
	public static WorkflowModel getModel(ClassLoader classLoader, File file) throws IOException, SAXException{
		WorkflowDigester dig = new WorkflowDigester(classLoader);
		
		try{
		WorkflowModel m = (WorkflowModel)dig.dig.parse(file);
		
		purgeModel(m, dig.error);
		return m;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	
	public static WorkflowModel getModel(ClassLoader classLoader, String modelXml) throws IOException, SAXException{
		WorkflowDigester dig = new WorkflowDigester(classLoader);
		
		try{
		WorkflowModel m = (WorkflowModel)dig.dig.parse(IOUtils.toInputStream(modelXml, "UTF-8"));
		
		purgeModel(m, dig.error);
		return m;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get the WorkflowModel from an InputStream
	 * @param classLoader
	 * @param inputStream
	 * @return the workflowmodel defined in the inputstream
	 * @throws IOException
	 * @throws SAXException
	 */
	public static WorkflowModel getModel(ClassLoader classLoader, InputStream inputStream) throws IOException, SAXException{
		WorkflowDigester dig = new WorkflowDigester(classLoader);
		
		WorkflowModel m = (WorkflowModel)dig.dig.parse(inputStream);
		
		purgeModel(m, dig.error);
		return m;
	}
	
	/**
	 * Purge the model
	 * @param model
	 * @param buf
	 */
	private static void purgeModel(WorkflowModel model, StringBuffer buf){
		for(IActivity a : model.getActivities().values()){
			if (a instanceof ProfilingActivity && ((ProfilingActivity)a).getBiObject() != null){
				ProfilingQueryObject r = ((ProfilingActivity)a).getBiObject();
				r.setDataBaseServer((DataBaseServer)model.getResource(r.getDataBaseRef()));
			}
		}
		
		for(Transition t : model.getTransitions()){
			try {
				t.rebuildReferences(model);
			} catch (WorkflowException e) {
				e.printStackTrace();
				buf.append(" -  Error while building transition : " + e.getMessage() + "\n\n");
			}
		}
		
		for (PoolModel p : model.getPools()) {
			for (String a : p.getReferences()) {
				IActivity act = model.getActivity(a);
				try {
					p.addChild((IActivity) act);
				} catch (Exception e) {

				}
			}
		}
	}
	
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

}
