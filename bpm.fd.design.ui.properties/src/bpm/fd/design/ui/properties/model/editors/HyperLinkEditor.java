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

import bpm.fd.api.core.model.components.definition.link.ComponentLink;
import bpm.fd.api.core.model.components.definition.link.LinkOptions;
import bpm.fd.design.ui.properties.i18n.Messages;
import bpm.fd.design.ui.properties.model.Property;
import bpm.fd.design.ui.properties.viewer.PropertiesContentProvider;

public class HyperLinkEditor extends ComponentEditor{

	public HyperLinkEditor(Composite parent) {
		super(parent);
		fillBar();
		
	}

	@Override
	protected void fillBar() {
		
		createHyperlink(getControl());
		createParameters();
	}

	private void createHyperlink(ExpandBar parent) {
		final TreeViewer viewer = createViewer(parent);
		viewer.setContentProvider(new PropertiesContentProvider());
		TreeViewerColumn colValue = createValueViewerColum(viewer);
		
		ExpandItem item = new ExpandItem(parent, SWT.NONE);
		item.setControl(viewer.getTree());
		item.setText(Messages.HyperLinkEditor_0);
		
		
		
		final Property text = new Property(Messages.HyperLinkEditor_1, new TextCellEditor(viewer.getTree()));
		final Property url = new Property(Messages.HyperLinkEditor_2, new TextCellEditor(viewer.getTree()));
		
		colValue.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				ComponentLink c = (ComponentLink)getComponentDefinition();
				if (c == null){return "";} //$NON-NLS-1$
				LinkOptions opts = (LinkOptions)c.getOptions(LinkOptions.class); 
				if (c == null){return "";} //$NON-NLS-1$
				if (element == text){return opts.getLabel();}
				if (element == url){return opts.getUrl();}
				return ""; //$NON-NLS-1$
			}
		});
		
		colValue.setEditingSupport(new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				ComponentLink c = (ComponentLink)getComponentDefinition();
				if (c == null){return ;}
				LinkOptions opts = (LinkOptions)c.getOptions(LinkOptions.class); 
				if (element == text){c.setLabel((String)value);}
				if (element == url){opts.setUrl((String)value);}
				notifyChangeOccured();
				viewer.refresh();
			}
			
			@Override
			protected Object getValue(Object element) {
				ComponentLink c = (ComponentLink)getComponentDefinition();
				if (c == null){return "";} //$NON-NLS-1$
				LinkOptions opts = (LinkOptions)c.getOptions(LinkOptions.class); 
				if (c == null){return "";} //$NON-NLS-1$
				if (element == text){return opts.getLabel();}
				if (element == url){return opts.getUrl();}
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
		input.add(url);
		viewer.setInput(input);
	}

}
