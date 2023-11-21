package bpm.gateway.ui.views.property.sections.nosql;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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

import bpm.gateway.core.transformations.nosql.SqoopTransformation;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class SqoopDefinitionSection extends AbstractPropertySection {
	private SqoopTransformation sqoopTransfo;
	
	private Text txtSqoopUrl;
	private Text txtHdfsDirectory;
	
	private Button btnImport;
	private Button btnExport;
	
	public SqoopDefinitionSection() {
	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		Composite composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(new GridLayout(2, false));

		Label l = getWidgetFactory().createLabel(composite, Messages.SqoopDefinitionSection_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		txtSqoopUrl = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		txtSqoopUrl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		txtSqoopUrl.addModifyListener(listener);

		Label l3 = getWidgetFactory().createLabel(composite, Messages.SqoopDefinitionSection_1);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		txtHdfsDirectory = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		txtHdfsDirectory.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		txtHdfsDirectory.addModifyListener(listener);

		btnImport = getWidgetFactory().createButton(composite, Messages.SqoopDefinitionSection_2, SWT.RADIO);
		btnImport.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		btnImport.setSelection(true);
		btnImport.addSelectionListener(importAdapter);

		btnExport = getWidgetFactory().createButton(composite, Messages.SqoopDefinitionSection_3, SWT.RADIO);
		btnExport.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		btnExport.addSelectionListener(exportAdapter);
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.sqoopTransfo = (SqoopTransformation) ((Node) ((NodePart) input).getModel()).getGatewayModel();
	}

	@Override
	public void refresh() {
		txtSqoopUrl.removeModifyListener(listener);
		txtSqoopUrl.setText(sqoopTransfo.getSqoopUrl() != null ? sqoopTransfo.getSqoopUrl() : ""); //$NON-NLS-1$
		txtSqoopUrl.addModifyListener(listener);

		txtHdfsDirectory.removeModifyListener(listener);
		txtHdfsDirectory.setText(sqoopTransfo.getHdfsDirectory() != null ? sqoopTransfo.getHdfsDirectory() : ""); //$NON-NLS-1$
		txtHdfsDirectory.addModifyListener(listener);

		btnImport.removeSelectionListener(importAdapter);
		btnImport.setSelection(sqoopTransfo.isImport());
		btnImport.addSelectionListener(importAdapter);

		btnExport.removeSelectionListener(exportAdapter);
		btnExport.setSelection(!sqoopTransfo.isImport());
		btnExport.addSelectionListener(exportAdapter);
	}

	private ModifyListener listener = new ModifyListener() {

		public void modifyText(ModifyEvent evt) {
			if (evt.widget == txtSqoopUrl) {
				sqoopTransfo.setSqoopUrl(txtSqoopUrl.getText());
			}
			else if (evt.widget == txtHdfsDirectory) {
				sqoopTransfo.setHdfsDirectory(txtHdfsDirectory.getText());
			}
		}
	};
	
	private SelectionAdapter importAdapter = new SelectionAdapter() {

		@Override
		public void widgetSelected(SelectionEvent e) {
			sqoopTransfo.setImport(true);
		}
	};
	
	private SelectionAdapter exportAdapter = new SelectionAdapter() {

		@Override
		public void widgetSelected(SelectionEvent e) {
			sqoopTransfo.setImport(false);
		}
	};
}
