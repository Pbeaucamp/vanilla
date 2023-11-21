package bpm.vanilla.repository.services.parsers;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import bpm.vanilla.platform.core.repository.Parameter;

public class FAVParser extends AbstractGeneralParser {

	public FAVParser(String xmlModel) throws Exception {
		super(xmlModel);
	}

	@Override
	protected void parseDependancies() throws Exception {
		Element root = document.getRootElement();
		if (!root.getName().equals("fav")) {
			root = root.element("fav");
		}
		if (root.element("fasdid") != null) {
			try {
				Integer i = Integer.parseInt(root.element("fasdid").getStringValue());
				dependanciesId.add(i);
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new Exception("Error when parsing dependancies of the CubeView.\n" + ex.getMessage(), ex);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * bpm.birepository.services.parsers.AbstractGeneralParser#parseParameters()
	 */
	@Override
	protected void parseParameters() throws Exception {
		Element root = document.getRootElement();

		if (!root.getName().equals("fav")) {
			root = root.element("fav");
		}
		try {
			for (Element e : (Collection<Element>) root.element("view").element("parameters").elements("parameter")) {
				String pName = e.element("name").getStringValue();

				Parameter p = new Parameter();
				p.setInstanceName(pName);
				p.setName(pName);

				parameters.add(p);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("Error when parsing parameters of the CubeView.\n" + ex.getMessage(), ex);
		}

	}

	@Override
	public List<Integer> getDataSourcesReferences() {
		return new ArrayList<Integer>();
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

}
