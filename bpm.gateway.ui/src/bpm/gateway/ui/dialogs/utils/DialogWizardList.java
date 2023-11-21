package bpm.gateway.ui.dialogs.utils;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.wizards.ExportWizardRegistry;
import org.eclipse.ui.internal.wizards.ImportWizardRegistry;
import org.eclipse.ui.internal.wizards.NewWizardRegistry;
import org.eclipse.ui.wizards.IWizardCategory;
import org.eclipse.ui.wizards.IWizardDescriptor;

import bpm.gateway.ui.i18n.Messages;

public class DialogWizardList extends Dialog{
	
	public static final int IMPORT_WIZARD = 0;
	public static final int EXPORT_WIZARD = 1;
	public static final int NEW_WIZARD = 2;
	
	private IWizardDescriptor selected;
	private TableViewer viewer;
	private String categoryFilter;
	private int type = -1;
	
	public DialogWizardList(Shell parentShell, String categoryFilter, int type) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.categoryFilter = categoryFilter;
		this.type = type;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		main.setLayout(new GridLayout());
		
		Label l = new Label(main, SWT.NONE);
		l.setBackground(ColorConstants.white);
		l.setText(Messages.DialogWizardList_0);
		l.setLayoutData(new GridData());
		
		viewer = new TableViewer(main, SWT.BORDER);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			public void dispose() {
				
				
			}
			
			public Object[] getElements(Object inputElement) {
				return (Object[])inputElement;
			}
		});
		
		viewer.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				
				return ((IWizardDescriptor)element).getLabel();
			}

			@Override
			public Image getImage(Object element) {
				try{
					return ((IWizardDescriptor)element).getImageDescriptor().createImage();
				}catch(Exception e){
					return super.getImage(element);
				}
				
			}
			
			
			
		});
		
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				if (viewer.getSelection().isEmpty()){
					getButton(IDialogConstants.OK_ID).setEnabled(false);
				}
				else{
					getButton(IDialogConstants.OK_ID).setEnabled(true);
				}
				
			}
		});
		
		return main;
	}

	
	
	@Override
	protected Control createButtonBar(Composite parent) {
		
		Control c =  super.createButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
		return c;
	}

	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogWizardList_1);
		getShell().setSize(600, 400);
		
		fill();
	}
	
	private void fill(){
		
		IWizardCategory cat = null;
		
		switch(type){
		case EXPORT_WIZARD:
			cat = ExportWizardRegistry.getInstance().findCategory(categoryFilter);
			break;
		case IMPORT_WIZARD:
			cat = ImportWizardRegistry.getInstance().findCategory(categoryFilter);
			break;
		case NEW_WIZARD:
			cat = NewWizardRegistry.getInstance().findCategory(categoryFilter);
			
		}
		
		
		viewer.setInput(cat.getWizards());
	}
	
	public IWizardDescriptor getWizardDescriptor(){
		return selected;
	}

	@Override
	protected void okPressed() {
		
		selected = (IWizardDescriptor)((IStructuredSelection)viewer.getSelection()).getFirstElement();
		
		super.okPressed();
	}
	
	
}
