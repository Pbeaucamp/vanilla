package bpm.forms.design.ui.composite;

import java.util.Collection;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import bpm.forms.core.design.IForm;
import bpm.forms.core.design.IInstanciationRule.Mode;
import bpm.forms.core.design.IInstanciationRule.ScheduledType;
import bpm.forms.design.ui.Activator;
import bpm.forms.design.ui.Messages;
import bpm.vanilla.platform.core.beans.Group;

public class CompositeInstanciationRule {

	private Button allowManual;
	private Button onlyOneInstanceByIp;
	private Button scheduleRule;
	private ICheckStateListener checkListener = new ICheckStateListener() {
		
		@Override
		public void checkStateChanged(CheckStateChangedEvent event) {
			Group g = (Group)event.getElement();
			
			if (event.getChecked()){
				form.getInstanciationRule().addGroupId(g.getId());
			}
			else{
				form.getInstanciationRule().removeGroupId(g.getId());
			}
			getClient().notifyListeners(SWT.Modify, new Event());
		}
	};
	
	private Button day, week, month;
	private CheckboxTreeViewer groups;
	private Text cronExpression;
	
	private Text expirationDate;
	
	private Composite client;
	
	private FormToolkit toolkit;
	
	private IForm form;
	
	
	private SelectionAdapter radioListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			onlyOneInstanceByIp.setEnabled(!scheduleRule.getSelection());
			form.getInstanciationRule().setUniqueIp(onlyOneInstanceByIp.getSelection());
			if (scheduleRule.getSelection()){
				form.getInstanciationRule().setMode(Mode.SCHEDULED);
				day.setEnabled(true);
				month.setEnabled(true);
				week.setEnabled(true);
			}
			else{
				form.getInstanciationRule().setMode(Mode.MANUAL);
				day.setEnabled(false);
				month.setEnabled(false);
				week.setEnabled(false);
			}
			
			if (day.getSelection()){
				form.getInstanciationRule().setScheduledType(ScheduledType.DAY);
			}
			else if (week.getSelection()){
				form.getInstanciationRule().setScheduledType(ScheduledType.WEEK);
			}
			else if (month.getSelection()){
				form.getInstanciationRule().setScheduledType(ScheduledType.MONTH);
			}
			
			getClient().notifyListeners(SWT.Modify, new Event());
			
		}
	};
	
	
	
	public CompositeInstanciationRule(FormToolkit toolkit){
		this.toolkit = toolkit;
	}
	
	public Composite getClient(){
		return client;
	}
	
	public void createContent(Composite parent){
			
		if (toolkit == null){
			this.toolkit = new FormToolkit(Display.getDefault());
		}
		
		client = toolkit.createComposite(parent);
		client.setLayout(new GridLayout());
		
		
		Composite b = toolkit.createComposite(client);
		b.setLayout(new GridLayout());
		b.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		allowManual = toolkit.createButton(b, Messages.CompositeInstanciationRule_0, SWT.RADIO);
		allowManual.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		allowManual.setSelection(true);
//		allowManual.addSelectionListener(radioListener);
		
		scheduleRule = toolkit.createButton(b, Messages.CompositeInstanciationRule_1, SWT.RADIO);
		scheduleRule.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
//		scheduleRule.addSelectionListener(radioListener);
		
		onlyOneInstanceByIp = toolkit.createButton(b, Messages.CompositeInstanciationRule_2, SWT.CHECK);
		onlyOneInstanceByIp.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		
		/*
		 * create Scheduling Composite
		 */
		Composite sched = toolkit.createComposite(client);
		sched.setLayout(new GridLayout(2, false));
		sched.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		day = toolkit.createButton(sched, Messages.CompositeInstanciationRule_3, SWT.RADIO);
		day.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		
		week = toolkit.createButton(sched, Messages.CompositeInstanciationRule_4, SWT.RADIO);
		week.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		month = toolkit.createButton(sched, Messages.CompositeInstanciationRule_5, SWT.RADIO);
		month.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		Label l = toolkit.createLabel(sched, Messages.CompositeInstanciationRule_6);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		groups = new CheckboxTreeViewer(toolkit.createTree(sched, SWT.CHECK | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL));
		groups.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		groups.getTree().setHeaderVisible(true);
		groups.getTree().setLinesVisible(true);
		groups.setContentProvider(new ITreeContentProvider() {
			
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			@Override
			public void dispose() {
				
				
			}
			
			@Override
			public Object[] getElements(Object inputElement) {
				Collection c = (Collection)inputElement;
				return c.toArray(new Object[c.size()]);
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
		groups.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Group)element).getName();
			}
		});
				
	}
	
	public void setInput(IForm form){
		/*
		 * detach listener
		 */
		allowManual.removeSelectionListener(radioListener);
		day.removeSelectionListener(radioListener);
		month.removeSelectionListener(radioListener);
		onlyOneInstanceByIp.removeSelectionListener(radioListener);
		scheduleRule.removeSelectionListener(radioListener);
		week.removeSelectionListener(radioListener);
		groups.removeCheckStateListener(checkListener);
		
		this.form = form;
		
		
		
		try{
			List<Group> l = Activator.getDefault().getVanillaContext().getVanillaApi().getVanillaSecurityManager().getGroups();
			groups.setInput(l);
			
			
			for(Group o : l){
				
					
				for(Integer i : this.form.getInstanciationRule().getGroupId()){
					if (i.intValue() == o.getId()){
						groups.setChecked(o, true);
						break;
					}
				}
					
				
			}
		
			
		}catch(Exception ex){
			ex.printStackTrace();
			MessageDialog.openError(getClient().getShell(), "Error", "Unable to load Vanilla Groups from " +  Activator.getDefault().getVanillaContext().getVanillaContext().getVanillaUrl() + ".\n" + ex.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		
		/*
		 * fill
		 */
		if (form.getInstanciationRule().getMode() == Mode.MANUAL){
			allowManual.setSelection(true);
			scheduleRule.setSelection(false);
			onlyOneInstanceByIp.setEnabled(true);
			onlyOneInstanceByIp.setSelection(form.getInstanciationRule().isUniqueIp());
			
			day.setEnabled(false);
			week.setEnabled(false);
			month.setEnabled(false);
		}
		else{
			allowManual.setSelection(false);
			scheduleRule.setSelection(true);
			onlyOneInstanceByIp.setEnabled(false);
			day.setEnabled(true);
			week.setEnabled(true);
			month.setEnabled(true);
		}
		
		
		
		/*
		 * attach listener
		 */
		allowManual.addSelectionListener(radioListener);
		day.addSelectionListener(radioListener);
		month.addSelectionListener(radioListener);
		onlyOneInstanceByIp.addSelectionListener(radioListener);
		scheduleRule.addSelectionListener(radioListener);
		week.addSelectionListener(radioListener);
		groups.addCheckStateListener(checkListener);
	}
	
	public IForm getInput() {
		return form;
	}
}
