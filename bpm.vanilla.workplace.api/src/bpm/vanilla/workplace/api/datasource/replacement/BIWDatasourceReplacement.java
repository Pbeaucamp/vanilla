package bpm.vanilla.workplace.api.datasource.replacement;

import java.io.ByteArrayOutputStream;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import bpm.vanilla.workplace.core.datasource.IDatasource;
import bpm.vanilla.workplace.core.datasource.IDatasourceReplacement;
import bpm.vanilla.workplace.core.datasource.ModelDatasourceJDBC;
import bpm.vanilla.workplace.core.datasource.ModelDatasourceRepository;

public class BIWDatasourceReplacement implements IDatasourceReplacement{
	
	private static final String NODE_SERVER = "dataBaseServer";
	
	private static final String NODE_GATEWAY_ACTIVITY = "gatewayActivity";
	private static final String NODE_GATEWAY_OBJECT = "gatewayObject";
	
	private static final String NODE_BIW_ACTIVITY = "BIWActivity";
	private static final String NODE_BIW_OBJECT = "BIWObject";
	
	private static final String NODE_START_STOP_ACTIVITY = "startstopactivity";
	private static final String NODE_START_STOP_OBJECT = "biRepositoryObject";
	
	private static final String NODE_BURST_ACTIVITY = "burstActivity";
	private static final String NODE_BURST_OBJECT = "birtObject";
	
	private static final String NODE_REPORT_ACTIVITY = "reportActivity";
	private static final String NODE_REPORT_FWR_OBJECT = "fwrObject";
	private static final String NODE_REPORT_BIRT_OBJECT = "birtObject";
	
	@Override
	public String replaceElement(String xml, IDatasource dsNew) throws Exception {
		Document document = DocumentHelper.parseText(xml);
		Element root = document.getRootElement();
		
		if(root.elements(NODE_GATEWAY_ACTIVITY) != null){
			for(Object o : root.elements(NODE_GATEWAY_ACTIVITY)){
				
				String datasourceName = ((Element)o).element("name").getText();
				if(dsNew.getName().equals(datasourceName)){
				
					ModelDatasourceRepository dsRepo = (ModelDatasourceRepository)dsNew;
					
					Element el = ((Element)o).element(NODE_GATEWAY_OBJECT);
					
					el.element("id").setText(dsRepo.getDirId());
				}
			}
		}
		
		if(root.elements(NODE_BIW_ACTIVITY) != null){
			for(Object o : root.elements(NODE_BIW_ACTIVITY)){
				
				String datasourceName = ((Element)o).element("name").getText();
				if(dsNew.getName().equals(datasourceName)){
				
					ModelDatasourceRepository dsRepo = (ModelDatasourceRepository)dsNew;
					
					Element el = ((Element)o).element(NODE_BIW_OBJECT);
					
					el.element("id").setText(dsRepo.getDirId());
				}
			}
		}
		
		if(root.elements(NODE_START_STOP_ACTIVITY) != null){
			for(Object o : root.elements(NODE_START_STOP_ACTIVITY)){
				
				String datasourceName = ((Element)o).element("name").getText();
				if(dsNew.getName().equals(datasourceName)){
				
					ModelDatasourceRepository dsRepo = (ModelDatasourceRepository)dsNew;
				
					Element el = ((Element)o).element(NODE_START_STOP_OBJECT);
					
					el.element("id").setText(dsRepo.getDirId());

				}
			}
		}
		
		if(root.elements(NODE_BURST_ACTIVITY) != null){
			for(Object o : root.elements(NODE_BURST_ACTIVITY)){
				
				String datasourceName = ((Element)o).element("name").getText();
				if(dsNew.getName().equals(datasourceName)){
				
					ModelDatasourceRepository dsRepo = (ModelDatasourceRepository)dsNew;
				
					Element el = ((Element)o).element(NODE_BURST_OBJECT);
					
					el.element("id").setText(dsRepo.getDirId());
				}
			}
		}
		
		if(root.elements(NODE_REPORT_ACTIVITY) != null){
			for(Object o : root.elements(NODE_REPORT_ACTIVITY)){
				
				String datasourceName = ((Element)o).element("name").getText();
				if(dsNew.getName().equals(datasourceName)){
				
					ModelDatasourceRepository dsRepo = (ModelDatasourceRepository)dsNew;
					
					Element el = ((Element)o).element(NODE_REPORT_FWR_OBJECT);
					if(el != null){
						el.element("id").setText(dsRepo.getDirId());
					}
					
					Element el2 = ((Element)o).element(NODE_REPORT_BIRT_OBJECT);
					if(el2 != null){
						el2.element("id").setText(dsRepo.getDirId());
					}
				}
			}
		}
		
		if(root.elements(NODE_SERVER) != null){
			for(Object o : root.elements(NODE_SERVER)){
				
				String datasourceName = ((Element)o).element("name").getText();
				if(dsNew.getName().equals(datasourceName)){
				
					ModelDatasourceJDBC dsJdbc = (ModelDatasourceJDBC)dsNew;

					if(((Element)o).element("url") != null){
						((Element)o).element("url").setText(dsJdbc.getHost());
					}
					
					if(((Element)o).element("port") != null){
						((Element)o).element("port").setText(dsJdbc.getPort());
					}
					
					if(((Element)o).element("dataBaseName") != null){
						((Element)o).element("dataBaseName").setText(dsJdbc.getDbName());
					}
					
					if(((Element)o).element("login") != null){
						((Element)o).element("login").setText(dsJdbc.getUser());
					}
					
					if(((Element)o).element("password") != null){
						((Element)o).element("password").setText(dsJdbc.getPassword());
					}
					
					if(((Element)o).element("jdbcDriver") != null){
						((Element)o).element("jdbcDriver").setText(dsJdbc.getDriver());
					}
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
//		return document.getRootElement().asXML();
	}
}
