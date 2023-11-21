package bpm.gateway.ui.views.property.sections.transformations.kpi;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.freemetrics.api.manager.FactoryManager;
import bpm.freemetrics.api.manager.IManager;
import bpm.freemetrics.api.organisation.application.Application;
import bpm.freemetrics.api.organisation.group.Group;
import bpm.freemetrics.api.organisation.metrics.Metric;
import bpm.freemetrics.api.organisation.relations.appl_metric.Assoc_Application_Metric;
import bpm.freemetrics.api.security.FmUser;
import bpm.freemetrics.api.utils.Tools;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.freemetrics.FreemetricServer;
import bpm.gateway.core.server.database.jdbc.JdbcConnectionProvider;
import bpm.gateway.core.transformations.freemetrics.KPIList;
import bpm.gateway.core.transformations.outputs.FreemetricsKPI;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.studio.jdbc.management.model.DriverInfo;







public class KPIListSection extends AbstractPropertySection {
	public static Color mainBrown = new Color(Display.getDefault(), 209,177, 129);
	public static Color secondBrown = new Color(Display.getDefault(), 238,226, 208);

	public static List<Item> viewerCachedDatas = new ArrayList<Item>();
	
	private CheckboxTreeViewer viewer;	
	private ViewerFilter checkFilter, metFilter,  appliFilter;
	
	private Node node;
	
	private Button showOnlyChecked;
	private Button useApplicationFilter;
	private Button useMetricFilter;
	private Text applicationFilter;
	private Text metricFilter;
	
		
	
	
	private void createFilterBar(Composite parent){
		ModifyListener modifyListener = new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				viewer.refresh();
				
			}
			
		};
		
		
		org.eclipse.swt.widgets.Group group = getWidgetFactory().createGroup(parent, Messages.KPIListSection_0);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		
		showOnlyChecked = getWidgetFactory().createButton(group, Messages.KPIListSection_1, SWT.CHECK);
		showOnlyChecked.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false, 2, 1));
		showOnlyChecked.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (showOnlyChecked.getSelection()){
					viewer.addFilter(checkFilter);
				}
				else{
					viewer.removeFilter(checkFilter);
				}
	
				viewer.refresh();
			}
		});
		
		
		useApplicationFilter =  getWidgetFactory().createButton(group, Messages.KPIListSection_2, SWT.CHECK);
		useApplicationFilter.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		useApplicationFilter.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				applicationFilter.setEnabled(useApplicationFilter.getSelection());
				
				if (useApplicationFilter.getSelection()){
					viewer.addFilter(appliFilter);
				}
				else{
					viewer.removeFilter(appliFilter);
				}
				viewer.refresh();
			}
			
		});
		
		applicationFilter = getWidgetFactory().createText(group, "", SWT.BORDER); //$NON-NLS-1$
		applicationFilter.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		applicationFilter.setEnabled(false);
		applicationFilter.addModifyListener(modifyListener);
		
		useMetricFilter = getWidgetFactory().createButton(group, Messages.KPIListSection_4, SWT.CHECK);
		useMetricFilter.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		useMetricFilter.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				metricFilter.setEnabled(useMetricFilter.getSelection());
				
				if (useMetricFilter.getSelection()){
					viewer.addFilter(metFilter);
				}
				else{
					viewer.removeFilter(metFilter);
				}
				viewer.refresh();
			}
			
		});
		
		metricFilter = getWidgetFactory().createText(group, "", SWT.BORDER); //$NON-NLS-1$
		metricFilter.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		metricFilter.setEnabled(false);
		metricFilter.addModifyListener(modifyListener);
		
		Button b = getWidgetFactory().createButton(group, Messages.KPIListSection_6, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		b.addSelectionListener(new SelectionAdapter(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				viewer.setAllChecked(true);
				for(Item i : (List<Item>)viewer.getInput()){
					((KPIList)node.getGatewayModel()).addAssociationId(i.assocId);
				}
			}
			
		});
		
		Button b1 = getWidgetFactory().createButton(group, Messages.KPIListSection_7, SWT.PUSH);
		b1.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		b1.addSelectionListener(new SelectionAdapter(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				viewer.setAllChecked(false);
				for(Item i : (List<Item>)viewer.getInput()){
					((KPIList)node.getGatewayModel()).removeAssociationId(i.assocId);
				}
			}
			
		});
		
		
		Button load = getWidgetFactory().createButton(group, Messages.KPIListSection_8, SWT.PUSH);
		load.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		load.addSelectionListener(new SelectionAdapter(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshViewerInput();
			}
			
		});
		
		
		checkFilter = new ViewerFilter(){

			@Override
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				for(Integer i : ((KPIList)node.getGatewayModel()).getAssocIds()){
					if (((Item)element).assocId == i.intValue()){
						return true;
					}
				}
				return false;
				
			}
			
		};
		
		metFilter = new ViewerFilter(){

			@Override
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				
				return ((Item)element).metricName.startsWith(metricFilter.getText());
			}
		};
		
		appliFilter = new ViewerFilter(){

			@Override
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				
				return ((Item)element).applicationName.startsWith(applicationFilter.getText());
			}
		};
		
	}
	
	
	@Override
	public void createControls(Composite parent,TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.setLayout(new GridLayout());
		
				
		createFilterBar(parent);
		
		viewer = new CheckboxTreeViewer(parent, SWT.VIRTUAL | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.getTree().setHeaderVisible(true);
		
		
		
		viewer.setContentProvider(new ITreeContentProvider(){

			public Object[] getChildren(Object parentElement) {
				if (parentElement instanceof List){
					return getElements(viewer.getInput());
				}
				return null;
			}

			public Object getParent(Object element) {
				if (element instanceof Item){
					return viewer.getInput();
				}
				return null;
			}

			public boolean hasChildren(Object element) {
				return element instanceof List ;
			}

			public Object[] getElements(Object inputElement) {
				List<Item> l = (List<Item>)inputElement;
				return l.toArray(new Item[l.size()]);
			}

			public void dispose() {
				
				
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				
				
			}
			
		});
		
		TreeViewerColumn apps = new TreeViewerColumn(viewer, SWT.NONE);
		apps.getColumn().setText(Messages.KPIListSection_9);
		apps.getColumn().setWidth(200);
		apps.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Item)element).applicationName;
			}

			@Override
			public Color getBackground(Object element) {
				if (((List)viewer.getInput()).indexOf(element) % 2 == 0){
					return mainBrown;
				}
				else{
					return secondBrown;
				}
				
			}
			
			

		});
		
		TreeViewerColumn mets = new TreeViewerColumn(viewer, SWT.NONE);
		mets.getColumn().setText(Messages.KPIListSection_10);
		mets.getColumn().setWidth(200);
		mets.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Item)element).metricName;
			}
			@Override
			public Color getBackground(Object element) {
				if (((List)viewer.getInput()).indexOf(element) % 2 == 0){
					return mainBrown;
				}
				else{
					return secondBrown;
				}
				
			}
			

		});

		
		viewer.addCheckStateListener(new ICheckStateListener(){

			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getChecked()){
					((KPIList)node.getGatewayModel()).addAssociationId(((Item)event.getElement()).assocId);
				}
				else{
					((KPIList)node.getGatewayModel()).removeAssociationId(((Item)event.getElement()).assocId);
				}
				
			}
			
		});

	}
	
	
	private void refreshViewerInput(){
		KPIList transfo = (KPIList)node.getGatewayModel(); 
		
		IRunnableWithProgress r = new IRunnableWithProgress(){

			public void run(IProgressMonitor monitor)
					throws InvocationTargetException,
					InterruptedException {
				synchronized (viewerCachedDatas) {
					viewerCachedDatas.clear();
					
					/*
					 * create the Connection to Freemetrics from its serverDefinition
					 * and Store the FmUser
					 */
					Properties prop = new Properties();
					
					FreemetricsKPI transfo = (FreemetricsKPI)node.getGatewayModel();
					FreemetricServer server = (FreemetricServer)transfo.getServer();
					FmUser user = null;
					IManager fmMgr =null;
					try{
						DriverInfo dInfo = JdbcConnectionProvider.getListDriver().getInfo(((DataBaseConnection)server.getCurrentConnection(null)).getDriverName());
						
						String dbUrl = dInfo.getUrlPrefix() + ((DataBaseConnection)server.getCurrentConnection(null)).getHost() + ":" + ((DataBaseConnection)server.getCurrentConnection(null)).getPort() + "/" + ((DataBaseConnection)server.getCurrentConnection(null)).getDataBaseName(); //$NON-NLS-1$ //$NON-NLS-2$
						
						prop.setProperty("driverClassName", dInfo.getClassName()); //$NON-NLS-1$
						prop.setProperty("username", ((DataBaseConnection)server.getCurrentConnection(null)).getLogin()); //$NON-NLS-1$
						prop.setProperty("password", ((DataBaseConnection)server.getCurrentConnection(null)).getPassword()); //$NON-NLS-1$
						prop.setProperty("url", dbUrl); //$NON-NLS-1$
						
						FactoryManager.init("", Tools.OS_TYPE_WINDOWS); //$NON-NLS-1$
						
						
						fmMgr = FactoryManager.getManager(); //$NON-NLS-1$
						
						
						user = fmMgr.getUserByNameAndPass(server.getFmLogin(), server.getFmPassword());

					}catch(Exception e){
						e.printStackTrace();
						return;
					}
					
					if (user == null){
						MessageDialog.openWarning(getPart().getSite().getShell(), Messages.KPIListSection_19, Messages.KPIListSection_20);
						
					}
					
					
					
						
						for(Metric met : fmMgr.getMetricsForOwnerId(user.getId())) {
							for(Assoc_Application_Metric ass : fmMgr.getAssoc_Application_MetricByMetricId(met.getId())){
								
								Item it = new Item();
								it.assocId = ass.getId();
								
								it.applicationName = ass.getApplicationsName();
								it.metricName = fmMgr.getMetricById(ass.getMetr_ID()).getName();
								
								viewerCachedDatas.add(it);
							}
						}
						
//						for(Application a : fmMgr.getApplicationsForGroup(g.getId())){
//							
//							for(Assoc_Application_Metric ass : fmMgr.getAssoc_Application_MetricsByAppId(a.getId())){
//								
//								Item it = new Item();
//								it.assocId = ass.getId();
//								
//								it.applicationName = a.getName();
//								it.metricName = fmMgr.getMetricById(ass.getMetr_ID()).getName();
//								
//								viewerCachedDatas.add(it);
//							}
//							
//						}
					
				}

				

				
			}
		};

		
		IProgressService service = PlatformUI.getWorkbench().getProgressService();
	    try {
			service.run(true, false, r);
			if (viewer.getInput() == null || ((List<?>)viewer.getInput()).isEmpty()){
		    	 viewer.setInput(viewerCachedDatas); 
		    }
		} catch (InvocationTargetException e) {
			
			e.printStackTrace();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
	    
	}
	
	@Override
	public void refresh() {
		
		KPIList transfo = (KPIList)node.getGatewayModel(); 

		
		if (viewerCachedDatas.isEmpty()){
			refreshViewerInput();
		}
		else if (viewer.getInput() != viewerCachedDatas){
			viewer.setInput(viewerCachedDatas);
		}
		
		
	     viewer.setAllChecked(false);

	     for(Integer v : transfo.getAssocIds()){
	    	 for(Item it : (List<Item>)viewer.getInput()){
		    	if (v.equals(it.assocId)){
		    		viewer.setChecked(it, true);
		    	}
		     } 
	     }
	     viewer.refresh();
	     
	     
	  
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.node = (Node)((NodePart) input).getModel();
       
	}
	
	
	private class Item{
		private int assocId;
		private String applicationName;
		private String metricName;
	}
}
