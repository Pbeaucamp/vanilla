package bpm.architect.web.client.panels;

import java.util.ArrayList;
import java.util.List;

import bpm.architect.web.client.I18N.Labels;
import bpm.architect.web.client.dialogs.FormDialog;
import bpm.gwt.commons.client.datasource.DatasourceDatasetManager;
import bpm.gwt.commons.client.dialog.RepositorySaveDialog;
import bpm.gwt.commons.client.dialog.RepositorySaveDialog.SaveHandler;
import bpm.gwt.commons.client.panels.CollapsePanel;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.repository.SaveItemInformations;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.DatasourceJdbc;
import bpm.vanilla.platform.core.beans.forms.Form;
import bpm.vanilla.platform.core.beans.forms.FormField;
import bpm.vanilla.platform.core.beans.forms.FormField.TypeField;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class FormDesignPanel extends Tab {

	private static FormDesignPanelUiBinder uiBinder = GWT.create(FormDesignPanelUiBinder.class);

	interface FormDesignPanelUiBinder extends UiBinder<Widget, FormDesignPanel> {}

	interface MyStyle extends CssResource {
		String mainPanel();
	}
	
	@UiField
	MyStyle style;
	
	private InfoUser infoUser;
	
	private TabManager tabManager;
	
	@UiField(provided=true)
	CollapsePanel collapsePanel;
	
	private LeftPanel leftPanel;
	private FormFieldPanel fieldPanel;

	private Form form;
	
	public FormDesignPanel(InfoUser infoUser, TabManager tabManager, Form form) {
		
		super(tabManager, Labels.lblCnst.Form(), true);
		this.form = form;
		
		collapsePanel = new CollapsePanel(300, 50);
		this.leftPanel = new LeftPanel(collapsePanel);
		collapsePanel.setLeftPanel(leftPanel);
		
		fieldPanel = new FormFieldPanel(form);
		collapsePanel.setCenterPanel(fieldPanel);
		
		this.add(uiBinder.createAndBindUi(this));
		this.addStyleName(style.mainPanel());
		this.infoUser = infoUser;
		
		this.tabManager = tabManager;
		

	}
	
	@UiHandler("btnSave")
	public void onSave(ClickEvent event) {
		List<FormField> fields = fieldPanel.getFields();
		form.setFields(fields);
		
		List<Group> availableGroups = infoUser.getAvailableGroups();
		
		SaveHandler saveHandler = new SaveHandler() {
			@Override
			public void saveItem(SaveItemInformations itemInfos, boolean close) {
				CommonService.Connect.getInstance().saveItem(itemInfos, new GwtCallbackWrapper<RepositoryItem>(FormDesignPanel.this, true, true) {
					@Override
					public void onSuccess(RepositoryItem result) {
						// TODO Auto-generated method stub
						
					}
					
				}.getAsyncCallback());
			}
		};
		
		RepositorySaveDialog<Form> dial = new RepositorySaveDialog<Form>(saveHandler, form, IRepositoryApi.FORM, form.getName(), form.getDescription(), availableGroups, true);
		dial.center();
	}
	
	@UiHandler("btnProps")
	public void onProps(ClickEvent event) {
		final FormDialog dial = new FormDialog(form);
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if(dial.isConfirm()) {
					CommonService.Connect.getInstance().getDataSetMetaData((DatasourceJdbc)form.getDatasource().getObject(), "Select * from " + form.getTableName(), new GwtCallbackWrapper<ArrayList<DataColumn>>(null, false) {
						@Override
						public void onSuccess(ArrayList<DataColumn> result) {
							List<FormField> fields = new ArrayList<FormField>();
							for(DataColumn col : result) {
								FormField field = new FormField(TypeField.TEXTBOX);
								field.setColumnName(col.getColumnName());
								field.setLabel(col.getColumnLabel());
								field.setName(col.getColumnLabel());
								field.setLeft(true);
								fields.add(field);
							}
							form.setFields(fields);
							fieldPanel = new FormFieldPanel(form);
							collapsePanel.setCenterPanel(fieldPanel);
						}						
					}.getAsyncCallback());
				}
			}
		});
		dial.center();
	}
	@UiHandler("btnDatasource")
	public void onDatasource(ClickEvent event) {
		DatasourceDatasetManager dial = new DatasourceDatasetManager(infoUser.getUser());
		dial.center();
	}
	
}
