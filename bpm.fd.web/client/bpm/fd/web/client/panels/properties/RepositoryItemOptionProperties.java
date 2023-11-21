package bpm.fd.web.client.panels.properties;

import java.util.ArrayList;
import java.util.List;

import bpm.fd.core.ComponentParameter;
import bpm.fd.core.DashboardComponent;
import bpm.fd.core.IComponentRepositoryItem;
import bpm.fd.web.client.services.DashboardService;
import bpm.gwt.commons.client.custom.CustomButton;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.dialog.RepositoryDialog;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.vanilla.platform.core.repository.Parameter;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class RepositoryItemOptionProperties extends CompositeProperties<IComponentRepositoryItem> {

	private static RepositoryItemOptionPropertiesUiBinder uiBinder = GWT.create(RepositoryItemOptionPropertiesUiBinder.class);

	interface RepositoryItemOptionPropertiesUiBinder extends UiBinder<Widget, RepositoryItemOptionProperties> {
	}

	@UiField
	LabelTextBox itemName;

	@UiField
	CustomButton btnBrowse;

	private IComponentRepositoryItem component;
	private RepositoryItem item;

	private PropertiesPanel propertiesPanel;

	public RepositoryItemOptionProperties(IComponentRepositoryItem comp, PropertiesPanel propertiesP) {
		initWidget(uiBinder.createAndBindUi(this));
		this.component = comp;
		itemName.setEnabled(false);
		this.propertiesPanel = propertiesP;

		if (component.getItemId() > 0) {
			CommonService.Connect.getInstance().getItemById(component.getItemId(), new GwtCallbackWrapper<RepositoryItem>(null, false) {
				@Override
				public void onSuccess(RepositoryItem result) {
					item = result;
					itemName.setText(item.getName());
					propertiesPanel.loadParameters((DashboardComponent) component, ((DashboardComponent) component).getParameters());
				}

			}.getAsyncCallback());
		}
	}

	@Override
	public void buildProperties(IComponentRepositoryItem component) {
		if (item != null) {
			component.setItemId(item.getId());
		}
	}

	@UiHandler("btnBrowse")
	public void onBrowse(ClickEvent event) {
		final RepositoryDialog dialog = new RepositoryDialog(component.getItemType());
		dialog.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dialog.isConfirm()) {
					item = dialog.getSelectedItem();

					DashboardService.Connect.getInstance().getRepositoryItemParameters(item.getId(), new GwtCallbackWrapper<List<Parameter>>(null, true) {
						@Override
						public void onSuccess(List<Parameter> result) {
							List<ComponentParameter> params = new ArrayList<ComponentParameter>();
							int i = 0;
							for (Parameter res : result) {
								ComponentParameter p = new ComponentParameter();
								p.setName(res.getName());
								p.setIndex(i);

								params.add(p);
								i++;
							}
							
							component.setItemId(item.getId());

							propertiesPanel.loadParameters((DashboardComponent) component, params);
							propertiesPanel.refreshOptions((DashboardComponent) component);
						}
					}.getAsyncCallback());

					itemName.setText(item.getName());
				}
			}
		});
		dialog.center();
	}

}
