package bpm.es.dndserver.test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class LogViewer {

	public static int showingNow = 0; //0, map
	
	public static int showingMap = 0;
	public static int showingRepository = 0;
	public static int showingVanilla = 0;
	
	public static final int ORDER_BY_DAY = 0;
	public static final int ORDER_BY_ID = 1;
	public static final int ORDER_BY_APP = 2;

	public LogViewer(FormToolkit toolkit, Composite parent) {

		TabFolder tabFolder = new TabFolder(parent, SWT.NONE);
		
		//tabFolder.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TREE_BORDER);

		//toolkit.paintBordersFor(tabFolder);
		
		tabFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		tabFolder.setLayout(new GridLayout());
		
//		tabFolder.setSimple(true);
//		
//		tabFolder.setSelectionBackground(new Color[] {
//				Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW),
//				Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW),
//				Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW) }, 
//				new int[] { 50, 100 });
//		
//		//tabFolder.setBackground(ColorConstants.white);
//		
//		tabFolder.setUnselectedImageVisible(false);
//		tabFolder.setUnselectedCloseVisible(false);
//		tabFolder.setMinimizeVisible(false);
//		tabFolder.setMaximizeVisible(false);
		
//		//user
//	    TabItem tabMap = new TabItem(tabFolder, SWT.NULL);
//	    tabMap.setText("User viewer");
//		mapViewer = new LogViewerMap(tabFolder, this);
//		tabMap.setControl(mapViewer.getMapComposite());
//		
//		//repository
//		TabItem tabRepository = new TabItem(tabFolder, SWT.NULL);
//	    tabRepository.setText("Repository Log Viewer");
//	    repositoryViewer = new LogViewerRepository(tabFolder, this);
//	    tabRepository.setControl(repositoryViewer.getComposite());
//	    
//	    //vanilla
//	    TabItem tabVanilla = new TabItem(tabFolder, SWT.NULL);
//	    tabVanilla.setText("Vanilla Log Viewer");		
//		vanillaViewer = new LogViewerVanilla(tabFolder, this);
//		tabVanilla.setControl(vanillaViewer.getComposite());
//		
//	    //stats
//	    TabItem tabStats = new TabItem(tabFolder, SWT.NULL);
//	    tabStats.setText("Statistics and charts");		
//		statsViewer = new LogViewerStats(toolkit, tabFolder, this);
//		tabStats.setControl(statsViewer.getComposite());
		
//		tabFolder.setSelection(tabMap);
	}
	
	public void showData() {
		
	}
}
