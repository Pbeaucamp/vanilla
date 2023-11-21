package bpm.profiling.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.profiling.database.bean.AnalysisInfoBean;
import bpm.profiling.ui.composite.CompositeQuery;

public class DialogQueryInfo extends Dialog {

	private CompositeQuery composite ;
	private AnalysisInfoBean bean;
	
	public DialogQueryInfo(Shell parentShell) {
		super(parentShell);
		
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		composite = new CompositeQuery(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		return composite;
	}

	@Override
	protected void okPressed() {
		
		bean = composite.getQueryInfo();
		
		super.okPressed();
	}

	public AnalysisInfoBean getBean(){
		return bean;
	}
}
