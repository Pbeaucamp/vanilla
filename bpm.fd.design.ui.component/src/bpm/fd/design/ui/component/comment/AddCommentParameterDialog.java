package bpm.fd.design.ui.component.comment;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.fd.api.core.model.components.definition.report.ReportComponentParameter;

public class AddCommentParameterDialog extends Dialog {

	private Text txtName;
	
	private ReportComponentParameter parameter;
	
	protected AddCommentParameterDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	@Override
	protected void initializeBounds() {
		super.initializeBounds();
		getShell().setText("Add comment parameter");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblPrompt = new Label(main, SWT.NONE);
		lblPrompt.setText("Name");
		lblPrompt.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));

		txtName = new Text(main, SWT.BORDER);
		txtName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		return main;
	}
	
	@Override
	protected void okPressed() {
		parameter = new ReportComponentParameter(txtName.getText(), 0);
		super.okPressed();
	}
	
	public ReportComponentParameter getParameter() {
		return parameter;
	}
}
