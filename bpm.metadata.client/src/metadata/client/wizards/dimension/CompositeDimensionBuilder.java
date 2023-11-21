package metadata.client.wizards.dimension;

import java.util.List;

import metadata.client.i18n.Messages;
import metadata.client.trees.TreeDataStreamElement;
import metadata.client.viewer.DataSourceContentProvider;
import metadata.client.viewer.DataSourceLabelProvider;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.sql.SQLDataStreamElement;
import bpm.metadata.resource.complex.DimensionHelper;
import bpm.metadata.resource.complex.FmdtDimension;

public class CompositeDimensionBuilder extends Composite{

	private TreeViewer dataSourceViewer;	
	private TableViewer dimensionViewer;
	private TableViewer previewViewer;
	
	private FmdtDimension dimension;
	private SashForm sashcomposite;
	private Composite upperComposite;
	private Text name;
	
	private Button geolocalizable;
	
	private Transfer[] transfert = new Transfer[]{TextTransfer.getInstance()};
	public CompositeDimensionBuilder(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout());
		
		Composite c = new Composite(this, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label l  = new Label(c, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.CompositeDimensionBuilder_0);
		
		name = new Text(c, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		name.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				dimension.setName(name.getText());
				
			}
		});
		
		
		geolocalizable = new Button(c, SWT.CHECK);
		geolocalizable.setLayoutData(new GridData(GridData.FILL,GridData.CENTER, true, false, 2, 1 ));
		geolocalizable.setText(Messages.CompositeDimensionBuilder_1);
		geolocalizable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dimension.setGeolocalisable(geolocalizable.getSelection());
			}
		});
		
		sashcomposite = new SashForm(this, SWT.VERTICAL);
		sashcomposite.setLayout(new GridLayout());
		sashcomposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		upperComposite = new Composite(sashcomposite, SWT.NONE);
		upperComposite.setLayout(new GridLayout(2, true));
		upperComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		buildDataSourceViewer();
		buildDimensionViewer();
	}
	
	
	private void buildDataSourceViewer(){
		dataSourceViewer = new TreeViewer(upperComposite, SWT.BORDER);
		dataSourceViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		dataSourceViewer.setContentProvider(new DataSourceContentProvider());
		dataSourceViewer.setComparator(new ViewerComparator());
		dataSourceViewer.setLabelProvider(new DataSourceLabelProvider());
		dataSourceViewer.addFilter(new ViewerFilter() {
			
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof IDataStreamElement){
					return ((TreeDataStreamElement)element).getDataStreamElement().getType().getParentType() == IDataStreamElement.Type.DIMENSION;
				}
				return true;
			}
		});
		
		dataSourceViewer.addDragSupport(DND.DROP_COPY, transfert, new DragSourceListener() {
			
			public void dragStart(DragSourceEvent event) {
				event.dataType = TextTransfer.getInstance().getSupportedTypes()[0];
				IStructuredSelection ss = (IStructuredSelection)dataSourceViewer.getSelection();
				
				if (!(ss.getFirstElement() instanceof SQLDataStreamElement)){
					return;
				}
				event.doit = true;
				dragSetData(event);
			}
			
			public void dragSetData(DragSourceEvent event) {
				IStructuredSelection ss = (IStructuredSelection)dataSourceViewer.getSelection();
				if (! (ss.getFirstElement() instanceof IDataStreamElement)){
					return;
				}
				IDataStreamElement field = (IDataStreamElement)ss.getFirstElement();
				
				event.data = field.getDataStream().getName() + "." + field.getName(); //$NON-NLS-1$
				
			}
			
			public void dragFinished(DragSourceEvent event) {
				
				event.detail = DND.DROP_COPY;
			}
		});
	}
	
	private void buildDimensionViewer(){
		dimensionViewer = new TableViewer(upperComposite, SWT.BORDER);
		dimensionViewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		dimensionViewer.getTable().setLinesVisible(true);
		dimensionViewer.getTable().setHeaderVisible(true);
		dimensionViewer.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
			
			public void dispose() {}
			
			public Object[] getElements(Object inputElement) {
				List<?> l = ((FmdtDimension)inputElement).getLevels();
				return l.toArray(new Object[l.size()]);
			}
		});
		dimensionViewer.setLabelProvider(new DataSourceLabelProvider());
		
		
		TableViewerColumn colV = new TableViewerColumn(dimensionViewer, SWT.CENTER);
		colV.getColumn().setWidth(200);
		colV.getColumn().setText(Messages.CompositeDimensionBuilder_3);
		colV.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return ((IDataStreamElement)element).getName();
			}
		});
	
		dimensionViewer.addDropSupport(DND.DROP_COPY | DND.DROP_MOVE, transfert, new DropTargetListener() {
			
			public void dropAccept(DropTargetEvent event) {

			}
			
			public void drop(DropTargetEvent event) {
				IDataStream table = dimension.getDataSource().getDataStreamNamed(((String)event.data).split("\\.")[0]); //$NON-NLS-1$
				if (table == null){
					event.detail = DND.DROP_NONE;
					return;
				}
				
				IDataStreamElement col = table.getElementNamed(((String)event.data).substring(table.getName().length() + 1));
				if (col == null){
					event.detail = DND.DROP_NONE;
					return;
				}
				
				if (event.detail == DND.DROP_COPY){
					try{
						dimension.addLevel(col);
						dimensionViewer.refresh();
						buildPreview();
					}catch(Exception ex){
						event.detail = DND.DROP_NONE;
						return;
					}
				}
				else if (event.detail == DND.DROP_MOVE){
					IDataStreamElement col2 = (IDataStreamElement)((TableItem)event.item).getData();
					dimension.swapLevels(col, col2);
					dimensionViewer.refresh();
					buildPreview();
				}
				
				
			}
			
			public void dragOver(DropTargetEvent event) {
				
			}
			
			public void dragOperationChanged(DropTargetEvent event) {
				
			}
			
			public void dragLeave(DropTargetEvent event) {
			
			}
			
			public void dragEnter(DropTargetEvent event) {
				event.detail = event.operations;
			}
		});
	
	
		dimensionViewer.addDragSupport(DND.DROP_MOVE, transfert, new DragSourceListener() {
			
			public void dragStart(DragSourceEvent event) {
				event.dataType = TextTransfer.getInstance().getSupportedTypes()[0];
				IStructuredSelection ss = (IStructuredSelection)dimensionViewer.getSelection();
				
				if (!(ss.getFirstElement() instanceof SQLDataStreamElement)){
					return;
				}
				event.doit = true;
				dragSetData(event);
			}
			
			public void dragSetData(DragSourceEvent event) {
				IStructuredSelection ss = (IStructuredSelection)dimensionViewer.getSelection();
				IDataStreamElement field = (IDataStreamElement)ss.getFirstElement();
				
				event.data = field.getDataStream().getName() + "." + field.getName(); //$NON-NLS-1$
				
			}
			
			public void dragFinished(DragSourceEvent event) {
				
				event.detail = DND.DROP_MOVE;
			}
		});
	}

	
	
	public void setInput(FmdtDimension dimension) {
		this.dimension = dimension;
		dataSourceViewer.setInput(this.dimension.getDataSource());
		dimensionViewer.setInput(this.dimension);
		
		name.setText(dimension.getName());
		geolocalizable.setSelection(dimension.isGeolocalisable());
		if (!dimension.getLevels().isEmpty()){
			buildPreview();
		}
	}
	
	private void buildPreview(){
		if (previewViewer != null){
			previewViewer.getControl().dispose();
		}
		
		previewViewer = new TableViewer(sashcomposite, SWT.BORDER | SWT.FULL_SELECTION);
		previewViewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1));
		previewViewer.getTable().setHeaderVisible(true);
		previewViewer.getTable().setLinesVisible(true);
		previewViewer.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
			
			public void dispose() {}
			
			public Object[] getElements(Object inputElement) {
				List<?> l = (List<?>)inputElement;
				return l.toArray(new Object[l.size()]);
			}
		});
	
		
		for(IDataStreamElement e : dimension.getLevels()){
			TableViewerColumn col = new TableViewerColumn(previewViewer, SWT.CENTER);
			col.getColumn().setText(e.getOutputName());
			col.getColumn().setWidth(100);
			final int i = dimension.getLevels().indexOf(e);
			col.setLabelProvider(new ColumnLabelProvider(){
				@Override
				public String getText(Object element) {
					
					return ((List<Object>)element).get(i) + ""; //$NON-NLS-1$
				}
			});
		}
		
		sashcomposite.layout();
		
		try {
			previewViewer.setInput(new DimensionHelper().getValues("Default", dimension, 5)); //$NON-NLS-1$
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
	}
	
	

}
