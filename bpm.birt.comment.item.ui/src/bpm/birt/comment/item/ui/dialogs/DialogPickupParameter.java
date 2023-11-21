package bpm.birt.comment.item.ui.dialogs;

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

public class DialogPickupParameter extends Dialog {

	private ComboViewer combo;
	private Text txtDefaultValue, txtPrompt;

	private List<String> parameters;
	
	private String selectedParameter;
	private String selectedDefaultValue;
	private String selectedPrompt;

	public DialogPickupParameter(Shell parentShell, List<String> parameters) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.parameters = parameters;
	}

	@Override
	protected void initializeBounds() {
		super.initializeBounds();
		getShell().setText("Select a parameter");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));

		combo = new ComboViewer(main, SWT.READ_ONLY);
		combo.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		combo.setContentProvider(new IStructuredContentProvider() {

			@Override
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<String> l = (List<String>) inputElement;
				return l.toArray(new String[l.size()]);
			}

			@Override
			public void dispose() { }

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }

		});
		combo.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return (String) element;
			}

		});
		combo.setInput(parameters);
		
		Label lblPrompt = new Label(main, SWT.NONE);
		lblPrompt.setText("Prompt");
		lblPrompt.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));

		txtPrompt = new Text(main, SWT.BORDER);
		txtPrompt.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		Label lblDefaultValue = new Label(main, SWT.NONE);
		lblDefaultValue.setText("Default value");
		lblDefaultValue.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));

		txtDefaultValue = new Text(main, SWT.BORDER);
		txtDefaultValue.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		return main;
	}
	
	@Override
	protected void okPressed() {
		selectedParameter = (String) ((IStructuredSelection) combo.getSelection()).getFirstElement();
		selectedDefaultValue = txtDefaultValue.getText();
		selectedPrompt = txtPrompt.getText();
		super.okPressed();
	}

	public String getSelectedParameter() {
		return selectedParameter;
	}

	public String getSelectedDefaultValue() {
		return selectedDefaultValue;
	}
	
	public String getSelectedPrompt() {
		return selectedPrompt;
	}
}
