package bpm.fd.design.ui.rcp.dialogs;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.IResource;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.rcp.Messages;


public class DialogSaveAs extends Dialog {
	
	private TableViewer viewer;
	private HashMap<Object, String> fileMap = new HashMap<Object, String>();
	private FdProject project;
//	private Button useCreatedFilesForProject;
	
	public DialogSaveAs(Shell parentShell, FdProject project) {
		super(parentShell);
		this.project = project;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		main.setLayout(new GridLayout());
		
		
		viewer = new TableViewer(main, SWT.BORDER | SWT.FULL_SELECTION);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		viewer.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			public void dispose() {
				
				
			}
			
			public Object[] getElements(Object inputElement) {
				Collection<Object> c = (Collection<Object>)inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		
		TableViewerColumn resourceCol = new TableViewerColumn(viewer, SWT.NONE);
		resourceCol.getColumn().setText(Messages.DialogSaveAs_0);
		resourceCol.getColumn().setWidth(200);
		resourceCol.setLabelProvider(new ColumnLabelProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				if (element instanceof FdModel){
					return ((FdModel)element).getName();
				}
				if (element instanceof Dictionary){
					return ((Dictionary)element).getName();
				}
				if (element instanceof IResource){
					return ((IResource)element).getName();
				}
				return ""; //$NON-NLS-1$
			}
			
		});
		
		TableViewerColumn resourceFile = new TableViewerColumn(viewer, SWT.NONE);
		resourceFile.getColumn().setText(Messages.DialogSaveAs_2);
		resourceFile.getColumn().setWidth(400);
		resourceFile.setLabelProvider(new ColumnLabelProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				String s = fileMap.get(element);
				if (s == null){
					return ""; //$NON-NLS-1$
				}
				return s;
			}
			
		});
		
		
		resourceFile.setEditingSupport(new EditingSupport(viewer) {
			DialogCellEditor editor = new DialogCellEditor(viewer.getTable()) {
			FileDialog fd = new FileDialog(getShell(), SWT.SAVE);
				@Override
				protected Object openDialogBox(Control cellEditorWindow) {
					
					Object o = ((IStructuredSelection)viewer.getSelection()).getFirstElement();
					if (o instanceof FdModel){
						fd.setFilterExtensions(new String[]{"*.freedashboard"}); //$NON-NLS-1$
					}
					else if (o instanceof Dictionary){
						fd.setFilterExtensions(new String[]{"*.dictionary"}); //$NON-NLS-1$
					}
					else if (o instanceof IResource){
						String z = ((IResource)o).getFile().getName();
						z = z.substring(z.lastIndexOf(".") + 1); //$NON-NLS-1$
						fd.setFilterExtensions(new String[]{"*." + z}); //$NON-NLS-1$
					}
					
					String s = fd.open();
					
					return s;
				}
			};
			@Override
			protected void setValue(Object element, Object value) {
				if (value != null){
					String sval = (String)value;
					Object o = ((IStructuredSelection)viewer.getSelection()).getFirstElement();
					if (o instanceof FdModel){
						if (!sval.endsWith(".freedashboard")){ //$NON-NLS-1$
							sval =	sval + ".freedashboard"; //$NON-NLS-1$
						}
					}
					else if (o instanceof Dictionary){
						if (!sval.endsWith(".dictionary")){ //$NON-NLS-1$
							sval =	sval + ".dictionary"; //$NON-NLS-1$
						}
						
					}
					else if (o instanceof IResource){
						String z = ((IResource)o).getFile().getName();
						z = z.substring(z.lastIndexOf(".") + 1); //$NON-NLS-1$
						if (!sval.endsWith("." + z)){ //$NON-NLS-1$
							sval =	sval + "." + z; //$NON-NLS-1$
						}
					}
					fileMap.put(element, sval);
					viewer.refresh();
					
				}
				
			}
			
			@Override
			protected Object getValue(Object element) {
				if (fileMap.get(element) == null){
					return ""; //$NON-NLS-1$
				}
				return fileMap.get(element);
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
		
		
//		useCreatedFilesForProject = new Button(main, SWT.CHECK);
//		useCreatedFilesForProject.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		useCreatedFilesForProject.setText("Update project files to saved files");
//		useCreatedFilesForProject.setToolTipText("Checking this option will update the current project by setting its files to the newly created files.\n If it is not checked, you will need to open the files manually from File +  Open Model and selecting the files");
//		
		return main;
	}
	
	private void fillDatas(){
		fileMap.put(project.getFdModel(), ""); //$NON-NLS-1$
		fileMap.put(project.getDictionary(), ""); //$NON-NLS-1$
		
		for(IResource r : project.getResources()){
			fileMap.put(r, ""); //$NON-NLS-1$
		}
		
		viewer.setInput(fileMap.keySet());
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#initializeBounds()
	 */
	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogSaveAs_19);
		getShell().setSize(600, 400);
		fillDatas();
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		try{
			IProject projetc = Activator.getDefault().getResourceProject();
			for(Object o : fileMap.keySet()){
				String filePath = fileMap.get(o);
				if (o instanceof FdModel){
					
					if (!filePath.endsWith(".freedashboard")){ //$NON-NLS-1$
						throw new Exception(Messages.DialogSaveAs_21);
					}
					try{
						XMLWriter writer = new XMLWriter(new FileOutputStream(filePath), OutputFormat.createPrettyPrint());
						writer.write(((FdModel)o).getElement());
						writer.close();
					}catch(IOException ex){
						
						ex.printStackTrace();
						throw ex;
					}
					
					
					
					
				}
				else if (o instanceof Dictionary){
					
					if (!filePath.endsWith(".dictionary")){ //$NON-NLS-1$
						throw new Exception(Messages.DialogSaveAs_23);
					}
					
					try{
						XMLWriter writer = new XMLWriter(new FileOutputStream(filePath), OutputFormat.createPrettyPrint());
						writer.write(((Dictionary)o).getElement());
						writer.close();
					}catch(IOException ex){
						
						ex.printStackTrace();
						throw ex;
					}
					
				}
				else if (o instanceof IResource){
					try{
						
						if (!filePath.endsWith(((IResource)o).getFile().getAbsolutePath().substring(((IResource)o).getFile().getAbsolutePath().lastIndexOf(".") + 1))){ //$NON-NLS-1$
							throw new Exception(Messages.DialogSaveAs_25);
						}
						
						FileOutputStream fos = new FileOutputStream(filePath);
						FileInputStream fis = new FileInputStream(((IResource)o).getFile());
						
						byte[] buffer = new byte[1024];
						int sz ;
						
						while((sz = (fis.read(buffer))) > 0){
							fos.write(buffer, 0, sz);
							
						}
						
						fos.close();
						fis.close();

					}catch(IOException e){
						e.printStackTrace();
						throw e;
					}
				}
			}
		}catch(Exception exp){
			exp.printStackTrace();
			MessageDialog.openError(getShell(), Messages.DialogSaveAs_26, exp.getCause().getMessage());
			return;
		}
		
		super.okPressed();
	}
	
	
}
