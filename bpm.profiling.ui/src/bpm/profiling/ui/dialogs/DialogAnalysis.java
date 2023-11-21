package bpm.profiling.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.profiling.database.bean.AnalysisInfoBean;
import bpm.profiling.ui.composite.CompositeAnalysisInfo;

public class DialogAnalysis extends Dialog {

	private CompositeAnalysisInfo info;
	private AnalysisInfoBean bean;
	
	public DialogAnalysis(Shell parentShell) {
		super(parentShell);
		
	}
	public DialogAnalysis(Shell parentShell, AnalysisInfoBean bean) {
		super(parentShell);
		this.bean = bean;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		info = new CompositeAnalysisInfo(parent, SWT.NONE, false);
		info.setLayoutData(new GridData(GridData.FILL_BOTH));
		if (bean != null){
			info.fillContent(bean);
		}
		return info;
	}

	@Override
	protected void okPressed() {
		info.performChange();
		bean = info.getAnalysisInformation();
		
		if (bean.getConnectionId() == -1){
			MessageDialog.openInformation(getShell(), "Missing informations", "Connection is not defined");
			return;
		}
		else if (bean.getName().equals("")){
			MessageDialog.openInformation(getShell(), "Missing informations", "Name is not defined");
			return;
		}
		
		super.okPressed();
	}

	public AnalysisInfoBean getInfos(){
		return bean;
	}
	
	
}
