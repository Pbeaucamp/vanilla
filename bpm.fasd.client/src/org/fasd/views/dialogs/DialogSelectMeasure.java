package org.fasd.views.dialogs;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPMeasure;
import org.freeolap.FreemetricsPlugin;


public class DialogSelectMeasure extends Dialog {
	private OLAPMeasure mes;
	private Combo cbo;
	private final HashMap<String, OLAPMeasure> map = new HashMap<String, OLAPMeasure>();
	private List<OLAPMeasure> list;
	
	public DialogSelectMeasure(Shell parentShell, List<OLAPMeasure> list) {
		super(parentShell);
		this.list  =list;
	}
	
	protected Control createDialogArea(Composite parent) {
		parent.setLayout(new GridLayout());
		
		Composite mainComp = new Composite(parent, SWT.NONE);
		mainComp.setLayout(new GridLayout(2, true));
		mainComp.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		for(OLAPMeasure m : FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getMeasures()){
			if(list == null || (list != null && !list.contains(m))) {
				map.put(m.getName(), m);
			}
		}
		Label l = new Label(mainComp, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(LanguageText.DialogSelectMeasure_Select_Meas);
		
		cbo = new Combo(mainComp, SWT.BORDER);
		cbo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cbo.setItems(map.keySet().toArray(new String[map.size()]));	
		
		Label errorMessage = new Label(mainComp, SWT.NONE);
		errorMessage.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		errorMessage.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_RED));
		errorMessage.setText(LanguageText.DialogSelectMeasure_0);
		
		if(map.size() == 0) {
			errorMessage.setVisible(true);
			cbo.setEnabled(false);
		}
		else {
			errorMessage.setVisible(false);
			cbo.setEnabled(true);
		}
		return parent;
	}

	public OLAPMeasure getMes(){
		return mes;
	}

	@Override
	protected void okPressed() {
		mes = map.get(cbo.getText());
		super.okPressed();
	}
	
	@Override
	protected void initializeBounds() {
		this.getShell().setText(LanguageText.DialogSelectMeasure_Select_Meas);
		super.initializeBounds();
		
	}
}
