package metadata.client.model.composites;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import metadata.client.helper.GroupHelper;
import metadata.client.i18n.Messages;
import metadata.client.model.dialog.DialogSelectElement;
import metadataclient.Activator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.resource.IResource;
import bpm.metadata.resource.SqlQueryFilter;
import bpm.metadata.scripting.Variable;

public class CompositeSqlFilter extends Composite{
private static final Color RED = new Color(Display.getDefault(), 255, 0, 0);

	
	private SqlQueryFilter filter;
	private IDataStreamElement ori;
	
	private Text origin, name;

	private static int begin = 725;
	private static int step = 20;
	
	private Viewer v;
	
	private CheckboxTreeViewer tableGroups;
	
	private boolean containApply = false;
	private Label errorLabel;
	private Button ok, cancel;
	private boolean isFilling = false;
	
	private ComboViewer locale;
	private Text outputName, query;
	
	
	private ContentProposalAdapter proposalAdapter;
	
	public CompositeSqlFilter(Composite parent, int style, boolean containApply) {
		super(parent, style);
		this.setLayout(new GridLayout(2, true));
		
		this.containApply = containApply;
		buildContent();
		fillDatas();
		
	}
	
	private void setErrorMessage(String message){
		if (message == null){
			errorLabel.setVisible(false);
		}
		else{
			errorLabel.setText(message);
			errorLabel.setVisible(true);
		}
		
	}
	
	public CompositeSqlFilter(Composite parent, int style, Viewer viewer, SqlQueryFilter filter, boolean containApply) {
		super(parent, style);
		this.containApply = containApply;
		this.v = viewer;
		this.setLayout(new GridLayout(2, true));
		buildContent();
		this.filter = filter;
		ori = filter.getOrigin();
	
		fillDatas();
	}
	
	public CompositeSqlFilter(Composite parent, int style) {
		super(parent, style);
		this.containApply = false;
		this.setLayout(new GridLayout(2, true));
		buildContent();
		fillDatas();
	
		
	}

	private void buildContent(){
		TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		errorLabel = new Label(this, SWT.NONE);
    	errorLabel.setLayoutData(new GridData(GridData.FILL, GridData.END, true, false, 2, 1));
    	errorLabel.setForeground(RED);
    	errorLabel.setVisible(false);
		
		TabItem itemGal = new TabItem(tabFolder, SWT.NONE, 0);
		itemGal.setText(Messages.CompositeComplexFilter_0); //$NON-NLS-1$
		itemGal.setControl(createGeneral(tabFolder));
    	
		TabItem itemSec = new TabItem(tabFolder, SWT.NONE, 1);
    	itemSec.setText(Messages.CompositeComplexFilter_1); //$NON-NLS-1$
    	itemSec.setControl(createSecurity(tabFolder));
   	
    	List<String> groups = GroupHelper.getGroups(begin, step);
		
		List<Secu> l = new ArrayList<Secu>();
		
		for(String s : groups){
			Secu _c = new Secu();
			_c.groupName = s;
			_c.visible = false;
			l.add(_c);
		}
		tableGroups.setInput(l);
    	
	}
	private Control createToolbar(Composite parent){
		ToolBar toolbar = new ToolBar(parent, SWT.NONE);
		toolbar.setLayoutData(new GridData());
		
		ToolItem checkAll = new ToolItem(toolbar, SWT.PUSH);
		checkAll.setToolTipText("Check All"); //$NON-NLS-1$
		checkAll.setImage(Activator.getDefault().getImageRegistry().get("check")); //$NON-NLS-1$
		checkAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				
				tableGroups.setAllChecked(true);
				for(Object o : ((IStructuredContentProvider)tableGroups.getContentProvider()).getElements(tableGroups.getInput())){
					filter.setGranted(((Secu)o).groupName, true);
				}
			}
		});
		
		ToolItem uncheckAll = new ToolItem(toolbar, SWT.PUSH);
		uncheckAll.setToolTipText("Uncheck All"); //$NON-NLS-1$
		uncheckAll.setImage(Activator.getDefault().getImageRegistry().get("uncheck")); //$NON-NLS-1$
		uncheckAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				tableGroups.setAllChecked(false);
				for(Object o : ((IStructuredContentProvider)tableGroups.getContentProvider()).getElements(tableGroups.getInput())){
					filter.setGranted(((Secu)o).groupName, false);
				}
			}
		});
		
		return toolbar;
	}
	private Control createSecurity(TabFolder folder){
		Composite c = new Composite(folder, SWT.NONE);
		c.setLayout(new GridLayout());
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createToolbar(c);
		
		tableGroups = new CheckboxTreeViewer(c, SWT.V_SCROLL  | SWT.VIRTUAL);
		tableGroups.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
		tableGroups.setContentProvider(new ITreeContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<Object> list = (List<Object>) inputElement;
				return list.toArray(new Object[list.size()]);
			}

			public void dispose() {
				
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
			}

			public Object[] getChildren(Object parentElement) {
				return null;
			}

			public Object getParent(Object element) {
				return null;
			}

			public boolean hasChildren(Object element) {
				return false;
			}
			
		});
		
		tableGroups.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Secu)element).groupName;
			}
		});

		tableGroups.addCheckStateListener(new ICheckStateListener() {
			
			public void checkStateChanged(CheckStateChangedEvent event) {
								
				Secu s = (Secu)event.getElement();
				if (filter == null){
					return;
				}
				boolean b = tableGroups.getChecked(s);
				
				filter.setGranted(s.groupName, b);
				
			}
		});		
		
		
		
		return c;
	}
	
	private Control createGeneral(TabFolder root){
		Composite parent = new Composite(root, SWT.NONE);
		parent.setLayout(new GridLayout(3, false));
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		Label l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.CompositeComplexFilter_2); //$NON-NLS-1$
		
		name = new Text(parent, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		name.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				IResource r = Activator.getDefault().getModel().getResource(name.getText());
				if ( r != null && r != filter){
					setErrorMessage(Messages.CompositeSqlFilter_2);
				}
				else{
					setErrorMessage(null);
				}
				notifyListeners(SWT.Selection, new Event());
				
			}
		});
			
		Label l2 = new Label(parent, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER,false, false));
		l2.setText(Messages.CompositeComplexFilter_3); //$NON-NLS-1$
		
		origin = new Text(parent, SWT.BORDER);
		origin.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		origin.setEnabled(false);
		
		Button b = new Button(parent, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		b.setText("..."); //$NON-NLS-1$
		b.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogSelectElement dial =null;
				dial = new DialogSelectElement(getShell());
				
				
				if (dial.open() == DialogSelectElement.OK){
					
					ori = dial.getDataStreamElement();
					origin.setText(ori.getName());
				}
				notifyListeners(SWT.Selection, new Event());
			}
			
		});
		
		
			
		Label l1 = new Label(parent, SWT.NONE);
		l1.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		l1.setText(Messages.DialogSqlFilter_2); //$NON-NLS-1$
		
		Button test = new Button(parent, SWT.PUSH);
		test.setLayoutData(new GridData(GridData.END, GridData.CENTER, true, false));
		test.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					
					String filterString = query.getText();
					
					
					for(Variable v : Activator.getDefault().getModel().getVariables()){
						if (filterString.contains(v.getSymbol())){
							filterString = filterString.replace(v.getSymbol(), " "); //$NON-NLS-1$
						}
					}
					
					String querySql = "select * from " + ori.getDataStream().getOrigin().getName(); //$NON-NLS-1$
					querySql += " where " + ori.getOrigin().getName() + " " + filterString; //$NON-NLS-1$ //$NON-NLS-2$
					
					for(Variable v : Activator.getDefault().getModel().getVariables()){
						if (filterString.contains(v.getSymbol())){
							querySql = querySql.replace(v.getSymbol(), "''"); //$NON-NLS-1$
							
						}
					}
					
					ori.getDataStream().getOrigin().getConnection().executeQuery(querySql, 1, null);
					MessageDialog.openInformation(getShell(), Messages.DialogSqlFilter_6, Messages.DialogSqlFilter_7); //$NON-NLS-1$ //$NON-NLS-2$
					
				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(getShell(), Messages.DialogSqlFilter_8, ex.getMessage()); //$NON-NLS-1$
				}
			}
			
		});
		test.setText(Messages.CompositeSqlFilter_5);
		
		query = new Text(parent,SWT.BORDER | SWT.WRAP | SWT.MULTI);
		query.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 2));
		query.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				
				notifyListeners(SWT.Selection, new Event());
				
			}
		});
		
		Group grpInternationalisation = new Group(parent, SWT.NONE);
		grpInternationalisation.setLayout(new GridLayout(3, false));
		grpInternationalisation.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
		grpInternationalisation.setText("Internationalisation");

		Label l3 = new Label(grpInternationalisation, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText(Messages.CompositeBusinessModel_5); //$NON-NLS-1$
		
		locale = new ComboViewer(grpInternationalisation, SWT.READ_ONLY);
		locale.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		locale.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<Locale> l = (List<Locale>)inputElement;
				return l.toArray(new Locale[l.size()]);
			}

			public void dispose() {
				
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
			}
			
		});
		locale.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				return ((Locale)element).getDisplayName();
			}
			
		});
		locale.setInput(Activator.getDefault().getModel().getLocales());
		try{
			locale.setSelection(new StructuredSelection(Locale.getDefault()));
		}catch(Exception ex){
			
		}

		locale.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection)locale.getSelection();
				
				if (ss.isEmpty() || filter == null){
					return;
				}
				String s = filter.getOutputName((Locale)ss.getFirstElement());
				if (s == null){
					s= ""; //$NON-NLS-1$
				}
				outputName.setText(s);
				
			}
			
		});
		
		
		Label l4 = new Label(grpInternationalisation, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l4.setText(Messages.CompositeBusinessModel_7); //$NON-NLS-1$
		
		outputName = new Text(grpInternationalisation, SWT.BORDER);
		outputName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		outputName.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				IStructuredSelection ss = (IStructuredSelection)locale.getSelection();
				
				if (ss.isEmpty()){
					return;
				}
				filter.setOutputName((Locale)ss.getFirstElement(), outputName.getText());
				notifyListeners(SWT.Selection, new Event());
			}
			
		});
		
		if (containApply){
			ok = new Button(this, SWT.PUSH);
			ok.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true,	false));
			ok.setText(Messages.CompositeComplexFilter_15); //$NON-NLS-1$
			ok.addSelectionListener(new SelectionAdapter(){
				
				public void widgetSelected(SelectionEvent e) {
					getFilter();
					if (v != null){
						v.refresh();
						Activator.getDefault().setChanged();
					}
					cancel.setEnabled(false);
					ok.setEnabled(false);
				}
				
			});
			ok.setEnabled(false);
			
			cancel = new Button(this, SWT.PUSH);
			cancel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true,	false));
			cancel.setText(Messages.CompositeComplexFilter_16); //$NON-NLS-1$
			cancel.addSelectionListener(new SelectionAdapter(){
				
				public void widgetSelected(SelectionEvent e) {
					fillDatas();
					cancel.setEnabled(false);
					ok.setEnabled(false);
				}
				
			});
			cancel.setEnabled(false);
			
			addListener(SWT.Selection, new Listener() {
				
				public void handleEvent(Event event) {
					if (!isFilling){
						ok.setEnabled(isFilled());
						cancel.setEnabled(true);
					}
					
				}
			});
		}
		createContentAssist();
		return parent;
	}
	private void createContentAssist(){
		List<Variable>  vars = Activator.getDefault().getModel().getVariables();
		
		String[] symbols = new String[vars.size()];
		for(int i = 0; i < vars.size(); i++){
			symbols[i] = vars.get(i).getSymbol();
		}
		
		proposalAdapter = new ContentProposalAdapter(
				query, 
				new TextContentAdapter(), 
				new SimpleContentProposalProvider(symbols),
				null, 
				null);;
	}
	private void fillDatas(){
		isFilling = true;
		
		if (filter == null){
			isFilling = false;
			return;
		}
		
		
		
		name.setText(filter.getName());
		origin.setText(filter.getOrigin().getName());
		query.setText(filter.getQuery());
	
		
		
		
		
		for(Secu s : (List<Secu>)tableGroups.getInput()){
			if (filter.isGrantedFor(s.groupName)){
				s.visible = true;
				
				tableGroups.setChecked(s, true);
			}
			else{
				s.visible = false;
				tableGroups.setChecked(s, false);
			}
		}
		
		List<Secu> i =  (List<Secu>)tableGroups.getInput();
		for(Secu s : i){
			tableGroups.setChecked(s, s.visible);
		}
	
		tableGroups.refresh();
		Object[] c = tableGroups.getCheckedElements();
		
		isFilling = false;
	}
	
	public SqlQueryFilter getFilter(){
		if (filter == null){
			filter = new SqlQueryFilter();
		}
		
		filter.setName(name.getText());
		filter.setOrigin(ori);
		filter.setQuery(query.getText());
		
		
		
		List<Secu> groups = (List<Secu>)tableGroups.getInput();
		for(Secu s : groups){
			if (tableGroups.getChecked(s)){
				s.visible = true;
				filter.setGranted(s.groupName, true);
				
			}
			else{
				s.visible = false;
				filter.setGranted(s.groupName, false);
			}
		}
		
		
		return filter;
	}
	

	/**
	 * 
	 * @return true if the composite is rightly filled
	 */
	public boolean isFilled(){
		return !errorLabel.isVisible() && !name.getText().trim().equals("") && ori != null && !query.getText().trim().equals("") ; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	private class Secu{
		public String groupName;
		Boolean visible;
	}
}
