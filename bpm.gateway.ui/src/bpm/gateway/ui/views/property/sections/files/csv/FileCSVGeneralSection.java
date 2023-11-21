package bpm.gateway.ui.views.property.sections.files.csv;

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
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.transformations.inputs.FileInputCSV;
import bpm.gateway.core.transformations.outputs.FileOutputCSV;
import bpm.gateway.core.transformations.outputs.FileOutputWeka;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.model.properties.FileCSVProperties;
import bpm.gateway.ui.gef.model.properties.FileWekaProperties;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class FileCSVGeneralSection extends AbstractPropertySection {

	private Node node;
	private Text separator;
	private Button truncate;
	private Button skipFirstRow;
	private Button checkIsJson;
	private Text txtJsonRootItem;
	private Text txtJsonDepth;

	public FileCSVGeneralSection() {
	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		Composite composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(new GridLayout(2, false));

		Label l = getWidgetFactory().createLabel(composite, Messages.FileCSVGeneralSection_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		separator = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		separator.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		separator.addModifyListener(listener);

		skipFirstRow = getWidgetFactory().createButton(composite, Messages.FileCSVGeneralSection_3, SWT.CHECK);
		skipFirstRow.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		skipFirstRow.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				((FileInputCSV) node.getGatewayModel()).setSkipFirstRow(skipFirstRow.getSelection());
			}
		});

		truncate = getWidgetFactory().createButton(composite, Messages.FileCSVGeneralSection_2, SWT.CHECK);
		truncate.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		truncate.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (node.getGatewayModel() instanceof FileOutputCSV) {
					((FileOutputCSV) node.getGatewayModel()).setDelete(truncate.getSelection());
				}
				else if (node.getGatewayModel() instanceof FileOutputWeka) {
					((FileOutputWeka) node.getGatewayModel()).setDelete(truncate.getSelection());
				}
			}
		});
		
		checkIsJson = getWidgetFactory().createButton(composite, "Is json", SWT.CHECK);
		checkIsJson.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		checkIsJson.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				((FileInputCSV) node.getGatewayModel()).setJson(checkIsJson.getSelection());
				updateUi();
			}
		});

		l = getWidgetFactory().createLabel(composite, "Json root item");
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		txtJsonRootItem = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		txtJsonRootItem.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		txtJsonRootItem.addModifyListener(listener);

		l = getWidgetFactory().createLabel(composite, "Depth");
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
				
		txtJsonDepth = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		txtJsonDepth.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		txtJsonDepth.addModifyListener(listener);
	}

	private void updateUi() {
		boolean isJson = checkIsJson.getSelection();
		separator.setEnabled(!isJson);
		skipFirstRow.setEnabled(!isJson);
		txtJsonRootItem.setEnabled(isJson);
		txtJsonDepth.setEnabled(isJson);
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();
		if (node.getGatewayModel() instanceof FileInputCSV) {
			skipFirstRow.setEnabled(true);
			truncate.setEnabled(false);
			separator.setEnabled(true);
			checkIsJson.setEnabled(true);
			txtJsonRootItem.setEnabled(true);
			txtJsonDepth.setEnabled(true);
		}
		else if (node.getGatewayModel() instanceof FileOutputCSV) {
			skipFirstRow.setEnabled(false);
			truncate.setEnabled(true);
			separator.setEnabled(true);
			checkIsJson.setEnabled(false);
			txtJsonRootItem.setEnabled(false);
			txtJsonDepth.setEnabled(false);
		}
		else if (node.getGatewayModel() instanceof FileOutputWeka) {
			skipFirstRow.setEnabled(false);
			truncate.setEnabled(true);
			separator.setEnabled(false);
			checkIsJson.setEnabled(false);
			txtJsonRootItem.setEnabled(false);
			txtJsonDepth.setEnabled(false);
		}
	}

	@Override
	public void refresh() {
		if (node.getAdapter(IPropertySource.class) instanceof FileCSVProperties) {
			FileCSVProperties properties = (FileCSVProperties) node.getAdapter(IPropertySource.class);

			separator.removeModifyListener(listener);
			separator.setText("" + properties.getPropertyValue(FileCSVProperties.PROPERTY_SEPARATOR)); //$NON-NLS-1$
			separator.addModifyListener(listener);
			if (node.getGatewayModel() instanceof FileOutputCSV) {
				truncate.setSelection(((FileOutputCSV) node.getGatewayModel()).getDelete());
			}
			else {
				FileInputCSV input = (FileInputCSV) node.getGatewayModel();
				skipFirstRow.setSelection(input.isSkipFirstRow());
				
				checkIsJson.setSelection(input.isJson());
				
				txtJsonRootItem.removeModifyListener(listener);
				txtJsonRootItem.setText(input.getJsonRootItem());
				txtJsonRootItem.addModifyListener(listener);
				
				txtJsonDepth.removeModifyListener(listener);
				txtJsonDepth.setText(input.getJsonDepth() + ""); //$NON-NLS-1$
				txtJsonDepth.addModifyListener(listener);
				
				updateUi();
			}
		}
		else if (node.getAdapter(IPropertySource.class) instanceof FileWekaProperties) {
			FileWekaProperties properties = (FileWekaProperties) node.getAdapter(IPropertySource.class);

			separator.removeModifyListener(listener);
			separator.setText("" + properties.getPropertyValue(FileWekaProperties.PROPERTY_SEPARATOR)); //$NON-NLS-1$
			separator.addModifyListener(listener);

			if (node.getGatewayModel() instanceof FileOutputWeka) {
				truncate.setSelection(((FileOutputWeka) node.getGatewayModel()).getDelete());
			}

		}
	}

	private ModifyListener listener = new ModifyListener() {

		public void modifyText(ModifyEvent evt) {
			FileCSVProperties properties = (FileCSVProperties) node.getAdapter(IPropertySource.class);

			if (evt.widget == separator) {
				properties.setPropertyValue(FileCSVProperties.PROPERTY_SEPARATOR, separator.getText());
			}
        	else if (evt.widget == txtJsonRootItem){
        		((FileInputCSV) node.getGatewayModel()).setJsonRootItem(txtJsonRootItem.getText());
        	}
        	else if (evt.widget == txtJsonDepth){
        		((FileInputCSV) node.getGatewayModel()).setJsonDepth(txtJsonDepth.getText());
        	}
		}
	};
}
