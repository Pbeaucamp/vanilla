package bpm.vanilla.server.client.ui.clustering.menu.uolap.viewers;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import bpm.vanilla.platform.core.beans.UOlapPreloadBean;
import bpm.vanilla.server.client.ui.clustering.menu.Messages;

public class QueryPreloadLabelProvider extends ColumnLabelProvider{
	public static enum Column{
		Group, Mdx
	}
	
	
	private Column type;
	public QueryPreloadLabelProvider(Column type){
		this.type = type;
	}
	
	@Override
	public String getText(Object element) {
		if (!(element instanceof UOlapPreloadBean)){
			return ""; //$NON-NLS-1$
		}
		UOlapPreloadBean b = (UOlapPreloadBean)element;
		switch(type){
		case Group:
			
			
			return QueryLogsLabelProvider.getGroupName(b.getVanillaGroupId());
			
		default:
			return "";//b.getMdxQuery(); //$NON-NLS-1$
		}
		
	}
}
