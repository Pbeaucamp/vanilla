package bpm.es.gedmanager.dialogs;

import java.util.Date;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import bpm.es.gedmanager.api.GedModel;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;

public class DialogChangePerempDate extends Dialog {

	private DocumentVersion version;
	private GedModel model;
	private DateTime dateTime;
	
	public DialogChangePerempDate(Shell parentShell, DocumentVersion version, GedModel model) {
		super(parentShell);
		
		
		this.version = version;
		this.model = model;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText("Change Peremption Date");
		Composite comp = new Composite(parent, SWT.NONE);
		
		comp.setLayout(new GridLayout(2, false));
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label lbl = new Label(comp, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, false));
		lbl.setText("Peremption Date");
		
		dateTime = new DateTime(comp, SWT.CALENDAR);
		dateTime.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		if(version.getPeremptionDate() != null) {
			dateTime.setDate(version.getPeremptionDate().getYear() + 1900, version.getPeremptionDate().getMonth(), version.getPeremptionDate().getDate());
		}
		
		return parent;
	}
	
	@Override
	protected void okPressed() {
		
		version.setPeremptionDate(new Date(
				dateTime.getYear() - 1900
				, dateTime.getMonth()
				, dateTime.getDay()));
		try {
			model.updateVersion(version);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.close();
	}
}
