package bpm.birep.admin.client.disco;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import adminbirep.Activator;
import adminbirep.Messages;
import adminbirep.icons.Icons;
import bpm.birep.admin.client.trees.TreeHelperType;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.disco.DiscoReportConfiguration;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class DisconnectedReportConfigurationPage extends WizardPage{

	private ListViewer viewerReport, viewerGroup;
	private TableViewer viewerConfig;
	
	private List<RepositoryItem> items;
	private List<Group> groups;
	private int repositoryId;
	
	private List<DiscoReportConfiguration> configs;
	
	public DisconnectedReportConfigurationPage(String pageName, List<RepositoryItem> items) {
		super(pageName);
		this.items = items;
		this.repositoryId = Activator.getDefault().getCurrentRepository().getId();
		try {
			this.groups = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroups();
		} catch (Exception e) {
			e.printStackTrace();
			this.groups = new ArrayList<Group>();
			MessageDialog.openError(getShell(), Messages.DisconnectedReportConfigurationPage_0, Messages.DisconnectedReportConfigurationPage_1 + e.getMessage());
		}
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, true));
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		Label lblReports = new Label(main, SWT.NONE);
		lblReports.setData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		lblReports.setText(Messages.DisconnectedReportConfigurationPage_2);
		
		Label lblGroups = new Label(main, SWT.NONE);
		lblGroups.setData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		lblGroups.setText(Messages.DisconnectedReportConfigurationPage_3);
		
		buildReportContent(main);
		buildGroupContent(main);
		
		Label lblManageParams = new Label(main, SWT.NONE);
		lblManageParams.setData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		lblManageParams.setText(Messages.DisconnectedReportConfigurationPage_4);
		
		buildConfigToolbar(main);
		buildConfigContent(main);
		
		setControl(main);
		
		if(items != null){
			viewerReport.setInput(items);
		}
		if(groups != null){
			viewerGroup.setInput(groups);
		}
	}

	private void buildReportContent(Composite main) {
		
		viewerReport = new ListViewer(main);
		viewerReport.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		viewerReport.setLabelProvider(new LabelProvider(){
			
			@Override
			public Image getImage(Object element) {
				if(element instanceof RepositoryItem){
					RepositoryItem item = (RepositoryItem)element;
					ImageRegistry reg = Activator.getDefault().getImageRegistry();
					String key = TreeHelperType.getKeyForType(item.getType(), item);
					return reg.get(key);
				}
				else {
					return null;
				}
			}
			
			@Override
			public String getText(Object element) {
				if(element instanceof RepositoryItem){
					return ((RepositoryItem) element).getItemName();
				}
				return null;
			}
		});
		viewerReport.setContentProvider(new IStructuredContentProvider() {
			
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }
			
			@Override
			public void dispose() { }
			
			@Override
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<RepositoryItem> items = (List<RepositoryItem>)inputElement;
				return items.toArray(new RepositoryItem[items.size()]);
			}
		});
		viewerReport.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				refreshConfig();
			}
		});
	}
	
	private void buildGroupContent(Composite main) {
		viewerGroup = new ListViewer(main);
		viewerGroup.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		viewerGroup.setLabelProvider(new LabelProvider(){
			
			@Override
			public String getText(Object element) {
				if(element instanceof Group){
					return ((Group) element).getName();
				}
				return null;
			}
		});
		viewerGroup.setContentProvider(new IStructuredContentProvider() {
			
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }
			
			@Override
			public void dispose() { }
			
			@Override
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<Group> items = (List<Group>)inputElement;
				return items.toArray(new Group[items.size()]);
			}
		});
		viewerGroup.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				refreshConfig();
			}
		});
	}

	private void buildConfigToolbar(Composite parent) {
		ToolBar bar = new ToolBar(parent, SWT.FLAT);
		bar.setLayoutData(new GridData());
		bar.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		
		ToolItem it = new ToolItem(bar, SWT.PUSH);
		it.setToolTipText(Messages.DisconnectedReportConfigurationPage_5);
		it.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ADD));
		it.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Object selectionReport = ((IStructuredSelection)viewerReport.getSelection()).getFirstElement();
				Object selectionGroup = ((IStructuredSelection)viewerGroup.getSelection()).getFirstElement();
				if(selectionReport != null && selectionReport instanceof RepositoryItem && selectionGroup != null && selectionGroup instanceof Group){
					RepositoryItem item = (RepositoryItem)selectionReport;
					Group group = (Group)selectionGroup;
					DialogReportConfiguration dial = new DialogReportConfiguration(getShell(), repositoryId, group.getId(), item);
					if(dial.open() == Dialog.OK){
						List<VanillaGroupParameter> params = dial.getSelectedParameters();
						List<String> formats = dial.getSelectedFormats();
						
						DiscoReportConfiguration discoConfig = new DiscoReportConfiguration();
						discoConfig.setItem(item);
						discoConfig.setGroup(group);
						discoConfig.setSelectedParameters(params);
						discoConfig.setSelectedFormats(formats);
						
						if(configs == null){
							configs = new ArrayList<DiscoReportConfiguration>();
						}
						configs.add(discoConfig);
						
						refreshConfig();
					}
					
					getContainer().updateButtons();
				}
			}
		});
		
		final ToolItem edit = new ToolItem(bar, SWT.PUSH);
		edit.setToolTipText(Messages.DisconnectedReportConfigurationPage_6);
		edit.setImage(Activator.getDefault().getImageRegistry().get(Icons.EDIT));
		edit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Object selectionReport = ((IStructuredSelection)viewerReport.getSelection()).getFirstElement();
				Object selectionGroup = ((IStructuredSelection)viewerGroup.getSelection()).getFirstElement();
				if(selectionReport != null && selectionReport instanceof RepositoryItem && selectionGroup != null && selectionGroup instanceof Group){
					RepositoryItem item = (RepositoryItem)selectionReport;
					Group group = (Group)selectionGroup;
					
					Object selection = ((IStructuredSelection)viewerConfig.getSelection()).getFirstElement();
					if(selection != null && selection instanceof DiscoReportConfiguration){
						final DiscoReportConfiguration selectedConfig = (DiscoReportConfiguration)selection;
						DialogReportConfiguration dial = new DialogReportConfiguration(getShell(), repositoryId, group.getId(), item, selectedConfig);
						if(dial.open() == Dialog.OK) {
							List<VanillaGroupParameter> params = dial.getSelectedParameters();
							List<String> formats = dial.getSelectedFormats();
	
							selectedConfig.setSelectedParameters(params);
							selectedConfig.setSelectedFormats(formats);
							
							refreshConfig();
						}
					}
					
					getContainer().updateButtons();
				}
			}
		});
		
		final ToolItem delt = new ToolItem(bar, SWT.PUSH);
		delt.setToolTipText(Messages.DisconnectedReportConfigurationPage_7);
		delt.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_DELETE));
		delt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for(Object o : ((IStructuredSelection)viewerConfig.getSelection()).toList()){
					configs.remove(o);
				}
				refreshConfig();
				
				getContainer().updateButtons();
			}
		});
	}

	private void buildConfigContent(Composite parent){
		viewerConfig = new TableViewer(parent, SWT.BORDER | SWT.H_SCROLL | SWT.FULL_SELECTION);
		viewerConfig.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		TableViewerColumn name = new TableViewerColumn(viewerConfig, SWT.NONE);
		name.getColumn().setText(Messages.DisconnectedReportConfigurationPage_8);
		name.getColumn().setWidth(350);

		TableViewerColumn typeField = new TableViewerColumn(viewerConfig, SWT.NONE);
		typeField.getColumn().setText(Messages.DisconnectedReportConfigurationPage_9);
		typeField.getColumn().setWidth(250);
		
		viewerConfig.setContentProvider(new IStructuredContentProvider() {

			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List l = (List) inputElement;
				return l.toArray(new Object[l.size()]);
			}

			public void dispose() { }

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }
		});
		viewerConfig.setLabelProvider(new ITableLabelProvider() {

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				switch (columnIndex) {
				case 0:
					return ((DiscoReportConfiguration) element).getParamDefinition();
				case 1:
					return ((DiscoReportConfiguration) element).getFormatDefinition();
				}
				return null;
			}

			public void addListener(ILabelProviderListener listener) { }

			public void dispose() { }

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener listener) { }
		});
		viewerConfig.getTable().setHeaderVisible(true);
	}
	
	private void refreshConfig(){
		Object selectionReport = ((IStructuredSelection)viewerReport.getSelection()).getFirstElement();
		Object selectionGroup = ((IStructuredSelection)viewerGroup.getSelection()).getFirstElement();
		if(selectionReport != null && selectionReport instanceof RepositoryItem && selectionGroup != null && selectionGroup instanceof Group){
			RepositoryItem item = (RepositoryItem) selectionReport;
			Group group = (Group) selectionGroup;
			
			if(configs != null){
				List<DiscoReportConfiguration> customConfigs = new ArrayList<DiscoReportConfiguration>();
				for(DiscoReportConfiguration config : configs){
					if(config.getGroup().getId() == group.getId() && config.getItem().getId() == item.getId()){
						customConfigs.add(config);
					}
				}
				
				viewerConfig.setInput(customConfigs);
			}
		}
	}

	@Override
	public boolean isPageComplete() {
		return configs != null && !configs.isEmpty();
	}

	@Override
	public boolean canFlipToNextPage() {
		return isPageComplete();
	}

	public List<DiscoReportConfiguration> getConfigs() {
		return configs;
	}
}
