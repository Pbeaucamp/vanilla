package bpm.vanilla.api.runtime.dto;


import java.util.ArrayList;
import java.util.List;

import bpm.metadata.layer.business.IBusinessModel;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class MetadataViewerItem extends ViewerItem {
	
	private List<MetadataBusinessModel> children;
	
	public MetadataViewerItem(RepositoryItem it,Group group,List<IBusinessModel> businessModels) throws Exception {
		super(it,"FMDT");
		
		children = new ArrayList<>();
		for(IBusinessModel businessModel : businessModels) {
			children.add(new MetadataBusinessModel(group,it,businessModel));
		}
	}

	public List<MetadataBusinessModel> getChildren() {
		return children;
	}
	
	
}
