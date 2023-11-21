package bpm.vanilla.repository.services.parsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

import bpm.vanilla.platform.core.beans.comments.CommentDefinition;
import bpm.vanilla.platform.core.beans.comments.CommentDefinition.TypeComment;
import bpm.vanilla.platform.core.beans.comments.CommentParameter;
import bpm.vanilla.platform.core.repository.Parameter;

public class BIRTParser implements IModelParser {
	protected Document document;

	private String xmlModel;
	private int repId;

	private List<Parameter> parameters = new ArrayList<Parameter>();
	protected List<Integer> dependanciesId = new ArrayList<Integer>();

	private List<CommentDefinition> commentsDef = new ArrayList<CommentDefinition>();

	public BIRTParser(String xmlModel) throws Exception {
		this.xmlModel = xmlModel;
		try {
			document = DocumentHelper.parseText(xmlModel);
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new Exception("Unable to rebuild dom4j document from Fd xml\n" + e.getMessage(), e);
		}

		parseDocument();
		parseDependancies();
	}

	private void parseDocument() {

		/*
		 * parse parameters
		 */
		List<Element> lst = new ArrayList<Element>();
		Element xmlElement = document.getRootElement();
		if (xmlElement.element("parameters") != null) {
			lst.addAll(xmlElement.element("parameters").elements("scalar-parameter"));

			for (Object o : xmlElement.element("parameters").elements("parameter-group")) {
				lst.addAll(((Element) o).element("parameters").elements("scalar-parameter"));
			}

			for (Object o : xmlElement.element("parameters").elements("cascading-parameter-group")) {
				lst.addAll(((Element) o).element("parameters").elements("scalar-parameter"));
			}
		}

		boolean maybecomments = false;

		for (Element e : lst) {
			String s = null;
			if (e.attribute("name") != null) {
				boolean skip = false;
				for (Object o : e.elements("property")) {
					if (((Element) o).attribute("name").getText().equals("hidden")) {
						skip = true;
						maybecomments = true;
						break;
					}
				}

				if (skip) {
					continue;
				}
				s = e.attribute("name").getText();
				Parameter param = new Parameter();
				param.setName(s);
				parameters.add(param);
			}

		}

		if (maybecomments) {
			try {
				XPath xpath = new Dom4jXPath("//birt:extended-item[@extensionName='Comment']");
				HashMap<String, String> nameSpaceMap = new HashMap<String, String>();
				nameSpaceMap.put("birt", xmlElement.getNamespaceURI());
				SimpleNamespaceContext nmsCtx = new SimpleNamespaceContext(nameSpaceMap);
				xpath.setNamespaceContext(nmsCtx);

				List<Node> comments = xpath.selectNodes(document);
				for (Node com : comments) {
					Element elem = (Element) com;

					String name = elem.attribute("name").getText();

					CommentDefinition def = new CommentDefinition();
					
					String label = "";
					boolean showAll = true;
					int nbComment = 0;
					int typeComment = 0;
					List<CommentParameter> parameters = null;

					for (Element e : (List<Element>) elem.elements("property")) {

						if (e.attributeValue("name").equals("Label")) {
							try {
								label = e.getText();
							} catch (Exception e1) {
							}
						}

						if (e.attributeValue("name").equals("Limit")) {
							try {
								showAll = Boolean.parseBoolean(e.getText());
							} catch (Exception e1) {
							}
						}

						if (e.attributeValue("name").equals("NbComment")) {
							try {
								nbComment = Integer.parseInt(e.getText());
							} catch (Exception e1) {
							}
						}

						if (e.attributeValue("name").equals("TypeComment")) {
							try {
								typeComment = Integer.parseInt(e.getText());
							} catch (Exception e1) {
							}
						}

						if (e.attributeValue("name").equals("CommentParameters")) {
							parameters = getCommentParameters(def, e.getText());
						}
					}

					def.setName(name);
					def.setLabel(label);
					def.setType(TypeComment.valueOf(typeComment));
					def.setLimit(showAll);
					def.setNbComments(nbComment);
					def.setParameters(parameters);

					commentsDef.add(def);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	private List<CommentParameter> getCommentParameters(CommentDefinition def, String parametersXml) {
		List<CommentParameter> params = new ArrayList<CommentParameter>();

		try {
			Document doc = DocumentHelper.parseText(parametersXml);
			Element root = doc.getRootElement();
			if (root != null) {
				for (Object obj : root.elements("CommentParam")) {
					Element el = (Element) obj;
					String name = el.element("Name") != null ? el.element("Name").getText() : null;
					String value = el.element("DefaultValue") != null ? el.element("DefaultValue").getText() : null;
					String prompt = el.element("Prompt") != null ? el.element("Prompt").getText() : null;

					if (name != null) {
						CommentParameter param = new CommentParameter();
						param.setParameterIdentifier(name);
						param.setDefaultValue(value);
						param.setPrompt(prompt);
						
						params.add(param);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return params;
	}

	private void parseDependancies() {
		Element ds = document.getRootElement().element("data-sources");

		if (ds != null) {
			for (Object o : ds.elements("oda-data-source")) {
				String extId = ((Element) o).attribute("extensionID").getText();
				if (extId.equals("bpm.metadata.birt.oda.runtime")) {

					for (Object p : ((Element) o).elements("property")) {
						if (((Element) p).attribute("name").getText().equals("DIRECTORY_ITEM_ID")) {
							dependanciesId.add(Integer.parseInt(((Element) p).getText()));
							break;
						}
					}

				}
				else if (extId.equals("bpm.csv.oda.runtime") || extId.equals("bpm.excel.oda.runtime")) {

					for (Object p : ((Element) o).elements("property")) {
						if (((Element) p).attribute("name").getText().equals("repository.item.id")) {
							dependanciesId.add(Integer.parseInt(((Element) p).getText()));
							break;
						}
					}

				}
				else if (((Element) o).attribute("extensionID").getText().equals("bpm.fwr.oda.runtime")) {

					for (Object p : ((Element) o).elements("property")) {
						if (((Element) p).attribute("name").getText().equals("FWREPORT_ID")) {
							dependanciesId.add(Integer.parseInt(((Element) p).getText()));
							break;
						}
					}

				}
			}
		}
	}

	@Override
	public List<Integer> getDependanciesDirectoryItemId() {
		return dependanciesId;
	}

	@Override
	public List<Parameter> getParameters() {
		return parameters;
	}

	@Override
	public String overrideXml(Object object) {
		return xmlModel;
	}

	@Override
	public List<Integer> getDataSourcesReferences() {
		return new ArrayList<Integer>();
	}

	public List<CommentDefinition> getCommentDefinitions(int itemId) {
		if (commentsDef == null) {
			return new ArrayList<CommentDefinition>();
		}

		for (CommentDefinition comDef : commentsDef) {
			comDef.setItemId(itemId);
		}
		return commentsDef;
	}

	public void setRepId(int repId) {
		this.repId = repId;
	}

	public int getRepId() {
		return repId;
	}
}
