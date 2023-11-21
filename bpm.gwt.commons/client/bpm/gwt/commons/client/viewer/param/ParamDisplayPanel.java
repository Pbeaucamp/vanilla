package bpm.gwt.commons.client.viewer.param;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.images.CommonImages;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.client.viewer.IReportViewer;
import bpm.gwt.commons.client.viewer.dialog.BirtSaveConfigDialog;
import bpm.gwt.commons.client.viewer.widget.ICanExpand;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ParamDisplayPanel extends Composite implements ICanExpand {

	private static ParamDisplayPanelUiBinder uiBinder = GWT.create(ParamDisplayPanelUiBinder.class);

	interface ParamDisplayPanelUiBinder extends UiBinder<Widget, ParamDisplayPanel> {
	}

	interface MyStyle extends CssResource {
		String imgExpend();
	}

	@UiField
	HTMLPanel parentPanel;

	@UiField
	SimplePanel mainPanel;

	@UiField
	CaptionPanel captionLimitRows, captionComments;

	@UiField
	TextBox txtLimitRows;

	@UiField
	CheckBox checkLimitRows, checkDisplayComments;

	@UiField
	Button btnRun, btnSaveConfig;

	@UiField
	Image imgExpend;

	@UiField
	MyStyle style;

	private IReportViewer viewer;
	private LaunchReportInformations itemInfo;

	private boolean isExpend = true;

	public ParamDisplayPanel(IReportViewer viewer) {
		initWidget(uiBinder.createAndBindUi(this));
		this.viewer = viewer;

		imgExpend.setResource(CommonImages.INSTANCE.imgCollapse());
		imgExpend.addStyleName(style.imgExpend());

		parentPanel.addStyleName(VanillaCSS.TAB_TOOLBAR);
	}

	public void buildContent(LaunchReportInformations itemInfo) {
		this.itemInfo = itemInfo;

		ParametersPanel paramPanels = new ParametersPanel(itemInfo, false);
		mainPanel.setWidget(paramPanels);

		if (itemInfo.getGroupParameters().isEmpty()) {
			if (isExpend) {
				viewer.expendParam();
				setImageExpendVisible(false);
			}
		}
		else {
			if (!isExpend) {
				viewer.expendParam();
				setImageExpendVisible(false);
			}
		}

		captionLimitRows.setCaptionText(LabelsConstants.lblCnst.PreviewRowLimit());
		if (itemInfo.getLimitRows() != null && itemInfo.getLimitRows() != -1) {
			txtLimitRows.setText(String.valueOf(itemInfo.getLimitRows()));
			txtLimitRows.setEnabled(true);
			checkLimitRows.setValue(true);
		}
		else {
			txtLimitRows.setText("");
			txtLimitRows.setEnabled(false);
			checkLimitRows.setValue(false);
		}
		checkDisplayComments.setValue(itemInfo.displayComments());

		btnSaveConfig.setText(LabelsConstants.lblCnst.SaveReportConfig());
		btnRun.setText(LabelsConstants.lblCnst.RunReport());
	}

	@UiHandler("btnRun")
	public void onRunClick(ClickEvent event) {
		Integer limitRows = -1;
		if (checkLimitRows.getValue()) {
			if (txtLimitRows.getText().isEmpty()) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.IndicateLimitRow());
				return;
			}

			try {
				limitRows = Integer.parseInt(txtLimitRows.getText());
			} catch (Exception e) {
				e.printStackTrace();
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.IndicateValidLimitRow());
				return;
			}
		}

		itemInfo.setLimitRows(limitRows);
		itemInfo.setDisplayComments(checkDisplayComments.getValue());

		viewer.runItem(itemInfo);
	}

	@Override
	public boolean isExpend() {
		return isExpend;
	}

	@Override
	public void setExpend(boolean isExpend) {
		this.isExpend = isExpend;
	}

	public void setImageExpendVisible(boolean visible) {
		this.imgExpend.setVisible(visible);
	}

	@Override
	public void setImgExpendLeft(int progressValue) {
		DOM.setStyleAttribute(imgExpend.getElement(), "left", progressValue + "px");
	}

	@UiHandler("btnSaveConfig")
	public void onBtnConfigSaveClick(ClickEvent event) {
		BirtSaveConfigDialog dial = new BirtSaveConfigDialog(viewer, itemInfo);
		dial.center();
	}

	@UiHandler("imgExpend")
	public void onImgExpendClick(ClickEvent event) {
		if (isExpend) {
			viewer.expendParam();
			imgExpend.setResource(CommonImages.INSTANCE.imgExpend());
		}
		else {
			viewer.expendParam();
			imgExpend.setResource(CommonImages.INSTANCE.imgCollapse());
		}
	}

	@UiHandler("checkLimitRows")
	public void onCheckLimitRowsChanged(ValueChangeEvent<Boolean> event) {
		if (event.getValue()) {
			txtLimitRows.setEnabled(true);
		}
		else {
			txtLimitRows.setEnabled(false);
		}
	}
	
	public void hideReportsElements(){
		captionLimitRows.getElement().getStyle().setDisplay(Display.NONE);
		captionComments.getElement().getStyle().setDisplay(Display.NONE);
		btnSaveConfig.setVisible(false);
	}
}
