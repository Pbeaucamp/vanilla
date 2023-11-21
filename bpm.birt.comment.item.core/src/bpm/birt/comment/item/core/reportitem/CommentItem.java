package bpm.birt.comment.item.core.reportitem;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.extension.ReportItem;

public class CommentItem extends ReportItem {

	public static final String EXTENSION_NAME = "Comment";

	public static final String PROPERTY_LABEL = "Label";
	public static final String PROPERTY_LIMIT = "Limit";
	public static final String PROPERTY_NB_COMMENT = "NbComment";

	public static final String PROPERTY_TYPE = "TypeComment";
	public static final String PROPERTY_PARAMETERS = "CommentParameters";

	private ExtendedItemHandle itemHandle;
	private String id;

	public CommentItem(ExtendedItemHandle itemHandle) {
		this.itemHandle = itemHandle;
		if (itemHandle.getName() == null || !itemHandle.getName().contains("CommentItem_")) {
			this.id = "CommentItem_" + new Object().hashCode();
			try {
				itemHandle.setName(id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public ExtendedItemHandle getItemHandle() {
		return itemHandle;
	}

	public void setItemHandle(ExtendedItemHandle itemHandle) {
		this.itemHandle = itemHandle;
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		return itemHandle.getStringProperty(PROPERTY_LABEL);
	}

	public void setLabel(String label) throws SemanticException {
		itemHandle.setStringProperty(PROPERTY_LABEL, label);
	}

	public boolean getLimit() {
		return itemHandle.getBooleanProperty(PROPERTY_LIMIT);
	}

	public void setLimit(boolean limit) throws SemanticException {
		itemHandle.setBooleanProperty(PROPERTY_LIMIT, limit);
	}

	public int getNbComment() {
		return itemHandle.getIntProperty(PROPERTY_NB_COMMENT);
	}

	public void setNbComment(int nbComment) throws SemanticException {
		itemHandle.setIntProperty(PROPERTY_NB_COMMENT, nbComment);
	}

	public int getTypeComment() {
		return itemHandle.getIntProperty(PROPERTY_TYPE);
	}

	public void setTypeComment(int typeComment) throws SemanticException {
		itemHandle.setIntProperty(PROPERTY_TYPE, typeComment);
	}

	public String getParametersXml() {
		return itemHandle.getStringProperty(PROPERTY_PARAMETERS);
	}

	public void setParametersXml(String parametersXml) throws SemanticException {
		itemHandle.setStringProperty(PROPERTY_PARAMETERS, parametersXml);
	}

	public List<CommentParameter> getParametersList() {
		return parseParametersXml(getParametersXml());
	}

	public void setParametersList(List<CommentParameter> parameters) throws SemanticException {
		setParametersXml(buildParametersXml(parameters));
	}

	private static final String COMMENT_PARAMS = "CommentParams";
	private static final String COMMENT_PARAM = "CommentParam";

	private String buildParametersXml(List<CommentParameter> parameters) {
		StringBuffer buf = new StringBuffer("<" + COMMENT_PARAMS + ">");
		if (parameters != null) {
			for (CommentParameter param : parameters) {
				buf.append("    <" + COMMENT_PARAM + ">");
				buf.append("        <Name>" + param.getName() + "</Name>");
				buf.append("        <DefaultValue>" + param.getDefaultValue() + "</DefaultValue>");
				buf.append("        <Prompt>" + param.getPrompt() + "</Prompt>");
				buf.append("    </" + COMMENT_PARAM + ">");
			}
		}
		buf.append("</" + COMMENT_PARAMS + ">");
		return buf.toString();
	}

	private List<CommentParameter> parseParametersXml(String parametersXml) {
		List<CommentParameter> params = new ArrayList<CommentParameter>();

		try {
			if (parametersXml != null) {
				Document doc = DocumentHelper.parseText(parametersXml);
				Element root = doc.getRootElement();
				if (root != null) {
					for (Object obj : root.elements(COMMENT_PARAM)) {
						Element el = (Element) obj;
						String name = el.element("Name") != null ? el.element("Name").getText() : null;
						String value = el.element("DefaultValue") != null ? el.element("DefaultValue").getText() : null;
						String prompt = el.element("Prompt") != null ? el.element("Prompt").getText() : null;

						if (name != null) {
							params.add(new CommentParameter(name, value, prompt));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return params;
	}
}
