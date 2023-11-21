package bpm.gateway.ui.resource.forms.wizard;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.server.userdefined.Parameter;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.i18n.Messages;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class FormMappingPage extends WizardPage {

	private CheckboxTableViewer gtwParams, orbeonParams;
	private HashMap<Parameter, Integer> map = new HashMap<Parameter, Integer>();
	
	private Text name;
	
	protected FormMappingPage(String pageName) {
		super(pageName);
		
	}

	public void createControl(Composite parent) {
		
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout());
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// create contents
		createPageContent(mainComposite);

		// page setting
		setControl( mainComposite );
		setPageComplete(true);
		
		
		
	}

	
	private Control createPageContent(Composite parent){
		
		Composite  serverC = new Composite(parent, SWT.NONE);
		serverC.setLayout(new GridLayout(3, false));
		serverC.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
				
		gtwParams = CheckboxTableViewer.newCheckList(serverC, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		gtwParams.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		gtwParams.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<Parameter> l = (List<Parameter>)inputElement;
				return l.toArray(new Parameter[l.size()]);
			}

			public void dispose() {
				
				
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				
				
			}
			
		});
		gtwParams.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				return ((Parameter)element).getName();
			}
			
		});
		gtwParams.addCheckStateListener(new ICheckStateListener(){

			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getChecked()){
					gtwParams.setAllChecked(false);
					gtwParams.setChecked(event.getElement(), true);
				}
				
			}
			
		});
		
		Composite buttonBar = new Composite(serverC, SWT.NONE);
		buttonBar.setLayout(new GridLayout());
		buttonBar.setLayoutData(new GridData(GridData.CENTER, GridData.FILL, false, true));
		
		Button map = new Button(buttonBar, SWT.PUSH);
		map.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		map.setText(Messages.FormMappingPage_0);
		map.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (gtwParams.getCheckedElements().length == 0){
					return;
				}
				
				if (orbeonParams.getCheckedElements().length == 0){
					return;
				}
				
				bpm.vanilla.platform.core.repository.Parameter p = (bpm.vanilla.platform.core.repository.Parameter)orbeonParams.getCheckedElements()[0];
				Parameter mp = (Parameter)gtwParams.getCheckedElements()[0];
				FormMappingPage.this.map.put(mp, p.getId());
				
			}
			
		});
		
		Button unmap = new Button(buttonBar, SWT.PUSH);
		unmap.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		unmap.setText(Messages.FormMappingPage_1);
		unmap.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (gtwParams.getCheckedElements().length == 0){
					return;
				}
				
				Parameter mp = (Parameter)gtwParams.getCheckedElements()[0];
				FormMappingPage.this.map.remove(mp);
				
			}
			
		});
		
		
		orbeonParams = CheckboxTableViewer.newCheckList(serverC, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		orbeonParams.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		orbeonParams.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<bpm.vanilla.platform.core.repository.Parameter> l = (List<bpm.vanilla.platform.core.repository.Parameter>)inputElement;
				return l.toArray(new bpm.vanilla.platform.core.repository.Parameter[l.size()]);
			}

			public void dispose() {
				
				
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				
				
			}
			
		});
		orbeonParams.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				bpm.vanilla.platform.core.repository.Parameter p = (bpm.vanilla.platform.core.repository.Parameter)element;
			return p.getInstanceName() + "." + p.getName(); //$NON-NLS-1$
			}
			
		});
		orbeonParams.addCheckStateListener(new ICheckStateListener(){

			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getChecked()){
					orbeonParams.setAllChecked(false);
					orbeonParams.setChecked(event.getElement(), true);
				}
				
			}
			
		});
		return serverC;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		return true;
	}


	protected void fill(IRepositoryApi sock, RepositoryItem dirIt)throws Exception{
		
		/*
		 * fill the GTW model parameters
		 */
		DocumentGateway doc = Activator.getDefault().getCurrentInput().getDocumentGateway(); 
		gtwParams.setInput(doc.getParameters());
		
		
		/*
		 * fill the orbeon parameters for this dirIt
		 */
		orbeonParams.setInput(sock.getRepositoryService().getParameters(dirIt));
		
	}

	public HashMap<Parameter, Integer> getMap() {
		return map;
	}
	
	
}
