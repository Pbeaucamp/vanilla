package bpm.gateway.ui.views.property.sections.vanilla;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.Server;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.ldap.LdapServer;
import bpm.gateway.core.transformations.vanilla.VanillaLdapSynchro;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class VanillaLdapSynchroSection extends AbstractPropertySection {
	private class ServerLabelProvider extends LabelProvider{

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			return ((Server)element).getName();
		}
		
	}
	
	protected Node node;
	
	protected ComboViewer ldapServers;
	private Text groupNode, groupName, userNode, userName, groupFilter, groupAttribute, userMemberNodeName;
	private Text groupMemberAttribute;
	private TableViewer userDetails;
	
	private ModifyListener listener = new ModifyListener(){

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
		 */
		public void modifyText(ModifyEvent e) {
			VanillaLdapSynchro t = (VanillaLdapSynchro)node.getGatewayModel();

			if (e.widget == groupName){
				t.setLdapGroupNodeName(groupName.getText());
			}
			else if (e.widget == groupNode){
				t.setLdapGroupDn(groupNode.getText());
			}
			else if (e.widget == userName){
				t.setLdapUserNodeName(userName.getText());
			}
			else if (e.widget == userNode){
				t.setLdapUsersDn(userNode.getText());
			}
			else if (e.widget == groupMemberAttribute){
				t.setLdapGroupMemberNodeName(groupMemberAttribute.getText());
			}
			else if (e.widget == groupFilter){
				t.setGroupFilter(groupFilter.getText());
			}			
			else if (e.widget == groupAttribute){
				t.setGroupAttribute(groupAttribute.getText());
			}
			else if (e.widget == userMemberNodeName){
				t.setUserMemberNodeName(userMemberNodeName.getText());
			}
		}
		
	};

	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#aboutToBeHidden()
	 */
	@Override
	public void aboutToBeHidden() {
		
		
		super.aboutToBeHidden();
	}


	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#aboutToBeShown()
	 */
	@Override
	public void aboutToBeShown() {
		super.aboutToBeShown();
		
	}

	private ISelectionChangedListener selectionListener = new ISelectionChangedListener(){
		public void selectionChanged(SelectionChangedEvent event) {
			
			VanillaLdapSynchro t = (VanillaLdapSynchro)node.getGatewayModel();
			if (event.getSource() == ldapServers){
				if (ldapServers.getSelection().isEmpty()){
					t.setLdapServer(null);
				}
				else{
					t.setLdapServer((LdapServer)((IStructuredSelection)ldapServers.getSelection()).getFirstElement());
				}
			}
			
			
				
			
		}
	};
	
	public VanillaLdapSynchroSection() {
	}
	
	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite composite = getWidgetFactory().createComposite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		/*
		 * servers
		 */
		Group gServers = getWidgetFactory().createGroup(composite, Messages.VanillaLdapSynchroSection_0);
		gServers.setLayout(new GridLayout(2, false));
		gServers.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label l = getWidgetFactory().createLabel(gServers, Messages.VanillaLdapSynchroSection_1);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		ldapServers = new ComboViewer(getWidgetFactory().createCCombo(gServers, SWT.READ_ONLY | SWT.BORDER));
		ldapServers.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		ldapServers.setLabelProvider(new ServerLabelProvider());
		ldapServers.setContentProvider(new IStructuredContentProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
			 */
			public void dispose() {
				
			}

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
			 */
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
			}

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
			 */
			public Object[] getElements(Object inputElement) {
				List l = (List)inputElement;
				return l.toArray(new Object[l.size()]);
			}
			
		});
		ldapServers.addSelectionChangedListener(selectionListener);
		
		
		/*
		 * ldapInfos
		 */
		Group ldapInfo = getWidgetFactory().createGroup(composite, Messages.VanillaLdapSynchroSection_3);
		ldapInfo.setLayout(new GridLayout(2, false));
		ldapInfo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label l3 = getWidgetFactory().createLabel(ldapInfo, Messages.VanillaLdapSynchroSection_4);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		groupNode = getWidgetFactory().createText(ldapInfo, "", SWT.FLAT | SWT.BORDER); //$NON-NLS-1$
		groupNode.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		groupNode.addModifyListener(listener);
		
		Label l4 = getWidgetFactory().createLabel(ldapInfo, Messages.VanillaLdapSynchroSection_6);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		groupName = getWidgetFactory().createText(ldapInfo, "", SWT.FLAT | SWT.BORDER); //$NON-NLS-1$
		groupName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		groupName.addModifyListener(listener);
		
		Label l5 = getWidgetFactory().createLabel(ldapInfo, Messages.VanillaLdapSynchroSection_8);
		l5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		userNode = getWidgetFactory().createText(ldapInfo, "", SWT.FLAT | SWT.BORDER); //$NON-NLS-1$
		userNode.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		userNode.addModifyListener(listener);
		
		Label l6 = getWidgetFactory().createLabel(ldapInfo, Messages.VanillaLdapSynchroSection_10);
		l6.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		userName = getWidgetFactory().createText(ldapInfo, "", SWT.FLAT | SWT.BORDER); //$NON-NLS-1$
		userName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		userName.addModifyListener(listener);
		
		Label l49 = getWidgetFactory().createLabel(ldapInfo, Messages.VanillaLdapSynchroSection_2);
		l49.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		groupFilter = getWidgetFactory().createText(ldapInfo, "", SWT.FLAT | SWT.BORDER); //$NON-NLS-1$
		groupFilter.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		groupFilter.addModifyListener(listener);
		
		Label l92 = getWidgetFactory().createLabel(ldapInfo, Messages.VanillaLdapSynchroSection_5);
		l92.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		groupAttribute = getWidgetFactory().createText(ldapInfo, "", SWT.FLAT | SWT.BORDER); //$NON-NLS-1$
		groupAttribute.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		groupAttribute.addModifyListener(listener);
		
		Label l7 = getWidgetFactory().createLabel(ldapInfo, Messages.VanillaLdapSynchroSection_12);
		l7.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		groupMemberAttribute= getWidgetFactory().createText(ldapInfo, "", SWT.FLAT | SWT.BORDER); //$NON-NLS-1$
		groupMemberAttribute.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		groupMemberAttribute.addModifyListener(listener);

		Label l2432 = getWidgetFactory().createLabel(ldapInfo, Messages.VanillaLdapSynchroSection_7);
		l2432.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		userMemberNodeName= getWidgetFactory().createText(ldapInfo, "", SWT.FLAT | SWT.BORDER); //$NON-NLS-1$
		userMemberNodeName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		userMemberNodeName.addModifyListener(listener);
		
		Label l8 = getWidgetFactory().createLabel(composite, Messages.VanillaLdapSynchroSection_14);
		l8.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		createTableViewer(composite);
	}
	
	@Override
	public void refresh() {
		ldapServers.removeSelectionChangedListener(selectionListener);
		groupMemberAttribute.removeModifyListener(listener);
		groupName.removeModifyListener(listener);
		groupNode.removeModifyListener(listener);
		userName.removeModifyListener(listener);
		userNode.removeModifyListener(listener);
		groupFilter.removeModifyListener(listener);
		groupAttribute.removeModifyListener(listener);
		userMemberNodeName.removeModifyListener(listener);
		
		
		VanillaLdapSynchro t = (VanillaLdapSynchro)node.getGatewayModel();
		
		/*
		 * fillServers
		 */
		
		ldapServers.setInput(ResourceManager.getInstance().getServers(LdapServer.class));
		if (t.getLdapServer() != null){
			ldapServers.setSelection(new StructuredSelection(t.getLdapServer()));
		}
		else{
			ldapServers.setSelection(new StructuredSelection());
		}
		
		/*
		 * fill Test fields
		 */
		groupName.setText(t.getLdapGroupNodeName());
		groupNode.setText(t.getLdapGroupDn());
		userNode.setText(t.getLdapUsersDn());
		userName.setText(t.getLdapUserNodeName());
		groupMemberAttribute.setText(t.getLdapGroupMemberNodeName());
		if(t.getGroupFilter() != null) {
			groupFilter.setText(t.getGroupFilter());
		}
		if(t.getGroupAttribute() != null) {
			groupAttribute.setText(t.getGroupAttribute());
		}
		if(t.getUserMemberNodeName() != null) {
			userMemberNodeName.setText(t.getUserMemberNodeName());
		}
		
		ldapServers.addSelectionChangedListener(selectionListener);
		groupMemberAttribute.addModifyListener(listener);
		groupName.addModifyListener(listener);
		groupNode.addModifyListener(listener);
		userName.addModifyListener(listener);
		userNode.addModifyListener(listener);
		groupFilter.addModifyListener(listener);
		groupAttribute.addModifyListener(listener);
		userMemberNodeName.addModifyListener(listener);
		
		List<Integer> tab = new ArrayList<Integer>();
		for(int i = 0; i< VanillaLdapSynchro.IX_USER_FIELD_NAME.length; i++){
			tab.add(i);
		}
		
		userDetails.setInput(tab);
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.node = (Node)((NodePart) input).getModel();
	}

	
	private void createTableViewer(Composite composite){
		userDetails = new TableViewer(getWidgetFactory().createTable(composite, SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.FLAT));
		userDetails.setContentProvider(new IStructuredContentProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
			 */
			public Object[] getElements(Object inputElement) {
				Collection<?> c = (Collection<?>)inputElement;
				return c.toArray(new Object[c.size()]);
			}

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
			 */
			public void dispose() {
				
			}

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
			 */
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
			}
			
		});
		userDetails.setLabelProvider(new ITableLabelProvider(){

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				VanillaLdapSynchro t = (VanillaLdapSynchro)node.getGatewayModel();
				
				if (columnIndex == 0){
					return VanillaLdapSynchro.IX_USER_FIELD_NAME[(Integer)element];	
				}
				else if (columnIndex == 1){
					return t.getUserAttribute((Integer)element);
				}
				return null;
			}

			public void addListener(ILabelProviderListener listener) {
				
			}

			public void dispose() {
				
			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {
				
			}
			
		});
		userDetails.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		userDetails.getTable().setHeaderVisible(true);
		
		TableColumn name = new TableColumn(userDetails.getTable(), SWT.NONE);
		name.setText(Messages.VanillaLdapSynchroSection_15);
		name.setWidth(150);
		
		TableColumn ldapAttribute = new TableColumn(userDetails.getTable(), SWT.NONE);
		ldapAttribute.setText(Messages.VanillaLdapSynchroSection_16);
		ldapAttribute.setWidth(150);
		
		userDetails.setColumnProperties(new String[]{"name", "node"}); //$NON-NLS-1$ //$NON-NLS-2$
		userDetails.setCellEditors(new CellEditor[]{null, new TextCellEditor(userDetails.getTable())});
		
		userDetails.setCellModifier(new ICellModifier(){

			public boolean canModify(Object element, String property) {
				if (property.equals("node")){ //$NON-NLS-1$
					return true;
				}
				return false;
			}

			public Object getValue(Object element, String property) {
				VanillaLdapSynchro t = (VanillaLdapSynchro)node.getGatewayModel();
				
				if (property.equals("name")){ //$NON-NLS-1$
					return VanillaLdapSynchro.IX_USER_FIELD_NAME[(Integer)element];
				}
				else if (property.equals("node")){ //$NON-NLS-1$
					return t.getUserAttribute((Integer)element);
				}
				return null;
			}

			public void modify(Object element, String property, Object value) {
				VanillaLdapSynchro t = (VanillaLdapSynchro)node.getGatewayModel();
				if (property.equals("node")){ //$NON-NLS-1$
					t.setUserAttributeMapping((Integer)((TableItem)element).getData(), (String)value);
					userDetails.refresh();
				}
				
			}
			
		});
	}
}


