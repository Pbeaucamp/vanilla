package bpm.fa.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.fa.api.olap.Dimension;
import bpm.fa.api.olap.Hierarchy;
import bpm.fa.api.olap.unitedolap.UnitedOlapStructure;
import bpm.fa.ui.composite.CompositePreloadConfig;

public class DialogPreload extends Dialog{
	
	private CompositePreloadConfig composite;
	private UnitedOlapStructure structure;
	
	public DialogPreload(Shell parentShell, UnitedOlapStructure structure) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.structure = structure;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		composite = new CompositePreloadConfig(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		return composite;
	}
	
	@Override
	protected void okPressed() {
		super.okPressed();
	}

	@Override
	protected void initializeBounds() {
		List<Hierarchy> l = new ArrayList<Hierarchy>();
		
		for(Dimension d : structure.getDimensions()){
			l.addAll(d.getHierarchies());
		}
		
		composite.init(l);
		super.initializeBounds();
	}
}
