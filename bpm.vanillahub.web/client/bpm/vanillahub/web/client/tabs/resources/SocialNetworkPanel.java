package bpm.vanillahub.web.client.tabs.resources;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.resources.ResourcePanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanillahub.core.beans.resources.SocialNetworkServer;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.images.Images;
import bpm.vanillahub.web.client.properties.resources.SocialNetworkServerProperties;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;

public class SocialNetworkPanel extends ResourcePanel<SocialNetworkServer> {

	public SocialNetworkPanel(IResourceManager resourceManager) {
		super(Labels.lblCnst.SocialServers(), Images.INSTANCE.action_b_24dp(), Labels.lblCnst.AddServerSocial(), resourceManager);
	}

	@Override
	public void loadResources() {
		((ResourceManager) getResourceManager()).loadSocialServers(this, this);
	}

	@Override
	protected String getDeleteConfirmMessage() {
		return Labels.lblCnst.DeleteSocialServerConfirm();
	}

	@Override
	protected List<ColumnWrapper<SocialNetworkServer>> buildCustomColumns(TextCell cell, ListHandler<SocialNetworkServer> sortHandler) {
		Column<SocialNetworkServer, String> colToken = new Column<SocialNetworkServer, String>(cell) {

			@Override
			public String getValue(SocialNetworkServer object) {
				return object.getTokenDisplay();
			}
		};
		colToken.setSortable(true);

		sortHandler.setComparator(colToken, new Comparator<SocialNetworkServer>() {

			@Override
			public int compare(SocialNetworkServer o1, SocialNetworkServer o2) {
				return o1.getTokenDisplay().compareTo(o2.getTokenDisplay());
			}
		});

		List<ColumnWrapper<SocialNetworkServer>> columns = new ArrayList<>();
		columns.add(new ColumnWrapper<SocialNetworkServer>(colToken, Labels.lblCnst.Token(), null));
		return columns;
	}

	@Override
	protected PropertiesPanel<Resource> buildPropertiesPanel(Resource resource) {
		return new SocialNetworkServerProperties(this, getResourceManager(), resource != null ? (SocialNetworkServer) resource : null);
	}

}
