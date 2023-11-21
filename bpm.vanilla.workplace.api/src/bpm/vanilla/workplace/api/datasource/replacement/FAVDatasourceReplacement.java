package bpm.vanilla.workplace.api.datasource.replacement;

import java.io.ByteArrayOutputStream;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import bpm.vanilla.workplace.core.datasource.IDatasource;
import bpm.vanilla.workplace.core.datasource.IDatasourceReplacement;
import bpm.vanilla.workplace.core.datasource.ModelDatasourceRepository;

public class FAVDatasourceReplacement implements IDatasourceReplacement{
	
	@Override
	public String replaceElement(String xml, IDatasource dsNew) throws Exception {
		Document document = DocumentHelper.parseText(xml);
		Element root = document.getRootElement();
		
		if(root != null) {
			ModelDatasourceRepository dsRepo = (ModelDatasourceRepository)dsNew;
			
			Element cubeNameElement = root.element("cubename");
			if(cubeNameElement != null) {
				cubeNameElement.setText(dsRepo.getCubeName());
			}
			
			Element fasdIdElement = root.element("fasdid");
			if(fasdIdElement != null) {
				fasdIdElement.setText(dsRepo.getDirId());
			}

			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			OutputFormat form = OutputFormat.createPrettyPrint();
			form.setTrimText(false);
			XMLWriter writer = new XMLWriter(bos, form);
			writer.write(root);
			writer.close();
			
			String result = bos.toString("UTF-8"); 
			
			return result;
		}
		
		throw new Exception("The FAV seems to be an old version. Please contact an administrator to import this item.");
	}

}
