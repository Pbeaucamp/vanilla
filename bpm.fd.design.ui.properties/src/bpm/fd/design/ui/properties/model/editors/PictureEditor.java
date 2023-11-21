package bpm.fd.design.ui.properties.model.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;

import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.image.ComponentPicture;
import bpm.fd.api.core.model.resources.FileImage;
import bpm.fd.api.core.model.resources.IResource;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.properties.i18n.Messages;
import bpm.fd.design.ui.properties.model.Property;
import bpm.fd.design.ui.properties.viewer.PropertiesContentProvider;

public class PictureEditor extends ComponentEditor{

	private ComboBoxViewerCellEditor cbo;
	
	public PictureEditor(Composite parent) {
		super(parent);
		fillBar();
	}

	@Override
	protected void fillBar() {
		createPicture(getControl());
		
	}

	private void createPicture(ExpandBar parent) {
		final TreeViewer viewer = createViewer(parent);
		viewer.setContentProvider(new PropertiesContentProvider());
		TreeViewerColumn colValue = createValueViewerColum(viewer);
		
		ExpandItem item = new ExpandItem(parent, SWT.NONE);
		item.setControl(viewer.getTree());
		item.setText(Messages.PictureEditor_0);
		
		cbo = new ComboBoxViewerCellEditor(viewer.getTree(), SWT.READ_ONLY);
		cbo.setContenProvider(new ArrayContentProvider());
		cbo.setLabelProvider(new LabelProvider(){@Override
		public String getText(Object element) {
			return ((IResource)element).getName();
		}});
		
		final Property pict = new Property(Messages.PictureEditor_1, cbo);
		
		colValue.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				ComponentPicture c = (ComponentPicture)getComponentDefinition();
				if (c == null){return "";} //$NON-NLS-1$
				if (element == pict){return c.getPictureUrl();}
				return ""; //$NON-NLS-1$
			}
		});
		
		colValue.setEditingSupport(new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				ComponentPicture c = (ComponentPicture)getComponentDefinition();
				if (c == null){return ;}
				if (element == pict){c.setPictureUrl(((IResource)value).getName());}
				notifyChangeOccured();
				viewer.refresh();
			}
			
			@Override
			protected Object getValue(Object element) {
				ComponentPicture c = (ComponentPicture)getComponentDefinition();
				if (c == null){return "";} //$NON-NLS-1$
				IResource r = Activator.getDefault().getProject().getResource(FileImage.class, c.getPictureUrl());
				if (element == pict){return r;}
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
		input.add(pict);
		viewer.setInput(input);
	}
	
	@Override
	public void setInput(EditPart editPart, ComponentConfig configuration,
			IComponentDefinition component) {
		super.setInput(editPart, configuration, component);
		cbo.setInput(Activator.getDefault().getProject().getResources(FileImage.class));
	}
}
