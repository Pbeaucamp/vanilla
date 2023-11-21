package bpm.birt.fusionmaps.ui.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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

import bpm.vanilla.map.core.design.fusionmap.ColorRange;


public class ColorRangeDialog extends Dialog{
	
	private List<ColorRange> rangeColor;

	private Text name;
	private Text colorHex;
	private Text minValue;
	private Text maxValue;
	private Button pickColor;

	public ColorRangeDialog(Shell parentShell, List<ColorRange> rangeColor) {
		super(parentShell);
		this.rangeColor = rangeColor;
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setSize(500, 400);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(3, false));
		main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label lblName = new Label(main, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblName.setText("Select a name for your range color: ");
		
		name = new Text(main, SWT.BORDER);
		name.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));

		Label lblHex = new Label(main, SWT.NONE);
		lblHex.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblHex.setText("Select the color: ");
		
		colorHex = new Text(main, SWT.BORDER);
		colorHex.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		
		pickColor = new Button(main, SWT.PUSH);
		pickColor.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		pickColor.setText("Pick Color");
		pickColor.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				ColorDialog dial = new ColorDialog(getShell());
				RGB rgb = dial.open();
				String rHex = Integer.toHexString(rgb.red);
				if(rHex.equals("0")){
					rHex = "00";
				}
		        String gHex = Integer.toHexString(rgb.green);
				if(gHex.equals("0")){
					gHex = "00";
				}
		        String bHex = Integer.toHexString(rgb.blue);
				if(bHex.equals("0")){
					bHex = "00";
				} 
		        String hex = rHex + gHex + bHex ;
		        
		        Color color = new Color(getShell().getDisplay(), rgb);
		        
		        colorHex.setText(hex);
		        colorHex.setBackground(color);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
				
			}	
		});	

		Label lblMin = new Label(main, SWT.NONE);
		lblMin.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblMin.setText("Select the minimum value: ");
		
		minValue = new Text(main, SWT.BORDER);
		minValue.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));

		Label lblMax = new Label(main, SWT.NONE);
		lblMax.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblMax.setText("Select the maximum value: ");
		
		maxValue = new Text(main, SWT.BORDER);
		maxValue.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		
		return main;
	}
	
	@Override
	protected void okPressed() {
		if(!name.equals("") && !colorHex.equals("") && !minValue.equals("") && !maxValue.equals("")){
			String nameCol = name.getText();
			String hex = colorHex.getText();
			int minVal = Integer.parseInt(minValue.getText());
			int maxVal = Integer.parseInt(maxValue.getText());
			ColorRange color = new ColorRange(nameCol, hex, minVal, maxVal);
			rangeColor.add(color);
		}
		super.okPressed();
	}
}
