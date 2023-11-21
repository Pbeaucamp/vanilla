package bpm.vanillahub.web.client.tabs.resources;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.resources.ResourcePanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanillahub.core.beans.resources.ServerMail;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.images.Images;
import bpm.vanillahub.web.client.properties.resources.ServerMailResourceProperties;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;

public class ServerMailPanel extends ResourcePanel<ServerMail> {

	public ServerMailPanel(IResourceManager resourceManager) {
		super(Labels.lblCnst.ServerMails(), Images.INSTANCE.action_b_24dp(), Labels.lblCnst.AddServerMail(), resourceManager);
	}

	@Override
	public void loadResources() {
		((ResourceManager) getResourceManager()).loadServerMails(this, this);
	}

	@Override
	protected String getDeleteConfirmMessage() {
		return Labels.lblCnst.DeleteServerMailConfirm();
	}

	@Override
	protected List<ColumnWrapper<ServerMail>> buildCustomColumns(TextCell cell, ListHandler<ServerMail> sortHandler) {
		Column<ServerMail, String> colSmtpHost = new Column<ServerMail, String>(cell) {

			@Override
			public String getValue(ServerMail object) {
				return object.getSmtpHostDisplay();
			}
		};
		colSmtpHost.setSortable(true);

		Column<ServerMail, String> colPort = new Column<ServerMail, String>(cell) {

			@Override
			public String getValue(ServerMail object) {
				return object.getPortDisplay();
			}
		};
		colPort.setSortable(true);

		Column<ServerMail, String> colIsSecured = new Column<ServerMail, String>(cell) {

			@Override
			public String getValue(ServerMail object) {
				return object.isSecured() ? Labels.lblCnst.ServerSecured() : Labels.lblCnst.ServerNotSecured();
			}
		};
		colIsSecured.setSortable(true);

		sortHandler.setComparator(colSmtpHost, new Comparator<ServerMail>() {

			@Override
			public int compare(ServerMail o1, ServerMail o2) {
				return o1.getSmtpHostDisplay().compareTo(o2.getSmtpHostDisplay());
			}
		});
		sortHandler.setComparator(colPort, new Comparator<ServerMail>() {

			@Override
			public int compare(ServerMail o1, ServerMail o2) {
				return o1.getPortDisplay().compareTo(o2.getPortDisplay());
			}
		});
		sortHandler.setComparator(colIsSecured, new Comparator<ServerMail>() {

			@Override
			public int compare(ServerMail o1, ServerMail o2) {
				return ((Boolean) o1.isSecured()).compareTo((Boolean) o2.isSecured());
			}
		});

		List<ColumnWrapper<ServerMail>> columns = new ArrayList<>();
		columns.add(new ColumnWrapper<ServerMail>(colSmtpHost, Labels.lblCnst.SmtpHost(), null));
		columns.add(new ColumnWrapper<ServerMail>(colPort, LabelsCommon.lblCnst.Port(), "150px"));
		columns.add(new ColumnWrapper<ServerMail>(colIsSecured, Labels.lblCnst.Security(), "150px"));
		return columns;
	}

	@Override
	protected PropertiesPanel<Resource> buildPropertiesPanel(Resource resource) {
		return new ServerMailResourceProperties(this, getResourceManager(), resource != null ? (ServerMail) resource : null);
	}

}
