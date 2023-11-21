package bpm.profiling.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.profiling.database.bean.TagBean;
import bpm.profiling.ui.composite.CompositeTag;

public class DialogCreateTag extends Dialog {

	private CompositeTag tagComposite;
	private TagBean tagbean;
	
	
	public DialogCreateTag(Shell parentShell) {
		super(parentShell);
		
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		tagComposite = new CompositeTag(parent, SWT.NONE, false);
		tagComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		return tagComposite;
	}

	@Override
	protected void okPressed() {
		tagComposite.performChanges();
		tagbean = tagComposite.getContent();
		super.okPressed();
	}
	
	
	
	public TagBean getTag(){
		return tagbean;
	}

	@Override
	protected void initializeBounds() {
		setShellStyle(getShellStyle() | SWT.RESIZE);
		getShell().setSize(400, 300);
		getShell().setText("Tag content");
	}

}
