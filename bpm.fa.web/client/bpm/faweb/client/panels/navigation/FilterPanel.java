package bpm.faweb.client.panels.navigation;

import bpm.faweb.client.MainPanel;

public class FilterPanel extends AbstractRepositoryStackPanel {

	private FilterConfigPanel filterConfigPanel;
	
	public FilterPanel(MainPanel mainPanel) {
		super(mainPanel, TypeViewer.FILTERS_CONFIG);
		
		filterConfigPanel = new FilterConfigPanel(mainPanel);
		panelContent.setWidget(filterConfigPanel);
	}

	@Override
	public void refresh() {
		filterConfigPanel.reload();
	}
}
