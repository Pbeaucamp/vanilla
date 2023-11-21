
package bpm.fd.design.ui.component.chart.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import bpm.fd.api.core.model.components.definition.chart.ChartNature;
import bpm.fd.api.core.model.components.definition.chart.ChartRenderer;
import bpm.fd.design.ui.component.Messages;

public class ChartNaturePage extends WizardPage{

	public static final String PAGE_NAME = "bpm.fd.design.ui.component.chart.pages.ChartNaturePage"; //$NON-NLS-1$
	public static final String PAGE_TITLE = Messages.ChartNaturePage_1;
	public static final String PAGE_DESCRIPTION = Messages.ChartNaturePage_2;

	
	private Button[] buttons = new Button[ChartNature.NATURE_NAMES.length];
//	private Button btnFs;
//	private Button btnHc;

	
	public ChartNaturePage(){
		this(PAGE_NAME);
		this.setTitle(PAGE_TITLE);
		this.setDescription(PAGE_DESCRIPTION);
	}
			
	
	protected ChartNaturePage(String pageName) {
		super(pageName);
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		
//		Group rendererGroup = new Group(main, SWT.NONE);
//		rendererGroup.setLayout(new GridLayout(2, true));
//		rendererGroup.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		rendererGroup.setText("Chart renderer api");
		
//		btnFs = new Button(rendererGroup, SWT.RADIO);
//		btnFs.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
//		btnFs.setText("FusionCharts");
//		btnFs.setSelection(true);
//		btnFs.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				initForRenderer(ChartRenderer.getRenderer(ChartRenderer.FUSION_CHART));
//			}
//		});
//		
//		btnHc = new Button(rendererGroup, SWT.RADIO);
//		btnHc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
//		btnHc.setText("HighCharts");
//		btnHc.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				initForRenderer(ChartRenderer.getRenderer(ChartRenderer.HIGHCHART));
//			}
//		});
		
		Group monoGroup = new Group(main, SWT.NONE);
		monoGroup.setLayout(new GridLayout(2, true));
		monoGroup.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		monoGroup.setText(Messages.ChartNaturePage_3);
		
		Group multiGroup = new Group(main, SWT.NONE);
		multiGroup.setLayout(new GridLayout(2, true));
		multiGroup.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		multiGroup.setText(Messages.ChartNaturePage_4);
		
		for(int i = 0; i < ChartNature.NATURE_NAMES.length ; i++){
			if(isNatureAllowed(i)) {
				try{
					if (ChartNature.getNature(i).isMultiSerie()){
						buttons[i] = new Button(multiGroup, SWT.RADIO);
					}
					else{
						if(ChartNature.getNature(i).getNature() == 14) {
							continue;
						}
						buttons[i] = new Button(monoGroup, SWT.RADIO);
					}
					
					buttons[i].addSelectionListener(new SelectionAdapter(){
	
						public void widgetSelected(SelectionEvent e) {
							if (((Button)e.widget).getSelection()){
								for(Button b : buttons){
									if (b != e.widget){
										try {
											b.setSelection(false);
										} catch(Exception e1) {
										}
									}
								}
							}
							getWizard().getNextPage(ChartNaturePage.this);
							getContainer().updateButtons();
						}
						
					});
				}catch(Exception e){
					
				}
				
				
				buttons[i].setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
				buttons[i].setText(ChartNature.NATURE_NAMES[i]);
			}
			
		}
		buttons[0].setSelection(true);
		
		setControl(main);
	}

	private boolean isNatureAllowed(int i) {
//		if(btnFs.getSelection()) {
//			if(ChartRenderer.getRenderer(ChartRenderer.FUSION_CHART).supportNature(i)) {
//				return true;
//			}
//		}
//		else {
//			if(ChartRenderer.getRenderer(ChartRenderer.HIGHCHART).supportNature(i)) {
//				return true;
//			}
//		}
//		return false;
		return true;
	}


	public void initForRenderer(ChartRenderer renderer){
		for(int i = 0 ; i < ChartNature.NATURE_NAMES.length; i++){
			try {
				buttons[i].setEnabled(renderer.supportNature(i));
			} catch (Exception e) {
				
			}
		}
	}

	public ChartNature getNature() throws Exception{
		for(int i = 0; i < buttons.length; i++){
			try {
				if (buttons[i].getSelection()){
					return ChartNature.getNature(i);
				}
			} catch (Exception e) {
				
			}
		}
		throw new Exception(Messages.ChartNaturePage_5);
	}


	public ChartRenderer getRenderer() {
//		if(btnFs.getSelection()) {
			return ChartRenderer.getRenderer(ChartRenderer.FUSION_CHART);
//		}
//		else if(btnHc.getSelection()) {
//			return ChartRenderer.getRenderer(ChartRenderer.HIGHCHART);
//		}
//		return null;
	}

}
