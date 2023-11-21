package bpm.gateway.ui.views.property.sections.files;

import org.eclipse.jface.viewers.IFilter;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.files.FileFolderReader;
import bpm.gateway.core.transformations.files.FileFolderReader.FileType;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;

public class FileReaderFilterXls implements IFilter {

	public boolean select(Object toTest) {
		if (toTest instanceof NodePart){
			
			Transformation t = (Transformation)((Node)((NodePart)toTest).getModel()).getGatewayModel();
			if (t instanceof FileFolderReader){
				return ((FileFolderReader)t).getFileType() ==  FileType.XLS;
			}
		}
		return false;
	}

}
