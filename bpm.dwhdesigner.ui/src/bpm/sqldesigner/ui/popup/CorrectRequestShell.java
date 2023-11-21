package bpm.sqldesigner.ui.popup;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.sqldesigner.ui.i18N.Messages;

public class CorrectRequestShell {

	private CanValidate cmd;
	private Shell shell;
	private Text text;
	private String request;
	private Label labelError;

	public CorrectRequestShell(Display parent, int style, CanValidate listener,
			String request, String error) {
		shell = new Shell(parent, style);

		shell.setLayout(new GridLayout(1, false));
		shell.setLayoutData(new GridData(GridData.BEGINNING,
				GridData.BEGINNING, true, false, 1, 1));
		this.request = request;
		initPopup(listener, error);
	}

	public void initPopup(CanValidate listener, String error) {
		cmd = listener;

		Composite compoMain = new Composite(shell, SWT.NONE);
		compoMain.setLayout(new GridLayout(2, false));
		compoMain.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING,
				true, false, 1, 1));

		Label label = new Label(compoMain, SWT.NONE);
		label.setText(Messages.CorrectRequestShell_0);

		text = new Text(compoMain, SWT.BORDER | SWT.H_SCROLL);
		text.setText(request);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.CR)
					cmd.validate();
			}
			public void keyReleased(KeyEvent e) {
			}

		});

		Composite btnCompo = new Composite(compoMain, SWT.PUSH);
		btnCompo.setLayout(new GridLayout(2, false));
		GridData gd2 = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		gd2.horizontalSpan = 2;
		btnCompo.setLayoutData(gd2);

		Button button = new Button(btnCompo, SWT.PUSH);
		button.setText(Messages.CorrectRequestShell_1);
		button.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				cmd.validate();
			}
		});

		Button buttonCancel = new Button(btnCompo, SWT.PUSH);
		buttonCancel.setText(Messages.CorrectRequestShell_2);
		buttonCancel.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});

		 labelError = new Label(compoMain, SWT.NONE);
		 GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		 gd.horizontalSpan=2;
		 gd.grabExcessHorizontalSpace=false;
		 labelError.setLayoutData(gd);
		
		 labelError.setText(error);

	}

	public Shell getShell() {
		return shell;
	}

	public Text getText() {
		return text;
	}

}
