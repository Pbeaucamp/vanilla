package bpm.vanilla.server.ui.wizard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class VanillaGroupsWizardPage extends WizardPage{

	private CheckboxTableViewer viewer;
	
	
	protected VanillaGroupsWizardPage(String pageName) {
		super(pageName);
		setDescription("Select Groups");
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText("Available Groups");
		
		viewer = CheckboxTableViewer.newCheckList(main, SWT.BORDER| SWT.FULL_SELECTION);
		viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			public void dispose() {
				
				
			}
			
			public Object[] getElements(Object inputElement) {
				Collection c = (Collection)inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});
		viewer.setLabelProvider(new LabelProvider());
		viewer.addCheckStateListener(new ICheckStateListener() {
			
			public void checkStateChanged(CheckStateChangedEvent event) {
				getContainer().updateButtons();
				
			}
		});
		
		setControl(main);
		
	}

	
	
	
	public void fillContent(Collection<String> groups){
		viewer.setInput(groups);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		return  viewer.getCheckedElements().length > 0;
	}
	
	
	public List<String> getGroups(){
		List<String> l = new ArrayList<String>();
		
		for(Object o : viewer.getCheckedElements()){
			l.add((String)o);
		}
		return l;
	}

	
	
}
