package metadata.client.model.dialog;

import java.util.ArrayList;
import java.util.List;

import metadata.client.i18n.Messages;
import metadata.client.model.composites.CompositeTableBrowser;
import metadata.client.preferences.PreferenceConstants;
import metadataclient.Activator;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.metadata.layer.business.AbstractBusinessTable;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.vanilla.platform.core.IVanillaContext;

/**
 * thos class provide a table for browse data
 * @author LCA
 *
 */
public class DialogBusinessTable extends Dialog {

	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 400);
		getShell().setText(Messages.DialogBusinessTable_0); //$NON-NLS-1$
	}

	private CompositeTableBrowser composite;
	private AbstractBusinessTable table;
	
	public DialogBusinessTable(Shell parentShell, AbstractBusinessTable table) {
		super(parentShell);
		this.table = table;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		List<List<String>> model;
		List<String> colNames = new ArrayList<String>();
		
		for(IDataStreamElement e : table.getColumns("none")){ //$NON-NLS-1$
			colNames.add(e.getName());
		}
		
		try {
			IVanillaContext ctx = null;
			try{
				ctx = Activator.getDefault().getVanillaContext();
			}catch(Exception ex){
				Logger.getLogger(getClass()).warn(Messages.DialogBusinessTable_1);
			}
			
			
			model = table.executeQuery(ctx, Activator.getDefault().getPreferenceStore().getInt(PreferenceConstants.P_BROWSE_ROW_NUMBER));
			composite = new CompositeTableBrowser(parent, SWT.NONE, colNames, model);
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		} catch (Exception e) {
			Activator.getLogger().error(Messages.DialogBusinessTable_2, e); //$NON-NLS-1$
			e.printStackTrace();
			MessageDialog.openError(getShell(), Messages.DialogBusinessTable_3, e.getMessage());
			close();
		}
		
		
		
		return parent;
	}

	
}
