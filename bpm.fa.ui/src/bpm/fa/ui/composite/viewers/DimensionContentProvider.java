package bpm.fa.ui.composite.viewers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import bpm.fa.api.olap.Dimension;
import bpm.fa.api.olap.Hierarchy;
import bpm.fa.api.olap.MeasureGroup;
import bpm.fa.api.olap.OLAPCube;
import bpm.fa.api.olap.OLAPMember;
import bpm.fa.api.olap.OLAPStructure;
import bpm.united.olap.api.runtime.IRuntimeContext;

public class DimensionContentProvider implements ITreeContentProvider{

	public static final int TYPE_ONLY_DIMENSION = 0;
	public static final int TYPE_ONLY_MEASURE = 1;
	public static final int TYPE_BOTH = 2;
	private OLAPCube cube;
	private int type;
	
	public DimensionContentProvider(int type){
		this.type = type;
	}
	

	@Override
	public Object[] getChildren(Object parentElement) {
		List l = new ArrayList();
		if (parentElement instanceof Dimension){
			l.addAll(((Dimension)parentElement).getHierarchies());
		}
		
		else if (parentElement instanceof Hierarchy){
			l.add(((Hierarchy)parentElement).getDefaultMember());
		}
		
		else if (parentElement instanceof OLAPMember){
			if (!((OLAPMember)parentElement).isSynchro()){
				try {
					cube.addChilds(((OLAPMember)parentElement), null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			l.addAll(((OLAPMember)parentElement).getMembers());
		}
		else if (parentElement instanceof MeasureGroup){
			l.addAll(((MeasureGroup)parentElement).getMeasures());
		}
		return l.toArray(new Object[l.size()]);
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof Hierarchy){
			for(Dimension d : cube.getDimensions()){
				if (((Hierarchy)element).getUniqueName().startsWith(d.getUniqueName())){
					return d;
				}
			}
		}
		else if (element instanceof OLAPMember){
//			((OLAPMember)element).get
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		
		if (element instanceof Dimension){
			return !((Dimension)element).getHierarchies().isEmpty();
		}
		else if (element instanceof Hierarchy){
			return ((Hierarchy)element).getDefaultMember() != null;
		}
		else if (element instanceof OLAPMember){
			
			if (((OLAPMember)element).isSynchro()){
				return !((OLAPMember)element).getMembers().isEmpty();
			}
			else{
				if (((OLAPMember)element).getHiera().getLevel().size() > ((OLAPMember)element).getLevelDepth()){
					return true;
				}
				
//				try {
//					return struct.addChilds((OLAPMember)element);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
			}
		}
		else if (element instanceof MeasureGroup){
			return !((MeasureGroup)element).getMeasures().isEmpty();
		}
		
		return false;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		cube = (OLAPCube)inputElement;

		List l = new ArrayList();
		
		switch (type) {
		case TYPE_BOTH:
			l.addAll(cube.getDimensions());
			l.addAll(cube.getMeasures());
			break;
		case TYPE_ONLY_DIMENSION:
			l.addAll(cube.getDimensions());
			break;
		case TYPE_ONLY_MEASURE:
			for(MeasureGroup group : cube.getMeasures()){
				l.addAll(group.getMeasures());
			}
			
			break;
		}

		return l.toArray(new Object[l.size()]);
	}

	@Override
	public void dispose() {
		
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		
	}

}
