package bpm.fa.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.fa.api.olap.OLAPResult;
import bpm.fa.ui.Messages;
import bpm.fa.ui.composite.CompositeChart;
import bpm.fa.ui.composite.viewers.OlapResultViewer;

public class DialogDrillTrhought extends Dialog{
	
	private OlapResultViewer resultViewer;
	private CompositeChart chartComposite;
	private OLAPResult result;
	private CTabFolder folder;
	
	public DialogDrillTrhought(Shell parentShell, OLAPResult result) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.result = result;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		
		folder = new CTabFolder(parent, SWT.BOTTOM);
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));
		folder.setLayout(new GridLayout(2, false));
		
		
		

		resultViewer = new OlapResultViewer();
		Control c = resultViewer.createControl(folder);
		c.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
		chartComposite = new CompositeChart();
		Control c2 = chartComposite.createComposite(folder);
		
		CTabItem datas = new CTabItem(folder, SWT.NONE);
		datas.setText(Messages.DialogDrillTrhought_0);
		datas.setControl(c);
		
		CTabItem chart = new CTabItem(folder, SWT.NONE);
		chart.setControl(c2);
		chart.setText(Messages.DialogDrillTrhought_1);
		return folder;
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogDrillTrhought_2);
		getShell().setSize(800, 600);
		resultViewer.setInput(result);
		chartComposite.setOlapResult(result);
	}
}
