package bpm.fd.design.ui.editor.style;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.fd.design.ui.tools.ColorManager;

public class DialogBorders extends Dialog{
	private ComboViewer style;
	private Text width;
	private Text color;
	
	private CssClass css;
	
	public DialogBorders(Shell parentShell, CssClass css) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.css= css;
	}
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(3, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText("Border Style");
		
		style = new ComboViewer(main, SWT.READ_ONLY);
		style.getControl().setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		style.setContentProvider(new ArrayContentProvider());
		style.setInput(CssConstants.borderStyle.getValues());
		style.setLabelProvider(new LabelProvider());
		style.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				css.setValue(CssConstants.borderStyle.getName(), (String)((IStructuredSelection)event.getSelection()).getFirstElement());
				
			}
		});
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText("Border Width");
		
		width = new Text(main, SWT.BORDER);
		width.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		
		width.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				css.setValue(CssConstants.borderWidth.getName(), width.getText() + "px");
				
			}
		});
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText("Border Color");
		
		color = new Text(main, SWT.BORDER);
		color.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		color.setEditable(false);

		
		Button b = new Button(main, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false));
		b.setText("...");
		b.setToolTipText("Pickup border color");
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String colorCode = css.getValue(CssConstants.borderColor.getName());
				RGB rgb = getRgb(colorCode);
				ColorDialog d = new ColorDialog(getShell());
				d.setRGB(rgb);
				
				rgb = d.open();
				colorCode = getColorCode(rgb);
				Color c =ColorManager.getColorRegistry().get(colorCode);
				
				if (c == null && colorCode != null){
					ColorManager.getColorRegistry().put(colorCode, rgb);
				}
				color.setBackground(ColorManager.getColorRegistry().get(colorCode));

				css.setValue(CssConstants.borderColor.getName(), colorCode);
			}
		});
		
		
		
		
		return main;
	}
	
	private RGB getRgb(String colorCode){
		try{
			String code = colorCode.replace("#", "");
			int r = Integer.parseInt(code.substring(0, 2), 16);
			int g = Integer.parseInt(code.substring(2, 4), 16);
			int b = Integer.parseInt(code.substring(4, 6), 16);
			
			return new RGB(r, g, b);
			
		}catch(Exception ex){
			return new RGB(0,0,0);
		}
	}
	private String getColorCode(RGB rgb){
		if (rgb != null){
			StringBuffer buf = new StringBuffer("#");
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
			buf.append(r); buf.append(g);buf.append(b);
			return buf.toString();
		}
		return "#000000";
	}

	@Override
	protected void initializeBounds() {
		getShell().setText("Css Background - " + css.getName());
		getShell().setSize(300, 180);
		
		
		if (css.getValue(CssConstants.borderStyle.getName()) != null){
			style.setSelection(new StructuredSelection(css.getValue(CssConstants.borderStyle.getName())));
		}
		
		if (css.getValue(CssConstants.borderWidth.getName()) != null){
			width.setText(css.getValue(CssConstants.borderWidth.getName()).replace("px", ""));
		}
		else{
			width.setText("1");
		}
		
		width.addVerifyListener(new VerifyListener() {  
			  
		    @Override  
		    public void verifyText(final VerifyEvent event) {  
		        switch (event.keyCode) {  
		            case SWT.BS:           // Backspace  
		            case SWT.DEL:          // Delete  
		            case SWT.HOME:         // Home  
		            case SWT.END:          // End  
		            case SWT.ARROW_LEFT:   // Left arrow  
		            case SWT.ARROW_RIGHT:  // Right arrow  
		                return;  
		        }  
		  
		        if (!Character.isDigit(event.character) || event.character == '.') {  
		            event.doit = false;  // disallow the action  
		        }  
		    }  
		  
		});  
		
		String colorCode = css.getValue(CssConstants.borderColor.getName());
		RGB rgb = getRgb(colorCode);

		if (rgb != null){
			if (colorCode != null){
				Color c = ColorManager.getColorRegistry().get(colorCode);
				if (c == null){
					ColorManager.getColorRegistry().put(colorCode, rgb);
				}
				color.setBackground(ColorManager.getColorRegistry().get(colorCode));
			}
			
			
		}
	}


}
