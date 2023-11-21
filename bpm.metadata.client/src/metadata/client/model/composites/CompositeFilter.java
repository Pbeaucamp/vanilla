package metadata.client.model.composites;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import metadata.client.helper.GroupHelper;
import metadata.client.i18n.Messages;
import metadata.client.trees.TreeDataSource;
import metadata.client.trees.TreeDataStreamElement;
import metadata.client.trees.TreeObject;
import metadata.client.trees.TreeParent;
import metadata.client.trees.TreeResource;
import metadata.client.viewer.TreeContentProvider;
import metadata.client.viewer.TreeLabelProvider;
import metadataclient.Activator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
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
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.physical.sql.SQLColumn;
import bpm.metadata.resource.Filter;
import bpm.metadata.resource.IResource;

public class CompositeFilter extends Composite {
	private static final Color RED = new Color(Display.getDefault(), 255, 0, 0);
	private TreeViewer viewer;
	private ListViewer list;
	
	private Text name;
	private Filter filter;
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
	
	private boolean isOnOlap;
	private IDataStreamElement previouslySelected;
	
	public CompositeFilter(Composite parent, int style, Viewer viewer, boolean containApply, Filter lov, boolean isOnOlap) {
		super(parent, style);
		this.filter = lov;
		this.v = viewer;
		this.containApply = containApply;
		this.isOnOlap = isOnOlap;
		this.setLayout(new GridLayout(2, false));
		
		TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		errorLabel = new Label(this, SWT.NONE);
    	errorLabel.setLayoutData(new GridData(GridData.FILL, GridData.END, true, false, 2, 1));
    	errorLabel.setForeground(RED);
    	errorLabel.setVisible(false);
		
		TabItem itemGal = new TabItem(tabFolder, SWT.NONE, 0);
		itemGal.setText(Messages.CompositeFilter_0); //$NON-NLS-1$
		itemGal.setControl(createGeneral(tabFolder));
    	
       	TabItem itemSec = new TabItem(tabFolder, SWT.NONE, 1);
    	itemSec.setText(Messages.CompositeFilter_1); //$NON-NLS-1$
    	itemSec.setControl(createSecurity(tabFolder));

    	setInput();
		fillData();
		if (lov == null){
			lov = new Filter();
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
					filter.setGranted(((Secu)o).groupName, false);
				}
				notifyListeners(SWT.Selection, new Event());
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
				if (filter == null){
					return;
				}			
				Secu s = (Secu)event.getElement();
				
				boolean b = tableGroups.getChecked(s);
				filter.setGranted(s.groupName, b);
				
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
	
	private Control createGeneral(TabFolder folder){
		
		
		Composite c = new Composite(folder, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		Label l = new Label(c, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.CompositeFilter_2); //$NON-NLS-1$
		
		name = new Text(c, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		name.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				IResource r = Activator.getDefault().getModel().getResource(name.getText());
				if ( r != null && r != filter){
					setErrorMessage(Messages.CompositeFilter_9);
				}
				else{
					setErrorMessage(null);
				}
				notifyListeners(SWT.Selection, new Event());
				
			}
		});
		
		Label l2 = new Label(c, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l2.setText(Messages.CompositeFilter_3);  //$NON-NLS-1$
		
		Label l3 = new Label(c, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l3.setText(Messages.CompositeFilter_4); //$NON-NLS-1$
		
		viewer = new TreeViewer(c, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
				
				if (ss.isEmpty() || !(ss.getFirstElement() instanceof TreeDataStreamElement)){
					list.setInput(new ArrayList<String>());
					notifyListeners(SWT.Selection, new Event());
					return;
				}
				
				IDataStreamElement e = ((TreeDataStreamElement)ss.getFirstElement()).getDataStreamElement();
				
				previouslySelected = e;
				if (e.getOrigin() instanceof SQLColumn){
					if (((SQLColumn)e.getOrigin()).getSqlTypeCode() == Types.BOOLEAN || Boolean.class.getName().equals(((SQLColumn)e.getOrigin()).getClassName())){
						list.setInput(new ArrayList<String>());
						notifyListeners(SWT.Selection, new Event());
						return;
					}
				}
				try{
					list.setInput(e.getDistinctValues());
				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(getShell(), Messages.CompositeFilter_10,ex.getMessage());
				}

				notifyListeners(SWT.Selection, new Event());
			}
			
		});
		viewer.addFilter(new ViewerFilter() {
			
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof IResource || element instanceof TreeResource){
					return false;
				}
				return true;
			}
		});
		list = new ListViewer(c, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
		list.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		if(isOnOlap) {
			list.setLabelProvider(new LabelProvider() {
				@Override
				public String getText(Object element) {
					if(previouslySelected != null) {
						String el = (String) element;
						return el.substring(previouslySelected.getDataStream().getOrigin().getName().length() + 1);
						 
					}
					return super.getText(element);
				}
			});
		}
		list.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<String> l = (List<String>)inputElement;
				return l.toArray(new String[l.size()]);
			}

			public void dispose() {
				
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
			}
			
		});
		list.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				notifyListeners(SWT.Selection, new Event());
				
			}
		});
		
		Group grpInternationalisation = new Group(this, SWT.NONE);
		grpInternationalisation.setLayout(new GridLayout(2, false));
		grpInternationalisation.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		grpInternationalisation.setText("Internationalisation");
		
		
		Label _l4 = new Label(grpInternationalisation, SWT.NONE);
		_l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		_l4.setText("Locale"); //$NON-NLS-1$

		
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
				if (filter != null){
					String s = filter.getOutputName((Locale)ss.getFirstElement());
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
				filter.setOutputName((Locale)ss.getFirstElement(), outputName.getText());
				notifyListeners(SWT.Selection, new Event());
			}
			
		});
	

		if (containApply){
			ok = new Button(this, SWT.PUSH);
			ok.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true,	false));
			ok.setText(Messages.CompositeFilter_5); //$NON-NLS-1$
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
			ok.setEnabled(false);
			
			cancel = new Button(this, SWT.PUSH);
			cancel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true,	false));
			cancel.setText(Messages.CompositeFilter_6); //$NON-NLS-1$
			cancel.addSelectionListener(new SelectionAdapter(){
				
				public void widgetSelected(SelectionEvent e) {
					fillData();
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
		if (filter == null){
			filter= new Filter();
		}
		
		filter.setName(name.getText());
		IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
		if (ss.getFirstElement() instanceof TreeDataStreamElement){
			filter.setOrigin(((TreeDataStreamElement)ss.getFirstElement()).getDataStreamElement()); 
		}
		
		ss = (IStructuredSelection)list.getSelection();
		List<String> l = new ArrayList<String>();
		for(Object o : ss.toList()){
			l.add(o + ""); //$NON-NLS-1$
		}
		
		filter.setValues(l);
		
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
	}
	
	public boolean isFilled(){
		return !errorLabel.isVisible() && !name.getText().trim().equals("") && !viewer.getSelection().isEmpty() && !list.getSelection().isEmpty(); //$NON-NLS-1$
	}
	
	public Filter getFilter(){
		return filter;
	}
	
	private void fillData(){
		if (filter != null){
			isFilling = true;
			name.setText(filter.getName());
			
			TreeParent root = (TreeParent)viewer.getInput();
			TreeObject ds = root.getChildNamed(filter.getOrigin().getDataStream().getDataSource().getName());
			TreeObject t = ((TreeParent)ds).getChildNamed(filter.getOrigin().getDataStream().getName());
			TreeObject c = ((TreeParent)t).getChildNamed(filter.getOrigin().getName());
			viewer.setSelection(new StructuredSelection(c));
			
		
			list.setSelection(new StructuredSelection(filter.getValues()));
			
			
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
			
			isFilling = false;
		}
	}
	
	private class Secu{
		public String groupName;
		Boolean visible;
	}
}
