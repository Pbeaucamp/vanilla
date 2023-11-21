package bpm.vanillahub.web.client.properties.creation.attribute;

import bpm.gwt.commons.client.custom.CustomCheckbox;
import bpm.gwt.commons.client.custom.TextHolderBox;
import bpm.gwt.commons.client.loading.IWait;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ODSProperties extends Composite {

	private static ODSPropertiesUiBinder uiBinder = GWT.create(ODSPropertiesUiBinder.class);

	interface ODSPropertiesUiBinder extends UiBinder<Widget, ODSProperties> {
	}

	@UiField
	TextHolderBox txtLimit, txtDatasetId, txtParameters;
	
	@UiField
	CustomCheckbox chkCrawlOneDataset;

	public ODSProperties(IWait waitPanel, Integer limit, boolean crawlOneDataset, String datasetId, String parameters) {
		initWidget(uiBinder.createAndBindUi(this));

		txtLimit.setText(limit != null ? String.valueOf(limit) : "");
		chkCrawlOneDataset.setValue(crawlOneDataset);
		txtDatasetId.setText(datasetId != null ? datasetId : "");
		txtParameters.setText(parameters != null ? parameters : "");
		
		updateUi();
	}
	
	@UiHandler("chkCrawlOneDataset")
	public void onCrawlOneDataset(ClickEvent event) {
		updateUi();
	}

	private void updateUi() {
		boolean crawlOneDataset = isCrawlOneDataset();
		txtDatasetId.setEnabled(crawlOneDataset);
		txtParameters.setEnabled(crawlOneDataset);
	}

	public boolean isValid() {
		Integer limit = getLimit();
		boolean crawlOneDataset = isCrawlOneDataset();
		
		String datasetId = getDatasetId();
		try {
			return (limit == null || limit > 0) && (!crawlOneDataset || !datasetId.isEmpty());
		} catch (Exception e) {
			return false;
		}
	}
	
	public Integer getLimit() {
		String limit = txtLimit.getText();
		return limit == null || limit.isEmpty() ? null : Integer.parseInt(limit);
	}
	
	public boolean isCrawlOneDataset() {
		return chkCrawlOneDataset.getValue();
	}
	
	public String getDatasetId() {
		return txtDatasetId.getText();
	}
	
	public String getParameters() {
		return txtParameters.getText();
	}
}
