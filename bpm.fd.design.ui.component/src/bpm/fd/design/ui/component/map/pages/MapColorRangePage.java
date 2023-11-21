package bpm.fd.design.ui.component.map.pages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import bpm.fd.design.ui.component.Messages;
import bpm.fd.design.ui.component.map.CompositeColorRange;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;

public class MapColorRangePage extends WizardPage{
	public static final String PAGE_NAME = "bpm.fd.design.ui.component.map.pages.ColorRangePage"; //$NON-NLS-1$
	public static final String PAGE_TITLE = Messages.MapColorRangePage_1;
	public static final String PAGE_DESCRIPTION = Messages.MapColorRangePage_2;

	private CompositeColorRange colorRanges;
	public MapColorRangePage(){
		this(PAGE_NAME);
		this.setTitle(PAGE_TITLE);
		this.setDescription(PAGE_DESCRIPTION);
	}
	
	protected MapColorRangePage(String pageName) {
		super(pageName);
	}
	
	public void createControl(Composite parent) {
		colorRanges = new CompositeColorRange();
		colorRanges.createContent(parent);
		
		colorRanges.getClient().addListener(SWT.Modify, new Listener() {
			
			public void handleEvent(Event event) {
				getContainer().updateButtons();
				
			}
		});
		colorRanges.setInput(new ArrayList<ColorRange>());
		setControl(colorRanges.getClient());
	}
	
	@Override
	public boolean isPageComplete() {
		return true;
	}
	
	public List<ColorRange> getColorRanges(){
		return colorRanges.getColorRanges();
	}
}
