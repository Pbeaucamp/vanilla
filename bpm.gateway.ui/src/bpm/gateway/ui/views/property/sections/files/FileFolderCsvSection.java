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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.transformations.files.FileFolderReader;
import bpm.gateway.core.transformations.inputs.FileInputCSV;
import bpm.gateway.core.transformations.outputs.FileOutputCSV;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.model.properties.FileCSVProperties;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class FileFolderCsvSection extends AbstractPropertySection {

	private FileFolderReader transfo;
	private Text separator;

	private Button skipFirstRow;
	
	public FileFolderCsvSection() {
		
	}

	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		
		Composite composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(new GridLayout(2, false));
		
		Label l = getWidgetFactory().createLabel(composite, Messages.FileCSVGeneralSection_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		
		separator = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		separator.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		separator.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				transfo.setCsvSeparator(separator.getText());
				
			}
		});
		
		
		

		skipFirstRow = getWidgetFactory().createButton(composite, Messages.FileCSVGeneralSection_3, SWT.CHECK);
		skipFirstRow.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		skipFirstRow.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				transfo.setSkipFirstRow(skipFirstRow.getSelection());
			}
			
		});


	}
	
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        transfo = (FileFolderReader)((Node)((NodePart) input).getModel()).getGatewayModel();
	}
	
	@Override
	public void refresh() {
		separator.setText(transfo.getCsvSeparator()); //$NON-NLS-1$
		skipFirstRow.setSelection(transfo.isSkipFirstRow());
	}
	
	
	
}
