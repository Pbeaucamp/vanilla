package bpm.architect.web.client.panels.fields;

import java.util.Arrays;
import java.util.List;

import bpm.architect.web.client.panels.FormFieldPanel;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.forms.FormField;
import bpm.vanilla.platform.core.beans.forms.ListFormField;
import bpm.vanilla.platform.core.beans.forms.FormField.TypeField;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ListboxPanel extends Composite implements FieldPanel {

	private static ListboxPanelUiBinder uiBinder = GWT.create(ListboxPanelUiBinder.class);

	interface ListboxPanelUiBinder extends UiBinder<Widget, ListboxPanel> {}

	@UiField
	LabelTextBox txtName, txtLabel, txtCol;
	
	@UiField
	CheckBox chkMandatory;
	
	@UiField
	ListBoxWithButton<TypeField> lstTypes;
	
	@UiField
	ListBoxWithButton<Datasource> lstDatasource;
	
	@UiField
	ListBoxWithButton<Dataset> lstDataset;
	
	@UiField
	ListBoxWithButton<DataColumn> lstKey;
	
	@UiField
	ListBoxWithButton<DataColumn> lstValue;
	
	private ListFormField field;
	
	public ListboxPanel(ListFormField f) {
		initWidget(uiBinder.createAndBindUi(this));
		this.field = f;
		
		CommonService.Connect.getInstance().getDatasources(new GwtCallbackWrapper<List<Datasource>>(null, false, false) {
			@Override
			public void onSuccess(List<Datasource> result) {
				lstDatasource.setList(result, true);
				
				if(field.getDatasource() != null) {
					lstDatasource.setSelectedObject(field.getDatasource());
					lstDataset.setList(lstDatasource.getSelectedObject().getDatasets(), true);
					
					if(field.getDataset() != null) {
						lstDataset.setSelectedObject(field.getDataset());
						lstKey.setList(lstDataset.getSelectedObject().getMetacolumns(), true);
						lstValue.setList(lstDataset.getSelectedObject().getMetacolumns(), true);
						
						if(field.getColumnKey() != null) {
							lstKey.setSelectedObject(field.getColumnKey());
						}
						if(field.getColumnValue() != null) {
							lstValue.setSelectedObject(field.getColumnValue());
						}
					}
				}
			}
		}.getAsyncCallback());
		
		lstDatasource.addChangeHandler(new ChangeHandler() {		
			@Override
			public void onChange(ChangeEvent event) {
				lstDataset.setList(lstDatasource.getSelectedObject().getDatasets(), true);
			}
		});
		
		lstDataset.addChangeHandler(new ChangeHandler() {		
			@Override
			public void onChange(ChangeEvent event) {
				lstKey.setList(lstDataset.getSelectedObject().getMetacolumns(), true);
				lstValue.setList(lstDataset.getSelectedObject().getMetacolumns(), true);
			}
		});
		
		txtName.setText(field.getName());
		txtLabel.setText(field.getLabel());
		txtCol.setText(field.getColumnName());
		chkMandatory.setValue(field.isMandatory());
		
		lstTypes.setList(Arrays.asList(TypeField.values()));
		lstTypes.setSelectedObject(field.getType());
		lstTypes.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				FormFieldPanel.getInstance().changeFieldType(ListboxPanel.this, lstTypes.getSelectedObject());
			}
		});
	}

	@Override
	public FormField getField() {
		field.setColumnName(txtCol.getText());
		field.setLabel(txtLabel.getText());
		field.setName(txtName.getText());
		field.setMandatory(chkMandatory.getValue());
		
		field.setDatasource(lstDatasource.getSelectedObject());
		field.setDataset(lstDataset.getSelectedObject());
		field.setColumnKey(lstKey.getSelectedObject());
		field.setColumnValue(lstValue.getSelectedObject());
		
		return field;
	}

	@UiHandler("imgDelete")
	public void onDelete(ClickEvent event) {
		FormFieldPanel.getInstance().deleteField(this);
	}
}
