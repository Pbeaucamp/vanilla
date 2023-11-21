package bpm.fd.design.ui.structure.dialogs.creation.css;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.fd.design.ui.Messages;

public class CompositeBorder extends CompositeCssPart{

	private static final int NONE = -1;
	private static final int DASH = 0;
	private static final int SOLID = 1;
	private static final int DOTTED = 2;
	
	private static final int TOP = 0;
	private static final int BOTTOM = 2;
	private static final int LEFT = 3;
	private static final int RIGHT = 4;
	
	private Button[] topButtons = new Button[3];
	private Button[] bottomButtons = new Button[3];
	private Button[] leftButtons = new Button[3];
	private Button[] rightButtons = new Button[3];
	private Combo unite;
	
	private Canvas preview;
	private Color color = new Color(Display.getDefault(), 0, 0, 0);
	private String size;
	private String previousCss;
	
	
	public CompositeBorder(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout());
		createContent();
	}

	@Override
	protected void createContent() {
		
		Composite colorCmp = new Composite(this, SWT.NONE);
		colorCmp.setLayout(new GridLayout(3, false));
		colorCmp.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label l = new Label(colorCmp, SWT.NONE);
		l.setText(Messages.CompositeBorder_0);
		l.setLayoutData(new GridData());
		
		final Text _l = new Text(colorCmp, SWT.READ_ONLY | SWT.BORDER);
		_l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		_l.setBackground(color);
		
		Button b = new Button(colorCmp, SWT.PUSH);
		b.setText("..."); //$NON-NLS-1$
		b.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ColorDialog d = new ColorDialog(getShell());
				RGB rgb = d.open();
				
				if (rgb != null){
					if (color != null){
						color.dispose();
					}
					
					color = new Color(Display.getDefault(), rgb);
					_l.setBackground(color);
				}
				preview.redraw();
				Event event = new Event();
				event.widget = CompositeBorder.this;
				CompositeBorder.this.notifyListeners(99, event);
			}
		});
		
		Composite main = new Composite(this, SWT.NONE);
		main.setLayout(new GridLayout(3, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		l = new Label(main, SWT.NONE);
		l.setText(Messages.CompositeBorder_2);
		l.setLayoutData(new GridData());
		
		final Text tsize = new Text(main, SWT.BORDER);
		tsize.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		tsize.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				size = tsize.getText() + unite.getText();
				
			}
		});
	
		
		unite = new Combo(main, SWT.READ_ONLY);
		unite.setItems( new String[]{"px", "cm", "mm", "em", "pt"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		
		unite.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		unite.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				size = tsize.getText() + unite.getText();
				
			}
			
			
		});
		unite.select(0);
		
		unite.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Event event = new Event();
				event.widget = CompositeBorder.this;
				CompositeBorder.this.notifyListeners(99, event);
			}
		});
		
		tsize.setText("1"); //$NON-NLS-1$
		tsize.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				Event event = new Event();
				event.widget = CompositeBorder.this;
				CompositeBorder.this.notifyListeners(99, event);
			}
		});
		
		Composite left = new Composite(main, SWT.NONE);
		left.setLayout(new GridLayout());
		left.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, false));
		createButtons(LEFT, left);
		
		Composite center = new Composite(main, SWT.NONE);
		center.setLayout(new GridLayout());
		center.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		createButtons(TOP, center);
		
		preview = new Canvas(center, SWT.BORDER);
		preview.addPaintListener(new PaintListener() {
			
			public void paintControl(PaintEvent e) {
				Rectangle _r = preview.getClientArea();
				
				Rectangle r = new Rectangle(_r.x + 10, _r.y + 10 , _r.width - 20, _r.height-20);
				
				int style = getType(TOP);
				
				if (color == null){
//					e.gc.setForeground(color);
				}
				else{
					e.gc.setForeground(color);
				}
				
				/*
				 * draw top
				 */
				style = getType(TOP);
				if (style != -1 ){
					e.gc.setLineStyle(style);
					
					e.gc.drawLine(r.x, r.y, r.x + r.width, r.y);
				}
				
				/*
				 * draw bottom
				 */
				style = getType(BOTTOM);
				if (style != -1 ){
					e.gc.setLineStyle(style);
					e.gc.drawLine(r.x, r.y + r.height, r.x + r.width, r.y + r.height);
				}
				
				/*
				 * draw left
				 */
				style = getType(LEFT);
				if (style !=-1 ){
					e.gc.setLineStyle(style);
					e.gc.drawLine(r.x, r.y, r.x , r.y + r.height);
				}
				
				/*
				 * draw right
				 */
				style = getType(RIGHT);
				if (style !=-1 ){
					e.gc.setLineStyle(style);
					e.gc.drawLine(r.x + r.width, r.y, r.x +r.width , r.y + r.height);
				}
				
				
			}
		});
		preview.setLayoutData(new GridData(GridData.FILL_BOTH));
		createButtons(BOTTOM, center);
		
		Composite right = new Composite(main, SWT.NONE);
		right.setLayout(new GridLayout());
		right.setLayoutData(new GridData(GridData.END, GridData.FILL, false, false));
		createButtons(RIGHT, right);
	}
	
	
	private void createButtons(int typeButton, Composite parent){
		Composite main = new Composite(parent, SWT.NONE);
		
		if (typeButton == BOTTOM || typeButton == TOP){
			main.setLayout(new GridLayout(3, true));
			main.setLayoutData(new GridData(GridData.CENTER, GridData.BEGINNING, true, false));
			for(int i = 0; i < 3; i++){
				if (typeButton == BOTTOM){
					bottomButtons[i] = new Button(main, SWT.TOGGLE);
//					bottomButtons[i].setImage(image);
					bottomButtons[i].setText("..."); //$NON-NLS-1$
					bottomButtons[i].setLayoutData(new GridData());
					createListener(bottomButtons,bottomButtons[i] );
				}
				else{
					topButtons[i] = new Button(main, SWT.TOGGLE);
//					topButtons[i].setImage(image);
					topButtons[i].setText("..."); //$NON-NLS-1$
					topButtons[i].setLayoutData(new GridData());
					createListener(topButtons,topButtons[i] );
				
				}
			}
		}
		else{
			main.setLayout(new GridLayout());
			main.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, true));
			for(int i = 0; i < 3; i++){
				if (typeButton == LEFT){
					leftButtons[i] = new Button(main, SWT.TOGGLE);
//					leftButtons[i].setImage(image);
					leftButtons[i].setText("..."); //$NON-NLS-1$
					leftButtons[i].setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
					createListener(leftButtons,leftButtons[i] );
				}
				else{
					rightButtons[i] = new Button(main, SWT.TOGGLE);
//					rightButtons[i].setImage(image);
					rightButtons[i].setText("..."); //$NON-NLS-1$
					rightButtons[i].setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
					createListener(rightButtons,rightButtons[i] );
				}
			}
		}
		
		
		
		
	}

	
	
	public static String convertColorInHexa(Color c){
		java.awt.Color col = new java.awt.Color(c.getRed(), c.getGreen(), c.getBlue());
		String s =  Integer.toHexString(col.getRGB());
		
		return s.substring(2, s.length());
		
		
	}
	@Override
	public String getCssToString() {
		StringBuffer buf = new StringBuffer();
		
		
		
		buf.append("\tborder-color:#" + convertColorInHexa(color) + ";\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("\tborder-top-style:" + getTypeName(TOP) + ";\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("\tborder-bottom-style:" + getTypeName(BOTTOM) + ";\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("\tborder-left-style:" + getTypeName(LEFT) + ";\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("\tborder-right-style:" + getTypeName(RIGHT) + ";\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("\tborder-width:" + size + ";\n"); //$NON-NLS-1$ //$NON-NLS-2$
		
		String res =  buf.toString();
		previousCss = res;
		
		return res;
	}

	
	private void createListener(final Button[] buttonBar, Button but){
		but.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (((Button)e.widget).getSelection()){
					
					for(Button b : buttonBar){
						if (b != e.widget){
							b.setSelection(false);
						}
					}
				}
				preview.redraw();

				Event event = new Event();
				event.widget = CompositeBorder.this;
				CompositeBorder.this.notifyListeners(99, event);

			}
		});
	}
	
	private String getTypeName(int side){
		switch(getType(side)){
		
		
		case SWT.LINE_SOLID:
			return "solid"; //$NON-NLS-1$
		case SWT.LINE_DOT:
			return "dotted"; //$NON-NLS-1$
		case SWT.LINE_DASH:
			return "dashed"; //$NON-NLS-1$
		}
		return "none"; //$NON-NLS-1$
	}
	private int getType(int side){
		Button[] tab = null;
		switch(side){
		case BOTTOM:
			tab = bottomButtons;
			break;
		case TOP:
			tab = topButtons;
			break;
		case LEFT:
			tab = leftButtons;
			break;
		case RIGHT:
			tab = rightButtons;
			break;
		}
		
		
		int k = NONE;
		for(int i = 0; i < tab.length; i++){
			if (tab[i].getSelection()){
				k =  i;
			}
		}
		
		switch(k){
		case DASH:
			return SWT.LINE_DASH;
		case DOTTED:
			return SWT.LINE_DOT;
		case SOLID:
			return SWT.LINE_SOLID;
		}
		
		return NONE;
	}

	@Override
	public String getPreviousCss() {
		if(previousCss != null) {
			return previousCss;
		}
		return getCssToString();
	}
}
