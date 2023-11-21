package org.fasd.views.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.fasd.security.SecurityDim;
import org.fasd.views.composites.CompositeMember;


public class DialogMember extends Dialog {
	private SecurityDim secuDim;
	private CompositeMember container;
	private String member;
	
	public DialogMember(Shell parentShell, SecurityDim dimView) {
		super(parentShell);
		secuDim = dimView;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		container = new CompositeMember(parent, SWT.NONE, secuDim);
		
		return parent;
	}

	@Override
	protected void okPressed() {
		member = container.getMember();
		super.okPressed();
	}

	public String getMember(){
		return member;
	}
	
	

}
