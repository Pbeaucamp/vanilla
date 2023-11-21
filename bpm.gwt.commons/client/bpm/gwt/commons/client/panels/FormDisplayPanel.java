package bpm.gwt.commons.client.panels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.custom.ListBoxWithButton.KeyValueItem;
import bpm.gwt.commons.client.dialog.FormEditDialog;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.vanilla.platform.core.beans.data.DatasetResultQuery;
import bpm.vanilla.platform.core.beans.forms.Form;
import bpm.vanilla.platform.core.beans.forms.FormField;
import bpm.vanilla.platform.core.beans.forms.FormField.TypeField;
import bpm.vanilla.platform.core.beans.forms.ListFormField;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class FormDisplayPanel extends Composite {

	private static FormDisplayPanelUiBinder uiBinder = GWT.create(FormDisplayPanelUiBinder.class);

	interface FormDisplayPanelUiBinder extends UiBinder<Widget, FormDisplayPanel> {}

	@UiField
	HTMLPanel leftPanel, rightPanel;
	
	private Form form;
	private Map<FormField, Widget> formWidgets = new HashMap<FormField, Widget>();
	
	private Map<String, FormField> editedLine;
	
	public FormDisplayPanel(RepositoryItem repositoryItem, final boolean showSearch) {
		initWidget(uiBinder.createAndBindUi(this));
		
		CommonService.Connect.getInstance().loadForm(repositoryItem, new GwtCallbackWrapper<Form>(null, false, false) {
			@Override
			public void onSuccess(Form result) {
				form = result;
				
				if(showSearch) {
					final FormEditDialog dial = new FormEditDialog(form);
					dial.addCloseHandler(new CloseHandler<PopupPanel>() {
						@Override
						public void onClose(CloseEvent<PopupPanel> event) {
							editedLine = dial.getSelectedLine();
							for(FormField f : form.getFields()) {
								if(editedLine != null) {
									f.setValue(editedLine.get(f.getLabel()).getValue());
								}
								if(f.isLeft()) {
									buildField(f, leftPanel);
								}
								else {
									buildField(f, rightPanel);
								}
							}
							
						}
					});
					dial.center();
				}
				else {
					for(FormField f : form.getFields()) {
						if(f.isLeft()) {
							buildField(f, leftPanel);
						}
						else {
							buildField(f, rightPanel);
						}
					}
				}
				
			}
		}.getAsyncCallback());
	}

	private void buildField(final FormField f, final HTMLPanel panel) {
		switch(f.getType()) {
			case TEXTBOX:
				LabelTextBox field = new LabelTextBox();
				field.setPlaceHolder(f.getLabel());
				if(f.getValue() != null) {
					field.setText(f.getValue());
				}
				panel.add(field);
				formWidgets.put(f, field);
				break;
			case LISTBOX:
				CommonService.Connect.getInstance().executeDataset(((ListFormField)f).getDataset(), new GwtCallbackWrapper<DatasetResultQuery>(null, false, false) {
					@Override
					public void onSuccess(DatasetResultQuery res) {
						ListBoxWithButton<KeyValueItem> list = new ListBoxWithButton<KeyValueItem>();
						list.setLabel(f.getLabel());
						List keys = res.getResult().get(((ListFormField)f).getColumnKey().getColumnName());
						List values = res.getResult().get(((ListFormField)f).getColumnValue().getColumnName());
						
						List<KeyValueItem> resList = new ArrayList<KeyValueItem>();
						
						for(int i = 0 ; i < keys.size() ; i++) {
							KeyValueItem it = new KeyValueItem();
							it.setKey(String.valueOf(keys.get(i)));
							it.setValue(String.valueOf(values.get(i)));
							resList.add(it);
						}
						
						list.setList(resList, true);
						if(f.getValue() != null) {
							KeyValueItem it = new KeyValueItem();
							it.setKey(f.getValue());
							list.setSelectedObject(it);
						}
						
						panel.add(list);
						formWidgets.put(f, list);
 					}
				}.getAsyncCallback());

				break;
			case CHECKBOX:
				CheckBox ch = new CheckBox();
				ch.setText(f.getLabel());
				if(f.getValue() != null) {
					boolean val = Boolean.parseBoolean(f.getValue());
					if(!val) {
						if(f.getValue().equals("1")) {
							val = true;
						}
					}
					ch.setValue(val);
				}
				panel.add(ch);
				formWidgets.put(f, ch);
				break;
		}
	}
	
	@UiHandler("btnSave")
	public void onSave(ClickEvent event) {
		fillFields();
		if(editedLine != null) {
			CommonService.Connect.getInstance().updateFormValues(form, editedLine, new GwtCallbackWrapper<Void>(null, false, false) {
				@Override
				public void onSuccess(Void result) {
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.formSaved(), LabelsConstants.lblCnst.formSavedMsg());
				}		
			}.getAsyncCallback());
		}
		else {
			CommonService.Connect.getInstance().saveFormValues(form, new GwtCallbackWrapper<Void>(null, false, false) {
				@Override
				public void onSuccess(Void result) {
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.formSaved(), LabelsConstants.lblCnst.formSavedMsg());
				}		
			}.getAsyncCallback());
		}
	}

	private void fillFields() {
		for(FormField field : formWidgets.keySet()) {
			Widget w = formWidgets.get(field);
			if(field.getType().equals(TypeField.TEXTBOX)) {
				field.setValue(((LabelTextBox)w).getText());
			}
			else if(field.getType().equals(TypeField.LISTBOX)) {
				field.setValue(((ListBoxWithButton<KeyValueItem>)w).getSelectedObject().getKey());
			}
			else if(field.getType().equals(TypeField.CHECKBOX)) {
				field.setValue(String.valueOf(((CheckBox)w).getValue()));
			}
		}
	}

}
