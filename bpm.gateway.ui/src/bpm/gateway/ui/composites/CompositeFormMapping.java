package bpm.gateway.ui.composites;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.forms.Form;
import bpm.gateway.core.server.userdefined.Parameter;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.views.ModelViewPart;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class CompositeFormMapping extends Composite {
	

	private CheckboxTableViewer gtwParams, orbeonParams;
	private Form form;
	
	private Text name;
	private ISelectionChangedListener gtwLst, orbeonLst;
	
	public CompositeFormMapping(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout());
		this.setBackground(parent.getBackground());
		createContent(this);
		createListeners();
		

	}
	
	public void setInput(Form form){
		this.form = form;
		
		if (form != null){
			name.setText(form.getName());
		}
		
	}
	
	private void createContent(Composite parent){
		
		Composite  serverC = new Composite(parent, SWT.NONE);
		serverC.setLayout(new GridLayout(3, false));
		serverC.setLayoutData(new GridData(GridData.FILL_BOTH));
		serverC.setBackground(parent.getBackground());
		
		Composite  c = new Composite(serverC, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		c.setBackground(parent.getBackground());
		
		Label l = new Label(c, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.CompositeFormMapping_0);
		l.setBackground(parent.getBackground());
		
		
		name = new Text(c, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		name.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				if (form != null){
					form.setName(name.getText());
					
					ModelViewPart v =(ModelViewPart) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ModelViewPart.ID);
					if (v != null){
						v.refresh();
					}
				}
				
			}
			
		});
		
		
		Label l2 = new Label(serverC, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false, 2, 1));
		l2.setText(Messages.CompositeFormMapping_1);
		l2.setBackground(parent.getBackground());
		
		Label l3 = new Label(serverC, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText(Messages.CompositeFormMapping_2);
		l3.setBackground(parent.getBackground());
		
		
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
		buttonBar.setBackground(parent.getBackground());
		
		Button map = new Button(buttonBar, SWT.PUSH | SWT.FLAT);
		map.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		map.setText(Messages.CompositeFormMapping_3);
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
				
				form.map(mp, p.getId());
				
			}
			
		});
		
		Button unmap = new Button(buttonBar, SWT.PUSH| SWT.FLAT);
		unmap.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		unmap.setText(Messages.CompositeFormMapping_4);
		unmap.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (gtwParams.getCheckedElements().length == 0){
					return;
				}
				
				Parameter mp = (Parameter)gtwParams.getCheckedElements()[0];
				form.unmap(mp.getName());
				
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
		
	}
	
	
	private void createListeners(){
		gtwLst = new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				orbeonParams.removeSelectionChangedListener(orbeonLst);
				
				HashMap<String, String> map = form.getMappings();
				
				Parameter p = (Parameter)((IStructuredSelection)gtwParams.getSelection()).getFirstElement();
				
				
				for(String s : map.keySet()){
					if (p.getName().equals(s)){
						
						for(bpm.vanilla.platform.core.repository.Parameter _p : (List<bpm.vanilla.platform.core.repository.Parameter>)orbeonParams.getInput()){
							if (map.get(s).equals(_p.getId() + "")){ //$NON-NLS-1$
								orbeonParams.setSelection(new StructuredSelection(_p));
								break;
							}
						}
						
						
					}
				}
				
				orbeonParams.addSelectionChangedListener(orbeonLst);
				
				
				
			}
			
		};
		gtwParams.addSelectionChangedListener(gtwLst);
		
		
		
		
		
		orbeonLst = new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				gtwParams.removeSelectionChangedListener(orbeonLst);
				
				HashMap<String, String> map = form.getMappings();
				
				bpm.vanilla.platform.core.repository.Parameter p = (bpm.vanilla.platform.core.repository.Parameter)((IStructuredSelection)orbeonParams.getSelection()).getFirstElement();
				
				
				for(String s : map.keySet()){
					if (map.get(s).equals(p.getId() + "")){ //$NON-NLS-1$
						
						for(Parameter _p : (List<Parameter>)gtwParams.getInput()){
							if (s.equals(_p.getName())){
								gtwParams.setSelection(new StructuredSelection(_p));
								break;
							}else{
								gtwParams.setSelection(new StructuredSelection());
							}
						}
						
						
					}
				}
				
				gtwParams.addSelectionChangedListener(orbeonLst);
				
				
				
			}
			
		};
		orbeonParams.addSelectionChangedListener(orbeonLst);
	}
	
	public void fill(IRepositoryApi sock, RepositoryItem dirIt)throws Exception{
		
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

	public void removeListners() {
		gtwParams.removeSelectionChangedListener(gtwLst);
		orbeonParams.removeSelectionChangedListener(orbeonLst);
		
	}

	public void addListners() {
		gtwParams.addSelectionChangedListener(gtwLst);
		orbeonParams.addSelectionChangedListener(orbeonLst);
		
	}
}
