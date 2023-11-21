package bpm.fd.design.ui.properties.model.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandItem;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.api.core.model.structure.Folder;
import bpm.fd.api.core.model.structure.FolderPage;
import bpm.fd.api.core.model.structure.IStructureElement;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.properties.i18n.Messages;
import bpm.fd.design.ui.properties.model.Property;
import bpm.fd.design.ui.properties.model.PropertyGroup;

public class FolderEditor extends StructureEditor{
	
	private class PropertyPage extends PropertyGroup{
		FolderPage page;
		Property model;
		Property title;
		Property name;
		public PropertyPage(FolderPage page, CellEditor cellEditor) {
			super(page.getName(), cellEditor);
			this.page = page;
			name = new Property(Messages.FolderEditor_0, new TextCellEditor(viewer.getTree()));
			model = new Property(Messages.FolderEditor_1, controllersCbo);
			title = new Property(Messages.FolderEditor_2, new TextCellEditor(viewer.getTree()));
				add(name);add(model);add(title);
		}
		
		public void setProperty(Property p, Object value){
			if (p == model){
				if (value == null){
					page.setModelName(null);
				}
				else{
					page.setModelName(((FdModel)value).getName());
				}
				
			}
			if (p == title){
				page.setTitle((String)value);
			}
			if (p == name){
				page.setName((String)value);
			}
		}
		
		public Object getPropertyValue(Property p){
			if (p == model){
				if (page.getContent().isEmpty()){
					return null;
				}
				else{
					return page.getContent().get(0);
				}
			}
			if (p == title){
				return page.getTitle();
			}
			if (p == name){
				return page.getName();
			}
			return null;
		}
		public String getPropertyValueString(Property p){
			if (p == model){
				if (page.getContent().isEmpty()){
					return ""; //$NON-NLS-1$
				}
				else{
					return page.getContent().get(0).getName();
				}
			}
			if (p == title){
				return page.getTitle();
			}
			if (p == name){
				return page.getName();
			}
			return ""; //$NON-NLS-1$
		}
	}
	
	
	private TreeViewer viewer;
	private ExpandItem item;
	private ComboBoxViewerCellEditor controllersCbo;
	private PropertyGroup pages;

	
	public FolderEditor(Composite parent) {
		super(parent);
		fillBar();
	}

	@Override
	protected void fillBar() {
		
		super.fillBar();
		createController();
		
	}
	private void createController(){
		viewer = createViewer(getControl());

		TreeViewerColumn valueCol = createValueViewerColum(viewer);

		
		
		controllersCbo = new ComboBoxViewerCellEditor(viewer.getTree(), SWT.READ_ONLY);
		controllersCbo.getViewer().setContentProvider(new ArrayContentProvider());
		controllersCbo.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((FdModel)element).getName();
			}
		});
		
		
		
		item = new ExpandItem(getControl(), SWT.NONE);
		item.setControl(viewer.getTree());
		item.setText(Messages.FolderEditor_5);

		pages = new PropertyGroup(Messages.FolderEditor_6, null);
		
		
		valueCol.setLabelProvider(new ColumnLabelProvider(){
			public String getText(Object element) {
				if (element instanceof Property && ((Property)element).getParent() instanceof PropertyPage ){
					return ((PropertyPage)((Property)element).getParent()).getPropertyValueString((Property)element);
				}
				return ""; //$NON-NLS-1$
			}
		});
		
		valueCol.setEditingSupport(new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				if (element instanceof Property && ((Property)element).getParent() instanceof PropertyPage ){
					((PropertyPage)((Property)element).getParent()).setProperty((Property)element, value);
				}
				notifyChangeOccured();
				viewer.refresh();
				
			}
			
			@Override
			protected Object getValue(Object element) {
				if (element instanceof Property && ((Property)element).getParent() instanceof PropertyPage ){
					return ((PropertyPage)((Property)element).getParent()).getPropertyValue((Property)element);
				}
				return null;
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				controllersCbo.getViewer().refresh();
				return ((Property)element).getCellEditor();
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});
		
		MenuManager mgr = new MenuManager();
		Action addPage = new Action(Messages.FolderEditor_8){
			public void run(){
				FolderPage page = Activator.getDefault().getProject().getFdModel().getStructureFactory().createFolderPage(Messages.FolderEditor_9);
				((Folder)getStructureElement()).addToContent(page);
				pages.add(new PropertyPage(page, null));
				viewer.refresh();
			}
		};
		
		final Action removePage = new Action(Messages.FolderEditor_10){
			public void run(){
				PropertyPage p = ((PropertyPage)((IStructuredSelection)viewer.getSelection()).getFirstElement());
				FolderPage page = p.page;
				((Folder)getStructureElement()).removeFromContent(page);
				pages.remove(p);
				refreshPages();
				viewer.refresh();
			}
		};
		mgr.add(addPage);
		mgr.add(new Separator());
		mgr.add(removePage);
		mgr.addMenuListener(new IMenuListener() {
			
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				
				removePage.setEnabled(!viewer.getSelection().isEmpty() && ((IStructuredSelection)viewer.getSelection()).getFirstElement() instanceof PropertyPage);
				
			}
		});
		viewer.getTree().setMenu(mgr.createContextMenu(viewer.getControl()));
		
		List l = new ArrayList();
		l.add(pages);
		viewer.setInput(l);
	}
	
	private void refreshPages(){
		pages.clear();
		for(Object o : ((Folder)getStructureElement()).getContent()){
			pages.add(new PropertyPage((FolderPage)o, null));
		}
	}
	
	

	
	
	@Override
	public void setInput(EditPart editPart, IStructureElement struc) {
		super.setInput(editPart, struc);
		
		
		if (controllersCbo != null){
			controllersCbo.setInput(((MultiPageFdProject)Activator.getDefault().getProject()).getPagesModels());
		}
		refreshPages();
		
				
		resizeItem(item);
		resize();
	}
}

