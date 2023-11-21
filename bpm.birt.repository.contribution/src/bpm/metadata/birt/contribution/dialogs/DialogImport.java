package bpm.metadata.birt.contribution.dialogs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.dom4j.Dom4jXPath;

import bpm.metadata.birt.oda.ui.Activator;
import bpm.metadata.birt.oda.ui.trees.TreeContentProvider;
import bpm.metadata.birt.oda.ui.trees.TreeDirectory;
import bpm.metadata.birt.oda.ui.trees.TreeItem;
import bpm.metadata.birt.oda.ui.trees.TreeLabelProvider;
import bpm.metadata.birt.oda.ui.trees.TreeParent;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.LinkedDocument;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class DialogImport extends Dialog {
	private static Color errorColor = new Color(Display.getDefault(), 255, 0, 0);
	private static Color color = new Color(null, 255, 255, 255);
	private Text host, login, password;
	private TreeViewer viewer;
	private IRepositoryApi sock;
	//private Button importFiles;
	private Text fileOutput;
	private ComboViewer groupNames;

	
	private TableViewer dependancies;
	private HashMap<LinkedDocument, String> fileMap = new HashMap<LinkedDocument, String>();
	private ComboViewer repositoryViewer;
	
	//added for linked documents 
//	private TableViewer viewerDep;
	
	

	public DialogImport(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}


	private void updateButtons(){
		try{
			boolean b = !fileOutput.getText().isEmpty()
			&& sock != null && !repositoryViewer.getSelection().isEmpty()
			&& !viewer.getSelection().isEmpty()
			&& ((IStructuredSelection)viewer.getSelection()).getFirstElement() instanceof TreeItem
			&& !groupNames.getSelection().isEmpty();
			
			getButton(IDialogConstants.OK_ID).setEnabled(b) ;
		}catch(Exception ex){
			ex.printStackTrace();
			getButton(IDialogConstants.OK_ID).setEnabled(false);
		}
		
	}
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		Label l = new Label(container, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Activator.getResourceString("ConnectionComposite.VanillaUrl")); //$NON-NLS-1$
		
		host = new Text(container, SWT.BORDER);
		host.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		host.setText(
				ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL) != null 
				? ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL) 
				: "http://localhost:7171/VanillaRuntime");
	
		Label l2 = new Label(container, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Activator.getResourceString("ConnectionComposite.Username")); //$NON-NLS-1$
		
		login = new Text(container, SWT.BORDER);
		login.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		login.setText("system");
		
		Label l3 = new Label(container, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText(Activator.getResourceString("DialogImport.1")); //$NON-NLS-1$
		
		password = new Text(container, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		password.setText("system");
		
		Button b = new Button(container, SWT.PUSH);
		b.setLayoutData(new GridData(SWT.FILL, GridData.CENTER, true, false, 2, 1));
		b.setText("Connection");
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				BaseVanillaContext vctx = new BaseVanillaContext(
						host.getText(), login.getText(), password.getText());
				
				IVanillaAPI api = new RemoteVanillaPlatform(vctx);
				
				User user = null;
				
				
				try{
					try{
						user = api.getVanillaSecurityManager().authentify("", vctx.getLogin(), vctx.getPassword(), false);
					}catch(Exception ex){
						repositoryViewer.setInput(Collections.EMPTY_LIST);
						groupNames.setInput(Collections.EMPTY_LIST);
						MessageDialog.openError(getShell(), "Connection", "Connection failed.\n" + ex.getMessage());
						return;
					}
					try{
						repositoryViewer.setInput(api.getVanillaRepositoryManager().getUserRepositories(vctx.getLogin()));
					}catch(Exception ex){
						ex.printStackTrace();
						repositoryViewer.setInput(Collections.EMPTY_LIST);
						MessageDialog.openError(getShell(), "Repository Loading", "Failed to load repositories.\n" + ex.getMessage());
					}
					
					
					try {
						groupNames.setInput(api.getVanillaSecurityManager().getGroups(user));
					} catch (Exception ex) {
						ex.printStackTrace();
						groupNames.setInput(Collections.EMPTY_LIST);
						MessageDialog.openError(getShell(), "Group Loading", "Failed to load groups.\n" + ex.getMessage());

					}
				}finally{
					updateButtons();
				}
				
				
			}
		});
		
		l3 = new Label(container, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText(Activator.getResourceString("ConnectionComposite.10"));  //$NON-NLS-1$
	
		
		groupNames = new ComboViewer(container, SWT.READ_ONLY);
		groupNames.setContentProvider(new ArrayContentProvider());
		groupNames.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Group)element).getName();
			}
		});
		groupNames.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		groupNames.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				repositoryViewer.setSelection(StructuredSelection.EMPTY);
				updateButtons();
				
			}
		});
		
		Label Repository = new Label(container, SWT.NONE);
		Repository.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		Repository.setText("Repository");
		
		repositoryViewer = new ComboViewer(container, SWT.READ_ONLY);
		repositoryViewer.setContentProvider(new ArrayContentProvider());
		repositoryViewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Repository)element).getName();
			}
		});
		repositoryViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				try{
					createInput();
				}catch(Throwable ex){
					ex.printStackTrace();
					MessageDialog.openError(getShell(), Activator.getResourceString("DialogImport.3"), ex.getMessage()); //$NON-NLS-1$
				}finally{
					updateButtons();
				}
				
			}
		});
		
		Combo combo = repositoryViewer.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		
		
		
		
		Composite container2 = new Composite(container, SWT.NONE);
		container2.setLayout(new GridLayout(2, true));
		container2.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
		
		viewer = new TreeViewer(container2, SWT.BORDER | SWT.V_SCROLL);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.setContentProvider(new TreeContentProvider());
		viewer.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
				if (ss.isEmpty() || !(ss.getFirstElement() instanceof TreeItem)){

				}
				else{
					
					fileMap.clear();
					
					
					try{
						RepositoryItem it = ((TreeItem)ss.getFirstElement()).getItem();
						
						for(LinkedDocument l : sock.getRepositoryService().getLinkedDocumentsForGroup(it.getId(), sock.getContext().getGroup().getId())){
							fileMap.put(l, l.getName() + "." + l.getFormat());
						}
						
					}catch(Exception ex){
						ex.printStackTrace();
						MessageDialog.openWarning(getShell(), "Warning", "An error occured when looking for BirtReport Resources. If you import the model it may missed ressources files if it require some.\nThe followinf error occured : " + ex.getMessage());
					}
					dependancies.setInput(fileMap.keySet());
					try {
						TreeItem ti = (TreeItem) ss.getFirstElement();
						ti.getItem();
					
					} catch (Exception e) {
						e.printStackTrace();
						MessageDialog.openError(getShell(), "Error", "Failed to get Linked Document");
					}
					
				}
				updateButtons();
			}
			
		});
		
		
		dependancies = new TableViewer(container2, SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		dependancies.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		dependancies.setContentProvider(new ArrayContentProvider());
		dependancies.getTable().setHeaderVisible(true);
		dependancies.getTable().setLinesVisible(true);
		
		final TableViewerColumn colName = new TableViewerColumn(dependancies, SWT.LEFT);
		colName.getColumn().setText("Resource Name");
		colName.getColumn().setWidth(200);
		colName.setLabelProvider(new ColumnLabelProvider(){

			@Override
			public String getText(Object element) {
				return ((LinkedDocument)element).getName();
			}
			
		});
		
		TableViewerColumn folder = new TableViewerColumn(dependancies, SWT.LEFT);
		folder.getColumn().setText("Download FileName");
		folder.getColumn().setWidth(200);
		folder.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return fileMap.get(element);
			}
		});
		folder.setEditingSupport(new EditingSupport(dependancies) {
			TextCellEditor editor = new TextCellEditor(dependancies.getTable());
			@Override
			protected void setValue(Object element, Object value) {
				String s = (String)value;
				if (!s.endsWith(((LinkedDocument)element).getFormat())){
					s = s + ((LinkedDocument)element).getFormat();
				}
				fileMap.put((LinkedDocument)element, (String)value);
				dependancies.refresh();
				
			}
			
			@Override
			protected Object getValue(Object element) {
				return fileMap.get((LinkedDocument)element);
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return editor;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});
		
//		Table tabDep = new Table(container, SWT.BORDER | SWT.V_SCROLL);
//		tabDep.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
//		
//		TableColumn col1 = new TableColumn(tabDep, SWT.NONE);
//		col1.setText("Birt include name");
//		col1.setWidth(100);
//		TableColumn col2 = new TableColumn(tabDep, SWT.NONE);
//		col2.setText("Filename");
//		col2.setWidth(450);
		
				
		Composite c = new Composite(container, SWT.NONE);
		c.setLayout(new GridLayout(3, false));
		c.setLayoutData(new GridData(GridData.FILL, GridData.END, true, false, 2, 1));
		
		Label l0 = new Label(c, SWT.NONE);
		l0.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l0.setText(Activator.getResourceString("DialogImport.4")); //$NON-NLS-1$
		
		fileOutput = new Text(c, SWT.BORDER);
		fileOutput.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		fileOutput.setEnabled(false);
		fileOutput.setForeground(color); // FP IDEA
		
		
		
		Button d = new Button(c, SWT.PUSH);
		d.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		d.setText("..."); //$NON-NLS-1$
		d.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getShell(), SWT.SAVE);
				 

				fd.setFilterPath(Platform.getLocation().toOSString());
				fd.setFilterExtensions(new String[]{"*.rptdesign", "*.*"}); //$NON-NLS-1$ //$NON-NLS-2$
				
				String s = fd.open();
				if (s != null){
					fileOutput.setText(s);
				}
				updateButtons();
			}
			
		});
		
//		fillDatas();
		
		return container;
	}
	
	
	private void createInput() throws Exception{
		
		try{
			if (repositoryViewer.getSelection().isEmpty() || groupNames.getSelection().isEmpty()){
				return;
			}
			
			Repository r = (Repository)((IStructuredSelection)repositoryViewer.getSelection()).getFirstElement();
			Group g = (Group)((IStructuredSelection)groupNames.getSelection()).getFirstElement();
			
			sock = new RemoteRepositoryApi(new BaseRepositoryContext(
					new BaseVanillaContext(
							host.getText(), login.getText(), password.getText()), g, r)); 
				
		}catch(Exception ex){
			ex.printStackTrace();
			sock = null;
			viewer.setInput( new TreeParent("root"));
			throw ex;
		}
		
		
		
		//IRepository rep = sock.getRepository(IRepositoryConnection.CUST_TYPE, IRepositoryConnection.BIRT_REPORT_SUBTYPE);
		final TreeParent root = new TreeParent("root"); //$NON-NLS-1$
		IRunnableWithProgress r = new IRunnableWithProgress() {
			
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException,
					InterruptedException {
				
				try{
					bpm.vanilla.platform.core.repository.Repository rep = new bpm.vanilla.platform.core.repository.Repository(sock, IRepositoryApi.CUST_TYPE, IRepositoryApi.BIRT_REPORT_SUBTYPE);
					
					
					
					for(RepositoryDirectory d : rep.getRootDirectories()){
						TreeDirectory tp = new TreeDirectory(d);
						root.addChild(tp);
						buildChilds(rep, tp);
						
						
						for(RepositoryItem di : rep.getItems(d)){
							TreeItem ti = new TreeItem(di);
							tp.addChild(ti);
						}
					}
				}catch(Throwable ex){
					ex.printStackTrace();
				}
				
				
			}
		};
		
		IProgressService service = PlatformUI.getWorkbench().getProgressService();
	     try {
	    	 service.run(false, false, r);
	     } catch (Throwable e) {
	       e.printStackTrace();
	     } 
		//sock.get
		//admin = new LinkedDocumentAdministrator(sock, new AdminAccess("http://192.168.1.133:8080/vanilla"));
		
		viewer.setInput(root);
	}

	private void buildChilds(bpm.vanilla.platform.core.repository.Repository rep, TreeDirectory parent){

		RepositoryDirectory dir = ((TreeDirectory)parent).getDirectory();
		
		
		try{
			for(RepositoryDirectory d : rep.getChildDirectories(dir)){
				TreeDirectory td = new TreeDirectory(d);
				parent.addChild(td);
				for(RepositoryItem di :rep.getItems(d)){
					TreeItem ti = new TreeItem(di);
					td.addChild(ti);
				}
				buildChilds(rep, td);
			}
		}catch(Throwable ex){
			ex.printStackTrace();
		}
		
		
	}

	
	@Override
	protected void okPressed() {
		
		StringBuffer st = new StringBuffer();
		File mainF = new File(fileOutput.getText());
		if (mainF.exists()){
			st.append(mainF.getAbsolutePath() +"\n");
		}
		
		for(LinkedDocument ld : fileMap.keySet()){
			String s = fileMap.get(ld);
			if (!s.isEmpty()){
				File f = new File(mainF.getParentFile(), s + ld.getFormat());
				if (f.exists()){
					st.append(f.getAbsolutePath() + "\n");
				}
			}
			
		}
		
		String s = st.toString();
		
		if (!s.isEmpty()){
			if (!MessageDialog.openQuestion(getShell(), "Import", "Some files already exists in the file system. Do you want to continue?\n" + s)){
				return;
			}
		}
		
		IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
		RepositoryItem it = ((TreeItem)ss.getFirstElement()).getItem();

		
		String xml = null;
		try{
			xml = sock.getRepositoryService().loadModel(it);
		}catch(Exception ex){
			ex.printStackTrace();
			MessageDialog.openError(getShell(), "Error", "Error when loading Birt Model :\n" + ex.getMessage());
			return;
		}
		
		try{
			xml = replaceResourcesFiles(xml);
		}catch(Exception ex){
			ex.printStackTrace();
			MessageDialog.openError(getShell(), "Error", "Error when replacing Resources names within Birt Model :\n" + ex.getMessage());
			return;
		}
		try{
			PrintWriter pw = new PrintWriter(fileOutput.getText(), "UTF-8");
			
			pw.write(xml);
			pw.close();
		}catch(Exception ex){
			ex.printStackTrace();
			MessageDialog.openError(getShell(), "Error", "Error when copying Birt file :\n" + ex.getMessage());
			return;
		}
		
		bpm.metadata.birt.contribution.Activator.getDefault().addItemPathMapping(fileOutput.getText(), it.getId());
		bpm.metadata.birt.contribution.Activator.getDefault().setRepositoryApi(sock);
		
		
		for (LinkedDocument ld : fileMap.keySet()){
			InputStream is = null;
			
			try{
				is = sock.getDocumentationService().importLinkedDocument(ld.getId());
			}catch(Exception ex){
				ex.printStackTrace();
				MessageDialog.openError(getShell(), "Error", "Error when uploading Birt Resource file " + ld.getName() + ":\n" + ex.getMessage());

			}
			
			try{
				File outputFolder = new File(fileOutput.getText()).getParentFile();
				FileOutputStream os = new FileOutputStream(outputFolder.getAbsolutePath() + "/" + fileMap.get(ld) + ld.getFormat());
				
				int sz = -1;
				byte[] buf = new byte[1024];
				
				while( (sz = is.read(buf)) != -1){
					os.write(buf, 0, sz);
				}
				
				is.close();
				os.close();
			}catch(Exception ex){
				ex.printStackTrace();
				MessageDialog.openError(getShell(), "Error", "Error when writing Birt Resource file " + ld.getName() + ":\n" + ex.getMessage());

			}
		}
		super.okPressed();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button b  = createButton(parent, IDialogConstants.OK_ID, "Load Item", true); //$NON-NLS-1$
		b.setEnabled(false);
				
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}
	
//	private void fillDatas(){
//		HashMap<String, String> commonPref;
//		try {
//			commonPref = PreferenceReader.readCommonsProperties();
//			String url = commonPref.get(CommonPreferenceConstants.P_BPM_REPOSITORY_URL).replace("\\:", ":");; //$NON-NLS-1$ //$NON-NLS-2$
//			String login = commonPref.get(CommonPreferenceConstants.P_BPM_REPOSITORY_LOGIN).replace("\\:", ":");; //$NON-NLS-1$ //$NON-NLS-2$
//			String password = commonPref.get(CommonPreferenceConstants.P_BPM_REPOSITORY_PASSWORD).replace("\\:", ":");; //$NON-NLS-1$ //$NON-NLS-2$
//
//			host.setText(url);
//			this.password.setText(password);
//			this.login.setText(login);
//			
//		}catch(Exception e){
//			e.printStackTrace();
//			
//			host.setText(Activator.getDefault().getPreferenceStore().getString(CommonPreferenceConstants.P_BPM_REPOSITORY_URL));
//			this.login.setText(Activator.getDefault().getPreferenceStore().getString(CommonPreferenceConstants.P_BPM_REPOSITORY_LOGIN));
//			this.password.setText(Activator.getDefault().getPreferenceStore().getString(CommonPreferenceConstants.P_BPM_REPOSITORY_PASSWORD));
//		}
//	}
	
//	public String getXml(){
//		return xml;
//	}
	
//	public String getFileName(){
//		return fileName;
//	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(800, 600);
		getShell().setText(Activator.getResourceString("DialogImport.11")); //$NON-NLS-1$

	}
	
	
	
	private String replaceResourcesFiles(String xml) throws Exception{
		Document document = DocumentHelper.parseText(xml);
		
		SimpleNamespaceContext nmsCtx = null;
		//NameSpace shit, needed for BIrt, amongst others
		HashMap nameSpaceMap = new HashMap();
		if (document.getRootElement().getNamespaceURI() != null){
			nameSpaceMap.put("birt", document.getRootElement().getNamespaceURI());
			nmsCtx = new SimpleNamespaceContext(nameSpaceMap);
		}		
		
		Dom4jXPath xpath;
		if (nmsCtx != null) {
			xpath = new Dom4jXPath("//birt:simple-property-list[@name='includeResource']/birt:value");
			xpath.setNamespaceContext(nmsCtx);
		}
		else {
			xpath = new Dom4jXPath("//simple-property-list[@name='includeResource']/value");
		}
		
		List<Node> nodes = xpath.selectNodes(document);
		boolean changed = false;
		for (Node n : nodes) {
			String depName = n.getText();
			for(LinkedDocument d : fileMap.keySet()){
				if (depName.endsWith(d.getName())){
					String fName = fileMap.get(d).replace(d.getFormat(), "");
					if (!fName.equals(depName)){
						n.setText(fName);
					}
					changed = true;
					break;
				}
			}
			
//			for(LinkedDocument d : )
		}
		
		if (changed){
			OutputFormat f = OutputFormat.createPrettyPrint();
			f.setEncoding("UTF-8");
			f.setTrimText(false);
			f.setNewlines(false);
			
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			
			XMLWriter writer = new XMLWriter(os, f);
			writer.write(document);
			os.close();
			return os.toString();
		}
		
		return xml;
	}
}
