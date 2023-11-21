package bpm.fd.design.ui.properties.model.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;

import bpm.fd.api.core.model.components.definition.buttons.ButtonOptions;
import bpm.fd.api.core.model.components.definition.buttons.ComponentButtonDefinition;
import bpm.fd.design.ui.properties.i18n.Messages;
import bpm.fd.design.ui.properties.model.Property;
import bpm.fd.design.ui.properties.viewer.PropertiesContentProvider;

public class ButtonEditor extends ComponentEditor{

	public ButtonEditor(Composite parent) {
		super(parent);
		fillBar();
	}

	@Override
	protected void fillBar() {
		createButton(getControl());
		
	}
	
	private void createButton(ExpandBar parent) {
		final TreeViewer viewer = createViewer(parent);
		viewer.setContentProvider(new PropertiesContentProvider());
		TreeViewerColumn colValue = createValueViewerColum(viewer);
		
		ExpandItem item = new ExpandItem(parent, SWT.NONE);
		item.setControl(viewer.getTree());
		item.setText(Messages.ButtonEditor_0);
		
		
		
		final Property text = new Property(Messages.ButtonEditor_1, new TextCellEditor(viewer.getTree()));
		
		colValue.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				ComponentButtonDefinition c = (ComponentButtonDefinition)getComponentDefinition();
				if (c == null){return "";} //$NON-NLS-1$
				ButtonOptions opts = (ButtonOptions)c.getOptions(ButtonOptions.class); 
				if (c == null){return "";} //$NON-NLS-1$
				if (element == text){return opts.getLabel();}
				return ""; //$NON-NLS-1$
			}
		});
		
		colValue.setEditingSupport(new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				ComponentButtonDefinition c = (ComponentButtonDefinition)getComponentDefinition();
				if (c == null){return ;}
				ButtonOptions opts = (ButtonOptions)c.getOptions(ButtonOptions.class); 
				if (element == text){opts.setLabel((String)value);}
				notifyChangeOccured();
				viewer.refresh();
			}
			
			@Override
			protected Object getValue(Object element) {
				ComponentButtonDefinition c = (ComponentButtonDefinition)getComponentDefinition();
				if (c == null){return "";} //$NON-NLS-1$
				ButtonOptions opts = (ButtonOptions)c.getOptions(ButtonOptions.class); 
				if (c == null){return "";} //$NON-NLS-1$
				if (element == text){return opts.getLabel();}
				return null;
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
		
		List input = new ArrayList();
		input.add(text);
		viewer.setInput(input);
	}

}
