package bpm.vanilla.workplace.api.datasource.extractor;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class FDModelExtractor {
	
	private static final String NODE_DOCUMENT_PROPERTIES = "document-properties";
	private static final String ATTRIBUTE_NAME = "name";
	
	private static final String NODE_PROJECT_DESCRIPTOR = "projectDescriptor";
	private static final String ATTRIBUTE_MODEL_NAME = "modelName";
	
	public static boolean isMainModel(String xml) throws Exception {
		Document document = DocumentHelper.parseText(xml);

		String modelName = "";
		if (document.getRootElement().elements(NODE_PROJECT_DESCRIPTOR) != null){
			for(Object o : document.getRootElement().elements(NODE_PROJECT_DESCRIPTOR)){
				modelName = ((Element)o).element(ATTRIBUTE_MODEL_NAME).getText();
			}
		}
		
		String name = "";
		if (document.getRootElement().elements(NODE_DOCUMENT_PROPERTIES) != null){
			for(Object o : document.getRootElement().elements(NODE_DOCUMENT_PROPERTIES)){
				name = ((Element)o).element(ATTRIBUTE_NAME).getText();
			}	
		}
		
		if(modelName != null && name != null && !modelName.isEmpty() && !name.isEmpty()){
			return modelName.equals(name);
		}
		else {
			return true;
		}
	}

}
