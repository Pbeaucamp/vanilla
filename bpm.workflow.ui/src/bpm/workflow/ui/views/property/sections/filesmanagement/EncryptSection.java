package bpm.workflow.ui.views.property.sections.filesmanagement;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.workflow.runtime.model.IEncrypt;
import bpm.workflow.runtime.resources.variables.ListVariable;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

public class EncryptSection extends AbstractPropertySection {
	public static Color mainBrown = new Color(Display.getDefault(), 209, 177, 129);
	public static Color secondBrown = new Color(Display.getDefault(), 238, 226, 208);

	private Button checkEncrypt;

	private Combo vanillaPathsPublic;
	private Text txtPublicKeyPath;

	private Combo vanillaPathsPrivate;
	private Text txtPrivateKeyPath;

	private Text txtPassword;

	private Node node;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.setLayout(new GridLayout(2, false));

		checkEncrypt = getWidgetFactory().createButton(parent, Messages.EncryptSection_0, SWT.CHECK);
		checkEncrypt.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 3, 1));

		Label lblPublic = getWidgetFactory().createLabel(parent, Messages.EncryptSection_1);
		lblPublic.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 2));
		
		vanillaPathsPublic = new Combo(parent, SWT.READ_ONLY);
		vanillaPathsPublic.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false));
		vanillaPathsPublic.setItems(ListVariable.VANILLA_PATHS);

		txtPublicKeyPath = getWidgetFactory().createText(parent, ""); //$NON-NLS-1$
		txtPublicKeyPath.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

		Label lblPrivate = getWidgetFactory().createLabel(parent, Messages.EncryptSection_3);
		lblPrivate.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 2));
		
		vanillaPathsPrivate = new Combo(parent, SWT.READ_ONLY);
		vanillaPathsPrivate.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false));
		vanillaPathsPrivate.setItems(ListVariable.VANILLA_PATHS);

		txtPrivateKeyPath = getWidgetFactory().createText(parent, ""); //$NON-NLS-1$
		txtPrivateKeyPath.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

		getWidgetFactory().createLabel(parent, Messages.EncryptSection_5);

		txtPassword = getWidgetFactory().createText(parent, "", SWT.PASSWORD); //$NON-NLS-1$
		txtPassword.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
	}

	@Override
	public void refresh() {
		if (!txtPublicKeyPath.isDisposed()) {
			checkEncrypt.removeSelectionListener(adapterEncrypt);
			vanillaPathsPublic.removeSelectionListener(adapterPublic);
			txtPublicKeyPath.removeModifyListener(listener);
			vanillaPathsPrivate.removeSelectionListener(adapterPrivate);
			txtPrivateKeyPath.removeModifyListener(listener);
			txtPassword.removeModifyListener(listener);
		}

		IEncrypt encrypt = (IEncrypt) node.getWorkflowObject();
		checkEncrypt.setSelection(encrypt.isEncrypt());

		int selectTo = -1;
		if(encrypt.getPublicKeyPath() != null) {
			for(String s : vanillaPathsPublic.getItems()) {
				selectTo++;
				if(s.equalsIgnoreCase(encrypt.getPublicKeyPath()))
					break;
			}
		}
		vanillaPathsPublic.select(selectTo);
		txtPublicKeyPath.setText(encrypt.getPublicKeyName() != null ? encrypt.getPublicKeyName() : ""); //$NON-NLS-1$

		selectTo = -1;
		if(encrypt.getPrivateKeyPath() != null) {
			for(String s : vanillaPathsPrivate.getItems()) {
				selectTo++;
				if(s.equalsIgnoreCase(encrypt.getPrivateKeyPath()))
					break;
			}
		}
		vanillaPathsPrivate.select(selectTo);
		txtPrivateKeyPath.setText(encrypt.getPrivateKeyName() != null ? encrypt.getPrivateKeyName() : ""); //$NON-NLS-1$
		
		txtPassword.setText(encrypt.getPassword() != null ? encrypt.getPassword() : ""); //$NON-NLS-1$
		
		if (checkEncrypt.getSelection()) {
			vanillaPathsPublic.setEnabled(true);
			txtPublicKeyPath.setEnabled(true);
			
			vanillaPathsPrivate.setEnabled(false);
			txtPrivateKeyPath.setEnabled(false);
			txtPassword.setEnabled(false);
		}
		else {
			vanillaPathsPublic.setEnabled(false);
			txtPublicKeyPath.setEnabled(false);
			
			vanillaPathsPrivate.setEnabled(true);
			txtPrivateKeyPath.setEnabled(true);
			txtPassword.setEnabled(true);
		}

		if (!txtPublicKeyPath.isDisposed()) {
			if (!checkEncrypt.isListening(SWT.Selection)) {
				checkEncrypt.addSelectionListener(adapterEncrypt);
			}
			if (!vanillaPathsPublic.isListening(SWT.Selection)) {
				vanillaPathsPublic.addSelectionListener(adapterPublic);
			}
			if (!txtPublicKeyPath.isListening(SWT.Selection)) {
				txtPublicKeyPath.addModifyListener(listener);
			}
			if (!vanillaPathsPrivate.isListening(SWT.Selection)) {
				vanillaPathsPrivate.addSelectionListener(adapterPrivate);
			}
			if (!txtPrivateKeyPath.isListening(SWT.Selection)) {
				txtPrivateKeyPath.addModifyListener(listener);
			}
			if (!txtPassword.isListening(SWT.Selection)) {
				txtPassword.addModifyListener(listener);
			}
		}
	}

	@Override
	public void aboutToBeShown() {
		if (!checkEncrypt.isListening(SWT.Selection)) {
			checkEncrypt.addSelectionListener(adapterEncrypt);
		}
		if (!vanillaPathsPublic.isListening(SWT.Selection)) {
			vanillaPathsPublic.addSelectionListener(adapterPublic);
		}
		if (!txtPublicKeyPath.isListening(SWT.Selection)) {
			txtPublicKeyPath.addModifyListener(listener);
		}
		if (!vanillaPathsPrivate.isListening(SWT.Selection)) {
			vanillaPathsPrivate.addSelectionListener(adapterPrivate);
		}
		if (!txtPrivateKeyPath.isListening(SWT.Selection)) {
			txtPrivateKeyPath.addModifyListener(listener);
		}
		if (!txtPassword.isListening(SWT.Selection)) {
			txtPassword.addModifyListener(listener);
		}

		super.aboutToBeShown();

	}

	public void aboutToBeHidden() {
		checkEncrypt.removeSelectionListener(adapterEncrypt);
		vanillaPathsPublic.removeSelectionListener(adapterPublic);
		txtPublicKeyPath.removeModifyListener(listener);
		vanillaPathsPrivate.removeSelectionListener(adapterPrivate);
		txtPrivateKeyPath.removeModifyListener(listener);
		txtPassword.removeModifyListener(listener);
		super.aboutToBeHidden();
	}

	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();
	}
	
	private SelectionAdapter adapterEncrypt = new SelectionAdapter() {

		@Override
		public void widgetSelected(SelectionEvent e) {
			((IEncrypt) node.getWorkflowObject()).setEncrypt(checkEncrypt.getSelection());

			if (checkEncrypt.getSelection()) {
				vanillaPathsPublic.setEnabled(true);
				txtPublicKeyPath.setEnabled(true);
				
				vanillaPathsPrivate.setEnabled(false);
				txtPrivateKeyPath.setEnabled(false);
				txtPassword.setEnabled(false);
			}
			else {
				vanillaPathsPublic.setEnabled(false);
				txtPublicKeyPath.setEnabled(false);
				
				vanillaPathsPrivate.setEnabled(true);
				txtPrivateKeyPath.setEnabled(true);
				txtPassword.setEnabled(true);
			}
		}
	};
	
	private SelectionAdapter adapterPublic = new SelectionAdapter() {

		@Override
		public void widgetSelected(SelectionEvent e) {
			((IEncrypt) node.getWorkflowObject()).setPublicKeyPath(vanillaPathsPublic.getText());
		}
	};
	
	private SelectionAdapter adapterPrivate = new SelectionAdapter() {

		@Override
		public void widgetSelected(SelectionEvent e) {
			((IEncrypt) node.getWorkflowObject()).setPrivateKeyPath(vanillaPathsPrivate.getText());
		}
	};

	private ModifyListener listener = new ModifyListener() {

		public void modifyText(ModifyEvent evt) {
			((IEncrypt) node.getWorkflowObject()).setPublicKeyName(txtPublicKeyPath.getText());
			((IEncrypt) node.getWorkflowObject()).setPrivateKeyName(txtPrivateKeyPath.getText());
			((IEncrypt) node.getWorkflowObject()).setPassword(txtPassword.getText());
		}
	};

}
