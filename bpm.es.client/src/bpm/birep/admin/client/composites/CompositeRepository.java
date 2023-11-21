package bpm.birep.admin.client.composites;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import adminbirep.Messages;
import bpm.birep.admin.client.views.ViewRepositoryDefinition;
import bpm.vanilla.platform.core.beans.Repository;

public class CompositeRepository extends Composite {

	private Button update;
	private Text name, societe, urlServlet, dbKey;
	private ViewRepositoryDefinition view;

	private Repository repository = new Repository();

	public CompositeRepository(Composite parent, int style) {
		super(parent, style);
		buildContent();

	}

	public CompositeRepository(Composite parent, int style, Repository repository) {
		super(parent, style);
		this.repository = repository;
		buildContent();
		fillDatas();
		view = (ViewRepositoryDefinition) adminbirep.Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewRepositoryDefinition.ID);
	}

	private void fillDatas() {
		if (repository == null) {
			return;
		}
		name.setText(repository.getName());
		societe.setText(repository.getSociete());
		urlServlet.setText(repository.getUrl());
		dbKey.setText(repository.getKey());
	}

	private void buildContent() {

		this.setLayout(new GridLayout(2, false));

		Label l1 = new Label(this, SWT.NONE);
		l1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l1.setText(Messages.CompositeRepository_0);

		name = new Text(this, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Label l2 = new Label(this, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l2.setText(Messages.CompositeRepository_1);

		societe = new Text(this, SWT.BORDER);
		societe.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Label l3 = new Label(this, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l3.setText(Messages.CompositeRepository_2);

		urlServlet = new Text(this, SWT.BORDER);
		urlServlet.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Label l4 = new Label(this, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l4.setText(Messages.CompositeRepository_3);

		dbKey = new Text(this, SWT.BORDER);
		dbKey.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Composite c = new Composite(this, SWT.NONE);
		c.setLayout(new GridLayout(2, true));
		c.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		update = new Button(c, SWT.PUSH);
		update.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		update.setText(Messages.CompositeRepository_4);
		update.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				repository.setName(name.getText());
				repository.setSociete(societe.getText());
				repository.setUrl(urlServlet.getText());
				repository.setKey(dbKey.getText());
				try {
					adminbirep.Activator.getDefault().getVanillaApi().getVanillaRepositoryManager().updateRepository(repository);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				try {
					view.createInput();
					view.refresh();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
	}
}
