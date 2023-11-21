package bpm.oda.driver.reader.wizards.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.nebula.widgets.pshelf.PShelf;
import org.eclipse.nebula.widgets.pshelf.PShelfItem;
import org.eclipse.nebula.widgets.pshelf.RedmondShelfRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.oda.driver.reader.Activator;
import bpm.oda.driver.reader.icons.IconsName;

public class DialogSave extends TitleAreaDialog{
	
	public static final String[] TAB_FORMAT = {"Excel", "CSV", "XML"};
	public static final String[] TAB_EXTENSION = {".xls", ".csv", ".xml"};
	
	public static final int INDEX_EXCEL = 0;
	public static final int INDEX_CSV = 1;
	public static final int INDEX_XML = 2;
	
	private Text txtName;
	private Combo comboFormat;
	private Label labelPath;
	private Button btnSave;
	private String pathSelected;
	
	private Shell currentShell;

	public DialogSave(Shell parentShell) {
		super(parentShell);
		currentShell = parentShell;
		
	}

	@Override
	protected Control createContents(Composite parent) {
		
		Control contents = super.createContents(parent);
		
		this.setTitle("Save preview results");
		this.setMessage("To save current results, select a format, a name, and a destination.");
		this.getShell().setText("Vanilla Reader");
		
		return contents;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new GridLayout(1,false));
		
		PShelf shelf = new PShelf(composite, SWT.SMOOTH | SWT.BORDER);
		shelf.setLayoutData(new GridData(GridData.FILL, GridData.FILL,true,true,1,1));
		shelf.setRenderer(new RedmondShelfRenderer());
		
		PShelfItem item = new PShelfItem(shelf, SWT.NONE);
		item.setImage(Activator.getDefault().getImageRegistry().get(IconsName.ICO_SAVE));
		item.setText("Save options");
		
		item.getBody().setLayout(new GridLayout(2, false));
		
	//Format
		Label l = new Label(item.getBody(), SWT.NONE);
		l.setText("Format:"	);
		l.setLayoutData(new GridData(GridData.FILL, GridData.FILL,false,false,1,1));
		
		comboFormat = new Combo(item.getBody(), SWT.READ_ONLY);
		comboFormat.setItems(TAB_FORMAT);
		comboFormat.setLayoutData(new GridData(GridData.FILL, GridData.FILL,true,false,1,1));
		
		l = new Label(item.getBody(), SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.FILL,true,false,2,1));
		
		
	//Name
		l = new Label(item.getBody(), SWT.NONE);
		l.setText("Name:");
		l.setLayoutData(new GridData(GridData.FILL, GridData.FILL,false,false,1,1));
		
		txtName = new Text(item.getBody(), SWT.BORDER | SWT.SINGLE);
		txtName.setLayoutData(new GridData(GridData.FILL, GridData.FILL,true,false,1,1));
		
		l = new Label(item.getBody(), SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.FILL,true,false,2,1));
		
		
	//Destination
		l = new Label(item.getBody(), SWT.NONE);
		l.setText("Destination:");
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER,false,false,1,1));
		
		btnSave = new Button(item.getBody(), SWT.PUSH);
		btnSave.setLayoutData(new GridData(GridData.FILL, GridData.FILL,false,false,1,1));
		btnSave.setText("Save in...");
		
		btnSave.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				
				FileDialog dialog = new FileDialog(currentShell, SWT.SAVE);
				
				if(txtName.getText().length() == 0 && comboFormat.getSelection() == null){
					MessageDialog.openError(currentShell, "Vanilla Reader Error", "To save the file, enter a name, and select a format.");
				}
				else{
					String fileName = txtName.getText();
					String fileFormat = TAB_FORMAT[comboFormat.getSelectionIndex()];
					fileFormat = getExtension(fileFormat);
					
					dialog.setFileName(fileName + fileFormat);
					labelPath.setText(dialog.open());
					pathSelected = labelPath.getText();
				}
				
				
			}
			
		});
		
		l = new Label(item.getBody(), SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.FILL,true,false,2,1));
		
		
		l = new Label(item.getBody(), SWT.NONE);
		l.setText("Selected Path:");
		l.setLayoutData(new GridData(GridData.FILL, GridData.FILL,false,false,1,1));
		
		labelPath = new Label(item.getBody(), SWT.NONE);
		labelPath.setLayoutData(new GridData(GridData.FILL, GridData.FILL,true,false,1,1));
		
		l = new Label(item.getBody(), SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.FILL,true,false,2,1));
		
		
		return composite;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		
		this.createButton(parent, IDialogConstants.OK_ID,
				IDialogConstants.OK_LABEL, true);
		
		this.createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, true);
	}
	
	private String getExtension(String format){
		
		if(format.equals(TAB_FORMAT[INDEX_EXCEL])){
			return TAB_EXTENSION[INDEX_EXCEL];
		}
		
		else if(format.equals(TAB_FORMAT[INDEX_CSV])){
			return TAB_EXTENSION[INDEX_CSV];
		}
		
		else{
			return TAB_EXTENSION[INDEX_XML];
		}
	}
	
	public String getPathSaved(){
		
		if(pathSelected.length() == 0){
			MessageDialog.openError(currentShell, "Vanilla Reader Error", "Error on saving file.");
			return null;
		}
		
		else{
			return pathSelected;
		}
	}

	
	
}
