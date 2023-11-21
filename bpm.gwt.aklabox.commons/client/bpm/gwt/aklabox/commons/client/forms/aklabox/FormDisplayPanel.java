package bpm.gwt.aklabox.commons.client.forms.aklabox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.document.management.core.model.AkladematAdminEntity;
import bpm.document.management.core.model.AkladematAdminEntity.DocType;
import bpm.document.management.core.model.City;
import bpm.document.management.core.model.Departement;
import bpm.document.management.core.model.Form;
import bpm.document.management.core.model.Form.FormType;
import bpm.document.management.core.model.FormField;
import bpm.document.management.core.model.FormField.FormFieldType;
import bpm.document.management.core.model.FormFieldValue;
import bpm.document.management.core.model.IAdminDematObject;
import bpm.document.management.core.model.IObject;
import bpm.document.management.core.model.LOV;
import bpm.document.management.core.model.MetadataLink;
import bpm.document.management.core.model.TypeTask;
import bpm.document.management.core.model.User;
import bpm.document.management.core.model.aklademat.Classification;
import bpm.document.management.core.utils.AklaboxConstant;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.observerpattern.Observer;
import bpm.gwt.aklabox.commons.client.services.AklaCommonService;
import bpm.gwt.aklabox.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.aklabox.commons.client.utils.ChildDialogComposite;
import bpm.gwt.aklabox.commons.client.utils.DefaultResultDialog;
import bpm.gwt.aklabox.commons.client.utils.HelperMethods;
import bpm.gwt.aklabox.commons.client.utils.MessageHelper;
import bpm.gwt.aklabox.commons.client.utils.PathHelper;
import bpm.gwt.aklabox.commons.client.utils.StringUtils;
import bpm.gwt.aklabox.commons.client.utils.WaitDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.Widget;

public class FormDisplayPanel extends ChildDialogComposite implements Observer, IFormPanel {

	private static FormDisplayPanelUiBinder uiBinder = GWT.create(FormDisplayPanelUiBinder.class);

	interface FormDisplayPanelUiBinder extends UiBinder<Widget, FormDisplayPanel> {
	}
	
	interface MyStyle extends CssResource{
		String fullBackground();
	}

//	@UiField
//	Image imgLogo;
//
//	@UiField
//	Label lblTitle, lblDescription;

	@UiField
	HTMLPanel bgPanel, sideBar, fieldPanel, customField, /*defaultFormPanel, extensionPanel,*/ thresholdPanel, typeTaskPanel;

	@UiField
	TextBox txtFieldLabel, /*txtExtensions,*/ txtRegex;

	@UiField
	TextArea txtFieldHelper;

	@UiField
	ListBox lstFieldType, lstAddress, lstTypeTask;

	@UiField
	CheckBox isRequired;

	@UiField
	Button btnSaveValue, btnAddField, btnSave;

	@UiField
	SimplePanel lstLov;
	
	@UiField
	MyStyle style;

	private IObject doc;
	private Form form;
	private MetadataLink link;
	private FormFieldItem selectedItemToPDFMap;

	private CheckBox chkCanAdd = new CheckBox("Autoriser l'ajout de valeurs");

	private boolean isAdd = true;
	private boolean isUploading;
	private boolean selectedToPDFForm = false;
	private User user;
	
	private List<FormField> fields;
	private List<FormFieldValue> values = new ArrayList<>();

	private boolean saveOnline = true;
	
	private List<String> sourceCols = null;

	public FormDisplayPanel(Form form, boolean isUploading, IObject doc, MetadataLink link, User user) {
		initWidget(uiBinder.createAndBindUi(this));
		this.form = form;
		this.isUploading = isUploading;
		this.doc = doc;
		this.link = link;
		this.user = user;
		
		this.saveOnline = true;

		buildContent(form);
	}

	public FormDisplayPanel(Form form, boolean isUploading, IObject doc, boolean isSelectedToPDF, User user) {
		initWidget(uiBinder.createAndBindUi(this));
		this.form = form;
		this.isUploading = isUploading;
		this.doc = doc;
		this.selectedToPDFForm = isSelectedToPDF;
		this.user = user;
		
		this.saveOnline = true;

		buildContent(form);
	}
	
	public FormDisplayPanel(Form form, IObject doc, User user, List<FormFieldValue> values) {
		initWidget(uiBinder.createAndBindUi(this));
		this.form = form;
		this.isUploading = true;
		this.doc = doc;
		this.selectedToPDFForm = false;
		this.user = user;
		
		this.saveOnline = false;
		this.values = values;

		buildContent(form);
	}

	private void buildContent(Form form) {
//		UserMain.getInstance().registerObserver(this);
		setColor();

//		lblTitle.setText(form.getFormName());
//		lblDescription.setText(form.getFormComment());
//		if (form.getFormLogo() != null) {
//			imgLogo.setUrl(PathHelper.getRightPath(form.getFormLogo()));
//		}
		if (form.getFormBackground() != null) {
			bgPanel.addStyleName(style.fullBackground());
			bgPanel.getElement().setAttribute("style", "background-image: url(" + PathHelper.getRightPath(form.getFormBackground()) + ");");
		}

		for (FormFieldType type : FormFieldType.values()) {
			lstFieldType.addItem(HelperMethods.getFormFieldTypeLabel(type), type.getType() + "");
		}

		lstLov.add(lovs);

//		if (!form.getFormType().equals(AklaboxConstant.FORM_EXTENSION)) {
//			defaultFormPanel.removeFromParent();
//		}
//		else {
//			getAllExtensions();
//		}

		AklaCommonService.Connect.getService().getTaskTypes( new GwtCallbackWrapper<List<TypeTask>>(null, false, false) {
			@Override
			public void onSuccess(List<TypeTask> result) {
				for (TypeTask tt : result) {
					lstTypeTask.addItem(tt.getName(), tt.getId() + "");
				}
			}
		}.getAsyncCallback());

		getAllFormFields();
		
		thresholdPanel.setVisible(false);
		typeTaskPanel.setVisible(false);
	}

	private void setColor() {
//		ThemeColorManager.setButtonColor(UserMain.getInstance().getUser().getSelectedTheme(), btnAddField);
//		ThemeColorManager.setButtonColor(UserMain.getInstance().getUser().getSelectedTheme(), btnSave);
	}

//	private void getAllExtensions() {
//		extensionPanel.clear();
//		if (form.getFileExtension() != null) {
//			if (!form.getFileExtension().isEmpty()) {
//				String[] formExt = form.getFileExtension().split(",");
//				for (String ext : formExt) {
//					extensionPanel.add(new ExtensionListItem(ext));
//				}
//			}
//		}
//	}

	private void getAllFormFields() {
		customField.clear();
		WaitDialog.showWaitPart(true);
		AklaCommonService.Connect.getService().getAllFormField(form.getId(), new AsyncCallback<List<FormField>>() {

			@Override
			public void onSuccess(List<FormField> result) {
				fields = result;
				fieldPanel.clear();
				for (FormField field : result) {
					FormFieldItem f;
					if (selectedToPDFForm) {
						f = new FormFieldItem(field, FormDisplayPanel.this, true, user);
					}
					else {
						f = new FormFieldItem(field, user);
					}
					if (!isUploading) {
						if(form.getType() !=  FormType.METADATA){
							f.showTrash();
						} else {
							f.showDisplayCol();
						}
						
					}
					if(sourceCols != null) f.fillColSource(sourceCols);
					fieldPanel.add(f);
				}
				WaitDialog.showWaitPart(false);
				if (isUploading) {
					sideBar.removeFromParent();
//					defaultFormPanel.removeFromParent();
					for (Widget w : fieldPanel) {
						if (w instanceof FormFieldItem) {
							FormFieldItem item = (FormFieldItem) w;
							item.getBtnDelete().removeFromParent();
							//item.getSourcePanel().removeFromParent();
						}
					}
				}
				else {
					btnSaveValue.removeFromParent();
					if(form.getType() ==  FormType.METADATA){
						sideBar.removeFromParent();
					}
				}
				if (getSelectedFormFieldType() == FormFieldType.LOV) {
					getAllLOV();
				}
				// else if (getSelectedFormFieldType() ==
				// FormFieldType.DEED_CLASSIFICATION) {
				// loadClassifications();
				// }
				if(values != null && !values.isEmpty()){
					retrieveAllFieldValues(values);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				new DefaultResultDialog(caught.getMessage(), "failure").show();
				WaitDialog.showWaitPart(false);
			}
		});
	}

	private FormFieldType getSelectedFormFieldType() {
		return FormFieldType.valueOf(Integer.parseInt(lstFieldType.getValue(lstFieldType.getSelectedIndex())));
	}

	private void getAllLOV() {
		customField.clear();
		customField.add(lstLov);
		customField.add(chkCanAdd);

		WaitDialog.showWaitPart(true);
		AklaCommonService.Connect.getService().getAllLov(new GwtCallbackWrapper<List<LOV>>(null, false, false) {

			@Override
			public void onSuccess(List<LOV> result) {
				if (result != null && !result.isEmpty()) {
					lovs.setValue(result.get(0));
					lovs.setAcceptableValues(result);
				}
				WaitDialog.showWaitPart(false);
			}
		}.getAsyncCallback());
	}

	// private void loadClassifications() {
	// customField.clear();
	// customField.add(lstLov);
	// customField.add(chkCanAdd);
	//
	// WaitDialog.showWaitPart(true);
	// AklaCommonService.Connect.getService().getDeedClassifications(true, new
	// GwtCallbackWrapper<List<Classification>>(null, false, false) {
	//
	// @Override
	// public void onSuccess(List<Classification> result) {
	// if (result != null && !result.isEmpty()) {
	// classifications.setValue(result.get(0));
	// classifications.setAcceptableValues(result);
	// }
	// WaitDialog.showWaitPart(false);
	// }
	// }.getAsyncCallback());
	// }

//	@UiHandler("btnAddExtension")
//	void onAddExtensions(ClickEvent e) {
//		if (!txtExtensions.getText().isEmpty()) {
//			extensionPanel.add(new ExtensionListItem(txtExtensions.getText()));
//		}
//		else {
//			new DefaultResultDialog("Please provide an extension", "failure").show();
//		}
//	}

//	@UiHandler("btnUpdateForm")
//	void onUpdateForm(ClickEvent e) {
//		String ext = "";
//		for (Widget w : extensionPanel) {
//			if (w instanceof ExtensionListItem) {
//				ext = ext + ((ExtensionListItem) w).lblExt.getText() + ",";
//			}
//		}
//		form.setFileExtension(ext);
//		AklaCommonService.Connect.getService().updateForm(form, new AsyncCallback<Void>() {
//
//			@Override
//			public void onFailure(Throwable caught) {
//				new DefaultResultDialog(caught.getMessage(), "failure").show();
//			}
//
//			@Override
//			public void onSuccess(Void result) {
//				new DefaultResultDialog("Form successfully updated the extension", "success").show();
//			}
//		});
//	}

	@UiHandler("lstFieldType")
	void onType(ChangeEvent e) {
		customField.clear();
		FormFieldType type = getSelectedFormFieldType();

		if (type == FormFieldType.STRING) {
			txtRegex.setVisible(true);
		}
		else if (type == FormFieldType.LOV) {
			getAllLOV();
		}
		else if (type == FormFieldType.THRESHOLD) {
			thresholdPanel.setVisible(true);
		}
		else if (type == FormFieldType.CALENDAR_TYPE) {
			typeTaskPanel.setVisible(true);
		}
		// else if (type == FormFieldType.DEED_CLASSIFICATION) {
		// loadClassifications();
		// }
		else {
			txtRegex.setVisible(false);
			thresholdPanel.setVisible(false);
			typeTaskPanel.setVisible(false);
		}
	}

	@UiHandler("btnAddField")
	void onAddField(ClickEvent e) {
		if (isAdd) {
			sideBar.getElement().getStyle().setRight(0, Unit.PX);
			thresholdPanel.setVisible(false);
			typeTaskPanel.setVisible(false);
			isAdd = false;
		}
		else {
			sideBar.getElement().getStyle().setRight(-300, Unit.PX);
			isAdd = true;
		}

	}

	@UiHandler("btnSave")
	void onSave(ClickEvent e) {
		FormFieldType type = getSelectedFormFieldType();

		FormField formField = new FormField(txtFieldLabel.getText(), txtFieldHelper.getText(), type, isRequired.getValue(), form.getId());
		formField.setCanAdd(chkCanAdd.getValue());
		if (type == FormFieldType.STRING) {
			String regex = txtRegex.getText();
			formField.setRegex(!regex.isEmpty() ? regex : null);
		}
		else if (type == FormFieldType.CALENDAR_TYPE) {
			formField.setTaskTypeId(Integer.parseInt(lstTypeTask.getValue(lstTypeTask.getSelectedIndex())));
		}
		else if (type == FormFieldType.LOV) {
			formField.setLovTableCode((LOV) lovs.getValue());
		}

		WaitDialog.showWaitPart(true);
		AklaCommonService.Connect.getService().saveFormField(formField, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				getAllFormFields();
				WaitDialog.showWaitPart(false);
			}

			@Override
			public void onFailure(Throwable caught) {
				WaitDialog.showWaitPart(false);
			}
		});

	}

	@UiHandler("btnSaveValue")
	public void onSaveValue(ClickEvent e) {
		try {
			saveFieldValues();
		} catch (Exception e1) {
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), e1.getMessage());
		}
	}

	public void saveFieldValues() throws Exception {
		values.clear(); 
		for (Widget panel : fieldPanel) {
			if (panel instanceof FormFieldItem) {
				FormFieldItem item = (FormFieldItem) panel;

				String value = "";
				FormField field = item.getField();
				if (field.getType() == FormFieldType.STRING || field.getType() == FormFieldType.NAME || field.getType() == FormFieldType.INTEGER) {
					value = item.txtBox.getText();

					if (!StringUtils.checkRegex(value, field.getRegex())) {
						throw new Exception(LabelsConstants.lblCnst.TheField() + " '" + field.getLabel() + "' " + LabelsConstants.lblCnst.doesNotMatchThePattern() + " '" + field.getRegex() + "' " + LabelsConstants.lblCnst.withTheValue() + " '" + value + "'");
					}
				}
				else if (field.getType() == FormFieldType.BOOLEAN) {
					for (Widget w : item.booleanPanel) {
						if (w instanceof RadioButton) {
							if (((RadioButton) w).getValue()) {
								value = ((RadioButton) w).getText();
								break;
							}
						}
					}
				}
				else if (field.getType() == FormFieldType.DATE) {
					value = DateTimeFormat.getFormat(AklaboxConstant.PATTERN_DATE).format(item.dpBox.getValue());
				}
				else if (field.getType() == FormFieldType.CALENDAR_TYPE) {
					value = item.getValue();
				}
				else if (field.getType() == FormFieldType.LOV || field.getType() == FormFieldType.DEED_CLASSIFICATION) {
					value = item.lstBox.getValue(item.lstBox.getSelectedIndex());
				}
				else if (field.getType() == FormFieldType.ADDRESS) {
					for (Widget w : item.addressField) {
						if (w instanceof TextBox) {
							value = value + ((TextBox) w).getText() + ";";
						}
						else if (w instanceof ListBox) {
							ListBox list = (ListBox) w;
							value = value + list.getValue(list.getSelectedIndex()) + ";";
						}
					}
				}

				FormFieldValue formFieldValue = new FormFieldValue(field.getId(), value, field.getType(), doc.getId());
				if (link != null) {
					formFieldValue.setMetadataLinkId(link.getId());
				}
				values.add(formFieldValue);
			}
		}

		if(saveOnline){
			WaitDialog.showWaitPart(true);
			AklaCommonService.Connect.getService().saveFormFieldValue(doc.getId(), values, new AsyncCallback<Void>() {

				@Override
				public void onSuccess(Void result) {
					closeParent();
					WaitDialog.showWaitPart(false);
				}

				@Override
				public void onFailure(Throwable caught) {
					new DefaultResultDialog(caught.getMessage(), "failure").show();
					WaitDialog.showWaitPart(false);
				}
			});
		}
		

	}

	public void retrieveAllFieldValues(Form form, final int userId, final IObject document, final boolean disableFields, DocType type) {
		if (type != null) {
			checkEntity(form, userId, document, type);
		}
		else {
			retrieveAllFieldValues(form, userId, document, disableFields);
		}
	}
	
	public void retrieveAllFieldValues(List<FormFieldValue> values) {
		//this.values = values;
		for(FormFieldValue v : values){
			for(FormField f : fields){
				if(v.getFormFieldId() == f.getId()){
					loadFormFieldValue(v, f, false);
				}
			}
		}
		
	}

	private void checkEntity(final Form form, final int userId, final IObject document, DocType type) {
		AklaCommonService.Connect.getService().getAdminEntitybyDoc(document.getId(), type, new GwtCallbackWrapper<AkladematAdminEntity<? extends IAdminDematObject>>(null, false, false) {

			@Override
			public void onSuccess(final AkladematAdminEntity<? extends IAdminDematObject> entity) {
				boolean disableFields = false;
				if (entity != null && entity.getId() != 0) {
					switch (entity.getDocumentStatus()) {
					case NEW:
					case PENDING:
					case ASSIGNED:
					case PENDING_SUSPENSION:
						disableFields = false;
						break;
					case REJECTED:
					case SUSPENDED:
					case VALIDATED:
						disableFields = true;
						break;

					default:
						break;
					}
				}
				
				retrieveAllFieldValues(form, userId, document, disableFields);
			}
		}.getAsyncCallback());
	}

	public void retrieveAllFieldValues(Form result, final int userId, final IObject document, final boolean disableFields) {
		if (disableFields) {
			btnSaveValue.removeFromParent();
		}
		AklaCommonService.Connect.getService().getAllFormField(result.getId(), new GwtCallbackWrapper<List<FormField>>(null, false, false) {

			@Override
			public void onSuccess(List<FormField> result) {
				for (FormField field : result) {
					loadFormFieldValueByDoc(userId, document, field, disableFields);
				}
			}
		}.getAsyncCallback());
	}

	private void loadFormFieldValueByDoc(int userId, IObject document, final FormField field, final boolean disableFields) {
		AklaCommonService.Connect.getService().getFormFieldValueByDoc(field.getId(), userId, document.getId(), new GwtCallbackWrapper<FormFieldValue>(null, false, false) {

			@Override
			public void onSuccess(FormFieldValue result) {
				if (result.getId() > 0) {
					loadFormFieldValue(result, field, disableFields);
				}
			}
		}.getAsyncCallback());
	}
	
	private void loadFormFieldValue(FormFieldValue result, FormField field, boolean disableFields) {
		
			for (Widget w : fieldPanel) {
				if (w instanceof FormFieldItem) {
					final FormFieldItem item = (FormFieldItem) w;

					FormField exist = item.getField();
					if (field.getId() == exist.getId()) {
						if (field.getType() == FormFieldType.STRING || field.getType() == FormFieldType.NAME || field.getType() == FormFieldType.INTEGER) {
							item.txtBox.setText(result.getValue());
						}
						else if (field.getType() == FormFieldType.BOOLEAN) {
							for (Widget a : item.booleanPanel) {
								if (a instanceof RadioButton) {
									if (((RadioButton) a).getValue()) {
										if (((RadioButton) a).getText().equals(result.getValue())) {
											((RadioButton) a).setValue(true);
										}

									}
								}
							}
						}
						else if (field.getType() == FormFieldType.DATE || field.getType() == FormFieldType.CALENDAR_TYPE) {
							try {
								String val = result.getValue();
								if (field.getType() == FormFieldType.CALENDAR_TYPE) {
									val = result.getValue().split(";")[0];
								}
								DateTimeFormat dtf = DateTimeFormat.getFormat(AklaboxConstant.PATTERN_DATE);
								Date date = dtf.parse(val);
								item.dpBox.setValue(date);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						else if (field.getType() == FormFieldType.LOV || field.getType() == FormFieldType.DEED_CLASSIFICATION) {
							String val = result.getValue();

							for (int i = 0; i < item.lstBox.getItemCount(); i++) {
								if (val != null && val.equals(item.lstBox.getValue(i))) {
									item.lstBox.setSelectedIndex(i);
									break;
								}
							}

						}
						else if (field.getType() == FormFieldType.ADDRESS) {
							final String[] val = result.getValue().split(";");
							int index = 0;

							for (Widget b : item.addressField) {
								if (b instanceof TextArea) {
									((TextArea) b).setText(val[index + 3]);
									index++;
								}
							}

							for (int x = 0; x < item.lstCountry.getItemCount(); x++) {
								if (item.lstCountry.getValue(x).equals(val[0])) {
									item.lstCountry.setSelectedIndex(x);

									int countryId = -1;
									try {
										countryId = Integer.parseInt(item.lstCountry.getValue(item.lstCountry.getSelectedIndex()));
									} catch (Exception ex) {
										countryId = item.countries.get(item.lstCountry.getValue(item.lstCountry.getSelectedIndex())).getCountryId();
									}

									loadDepartments(item, val, countryId);
									break;
								}
							}
						}
					}
				}
			}

		if (disableFields) {
			disableFields();
		}
	}

	private void loadDepartments(final FormFieldItem item, final String[] val, int countryId) {
		AklaCommonService.Connect.getService().getDepartements(countryId, new GwtCallbackWrapper<List<Departement>>(null, false, false) {

			@Override
			public void onSuccess(List<Departement> result) {
				item.lstRegion.clear();
				item.regions.clear();
				for (Departement c : result) {
					item.regions.put(c.getName(), c);
					item.lstRegion.addItem(c.getName(), c.getId() + "");
				}

				for (int i = 0; i < item.lstRegion.getItemCount(); i++) {
					if (item.lstRegion.getValue(i).equals(val[1])) {
						item.lstRegion.setSelectedIndex(i);
						break;
					}
				}

				int cityId = -1;
				try {
					cityId = Integer.parseInt(item.lstRegion.getValue(item.lstRegion.getSelectedIndex()));
				} catch (Exception ex) {
					cityId = item.regions.get(item.lstRegion.getValue(item.lstRegion.getSelectedIndex())).getId();
				}

				loadCities(item, val, cityId);
			}
		}.getAsyncCallback());
	}

	private void loadCities(final FormFieldItem item, final String[] val, int cityId) {
		AklaCommonService.Connect.getService().getCities(cityId, new GwtCallbackWrapper<List<City>>(null, false, false) {

			@Override
			public void onSuccess(List<City> result) {
				item.lstCity.clear();
				item.cities.clear();
				item.lstCity.addItem(LabelsConstants.lblCnst.SelectCity());
				for (City c : result) {
					item.cities.put(c.getCityName(), c);
					item.lstCity.addItem(c.getCityName(), String.valueOf(c.getCityId()));
				}

				for (int i = 0; i < item.lstCity.getItemCount(); i++) {
					if (item.lstCity.getValue(i).equals(val[2])) {
						item.lstCity.setSelectedIndex(i);
						break;
					}
				}
			}
		}.getAsyncCallback());
	}

	private void disableFields() {
		for (int i = 0; i < fieldPanel.getWidgetCount(); i++) {
			disableChildren(((FormFieldItem) fieldPanel.getWidget(i)).getWidget());
		}
	}

	private void disableChildren(Widget elem) {
		if (elem instanceof HTMLPanel) {
			for (int i = 0; i < ((HTMLPanel) elem).getWidgetCount(); i++) {
				disableChildren(((HTMLPanel) elem).getWidget(i));
			}
		}
		else if (elem instanceof HorizontalPanel) {
			for (int i = 0; i < ((HorizontalPanel) elem).getWidgetCount(); i++) {
				disableChildren(((HorizontalPanel) elem).getWidget(i));
			}
		}
		else if (elem instanceof FocusWidget) {
			if (elem instanceof TextBox) {
				((TextBox) elem).setReadOnly(true);
			}
			else {
				((FocusWidget) elem).setEnabled(false);
			}
		}
	}

	@Override
	public void notifyObserver() {
		setColor();
	}

	public HTMLPanel getFieldPanel() {
		return fieldPanel;
	}

	public void setSelectedItemToPDFMap(FormFieldItem formFieldItem) {
		this.selectedItemToPDFMap = formFieldItem;
	}

	public FormFieldItem getSelectedItemToPDFMap() {
		return this.selectedItemToPDFMap;
	}

	private ValueListBox<LOV> lovs = new ValueListBox<LOV>(new Renderer<LOV>() {
		@Override
		public String render(LOV object) {
			return object != null ? object.getValueName() : "";
		}

		@Override
		public void render(LOV object, Appendable appendable) throws IOException {
			appendable.append(render(object));
		}
	});

	private ValueListBox<Classification> classifications = new ValueListBox<Classification>(new Renderer<Classification>() {
		@Override
		public String render(Classification object) {
			return object != null ? object.getName() : "";
		}

		@Override
		public void render(Classification object, Appendable appendable) throws IOException {
			appendable.append(render(object));
		}
	});

	@Override
	public void fillForm(Form form) {
		// TODO Auto-generated method stub
		
	}
	public List<FormFieldValue> getValues() {
		return values;
	}
	
	public List<FormField> getFields() {
		return fields;
	}

	public Button getBtnSaveValue() {
		return btnSaveValue;
	}
	
	public void refreshSourceConnection(List<String> cols){
		this.sourceCols = cols;
		for (Widget w : fieldPanel) {
			if (w instanceof FormFieldItem) {
				FormFieldItem item = (FormFieldItem) w;
				item.fillColSource(sourceCols);
			}
		}
	}
}
