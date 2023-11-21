package metadata.client.wizards.big;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import metadataclient.Activator;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.vanilla.platform.core.components.gateway.GatewayModelGeneration4Fmdt;

public class ConfigurationPage extends WizardPage{
	private static String[] outputTypes = {"CSV", "XLS", "XML"};
	
	private CheckboxTableViewer businessTables;
	private ComboViewer modelViewer, packageViewer;
	
	private Text transformationName, generatedFile;
	private Combo outputType;
	private ComboViewer charSetOutput;
	
	protected ConfigurationPage(String pageName) {
		super(pageName);
	}

	@Override
	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, true));
		buildContent(main);
		setControl(main);
		
	}
	
	private void buildContent(Composite parent){
		/*
		 * composite selection
		 */
		Composite selection = new Composite(parent, SWT.NONE);
		selection.setLayout(new GridLayout(2, false));
		selection.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		
		Label l = new Label(selection, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText("Business Model");
		
		modelViewer = new ComboViewer(selection, SWT.READ_ONLY);
		modelViewer.setContentProvider(new ArrayContentProvider());
		modelViewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((IBusinessModel)element).getName();
		}});
		modelViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		modelViewer.setInput(Activator.getDefault().getCurrentModel().getBusinessModels());
		modelViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty()){
					return;
				}
				IBusinessModel model = (IBusinessModel)((IStructuredSelection)event.getSelection()).getFirstElement();
				packageViewer.setInput(model.getBusinessPackages("none"));
				
			}
		});
		
		
		l = new Label(selection, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText("Business Package");
		
		packageViewer = new ComboViewer(selection, SWT.READ_ONLY);
		packageViewer.setContentProvider(new ArrayContentProvider());
		packageViewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((IBusinessPackage)element).getName();
		}});
		packageViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		packageViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty()){
					return;
				}
				IBusinessPackage p = (IBusinessPackage)((IStructuredSelection)event.getSelection()).getFirstElement();
				businessTables.setInput(p.getBusinessTables("none"));
				
			}
		});
		
		
		
		l = new Label(selection, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false, 2, 1));
		l.setText("Select BusinessTables");
		
		businessTables = CheckboxTableViewer.newCheckList(selection, SWT.BORDER | SWT.V_SCROLL);
		businessTables.setContentProvider(new ArrayContentProvider());
		businessTables.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				
				return ((IBusinessTable)element).getName();
			}
		});
	
		businessTables.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
		businessTables.addCheckStateListener(new ICheckStateListener() {
			
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				getContainer().updateButtons();
				
			}
		});
	
	
		
		
		/*
		 * composite settings
		 */
		Composite output = new Composite(parent, SWT.NONE);
		output.setLayout(new GridLayout(3, false));
		output.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		
		l = new Label(output, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText("Transformation Model Name");
		
		transformationName = new Text(output, SWT.BORDER);
		transformationName.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		transformationName.setText("Gateway Fmdt Extraction");
		
		l = new Label(output, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText("Extraction Output Type");
		
		outputType = new Combo(output, SWT.READ_ONLY);
		outputType.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		outputType.setItems(outputTypes);
		outputType.select(0);
		
		l = new Label(output, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText("File Encoding");
		
		charSetOutput = new ComboViewer(output, SWT.READ_ONLY);
		charSetOutput.getControl().setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		charSetOutput.setContentProvider(new ArrayContentProvider());
		charSetOutput.setLabelProvider(new LabelProvider());
		charSetOutput.setInput(Charset.availableCharsets().values());
		charSetOutput.setSelection(new StructuredSelection(Charset.forName("UTF-8")));
		
		
		l = new Label(output, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText("Gateway Model File");
		
		generatedFile = new Text(output, SWT.BORDER);
		generatedFile.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		generatedFile.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();
				
			}
		});
		
		Button b = new Button(output, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false));
		b.setText("...");
		b.setToolTipText("Select a destination file");
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog d = new FileDialog(getShell(), SWT.SAVE);
				d.setFilterExtensions(new String[]{"*.gateway"});
				String s = d.open();
				if (s != null){
					generatedFile.setText(s);
				}
				getContainer().updateButtons();
			}
		});
	}

	
	@Override
	public boolean isPageComplete() {
		
		if (businessTables.getCheckedElements().length <= 0){
			setErrorMessage("Select the Business Tables.");
			return false;
		}
		if (generatedFile.getText().isEmpty()){
			setErrorMessage("Define the file for the generated Gateway transformation.");
			return false;
		}
		setErrorMessage(null);
		return true;
	}
	
	public GatewayModelGeneration4Fmdt createConfig(){
		List<String> tableNames = new ArrayList<String>();
		for(Object o : businessTables.getCheckedElements()){
			tableNames.add(((IBusinessTable)o).getName());
		}
		
		
		GatewayModelGeneration4Fmdt conf = new GatewayModelGeneration4Fmdt(
				transformationName.getText(), 
				Activator.getDefault().getRepositoryContext().getRepository().getId(), 
				Activator.getDefault().getCurrentModelDirectoryItemId(), 
				modelViewer.getCombo().getText(), 
				packageViewer.getCombo().getText(), 
				tableNames, 
				Activator.getDefault().getRepositoryContext().getGroup().getId(),
				outputType.getText(),
				charSetOutput.getCombo().getText());
		
		return conf;
	}
	
	public File getFile(){
		return new File(generatedFile.getText());
	}
}
