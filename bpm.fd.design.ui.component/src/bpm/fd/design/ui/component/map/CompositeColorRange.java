package bpm.fd.design.ui.component.map;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColorCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.component.Messages;
import bpm.fd.design.ui.icons.Icons;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;

/**
 * if a colorRange is added an event with event.data={"add", ColorRange} is created
 * if a colorRange is delete an event with event.data={"delete", List<ColorRange>}
 * and the client composite notify  its listeners on SWT.Modify event type
 * 
 * 
 * @author ludo
 *
 */
public class CompositeColorRange {
	private TableViewer viewer;
	private Composite client;
	private ToolItem addRange, deleteRange;
	
	private static HashMap<String, Color > colors = new HashMap<String, Color >();
	
	private static Color getColor(String hexaCode){
		for(String s : colors.keySet()){
			if (s.equals(hexaCode)){
				return colors.get(s);
			}
		}
		int r = Integer.parseInt(hexaCode.substring(0, 2), 16);
		int g = Integer.parseInt(hexaCode.substring(2, 4), 16);
		int b = Integer.parseInt(hexaCode.substring(4, 6), 16);
		
		Color c = new Color(Display.getCurrent(), r, g, b);
		colors.put(hexaCode, c);
		return c;
		
	}
	
	public Composite createContent(Composite parent){
		client = new Composite(parent, SWT.NONE);
		client.setLayout(new GridLayout(2, false));
		
		ToolBar toolbar = new ToolBar(client, SWT.VERTICAL);
		toolbar.setLayout(new GridLayout());
		toolbar.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true));
		
		addRange = new ToolItem(toolbar, SWT.PUSH);
		addRange.setToolTipText("Add a color Range"); //$NON-NLS-1$
		addRange.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				ColorRange color = new ColorRange();
				((List)viewer.getInput()).add(color);
				viewer.refresh();
				Event ev = new Event();
				ev.data = new Object[]{"add", color}; //$NON-NLS-1$
				client.notifyListeners(SWT.Modify, ev);
			}
		});
		addRange.setImage(Activator.getDefault().getImageRegistry().get(Icons.add));
		
		deleteRange = new ToolItem(toolbar, SWT.PUSH);
		deleteRange.setToolTipText(Messages.CompositeColorRange_2);
		deleteRange.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				Event ev = new Event();
				List l = ((IStructuredSelection)viewer.getSelection()).toList();
				ev.data = new Object[]{"delete", l}; //$NON-NLS-1$
				((List)viewer.getInput()).removeAll(l);
				client.notifyListeners(SWT.Modify, ev);
				viewer.refresh();
			}
		});
		deleteRange.setEnabled(false);
		deleteRange.setImage(Activator.getDefault().getImageRegistry().get(Icons.delete));
		
		viewer = new TableViewer(client, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				deleteRange.setEnabled(!viewer.getSelection().isEmpty());
				
			}
		});
		
		
		
		TableViewerColumn col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText(Messages.CompositeColorRange_4);
		col.getColumn().setWidth(100);
		col.setEditingSupport(new EditingSupport(viewer) {
			ColorCellEditor editor = new ColorCellEditor(viewer.getTable()); 
			@Override
			protected void setValue(Object element, Object value) {
				String r = Integer.toHexString(((RGB)value).red);
				if (r.length() == 1){
					r = "0" + r; //$NON-NLS-1$
				}
				String b = Integer.toHexString(((RGB)value).blue);
				if (b.length() == 1){
					b = "0" + b; //$NON-NLS-1$
				}
				String g = Integer.toHexString(((RGB)value).green);
				if (g.length() == 1){
					g = "0" + g; //$NON-NLS-1$
				}
				
				
				String s = r + g + b;
				((ColorRange)element).setHex(s);
				viewer.refresh();
				
			}
			
			@Override
			protected Object getValue(Object element) {
				ColorRange range = (ColorRange)element;

				int r = Integer.parseInt(range.getHex().substring(0, 2), 16);
				int g = Integer.parseInt(range.getHex().substring(2, 4), 16);
				int b = Integer.parseInt(range.getHex().substring(4, 6), 16);
				return new RGB(r,g, b);			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return editor;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public Color getBackground(Object element) {
				ColorRange colorRange = (ColorRange)element;
				return getColor(colorRange.getHex());
			}
			
			@Override
			public String getText(Object element) {
				return ((ColorRange)element).getHex();
			}
		});
		
		col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText(Messages.CompositeColorRange_8);
		col.getColumn().setWidth(250);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if (((ColorRange)element).getName() != null){
					return ((ColorRange)element).getName(); 
				}
				return ""; //$NON-NLS-1$
			}
		});
		
		col.setEditingSupport(new EditingSupport(viewer) {
			TextCellEditor editor = new TextCellEditor(viewer.getTable());
			@Override
			protected void setValue(Object element, Object value) {
				try{
					((ColorRange)element).setName((String)value);				
				}catch(Exception ex){
					ex.printStackTrace();
				}
				viewer.refresh();
			}
			
			@Override
			protected Object getValue(Object element) {
				
				return ((ColorRange)element).getName();
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
		
		
		col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText(Messages.CompositeColorRange_10);
		col.getColumn().setWidth(250);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if (((ColorRange)element).getMin() != null){
					return ((ColorRange)element).getMin() + "";  //$NON-NLS-1$
				}
				return ""; //$NON-NLS-1$
			}
		});
		
		col.setEditingSupport(new EditingSupport(viewer) {
			TextCellEditor editor = new TextCellEditor(viewer.getTable());
			@Override
			protected void setValue(Object element, Object value) {
				try{
					((ColorRange)element).setMin(Integer.parseInt((String)value));				
				}catch(Exception ex){
					ex.printStackTrace();
				}
				viewer.refresh();
			}
			
			@Override
			protected Object getValue(Object element) {
				Integer s = ((ColorRange)element).getMin(); 
				return s != null ? "" + s : ""; //$NON-NLS-1$ //$NON-NLS-2$
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
		
		col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText(Messages.CompositeColorRange_15);
		col.getColumn().setWidth(250);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if (((ColorRange)element).getMax() != null){
					return ((ColorRange)element).getMax() + "";  //$NON-NLS-1$
				}
				return ""; //$NON-NLS-1$
			}
		});
		
		
		col.setEditingSupport(new EditingSupport(viewer) {
			TextCellEditor editor = new TextCellEditor(viewer.getTable());
			@Override
			protected void setValue(Object element, Object value) {
				try{
					((ColorRange)element).setMax(Integer.parseInt((String)value));				
				}catch(Exception ex){
					ex.printStackTrace();
				}
				viewer.refresh();
			}
			
			@Override
			protected Object getValue(Object element) {
				Integer s = ((ColorRange)element).getMax(); 
				return s != null ? "" + s : ""; //$NON-NLS-1$ //$NON-NLS-2$
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
	
		
		
		return client;
	}
	
	public Composite getClient(){
		return client;
	}
	
	public void setInput(List<ColorRange>ranges){
		viewer.setInput(ranges);
	}

	public List<ColorRange> getColorRanges(){
		return (List)viewer.getInput();
	}
	
}
