package bpm.sqldesigner.ui.popup;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.sqldesigner.ui.i18N.Messages;

public class ChooseNameShell {

	private CanValidate cmd;
	private Shell shell;
	private Text text;

	public ChooseNameShell(Shell parent, int style, CanValidate command) {
		shell = new Shell(parent, style);
		initPopup(command);
	}

	public ChooseNameShell(Display parent, int style, CanValidate command) {
		shell = new Shell(parent, style);
		initPopup(command);
	}

	public void initPopup(CanValidate command) {
		cmd = command;

		shell.setLayout(new GridLayout(2, false));
		shell.setLayoutData(new GridData(GridData.BEGINNING));

		Label label = new Label(shell, SWT.NONE);
		label.setText(Messages.ChooseNameShell_0);

		text = new Text(shell, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.CR)
					cmd.validate();
			}

			public void keyReleased(KeyEvent e) {
			}

		});

		Button button = new Button(shell, SWT.PUSH);
		button.setText(Messages.ChooseNameShell_1);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		gd.horizontalSpan = 2;
		button.setLayoutData(gd);
		button.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				cmd.validate();
			}
		});

	}

	public Shell getShell() {
		return shell;
	}

	public Text getText() {
		return text;
	}

}
