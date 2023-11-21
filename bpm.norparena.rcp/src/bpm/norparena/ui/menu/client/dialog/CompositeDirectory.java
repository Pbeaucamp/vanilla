package bpm.norparena.ui.menu.client.dialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.norparena.ui.menu.Messages;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;

public class CompositeDirectory extends Composite {

	private RepositoryDirectory directory;
	private Text name, comment, creator, creation;
	private int typeV = -1;

	public CompositeDirectory(Composite parent, int style, RepositoryDirectory di, int type) {
		super(parent, style);
		directory = di;
		this.typeV = type;
		;

		this.setLayout(new GridLayout(2, false));

		Label l1 = new Label(this, SWT.NONE);
		l1.setLayoutData(new GridData());
		l1.setText(Messages.CompositeDirectory_0);

		name = new Text(this, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Label l4 = new Label(this, SWT.NONE);
		l4.setLayoutData(new GridData());
		l4.setText(Messages.CompositeDirectory_1);

		creation = new Text(this, SWT.BORDER);
		creation.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		creation.setEnabled(false);

		Label l5 = new Label(this, SWT.NONE);
		l5.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 4));
		l5.setText(Messages.CompositeDirectory_2);

		comment = new Text(this, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		comment.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 4));
	}

	public void fillData() {
		name.setText(directory.getName());
		comment.setText(directory.getComment());

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); //$NON-NLS-1$
		creation.setText(sdf.format(directory.getDateCreation()));
	}

	public void setDirectory() {
		directory.setName(name.getText());
		directory.setComment(comment.getText());
		directory.setDateCreation(directory.getDateCreation() != null ? directory.getDateCreation() : Calendar.getInstance().getTime());
	}

}
