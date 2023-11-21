package bpm.gateway.ui.views.property.sections.vanilla;

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

import bpm.gateway.core.transformations.vanilla.VanillaCreateUser;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class VanillaUserUpdateSection extends AbstractPropertySection{
	private Button update;
	private VanillaCreateUser transfo;
	private SelectionAdapter lst = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			transfo.setUpdateExisting(update.getSelection());
		}
	};
	
	public void createControls(Composite parent,TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);
		Composite main = getWidgetFactory().createComposite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		
		update = getWidgetFactory().createButton(main, Messages.VanillaUserUpdateSection_0, SWT.CHECK);
		update.setLayoutData( new GridData(GridData.FILL, GridData.CENTER, true, false));
		
	}
	
	@Override
	public void refresh() {
		update.removeSelectionListener(lst);
		update.setSelection(transfo.isUpdateExisting());
		update.addSelectionListener(lst);
	}
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.transfo = (VanillaCreateUser)((Node)((NodePart) input).getModel()).getGatewayModel();

	}
}
