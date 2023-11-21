package bpm.mdm.ui.model.composites;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.eclipse.ui.statushandlers.StatusManager;

import bpm.mdm.model.Attribute;
import bpm.mdm.model.Entity;
import bpm.mdm.model.Rule;
import bpm.mdm.model.rules.EntityLinkRule;
import bpm.mdm.model.rules.impl.EntityLinkRuleImpl;
import bpm.mdm.model.runtime.Row;
import bpm.mdm.model.runtime.RuntimeFactory;
import bpm.mdm.model.runtime.exception.AbstractRowException;
import bpm.mdm.model.runtime.exception.RowsExceptionHolder;
import bpm.mdm.model.runtime.exception.AbstractRowException.OperationType;
import bpm.mdm.model.storage.EntityStorageStatistics;
import bpm.mdm.model.storage.IEntityStorage;
import bpm.mdm.remote.MdmRemote;
import bpm.mdm.remote.StoreReader;
import bpm.mdm.ui.Activator;
import bpm.mdm.ui.i18n.Messages;
import bpm.mdm.ui.icons.IconNames;

public class DialogBrowseEntity extends Dialog{
	private static final Color RED = new Color(Display.getDefault(), 255, 0 , 0);
	private static final Font DIRTY_FONT = 	new Font(Display.getDefault(), new FontData("Arial", 10, SWT.BOLD | SWT.ITALIC)); //$NON-NLS-1$
	private class ColLableProvider extends ColumnLabelProvider{
		private Attribute att;
		private boolean usePicture = false;
		ColLableProvider(Attribute att){
			this.att = att;
			
		}
		ColLableProvider(Attribute att, boolean usePicture){
			this.att = att;
			this.usePicture = usePicture;
		}
		
		@Override
		public String getText(Object element) {
			Object value = ((Row)element).getValue(att);
			if (value == null){
				return ""; //$NON-NLS-1$
			}
			
			return value + ""; //$NON-NLS-1$
		}
		
		@Override
		public Color getBackground(Object element) {
			Object value = ((Row)element).getValue(att);
			
			for(Rule r : att.getRules()){
				if (r instanceof EntityLinkRule){
					//lookinfor n
					if (((EntityLinkRuleImpl)r).getEntityReader()== null){
						Entity ent = null;
						for(Entity e : Activator.getDefault().getMdmProvider().getModel().getEntities()){
							for(Attribute a : e.getAttributes()){
								if (a == ((EntityLinkRuleImpl)r).getLinkedAttribute()){
									ent = e;
									break;
								}
							}
							if (ent != null){
								break;
							}
						}
						if (ent != null){
							StoreReader  reader = ((MdmRemote)Activator.getDefault().getMdmProvider()).createStoreReader(ent);
							((EntityLinkRuleImpl)r).setEntityReader(reader);
						}
						
					}
				}
				if (r.isActive() && !r.evaluate(value)){
					return RED;
				}
			}
			return super.getBackground(element);
		}
		
		@Override
		public Font getFont(Object element) {
			if (dirtyObject.contains(element)){
				return DIRTY_FONT;
			}
			return super.getFont(element);
		}
		

		@Override
		public Image getImage(Object element) {
			if (!usePicture){
				return super.getImage(element);
			}
			OperationType type = store.getType((Row)element);
			if (type == null){
				return super.getImage(element);
			}
			switch(type){
			case CREATE:
				return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ADD);
			case UPDATE:
				return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_PRINT_EDIT);
			case DELETE:
				return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_DELETE);
			default:
				return super.getImage(element);
			}
			
		}
	}

	private class AttributeCellEditorValidator implements ICellEditorValidator{

		private Attribute att;
		AttributeCellEditorValidator(Attribute att){
			this.att = att;
		}
		@Override
		public String isValid(Object value) {
			if ((value == null || value.equals("")) && att.isNullAllowed()){ //$NON-NLS-1$
				return Messages.DialogBrowseEntity_0;
			}
				
			for(Rule r : att.getRules()){
				
				if (r.isActive() && !r.evaluate(value)){
					return Messages.DialogBrowseEntity_4 + r.getName() + Messages.DialogBrowseEntity_5;
				}
			}
			if (value != null){
				try {
					
					att.getDataType().convertFromString((String)value);
					return null;
				} catch (Exception e) {
					return Messages.DialogBrowseEntity_1 + value + Messages.DialogBrowseEntity_2 + att.getDataType().getName();
				}
			}
			
			return null;
		}
		
	}
	
	
	private static class _Comparator extends ViewerComparator{
		Attribute a;
		_Comparator(Attribute a){
			this.a = a;
		}
		
		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			Comparable value = (Comparable)((Row)e1).getValue(a);
			Comparable v2 = (Comparable)((Row)e2).getValue(a);
			
			if (value  != null && v2 != null){
				return value.compareTo(v2);
			}
			else if (value == null){
				if (v2 == null){
					return 0;
				}
				else {
					return 1;
				}
			}
			
			else{
				return v2 == null ? 0 : -1;
			}
			
		}
	}
	
	private List<Object> dirtyObject = new ArrayList<Object>();
	
	
	public class RowEditSupport extends EditingSupport{
		private Attribute attribute;
		private TextCellEditor editor;
		
		public RowEditSupport(ColumnViewer viewer, Attribute attribute) {
			super(viewer);
			this.attribute = attribute;
			editor = new TextCellEditor((Composite)viewer.getControl());
			editor.setValidator(new AttributeCellEditorValidator(attribute));
			editor.addListener(new ICellEditorListener() {
				
				@Override
				public void editorValueChanged(boolean oldValidState, boolean newValidState) {
					setErrorMessage();
				}
				
				@Override
				public void cancelEditor() {
					setErrorMessage();
					
				}
				
				@Override
				public void applyEditorValue() {
					setErrorMessage();
					
				}
			});
		}

		private void setErrorMessage(){
			
			editor.getControl().setBackground(editor.getErrorMessage() == null ? null : RED);
		}
		@Override
		protected boolean canEdit(Object element) {
			if (Activator.getDefault().getControler().isDirty()){
				return false;
			}
			
			for(Attribute a : entity.getAttributes()){
				if (a != attribute && columnsLabelProvider.get(a).getBackground(element) != null){
					return store.getType((Row)element) == OperationType.CREATE ;
				}
			}
			return (attribute.isId() ? store.getType((Row)element) == OperationType.CREATE : true);
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}

		@Override
		protected Object getValue(Object element) {
			Object value = ((Row)element).getValue(attribute);
			
			if (value == null){
				return ""; //$NON-NLS-1$
			}
			return value.toString();
		}

		@Override
		protected void setValue(Object element, Object value) {
			
			Object curVal = ((Row)element).getValue(attribute);
			if ((curVal == null && value != null) || 
					(curVal != null && value == null) || 
					(curVal !=value && !curVal.toString().equals(value.toString()))){
				if (editor.getErrorMessage() != null){
					return;
				}
				if (!dirtyObject.contains(element)){
					dirtyObject.add(element);
					try {
						store.updateRow(((Row)element));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				((Row)element).setValue(attribute, value);
				getViewer().refresh();
				dirtyViewer.refresh();
			}
			
		}
		
	}
	
	
	private class ErrorContentProvider implements ITreeContentProvider{
		private HashMap<Attribute, List<String>> errors;
		@Override	
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof Attribute){
				List l = errors.get(parentElement);
				return l.toArray(new Object[l.size()]);
			}
			return null;
		}

		@Override
		public Object getParent(Object element) {
			
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			if (element instanceof Attribute){
				return errors.get(element) != null;
			}
			return false;
		}

		@Override
		public Object[] getElements(Object inputElement) {
			errors = (HashMap<Attribute, List<String>>)inputElement;
			Collection c = errors.keySet();
			return c.toArray(new Object[c.size()]);
		}

		@Override
		public void dispose() {
			
			
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			
			
		}
		
	}
	
	
	private EntityStorageStatistics stat;
	private ToolItem next;
	private TableViewer viewer, dirtyViewer;
	
	//columnsViewer of the dataViewer
	private HashMap<Attribute, ColLableProvider> columnsLabelProvider = new HashMap<Attribute, ColLableProvider>();
	
	private TreeViewer errors;
	private WritableList input;
	
	private IEntityStorage store;
	private Entity entity;
	

	private int currentChunk = 0;
	private String baseShellTitle = null;
	public DialogBrowseEntity(Shell parentShell, IEntityStorage store, Entity entity) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
		this.store = store;
		this.entity = entity;
	}

	
	protected void createRowToolbar(ToolBar bar, boolean forErrors){
		
		
		ToolItem add = new ToolItem(bar, SWT.PUSH);
		add.setToolTipText(Messages.DialogBrowseEntity_7);
		add.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ADD));
		add.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Row row = RuntimeFactory.eINSTANCE.createRow();
				try {
					store.createRow(row);
				} catch (Exception e1) {
					
					e1.printStackTrace();
				}
				//we add the row to the dirty object to avoid
				//this new row being updated after created
				//when flushing the store
				dirtyObject.add(row);
				input.add(row);
				viewer.refresh();
				dirtyViewer.refresh();
				dirtyViewer.setSelection(new StructuredSelection(row));
				dirtyViewer.reveal(row);
				
				viewer.setSelection(new StructuredSelection(row));
				viewer.reveal(row);
			}
		});
		add.setEnabled(!Activator.getDefault().getControler().isDirty() && !forErrors);
		
		add = new ToolItem(bar, SWT.PUSH);
		add.setToolTipText(Messages.DialogBrowseEntity_8);
		add.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_DELETE));
		add.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for(Object o : (List)((IStructuredSelection)viewer.getSelection()).toList()){
					try {
						store.deleteRow((Row)o);
						input.remove(o);
						dirtyObject.add(o);
					} catch (Exception e1) {
						
						e1.printStackTrace();
					}
					
					
				}
				dirtyViewer.refresh();
				viewer.refresh();
				
			}
		});
		add.setEnabled(!Activator.getDefault().getControler().isDirty());
		
		
		final ToolItem prev = new ToolItem(bar, SWT.PUSH);
		prev.setToolTipText(Messages.DialogBrowseEntity_3);
		prev.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_BACK));
		prev.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (currentChunk > 0){
					currentChunk--;
					loadRows();
					prev.setEnabled(currentChunk > 0);
				}
				next.setEnabled(currentChunk + 1 < stat.getChunkNumber() );
				updateTitle();
			}
		});
		prev.setEnabled(currentChunk > 0 && !forErrors);

		
		next = new ToolItem(bar, SWT.PUSH);
		next.setToolTipText(Messages.DialogBrowseEntity_6);
		next.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_FORWARD));
		next.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (input.size() > 0){
					currentChunk++;
					loadRows();
					prev.setEnabled(input.size() > 0);
				}
				next.setEnabled(currentChunk + 1 < stat.getChunkNumber() );
				updateTitle();
			}
		});
		next.setEnabled(false);
		//next.setEnabled(!Activator.getDefault().getControler().isDirty());
	
		add = new ToolItem(bar, SWT.PUSH);
		add.setToolTipText(Messages.DialogBrowseEntity_15);
		add.setImage(Activator.getDefault().getImageRegistry().get(IconNames.REFRESH));
		add.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					store.cancel();
					dirtyObject.clear();
					currentChunk = 0;
					getStatistics();
					loadRows();
					updateTitle();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
				
			}
		});
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
		root.setLayoutData(new GridData(GridData.FILL_BOTH));
		root.setLayout(new FillLayout(SWT.VERTICAL));
		
		
		Composite c = new Composite(root, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		//c.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(c, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		l.setText(Messages.DialogBrowseEntity_16);
		
		ToolBar bar = new ToolBar(c, SWT.VERTICAL);
		bar.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true));
		
		createRowToolbar(bar, false);
		
		
		viewer = new TableViewer(c, SWT.VIRTUAL | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		ColumnViewerToolTipSupport.enableFor(viewer, org.eclipse.jface.window.ToolTip.RECREATE);
		for(Attribute a : entity.getAttributes()){
			TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
			col.getColumn().setText(a.getName());
			col.getColumn().setToolTipText(a.getDescription());
			col.getColumn().setWidth(200);
			ColLableProvider colLabelProvider = new ColLableProvider(a);
			col.setLabelProvider(colLabelProvider);
			RowEditSupport editSupport = new RowEditSupport(viewer, a);
			col.setEditingSupport(editSupport);
			final Attribute att = a;
			col.getColumn().addSelectionListener(new SelectionAdapter() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					viewer.setComparator(new _Comparator(att));
					
				}

			});
			columnsLabelProvider.put(a, colLabelProvider);
		}
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				
				dirtyViewer.setSelection(event.getSelection());
				if (!event.getSelection().isEmpty()){
					dirtyViewer.reveal(((IStructuredSelection)event.getSelection()).getFirstElement());
				}
				
				//show errors from selection
				HashMap<Attribute, List<String>> err = new HashMap<Attribute, List<String>>();
				Row row = (Row)((IStructuredSelection)viewer.getSelection()).getFirstElement();
				if (row == null){
					return;
				}
				
				//check errors only if bg is RED for an attribute	
				for(Attribute a : entity.getAttributes()){
					if (columnsLabelProvider.get(a).getBackground(row) != RED){
						continue;
					}
					List<String> l = err.get(a);
					
					if (row.getValue(a) == null && !a.isNullAllowed()){
						if (l == null){
							 err.put(a, new ArrayList<String>());
							 l = err.get(a);
						}
						l.add(Messages.DialogBrowseEntity_9);
						
					}
					
					if (row.getValue(a) != null){
						try{
							a.getDataType().convertFromString(row.getValue(a).toString());
						}catch(Exception ex){
							l.add(Messages.DialogBrowseEntity_17 + row.getValue(a) + Messages.DialogBrowseEntity_18+ a.getDataType().getName());
						}
					}
					
					for(Rule r : a.getRules()){
						if (r.isActive() && !r.evaluate(row.getValue(a))){
							if (l == null){
								 err.put(a, new ArrayList<String>());
								 l = err.get(a);
							}
							l.add(Messages.DialogBrowseEntity_10 + r.getName() + Messages.DialogBrowseEntity_11);
						}
					}
					
				}
				errors.setInput(err);
			}
		});
		
				

		Composite cot = new Composite(root, SWT.NONE);
		cot.setLayout(new GridLayout());
		//cot.setLayoutData(new GridLayout(2, true));
		
		
		
		SashForm main = new SashForm(cot, SWT.NONE);
		main.setLayout(new GridLayout(2, true));

		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		Composite cc = new Composite(main, SWT.NONE);
		cc.setLayout(new GridLayout(2, false));
		cc.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		l = new Label(cc, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 2, 1));
		l.setText(Messages.DialogBrowseEntity_19);
		
		ToolBar tb = new ToolBar(cc, SWT.VERTICAL);
		tb.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true));
		
		final ToolItem it = new ToolItem(tb, SWT.PUSH);
		it.setToolTipText(Messages.DialogBrowseEntity_20);
		it.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_DELETE));
		it.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)dirtyViewer.getSelection();
				if (ss.isEmpty()){
					return;
				}
				
				
				Row originalRow = null;
				
				try{
					originalRow = store.lookup((Row)ss.getFirstElement());
					if (originalRow != null){
						input.set(input.indexOf(ss.getFirstElement()), originalRow);
					}
					else{
						input.remove(ss.getFirstElement());
					}
				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(getShell(), Messages.DialogBrowseEntity_21, Messages.DialogBrowseEntity_22 + ex.getMessage());
					return;
				}
				
				dirtyObject.remove(ss.getFirstElement());
				dirtyViewer.refresh();
				store.cancel((Row)ss.getFirstElement());
				viewer.refresh();
				
			}
		});
		it.setEnabled(false);
		
		dirtyViewer = new TableViewer(cc, SWT.VIRTUAL | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		dirtyViewer.setContentProvider(new ArrayContentProvider());
		dirtyViewer.getTable().setLinesVisible(true);
		dirtyViewer.getTable().setHeaderVisible(true);
		dirtyViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		dirtyViewer.getTable().setToolTipText(Messages.DialogBrowseEntity_23);
		ColumnViewerToolTipSupport.enableFor(dirtyViewer, org.eclipse.jface.window.ToolTip.RECREATE);
		
		for(Attribute a : entity.getAttributes()){
			TableViewerColumn col = new TableViewerColumn(dirtyViewer, SWT.NONE);
			col.getColumn().setText(a.getName());
			col.getColumn().setToolTipText(a.getDescription());
			col.getColumn().setWidth(200);
			col.setLabelProvider(new ColLableProvider(a, entity.getAttributes().indexOf(a) == 0));
			
			final Attribute att = a;
			col.getColumn().addSelectionListener(new SelectionAdapter() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					viewer.setComparator(new _Comparator(att));
					
				}

			});
		}
		
		cc = new Composite(main, SWT.NONE);
		cc.setLayout(new GridLayout());
		cc.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		l = new Label(cc, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.DialogBrowseEntity_24);
		
		errors = new TreeViewer(cc, SWT.VIRTUAL | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		errors.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		errors.getTree().setHeaderVisible(true);
		errors.setAutoExpandLevel(2);

		TreeViewerColumn col = new TreeViewerColumn(errors, SWT.NONE);
		col.getColumn().setText(Messages.DialogBrowseEntity_12);
		col.getColumn().setWidth(200);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if (element instanceof Attribute){
					return ((Attribute)element).getName();
				}
				return super.getText(element);
			}
		});
		
		errors.setContentProvider(new ErrorContentProvider());
		
		
		dirtyViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				HashMap<Attribute, List<String>> err = new HashMap<Attribute, List<String>>();

				it.setEnabled(!dirtyViewer.getSelection().isEmpty());
				if (!dirtyViewer.getSelection().isEmpty()){
					
					Row row = (Row)((IStructuredSelection)dirtyViewer.getSelection()).getFirstElement();
					
					
					for(Attribute a : entity.getAttributes()){
						List<String> l = err.get(a);
						
						if (row.getValue(a) == null && !a.isNullAllowed()){
							if (l == null){
								 err.put(a, new ArrayList<String>());
								 l = err.get(a);
							}
							l.add(Messages.DialogBrowseEntity_9);
						}
						
						for(Rule r : a.getRules()){
							if (r.isActive() && !r.evaluate(row.getValue(a))){
								if (l == null){
									 err.put(a, new ArrayList<String>());
									 l = err.get(a);
								}
								l.add(Messages.DialogBrowseEntity_10 + r.getName() + Messages.DialogBrowseEntity_11);
							}
						}
						
					}
					
				}
				
				errors.setInput(err);
			}
		});
		dirtyViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				viewer.setSelection(event.getSelection());
				if (!event.getSelection().isEmpty()){
					viewer.reveal(((IStructuredSelection)event.getSelection()).getFirstElement());
				}
				
			}
		});
		
		main.setWeights(new int[]{70, 30});
		return main;
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setSize(800, 600);
		
		getStatistics();
		loadRows();
		baseShellTitle = Messages.DialogBrowseEntity_13 + entity.getName() + Messages.DialogBrowseEntity_14;
		updateTitle();
	}
	protected void updateTitle(){
		getShell().setText(baseShellTitle + Messages.DialogBrowseEntity_25 + (currentChunk+1) + "/" + stat.getChunkNumber() + " " + stat.getRowsNumber() + " rows"); //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(!Activator.getDefault().getControler().isDirty());
	}
	
	@Override
	protected void okPressed() {
		try {
			store.flush();
			
		}catch(RowsExceptionHolder ex){
			ex.printStackTrace();
			MultiStatus status = new MultiStatus(Activator.PLUGIN_ID, IStatus.WARNING, Messages.DialogBrowseEntity_29, 
					ex);
			
			dirtyObject.clear();
			loadRows();
			for(AbstractRowException e : ex.getErrors()){
				status.add(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage()));
				
				try{
					dirtyObject.add(e.getRow());
					if (e.getType() == OperationType.CREATE){
						input.add(e.getRow());
						store.createRow(e.getRow());
					}
					else if (e.getType() == OperationType.UPDATE){
						//TODO:
						store.updateRow(e.getRow());
					}	
				}catch(Exception ex2){
					ex2.printStackTrace();
				}
				
				viewer.refresh();
				dirtyViewer.refresh();
			}
			
			StatusManager.getManager().handle(status, StatusManager.BLOCK);
			
			
			return;
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), Messages.DialogBrowseEntity_30, Messages.DialogBrowseEntity_31 + e.getMessage());
			return;
		}
		super.okPressed();
	}
	
	
	private void getStatistics(){
		try{
			stat = store.getStorageStatistics();
			next.setEnabled(currentChunk + 1 < stat.getChunkNumber() );
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	protected void loadRows(){
		final Realm realm = Realm.getDefault();
		Job job = new Job(Messages.DialogBrowseEntity_32 + entity.getName() + Messages.DialogBrowseEntity_33) {
			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask(Messages.DialogBrowseEntity_34, IProgressMonitor.UNKNOWN);
				
				List<Row> rows = null;
				try{
					rows = getStore().getRows(currentChunk);
				}catch(Exception e){
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.DialogBrowseEntity_35, e);
				}
				HashMap<Row, Row>  dirtyRowsMapping = new HashMap<Row, Row>();
				
				for(Object o : rows){
					for(Object oo : getDirtyOjects()){
						if (((Row)oo).match(getEntity().getAttributesId(), (Row)o)){
							dirtyRowsMapping.put((Row)o, (Row)oo);
							break;
						}
					}
				}
				for(Row r : dirtyRowsMapping.keySet()){
					Collections.replaceAll(rows, r, dirtyRowsMapping.get(r));
				}
				
				//we add the new row to the end from the store
				//to be able to edit it
				for(Object o : getDirtyOjects()){
					if (getStore().getType((Row)o) == OperationType.CREATE){
						rows.add((Row)o);
					}
				}
				try{
					setInputViewer(new WritableList(realm,  rows, Row.class));
				}catch(Exception e){
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.DialogBrowseEntity_36, e);
				}
				
				
				
				Display.getDefault().asyncExec(new Runnable(){public void run(){fillViewers();}});
				return Status.OK_STATUS;
			}

			
		};
		job.setUser(true);
		job.schedule();
		
		IProgressService service = PlatformUI.getWorkbench().getProgressService();
		service.showInDialog(getShell(), job);
		try {
			job.join();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		
		if (job.getResult().getSeverity() == IStatus.ERROR){
			MessageDialog.openError(getShell(), getShell().getText(), Messages.DialogBrowseEntity_37 + job.getResult().getMessage());
			close();
		}
	}
	protected void setInputViewer(WritableList writableList) {
		input = writableList;
	}
	
	protected Entity getEntity() {
		return entity;
	}


	protected List<Object> getDirtyOjects() {
		return dirtyObject;
	}


	protected IEntityStorage getStore() {
		return store;
	}


	@Override
	protected void cancelPressed() {
		try {
			store.cancel();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		super.cancelPressed();
	}
	
	protected void fillViewers(){
		viewer.setInput(input);dirtyViewer.setInput(dirtyObject);
	}
}
