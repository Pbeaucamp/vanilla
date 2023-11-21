package bpm.metadata.web.client.panels.properties;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.FmdtServices;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.shared.fmdt.metadata.Metadata;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataModel;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataPackage;
import bpm.metadata.web.client.panels.TableSelectionPanel;
import bpm.vanilla.platform.core.beans.data.DatabaseTable;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.resources.D4C;

public class PackageProperties extends Composite implements IPanelProperties {

	private static PackagePropertiesUiBinder uiBinder = GWT.create(PackagePropertiesUiBinder.class);

	interface PackagePropertiesUiBinder extends UiBinder<Widget, PackageProperties> {
	}
	
	@UiField
	LabelTextBox txtName;
	
	@UiField
	SimplePanel panelTables;

	private IWait waitPanel;
	private Datasource datasource;
	private MetadataModel model;
	private MetadataPackage pack;
	
	private TableSelectionPanel tableSelectionPanel;
	
	public PackageProperties(IWait waitPanel, Metadata metadata, Datasource datasource, MetadataModel model, MetadataPackage pack) {
		initWidget(uiBinder.createAndBindUi(this));
		this.waitPanel = waitPanel;
		this.datasource = datasource;
		this.model = model;
		this.pack = pack;
		
		txtName.setText(pack.getName());
		
		this.tableSelectionPanel = new TableSelectionPanel(null);
		panelTables.setWidget(tableSelectionPanel);
		
		buildPhysicalTree(metadata, datasource);
		buildLogicalTree(pack.getTables());
	}

	public void buildPhysicalTree(Metadata metadata, Datasource datasource) {
		if (datasource != null) {
			D4C d4cServer = metadata.getD4cServer();
			String d4cOrganisation = metadata.getD4cOrganisation();
			
			if (d4cServer != null) {
				FmdtServices.Connect.getInstance().getDatabaseStructure(d4cServer, d4cOrganisation, datasource, false, new GwtCallbackWrapper<List<DatabaseTable>>(waitPanel, true, true) {

					@Override
					public void onSuccess(List<DatabaseTable> result) {
						tableSelectionPanel.buildPhysicalTree(result);
					}
				}.getAsyncCallback());
			}
			else {
				FmdtServices.Connect.getInstance().getDatabaseStructure(datasource, false, new GwtCallbackWrapper<List<DatabaseTable>>(waitPanel, true, true) {

					@Override
					public void onSuccess(List<DatabaseTable> result) {
						tableSelectionPanel.buildPhysicalTree(result);
					}
				}.getAsyncCallback());
			}
		}
	}

	private void buildLogicalTree(List<DatabaseTable> logicalTables) {
		tableSelectionPanel.buildLogicalTree(logicalTables);
	}

	@Override
	public boolean isValid() {
		String packName = txtName.getText();
		return packName != null && !packName.isEmpty() && tableSelectionPanel.isComplete();
	}

	@Override
	public void apply() {
		String packName = txtName.getText();
		pack.setName(packName);
		
		List<DatabaseTable> logicalTables = tableSelectionPanel.getLogicalTables();
		datasource.setTables(logicalTables);
//		model.setTables(logicalTables);
		pack.setTables(logicalTables);
	}

}
