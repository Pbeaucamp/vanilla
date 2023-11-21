package bpm.fd.design.ui.editor.palette;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.fd.api.core.model.resources.Palette;

public class DialogPalette extends Dialog{
	
	private CompositePalette composite;
	private Palette palette;
	
	public DialogPalette(Shell parentShell,Palette palette) {
		super(parentShell);
		this.palette = palette;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		composite = new CompositePalette(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		return composite;
	}
	
	@Override
	protected void initializeBounds() {
		super.initializeBounds();
		composite.fill(palette);
	}
}
