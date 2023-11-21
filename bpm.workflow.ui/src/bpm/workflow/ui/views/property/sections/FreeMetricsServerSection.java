package bpm.workflow.ui.views.property.sections;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.workflow.runtime.model.IServer;
import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.model.activities.KPIActivity;
import bpm.workflow.runtime.resources.servers.FreemetricServer;
import bpm.workflow.runtime.resources.servers.ListServer;
import bpm.workflow.runtime.resources.servers.Server;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

/**
 * Section for the selection of a freemetrics server (KPI Activity)
 * @author Charles MARTIN
 *
 */
public class FreeMetricsServerSection extends AbstractPropertySection {
	private Combo server;
	private Node node;
	private Composite composite;
	
	public FreeMetricsServerSection() {}
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		
		composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(2, false));
		
		CLabel labelLabel = getWidgetFactory().createCLabel(composite, Messages.FreeMetricsServerSection_0);
		labelLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		server = new Combo(composite, SWT.READ_ONLY);
		server.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		server.addSelectionListener(adaptder);
	}

	@Override
	public void refresh() {
		List<Server> servers = ListServer.getInstance().getServers();
		List<Server> temp = new ArrayList<Server>();
		for (Server s : servers) {
			if (s.getClass() == ((IServer) node.getWorkflowObject()).getServerClass()) {
				temp.add(s);
			}
		}
	
		int selectTo = -1;
		String[] items = new String[temp.size()];
		int index = 0;
		for (Server s : temp) {
			items[index] = s.getName();
			if (s.getClass() == ((IServer) node.getWorkflowObject()).getServerClass() 
					&& ((IServer) node.getWorkflowObject()).getServer() != null 
					&& s.getName().equalsIgnoreCase(((IServer) node.getWorkflowObject()).getServer().getName())) {
				selectTo = index;
			}
			index++;
		}
		
		server.setItems(items);
		server.select(selectTo);
		
		
	}
	
	@Override
	public void aboutToBeShown() {
		super.aboutToBeShown();
		
	}


	@Override
	public void aboutToBeHidden() {
		super.aboutToBeHidden();
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.node = (Node)((NodePart) input).getModel();
	}
	

	SelectionAdapter adaptder = new SelectionAdapter() {


		@Override
		public void widgetSelected(SelectionEvent e) {
			Server s = null;
			if (server.getText() != null) {
				s = ListServer.getInstance().getServer(server.getText());
			}
			
			if (s!=null && s instanceof FreemetricServer) {
				((KPIActivity) node.getWorkflowObject()).setServer(s);
				
			}
			
			if (s != null){
				((WorkflowModel)Activator.getDefault().getCurrentModel()).addResource(s);
			}
			
		}
	
	};

//	protected void selectReportItem(Server s) {
//		DialogRepositoryObject dial = new DialogRepositoryObject(composite.getShell() , (BiRepository) s,0);
//		
//		if (dial.open() == Dialog.OK) {
//			IDirectoryItem item = dial.getRepositoryItem();
//			ReportObject biObject = null;
//			if (item.getType() == IRepositoryConnection.FWR_TYPE) {
//				biObject = new FwrObject(item.getId(), (BiRepository) s);
//			}
//			else if (item.getType() == IRepositoryConnection.CUST_TYPE) {
//				if (item.getSubType().equalsIgnoreCase(IRepositoryConnection.SUBTYPES_NAMES[IRepositoryConnection.BIRT_REPORT_SUBTYPE])) {
//					biObject = new BirtReport(item.getId(), (BiRepository) s);	
//				}
//				else if (item.getSubType().equalsIgnoreCase(IRepositoryConnection.SUBTYPES_NAMES[IRepositoryConnection.JASPER_REPORT_SUBTYPE])) {
//					biObject = new JrxmlReport(item.getId(), (BiRepository) s);	
//					try {
//						IJrxml ij = ((BiRepository) s).getRepositoryConnection().loadJrxml(item);
//						((JrxmlReport) biObject).setDataSourceId(ij.getDatasourceId());
//					} catch (Exception e1) {
//
//					}
//				}
//				
//			}
//			
//			((ReportActivity) node.getWorkflowObject()).setReportObject(biObject);
//			biObject.setName(item.getName());
//			((IBIRepServer) node.getWorkflowObject()).setItemName(item.getName());
//			Activator.getDefault().getModel().addResource(s);
//		}
//		
//	}

//	protected void selectInterfaceItem(Server s) {
//		DialogRepositoryObject dial = new DialogRepositoryObject(composite.getShell() , (BiRepository) s,0);
//		
//		if (dial.open() == Dialog.OK) {
//			IDirectoryItem item = dial.getRepositoryItem();
//			if (item.getType() == IRepositoryConnection.CUST_TYPE && item.getSubType().equalsIgnoreCase(IRepositoryConnection.SUBTYPES_NAMES[IRepositoryConnection.ORBEON_XFORMS])) {
//				InterfaceObject biObject = new InterfaceObject(item.getId(), (BiRepository) s);	
//				biObject.setItemName(item.getName());
//				biObject.setName(item.getName());
//				((InterfaceActivity) node.getWorkflowObject()).setInterface(biObject);
//				Activator.getDefault().getModel().addResource(s);
//			}
//		}
//		
//	}

}

