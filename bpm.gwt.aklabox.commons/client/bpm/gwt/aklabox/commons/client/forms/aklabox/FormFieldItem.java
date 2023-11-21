package bpm.gwt.aklabox.commons.client.forms.aklabox;

import java.util.HashMap;
import java.util.List;

import bpm.document.management.core.model.City;
import bpm.document.management.core.model.Country;
import bpm.document.management.core.model.Departement;
import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.FormField;
import bpm.document.management.core.model.FormField.FormFieldType;
import bpm.document.management.core.model.IObject;
import bpm.document.management.core.model.IObject.ItemTreeType;
import bpm.document.management.core.model.User;
import bpm.document.management.core.model.aklademat.Classification;
import bpm.document.management.core.utils.AklaboxConstant;
import bpm.document.management.core.utils.DocumentUtils;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.dialogs.AklaboxSelectionDialog;
import bpm.gwt.aklabox.commons.client.services.AklaCommonService;
import bpm.gwt.aklabox.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.aklabox.commons.client.utils.DefaultDialog;
import bpm.gwt.aklabox.commons.client.utils.DefaultResultDialog;
import bpm.gwt.aklabox.commons.client.utils.MessageDialog;
import bpm.gwt.aklabox.commons.client.utils.StringUtils;
import bpm.gwt.aklabox.commons.client.utils.Tools;
import bpm.gwt.aklabox.commons.client.utils.WaitDialog;
import bpm.gwt.aklabox.commons.shared.FormFieldValueDTO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;


/**
 * Can be deleted someday when it is not used anymore
 */
@Deprecated
public class FormFieldItem extends Composite {

	private static FormFieldItemUiBinder uiBinder = GWT.create(FormFieldItemUiBinder.class);

	interface FormFieldItemUiBinder extends UiBinder<Widget, FormFieldItem> {
	}

	@UiField
	Label lblLabel, lblHelper, lblFileName;
	@UiField
	HTMLPanel typePanel, booleanPanel, addressField, thresholdPanel, trashPanel, uploadPanel, userPanel,
	sourcePanel, displayPanel;

	@UiField
	HorizontalPanel lstPanel;

	@UiField
	TextBox txtBox, txtMin, txtMax, txtValue;
	@UiField
	DateBox dpBox;
	@UiField
	public ListBox lstBox, lstUser, lstCol;
	@UiField
	Button btnSave, btnAddLst, btnImport;
	@UiField
	Image btnDelete;
	@UiField
	ListBox lstCountry, lstCity, lstRegion;

	@UiField
	TextArea txtLineOne;
	@UiField
	CheckBox cbColKey, cbDisplay;
	@UiField
	RadioButton selectToPdf;

	private FormField field;

	public HashMap<String, Country> countries = new HashMap<String, Country>();
	public HashMap<String, Departement> regions = new HashMap<String, Departement>();
	public HashMap<String, City> cities = new HashMap<String, City>();

	public boolean regionReady = false;

	private IObject object;
	private FormDisplayPanel fdp;
	private boolean notifySelected = false;
	private User user;

	public FormFieldItem(FormField field, User user) {
		initWidget(uiBinder.createAndBindUi(this));
		this.user = user;

		txtLineOne.setVisibleLines(2);

		trashPanel.getElement().getStyle().setDisplay(Display.NONE);
		this.field = field;
		dpBox.setFormat(new DateBox.DefaultFormat(DateTimeFormat.getFormat(PredefinedFormat.DATE_LONG)));
		dpBox.getDatePicker().getElement().getStyle().setZIndex(999);
		
		String fieldLabel = Tools.getChorusLabel(field.getLabel());
		lblLabel.setText(fieldLabel);
		lblLabel.setTitle(fieldLabel);
		
		lblHelper.setText(field.getHelpText());
		btnSave.removeFromParent();

		sourcePanel.setVisible(false);
		displayPanel.setVisible(false);
		
		initField(field.getType());
		if (field.isRequired()) {
			lblLabel.setText(field.getLabel() + "*");
		}
		populateCountry();
		cbColKey.setValue(field.isKeyFilterSourceConnection());
	}

	public FormFieldItem(FormField field2, FormDisplayPanel formDisplayPanel, boolean notifySelected, User user) {
		initWidget(uiBinder.createAndBindUi(this));

		this.notifySelected = notifySelected;
		this.user = user;
		this.fdp = formDisplayPanel;

		txtLineOne.setVisibleLines(2);

		trashPanel.getElement().getStyle().setDisplay(Display.NONE);
		this.field = field2;
		dpBox.setFormat(new DateBox.DefaultFormat(DateTimeFormat.getFormat(PredefinedFormat.DATE_LONG)));
		dpBox.getDatePicker().getElement().getStyle().setZIndex(999);
		
		String fieldLabel = Tools.getChorusLabel(field.getLabel());
		lblLabel.setText(fieldLabel);
		lblLabel.setTitle(fieldLabel);
		lblHelper.setText(field.getHelpText());
		
		btnSave.removeFromParent();
		sourcePanel.setVisible(false);
		displayPanel.setVisible(false);
		initField(field.getType());
		if (field.isRequired()) {
			lblLabel.setText(field.getLabel() + "*");
		}
		populateCountry();
		if (notifySelected) {
			selectToPdf.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		}
		cbColKey.setValue(field.isKeyFilterSourceConnection());
	}

	public void initField(FormFieldType type) {
		typePanel.clear();

		switch (type) {
		case STRING:
		case NAME:
		case INTEGER:
			typePanel.add(txtBox);
			break;
		case BOOLEAN:
			typePanel.add(booleanPanel);
			break;
		case DATE:
			typePanel.add(dpBox);
			break;
		case CALENDAR_TYPE:
			typePanel.add(dpBox);
			typePanel.add(userPanel);

			try {
				for (User u : field.getTypeTask().getUsers()) {
					lstUser.addItem(u.getFirstName() + " " + u.getLastName(), u.getEmail());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			break;
		case LOV:
			if (field.getCanAdd() != null && field.getCanAdd()) {
				btnAddLst.setVisible(true);
			}
			else {
				btnAddLst.setVisible(false);
			}

			typePanel.add(lstPanel);
			getAllLovItems();
			break;
		case OPTIONS:
			// TODO LATER
			break;
		case ADDRESS:
			lblLabel.setVisible(false);
			typePanel.add(addressField);
			break;
		case THRESHOLD:
			typePanel.add(thresholdPanel);
			setThreshold();
			break;
		case UPLOAD_DOCUMENT:
			typePanel.add(uploadPanel);
			break;
		case DEED_CLASSIFICATION:
			btnAddLst.setVisible(false);
			typePanel.add(lstPanel);
			getDeedClassifications();
			break;
		default:
			break;
		}

	}

	private void getAllLovItems() {
		WaitDialog.showWaitPart(true);
		AklaCommonService.Connect.getService().getAllLovByTableCode(field.getLovTableCode(), new GwtCallbackWrapper<HashMap<String, String>>(null, false, false) {

			@Override
			public void onSuccess(HashMap<String, String> result) {
				lstBox.clear();
				if (result != null) {
					for (String key : result.keySet()) {
						String value = result.get(key);
						lstBox.addItem(key, value);
					}
				}
				WaitDialog.showWaitPart(false);
			}
		}.getAsyncCallback());
	}

	private void getDeedClassifications() {
		WaitDialog.showWaitPart(true);
		AklaCommonService.Connect.getService().getDeedClassifications(true, new GwtCallbackWrapper<List<Classification>>(null, false, false) {

			@Override
			public void onSuccess(List<Classification> result) {
				lstBox.clear();
				for (Classification classification : result) {
					lstBox.addItem(classification.getName());
				}
				WaitDialog.showWaitPart(false);
			}
		}.getAsyncCallback());
	}

	private void setThreshold() {
		txtMax.setText("10 000");
		txtMin.setText("1000");
		txtMax.setEnabled(false);
		txtMin.setEnabled(false);
	}

	private void populateCountry() {
		AklaCommonService.Connect.getService().getCountry(new AsyncCallback<List<Country>>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(List<Country> result) {
				lstCountry.clear();
				countries.clear();
				lstCountry.addItem(LabelsConstants.lblCnst.SelectCountry());
				for (Country c : result) {

					countries.put(c.getCountryName(), c);

					lstCountry.addItem(c.getCountryName(), String.valueOf(c.getCountryId()));
				}
			}
		});
	}

	@UiHandler("lstCountry")
	void onClick(ClickEvent e) {

		try {
			int i = -1;
			try {
				i = Integer.parseInt(lstCountry.getValue(lstCountry.getSelectedIndex()));
			} catch (Exception ex) {
				i = countries.get(lstCountry.getItemText(lstCountry.getSelectedIndex())).getCountryId();
			}

			AklaCommonService.Connect.getService().getDepartements(i, new AsyncCallback<List<Departement>>() {

				@Override
				public void onFailure(Throwable caught) {
				}

				@Override
				public void onSuccess(List<Departement> result) {
					lstRegion.clear();
					regions.clear();
					for (Departement c : result) {
						regions.put(c.getName(), c);
						lstRegion.addItem(c.getName(), c.getId() + "");
					}
				}
			});
		} catch (Exception e1) {
		}
	}

	@UiHandler("lstRegion")
	void onRegionClick(ClickEvent e) {

		int i = -1;
		try {
			i = Integer.parseInt(lstRegion.getValue(lstRegion.getSelectedIndex()));
		} catch (Exception ex) {
			i = regions.get(lstRegion.getItemText(lstRegion.getSelectedIndex())).getId();
		}

		AklaCommonService.Connect.getService().getCities(i, new AsyncCallback<List<City>>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(List<City> result) {

				lstCity.clear();
				cities.clear();
				lstCity.addItem(LabelsConstants.lblCnst.SelectCity());
				for (City c : result) {
					cities.put(c.getCityName(), c);
					lstCity.addItem(c.getCityName(), String.valueOf(c.getCityId()));
				}

				regionReady = true;
			}
		});
	}

	@UiHandler("btnDelete")
	void onDelete(ClickEvent e) {
		final MessageDialog message = new MessageDialog(LabelsConstants.lblCnst.AreYouSureYouWantToDeleteThisFormField(), true);
		DefaultDialog dial = new DefaultDialog(LabelsConstants.lblCnst.Delete(), message, 400, 210, 10);
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (message.isConfirm()) {
					deleteFormField();
				}
			}
		});
		dial.center();
	}

	public void deleteFormField() {
		WaitDialog.showWaitPart(true);
		AklaCommonService.Connect.getService().deleteFormField(field, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				FormFieldItem.this.removeFromParent();
				WaitDialog.showWaitPart(false);
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				new DefaultResultDialog(caught.getMessage(), "failure").show();
				WaitDialog.showWaitPart(false);
			}
		});
	}

	public Image getBtnDelete() {
		return btnDelete;
	}
	
	public HTMLPanel getDisplayPanel() {
		return displayPanel;
	}

	public void setBtnDelete(Image btnDelete) {
		this.btnDelete = btnDelete;
	}

	public FormField getField() {
		return field;
	}

	public void setField(FormField field) {
		this.field = field;
	}

	public String getValue() throws Exception {
		String value = "";

		if (field.getType() == FormFieldType.STRING || field.getType() == FormFieldType.NAME || field.getType() == FormFieldType.INTEGER) {
			value = txtBox.getText();
			if (!StringUtils.checkRegex(value, field.getRegex())) {
				throw new Exception(LabelsConstants.lblCnst.TheField() + " '" + field.getLabel() + "' " + LabelsConstants.lblCnst.doesNotMatchThePattern() + " '" + field.getRegex() + "' " + LabelsConstants.lblCnst.withTheValue() + " '" + value + "'");
			}
		}
		else if (field.getType() == FormFieldType.BOOLEAN) {
			for (Widget w : booleanPanel) {
				if (w instanceof RadioButton) {
					if (((RadioButton) w).getValue()) {
						value = ((RadioButton) w).getText();
						break;
					}
				}
			}
		}
		else if (field.getType() == FormFieldType.DATE) {
			if (dpBox.getValue() != null) {
				value = DateTimeFormat.getFormat(AklaboxConstant.PATTERN_DATE).format(dpBox.getValue());
			}
			else {
				value = "";
			}
		}
		else if (field.getType() == FormFieldType.CALENDAR_TYPE) {
			if (dpBox.getValue() != null) {
				value = com.google.gwt.i18n.client.DateTimeFormat.getFormat("yyyy-MM-dd").format(dpBox.getValue());
			}
			else {
				value = "";
			}
			value += ";" + lstUser.getValue(lstUser.getSelectedIndex());
		}
		else if (field.getType() == FormFieldType.LOV || field.getType() == FormFieldType.DEED_CLASSIFICATION) {
			try {
				value = lstBox.getValue(lstBox.getSelectedIndex());
			} catch (Exception e) {
				value = "";
			}
		}
		else if (field.getType() == FormFieldType.ADDRESS) {
			for (Widget w : addressField) {
				if (w instanceof TextBox) {
					value = value + ((TextBox) w).getText() + ";";
				}
				else if (w instanceof TextArea) {
					value = value + ((TextArea) w).getText() + ";";
				}
				else if (w instanceof ListBox) {
					ListBox list = (ListBox) w;
					value = value + list.getItemText(list.getSelectedIndex()) + ";";
				}
			}
		}
		else if (field.getType() == FormFieldType.UPLOAD_DOCUMENT) {
			if (object != null) {
				value = object.getId() + "";
			}
			else {
				value = "";
			}
		}
		return value;
	}

	public void setValue(String value) {
		if (field.getType() == FormFieldType.STRING) {
			txtBox.setText(value);
		}
		else if (field.getType() == FormFieldType.NAME || field.getType() == FormFieldType.INTEGER) {
			txtBox.setText(value);
		}
		else if (field.getType() == FormFieldType.THRESHOLD) {
			txtValue.setText(value);
		}
		else if (field.getType() == FormFieldType.BOOLEAN) {
			for (Widget w : booleanPanel) {
				if (w instanceof RadioButton) {
					if (((RadioButton) w).getText().equals(value)) {
						((RadioButton) w).setValue(true);
						break;
					}
				}
			}
		}
		else if (field.getType() == FormFieldType.DATE) {
			DateTimeFormat dtf = DateTimeFormat.getFormat(AklaboxConstant.PATTERN_DATE);
			// dpBox.setValue(com.google.gwt.i18n.client.DateTimeFormat.getMediumDateFormat().parse(value));
			dpBox.setValue(dtf.parse(value));
		}
		else if (field.getType() == FormFieldType.CALENDAR_TYPE) {
			String date = "";
			String user = "";
			try {
				date = value.split(";")[0];
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				user = value.split(";")[1];
			} catch (Exception e) {
				e.printStackTrace();
			}

			dpBox.setValue(com.google.gwt.i18n.client.DateTimeFormat.getFormat("yyyy-MM-dd").parse(value));
			for (int i = 0; i < lstUser.getItemCount(); i++) {
				if (lstUser.getValue(i).equals(user)) {
					lstUser.setSelectedIndex(i);
					break;
				}
			}
		}
		else if (field.getType() == FormFieldType.LOV) {
			for (int i = 0; i < lstBox.getItemCount(); i++) {
				if (lstBox.getValue(i).equals(value)) {
					lstBox.setSelectedIndex(i);
					break;
				}
			}
		}
		else if (field.getType() == FormFieldType.ADDRESS) {
			String[] values = value.split(";");
			for (Widget w : addressField) {
				if (w instanceof TextBox) {
					((TextBox) w).setText(values[0]);
				}
				else if (w instanceof TextArea) {
					((TextArea) w).setText(values[1]);
				}
				else if (w instanceof ListBox) {
					ListBox list = (ListBox) w;
					for (int i = 0; i < list.getItemCount(); i++) {
						if (list.getItemText(i).equals(values[2])) {
							list.setSelectedIndex(i);
							break;
						}
					}
				}
			}
		}
		else if (field.getType() == FormFieldType.UPLOAD_DOCUMENT) {
			if (!value.isEmpty()) {
				AklaCommonService.Connect.getService().getDocInfo(Integer.parseInt(value), new AsyncCallback<Documents>() {

					@Override
					public void onSuccess(Documents result) {
						object = result;
					}

					@Override
					public void onFailure(Throwable caught) {
						DefaultResultDialog dial = new DefaultResultDialog(LabelsConstants.lblCnst.UnableToGetInformations(), "failure");
					}
				});
			}

		}
		else if (field.getType() == FormFieldType.DEED_CLASSIFICATION) {
			for (int i = 0; i < lstBox.getItemCount(); i++) {
				if (lstBox.getValue(i).equals(value)) {
					lstBox.setSelectedIndex(i);
					break;
				}
			}
		}
	}

	public void setValue(FormFieldValueDTO value, boolean disableField) {

	}

	@UiHandler("btnAddLst")
	public void onAddLst(ClickEvent e) {
		final AddListValue createDoc = new AddListValue(field.getLovTableCode());

		DefaultDialog defaultDialog = new DefaultDialog(LabelsConstants.lblCnst.addFormVal() + field.getLabel(), createDoc, 500, 210, 10);
		defaultDialog.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (createDoc.isConfirm()) {
					getAllLovItems();
				}
			}
		});
		defaultDialog.center();
	}

	public void showTrash() {
		trashPanel.setVisible(true);
	}

	public Widget getWidget() {
		return super.getWidget();
	}

	@UiHandler("btnImport")
	public void onImportDoc(ClickEvent e) {
//		final TreeDialog docTree = new TreeDialog(true);
//		docTree.addCloseHandler(new CloseHandler<PopupPanel>() {
//
//			@Override
//			public void onClose(CloseEvent<PopupPanel> event) {
//				if (docTree.isConfirm() && docTree.isDocument()) {
//					IObject obj = docTree.getSelectedObject();
//					lblFileName.setText(obj.getName());
//
//				}
//				else {
//
//				}
//			}
//		});
//		docTree.center();
		
		final AklaboxSelectionDialog dial = new AklaboxSelectionDialog(null, user, new DocumentUtils(), ItemTreeType.ENTERPRISE, null, true);
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isConfirm() && dial.getSelectedItem() instanceof Documents) {
					IObject obj = dial.getSelectedItem();
					lblFileName.setText(obj.getName());

				}
				else {

				}
			}
		});
		dial.center();
	}

	@UiHandler("btnClear")
	public void onClearFile(ClickEvent e) {
		object = null;
		lblFileName.setText(LabelsConstants.lblCnst.NoFileSelected());
	}

	@UiHandler("selectToPdf")
	public void onSelectedItem(ClickEvent e) {
		this.fdp.setSelectedItemToPDFMap(this);
	}
	
	public HTMLPanel getSourcePanel(){
		return sourcePanel;
	}
	
	public ListBox getLstColSource(){
		return lstCol;
	}
	
	public void fillColSource(List<String> cols){
		if(cols == null){
			sourcePanel.setVisible(false);
			field.setColSourceConnection("");
		} else {
			sourcePanel.setVisible(true);
			lstCol.clear();
			lstCol.addItem("");
			for(String col : cols){
				lstCol.addItem(col);
				if(field.getColSourceConnection() != null && !field.getColSourceConnection().isEmpty() && col.equals(field.getColSourceConnection())){
					lstCol.setSelectedIndex(cols.indexOf(col)+1);
				}
			}
		}
		
	}
	
	public void showDisplayCol(){
		displayPanel.setVisible(true);
		cbDisplay.setValue(field.isDisplayed());
		
	}
	
	@UiHandler("lstCol")
	public void onChangeCol(ChangeEvent e){
		field.setColSourceConnection(lstCol.getValue(lstCol.getSelectedIndex()));
	}
	
	@UiHandler("cbColKey")
	public void onClickKey(ClickEvent e){
		field.setIsKeyFilterSourceConnection(cbColKey.getValue());
	}
	
	public boolean mustBeDisplayed(){
		return cbDisplay.getValue();
	}
	
	@UiHandler("cbDisplay")
	public void onClickDisplay(ClickEvent e){
		field.setDisplayed(cbDisplay.getValue());
	}
}
