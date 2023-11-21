package bpm.birep.admin.client.disco;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import adminbirep.Activator;
import adminbirep.Messages;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.disco.DiscoReportConfiguration;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.components.VanillaParameterComponent;
import bpm.vanilla.platform.core.components.report.ReportRuntimeConfig;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class DialogReportConfiguration extends Dialog{

	private static final String[] FORMATS = {"HTML", "PDF"}; //$NON-NLS-1$ //$NON-NLS-2$
	private static final String[] FORMATS_VIEW = {"HTML"}; //$NON-NLS-1$
	
	private Composite composite ;
	private ListViewer listFormats;
	
	private RepositoryItem item;
	private DiscoReportConfiguration selectedConfig;
	
	private List<String> selectedFormats;
	private List<VanillaGroupParameter> groupParams;
	
	private HashMap<VanillaGroupParameter, List<Viewer>> cascadingControls = new HashMap<VanillaGroupParameter, List<Viewer>>(); 
	
	private VanillaParameterComponent paramComponent;
	
	private ReportRuntimeConfig runtimeConfig;
	
	
	public DialogReportConfiguration(Shell parentShell, int repositoryId, int groupId, RepositoryItem item) {
		super(parentShell);
		this.item = item;

		ObjectIdentifier ident = new ObjectIdentifier(repositoryId, item.getId());
		
		runtimeConfig = new ReportRuntimeConfig();
		runtimeConfig.setObjectIdentifier(ident);
		runtimeConfig.setVanillaGroupId(groupId);
		runtimeConfig.setLocale(null);
		runtimeConfig.setAlternateDataSourceConfiguration(null);
		runtimeConfig.setParameters(null);
		runtimeConfig.setOutputFormat(null);
		
		paramComponent = Activator.getDefault().getRemoteParameter();

		try {
			groupParams = paramComponent.getParameters(runtimeConfig);
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), Messages.DialogReportConfiguration_3, Messages.DialogReportConfiguration_4 + e.getMessage());
		}
	}
	
	public DialogReportConfiguration(Shell parentShell, int repositoryId, Integer groupId, RepositoryItem item, DiscoReportConfiguration selectedConfig) {
		this(parentShell, repositoryId, groupId, item);
		this.selectedConfig = selectedConfig;
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 480);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		composite.setLayout(new GridLayout());

		buildFormat(composite);
		
		if(selectedConfig != null){
			loadConfig(selectedConfig);
		}
		else {
			if(groupParams != null){
				buildParameters(composite, groupParams);
			}
		}
		
		return composite;
	}
	
	@Override
	protected void okPressed() {
		if(getSelectedFormats() != null && !getSelectedFormats().isEmpty()){
			
			if(getSelectedParameters() != null && !getSelectedParameters().isEmpty()){
				boolean allParamOk = true;
				for(VanillaGroupParameter gr : getSelectedParameters()){
					if(gr.getParameters() != null){
						for(VanillaParameter param : gr.getParameters()){
							if(param.isRequired() && (param.getSelectedValues() == null || param.getSelectedValues().isEmpty())){
								allParamOk = false;
								break;
							}
						}
						
						if(!allParamOk) {
							break;
						}
					}
				}
				
				if(allParamOk){
					super.okPressed();
				}
				else {
					MessageDialog.openError(getShell(), Messages.DialogReportConfiguration_5, Messages.DialogReportConfiguration_6);
				}
			}
			else {
				super.okPressed();
			}
		}
		else {
			MessageDialog.openError(getShell(), Messages.DialogReportConfiguration_7, Messages.DialogReportConfiguration_8);
		}
	}
	
	private void loadConfig(DiscoReportConfiguration selectedConfig) {
		List<String> selections = new ArrayList<String>();
		if(selectedConfig.getSelectedFormats() != null){
			for(int i=0; i<FORMATS.length; i++){
				for(String selectedFormat : selectedConfig.getSelectedFormats()){
					if(selectedFormat.equals(FORMATS[i])){
						selections.add(FORMATS[i]);
					}
				}
			}
		}
		listFormats.setSelection(new StructuredSelection(selections));
		
		groupParams = selectedConfig.getSelectedParameters();
		if(groupParams != null){
			buildParameters(composite, groupParams);
		}
	}

	private void buildFormat(Composite parent){
		org.eclipse.swt.widgets.Group groupFormat = new org.eclipse.swt.widgets.Group(parent, SWT.NONE);
		groupFormat.setLayout(new GridLayout());
		groupFormat.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		groupFormat.setText(Messages.DialogReportConfiguration_9);
		
		listFormats = new ListViewer(groupFormat);
		listFormats.getList().setLayoutData(new GridData(GridData.FILL, SWT.BEGINNING, true, false));
		listFormats.setContentProvider(new IStructuredContentProvider() {
			
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }
			
			@Override
			public void dispose() { }
			
			@Override
			public Object[] getElements(Object inputElement) {
				return (String[]) inputElement;
			}
		});
		listFormats.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return (String)element;
			}
		});
		listFormats.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				selectedFormats = new ArrayList<String>();
				
				IStructuredSelection selectionFormat = ((IStructuredSelection) listFormats.getSelection());
				if(!selectionFormat.isEmpty()){
					for(Object format : selectionFormat.toList()){
						if(format instanceof String){
							selectedFormats.add((String)format);
						}
					}
				}
			}
		});
		
		if(item.getType() == IRepositoryApi.FAV_TYPE) {
			listFormats.setInput(FORMATS_VIEW);
		}
		else {
			listFormats.setInput(FORMATS);
		}
	}
	
	private void buildParameters(Composite parent, List<VanillaGroupParameter> groupParams) {
		for (VanillaGroupParameter groupParameter : groupParams){
			
			boolean isGroupDisplay = false;
			for(VanillaParameter param : groupParameter.getParameters()){
				if(!param.isHidden()){
					isGroupDisplay = true;
				}
			}
			
			if(isGroupDisplay){
				String labelGroupParameter = null;
				if(groupParameter.getPromptText() != null && !groupParameter.getPromptText().equals("")){ //$NON-NLS-1$
					labelGroupParameter = groupParameter.getPromptText();
				}
				else if(groupParameter.getDisplayName() != null && !groupParameter.getDisplayName().equals("")){ //$NON-NLS-1$
					labelGroupParameter = groupParameter.getDisplayName();
				}
				else{
					labelGroupParameter = groupParameter.getName();
				}
				
				org.eclipse.swt.widgets.Group panelGroupParam = new org.eclipse.swt.widgets.Group(parent, SWT.NONE);
				panelGroupParam.setLayout(new GridLayout());
				panelGroupParam.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
				panelGroupParam.setText(labelGroupParameter);

				for(final VanillaParameter parameter : groupParameter.getParameters()){

					if(!parameter.isHidden()){

						Composite paramPanel = new Composite(panelGroupParam, SWT.NONE);
						paramPanel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
						paramPanel.setLayout(new GridLayout(2, false));
						
						if(parameter.getPromptText() != null && !parameter.getPromptText().isEmpty()){
							Label lblParam = new Label(paramPanel, SWT.NONE);
							lblParam.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
							lblParam.setText(parameter.getPromptText());
						}
						else if(parameter.getDisplayName() != null && !parameter.getDisplayName().isEmpty()){
							Label lblParam = new Label(paramPanel, SWT.NONE);
							lblParam.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
							lblParam.setText(parameter.getDisplayName());
						}
						else {
							Label lblParam = new Label(paramPanel, SWT.NONE);
							lblParam.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
							lblParam.setText(parameter.getName());
						}
						
						if (parameter.getControlType() == VanillaParameter.LIST_BOX) {
							createComboField(paramPanel, groupParameter, parameter);
						}
						else if(parameter.getControlType() == VanillaParameter.RADIO_BUTTON){
							createRadioButton(paramPanel, parameter);
						}
						else {
							createTextField(paramPanel, parameter);
						}

					}
				}

			}
		}
	}

	private void createComboField(Composite parent, final VanillaGroupParameter groupParam, final VanillaParameter parameter) {
		if(parameter.getParamType().equals("multi-value")) { //$NON-NLS-1$
			final ListViewer listViewer = new ListViewer(parent);
			listViewer.getList().setLayoutData(new GridData(GridData.FILL, SWT.BEGINNING, true, false));
			listViewer.setContentProvider(new IStructuredContentProvider() {
				
				@Override
				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }
				
				@Override
				public void dispose() { }
				
				@Override
				@SuppressWarnings("unchecked")
				public Object[] getElements(Object inputElement) {
					Set<String> values = (Set<String>)inputElement;
					return values.toArray(new String[values.size()]);
				}
			});
			listViewer.setLabelProvider(new LabelProvider(){
				@Override
				public String getText(Object element) {
					return (String)element;
				}
			});
			listViewer.addSelectionChangedListener(new ISelectionChangedListener() {
				
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					parameter.getSelectedValues().clear();
					
					IStructuredSelection selectionFormat = ((IStructuredSelection) listViewer.getSelection());
					if(!selectionFormat.isEmpty()){
						for(Object format : selectionFormat.toList()){
							if(format instanceof String){
								parameter.addSelectedValue((String)format);
							}
						}
					}
				}
			});
			
			if (!parameter.getValues().isEmpty()) {
				Set<String> values = parameter.getValues().keySet();
				listViewer.setInput(values);
			}
		}
		else {
			final ComboViewer comboViewer = new ComboViewer(parent);
			if(groupParam.isCascadingGroup()) {
				if(cascadingControls.get(groupParam) == null) {
					cascadingControls.put(groupParam, new ArrayList<Viewer>());
				}
				cascadingControls.get(groupParam).add(comboViewer);
			}
			comboViewer.getCombo().setLayoutData(new GridData(GridData.FILL, SWT.BEGINNING, true, false));
			comboViewer.setContentProvider(new IStructuredContentProvider() {
				
				@Override
				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }
				
				@Override
				public void dispose() { }
				
				@Override
				@SuppressWarnings("unchecked")
				public Object[] getElements(Object inputElement) {
					Set<String> values = (Set<String>)inputElement;
					return values.toArray(new String[values.size()]);
				}
			});
			comboViewer.setLabelProvider(new LabelProvider(){
				@Override
				public String getText(Object element) {
					return (String)element;
				}
			});
			comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					parameter.getSelectedValues().clear();
					
					IStructuredSelection selectionFormat = ((IStructuredSelection) comboViewer.getSelection());
					if(!selectionFormat.isEmpty()){
						for(Object format : selectionFormat.toList()){
							if(format instanceof String){
								parameter.addSelectedValue((String)format);
							}
						}
					}
					
					if(cascadingControls != null && cascadingControls.get(groupParam) != null){
						int cbindex = cascadingControls.get(groupParam).indexOf(comboViewer);
						
						try {
							VanillaParameter p = groupParam.getParameters().get(cbindex + 1);
							runtimeConfig.setParameters(groupParams);
							VanillaParameter res = paramComponent.getReportParameterValues(runtimeConfig, p.getName());
							cascadingControls.get(groupParam).get(cbindex + 1).setInput(res.getValues().keySet());
							
						} catch(Exception e) {
							
						}
					}
				}
			});
			
			if (!parameter.getValues().isEmpty()) {
				Set<String> values = parameter.getValues().keySet();
				comboViewer.setInput(values);
			}
		}

	}

	private void createTextField(Composite parent, final VanillaParameter parameter) {
		final Text textField = new Text(parent, SWT.BORDER);
		textField.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		textField.setText(parameter.getDefaultValue() != null ? parameter.getDefaultValue() : ""); //$NON-NLS-1$
		textField.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				String value = textField.getText();
				parameter.getSelectedValues().clear();
				parameter.addSelectedValue(value);
			}
		});
	}

	private void createRadioButton(Composite parent, VanillaParameter parameter) {
		if(!parameter.getValues().isEmpty()){
			for(Entry<String, String> entry : parameter.getValues().entrySet()){

				Button radioBtn = new Button(parent, SWT.RADIO);
				radioBtn.setText(entry.getKey());
			}
		}

	}
	
	public List<String> getSelectedFormats(){
		return selectedFormats;
	}
	
	public List<VanillaGroupParameter> getSelectedParameters(){
		return groupParams;
	}
}
