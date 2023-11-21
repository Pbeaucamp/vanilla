package bpm.fd.design.ui.properties.model.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;

import bpm.fd.api.core.model.components.definition.text.LabelComponent;
import bpm.fd.api.core.model.components.definition.text.LabelOptions;
import bpm.fd.api.core.model.components.definition.text.LabelRenderer;
import bpm.fd.design.ui.properties.i18n.Messages;
import bpm.fd.design.ui.properties.model.Property;
import bpm.fd.design.ui.properties.viewer.PropertiesContentProvider;

public class LabelEditor extends ComponentEditor{

	public LabelEditor(Composite parent) {
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
		item.setText(Messages.LabelEditor_0);
		
		final Property renderer = new Property(Messages.LabelEditor_1, new ComboBoxCellEditor(viewer.getTree(), new String[]{
			LabelRenderer.PARAGRAPH.name(), LabelRenderer.DIV.name(), LabelRenderer.SPAN.name(), LabelRenderer.H1.name(), LabelRenderer.H2.name(), 
			LabelRenderer.H3.name(), LabelRenderer.H4.name(),
			LabelRenderer.H5.name(),LabelRenderer.H6.name(), LabelRenderer.HR.name()
			
		}));
		
		final TextCellEditor ted = new TextCellEditor(viewer.getTree(), SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		
		final Property content = new Property(Messages.LabelEditor_2, ted, true);
		
		colValue.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				LabelComponent c = (LabelComponent)getComponentDefinition();
				if (c == null){return "";} //$NON-NLS-1$
				if (element == renderer){return ((LabelRenderer)c.getRenderer()).name();}
				if (element == content){return ((LabelOptions)c.getOptions(LabelOptions.class)).getText();}
				return ""; //$NON-NLS-1$
			}
		});
		
		colValue.setEditingSupport(new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				LabelComponent c = (LabelComponent)getComponentDefinition();
				if (c == null){return ;}
				try{
					if (element == renderer){c.setRenderer(LabelRenderer.values()[(Integer)value]);}
				}catch(Exception ex){}
				if (element == content){((LabelOptions)c.getOptions(LabelOptions.class)).setText((String)value);}
				notifyChangeOccured();
				viewer.refresh();
			}
			
			@Override
			protected Object getValue(Object element) {
				LabelComponent c = (LabelComponent)getComponentDefinition();
				if (c == null){return null;}
				if (element == renderer){return ((LabelRenderer)c.getRenderer()).getRendererStyle();}
				if (element == content){return ((LabelOptions)c.getOptions(LabelOptions.class)).getText();}
				return null;
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				((Property)element).updateContentAssist(getComponentParameterNames());
				return ((Property)element).getCellEditor();
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return ((Property)element).getCellEditor() != null;
			}
		});
		
		List input = new ArrayList();
		input.add(renderer);
		input.add(content);
		viewer.setInput(input);
	}

}
