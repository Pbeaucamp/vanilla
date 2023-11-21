package bpm.fwr.api.birt;

import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.ElementFactory;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.TextItemHandle;

import bpm.fwr.api.beans.components.TextHTMLComponent;

public class TextHTMLComponentCreator implements IComponentCreator<TextHTMLComponent> {

	@Override
	public DesignElementHandle createComponent(ReportDesignHandle designHandle, ElementFactory designFactory, String selectedLanguage, TextHTMLComponent component, String outputFormat) throws Exception {
		TextItemHandle labelText = designFactory.newTextItem("Text_" + new Object().hashCode());
		labelText.setContentType("html");
		labelText.setContent(component.getTextContent());
		return labelText;
	}

}
