package groupviewer.views;

import groupviewer.generator.InpactFlexGenerator;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import adminbirep.Activator;
import bpm.birep.admin.client.trees.TreeDatasource;
import bpm.birep.admin.client.trees.TreeItem;
import bpm.vanilla.platform.core.repository.DataSource;
import bpm.vanilla.platform.core.repository.DataSourceImpact;
import bpm.vanilla.platform.core.repository.RepositoryItem;


public class CrossRefView extends ViewPart implements ISelectionListener {
	public static final String ID="groupviewer.views.CrossRefView";
	private Action refresh;
	private Browser browse;
	protected Button showDependantItem, showRequestedItems;
	protected int behavior = 0;
	protected RepositoryItem selectedItem; 
	private InpactFlexGenerator generator;
	private List<RepositoryItem> model;
	
	private static final int SHOW_DEPENDANT = 0;
	private static final int SHOW_REQUESTED = 1;
	

	public CrossRefView() {
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().addSelectionListener(this);
	}
	
	@Override
	public void dispose() {
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().removeSelectionListener(this);
		super.dispose();
	}
	
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());	
		Composite behavior = new Composite(parent, SWT.NONE);
		behavior.setLayout(new GridLayout(2, true));
		behavior.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		SelectionListener behaviorListener = new SelectionAdapter(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if (showDependantItem.getSelection()){
					CrossRefView.this.behavior = SHOW_DEPENDANT;
				}
				else{
					CrossRefView.this.behavior = SHOW_REQUESTED;
				}
				
				if (selectedItem == null){
					return;
				}
				createModel(selectedItem, CrossRefView.this.behavior);
			}
		};
		
		showDependantItem = new Button(behavior, SWT.RADIO);
		showDependantItem.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		showDependantItem.setText("Show Dependant Items");
		showDependantItem.setSelection(true);
		showDependantItem.addSelectionListener(behaviorListener);
		
		showRequestedItems = new Button(behavior, SWT.RADIO);
		showRequestedItems.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		showRequestedItems.setText("Show Requested Items");
		showRequestedItems.addSelectionListener(behaviorListener);
		
		browse = new Browser(behavior,  SWT.NONE);
		browse.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true,2,1));
		//fill();
		//ExportModel mod = ExportModel.getInstance();
		//browse.setUrl();
		createActions();
		createToolbar();
	}

	@Override
	public void setFocus() {
		browse.setFocus();
	}
	
	private void createActions(){
		this.refresh = new Action("Refresh", getImageDescriptor("refresh.png")){
			@Override
			public void run() {
				doRefresh();
			}
			
		};
	}	
	private void createToolbar() {
        IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
        mgr.add(this.refresh);
	}
	
	protected void createModel(RepositoryItem dirIt, int behavior){
		switch(behavior){
		case SHOW_DEPENDANT:
			try {
				model = Activator.getDefault().getRepositoryApi().getRepositoryService().getDependantItems(dirIt);
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
			break;
		case SHOW_REQUESTED:
			try {
				model = Activator.getDefault().getRepositoryApi().getRepositoryService().getNeededItems(dirIt.getId());
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
			break;
		}
		fill();
		
	}
	private void createModel(DataSource ds){	
		try {
			List<RepositoryItem> l = new ArrayList<RepositoryItem>();

			for (DataSourceImpact dsi : Activator.getDefault().getRepositoryApi().getImpactDetectionService().getForDataSourceId(ds.getId())) {
				RepositoryItem it = Activator.getDefault().getRepositoryApi().getRepositoryService().getDirectoryItem(dsi.getDirectoryItemId());

				if (it != null) {

					if (((RepositoryItem) it).isVisible()) {
						l.add((RepositoryItem) it);
					}
				}

			}
			model = l;
			fill();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private ImageDescriptor getImageDescriptor(String name){
        String iconPath = "/icons/";
        ImageDescriptor desc;
        try { 
       	 URL url = getClass().getResource(iconPath + name);
       	 desc = ImageDescriptor.createFromURL(url);
		} catch (Exception e) {
			desc = ImageDescriptor.getMissingImageDescriptor();
		}   
		return desc;
	}
	
	private void doRefresh() {
		String current = browse.getUrl();
		browse.setUrl(current);
		//fill();
		//ExportModel mod = ExportModel.getInstance();
		//browse.setUrl(mod.getLaunchURL().toString());
	}
	
	private void fill(){
		
		Runnable tfill = new Runnable(){
			public void run() {
				//CrossRefDataLoader loader = new CrossRefDataLoader();
				//if (loader.getErrorCode() == 0){
				//	InpactFlexGenerator  generator = new InpactFlexGenerator(loader);
				//	generator.generate();
				//}
				//generator.flush();
				generator = new InpactFlexGenerator();
				if (behavior == SHOW_DEPENDANT){
					generator.generateDependItem(selectedItem, model);
					browse.setUrl(generator.generate().toString());
				} else if (behavior == SHOW_REQUESTED){
					generator.generateNeededItem(selectedItem, model);
					browse.setUrl(generator.generate().toString());
				} else if (behavior == 0){
					browse.setUrl("");
				} 
			}
			
		};
		org.eclipse.swt.custom.BusyIndicator.showWhile(Display.getCurrent(), tfill);
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection.isEmpty()){
			return;
		}
		
		//boolean timed = false;
		
		IStructuredSelection ss = (IStructuredSelection)selection;
		Object o = ss.getFirstElement();

		if (o instanceof TreeItem){
			selectedItem = ((TreeItem)o).getItem(); 
			createModel(selectedItem, behavior);
		}
		else if (o instanceof TreeDatasource){
			createModel(((TreeDatasource)o).getDataSource());
		}
	}
}
