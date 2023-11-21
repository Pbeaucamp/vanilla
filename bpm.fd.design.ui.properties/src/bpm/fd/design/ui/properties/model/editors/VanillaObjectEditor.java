package bpm.fd.design.ui.properties.model.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.ui.handlers.IHandlerService;

import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.IRepositoryObjectReference;
import bpm.fd.api.core.model.components.definition.markdown.ComponentMarkdown;
import bpm.fd.api.core.model.components.definition.report.ComponentKpi;
import bpm.fd.api.core.model.components.definition.report.ComponentReport;
import bpm.fd.api.core.model.components.definition.report.ReportComponentParameter;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.properties.i18n.Messages;
import bpm.fd.design.ui.properties.model.Property;
import bpm.fd.design.ui.properties.viewer.PropertiesContentProvider;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.Parameter;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.repository.ui.dialogs.DialogDirectoryItemPicker;

public abstract class VanillaObjectEditor extends ComponentEditor{
	
	private class ItemDialogEditor extends DialogCellEditor{

		public ItemDialogEditor(Composite paren){
			super(paren);
		}
		
		@Override
		protected Object openDialogBox(Control cellEditorWindow) {
			if (bpm.fd.repository.ui.Activator.getDefault().getRepositoryConnection() == null){
				IHandlerService handlerService = (IHandlerService) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getService(IHandlerService.class);
				try {
					handlerService.executeCommand("bpm.repository.ui.commands.connection", null);
				} catch (Exception ex) {
					
					ex.printStackTrace();
				} 
				
				if (bpm.fd.repository.ui.Activator.getDefault().getRepositoryConnection() == null){
					MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.VanillaObjectEditor_1, Messages.VanillaObjectEditor_2);
					return null;
				}
			}
			IRepositoryApi sock = bpm.fd.repository.ui.Activator.getDefault().getRepositoryConnection();
			DialogDirectoryItemPicker picker = new DialogDirectoryItemPicker(getControl().getShell(), 
					sock, 
					getItemRef().getObjectType(), getItemRef().getObjectSubtypes());
			
			if (picker.open() == DialogDirectoryItemPicker.OK){
				RepositoryItem item = picker.getDirectoryItem();
				
				getItemRef().setDirectoryItemId(item.getId());
				ItemKey key = null;
				for(ItemKey k : itemNames.keySet()){
					if (k.itemId == item.getId() && k.repositoryUrl.equals(sock.getContext().getRepository().getUrl())){
						key = k;
						break;
					}
				}
				
				
				if (key == null){
					key = new ItemKey(item.getId(), sock.getContext().getRepository().getUrl());
					itemNames.put(key, item.getItemName());
				}
				
				/*
				 * parameter update
				 */
				try {
					List<ComponentParameter> parameters = new ArrayList<ComponentParameter>();
					int i = 0;
					for(Parameter p : bpm.fd.repository.ui.Activator.getDefault().getRepositoryConnection().getRepositoryService().getParameters(item)){
						ComponentParameter cp = new ReportComponentParameter(p.getName(), i++);
						parameters.add(cp);
						
					}
					
					List<ComponentParameter> toRemove = new ArrayList<ComponentParameter>();
					List<ComponentParameter> toadd = new ArrayList<ComponentParameter>();
					for(ComponentParameter p : ((IComponentDefinition)getItemRef()).getParameters()){
						boolean found = false;
						for(ComponentParameter _p : parameters){
							if (p.getName().equals(_p.getName())){
								
								found = true;
								break;
							}
						}
						if (!found){
							toRemove.add(p);
						}
						
					}
					
					for(ComponentParameter _p : parameters){
						boolean found = false;
						for(ComponentParameter p : ((IComponentDefinition)getItemRef()).getParameters()){
							if (p.getName().equals(_p.getName())){
								found = true;
								break;
							}
						}
						if (!found){
							toadd.add(_p);
						}
					}
					for(ComponentParameter p: toadd){
						((IComponentDefinition)getItemRef()).addComponentParameter(p);
					}
					
					for(ComponentParameter p: toRemove){
						((IComponentDefinition)getItemRef()).removeComponentParameter(p);
					}
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
				bpm.fd.design.ui.Activator.getDefault().getProject().getDictionary().firePropertyChange(Dictionary.PROPERTY_COMPONENT_CHANGED, null, ref);
				refreshParameters();
			}
			return getItemRef();
		}
		
	}
	
	
	static class ItemKey{
		String repositoryUrl;
		int  itemId;
		ItemKey(int id, String url){
			this.repositoryUrl = url;
			this.itemId = id;
		}
	}
	
	private static HashMap<ItemKey, String> itemNames = new HashMap<ItemKey, String>();
	private IRepositoryObjectReference ref;
	
	private ExpandItem item ;
	public VanillaObjectEditor(Composite parent) {
		super(parent);
		fillBar();
	}

	@Override
	protected void fillBar() {
		createContent(getControl());
//		createParameters();
	}
	
	protected IRepositoryObjectReference getItemRef(){
		return ref;
	}
	abstract protected void setHeight(Integer height);
	abstract protected void setWidth(Integer width);
	abstract protected int getHeight();
	abstract protected int getWidth();
	abstract protected void setItemId(Integer id);
	
	protected void createContent(ExpandBar parent) {
		final TreeViewer viewer = createViewer(parent);
		viewer.setContentProvider(new PropertiesContentProvider());
		TreeViewerColumn colValue = createValueViewerColum(viewer);
		
		item = new ExpandItem(parent, SWT.NONE);
		item.setControl(viewer.getTree());
		item.setText(Messages.VanillaObjectEditor_3);
		
		
		
		final Property width = new Property(Messages.VanillaObjectEditor_4, new TextCellEditor(viewer.getTree()));
		final Property height = new Property(Messages.VanillaObjectEditor_5, new TextCellEditor(viewer.getTree()));
		final Property vanillaObject = new Property(Messages.VanillaObjectEditor_6, new ItemDialogEditor(viewer.getTree()));
		
		colValue.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if (getComponentDefinition() == null){return "";} //$NON-NLS-1$
				if (element == width){return getWidth() + "";} //$NON-NLS-1$
				if (element == height){return getHeight() + "";} //$NON-NLS-1$
				if (element == vanillaObject){return itemNames.get(getItemKey()) + "";} //$NON-NLS-1$
				return ""; //$NON-NLS-1$
			}

			
		});
		
		colValue.setEditingSupport(new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				if (getComponentDefinition() == null){return;}
				if (element == width){setWidth(Integer.parseInt((String)value));}
				if (element == height){setHeight(Integer.parseInt((String)value));}
				if (element == height){}
				notifyChangeOccured();
				viewer.refresh();
			}
			
			@Override
			protected Object getValue(Object element) {
				if (getComponentDefinition() == null){return "";} //$NON-NLS-1$
				if (element == width){return getWidth() + "";} //$NON-NLS-1$
				if (element == height){return getHeight() + "";} //$NON-NLS-1$
				if (element == vanillaObject){return ref;}
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
		input.add(vanillaObject);
		input.add(width);
		input.add(height);
		viewer.setInput(input);
	}

	@Override
	public void setInput(EditPart editPart, ComponentConfig conf, IComponentDefinition component){
		this.ref = (IRepositoryObjectReference)component;
		
		if (item != null){
			if (component instanceof ComponentReport){
				item.setText(Messages.VanillaObjectEditor_15);
			}
			else if(component instanceof ComponentMarkdown){
				item.setText(Messages.VanillaObjectEditor_17);
			}
			else if(component instanceof ComponentKpi){
				item.setText("Kpi Theme");
			}
			else {
				item.setText(Messages.VanillaObjectEditor_16);
			}
		}
		
		super.setInput(editPart, conf, component);
	}
	
	private ItemKey getItemKey(){
		IRepositoryApi sock = bpm.fd.repository.ui.Activator.getDefault().getRepositoryConnection();
		for(ItemKey k : itemNames.keySet()){
			if (k.itemId == ref.getDirectoryItemId() && k.repositoryUrl.equals(sock.getContext().getRepository().getUrl())){
				return k;
			}
		}
		return null;
	}
}
