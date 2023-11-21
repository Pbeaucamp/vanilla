package org.fasd.views.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPHierarchy;

public class DialogAddHiera extends Dialog {

	private Text name, allMember, desc;
	private OLAPHierarchy hiera;
	private String dimName = ""; //$NON-NLS-1$

	@Override
	protected void okPressed() {
		hiera = new OLAPHierarchy();
		hiera.setName(name.getText());
		hiera.setAllMember(allMember.getText());
		hiera.setDesc(desc.getText());
		super.okPressed();
	}

	public DialogAddHiera(Shell parentShell, String dimName) {
		super(parentShell);
		this.dimName = dimName;
	}

	@Override
	protected Control createDialogArea(final Composite container) {
		container.setLayout(new GridLayout());
		Composite parent = new Composite(container, SWT.NONE);
		parent.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		parent.setLayout(new GridLayout(2, false));

		Label lb1 = new Label(parent, SWT.NONE);
		lb1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lb1.setText(LanguageText.DialogAddHiera_Name_);

		name = new Text(parent, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		name.setText(LanguageText.DialogAddHiera_Default);
		name.selectAll();

		Label l2 = new Label(parent, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(LanguageText.DialogAddHiera_Desc_);

		desc = new Text(parent, SWT.BORDER);
		desc.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		l2 = new Label(parent, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(LanguageText.DialogAddHiera_0);

		allMember = new Text(parent, SWT.BORDER);
		allMember.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		allMember.setText(LanguageText.DialogAddHiera_All_ + dimName);

		return parent;
	}

	public OLAPHierarchy getHiera() {
		return hiera;
	}

	@Override
	protected void initializeBounds() {
		this.getShell().setText(LanguageText.DialogAddHiera_New_Hierar);
		super.initializeBounds();
		this.getShell().setSize(400, 200);
	}

}
