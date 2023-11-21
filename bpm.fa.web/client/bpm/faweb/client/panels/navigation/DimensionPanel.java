package bpm.faweb.client.panels.navigation;

import java.util.List;

import com.google.gwt.user.client.ui.Tree;

import bpm.faweb.client.MainPanel;
import bpm.faweb.client.tree.DimensionTreePanel;
import bpm.faweb.client.tree.FaWebTreeItem;
import bpm.faweb.shared.infoscube.ItemOlapMember;

public class DimensionPanel extends AbstractRepositoryStackPanel {

	private DimensionTreePanel treePanel;
	
	public DimensionPanel(MainPanel mainPanel, String[] unames) {
		super(mainPanel, TypeViewer.DIMENSION);
		
		treePanel = new DimensionTreePanel(mainPanel, this, unames);
		panelContent.setWidget(treePanel);
	}

	@Override
	public void refresh() {
		treePanel.refresh();
	}

	public List<FaWebTreeItem> getExtremite() {
		return treePanel.getExtremite();
	}

	public FaWebTreeItem findRootItem(String currMb) {
		return treePanel.findRootItem(currMb);
	}

	public void addNext(List<ItemOlapMember> newmembers, FaWebTreeItem theone) {
		treePanel.addNext(newmembers, theone);
	}

	public Tree getTree() {
		return treePanel.getTreeDim();
	}

	public FaWebTreeItem findRootItem2(String filter) {
		return treePanel.findRootItem2(filter);
	}
}
