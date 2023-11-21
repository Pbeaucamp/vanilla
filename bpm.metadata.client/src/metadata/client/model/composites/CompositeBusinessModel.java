package metadata.client.model.composites;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import metadata.client.helper.GroupHelper;
import metadata.client.i18n.Messages;
import metadataclient.Activator;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import bpm.metadata.layer.business.BusinessModel;

public class CompositeBusinessModel extends Composite {

	private Text name;
	private Text description;
	
	private CheckboxTreeViewer tableGroups;
	
	private Viewer viewer;
	private boolean containApply = false;
	private BusinessModel model;
	
	private ComboViewer locale;
	private Text outputName;
	
	private int begin = 0;
	private int step = 1000;
	
	public CompositeBusinessModel(Composite parent, int style, Viewer viewer, boolean containApply, BusinessModel model) {
		super(parent, style);
		this.setLayout(new GridLayout());
		this.containApply = containApply;
		this.model = model;
		this.viewer = viewer;
		
		
		TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

		TabItem itemGal = new TabItem(tabFolder, SWT.NONE, 0);
		itemGal.setText(Messages.CompositeBusinessModel_0); //$NON-NLS-1$
		itemGal.setControl(createGeneral(tabFolder));
    	
    	TabItem itemSec = new TabItem(tabFolder, SWT.NONE, 1);
    	itemSec.setText(Messages.CompositeBusinessModel_1); //$NON-NLS-1$
    	itemSec.setControl(createSecurity(tabFolder));
    	
    	setInput();
    	fillData();
	}
	private Control createToolbar(Composite parent){
		ToolBar toolbar = new ToolBar(parent, SWT.NONE);
		toolbar.setLayoutData(new GridData());
		
		ToolItem checkAll = new ToolItem(toolbar, SWT.PUSH);
		checkAll.setToolTipText("Check All"); //$NON-NLS-1$
		checkAll.setImage(Activator.getDefault().getImageRegistry().get("check"));
		checkAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				
				tableGroups.setAllChecked(true);
				for(Object o : ((IStructuredContentProvider)tableGroups.getContentProvider()).getElements(tableGroups.getInput())){
					model.setGranted(((Secu)o).groupName, true);
				}
				notifyListeners(SWT.Selection, new Event());
			}
		});
		
		ToolItem uncheckAll = new ToolItem(toolbar, SWT.PUSH);
		uncheckAll.setToolTipText("Uncheck All"); //$NON-NLS-1$
		uncheckAll.setImage(Activator.getDefault().getImageRegistry().get("uncheck"));
		uncheckAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				tableGroups.setAllChecked(false);
				for(Object o : ((IStructuredContentProvider)tableGroups.getContentProvider()).getElements(tableGroups.getInput())){
					model.setGranted(((Secu)o).groupName, false);
				}
				notifyListeners(SWT.Selection, new Event());
			}
		});
		
		return toolbar;
	}
	private Control createSecurity(TabFolder folder){
		Composite parent = new Composite(folder, SWT.NONE);
		parent.setLayout(new GridLayout(2, false));
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l0 = new Label(parent, SWT.NONE);
		l0.setLayoutData(new GridData());
		l0.setText(Messages.CompositeBusinessModel_2); //$NON-NLS-1$
		
		createToolbar(parent);
		
		tableGroups = new CheckboxTreeViewer(parent, SWT.V_SCROLL | SWT.VIRTUAL);
		tableGroups.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
		tableGroups.setContentProvider(new ITreeContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<Secu> list = (List<Secu>) inputElement;
				return list.toArray(new Secu[list.size()]);
			}

			public void dispose() {
				
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
			}
			@Override
			public Object[] getChildren(Object parentElement) {
				return null;
			}

			@Override
			public Object getParent(Object element) {
				return null;
			}

			@Override
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

		tableGroups.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				if (tableGroups.getSelection().isEmpty()){
					return;
				}
			}
			
		});		
		
		return parent;
	}

	
	private Control createGeneral(TabFolder folder){
		
		Composite parent = new Composite(folder, SWT.NONE);
		parent.setLayout(new GridLayout(3, true));
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.CompositeBusinessModel_3); //$NON-NLS-1$
		
		name = new Text(parent, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		Label l2 = new Label(parent, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true, 1, 2));
		l2.setText(Messages.CompositeBusinessModel_4); //$NON-NLS-1$
		
		description = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		description.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 2));

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
		locale.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection)locale.getSelection();
				
				if (ss.isEmpty()){
					return;
				}
				String s = model.getOuputName((Locale)ss.getFirstElement());
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
				model.setOutputName((Locale)ss.getFirstElement(), outputName.getText());
				
			}
			
		});
	
		
				
		if (containApply){
			Composite bar = new Composite(this, SWT.NONE);
			bar.setLayout(new GridLayout(2, true));
			bar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
			
			Button b = new Button(bar, SWT.PUSH);
			b.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			b.setText(Messages.CompositeBusinessModel_8); //$NON-NLS-1$
			b.addSelectionListener(new SelectionAdapter(){

				@Override
				public void widgetSelected(SelectionEvent e) {
					setData();
					if (viewer!= null){
						viewer.refresh();
						Activator.getDefault().setChanged();
					}
				}
				
			});
			
			Button b2 = new Button(bar, SWT.PUSH);
			b2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			b2.setText(Messages.CompositeBusinessModel_9); //$NON-NLS-1$
			b2.addSelectionListener(new SelectionAdapter(){

				@Override
				public void widgetSelected(SelectionEvent e) {
					fillData();
				}
				
			});
			
		}
		return parent;
	}
	
	private void fillData(){
		name.setText(model.getName());
		description.setText(model.getDescription());
		for(Secu s : (List<Secu>)tableGroups.getInput()){
			if (model.isGrantedFor(s.groupName)){
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
		viewer.refresh();
		
		//XXX Leave it else it doesn't work.
		//Don't ask...
		Object[] c = tableGroups.getCheckedElements();
		
	}
	
	private void setData(){
		List<Secu> groups = (List<Secu>)tableGroups.getInput();
		
		for(Secu s : groups){
			if (tableGroups.getChecked(s)){
				s.visible = true;
				model.setGranted(s.groupName, true);
				
			}
			else{
				s.visible = false;
				model.setGranted(s.groupName, false);
			}
		}
		model.setName(name.getText());
		model.setDescription(description.getText());
	}
	
	private void setInput(){
		List<String> groups = GroupHelper.getGroups(begin, step);
		
		List<Secu> l = new ArrayList<Secu>();
		
		for(String s : groups){
			Secu c = new Secu();
			c.groupName = s;
			c.visible = false;
			l.add(c);
		}
		tableGroups.setInput(l);
		fillData();
	}


	private class Secu{
		public String groupName;
		Boolean visible;
	}
}
