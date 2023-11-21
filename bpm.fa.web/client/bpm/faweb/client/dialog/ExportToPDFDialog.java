package bpm.faweb.client.dialog;

import java.util.List;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.services.FaWebService;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ExportToPDFDialog extends AbstractDialogBox {

	private static ExportToPDFDialogUiBinder uiBinder = GWT.create(ExportToPDFDialogUiBinder.class);

	interface ExportToPDFDialogUiBinder extends UiBinder<Widget, ExportToPDFDialog> {
	}

	@UiField
	TextBox txtTitle;
	@UiField
	TextArea areaDescription;
	@UiField
	RadioButton rbLandscape, rbPortrait;
	@UiField
	ListBox lstPageSizes;
	@UiField
	HTMLPanel contentPanel;

	private boolean isLandscape = false;

	private List<List<String>> cellList;
	private List<String> headerList;
	private String filters;
	private String origin;
	private boolean isPDF;

	public ExportToPDFDialog(boolean isPDF, List<List<String>> cellList, List<String> headerList, String filters, String origin) {
		super("Report Export", true, true);
		this.isPDF = isPDF;
		this.cellList = cellList;
		this.headerList = headerList;
		this.filters = filters;
		this.origin = origin;

		setWidget(uiBinder.createAndBindUi(this));
		if (isPDF) {
			buildListPages();
			rbPortrait.setValue(true);
		}
		else {
			buildXLS();
		}
		setPlaceHolder();
	}

	private void buildXLS() {
		lstPageSizes.removeFromParent();
		rbLandscape.removeFromParent();
		rbPortrait.removeFromParent();
	}

	private void setPlaceHolder() {
		txtTitle.getElement().setAttribute("placeholder", "Report Title");
		areaDescription.getElement().setAttribute("placeholder", "Report Description");
	}

	private void buildListPages() {
		lstPageSizes.addItem("Letter");
		lstPageSizes.addItem("Tabloid");
		lstPageSizes.addItem("Legal");
		lstPageSizes.addItem("A3");
		lstPageSizes.addItem("A4");
		lstPageSizes.addItem("A5");
		lstPageSizes.addItem("B4");
		lstPageSizes.addItem("B5");
		lstPageSizes.addItem("PostCard");
	}

	@UiHandler("rbLandscape")
	void onLandscape(ClickEvent e) {
		isLandscape = true;
	}

	@UiHandler("rbPortrait")
	void onPortrait(ClickEvent e) {
		isLandscape = false;
	}

	@UiHandler("btnCancel")
	void onCancel(ClickEvent e) {
		this.hide();
	}

	@UiHandler("btnExport")
	void onExport(ClickEvent e) {
		if (txtTitle.getText().isEmpty()) {
			Window.alert("Please Enter a Title for your report");
		}
		else {
			if (isPDF) {
				onExportToPDF();
			}
			else {
				onExportToXLS();
			}
		}
	}

	private void onExportToXLS() {
		FaWebService.Connect.getInstance().exportFromDrillThroughXLS(txtTitle.getText(), areaDescription.getText(), origin, filters, headerList, cellList, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(String result) {
				System.out.println(result);
				Window.open(GWT.getHostPageBaseURL() + FreeAnalysisWeb.getRightPath(result), "_blank", "enabled");
				hide();
			}
		});
	}

	private void onExportToPDF() {

		FaWebService.Connect.getInstance().exportFromDrilThroughPDF(isLandscape, lstPageSizes.getSelectedIndex(), txtTitle.getText(), origin, areaDescription.getText(), filters, headerList, cellList, new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {
				System.out.println(result);
				Window.open(GWT.getHostPageBaseURL() + FreeAnalysisWeb.getRightPath(result), "_blank", "enabled");
				hide();
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}
		});
	}
}
