package bpm.fd.design.ui.editor.style;

import java.io.FileInputStream;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Listener;

import bpm.fd.api.core.model.resources.FileImage;
import bpm.fd.api.core.model.resources.IResource;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.tools.ColorManager;

public class CssComposite extends Composite{
	private static class CssLabelProvider extends ColumnLabelProvider{
		@Override
		public String getText(Object element) {
			return ((CssConstants)element).name();
		}
	}
	private class CssValueLabelProvider extends ColumnLabelProvider{
		@Override
		public String getText(Object element) {
			return data.getValue(((CssConstants)element).getName());
		}
	}
	
	private enum EditorType{Font, Background, Border}
	
	private class CssDialogEditor extends DialogCellEditor{
		EditorType type;
		public CssDialogEditor(EditorType type, Composite parent){
			super(parent); this.type = type;
		}
		@Override
		protected Object openDialogBox(Control cellEditorWindow) {
			switch (type){
			case Background:
				openBackground();break;
			case Border:
				openBorder();break;
			case Font:
				openFont();break;
			}
			canvas.redraw();
			return null;
		}
		private void openBorder(){
			DialogBorders d = new DialogBorders(getShell(), data);
			if (d.open()  == DialogBorders.OK){
				borderViewer.refresh();
			}
		}
		private void openBackground(){
			DialogBackground d = new DialogBackground(getShell(), data);
			if (d.open()  == DialogBackground.OK){
				backgroundViewer.refresh();
				String colorcode = data.getValue(CssConstants.backgroundColor.getName());
				if (colorcode != null){
					Color bgCol = ColorManager.getColorRegistry().get(colorcode);
					if (bgCol == null){
						RGB rgb = adaptColor(CssConstants.backgroundColor);
						ColorManager.getColorRegistry().put(colorcode, rgb);
						bgCol = ColorManager.getColorRegistry().get(colorcode);
					}
					canvas.setBackground(bgCol);
//					bgCol.dispose();
				}
			}
		}
		private void openFont(){

			FontData fd = adaptFont();
			FontDialog d = new FontDialog(getShell());
			RGB color = adaptColor(CssConstants.fontColor);
			if (color != null){
				d.setRGB(color);
			}
			
			
			d.setFontList(new FontData[]{fd});
			fd = d.open();
			color = d.getRGB();
			
			if (fd != null){
				updateColor(color, CssConstants.fontColor);
				updateFontProperties(fd);
				
				fontViewer.refresh();
			}
		}
	}
	
	
	private CssClass data;
	private TableViewer fontViewer;
	
	
	private TableViewer backgroundViewer;
	private Canvas canvas;
	
	private TableViewer borderViewer;
	
	
	private ExpandBar bar;
	
	public CssComposite(Composite parent, int style) {
		super(parent, style);
		addListener(SWT.Resize, new Listener(){
			@Override
			public void handleEvent(Event event) {
				canvas.redraw();
				
			}
		});
		setLayout(new FillLayout(SWT.HORIZONTAL));
		bar = new ExpandBar(this, SWT.V_SCROLL);
		
		canvas = new Canvas(this, SWT.NONE);
		canvas.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				//darw imagae
				
				
				
				String imPath = data.getValue(CssConstants.backgroundImage.getName());
				if (imPath != null){
					imPath = imPath.replace("url(", "").replace(")", "");
					Image im = null;
					for(IResource r : Activator.getDefault().getProject().getResources(FileImage.class)){
						if (r.getName().equals(imPath)){
							try{
								im = new Image(e.display, new FileInputStream(r.getFile()));
								break;
							}catch(Exception ex){
								
							}
						}
					}
					
					if (im != null){
						if (data != null){
							data.setValue("min-height", im.getImageData().height + "px");
						}
						e.gc.drawImage(im, 0, 0, im.getImageData().width, im.getImageData().height, 0, 0, canvas.getClientArea().width, canvas.getClientArea().height);
						im.dispose();
					}
				}
				
				
				
				//draw text
				FontData fontData = adaptFont();
				Font f = new Font(e.display, fontData);
				Color c = new Color(e.display, adaptColor(CssConstants.fontColor));
				
				e.gc.setForeground(c);
				e.gc.setFont(f);
				e.gc.setBackground(canvas.getBackground());
				String text = "AbBbCcDdEe";
				int canvasWidth = canvas.getSize().x; 
				int textWidth = e.gc.stringExtent(text).x; 
				e.gc.drawText(text, 
						canvasWidth / 2 - textWidth / 2, 
						(int)(canvas.getSize().y - fontData.height ) / 2, true);
				
				f.dispose();
				c.dispose();
				
				//draw border
				c = new Color(e.display, adaptColor(CssConstants.borderColor));
				int lineWidth = 1;
				try{
					lineWidth = Integer.parseInt(data.getValue(CssConstants.borderWidth.getName()).replace("px", "").trim());
				}catch(Exception ex){
					
				}
				
				int lineStyle = SWT.LINE_SOLID;
				
				try{
					String d = data.getValue(CssConstants.borderStyle.getName());
					if (CssConstants.borderStyle.getValues()[0].equals(d)){
						return;
					}
					else if (CssConstants.borderStyle.getValues()[1].equals(d)){
						lineStyle = SWT.LINE_DOT;
					}
					else if (CssConstants.borderStyle.getValues()[2].equals(d)){
						lineStyle = SWT.LINE_DASH;
					}
					else if (CssConstants.borderStyle.getValues()[3].equals(d)){
						lineStyle = SWT.LINE_SOLID;
					}
					
				}catch(Exception ex){
					
				}
				
				e.gc.setLineStyle(lineStyle);
				e.gc.setForeground(c);
				e.gc.setLineWidth(lineWidth);
				e.gc.drawLine(10					,10						, 10					, canvas.getSize().y-10);
				e.gc.drawLine(10					,canvas.getSize().y - 10,canvas.getSize().x-10 , canvas.getSize().y-10);
				e.gc.drawLine(canvas.getSize().x-10,canvas.getSize().y-10	, canvas.getSize().x-10	, 10);
				e.gc.drawLine(canvas.getSize().x-10	, 10	, 10					, 10);
				e.gc.dispose();
				
			}
		});
		createFont();
		createBackground();
		createBorder();
	}
	
	final protected TableViewerColumn createValueViewerColum(TableViewer viewer){
		TableViewerColumn value = new TableViewerColumn(viewer, SWT.LEFT);
		value.getColumn().setWidth(150);
		return value;
	}
	
	private void createFont(){
		fontViewer = new TableViewer(bar, SWT.BORDER | SWT.FULL_SELECTION);
		fontViewer.setContentProvider(new ArrayContentProvider());
		fontViewer.getTable().setLinesVisible(true);
		fontViewer.getTable().setBackground(bar.getBackground());
		fontViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		fontViewer.setInput(new Object[]{
			CssConstants.fontFamily, 
			CssConstants.fontSize, 
			CssConstants.fontColor, CssConstants.fontWeight,
			CssConstants.textDecoration
		});
		TableViewerColumn col = createValueViewerColum(fontViewer);
		col.setLabelProvider(new CssLabelProvider());
		col.getColumn().setText("Property");
		
		col = createValueViewerColum(fontViewer);
		col.setLabelProvider(new CssValueLabelProvider());
		col.setEditingSupport(new EditingSupport(fontViewer) {
			private CssDialogEditor editor = new CssDialogEditor(EditorType.Font,fontViewer.getTable());
			@Override
			protected void setValue(Object element, Object value) {
			}
			
			@Override
			protected Object getValue(Object element) {
				return element;
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
		col.getColumn().setText("Value");
		
		
		
		ExpandItem item = new ExpandItem(bar, SWT.NONE);
		item.setControl(fontViewer.getTable());
		item.setText("Font");

		item.getControl().pack(true);
		Point size = item.getControl().computeSize(bar.getClientArea().width,
				SWT.DEFAULT);
				
		item.getControl().pack(true);
		item.setHeight(size.y);
		item.setExpanded(true);
	}

	private void createBackground(){
		
		backgroundViewer = new TableViewer(bar, SWT.BORDER | SWT.FULL_SELECTION);
		backgroundViewer.setContentProvider(new ArrayContentProvider());
		backgroundViewer.getTable().setLinesVisible(true);
		backgroundViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		backgroundViewer.getTable().setHeaderVisible(true);
		backgroundViewer.getTable().setBackground(bar.getBackground());
		backgroundViewer.setInput(new Object[]{
			CssConstants.backgroundImage, 
			CssConstants.backgroundRepeat,
			CssConstants.backgroundColor
		});
		TableViewerColumn col = createValueViewerColum(backgroundViewer);
		col.setLabelProvider(new CssLabelProvider());
		col.getColumn().setText("Property");
		
		col = createValueViewerColum(backgroundViewer);
		col.setLabelProvider(new CssValueLabelProvider());
		col.getColumn().setText("Values");
		col.setEditingSupport(new EditingSupport(backgroundViewer) {
			private CssDialogEditor editor = new CssDialogEditor(EditorType.Background,backgroundViewer.getTable());
			@Override
			protected void setValue(Object element, Object value) {
			}
			
			@Override
			protected Object getValue(Object element) {
				return element;
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
		
		ExpandItem item = new ExpandItem(bar, SWT.NONE);
		item.setControl(backgroundViewer.getTable());
		item.setText("Background");
		//item.setExpanded(true);
		
		
		
		item.getControl().pack(true);
		Point size = item.getControl().computeSize(bar.getClientArea().width,
				SWT.DEFAULT);
				
		item.getControl().pack(true);
		item.setHeight(size.y);
		item.setExpanded(true);
	}
	
	
	private void createBorder(){
		borderViewer = new TableViewer(bar, SWT.BORDER | SWT.FULL_SELECTION);
		borderViewer.setContentProvider(new ArrayContentProvider());
		borderViewer.getTable().setLinesVisible(true);
		borderViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		borderViewer.getTable().setHeaderVisible(true);
		borderViewer.getTable().setBackground(bar.getBackground());
		borderViewer.setInput(new Object[]{
			CssConstants.borderStyle, 
			CssConstants.borderWidth, 
			CssConstants.borderColor
		});
		TableViewerColumn col = createValueViewerColum(borderViewer);
		col.setLabelProvider(new CssLabelProvider());
		col.getColumn().setText("Property");
		
		col = createValueViewerColum(borderViewer);
		col.setLabelProvider(new CssValueLabelProvider());
		col.getColumn().setText("Values");
		col.setEditingSupport(new EditingSupport(borderViewer) {
			private CssDialogEditor editor = new CssDialogEditor(EditorType.Border,borderViewer.getTable());
			@Override
			protected void setValue(Object element, Object value) {
			}
			
			@Override
			protected Object getValue(Object element) {
				return element;
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
			
		
		ExpandItem item = new ExpandItem(bar, SWT.NONE);
		item.setControl(borderViewer.getTable());
		item.setText("Borders");
		item.getControl().pack(true);
		Point size = item.getControl().computeSize(bar.getClientArea().width,
				SWT.DEFAULT);
				
		item.getControl().pack(true);
		item.setHeight(size.y);
		item.setExpanded(true);
	}
	
	
	private FontData adaptFont(){
		int size = 10;
		try{
			size = Integer.parseInt(data.getValue(CssConstants.fontSize.getName()));
		}catch(Exception ex){
			
		}
		String fontName = "Arial";
		
		try{
			fontName = data.getValue(CssConstants.fontFamily.getName());
			if (fontName == null){
				fontName = "Arial";
			}
		}catch(Exception ex){
			
		}
		
		int type = SWT.NORMAL;
		
		if (data.getValue(CssConstants.fontType.getName()) != null){
			if (data.getValue(CssConstants.fontType.getName()).equals(CssConstants.fontType.getValues()[1])){
				type = SWT.ITALIC;
			}
		}
		
		//weight
		if (data.getValue(CssConstants.fontWeight.getName()) != null){
			if (data.getValue(CssConstants.fontWeight.getName()) .equals(CssConstants.fontWeight.getValues()[1]) || 
					data.getValue(CssConstants.fontWeight.getName()) .equals(CssConstants.fontWeight.getValues()[2])){
				type = type | SWT.BOLD;
			}
		}
		//TODO : style bit constants for text-decoration, font-weight and font-style
		FontData dt =  new FontData(fontName, size, type);
		return dt;
	}
	private void updateFontProperties(FontData dt){
		
		if ((dt.getStyle()&SWT.ITALIC) != 0 ){
			data.setValue(CssConstants.fontType.getName(), "italic");
		}
		else{
			data.setValue(CssConstants.fontType.getName(), "normal");
		}
		
		if ((dt.getStyle()&SWT.BOLD) != 0){
			data.setValue(CssConstants.fontWeight.getName(), "bold");
		}
		else{
			data.setValue(CssConstants.fontWeight.getName(), "normal");
		}
		
		
		if ((dt.getStyle()&SWT.UNDERLINE_SINGLE) != 0 ){
			data.setValue(CssConstants.textDecoration.getName(), "underline");
		}
		else{
			data.setValue(CssConstants.textDecoration.getName(), "none");
		}
		data.setValue(CssConstants.fontFamily.getName(), dt.getName())		;
		
		data.setValue(CssConstants.fontSize.getName(), dt.getHeight() + "");
		
		
	}
	private void updateColor(RGB color, CssConstants cte){
		StringBuffer buf = new StringBuffer("#");
		String r = Integer.toHexString(color.red);
		if (r.length() == 1){
			r = "0" + r; //$NON-NLS-1$
		}
		String b = Integer.toHexString(color.blue);
		if (b.length() == 1){
			b = "0" + b; //$NON-NLS-1$
		}
		String g = Integer.toHexString(color.green);
		if (g.length() == 1){
			g = "0" + g; //$NON-NLS-1$
		}	
		buf.append(r); buf.append(g);buf.append(b);
		data.setValue(cte.getName(), buf.toString());
	}
	
	private RGB adaptColor(CssConstants cte){
		try{
			String code = data.getValue(cte.getName()).replace("#", "");
			int r = Integer.parseInt(code.substring(0, 2), 16);
			int g = Integer.parseInt(code.substring(2, 4), 16);
			int b = Integer.parseInt(code.substring(4, 6), 16);
			
			return new RGB(r, g, b);
			
		}catch(Exception ex){
			return null;
		}

	}
	
	public void fill(CssClass css){
		this.data = css;
		fontViewer.refresh();
		backgroundViewer.refresh();
		borderViewer.refresh();
	}
	
		

}
