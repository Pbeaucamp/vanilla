package bpm.fd.design.ui.component.map.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import bpm.fd.api.core.model.components.definition.maps.MapRenderer;
import bpm.fd.design.ui.component.Messages;

public class MapRendererPage extends WizardPage{

	public static final String PAGE_NAME = "bpm.fd.design.ui.component.map.pages.MapRendererPage"; //$NON-NLS-1$
	public static final String PAGE_TITLE = Messages.MapRendererPage_1;
	public static final String PAGE_DESCRIPTION = Messages.MapRendererPage_2;

	
	private Button[] buttons = new Button[MapRenderer.RENDERER_NAMES.length];
	
	
	public MapRendererPage(){
		this(PAGE_NAME);
		this.setTitle(PAGE_TITLE);
		this.setDescription(PAGE_DESCRIPTION);
	}
			
	
	protected MapRendererPage(String pageName) {
		super(pageName);
		
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		
		for(int i = 0; i < MapRenderer.RENDERER_NAMES.length; i++){
//			if (i == ChartRenderer.OPEN_FLASH_CHART){
//				continue;
//			}
			buttons[i] = new Button(main, SWT.RADIO);
			buttons[i].setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			buttons[i].setText(MapRenderer.RENDERER_NAMES[i]);
//			if (i != ChartRenderer.FUSION_CHART && i != ChartRenderer.FUSION_CHART_FREE && i!= ChartRenderer.JFREE_CHART){
//				buttons[i].setEnabled(false);
//			}
//			if (i != MapRenderer.VANILLA_FUSION_MAP){
//				buttons[i].setEnabled(false);
//			}
		}
		buttons[0].setSelection(true);
		
		setControl(main);
	}


	public MapRenderer getRenderer() throws Exception{
		for(int i = 0; i < buttons.length; i++){
//			if (i == ChartRenderer.OPEN_FLASH_CHART){
//				continue;
//			}
			if (buttons[i].getSelection()){
				return MapRenderer.getRenderer(i);
			}
		}
		throw new Exception(Messages.MapRendererPage_3);
	}

}
