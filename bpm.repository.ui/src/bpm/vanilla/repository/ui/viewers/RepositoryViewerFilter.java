package bpm.vanilla.repository.ui.viewers;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class RepositoryViewerFilter extends ViewerFilter{

	private int[] type;
	private int[] subtype;
	
	
	public RepositoryViewerFilter(int[] type, int[] subtype){
		this.type = type;
		this.subtype = subtype;
	}
	
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		
		if (element instanceof RepositoryItem){
			
			for(int t : type){
				if (t == ((RepositoryItem)element).getType()){
					if (t == IRepositoryApi.CUST_TYPE){
						
						if (subtype == null){
							return true;
						}
						for(int s : subtype){
							if (s == ((RepositoryItem)element).getSubtype()){
								return true;
							}
						}
					}
					return true;
				}
			}
			
			return false;
		}
		else if (element instanceof RepositoryDirectory){
			return true;
		}
		
		
		return false;
	}

}
