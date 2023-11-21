package bpm.fd.repository.ui.dialogs;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.FdProjectRepositoryDescriptor;
import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.IResource;
import bpm.fd.api.core.model.tools.ModelLoader;
import bpm.fd.repository.ui.Messages;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.repository.ui.Activator;
import bpm.vanilla.repository.ui.dialogs.DialogShowDependencies;
import bpm.vanilla.repository.ui.icons.IconsRegistry;

public class DialogUpdate extends Dialog{

	private CheckboxTableViewer viewer;
	private FdProject  project;
	private IRepositoryApi sock;
	private IVanillaAPI vanillaApi;
	private Repository rep;
	private ComboViewer group;
	
	private HashMap<Object, RepositoryItem> haveDependencies = new HashMap<Object, RepositoryItem>();
	
	private Button btnDependencies;
	
	public DialogUpdate(Shell parentShell, FdProject project, IRepositoryApi sock, IVanillaAPI vanillaApi, Repository rep) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.project = project;
		this.sock = sock;
		this.rep = rep;
		this.vanillaApi = vanillaApi;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
	
		ToolBar tb = new ToolBar(c, SWT.NONE);
		tb.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		
		ToolItem it = new ToolItem(tb, SWT.PUSH);
		it.setText("Check");
		it.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				viewer.setAllChecked(true);
			}
		});
		
		ToolItem cit = new ToolItem(tb, SWT.PUSH);
		cit.setText("Uncheck");
		cit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				viewer.setAllChecked(false);
			}
		});
		
		Label l = new Label(c, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DialogUpdate_0);
		
		group = new ComboViewer(c, SWT.READ_ONLY);
		group.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		group.setContentProvider(new ArrayContentProvider());
		group.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Group)element).getName();
			}
		});
		
		l = new Label(c, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l.setText(Messages.DialogUpdate_1);
		
		btnDependencies = new Button(c, SWT.PUSH);
		btnDependencies.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false, 1, 1));
		btnDependencies.setImage(Activator.getDefault().getImageRegistry().get(IconsRegistry.DEPENDENCIES));
		btnDependencies.setToolTipText("Show dependencies");
		btnDependencies.setEnabled(false);
		
		btnDependencies.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Object o = ((IStructuredSelection)viewer.getSelection()).getFirstElement();
				
				DialogShowDependencies dial = new DialogShowDependencies(getShell(), haveDependencies.get(o), sock, DialogShowDependencies.TITLE);
				try {
					dial.hasDependencies();
					dial.open();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		viewer = CheckboxTableViewer.newCheckList(c, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		viewer.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<Object> l = new ArrayList<Object>();
				l.add(project.getDictionary());
				l.add(project.getFdModel());
				if (project instanceof MultiPageFdProject){
					l.addAll(((MultiPageFdProject)project).getPagesModels());
				}
				
				for(IResource r : project.getResources()){
					l.add(r);
				}
				
				
				return l.toArray(new Object[l.size()]);
			}

			public void dispose() {
				
				
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
		});
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		viewer.addCheckStateListener(new ICheckStateListener(){

			public void checkStateChanged(CheckStateChangedEvent event) {
				if (getBackground(event.getElement()) == null){
					
				}
				else{
					viewer.setChecked(event.getElement(), true);
				}
				
			}
			
		});
		
		TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
		col.getColumn().setText(Messages.DialogUpdate_2);
		col.getColumn().setWidth(200);
		col.setLabelProvider(new ColumnLabelProvider(){

			@Override
			public String getText(Object element) {
				if (element instanceof Dictionary){
					return Messages.DialogUpdate_3;
				}
				else if (element instanceof FdModel){
					
					return Messages.DialogUpdate_4;
				}
				else if (element instanceof IResource){
					return Messages.DialogUpdate_5;
				}
				return ""; //$NON-NLS-1$
			}

			@Override
			public Color getBackground(Object element) {
				return DialogUpdate.this.getBackground(element);
			}
			
			
			
		});
		
		col = new TableViewerColumn(viewer, SWT.NONE);
		col.getColumn().setText(Messages.DialogUpdate_7);
		col.getColumn().setWidth(200);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public Color getBackground(Object element) {
				return DialogUpdate.this.getBackground(element);
			}
			@Override
			public String getText(Object element) {
				if (element instanceof Dictionary){
					return ((Dictionary)element).getName();
				}
				else if (element instanceof FdModel){
					
					return ((FdModel)element).getName();
				}
				else if (element instanceof IResource){
					return ((IResource)element).getName();
				}
				return element.toString();
			}
			
		});

		col = new TableViewerColumn(viewer, SWT.NONE);
		col.getColumn().setText(Messages.DialogUpdate_8);
		col.getColumn().setWidth(200);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public Color getBackground(Object element) {
				return DialogUpdate.this.getBackground(element);
			}
			@Override
			public String getText(Object element) {
				if (getBackground(element) != null){
					return Messages.DialogUpdate_9;
				}
				return Messages.DialogUpdate_10;
			}
			
		});
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Object o = ((IStructuredSelection)event.getSelection()).getFirstElement();
				if(haveDependencies.keySet().contains(o)) {
					btnDependencies.setEnabled(true);
				}
				else {
					btnDependencies.setEnabled(false);
				}
			}
		});
		
		return c;
	}
	
	private Color getBackground(Object o){
		FdProjectRepositoryDescriptor desc = (FdProjectRepositoryDescriptor)project.getProjectDescriptor();
		if (o == project.getDictionary()){
			if (desc.getDictionaryDirectoryItemId() != null && desc.getDictionaryDirectoryItemId().intValue() > 0){
				return null;
			}
		}
		else if (o == project.getFdModel()){
			if (desc.getModelDirectoryItemId() != null && desc.getModelDirectoryItemId().intValue() > 0){
				return null;
			}
		}
		else if ( o instanceof FdModel){
			if (desc.getModelPageId((FdModel)o)!= null && desc.getModelPageId((FdModel)o).intValue() > 0){
				return null;
			}
		}
		else{
			if (desc.getResourceId((IResource)o) != null){
				return null;
			}
		}
		
		return ColorConstants.gray;
	}

	private void setViewerContent() throws Exception{
		List<Object> l = new ArrayList<Object>();
		l.add(project.getFdModel());
		l.add(project.getDictionary());
		
		
		if (project instanceof MultiPageFdProject){
			l.addAll(((MultiPageFdProject)project).getPagesModels());
		}
		
		
		for(IResource r : project.getResources()){
			l.add(r);
		}
		
		Repository rep = new Repository(sock);
		FdProjectRepositoryDescriptor desc = (FdProjectRepositoryDescriptor)project.getProjectDescriptor();
		for(Object obj : l) {
			Integer i = null;
			if(obj instanceof FdModel) {
				FdModel mod = (FdModel) obj;
				i = desc.getModelPageId(mod);
				if(i == null) {
					i = desc.getModelDirectoryItemId();
				}
			}
			else if(obj instanceof Dictionary) {
				i = desc.getDictionaryDirectoryItemId(); 
			}
			else if(obj instanceof IResource) {
				IResource res = (IResource) obj;
				i = desc.getResourceId(res);
			}
			
			if(i != null) {
				RepositoryItem it = rep.getItem(i);
				List<RepositoryItem> items = sock.getRepositoryService().getDependantItems(it);
				if(items != null && items.size() > 0) {
					
					haveDependencies.put(obj,it);
				}
			}
		}
		
		
		viewer.setInput(l);
		
		//set checked
		for(Object o : l){
			if (o instanceof IResource){
				if (((FdProjectRepositoryDescriptor)project.getProjectDescriptor()).getResourceId((IResource)o) == null){
					viewer.setChecked(o, true);
				}
			}
			else if (o instanceof FdModel){
				if (((FdProjectRepositoryDescriptor)project.getProjectDescriptor()).getModelPageId((FdModel)o) == null){
					viewer.setChecked(o, true);
				}
				
				
			}
		}
		
		try{
			group.setInput(vanillaApi.getVanillaSecurityManager().getGroups().toArray(new Group[vanillaApi.getVanillaSecurityManager().getGroups().size()]));
		}catch(Exception ex){
			ex.printStackTrace();
			MessageDialog.openError(getShell(), Messages.DialogUpdate_11, ex.getMessage());
		}
	}

	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogUpdate_12);
		getShell().setSize(700, 500);
		try {
			setViewerContent();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void okPressed() {
		
//			M
	
		List<Object> elementToUpdate = new ArrayList<Object>();
		List<Object> elementToAdd = new ArrayList<Object>();
		
		
		for(Object o : viewer.getCheckedElements()){
			if (getBackground(o) != null){
				elementToAdd.add(o);
			}
			else{
				elementToUpdate.add(o);
			}
			
		}
		try {
			ModelLoader.update(elementToUpdate, elementToAdd, project, sock, group.getCombo().getText());
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), Messages.DialogUpdate_13, e.getMessage());
		}
		
		
		
		
		super.okPressed();
	}
	
	
	
	
}
