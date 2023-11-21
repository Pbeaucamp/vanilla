package bpm.gateway.ui.composites;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.transformations.SortElement;
import bpm.gateway.core.transformations.SortTransformation;
import bpm.gateway.ui.i18n.Messages;

public class SortComposite extends Composite {

	private CheckboxTableViewer viewer;
	
	private static final String[] SORTS = new String[]{"ASC", "DESC"}; //$NON-NLS-1$ //$NON-NLS-2$
	private SortTransformation transfo;
	
	
	public SortComposite(Composite parent, int style) {
		super(parent, style);
		buildContent();
		setDnd();
	}
	
	
	public void setInput(SortTransformation transfo) throws Exception{
		this.transfo = transfo;
		
		viewer.setInput(transfo.getDescriptor(transfo).getStreamElements());	
		
		for(SortElement se : transfo.getSorts()){
			for(StreamElement e : (List<StreamElement>)viewer.getInput()){
				if(se.getColumnSort().equals(e.getFullName())){
					viewer.setChecked(e, true);
					break;
				}
			}
		}
		viewer.refresh();
	}
	
	
	private void buildContent(){
		this.setLayout(new GridLayout());
		
		viewer = CheckboxTableViewer.newCheckList(this, SWT.BORDER  | SWT.FULL_SELECTION);
		viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<StreamElement> st = (List<StreamElement>)inputElement;
				return st.toArray(new StreamElement[st.size()]);
			}

			public void dispose() { }

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }
			
		});
		viewer.setLabelProvider(new ITableLabelProvider(){

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				switch(columnIndex){
				case 0:
					return ((StreamElement)element).name;

				case 1:
					
					try{
						StreamElement el = (StreamElement)element;
						for(SortElement e : transfo.getSorts()){
							if (e.getColumnSort().equals(el.originTransfo + "::" + el.name)){ //$NON-NLS-1$
								if (e.isType()){
									return "ASC"; //$NON-NLS-1$
								}
								else{
									return "DSC"; //$NON-NLS-1$
								}
							}
						}
					}catch(Exception e){
						e.printStackTrace();
					}
					
				}
				return null;
			}

			public void addListener(ILabelProviderListener listener) { }

			public void dispose() { }

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener listener) { }
			
		});
		viewer.addCheckStateListener(new ICheckStateListener(){

			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getChecked()){
					transfo.setOrder((StreamElement)event.getElement(), true);
				}
				else{
					transfo.removeOrder((StreamElement)event.getElement());
				}
				viewer.refresh();
			}
			
		});
		viewer.setSorter(new ViewerSorter(){

			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				StreamDescriptor desc;
				try {
					desc = transfo.getDescriptor(transfo);
				} catch (ServerException e) {
					return 0;
					
				}
				
				SortElement s1 = null;
				SortElement s2 = null;
				
				for(SortElement s : transfo.getSorts()){
					if (s.getColumnSort().equals(((StreamElement)e1).getFullName())){
						s1 = s;
					}
					if (s.getColumnSort().equals(((StreamElement)e2).getFullName())){
						s2 = s;
					}
				}
				
				if (s1 == null && s2 == null){
					return new Integer(desc.getStreamElements().indexOf(e1)).compareTo(desc.getStreamElements().indexOf(e2)); 
				}
				else if (s1 != null  && s2 != null){
					return new Integer(transfo.getSorts().indexOf(s1)).compareTo(transfo.getSorts().indexOf(s2)); 
				}
				else if (s1 == null){
					return 1;
				}
				else if (s2 == null){
					return -1;
				}
				
				return super.compare(viewer, e1, e2);
			}
			
		});
		
		
		TableColumn typeCol = new TableColumn(viewer.getTable(), SWT.NONE);
		typeCol.setText(Messages.SortComposite_4);
		typeCol.setWidth(500);
		
		TableColumn firstCol = new TableColumn (viewer.getTable(), SWT.NONE);
		firstCol.setText(Messages.SortComposite_5);
		firstCol.setWidth(200);
		
		viewer.setColumnProperties(new String[]{"name", "sort"}); //$NON-NLS-1$ //$NON-NLS-2$
		viewer.setCellModifier(new SortingCellModifier());
	
		viewer.getTable().setHeaderVisible(true);
		
		viewer.setCellEditors(new CellEditor[]{
				new TextCellEditor(viewer.getTable()),
				new ComboBoxCellEditor(viewer.getTable(), SORTS)});

		
	}
	
	private class SortingCellModifier implements ICellModifier{

		public boolean canModify(Object element, String property) {
			return property.equals("sort"); //$NON-NLS-1$
			
		}

		public Object getValue(Object element, String property) {
			if (property.equals("name")){ //$NON-NLS-1$
				return ((StreamElement)element).name;
				
			}
			
			else if (property.equals("sort")){ //$NON-NLS-1$
				try{
					StreamElement el = (StreamElement)element;
					for(SortElement e : transfo.getSorts()){
						if (e.getColumnSort().equals(el.getFullName())){
							if (e.isType()){
								return 0;
							}
							else{
								return 1;
							}
						}
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				
				
			}
			
			return null;
		}

		public void modify(Object element, String property, Object value) {
			if (property.equals("sort")){ //$NON-NLS-1$
				if (value.equals(0)){
					transfo.setOrder((StreamElement)((TableItem)element).getData(), true);
				}
				else{
					transfo.setOrder((StreamElement)((TableItem)element).getData(), false);
				}
			}
			
			viewer.refresh();
			
		}
		
	}
	
	private void setDnd(){
		int ops = DND.DROP_COPY;
		Transfer[] transfers = new Transfer[] { TextTransfer.getInstance()};
		
		viewer.addDragSupport(ops, transfers, new DragSourceListener(){

			public void dragFinished(DragSourceEvent event) { }

			public void dragSetData(DragSourceEvent event) {
				IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
				
				if (viewer.getChecked(ss.getFirstElement())){
					event.data = "TSF" +  ((List<StreamElement>)viewer.getInput()).indexOf(ss.getFirstElement()); //$NON-NLS-1$
				}
				
			}

			public void dragStart(DragSourceEvent event) { }
			
		});
		
		viewer.addDropSupport(ops, transfers, new DropTargetListener(){

			public void dragEnter(DropTargetEvent event) {
				event.detail = DND.DROP_COPY;
				
			}

			public void dragLeave(DropTargetEvent event) { }

			public void dragOperationChanged(DropTargetEvent event) { }

			public void dragOver(DropTargetEvent event) { }

			public void drop(DropTargetEvent event) {
				Object o = ((TableItem) event.item).getData();
				int source = Integer.parseInt(((String)event.data).substring(3));
				
				if (!viewer.getChecked(o)){
					return;
					
				}
				
				int target = ((List<StreamElement>)viewer.getInput()).indexOf(o);
				
				try {
					transfo.swapOrders(source, target);
				} catch (Exception e) {
					e.printStackTrace();
					MessageDialog.openInformation(getShell(), "Error", e.getMessage()); //$NON-NLS-1$
				}
				
				viewer.refresh();
				
			}

			public void dropAccept(DropTargetEvent event) { }
			
		});
	}
}
