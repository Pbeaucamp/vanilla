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
import bpm.vanilla.workplace.core.datasource.ModelDatasourceFD;
import bpm.vanilla.workplace.core.datasource.ModelDatasourceJDBC;
import bpm.vanilla.workplace.core.datasource.ModelDatasourceRepository;

public class FDDicoDatasourceReplacement implements IDatasourceReplacement{
	
	private static final String NODE_DATASOURCE = "dataSource";

	private static final String NODE_MAP = "map";
	private static final String NODE_MAP_INFO = "mapInfo";
	private static final String NODE_VANILLA_RUNTIME_URL = "vanillaRuntimeUrl";
	
	private static final String NODE_OLAP_VIEW = "olapView";
	private static final String NODE_DIRECTORY_ITEM_ID = "directoryItemId";
	
	private static final String NODE_DEPENDANCIES = "dependancies";
	private static final String NODE_DEPENDANCIES_ITEM_ID = "dependantDirectoryItemId";

	private static final String NODE_PUBLIC_PROPS = "publicProperty";
	private static final String NODE_PRIVATE_PROPS = "privateProperty";
	
	@Override
	public String replaceElement(String xml, IDatasource dsNew) throws Exception {
		Document document = DocumentHelper.parseText(xml);

		if (document.getRootElement().elements(NODE_DATASOURCE) != null){
			for(Object o : document.getRootElement().elements(NODE_DATASOURCE)){
				String datasourceName = ((Element)o).element("name").getText();
				
				if(dsNew.getName().equals(datasourceName)){
					if (dsNew.getExtensionId().equals(IDatasourceType.METADATA) 
							|| dsNew.getExtensionId().equals(IDatasourceType.METADATA_OLAP)){
	
						ModelDatasourceRepository dsRepo = (ModelDatasourceRepository)dsNew;
						
						for(Object p : ((Element)o).elements(NODE_PUBLIC_PROPS)){
							if(((Element)p).attributeValue("name").equals(IDatasourceType.METADATA_USER)){
								((Element)p).setText(dsRepo.getUser());
							}
							else if(((Element)p).attributeValue("name").equals(IDatasourceType.METADATA_PASSWORD)){
								((Element)p).setText(dsRepo.getPassword());
							}
							else if(((Element)p).attributeValue("name").equals(IDatasourceType.METADATA_VANILLA_URL)){
								((Element)p).setText(dsRepo.getVanillaRuntimeUrl());
							}
							else if (((Element)p).attributeValue("name").equals(IDatasourceType.METADATA_DIRECTORY_ITEM_ID)){
								((Element)p).setText(dsRepo.getDirId());
							}
							else if (((Element)p).attributeValue("name").equals(IDatasourceType.METADATA_REPOSITORY_ID)){
								((Element)p).setText(dsRepo.getRepositoryId());
							}
							else if (((Element)p).attributeValue("name").equals(IDatasourceType.GROUP_NAME)){
								((Element)p).setText(dsRepo.getGroupName());
							}
						}
					}
					else if (dsNew.getExtensionId().equals(IDatasourceType.CSV_ODA) 
							|| dsNew.getExtensionId().equals(IDatasourceType.EXCEL_ODA)){

						ModelDatasourceRepository dsRepo = (ModelDatasourceRepository)dsNew;
						
						for(Object p : ((Element)o).elements(NODE_PRIVATE_PROPS)){
							if (((Element)p).attributeValue("name").equals(IDatasourceType.CSV_REPOSITORY_ITEM_ID)){
								((Element)p).setText(dsRepo.getDirId());
							}
							else if (((Element)p).attributeValue("name").equals(IDatasourceType.CSV_REPOSITORY_ID)){
								((Element)p).setText(dsRepo.getRepositoryId());
							}
							else if (((Element)p).attributeValue("name").equals(IDatasourceType.CSV_GROUP_ID)){
								((Element)p).setText(dsRepo.getGroupId());
							}
							else if (((Element)p).attributeValue("name").equals(IDatasourceType.CSV_USER)){
								((Element)p).setText(dsRepo.getUser());
							}
							else if (((Element)p).attributeValue("name").equals(IDatasourceType.CSV_PASSWORD)){
								((Element)p).setText(dsRepo.getPassword());
							}
						}
					}
					else if (dsNew.getExtensionId().equals(IDatasourceType.FWR_ODA)){

						ModelDatasourceRepository dsRepo = (ModelDatasourceRepository)dsNew;

						for(Object p : ((Element)o).elements(NODE_PUBLIC_PROPS)){
							if(((Element)p).attributeValue("name").equals(IDatasourceType.FWR_USER)){
								((Element)p).setText(dsRepo.getUser());
							}
							else if(((Element)p).attributeValue("name").equals(IDatasourceType.FWR_PASSWORD)){
								((Element)p).setText(dsRepo.getPassword());
							}
							else if(((Element)p).attributeValue("name").equals(IDatasourceType.FWR_URL)){
								((Element)p).setText(dsRepo.getVanillaRuntimeUrl());
							}
							else if (((Element)p).attributeValue("name").equals(IDatasourceType.FWR_REPOSITORY_URL)){
								((Element)p).setText(dsRepo.getRepositoryUrl());
							}
							else if (((Element)p).attributeValue("name").equals(IDatasourceType.FWR_GROUP_ID)){
								((Element)p).setText(dsRepo.getGroupId());
							}
							else if (((Element)p).attributeValue("name").equals(IDatasourceType.FWR_GROUP_NAME)){
								((Element)p).setText(dsRepo.getGroupName());
							}
						}
						
						for(Object p : ((Element)o).elements(NODE_PRIVATE_PROPS)){
							if (((Element)p).attributeValue("name").equals(IDatasourceType.FWR_REPORT_ID)){
								((Element)p).setText(dsRepo.getDirId());
							}
						}
					}
					else if (dsNew.getExtensionId().equals(IDatasourceType.JDBC)){
	
						ModelDatasourceJDBC dsJdbc = (ModelDatasourceJDBC)dsNew;
						
						for(Object p : ((Element)o).elements(NODE_PUBLIC_PROPS)){
							if(((Element)p).attributeValue("name").equals(IDatasourceType.ODA_DRIVER)){
								((Element)p).setText(dsJdbc.getDriverClass());
							}
							else if(((Element)p).attributeValue("name").equals(IDatasourceType.ODA_URL)){
								((Element)p).setText(dsJdbc.getFullUrl());
							}
							else if(((Element)p).attributeValue("name").equals(IDatasourceType.ODA_USER)){
								((Element)p).setText(dsJdbc.getUser());
							}
							else if (((Element)p).attributeValue("name").equals(IDatasourceType.ODA_PASSWORD)){
								((Element)p).setText(dsJdbc.getPassword());
							}
						}
					}
					else if (dsNew.getExtensionId().equals(IDatasourceType.LIST_DATA)){

						ModelDatasourceRepository dsRepo = (ModelDatasourceRepository)dsNew;
						
						for(Object p : ((Element)o).elements(NODE_PUBLIC_PROPS)){
							if(((Element)p).attributeValue("name").equals(IDatasourceType.LIST_DATA_USER)){
								((Element)p).setText(dsRepo.getUser());
							}
							else if(((Element)p).attributeValue("name").equals(IDatasourceType.LIST_DATA_PASSWORD)){
								((Element)p).setText(dsRepo.getPassword());
							}
							else if(((Element)p).attributeValue("name").equals(IDatasourceType.LIST_DATA_RUNTIME_URL)){
								((Element)p).setText(dsRepo.getVanillaRuntimeUrl());
							}
							else if (((Element)p).attributeValue("name").equals(IDatasourceType.LIST_DATA_REPOSITORY_ID)){
								((Element)p).setText(dsRepo.getRepositoryId());
							}
						}
					}
					else if (dsNew.getExtensionId().equals(IDatasourceType.FUSION_MAP)){

						ModelDatasourceRepository dsRepo = (ModelDatasourceRepository)dsNew;
						
						for(Object p : ((Element)o).elements(NODE_PUBLIC_PROPS)){
							if(((Element)p).attributeValue("name").equals(IDatasourceType.MAP_RUNTIME_URL)){
								((Element)p).setText(dsRepo.getVanillaRuntimeUrl());
							}
						}
					}
				}
			}
		}
		
		if (document.getRootElement().elements(NODE_MAP) != null){
			for(Object o : document.getRootElement().elements(NODE_MAP)){
				
				if(dsNew.getName().equals(((Element)o).attributeValue("name"))){
					
					ModelDatasourceRepository dsRepo = (ModelDatasourceRepository)dsNew;
					
					if(((Element)o).element(NODE_MAP_INFO) != null){
						((Element)o).element(NODE_MAP_INFO).attribute(NODE_VANILLA_RUNTIME_URL).setText(dsRepo.getVanillaRuntimeUrl());
					}
				}
			}	
		}
		
		if (document.getRootElement().elements(NODE_OLAP_VIEW) != null){
			for(Object o : document.getRootElement().elements(NODE_OLAP_VIEW)){
				
				if(dsNew.getName().equals(((Element)o).attributeValue("name"))){

					ModelDatasourceRepository dsRepo = (ModelDatasourceRepository)dsNew;
					if(((Element)o).element(NODE_DIRECTORY_ITEM_ID) != null){
						((Element)o).element(NODE_DIRECTORY_ITEM_ID).setText(dsRepo.getDirId());
					}
				}
			}	
		}
		
		if (document.getRootElement().elements(NODE_DEPENDANCIES) != null){
			for(Object o : document.getRootElement().elements(NODE_DEPENDANCIES)){
				
				if(dsNew.getName().equals(IDatasourceType.NAME_FDDICO_DEPENDANCIES)){
					
					ModelDatasourceFD dsDep = (ModelDatasourceFD)dsNew;
					
					
					for(Object p : ((Element)o).elements(NODE_DEPENDANCIES_ITEM_ID)){
						int id = Integer.parseInt(((Element)p).getText());
						
						if(dsDep.getDependancies().get(id) != null){
							((Element)p).setText(String.valueOf(dsDep.getDependancies().get(id)));
						}
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
