package metadata.client.wizards.connection;

import metadata.client.i18n.Messages;
import metadata.client.model.composites.CompositeConnectionSql;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import bpm.metadata.layer.physical.IConnection;

public class PageSqlConnection extends WizardPage {

	private CompositeConnectionSql composite;

	protected PageSqlConnection(String pageName) {
		super(pageName);
		this.setTitle(Messages.PageSqlConnection_0);
	}

	public void createControl(Composite parent) {

		composite = new CompositeConnectionSql(parent, SWT.NONE, false);
		composite.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event event) {
				getContainer().updateButtons();

			}
		});

		setControl(composite);
	}

	@Override
	public boolean isPageComplete() {
		if (composite.getConnection() != null) {
			try {
				composite.setConnection();
			} catch (Exception ex) {

			}
		}
		return super.isPageComplete();
	}

	public IConnection getConnection() {
		try {
			composite.setConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return composite.getConnection();
	}
}
