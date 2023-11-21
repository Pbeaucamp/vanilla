package bpm.vanillahub.web.client.properties.parameters;

import java.util.ArrayList;
import java.util.List;

import bpm.vanillahub.core.beans.activities.attributes.Quandl;
import bpm.vanillahub.core.beans.activities.attributes.QuandlParameter;
import bpm.vanillahub.web.client.properties.creation.attribute.QuandlProperties;
import bpm.vanillahub.web.client.tabs.resources.ResourceManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class QuandlParametersPanel extends Composite {

	private static QuandlParametersPanelUiBinder uiBinder = GWT.create(QuandlParametersPanelUiBinder.class);

	interface QuandlParametersPanelUiBinder extends UiBinder<Widget, QuandlParametersPanel> {
	}

	interface MyStyle extends CssResource {
		String panelParam();

		String txt();

		String checkbox();

		String lblHelp();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel mainPanel;

	@UiField
	Image btnAdd;

	private QuandlProperties panelParent;
	
	private ResourceManager resourceManager;
	private List<QuandlParameterWidget> widgetParameters = new ArrayList<QuandlParameterWidget>();

	public QuandlParametersPanel(QuandlProperties panelParent, ResourceManager resourceManager, Quandl quandl) {
		initWidget(uiBinder.createAndBindUi(this));
		this.panelParent = panelParent;
		this.resourceManager = resourceManager;

		if (quandl.getParameters() != null) {
			createParameters(quandl.getParameters());
		}
	}

	private void createParameters(List<QuandlParameter> parameters) {
		for (QuandlParameter param : parameters) {
			QuandlParameterWidget widgetParam = new QuandlParameterWidget(this, resourceManager, param);
			widgetParameters.add(widgetParam);

			mainPanel.add(widgetParam);
		}
	}

	public List<QuandlParameter> getParameters() {
		List<QuandlParameter> params = new ArrayList<QuandlParameter>();
		if (widgetParameters != null) {
			for (QuandlParameterWidget widg : widgetParameters) {
				QuandlParameter param = widg.getParameter();
				params.add(param);
			}
		}
		return params;
	}

	@UiHandler("btnAdd")
	public void onAddClick(ClickEvent event) {
		QuandlParameterWidget widgetParam = new QuandlParameterWidget(this, resourceManager, new QuandlParameter());
		widgetParameters.add(widgetParam);

		mainPanel.add(widgetParam);
	}
	
	public void removeParameter(QuandlParameterWidget paramWidget) {
		widgetParameters.remove(paramWidget);
		mainPanel.remove(paramWidget);
		
		refreshGeneratedUrl();
	}

	public void refreshGeneratedUrl() {
		panelParent.refreshGeneratedUrl();	
	}
}
