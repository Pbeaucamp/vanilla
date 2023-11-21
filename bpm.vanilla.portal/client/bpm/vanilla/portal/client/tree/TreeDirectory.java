package bpm.vanilla.portal.client.tree;

import bpm.gwt.commons.client.custom.CustomHTML;
import bpm.gwt.commons.shared.repository.PortailRepositoryDirectory;
import bpm.vanilla.portal.client.images.PortalImage;
import bpm.vanilla.portal.client.panels.navigation.TypeViewer;

import com.google.gwt.user.client.ui.Image;

public class TreeDirectory extends TreeItemOk {

	private PortailRepositoryDirectory directory;
	
	public TreeDirectory(PortailRepositoryDirectory directory, TypeViewer typeViewer) {
		super(new CustomHTML(new Image(PortalImage.INSTANCE.folder()) + " " + directory.getName()), typeViewer);
		this.directory = directory;
	}

	public PortailRepositoryDirectory getDirectory() {
		return directory;
	}
	
}
