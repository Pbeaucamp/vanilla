package bpm.workflow.ui.views.property.sections.mail;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.workflow.runtime.model.IServer;
import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.model.activities.MailActivity;
import bpm.workflow.runtime.resources.servers.ListServer;
import bpm.workflow.runtime.resources.servers.Server;
import bpm.workflow.runtime.resources.servers.ServerMail;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

/**
 * Section for the selection of the mail server (Mail activity)
 * 
 * @author CHARBONNIER, MARTIN
 * 
 */
public class MailServerSection extends AbstractPropertySection {
	private Combo mailServer;
	private Node node;
	private Composite composite;

	public MailServerSection() {}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(2, false));

		CLabel labelLabel = getWidgetFactory().createCLabel(composite, Messages.MailServerSection_0);
		labelLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		mailServer = new Combo(composite, SWT.READ_ONLY);
		mailServer.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		mailServer.addSelectionListener(adaptder);

		CLabel space = getWidgetFactory().createCLabel(composite, ""); //$NON-NLS-1$
		space.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false, 2, 1));
	}

	@Override
	public void refresh() {
		List<Server> servers = ListServer.getInstance().getServers();
		List<Server> temp = new ArrayList<Server>();
		for(Server s : servers) {
			if(s.getClass() == ((IServer) node.getWorkflowObject()).getServerClass()) {
				temp.add(s);
			}
		}

		int selectTo = -1;
		String[] items = new String[temp.size()];
		int index = 0;
		for(Server s : temp) {
			items[index] = s.getName();
			if(s.getClass() == ((IServer) node.getWorkflowObject()).getServerClass() && ((IServer) node.getWorkflowObject()).getServer() != null && s.getName().equalsIgnoreCase(((IServer) node.getWorkflowObject()).getServer().getName())) {
				selectTo = index;
			}
			index++;
		}

		mailServer.setItems(items);
		mailServer.select(selectTo);
	}

	@Override
	public void aboutToBeShown() {
		super.aboutToBeShown();

	}

	@Override
	public void aboutToBeHidden() {
		super.aboutToBeHidden();
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();
	}

	SelectionAdapter adaptder = new SelectionAdapter() {

		@Override
		public void widgetSelected(SelectionEvent e) {
			if(e.widget == mailServer) {
				Server s = null;
				if(mailServer.getText() != null) {
					s = ListServer.getInstance().getServer(mailServer.getText());
				}

				if(s != null && s instanceof ServerMail) {
					((MailActivity) node.getWorkflowObject()).setServer(s);

				}

				if(s != null) {
					((WorkflowModel) Activator.getDefault().getCurrentModel()).addResource(s);
				}
			}

		}

	};

}
