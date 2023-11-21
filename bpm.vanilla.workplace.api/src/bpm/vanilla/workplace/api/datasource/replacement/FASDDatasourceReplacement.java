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

public class FASDDatasourceReplacement implements IDatasourceReplacement{
	
	private static final String NODE_DATASOURCES = "datasources";
	private static final String NODE_DATASOURCE_ODA = "datasource-oda";
	
	private static final String NODE_PUBLIC_PROPS = "public-properties";
	private static final String NODE_PRIVATE_PROPS = "private-properties";
	
	@Override
	public String replaceElement(String xml, IDatasource dsNew) throws Exception {
		Document document = DocumentHelper.parseText(xml);
		Element ds = document.getRootElement().element(NODE_DATASOURCES);
		
		if (ds != null){
			for(Object o : ds.elements(NODE_DATASOURCE_ODA)){
				
				if(dsNew.getName().equals(((Element)o).element("name").getText())){
					if (dsNew.getExtensionId().equals(IDatasourceType.METADATA) 
							|| dsNew.getExtensionId().equals(IDatasourceType.METADATA_OLAP)){

						ModelDatasourceRepository dsRepo = (ModelDatasourceRepository)dsNew;
						for(Object p : ((Element)o).element(NODE_PUBLIC_PROPS).elements("property")){
							if(((Element)p).element("name").getText().equals(IDatasourceType.METADATA_USER)){
								((Element)p).element("value").setText(dsRepo.getUser());
							}
							else if(((Element)p).element("name").getText().equals(IDatasourceType.METADATA_PASSWORD)){
								((Element)p).element("value").setText(dsRepo.getPassword());
							}
							else if(((Element)p).element("name").getText().equals(IDatasourceType.METADATA_URL)){
								((Element)p).element("value").setText(dsRepo.getRepositoryUrl());
							}
							else if (((Element)p).element("name").getText().equals(IDatasourceType.METADATA_DIRECTORY_ITEM_ID)){
								((Element)p).element("value").setText(dsRepo.getDirId());
							}
							else if (((Element)p).element("name").getText().equals(IDatasourceType.METADATA_REPOSITORY_ID)){
								((Element)p).element("value").setText(dsRepo.getRepositoryId());
							}
							else if (((Element)p).element("name").getText().equals(IDatasourceType.GROUP_NAME)){
								((Element)p).element("value").setText(dsRepo.getGroupName());
							}
						}
					}
					else if (dsNew.getExtensionId().equals(IDatasourceType.CSV_ODA) 
							|| dsNew.getExtensionId().equals(IDatasourceType.EXCEL_ODA)){

						ModelDatasourceRepository dsRepo = (ModelDatasourceRepository)dsNew;
						for(Object p : ((Element)o).element(NODE_PRIVATE_PROPS).elements("property")){
							if (((Element)p).element("name").getText().equals(IDatasourceType.CSV_REPOSITORY_ITEM_ID)){
								((Element)p).element("value").setText(dsRepo.getDirId());
							}
							else if (((Element)p).element("name").getText().equals(IDatasourceType.CSV_REPOSITORY_ID)){
								((Element)p).element("value").setText(dsRepo.getRepositoryId());
							}
							else if (((Element)p).element("name").getText().equals(IDatasourceType.CSV_GROUP_ID)){
								((Element)p).element("value").setText(dsRepo.getGroupId());
							}
							else if (((Element)p).element("name").getText().equals(IDatasourceType.CSV_USER)){
								((Element)p).element("value").setText(dsRepo.getUser());
							}
							else if (((Element)p).element("name").getText().equals(IDatasourceType.CSV_PASSWORD)){
								((Element)p).element("value").setText(dsRepo.getPassword());
							}
						}
					}
					else if (dsNew.getExtensionId().equals(IDatasourceType.FWR_ODA)){
	
						ModelDatasourceRepository dsRepo = (ModelDatasourceRepository)dsNew;
						for(Object p : ((Element)o).element(NODE_PUBLIC_PROPS).elements("property")){
							if(((Element)p).element("name").getText().equals(IDatasourceType.FWR_USER)){
								((Element)p).element("value").setText(dsRepo.getUser());
							}
							else if(((Element)p).element("name").getText().equals(IDatasourceType.FWR_PASSWORD)){
								((Element)p).element("value").setText(dsRepo.getPassword());
							}
							else if(((Element)p).element("name").getText().equals(IDatasourceType.FWR_URL)){
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
	
						for(Object p : ((Element)o).element(NODE_PRIVATE_PROPS).elements("property")){
							if (((Element)p).element("name").getText().equals(IDatasourceType.FWR_REPORT_ID)){
								((Element)p).element("value").setText(dsRepo.getDirId());
							}
						}
					}
					else if (dsNew.getExtensionId().equals(IDatasourceType.JDBC)){
	
						ModelDatasourceJDBC dsJdbc = (ModelDatasourceJDBC)dsNew;
						for(Object p : ((Element)o).element(NODE_PUBLIC_PROPS).elements("property")){
							if(((Element)p).element("name").getText().equals(IDatasourceType.ODA_DRIVER)){
								((Element)p).element("value").setText(dsJdbc.getDriverClass());
							}
							else if(((Element)p).element("name").getText().equals(IDatasourceType.ODA_URL)){
								((Element)p).element("value").setText(dsJdbc.getFullUrl());
							}
							else if(((Element)p).element("name").getText().equals(IDatasourceType.ODA_USER)){
								((Element)p).element("value").setText(dsJdbc.getUser());
							}
							else if (((Element)p).element("name").getText().equals(IDatasourceType.ODA_PASSWORD)){
								((Element)p).element("value").setText(dsJdbc.getPassword());
							}
						}
					}
					else if (dsNew.getExtensionId().equals(IDatasourceType.LIST_DATA)){
						
						ModelDatasourceRepository dsRepo = (ModelDatasourceRepository)dsNew;
						
						for(Object p : ((Element)o).element(NODE_PUBLIC_PROPS).elements("property")){
							if(((Element)p).element("name").getText().equals(IDatasourceType.LIST_DATA_USER)){
								((Element)p).element("value").setText(dsRepo.getUser());
							}
							else if(((Element)p).element("name").getText().equals(IDatasourceType.LIST_DATA_PASSWORD)){
								((Element)p).element("value").setText(dsRepo.getPassword());
							}
							else if(((Element)p).element("name").getText().equals(IDatasourceType.LIST_DATA_REPOSITORY_ID)){
								((Element)p).element("value").setText(dsRepo.getRepositoryId());
							}
							else if (((Element)p).element("name").getText().equals(IDatasourceType.LIST_DATA_RUNTIME_URL)){
								((Element)p).element("value").setText(dsRepo.getVanillaRuntimeUrl());
							}
						}
					}
					break;
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
