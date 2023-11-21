package bpm.fa.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.fa.ui.Messages;
import bpm.fa.ui.composite.CompositeChartPlayer;
import bpm.fa.ui.ktable.CubeModel;

public class DialogChart extends Dialog{
	

	private CompositeChartPlayer player;
	private CubeModel cubeModel;
	public DialogChart(Shell parentShell,CubeModel cubeModel) {
		super(parentShell);
		setBlockOnOpen(false);
		this.cubeModel = cubeModel;
		setShellStyle(SWT.SHELL_TRIM | SWT.BORDER);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		player = new CompositeChartPlayer(parent, SWT.NONE);
		player.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		return player;
	}
	@Override
	protected void initializeBounds() {
		player.setInput(cubeModel);
		getShell().setSize(800, 600);
		getShell().setText(Messages.DialogChart_0);
	}
}
