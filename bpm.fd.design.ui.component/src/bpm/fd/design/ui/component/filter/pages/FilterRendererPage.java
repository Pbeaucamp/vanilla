package bpm.fd.design.ui.component.filter.pages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import bpm.fd.api.core.model.components.definition.filter.FilterRenderer;
import bpm.fd.design.ui.component.Messages;

public class FilterRendererPage extends WizardPage{

	public static final String PAGE_NAME = "bpm.fd.design.ui.component.filter.pages.FilterRendererPage"; //$NON-NLS-1$
	public static final String PAGE_TITLE = Messages.FilterRendererPage_1;
	public static final String PAGE_DESCRIPTION = Messages.FilterRendererPage_2;

	
	private Button[] buttons = new Button[FilterRenderer.RENDERER_NAMES.length];
	
	private List<Integer> mapper = new ArrayList<Integer>();
	
	public FilterRendererPage(){
		this(PAGE_NAME);
		this.setTitle(PAGE_TITLE);
		this.setDescription(PAGE_DESCRIPTION);
	}
			
	
	protected FilterRendererPage(String pageName) {
		super(pageName);
		
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		int k = 0;
		for(int i = 0; i < FilterRenderer.RENDERER_NAMES.length; i++){
			if (i == FilterRenderer.RADIOBUTTON|| i==FilterRenderer.DROP_DOWN_LIST_BOX || i == FilterRenderer.TEXT_FIELD || i == FilterRenderer.DATE_PIKER || i== FilterRenderer.MENU || i== FilterRenderer.DYNAMIC_TEXT || i==FilterRenderer.SLIDER) {
				buttons[k] = new Button(main, SWT.RADIO);
				buttons[k].setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
				buttons[k].setText(FilterRenderer.RENDERER_NAMES[i]);
			}
			mapper.add(k);
			k++;
			
			
		}
		buttons[0].setSelection(true);
		
		setControl(main);
	}


	public FilterRenderer getRenderer() throws Exception{
		for(int i = 0; i < buttons.length; i++){
			if (buttons[i] != null && buttons[i].getSelection()){
				return FilterRenderer.getRenderer(mapper.get(i));
			}
		}
		throw new Exception(Messages.FilterRendererPage_3);
	}


	

	
}
