package metadata.client.model.composites;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import metadata.client.helper.GroupHelper;
import metadata.client.i18n.Messages;
import metadata.client.trees.TreeDataSource;
import metadata.client.trees.TreeDataStreamElement;
import metadata.client.trees.TreeObject;
import metadata.client.trees.TreeParent;
import metadata.client.viewer.TreeContentProvider;
import metadata.client.viewer.TreeLabelProvider;
import metadataclient.Activator;

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
import org.eclipse.jface.viewers.TreeViewer;
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

import bpm.metadata.MetaData;
import bpm.metadata.layer.logical.AbstractDataSource;
import bpm.metadata.resource.IResource;
import bpm.metadata.resource.ListOfValue;

public class CompositeLoV extends Composite {
	private static final Color RED = new Color(Display.getDefault(), 255, 0, 0);

	private TreeViewer viewer;
	private Text name;
	private ListOfValue lov;
	private Viewer v;
	private boolean containApply = false;
	
	private static int begin = 725;
	private static int step = 20;
	
	private CheckboxTreeViewer tableGroups;
	
	private Label errorLabel;
	private Button ok, cancel;
	private boolean isFilling = false;
	
	private ComboViewer locale;
	private Text outputName;
	
	public CompositeLoV(Composite parent, int style, Viewer viewer, boolean containApply, ListOfValue lov) {
		super(parent, style);
		this.lov = lov;
		this.v = viewer;
		this.containApply = containApply;
		
		this.setLayout(new GridLayout(2, false));
		
		TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true , 2, 1));

		errorLabel = new Label(this, SWT.NONE);
    	errorLabel.setLayoutData(new GridData(GridData.FILL, GridData.END, true, false, 2, 1));
    	errorLabel.setForeground(RED);
    	errorLabel.setVisible(false);

		
		TabItem itemGal = new TabItem(tabFolder, SWT.NONE, 0);
		itemGal.setText(Messages.CompositeLoV_0); //$NON-NLS-1$
		itemGal.setControl(createGeneral(tabFolder));
    	
       	TabItem itemSec = new TabItem(tabFolder, SWT.NONE, 1);
    	itemSec.setText(Messages.CompositeLoV_1); //$NON-NLS-1$
    	itemSec.setControl(createSecurity(tabFolder));

    	setInput();
		fillData();
		if (lov == null){
			lov = new ListOfValue();
		}
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

	private Control createGeneral(TabFolder folder){
		Composite c = new Composite(folder, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		
		Label l = new Label(c, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l.setText(Messages.CompositeLoV_2); //$NON-NLS-1$
		
		name = new Text(c, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		name.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				IResource r = Activator.getDefault().getModel().getResource(name.getText());
				if ( r != null && r != lov){
					setErrorMessage(Messages.CompositeLoV_6);
				}
				else{
					setErrorMessage(null);
				}
				notifyListeners(SWT.Selection, new Event());
				
			}
		});
		Label l2 = new Label(c, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		l2.setText(Messages.CompositeLoV_3);  //$NON-NLS-1$
		
		viewer = new TreeViewer(c, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				notifyListeners(SWT.Selection, new Event());
				
			}
		});
		
		Group grpInternationalisation = new Group(this, SWT.NONE);
		grpInternationalisation.setLayout(new GridLayout(2, false));
		grpInternationalisation.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		grpInternationalisation.setText("Internationalisation");
		
		Label l3 = new Label(grpInternationalisation, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText(Messages.CompositeBusinessModel_5); //$NON-NLS-1$
		
		locale = new ComboViewer(grpInternationalisation, SWT.READ_ONLY);
		locale.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
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
				
				if (ss.isEmpty()){
					return;
				}
				
				if (lov != null){
					String s = lov.getOutputName((Locale)ss.getFirstElement());
					if (s == null){
						s= ""; //$NON-NLS-1$
					}
					outputName.setText(s);
				}
			
				
			}
			
		});
		
		
		Label l4 = new Label(grpInternationalisation, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l4.setText(Messages.CompositeBusinessModel_7); //$NON-NLS-1$
		
		outputName = new Text(grpInternationalisation, SWT.BORDER);
		outputName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		outputName.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				IStructuredSelection ss = (IStructuredSelection)locale.getSelection();
				
				if (ss.isEmpty()){
					return;
				}
				lov.setOutputName((Locale)ss.getFirstElement(), outputName.getText());
				notifyListeners(SWT.Selection, new Event());
			}
			
		});
		
		if (containApply){
			ok = new Button(this, SWT.PUSH);
			ok.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true,	false));
			ok.setText(Messages.CompositeLoV_4); //$NON-NLS-1$
			ok.setEnabled(false);
			ok.addSelectionListener(new SelectionAdapter(){
				
				public void widgetSelected(SelectionEvent e) {
					setData();
					if (v != null){
						v.refresh();
						Activator.getDefault().setChanged();
					}
					cancel.setEnabled(false);
					ok.setEnabled(false);
				}
				
			});
			
			cancel = new Button(this, SWT.PUSH);
			cancel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true,	false));
			cancel.setText(Messages.CompositeLoV_5); //$NON-NLS-1$
			cancel.setEnabled(false);
			cancel.addSelectionListener(new SelectionAdapter(){
				
				public void widgetSelected(SelectionEvent e) {
					fillData();
					cancel.setEnabled(false);
					ok.setEnabled(false);
				}
				
			});
			
			addListener(SWT.Selection, new Listener() {
				
				public void handleEvent(Event event) {
					if (!isFilling){
						ok.setEnabled(isFilled());
						cancel.setEnabled(true);
					}
					
				}
			});
			
		}
		
		
	

		return c;
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
					lov.setGranted(((Secu)o).groupName, true);
				}
				notifyListeners(SWT.Selection, new Event());
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
					lov.setGranted(((Secu)o).groupName, false);
				}
				notifyListeners(SWT.Selection, new Event());
			}
		});
		
		return toolbar;
	}
	
	private Control createSecurity(TabFolder folder){
		Composite c = new Composite(folder, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		
		createToolbar(c);
		
		tableGroups = new CheckboxTreeViewer(c, SWT.V_SCROLL | SWT.VIRTUAL);
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
				if (lov == null){
					return;
				}				
				Secu s = (Secu)event.getElement();
				
				boolean b = tableGroups.getChecked(s);
				lov.setGranted(s.groupName, b);
				
			}
		});	
		
		List<String> groups = GroupHelper.getGroups(begin, step);
		
		List<Secu> l = new ArrayList<Secu>();
		
		for(String s : groups){
			Secu _c = new Secu();
			_c.groupName = s;
			_c.visible = false;
			l.add(_c);
		}
		tableGroups.setInput(l);
		
		return c;
	}
	
	private void setInput(){
		MetaData model = Activator.getDefault().getModel();
		
		TreeParent root = new TreeParent(""); //$NON-NLS-1$
		
		for(AbstractDataSource ds : model.getDataSources()){
			root.addChild(new TreeDataSource(ds, false));
		}
		viewer.setInput(root);
	}

	public void setData(){
		if (lov == null){
			lov = new ListOfValue();
		}
		
		lov.setName(name.getText());
		IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
		if (ss.getFirstElement() instanceof TreeDataStreamElement){
			lov.setOrigin(((TreeDataStreamElement)ss.getFirstElement()).getDataStreamElement()); 
		}
		List<Secu> groups = (List<Secu>)tableGroups.getInput();
		for(Secu s : groups){
			if (tableGroups.getChecked(s)){
				s.visible = true;
				lov.setGranted(s.groupName, true);
				
			}
			else{
				s.visible = false;
				lov.setGranted(s.groupName, false);
			}
		}
	}
	
	public ListOfValue getLov(){
		return lov;
	}
	
	private void fillData(){
		if (lov != null){
			isFilling = true;
			name.setText(lov.getName());
			
			TreeParent root = (TreeParent)viewer.getInput();
			TreeObject ds = root.getChildNamed(lov.getOrigin().getDataStream().getDataSource().getName());
			TreeObject t = ((TreeParent)ds).getChildNamed(lov.getOrigin().getDataStream().getName());
			TreeObject c = ((TreeParent)t).getChildNamed(lov.getOrigin().getName());
			viewer.setSelection(new StructuredSelection(c));
			
			for(Secu s : (List<Secu>)tableGroups.getInput()){
				if (lov.isGrantedFor(s.groupName)){
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
			isFilling = false;
		}
	}
	
	/**
	 * 
	 * @return true if the composite is rightly filled
	 */
	public boolean isFilled(){
		return !errorLabel.isVisible() && !name.getText().trim().equals("") && !viewer.getSelection().isEmpty(); //$NON-NLS-1$
	}
	
	private class Secu{
		public String groupName;
		Boolean visible;
	}
}
