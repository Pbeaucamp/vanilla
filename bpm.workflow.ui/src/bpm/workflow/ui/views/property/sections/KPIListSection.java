package bpm.workflow.ui.views.property.sections;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.freemetrics.api.manager.FactoryManager;
import bpm.freemetrics.api.manager.IManager;
import bpm.freemetrics.api.organisation.metrics.Metric;
import bpm.freemetrics.api.organisation.relations.appl_metric.Assoc_Application_Metric;
import bpm.freemetrics.api.security.FmUser;
import bpm.freemetrics.api.utils.Tools;
import bpm.metadata.layer.physical.sql.FactorySQLConnection;
import bpm.workflow.runtime.model.activities.KPIActivity;
import bpm.workflow.runtime.resources.servers.FreemetricServer;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

/**
 * Section for the KPI Activity
 * 
 * @author Charles MARTIN
 * 
 */
public class KPIListSection extends AbstractPropertySection {
	public static Color mainBrown = new Color(Display.getDefault(), 209, 177, 129);
	public static Color secondBrown = new Color(Display.getDefault(), 238, 226, 208);

	private Combo viewer;
	private Combo select;
	private Node node;
	private HashMap<String, Integer> mapAssoc = new HashMap<String, Integer>();

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.setLayout(new GridLayout());

		org.eclipse.swt.widgets.Group general = new org.eclipse.swt.widgets.Group(parent, SWT.NONE);
		general.setLayout(new GridLayout(2, false));
		general.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		general.setText(Messages.KPIListSection_0);

		viewer = new Combo(general, SWT.READ_ONLY);
		viewer.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		org.eclipse.swt.widgets.Group comparaison = new org.eclipse.swt.widgets.Group(parent, SWT.NONE);
		comparaison.setLayout(new GridLayout(3, false));
		comparaison.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		comparaison.setText(Messages.KPIListSection_1);

		CLabel labelcom = getWidgetFactory().createCLabel(comparaison, Messages.KPIListSection_2);
		labelcom.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		select = new Combo(comparaison, SWT.READ_ONLY);
		select.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));

		CLabel labelcom1 = getWidgetFactory().createCLabel(comparaison, Messages.KPIListSection_3);
		labelcom1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

	}

	@Override
	public void refresh() {

		final List<Item> input = new ArrayList<Item>();
		final List<String> listeAssoc = new ArrayList<String>();
		IRunnableWithProgress r = new IRunnableWithProgress() {

			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				/*
				 * create the Connection to Freemetrics from its serverDefinition and Store the FmUser
				 */

				KPIActivity transfo = (KPIActivity) node.getWorkflowObject();
				FreemetricServer server = (FreemetricServer) transfo.getServer();
				FmUser user = null;
				IManager fmMgr = null;
				try {

					bpm.metadata.layer.physical.sql.SQLConnection connection = FactorySQLConnection.getInstance().createConnection(((FreemetricServer) transfo.getServer()).getJdbcDriver(), (((FreemetricServer) transfo.getServer()).getUrl()), (((FreemetricServer) transfo.getServer()).getPort()), (((FreemetricServer) transfo.getServer()).getDataBaseName()), (((FreemetricServer) transfo.getServer()).getLogin()), (((FreemetricServer) transfo.getServer()).getPassword()), (((FreemetricServer) transfo.getServer()).getDataBaseName()));

					FactoryManager.init("", Tools.OS_TYPE_WINDOWS); //$NON-NLS-1$
					fmMgr = FactoryManager.getInstance().getManager();

					user = fmMgr.getUserByNameAndPass(server.getFmLogin(), server.getFmPassword());

				} catch(Exception e) {
					e.printStackTrace();
					return;
				}

				for(Metric met : fmMgr.getMetricsForOwnerId(user.getId())) {
					for(Assoc_Application_Metric ass : fmMgr.getAssoc_Application_MetricByMetricId(met.getId())) {
						StringBuffer buf = new StringBuffer();
						Item it = new Item();

						it.applicationName = ass.getApplicationsName();
						it.metricName = fmMgr.getMetricById(ass.getMetr_ID()).getName();

						input.add(it);
						buf.append(it.applicationName);
						buf.append("-"); //$NON-NLS-1$
						buf.append(it.metricName);
						listeAssoc.add(buf.toString());
						mapAssoc.put(buf.toString(), ass.getId());
					}

				}

			}
		};

		IProgressService service = PlatformUI.getWorkbench().getProgressService();
		try {
			service.run(true, false, r);
			String[] assoc = new String[listeAssoc.size()];
			for(int i = 0; i < listeAssoc.size(); i++) {
				assoc[i] = listeAssoc.get(i);

			}

			viewer.setItems(assoc);

			int index = 0;
			int idNode = ((KPIActivity) node.getWorkflowObject()).getAssocid();
			String assocString = ""; //$NON-NLS-1$
			for(String string : mapAssoc.keySet()) {
				if(idNode == mapAssoc.get(string)) {
					assocString = string;
				}
			}
			for(String s : viewer.getItems()) {

				if(s.equalsIgnoreCase(assocString)) {
					viewer.select(index);
					break;
				}
				index++;
			}

			String[] comp = new String[3];
			comp[0] = "<"; //$NON-NLS-1$
			comp[1] = ">"; //$NON-NLS-1$
			comp[2] = "="; //$NON-NLS-1$

			select.setItems(comp);
			String selectS = ((KPIActivity) node.getWorkflowObject()).getComparator();
			int index2 = 0;
			for(String s : select.getItems()) {

				if(s.equalsIgnoreCase(selectS)) {
					select.select(index2);
					break;
				}
				index2++;
			}

		} catch(InvocationTargetException e) {
			// Operation was canceled
		} catch(InterruptedException e) {
			// Handle the wrapped exception
		}
	}

	@Override
	public void aboutToBeShown() {

		if(!viewer.isListening(SWT.Selection)) {
			viewer.addSelectionListener(selection);
		}
		if(!select.isListening(SWT.Selection)) {
			select.addSelectionListener(selection);
		}
		super.aboutToBeShown();

	}

	@Override
	public void aboutToBeHidden() {

		viewer.removeSelectionListener(selection);
		select.removeSelectionListener(selection);
		super.aboutToBeHidden();
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();

	}

	private SelectionListener selection = new SelectionListener() {

		public void widgetDefaultSelected(SelectionEvent e) {}

		public void widgetSelected(SelectionEvent e) {

			((KPIActivity) node.getWorkflowObject()).setAssocid(mapAssoc.get(viewer.getText()).toString());
			((KPIActivity) node.getWorkflowObject()).setComparator(select.getText());

		}

	};

	private class Item {
		private String applicationName;
		private String metricName;
	}
}
