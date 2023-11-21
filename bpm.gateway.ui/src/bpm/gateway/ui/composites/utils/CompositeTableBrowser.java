package bpm.gateway.ui.composites.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.transformations.utils.Condition;
import bpm.gateway.runtime2.tools.StringParser;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.composites.FilterTable2;
import bpm.gateway.ui.editors.GatewayEditorInput;
import bpm.gateway.ui.i18n.Messages;
import bpm.vanilla.platform.core.IVanillaContext;

public class CompositeTableBrowser extends Composite {

	private TableViewer table;
	private List<List<Object>> model;
	private List<String> colNames; 
	private Object data;
	private TableViewer filters;
	
	public CompositeTableBrowser(Composite parent, int style, List<String> colNames, List<List<Object>> model) {
		super(parent, style);
		this.setLayout(new GridLayout());
		this.colNames = colNames;
		this.model = model;
		buildContent();
	}
	
	private void buildContent(){
		Composite comp  = new Composite(this, SWT.NONE);
		comp.setLayout(new FillLayout(SWT.VERTICAL));
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		
		Composite val = new Composite(comp, SWT.NONE);
		val.setLayout(new GridLayout());
		
		Label l = new Label(val, SWT.NONE);;
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		l.setText(Messages.CompositeTableBrowser_0);
		
		
		table = new TableViewer(val, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION);
		table.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		table.setContentProvider(new IStructuredContentProvider(){
			public Object[] getElements(Object inputElement) {
				List l = (List)inputElement;
				
				return l.toArray(new Object[l.size()]) ;
			}

			public void dispose() {}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

		});
		table.setLabelProvider(
				new Decorator(new LabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
		
		if (colNames != null){
			for(String s : colNames){
				TableColumn c = new TableColumn(table.getTable(), SWT.NONE);
				c.setText(s);
				c.setWidth(100);
			}
		}
		else if (model.get(0) != null){
			for (int i = 0; i< model.get(0).size(); i++){
				TableColumn c = new TableColumn(table.getTable(), SWT.NONE);
				c.setWidth(100);
			}
		}
		
		
		
		table.addFilter(new ViewerFilter(){

			public boolean beval(Object value, int op, String cond) throws Exception{
				DocumentGateway doc = null;
				IEditorPart e = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
				if (e.getEditorInput() instanceof GatewayEditorInput){
					doc = ((GatewayEditorInput)e.getEditorInput()).getDocumentGateway();
				}
				
				switch(op){
				case Condition.IN:
					for (String s : cond.split("]")){ //$NON-NLS-1$
						int compare = ((Comparable)value).compareTo(convertStringToNumber(doc.getStringParser().getValue(doc, s), value.getClass().getName()));
		
						if (compare == 0){
							return true;
						}
					}
					return false;
				
				case Condition.CONTAINS:
					if (value == null){
						return false;
					}
					
					return value.toString().contains(cond);
			
				case Condition.ENDSWIDTH:
					if (value == null){
						return false;
					}
					
					return value.toString().endsWith(cond);
					
				case Condition.STARTSWIDTH:
					
					if (value == null){
						return false;
					}
					
					return value.toString().startsWith(cond);
					
				case Condition.DIFFERENT:
					
					if ((cond == null || cond.equalsIgnoreCase("null"))){ //$NON-NLS-1$
						return ! (value == null);
					}
					else {
						return ((Comparable)value).compareTo(convertStringToNumber(doc.getStringParser().getValue(doc, cond), value.getClass().getName())) != 0;
					}
				
					
//					return !value.equals(cond);
					
		
				case Condition.EQUAL:
					
					if (( cond == null || cond.equalsIgnoreCase("null"))){ //$NON-NLS-1$
							return value == null;
					}
					else{
						return ((Comparable)value).compareTo(convertStringToNumber(doc.getStringParser().getValue(doc, cond), value.getClass().getName())) == 0;
					}
						
		
			
					
				case Condition.GREATER_EQ_THAN:
					
					if (value instanceof Number){
						int compare = ((Comparable)value).compareTo(convertStringToNumber(cond, value.getClass().getName()));

						return compare == 0 || compare == 1; 
					}
						
				
		
				case Condition.GREATER_THAN:
					
					if (value instanceof Number){
						Object o = convertStringToNumber(cond, 
								//transfo.getDescriptor().getJavaClass(colNumber)
								value.getClass().getName()
								);
						int compare = ((Comparable)value).compareTo(o);
						
						return compare == 1; 
					}
					
				case Condition.LESSER_EQ_THAN:
		
					if (value instanceof Number){
						int compare = ((Comparable)value).compareTo(convertStringToNumber(cond, 
								value.getClass().getName()
								));
						return compare == 0 || compare == -1; 
					}
					
				case Condition.LESSER_THAN:
					if (value instanceof Number){
						int compare = ((Comparable)value).compareTo(convertStringToNumber(cond, 
		//						transfo.getDescriptor().getJavaClass(colNumber)
								value.getClass().getName()
								));
		//				System.out.println("number " + conditionNumber + " " +value.toString() + "<" + condition.getValue() + "=" + (compare == -1));
						return compare == -1; 
					}
				
					
				}
				return true;
				
			}
			
			@Override
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				
				
				boolean result = true;	
				for(FilterBean bean : (List<FilterBean>)filters.getInput()){
					Object value = ((List<Object>)element).get(bean.field);
					try{
						switch(bean.logical){
						case Condition.NONE:
							result = beval(value, bean.operator, bean.value);
							break;
							
						case Condition.AND:
							result = beval(value, bean.operator, bean.value) && result;
							break;
							
						case Condition.OR:
							result = beval(value, bean.operator, bean.value) || result;
							break;
						}
					}catch(Exception e){
						e.printStackTrace();
					}
					
				}
				
				return result;
				
			}
			
		});
		
		
		Composite filt = new Composite(comp, SWT.NONE);
		filt.setLayout(new GridLayout());
		
		Label l1 = new Label(filt, SWT.NONE);
		l1.setText(Messages.CompositeTableBrowser_1);
		
		filters = new TableViewer(filt, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION);
		filters.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		filters.setContentProvider(new IStructuredContentProvider(){
			public Object[] getElements(Object inputElement) {
				List<FilterBean> l = (List<FilterBean>)inputElement;
				
				return l.toArray(new FilterBean[l.size()]) ;
			}

			public void dispose() {}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

		});
		filters.setLabelProvider(new ITableLabelProvider(){

			public Image getColumnImage(Object element, int columnIndex) {
				
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				switch(columnIndex){
				case 0:
					return Condition.LOGICALS[((FilterBean)element).logical];
				case 1:
					return colNames.get(((FilterBean)element).field);
				case 2:
					return Condition.OPERATORS[((FilterBean)element).operator];
				case 3:
					return ((FilterBean)element).value;
				}
				
				return ""; //$NON-NLS-1$
			}

			public void addListener(ILabelProviderListener listener) {}

			public void dispose() {}

			public boolean isLabelProperty(Object element, String property) {
				
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {}

		});

		
		TableColumn logical = new TableColumn(filters.getTable(), SWT.NONE);
		logical.setText(Messages.CompositeTableBrowser_6);
		logical.setWidth(200);
		
		
		TableColumn column = new TableColumn(filters.getTable(), SWT.NONE);
		column.setText(Messages.CompositeTableBrowser_7);
		column.setWidth(200);
		
		
		TableColumn comparator = new TableColumn(filters.getTable(), SWT.NONE);
		comparator.setText(Messages.CompositeTableBrowser_8);
		comparator.setWidth(200);
		
		
		TableColumn value = new TableColumn(filters.getTable(), SWT.NONE);
		value.setText(Messages.CompositeTableBrowser_9);
		value.setWidth(200);
		
		filters.getTable().setHeaderVisible(true);
		filters.getTable().setLinesVisible(true);
		
		
		filters.setColumnProperties(new String[]{"logical", "field", "operator", "value"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		
		filters.setCellEditors(new CellEditor[]{
				new ComboBoxCellEditor(filters.getTable(), Condition.LOGICALS),
				new ComboBoxCellEditor(filters.getTable(), colNames.toArray(new String[colNames.size()])), 
				new ComboBoxCellEditor(filters.getTable(), Condition.OPERATORS),
				new TextCellEditor(filters.getTable())
				});

		
		filters.setCellModifier(new ICellModifier(){

			public boolean canModify(Object element, String property) {
				if (property.equals("logical") && ((List)filters.getInput()).size() <= 1){ //$NON-NLS-1$
					return false;
				}
				return true;
			}

			public Object getValue(Object element, String property) {
				if (property.equals("logical")){ //$NON-NLS-1$
					for(int i = 0; i < Condition.LOGICALS.length; i++){
						if (i == ((FilterBean)element).logical){
							return i;
						}
					}
				}
				else if (property.equals("field")){ //$NON-NLS-1$
					for(int i = 0; i < colNames.size(); i++){
						if (i == (((FilterBean)element).field)){
							return i;
						}
					}
				}
				else if (property.equals("operator")){ //$NON-NLS-1$
					for(int i = 0; i < Condition.OPERATORS.length; i++){
						if (i == ((FilterBean)element).operator){
							return i;
						}
					}
				}
				else if (property.equals("value")){ //$NON-NLS-1$
					return ((FilterBean)element).value;
				}
				return null;
			}

			public void modify(Object element, String property, Object value) {
				if (property.equals("logical")){ //$NON-NLS-1$
					if (((Integer)value) < 0){
						return;
					}
					((FilterBean)((TableItem)element).getData()).logical = ((Integer)value);
				}
				else if (property.equals("field")){ //$NON-NLS-1$
					if (((Integer)value) < 0){
						return;
					}
					((FilterBean)((TableItem)element).getData()).field = ((Integer)value);
				}
				else if (property.equals("operator")){ //$NON-NLS-1$
					if (((Integer)value) < 0){
						return;
					}
					((FilterBean)((TableItem)element).getData()).operator = ((Integer)value);

				}
				else if (property.equals("value")){ //$NON-NLS-1$
					((FilterBean)((TableItem)element).getData()).value = ((String)value);
				}
				filters.refresh();
			
			}
			
		});
	
		filters.setInput(new ArrayList<FilterBean>());
		createContextMenu();
		
		
		Button b = new Button(this, SWT.NONE);
		b.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		b.setText(Messages.CompositeTableBrowser_23);
		b.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				table.refresh();
			}
			
		});
	}

	

	private void createContextMenu(){
		final MenuManager menuMgr = new MenuManager();
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager mgr) {
            	final IStructuredSelection ss =  (IStructuredSelection)filters.getSelection();
            	if (!ss.isEmpty()){
            		menuMgr.add(new Action(Messages.CompositeTableBrowser_24){
            			public void run(){
            				FilterBean b = (FilterBean) ss.getFirstElement();
            				((List)filters.getInput()).remove(b);
            				filters.refresh();
            			
            			}
            		});
            	}
            	menuMgr.add(new Action(Messages.CompositeTableBrowser_25){
            		public void run(){
            			List<FilterBean> l = new ArrayList<FilterBean>(((List)filters.getInput()));
        				l.add(new FilterBean());
        				filters.setInput(l);
        			
        			}
            	});
            	
            	
            	
            }
        });

        filters.getControl().setMenu(menuMgr.createContextMenu(filters.getControl()));
	}
	
	
	
	public void setData(){
		if (!table.getSelection().isEmpty()){
			data = ((IStructuredSelection)table.getSelection()).getFirstElement();
		}
		
	}
	
	public void fill(){
		if (model.size()!=0){
			
			table.setInput(model);
			table.getTable().setHeaderVisible(true);
			table.getTable().setLinesVisible(false);
		}
	}
	
	@Override
	public Object getData(){
		return data;
	}
	
	private class FilterBean{
		int field;
		int logical;
		int operator;
		String value = Messages.CompositeTableBrowser_26;
	}
	
	
	private Comparable convertStringToNumber(String value, String  className) throws Exception{
		
		Class  c = Class.forName(className);
		
		Comparable n = null;
		
		if (java.util.Date.class.isAssignableFrom(c)){
		
		}else{
			try {
				n =  (Comparable) c.getConstructor(new Class[]{String.class}).newInstance(new Object[]{value});
			}catch(Exception e){
				
				try{
					c = Integer.class; 
					n =  (Comparable) c.getConstructor(new Class[]{String.class}).newInstance(new Object[]{value});
				}catch(Exception ex){
					
				}
				
				
			}
		}
		     
		
		if (n == null)
			throw new Exception(Messages.CompositeTableBrowser_27);
		
		return n;
	
	}
	
	private class Decorator extends DecoratingLabelProvider implements ITableLabelProvider{

		public Decorator(ILabelProvider provider, ILabelDecorator decorator) {
			super(provider, decorator);
			
		}

		public Image getColumnImage(Object element, int columnIndex) {
			
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (element == null)
				return "";  //$NON-NLS-1$
			try{
				Object o = ((List<Object>)element).get(columnIndex);
				try {
					return o == null ? "NULL" : new String(o.toString().getBytes("UTF-8"), "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				} catch (UnsupportedEncodingException e) {
					return o == null ? "NULL" : o.toString(); //$NON-NLS-1$
				}
			}catch(ArrayIndexOutOfBoundsException e){
				e.printStackTrace();
				return Messages.CompositeTableBrowser_32;
			}
			
		}

		@Override
		public Color getBackground(Object element) {
			if (model.indexOf(element) % 2 == 0){
				return FilterTable2.mainBrown;
			}
			else{
				return FilterTable2.secondBrown;
			}
			
		}
		
		
		
	}
}
