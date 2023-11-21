package bpm.vanilla.workplace.api.datasource.extractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.vanilla.workplace.core.datasource.IDatasource;
import bpm.vanilla.workplace.core.datasource.IDatasourceExtractor;
import bpm.vanilla.workplace.core.datasource.IDatasourceType;
import bpm.vanilla.workplace.core.datasource.ModelDatasourceFD;
import bpm.vanilla.workplace.core.datasource.IDatasourceType.DatasourceType;
import bpm.vanilla.workplace.core.disco.DisconnectedBackupConnection;

public class FDDatasourceExtractor implements IDatasourceExtractor{
	
	private static final String NODE_PROJECT_DESCRIPTOR = "projectDescriptor";
	
	private static final String ATTRIBUTE_MODEL_REPOSITORY_ITEM_ID = "modelRepositoryItemId";
	private static final String ATTRIBUTE_DICTIONARY_REPOSITORY_ITEM_ID = "dictionaryRepositoryItemId";
	
	private static final String NODE_DEPENDANCIES = "dependancies";
	private static final String NODE_DEPENDANCIES_ITEM_ID = "dependantDirectoryItemId";
	
	@Override
	public List<IDatasource> extractDatasources(String xml) throws Exception {
		List<IDatasource> datasources = new ArrayList<IDatasource>();

		Document document = DocumentHelper.parseText(xml);

		if (document.getRootElement().elements(NODE_PROJECT_DESCRIPTOR) != null){
			for(Object o : document.getRootElement().elements(NODE_PROJECT_DESCRIPTOR)){
				String datasourceName = IDatasourceType.NAME_FD_PROJECT_DESCRIPTOR;
				String modelRepositoryItemId = ((Element)o).attributeValue(ATTRIBUTE_MODEL_REPOSITORY_ITEM_ID);
				String dictionaryRepositoryItemId = ((Element)o).attributeValue(ATTRIBUTE_DICTIONARY_REPOSITORY_ITEM_ID);
				
				ModelDatasourceFD datasource = new ModelDatasourceFD();
				datasource.setName(datasourceName);
				datasource.setType(DatasourceType.DATASOURCE_REPOSITORY);
				datasource.setModelRepositoryItemId(modelRepositoryItemId);
				datasource.setDictionaryRepositoryItemId(dictionaryRepositoryItemId);
				
				datasources.add(datasource);
			}
		}
		
		if (document.getRootElement().elements(NODE_DEPENDANCIES) != null){
			for(Object o : document.getRootElement().elements(NODE_DEPENDANCIES)){
				
				HashMap<Integer, Integer> dependancies = new HashMap<Integer, Integer>();
				for(Object p : ((Element)o).elements(NODE_DEPENDANCIES_ITEM_ID)){
					
					int depId = Integer.parseInt(((Element)p).getText());
					
					dependancies.put(depId, depId);
				}

				ModelDatasourceFD ds = new ModelDatasourceFD();
				ds.setName(IDatasourceType.NAME_FDDICO_DEPENDANCIES);
				ds.setType(DatasourceType.DATASOURCE_REPOSITORY);
				ds.setDependancies(dependancies);
				
				datasources.add(ds);
			}	
		}
		
		return datasources;
	}

	@Override
	public DisconnectedBackupConnection extractBackupConnection(int itemId, String xml) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
