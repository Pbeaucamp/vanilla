package bpm.workflow.ui.views.property.sections;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.metadata.MetaDataReader;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.query.SavedQuery;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.workflow.runtime.model.IAcceptInput;
import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.IOutputProvider;
import bpm.workflow.runtime.model.IRepositoryItem;
import bpm.workflow.runtime.model.WorkflowDigester;
import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.model.activities.BiWorkFlowActivity;
import bpm.workflow.runtime.model.activities.InterfaceActivity;
import bpm.workflow.runtime.model.activities.InterfaceGoogleActivity;
import bpm.workflow.runtime.model.activities.reporting.ReportActivity;
import bpm.workflow.runtime.model.activities.vanilla.GatewayActivity;
import bpm.workflow.runtime.model.activities.vanilla.GedActivity;
import bpm.workflow.runtime.model.activities.vanilla.MetadataToD4CActivity;
import bpm.workflow.runtime.resources.BIWObject;
import bpm.workflow.runtime.resources.BiRepositoryObject;
import bpm.workflow.runtime.resources.BirtReport;
import bpm.workflow.runtime.resources.FwrObject;
import bpm.workflow.runtime.resources.GatewayObject;
import bpm.workflow.runtime.resources.InterfaceObject;
import bpm.workflow.runtime.resources.JrxmlReport;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.dialogs.DialogRepositoryObject;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

/**
 * Section for the selection of an item located on a repository
 * 
 * @author CHARBONNIER, MARTIN
 * 
 */
public class RepositoryServerSection extends AbstractPropertySection {
	private Node node;
	private Composite composite;
	private Text itemName;
	private Button checkMassReporting;
	private ComboViewer lstMetadataQuery;
	private Label lblMeQuery;

	public RepositoryServerSection() {}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(3, false));

		CLabel itemLabel = getWidgetFactory().createCLabel(composite, Messages.RepositoryServerSection_2);
		itemLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		itemName = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		itemName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		itemName.setEnabled(false);

		Button btnSelectItem = new Button(composite, SWT.PUSH);
		btnSelectItem.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		btnSelectItem.setText("..."); //$NON-NLS-1$

		btnSelectItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(node.getWorkflowObject() instanceof ReportActivity) {
					selectReportItem();
				}
				else if(node.getWorkflowObject() instanceof InterfaceActivity) {
					InterfaceActivity act = (InterfaceActivity) node.getWorkflowObject();
					if(act.getType().equalsIgnoreCase("0")) { //$NON-NLS-1$
						selectInterfaceItem();
					}
					else {
						selectInterfaceFDItem();
					}

				}
				else if(node.getWorkflowObject() instanceof InterfaceGoogleActivity) {
					selectInterfaceGoogleItem();

				}

				else if(node.getWorkflowObject() instanceof GatewayActivity) {
					selectGatewayItem();

				}
				else if(node.getWorkflowObject() instanceof BiWorkFlowActivity) {
					selectBiwItems();
				}
				else if(node.getWorkflowObject() instanceof MetadataToD4CActivity) {
					try {
						selectFmdtItems();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				else {
					selectRepositoryItem();
				}
			}

		});

		checkMassReporting = new Button(composite, SWT.CHECK);
		checkMassReporting.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
		checkMassReporting.setText(Messages.RepositoryServerSection_0);
		checkMassReporting.setToolTipText(Messages.RepositoryServerSection_1);
		checkMassReporting.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ReportActivity act = (ReportActivity) node.getWorkflowObject();
				act.setMassReporting(checkMassReporting.getSelection());
			}
		});
		checkMassReporting.setVisible(false);
		
		lblMeQuery = new Label(composite, SWT.NONE);
		lblMeQuery.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
		lblMeQuery.setText(Messages.RepositoryServerSection_3);
		lblMeQuery.setToolTipText(Messages.RepositoryServerSection_4);
		
		lstMetadataQuery = new ComboViewer(composite, SWT.BORDER);
		lstMetadataQuery.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
		lstMetadataQuery.setContentProvider(new ArrayContentProvider());
		lstMetadataQuery.setLabelProvider(new LabelProvider());
		

		
		lstMetadataQuery.addSelectionChangedListener(new ISelectionChangedListener() {		
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				((MetadataToD4CActivity)node.getWorkflowObject()).setQueryName(lstMetadataQuery.getCombo().getText());
			}
		});


		getWidgetFactory().createCLabel(composite, ""); //$NON-NLS-1$
	}
	
	private void selectFmdtItems() throws Exception {
		DialogRepositoryObject dial = new DialogRepositoryObject(composite.getShell(), IRepositoryApi.FMDT_TYPE);

		if(dial.open() == Dialog.OK) {
			RepositoryItem item = dial.getRepositoryItem();
			BiRepositoryObject obj = new BiRepositoryObject();
			obj.setId(item.getId() + ""); //$NON-NLS-1$
			obj.setItemName(item.getItemName());
			((IRepositoryItem) node.getWorkflowObject()).setItem(obj);
			
			//load queries
			List<IBusinessModel> bModels = null;
			String result = Activator.getDefault().getRepositoryConnection().getRepositoryService().loadModel(item);
			try {
				bModels = MetaDataReader.read(Activator.getDefault().getRepositoryConnection().getContext().getGroup().getName(), IOUtils.toInputStream(result, "UTF-8"), Activator.getDefault().getRepositoryConnection(), false); //$NON-NLS-1$
			} catch (Exception e) {
				e.printStackTrace();
				return ;
			}

			List<String> queries = new ArrayList<String>();
			if (bModels != null) {
				for (IBusinessModel bmod : bModels) {

					List<IBusinessPackage> packages = bmod.getBusinessPackages(Activator.getDefault().getRepositoryConnection().getContext().getGroup().getName());
					for (IBusinessPackage pack : packages) {
						if (pack.isExplorable()) {
							for(SavedQuery query: pack.getSavedQueries()){
								queries.add(query.getName());
								if(((MetadataToD4CActivity)node.getWorkflowObject()).getQueryName() == null) {
									((MetadataToD4CActivity)node.getWorkflowObject()).setQueryName(query.getName());
								}
							}
						}
					}				
				}
			}
			
			lstMetadataQuery.setInput(queries);
		}

		refresh();
	}

	protected void selectRepositoryItem() {
		DialogRepositoryObject dial = new DialogRepositoryObject(composite.getShell(), -1);

		if(dial.open() == Dialog.OK) {
			RepositoryItem item = dial.getRepositoryItem();
			BiRepositoryObject obj = new BiRepositoryObject();
			obj.setId(item.getId() + ""); //$NON-NLS-1$
			obj.setItemName(item.getItemName());
			((IRepositoryItem) node.getWorkflowObject()).setItem(obj);
		}

		refresh();
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
		this.node = (Node) ((NodePart) input).getModel();
		if(node.getWorkflowObject() instanceof ReportActivity) {
			checkMassReporting.setVisible(true);
			if(((ReportActivity) node.getWorkflowObject()).isMassReporting()) {
				checkMassReporting.setSelection(true);
			}
		}
		else {
			checkMassReporting.setVisible(false);
		}
		if(!(node.getWorkflowObject() instanceof MetadataToD4CActivity)) {
			lblMeQuery.setVisible(false);
			lstMetadataQuery.getCombo().setVisible(false);
		}
		else {
			try {
				lstMetadataQuery.getCombo().setText(((MetadataToD4CActivity)node.getWorkflowObject()).getQueryName());
			} catch (Exception e) {
			}
		}

		refresh();
	}

	protected void selectBiwItems() {
		DialogRepositoryObject dial = new DialogRepositoryObject(composite.getShell(), 680);

		if(dial.open() == Dialog.OK) {
			RepositoryItem item = dial.getRepositoryItem();
			if(item.getType() == IRepositoryApi.BIW_TYPE) {
				BIWObject biObject = new BIWObject(item.getId());
				biObject.setItemName(item.getItemName());
				IRepositoryApi r = Activator.getDefault().getRepositoryConnection();

				String xml = ""; //$NON-NLS-1$
				WorkflowModel doc = null;
				try {
					xml = r.getRepositoryService().loadModel(item);
					InputStream is = IOUtils.toInputStream(xml, "UTF-8"); //$NON-NLS-1$

					doc = WorkflowDigester.getModel(WorkflowModel.class.getClassLoader(), is);
				} catch(Exception e) {
					e.printStackTrace();
				}

				String subWorkflow = doc.getId() + r.getContext().getRepository().getId() + item.getId();
				((BiWorkFlowActivity) node.getWorkflowObject()).setIdBIW(subWorkflow);
				((BiWorkFlowActivity) node.getWorkflowObject()).setBIWObject(biObject);
			}
		}
		refresh();
	}

	protected void selectReportItem() {
		DialogRepositoryObject dial = new DialogRepositoryObject(composite.getShell(), 780);

		if(dial.open() == Dialog.OK) {
			RepositoryItem item = dial.getRepositoryItem();

			if(item.getType() == IRepositoryApi.FWR_TYPE) {
				FwrObject biObject = new FwrObject(item.getId());
				((ReportActivity) node.getWorkflowObject()).setReportObject(biObject);
				biObject.setItemName(item.getItemName());
				((ReportActivity) node.getWorkflowObject()).setReportObject(biObject);
			}
			else if(item.getType() == IRepositoryApi.CUST_TYPE) {
				if(item.getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE) {
					BirtReport biObject = new BirtReport(item.getId());
					((ReportActivity) node.getWorkflowObject()).setReportObject(biObject);
					biObject.setItemName(item.getItemName());
					((ReportActivity) node.getWorkflowObject()).setReportObject(biObject);
				}
				else if(item.getSubtype() == IRepositoryApi.JASPER_REPORT_SUBTYPE) {
					JrxmlReport biObject = new JrxmlReport(item.getId());
					((ReportActivity) node.getWorkflowObject()).setReportObject(biObject);
					biObject.setItemName(item.getItemName());
					((ReportActivity) node.getWorkflowObject()).setReportObject(biObject);

				}

			}
			ReportActivity ac = (ReportActivity) node.getWorkflowObject();
			for(IActivity a : Activator.getDefault().getCurrentInput().getWorkflowModel().getActivities().values()) {
				if(a instanceof IAcceptInput) {
					if(a instanceof GedActivity) {
						((GedActivity) a).addItemIdForFile(((ReportActivity) ac).getBiObject().getId(), ((IOutputProvider) ac).getOutputVariable());
					}
				}
			}

			refresh();

		}

	}

	protected void selectInterfaceItem() {
		DialogRepositoryObject dial = new DialogRepositoryObject(composite.getShell(), 880);

		if(dial.open() == Dialog.OK) {
			RepositoryItem item = dial.getRepositoryItem();
			if(item.getType() == IRepositoryApi.CUST_TYPE && item.getSubtype() == IRepositoryApi.ORBEON_XFORMS) {
				InterfaceObject biObject = new InterfaceObject(item.getId());
				biObject.setItemName(item.getItemName());
				((InterfaceActivity) node.getWorkflowObject()).setInterface(biObject);
			}
		}
		refresh();
	}

	protected void selectInterfaceFDItem() {
		DialogRepositoryObject dial = new DialogRepositoryObject(composite.getShell(), 480);

		if(dial.open() == Dialog.OK) {
			RepositoryItem item = dial.getRepositoryItem();

			InterfaceObject biObject = new InterfaceObject(item.getId());
			biObject.setItemName(item.getItemName());
			((InterfaceActivity) node.getWorkflowObject()).setInterface(biObject);

		}
		refresh();
	}

	protected void selectInterfaceGoogleItem() {
		DialogRepositoryObject dial = new DialogRepositoryObject(composite.getShell(), 580);

		if(dial.open() == Dialog.OK) {
			RepositoryItem item = dial.getRepositoryItem();
			if(item.getType() == IRepositoryApi.URL) {
				InterfaceObject biObject = new InterfaceObject(item.getId());
				biObject.setItemName(item.getItemName());
				((InterfaceGoogleActivity) node.getWorkflowObject()).setInterface(biObject);
			}
		}
		refresh();
	}

	protected void selectGatewayItem() {
		DialogRepositoryObject dial = new DialogRepositoryObject(composite.getShell(), 980);

		if(dial.open() == Dialog.OK) {
			RepositoryItem item = dial.getRepositoryItem();
			if(item.getType() == IRepositoryApi.GTW_TYPE) {
				GatewayObject biObject = new GatewayObject(item.getId());
				biObject.setItemName(item.getItemName());
				((GatewayActivity) node.getWorkflowObject()).setGatewayObject(biObject);
			}
		}
		refresh();
	}

	protected void selectTaskListItem() {

		DialogRepositoryObject dial = new DialogRepositoryObject(composite.getShell(), 980);

		if(dial.open() == Dialog.OK) {
			RepositoryItem item = dial.getRepositoryItem();
			if(item.getType() == IRepositoryApi.TASK_LIST) {
				GatewayObject biObject = new GatewayObject(item.getId());
				biObject.setItemName(item.getItemName());
				((GatewayActivity) node.getWorkflowObject()).setGatewayObject(biObject);
			}
		}
		refresh();
	}

	@Override
	public void refresh() {
		if(((IRepositoryItem) node.getWorkflowObject()).getItem() != null) {
			itemName.setText(((IRepositoryItem) node.getWorkflowObject()).getItem().getItemName());
		}
		else {
			itemName.setText(""); //$NON-NLS-1$
		}
		super.refresh();
	}
}
