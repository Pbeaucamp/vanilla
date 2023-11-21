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
import org.fasd.olap.OLAPDimension;
import org.freeolap.FreemetricsPlugin;


public class DialogSelectDimension extends Dialog {
	private OLAPDimension dim;
	private Combo cbo;
	private final HashMap<String, OLAPDimension> map = new HashMap<String, OLAPDimension>();
	private List<OLAPDimension> list;
	
	public DialogSelectDimension(Shell parentShell, List<OLAPDimension> list) {
		super(parentShell);
		this.list  =list;
	}
	
	protected Control createDialogArea(Composite parent) {
		parent.setLayout(new GridLayout());
		
		Composite mainComp = new Composite(parent, SWT.NONE);
		mainComp.setLayout(new GridLayout(2, true));
		mainComp.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		for(OLAPDimension d : FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getDimensions()){
			if(list == null || (list != null && !list.contains(d))) {
				map.put(d.getName(), d);
			}
		}
		Label l = new Label(mainComp, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(LanguageText.DialogSelectDimension_Select_Dim);
		
		cbo = new Combo(mainComp, SWT.BORDER);
		cbo.setSize(150, 30);
		cbo.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		cbo.setItems(map.keySet().toArray(new String[map.size()]));
		
		Label errorMessage = new Label(mainComp, SWT.NONE);
		errorMessage.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		errorMessage.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_RED));
		errorMessage.setText(LanguageText.DialogSelectDimension_0);
		
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

	public OLAPDimension getDim(){
		return dim;
	}

	@Override
	protected void okPressed() {
		dim = map.get(cbo.getText());
		super.okPressed();
	}
	
	@Override
	protected void initializeBounds() {
		this.getShell().setText(LanguageText.DialogSelectDimension_Select_Dim);
		super.initializeBounds();
		
	}
}
