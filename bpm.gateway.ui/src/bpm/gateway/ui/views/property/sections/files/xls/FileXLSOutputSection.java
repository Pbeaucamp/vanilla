package bpm.gateway.ui.views.property.sections.files.xls;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.transformations.outputs.FileOutputXLS;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class FileXLSOutputSection extends AbstractPropertySection {

	private Node node;
	private Button append, includeHeaders;
	private Button truncate;
	
	
	public FileXLSOutputSection() {
		
	}

	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		
		Composite composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(new GridLayout(2, false));
		
			
		append = getWidgetFactory().createButton(composite, Messages.FileXLSOutputSection_0, SWT.CHECK);
		append.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		append.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				((FileOutputXLS)node.getGatewayModel()).setAppend(append.getSelection());
				
				includeHeaders.setEnabled(!append.getSelection());
			}
			
		});
		
		
		includeHeaders = getWidgetFactory().createButton(composite, Messages.FileXLSOutputSection_1, SWT.CHECK);
		includeHeaders.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		includeHeaders.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				((FileOutputXLS)node.getGatewayModel()).setIncludeHeader(includeHeaders.getSelection());
			}
			
		});
		
		truncate = getWidgetFactory().createButton(composite, Messages.FileXLSOutputSection_2, SWT.CHECK);
		truncate.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		truncate.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				((FileOutputXLS)node.getGatewayModel()).setDelete(truncate.getSelection());
			}
			
		});

	}
	
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.node = (Node)((NodePart) input).getModel();
	}
	
	@Override
	public void refresh() {
		append.setSelection(((FileOutputXLS)node.getGatewayModel()).isAppend());
		
		if (append.getSelection()){
			includeHeaders.setEnabled(false);
		}
		else{
			includeHeaders.setEnabled(true);
			includeHeaders.setSelection(((FileOutputXLS)node.getGatewayModel()).isIncludeHeader());
		}
		
		truncate.setSelection(((FileOutputXLS)node.getGatewayModel()).getDelete());


	}
	
	
	
}
