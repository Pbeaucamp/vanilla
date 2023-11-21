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

import bpm.fd.api.core.model.components.definition.comment.CommentOptions;
import bpm.fd.api.core.model.components.definition.comment.ComponentComment;
import bpm.fd.design.ui.properties.i18n.Messages;
import bpm.fd.design.ui.properties.model.Property;
import bpm.fd.design.ui.properties.viewer.PropertiesContentProvider;

public class CommentEditor extends ComponentEditor{

	public CommentEditor(Composite parent) {
		super(parent);
		fillBar();
	}

	@Override
	protected void fillBar() {
		createComment(getControl());
		
	}
	
	private void createComment(ExpandBar parent) {
		final TreeViewer viewer = createViewer(parent);
		viewer.setContentProvider(new PropertiesContentProvider());
		TreeViewerColumn colValue = createValueViewerColum(viewer);
		
		ExpandItem item = new ExpandItem(parent, SWT.NONE);
		item.setControl(viewer.getTree());
		item.setText(Messages.CommentEditor_0);
		
		final Property validation = new Property("Is validation", new CheckboxCellEditor(viewer.getTree()));
		final Property limit = new Property("As limit", new CheckboxCellEditor(viewer.getTree()));
		final Property limitNumber = new Property("Limit number", new TextCellEditor(viewer.getTree()));
		
		colValue.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				ComponentComment c = (ComponentComment)getComponentDefinition();
				if (c == null){return "";} //$NON-NLS-1$
				CommentOptions opts = (CommentOptions)c.getOptions(CommentOptions.class); 
				if (c == null){return "";} //$NON-NLS-1$
				if (element == validation){return opts.isValidation() + "";} //$NON-NLS-1$
				if (element == limit){return opts.isLimitComment() + "";} //$NON-NLS-1$
				if (element == limitNumber){return opts.getLimit() + "";} //$NON-NLS-1$
				return ""; //$NON-NLS-1$
			}
		});
		
		colValue.setEditingSupport(new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				ComponentComment c = (ComponentComment)getComponentDefinition();
				if (c == null){return ;}
				CommentOptions opts = (CommentOptions)c.getOptions(CommentOptions.class); 
				if (element == validation){opts.setValidation((Boolean)value);}
				if (element == limit){opts.setLimitComment((Boolean)value);}
				try {
					if (element == limitNumber){opts.setLimit(Integer.parseInt((String) value));} //$NON-NLS-1$
				} catch (NumberFormatException e) {
				}
				notifyChangeOccured();
				viewer.refresh();
			}
			
			@Override
			protected Object getValue(Object element) {
				ComponentComment c = (ComponentComment)getComponentDefinition();
				if (c == null){return "";} //$NON-NLS-1$
				CommentOptions opts = (CommentOptions)c.getOptions(CommentOptions.class); 
				if (c == null){return "";} //$NON-NLS-1$
				if (element == validation){return opts.isValidation();}
				if (element == limit){return opts.isLimitComment();}
				if (element == limitNumber){return opts.getLimit();} //$NON-NLS-1$
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
		input.add(validation);
		input.add(limit);
		input.add(limitNumber);
		viewer.setInput(input);
	}
}
