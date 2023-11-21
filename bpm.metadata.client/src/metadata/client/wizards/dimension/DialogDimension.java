package metadata.client.wizards.dimension;

import metadata.client.i18n.Messages;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.metadata.layer.logical.sql.SQLDataSource;
import bpm.metadata.resource.complex.FmdtDimension;

public class DialogDimension extends Dialog{
	

	private CompositeDimensionBuilder dimensionComposite;
	private FmdtDimension dimension;
	
	public DialogDimension(Shell parentShell, FmdtDimension dimension) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.dimension = dimension;
	}
	
	public DialogDimension(Shell parentShell, SQLDataSource dataSource) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		
		this.dimension = new FmdtDimension();
		this.dimension.setDataSource(dataSource);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		dimensionComposite = new CompositeDimensionBuilder(parent, SWT.NONE);
		dimensionComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		dimensionComposite.setInput(dimension);
		
		return dimensionComposite;
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogDimension_0);
		getShell().setSize(800, 600);
	}

	public FmdtDimension getDimension() {
		return dimension;
	}
	
}
