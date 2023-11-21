package bpm.workflow.ui.viewer;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.workflow.runtime.model.WorkfowModelParameter;
import bpm.workflow.runtime.model.activities.TaskListActivity;
import bpm.workflow.runtime.resources.servers.DataBaseServer;
import bpm.workflow.runtime.resources.servers.FileServer;
import bpm.workflow.runtime.resources.servers.FreemetricServer;
import bpm.workflow.runtime.resources.servers.Server;
import bpm.workflow.runtime.resources.servers.ServerMail;
import bpm.workflow.runtime.resources.variables.Variable;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.icons.IconsNames;

public class TreeLabelProvider extends LabelProvider {
	
	@Override
	public String getText(Object element) {
		
		
		if (element instanceof TreeObject){
			return ((TreeObject)element).getName();
		}
		else if (element instanceof Server){
			return ((Server)element).getName();
		}
		else if (element instanceof Variable) {
			return ((Variable)element).getName();
		}
		else if (element instanceof WorkfowModelParameter) {
			return ((WorkfowModelParameter)element).getName();
		}

		return super.getText(element);
	}

	@Override
	public Image getImage(Object element) {
		return getImageFor(element);
	}
	
	
	public static Image getImageFor(Object o){
		 if (o instanceof FreemetricServer) {
				return Activator.getDefault().getImageRegistry().get(IconsNames.FREEMETRICS_SERVER);
			}
		if (o instanceof DataBaseServer){
			return Activator.getDefault().getImageRegistry().get(IconsNames.SERVER_DATABASE);
		}
		else if (o instanceof Variable){
			return Activator.getDefault().getImageRegistry().get(IconsNames.VARIABLE_16);
		}
		else if (o instanceof TaskListActivity){
			return Activator.getDefault().getImageRegistry().get(IconsNames.TASKLIST);
		}
		else if (o instanceof TreeStaticObject || o instanceof TreeDirectory){
			return Activator.getDefault().getImageRegistry().get(IconsNames.TABLE_16);
		}
		else if (o instanceof ServerMail){
			return Activator.getDefault().getImageRegistry().get(IconsNames.SERVER_MAIL);
		}
		else if (o instanceof FileServer){
			return Activator.getDefault().getImageRegistry().get(IconsNames.FILESERVER_16);
		}
		else if (o instanceof FileServer) {
			return Activator.getDefault().getImageRegistry().get(IconsNames.FILESERVER_16);
		}
		
		else if (o instanceof TreeItem) {
			Image img = null;
			RepositoryItem item = ((TreeItem) o).getItem();
			int type = item.getType();
			switch (type) {
			case IRepositoryApi.FASD_TYPE:
				img = Activator.getDefault().getImageRegistry().get(IconsNames.FASD);
				break;
				
			case IRepositoryApi.FMDT_TYPE:
				img = Activator.getDefault().getImageRegistry().get(IconsNames.FMDT);
				break;
				
			case IRepositoryApi.FWR_TYPE:
				img = Activator.getDefault().getImageRegistry().get(IconsNames.FWR);
				break;
				
			case IRepositoryApi.BIW_TYPE:
				img = Activator.getDefault().getImageRegistry().get(IconsNames.BIW);
				break;
				
			case IRepositoryApi.FAV_TYPE:
				img = Activator.getDefault().getImageRegistry().get(IconsNames.FASD_VIEW);
				break;
				
			case IRepositoryApi.FD_TYPE:
				img = Activator.getDefault().getImageRegistry().get(IconsNames.FD);
				break;
				
			case IRepositoryApi.GTW_TYPE:
				img = Activator.getDefault().getImageRegistry().get(IconsNames.GATEWAY_16);
				break;
				
			case IRepositoryApi.CUST_TYPE:
				if (item.getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE) {
					img = Activator.getDefault().getImageRegistry().get(IconsNames.BIRT);
				}
				else if (item.getSubtype() == IRepositoryApi.JASPER_REPORT_SUBTYPE) {
					img = Activator.getDefault().getImageRegistry().get(IconsNames.JRXML);
				} 
				else if (item.getSubtype() == IRepositoryApi.ORBEON_XFORMS) {
					img = Activator.getDefault().getImageRegistry().get(IconsNames.ORBEON_16);
				} 
				
				break;
				

			default:
				break;
			}
			return img;
		}
		return null;
	}
	

}
