package bpm.workflow.ui.composites;

import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.workflow.ui.Messages;
import bpm.workflow.ui.editors.WorkflowEditorInput;
import bpm.workflow.ui.wizards.WorkflowInformationPage;

/**
 * Composite for the visualization of the fields of a form and create variables
 * for them
 * 
 * @author CHARBONNIER, MARTIN
 * 
 */
public class CompositeWorkflowInformations extends Composite {
	private Text name, fileName, description, author;
	private Button b;

	private WorkflowInformationPage pageParent;

	public CompositeWorkflowInformations(WorkflowInformationPage pageParent, Composite parentComposite, int style, WorkflowEditorInput input) {
		super(parentComposite, style);
		this.pageParent = pageParent;
		
		setLayout(new GridLayout());
		setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		createContent();
		
		fill(input);
	}

	private void createContent() {
		Composite main = new Composite(this, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.WorkflowInformationPage_1);

		name = new Text(main, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		name.addModifyListener(listener);

		Label l2 = new Label(main, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true, 1, 2));
		l2.setText(Messages.WorkflowInformationPage_2);

		description = new Text(main, SWT.BORDER);
		description.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 2));

		Label l3 = new Label(main, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText(Messages.WorkflowInformationPage_3);

		author = new Text(main, SWT.BORDER);
		author.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Composite fileBar = new Composite(main, SWT.NONE);
		fileBar.setLayout(new GridLayout(3, false));
		fileBar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Label l4 = new Label(fileBar, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l4.setText(Messages.WorkflowInformationPage_4);

		fileName = new Text(fileBar, SWT.BORDER);
		fileName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		fileName.addModifyListener(listener);

		b = new Button(fileBar, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		b.setText("..."); //$NON-NLS-1$
		b.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getShell());
				fd.setFilterExtensions(new String[] { "*.biw", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$

				String path = fd.open();

				if (fd != null) {
					fileName.setText(path);
					if (pageParent != null) {
						pageParent.updateButtons();
					}
				}
			}
		});
	}

	public void fill(WorkflowEditorInput input) {
		if (input != null) {
			fileName.setEnabled(false);
			b.setEnabled(false);

			name.setText(input.getWorkflowModel().getName());
			description.setText(input.getWorkflowModel().getDescription());
			author.setText(""); //$NON-NLS-1$
			fileName.setText(input.getName());
		}

	}

	public boolean isComplete() {
		return !(name.getText().trim().equals("") || fileName.getText().equals("")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public Properties getProperties() {
		Properties p = new Properties();
		p.setProperty("name", name.getText()); //$NON-NLS-1$
		p.setProperty("description", description.getText()); //$NON-NLS-1$
		p.setProperty("author", author.getText()); //$NON-NLS-1$
		p.setProperty("fileName", fileName.getText()); //$NON-NLS-1$

		return p;
	}

	private ModifyListener listener = new ModifyListener() {

		public void modifyText(ModifyEvent e) {
			if (pageParent != null) {
				pageParent.updateButtons();
			}
		}
	};
}
