package bpm.vanilla.workplace.api.datasource.extractor;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.vanilla.workplace.core.datasource.IDatasource;
import bpm.vanilla.workplace.core.datasource.IDatasourceExtractor;
import bpm.vanilla.workplace.core.datasource.ModelDatasourceRepository;
import bpm.vanilla.workplace.core.datasource.IDatasourceType.DatasourceType;
import bpm.vanilla.workplace.core.disco.DisconnectedBackupConnection;

public class FAVDatasourceExtractor implements IDatasourceExtractor{
	
	@Override
	public List<IDatasource> extractDatasources(String xml) throws Exception {
		List<IDatasource> datasources = new ArrayList<IDatasource>();

		Document document = DocumentHelper.parseText(xml);
		Element root = document.getRootElement();
		
		String cubeName = null;
		String fasdId = null;
		if (root.element("cubename") != null){
			cubeName = root.element("cubename").getText();
		}
		
		if(root.element("fasdid") != null){
			fasdId = root.element("fasdid").getText();
		}
		
		ModelDatasourceRepository ds = new ModelDatasourceRepository();
		ds.setType(DatasourceType.DATASOURCE_REPOSITORY);
		ds.setDirId(fasdId);
		ds.setCubeName(cubeName);
		
		datasources.add(ds);
		return datasources;
	}

	@Override
	public DisconnectedBackupConnection extractBackupConnection(int itemId, String xml) throws Exception {
		return null;
	}
}
