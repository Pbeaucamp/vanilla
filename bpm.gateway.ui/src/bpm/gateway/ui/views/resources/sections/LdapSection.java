package bpm.gateway.ui.views.resources.sections;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.server.ldap.LdapConnection;
import bpm.gateway.ui.resource.composite.connection.LdapConnectionComposite;

public class LdapSection extends AbstractPropertySection {

	private LdapConnectionComposite composite;
	private LdapConnection connection;
	
	public LdapSection() {
		
	}
	
	
	
	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);
		
		Composite container = getWidgetFactory().createFlatFormComposite(parent);
		container.setLayout(new GridLayout());
		
		
		composite = new LdapConnectionComposite(container, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));


	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof LdapConnection);
        this.connection = ((LdapConnection) input);
	}

	@Override
	public void refresh() {
		composite.releaseListener();
		composite.fillDatas(connection);
		composite.attachListener();
		super.refresh();
	}



//
//	@Override
//	public void aboutToBeHidden() {
//		composite.releaseListener();
//		super.aboutToBeHidden();
//	}
//
//
//
//
//	@Override
//	public void aboutToBeShown() {
//		composite.attachListener();
//		super.aboutToBeShown();
//	}

	

}
