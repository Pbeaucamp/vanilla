package bpm.es.pack.manager.wizard.exp;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.es.pack.manager.I18N.Messages;

public class InfoPage extends WizardPage {

	private Text name, desc;

	protected InfoPage(String pageName) {
		super(pageName);
	}

	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new FillLayout());

		createPageContent(mainComposite);

		setControl(mainComposite);
		setPageComplete(true);
	}

	private void createPageContent(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(2, false));

		Label l = new Label(c, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, SWT.CENTER, false, false));
		l.setText(Messages.InfoPage_0);

		name = new Text(c, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		name.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();
			}
		});

		Label l2 = new Label(c, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true));
		l2.setText(Messages.InfoPage_1);

		desc = new Text(c, SWT.BORDER | SWT.WRAP);
		desc.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
	}

	@Override
	public boolean isPageComplete() {
		if (name.getText().trim().equals("")) { //$NON-NLS-1$
			return false;
		}
		return true;
	}

	public String getName() {
		return name.getText();
	}

	public String getDescription() {
		return desc.getText();
	}
}
