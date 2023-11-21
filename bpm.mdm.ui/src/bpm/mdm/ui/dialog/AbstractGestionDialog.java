package bpm.mdm.ui.dialog;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
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

import bpm.mdm.ui.Activator;
import bpm.vanilla.platform.core.beans.Group;

public abstract class AbstractGestionDialog extends Dialog {

	protected Text txtName;
	protected Text txtSourceExterne;
	protected Text txtIdentifiantExterne;
	
	protected CheckboxTreeViewer groupTable;
	
	protected AbstractGestionDialog(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setSize(400, 300);
		super.initializeBounds();
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		
		parent.setLayout(new GridLayout(2, false));
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label lblName = new Label(parent, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblName.setText("Name");
		
		txtName = new Text(parent, SWT.BORDER);
		txtName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		Label lblSourceExterne = new Label(parent, SWT.NONE);
		lblSourceExterne.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblSourceExterne.setText("Source externe");
		
		txtSourceExterne = new Text(parent, SWT.BORDER);
		txtSourceExterne.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		Label lblIdExterne = new Label(parent, SWT.NONE);
		lblIdExterne.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblIdExterne.setText("Identifiant externe");
		
		txtIdentifiantExterne = new Text(parent, SWT.BORDER);
		txtIdentifiantExterne.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		if(this instanceof AddSupplierDialog) {
		
			groupTable = new CheckboxTreeViewer(parent, SWT.BORDER | SWT.V_SCROLL);
			groupTable.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
			groupTable.setLabelProvider(new LabelProvider() {
				@Override
				public String getText(Object element) {
					return ((Group)element).getName();
				}
			});
			groupTable.setContentProvider(new ITreeContentProvider() {
				
				@Override
				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			
				}
				
				@Override
				public void dispose() {
			
				}
				
				@Override
				public Object[] getElements(Object inputElement) {
					return ((List)inputElement).toArray();
				}
				
				@Override
				public boolean hasChildren(Object element) {
					return false;
				}
				
				@Override
				public Object getParent(Object element) {
					return null;
				}
				
				@Override
				public Object[] getChildren(Object parentElement) {
					return null;
				}
			});
			
			try {
				groupTable.setInput(Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroups());
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return parent;
	}
	
}
