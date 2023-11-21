package bpm.map.viewer.web.client.dialogs;

import java.util.List;

import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.dialog.ged.IndexFormDialog;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.gwt.commons.shared.repository.DocumentDefinitionDTO;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.map.viewer.web.client.UserSession;
import bpm.map.viewer.web.client.I18N.LabelsConstants;
import bpm.map.viewer.web.client.panel.viewer.MapViewer;
import bpm.map.viewer.web.client.services.MapViewerService;
import bpm.vanilla.platform.core.beans.Group;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ExportMapDial extends AbstractDialogBox {
	private static ExportMapDialUiBinder uiBinder = GWT
			.create(ExportMapDialUiBinder.class);

	interface ExportMapDialUiBinder extends
			UiBinder<Widget, ExportMapDial> {
	}

	interface MyStyle extends CssResource {

	}

	@UiField
	TextBox txtName, txtAuthor;
	
	@UiField
	TextArea txtDescriptif;
	
	@UiField
	Label lblName, lblAuthor, lblDescriptif, lblType, lblVersion;

	@UiField
	ListBox lstType, lstVersion;
	
	@UiField
	CheckBox chkGED/*, chkExport*/;
	
	@UiField
	MyStyle style;

	private MapViewer parent;
	private List<Group> availableGroups;
	private List<DocumentDefinitionDTO> allDocuments;
	private DocumentDefinitionDTO selectedDocument = null;

	public static LabelsConstants lblCnst = (LabelsConstants) GWT
			.create(LabelsConstants.class);

	public ExportMapDial(MapViewer parent) {
		super(lblCnst.ExportMap(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		
		createButtonBar(lblCnst.Ok(), okHandler, lblCnst.Cancel(), cancelHandler);
		
		initUI();
		availableGroups = UserSession.getInstance().getGroups();
		
	}
	
	public void initUI(){
		this.lblName.setText(lblCnst.MapTitle());
		this.lblAuthor.setText(lblCnst.Author());
		this.lblDescriptif.setText(lblCnst.Description());
		
		lblType.setText(bpm.gwt.commons.client.I18N.LabelsConstants.lblCnst.Format());
		lstType.addItem(CommonConstants.FORMAT_PDF_NAME);
		lstType.addItem(CommonConstants.FORMAT_PNG_NAME);
		
		chkGED.setText(lblCnst.AddMapToGed());
		onGEDChange(null);
//		chkExport.setText(bpm.gwt.commons.client.I18N.LabelsConstants.lblCnst.DownloadFile());
		
		updateBtn();
		
		lblVersion.setText(bpm.gwt.commons.client.I18N.LabelsConstants.lblCnst.NewVersionOf());
		lstVersion.addItem(bpm.gwt.commons.client.I18N.LabelsConstants.lblCnst.NewDocument());
		CommonService.Connect.getInstance().getAllGEDDocuments(new GwtCallbackWrapper<List<DocumentDefinitionDTO>>(parent.getViewer(), true) {
					@Override
					public void onSuccess(List<DocumentDefinitionDTO> result) {
						allDocuments = result;
						for(DocumentDefinitionDTO doc : result){
							lstVersion.addItem(doc.getName());
						}
					}
		}.getAsyncCallback());
	}
	
	public String getName() {
		return txtName.getText();
	}

	public void setName(String name) {
		txtName.setText(name);
	}
	
	public String getAuthor() {
		return txtAuthor.getText();
	}

	public void setAuthor(String author) {
		txtAuthor.setText(author);
	}
	
	public String getDescriptif() {
		return txtDescriptif.getText();
	}

	public void setDescriptif(String descriptif) {
		txtDescriptif.setText(descriptif);
	}
	
	@UiHandler("txtName")
	public void onNameChange(ValueChangeEvent<String> event) {
		
	}
	
	@UiHandler("txtAuthor")
	public void onAuthorChange(ValueChangeEvent<String> event) {
		
	}
	
	@UiHandler("txtDescriptif")
	public void onDescriptifChange(ValueChangeEvent<String> event) {
		
	}
	
	@UiHandler("lstType")
	public void onTypeChange(ChangeEvent event) {
		if(lstType.getValue(lstType.getSelectedIndex()).equals(CommonConstants.FORMAT_PDF_NAME)){
			txtAuthor.setVisible(true);
			txtDescriptif.setVisible(true);
//			txtName.setVisible(true);
			lblAuthor.setVisible(true);
			lblDescriptif.setVisible(true);
//			lblName.setVisible(true);
		} else {
			txtAuthor.setVisible(false);
			txtDescriptif.setVisible(false);
//			txtName.setVisible(false);
			lblAuthor.setVisible(false);
			lblDescriptif.setVisible(false);
//			lblName.setVisible(false);
		}
	}
	
	@UiHandler("chkGED")
	public void onGEDChange(ClickEvent event) {
		if(chkGED.getValue()){
			lblVersion.setVisible(true);
			lstVersion.setVisible(true);
		} else {
			lblVersion.setVisible(false);
			lstVersion.setVisible(false);
		}
	}
	
	@UiHandler("lstVersion")
	public void onVersionChange(ChangeEvent event) {
		if(lstVersion.getSelectedIndex() == 0){
			selectedDocument = null;
		} else {
			selectedDocument = allDocuments.get(lstVersion.getSelectedIndex()-1);
		}
	}
	
	public void updateBtn() {
		
	}
	
	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			String img = parent.getMapContent().getMapPrint();
			if(lstType.getValue(lstType.getSelectedIndex()).equals(CommonConstants.FORMAT_PDF_NAME)){
				MapViewerService.Connect.getInstance().exportToPdf(txtName.getText(), txtDescriptif.getText(), txtAuthor.getText(), img, CommonConstants.FORMAT_PDF, 
						new GwtCallbackWrapper<String>(parent.getViewer(), true) {
							@Override
							public void onSuccess(String result) {
								String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.PREVIEW_STREAM_SERVLET + "?" + CommonConstants.STREAM_HASHMAP_NAME + "=" + result + "&" + CommonConstants.STREAM_HASHMAP_FORMAT + "=" + CommonConstants.FORMAT_PDF;
								ToolsGWT.doRedirect(fullUrl);
								
								if(chkGED.getValue()){
									final IndexFormDialog dial = new IndexFormDialog(selectedDocument, availableGroups, result, CommonConstants.FORMAT_PDF, txtName.getText(), txtDescriptif.getText());
									dial.center();
									dial.addCloseHandler(new CloseHandler<PopupPanel>() {
										@Override
										public void onClose(CloseEvent<PopupPanel> event) {
											if(dial.getIndexState() == 1){
												MessageHelper.openMessageDialog(bpm.gwt.commons.client.I18N.LabelsConstants.lblCnst.Success(), bpm.gwt.commons.client.I18N.LabelsConstants.lblCnst.SuccessIndex());
												cancelHandler.onClick(null);
											} else if(dial.getIndexState() == 0){
												MessageHelper.openMessageDialog(bpm.gwt.commons.client.I18N.LabelsConstants.lblCnst.Error(), dial.getErrorMsg());
												cancelHandler.onClick(null);
											}
											
										}
									});
								}
							}
				}.getAsyncCallback());
			} else {
				MapViewerService.Connect.getInstance().exportToImage(txtName.getText(), img, CommonConstants.FORMAT_PNG, 
						new GwtCallbackWrapper<String>(parent.getViewer(), true) {
							@Override
							public void onSuccess(String result) {
								String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.PREVIEW_STREAM_SERVLET + "?" + CommonConstants.STREAM_HASHMAP_NAME + "=" + result + "&" + CommonConstants.STREAM_HASHMAP_FORMAT + "=" + CommonConstants.FORMAT_PNG;
								ToolsGWT.doRedirect(fullUrl);
								
								if(chkGED.getValue()){
									final IndexFormDialog dial = new IndexFormDialog(selectedDocument, availableGroups, result, CommonConstants.FORMAT_PNG,  txtName.getText(), txtDescriptif.getText());
									dial.center();
									dial.addCloseHandler(new CloseHandler<PopupPanel>() {
										@Override
										public void onClose(CloseEvent<PopupPanel> event) {
											if(dial.getIndexState() == 1){
												MessageHelper.openMessageDialog(bpm.gwt.commons.client.I18N.LabelsConstants.lblCnst.Success(), bpm.gwt.commons.client.I18N.LabelsConstants.lblCnst.SuccessIndex());
												cancelHandler.onClick(null);
											} else if(dial.getIndexState() == 0){
												MessageHelper.openMessageDialog(bpm.gwt.commons.client.I18N.LabelsConstants.lblCnst.Error(), dial.getErrorMsg());
												cancelHandler.onClick(null);
											}
										}
									});
								}
							}
				}.getAsyncCallback());
			}
			
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {	
		@Override
		public void onClick(ClickEvent event) {
			ExportMapDial.this.hide();
		}
	};
}
