package metadata.client.model.dialog;

import java.util.ArrayList;
import java.util.List;

import metadata.client.i18n.Messages;
import metadata.client.model.composites.CompositeTableBrowser;
import metadataclient.Activator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.metadata.resource.ListOfValue;

/**
 * thos class provide a table for browse data
 * @author LCA
 *
 */
public class DialogColumn extends Dialog {

	private List value;
	
	
	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 400);
		getShell().setText(Messages.DialogColumn_0); //$NON-NLS-1$
	}

	private CompositeTableBrowser composite;
	private ListOfValue lov;
	
	public DialogColumn(Shell parentShell, ListOfValue lov) {
		super(parentShell);
		this.lov = lov;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		List<List<String>> model = new ArrayList<List<String>>();
		List<String> colNames = new ArrayList<String>();
		
		colNames.add(lov.getName());
		
		
		try {
			for(String s : lov.getValues()){
				ArrayList<String> l = new ArrayList<String>();
				l.add(s);
				model.add(l);
			}
			
			composite = new CompositeTableBrowser(parent, SWT.NONE, colNames, model);
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		} catch (Exception e) {
			Activator.getLogger().error(Messages.DialogColumn_1, e); //$NON-NLS-1$
			e.printStackTrace();
		}
		
		
		
		return parent;
	}

	public List<String> getValue(){
		return value;
	}
	
	@Override
	protected void okPressed() {
		composite.setData();
		value = composite.getData(); 
//		if (l != null && l.size() != 0){
//			value = l;
//		}
		
		super.okPressed();
	}

}
