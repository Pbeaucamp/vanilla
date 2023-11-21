package bpm.metadata.ui.birtreport.viewer;

import metadataclient.Activator;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import bpm.metadata.resource.ComplexFilter;
import bpm.metadata.resource.Filter;
import bpm.metadata.resource.ListOfValue;
import bpm.metadata.resource.Prompt;
import bpm.metadata.resource.SqlQueryFilter;
import bpm.metadata.resource.complex.FmdtDimension;
import bpm.metadata.ui.birtreport.trees.TreeBusinessTable;
import bpm.metadata.ui.birtreport.trees.TreeColumn;
import bpm.metadata.ui.birtreport.trees.TreeDataStream;
import bpm.metadata.ui.birtreport.trees.TreeDataStreamElement;
import bpm.metadata.ui.birtreport.trees.TreeModel;
import bpm.metadata.ui.birtreport.trees.TreePackage;
import bpm.metadata.ui.birtreport.trees.TreeParent;
import bpm.metadata.ui.birtreport.trees.TreeRelation;
import bpm.metadata.ui.birtreport.trees.TreeResource;

public class TreeLabelProvider extends LabelProvider {
	
	private static final Image[] img = new Image[]{};
	
	@Override
	public String getText(Object obj) {
		return obj.toString();
	}
	@Override
	public Image getImage(Object obj) {
		if (obj instanceof TreeBusinessTable){
			return Activator.getDefault().getImageRegistry().get("bus_table"); //$NON-NLS-1$
		}
//		else if (obj instanceof TreeTable){
//			return Activator.getDefault().getImageRegistry().get("phy_table"); //$NON-NLS-1$
//		}
		else if (obj instanceof TreeDataStream){
			if (((TreeDataStream)obj).getDataStream().getGenericFilters().isEmpty()){
				return Activator.getDefault().getImageRegistry().get("log_table"); //$NON-NLS-1$	
			}
			else{
				return Activator.getDefault().getImageRegistry().get("log_table_filtered"); //$NON-NLS-1$
			}
			
		}
		else if (obj instanceof TreeDataStreamElement){
			return Activator.getDefault().getImageRegistry().get("log_column"); //$NON-NLS-1$
		}
		else if (obj instanceof TreeColumn){
			return Activator.getDefault().getImageRegistry().get("phy_column"); //$NON-NLS-1$
		}
		else if (obj instanceof TreeRelation){
			return Activator.getDefault().getImageRegistry().get("relation"); //$NON-NLS-1$
		}
		else if (obj instanceof TreeRelation){
			return Activator.getDefault().getImageRegistry().get("relation"); //$NON-NLS-1$
		}
		else if (obj instanceof TreePackage){
			return Activator.getDefault().getImageRegistry().get("package"); //$NON-NLS-1$
		}
		else if (obj instanceof TreeModel){
			return Activator.getDefault().getImageRegistry().get("model"); //$NON-NLS-1$
		}
		else if (obj instanceof TreeResource){
			TreeResource r = (TreeResource)obj;
			if (r.getResource() instanceof Prompt){
				return Activator.getDefault().getImageRegistry().get("prompt"); //$NON-NLS-1$
			}
			if (r.getResource() instanceof ComplexFilter){
				return Activator.getDefault().getImageRegistry().get("filter_complex"); //$NON-NLS-1$	
			}
			if (r.getResource() instanceof ListOfValue){
				return Activator.getDefault().getImageRegistry().get("lov"); //$NON-NLS-1$
			}
			if (r.getResource() instanceof SqlQueryFilter){
				return Activator.getDefault().getImageRegistry().get("filter_sql"); //$NON-NLS-1$
			}
			if (r.getResource() instanceof Filter){
				return Activator.getDefault().getImageRegistry().get("filter"); //$NON-NLS-1$
			}
			if (r.getResource() instanceof FmdtDimension){
				return Activator.getDefault().getImageRegistry().get("dimension"); //$NON-NLS-1$
			}
			
			
		}
//		else if (obj instanceof TreeDirectory){
//			return Activator.getDefault().getImageRegistry().get("folder"); //$NON-NLS-1$
//		}
//		else if (obj instanceof TreeItem){
//			TreeItem i = (TreeItem)obj;
//			if (i.getItem().getType() == IRepositoryConnection.FASD_TYPE){
//				return Activator.getDefault().getImageRegistry().get("cube"); //$NON-NLS-1$
//			}
//		}
		else if (obj instanceof TreeParent){
			return Activator.getDefault().getImageRegistry().get("object"); //$NON-NLS-1$
		}
		return null;
	}

}
