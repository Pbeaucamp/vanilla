package bpm.vanilla.server.ui.views;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

import bpm.birep.admin.datas.vanilla.Group;
import bpm.vanilla.server.client.ServerType;
import bpm.vanilla.server.client.communicators.ServerConfigInfo;
import bpm.vanilla.server.commons.communications.admin.AdminMessageConstants;
import bpm.vanilla.server.ui.Activator;
import bpm.vanilla.server.ui.preferences.PreferencesConstants;

public class ViewServerInfo extends ViewPart {
	private TabbedPropertySheetWidgetFactory toolkit;
	private ScrolledForm form; 
	
	
	private Text serverUrl;
	private Text serverState;
	private Button startButton, stopButton, connectButton;
	
	private TableViewer viewerConfig;
	private ComboViewer serverTypeViewer;
	
	private Button resetConfig, cancel;
	public ViewServerInfo() {
		
	}
	
	public Point getSize(){
		return form.getParent().getSize();
	}
	private void createServerConfigSection(){
		Section section = toolkit.createSection(form.getBody(),Section.DESCRIPTION | Section.TWISTIE | Section.EXPANDED);
		section.setText("Server Config");
		section.setDescription("Show Server Configuration");
		section.setLayout(new GridLayout());
		section.setExpanded(false);
		section.setLayoutData( new TableWrapData(TableWrapData.FILL_GRAB));

		
		Composite composite = toolkit.createComposite(section);
		composite.setLayout(new GridLayout(2, true));
		section.setClient(composite);
		
		viewerConfig  = new TableViewer(composite, SWT.BORDER |SWT.FULL_SELECTION);
		viewerConfig.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			public void dispose() {
				
				
			}
			
			public Object[] getElements(Object inputElement) {
				ServerConfigInfo c = ((ServerConfigInfo)inputElement);
				List l = c.getPropertiesShortNames();
				return l.toArray(new Object[l.size()]);
			}
		});
		viewerConfig.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		viewerConfig.getTable().setLinesVisible(true);
		viewerConfig.getTable().setHeaderVisible(true);
		
		TableViewerColumn cName = new TableViewerColumn(viewerConfig, SWT.NONE);
		cName.getColumn().setWidth(100);
		cName.getColumn().setText("Property");
		cName.setLabelProvider(new ColumnLabelProvider());
		
	
		TableViewerColumn cVal = new TableViewerColumn(viewerConfig, SWT.NONE);
		cVal.getColumn().setWidth(100);
		cVal.getColumn().setText("Value");
		cVal.setLabelProvider(new ColumnLabelProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				String v = ((ServerConfigInfo)viewerConfig.getInput()).getValue((String)element);
				
				return v == null ? "" : v;
			}
			
		});
		cVal.setEditingSupport(new EditingSupport(viewerConfig) {
			TextCellEditor editor = new TextCellEditor(viewerConfig.getTable());
			@Override
			protected void setValue(Object element, Object value) {
				ServerConfigInfo cfg = (ServerConfigInfo)viewerConfig.getInput();
				cfg.setValue((String)element, (String)value);
				resetConfig.setEnabled(true);
				cancel.setEnabled(true);
				viewerConfig.refresh();
				
			}
			
			@Override
			protected Object getValue(Object element) {
				String v = ((ServerConfigInfo)viewerConfig.getInput()).getValue((String)element);
				
				return v == null ? "" : v;
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return editor;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});

	
		resetConfig = toolkit.createButton(composite, "Config Server", SWT.PUSH);
		resetConfig.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		resetConfig.setEnabled(false);
		resetConfig.addSelectionListener(new SelectionAdapter(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					Activator.getDefault().getServerRemote().resetServerConfig((ServerConfigInfo)viewerConfig.getInput());
					viewerConfig.setInput(Activator.getDefault().getServerRemote().getServerConfig());
					cancel.setEnabled(false);
					resetConfig.setEnabled(false);
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getSite().getShell(), "Problem Occured", "Error when trying to retrive Server Configuration:\n" + e1.getMessage());
				}
			}
			
		});
		
		cancel = toolkit.createButton(composite, "Cancel", SWT.PUSH);
		cancel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		cancel.setEnabled(false);
		cancel.addSelectionListener(new SelectionAdapter(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					viewerConfig.setInput(Activator.getDefault().getServerRemote().getServerConfig());
					cancel.setEnabled(false);
					resetConfig.setEnabled(false);
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getSite().getShell(), "Problem Occured", "Error when trying to retrive Server Configuration:\n" + e1.getMessage());
				}
			}
			
		});
	}
	
	private void createServerInfoSection(){
		Section section = toolkit.createSection(form.getBody(),Section.DESCRIPTION | Section.TWISTIE | Section.EXPANDED);
		section.setText("Server Informations");
		section.setDescription("Show Server Informations");
		section.setLayout(new GridLayout());	
		section.setLayoutData( new TableWrapData(TableWrapData.FILL_GRAB));
		
		
		
		Composite composite = toolkit.createComposite(section);
		composite.setLayout(new GridLayout(3, false));
		section.setClient(composite);
		
		
		Label l = toolkit.createLabel(composite, "Server Type");
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

	
		serverTypeViewer = new ComboViewer(toolkit.createCCombo(composite, SWT.READ_ONLY));
		serverTypeViewer.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			public void dispose() {
				
				
			}
			
			public Object[] getElements(Object inputElement) {
				return (Object[])inputElement;
			}
		});
		serverTypeViewer.setLabelProvider(new LabelProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				return ((ServerType)element).getTypeName();
			}
			
		});
		serverTypeViewer.getCCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		serverTypeViewer.setInput(new ServerType[]{ServerType.REPORTING, ServerType.GATEWAY, ServerType.FREEMETADATA});
		serverTypeViewer.setSelection(new StructuredSelection(ServerType.REPORTING));
		serverTypeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				serverState.setText("Disconnnected");
				Activator.getDefault().setServerUrl(null, null);
				
			}
		});

		
		l = toolkit.createLabel(composite, "Server Url");
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		serverUrl = toolkit.createText(composite, "");
		serverUrl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
		serverUrl.setText(Activator.getDefault().getPreferenceStore().getString(PreferencesConstants.REPORTING_SERVER_URL));
		
		connectButton = toolkit.createButton(composite, "Connect", SWT.PUSH);
		connectButton.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		connectButton.addSelectionListener(new SelectionAdapter(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				ServerType serverType = (ServerType)((IStructuredSelection)serverTypeViewer.getSelection()).getFirstElement();
				
				Activator.getDefault().setServerUrl(serverType, serverUrl.getText());
				try {
					if (Activator.getDefault().getServerRemote().isServerRunning()){
						serverState.setText(AdminMessageConstants.RESPONSE_SERVER_STATE_ON);
					}
					else{
						serverState.setText(AdminMessageConstants.RESPONSE_SERVER_STATE_OFF);
					}
					
					viewerConfig.setInput(Activator.getDefault().getServerRemote().getServerConfig());
					stopButton.setEnabled(true);
					startButton.setEnabled(true);
				} catch (Exception e1) {
					Activator.getDefault().setServerUrl(null, null);
					stopButton.setEnabled(false);
					startButton.setEnabled(false);
					e1.printStackTrace();
					MessageDialog.openInformation(getSite().getShell(), "Server Message", e1.getMessage());
				}
				
				getViewSite().getActionBars().getMenuManager().updateAll(true);
			}
			
		});
		
		l = toolkit.createLabel(composite, "Server State");
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		serverState = toolkit.createText(composite, "");
		serverState.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		serverState.setEditable(false);
		
		Composite bBar = toolkit.createComposite(composite);
		bBar.setLayout(new GridLayout(2, true));
		bBar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
	
		startButton = toolkit.createButton(bBar, "Start Server", SWT.PUSH);
		startButton.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		startButton.addSelectionListener(new SelectionAdapter(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				try {
					Activator.getDefault().getServerRemote().startServer();
					if (Activator.getDefault().getServerRemote().isServerRunning()){
						serverState.setText(AdminMessageConstants.RESPONSE_SERVER_STATE_ON);
					}
					else{
						serverState.setText(AdminMessageConstants.RESPONSE_SERVER_STATE_OFF);
					}
				} catch (Exception e1) {
					
					e1.printStackTrace();
					MessageDialog.openInformation(getSite().getShell(), "Server Message", e1.getMessage());
				}
				
				
			}
			
		});
		startButton.setEnabled(false);
		
		stopButton = toolkit.createButton(bBar, "Stop Server", SWT.PUSH);
		stopButton.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		stopButton.addSelectionListener(new SelectionAdapter(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				try {
					Activator.getDefault().getServerRemote().stopServer();
					if (Activator.getDefault().getServerRemote().isServerRunning()){
						serverState.setText(AdminMessageConstants.RESPONSE_SERVER_STATE_ON);
					}
					else{
						serverState.setText(AdminMessageConstants.RESPONSE_SERVER_STATE_OFF);
					}
				} catch (Exception e1) {
					
					e1.printStackTrace();
					MessageDialog.openInformation(getSite().getShell(), "Server Message", e1.getMessage());
				}
				
				
			}
			
		});
		stopButton.setEnabled(false);
	}
	
	@Override
	public void createPartControl(Composite parent) {
		toolkit = new TabbedPropertySheetWidgetFactory();
				  
		form = toolkit.createScrolledForm(parent);
		form.setExpandHorizontal(false);
		form.setAlwaysShowScrollBars(false);
		form.setExpandVertical(true);
		form.getHorizontalBar().setEnabled(false);
		//form.getHorizontalBar().dispose();
		form.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = true;
		form.getBody().setLayout(layout);
//		form.getBody().setLayout(new GridLayout());
		
		createServerInfoSection();
		
		createServerConfigSection();
	
	
		
	}

	@Override
	public void setFocus() {
		

	}

}
