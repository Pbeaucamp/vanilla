package bpm.es.clustering.ui.composites;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import bpm.es.clustering.ui.Messages;
import bpm.vanilla.platform.core.IVanillaServerManager;
import bpm.vanilla.platform.core.beans.tasks.ServerType;
import bpm.vanilla.platform.core.components.GatewayComponent;
import bpm.vanilla.platform.core.components.ReportingComponent;

public class ServerClientDetailPage implements IDetailsPage {

	private IManagedForm managedForm;
	private Text type;
	private Text url;
	private Text state;

	private IVanillaServerManager input;

	public void createContents(Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();

		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		Section s = toolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR);
		s.setLayout(new GridLayout());
		s.setLayoutData(new GridData(GridData.FILL_BOTH));
		s.setText(Messages.ServerClientDetailPage_0);
		s.setDescription(Messages.ServerClientDetailPage_1);

		Composite client = toolkit.createComposite(s);
		client.setLayout(new GridLayout(2, false));
		client.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label l = toolkit.createLabel(client, Messages.ServerClientDetailPage_2);
		l.setLayoutData(new GridData());

		type = toolkit.createText(client, "", SWT.FLAT); //$NON-NLS-1$
		type.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		type.setEnabled(false);

		l = toolkit.createLabel(client, Messages.ServerClientDetailPage_4);
		l.setLayoutData(new GridData());

		url = toolkit.createText(client, "", SWT.FLAT); //$NON-NLS-1$
		url.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		url.setEnabled(false);

		l = toolkit.createLabel(client, Messages.ServerClientDetailPage_6);
		l.setLayoutData(new GridData());

		state = toolkit.createText(client, "", SWT.FLAT); //$NON-NLS-1$
		state.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		state.setEnabled(false);

		s.setClient(client);

		toolkit.paintBordersFor(client);

		managedForm.addPart(new SectionPart(s));
	}

	public void commit(boolean onSave) {
	}

	public void dispose() {
	}

	public void initialize(IManagedForm form) {
		managedForm = form;

	}

	public boolean isDirty() {
		return false;
	}

	public boolean isStale() {
		return false;
	}

	public void refresh() {
	}

	public void setFocus() {
	}

	public boolean setFormInput(Object input) {
		return false;
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection ss = (IStructuredSelection) selection;

		if (ss.isEmpty()) {
			input = null;
		}
		else {
			input = (IVanillaServerManager) ss.getFirstElement();
		}

		update();

	}

	private void update() {
		if (input == null) {
			type.setText(""); //$NON-NLS-1$
			url.setText(""); //$NON-NLS-1$
		}
		else {
			if (input instanceof ReportingComponent) {
				type.setText(ServerType.REPORTING.name());
				url.setText(input.getUrl());
			}
			else if (input instanceof GatewayComponent) {
				type.setText(ServerType.GATEWAY.name());
				url.setText(input.getUrl());
			}

			try {
				state.setText(input.isStarted() ? Messages.ServerClientDetailPage_10 : Messages.ServerClientDetailPage_11);
			} catch (Exception e) {
				state.setText(Messages.ServerClientDetailPage_12);
				e.printStackTrace();
			}
		}
	}

}
