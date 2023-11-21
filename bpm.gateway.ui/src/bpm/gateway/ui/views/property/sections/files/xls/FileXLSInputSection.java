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

import bpm.gateway.core.transformations.inputs.FileInputXLS;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class FileXLSInputSection extends AbstractPropertySection {

	private Node node;
	private Button skipFirstRow;
	
	
	public FileXLSInputSection() {
		
	}

	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		
		Composite composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(new GridLayout(2, false));
		
			
		skipFirstRow = getWidgetFactory().createButton(composite, Messages.FileXLSInputSection_0, SWT.CHECK);
		skipFirstRow.setToolTipText(Messages.FileXLSInputSection_1);
		
		skipFirstRow.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		skipFirstRow.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				((FileInputXLS)node.getGatewayModel()).setSkipFirstRow(skipFirstRow.getSelection());
				

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
		skipFirstRow.setSelection(((FileInputXLS)node.getGatewayModel()).getSkipFirstRow());
		

	}
	
	
	
}
