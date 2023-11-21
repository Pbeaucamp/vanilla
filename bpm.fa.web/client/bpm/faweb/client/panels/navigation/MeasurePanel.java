package bpm.faweb.client.panels.navigation;

import com.google.gwt.user.client.ui.Tree;

import bpm.faweb.client.MainPanel;
import bpm.faweb.client.tree.MeasureTreePanel;

public class MeasurePanel extends AbstractRepositoryStackPanel {

	private MeasureTreePanel treePanel;
	
	public MeasurePanel(MainPanel mainPanel, String[] unames) {
		super(mainPanel, TypeViewer.MEASURE);
		
		treePanel = new MeasureTreePanel(mainPanel, unames);
		panelContent.setWidget(treePanel);
	}

	@Override
	public void refresh() {
		treePanel.refresh();
	}

	public Tree getTree() {
		return treePanel.getTreeMes();
	}
}
