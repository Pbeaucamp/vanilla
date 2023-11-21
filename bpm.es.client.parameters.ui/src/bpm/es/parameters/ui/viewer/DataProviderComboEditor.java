package bpm.es.parameters.ui.viewer;

import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import bpm.es.parameters.ui.Messages;
import bpm.es.parameters.ui.views.DatasProviderHelper;
import bpm.vanilla.platform.core.repository.DatasProvider;


public class DataProviderComboEditor extends ComboBoxCellEditor{
	private DatasProviderHelper helper;
	public DataProviderComboEditor(Composite parent, DatasProviderHelper helper){
		super(parent, new String[]{}, SWT.READ_ONLY);
		this.helper = helper;
	}
	
		
	@Override
	public String[] getItems() {
		String[] items = new String[helper.getDatasProviders().size() + 1];
		
		items[0] = Messages.DataProviderComboEditor_0;
		int count = 1;
		
		for(DatasProvider d : helper.getDatasProviders()){
			items[count++] = d.getName();
		}
		return items;
	}
	
	
	
	public int getProviderIndexFromId(int id){
		int i = 0;
		for(DatasProvider d : helper.getDatasProviders()){
			if (d.getId() == id){
				return i + 1;
			}
			i++;
		}
		return -1;
	}
	
	public DatasProvider getProviderFromIndex(int index){
		
		if (index <= 0){
			return null;
		}
		int i = 0;
		for(DatasProvider d : helper.getDatasProviders()){
			if (i == index - 1){
				return d;
			}
			i++;
		}
		
		return null;
	}
}
