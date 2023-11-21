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

public class FDDatasourceReplacement implements IDatasourceReplacement{
	
	private static final String NODE_PROJECT_DESCRIPTOR = "projectDescriptor";

	private static final String ATTRIBUTE_MODEL_REPOSITORY_ITEM_ID = "modelRepositoryItemId";
	private static final String ATTRIBUTE_DICTIONARY_REPOSITORY_ITEM_ID = "dictionaryRepositoryItemId";

	private static final String NODE_DEPENDANCIES = "dependancies";
	private static final String NODE_DEPENDANCIES_ITEM_ID = "dependantDirectoryItemId";
	
	@Override
	public String replaceElement(String xml, IDatasource dsNew) throws Exception {
		Document document = DocumentHelper.parseText(xml);

		if (document.getRootElement().elements(NODE_PROJECT_DESCRIPTOR) != null){
			for(Object o : document.getRootElement().elements(NODE_PROJECT_DESCRIPTOR)){

				if(dsNew.getName().equals(IDatasourceType.NAME_FD_PROJECT_DESCRIPTOR)){
					ModelDatasourceFD dsDep = (ModelDatasourceFD)dsNew;
					
					if(((Element)o).attribute(ATTRIBUTE_MODEL_REPOSITORY_ITEM_ID) != null){
						((Element)o).attribute(ATTRIBUTE_MODEL_REPOSITORY_ITEM_ID).setText(dsDep.getModelRepositoryItemId());
					}
					
					if(((Element)o).attribute(ATTRIBUTE_DICTIONARY_REPOSITORY_ITEM_ID) != null){
						((Element)o).attribute(ATTRIBUTE_DICTIONARY_REPOSITORY_ITEM_ID).setText(dsDep.getDictionaryRepositoryItemId());
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
