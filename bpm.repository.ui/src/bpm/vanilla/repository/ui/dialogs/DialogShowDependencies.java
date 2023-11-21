package bpm.vanilla.repository.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.repository.ui.Messages;
import bpm.vanilla.repository.ui.viewers.RepositoryLabelProvider;

public class DialogShowDependencies extends Dialog {

	public static final String TITLE = Messages.DialogShowDependencies_0;
	public static final String UPDATE_TITLE = Messages.DialogShowDependencies_1;
	public static final String DELETE_TITLE = Messages.DialogShowDependencies_2;
	
	private RepositoryItem selectedItem;
	
	private TreeViewer tree;
	
	private IRepositoryApi sock;
	
	private List<TreeDirectoryItem> input;
	
	private String title;
	
	public DialogShowDependencies(Shell parentShell, RepositoryItem item, IRepositoryApi sock, String title) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		
		this.selectedItem = item;
		this.sock = sock;
		this.title = title;
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setText(title);
		getShell().setSize(600, 500);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		tree = new TreeViewer(main, SWT.BORDER);
		tree.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		tree.setLabelProvider(new RepositoryLabelProvider() {
			@Override
			public String getText(Object element) {
				RepositoryItem item = ((TreeDirectoryItem)element).getItem();
				return super.getText(item);
			}
			
			@Override
			public Image getImage(Object element) {
				RepositoryItem item = ((TreeDirectoryItem)element).getItem();
				return super.getImage(item);
			}
		});
		
		tree.setContentProvider(new ITreeContentProvider() {
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
			}
			
			@Override
			public void dispose() {
				
			}
			
			@Override
			public Object[] getElements(Object inputElement) {
				return ((List<TreeDirectoryItem>)inputElement).toArray(new Object[((List<TreeDirectoryItem>)inputElement).size()]);
			}
			
			@Override
			public boolean hasChildren(Object element) {
				return ((TreeDirectoryItem)element).getChildren() != null && ((TreeDirectoryItem)element).getChildren().size() > 0;
			}
			
			@Override
			public Object getParent(Object element) {
				return ((TreeDirectoryItem)element).getParent();
			}
			
			@Override
			public Object[] getChildren(Object parentElement) {
				return ((TreeDirectoryItem)parentElement).getChildren().toArray(new Object[((TreeDirectoryItem)parentElement).getChildren().size()]);
			}
		});
		
		try {
			if(input == null) {
				hasDependencies();
			}
			tree.setInput(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return main;
	}

	public boolean hasDependencies() throws Exception {
		
		IVanillaAPI api = new RemoteVanillaPlatform(sock.getContext().getVanillaContext());
		Repository rep = sock.getContext().getRepository();
		
		
		List<RepositoryItem> items = sock.getRepositoryService().getDependantItems(selectedItem) ;
		
		List<TreeDirectoryItem> result = new ArrayList<TreeDirectoryItem>();
		
		for(RepositoryItem it : items) {
			TreeDirectoryItem tdi = new TreeDirectoryItem(it);
			result.add(tdi);
			tdi.setChildren(getChilds(it));
		}
		
		if(result.size() > 0) {
			input = result;
			return true;
		}
		return false;
	}
	
	private List<TreeDirectoryItem> getChilds(RepositoryItem parent) throws Exception {
		List<RepositoryItem> items = sock.getRepositoryService().getDependantItems(parent);
		List<TreeDirectoryItem> result = new ArrayList<TreeDirectoryItem>();
		
		for(RepositoryItem it : items) {
			TreeDirectoryItem tdi = new TreeDirectoryItem(it);
			result.add(tdi);
			tdi.setChildren(getChilds(it));
		}
		
		return result;
	}
	
	private class TreeDirectoryItem {
		
		private RepositoryItem item;
		private List<TreeDirectoryItem> children;
		private TreeDirectoryItem parent;
		
		public TreeDirectoryItem(RepositoryItem item) {
			this.item = item;
		}
		
		public RepositoryItem getItem() {
			return item;
		}

		public void addChild(TreeDirectoryItem child) {
			if(children == null) {
				children = new ArrayList<TreeDirectoryItem>();
			}
			children.add(child);
		}
		
		public List<TreeDirectoryItem> getChildren() {
			return children;
		}

		public void setChildren(List<TreeDirectoryItem> children) {
			this.children = children;
		}

		public TreeDirectoryItem getParent() {
			return parent;
		}

		public void setParent(TreeDirectoryItem parent) {
			this.parent = parent;
		}
		
	}
}
