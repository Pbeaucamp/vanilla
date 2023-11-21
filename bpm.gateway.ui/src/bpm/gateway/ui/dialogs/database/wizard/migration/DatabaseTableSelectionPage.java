package bpm.gateway.ui.dialogs.database.wizard.migration;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.server.file.FileXLSHelper;
import bpm.gateway.ui.viewer.TreeContentProvider;
import bpm.gateway.ui.viewer.TreeObject;
import bpm.gateway.ui.viewer.TreeParent;

public class DatabaseTableSelectionPage extends WizardPage implements IGatewayWizardPage{

	private CheckboxTreeViewer viewer;
	
	protected DatabaseTableSelectionPage(String pageName) {
		super(pageName);
		
	}

	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout());
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// create contents
		createPageContent(mainComposite);

		// page setting
		setControl( mainComposite );
		setPageComplete(false);

	}
	
	private void createPageContent(Composite parent){
		viewer = new CheckboxTreeViewer(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new TreeContentProvider());
		viewer.addCheckStateListener(new ICheckStateListener(){

			public void checkStateChanged(CheckStateChangedEvent event) {
				for(Object o : ((TreeContentProvider)viewer.getContentProvider()).getChildren(event.getElement())){
					viewer.setChecked(o, event.getChecked());
				}
				getContainer().updateButtons();
				
			}
			
		});
		
	}
	

	
	public Properties getPageProperties(){
		Properties p = new Properties();
		return p;
	}
	
	
	
	private void createInputForDatabase(Properties props) throws Exception{

		DataBaseServer server = (DataBaseServer)ResourceManager.getInstance().getServer(props.getProperty("sourceServer")); //$NON-NLS-1$
		Connection jdbcSock = ((DataBaseConnection)server.getCurrentConnection(null)).getSocket(null);
		
		DatabaseMetaData dmd = jdbcSock.getMetaData();
		
		String rootName = ""; //$NON-NLS-1$
		if (props.getProperty("sourceSchema") != null){ //$NON-NLS-1$
			rootName = jdbcSock.getCatalog() + "." + props.getProperty("sourceSchema"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else{
			rootName = jdbcSock.getCatalog();
		}
		TreeParent root = new TreeParent(""); //$NON-NLS-1$
		
		TreeParent tSch = new TreeParent(rootName);
		
		
		ResultSet rs = dmd.getTables(jdbcSock.getCatalog(), 
				props.getProperty("sourceSchema"), "%",  //$NON-NLS-1$ //$NON-NLS-2$
				new String[]{"TABLE"}); //$NON-NLS-1$
		
		while(rs.next()){
			tSch.addChild(new TreeParent(rs.getString("TABLE_NAME"))); //$NON-NLS-1$
		}
		
		root.addChild(tSch);
		viewer.setInput(root);
	}
	
	public void createInput(Properties props) throws Exception{
		
		if (props.getProperty("sourceXlsFile") != null){ //$NON-NLS-1$
			createInputForXls(props);
		}
		else{
			createInputForDatabase(props);
		}
		
		
		
	}
	private void createInputForXls(Properties props) throws Exception{
		
		FileInputStream fis = new FileInputStream(props.getProperty("sourceXlsFile")); //$NON-NLS-1$
		
		TreeParent root = new TreeParent(""); //$NON-NLS-1$
		for(String s : FileXLSHelper.getWorkSheetsNames(fis)){
			TreeParent  table = new TreeParent(s);
			root.addChild(table);
		}
		viewer.setInput(root);
	}
	
	protected List<String> getTablesToCreate(){
		List<String> l = new ArrayList<String>();
		
		for(Object o : viewer.getCheckedElements()){
			Object[] childs = ((ITreeContentProvider)viewer.getContentProvider()).getChildren(o);
			if (childs != null && childs.length > 0){
				continue;
			}
			
			l.add(((TreeObject)o).getName());
		}
		
		return l;
	}

	@Override
	public boolean isPageComplete() {
		return viewer.getCheckedElements().length > 0;
	}
	@Override
	public boolean canFlipToNextPage() {
		return isPageComplete();
	}
	
}
