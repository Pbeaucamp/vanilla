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

import bpm.fd.api.core.model.components.definition.jsp.ComponentFlourish;
import bpm.fd.api.core.model.components.definition.jsp.ComponentFlourish;
import bpm.fd.api.core.model.components.definition.jsp.ComponentFlourish;
import bpm.fd.api.core.model.components.definition.jsp.FlourishOptions;
import bpm.fd.api.core.model.components.definition.jsp.FlourishOptions;
import bpm.fd.api.core.model.components.definition.jsp.FlourishOptions;
import bpm.fd.design.ui.properties.i18n.Messages;
import bpm.fd.design.ui.properties.model.Property;
import bpm.fd.design.ui.properties.viewer.PropertiesContentProvider;

public class FlourishEditor extends ComponentEditor{

	public FlourishEditor(Composite parent) {
		super(parent);
		fillBar();
		
	}

	@Override
	protected void fillBar() {
		
		createLabel(getControl());
		createParameters();
	}

	private void createLabel(ExpandBar parent) {
		final TreeViewer viewer = createViewer(parent);
		viewer.setContentProvider(new PropertiesContentProvider());
		TreeViewerColumn colValue = createValueViewerColum(viewer);
		
		ExpandItem item = new ExpandItem(parent, SWT.NONE);
		item.setControl(viewer.getTree());
		item.setText("Flourish");
		
		
		final Property borderWidth = new Property(Messages.JspEditor_1, new TextCellEditor(viewer.getTree()));
		final Property height = new Property(Messages.JspEditor_2, new TextCellEditor(viewer.getTree()));
		final Property width = new Property(Messages.JspEditor_3, new TextCellEditor(viewer.getTree()));
		final Property url = new Property("Flourish ID", new TextCellEditor(viewer.getTree()));
		
		colValue.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				ComponentFlourish c = (ComponentFlourish)getComponentDefinition();
				
				if (c == null){return "";} //$NON-NLS-1$
				FlourishOptions opts = (FlourishOptions)c.getOptions(FlourishOptions.class);
				if (element == borderWidth){return opts.getBorder_width() + "";} //$NON-NLS-1$
				if (element == width){return opts.getWidth() + "";} //$NON-NLS-1$
				if (element == height){return opts.getHeight() + "";} //$NON-NLS-1$
				if (element == url){return c.getUrl() + "";} //$NON-NLS-1$
				return ""; //$NON-NLS-1$
			}
		});
		
		colValue.setEditingSupport(new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				ComponentFlourish c = (ComponentFlourish)getComponentDefinition();
				FlourishOptions opts = (FlourishOptions)c.getOptions(FlourishOptions.class);
				if (c == null){return ;}
				try{
					if (element == borderWidth){opts.setBorder_width(Integer.valueOf((String)value));}
					if (element == width){opts.setWidth(Integer.valueOf((String)value));}
					if (element == height){opts.setHeight(Integer.valueOf((String)value));}
				}catch(Exception ex){}
				if (element == url){c.setUrl((String)value);}
				notifyChangeOccured();
				viewer.refresh();
			}
			
			@Override
			protected Object getValue(Object element) {
				ComponentFlourish c = (ComponentFlourish)getComponentDefinition();
				FlourishOptions opts = (FlourishOptions)c.getOptions(FlourishOptions.class);
				if (c == null){return "";} //$NON-NLS-1$
				if (element == borderWidth){return opts.getBorder_width() + "";} //$NON-NLS-1$
				if (element == width){return opts.getWidth() + "";} //$NON-NLS-1$
				if (element == height){return opts.getHeight() + "";} //$NON-NLS-1$
				if (element == url){return c.getUrl() + "";} //$NON-NLS-1$
				return ""; //$NON-NLS-1$
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
		input.add(url);
		input.add(width);
		input.add(height);
		input.add(borderWidth);
		viewer.setInput(input);
	}

}
