package bpm.fwr.client.dialogs;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import bpm.fwr.api.beans.Constants.Orientation;
import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.draggable.dragcontrollers.DataDragController;
import bpm.fwr.client.draggable.dropcontrollers.VariableDropController;
import bpm.fwr.client.draggable.widgets.DraggableVariableHTML;
import bpm.fwr.client.utils.SizeComponentConstants;
import bpm.fwr.client.utils.VariableTypes;
import bpm.fwr.client.widgets.TextAreaWidget;
import bpm.fwr.client.widgets.TextAreaWidget.TextAreaType;
import bpm.fwr.shared.models.metadata.FwrMetadata;
import bpm.fwr.shared.models.report.wysiwyg.ReportParameters;
import bpm.fwr.shared.models.report.wysiwyg.ReportParameters.ReportParametersType;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SaveOptionsDialogBoxPanel extends Composite implements ICustomPanel {
	// private static final String CSS_LBL_METADATA = "lblMetadataLanguage";
	private static final String CSS_LIST_BOX_METADATA = "listBoxMetadataLanguage";
	private static final String CSS_LABEL_BOTTOM_LEFT = "lblBottomLeft";
	private static final String CSS_TEXT_AREA = "textAreaVariable";
	private static final String CSS_LABEL_AREA = "labelArea";

	private static final String A4 = "A4";

	private static final String PDF = "PDF";
	private static final String HTML = "HTML";
	private static final String EXCEL = "Excel";
	private static final String EXCEL_PLAIN_LIST = "Excel (Plain List)";
	private static final String DOC = "DOC";

	private static SaveOptionsDialogBoxPanelUiBinder uiBinder = GWT.create(SaveOptionsDialogBoxPanelUiBinder.class);

	interface SaveOptionsDialogBoxPanelUiBinder extends UiBinder<Widget, SaveOptionsDialogBoxPanel> {
	}

	@UiField
	Label lblSelectOutputType, lblWidth, lblHeight, lblLeft, lblTop, lblRight, lblBottom, lblHeader, lblVariable;

	@UiField
	TextBox txtWidth, txtHeight, txtLeft, txtTop, txtRight, txtBottom;

	@UiField
	CaptionPanel captionOutput, captionOrientation, captionPageSize, captionMargins, captionMetadataLanguage;

	@UiField
	CaptionPanel captionHeaderFooter;

	@UiField
	ListBox listBoxOutputType, listBoxPageSize;

	@UiField
	RadioButton radioBtnPortrait, radioBtnLandscape, radioBtnStandard, radioBtnCustom;

	@UiField
	HTMLPanel metadataLanguagePanel;

	@UiField
	VerticalPanel panelTextAreas;

	@UiField
	AbsolutePanel dragVariablePanel, listVariable, listPrompt;

	private TextAreaWidget txtAreaFooterRight, txtAreaHeaderLeft, txtAreaHeaderRight, txtAreaFooterLeft;
	private CheckBox checkNbTopLeft, checkNbTopRight, checkNbBottomLeft, checkNbBottomRight;
	private ListBox listMedataLanguage;

	private PickupDragController dragController;

	// private List<ListBox> listBoxLanguages = new ArrayList<ListBox>();
	// private List<FwrMetadata> metadatasAvailableForLanguage = new
	// ArrayList<FwrMetadata>();

	public SaveOptionsDialogBoxPanel(ReportParameters reportParams, List<FwrMetadata> metadatas) {
		initWidget(uiBinder.createAndBindUi(this));

		// Output Type Part
		captionOutput.setCaptionText(Bpm_fwr.LBLW.OutputType());
		lblSelectOutputType.setText(Bpm_fwr.LBLW.SelectOutputType());

		listBoxOutputType.addItem(PDF);
		listBoxOutputType.addItem(HTML);
		listBoxOutputType.addItem(EXCEL);
		listBoxOutputType.addItem(EXCEL_PLAIN_LIST);
		listBoxOutputType.addItem(DOC);

		if (reportParams.getOutputType().equals(PDF)) {
			listBoxOutputType.setSelectedIndex(0);
		}
		else if (reportParams.getOutputType().equals(HTML)) {
			listBoxOutputType.setSelectedIndex(1);
		}
		else if (reportParams.getOutputType().equals(EXCEL)) {
			listBoxOutputType.setSelectedIndex(2);
		}
		else if (reportParams.getOutputType().equals(EXCEL_PLAIN_LIST)) {
			listBoxOutputType.setSelectedIndex(3);
		}
		else {
			listBoxOutputType.setSelectedIndex(4);
		}

		// Orientation Part
		captionOrientation.setCaptionText(Bpm_fwr.LBLW.Orientation());

		radioBtnPortrait.setText(Bpm_fwr.LBLW.Portrait());
		radioBtnLandscape.setText(Bpm_fwr.LBLW.Landscape());

		if (reportParams.getOrientation() == Orientation.PORTRAIT) {
			radioBtnPortrait.setValue(true);
		}
		else {
			radioBtnLandscape.setValue(true);
		}

		// PageSize Part
		captionPageSize.setCaptionText(Bpm_fwr.LBLW.PageSize());

		radioBtnStandard.setText(Bpm_fwr.LBLW.Standard());
		radioBtnStandard.addClickHandler(btnClickHandler);
		listBoxPageSize.addItem("A4");
		// listBoxPageSize.addItem("Legal");
		// listBoxPageSize.addItem("Letter");

		radioBtnCustom.setText(Bpm_fwr.LBLW.Custom());
		radioBtnCustom.addClickHandler(btnClickHandler);
		lblWidth.setText(Bpm_fwr.LBLW.Width());
		lblHeight.setText(Bpm_fwr.LBLW.Height());

		if (reportParams.getWidth() == SizeComponentConstants.WIDTH_REPORT && reportParams.getHeight() == SizeComponentConstants.HEIGHT_REPORT) {
			radioBtnStandard.setValue(true);
			radioBtnCustom.setValue(false);
		}
		else {
			radioBtnCustom.setValue(true);
			radioBtnStandard.setValue(false);
			txtWidth.setText(reportParams.getWidth() + "");
			txtHeight.setText(reportParams.getHeight() + "");
		}

		// Margins Part
		captionMargins.setCaptionText(Bpm_fwr.LBLW.Margin());

		lblLeft.setText(Bpm_fwr.LBLW.MarginLeft());
		txtLeft.setText(reportParams.getMarginLeft());

		lblTop.setText(Bpm_fwr.LBLW.MarginTop());
		txtTop.setText(reportParams.getMarginTop());

		lblRight.setText(Bpm_fwr.LBLW.MarginRight());
		txtRight.setText(reportParams.getMarginRight());

		lblBottom.setText(Bpm_fwr.LBLW.MarginBottom());
		txtBottom.setText(reportParams.getMarginBottom());

		if (metadatas != null) {

			// boolean hideCaption = true;

			HashMap<String, String> locales = new HashMap<String, String>();
			for (FwrMetadata metadata : metadatas) {
				if (metadata.getLocales() != null) {
					for (Entry<String, String> locale : metadata.getLocales().entrySet()) {
						if (locales.get(locale.getKey()) == null) {
							locales.put(locale.getKey(), locale.getValue());
						}
					}
				}
			}

			if (locales.isEmpty()) {
				captionMetadataLanguage.setVisible(false);
			}
			else {
				captionMetadataLanguage.setCaptionText(Bpm_fwr.LBLW.MetadataLanguage());

				// Label lblMetadata = new Label(metadata.getName());
				// lblMetadata.addStyleName(CSS_LBL_METADATA);

				listMedataLanguage = new ListBox();
				listMedataLanguage.addStyleName(CSS_LIST_BOX_METADATA);
				int i = 0;
				int index = -1;
				for (Entry<String, String> locale : locales.entrySet()) {
					if (reportParams.getSelectedLanguage() != null && reportParams.getSelectedLanguage().equals(locale.getKey())) {
						index = i;
					}
					listMedataLanguage.addItem(locale.getValue(), locale.getKey());
					i++;
				}
				if (index != -1) {
					listMedataLanguage.setSelectedIndex(index);
				}
				// listBoxLanguages.add(listMedataLanguage);
				// metadatasAvailableForLanguage.add(metadata);

				// HorizontalPanel metadataLPanel = new HorizontalPanel();
				// metadataLPanel.add(lblMetadata);
				// metadataLPanel.add(listMedataLanguage);

				metadataLanguagePanel.add(listMedataLanguage);
			}
		}
		else {
			captionMetadataLanguage.setVisible(false);
		}

		// Right Part
		captionHeaderFooter.setCaptionText(Bpm_fwr.LBLW.CaptionHeaderFooter());
		lblHeader.setText(Bpm_fwr.LBLW.LabelHeader());

		Label lblHeaderLeft = new Label(Bpm_fwr.LBLW.LabelHeaderLeft());
		lblHeaderLeft.addStyleName(CSS_LABEL_AREA);
		txtAreaHeaderLeft = new TextAreaWidget(TextAreaType.TOP_LEFT);
		txtAreaHeaderLeft.addStyleName(CSS_TEXT_AREA);
		checkNbTopLeft = new CheckBox(Bpm_fwr.LBLW.DisplayNumberPage());

		Label lblHeaderRight = new Label(Bpm_fwr.LBLW.LabelHeaderRight());
		lblHeaderRight.addStyleName(CSS_LABEL_AREA);
		txtAreaHeaderRight = new TextAreaWidget(TextAreaType.TOP_RIGHT);
		txtAreaHeaderRight.addStyleName(CSS_TEXT_AREA);
		checkNbTopRight = new CheckBox(Bpm_fwr.LBLW.DisplayNumberPage());

		Label lblFooterLeft = new Label(Bpm_fwr.LBLW.LabelFooterLeft());
		lblFooterLeft.addStyleName(CSS_LABEL_BOTTOM_LEFT);
		txtAreaFooterLeft = new TextAreaWidget(TextAreaType.BOTTOM_LEFT);
		txtAreaFooterLeft.addStyleName(CSS_TEXT_AREA);
		checkNbBottomLeft = new CheckBox(Bpm_fwr.LBLW.DisplayNumberPage());

		Label lblFooterRight = new Label(Bpm_fwr.LBLW.LabelFooterRight());
		lblFooterRight.addStyleName(CSS_LABEL_AREA);
		txtAreaFooterRight = new TextAreaWidget(TextAreaType.BOTTOM_RIGHT);
		txtAreaFooterRight.addStyleName(CSS_TEXT_AREA);
		checkNbBottomRight = new CheckBox(Bpm_fwr.LBLW.DisplayNumberPage());

		panelTextAreas.add(lblHeaderLeft);
		panelTextAreas.add(txtAreaHeaderLeft);
		panelTextAreas.add(checkNbTopLeft);
		panelTextAreas.add(lblHeaderRight);
		panelTextAreas.add(txtAreaHeaderRight);
		panelTextAreas.add(checkNbTopRight);
		panelTextAreas.add(lblFooterLeft);
		panelTextAreas.add(txtAreaFooterLeft);
		panelTextAreas.add(checkNbBottomLeft);
		panelTextAreas.add(lblFooterRight);
		panelTextAreas.add(txtAreaFooterRight);
		panelTextAreas.add(checkNbBottomRight);

		txtAreaHeaderLeft.setText(reportParams.getTopLeft());
		txtAreaHeaderRight.setText(reportParams.getTopRight());
		txtAreaFooterLeft.setText(reportParams.getBottomLeft());
		txtAreaFooterRight.setText(reportParams.getBottomRight());

		checkNbTopLeft.setValue(reportParams.isNbPageTopLeft());
		checkNbTopRight.setValue(reportParams.isNbPageTopRight());
		checkNbBottomLeft.setValue(reportParams.isNbPageBottomLeft());
		checkNbBottomRight.setValue(reportParams.isNbPageBottomRight());

		lblVariable.setText(Bpm_fwr.LBLW.ReportVariables());

		DraggableVariableHTML userGroup = new DraggableVariableHTML(Bpm_fwr.LBLW.UserGroup(), this, VariableTypes.USER_GROUP);
		DraggableVariableHTML date = new DraggableVariableHTML(Bpm_fwr.LBLW.Date(), this, VariableTypes.DATE);

		listVariable.add(userGroup);
		listVariable.add(date);

		dragController = new DataDragController(dragVariablePanel, false);
		dragController.makeDraggable(userGroup);
		dragController.makeDraggable(date);

		DropController dropControllerTopLeft = new VariableDropController(txtAreaHeaderLeft);
		DropController dropControllerTopRight = new VariableDropController(txtAreaHeaderRight);
		DropController dropControllerBottomLeft = new VariableDropController(txtAreaFooterLeft);
		DropController dropControllerBottomRight = new VariableDropController(txtAreaFooterRight);

		dragController.registerDropController(dropControllerTopLeft);
		dragController.registerDropController(dropControllerTopRight);
		dragController.registerDropController(dropControllerBottomLeft);
		dragController.registerDropController(dropControllerBottomRight);

		updatePageSizePart();
	}

	@Override
	protected void onDetach() {
		dragController.unregisterDropControllers();
		super.onDetach();
	}

	private void updatePageSizePart() {
		if (radioBtnStandard.getValue()) {
			listBoxPageSize.setEnabled(true);

			txtWidth.setEnabled(false);
			txtHeight.setEnabled(false);
		}
		else {
			listBoxPageSize.setEnabled(false);

			txtWidth.setEnabled(true);
			txtHeight.setEnabled(true);
		}
	}

	private ClickHandler btnClickHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			if (event.getSource().equals(radioBtnStandard) || event.getSource().equals(radioBtnCustom)) {
				updatePageSizePart();
			}
		}
	};

	public ReportParameters getReportParameters() {
		int top = 0;
		int left = 0;
		int right = 0;
		int bottom = 0;

		try {
			top = Integer.parseInt(txtTop.getText());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			left = Integer.parseInt(txtLeft.getText());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			right = Integer.parseInt(txtRight.getText());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			bottom = Integer.parseInt(txtBottom.getText());
		} catch (Exception e) {
			e.printStackTrace();
		}

		int width = 0;
		int height = 0;

		ReportParameters parameters = new ReportParameters(ReportParametersType.NEW);
		if (radioBtnStandard.getValue()) {
			if (listBoxPageSize.getValue(listBoxPageSize.getSelectedIndex()).equals(A4)) {
				width = SizeComponentConstants.WIDTH_REPORT;
				height = SizeComponentConstants.HEIGHT_REPORT;
				
				parameters.setPageSize(A4);
			}
		}
		else {
			try {
				width = Integer.parseInt(txtWidth.getText());
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				height = Integer.parseInt(txtHeight.getText());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			parameters.setPageSize(height + "]" + width);
		}

		parameters.setOutputType(listBoxOutputType.getValue(listBoxOutputType.getSelectedIndex()));
		parameters.setMargins(top, left, right, bottom);
		parameters.setWidth(width);
		parameters.setHeight(height);
		if (radioBtnPortrait.getValue()) {
			parameters.setOrientation(Orientation.PORTRAIT);
		}
		else {
			parameters.setOrientation(Orientation.LANDSCAPE);
		}

		if (listMedataLanguage != null) {
			String value = listMedataLanguage.getValue(listMedataLanguage.getSelectedIndex());
			parameters.setSelectedLanguage(value);
		}

		// if(metadatasAvailableForLanguage != null &&
		// !listBoxLanguages.isEmpty()){
		// for(int i = 0; i < metadatasAvailableForLanguage.size(); i++){
		// String value =
		// listBoxLanguages.get(i).getValue(listBoxLanguages.get(i).getSelectedIndex());
		// parameters.addMetadataLanguage(metadatasAvailableForLanguage.get(i).getId(),
		// value);
		// }
		// }

		String topLeft = txtAreaHeaderLeft.getText().toString();
		String topRight = txtAreaHeaderRight.getText().toString();
		String bottomLeft = txtAreaFooterLeft.getText().toString();
		String bottomRight = txtAreaFooterRight.getText().toString();

		parameters.setTopLeft(topLeft);
		parameters.setTopRight(topRight);
		parameters.setBottomLeft(bottomLeft);
		parameters.setBottomRight(bottomRight);

		parameters.setNbPageTopLeft(checkNbTopLeft.getValue());
		parameters.setNbPageTopRight(checkNbTopRight.getValue());
		parameters.setNbPageBottomLeft(checkNbBottomLeft.getValue());
		parameters.setNbPageBottomRight(checkNbBottomRight.getValue());

		return parameters;
	}

	@Override
	public boolean isComplete() {
		return true;
	}

	// public void setListBoxLanguages(List<ListBox> listBoxLanguages) {
	// this.listBoxLanguages = listBoxLanguages;
	// }
	//
	// public List<ListBox> getListBoxLanguages() {
	// return listBoxLanguages;
	// }
}
