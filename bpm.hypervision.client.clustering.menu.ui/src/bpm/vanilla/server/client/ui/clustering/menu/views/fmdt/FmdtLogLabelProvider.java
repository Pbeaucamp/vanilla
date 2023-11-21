package bpm.vanilla.server.client.ui.clustering.menu.views.fmdt;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import bpm.vanilla.platform.core.beans.FMDTQueryBean;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;

public class FmdtLogLabelProvider extends ColumnLabelProvider{
	private static Color RED = new Color(Display.getDefault(), 240, 15, 50);
	public static enum Type{
		REP, IT, GROUP, DATE, TIME, EFFECTIVE, WEIGHT;
	}
	private Type type;
	private SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yy HH:mm"); //$NON-NLS-1$
	private SimpleDateFormat sdfTime= new SimpleDateFormat("HH:mm:ss,SSS"); //$NON-NLS-1$
	
	/*
	 * shared object to avoid calling the server
	 */
	private static List<Group> groups;
	private static List<Repository> repositories;
	
	
	public static void shareVanillaObjects(List<Group> groups, List<Repository> repositories){
		FmdtLogLabelProvider.groups = groups;
		FmdtLogLabelProvider.repositories = repositories;
	}
	
	private String getVanillaObjectName(FMDTQueryBean  bean){
		if (type == Type.GROUP){
			for(Group g : groups){
				if (g.getId().intValue() == bean.getVanillaGroupId()){
					return g.getName();
				}
			}
			return bean.getVanillaGroupId() + ""; //$NON-NLS-1$
		}
		else if (type == Type.REP){
			for(Repository rep : repositories){
				if (rep.getId() == bean.getRepositoryId()){
					return rep.getName();
				}
			}
			return bean.getRepositoryId() + ""; //$NON-NLS-1$
		}
		return null;
		
	}
	
	public FmdtLogLabelProvider(Type type){
		this.type = type;
		sdfTime.setTimeZone(TimeZone.getDefault());
	}
	@Override
	public String getText(Object element) {
		FMDTQueryBean bean = (FMDTQueryBean)element;
		switch(type){
		case DATE:
			if (bean.getDate() != null){
				return sdfDate.format(bean.getDate());
			}
			return ""; //$NON-NLS-1$
		case EFFECTIVE:
			return bean.getEffectiveQuery();
		case GROUP:
			return getVanillaObjectName(bean);
		case IT:
			return bean.getDirectoryItemId()  + ""; //$NON-NLS-1$
		case REP:
			return getVanillaObjectName(bean);
		case TIME:
			if (bean.getDuration() > 0){
				double duration = bean.getDuration();
				Double mill = duration % 1000;
				
				double secTmp = Math.floor(duration/1000);
				Double sec = secTmp % 60;
				
				double minTmp = Math.floor(secTmp/60);
				Double min = minTmp % 60;
				
				double hTmp = Math.floor(minTmp/60);
				Double h = hTmp % 24;
				
				return h.intValue() + " hours, " + min.intValue() + " mins, " + sec.intValue() + " seconds, " + mill.intValue() + " milliseconds";
//				return sdfTime.format(new Date(bean.getDuration()));
			}
			return ""; //$NON-NLS-1$
		case WEIGHT:
			return bean.getWeight() + ""; //$NON-NLS-1$
		}
		return ""; //$NON-NLS-1$
	}

	@Override
	public Color getBackground(Object element) {
		FMDTQueryBean bean = (FMDTQueryBean)element;
		if (bean.getFailureCause() != null){
			return RED;
		}
		return super.getBackground(element);
	}
}
