package bpm.vanilla.workplace.api.datasource.replacement;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import bpm.fwr.api.beans.FWRReport;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.api.beans.dataset.DataSource;
import bpm.vanilla.platform.core.utils.MD5Helper;
import bpm.vanilla.workplace.core.datasource.IDatasource;
import bpm.vanilla.workplace.core.datasource.IDatasourceReplacement;
import bpm.vanilla.workplace.core.datasource.ModelDatasourceRepository;

import com.thoughtworks.xstream.XStream;

public class FWRDatasourceReplacement implements IDatasourceReplacement{

	private static final String NODE_DATASET = "dataset";
	private static final String NODE_NAME = "name";
	private static final String NODE_DATASOURCE = "datasource";
	private static final String NODE_GROUP = "group";
	private static final String NODE_ITEM = "itemid";
	private static final String NODE_PASSWORD = "password";
	private static final String NODE_URL = "url";
	private static final String NODE_USER = "user";

	private static final String NODE_DATASETS = "datasets";
	private static final String NODE_CONTAINER = "container";
	
	@Override
	public String replaceElement(String xml, IDatasource dsNew) throws Exception {
		try {
			return replaceForNewModel(xml, dsNew);
		} catch(Exception e) {
			e.printStackTrace();
			return replaceForOldModel(xml, dsNew);
		}
	}
	
	private String replaceForNewModel(String xml, IDatasource dsNew) {
		XStream xstream = new XStream();
		FWRReport report = (FWRReport) xstream.fromXML(xml);
		
		List<DataSet> datasets = report.getAllDatasets();
		if(datasets != null) {
			for(DataSet dataset : datasets){
				DataSource datasource = dataset.getDatasource();
				
				ModelDatasourceRepository ds = (ModelDatasourceRepository) dsNew;
				
				String password = MD5Helper.encode(ds.getPassword());
				
				datasource.setGroup(ds.getGroupName());
				datasource.setItemId(ds.getDirId());
				datasource.setUser(ds.getUser());
				datasource.setPassword(password);
				datasource.setUrl(ds.getRepositoryUrl());
				datasource.setRepositoryId(Integer.parseInt(ds.getRepositoryId()));
			}
		}
		
		return xstream.toXML(report);
	}

	private String replaceForOldModel(String xml, IDatasource dsNew) throws DocumentException, IOException {
		Document document = DocumentHelper.parseText(xml);
		Element root = null;
		for(Object e : document.getRootElement().elements()){
			if (((Element)e).element(NODE_CONTAINER) != null){
				root = ((Element)e).element(NODE_CONTAINER);
				break;
			}
		}
		
		if(root != null && root.element(NODE_DATASETS).elements(NODE_DATASET) != null){
			
			for(Object dataSet :  root.element(NODE_DATASETS).elements(NODE_DATASET)){
				Element eSource = ((Element)dataSet).element(NODE_DATASOURCE);
				
				String name = eSource.element(NODE_NAME).getText();
				
				if(name.equals(dsNew.getName())){
					ModelDatasourceRepository dsRepo = (ModelDatasourceRepository)dsNew;
					
					eSource.element(NODE_GROUP).setText(dsRepo.getGroupName());
					eSource.element(NODE_ITEM).setText(dsRepo.getDirId());
					eSource.element(NODE_PASSWORD).setText(dsRepo.getPassword());
					eSource.element(NODE_URL).setText(dsRepo.getRepositoryUrl());
					eSource.element(NODE_USER).setText(dsRepo.getUser());
				}
			}
		}
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		
		OutputFormat form = OutputFormat.createPrettyPrint();
		form.setTrimText(false);
		XMLWriter writer = new XMLWriter(bos, form);
		writer.write(document.getRootElement());
		writer.close();
		
		String result = bos.toString("UTF-8"); 
		
		return result;
	}
}
