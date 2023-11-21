package bpm.gwt.commons.client.viewer;

import java.util.List;

import bpm.gwt.commons.client.panels.FormDisplayPanel;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.vanilla.platform.core.beans.Group;

public class FormVanillaViewer extends Viewer {

	public FormVanillaViewer(VanillaViewer vanillaViewer, PortailRepositoryItem portailItem, Group selectedGroup, List<Group> availableGroups) {
		super(vanillaViewer);
		
		vanillaViewer.launchReport(this, portailItem, selectedGroup, false, false);
	}

	@Override
	public void defineToolbar(LaunchReportInformations itemInfo) {}

	private void launchItem() {
		runItem(null);
	}

	@Override
	public void runItem(LaunchReportInformations itemInfo) {
		defineToolbar(null);
		reportFrame.removeFromParent();
		reportPanel.add(new FormDisplayPanel(itemInfo.getItem().getItem(), true));
	}
}
