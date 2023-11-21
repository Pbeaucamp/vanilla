package bpm.forms.design.ui.composite.tools;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.layout.GridData;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.Section;

public class ExpantionListener implements IExpansionListener{

	private List<Section> registeredSection = new ArrayList<Section>();
	
	
	public void registerSection(Section s){
		registeredSection.add(s);
	}
	
	@Override
	public void expansionStateChanged(ExpansionEvent e) {
		
		
	}

	@Override
	public void expansionStateChanging(ExpansionEvent e) {
			if (e.getState()){
			
			for(Section s : registeredSection){
				if (s != e.widget){
					s.setExpanded(false);
					s.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
				}
			}
			((Section)e.getSource()).setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		}
			else{
				((Section)e.getSource()).setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
			}
	}
	
}