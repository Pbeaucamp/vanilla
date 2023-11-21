package bpm.fa.ui.composite.viewers;

import org.eclipse.jface.viewers.LabelProvider;

import bpm.united.olap.api.model.Dimension;
import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.Measure;



public class UnitedOlapLabelProvider extends LabelProvider{

	@Override
	public String getText(Object element) {
		if (element instanceof Dimension){
			return ((Dimension)element).getName();
		}
		else if (element instanceof Hierarchy){
			return ((Hierarchy)element).getName();
		}
		else if (element instanceof Level){
			return ((Level)element).getName();
		}
		else if (element instanceof Measure){
			return ((Measure)element).getName();
		}
		
		return element.toString();
	}
}