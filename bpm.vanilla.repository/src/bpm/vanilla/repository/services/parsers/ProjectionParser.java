package bpm.vanilla.repository.services.parsers;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class ProjectionParser extends AbstractGeneralParser{

	public ProjectionParser(String xmlModel) throws Exception {
		super(xmlModel);
	}

	@Override
	public String overrideXml(Object object) throws Exception {
		Element xml = document.getRootElement();
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		
		OutputFormat form = OutputFormat.createPrettyPrint();
		form.setTrimText(false);
		XMLWriter writer = new XMLWriter(bos, form);
		writer.write(xml);
		writer.close();
		
		String result = bos.toString("UTF-8"); 
		
		return result;
	}

	@Override
	public void parseDependancies() throws Exception {
		Element root  = document.getRootElement();
		
		if (root.element("fasd") != null){
			try{
				Integer i = Integer.parseInt(root.element("fasd").getStringValue());
				dependanciesId.add(i);
			}catch(Exception ex){
				ex.printStackTrace();
				throw new Exception("Error when parsing dependancies of the Projection.\n" + ex.getMessage(), ex);
			}
			
		}
	}

	@Override
	public void parseParameters() throws Exception {
		
	}

	@Override
	public List<Integer> getDataSourcesReferences() {
		return new ArrayList<Integer>();
	}

}
