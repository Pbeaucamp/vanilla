package bpm.birep.admin.client.dialog.item;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import adminbirep.Activator;
import adminbirep.Messages;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.DataSource;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class DialogJasper extends DialogItem {
	private String xml;
	
	private Combo datasource;
	private Integer[] datasourceid;
	private List<DataSource> datasources;

	public DialogJasper(Shell parentShell, RepositoryDirectory target, String xml) {
		super(parentShell, target);
		this.xml = xml;
	}

	public String[] ItemDatasource() throws Exception{
		datasources = Activator.getDefault().getRepositoryApi().getImpactDetectionService().getDatasourcesByType(DataSource.DATASOURCE_JDBC);
		int size = datasources.size();
		Iterator<DataSource> it = datasources.iterator();
		String[] datasourcename = new String[size];
		datasourceid = new Integer[size];
		int i = 0;
		while(it.hasNext()){
			DataSource data = (DataSource)it.next();
			datasourcename[i] = data.getName(); 
			datasourceid[i] = (Integer)data.getId();
			i++;
		}
		return datasourcename;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		super.createDialogArea(parent);
		Label dl = new Label(c, SWT.NONE);
		dl.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		dl.setText("DataSourceName"); //$NON-NLS-1$
			
		datasource  = new Combo(c, SWT.READ_ONLY);
		datasource.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		try {
			datasource.setItems(ItemDatasource());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return c;
	}
	
	@Override
	protected void okPressed() {
		IRepositoryApi sock = Activator.getDefault().getRepositoryApi();
		RepositoryItem item = null;
		try {
			if (datasource.getSelectionIndex() != -1) {
				
				String fullXml = createJasperXml(datasourceid[datasource.getSelectionIndex()]);
				
				item = sock.getRepositoryService().addDirectoryItemWithDisplay(
						IRepositoryApi.CUST_TYPE,
						IRepositoryApi.JASPER_REPORT_SUBTYPE,
						target, 
						name.getText(), 
						comment.getText(), 
						internalversion.getText(), "", fullXml, true); //$NON-NLS-1$
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), "Error", e.getMessage()); //$NON-NLS-1$
			return;
		}

		
		try {
			for(Object g : groups.getCheckedElements()){
				Activator.getDefault().getRepositoryApi().getAdminService().addGroupForItem(((Group)g).getId(), item.getId());
			}
			
			for(Object g : groups.getCheckedElements()){
				Activator.getDefault().getRepositoryApi().getAdminService().setObjectRunnableForGroup(((Group)g).getId(), item);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.okPressed();
	}


	private String createJasperXml(int datasourceId) {
		StringBuilder buf = new StringBuilder();
		buf.append("<JASPER>"); //$NON-NLS-1$
		buf.append("    <type>" + IRepositoryApi.SUBTYPES_NAMES[IRepositoryApi.JASPER_REPORT_SUBTYPE]+ "</type>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("    <datasourceid>" + datasourceId + "</datasourceid>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		if (xml.startsWith("<?")){ //$NON-NLS-1$
			xml = xml.substring(xml.indexOf("?>") + 2); //$NON-NLS-1$
		}
		buf.append("    <xml>\n" + xml + "</xml>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("</JASPER>"); //$NON-NLS-1$
		return buf.toString();
	}
}
