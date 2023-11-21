package bpm.gateway.ui.views.property.sections.vanilla;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.transformations.vanilla.VanillaRoleGroupAssocaition;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Role;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class VanillaRoleGroupMappingSection extends AbstractPropertySection{
	
	protected CheckboxTableViewer rolesTable;
	protected ComboViewer groupId;
	protected ToolItem selectAll, uncheckAll, refresh, showOnlySelected;
	
	private VanillaRoleGroupAssocaition transfo;
	
	private ICheckStateListener checkListener = new ICheckStateListener() {
		
		public void checkStateChanged(CheckStateChangedEvent event) {
			if (event.getChecked()){
				transfo.addRole(((Role)event.getElement()).getId());
			}
			else{
				transfo.removeRole(((Role)event.getElement()).getId());
			}
			
		}
	};
	
	private ISelectionChangedListener groupList = new ISelectionChangedListener() {
		
		public void selectionChanged(SelectionChangedEvent event) {
			IStructuredSelection ss = (IStructuredSelection)groupId.getSelection();
			Object o = ss.getFirstElement();
			try{
				int i = transfo.getInputs().get(0).getDescriptor(transfo).getStreamElements().indexOf(o);
				
				if ( i < 0){
					transfo.setGroupIdIndex(null);
				}
				else{
					transfo.setGroupIdIndex(i);
				}
			}catch(Exception ex){
				
			}
			
			
			
		}
	};
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);
		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite main = getWidgetFactory().createComposite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, true));
		
		Label l = getWidgetFactory().createLabel(main, Messages.VanillaRoleGroupMappingSection_0, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		groupId = new ComboViewer(getWidgetFactory().createCCombo(main, SWT.NONE));
		groupId.getCCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		groupId.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }
			
			public void dispose() { }
			
			public Object[] getElements(Object inputElement) {
				Collection c = (Collection)inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});
		groupId.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((StreamElement)element).name;
			}
		});
		
		
		
		ToolBar bar = new ToolBar(main, SWT.HORIZONTAL);
		bar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		createItems(bar);
		
		l = getWidgetFactory().createLabel(main, Messages.VanillaRoleGroupMappingSection_1, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		
		/*
		 * create Table
		 */
		rolesTable = CheckboxTableViewer.newCheckList(main, SWT.BORDER);
		rolesTable.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		rolesTable.addCheckStateListener(checkListener);
		rolesTable.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }
			
			public void dispose() { }
			
			public Object[] getElements(Object inputElement) {
				Collection c = (Collection)inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});
	
		TableViewerColumn col = new TableViewerColumn(rolesTable, SWT.FILL);
		col.getColumn().setText(Messages.VanillaRoleGroupMappingSection_2);
		col.getColumn().setWidth(400);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Role)element).getName();
			}
		});
		
		
	}
	
	private void createItems(ToolBar main){
		selectAll = new ToolItem(main, SWT.PUSH);
		selectAll.setText(Messages.VanillaRoleGroupMappingSection_3);
		selectAll.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				for(Object o : ((IStructuredContentProvider)rolesTable.getContentProvider()).getElements(rolesTable.getInput())){
					rolesTable.setChecked(o, true);
					transfo.addRole(((Role)o).getId());
				}
				
				rolesTable.refresh();
			}
			
		});
		
		uncheckAll = new ToolItem(main, SWT.PUSH);
		uncheckAll.setText(Messages.VanillaRoleGroupMappingSection_4);
		uncheckAll.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				for(Object o : ((IStructuredContentProvider)rolesTable.getContentProvider()).getElements(rolesTable.getInput())){
					rolesTable.setChecked(o, false);
					transfo.removeRole(((Role)o).getId());
				}
				
				rolesTable.refresh();
			}
			
		});
		
		
		refresh = new ToolItem(main, SWT.PUSH);
		refresh.setText(Messages.VanillaRoleGroupMappingSection_5);
		refresh.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				try {
					IVanillaAPI vanillaApi = new RemoteVanillaPlatform(Activator.getDefault().getVanillaContext());
					rolesTable.setInput(vanillaApi.getVanillaSecurityManager().getRoles());
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getPart().getSite().getShell(), Messages.VanillaRoleGroupMappingSection_8, Messages.VanillaRoleGroupMappingSection_9+  e1.getMessage());
				}
			}
			
		});
	}

	@Override
	public void refresh() {
		
		/*
		 * GroupID
		 */
		groupId.removeSelectionChangedListener(groupList);
		if (!transfo.getInputs().isEmpty()){
			try{
				groupId.setInput(transfo.getInputs().get(0).getDescriptor(transfo).getStreamElements());
				
				if (transfo.getGroupIdIndex() != null){
					for(int i = 0; i < ((List)groupId.getInput()).size(); i++){
						if (transfo.getGroupIdIndex().intValue() == i){
							groupId.setSelection(new StructuredSelection(((List)groupId.getInput()).get(i)));
						}
					}
				}
				
			}catch(Exception ex){
				
			}
		}
		groupId.addSelectionChangedListener(groupList);
		/*
		 * load Roles
		 */

			

		try {
			IVanillaAPI vanillaApi = new RemoteVanillaPlatform(Activator.getDefault().getVanillaContext());
			rolesTable.setInput(vanillaApi.getVanillaSecurityManager().getRoles());
			
			
			for(Role r : (List<Role>)rolesTable.getInput()){
				for(Integer i : transfo.getRolesId()){
					if (r.getId().intValue() == i.intValue()){
						rolesTable.setChecked(r, true);
					}
				}
			}
			
		} catch (Exception e1) {
			e1.printStackTrace();
			MessageDialog.openError(getPart().getSite().getShell(), Messages.VanillaRoleGroupMappingSection_10, Messages.VanillaRoleGroupMappingSection_11+  e1.getMessage());
		}

		
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.transfo = (VanillaRoleGroupAssocaition)((Node)((NodePart) input).getModel()).getGatewayModel();
	}
}
