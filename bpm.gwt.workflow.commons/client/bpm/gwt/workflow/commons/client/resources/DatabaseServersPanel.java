package bpm.gwt.workflow.commons.client.resources;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.images.Images;
import bpm.gwt.workflow.commons.client.resources.properties.DatasourceResourceProperties;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.vanilla.platform.core.beans.resources.DatabaseServer;
import bpm.vanilla.platform.core.beans.resources.Resource;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;

public class DatabaseServersPanel extends ResourcePanel<DatabaseServer> {

	public DatabaseServersPanel(IResourceManager resourceManager) {
		super(LabelsCommon.lblCnst.DatabaseServers(), Images.INSTANCE.action_b_24dp(), LabelsCommon.lblCnst.AddDatabaseServer(), resourceManager);
	}

	@Override
	public void loadResources() {
		getResourceManager().loadDatabaseServers(this, this);
	}

	@Override
	protected String getDeleteConfirmMessage() {
		return LabelsCommon.lblCnst.DeleteDatabaseServerConfirm();
	}

	@Override
	protected List<ColumnWrapper<DatabaseServer>> buildCustomColumns(TextCell cell, ListHandler<DatabaseServer> sortHandler) {
		Column<DatabaseServer, String> colDriver = new Column<DatabaseServer, String>(cell) {

			@Override
			public String getValue(DatabaseServer object) {
				return object.getDriverJdbc();
			}
		};
		colDriver.setSortable(true);

		Column<DatabaseServer, String> colDatabaseUrl = new Column<DatabaseServer, String>(cell) {

			@Override
			public String getValue(DatabaseServer object) {
				return object.getDatabaseUrlDisplay();
			}
		};
		colDatabaseUrl.setSortable(true);

		Column<DatabaseServer, String> colLogin = new Column<DatabaseServer, String>(cell) {

			@Override
			public String getValue(DatabaseServer object) {
				return object.getLogin();
			}
		};
		colLogin.setSortable(true);

		sortHandler.setComparator(colDriver, new Comparator<DatabaseServer>() {

			@Override
			public int compare(DatabaseServer o1, DatabaseServer o2) {
				return o1.getDriverJdbc().compareTo(o2.getDriverJdbc());
			}
		});
		sortHandler.setComparator(colDatabaseUrl, new Comparator<DatabaseServer>() {

			@Override
			public int compare(DatabaseServer o1, DatabaseServer o2) {
				return o1.getDatabaseUrlDisplay().compareTo(o2.getDatabaseUrlDisplay());
			}
		});
		sortHandler.setComparator(colLogin, new Comparator<DatabaseServer>() {

			@Override
			public int compare(DatabaseServer o1, DatabaseServer o2) {
				return o1.getLogin().compareTo(o2.getLogin());
			}
		});

		List<ColumnWrapper<DatabaseServer>> columns = new ArrayList<>();
		columns.add(new ColumnWrapper<DatabaseServer>(colDriver, LabelsCommon.lblCnst.DriverJdbc(), "150px"));
		columns.add(new ColumnWrapper<DatabaseServer>(colDatabaseUrl, LabelsCommon.lblCnst.DatabaseUrl(), null));
		columns.add(new ColumnWrapper<DatabaseServer>(colLogin, LabelsCommon.lblCnst.Login(), "150px"));
		return columns;
	}

	@Override
	protected PropertiesPanel<Resource> buildPropertiesPanel(Resource resource) {
		return new DatasourceResourceProperties(this, getResourceManager(), resource != null ? (DatabaseServer) resource : null);
	}

}
