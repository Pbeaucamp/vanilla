package metadata.client.model.dialog;

import java.util.ArrayList;
import java.util.List;

import metadata.client.i18n.Messages;
import metadata.client.model.composites.CompositeTableBrowser;
import metadata.client.preferences.PreferenceConstants;
import metadataclient.Activator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.metadata.layer.physical.IColumn;
import bpm.metadata.layer.physical.ITable;
import bpm.metadata.layer.physical.olap.UnitedOlapConnection;

/**
 * thos class provide a table for browse data
 * @author LCA
 *
 */
public class DialogPhysicalTable extends Dialog {

	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 400);
		getShell().setText(Messages.DialogPhysicalTable_0); //$NON-NLS-1$
	}

	private CompositeTableBrowser composite;
	private ITable table;
	
	public DialogPhysicalTable(Shell parentShell, ITable table) {
		super(parentShell);
		this.table = table;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		List<List<String>> model;
		List<String> colNames = new ArrayList<String>();
		
		for(IColumn e : table.getColumns()){
			colNames.add(e.getName());
		}
		
		try {
			int maxRows = Activator.getDefault().getPreferenceStore().getInt(PreferenceConstants.P_BROWSE_ROW_NUMBER);
			if(table.getConnection() instanceof UnitedOlapConnection) {
				model = table.getConnection().executeQuery(table.getName(), maxRows, null); //$NON-NLS-1$
			}
			else {
				model = table.getConnection().executeQuery("select * from " + table.getName(), maxRows, null); //$NON-NLS-1$
			}
			composite = new CompositeTableBrowser(parent, SWT.NONE, colNames, model);
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		} catch (Exception e) {
			Activator.getLogger().error(Messages.DialogPhysicalTable_2, e); //$NON-NLS-1$
			e.printStackTrace();
		}
		
		
		
		return parent;
	}

	
}
