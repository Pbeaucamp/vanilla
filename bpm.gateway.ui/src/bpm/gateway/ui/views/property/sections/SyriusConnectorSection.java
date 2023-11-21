package bpm.gateway.ui.views.property.sections;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.database.DataBaseHelper;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.tsbn.syrius.SyriusConnector;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;

public class SyriusConnectorSection extends AbstractPropertySection {

	private Node node;
	private SyriusConnector transfo;

	private Text txtService;
	private Text txtUser;
	private Text txtPassword;

	private Text txtYear;
	private Text txtPeriod;
	
	private Text txtFinessFilter;
	private Text txtOrderFilter;
	
	private ComboViewer cbServer;
	private ComboViewer cbColumnCp;
	private ComboViewer cbColumnCodeGeo;
	private Text definitionTxtGeo, definitionTxtPatients, definitionTxtActes, definitionTxtDiags;
	
	private boolean definitionModified;
	
	private List<String> columns = new ArrayList<String>();

	private ModifyListener listener = new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent e) {
			transfo.setUserId(txtUser.getText());
			transfo.setPassword(txtPassword.getText());
			transfo.setYear(txtYear.getText());
			transfo.setPeriod(txtPeriod.getText());
			transfo.setServiceUrl(txtService.getText());
			transfo.setFinessFilter(txtFinessFilter.getText());
			transfo.setOrderFilter(txtOrderFilter.getText());
			transfo.setSqlPatient(definitionTxtPatients.getText());
			transfo.setSqlActes(definitionTxtActes.getText());
			transfo.setSqlDiag(definitionTxtDiags.getText());
		}
	};

	private ModifyListener definitionModifyListener = new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent e) {
			transfo.setSqlGeo(definitionTxtGeo.getText());
			definitionModified = true;
		}
	};

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();
		transfo = (SyriusConnector) node.getGatewayModel();
	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout());

		// File input regex
		Group grp = new Group(composite, SWT.NONE);
		grp.setLayout(new GridLayout());
		grp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		grp.setText("Server");
		Composite serverComposite = new Composite(grp, SWT.NONE);
		serverComposite.setLayout(new GridLayout(2, false));
		serverComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label lbl = new Label(serverComposite, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lbl.setText("Server");
		
		cbServer = new ComboViewer(serverComposite, SWT.DROP_DOWN | SWT.PUSH);
		cbServer.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		cbServer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((DataBaseServer)element).getName();
			}
		});
		cbServer.setContentProvider(new ArrayContentProvider());
		cbServer.addSelectionChangedListener(new ISelectionChangedListener() {			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				DataBaseServer server = (DataBaseServer) ((IStructuredSelection)event.getSelection()).getFirstElement();
				
				transfo.setServer(server);
			}
		});
		
		grp = new Group(composite, SWT.NONE);
		grp.setLayout(new GridLayout());
		grp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		grp.setText("Sql definitions");
		Composite sqlComposite = new Composite(grp, SWT.NONE);
		sqlComposite.setLayout(new GridLayout(2, false));
		sqlComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		lbl = new Label(sqlComposite, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lbl.setText("PATIENT Definition");
		
		definitionTxtPatients = new Text(sqlComposite, SWT.BORDER | SWT.WRAP | SWT.MULTI | SWT.V_SCROLL);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.heightHint = 5 * definitionTxtPatients.getLineHeight();
		definitionTxtPatients.setLayoutData(gridData);
		definitionTxtPatients.setText("");

		lbl = new Label(sqlComposite, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lbl.setText("ACTES Definition");
		
		definitionTxtActes = new Text(sqlComposite, SWT.BORDER | SWT.WRAP | SWT.MULTI | SWT.V_SCROLL);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.heightHint = 5 * definitionTxtActes.getLineHeight();
		definitionTxtActes.setLayoutData(gridData);
		definitionTxtActes.setText("");

		lbl = new Label(sqlComposite, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lbl.setText("DIAGNOSTICS Definition");
		
		definitionTxtDiags = new Text(sqlComposite, SWT.BORDER | SWT.WRAP | SWT.MULTI | SWT.V_SCROLL);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.heightHint = 5 * definitionTxtDiags.getLineHeight();
		definitionTxtDiags.setLayoutData(gridData);
		definitionTxtDiags.setText("");
		
		//code geo infos
		grp = new Group(composite, SWT.NONE);
		grp.setLayout(new GridLayout());
		grp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		grp.setText("Code Geo");
		Composite codeGeoComposite = new Composite(grp, SWT.NONE);
		codeGeoComposite.setLayout(new GridLayout(2, false));
		codeGeoComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		lbl = new Label(codeGeoComposite, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lbl.setText("GEO Definition");
		
		definitionTxtGeo = new Text(codeGeoComposite, SWT.BORDER | SWT.WRAP | SWT.MULTI | SWT.V_SCROLL);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.heightHint = 5 * definitionTxtGeo.getLineHeight();
		definitionTxtGeo.setLayoutData(gridData);
		definitionTxtGeo.setText("");
		definitionTxtGeo.addModifyListener(definitionModifyListener);
		
		lbl = new Label(codeGeoComposite, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lbl.setText("Code geo column");
		
		cbColumnCodeGeo = new ComboViewer(codeGeoComposite, SWT.DROP_DOWN);
		cbColumnCodeGeo.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		cbColumnCodeGeo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((String)element);
			}
		});
		cbColumnCodeGeo.setContentProvider(new ArrayContentProvider());
		cbColumnCodeGeo.addSelectionChangedListener(new ISelectionChangedListener() {			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				String server = (String) ((IStructuredSelection)event.getSelection()).getFirstElement();
				
				transfo.setColumnCodeGeo(server);
			}
		});

		cbColumnCodeGeo.getControl().addListener(SWT.MouseDown, new Listener(){
		    public void handleEvent(Event event) {
				if((columns.isEmpty() && transfo.getSqlGeo() != null && !transfo.getSqlGeo().isEmpty()) 
						|| !columns.isEmpty() && definitionModified) {
					columns.clear();
					
					
					try {
						DefaultStreamDescriptor desc = DataBaseHelper.createDescriptorFromQuery(transfo.getName(), transfo.getServer(), transfo.getSqlGeo(), transfo.getDocument());
						
						for(int i = 0 ; i < desc.getColumnCount() ; i++) {
							columns.add(desc.getColumnName(i));
						}
						
						
					
					} catch (Exception e) {
						
						e.printStackTrace();
					}
				}
				if(columns != null && !columns.isEmpty()) {
					cbColumnCodeGeo.setInput(columns);
				}
			}
		});
		
		lbl = new Label(codeGeoComposite, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lbl.setText("Code postal column");
		
		cbColumnCp = new ComboViewer(codeGeoComposite, SWT.DROP_DOWN);
		cbColumnCp.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		cbColumnCp.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((String)element);
			}
		});
		cbColumnCp.setContentProvider(new ArrayContentProvider());
		cbColumnCp.addSelectionChangedListener(new ISelectionChangedListener() {			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				String server = (String) ((IStructuredSelection)event.getSelection()).getFirstElement();
				
				transfo.setColumnCp(server);
			}
		});
		cbColumnCp.getControl().addListener(SWT.MouseDown, new Listener(){
		    public void handleEvent(Event event) {
				if((columns.isEmpty() && transfo.getSqlGeo() != null && !transfo.getSqlGeo().isEmpty()) 
						|| !columns.isEmpty() && definitionModified) {
					columns.clear();
					
					
					try {
						DefaultStreamDescriptor desc = DataBaseHelper.createDescriptorFromQuery(transfo.getName(), transfo.getServer(), transfo.getSqlGeo(), transfo.getDocument());
						
						for(int i = 0 ; i < desc.getColumnCount() ; i++) {
							columns.add(desc.getColumnName(i));
						}
						
						
					
					} catch (Exception e) {
						
						e.printStackTrace();
					}
				}
				if(columns != null && !columns.isEmpty()) {
					cbColumnCp.setInput(columns);
				}
			}
		});
		
		// user info
		grp = new Group(composite, SWT.NONE);
		grp.setLayout(new GridLayout());
		grp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		grp.setText("User information");
		Composite userComposite = new Composite(grp, SWT.NONE);
		userComposite.setLayout(new GridLayout(2, false));
		userComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		lbl = new Label(userComposite, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lbl.setText("Service url");

		txtService = new Text(userComposite, SWT.BORDER);
		txtService.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtService.addModifyListener(listener);
		
		lbl = new Label(userComposite, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lbl.setText("User Id");

		txtUser = new Text(userComposite, SWT.BORDER);
		txtUser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtUser.addModifyListener(listener);

		lbl = new Label(userComposite, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lbl.setText("Password");

		txtPassword = new Text(userComposite, SWT.BORDER);
		txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtPassword.addModifyListener(listener);

		// parameters
		grp = new Group(composite, SWT.NONE);
		grp.setLayout(new GridLayout());
		grp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		grp.setText("Parameters");
		Composite paramComposite = new Composite(grp, SWT.NONE);
		paramComposite.setLayout(new GridLayout(2, false));
		paramComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		lbl = new Label(paramComposite, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lbl.setText("Year");

		txtYear = new Text(paramComposite, SWT.BORDER);
		txtYear.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtYear.addModifyListener(listener);

		lbl = new Label(paramComposite, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lbl.setText("Period");

		txtPeriod = new Text(paramComposite, SWT.BORDER);
		txtPeriod.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtPeriod.addModifyListener(listener);
		
		lbl = new Label(paramComposite, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lbl.setText("Finess filter");

		txtFinessFilter = new Text(paramComposite, SWT.BORDER);
		txtFinessFilter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtFinessFilter.addModifyListener(listener);
		
		lbl = new Label(paramComposite, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lbl.setText("Order filter");

		txtOrderFilter = new Text(paramComposite, SWT.BORDER);
		txtOrderFilter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtOrderFilter.addModifyListener(listener);
	}

	@Override
	public void refresh() {
		try {
			txtUser.removeModifyListener(listener);
			txtUser.setText(transfo.getUserId());
			txtUser.addModifyListener(listener);
			
			txtPassword.removeModifyListener(listener);
			txtPassword.setText(transfo.getPassword());
			txtPassword.addModifyListener(listener);
			
			txtYear.removeModifyListener(listener);
			txtYear.setText(transfo.getYear());
			txtYear.addModifyListener(listener);
			
			txtPeriod.removeModifyListener(listener);
			txtPeriod.setText(transfo.getPeriod());
			txtPeriod.addModifyListener(listener);
			
			txtService.removeModifyListener(listener);
			txtService.setText(transfo.getServiceUrl());
			txtService.addModifyListener(listener);
			
			txtFinessFilter.removeModifyListener(listener);
			txtFinessFilter.setText(transfo.getFinessFilter());
			txtFinessFilter.addModifyListener(listener);
			
			txtOrderFilter.removeModifyListener(listener);
			txtOrderFilter.setText(transfo.getOrderFilter());
			txtOrderFilter.addModifyListener(listener);
			
			definitionTxtPatients.removeModifyListener(listener);
			definitionTxtPatients.setText(transfo.getSqlPatient());
			definitionTxtPatients.addModifyListener(listener);
			
			definitionTxtActes.removeModifyListener(listener);
			definitionTxtActes.setText(transfo.getSqlActes());
			definitionTxtActes.addModifyListener(listener);
			
			definitionTxtDiags.removeModifyListener(listener);
			definitionTxtDiags.setText(transfo.getSqlDiag());
			definitionTxtDiags.addModifyListener(listener);
			
			cbServer.setInput(ResourceManager.getInstance().getServers(DataBaseServer.class));
			if(transfo.getServer() != null) {
				cbServer.setSelection(new StructuredSelection(transfo.getServer()));
			}
			
			definitionTxtGeo.setText(transfo.getSqlGeo());
			
			if(transfo.getSqlGeo() != null && !transfo.getSqlGeo().isEmpty()) {
				definitionModified = true;
				cbColumnCodeGeo.getCombo().setText(transfo.getColumnCodeGeo());
				cbColumnCp.getCombo().setText(transfo.getColumnCp());
			}
			
			
 			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
