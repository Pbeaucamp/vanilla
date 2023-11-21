package bpm.gateway.ui.views.property.sections.mdm;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.freemetrics.api.features.actions.Action;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.icons.IconsNames;
import bpm.mdm.model.Attribute;
import bpm.mdm.model.Entity;
import bpm.mdm.model.Model;
import bpm.mdm.model.api.IMdmProvider;

public class MdmDialog extends Dialog{
	

	private TreeViewer viewer;
	private IMdmProvider provider; 
	
	private Entity entity;
	
	public MdmDialog(Shell parentShell, IMdmProvider provider) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.provider = provider;
	}
	@Override
	protected Control createDialogArea(Composite parent) {
		
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		viewer = new TreeViewer(main, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				if (element instanceof Entity){
					return ((Entity)element).getName();
				}
				else if (element instanceof Attribute){
					return ((Attribute)element).getName();
				}
				
				return super.getText(element);
			}
			
			@Override
			public Image getImage(Object element) {
				if (element instanceof Entity){
					return Activator.getDefault().getImageRegistry().get(IconsNames.mdm_entity);
				}
				else if (element instanceof Attribute){
					return Activator.getDefault().getImageRegistry().get(IconsNames.mdm_attribute);
				}
				return super.getImage(element);
			}
		});
		viewer.setContentProvider(new ITreeContentProvider() {
			
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			@Override
			public void dispose() {
				
				
			}
			
			@Override
			public Object[] getElements(Object inputElement) {
				List l = ((Model)inputElement).getEntities();
				return l.toArray(new Object[l.size()]);
			}
			
			@Override
			public boolean hasChildren(Object element) {
				if (element instanceof Entity){
					return !((Entity)element).getAttributes().isEmpty();
				}
				return false;
			}
			
			@Override
			public Object getParent(Object element) {
				return ((EObject)element).eContainer();
			}
			
			@Override
			public Object[] getChildren(Object parentElement) {
				if (parentElement instanceof Entity){
					List l = ((Entity)parentElement).getAttributes();
					return l.toArray(new Object[l.size()]);
				}
				return null;
			}
		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection)event.getSelection();
				if (ss.isEmpty() || !(ss.getFirstElement() instanceof Entity)){
					entity = null;
				}
				else{
					entity = (Entity)ss.getFirstElement();
				}
				updateButtons();
				
			}
		});
		
		return main;
	}
	
	private void updateButtons(){
		getButton(IDialogConstants.OK_ID).setEnabled(entity != null);
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setSize(800, 600);
		viewer.setInput(provider.getModel());
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}
	
	@Override
	protected void okPressed() {
		super.okPressed();
	}
	
	public Entity getEntity(){
		return entity;
	}
}
