package bpm.gateway.ui.views.property.sections.vanilla;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
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

import bpm.gateway.core.transformations.vanilla.VanillaCommentExtraction;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.tools.dialogs.DialogRepositoryObject;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class VanillaCommentExtractionSection extends AbstractPropertySection {

	private RepositoryItem item;
	
	private Text directoryItem;
	private Button browser;

	private VanillaCommentExtraction transfo;
	
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        Assert.isTrue(((Node)((NodePart) input).getModel()).getGatewayModel() instanceof VanillaCommentExtraction);
        this.transfo = (VanillaCommentExtraction)(((Node)((NodePart) input).getModel()).getGatewayModel());
	}
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {

		super.createControls(parent, aTabbedPropertySheetPage);
		
		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		
		//Select a repository item
		Label l = getWidgetFactory().createLabel(composite, Messages.VanillaCommentExtractionSection_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		directoryItem = getWidgetFactory().createText(composite, "", SWT.BORDER); //$NON-NLS-1$
		directoryItem.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		directoryItem.setEnabled(false);
		
		browser = getWidgetFactory().createButton(composite, "...", SWT.PUSH); //$NON-NLS-1$
		browser.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		browser.setEnabled(false);
		browser.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogRepositoryObject dial = new DialogRepositoryObject(getPart().getSite().getShell(), -2);
				
				if (dial.open() == Dialog.OK){
					item = dial.getRepositoryItem();
					directoryItem.setText(item.getItemName());
					transfo.setItemId(item.getId());
				}
			}
			
		});
	}

	@Override
	public void refresh() {
		browser.setEnabled(Activator.getDefault().getRepositoryContext() != null);
	}
	
}
