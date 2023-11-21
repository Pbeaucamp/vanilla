package bpm.gwt.commons.client.meta;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.vanilla.platform.core.beans.meta.MetaForm;

public class MetaFormsPanel extends Composite {

	private static MetasPanelUiBinder uiBinder = GWT.create(MetasPanelUiBinder.class);

	interface MetasPanelUiBinder extends UiBinder<Widget, MetaFormsPanel> {
	}
	
	@UiField
	ListBoxWithButton<MetaForm> lstForms;
	
	private IWait waitPanel;

	public MetaFormsPanel(IWait waitPanel) {
		initWidget(uiBinder.createAndBindUi(this));
		this.waitPanel = waitPanel;
		
		loadForms();
	}
	
	private void loadForms() {
		CommonService.Connect.getInstance().getMetaForms(new GwtCallbackWrapper<List<MetaForm>>(waitPanel, true, true) {

			@Override
			public void onSuccess(List<MetaForm> result) {
				loadForms(result);
			}
		}.getAsyncCallback());
	}

	public void loadForms(List<MetaForm> forms) {
		if (forms == null) {
			forms = new ArrayList<MetaForm>();
		}
		for (MetaForm form : forms) {
			lstForms.addItem(form.getName(), form);
		}
	}
	
	public boolean isFormSelected() {
		return getSelectedForm() != null;
	}
	
	public MetaForm getSelectedForm() {
		return lstForms.getSelectedObject();
	}
}
