package org.fasd.views.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.fasd.datasource.DataSource;
import org.fasd.datasource.DatasourceOda;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.FAModel;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPHierarchy;
import org.fasd.utils.trees.TreeDim;
import org.fasd.utils.trees.TreeHierarchy;
import org.fasd.utils.trees.TreeLabelProvider;
import org.fasd.utils.trees.TreeParent;
import org.fasd.utils.trees.TreeUOlapContentProvider;
import org.fasd.utils.trees.TreeUOlapMember;
import org.freeolap.FreemetricsPlugin;

import bpm.fa.api.olap.unitedolap.UnitedOlapLoader;
import bpm.fa.api.olap.unitedolap.UnitedOlapLoaderFactory;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.impl.RuntimeContext;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;

public class DialogDimBrowser extends Dialog {
	private OLAPDimension dim;
	private TreeViewer viewer;
	private TabFolder tabFolder;
	private TabItem treeItem;
	private TreeParent model;
	private String schemaId;
	private IRuntimeContext ctx;
	
	@Override
	public int open() {
		int r = super.open();
		return r;
	}


	public DialogDimBrowser(Shell parentShell, OLAPDimension dim) {
		super(parentShell);
		this.dim = dim;
		setShellStyle(getShellStyle()|SWT.RESIZE);
				
	}

	
	@Override
	protected Control createDialogArea(Composite parent) {
		parent.setLayout(new GridLayout(1, true));
		
		tabFolder = new TabFolder(parent, SWT.NONE);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		treeItem = new TabItem(tabFolder, SWT.NONE, 0);
		treeItem.setText(LanguageText.DialogDimBrowser_Dim_Tree_Browser);
		treeItem.setControl(createBrowser(tabFolder));
    	
		return parent;
	}
	
	private Control createBrowser(TabFolder parent){
		Composite tree = new Composite(parent, SWT.NONE);
		tree.setLayout(new GridLayout());
		viewer = new TreeViewer(tree, SWT.NONE);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		try {
			loadSchema();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.setContentProvider(new TreeUOlapContentProvider(schemaId, ctx));
		
		viewer.setInput(createModel(dim));
		return tree;
	}
	
	public TreeParent createModel(OLAPDimension dim) {
		FAModel faMod = dim.getParent().getParent();
		boolean utdOlap = false;
		for(DataSource ds : faMod.getDataSources()) {
			if(ds instanceof DatasourceOda) {
				utdOlap = true;
				break;
			}
		}
		if(utdOlap) {
			try {
				model = createUOlapModel(dim);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			model = dim.createModel();
		}
		return model;
	}
	

	private TreeParent createUOlapModel(OLAPDimension dim) throws Exception {
		
		TreeParent root = new TreeParent(""); //$NON-NLS-1$
		
		TreeDim td = new TreeDim(dim);
		
		for(OLAPHierarchy h : dim.getHierarchies()){
			TreeHierarchy th = new TreeHierarchy(h);
			TreeUOlapMember allMember = new TreeUOlapMember("All " + dim.getName(), "[" + dim.getName() + "." + h.getName() + "]" + ".[All " + dim.getName() + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			
			th.addChild(allMember);

			td.addChild(th);
		}
		root.addChild(td);
		return root;
	}

	private void loadSchema() throws Exception {
		UnitedOlapLoader loader = UnitedOlapLoaderFactory.getLoader();
		if(FreemetricsPlugin.getDefault().getRepositoryConnection() == null) {
			ctx = new RuntimeContext("system", "system", "System", 1); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		else {
			IRepositoryApi sock = FreemetricsPlugin.getDefault().getRepositoryConnection();
			IVanillaAPI api = FreemetricsPlugin.getDefault().getVanillaApi();
			
			ctx = new RuntimeContext(sock.getContext().getVanillaContext().getLogin(), sock.getContext().getVanillaContext().getPassword(), 
					api.getVanillaSecurityManager().getGroupById(sock.getContext().getGroup().getId()).getName(), sock.getContext().getGroup().getId());
		}
		FAModel model = FreemetricsPlugin.getDefault().getFAModel();
		
		schemaId = loader.getSchemaId(model);
		if(schemaId == null) {
			
			schemaId = loader.loadModel(model, ctx, null);
		}
		else{
			schemaId = loader.reloadModel(null, model, ctx, null);
		}
	}


	@Override
	protected void initializeBounds() {
		super.initializeBounds();
		this.getShell().setSize(800,600);
		this.getShell().setText(LanguageText.DialogDimBrowser_Dim_Bro);
	}
	
	@Override
	public boolean close() {
		return super.close();
	}
	
}
