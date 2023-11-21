package bpm.fd.design.ui.properties.model.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;

import bpm.fd.api.core.model.components.definition.timer.ComponentTimer;
import bpm.fd.api.core.model.components.definition.timer.TimerOptions;
import bpm.fd.design.ui.properties.i18n.Messages;
import bpm.fd.design.ui.properties.model.Property;
import bpm.fd.design.ui.properties.viewer.PropertiesContentProvider;

public class TimerEditor extends ComponentEditor{

	public TimerEditor(Composite parent) {
		super(parent);
		fillBar();
	}

	@Override
	protected void fillBar() {
		createTimer(getControl());
		
	}
	
	private void createTimer(ExpandBar parent) {
		final TreeViewer viewer = createViewer(parent);
		viewer.setContentProvider(new PropertiesContentProvider());
		TreeViewerColumn colValue = createValueViewerColum(viewer);
		
		ExpandItem item = new ExpandItem(parent, SWT.NONE);
		item.setControl(viewer.getTree());
		item.setText(Messages.TimerEditor_0);
		
		
		
		final Property text = new Property(Messages.TimerEditor_1, new TextCellEditor(viewer.getTree()));
		final Property delay = new Property(Messages.TimerEditor_2, new TextCellEditor(viewer.getTree()));
		final Property autoStart = new Property(Messages.TimerEditor_3, new CheckboxCellEditor(viewer.getTree()));
		
		
		colValue.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				ComponentTimer c = (ComponentTimer)getComponentDefinition();
				if (c == null){return "";} //$NON-NLS-1$
				TimerOptions opts = (TimerOptions)c.getOptions(TimerOptions.class); 
				if (c == null){return "";} //$NON-NLS-1$
				if (element == text){return opts.getLabel();}
				if (element == delay){return opts.getDelay() + "";} //$NON-NLS-1$
				if (element == autoStart){return opts.isStartOnLoad() + "";} //$NON-NLS-1$
				return ""; //$NON-NLS-1$
			}
		});
		
		colValue.setEditingSupport(new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				ComponentTimer c = (ComponentTimer)getComponentDefinition();
				if (c == null){return ;}
				TimerOptions opts = (TimerOptions)c.getOptions(TimerOptions.class); 
				if (element == text){opts.setLabel((String)value);}
				if (element == autoStart){opts.setStartOnLoad((Boolean)value);}
				try{
					if (element == delay){opts.setDelay(Integer.valueOf((String)value));}
				}catch(Exception ex){}
				notifyChangeOccured();
				viewer.refresh();
			}
			
			@Override
			protected Object getValue(Object element) {
				ComponentTimer c = (ComponentTimer)getComponentDefinition();
				if (c == null){return "";} //$NON-NLS-1$
				TimerOptions opts = (TimerOptions)c.getOptions(TimerOptions.class); 
				if (c == null){return "";} //$NON-NLS-1$
				if (element == text){return opts.getLabel();}
				if (element == autoStart){return opts.isStartOnLoad();}
				if (element == delay){return opts.getDelay() + ""; } //$NON-NLS-1$
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
		input.add(text);input.add(autoStart);input.add(delay);
		viewer.setInput(input);
	}

}
