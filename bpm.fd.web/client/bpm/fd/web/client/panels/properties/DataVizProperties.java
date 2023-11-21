package bpm.fd.web.client.panels.properties;

import java.util.List;

import bpm.data.viz.core.preparation.DataPreparation;
import bpm.fd.core.IComponentOption;
import bpm.fd.core.component.DataVizComponent;
import bpm.fd.web.client.services.DashboardService;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class DataVizProperties extends CompositeProperties<IComponentOption> {

	private static DataVizPropertiesUiBinder uiBinder = GWT.create(DataVizPropertiesUiBinder.class);

	interface DataVizPropertiesUiBinder extends UiBinder<Widget, DataVizProperties> {}

	@UiField
	ListBoxWithButton<DataPreparation> lstDataPreparation;
	private DataVizComponent componentDataViz;
	
	
	public DataVizProperties(DataVizComponent component) {
		initWidget(uiBinder.createAndBindUi(this));
		this.componentDataViz = component;
		
		DashboardService.Connect.getInstance().getDataPreparations(new GwtCallbackWrapper<List<DataPreparation>>(null, false, false) {
			@Override
			public void onSuccess(List<DataPreparation> result) {
				lstDataPreparation.setList(result, true);
				if(componentDataViz.getDatavizId() != null && componentDataViz.getDatavizId() > 0) {
					for(DataPreparation d : result) {
						if(d.getId() == componentDataViz.getDatavizId()) {
							lstDataPreparation.setSelectedObject(d);
							break;
						}
					}
				}
			}
		}.getAsyncCallback());
		
		lstDataPreparation.addChangeHandler(new ChangeHandler() {		
			@Override
			public void onChange(ChangeEvent event) {
				componentDataViz.setDataviz(lstDataPreparation.getSelectedObject());
			}
		});
	}


	@Override
	public void buildProperties(IComponentOption component) {
		
		
	}

}
