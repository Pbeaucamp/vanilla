package bpm.workflow.ui.views.property.sections;

import java.util.ArrayList;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.workflow.runtime.model.activities.reporting.BurstActivity;
import bpm.workflow.runtime.model.activities.reporting.ReportActivity;
import bpm.workflow.runtime.resources.variables.ListVariable;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;
import bpm.workflow.ui.icons.IconsNames;

/**
 * Section for the selection of the groups
 * @author CHARBONNIER, MARTIN
 *
 */
public class GroupsSection extends AbstractPropertySection {
	private List listAvailable;
	private List listSelected; 
	private Node node;
	private Button add;
	private Button remove;

	
	private IRepositoryApi sock;

	public GroupsSection() { }
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);

		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(3, false));
		
		Label l = new Label(composite,  SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.GroupsSection_2);
		
		new Label(composite, SWT.NONE);
		
		Label l2 = new Label(composite,  SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Messages.GroupsSection_0); 
		
		listAvailable = new List (composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		listAvailable.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		Composite c = new Composite(composite, SWT.NONE);
		c.setLayoutData(new GridData(GridData.CENTER, GridData.FILL, false, true, 1, 2));
		c.setLayout(new GridLayout());
		
		
		Composite cc = new Composite(c, SWT.NONE);
		cc.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, true));
		cc.setLayout(new GridLayout());
		
		add = new Button(cc, SWT.PUSH);
		add.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		add.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.ADD_16));
		add.setToolTipText(Messages.GroupsSection_3);
		
		remove = new Button(cc, SWT.PUSH);
		remove.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		remove.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.DEL_16));
		remove.setToolTipText(Messages.GroupsSection_1); 
		
		listSelected = new List (composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		listSelected.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.node = (Node)((NodePart) input).getModel();
	}
	
	@Override
	public void refresh() {
		ReportActivity a = (ReportActivity) node.getWorkflowObject();
		try {
			int itemid = ((ReportActivity) a).getBiObject().getId();
			sock = Activator.getDefault().getRepositoryConnection();
			
			java.util.List<String> notselected = new ArrayList<String>();
			java.util.List<String> selectedGroups = new ArrayList<String>();
			
			RepositoryItem item = new RepositoryItem();
			item.setId(itemid);
			java.util.List<Integer> availableGroups = sock.getAdminService().getAllowedGroupId(item);
			String[] var = ListVariable.getInstance().getNoEnvironementVariable();

			
			if (a instanceof BurstActivity) {
				selectedGroups = ((BurstActivity) a).getGroups();
			}
			else if (a.getSecurityProvider() != null) {
				selectedGroups.add(a.getSecurityProvider());
				
			}
			
			for (Integer g : availableGroups) {
				String groupName = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroupById(g).getName();
				if (!selectedGroups.contains(groupName)) {
					notselected.add(groupName);
				}
			}
			if (!(a instanceof BurstActivity)) {
				for (String g : var) {
					if (!selectedGroups.contains(g)) {
						notselected.add(g);
					}
				}
			}
			
		
			listAvailable.setItems(notselected.toArray(new String[notselected.size()]));
			listSelected.setItems(selectedGroups.toArray(new String[selectedGroups.size()]));
			
		} catch (Exception e) {
			listAvailable.removeAll();
			listSelected.removeAll();
		}
	}
	
	@Override
	public void aboutToBeShown() {
		if (!add.isListening(SWT.Selection))
			add.addSelectionListener(adapteradd);
		if (!remove.isListening(SWT.Selection))
			remove.addSelectionListener(adapterrm);
		
		super.aboutToBeShown();
		
	}
	
	@Override
	public void aboutToBeHidden() {
		add.removeSelectionListener(adapteradd);
		remove.removeSelectionListener(adapterrm);
		
		super.aboutToBeHidden();
	}


	
	
	private SelectionAdapter adapteradd = new SelectionAdapter(){

		@Override
		public void widgetSelected(SelectionEvent e) {
		
			if (listAvailable.getSelection().length == 0){
				return;
			}
			
			if (node.getWorkflowObject() instanceof BurstActivity) {
				java.util.List<String> temp = new ArrayList<String>();
				for (int k = 0; k < listAvailable.getSelectionIndices().length; k++) {
					String s = listAvailable.getItem(listAvailable.getSelectionIndices()[k]);
					listSelected.add(s);
					temp.add(s);
					((BurstActivity)node.getWorkflowObject()).addGroup(s);
				}
				for (String t : temp) {
					listAvailable.remove(t);
				}
				
			}
			else if (node.getWorkflowObject() instanceof ReportActivity) {
				if (((ReportActivity) node.getWorkflowObject()).getSecurityProvider() == null) {
					String s = listAvailable.getItem(listAvailable.getSelectionIndices()[0]);
					((ReportActivity)node.getWorkflowObject()).setSecurityProvider(s);
					listSelected.add(s);
					listAvailable.remove(s);
				}
			}

			
			
		}
		
	};
	
	private SelectionAdapter adapterrm = new SelectionAdapter(){

		@Override
		public void widgetSelected(SelectionEvent e) {
			
			if (listSelected.getSelection().length == 0){
				return;
			}
			
			if (node.getWorkflowObject() instanceof BurstActivity) {
				java.util.List<String> temp = new ArrayList<String>();
				for (int k = 0; k < listSelected.getSelectionIndices().length; k++) {
					String s = listSelected.getItem(listSelected.getSelectionIndices()[k]);
					listAvailable.add(s);
					temp.add(s);
					((BurstActivity)node.getWorkflowObject()).removeGroup(s);
				}
		
				for (String t : temp) {
					listSelected.remove(t);
				}
			}
			else {
				String s = listSelected.getItem(listSelected.getSelectionIndices()[0]);
				listAvailable.add(s);
				((ReportActivity) node.getWorkflowObject()).delSecurityProvider();
				listSelected.remove(s);
			}
		}
		
	};
	

}
