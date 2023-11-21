package bpm.gwt.aklabox.commons.client.utils;

import bpm.document.management.core.model.Enterprise;
import bpm.gwt.aklabox.commons.client.customs.TreeItemPanel;
import bpm.gwt.aklabox.commons.client.customs.TreeItemPanel.ItemSize;
import bpm.gwt.aklabox.commons.client.images.CommonImages;
import bpm.gwt.aklabox.commons.client.tree.CustomTreeItem;

public class EnterpriseTreeItem extends CustomTreeItem {

	private Enterprise enterprise;

	public EnterpriseTreeItem(Enterprise enterprise, boolean displayCustomLogo) {
		this.enterprise = enterprise;

		TreeItemPanel treeItemPanel = null;
		if (displayCustomLogo && enterprise.getLogo() != null && !enterprise.getLogo().isEmpty()) {
			String logoPath = PathHelper.getRightPath(enterprise.getLogo());
			treeItemPanel = new TreeItemPanel(logoPath, enterprise.getEnterpriseName(), ItemSize.BIG);
		}
		else {
			treeItemPanel = new TreeItemPanel(CommonImages.INSTANCE.ic_enterprise(), enterprise.getEnterpriseName(), ItemSize.BIG);
		}
		setWidget(treeItemPanel);
	}

	public Enterprise getEnterprise() {
		return enterprise;
	}
}
