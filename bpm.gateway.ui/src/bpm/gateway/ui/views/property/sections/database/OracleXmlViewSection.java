package bpm.gateway.ui.views.property.sections.database;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.server.database.OracleXmlViewHelper;
import bpm.gateway.core.transformations.inputs.OracleXmlView;
import bpm.gateway.core.transformations.utils.DefinitionXSD;
import bpm.gateway.ui.dialogs.utils.xml.DialogOracleViewDefinition;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.tools.dialogs.DialogPickupConstant;

public class OracleXmlViewSection extends AbstractPropertySection {

	private Node node;

	private Composite xsdComp;

	private Text txtDefinition;
	private Button btnXSDVariable, btnXSDDefinition;
	private ComboViewer cbXPathsViewer;
	private CCombo cbXPaths;

	public OracleXmlViewSection() {
	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		Composite composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(new GridLayout(2, false));

		Composite optionComp = getWidgetFactory().createComposite(composite);
		optionComp.setLayout(new GridLayout());
		optionComp.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		xsdComp = getWidgetFactory().createComposite(optionComp);
		xsdComp.setLayout(new GridLayout(4, false));
		xsdComp.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		Label lblXSD = getWidgetFactory().createLabel(xsdComp, Messages.OracleXmlViewSection_0);
		lblXSD.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));

		txtDefinition = getWidgetFactory().createText(xsdComp, ""); //$NON-NLS-1$
		txtDefinition.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		btnXSDVariable = getWidgetFactory().createButton(xsdComp, Messages.FileGeneralSection_2, SWT.PUSH);
		btnXSDVariable.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		btnXSDVariable.addSelectionListener(variableAdapter);

		Button btnLoadPaths = getWidgetFactory().createButton(xsdComp, Messages.OracleXmlViewSection_1, SWT.PUSH);
		btnLoadPaths.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		btnLoadPaths.addSelectionListener(loadPath);

		Label lblXPath = getWidgetFactory().createLabel(xsdComp, Messages.OracleXmlViewSection_2);
		lblXPath.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		cbXPaths = getWidgetFactory().createCCombo(xsdComp);
		cbXPaths.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		cbXPaths.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Object selection = ((IStructuredSelection) cbXPathsViewer.getSelection()).getFirstElement();

				if (selection instanceof DefinitionXSD) {
					DefinitionXSD definition = (DefinitionXSD) selection;
					definition.setType(DefinitionXSD.ITERABLE);
					((OracleXmlView) node.getGatewayModel()).setRootElement(definition);
					try {
						OracleXmlViewHelper.getRootXSD((OracleXmlView) node.getGatewayModel());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		cbXPathsViewer = new ComboViewer(cbXPaths);
		cbXPathsViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof DefinitionXSD) {
					return ((DefinitionXSD) element).getElementPath();
				}
				return ""; //$NON-NLS-1$
			}
		});
		cbXPathsViewer.setContentProvider(new ArrayContentProvider());

		btnXSDDefinition = getWidgetFactory().createButton(xsdComp, Messages.OracleXmlViewSection_4, SWT.PUSH);
		btnXSDDefinition.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 4, 1));
		btnXSDDefinition.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogOracleViewDefinition dial = new DialogOracleViewDefinition(getPart().getSite().getShell(), (OracleXmlView) node.getGatewayModel());
				dial.open();
			}
		});
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();
	}

	@Override
	public void refresh() {
		OracleXmlView fileXML = (OracleXmlView) node.getGatewayModel();

		txtDefinition.removeModifyListener(listener);
		txtDefinition.setText(fileXML.getDefinition() != null ? fileXML.getDefinition() : ""); //$NON-NLS-1$
		txtDefinition.addModifyListener(listener);

		refreshCbXPath(fileXML);
	}

	private void refreshCbXPath(OracleXmlView fileXML) {
		try {
			List<DefinitionXSD> xsdDefs = OracleXmlViewHelper.getXPaths(fileXML);
			DefinitionXSD selectedDef = null;
			if (xsdDefs != null && fileXML.getRootElement() != null) {
				for (DefinitionXSD def : xsdDefs) {
					if (def.getElementPath().equals(fileXML.getRootElement().getElementPath())) {
						selectedDef = def;
						break;
					}
				}
			}

			cbXPathsViewer.setInput(xsdDefs.toArray(new DefinitionXSD[xsdDefs.size()]));

			if (selectedDef != null) {
				cbXPathsViewer.setSelection(new StructuredSelection(selectedDef));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ModifyListener listener = new ModifyListener() {

		public void modifyText(ModifyEvent evt) {
			if (evt.widget == txtDefinition) {
				((OracleXmlView) node.getGatewayModel()).setDefinition(txtDefinition.getText());
			}
		}
	};

	private SelectionAdapter variableAdapter = new SelectionAdapter() {

		@Override
		public void widgetSelected(SelectionEvent e) {
			DialogPickupConstant d = new DialogPickupConstant(getPart().getSite().getShell());
			if (d.open() == Dialog.OK) {
				if (e.getSource() == btnXSDVariable) {
					txtDefinition.setText(d.getVariable().getOuputName());

					Event ev = new Event();
					ev.widget = txtDefinition;
					listener.modifyText(new ModifyEvent(ev));
				}
			}
		}
	};

	private SelectionAdapter loadPath = new SelectionAdapter() {

		@Override
		public void widgetSelected(SelectionEvent e) {
			refreshCbXPath((OracleXmlView) node.getGatewayModel());
		}
	};
}
