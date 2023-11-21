package bpm.birep.admin.client.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.part.ViewPart;

import adminbirep.Activator;

public class BottomToolBarView extends ViewPart {

	public static final String ID = "bpm.birep.admin.client.views.BottomToolBarView"; //$NON-NLS-1$

	@Override
	public void createPartControl(Composite parent) {	
		
		parent.setLayout(new GridLayout(1, true));
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
    	final List<Button> items = new ArrayList<Button>();
    	IPerspectiveDescriptor lastPerspective = null;
    	for(IPerspectiveDescriptor pd : Activator.getDefault().getWorkbench().getPerspectiveRegistry().getPerspectives()){
    		
    		// skip this Perspective we want it be the last button of the toolbar
    		if (pd.getId().equals("MainPerspective")){ //$NON-NLS-1$
    			lastPerspective = pd;
    			continue;
    		}
    		
    		final IPerspectiveDescriptor _pd = pd;
    		final Button i = new Button(parent, SWT.TOGGLE);
    		i.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false));
    		items.add(i);
    		
    		i.setImage(ImageDescriptor.createFromImageData(pd.getImageDescriptor().getImageData().scaledTo(16, 16)).createImage());
    		i.setToolTipText(pd.getLabel());
    		i.addSelectionListener(new SelectionAdapter(){
    			@Override
    			public void widgetSelected(SelectionEvent e) {
    				
    				for(Button it : items){
    					if (i != it){
    						it.setSelection(false);
    					}
    				}
    				Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(_pd);
        		}
    		});
    		
    		
    	    		
    	}
    	
    	/*
    	 * add the Menu As Last Perspective Button
    	 */
    	if (lastPerspective != null){
    		final Button i = new Button(parent, SWT.TOGGLE);
    		final IPerspectiveDescriptor _pd = lastPerspective;
    		items.add(i);
    		i.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
    		
    		i.setImage(ImageDescriptor.createFromImageData(lastPerspective.getImageDescriptor().getImageData().scaledTo(16, 16)).createImage());
    		i.setToolTipText(lastPerspective.getLabel());
    		i.addSelectionListener(new SelectionAdapter(){
    			@Override
    			public void widgetSelected(SelectionEvent e) {
    				
    				for(Button it : items){
    					if (i != it){
    						it.setSelection(false);
    					}
    				}
    				Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(_pd);
        		}
    		});
    	}
	}

	@Override
	public void setFocus() {
		
		
	}
	
	
	

}
