package metadata.client.viewer;

import metadata.client.trees.TreeBusinessTable;
import metadata.client.trees.TreeColumn;
import metadata.client.trees.TreeDataSource;
import metadata.client.trees.TreeDataStream;
import metadata.client.trees.TreeDataStreamElement;
import metadata.client.trees.TreeDirectory;
import metadata.client.trees.TreeItem;
import metadata.client.trees.TreeJoin;
import metadata.client.trees.TreeLov;
import metadata.client.trees.TreeModel;
import metadata.client.trees.TreePackage;
import metadata.client.trees.TreeParent;
import metadata.client.trees.TreeRelation;
import metadata.client.trees.TreeResource;
import metadata.client.trees.TreeSavedQuery;
import metadata.client.trees.TreeTable;
import metadataclient.Activator;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import bpm.metadata.resource.ComplexFilter;
import bpm.metadata.resource.Filter;
import bpm.metadata.resource.ListOfValue;
import bpm.metadata.resource.Prompt;
import bpm.metadata.resource.SqlQueryFilter;
import bpm.metadata.resource.complex.FmdtDimension;
import bpm.vanilla.platform.core.IRepositoryApi;




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
		else if (obj instanceof TreeTable){
			return Activator.getDefault().getImageRegistry().get("phy_table"); //$NON-NLS-1$
		}
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
		else if (obj instanceof TreeDataSource){
			return Activator.getDefault().getImageRegistry().get("datasource"); //$NON-NLS-1$
		}
		else if (obj instanceof TreePackage){
			return Activator.getDefault().getImageRegistry().get("package"); //$NON-NLS-1$
		}
		else if (obj instanceof TreeModel){
			return Activator.getDefault().getImageRegistry().get("model"); //$NON-NLS-1$
		}
		else if (obj instanceof TreeJoin){
			
		}
		else if (obj instanceof TreeLov){
			return Activator.getDefault().getImageRegistry().get("lov"); //$NON-NLS-1$
		}
		else if (obj instanceof TreeSavedQuery){
			return Activator.getDefault().getImageRegistry().get("savedquery"); //$NON-NLS-1$
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
		else if (obj instanceof TreeDirectory){
			return Activator.getDefault().getImageRegistry().get("folder"); //$NON-NLS-1$
		}
		else if (obj instanceof TreeItem){
			TreeItem i = (TreeItem)obj;
			if (i.getItem().getType() == IRepositoryApi.FASD_TYPE){
				return Activator.getDefault().getImageRegistry().get("cube"); //$NON-NLS-1$
			}
		}
		else if (obj instanceof TreeParent){
			return Activator.getDefault().getImageRegistry().get("object"); //$NON-NLS-1$
		}
		else {
			return Activator.getDefault().getImageRegistry().get("object"); //$NON-NLS-1$
		}
		
		return null;
	}

}
