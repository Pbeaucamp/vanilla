package bpm.birep.admin.client.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import adminbirep.Messages;

public class DialogSearch extends Dialog{

	private Text xml;
	
	private Text txtSearch;
	private Button btnNext;
	private Button btnPrevious;
	

	public DialogSearch(Shell parentShell, Text xml) {
		super(parentShell);
		this.xml = xml;
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite root = new Composite(parent, SWT.NONE);
		root.setLayout(new GridLayout(2,true));
		root.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		txtSearch = new Text(root, SWT.BORDER);
		txtSearch.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		btnPrevious = new Button(root, SWT.PUSH);
		btnPrevious.setLayoutData(new GridData(SWT.FILL, SWT. FILL, true, true));
		btnPrevious.setText(Messages.DialogSearch_0);
		btnPrevious.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Point p = xml.getSelection();
				int end = p.y;
				int previous = -1;
				int index = -1;
				while(true) {
					index = xml.getText().indexOf(txtSearch.getText(), previous);
					if(index < 0 || index + txtSearch.getText().length() >=  end) {
						break;
					}
					previous = index + txtSearch.getText().length();
				}
				if(previous > -1) {
					xml.setSelection(previous - txtSearch.getText().length(), previous);
				}
			}
		});
		
		btnNext = new Button(root, SWT.PUSH);
		btnNext.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		btnNext.setText(Messages.DialogSearch_1);
		btnNext.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Point p = xml.getSelection();
				int start = p.y;
				int index = xml.getText().indexOf(txtSearch.getText(), start);
				if(index > -1) {
					xml.setSelection(index, index + txtSearch.getText().length());
				}
			}
		});
		
		return root;
	}
	
	@Override
	protected Control createButtonBar(Composite parent) {
		return null;
	}
}
