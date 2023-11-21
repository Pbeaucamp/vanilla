package bpm.fd.design.ui.component.chart.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import bpm.fd.api.core.model.components.definition.chart.ChartRenderer;
import bpm.fd.design.ui.component.Messages;
/**
 * 
 * @author ludo
 * @deprecated : no more distinct renderer on chart
 */
public abstract class ChartRendererPage extends WizardPage{

	public static final String PAGE_NAME = "bpm.fd.design.ui.component.chart.pages.ChartRendererPage"; //$NON-NLS-1$
	public static final String PAGE_TITLE = Messages.ChartRendererPage_1;
	public static final String PAGE_DESCRIPTION = Messages.ChartRendererPage_2;

	
	private Button[] buttons = new Button[ChartRenderer.RENDERER_NAMES.length];
	
	
	public ChartRendererPage(){
		this(PAGE_NAME);
		this.setTitle(PAGE_TITLE);
		this.setDescription(PAGE_DESCRIPTION);
	}
			
	
	protected ChartRendererPage(String pageName) {
		super(pageName);
		
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		
		for(int i = 0; i < ChartRenderer.RENDERER_NAMES.length; i++){
//			if (i == ChartRenderer.OPEN_FLASH_CHART){
//				continue;
//			}
			buttons[i] = new Button(main, SWT.RADIO);
			buttons[i].setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			buttons[i].setText(ChartRenderer.RENDERER_NAMES[i]);
//			if (i != ChartRenderer.FUSION_CHART && i != ChartRenderer.FUSION_CHART_FREE && i!= ChartRenderer.JFREE_CHART){
//				buttons[i].setEnabled(false);
//			}
		}
		buttons[0].setSelection(true);
		
		setControl(main);
	}


	public ChartRenderer getRenderer() throws Exception{
		for(int i = 0; i < buttons.length; i++){
//			if (i == ChartRenderer.OPEN_FLASH_CHART){
//				continue;
//			}
			if (buttons[i].getSelection()){
				return ChartRenderer.getRenderer(i);
			}
		}
		throw new Exception(Messages.ChartRendererPage_3);
	}

}
