package bpm.fd.design.ui.wizard.fmdt;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import bpm.fd.api.core.model.components.definition.filter.FilterRenderer;
import bpm.fd.design.ui.Messages;
import bpm.fd.design.ui.wizard.fmdt.FmdDataSetHelper.Options;

public class DialogFmdtResourceGeneration extends Dialog{
	

	private Button generateDataSets;
	private Button generatedFilters;
	private Combo filterRenderers;
	private Options opt ;
	
	public DialogFmdtResourceGeneration(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		
		
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		l.setText(Messages.DialogFmdtResourceGeneration_0);
		
		generateDataSets = new Button(main, SWT.CHECK);
		generateDataSets.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		generateDataSets.setText(Messages.DialogFmdtResourceGeneration_1);
		generateDataSets.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				generatedFilters.setEnabled(generateDataSets.getSelection());
				filterRenderers.setEnabled(generateDataSets.getSelection());
				getButton(IDialogConstants.OK_ID).setEnabled(generateDataSets.getSelection());
			}
		});
		
		generatedFilters = new Button(main, SWT.CHECK);
		generatedFilters.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		generatedFilters.setText(Messages.DialogFmdtResourceGeneration_2);
		generatedFilters.setEnabled(false);
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.DialogFmdtResourceGeneration_3);
		
		
		filterRenderers = new Combo(main, SWT.READ_ONLY);
		filterRenderers.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		filterRenderers.setEnabled(false);
		filterRenderers.setItems(FilterRenderer.RENDERER_NAMES);
		filterRenderers.select(0);
		
		return main;
	}
	
	@Override
	protected void okPressed() {
		
		if (generateDataSets.getSelection()){
			if (generatedFilters.getSelection()){
				
				
				opt = new Options(true, filterRenderers.getSelectionIndex());
			}
			else{
				opt = new Options(false, -1);
			}
			
		}
		super.okPressed();
	}
	
	public Options getOption(){
		return opt;
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogFmdtResourceGeneration_4);
		super.initializeBounds();
	}
}
