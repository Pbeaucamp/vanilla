package bpm.forms.design.ui.composite;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

import bpm.forms.core.design.IForm;
import bpm.forms.core.design.IFormFieldMapping;
import bpm.forms.core.runtime.IFormInstance;
import bpm.forms.core.runtime.IFormInstanceFieldState;
import bpm.forms.core.tools.InstanceAccessHelper;
import bpm.forms.design.ui.Activator;
import bpm.forms.design.ui.Messages;
import bpm.forms.design.ui.icons.IconsNames;
import bpm.forms.remote.services.RemoteValidator;
import bpm.vanilla.platform.core.beans.Group;

public class CompositeFormRuntime {

	private static Color DEPASSED = new Color(Display.getDefault(), 255, 111, 111);
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm"); //$NON-NLS-1$
	
	private Composite client;
	private TableViewer instances;
	private FormToolkit toolkit;
	
	private IForm input;
	
	private List<Group> vanillaGroups = new ArrayList<Group>();
	
	private CompositeCurrentFormInstances fieldStates;
	
	
	public CompositeFormRuntime(FormToolkit toolkit){
		this.toolkit = toolkit;
	}
	
	
	
	public Composite getClient(){
		return client;
	}
	
	public void createContent(Composite parent){
		if (toolkit == null ){
			toolkit = new FormToolkit(Display.getDefault());
		}
		
		client = toolkit.createComposite(parent);
		client.setLayout(new GridLayout());
		
		createTable(client);
		createFieldStates(client);
	}
	
	
	private void createFieldStates(Composite parent){
		
		fieldStates = new CompositeCurrentFormInstances(toolkit);
		fieldStates.createContent(parent);
		fieldStates.getClient().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		instances.addSelectionChangedListener(fieldStates);

	}
	
	private void createTable(Composite parent){
		instances = new TableViewer(toolkit.createTable(parent, SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL | SWT.MULTI));
		instances.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		instances.getTable().setLinesVisible(true);
		instances.getTable().setHeaderVisible(true);
		instances.setContentProvider(new ObservableListContentProvider());
		
		
		TableViewerColumn c = new TableViewerColumn(instances, SWT.LEFT);
		c.getColumn().setText(Messages.CompositeFormRuntime_1);
		c.getColumn().setWidth(100);
		c.setLabelProvider(new ColoredLabelProvider(){
			@Override
			public String getText(Object element) {
				try{
					return sdf.format(((IFormInstance)element).getCreationDate());
				}catch(Exception ex){
					return ""; //$NON-NLS-1$
				}
			}
		
		});
		
		c = new TableViewerColumn(instances, SWT.LEFT);
		c.getColumn().setText(Messages.CompositeFormRuntime_3);
		c.getColumn().setWidth(50);
		c.setLabelProvider(new ColoredLabelProvider(){
			@Override
			public String getText(Object element) {
				try{
					return "" + Activator.getDefault().getServiceProvider().getDefinitionService().getFormDefinition(((IFormInstance)element).getFormDefinitionId()).getVersion(); //$NON-NLS-1$
				}catch(Exception ex){
					return "unknown"; //$NON-NLS-1$
				}

			}
		});
		
		
		c = new TableViewerColumn(instances, SWT.LEFT);
		c.getColumn().setText(Messages.CompositeFormRuntime_6);
		c.getColumn().setWidth(100);
		c.setLabelProvider(new ColoredLabelProvider(){
			@Override
			public String getText(Object element) {
				
				for(Group g : vanillaGroups){
					if (g.getId() == ((IFormInstance)element).getGroupId()){
						return g.getName();
					}
				}
				
				return ((IFormInstance)element).getGroupId() + ""; //$NON-NLS-1$
			}
		});
		
		c = new TableViewerColumn(instances, SWT.LEFT);
		c.getColumn().setText(Messages.CompositeFormRuntime_8);
		c.getColumn().setWidth(100);
		c.setLabelProvider(new ColoredLabelProvider(){
			@Override
			public String getText(Object element) {
				try{
					return InstanceAccessHelper.getFormUrl((IFormInstance)element, Activator.getDefault().getVanillaContext().getVanillaContext());
				}catch(Exception ex){
					ex.printStackTrace();
					return ex.getMessage();
				}
			}
		});
		
		//only to allow copy paste
		c.setEditingSupport(new EditingSupport(instances) {
				
			private DialogCellEditor ed = new DialogCellEditor(instances.getTable()) {
				
				@Override
				protected Object openDialogBox(Control cellEditorWindow) {
					String s = (String)getValue();
					try {
						PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL(s));
					} catch (PartInitException e) {
						
						e.printStackTrace();
					} catch (MalformedURLException e) {
						
						e.printStackTrace();
					}
					return null;
				}
			};
			
			@Override
			protected void setValue(Object element, Object value) {
				instances.refresh();
				
			}
			
			@Override
			protected Object getValue(Object element) {
				try{
					
					String s = InstanceAccessHelper.getFormUrl((IFormInstance)element, Activator.getDefault().getVanillaContext().getVanillaContext());
					ed.setValue(s);
					return s;
				}catch(Exception ex){
					ex.printStackTrace();
					return ex.getMessage();
				}
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				
				return ed;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});
	
	
	
		c = new TableViewerColumn(instances, SWT.LEFT);
		c.getColumn().setText(Messages.CompositeFormRuntime_9);
		c.getColumn().setWidth(100);
		c.setLabelProvider(new ColoredLabelProvider(){
			@Override
			public String getText(Object element) {
				try{
					return InstanceAccessHelper.getValidationFormUrl((IFormInstance)element, Activator.getDefault().getVanillaContext());
				}catch(Exception ex){
					ex.printStackTrace();
					return ex.getMessage();
				}
			}
		});
		c.setEditingSupport(new EditingSupport(instances) {
			
			private DialogCellEditor ed = new DialogCellEditor(instances.getTable()) {
				
				@Override
				protected Object openDialogBox(Control cellEditorWindow) {
					String s = (String)getValue();
					try {
						PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL(s));
					} catch (PartInitException e) {
						
						e.printStackTrace();
					} catch (MalformedURLException e) {
						
						e.printStackTrace();
					}
					return null;
				}
			};
			
			@Override
			protected void setValue(Object element, Object value) {
				instances.refresh();
				
			}
			
			@Override
			protected Object getValue(Object element) {
				try{
					
					String s = InstanceAccessHelper.getValidationFormUrl((IFormInstance)element, Activator.getDefault().getVanillaContext());
					ed.setValue(s);
					return s;
				}catch(Exception ex){
					ex.printStackTrace();
					return ex.getMessage();
				}
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				
				return ed;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});
		
		c = new TableViewerColumn(instances, SWT.LEFT);
		c.getColumn().setText(Messages.CompositeFormRuntime_10);
		c.getColumn().setWidth(100);
		c.setLabelProvider(new ColoredLabelProvider(){
			@Override
			public String getText(Object element) {
				try{
					return sdf.format(((IFormInstance)element).getLastSubmitionDate());
				}catch(Exception ex){
					return ""; //$NON-NLS-1$
				}
			}
			
			@Override
			public Image getImage(Object element) {
				ImageRegistry reg = Activator.getDefault().getImageRegistry();
				if (((IFormInstance)element).isSubmited()){
					return reg.get(IconsNames.checked);
				}
				else{
					return reg.get(IconsNames.unchecked);
				}
			}
		});
		
		
		c = new TableViewerColumn(instances, SWT.LEFT);
		c.getColumn().setText(Messages.CompositeFormRuntime_12);
		c.getColumn().setWidth(100);
		c.setLabelProvider(new ColoredLabelProvider(){
			@Override
			public String getText(Object element) {
				try{
					return sdf.format(((IFormInstance)element).getLastValidationDate());
				}catch(Exception ex){
					return ""; //$NON-NLS-1$
				}
			}
			
			@Override
			public Image getImage(Object element) {
				ImageRegistry reg = Activator.getDefault().getImageRegistry();
				if (((IFormInstance)element).isValidated()){
					return reg.get(IconsNames.checked);
				}
				else{
					return reg.get(IconsNames.unchecked);
				}
			}
		});
		
		c = new TableViewerColumn(instances, SWT.LEFT);
		c.getColumn().setText(Messages.CompositeFormRuntime_14);
		c.getColumn().setWidth(100);
		c.setLabelProvider(new ColoredLabelProvider(){
			@Override
			public String getText(Object element) {
				try{
					return sdf.format(((IFormInstance)element).getExpirationDate());
				}catch(Exception ex){
					return ""; //$NON-NLS-1$
				}
			}
		});
		

		
		createMenu();
		
	}
	
	
	private void createMenu(){
		MenuManager mgr = new MenuManager();
		
		final Action deleteInstance = new Action(Messages.CompositeFormRuntime_16){
			public void run(){
				boolean errors = false;
				StringBuffer resume = new StringBuffer();
				for(Object o : ((IStructuredSelection)instances.getSelection()).toList()){
					if (o instanceof IFormInstance){
						try{
							Activator.getDefault().getServiceProvider().getInstanceService().delete((IFormInstance)o);
							resume.append(Messages.CompositeFormRuntime_17 + ((IFormInstance)o).getId() + Messages.CompositeFormRuntime_18);
						}catch(Exception ex){
							ex.printStackTrace();
							errors = true;
							resume.append(Messages.CompositeFormRuntime_19 + ((IFormInstance)o).getId() + " : " + ex.getMessage() + "\n"); //$NON-NLS-2$ //$NON-NLS-3$
						}
					}
				}
				
				if (errors){
					MessageDialog.openWarning(getClient().getShell(), Messages.CompositeFormRuntime_22, resume.toString());
				}
				else{
					MessageDialog.openInformation(getClient().getShell(), Messages.CompositeFormRuntime_23, resume.toString());
				}
			}
		};
	
		mgr.add(deleteInstance);
		
		
		
		final Action validateInstance = new Action(Messages.CompositeFormRuntime_24){
			public void run(){
				boolean errors = false;
				StringBuffer resume = new StringBuffer();
				
				RemoteValidator validator = new RemoteValidator();
				
				for(Object o : ((IStructuredSelection)instances.getSelection()).toList()){
					if (o instanceof IFormInstance){
						if (((IFormInstance)o).hasBeenValidated()){
							continue;
						}
						if (!((IFormInstance)o).hasBeenSubmited()){
							continue;
						}
						try{
							List<IFormInstanceFieldState> fieldsSate = Activator.getDefault().getServiceProvider().getInstanceService().getFieldsState(((IFormInstance)o).getId());
							Properties prop = new Properties();
							
							for(IFormFieldMapping fMap : Activator.getDefault().getServiceProvider().getDefinitionService().getFormDefinition(((IFormInstance)o).getFormDefinitionId()).getIFormFieldMappings()){

								for(IFormInstanceFieldState state : fieldsSate){
									if (fMap.getId() == state.getFormFieldMappingId()){
										prop.setProperty(fMap.getFormFieldId(), state.getValue());
									}
								}
							}
							validator.validate((IFormInstance)o, prop, Activator.getDefault().getVanillaContext());
							resume.append(Messages.CompositeFormRuntime_25 + ((IFormInstance)o).getId() + Messages.CompositeFormRuntime_26);
						}catch(Exception ex){
							ex.printStackTrace();
							errors = true;
							resume.append(Messages.CompositeFormRuntime_27 + ((IFormInstance)o).getId() + " : " + ex.getMessage() + "\n"); //$NON-NLS-2$ //$NON-NLS-3$
						}
					}
				}
				
				if (errors){
					MessageDialog.openWarning(getClient().getShell(), Messages.CompositeFormRuntime_30, resume.toString());
				}
				else{
					MessageDialog.openInformation(getClient().getShell(), Messages.CompositeFormRuntime_31, resume.toString());
				}
			}
		};
		mgr.add(validateInstance);
		
		
		mgr.addMenuListener(new IMenuListener() {
			
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				deleteInstance.setEnabled(!instances.getSelection().isEmpty());
				validateInstance.setEnabled(!instances.getSelection().isEmpty());
			}
		});
		
		instances.getTable().setMenu(mgr.createContextMenu(instances.getControl()));
	}
	
	public void setInput(IForm form){
		this.input = form;
		
		try{
			vanillaGroups = Activator.getDefault().getVanillaContext().getVanillaApi().getVanillaSecurityManager().getGroups();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		
		try{
			WritableList l = new WritableList(Activator.getDefault().getServiceProvider().getInstanceService().getRunningInstances(form), IFormInstance.class);
			instances.setInput(l);
		}catch(Exception ex){
			ex.printStackTrace();
			instances.setInput(new WritableList());
			MessageDialog.openError(client.getShell(), Messages.CompositeFormRuntime_32, Messages.CompositeFormRuntime_33 + ex.getMessage());
		}
		
	}
	
	private class ColoredLabelProvider extends ColumnLabelProvider{
		@Override
		public Color getBackground(Object element) {
							
			Date d = new Date();
			Date exp = ((IFormInstance)element).getExpirationDate();
			if (exp != null && ((IFormInstance)element).getLastSubmitionDate() == null && d.after(exp)){
				return DEPASSED;
			}
			else{
				return null;
			}
			
		}
	}
	
}
