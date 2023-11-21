package bpm.gateway.ui.palette.customizer.wizard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import bpm.gateway.ui.palette.customizer.Activator;
import bpm.gateway.ui.palette.customizer.icons.IconsNames;
import bpm.gateway.ui.palette.customizer.utils.PaletteEntry;


public class PaletteContentPage extends WizardPage{
	
	
	
	private TableViewer transfoViewer;
	private TreeViewer paletteViewer;
	private LinkedHashMap<String, List<PaletteEntry>> content = new LinkedHashMap<String, List<PaletteEntry>>();
	
	protected PaletteContentPage(String pageName) {
		super(pageName);
		
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, true));
		setControl(main);
		
		transfoViewer = new TableViewer(main, SWT.BORDER | SWT.V_SCROLL);
		transfoViewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 2));
		transfoViewer.getTable().setHeaderVisible(true);
	
		transfoViewer.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			public void dispose() {
				
				
			}
			
			public Object[] getElements(Object inputElement) {
				Collection c = (Collection)inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});
		transfoViewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((PaletteEntry)element).getEntryName();
			}
			
			@Override
			public Image getImage(Object element) {
				return Activator.getDefault().getImageRegistry().get(PaletteEntry.keyImages.get(((PaletteEntry)element).getTransformationClass()));
			}
			
		});
		transfoViewer.setSorter(new ViewerSorter());
		transfoViewer.addFilter(new ViewerFilter(){

			@Override
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				for(Collection<PaletteEntry> c : content.values()){
					for(PaletteEntry p : c){
						if (p.getTransformationClass() == ((PaletteEntry)element).getTransformationClass()){
							return false;
						}
					}
				}
				return true;
			}
			
		});
		ToolBar bar = new ToolBar(main, SWT.NONE);
		bar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		ToolItem add = new ToolItem(bar, SWT.PUSH);
		add.setToolTipText("New Folder");
		add.setText("Add");
		add.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.add));
		add.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				InputDialog d = new InputDialog(getShell(), "Create A Folder For the Palette", "Folder Name", "newFolder", null);
				
				if (d.open() == InputDialog.OK){
					String folderName = d.getValue();
					
					for(String s : content.keySet()){
						if (s.equals(folderName)){
							MessageDialog.openInformation(getShell(), "Creating Folder", "A folder with this name already exists.");
							return;
						}
					}
					content.put(folderName, new ArrayList<PaletteEntry>());
					paletteViewer.refresh();
				}
				
			}
		});
		
		final ToolItem del = new ToolItem(bar, SWT.PUSH);
		del.setToolTipText("Delete");
		del.setText("Del");
		del.setEnabled(false);
		del.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.delete));
		del.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)paletteViewer.getSelection();
				
				for(Object o : ss.toList()){
					for(String s : content.keySet()){
						if (content.get(s) != null){
							content.get(s).remove(o);
						}
						
					}
					
					for(String s : content.keySet()){
						if (s.equals(o)){
							content.remove(s);
							
						}
					}
				}
				
				
				
				paletteViewer.refresh();
				transfoViewer.refresh();
			}
		});
		
		paletteViewer = new TreeViewer(main, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
		paletteViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		paletteViewer.getTree().setHeaderVisible(true);
		paletteViewer.setAutoExpandLevel(2);
		paletteViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection)paletteViewer.getSelection();
				if (ss.isEmpty()){
					del.setEnabled(false);
				}
				else{
					del.setEnabled(true);
				}
				
			}
		});
		paletteViewer.setContentProvider(new ITreeContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			public void dispose() {
				
				
			}
			
			public Object[] getElements(Object inputElement) {
				HashMap<String, List<PaletteEntry>> model = (HashMap<String, List<PaletteEntry>>)inputElement;
				return model.keySet().toArray(new String[model.size()]);
			}
			
			public boolean hasChildren(Object element) {
				if (element instanceof String){
					return content.get(element).size() > 0;
				}
				return false;
			}
			
			public Object getParent(Object element) {
				if (element instanceof PaletteEntry){
					for(String k : content.keySet()){
						if (content.get(k) == element){
							return k;
						}
					}
				}
				return null;
			}
			
			public Object[] getChildren(Object parentElement) {
				if (parentElement instanceof String){
					List l = content.get(parentElement);
					return l.toArray(new Object[l.size()]);
				}
				return null;
			}
		});
		paletteViewer.setInput(content);
		
		TreeViewerColumn col  = new TreeViewerColumn(paletteViewer, SWT.LEFT);
		col.getColumn().setText("Folder Name");
		col.getColumn().setWidth(100);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if (element instanceof PaletteEntry){
					return "";
				}
				return super.getText(element);
			}
		});
		
		col  = new TreeViewerColumn(paletteViewer, SWT.LEFT);
		col.getColumn().setText("Transformation");
		col.getColumn().setWidth(100);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if (element instanceof PaletteEntry){
					return ((PaletteEntry)element).getTransformationClass().getSimpleName();
				}
				else{
					return "";
				}
				
			}
			
			@Override
			public Image getImage(Object element) {
				if (element instanceof PaletteEntry){
					return Activator.getDefault().getImageRegistry().get(PaletteEntry.keyImages.get(((PaletteEntry)element).getTransformationClass()));
				}
				else{
					return null;
				}
				
			}
		});
		
		
		col  = new TreeViewerColumn(paletteViewer, SWT.LEFT);
		col.getColumn().setText("Entry Name");
		col.getColumn().setWidth(100);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if (element instanceof PaletteEntry){
					return ((PaletteEntry)element).getEntryName();
				}
				else{
					return "";
				}
			}
		});
		
		col  = new TreeViewerColumn(paletteViewer, SWT.LEFT);
		col.getColumn().setText("Entry Description");
		col.getColumn().setWidth(100);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if (element instanceof PaletteEntry){
					return ((PaletteEntry)element).getEntryDescription();
				}
				else{
					return "";
				}
			}
		});
		
		init();
	}
	
	private void init(){
		List<PaletteEntry> l  = new ArrayList<PaletteEntry>();
		for(String k : PaletteEntry.defaultPalette.keySet()){
			l.addAll(PaletteEntry.defaultPalette.get(k));
		}
		transfoViewer.setInput(l);
		
		initDnD();
	}

	
	private void initDnD(){
		int operations = DND.DROP_COPY| DND.DROP_MOVE;
		Transfer[] transferTypes = new Transfer[]{TextTransfer.getInstance()};
		transfoViewer.addDragSupport(operations, transferTypes , new DragSourceListener(){

			public void dragFinished(DragSourceEvent event) {
				
				
			}

			public void dragSetData(DragSourceEvent event) {
				PaletteEntry entry = (PaletteEntry)((IStructuredSelection)transfoViewer.getSelection()).getFirstElement();
				
				event.data = entry.getTransformationClass().getName();
				
			}

			public void dragStart(DragSourceEvent event) {
				
			}
			
		});
		paletteViewer.addDropSupport(operations, transferTypes, new ViewerDropAdapter(paletteViewer){
			private String group;
			@Override
			public boolean performDrop(Object data) {
				for(Collection<PaletteEntry> c : content.values()){
					try{
						if (c.contains(Class.forName((String)data))){
							MessageDialog.openInformation(getShell(), "Add Palette entry", "This entry already exists in another group");
							return false;
						}
					}catch(Exception ex){
						
					}
					
				}
				if (content.get(group) == null){
					content.put(group, new ArrayList<PaletteEntry>());
				}
				PaletteEntry entry = null;
				for(List<PaletteEntry> l : PaletteEntry.defaultPalette.values()){
					for(PaletteEntry e : l){
						if (e.getTransformationClass().getName().equals(data)){
							entry = new PaletteEntry(e);
						}
					}
					
				}
				if (entry != null){
					content.get(group).add(entry);
				}
				getContainer().updateButtons();
				transfoViewer.refresh();
				paletteViewer.refresh();
				return true;
				
			}

			@Override
			public void drop(DropTargetEvent event) {
				group = (String) determineTarget(event);
				super.drop(event);
			}
			@Override
			public boolean validateDrop(Object target, int operation,
					TransferData transferType) {
				
				return target != null;
			}
			@Override
			public void dropAccept(DropTargetEvent event) {
				if (this.determineLocation(event) == 3){
					super.dropAccept(event);
				}
				
				
			}
			
		});
	}
	
	public HashMap<String, List<PaletteEntry>> getMap(){
		return content;
	}
	
	@Override
	public boolean isPageComplete() {
		return !content.isEmpty();
	}
}
