package bpm.fd.design.ui.properties.model.editors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandItem;

import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.DrillDrivenComponentConfig;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.structure.DrillDrivenStackableCell;
import bpm.fd.api.core.model.structure.IStructureElement;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.properties.i18n.Messages;
import bpm.fd.design.ui.properties.viewer.PropertiesContentProvider;

public class DrivenStackedCellEditor extends StructureEditor{
	private TreeViewer viewer;
	private ExpandItem item;
	private ComboBoxViewerCellEditor controllersCbo;
	
	public DrivenStackedCellEditor(Composite parent) {
		super(parent);
		fillBar();
	}

	@Override
	protected void fillBar() {
		
		super.fillBar();
		createController();
	}
	private void createController(){
		viewer = new TreeViewer(getControl(), SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION);
		viewer.getTree().setBackground(getControl().getBackground());
		viewer.getTree().setLinesVisible(true);
		viewer.setAutoExpandLevel(TreeViewer.ALL_LEVELS);
		viewer.setContentProvider(new PropertiesContentProvider());
		viewers.add(viewer);
		TreeViewerColumn label = new TreeViewerColumn(viewer, SWT.LEFT);
		label.getColumn().setWidth(100);
		label.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return ((ComponentConfig)element).getTargetComponent().getName();
			}
		});

		TreeViewerColumn valueCol = createValueViewerColum(viewer);

		
		ViewerFilter filter = new ViewerFilter() {
			
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (getStructureElement() == null){return false;}
				if (DrivenStackedCellEditor.this.viewer.getSelection().isEmpty()){return false;}
				DrillDrivenComponentConfig c = ((DrillDrivenComponentConfig)((IStructuredSelection)DrivenStackedCellEditor.this.viewer.getSelection()).getFirstElement());
				for(ComponentParameter p : c.getTargetComponent().getParameters()){
					
					String componentName = c.getComponentNameFor(p);
					IComponentDefinition d = Activator.getDefault().getProject().getDictionary().getComponent(componentName);
					
					if (d != null && DrillDrivenStackableCell.isSupportedAsController(d) && d == element){
						return true;
					}
				}
				return false;
			}
		};
		
		controllersCbo = new ComboBoxViewerCellEditor(viewer.getTree(), SWT.READ_ONLY);
		controllersCbo.getViewer().setContentProvider(new ArrayContentProvider());
		controllersCbo.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((IComponentDefinition)element).getName();
			}
		});
		
		controllersCbo.getViewer().addFilter(filter);
		
		
		
		item = new ExpandItem(getControl(), SWT.NONE);
		item.setControl(viewer.getTree());
		item.setText(Messages.DrivenStackedCellEditor_0);

		valueCol.setLabelProvider(new ColumnLabelProvider(){
			public String getText(Object element) {
				try{
					return ((DrillDrivenComponentConfig)element).getController().getName();
				}catch(Exception ex){
					return ""; //$NON-NLS-1$
				}
			}
		});
		
		valueCol.setEditingSupport(new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				if (getStructureElement() == null){return ;}
				((DrillDrivenComponentConfig)element).setController((IComponentDefinition)value);
				notifyChangeOccured();
				viewer.refresh();
				
			}
			
			@Override
			protected Object getValue(Object element) {
				if (getStructureElement() == null){return null;}
				return((DrillDrivenComponentConfig)element).getController();

			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				controllersCbo.getViewer().refresh();
				return controllersCbo;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});
		
	}
	
	@Override
	public void setInput(EditPart editPart, IStructureElement struc) {
		super.setInput(editPart, struc);
		List<ComponentConfig> l = new ArrayList<ComponentConfig>(((DrillDrivenStackableCell)struc).getConfigs());
		Collections.sort(l, new Comparator<ComponentConfig>() {
			@Override
			public int compare(ComponentConfig o1, ComponentConfig o2) {
				try{
					return (o1.getTargetComponent().getName().compareTo(o2.getTargetComponent().getName()));
				}catch(Exception ex){
					return 0;
				}
				
			}
		});
		if (controllersCbo != null){
			controllersCbo.setInput(Activator.getDefault().getProject().getDictionary().getComponents());
		}
		
		viewer.setInput(l);
		
		resizeItem(item);
		
		
		
		
	}
}

