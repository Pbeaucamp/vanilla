package bpm.es.sessionmanager.views;

import java.text.SimpleDateFormat;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import bpm.es.sessionmanager.api.SessionManager;
import bpm.es.sessionmanager.views.composite.VisualMappingComposite;

public class LogViewerMap {
	
	private SessionManager manager;
	//private int userId;
	
	//private FormToolkit toolkit;
	//private Section mapLogSection;
	//private Composite mapLogComposite;
	private Composite general;
	
	private VisualMappingComposite visualMap;
	
	private SimpleDateFormat fullFormat = new SimpleDateFormat("EEE, d MMM yyyy hh:mm aaa"); //$NON-NLS-1$
	
	private final LogViewer dad;
	
	public LogViewerMap(Composite parent, LogViewer daddy) {
		//this.toolkit = toolkit;
		this.dad = daddy;
		
		//mapLogSection = toolkit.createSection(parent, Section.TITLE_BAR| Section.TWISTIE|Section.EXPANDED);
//		mapLogComposite = new Composite(parent, SWT.NONE);
//		mapLogComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
//		mapLogComposite.setLayout(new GridLayout());
		//mapLogSection.setText("User Viewer");
		//mapLogSection.addExpansionListener(new ExpansionAdapter() {
		//	public void expansionStateChanged(ExpansionEvent e) {
//				if (e.getState()) {
//					System.out.println("user viewer expanded");
//					dad.showMap(true);
//					dad.showVanillaLogs(false);
//					dad.showRepositoryLogs(false);
//				}
		//	}
		//});
		
		createComposite(parent);
	}
	
	public Composite getMapComposite() {
		return general;
	}

	private void createComposite(Composite parent) {
		general = new Composite(parent, SWT.NO_SCROLL);
		general.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		general.setLayout(new GridLayout());
		
		Composite buttonBar = new Composite(general, SWT.NONE);
		buttonBar.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		buttonBar.setLayout(new GridLayout(5, false));
		
		visualMap = new VisualMappingComposite(general, SWT.NONE);
		visualMap.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		//mapLogSection.setClient(general);
	}
	
	public void showData(SessionManager manager, int userId) {
		//this.userId = userId;
		this.manager = manager;
		
		//userMapping.setInput(map.getUserMapping(), VanillaSqlModel.getVanillaUserTable(), null);
		//manager.getUsers();
		try {
			visualMap.setInput(this.manager);
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(general.getShell(), "Error", "Error opening visual " + //$NON-NLS-1$ //$NON-NLS-2$
					"viewer :" + e.getMessage()); //$NON-NLS-1$
		}
	}
	
//	public boolean isExpanded() {
//		return mapLogSection.isExpanded();	
//	}
//	
//	public void setExpanded(boolean show) {
//		if (show)
//			mapLogSection.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
//		else
//			mapLogSection.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
//		
//		mapLogSection.layout();
//		
//		mapLogSection.setExpanded(show);
//	}
}
