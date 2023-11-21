package bpm.vanilla.server.client.ui.clustering.menu.uolap.viewers;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;

import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.UOlapQueryBean;
import bpm.vanilla.server.client.ui.clustering.menu.Activator;
import bpm.vanilla.server.client.ui.clustering.menu.Messages;

public class QueryLogsLabelProvider extends ColumnLabelProvider{
	public static enum Column{
		Group, Mdx, Time, Date
	}
	private static HashMap<Integer, Group> loadedGroups = new HashMap<Integer, Group>();
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm"); //$NON-NLS-1$
	private Column type;
	private StructuredViewer viewer;
	public QueryLogsLabelProvider(Column type, StructuredViewer viewer){
		this.type = type;
		this.viewer = viewer;
	}
	
	
	
	private String computeAverageTime(String element){
		List<UOlapQueryBean> l = ((HashMap<String, List<UOlapQueryBean>>)viewer.getInput()).get(element);
		
		long total = 0;
		for(UOlapQueryBean b : l){
			total += b.getExecutionTime();
		}
		
		return (float)(total / (1000.0 * (float)l.size())) + ""; //$NON-NLS-1$
		
	}
	
	private String countRuns(String element){
		List<UOlapQueryBean> l = ((HashMap<String, List<UOlapQueryBean>>)viewer.getInput()).get(element);
		return "" + l.size(); //$NON-NLS-1$
	}
	
	@Override
	public String getText(Object element) {
		if (element instanceof String){
			
			
			switch(type){
			case Date:
				return countRuns((String)element);
			case Time: 
				return computeAverageTime((String)element);
			}
		}
		
		
		if (!(element instanceof UOlapQueryBean)){
			return ""; //$NON-NLS-1$
		}
		UOlapQueryBean b = (UOlapQueryBean)element;
		switch(type){
		case Date:
			return sdf.format(b.getExecutionDate());
		case Time: 
			return ((float)(b.getExecutionTime() / 1000.)) + ""; //$NON-NLS-1$
		case Group:
			
			return getGroupName(b.getVanillaGroupId());
			
		default:
			return "";//b.getMdxQuery(); //$NON-NLS-1$
		}
		
	}
	
	public static String getGroupName(int groupId){
		return getGroup(groupId).getName();
	}

	public static Group getGroup(int groupId) {
		if (loadedGroups.get(groupId) == null){
			try{
				loadedGroups.put(groupId, Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroupById(groupId));
			}catch(Exception ex){
				return null;
			}
		}
		return loadedGroups.get(groupId);
	}
}
