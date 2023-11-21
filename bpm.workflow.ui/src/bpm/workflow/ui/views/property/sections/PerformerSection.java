package bpm.workflow.ui.views.property.sections;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.IManual;
import bpm.workflow.runtime.model.PoolModel;
import bpm.workflow.runtime.model.WorkflowException;
import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.model.activities.InterfaceActivity;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

/**
 * Section for the selection of the performer of the interface activity (Orbeon or FD)
 * @author CHARBONNIER, MARTIN
 *
 */
public class PerformerSection extends AbstractPropertySection {
	private ComboViewer groups;
	private Node node;
	private Text itemName;
	private IRepositoryApi sock;
	
	private List<Group> vanillaGroups;

	public PerformerSection() {
	}
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);
		
		final Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(2, false));
		
		CLabel itemLabel = getWidgetFactory().createCLabel(composite, Messages.PerformerSection_0); 
		itemLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		itemName = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		itemName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		itemName.setEnabled(false);
		
		CLabel lblGroup = getWidgetFactory().createCLabel(composite, Messages.PerformerSection_1);
		lblGroup.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		groups = new ComboViewer(composite, SWT.READ_ONLY); //$NON-NLS-1$
		groups.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		groups.setContentProvider(new ArrayContentProvider());
		groups.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Group)element).getName();
			}
		});
	}
	
	
	@Override
	public void refresh() {
		groups.getCombo().removeSelectionListener(adapter);
		if (node.getWorkflowObject() instanceof InterfaceActivity && ((InterfaceActivity) node.getWorkflowObject()).getInterface() != null) {
			int itemid = ((InterfaceActivity) node.getWorkflowObject()).getInterface().getId();
			if (itemid > 0) {
				String s = ((InterfaceActivity) node.getWorkflowObject()).getInterface().getItemName();
				itemName.setText(s);
			}
			if (sock == null)
				sock = Activator.getDefault().getRepositoryConnection();
			
			try {
				
				RepositoryItem item = new RepositoryItem();
				item.setId(itemid);
				
				List<Integer> groupIds = sock.getAdminService().getAllowedGroupId(item);
				
				vanillaGroups = new ArrayList<Group>();
				for(Integer grId : groupIds){
					vanillaGroups.add(Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroupById(grId));
				}
				
				groups.setInput(vanillaGroups.toArray(new Group[vanillaGroups.size()]));
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!((InterfaceActivity) node.getWorkflowObject()).getGroupForValidation().equals("")) { //$NON-NLS-1$
				String g = ((InterfaceActivity) node.getWorkflowObject()).getGroupForValidation();
				int index = -1;
				for (Group s : vanillaGroups) {
					index ++;
					if (s.getName().equalsIgnoreCase(g)) {						
						break;
					}
						
				}
				
				groups.getCombo().select(index);
			}
		}
		groups.getCombo().addSelectionListener(adapter);
	}
	
	
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.node = (Node)((NodePart) input).getModel();
	}
	
	
	private SelectionAdapter adapter = new SelectionAdapter(){

		@Override
		public void widgetSelected(SelectionEvent e) {
			IStructuredSelection ss = (IStructuredSelection)groups.getSelection();
			
			if(!ss.isEmpty() && ss.getFirstElement() instanceof Group) {
				Group group = (Group)ss.getFirstElement();
				IManual act = (IManual) node.getWorkflowObject();
				act.setGroupForValidation(group.getName());
				try {
					if (((WorkflowModel)Activator.getDefault().getCurrentModel()).containsPoolModel(group.getName())) {
						((WorkflowModel)Activator.getDefault().getCurrentModel()).addActivityToPool((IActivity) node.getWorkflowObject(), group.getName());	
					}
					else {
						PoolModel p = new PoolModel(group.getName());
						p.addChild(((IActivity) node.getWorkflowObject()));
						((WorkflowModel)Activator.getDefault().getCurrentModel()).addPool(p);
					}
				} catch (WorkflowException e1) {
					e1.printStackTrace();
				}
			}
		}
	};

}

