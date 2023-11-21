package bpm.birep.admin.client.dialog;

import java.nio.charset.Charset;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import adminbirep.Messages;

public class DialogEncoding extends Dialog {
	
	private Charset charset;
	private String encodingName;

	public DialogEncoding(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = (Composite) super.createDialogArea(parent);

		Label l = new Label(main, SWT.NONE);
		l.setText(Messages.DialogCustom_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		final Combo encoding = new Combo(main, SWT.NONE);
		encoding.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		encoding.setItems(Charset.availableCharsets().keySet().toArray(new String[Charset.availableCharsets().keySet().size()]));
		encoding.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				encodingName = encoding.getText();
				charset = Charset.forName(encodingName);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) { }
		});

		return main;
	}

	public Charset getCharset() {
		return charset;
	}
	
	public String getEncodingName() {
		return encodingName;
	}
}
