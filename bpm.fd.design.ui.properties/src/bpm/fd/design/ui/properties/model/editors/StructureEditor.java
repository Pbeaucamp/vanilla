package bpm.fd.design.ui.properties.model.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.events.ExpandListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.TreeColumn;

import bpm.fd.api.core.model.events.ElementsEventType;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.structure.IStructureElement;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.editor.style.DialogCssProperties;
import bpm.fd.design.ui.properties.i18n.Messages;
import bpm.fd.design.ui.properties.model.Property;
import bpm.fd.design.ui.properties.model.PropertyGroup;
import bpm.fd.design.ui.properties.viewer.PropertiesContentProvider;
import bpm.fd.design.ui.properties.viewer.PropertyLabelProvider;
import bpm.fd.design.ui.tools.ColorManager;

public class StructureEditor implements IPropertyEditor{
	
	private class EventDialogCellEditor extends DialogCellEditor{
		 protected EventDialogCellEditor(Composite parent) {
		        super(parent);
		  }
		@Override
		protected Object openDialogBox(Control cellEditorWindow) {
			DialogEvent d = new DialogEvent(cellEditorWindow.getShell(), (String)getValue());
			
			if (d.open() == DialogEvent.OK){
				return d.getContent();
			}
			return null;
		}
		
	}
	
	
	private static final Color used = ColorManager.getColorRegistry().get(ColorManager.EVENT_USED);
	protected List<TreeViewer> viewers = new ArrayList<TreeViewer>();
	private ExpandBar bar;
	
	private IStructureElement struct;
	private EditPart editPart;
	private PropertyGroup cssClasses;
	private CellEditor cssDialogEditor;
	private TreeViewer cssViewer;
	public StructureEditor(Composite parent){
		bar = new ExpandBar(parent, SWT.V_SCROLL);
		bar.addControlListener(new ControlListener() {
			
			@Override
			public void controlResized(ControlEvent e) {
				resize();
				
			}
			
			@Override
			public void controlMoved(ControlEvent e) {
				
				
			}
		});
		bar.addExpandListener(new ExpandListener() {
			
			@Override
			public void itemExpanded(ExpandEvent e) {
				
				ExpandItem it = (ExpandItem)e.item;
				it.getControl().pack(true);
				Point size = it.getControl().computeSize(bar.getClientArea().width,
						SWT.DEFAULT);
						
				it.getControl().pack(true);
//				it.setHeight(it.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
				it.setHeight(size.y);
				resize();
				
			}
			
			@Override
			public void itemCollapsed(ExpandEvent e) {
			}
		});
	
		createGeneral();
		createEvents();
		createCss();
	}
	

	final public ExpandBar getControl(){
		return bar;
	}
	
	protected IStructureElement getStructureElement(){
		return struct;
	}
	private void setCssClasses(String classes){
		getStructureElement().setCssClass(classes);
		cssClasses.clear();
		if (classes == null){
			return;
		}
		for(String s : classes.split(" ")){ //$NON-NLS-1$
			if (s.trim().length() > 0){
				final String val = s.trim();
				Property p = new Property(Messages.StructureEditor_1, cssDialogEditor){
					@Override
					public String getValue() {
						return val;
					}
				};
				cssClasses.add(p);
				
			}
		}
		cssViewer.refresh();
		
	}
	private void createCss(){
		cssViewer = createViewer(getControl());
		TreeViewerColumn valueCol = createValueViewerColum(cssViewer);

		cssDialogEditor = new DialogCellEditor(cssViewer.getTree()) {
			
			@Override
			protected Object openDialogBox(Control cellEditorWindow) {
				DialogCssProperties d = new DialogCssProperties(cellEditorWindow.getShell(), (String)getValue());
				d.open();
				return null;
			}
		};
		
		
		ExpandItem item = new ExpandItem(getControl(), SWT.NONE);
		item.setControl(cssViewer.getTree());
		item.setText(Messages.StructureEditor_2);
		
		cssClasses = new PropertyGroup(Messages.StructureEditor_3, new TextCellEditor(cssViewer.getTree())); 
			
		valueCol.setLabelProvider(new ColumnLabelProvider(){
			
			public String getText(Object element) {
				if (getStructureElement() == null){return "";} //$NON-NLS-1$
				if (cssClasses == element){
					return getStructureElement().getCssClass();
				}
				return ((Property)element).getValue();
			}
		});
		
		
		valueCol.setEditingSupport(new EditingSupport(cssViewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				if (element == cssClasses){setCssClasses((String)value);}
				notifyChangeOccured();
				cssViewer.refresh();
				
			}
			
			@Override
			protected Object getValue(Object element) {
				if (element == cssClasses){return getStructureElement().getCssClass();}
				else{
					return ((Property)element).getValue();
				}
//				return null;
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return ((Property)element).getCellEditor();
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});
		List input = new ArrayList();
		input.add(cssClasses);
		cssViewer.setInput(input);
		
	}
	
	private void createGeneral(){
		final TreeViewer viewer = createViewer(bar);
		TreeViewerColumn valueCol = createValueViewerColum(viewer);

		ExpandItem item = new ExpandItem(bar, SWT.NONE);
		item.setControl(viewer.getTree());
		item.setText(Messages.StructureEditor_5);
		
		final Property name = new Property(Messages.StructureEditor_6, new TextCellEditor(viewer.getTree())); 
	
		valueCol.setLabelProvider(new ColumnLabelProvider(){
			public String getText(Object element) {
				if (struct == null){
					return ""; //$NON-NLS-1$
				}
				if (element == name){
					return (struct).getName();
				}
				return ""; //$NON-NLS-1$
			}
		});
		
		
		
		List input = new ArrayList();
		input.add(name);
		viewer.setInput(input);
		
	}
	
	final protected TreeViewerColumn createValueViewerColum(TreeViewer viewer){
		TreeViewerColumn value = new TreeViewerColumn(viewer, SWT.LEFT);
		value.getColumn().setWidth(100);
		return value;
	}
	
	protected void fillBar(){
		
	}
	
	/**
	 * refresh the viewers can be overriden to init some CellEditors
	 * must be called when overriden
	 */
	public void setInput(EditPart editPart, IStructureElement struc){
		this.struct = struc;
		this.editPart = editPart;
		
		eventViewer.setInput(new ArrayList(struct.getEventsType()));
		setCssClasses(getStructureElement().getCssClass());
		for(TreeViewer v : viewers){
			v.refresh();
		}
	}

	final protected TreeViewer createViewer(Composite parent) {
		TreeViewer viewer = new TreeViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION);
		viewer.getTree().setBackground(parent.getBackground());
		viewer.getTree().setLinesVisible(true);
		viewer.setAutoExpandLevel(TreeViewer.ALL_LEVELS);
		viewer.setContentProvider(new PropertiesContentProvider());
		//viewer.addFilter(filter);
		TreeViewerColumn label = new TreeViewerColumn(viewer, SWT.LEFT);

		label.setLabelProvider(new PropertyLabelProvider());

		viewers.add(viewer);

		
		return viewer;
	}
	
	
	
	public void resize() {
		//bar.layout();
		for(TreeViewer viewer : viewers){
			viewer.refresh();
			int count = 0;
			for (TreeColumn tc : viewer.getTree().getColumns()) {

				if (count == 0) {
					tc.pack();
					count = tc.getWidth();
				} else {
					tc.setWidth(viewer.getTree().getBounds().width - count
							- viewer.getTree().getBorderWidth() * 3);
				}
				count++;
			}
			viewer.expandAll();
		}
		
	}
	
	
	public void notifyChangeOccured(){
		if (getStructureElement() != null){
			Activator.getDefault().getProject().getDictionary().firePropertyChange(Dictionary.PROPERTY_COMPONENT_CHANGED, null, getStructureElement());
		}
		if (editPart != null){
			editPart.refresh();
		}
	}
	
	
	protected void resizeItem(ExpandItem item){
		if (item.getExpanded()){
			
			item.getControl().pack(true);
			Point size = item.getControl().computeSize(bar.getClientArea().width,
					SWT.DEFAULT);
					
			item.getControl().pack(true);
//			it.setHeight(it.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
			item.setHeight(size.y);
//			resize();
		}
	}
	

	
	private TreeViewer eventViewer;
	
	private void createEvents(){
		eventViewer = new TreeViewer(bar, SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION);
		eventViewer.getTree().setBackground(bar.getBackground());
		eventViewer.getTree().setLinesVisible(true);
		eventViewer.setAutoExpandLevel(TreeViewer.ALL_LEVELS);
		eventViewer.setContentProvider(new PropertiesContentProvider());
		viewers.add(eventViewer);
		TreeViewerColumn label = new TreeViewerColumn(eventViewer, SWT.RIGHT);
		//label.getColumn().setWidth(100);
		label.setLabelProvider(new PropertyLabelProvider(){
			@Override
			public Color getForeground(Object element) {
				if (getStructureElement() == null){return null;}
				
				String s = getStructureElement().getJavaScript((ElementsEventType)element);
				if (s != null && !s.trim().isEmpty()){
					return used;
				}
				return null;
			}
		});

		TreeViewerColumn valueCol = createValueViewerColum(eventViewer);
		
		ExpandItem item = new ExpandItem(bar, SWT.NONE);
		item.setControl(eventViewer.getTree());
		item.setText(Messages.StructureEditor_9);
		
		final EventDialogCellEditor editor = new EventDialogCellEditor(eventViewer.getTree());
		
		valueCol.setLabelProvider(new ColumnLabelProvider(){
			public String getText(Object element) {
				if (getStructureElement() == null){return "";} //$NON-NLS-1$
				if (getStructureElement().getJavaScript((ElementsEventType)element) != null){
					return "..."; //$NON-NLS-1$
				}
				return ""; //$NON-NLS-1$
			}
			
			
		});
		
		
		valueCol.setEditingSupport(new EditingSupport(eventViewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				if (getStructureElement() == null){return ;}
				getStructureElement().setJavaScript((ElementsEventType)element, (String)value);
				notifyChangeOccured();
				eventViewer.refresh();
				
			}
			
			@Override
			protected Object getValue(Object element) {
				if (getStructureElement() == null){return null;}
				return getStructureElement().getJavaScript((ElementsEventType)element);

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
		
	}


	@Override
	public void filter(String propertyName) {
		/*_filterValue = propertyName;
		for(TreeViewer v : viewers){
			v.refresh();
		}*/
		
	}
	/*
	
	private String _filterValue;
	private ViewerFilter filter = new ViewerFilter(){

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			if (_filterValue == null){
				return true;
			}
			if (element instanceof Property){
				return ((Property)element).getName().contains(_filterValue);
			}
			return true;
		}
		
	};*/


	@Override
	public boolean next(String text) {
		
		return false;
	}
}
