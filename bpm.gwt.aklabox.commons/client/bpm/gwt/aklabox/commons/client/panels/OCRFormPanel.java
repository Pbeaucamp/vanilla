package bpm.gwt.aklabox.commons.client.panels;

import java.util.ArrayList;
import java.util.List;

import bpm.aklabox.workflow.core.IAklaflowConstant;
import bpm.aklabox.workflow.core.model.resources.AklaBoxServer;
import bpm.aklabox.workflow.core.model.resources.FormCell;
import bpm.aklabox.workflow.core.model.resources.IResource;
import bpm.aklabox.workflow.core.model.resources.StandardForm;
import bpm.document.management.core.model.Form;
import bpm.document.management.core.model.User;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.dialogs.OCRModelViewer;
import bpm.gwt.aklabox.commons.client.services.AklaCommonService;
import bpm.gwt.aklabox.commons.client.utils.AklaTextArea;
import bpm.gwt.aklabox.commons.client.utils.AklaTextBox;
import bpm.gwt.aklabox.commons.client.utils.CellTableItem;
import bpm.gwt.aklabox.commons.client.utils.ChildDialogComposite;
import bpm.gwt.aklabox.commons.client.utils.DefaultDialog;
import bpm.gwt.aklabox.commons.client.utils.DefaultResultDialog;
import bpm.gwt.aklabox.commons.client.utils.MessageUtil;
import bpm.gwt.aklabox.commons.client.utils.WaitDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class OCRFormPanel extends ChildDialogComposite {

	private static OCRFormPanelUiBinder uiBinder = GWT.create(OCRFormPanelUiBinder.class);

	interface OCRFormPanelUiBinder extends UiBinder<Widget, OCRFormPanel> {
	}

	@UiField
	AklaTextBox txtCellName, txtCellXAxis, txtCellYAxis, txtCellWidth, txtCellHeight, txtName;

	@UiField
	AklaTextArea txtDescription;

	@UiField
	HTMLPanel formCellPanel, headerPanel, generalPanel, messagePanel, footerPanel;

	@UiField
	ListBox /*lstServers, */lstForms;

	@UiField
	Label lblTitle, lblDescription;

	@UiField
	Button btnCancel, btnSave, btnUpdate;
	
	@UiField
	SimplePanel imgForm;

	// private boolean displayHeader;
	private User user;
	private IResource resource;
	private List<FormCell> formCells = new ArrayList<FormCell>();
	private List<Form> forms;
	private DefaultDialog dialtohide;

	public OCRFormPanel(boolean displayFooter, User user, DefaultDialog dialtohide) {
		initWidget(uiBinder.createAndBindUi(this));

		// this.displayHeader = displayHeader;
		this.user = user;
		this.dialtohide = dialtohide;
		this.resource = new StandardForm();
		lstForms.getElement().getStyle().setProperty("width", "15%!important");
		lblTitle.setText(LabelsConstants.lblCnst.Form());
		lblDescription.setText(LabelsConstants.lblCnst.DefineFormInformation());
		if (displayFooter) {

			footerPanel.setVisible(true);

		}
		else {

			footerPanel.setVisible(false);
		}
		
		imgForm.setVisible(false);
		getAllAklaBoxForms();
//		try {
//			getAllAklaBoxServers();
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
	}

	public OCRFormPanel(boolean displayHeader, User user, IResource resource, DefaultDialog dialtohide) {
		this(displayHeader, user, dialtohide);
		this.resource = resource;

		txtName.setText(resource.getName());
		txtDescription.setText(((StandardForm) resource).getDescription());
		
		
	}

	@UiHandler("btnSaveCell")
	void onSaveCell(ClickEvent e) {
		if (txtCellXAxis.getText().matches(LabelsConstants.REGEX_DOUBLE) && txtCellYAxis.getText().matches(LabelsConstants.REGEX_DOUBLE) && txtCellWidth.getText().matches(LabelsConstants.REGEX_DOUBLE) && txtCellHeight.getText().matches(LabelsConstants.REGEX_DOUBLE)) {
			FormCell cell = new FormCell(txtCellName.getText(), Integer.parseInt(txtCellXAxis.getText()), Integer.parseInt(txtCellYAxis.getText()), Integer.parseInt(txtCellWidth.getText()), Integer.parseInt(txtCellHeight.getText()));
			formCells.add(cell);
			if (/*lstServers.getSelectedIndex() != 0 && */lstForms.getSelectedIndex() != 0) {
				formCellPanel.add(new CellTableItem(cell, this, /*Integer.parseInt(lstServers.getValue(lstServers.getSelectedIndex())),*/ Integer.parseInt(lstForms.getValue(lstForms.getSelectedIndex()))));
			}
			else {
				formCellPanel.add(new CellTableItem(cell, this));
			}

			txtCellName.setText("");
			txtCellXAxis.setText("");
			txtCellYAxis.setText("");
			txtCellWidth.setText("");
			txtCellHeight.setText("");
			messagePanel.clear();
		}
		else {
			messagePanel.add(new MessageUtil(LabelsConstants.lblCnst.InvalidNumberCell(), false));
		}
		checkSelectedForm();
	}

	public void checkSelectedForm() {
		lstForms.setEnabled(true);
		for (Widget w : formCellPanel) {
			if (w instanceof CellTableItem) {
				if (((CellTableItem) w).getFormId() != 0) {
					lstForms.setEnabled(false);
					break;
				}
			}
		}

	}

	@UiHandler("lblCellDefineCrop")
	void onCellDefineCrop(ClickEvent e) {
//		int aklaBoxServerId = getSelectedAklaboxServer();
//		if (aklaBoxServerId == 0) {
//			messagePanel.add(new MessageUtil(LabelsConstants.lblCnst.SelectAklaBoxServer(), false));
//			return;
//		}
		
		if (txtName.getText().isEmpty()) {
			messagePanel.add(new MessageUtil(LabelsConstants.lblCnst.ProvideNameBeforeCellCropper(), false));
			return;
		}
		
		DefaultDialog dialog = new DefaultDialog(LabelsConstants.lblCnst.CellCroppingTool());
		dialog.setChildContent(new ImageCroppingTool(this, dialog, resource, forms));
		dialog.setDialogWidth("90%", "5%");
		dialog.show();
	}

	private void onInitFormCell() {
		WaitDialog.showWaitPart(true);
		AklaCommonService.Connect.getService().getAllFormCells((StandardForm) resource, new AsyncCallback<List<FormCell>>() {

			@Override
			public void onSuccess(List<FormCell> result) {
				for (FormCell cell : result) {

					if (/*lstServers.getSelectedIndex() != 0 && */lstForms.getSelectedIndex() != 0) {
						formCellPanel.add(new CellTableItem(cell, OCRFormPanel.this, /*Integer.parseInt(lstServers.getValue(lstServers.getSelectedIndex())),*/ Integer.parseInt(lstForms.getValue(lstForms.getSelectedIndex()))));
					}
					else {
						formCellPanel.add(new CellTableItem(cell, OCRFormPanel.this));
					}
					OCRFormPanel.this.formCells.add(cell);
				}
				WaitDialog.showWaitPart(false);

				checkSelectedForm();
				
				if(resource != null){
					String path = ((StandardForm) resource).getBaseImage();
					if(path != null && !path.isEmpty()){
						imgForm.setVisible(true);
						imgForm.add(new OCRModelViewer((StandardForm) resource, result));
					}
				}
				
			}

			@Override
			public void onFailure(Throwable caught) {
				new DefaultResultDialog(caught.getMessage(), "failure").show();
				WaitDialog.showWaitPart(false);
			}
		});
	}

//	@UiHandler("lstServers")
//	void onChangeAklaBoxServer(ChangeEvent e) {
//		int aklaBoxServerId = getSelectedAklaboxServer();
//		if (aklaBoxServerId == 0) {
//			lstForms.clear();
//		}
//		else {
//			getAllAklaBoxForms(aklaBoxServerId);
//		}
//	}

//	private void getAllAklaBoxServers() {
//		WaitDialog.showWaitPart(true);
//		AklaCommonService.Connect.getService().getAllAklaBoxServers(user, new AsyncCallback<List<AklaBoxServer>>() {
//
//			@Override
//			public void onFailure(Throwable caught) {
//				WaitDialog.showWaitPart(false);
//				new DefaultResultDialog(caught.getMessage(), "failure").show();
//			}
//
//			@Override
//			public void onSuccess(List<AklaBoxServer> result) {
//				lstServers.clear();
//				lstServers.addItem(LabelsConstants.lblCnst.SelectAklaBoxServer(), "0");
//				// aklaFlowContent.getAklaFlowMain().setListAklaboxServer(result);
//				for (AklaBoxServer server : result) {
//					lstServers.addItem(server.getName(), String.valueOf(server.getId()));
//				}
//
//				if (((StandardForm) resource).getSelectedAklaboxServer() != 0) {
//					int id = ((StandardForm) resource).getSelectedAklaboxServer();
//					for (int i = 0; i < lstServers.getItemCount(); i++) {
//						if (Integer.parseInt(lstServers.getValue(i)) == id) {
//							lstServers.setSelectedIndex(i);
//							break;
//						}
//					}
//				}
//
//				WaitDialog.showWaitPart(false);
//				onChangeAklaBoxServer(null);
//			}
//		});
//	}

	private void getAllAklaBoxForms(/*int aklaBoxServerId*/) {
		WaitDialog.showWaitPart(true);
		AklaCommonService.Connect.getService().getAllAklaBoxForm(/*aklaBoxServerId,*/ new AsyncCallback<List<Form>>() {

			@Override
			public void onSuccess(List<Form> result) {
				WaitDialog.showWaitPart(false);
				forms = result;
				lstForms.clear();
				lstForms.addItem("", "0");
				for (Form form : result) {
					lstForms.addItem(form.getFormName(), form.getId() + "");
				}

//				if (lstServers.getSelectedIndex() != 0) {
					if (((StandardForm) resource).getLinkedAklaboxForm() != 0) {
						int id = ((StandardForm) resource).getLinkedAklaboxForm();
						for (int i = 0; i < lstForms.getItemCount(); i++) {
							if (Integer.parseInt(lstForms.getValue(i)) == id) {
								lstForms.setSelectedIndex(i);
								break;
							}
						}
					}
					onInitFormCell();
//				}
			}

			@Override
			public void onFailure(Throwable caught) {
				WaitDialog.showWaitPart(false);
				new DefaultResultDialog(caught.getMessage(), "failure").show();
			}
		});
	}

//	public int getSelectedAklaboxServer() {
//		return Integer.parseInt(lstServers.getValue(lstServers.getSelectedIndex()));
//	}

	public int getSelectedAklaboxForm() {
		return Integer.parseInt(lstForms.getValue(lstForms.getSelectedIndex()));
	}

	public void setSelectedAklaboxForm(int listIndex) {
		lstForms.setSelectedIndex(listIndex);
	}

	private List<Integer> getCellsAklaFormFields() {
		List<Integer> result = new ArrayList<>();
		for (int i = 0; i < formCellPanel.getWidgetCount(); i++) {
			if (formCellPanel.getWidget(i) instanceof CellTableItem) {
				result.add(((CellTableItem) formCellPanel.getWidget(i)).getFormFieldId());
			}
		}
		return result;
	}

	private List<Integer> getCellsAklaForms() {
		List<Integer> result = new ArrayList<>();
		for (int i = 0; i < formCellPanel.getWidgetCount(); i++) {
			if (formCellPanel.getWidget(i) instanceof CellTableItem) {
				result.add(((CellTableItem) formCellPanel.getWidget(i)).getFormId());
			}
		}
		return result;
	}

	public void updateTableCells(List<CellTableItem> items) {
		formCellPanel.clear();
		for (CellTableItem item : items) {
			formCellPanel.add(item);
		}
	}

	public List<FormCell> getFormCells() {
		return formCells;
	}

	public void setFormCells(List<FormCell> formCells) {
		this.formCells = formCells;
	}

	@UiHandler("btnUpdate")
	void onUpdateResource(ClickEvent e) {
		saveOrUpdateResource(IAklaflowConstant.ACTION_UPDATE);
	}

	@UiHandler("btnCancel")
	void onCancelResource(ClickEvent e) {
		dialtohide.hide();
	}

	@UiHandler("btnSave")
	void onSaveResource(ClickEvent e) {
		saveOrUpdateResource(IAklaflowConstant.ACTION_SAVE);
	}

	private boolean isValidFields(HTMLPanel panel) {
		boolean isValid = false;
		for (Widget w : panel) {
			if (w instanceof TextBox) {
				if (((TextBox) w).getText().isEmpty()) {
					w.getElement().setAttribute("style", "border-color: rgb(204, 80, 80) !important;");
					isValid = false;
				}
				else {
					w.getElement().setAttribute("style", "border-color: #e7e7e7 !important;");
					isValid = true;
				}
			}
		}
		messagePanel.clear();
		if (!isValid) {
			messagePanel.add(new MessageUtil(LabelsConstants.lblCnst.ProvideEmptyFills(), false));
		}
		return isValid;
	}

	public void saveOrUpdateResource(String action) {

		if (isValidFields(generalPanel)) {

			if (action.equals(IAklaflowConstant.ACTION_SAVE)) {
				String baseImage = null;
				if (resource instanceof StandardForm) {
					baseImage = ((StandardForm) resource).getBaseImage();
				}
				resource = new StandardForm(txtName.getText(), user.getUserId(), txtDescription.getText());

				if (resource instanceof StandardForm) {
					((StandardForm) resource).setBaseImage(baseImage);
				}

			}
			else if (action.equals(IAklaflowConstant.ACTION_UPDATE)) {
				resource.setName(txtName.getText());
				((StandardForm) resource).setDescription(txtDescription.getText());

			}
			saveOrUpdateResource(resource, action);
		}

	}

	private void saveOrUpdateResource(IResource resource, String action) {

		if (action.equals(IAklaflowConstant.ACTION_SAVE)) {
//			((StandardForm) resource).setSelectedAklaboxServer(getSelectedAklaboxServer());
			((StandardForm) resource).setLinkedAklaboxForm(getSelectedAklaboxForm());
			saveStandardResouce(resource, getFormCells());
		}
		else if (action.equals(IAklaflowConstant.ACTION_UPDATE)) {
			updateStandardResource(resource, getFormCells());
		}

	}

	public void saveStandardResouce(IResource resource2, List<FormCell> formCells) {
		WaitDialog.showWaitPart(true);
		boolean isLinked = ((StandardForm) resource).getLinkedAklaboxForm() != 0;
		AklaCommonService.Connect.getService().saveStandardForm((StandardForm) resource, formCells, (isLinked) ? getCellsAklaForms() : null, (isLinked) ? getCellsAklaFormFields() : null, new AsyncCallback<StandardForm>() {

			@Override
			public void onFailure(Throwable caught) {
				new DefaultResultDialog(caught.getMessage(), "failure").show();
				WaitDialog.showWaitPart(false);
			}

			@Override
			public void onSuccess(StandardForm result) {
				if (result.getBaseImage() != null) {
					AklaCommonService.Connect.getService().saveFormTextTemplate(result, new AsyncCallback<Void>() {

						@Override
						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onSuccess(Void result) {
							// TODO Auto-generated method stub

						}
					});
				}
				new DefaultResultDialog(LabelsConstants.lblCnst.ResourceSaved(), "success").show();
				// resourcePanel.buildResourceTree();
				dialtohide.hide();
				WaitDialog.showWaitPart(false);
			}
		});

	}

	public void updateStandardResource(IResource resource, List<FormCell> formCells) {
		WaitDialog.showWaitPart(true);
		boolean isLinked = ((StandardForm) resource).getLinkedAklaboxForm() != 0;
		AklaCommonService.Connect.getService().updateStandardForm((StandardForm) resource, formCells, (isLinked) ? getCellsAklaForms() : null, (isLinked) ? getCellsAklaFormFields() : null, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				new DefaultResultDialog(caught.getMessage(), "failure").show();
				WaitDialog.showWaitPart(false);
			}

			@Override
			public void onSuccess(Void result) {
				new DefaultResultDialog(LabelsConstants.lblCnst.ResourceUpdated(), "success").show();
				// resourcePanel.buildResourceTree();
				dialtohide.hide();
				WaitDialog.showWaitPart(false);
			}
		});
	}

	public IResource getResource() {
		return resource;
	}

	public void setResource(IResource resource) {
		this.resource = resource;
	}

}
