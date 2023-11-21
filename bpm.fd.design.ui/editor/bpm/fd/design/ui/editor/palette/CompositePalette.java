package bpm.fd.design.ui.editor.palette;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColorCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import bpm.fd.api.core.model.datas.ColumnDescriptor;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.api.core.model.datas.QueryHelper;
import bpm.fd.api.core.model.resources.Palette;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.icons.Icons;
import bpm.fd.design.ui.tools.ColorManager;
import bpm.fd.design.ui.viewer.labelprovider.DatasLabelProvider;



public class CompositePalette extends Composite{
	private class Validator implements IInputValidator{

		@Override
		public String isValid(String newText) {
			if (palette.getColor(newText) != null){
				return "This key already exists";
			}
			return null;
		}
		
	}
	
	private static RGB getColor(String colorCode){
		int r = Integer.parseInt(colorCode.substring(0, 2), 16);
		int g = Integer.parseInt(colorCode.substring(2, 4), 16);
		int b = Integer.parseInt(colorCode.substring(4, 6), 16);
		
		return  new RGB( r, g, b);
	}
	private static String getColorCode(RGB rgb){
		String r = Integer.toHexString(rgb.red);
		if (r.length() == 1){
			r = "0" + r; //$NON-NLS-1$
		}
		String b = Integer.toHexString(rgb.blue);
		if (b.length() == 1){
			b = "0" + b; //$NON-NLS-1$
		}
		String g = Integer.toHexString(rgb.green);
		if (g.length() == 1){
			g = "0" + g; //$NON-NLS-1$
		}
		String s = r + g + b;
		return s;
	}
	
	private static RGB randomRGB(){
		int r =(int)( Math.random() * 255.);
		int g = (int)( Math.random() * 255.);
		int b = (int)( Math.random() * 255.);
		return new RGB(r,g,b);
	}
	
	private Text name;
	private TableViewer viewer;
	private ComboViewer dataSource;
	private ComboViewer dataSet;
//	private ComboViewer labelField;
	private ComboViewer keyField;
	private Button gatherKeys;
	
	private Palette palette;
	
	public CompositePalette(Composite parent, int style){
		super(parent, style);
		setLayout(new GridLayout());
		
		
		
		Group group = new Group(this, SWT.NONE);
		group.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		group.setText("Key Loading");
		group.setLayout(new GridLayout(2, false));
		
		Label l = new Label(group, SWT.NONE);
		l.setText("DataSource");
		l.setLayoutData(new GridData());
		
		dataSource = new ComboViewer(group, SWT.READ_ONLY);
		dataSource.getControl().setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		dataSource.setContentProvider(new ArrayContentProvider());
		dataSource.setLabelProvider(new DatasLabelProvider());
		dataSource.setInput(Activator.getDefault().getProject().getDictionary().getDatasources());
		dataSource.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty()){
					dataSet.setInput(new ArrayList());
				}
				else{
					DataSource ds = (DataSource)((IStructuredSelection)event.getSelection()).getFirstElement();
					dataSet.setInput(Activator.getDefault().getProject().getDictionary().getDataSetsFor(ds));
				}
				
			}
		});
		
		l = new Label(group, SWT.NONE);
		l.setText("DataSet");
		l.setLayoutData(new GridData());
	
		dataSet = new ComboViewer(group, SWT.READ_ONLY);
		dataSet.getControl().setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		dataSet.setContentProvider(new ArrayContentProvider());
		dataSet.setLabelProvider(new DatasLabelProvider()); 
		dataSet.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty()){
					keyField.setInput(new ArrayList());
				}
				else{
					DataSet ds = (DataSet)((IStructuredSelection)event.getSelection()).getFirstElement();
					keyField.setInput(ds.getDataSetDescriptor().getColumnsDescriptors());
				}
				
			}
		});
		
		l = new Label(group, SWT.NONE);
		l.setText("Key Field");
		l.setLayoutData(new GridData());
		
		keyField = new ComboViewer(group, SWT.READ_ONLY);
		keyField.getControl().setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		keyField.setContentProvider(new ArrayContentProvider());
		keyField.setLabelProvider(new DatasLabelProvider()); 
		keyField.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				gatherKeys.setEnabled(!event.getSelection().isEmpty());
				
			}
		});
//		
//		l = new Label(group, SWT.NONE);
//		l.setText("Label Field");
//		l.setLayoutData(new GridData());
//		
//		labelField = new ComboViewer(group, SWT.READ_ONLY);
//		labelField.getControl().setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
//		labelField.setContentProvider(new ArrayContentProvider());
//		labelField.setLabelProvider(new DatasLabelProvider()); 
		
		
		gatherKeys = new Button(group, SWT.PUSH);
		gatherKeys.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		gatherKeys.setText("Load Keys from DataSet");
		gatherKeys.setEnabled(false);
		gatherKeys.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				executeQuery();
			}
		});
		
		/*
		 * viewer
		 */
		Group c = new Group(this, SWT.NONE);
		c.setText("Palette");
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		c.setLayout(new GridLayout(2, false));
		
		l = new Label(c, SWT.NONE);
		l.setText("Name");
		l.setLayoutData(new GridData());
		
		name = new Text(c, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		name.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				palette.setName(name.getText());
				
			}
		});
		
		ToolBar bar = new ToolBar(c, SWT.VERTICAL);
		bar.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true));
		
		/*
		 * fill ToolBar
		 */
		ToolItem it = new ToolItem(bar, SWT.PUSH);
		it.setImage(Activator.getDefault().getImageRegistry().get(Icons.add));
		it.setToolTipText("Add a New Key");
		it.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				InputDialog d = new InputDialog(getShell(), "Color Key", "Enter key : ", "myKey", new Validator());
				if (d.open() == InputDialog.OK){
					addColorFor(d.getValue());
					viewer.refresh();
				}
				
			}
		});
		
		final ToolItem itD = new ToolItem(bar, SWT.PUSH);
		itD.setImage(Activator.getDefault().getImageRegistry().get(Icons.delete));
		itD.setToolTipText("Remove selected Keys");
		itD.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				for(Object o : ((IStructuredSelection)viewer.getSelection()).toArray()){
					palette.remove((String)o);
				}
				viewer.refresh();
			}
		});
		itD.setEnabled(false);
		
		
		/*
		 * create TableViewer for palette 
		 */
		viewer = new TableViewer(c, SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.VIRTUAL) ;
		viewer.setContentProvider(new IStructuredContentProvider(){

			@Override
			public Object[] getElements(Object inputElement) {
				Collection l = palette.getKeys();
				
				return l.toArray(new Object[l.size()]);
			}

			@Override
			public void dispose() {
				
				
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				
				
			}
			
		});
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setComparator(new ViewerComparator());
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				itD.setEnabled(!event.getSelection().isEmpty());
				
			}
		});
		TableViewerColumn key = new TableViewerColumn(viewer, SWT.NONE);
		key.getColumn().setText("Key");
		key.getColumn().setWidth(100);
		key.setLabelProvider(new ColumnLabelProvider());
		
		
		TableViewerColumn color = new TableViewerColumn(viewer, SWT.NONE);
		color.getColumn().setText("Color");
		color.getColumn().setWidth(100);
		color.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return "";
			}
			@Override
			public Color getBackground(Object element) {
				RGB rgb = getColor(palette.getColor((String)element));
				if (rgb != null){
					Color  c = ColorManager.getColorRegistry().get(palette.getColor((String)element));
					if (c == null){
						ColorManager.getColorRegistry().put(palette.getColor((String)element), rgb);
						c = ColorManager.getColorRegistry().get(palette.getColor((String)element));
					}
					return c;
					
				}
				return super.getBackground(element);
			}
		});
		color.setEditingSupport(new EditingSupport(viewer) {
			private ColorCellEditor editor = new ColorCellEditor(viewer.getTable());
			@Override
			protected void setValue(Object element, Object value) {
				palette.addColor((String)element, getColorCode((RGB)value), true);
				viewer.refresh();
				
			}
			
			@Override
			protected Object getValue(Object element) {
				return getColor(palette.getColor((String)element));
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
	}

	private void executeQuery(){
		DataSource dataSource = (DataSource)((IStructuredSelection)this.dataSource.getSelection()).getFirstElement();
		DataSet dataSet = (DataSet)((IStructuredSelection)this.dataSet.getSelection()).getFirstElement();
		ColumnDescriptor field = (ColumnDescriptor)((IStructuredSelection)this.keyField.getSelection()).getFirstElement();
		org.eclipse.datatools.connectivity.oda.IQuery q = null;
		IResultSet rs = null;
		
		try{
			q = QueryHelper.buildquery(dataSource, dataSet);
			rs = q.executeQuery();
			
			while(rs.next()){
				String key = rs.getString(field.getColumnIndex());
				if (key != null && palette.getColor(key) == null){
					addColorFor(key);
					
				}
			}
		}catch(Exception ex){
			
		}finally{
			if (rs != null){
				try{
					rs.close();
				}catch(Exception ex){}
			}
			if (q != null){
				try{
					q.close();
				}catch(Exception ex){}
			}
			
		}
		viewer.setInput(palette.getKeys());
		
	}
	private void addColorFor(String key) {
		RGB rgb = null;
		boolean used = false;
		boolean offestOk = false;
		
		do{
			rgb = randomRGB();
			
			for(String s : palette.getKeys()){
				if (palette.getColor(s) !=null){
					RGB e = getColor(palette.getColor(s));
					if (e.equals(rgb)){
						used = true;
						break;
					}
				}
			}
			if (!used){
				offestOk = true;
				int val = rgb.red + rgb.blue + rgb.green;
				for(String s : palette.getKeys()){
					if (palette.getColor(s) !=null){
						RGB e = getColor(palette.getColor(s));
						int val2 = e.red + e.blue + e.green;
						
						if (Math.abs(val - val2) < 30){
							offestOk = false;
							break;
						}
					}
				}
			}
			
		}while(used && !offestOk);
		palette.addColor(key, getColorCode(rgb), false);
		
	}
	
	public void fill(Palette palette){
		this.palette = palette; 
		name.setText(palette.getName());
		viewer.setInput(palette.getKeys());
	}
}
