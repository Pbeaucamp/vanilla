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

import bpm.fd.api.core.model.components.definition.styledtext.ComponentStyledTextInput;
import bpm.fd.api.core.model.components.definition.styledtext.StyledTextOptions;
import bpm.fd.design.ui.properties.i18n.Messages;
import bpm.fd.design.ui.properties.model.Property;
import bpm.fd.design.ui.properties.viewer.PropertiesContentProvider;

public class StyledTextEditor extends ComponentEditor{

	public StyledTextEditor(Composite parent) {
		super(parent);
		fillBar();
	}

	@Override
	protected void fillBar() {
		createStyledText(getControl());
		
	}
	
	private void createStyledText(ExpandBar parent) {
		final TreeViewer viewer = createViewer(parent);
		viewer.setContentProvider(new PropertiesContentProvider());
		TreeViewerColumn colValue = createValueViewerColum(viewer);
		
		ExpandItem item = new ExpandItem(parent, SWT.NONE);
		item.setControl(viewer.getTree());
		item.setText(Messages.StyledTextEditor_0);
		
		
		
		final Property text = new Property(Messages.StyledTextEditor_1, new TextCellEditor(viewer.getTree()));
		final Property col = new Property(Messages.StyledTextEditor_2, new TextCellEditor(viewer.getTree()));
		final Property row = new Property(Messages.StyledTextEditor_3, new TextCellEditor(viewer.getTree()));
		
		colValue.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				ComponentStyledTextInput c = (ComponentStyledTextInput)getComponentDefinition();
				if (c == null){return "";} //$NON-NLS-1$
				StyledTextOptions opts = (StyledTextOptions)c.getOptions(StyledTextOptions.class); 
				if (c == null){return "";} //$NON-NLS-1$
				if (element == text){return opts.getInitialValue();}
				if (element == col){return opts.getColumnsNumber() + "";} //$NON-NLS-1$
				if (element == row){return opts.getRowsNumber() + "";} //$NON-NLS-1$
				return ""; //$NON-NLS-1$
			}
		});
		
		colValue.setEditingSupport(new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				ComponentStyledTextInput c = (ComponentStyledTextInput)getComponentDefinition();
				if (c == null){return ;}
				StyledTextOptions opts = (StyledTextOptions)c.getOptions(StyledTextOptions.class); 
				if (element == text){opts.setInitialValue((String)value);}
				try{
					if (element == col){opts.setColumnsNumber(Integer.valueOf((String)value));}
					if (element == row){opts.setRowsNumber(Integer.valueOf((String)value));}
				}catch(Exception ex){}
				notifyChangeOccured();
				viewer.refresh();
			}
			
			@Override
			protected Object getValue(Object element) {
				ComponentStyledTextInput c = (ComponentStyledTextInput)getComponentDefinition();
				if (c == null){return "";} //$NON-NLS-1$
				StyledTextOptions opts = (StyledTextOptions)c.getOptions(StyledTextOptions.class); 
				if (c == null){return "";} //$NON-NLS-1$
				if (element == text){return opts.getInitialValue();}
				if (element == col){return opts.getColumnsNumber()+ "";} //$NON-NLS-1$
				if (element == row){return opts.getRowsNumber() + "";} //$NON-NLS-1$
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
		input.add(text);input.add(col);input.add(row);
		viewer.setInput(input);
	}
}
