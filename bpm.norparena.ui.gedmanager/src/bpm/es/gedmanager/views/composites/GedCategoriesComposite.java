package bpm.es.gedmanager.views.composites;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

import bpm.es.gedmanager.Activator;
import bpm.es.gedmanager.Messages;
import bpm.es.gedmanager.api.GedModel;
import bpm.es.gedmanager.dialogs.DialogAddCategory;
import bpm.es.gedmanager.icons.IconsNames;
import bpm.vanilla.platform.core.beans.ged.Category;
import bpm.vanilla.platform.core.beans.ged.GedDocument;

public class GedCategoriesComposite {
	private Composite parent;
	
	private TreeViewer categoryViewer;

	private GedModel model;

	public GedCategoriesComposite(Composite par, GedModel mod) {
		this.parent = par;
		this.model = mod;
		
		Composite buttonBar = new Composite(par, SWT.NONE);
		buttonBar.setLayout(new GridLayout(3, false));
		buttonBar.setLayoutData(new GridData());
		
		Button bAdd = new Button(buttonBar, SWT.PUSH);
		bAdd.setText(Messages.GedCategoriesComposite_0);
		bAdd.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.category_add));
		bAdd.setLayoutData(new GridData());
		bAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					StructuredSelection ss = (StructuredSelection) categoryViewer.getSelection();
					Object o = ss.getFirstElement();
					
					Category cat = null;
					
					if (o instanceof Category) {
						cat = (Category) o;
					}
					
					DialogAddCategory addCat = new DialogAddCategory(Display.getDefault().getActiveShell(),
							cat);
					
					Category newCat = null;
					if (addCat.open() == Dialog.OK) {
						newCat = addCat.getCategory();
					}
					
					if (newCat != null) {
						model.addCategory(newCat);
					}
					
					
					showData(model);
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageDialog.openError(Display.getDefault().getActiveShell(), 
							Messages.GedCategoriesComposite_2, "" + ex.getMessage()); //$NON-NLS-2$
				}
			}
		});
		Button bDel = new Button(buttonBar, SWT.PUSH);
		bDel.setText(Messages.GedCategoriesComposite_4);
		bDel.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.category_delete));
		bDel.setLayoutData(new GridData());
		bDel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					StructuredSelection ss = (StructuredSelection) categoryViewer.getSelection();
					Object o = ss.getFirstElement();
					
					if (o instanceof Category) {
						model.deleteCategory((Category) o);
						
						showData(model);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageDialog.openError(Display.getDefault().getActiveShell(), 
							Messages.GedCategoriesComposite_5, "" + ex.getMessage()); //$NON-NLS-2$
				}
			}
		});
		
		Button bDelDoc = new Button(buttonBar, SWT.PUSH);
		bDelDoc.setText(Messages.GedCategoriesComposite_7);
		bDelDoc.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.document_delete));
		bDelDoc.setLayoutData(new GridData());
		bDelDoc.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					StructuredSelection ss = (StructuredSelection) categoryViewer.getSelection();
					Object o = ss.getFirstElement();
					
					if (o instanceof GedDocument) {
						GedDocument ddef = (GedDocument) o;
						model.delDocCat(ddef.getId(), ((GedDocument) ddef).getCategoryId());
						
						showData(model);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageDialog.openError(Display.getDefault().getActiveShell(), 
							Messages.GedCategoriesComposite_1, "" + ex.getMessage()); //$NON-NLS-2$
				}
			}
		});

		categoryViewer = new TreeViewer(parent, SWT.SCROLL_LINE | SWT.BORDER | SWT.MULTI);
		
		categoryViewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		categoryViewer.setContentProvider(new ITreeContentProvider() {
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
			
			public void dispose() {
			}
			
			public Object[] getElements(Object inputElement) {
				return getChildren(inputElement);
			}
			
			public boolean hasChildren(Object element) {
				if (element instanceof Category) {
					return true;
				}
				return false;
			}
			
			public Object getParent(Object element) {
				return null;
			}
			
			public Object[] getChildren(Object parentElement) {
				if (parentElement instanceof GedModel) {
					GedModel model = (GedModel) parentElement;
					try {
						return model.getCategories().toArray(new Object[0]);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (parentElement instanceof Category) {
					Category cat = (Category) parentElement;
					try {
						return model.getDocumentsForCategory(cat).toArray(new Object[0]);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return null;
			}
		});
		
		categoryViewer.setLabelProvider(new LabelProvider() {
			
			public Image getImage(Object element) {
				if (element instanceof Category) {
					return Activator.getDefault().getImageRegistry().get(IconsNames.category);
				}
				else if (element instanceof GedDocument) {
//					GedDocument doc = (GedDocument) element;
//					
//					if (doc.getFormat().endsWith("pdf")) //$NON-NLS-1$
//						return Activator.getDefault().getImageRegistry().get(IconsNames.document_pdf);
//					else if (doc.getFormat().endsWith("txt") || //$NON-NLS-1$
//							doc.getFormat().endsWith("doc") || //$NON-NLS-1$
//							doc.getFormat().endsWith("odt")) //$NON-NLS-1$
//						return Activator.getDefault().getImageRegistry().get(IconsNames.document_word);
//					else if (doc.getFormat().endsWith("xls") || //$NON-NLS-1$
//							doc.getFormat().endsWith("ods")) //$NON-NLS-1$
//						return Activator.getDefault().getImageRegistry().get(IconsNames.document_excel);
//					else if (doc.getFormat().endsWith("zip") || //$NON-NLS-1$
//							doc.getFormat().endsWith("rar")) //$NON-NLS-1$
//						return Activator.getDefault().getImageRegistry().get(IconsNames.document_compressed);
//					else
						return Activator.getDefault().getImageRegistry().get(IconsNames.document);
				}
				else
					return null;
			}
			public String getText(Object element) {
				if (element instanceof Category) {
					return ((Category)element).getName();
				}
				else if (element instanceof GedDocument) {
					return ((GedDocument)element).getName();
				}
				else 
					return "unknown"; //$NON-NLS-1$
			}
		});

//		TreeViewerEditor.create(categoryViewer, new NameEditorActivationStrategy(categoryViewer), 
//				ColumnViewerEditor.DEFAULT);
		TreeViewerEditor.create(categoryViewer, 
			new ColumnViewerEditorActivationStrategy(categoryViewer), ColumnViewerEditor.TABBING_CYCLE_IN_ROW);
//		categoryViewer.getColumnViewerEditor();
//		//categoryViewer.setColumnViewerEditor(new )
//		categoryViewer.getTree().getColumn(0).se
		setupDND();
		
		categoryViewer.setInput(model);
		
		
	}
	
	private void setupDND() {
	    Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
	    DragSource source = new DragSource(categoryViewer.getControl(), DND.DROP_COPY);
	    source.setTransfer(types);

	    
	    DropTarget target = new DropTarget(categoryViewer.getControl(), DND.DROP_COPY);
	    target.setTransfer(types);
	    target.addDropListener(new DropTargetAdapter() {
		    public void dragEnter(DropTargetEvent event) {
		    	event.detail = DND.DROP_COPY;
		    }
	
		    public void dragOver(DropTargetEvent event) {
		    	  
		    }
	      
		    public void drop(DropTargetEvent event) {
		    	StructuredSelection ss = (StructuredSelection) categoryViewer.getSelection();
		    	Object o = ss.getFirstElement();
			    	  
			    if (event.item.getData() instanceof Category) {
			    	Category category = (Category)event.item.getData();
			    	ActionAddDocCat dc = new ActionAddDocCat(Activator.getDefault().getDndObject(), 
			    			category, model);
			    	
		    		try {	
		    			IProgressService service = PlatformUI.getWorkbench().getProgressService();
						service.run(true, false, dc);			
						
						showData(model);
		    		} catch (Exception e) {
						MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.GedCategoriesComposite_20, " " + e.getMessage()); //$NON-NLS-2$
					} finally {
					
					}
			    }
		    }
	    });
	}
	
	public class ActionAddDocCat implements IRunnableWithProgress {
		private List<Object> objs;
		private Category category;
		private GedModel model;
		
		public ActionAddDocCat(List<Object> objs, Category category, GedModel model) {
			this.category = category;
			this.objs = objs;
			this.model = model;
		}
		
		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			try {
				monitor.beginTask("Adding " + objs.size() + " document(s) to category", objs.size() + 1); //$NON-NLS-1$ //$NON-NLS-2$
				for (Object obj : objs) {
					if (obj instanceof GedDocument) {
						monitor.subTask("Adding " + ((GedDocument) obj).getName()); //$NON-NLS-1$
						model.addDocCat(category, (GedDocument) obj);
						monitor.worked(1);
					}
				}
				monitor.subTask(Messages.GedCategoriesComposite_25);
				
				monitor.worked(1);
				showData(model);
			} catch (Exception e) {
				new InvocationTargetException(e, "error while linking document/category on server"); //$NON-NLS-1$
			}
		}
	}
	
	public void showData(GedModel model) {
		categoryViewer.setInput(null);
		categoryViewer.setInput(model);
	}
	
}
