package bpm.vanilla.workplace.api.datasource.replacement;

import java.io.ByteArrayOutputStream;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import bpm.vanilla.workplace.core.datasource.IDatasource;
import bpm.vanilla.workplace.core.datasource.IDatasourceReplacement;
import bpm.vanilla.workplace.core.datasource.IDatasourceType;
import bpm.vanilla.workplace.core.datasource.ModelDatasourceJDBC;
import bpm.vanilla.workplace.core.datasource.ModelDatasourceRepository;

public class BIGDatasourceReplacement implements IDatasourceReplacement{
	
	private static final String NODE_ODA_INPUT = "odaInput";
	private static final String NODE_ODA_INPUT_WITH_PARAM = "odaInputWithParameters";
	
	private static final String NODE_PUBLIC_DATASOURCE = "publicDataSource";
	private static final String NODE_PRIVATE_DATASOURCE = "privateDataSource";
	
	private static final String NODE_SERVER = "servers";
	private static final String NODE_DATABASE_SERVER = "dataBaseServer";
	private static final String NODE_FM_SERVER = "freemetricsServer";
	private static final String NODE_DATABASE_CONNECTION = "dataBaseConnection";
	
	private static final String NODE_OLAP_INPUT = "olapInput";
	private static final String NODE_FMDT_INPUT = "fmdtInput";
	private static final String NODE_OLAP_DIM_INPUT = "olapDimensionInput";
	
	@Override
	public String replaceElement(String xml, IDatasource dsNew) throws Exception {
		Document document = DocumentHelper.parseText(xml);
		Element root = document.getRootElement();
		
		if (root.elements(NODE_ODA_INPUT) != null){
			
			for(Object o : root.elements(NODE_ODA_INPUT)){
				replaceDatasourceFromOdaInput(o, dsNew);
			}
		}
		
		if (root.elements(NODE_ODA_INPUT_WITH_PARAM) != null){
			
			for(Object o : root.elements(NODE_ODA_INPUT_WITH_PARAM)){
				replaceDatasourceFromOdaInput(o, dsNew);
			}
		}
		
		if (document.getRootElement().element(NODE_SERVER) != null){
			
			Element servers = document.getRootElement().element(NODE_SERVER);
			
			if(servers.elements(NODE_DATABASE_SERVER) != null){
				for(Object o : servers.elements(NODE_DATABASE_SERVER)){
					
					String datasourceName = ((Element)o).element("name").getText();
					if(dsNew.getName().equals(datasourceName)){
					
						ModelDatasourceJDBC dsJdbc = (ModelDatasourceJDBC)dsNew;
						
						Element con = ((Element)o).element(NODE_DATABASE_CONNECTION);
						if(con != null){
							if(con.element("host") != null){
								con.element("host").setText(dsJdbc.getHost());
							}
							
							if(con.element("port") != null){
								con.element("port").setText(dsJdbc.getPort());
							}
							
							if(con.element("dataBaseName") != null){
								con.element("dataBaseName").setText(dsJdbc.getDbName());
							}
							
							if(con.element("login") != null){
								con.element("login").setText(dsJdbc.getUser());
							}
							
							if(con.element("password") != null){
								con.element("password").setText(dsJdbc.getPassword());
							}
							
							if(con.element("driverName") != null){
								con.element("driverName").setText(dsJdbc.getDriver());
							}
							
							if(con.element("useFullUrl") != null){
								con.element("useFullUrl").setText(String.valueOf(dsJdbc.isUseFullUrl()));
							}
							
							if(con.element("fullUrl") != null){
								con.element("fullUrl").setText(dsJdbc.getFullUrl());
							}
						}
					}
				}
			}
			
			if(servers.elements(NODE_FM_SERVER) != null){
				for(Object o : servers.elements(NODE_FM_SERVER)){
					
					String datasourceName = ((Element)o).element("name").getText();
					if(dsNew.getName().equals(datasourceName)){
					
						ModelDatasourceJDBC dsJdbc = (ModelDatasourceJDBC)dsNew;
						
						((Element)o).element("freemtricsLogin").setText(dsJdbc.getFmUser());
						((Element)o).element("freemtricsPassword").setText(dsJdbc.getFmPassword());
						
						Element con = ((Element)o).element(NODE_DATABASE_CONNECTION);
						if(con != null){
							if(con.element("host") != null){
								con.element("host").setText(dsJdbc.getHost());
							}
							
							if(con.element("port") != null){
								con.element("port").setText(dsJdbc.getPort());
							}
							
							if(con.element("dataBaseName") != null){
								con.element("dataBaseName").setText(dsJdbc.getDbName());
							}
							
							if(con.element("login") != null){
								con.element("login").setText(dsJdbc.getUser());
							}
							
							if(con.element("password") != null){
								con.element("password").setText(dsJdbc.getPassword());
							}
							
							if(con.element("driverName") != null){
								con.element("driverName").setText(dsJdbc.getDriver());
							}
							
							if(con.element("useFullUrl") != null){
								con.element("useFullUrl").setText(String.valueOf(dsJdbc.isUseFullUrl()));
							}
							
							if(con.element("fullUrl") != null){
								con.element("fullUrl").setText(dsJdbc.getFullUrl());
							}
						}
					}
				}
			}
		}
		
		if (document.getRootElement().elements(NODE_OLAP_INPUT) != null){
			for(Object o : document.getRootElement().elements(NODE_OLAP_INPUT)){
				replaceDatasourceFromInput((Element)o, false, dsNew);
			}
		}
		
		if (document.getRootElement().elements(NODE_FMDT_INPUT) != null){
			for(Object o : document.getRootElement().elements(NODE_FMDT_INPUT)){
				replaceDatasourceFromInput((Element)o, true, dsNew);
			}
		}
		
		if (document.getRootElement().elements(NODE_OLAP_DIM_INPUT) != null){
			for(Object o : document.getRootElement().elements(NODE_OLAP_DIM_INPUT)){
				replaceDatasourceFromInput((Element)o, false, dsNew);
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

	private void replaceDatasourceFromInput(Element e, boolean fmdt, IDatasource dsNew){
		String datasourceName = e.element("name").getText();
		
		if(dsNew.getName().equals(datasourceName)){

			ModelDatasourceRepository dsRepo = (ModelDatasourceRepository)dsNew;
			if(!fmdt){
				Element con = e.element("directoryItemId");
				if(con != null){
					con.setText(dsRepo.getDirId());
				}
			}
			else {
				Element con = e.element("repositoryItemId");
				if(con != null){
					con.setText(dsRepo.getDirId());
				}
			}
		}
	}
	
	private void replaceDatasourceFromOdaInput(Object o, IDatasource dsNew){
		String datasourceName = ((Element)o).element("name").getText();
		
		if(dsNew.getName().equals(datasourceName)){
			if (dsNew.getExtensionId().equals(IDatasourceType.METADATA) 
					|| dsNew.getExtensionId().equals(IDatasourceType.METADATA_OLAP)){
	
				Element e = ((Element)o).element(NODE_PUBLIC_DATASOURCE);
				if(e != null){
					
					ModelDatasourceRepository dsRepo = (ModelDatasourceRepository)dsNew;
					
					for(Object p : ((Element)e).elements("property")){
						if(((Element)p).element("name").getText().equals(IDatasourceType.METADATA_USER)){
							((Element)p).element("value").setText(dsRepo.getUser());
						}
						else if(((Element)p).element("name").getText().equals(IDatasourceType.METADATA_PASSWORD)){
							((Element)p).element("value").setText(dsRepo.getPassword());
						}
						else if(((Element)p).element("name").getText().equals(IDatasourceType.METADATA_VANILLA_URL)){
							((Element)p).element("value").setText(dsRepo.getVanillaRuntimeUrl());
						}
						else if (((Element)p).element("name").getText().equals(IDatasourceType.METADATA_DIRECTORY_ITEM_ID)){
							((Element)p).element("value").setText(dsRepo.getDirId());
						}
						else if (((Element)p).element("name").getText().equals(IDatasourceType.GROUP_NAME)){
							((Element)p).element("value").setText(dsRepo.getGroupName());
						}
						else if (((Element)p).element("name").getText().equals(IDatasourceType.METADATA_REPOSITORY_ID)){
							((Element)p).element("value").setText(dsRepo.getRepositoryId());
						}
					}
				}
			}
			else if (dsNew.getExtensionId().equals(IDatasourceType.CSV_ODA) 
					|| dsNew.getExtensionId().equals(IDatasourceType.EXCEL_ODA)){
	
				Element p = ((Element)o).element(NODE_PRIVATE_DATASOURCE);
				if(p != null){

					ModelDatasourceRepository dsRepo = (ModelDatasourceRepository)dsNew;
					
					for(Object e : ((Element)p).elements("property")){
						if (((Element)e).element("name").getText().equals(IDatasourceType.CSV_REPOSITORY_ITEM_ID)){
							((Element)e).element("value").setText(dsRepo.getDirId());
						}
						else if (((Element)e).element("name").getText().equals(IDatasourceType.CSV_REPOSITORY_ID)){
							((Element)e).element("value").setText(dsRepo.getRepositoryId());
						}
						else if (((Element)e).element("name").getText().equals(IDatasourceType.CSV_GROUP_ID)){
							((Element)e).element("value").setText(dsRepo.getGroupId());
						}
						else if (((Element)e).element("name").getText().equals(IDatasourceType.CSV_USER)){
							((Element)e).element("value").setText(dsRepo.getUser());
						}
						else if (((Element)e).element("name").getText().equals(IDatasourceType.CSV_PASSWORD)){
							((Element)e).element("value").setText(dsRepo.getPassword());
						}
					}
				}
			}
			else if (dsNew.getExtensionId().equals(IDatasourceType.FWR_ODA)){
				
				Element e = ((Element)o).element(NODE_PUBLIC_DATASOURCE);
				if(e != null){

					ModelDatasourceRepository dsRepo = (ModelDatasourceRepository)dsNew;

					for(Object p : ((Element)e).elements("property")){
						if (((Element)p).element("name").getText().equals(IDatasourceType.FWR_USER)){
							((Element)p).element("value").setText(dsRepo.getUser());
						}
						else if (((Element)p).element("name").getText().equals(IDatasourceType.FWR_PASSWORD)){
							((Element)p).element("value").setText(dsRepo.getPassword());
						}
						else if (((Element)p).element("name").getText().equals(IDatasourceType.FWR_URL)){
							((Element)p).element("value").setText(dsRepo.getVanillaRuntimeUrl());
						}
						else if (((Element)p).element("name").getText().equals(IDatasourceType.FWR_REPOSITORY_URL)){
							((Element)p).element("value").setText(dsRepo.getRepositoryUrl());
						}
						else if (((Element)p).element("name").getText().equals(IDatasourceType.FWR_GROUP_ID)){
							((Element)p).element("value").setText(dsRepo.getGroupId());
						}
						else if (((Element)p).element("name").getText().equals(IDatasourceType.FWR_GROUP_NAME)){
							((Element)p).element("value").setText(dsRepo.getGroupName());
						}
					}
	
					Element g = ((Element)o).element(NODE_PRIVATE_DATASOURCE);
					if(g != null){
						for(Object f : ((Element)g).elements("property")){
							if (((Element)f).element("name").getText().equals(IDatasourceType.FWR_REPORT_ID)){
								((Element)f).element("value").setText(dsRepo.getDirId());
							}
						}
					}
				}
			}
			else if (dsNew.getExtensionId().equals(IDatasourceType.JDBC)){
	
				Element e = ((Element)o).element(NODE_PUBLIC_DATASOURCE);
				if(e != null){

					ModelDatasourceJDBC dsJdbc = (ModelDatasourceJDBC)dsNew;

					for(Object p : ((Element)e).elements("property")){
						if (((Element)p).element("name").getText().equals(IDatasourceType.ODA_DRIVER)){
							((Element)p).element("value").setText(dsJdbc.getDriver());
						}
						else if (((Element)p).element("name").getText().equals(IDatasourceType.ODA_URL)){
							((Element)p).element("value").setText(dsJdbc.getFullUrl());
						}
						else if (((Element)p).element("name").getText().equals(IDatasourceType.ODA_USER)){
							((Element)p).element("value").setText(dsJdbc.getUser());
						}
						else if (((Element)p).element("name").getText().equals(IDatasourceType.ODA_PASSWORD)){
							((Element)p).element("value").setText(dsJdbc.getPassword());
						}
					}
				}
			}
			else if (dsNew.getExtensionId().equals(IDatasourceType.FM)){
	
				Element e = ((Element)o).element(NODE_PUBLIC_DATASOURCE);
				if(e != null){

					ModelDatasourceJDBC dsJdbc = (ModelDatasourceJDBC)dsNew;
					
					for(Object p : ((Element)e).elements("property")){
						if (((Element)p).element("name").getText().equals(IDatasourceType.FM_USER)){
							((Element)p).element("value").setText(dsJdbc.getFmUser());
						}
						else if (((Element)p).element("name").getText().equals(IDatasourceType.FM_PASSWORD)){
							((Element)p).element("value").setText(dsJdbc.getFmPassword());
						}
					}
				}
			}
			else if (dsNew.getExtensionId().equals(IDatasourceType.LIST_DATA)){
	
				Element e = ((Element)o).element(NODE_PUBLIC_DATASOURCE);
				if(e != null){
					
					ModelDatasourceRepository dsRepo = (ModelDatasourceRepository)dsNew;
					
					for(Object p : ((Element)e).elements("property")){
						if(((Element)p).element("name").getText().equals(IDatasourceType.LIST_DATA_USER)){
							((Element)p).element("value").setText(dsRepo.getUser());
						}
						else if(((Element)p).element("name").getText().equals(IDatasourceType.LIST_DATA_PASSWORD)){
							((Element)p).element("value").setText(dsRepo.getPassword());
						}
						else if(((Element)p).element("name").getText().equals(IDatasourceType.LIST_DATA_RUNTIME_URL)){
							((Element)p).element("value").setText(dsRepo.getVanillaRuntimeUrl());
						}
						else if (((Element)p).element("name").getText().equals(IDatasourceType.LIST_DATA_REPOSITORY_ID)){
							((Element)p).element("value").setText(dsRepo.getRepositoryId());
						}
					}
				}
			}
			else if (dsNew.getExtensionId().equals(IDatasourceType.FUSION_MAP)){
				
				Element e = ((Element)o).element(NODE_PUBLIC_DATASOURCE);
				if(e != null){

					ModelDatasourceRepository dsRepo = (ModelDatasourceRepository)dsNew;
					
					for(Object p : ((Element)e).elements("property")){
						if(((Element)p).element("name").getText().equals(IDatasourceType.MAP_RUNTIME_URL)){
							((Element)p).element("value").setText(dsRepo.getVanillaRuntimeUrl());
						}
					}
				}
			}
		}
	}
}
