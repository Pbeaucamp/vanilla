package bpm.fd.design.ui.properties.model.editors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
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

import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.comment.ComponentComment;
import bpm.fd.api.core.model.components.definition.jsp.ComponentD4C;
import bpm.fd.api.core.model.components.definition.jsp.ComponentFlourish;
import bpm.fd.api.core.model.components.definition.jsp.ComponentJsp;
import bpm.fd.api.core.model.components.definition.link.ComponentLink;
import bpm.fd.api.core.model.components.definition.report.ReportComponentParameter;
import bpm.fd.api.core.model.components.definition.text.LabelComponent;
import bpm.fd.api.core.model.events.ElementsEventType;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.FileProperties;
import bpm.fd.api.core.model.resources.IResource;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.editor.style.DialogCssProperties;
import bpm.fd.design.ui.properties.i18n.Messages;
import bpm.fd.design.ui.properties.model.Property;
import bpm.fd.design.ui.properties.model.PropertyFinder;
import bpm.fd.design.ui.properties.model.PropertyGroup;
import bpm.fd.design.ui.properties.model.PropertyGroupParameter;
import bpm.fd.design.ui.properties.model.PropertyI18n;
import bpm.fd.design.ui.properties.viewer.PropertiesContentProvider;
import bpm.fd.design.ui.properties.viewer.PropertyLabelProvider;
import bpm.fd.design.ui.tools.ColorManager;

public abstract class ComponentEditor implements IComponentEditor{
	
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
	private List<TreeViewer> viewers = new ArrayList<TreeViewer>();
	private ExpandBar bar;
	private TreeViewer parameterViewer;
	
	private IComponentDefinition def;
	private ComboBoxViewerCellEditor parameterProviderEditor;
	private EditPart editPart;
	
	private ExpandItem parameterItem;
	
	private PropertyFinder finder;
	private int finderViewerIndex = 0;
	private String currentSearch = null;
	
	public ComponentEditor(Composite parent){
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
		createCss();
		createI18n();
		createEvents();
		createParameters();
	}
	

	public boolean next(String text){
		if (text.trim().equals("")){ //$NON-NLS-1$
			return false;
		}
		if (!text.toLowerCase().trim().equals(currentSearch)){
			finder = null;
		}
		if (finder == null){
			currentSearch = text.toLowerCase().trim();
			finder = new PropertyFinder((List)viewers.get(finderViewerIndex).getInput());
			finder.find(currentSearch);
		}
		Object o = finder.getNext();
		if (o == null){
			if (finderViewerIndex < viewers.size() - 1){
				finderViewerIndex++;
				finder = null;
				return next(text);
			}
			else{
				finder = null;
				finderViewerIndex = 0;
				return false;
			}
		}
		else{
			if (!bar.getItem(finderViewerIndex).getExpanded()){
				bar.getItem(finderViewerIndex).setExpanded(true);
				resizeItem(bar.getItem(finderViewerIndex));
			}
			
			viewers.get(finderViewerIndex).setSelection(new StructuredSelection(o));
			
			return true;
		}
	}
	
	protected String[] getComponentParameterNames(){
		List<String> parameters = new ArrayList<String>();
		
		if (getComponentDefinition() != null){
			for(ComponentParameter pd : getComponentDefinition().getParameters()){
				parameters.add("{$" + pd.getName() + "}"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return parameters.toArray(new String[parameters.size()]);

	}
	protected void refreshParameters(){
		
		if (parameterProviderEditor != null){
			
			List l = new ArrayList();
			l.add(new PropertyGroupParameter(((PropertyGroupParameter)((List)parameterViewer.getInput()).get(0)).getConfig(), parameterProviderEditor));
			parameterViewer.setInput(l);
		}
//		parameterViewer.getTree().pack(true);
		parameterViewer.refresh();
		bar.getParent().layout();
		
		for(ExpandItem i : bar.getItems()){
			if (i.getControl() == parameterViewer.getControl()){
				if (i.getExpanded()){
					
					i.getControl().pack(true);
					Point size = i.getControl().computeSize(bar.getClientArea().width,
							SWT.DEFAULT);
							
					i.getControl().pack(true);
//					it.setHeight(it.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
					i.setHeight(size.y);
//					resize();
				}
			}
		}
	}
	
	final public ExpandBar getControl(){
		return bar;
	}
	
	protected IComponentDefinition getComponentDefinition(){
		return def;
	}
	
	protected void createParameters(){
		parameterViewer = createViewer(bar);
				
		TreeViewerColumn valueCol = createValueViewerColum(parameterViewer);
		valueCol.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if (((Property)element).getParent() != null && ((Property)element).getParent().getParent() != null){
					if (((Property)element).getParent().getParent() instanceof PropertyGroupParameter){
						return ((PropertyGroupParameter)((Property)element).getParent().getParent()).getPropertyValueString((Property)element);
					}
				}
				
				return ""; //$NON-NLS-1$
			}
		});
		parameterItem = new ExpandItem(bar, SWT.NONE);
		parameterItem.setControl(parameterViewer.getTree());
		parameterItem.setText(Messages.ComponentEditor_2);
		

		parameterProviderEditor = new ComboBoxViewerCellEditor(parameterViewer.getTree(), SWT.READ_ONLY);
		parameterProviderEditor.setContenProvider(new ArrayContentProvider());
		parameterProviderEditor.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((IComponentDefinition)element).getName();
			}
		});
		
		valueCol.setEditingSupport(new EditingSupport(parameterViewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				if (((Property)element).getParent().getParent() instanceof PropertyGroupParameter){
					((PropertyGroupParameter)((Property)element).getParent().getParent()).setPropertyValue((Property)element, value);
				}
				notifyChangeOccured();
				parameterViewer.refresh();
				
			}
			
			@Override
			protected Object getValue(Object element) {
				return ((PropertyGroupParameter)((Property)element).getParent().getParent()).getPropertyValue((Property)element);
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return ((Property)element).getCellEditor();
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return ((Property)element).getCellEditor() != null;
			}
		});
	
		//create the menu to add/remove parameters 
		MenuManager mgr = new MenuManager();
		final Action addP = new Action(Messages.ComponentEditor_3){
			public void run(){
				int number = getComponentDefinition().getParameters().size();
				ReportComponentParameter p = new ReportComponentParameter("Parameter_" + number, number); //$NON-NLS-1$
				getComponentDefinition().addComponentParameter(p); //$NON-NLS-1$
				notifyChangeOccured();
				refreshParameters();

			}
		};
		
		final Action remP = new Action(Messages.ComponentEditor_5){
			public void run(){
				Property p = (Property)((IStructuredSelection)parameterViewer.getSelection()).getFirstElement();
				ComponentParameter param = null;
				if (p.getParent() instanceof PropertyGroupParameter){
					param = ((PropertyGroupParameter)p.getParent()).removeParameter(p);
				}
				getComponentDefinition().removeComponentParameter(param); //$NON-NLS-1$
				notifyChangeOccured();
				refreshParameters();

			}
		};
		
		
		mgr.add(addP);
		mgr.add(remP);
		mgr.addMenuListener(new IMenuListener() {
			
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				if (getComponentDefinition() != null && 
					(getComponentDefinition() instanceof LabelComponent || 
					getComponentDefinition() instanceof ComponentLink ||
					getComponentDefinition() instanceof ComponentJsp||
					getComponentDefinition() instanceof ComponentD4C||
					getComponentDefinition() instanceof ComponentFlourish||
					getComponentDefinition() instanceof ComponentComment)){
					addP.setEnabled(true);
					boolean enabled = !parameterViewer.getSelection().isEmpty();
					Object o = ((IStructuredSelection)parameterViewer.getSelection()).getFirstElement();
					
					enabled = o instanceof Property && ((Property)o).getParent() != null && ((Property)o).getParent() instanceof PropertyGroupParameter;
					
					remP.setEnabled(enabled);
				}
				else{
					addP.setEnabled(false);
					remP.setEnabled(false);
				}
				
				
			}
		});
		parameterViewer.getTree().setMenu(mgr.createContextMenu(parameterViewer.getControl()));
	}
	
	private void createGeneral(){
		final TreeViewer viewer = createViewer(bar);
		TreeViewerColumn valueCol = createValueViewerColum(viewer);

		ExpandItem item = new ExpandItem(bar, SWT.NONE);
		item.setControl(viewer.getTree());
		item.setText(Messages.ComponentEditor_6);
		
		final Property name = new Property(Messages.ComponentEditor_7, new TextCellEditor(viewer.getTree())); 
		final Property comment = new Property(Messages.ComponentEditor_8, new TextCellEditor(viewer.getTree(), SWT.MULTI | SWT.WRAP));
	
		valueCol.setLabelProvider(new ColumnLabelProvider(){
			public String getText(Object element) {
				if (def == null){
					return ""; //$NON-NLS-1$
				}
				if (element == name){
					return (def).getName();
				}
				if (element == comment){
					return def.getComment();
				}
				return ""; //$NON-NLS-1$
			}
		});
		
		
		valueCol.setEditingSupport(new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				if (element == name){def.setName((String)value);}
				if (element == comment){def.setComment((String)value);}
				notifyChangeOccured();
				viewer.refresh();
				
			}
			
			@Override
			protected Object getValue(Object element) {
				if (element == name){return def.getName();}
				if (element == comment){return def.getComment();}
				return null;
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
		input.add(name);input.add(comment);
		viewer.setInput(input);
		
	}
	
	final protected TreeViewerColumn createValueViewerColum(TreeViewer viewer){
		TreeViewerColumn value = new TreeViewerColumn(viewer, SWT.LEFT);
		value.getColumn().setWidth(100);
		return value;
	}
	
	abstract protected void fillBar(); 
	
	/**
	 * refresh the viewers can be overriden to init some CellEditors
	 * must be called when overriden
	 */
	public void setInput(EditPart editPart, ComponentConfig configuration, IComponentDefinition component){
		this.def = component;
		this.editPart = editPart;
		if (parameterProviderEditor != null){
			List<IComponentDefinition> defs = new ArrayList<IComponentDefinition>(Activator.getDefault().getProject().getComponents().keySet());
			Collections.sort(defs, new Comparator<IComponentDefinition>() {
				@Override
				public int compare(IComponentDefinition o1, IComponentDefinition o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
			parameterProviderEditor.setInput(defs);
			List l = new ArrayList();
			l.add(new PropertyGroupParameter(configuration, parameterProviderEditor));
			parameterViewer.setInput(l);
		}
		
		List<String> l  = new ArrayList<String>();
		for(IResource r : Activator.getDefault().getProject().getResources(FileProperties.class)){
			FileProperties fp = (FileProperties)r;
			if (fp.getLocaleName() != null){
				l.add(fp.getLocaleName());
			}

		}
		
		localeCbo.setItems(l.toArray(new String[l.size()]));
		loadI18n(null);
		eventViewer.setInput(new ArrayList(component.getEventsType()));
		
		setCssClasses(component.getCssClass());
		for(TreeViewer v : viewers){
			v.refresh();
		}
	}

	final protected TreeViewer createViewer(Composite parent) {
		final TreeViewer viewer = new TreeViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION);
		viewer.getTree().setBackground(parent.getBackground());
		viewer.getTree().setLinesVisible(true);
		viewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				if (element instanceof Property){return ((Property)element).getName();}
				return super.getText(element);
			}
		});
		viewer.setAutoExpandLevel(TreeViewer.ALL_LEVELS);
		viewer.setContentProvider(new PropertiesContentProvider());
//		viewer.addFilter(filter);
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
			viewer.getControl().pack();
		}
		
	}
	
	
	public void notifyChangeOccured(){
		
		if (getComponentDefinition() != null && Activator.getDefault().getProject() != null){
			Activator.getDefault().getProject().getDictionary().firePropertyChange(Dictionary.PROPERTY_COMPONENT_CHANGED, null, getComponentDefinition());
		}
		if (editPart != null){
			editPart.refresh();
		}
	}
	private ComboBoxCellEditor localeCbo;
	private PropertyGroup locale;
	
	protected void resizeAllItems(){
		for(ExpandItem item : bar.getItems()){
			if (item.getExpanded()){
				
				item.getControl().pack(true);
				Point size = item.getControl().computeSize(bar.getClientArea().width,
						SWT.DEFAULT);
						
				item.getControl().pack(true);
				item.setHeight(size.y);
			}
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
	
	private void createI18n(){
		final TreeViewer viewer = createViewer(bar);
		TreeViewerColumn valueCol = createValueViewerColum(viewer);
		
		final ExpandItem item = new ExpandItem(bar, SWT.NONE);
		item.setControl(viewer.getTree());
		item.setText("I18N"); //$NON-NLS-1$
		
		localeCbo = new ComboBoxCellEditor(viewer.getTree(), new String[]{}, SWT.READ_ONLY);
		
		locale = new PropertyGroup(Messages.ComponentEditor_12, localeCbo); 
	
		valueCol.setLabelProvider(new ColumnLabelProvider(){
			public String getText(Object element) {
				try{
					if (element == locale){return localeCbo.getItems()[(Integer)localeCbo.getValue()];};
				}catch(Exception ex){}
				if (element instanceof PropertyI18n){return ((PropertyI18n)element).getPropertyValue();}
				return ""; //$NON-NLS-1$
			}
		});
		
		
		valueCol.setEditingSupport(new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				
				if (element == locale){
	
					if (localeCbo.getValue() != null && (Integer)localeCbo.getValue() > -1){
						loadI18n(localeCbo.getItems()[(Integer)localeCbo.getValue()]);
					}
					else{
						return;
					}
					
					
				}
				if (element instanceof PropertyI18n){((PropertyI18n)element).setPropertyValue(value);}
				notifyChangeOccured();
				viewer.refresh();
				viewer.expandAll();
				resizeItem(item);
			}
			
			@Override
			protected Object getValue(Object element) {
				if (element == locale){return localeCbo.getValue();};
				if (element instanceof PropertyI18n){return ((PropertyI18n)element).getPropertyValue();}
				return null;
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
		input.add(locale);
		viewer.setInput(input);
		
	}
	
	private void loadI18n(String localeName){
		locale.clear();
		if (getComponentDefinition() != null){
			IWorkspace workspace = ResourcesPlugin.getWorkspace(); 
			IWorkspaceRoot r = workspace.getRoot();
			IProject p = r.getProject(Activator.getDefault().getProject().getProjectDescriptor().getProjectName());
			
			String end = ""; //$NON-NLS-1$
			if (localeName != null){
				end = "_" + localeName; //$NON-NLS-1$
			}
			else {
				return;
			}
			
			IFile file = p.getFile(localeName + "_" + "components.properties"); //$NON-NLS-1$ //$NON-NLS-2$
			Properties  prop =  new Properties();
			if (file.exists()){
				try{
					prop.load(new FileInputStream(file.getLocation().toOSString()));
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			else{
				file = null;
			}
			
			for(Object o : prop.keySet()){
				if (((String)o).startsWith(getComponentDefinition().getId())){
					locale.add(new PropertyI18n(((String)o).replace(getComponentDefinition().getId() + ".", ""), //$NON-NLS-1$ //$NON-NLS-2$
							(String)o, file,
							prop, new TextCellEditor(localeCbo.getControl().getParent())));
				}
				
			}
			
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
				if (getComponentDefinition() == null){return null;}
				
				String s = getComponentDefinition().getJavaScript((ElementsEventType)element);
				if (s != null && !s.trim().isEmpty()){
					return used;
				}
				return null;
			}
		});

		TreeViewerColumn valueCol = createValueViewerColum(eventViewer);
		
		ExpandItem item = new ExpandItem(bar, SWT.NONE);
		item.setControl(eventViewer.getTree());
		item.setText(Messages.ComponentEditor_18);
		
		final EventDialogCellEditor editor = new EventDialogCellEditor(eventViewer.getTree());
		
		valueCol.setLabelProvider(new ColumnLabelProvider(){
			public String getText(Object element) {
				if (getComponentDefinition() == null){return "";} //$NON-NLS-1$
				if (getComponentDefinition().getJavaScript((ElementsEventType)element) != null){
					return "..."; //$NON-NLS-1$
				}
				return ""; //$NON-NLS-1$
			}
			
			
		});
		
		
		valueCol.setEditingSupport(new EditingSupport(eventViewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				if (getComponentDefinition() == null){return ;}
				getComponentDefinition().setJavaScript((ElementsEventType)element, (String)value);
				notifyChangeOccured();
				eventViewer.refresh();
				
			}
			
			@Override
			protected Object getValue(Object element) {
				if (getComponentDefinition() == null){return null;}
				return getComponentDefinition().getJavaScript((ElementsEventType)element);

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
	
	private PropertyGroup cssClasses;
	private TreeViewer cssViewer;
	private void setCssClasses(String classes){
		cssClasses.clear();
		if (classes == null){
			return;
		}
		for(String s : classes.split(" ")){ //$NON-NLS-1$
			if (s.trim().length() > 0){
				final String val = s.trim();
				Property p = new Property(Messages.ComponentEditor_23, cssDialogEditor){
					@Override
					public String getValue() {
						return val;
					}
				};
				cssClasses.add(p);
				
			}
		}
		
	}
	
	private CellEditor cssDialogEditor;
	private void createCss(){
		cssViewer = createViewer(bar);
		TreeViewerColumn valueCol = createValueViewerColum(cssViewer);

		
		cssDialogEditor = new DialogCellEditor(cssViewer.getTree()) {
			
			@Override
			protected Object openDialogBox(Control cellEditorWindow) {
//				DialogCssClass d = new DialogCssClass(cellEditorWindow.getShell(), 
//						getComponentDefinition(),
//						(String)getValue());
				DialogCssProperties d = new DialogCssProperties(cellEditorWindow.getShell(), (String)getValue());
				d.open();
				return null;
			}
		};
		
		ExpandItem item = new ExpandItem(bar, SWT.NONE);
		item.setControl(cssViewer.getTree());
		item.setText(Messages.ComponentEditor_24);
		
		cssClasses = new PropertyGroup(Messages.ComponentEditor_25, new TextCellEditor(cssViewer.getTree())); 
			
		valueCol.setLabelProvider(new ColumnLabelProvider(){
			
			public String getText(Object element) {
				if (getComponentDefinition() == null){return "";} //$NON-NLS-1$
				if (cssClasses == element){
					return getComponentDefinition().getCssClass();
				}
				return ((Property)element).getValue();
			}
		});
		
		
		valueCol.setEditingSupport(new EditingSupport(cssViewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				if (element == cssClasses){getComponentDefinition().setCssClass((String)value);setCssClasses((String)value);}
				notifyChangeOccured();
				cssViewer.expandAll();
				cssViewer.refresh();
				resizeAllItems();
			}
			
			@Override
			protected Object getValue(Object element) {
				if (element == cssClasses){return getComponentDefinition().getCssClass();}
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
	
	@Override
	public void filter(String propertyName) {
		/*
		_filterValue = propertyName;
		filter.setPattern(propertyName == null ? "*" : propertyName);
		
		
		for(ExpandItem i : getControl().getItems()){
			for(TreeViewer v : viewers){
				if (i.getControl() == v.getTree()){
					v.refresh();
					resizeItem(i);
				}
				
			}
		}
		*/
	}
	
	
	private String _filterValue;
//	private PatternFilter filter = new PatternFilter(){
//
//		@Override
//		protected boolean isLeafMatch(Viewer viewer, Object element) {
//			
//			return super.isLeafMatch(viewer, element);
//		}
//	};
}
