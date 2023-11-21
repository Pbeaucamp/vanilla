package bpm.fa.ui.composite.viewers;

import org.eclipse.jface.viewers.LabelProvider;

import bpm.fa.api.olap.Dimension;
import bpm.fa.api.olap.Hierarchy;
import bpm.fa.api.olap.Level;
import bpm.fa.api.olap.Measure;
import bpm.fa.api.olap.MeasureGroup;
import bpm.fa.api.olap.OLAPMember;

public class StructureLabelProvider extends LabelProvider{

	@Override
	public String getText(Object element) {
		if (element instanceof Dimension){
			return ((Dimension)element).getName();
		}
		else if (element instanceof Hierarchy){
			return ((Hierarchy)element).getUniqueName();
		}
		else if (element instanceof Level){
			return ((Level)element).getName();
		}
		
		else if (element instanceof OLAPMember){
			return ((OLAPMember)element).getCaption();
		}
		else if (element instanceof Measure){
			return ((Measure)element).getName();
		}
		else if (element instanceof MeasureGroup){
			return ((MeasureGroup)element).getName();
		}
		return element.toString();
	}
}
