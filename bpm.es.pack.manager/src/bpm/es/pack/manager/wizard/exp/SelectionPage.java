package bpm.es.pack.manager.wizard.exp;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import bpm.es.pack.manager.provider.RepositoryContentProvider;
import bpm.es.pack.manager.provider.RepositoryLabelProvider;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class SelectionPage extends WizardPage {

	private Repository repository;

	private CheckboxTreeViewer viewer;

	private boolean exportToVanillaPlace = false;

	protected SelectionPage(String pageName, Repository repository) {
		super(pageName);
		this.repository = repository;
	}

	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new FillLayout());

		createPageContent(mainComposite);

		setControl(mainComposite);
		setPageComplete(true);
	}

	private void createPageContent(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout());

		viewer = new CheckboxTreeViewer(c, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		viewer.setContentProvider(new RepositoryContentProvider());
		viewer.setLabelProvider(new RepositoryLabelProvider());
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				final IStructuredSelection ss =  (IStructuredSelection)event.getSelection();
				if (ss.isEmpty() || !(ss.getFirstElement() instanceof RepositoryDirectory)){
					return;
				}
				
				for(Object o : ss.toList()){
					if (!(o instanceof RepositoryDirectory)){
						continue;
					}
					
					RepositoryDirectory dir = (RepositoryDirectory)o;
					((RepositoryContentProvider)viewer.getContentProvider()).getChildren(dir);
				}
				
//				BusyIndicator.showWhile(Display.getDefault(), new Runnable(){
//					public void run(){
//						for(Object o : ss.toList()){
//							if (!(o instanceof RepositoryDirectory)){
//								continue;
//							}
//							
//							RepositoryDirectory dir = (RepositoryDirectory)o;
//							((RepositoryContentProvider)viewer.getContentProvider()).getChildren(dir);
//
////							viewer.refresh(dir);
//						}
//					}
//				});
			}
		});
		viewer.addCheckStateListener(new ICheckStateListener() {

			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getElement() instanceof RepositoryDirectory) {
					try {
						RepositoryDirectory dir = (RepositoryDirectory)event.getElement();
//						if((dir.getDirectories() == null || dir.getDirectories().isEmpty()) && (dir.getItems() == null || dir.getItems().isEmpty())){
							viewer.setSelection(new StructuredSelection(event.getElement()), true);
//						}
						
						Object[] items = ((RepositoryContentProvider)viewer.getContentProvider()).getChildren(event.getElement());
						checkChilds(items, event.getChecked());

//						viewer.refresh();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				getContainer().updateButtons();
			}
		});

		viewer.setInput(repository);
	}

	private void checkChilds(Object[] items, boolean checked) throws Exception {
		for(Object item : items){
			
			if(item instanceof RepositoryDirectory){
				RepositoryDirectory dir = (RepositoryDirectory)item;
//				if((dir.getDirectories() == null || dir.getDirectories().isEmpty()) && (dir.getItems() == null || dir.getItems().isEmpty())){
					viewer.setSelection(new StructuredSelection(item), true);
//				}
				
				Object[] newItems = ((RepositoryContentProvider)viewer.getContentProvider()).getChildren(item);
				checkChilds(newItems, checked);
			}
			viewer.setChecked(item, checked);
		}
	}

	@Override
	public boolean isPageComplete() {
		if (exportToVanillaPlace) {
			return true;
		}
		else {
			return viewer.getCheckedElements().length > 0;
		}
	}

	public List<RepositoryItem> getSelectedItems() {
		List<RepositoryItem> items = new ArrayList<RepositoryItem>();
		for (Object element : viewer.getCheckedElements()) {
			if (element instanceof RepositoryItem) {
				items.add((RepositoryItem) element);
			}
		}
		return items;
	}

	public List<RepositoryDirectory> getSelectedDirectories() {
		List<RepositoryDirectory> dirs = new ArrayList<RepositoryDirectory>();
		for (Object element : viewer.getCheckedElements()) {
			if (element instanceof RepositoryDirectory) {
				dirs.add((RepositoryDirectory) element);
			}
		}
		return dirs;
	}

	@Override
	public IWizardPage getNextPage() {
		// we set the information for the export options
		//		
		// ExportDetails infos = ((ExportWizard)getWizard()).infos;
		// infos.purgeItems();
		//		
		// for(Object o : viewer.getCheckedElements()){
		//
		// if (o instanceof TreeItem){
		//				String path = ""; //$NON-NLS-1$
		//				
		// TreeObject current = (TreeItem)o;
		// while (current.getParent() instanceof TreeDirectory){
		// current = current.getParent();
		//					path = current.getName() + "/" + path; //$NON-NLS-1$
		// }
		// infos.addItem(((TreeItem)o).getItem(), path);
		// }
		// }
		//		
		return super.getNextPage();
	}

	@Override
	protected boolean isCurrentPage() {
		return super.isCurrentPage();
	}
}
