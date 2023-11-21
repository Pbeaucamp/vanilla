package bpm.vanilla.server.gateway.server.generators;

import java.io.ByteArrayInputStream;
import java.util.Collection;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DataStream;
import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.transformations.inputs.FMDTInput;
import bpm.gateway.core.transformations.outputs.FileOutputCSV;
import bpm.gateway.core.transformations.outputs.FileOutputXLS;
import bpm.gateway.core.transformations.outputs.FileOutputXML;
import bpm.metadata.MetaDataReader;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.query.QuerySql;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.components.gateway.GatewayModelGeneration4Fmdt;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class TransfoFmdtGenerator {

	private GatewayModelGeneration4Fmdt config;
	private IRepositoryContext repContext;
	public TransfoFmdtGenerator(IRepositoryContext repContext, 
			GatewayModelGeneration4Fmdt config){
		
		this.repContext = repContext;
		this.config = config;
	}
	public DocumentGateway createTransfo() throws Exception{
		DocumentGateway gateway = new DocumentGateway();
		gateway.setProjectName(config.getTransformationName());
		gateway.setName(config.getTransformationName());
		
		gateway.setRepositoryContext(repContext);
		
		
		IRepositoryApi sock = new RemoteRepositoryApi(repContext);
		
		
		RepositoryItem fmdtItem = null;
		try{
			fmdtItem = sock.getRepositoryService().getDirectoryItem(config.getFmdtDirectoryItemId());
		}catch(Exception e){
			throw new Exception("Could not find the FMDT DirectoryItem within the repository", e);
		}
		String xml = null;
		
		try{
			xml = sock.getRepositoryService().loadModel(fmdtItem);
		}catch(Exception e){
			throw new Exception("Could not load the FMDT model from repository", e);
		}
		
		Collection<IBusinessModel> models = null;
		try {
			models = MetaDataReader.read(
					repContext.getGroup().getName(),
					new ByteArrayInputStream(xml.getBytes("UTF-8")),
					sock, 
					true);
		} catch (Exception e) {
			throw new Exception("Could not restore the FMDT model");
		}
		
		IBusinessPackage fmdtPack = null;
		
		for(IBusinessModel m : models){
			if (m.getName().equals(config.getFmdtBusinessModelName())){
				fmdtPack = m.getBusinessPackage(config.getFmdtBusinessPackageName(), repContext.getGroup().getName());
				break;
			}
		}
		
		if (fmdtPack == null){
			throw new Exception("Could not found the BusinessPackage " + config.getFmdtBusinessPackageName() + " from BusinessModel " + config.getFmdtBusinessModelName());
		}
		
		int count = 0;
		for(String tableName : config.getFmdtBusinessTableNames()){
			count++;
			//create input
			FMDTInput extract = new FMDTInput();
			extract.setBusinessModelName(config.getFmdtBusinessModelName());
			extract.setBusinessPackageName(config.getFmdtBusinessPackageName());
			extract.setName("Extract from BusinessTable " + tableName);
			extract.setTemporaryFilename("{$" + ResourceManager.VAR_GATEWAY_TEMP + "}" + extract.getName());
			extract.setRepositoryItemId(config.getFmdtDirectoryItemId());
			extract.setPositionX(10);
			extract.setPositionY(100 * count);
			//add input to model
			gateway.addTransformation(extract);
			
			//set the input descriptor throught a FMDT query
			IBusinessTable table = fmdtPack.getBusinessTable(repContext.getGroup().getName(), tableName);
			if (table == null){
				throw new Exception("Could not find the BusinessTable " + tableName);
			}
			
			QuerySql query = new QuerySql();
			query.getSelect().addAll(table.getColumnsOrdered(repContext.getGroup().getName()));
			
			extract.setDefinition(query.getXml());
			
			//create the output
			AbstractTransformation outputCsv = null;
			
			if ("XML".equals(config.getOutputType())){
				outputCsv = new FileOutputXML();
				((FileOutputXML)outputCsv).setEncoding(config.getEncoding());
				((FileOutputXML)outputCsv).setDelete(true);
			}
			else if ("XLS".equals(config.getOutputType())){
				outputCsv = new FileOutputXLS();
				((FileOutputXLS)outputCsv).setEncoding(config.getEncoding());
				((FileOutputXLS)outputCsv).setDelete(true);
				((FileOutputXLS)outputCsv).setAppend(false);
				((FileOutputXLS)outputCsv).setIncludeHeader(false);
				((FileOutputXLS)outputCsv).setSheetName(tableName);
			}
			else{
				outputCsv = new FileOutputCSV();
				((FileOutputCSV)outputCsv).setEncoding(config.getEncoding());
				((FileOutputCSV)outputCsv).setDelete(true);
				((FileOutputCSV)outputCsv).setSeparator(';');
				((FileOutputCSV)outputCsv).setAppend(false);
				
			}
			
			
			outputCsv.setPositionX(100);
			outputCsv.setPositionY(100 * count);
			outputCsv.setName("Write " + tableName + " on file");
			outputCsv.setTemporaryFilename("{$ENV_" + ResourceManager.VAR_GATEWAY_TEMP + "}" + outputCsv.getName());
			
			
			((DataStream)outputCsv).setDefinition("{$ENV_" + ResourceManager.VAR_GATEWAY_TEMP + "}" 
					+ tableName + ".csv");
		
			
			//add input to model
			gateway.addTransformation(outputCsv);
			
			//link steps
			extract.addOutput(outputCsv);
		}
		return gateway;
		
	}
}
