package bpm.es.sessionmanager.views.providers;

import java.text.SimpleDateFormat;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import bpm.vanilla.platform.core.beans.VanillaLogs;

public class LogLabelProvider extends LabelProvider {
	private SimpleDateFormat dayFormat = new SimpleDateFormat("EEE, d MMM yyyy"); //$NON-NLS-1$
	
	public String getText(Object obj) {
		//return obj.toString();
		if (obj instanceof VanillaLogs) {
			return dayFormat.format(((VanillaLogs)obj).getDate());
		}
		
		return ""; //$NON-NLS-1$
	}
	public Image getImage(Object obj) {
//		ImageRegistry reg = Activator.getDefault().getImageRegistry();
//		if (obj instanceof TreeUser) {
//			return reg.get("user");
//		}
//		else if (obj instanceof TreeGroup) {
//			return reg.get("group");
//		}
//		else if (obj instanceof TreeRole) {
//			return reg.get("role");
//		}
//		else if (obj instanceof TreeJetty) {
//			return reg.get("jetty_on");
//		}
//		else if (obj instanceof TreeDirectory){
//			return reg.get("folder");
//		}
//		else if (obj instanceof TreeDatasource){
//			return reg.get("datasource");
//		}
//		else if (obj instanceof TreeRepositoryDefinition){
//			return reg.get("repository");
//		}
//		else if (obj instanceof TreeVariable){
//			return reg.get("variable");
//		}
//		else if (obj instanceof TreeItem){
//			String key =  getKeyForType(((TreeItem)obj).getItem().getType(), ((TreeItem)obj).getItem());
//			return reg.get(key);
//		}
				
		//return reg.get("default");
		return null;
	}
}
