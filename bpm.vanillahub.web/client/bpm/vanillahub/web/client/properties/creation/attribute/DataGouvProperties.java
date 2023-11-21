package bpm.vanillahub.web.client.properties.creation.attribute;

import java.util.HashMap;

import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.vanillahub.core.beans.activities.TypeOpenData;
import bpm.vanillahub.core.beans.activities.attributes.DataGouv;
import bpm.vanillahub.core.beans.activities.attributes.DataGouvDataset;
import bpm.vanillahub.core.beans.activities.attributes.DataGouvResource;
import bpm.vanillahub.core.beans.activities.attributes.DataGouvSummary;
import bpm.vanillahub.web.client.dialogs.BuildDataGouvDialog;
import bpm.vanillahub.web.client.services.ResourcesService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class DataGouvProperties extends Composite {

	private static DataGouvPropertiesUiBinder uiBinder = GWT.create(DataGouvPropertiesUiBinder.class);

	interface DataGouvPropertiesUiBinder extends UiBinder<Widget, DataGouvProperties> {
	}

	@UiField
	ListBox lstDatasets, lstResources;

	@UiField
	TextArea txtDatasetDescription, txtResourceDescription;

	@UiField
	FocusPanel btnNext, btnPrevious;

	@UiField
	Label lblPageNumber;

	private IWait waitPanel;
	private DataGouv dataGouv;

	private DataGouvSummary summary;
	private int pageSize;
	private int nbPage;
	private int pageNumber = 1;

	private String openDataUrl;

	public DataGouvProperties(IWait waitPanel, DataGouv dataGouv, String openDataUrl) {
		initWidget(uiBinder.createAndBindUi(this));
		this.waitPanel = waitPanel;

		if (dataGouv != null && openDataUrl != null && !openDataUrl.isEmpty()) {
			refresh(openDataUrl, dataGouv, true);
		}
		else {
			refreshUi(null);
		}
	}

	public boolean isValid() {
		DataGouv dataGouv = getDataGouv();
		return dataGouv.isValid();
	}

	public void refresh(String openDataUrl, DataGouv dataGouv, boolean initPageNumber) {
		if (initPageNumber) {
			pageNumber = 1;
		}

		this.openDataUrl = openDataUrl;
		this.dataGouv = dataGouv;
		this.pageSize = extractPageSize(openDataUrl);
		String urlWithPage = getUrlWithPage();

		waitPanel.showWaitPart(true);
		ResourcesService.Connect.getInstance().getOpenDataDatasets(TypeOpenData.DATA_GOUV, urlWithPage, new GwtCallbackWrapper<DataGouvSummary>(waitPanel, true) {

			@Override
			public void onSuccess(DataGouvSummary result) {
				refreshUi(result);
			}
		}.getAsyncCallback());
	}

	private int extractPageSize(String openDataUrl) {
		try {
			HashMap<String, String> props = ToolsGWT.parseUrl(openDataUrl);
			return props.get("page_size") != null ? Integer.parseInt(props.get("page_size")) : BuildDataGouvDialog.PAGE_SIZE_DEFAULT;
		} catch (Exception e) {
			e.printStackTrace();
			return BuildDataGouvDialog.PAGE_SIZE_DEFAULT;
		}
	}

	private void refreshUi(DataGouvSummary result) {
		this.summary = result;
		this.nbPage = getNbPage();

		lstDatasets.clear();
		if (result != null && result.getDatasets() != null && !result.getDatasets().isEmpty()) {
			int selectedIndex = 0;
			for (int i = 0; i < result.getDatasets().size(); i++) {
				DataGouvDataset ds = result.getDatasets().get(i);
				lstDatasets.addItem(ds.getTitle(), ds.getId());

				if (dataGouv != null && dataGouv.getDatasetId() != null && ds.getId().equals(dataGouv.getDatasetId())) {
					selectedIndex = i;
				}
			}
			lstDatasets.setSelectedIndex(selectedIndex);

			NativeEvent event = Document.get().createChangeEvent();
			ChangeEvent.fireNativeEvent(event, lstDatasets);
		}

		if (summary == null) {
			btnNext.setVisible(false);
			lblPageNumber.setText("0/0");
			btnPrevious.setVisible(false);
		}
		else {
			btnNext.setVisible(true);
			btnPrevious.setVisible(true);
			if (pageNumber == 1) {
				btnPrevious.setVisible(false);
			}

			if (pageNumber >= nbPage) {
				btnNext.setVisible(false);
			}
			lblPageNumber.setText((pageNumber) + "/" + nbPage);
		}
	}

	private DataGouvDataset findSelectedDataset() {
		String datasetId = lstDatasets.getValue(lstDatasets.getSelectedIndex());
		if (datasetId != null && !datasetId.isEmpty()) {
			for (DataGouvDataset ds : summary.getDatasets()) {
				if (ds.getId().equals(datasetId)) {
					return ds;
				}
			}
		}

		return null;
	}

	private DataGouvResource findSelectedResource() {
		DataGouvDataset ds = findSelectedDataset();

		String resourceId = lstResources.getValue(lstResources.getSelectedIndex());
		if (ds != null && resourceId != null && !resourceId.isEmpty()) {
			for (DataGouvResource res : ds.getResources()) {
				if (res.getId().equals(resourceId)) {
					return res;
				}
			}
		}

		return null;

	}

	@UiHandler("btnPrevious")
	public void onPreviousClick(ClickEvent event) {
		if (pageNumber > 1) {
			pageNumber--;
			refresh(openDataUrl, dataGouv, false);
		}
	}

	@UiHandler("btnNext")
	public void onNextClick(ClickEvent event) {
		pageNumber++;
		refresh(openDataUrl, dataGouv, false);
	}

	@UiHandler("lstDatasets")
	public void onDatasetChange(ChangeEvent event) {
		DataGouvDataset ds = findSelectedDataset();
		txtDatasetDescription.setText(ds.getHelp());

		lstResources.clear();
		if (ds.getResources() != null && !ds.getResources().isEmpty()) {
			int selectedIndex = 0;
			for (int i = 0; i < ds.getResources().size(); i++) {
				DataGouvResource res = ds.getResources().get(i);
				lstResources.addItem(res.toString(), res.getId());

				if (dataGouv != null && dataGouv.getResourceId() != null && res.getId().equals(dataGouv.getResourceId())) {
					selectedIndex = i;
				}
			}
			lstResources.setSelectedIndex(selectedIndex);

			NativeEvent eventChange = Document.get().createChangeEvent();
			ChangeEvent.fireNativeEvent(eventChange, lstResources);
		}
	}

	@UiHandler("lstResources")
	public void onResourceChange(ChangeEvent event) {
		DataGouvResource res = findSelectedResource();
		txtResourceDescription.setText(res.getHelp());
	}

	public DataGouv getDataGouv() {
		DataGouvDataset ds = findSelectedDataset();
		DataGouvResource res = findSelectedResource();

		if (dataGouv == null) {
			dataGouv = new DataGouv();
		}
		dataGouv.setDatasetId(ds != null ? ds.getId() : null);
		dataGouv.setResourceId(res != null ? res.getId() : null);

		return dataGouv;
	}

	public String getUrlWithPage() {
		return openDataUrl + "&page=" + pageNumber;
	}

	private int getNbPage() {
		return summary != null && pageSize > 0 ? (summary.getNbResult() / pageSize + (summary.getNbResult() % pageSize > 0 ? 1 : 0)) : 0;
	}
}
