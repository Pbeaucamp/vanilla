package bpm.gateway.ui.dialogs.database;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.server.userdefined.Parameter;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.ui.Activator;
import bpm.sqldesigner.query.SQLDesignerComposite;
import bpm.sqldesigner.query.model.filter.ColumnFilter;

public class DialogSqlDesigner extends Dialog {

	
	private SQLDesignerComposite composite;
	private DataBaseConnection con; 
	private DocumentGateway document;
	
	public  DialogSqlDesigner(Shell parentShell, DataBaseServer server, DocumentGateway document) {
		super(parentShell);
		setShellStyle(getShellStyle() |   SWT.RESIZE);
		this.con = (DataBaseConnection)server.getCurrentConnection(null);
		this.document = document;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		
		List<ColumnFilter> f = new ArrayList<ColumnFilter>();
		
		for(Variable v : ResourceManager.getInstance().getVariables()){
			ColumnFilter c = new ColumnFilter();
			c.setName(v.getName());
			c.setValue(v.getOuputName());
			f.add(c);
		}
		
		
		for(Parameter p : ResourceManager.getInstance().getParameters()){
			ColumnFilter c = new ColumnFilter();
			c.setName(p.getName());
			c.setValue(p.getOuputName());
			f.add(c);
		}
		
		composite = new SQLDesignerComposite(parent, SWT.NONE, con, f, document);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		return composite;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		query = composite.getQuery();
		
		super.okPressed();
//		composite.dispose();
		
	}
	
	
	public String getSqlStatement(){
		return query;
	}
	
	private String query;
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#initializeBounds()
	 */
	@Override
	protected void initializeBounds() {
		getShell().setSize(800, 600);
	
	}

}
