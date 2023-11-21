package metadata.client.model.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.metadata.layer.logical.ICalculatedElement;
import bpm.metadata.layer.logical.IDataStreamElement;

public class DialogDateSplit extends Dialog {

	private IDataStreamElement dateColumn;
	private Composite composite;
	private Button annee;
	private Button trimestre;
	private Button mois;
	private Button semaine;
	private Button jour;
	private Button heure;
	private Button minute;
	private Button seconde;
	
	

	public DialogDateSplit(Shell parentShell, IDataStreamElement dateColumn) {
		super(parentShell);
		this.dateColumn = dateColumn;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		annee = new Button(composite, SWT.CHECK);
		annee.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		annee.setText("Année");
		
		mois = new Button(composite, SWT.CHECK);
		mois.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		mois.setText("Mois");
		
		jour = new Button(composite, SWT.CHECK);
		jour.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		jour.setText("Jour");
		
		heure = new Button(composite, SWT.CHECK);
		heure.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		heure.setText("Heure");
		
		minute = new Button(composite, SWT.CHECK);
		minute.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		minute.setText("Minute");
		
		seconde = new Button(composite, SWT.CHECK);
		seconde.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		seconde.setText("Seconde");
		
		return parent;
	}


	@Override
	protected void okPressed() {
		
		if(annee.getSelection()) {
			dateColumn.getDataStream().addCalculatedElement(createPart("Year"));
		}
//		if(trimestre.getSelection()) {
//			dateColumn.getDataStream().addCalculatedElement(createPart("Quarter"));
//		}
		if(mois.getSelection()) {
			dateColumn.getDataStream().addCalculatedElement(createPart("Month"));
		}
//		if(semaine.getSelection()) {
//			dateColumn.getDataStream().addCalculatedElement(createPart("Week"));
//		}
		if(jour.getSelection()) {
			dateColumn.getDataStream().addCalculatedElement(createPart("Day"));
		}
		if(heure.getSelection()) {
			dateColumn.getDataStream().addCalculatedElement(createPart("Hour"));
		}
		if(minute.getSelection()) {
			dateColumn.getDataStream().addCalculatedElement(createPart("Minute"));
		}
		if(seconde.getSelection()) {
			dateColumn.getDataStream().addCalculatedElement(createPart("Second"));
		}
		
		super.okPressed();
	}

	private ICalculatedElement createPart(String datePart) {
		ICalculatedElement element = new ICalculatedElement("Extract(" + datePart + " From " + "`" + dateColumn.getDataStream().getName() + "`" + "." + dateColumn.getOrigin().getShortName() + ")");
		element.setName(datePart);
		element.setClassType(ICalculatedElement.STRING);
		
		return element;
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(800, 600);
		getShell().setText("Split date");
	}
}
