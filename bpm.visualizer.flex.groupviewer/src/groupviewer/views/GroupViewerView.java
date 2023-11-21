package groupviewer.views;

import groupviewer.GroupDataLoader;
import groupviewer.factory.GroupViewFactory;
import groupviewer.generator.GroupFlexGenerator;
import groupviewer.models.ContentModel;
import groupviewer.parts.ContentPart;

import java.net.URL;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import bpm.vanilla.platform.core.beans.Group;

public class GroupViewerView extends ViewPart {

	public static final String DATA_PROP = "Data change";
	public static final String ID = "groupviewer.views.GroupViewerView";
	private ScrollingGraphicalViewer viewer;
	private EditDomain domain = new EditDomain();
	private GroupViewFactory factory = new GroupViewFactory();
	//private Action export , refresh;
	private Action refresh;
	private Browser browse;
	private CTabItem itemGef,itemFlex;
	private CTabFolder folder;
	
	/**
	 * The constructor.
	 */
	public GroupViewerView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {	
		// TabFolder
		folder = new CTabFolder(parent, SWT.BOTTOM);
		folder.setSimple(true);
		folder.setLayout(new GridLayout());
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));

		// GEF
		itemGef = new CTabItem(folder, SWT.NONE);
		itemGef.setText("GEF");
		// Viewer
		viewer = new ScrollingGraphicalViewer();
		viewer.createControl(folder);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.getControl().setBackground(ColorConstants.white);
		domain.addViewer(viewer);
		viewer.setRootEditPart(new ScalableRootEditPart());
		viewer.setEditPartFactory(factory);
		viewer.setContents(new ContentModel());
		// add
		itemGef.setControl(viewer.getControl());
		
		
		//FLEX
		itemFlex = new CTabItem(folder, SWT.NONE);
		itemFlex.setText("Flex");
		browse = new Browser(folder, SWT.NONE);
		//refreshBrowserURL();
		// add
		itemFlex.setControl(browse);
		
		// INIT
		createActions();
		createToolbar();
		fill();
		folder.setSelection(itemGef);
	}
	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
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
	
	private void createActions(){
		/*this.export = new Action("Export to FLEX", getImageDescriptor("flex-ico.png")){
			@Override
			public void run() {
				doExport();	
			}
		};*/
		
		this.refresh = new Action("Refresh", getImageDescriptor("refresh.png")){
			@Override
			public void run() {
				doRefresh();
			}
		};
	}
	private void createToolbar() {
        IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
        //mgr.add(this.export);
        mgr.add(this.refresh);
	}
	/*private void doExport() {
		// Instantiates and initializes the wizard
		ExportWizard wiz = new ExportWizard();
		wiz.setModel(ExportModel.getInstance());
	    WizardDialog dialog = new WizardDialog(getSite().getShell(), wiz);
	    dialog.create();
	    dialog.open();
	   
	    if (wiz.performFinish()){
	    	generateXML();
	    	browse.refresh();
	    }
	    
	}*/
	private void generateXML(GroupDataLoader loader) {
		GroupFlexGenerator generator = new GroupFlexGenerator(loader);
		refreshBrowserURL(generator.generate());	
	}

	private void doRefresh(){
		ContentPart view = (ContentPart) viewer.getContents();
		view.removeAll();
		fill();
	}
	
	public void refreshBrowserURL(URL url){	
		browse.setUrl(url.toString());
	}
	
	private void fill(){
		final GroupDataLoader groupDataLoader = new GroupDataLoader();
		Runnable  tFill = new Runnable(){
			public void run() {
				ContentPart view = (ContentPart) viewer.getContents();		
				List<Group> grpLst = groupDataLoader.getGroupList();
				for (Group grp : grpLst) {
					view.addUsersToGroup(grp, groupDataLoader.getUsersInGroup(grp));
				}
				generateXML(groupDataLoader);
			}	
		};
		org.eclipse.swt.custom.BusyIndicator.showWhile(Display.getCurrent(), tFill);
		//Display.getDefault().asyncExec(tFill);
		//firePartPropertyChanged(DATA_PROP, "", "");
	}

}