package bpm.sqldesigner.ui;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.progress.IProgressService;

import bpm.sqldesigner.api.document.DocumentSnapshot;
import bpm.sqldesigner.api.document.SchemaView;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.api.xml.SaveData;
import bpm.sqldesigner.ui.action.ActionOpenWorkspace;
import bpm.sqldesigner.ui.action.SearchTableAction;
import bpm.sqldesigner.ui.editor.SQLDesignEditorInput;
import bpm.sqldesigner.ui.editor.SQLDesignGraphicalEditor;
import bpm.sqldesigner.ui.editor.save.SaveConstants;
import bpm.sqldesigner.ui.i18N.Messages;
import bpm.sqldesigner.ui.internal.Workspace;
import bpm.sqldesigner.ui.internal.WorkspaceSerializer;
import bpm.sqldesigner.ui.preferences.PreferenceConstants;
import bpm.sqldesigner.ui.snapshot.editor.SnapshotEditor;
import bpm.sqldesigner.ui.snapshot.editor.SnapshotEditorInput;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

//	private Action save;
	private Action saveAll;
	private Action saveAllAs;
	private Action open;
	private Action close;
	private Action newDoc;
	private IWorkbenchAction about;
	private IWorkbenchAction exit;
//	private IWorkbenchAction preferences/*, importWiz, exportWiz*/;
	private Action exportImage;
	private SearchTableAction search;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	@Override
	protected void makeActions(final IWorkbenchWindow window) {

		saveAll = new Action(Messages.ApplicationActionBarAdvisor_0) {
			@Override
			public void run() {
				Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().saveAllEditors(true);
				
				
				if (Activator.getDefault().getWorkspace().getFileName() == null){
					FileDialog fd = new FileDialog(window.getShell(), SWT.SAVE);
					fd.setText(Messages.ApplicationActionBarAdvisor_1);
					fd.setFilterPath("C:/"); //$NON-NLS-1$
					fd.setFilterExtensions(new String[] { "*."+ SaveConstants.EXTENTION }); //$NON-NLS-1$
					String selected = fd.open();
					
					if (SaveData.checkFile(selected)) {
						boolean erase = MessageDialog.openQuestion(	window.getShell(),Messages.ApplicationActionBarAdvisor_4,Messages.ApplicationActionBarAdvisor_5+ selected + Messages.ApplicationActionBarAdvisor_6);
						if (!erase){
							return;
						}
							
					}
					Activator.getDefault().getWorkspace().setFileName(selected);
				}
				
				
				
				try{
					Workspace wks = Activator.getDefault().getWorkspace();
					
					
					
					
					IProgressService service = PlatformUI.getWorkbench().getProgressService();
					
					service.run(false, false, new SaveWorkspaceRunnable());
					
					
					
					addFileToList(wks.getFileName());
					
					
					Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell().setText("BiDwhDesigner - " + wks.getFileName()); //$NON-NLS-1$

				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(window.getShell(), Messages.ApplicationActionBarAdvisor_8, Messages.ApplicationActionBarAdvisor_9 + ex.getMessage());
				}
			};
		};
		saveAll.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("save")); //$NON-NLS-1$
		saveAll.setEnabled(true);

//		save = new Action("Save Active Editor") {
//			@Override
//			public void run() {
//				Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().saveEditor(
//								Activator.getDefault().getWorkbench()
//										.getActiveWorkbenchWindow()
//										.getActivePage().getActiveEditor(),
//								true);
//			};
//		};
//		save.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("save"));
//		save.setEnabled(true);

		saveAllAs = new Action(Messages.ApplicationActionBarAdvisor_11) {
			@Override
			public void run() {
				FileDialog fd = new FileDialog(window.getShell(), SWT.SAVE);
				fd.setText(Messages.ApplicationActionBarAdvisor_12);
				fd.setFilterPath("C:/"); //$NON-NLS-1$
				fd.setFilterExtensions(new String[] { "*." + SaveConstants.EXTENTION }); //$NON-NLS-1$
				String selected = fd.open();

				if (SaveData.checkFile(selected)) {
					boolean erase = MessageDialog.openQuestion(	window.getShell() ,Messages.ApplicationActionBarAdvisor_15,Messages.ApplicationActionBarAdvisor_16+ selected + Messages.ApplicationActionBarAdvisor_17);
					if (!erase)
						return;
				}
				

				Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().saveAllEditors(true);
				try{
					Workspace wks = Activator.getDefault().getWorkspace();
					wks.setFileName(selected);
					
					
					IProgressService service = PlatformUI.getWorkbench().getProgressService();
					
					service.run(false, false, new SaveWorkspaceRunnable());

					
					Activator.getDefault().getWorkspace().setFileName(selected);
					addFileToList(selected);
					
					
					Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell().setText("BiDwhDesigner - " + selected); //$NON-NLS-1$

				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(window.getShell(), Messages.ApplicationActionBarAdvisor_19, Messages.ApplicationActionBarAdvisor_20 + ex.getMessage());
				}
				
				
				
			};
		};
		saveAllAs.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("save")); //$NON-NLS-1$
		saveAllAs.setEnabled(true);

		open = new Action(Messages.ApplicationActionBarAdvisor_22) {
			@Override
			public void run() {
				Shell shell = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
				FileDialog fd = new FileDialog(shell, SWT.OPEN);
				fd.setText(Messages.ApplicationActionBarAdvisor_23);
				fd.setFilterPath("C:/"); //$NON-NLS-1$
				fd.setFilterExtensions(new String[] { "*." + SaveConstants.EXTENTION }); //$NON-NLS-1$
				String selected = fd.open();

				if (!SaveData.checkFile(selected)) {
					MessageDialog.openError(shell,Messages.ApplicationActionBarAdvisor_26,Messages.ApplicationActionBarAdvisor_27 + selected	+ Messages.ApplicationActionBarAdvisor_28);
					return;
				}

//				SaveConstants.closeWorkspace();

				try {
					
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				
					Workspace wks = WorkspaceSerializer.load(new FileInputStream(selected));
					wks.setFileName(selected);
					Activator.getDefault().setWorkspace(wks);
					
					//XXX no more needed
					for(DatabaseCluster cluster : wks.getOpenedClusters()){
						if (cluster.getName() == null){
							Date date = cluster.getDate();
							SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy"); //$NON-NLS-1$
							
							cluster.setName(cluster.getProductName() + " " + selected+" ("+format.format(date)+")");	 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						}
					}
					
					/*
					 *open views 
					 */
					for(SchemaView v : wks.getOpenedSchemaViews()){
						try{
							SQLDesignEditorInput editorInput = new SQLDesignEditorInput(v);
							SQLDesignGraphicalEditor editor = (SQLDesignGraphicalEditor)page.openEditor(editorInput, SQLDesignGraphicalEditor.ID);

							editor.setSchemaLoaded(true);
		
							((ScalableRootEditPart) editor.getViewer()
									.getRootEditPart()).getZoomManager()
									.setZoom(v.getScale());

							editor.setName(v.getCluster().getProductName() + " - " + v.getName()); //$NON-NLS-1$
							editor.getViewer().setContents(v.getSchema());
						}catch(Exception ex){
							ex.printStackTrace();
						}
									
					}
					
					/*
					 * open datawarehouse views 
					 */
					for(DocumentSnapshot v : wks.getOpenedDocumentSnapshots()){
						try{
							SnapshotEditorInput editorInput = new SnapshotEditorInput(v);
							page.openEditor(editorInput, SnapshotEditor.ID);

						}catch(Exception ex){
							ex.printStackTrace();
						}
					}
					

				} catch (Exception e) {
					e.printStackTrace();
					MessageDialog.openError(window.getShell(), Messages.ApplicationActionBarAdvisor_34, Messages.ApplicationActionBarAdvisor_35 + selected + "\n" + e.getMessage());  //$NON-NLS-3$ //$NON-NLS-1$ //$NON-NLS-1$ //$NON-NLS-1$ //$NON-NLS-1$
				}

				addFileToList(selected);

				Activator.getDefault().getWorkbench()
						.getActiveWorkbenchWindow().getShell().setText(
								"BiDwhDesigner - " + selected); //$NON-NLS-1$

				close.setEnabled(true);
//				save.setEnabled(true);
				saveAll.setEnabled(true);
				saveAllAs.setEnabled(true);
			};
		};
		open.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("folder")); //$NON-NLS-1$
		open.setEnabled(true);

		close = new Action(Messages.ApplicationActionBarAdvisor_39) {
			@Override
			public void run() {

				Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(true);
				Activator.getDefault().getWorkspace().close();
				Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell().setText("BiDwhDesigner"); //$NON-NLS-1$

				Activator.getDefault().setWorkspace(new Workspace());
				
				close.setEnabled(true);
//				save.setEnabled(false);
				saveAll.setEnabled(true);
				saveAllAs.setEnabled(true);
			};
		};
		close.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("close")); //$NON-NLS-1$
		close.setEnabled(true);

		newDoc = new Action(Messages.ApplicationActionBarAdvisor_42) {
			@Override
			public void run() {
				Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell().setText("BiDwhDesigner " ); //$NON-NLS-1$

				
				Activator.getDefault().setWorkspace(new Workspace());
				close.setEnabled(true);
//				save.setEnabled(true);
				saveAll.setEnabled(true);
				saveAllAs.setEnabled(true);
			};
		};
		newDoc.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD));
		newDoc.setEnabled(true);

		about = ActionFactory.ABOUT.create(window);
		register(about);

		exit = ActionFactory.QUIT.create(window);
		register(exit);
		
//		importWiz = ActionFactory.IMPORT.create(window);
//		register(importWiz);
//		
//		exportWiz = ActionFactory.EXPORT.create(window);
//		register(exportWiz);

		exportImage = new Action(Messages.ApplicationActionBarAdvisor_2) {
			@Override
			public void run() {
				Shell shell = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
				FileDialog fd = new FileDialog(shell, SWT.SAVE);
				fd.setText(Messages.ApplicationActionBarAdvisor_45);
				fd.setFilterPath("C:/"); //$NON-NLS-1$
				fd.setFilterExtensions(new String[] { "*.png" }); //$NON-NLS-1$
				String selected = fd.open();

				if (SaveData.checkFile(selected)) {
					boolean erase = MessageDialog.openQuestion(	shell,Messages.ApplicationActionBarAdvisor_48,Messages.ApplicationActionBarAdvisor_49+ selected + Messages.ApplicationActionBarAdvisor_50);
					if (!erase){
						return;
					}
						
				}

				SQLDesignGraphicalEditor editor = (SQLDesignGraphicalEditor) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
				IFigure figure = ((ScalableRootEditPart) editor.getViewer().getRootEditPart()).getLayer(LayerConstants.PRINTABLE_LAYERS);

				Image img1 = new Image(null,
						new org.eclipse.swt.graphics.Rectangle(0, 0, figure
								.getBounds().width,
								figure.getBounds().height + 1));
				GC gc1 = new GC(img1);

				Graphics grap1 = new SWTGraphics(gc1);
				figure.paint(grap1);

				ImageLoader loader1 = new ImageLoader();
				loader1.data = new ImageData[] { img1.getImageData() };
				loader1.save(selected, SWT.IMAGE_PNG);
				img1.dispose();
				gc1.dispose();
			}
		};
		exportImage.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("png")); //$NON-NLS-1$

		search = new SearchTableAction();

//		preferences = ActionFactory.PREFERENCES.create(window);
//		register(preferences);
	}

	@Override
	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager fileMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_52,
				IWorkbenchActionConstants.M_FILE);
		fileMenu.add(newDoc);
		fileMenu.add(open);
		final MenuManager recentMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_53,
				IWorkbenchActionConstants.MB_ADDITIONS);
		fileMenu.add(recentMenu);
		fileMenu.add(new Separator());

		
		fileMenu.add(close);
//		fileMenu.add(save);
		fileMenu.add(saveAll);
		fileMenu.add(saveAllAs);
		fileMenu.add(new Separator());
		fileMenu.add(exportImage);
//		fileMenu.add(importWiz);
//		fileMenu.add(exportWiz);
//		fileMenu.add(new Separator());
		
//		fileMenu.add(preferences);
		fileMenu.add(new Separator());
		fileMenu.add(exit);

		
		

		
		 IPreferenceStore store = Activator.getDefault().getPreferenceStore();
	    	
	        String[] recent = new String[]{
	        		store.getString(PreferenceConstants.P_RECENTFILE1),
	        		store.getString(PreferenceConstants.P_RECENTFILE2),
	            	store.getString(PreferenceConstants.P_RECENTFILE3),
	            	store.getString(PreferenceConstants.P_RECENTFILE4),
	            	store.getString(PreferenceConstants.P_RECENTFILE5)
	        };
	        
	        for(String s : recent){
	        	if (!s.trim().equals("")){ //$NON-NLS-1$
	        		Action a = new ActionOpenWorkspace(s);
	        		a.setText(s);
	        		recentMenu.add(a);
	        	}
	        }
		

		menuBar.add(fileMenu);

		final MenuManager editMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_54,
				IWorkbenchActionConstants.M_EDIT);
		editMenu.add(search);
		editMenu.addMenuListener(new IMenuListener() {

			public void menuAboutToShow(IMenuManager manager) {
				IWorkbenchPage page = Activator.getDefault().getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				if (page != null) {
					SQLDesignGraphicalEditor editor = (SQLDesignGraphicalEditor) page
							.getActiveEditor();
					if (editor != null)
//						if (editor.getDropListener() != null)
							if (editor.getSchema() != null) {
								search.setEnabled(true);
								return;
							}
				}
				search.setEnabled(false);
			}

		});
		menuBar.add(editMenu);

		MenuManager helpMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_55,
				IWorkbenchActionConstants.M_HELP);
		helpMenu.add(about);
		menuBar.add(helpMenu);

	}

	
	
	private void addFileToList(String path){
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String[] list = {store.getString(PreferenceConstants.P_RECENTFILE1),
						store.getString(PreferenceConstants.P_RECENTFILE2),
						store.getString(PreferenceConstants.P_RECENTFILE3),
						store.getString(PreferenceConstants.P_RECENTFILE4),
						store.getString(PreferenceConstants.P_RECENTFILE5)};

		boolean isEverListed = false;
		for(int i=0;i<list.length;i++){
			if (list[i].equals(path))
				isEverListed = true;
		}

		if (!isEverListed){
			list[4] = list[3];
    		list[3] = list[2];
    		list[2] = list[1];
    		list[1] = list[0];
    		list[0] = path;
		}
		
		
		store.setValue(PreferenceConstants.P_RECENTFILE1, list[0]);
		store.setValue(PreferenceConstants.P_RECENTFILE2, list[1]);
		store.setValue(PreferenceConstants.P_RECENTFILE3, list[2]);
		store.setValue(PreferenceConstants.P_RECENTFILE4, list[3]);
		store.setValue(PreferenceConstants.P_RECENTFILE5, list[4]);
		
	}

	
	
	private class SaveWorkspaceRunnable implements IRunnableWithProgress{
		

			public void run(IProgressMonitor monitor)throws InvocationTargetException, InterruptedException {
				try{
					WorkspaceSerializer.save(new FileOutputStream( Activator.getDefault().getWorkspace().getFileName()), Activator.getDefault().getWorkspace(), monitor);
				}catch(Exception ex){
					throw new InvocationTargetException(ex, Messages.ApplicationActionBarAdvisor_56 + ex.getMessage());
				}
				
			}

	}
}
