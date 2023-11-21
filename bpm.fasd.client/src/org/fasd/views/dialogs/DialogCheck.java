package org.fasd.views.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class DialogCheck extends Dialog {

	private List<String> log;
	private String title;

	public DialogCheck(String title, Shell parentShell, List<String> log) {
		super(parentShell);
		this.log = log;
		this.title = title;

	}

	@Override
	protected Control createDialogArea(Composite parent) {
		ListViewer viewer = new ListViewer(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.getList().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setLabelProvider(new LabelProvider());
		viewer.setContentProvider(new IStructuredContentProvider() {

			public void dispose() {

			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

			public Object[] getElements(Object inputElement) {
				return (String[]) inputElement;
			}

		});
		viewer.setInput(log.toArray(new String[log.size()]));

		return parent;
	}

	@Override
	protected void initializeBounds() {
		super.initializeBounds();
		this.getShell().setText(title);
		this.getShell().setSize(400, 300);
	}

}
