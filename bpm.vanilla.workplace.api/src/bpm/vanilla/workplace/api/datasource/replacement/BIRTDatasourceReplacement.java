package bpm.vanilla.workplace.api.datasource.replacement;

import java.io.ByteArrayOutputStream;

import org.apache.commons.codec.binary.Base64;
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

public class BIRTDatasourceReplacement implements IDatasourceReplacement{

	private static final String NODE_DATASOURCES = "data-sources";
	private static final String NODE_DATASOURCE = "oda-data-source";
	
	@Override
	public String replaceElement(String xml, IDatasource newDs) throws Exception {
		Document document = DocumentHelper.parseText(xml);
		Element ds = document.getRootElement().element(NODE_DATASOURCES);
		
		if (ds != null){
			for(Object o : ds.elements(NODE_DATASOURCE)){

				String datasourceName = ((Element)o).attributeValue("name");
				if(newDs.getName().equals(datasourceName)){
					if (newDs.getExtensionId().equals(IDatasourceType.METADATA) 
							|| newDs.getExtensionId().equals(IDatasourceType.METADATA_OLAP)){
					
						ModelDatasourceRepository dsRepo = (ModelDatasourceRepository)newDs;
						for(Object p : ((Element)o).elements("property")){
							if(((Element)p).attribute("name").getText().equals(IDatasourceType.METADATA_USER)){
								((Element)p).setText(dsRepo.getUser());
							}
							else if(((Element)p).attribute("name").getText().equals(IDatasourceType.METADATA_PASSWORD)){
								((Element)p).setText(dsRepo.getPassword());
							}
							else if(((Element)p).attribute("name").getText().equals(IDatasourceType.METADATA_URL)){
								((Element)p).setText(dsRepo.getRepositoryUrl());
							}
							else if (((Element)p).attribute("name").getText().equals(IDatasourceType.METADATA_DIRECTORY_ITEM_ID)){
								((Element)p).setText(dsRepo.getDirId());
							}
							else if (((Element)p).attribute("name").getText().equals(IDatasourceType.GROUP_NAME)){
								((Element)p).setText(dsRepo.getGroupName());
							}
							else if (((Element)p).attribute("name").getText().equals(IDatasourceType.METADATA_REPOSITORY_ID)){
								((Element)p).setText(dsRepo.getRepositoryId());
							}
							else if (((Element)p).attribute("name").getText().equals(IDatasourceType.METADATA_VANILLA_URL)){
								((Element)p).setText(dsRepo.getVanillaRuntimeUrl());
							}
						}
					}
					else if (newDs.getExtensionId().equals(IDatasourceType.CSV_ODA) 
							|| newDs.getExtensionId().equals(IDatasourceType.EXCEL_ODA)){

						ModelDatasourceRepository dsRepo = (ModelDatasourceRepository)newDs;

						for(Object p : ((Element)o).elements("list-property")){
							String priv = ((Element)p).attribute("name").getText();
							if (priv.equals("privateDriverProperties")){
								for(Object e : ((Element)p).elements("ex-property")){
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
					}
					else if (newDs.getExtensionId().equals(IDatasourceType.FWR_ODA)){

						ModelDatasourceRepository dsRepo = (ModelDatasourceRepository)newDs;
						for(Object p : ((Element)o).elements("property")){
							if (((Element)p).attribute("name").getText().equals(IDatasourceType.FWR_USER)){
								((Element)p).setText(dsRepo.getUser());
							}
							else if (((Element)p).attribute("name").getText().equals(IDatasourceType.FWR_PASSWORD)){
								((Element)p).setText(dsRepo.getPassword());
							}
							else if (((Element)p).attribute("name").getText().equals(IDatasourceType.FWR_URL)){
								((Element)p).setText(dsRepo.getVanillaRuntimeUrl());
							}
							else if (((Element)p).attribute("name").getText().equals(IDatasourceType.FWR_REPOSITORY_URL)){
								((Element)p).setText(dsRepo.getRepositoryUrl());
							}
							else if (((Element)p).attribute("name").getText().equals(IDatasourceType.FWR_GROUP_ID)){
								((Element)p).setText(dsRepo.getGroupId());
							}
							else if (((Element)p).attribute("name").getText().equals(IDatasourceType.FWR_GROUP_NAME)){
								((Element)p).setText(dsRepo.getGroupName());
							}
						}
						
						for(Object p : ((Element)o).elements("list-property")){
							String priv = ((Element)p).attribute("name").getText();
							if (priv.equals("privateDriverProperties")){
								for(Object e : ((Element)p).elements("ex-property")){
									if (((Element)e).element("name").getText().equals(IDatasourceType.FWR_REPORT_ID)){
										((Element)e).element("value").setText(dsRepo.getDirId());
									}
								}
							}
						}
					}
					else if (newDs.getExtensionId().equals(IDatasourceType.JDBC)){
	
						ModelDatasourceJDBC dsJdbc = (ModelDatasourceJDBC)newDs;
						for(Object p : ((Element)o).elements("property")){
							if (((Element)p).attribute("name").getText().equals(IDatasourceType.ODA_DRIVER)){
								((Element)p).setText(dsJdbc.getDriverClass());
							}
							else if (((Element)p).attribute("name").getText().equals(IDatasourceType.ODA_URL)){
								((Element)p).setText(dsJdbc.getFullUrl());
							}
							else if (((Element)p).attribute("name").getText().equals(IDatasourceType.ODA_USER)){
								((Element)p).setText(dsJdbc.getUser());
							}
						}
						

						
						for(Object p : ((Element)o).elements("encrypted-property")){
							if (((Element)p).attribute("name").getText().equals(IDatasourceType.ODA_PASSWORD)){
								String encryption = ((Element)p).attributeValue("encryptionID");
								if (encryption.equalsIgnoreCase("base64")){
									((Element)p).setText(new String(Base64.decodeBase64(dsJdbc.getPassword().getBytes())));
								}
								else{
									((Element)p).setText(dsJdbc.getPassword());
								}
							}
						}
					}
					else if (newDs.getExtensionId().equals(IDatasourceType.FM)){

						ModelDatasourceJDBC dsJdbc = (ModelDatasourceJDBC)newDs;
						
						for(Object p : ((Element)o).elements("property")){
							if (((Element)p).attribute("name").getText().equals(IDatasourceType.FM_USER)){
								((Element)p).setText(dsJdbc.getFmUser());
							}
						}
						
						for(Object p : ((Element)o).elements("encrypted-property")){
							if (((Element)p).attribute("name").getText().equals(IDatasourceType.FM_PASSWORD)){
								String encryption = ((Element)p).attributeValue("encryptionID");
								if (encryption.equalsIgnoreCase("base64")){
									((Element)p).setText(new String(Base64.decodeBase64(dsJdbc.getFmPassword().getBytes())));
								}
								else{
									((Element)p).setText(dsJdbc.getFmPassword());
								}
							}
						}
					}
					else if (newDs.getExtensionId().equals(IDatasourceType.LIST_DATA)){
						
						ModelDatasourceRepository dsRepo = (ModelDatasourceRepository)newDs;
						
						for(Object p : ((Element)o).elements("property")){
							if (((Element)p).attribute("name").getText().equals(IDatasourceType.LIST_DATA_USER)){
								((Element)p).setText(dsRepo.getUser());
							}
							else if (((Element)p).attribute("name").getText().equals(IDatasourceType.LIST_DATA_RUNTIME_URL)){
								((Element)p).setText(dsRepo.getVanillaRuntimeUrl());
							}
							else if (((Element)p).attribute("name").getText().equals(IDatasourceType.LIST_DATA_REPOSITORY_ID)){
								((Element)p).setText(dsRepo.getRepositoryId());
							}
						}
						
						for(Object p : ((Element)o).elements("encrypted-property")){
							if (((Element)p).attribute("name").getText().equals(IDatasourceType.LIST_DATA_PASSWORD)){
								String encryption = ((Element)p).attributeValue("encryptionID");
								if (encryption.equalsIgnoreCase("base64")){
									((Element)p).setText(new String(Base64.decodeBase64(dsRepo.getPassword().getBytes())));
								}
								else{
									((Element)p).setText(dsRepo.getPassword());
								}
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
//		return document.asXML();
	}
}
