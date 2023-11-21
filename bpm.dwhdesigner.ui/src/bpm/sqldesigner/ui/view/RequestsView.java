package bpm.sqldesigner.ui.view;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.ui.part.ViewPart;

import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.ui.view.tab.TabRequests;

public class RequestsView extends ViewPart{

	public static final String ID = "bpm.sqldesigner.ui.requestsview"; //$NON-NLS-1$
	private TabFolder tabFolder;
	private HashMap<DatabaseCluster, TabRequests> tabs = new HashMap<DatabaseCluster, TabRequests>();

	@Override
	public void createPartControl(Composite parent) {

		tabFolder = new TabFolder(parent, SWT.TOP);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

	}

	public TabRequests addTab(DatabaseCluster cluster) {
		TabRequests tab = new TabRequests(tabFolder, SWT.NONE, cluster);
		tabs.put(cluster, tab);
		return tab;
	}
	
	public TabRequests getTab(DatabaseCluster cluster) {
		return tabs.get(cluster);
	}

	@Override
	public void setFocus() {

	}

	public void removeTab(DatabaseCluster databaseCluster) {
		tabs.get(databaseCluster).closeTab();
		
	}

	public void clear() {
		for (DatabaseCluster db : tabs.keySet()){
			TabRequests tab = tabs.get(db);
			tab.closeTab();
		}
		tabs.clear();
	}

}
