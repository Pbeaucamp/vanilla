package bpm.vanilla.repository.ui.viewers;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;


public class RepositoryViewerComparator extends ViewerComparator{

	
	private static final int DIR_DIR = 0;
	private static final int DIR_IT = 1;
	private static final int IT_IT = 3;
	private static final int IT_DIR = 2;
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		
		
		switch(getCase(e1, e2)){
		case DIR_DIR:
			return ((RepositoryDirectory)e1).getName().compareTo(((RepositoryDirectory)e2).getName());
		case DIR_IT :
			return -1;
		case IT_IT:
			RepositoryItem i1 = (RepositoryItem)e1;
			RepositoryItem i2 = (RepositoryItem)e2;
			
			if (i1.getType() != i2.getType()){
				return Integer.valueOf(i1.getType()).compareTo(i2.getType()) ;
			}
			else if (i1.getSubtype() != i2.getSubtype()){
				return Integer.valueOf(i1.getSubtype()).compareTo(i2.getSubtype()) ;
			}
			else{
				return i1.getItemName().compareTo(i2.getItemName());
			}
			
		case IT_DIR:
			return 1;
			
			
		}
		return super.compare(viewer, e1, e2);
	}

	
	private int getCase(Object o1, Object o2){
		int i1 = (o1 instanceof RepositoryDirectory) ? 0 : 1;
		int i2 = (o2 instanceof RepositoryDirectory) ? 0 : 2;
		
		return i1+i2;
		
		
	}
}
