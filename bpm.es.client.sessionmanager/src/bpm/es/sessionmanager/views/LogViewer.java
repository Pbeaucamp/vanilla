package bpm.es.sessionmanager.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

import bpm.es.sessionmanager.Messages;
import bpm.es.sessionmanager.api.SessionManager;

public class LogViewer {

	public static int showingNow = 0; //0, map
	
	public static int showingMap = 0;
	public static int showingRepository = 0;
	public static int showingVanilla = 0;
	
	public static final int ORDER_BY_DAY = 0;
	public static final int ORDER_BY_ID = 1;
	public static final int ORDER_BY_APP = 2;

	private LogViewerVanilla vanillaViewer;
	private LogViewerStats statsViewer;
	
	public LogViewer(FormToolkit toolkit, Composite parent) {

		TabFolder tabFolder = new TabFolder(parent, SWT.NONE);
		
		tabFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		tabFolder.setLayout(new GridLayout());
	    
	    //vanilla
	    TabItem tabVanilla = new TabItem(tabFolder, SWT.NULL);
	    tabVanilla.setText(Messages.LogViewer_2);		
		vanillaViewer = new LogViewerVanilla(tabFolder);
		tabVanilla.setControl(vanillaViewer.getComposite());
		
	    //stats
	    TabItem tabStats = new TabItem(tabFolder, SWT.NULL);
	    tabStats.setText(Messages.LogViewer_3);		
		statsViewer = new LogViewerStats(toolkit, tabFolder, this);
		tabStats.setControl(statsViewer.getComposite());
		
		tabFolder.setSelection(tabVanilla);
	}
	
	public void showData(SessionManager manager, int userId) throws Exception {
		vanillaViewer.showData(manager);
		statsViewer.showData(manager, userId);
	}

}
