package bpm.gateway.ui.views.property.sections.olap;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.gateway.core.transformations.olap.DimensionFilter;
import bpm.gateway.core.transformations.olap.OlapFactExtractorTranformation;
import bpm.gateway.core.transformations.olap.OlapHelper;
import bpm.gateway.ui.i18n.Messages;

public class DialogOlapLevelValues extends Dialog {

	private org.eclipse.swt.widgets.List values;
	private OlapFactExtractorTranformation transfo;
	private DimensionFilter filter;
	private String levelName;
	private String value;
	
	public DialogOlapLevelValues(Shell parentShell, OlapFactExtractorTranformation transfo, DimensionFilter filter, String levelName) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		
		this.transfo = transfo;
		this.filter = filter;
		this.levelName = levelName;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		values = new org.eclipse.swt.widgets.List(parent, SWT.BORDER | SWT.V_SCROLL);
		values.setLayoutData(new GridData(GridData.FILL_BOTH));
		values.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				getButton(IDialogConstants.OK_ID).setEnabled(values.getSelectionCount() != 0);
			}
			
		});
		
		return values;
	}
	
	
	private void load(){
		try{
			List<String> l = transfo.getDocument().getOlapHelper().getLevelValues(true, transfo, filter, levelName);
			values.setItems(l.toArray(new String[l.size()]));
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}

	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogOlapLevelValues_0 + levelName);
		getShell().setSize(400, 300);
		load();
	}

	public Object getValue() {
		return value;
	}

	@Override
	protected void okPressed() {
		value = values.getSelection()[0];
		super.okPressed();
	}
	
	

}
