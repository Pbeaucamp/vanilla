package bpm.gateway.ui.views.property.sections.files;

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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.transformations.inputs.FileInputPDFForm;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class PDFFormInputSection extends AbstractPropertySection {

	private Text filePath, propertiesFilePath;
	private FileInputPDFForm transfo;

	private ModifyListener txtLst = new ModifyListener() {

		public void modifyText(ModifyEvent e) {
			if (e.getSource() == filePath) {
				transfo.setDefinition(filePath.getText());
			}
			else if (e.getSource() == propertiesFilePath) {
				transfo.setPropertiesFilePath(propertiesFilePath.getText());
			}
			// transfo.refreshDescriptor();
		}
	};

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		Composite composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(new GridLayout(3, false));

		Label l = getWidgetFactory().createLabel(composite, Messages.KmlGeneralSection_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		filePath = getWidgetFactory().createText(composite, "", SWT.BORDER); //$NON-NLS-1$
		filePath.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Button browse = getWidgetFactory().createButton(composite, "...", SWT.PUSH); //$NON-NLS-1$
		browse.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		browse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getPart().getSite().getShell(), SWT.OPEN);
				fd.setFilterExtensions(new String[] { "*.pdf", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$

				String s = fd.open();

				if (s != null) {
					filePath.setText(s);
				}
			}
		});

		Label lblProp = getWidgetFactory().createLabel(composite, Messages.PDFFormInputSection_0);
		lblProp.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		propertiesFilePath = getWidgetFactory().createText(composite, "", SWT.BORDER); //$NON-NLS-1$
		propertiesFilePath.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Button browseProps = getWidgetFactory().createButton(composite, "...", SWT.PUSH); //$NON-NLS-1$
		browseProps.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		browseProps.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getPart().getSite().getShell(), SWT.OPEN);
				fd.setFilterExtensions(new String[] { "*.xml", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$

				String s = fd.open();

				if (s != null) {
					propertiesFilePath.setText(s);
				}
			}
		});
	}

	@Override
	public void refresh() {
		filePath.removeModifyListener(txtLst);
		filePath.setText(transfo.getDefinition() != null ? transfo.getDefinition() : ""); //$NON-NLS-1$
		filePath.addModifyListener(txtLst);

		propertiesFilePath.removeModifyListener(txtLst);
		propertiesFilePath.setText(transfo.getPropertiesFilePath() != null ? transfo.getPropertiesFilePath() : ""); //$NON-NLS-1$
		propertiesFilePath.addModifyListener(txtLst);
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.transfo = (FileInputPDFForm) ((Node) ((NodePart) input).getModel()).getGatewayModel();
	}
}
