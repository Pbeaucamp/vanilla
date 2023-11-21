package bpm.gateway.ui.views.property.sections;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.core.tsbn.rpu.RpuConnector;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class RpuConnectorSection extends AbstractPropertySection {

	private Node node;
	private RpuConnector transfo;

	private Text folderPath;
	private Button browse;

	private Text txtDateDebut;
	private Text txtDateFin;

	private Text txtFinessFilter;
	private Text txtOrderFilter;

	private ComboViewer cbServer;
	private Text definitionTxtPatients, definitionTxtActes, definitionTxtDiags;

	private ModifyListener listener = new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent e) {
			transfo.setDateDebut(txtDateDebut.getText());
			transfo.setDateFin(txtDateFin.getText());
			transfo.setFinessFilter(txtFinessFilter.getText());
			transfo.setOrderFilter(txtOrderFilter.getText());
			transfo.setSqlPatient(definitionTxtPatients.getText());
			transfo.setSqlActes(definitionTxtActes.getText());
			transfo.setSqlDiag(definitionTxtDiags.getText());
			transfo.setOutputFile(folderPath.getText());
		}
	};

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();
		transfo = (RpuConnector) node.getGatewayModel();
	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout());

		// File input regex
		Group grp = new Group(composite, SWT.NONE);
		grp.setLayout(new GridLayout());
		grp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		grp.setText(Messages.RpuConnectorSection_0);
		Composite serverComposite = new Composite(grp, SWT.NONE);
		serverComposite.setLayout(new GridLayout(2, false));
		serverComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label lbl = new Label(serverComposite, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lbl.setText(Messages.RpuConnectorSection_1);

		cbServer = new ComboViewer(serverComposite, SWT.DROP_DOWN | SWT.PUSH);
		cbServer.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		cbServer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((DataBaseServer) element).getName();
			}
		});
		cbServer.setContentProvider(new ArrayContentProvider());
		cbServer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				DataBaseServer server = (DataBaseServer) ((IStructuredSelection) event.getSelection()).getFirstElement();

				transfo.setServer(server);
			}
		});

		grp = new Group(composite, SWT.NONE);
		grp.setLayout(new GridLayout());
		grp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		grp.setText(Messages.RpuConnectorSection_2);
		Composite folderComposite = new Composite(grp, SWT.NONE);
		folderComposite.setLayout(new GridLayout(2, false));
		folderComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		folderPath = getWidgetFactory().createText(folderComposite, ""); //$NON-NLS-1$
		folderPath.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		folderPath.addModifyListener(listener);

		browse = getWidgetFactory().createButton(folderComposite, Messages.FileGeneralSection_3, SWT.PUSH);
		browse.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		browse.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dd = new DirectoryDialog(getPart().getSite().getShell());
				String varName = null;
				Variable v = null;
				String filterPath = null;
				try {
					if (folderPath.getText().startsWith("{$")) { //$NON-NLS-1$
						varName = folderPath.getText().substring(0, folderPath.getText().indexOf("}") + 1); //$NON-NLS-1$

						v = ResourceManager.getInstance().getVariableFromOutputName(varName);
						String h = v.getValueAsString();
						dd.setFilterPath(h.startsWith("/") && h.contains(":") ? h.substring(1) : h); //$NON-NLS-1$ //$NON-NLS-2$
						filterPath = dd.getFilterPath();
					}
				} catch (Exception e1) {}
				
				String path = dd.open();
				if (path != null) {
					if (varName != null) {
						path = v.getOuputName() + path.substring(filterPath.length());
					}
					folderPath.setText(path);
					transfo.setOutputFile(path);
				}
			}
		});

		grp = new Group(composite, SWT.NONE);
		grp.setLayout(new GridLayout());
		grp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		grp.setText(Messages.RpuConnectorSection_3);
		Composite sqlComposite = new Composite(grp, SWT.NONE);
		sqlComposite.setLayout(new GridLayout(2, false));
		sqlComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		lbl = new Label(sqlComposite, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lbl.setText(Messages.RpuConnectorSection_4);

		definitionTxtPatients = new Text(sqlComposite, SWT.BORDER | SWT.WRAP | SWT.MULTI | SWT.V_SCROLL);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.heightHint = 5 * definitionTxtPatients.getLineHeight();
		definitionTxtPatients.setLayoutData(gridData);
		definitionTxtPatients.setText(""); //$NON-NLS-1$

		lbl = new Label(sqlComposite, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lbl.setText(Messages.RpuConnectorSection_6);

		definitionTxtActes = new Text(sqlComposite, SWT.BORDER | SWT.WRAP | SWT.MULTI | SWT.V_SCROLL);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.heightHint = 5 * definitionTxtActes.getLineHeight();
		definitionTxtActes.setLayoutData(gridData);
		definitionTxtActes.setText(""); //$NON-NLS-1$

		lbl = new Label(sqlComposite, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lbl.setText(Messages.RpuConnectorSection_8);

		definitionTxtDiags = new Text(sqlComposite, SWT.BORDER | SWT.WRAP | SWT.MULTI | SWT.V_SCROLL);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.heightHint = 5 * definitionTxtDiags.getLineHeight();
		definitionTxtDiags.setLayoutData(gridData);
		definitionTxtDiags.setText(""); //$NON-NLS-1$

		// parameters
		grp = new Group(composite, SWT.NONE);
		grp.setLayout(new GridLayout());
		grp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		grp.setText(Messages.RpuConnectorSection_10);
		Composite paramComposite = new Composite(grp, SWT.NONE);
		paramComposite.setLayout(new GridLayout(2, false));
		paramComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		lbl = new Label(paramComposite, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lbl.setText(Messages.RpuConnectorSection_11);

		txtDateDebut = new Text(paramComposite, SWT.BORDER);
		txtDateDebut.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtDateDebut.addModifyListener(listener);

		lbl = new Label(paramComposite, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lbl.setText(Messages.RpuConnectorSection_12);

		txtDateFin = new Text(paramComposite, SWT.BORDER);
		txtDateFin.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtDateFin.addModifyListener(listener);

		lbl = new Label(paramComposite, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lbl.setText(Messages.RpuConnectorSection_13);

		txtFinessFilter = new Text(paramComposite, SWT.BORDER);
		txtFinessFilter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtFinessFilter.addModifyListener(listener);

		lbl = new Label(paramComposite, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lbl.setText(Messages.RpuConnectorSection_14);

		txtOrderFilter = new Text(paramComposite, SWT.BORDER);
		txtOrderFilter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtOrderFilter.addModifyListener(listener);
	}

	@Override
	public void refresh() {
		try {
			folderPath.removeModifyListener(listener);
			folderPath.setText(transfo.getOutputFile());
			folderPath.addModifyListener(listener);
			
			txtDateDebut.removeModifyListener(listener);
			txtDateDebut.setText(transfo.getDateDebut());
			txtDateDebut.addModifyListener(listener);

			txtDateFin.removeModifyListener(listener);
			txtDateFin.setText(transfo.getDateFin());
			txtDateFin.addModifyListener(listener);

			txtFinessFilter.removeModifyListener(listener);
			txtFinessFilter.setText(transfo.getFinessFilter());
			txtFinessFilter.addModifyListener(listener);

			txtOrderFilter.removeModifyListener(listener);
			txtOrderFilter.setText(transfo.getOrderFilter());
			txtOrderFilter.addModifyListener(listener);

			definitionTxtPatients.removeModifyListener(listener);
			definitionTxtPatients.setText(transfo.getSqlPatient());
			definitionTxtPatients.addModifyListener(listener);

			definitionTxtActes.removeModifyListener(listener);
			definitionTxtActes.setText(transfo.getSqlActes());
			definitionTxtActes.addModifyListener(listener);

			definitionTxtDiags.removeModifyListener(listener);
			definitionTxtDiags.setText(transfo.getSqlDiag());
			definitionTxtDiags.addModifyListener(listener);

			cbServer.setInput(ResourceManager.getInstance().getServers(DataBaseServer.class));
			if (transfo.getServer() != null) {
				cbServer.setSelection(new StructuredSelection(transfo.getServer()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
